package net.mh.kafkabrowser;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mh.kafkabrowser.kafka.KafkaConfiguration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by markus on 03/11/2016.
 */
@DirtiesContext
@ContextConfiguration
public class AbstractKafkaIntegrationTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractKafkaIntegrationTest.class);

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(2, true, 4, "test");

    protected BlockingQueue<ConsumerRecord<Integer, String>> records;
    private KafkaMessageListenerContainer<Integer, String> container;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka);
        DefaultKafkaConsumerFactory<Integer, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        container = new KafkaMessageListenerContainer<>(cf, new ContainerProperties("test"));
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<Integer, String>) record -> records.add(record));
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic()); // we create three topics
    }

    @After
    public void tearDown() {
        container.stop();
    }

    @TestConfiguration
    public static class TestApplicationConfiguration {

        @Autowired
        private KafkaProperties kafkaProperties;
        @Autowired
        private ObjectMapper objectMapper;

        @Bean
        public KafkaConfiguration kafkaConfiguration() {
            kafkaProperties.setBootstrapServers(Arrays.asList(embeddedKafka.getBrokersAsString().split(",")));
            return new KafkaConfiguration(kafkaProperties);
        }

        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            objectMapper.registerModule(new Jackson2HalModule());
            return new RestTemplateBuilder().interceptors((ClientHttpRequestInterceptor) (request, body, execution) -> {
                LOGGER.debug("{}", request);
                return execution.execute(request, body);
            });
        }
    }
}
