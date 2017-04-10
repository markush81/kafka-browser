package net.mh.kafkabrowser.resource;

import net.mh.kafkabrowser.kafka.KafkaConfiguration;
import net.mh.kafkabrowser.model.BrowserConsumer;
import net.mh.kafkabrowser.model.BrowserConsumerRequest;
import net.mh.kafkabrowser.store.BrowserConsumerStore;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by markus on 08.04.17.
 */
@RestController
@RequestMapping
public class BrowserConsumerResource extends AbstractResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowserConsumerResource.class);

    private Random random;
    private KafkaConfiguration kafkaConfiguration;
    private BrowserConsumerStore browserConsumerStore;

    @Autowired
    public BrowserConsumerResource(KafkaConfiguration kafkaConfiguration, BrowserConsumerStore browserConsumerStore) {
        this.random = new Random();
        this.kafkaConfiguration = kafkaConfiguration;
        this.browserConsumerStore = browserConsumerStore;
    }

    @RequestMapping(path = "/consumer", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<BrowserConsumer> createConsumer() {
        return createConsumer(defaultBrowserConsumerRequest());
    }

    @RequestMapping(path = "/consumer/{consumerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<BrowserConsumer> getConsumer(@PathVariable String consumerId) {
        BrowserConsumer browserConsumer = browserConsumerStore.get(consumerId);
        if (browserConsumer == null) {
            throw new IllegalArgumentException(String.format("ConsumerId: %s unknown.", consumerId));
        }
        return ResponseEntity.ok(browserConsumer);
    }

    @RequestMapping(path = "/consumer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<BrowserConsumer> createConsumer(@RequestBody BrowserConsumerRequest browserConsumerRequest) {
        try {
            Deserializer keyDeserializer = (Deserializer) Class.forName(browserConsumerRequest.getKeyDeserializer()).newInstance();
            Deserializer valueDeserializer = (Deserializer) Class.forName(browserConsumerRequest.getKeyDeserializer()).newInstance();

            Map<String, Object> consumerConfigs = kafkaConfiguration.consumerConfigs();
            consumerConfigs.put(ConsumerConfig.GROUP_ID_CONFIG, String.format("browser-%s", Math.abs(random.nextInt()))); //NOSONAR
            consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumerConfigs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            DefaultKafkaConsumerFactory defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory(consumerConfigs, keyDeserializer, valueDeserializer);

            Consumer kafkaConsumer = defaultKafkaConsumerFactory.createConsumer();

            Map<String, List<PartitionInfo>> topics = kafkaConsumer.listTopics();


            BrowserConsumer browserConsumer = new BrowserConsumer(kafkaConsumer, (Integer) consumerConfigs.get(ConsumerConfig.MAX_POLL_RECORDS_CONFIG), browserConsumerRequest.getKeyDeserializer(), browserConsumerRequest.getValueDeserializer());

            browserConsumer.add(linkTo(methodOn(ApplicationResource.class).get()).withRel("browser"));
            browserConsumer.add(linkTo(methodOn(BrowserConsumerResource.class).getConsumer(browserConsumer.getConsumerId())).withSelfRel());
            topics.keySet().forEach(topic -> browserConsumer.add(linkTo(methodOn(TopicResource.class).get(browserConsumer.getConsumerId(), topic)).withRel("topics")));
            browserConsumerStore.add(browserConsumer);

            return getConsumer(browserConsumer.getConsumerId());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            LOGGER.error("{}", ex.getMessage(), ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    private BrowserConsumerRequest defaultBrowserConsumerRequest() {
        return new BrowserConsumerRequest(StringDeserializer.class.getName(), StringDeserializer.class.getName());
    }
}
