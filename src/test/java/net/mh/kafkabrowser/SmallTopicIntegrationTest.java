package net.mh.kafkabrowser;

import net.mh.kafkabrowser.model.BrowserConsumer;
import net.mh.kafkabrowser.model.Topic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static com.github.grantwest.eventually.EventuallyLambdaMatcher.eventuallyEval;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by markus on 08.04.17.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = {"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SmallTopicIntegrationTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testTopics_EmptyPage() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<BrowserConsumer> browserConsumer = restTemplate.postForEntity("/consumer", new HttpEntity<>("{\"keyDeserializer\": \"org.apache.kafka.common.serialization.IntegerDeserializer\", \"valueDeserializer\": \"org.apache.kafka.common.serialization.StringDeserializer\"}", headers), BrowserConsumer.class);
        assertThat(browserConsumer.getStatusCode(), equalTo(HttpStatus.OK));

        BrowserConsumer body = browserConsumer.getBody();

        Link topics = body.getLink("topics");
        assertThat(topics, notNullValue());
        assertThat(topics.getHref(), endsWith("test"));

        ResponseEntity<Topic> topic = restTemplate.getForEntity(String.format("/consumer/%s/topic/test", body.getConsumerId()), Topic.class);

        Topic topicBody = topic.getBody();
        assertThat(topicBody.hasLink("next"), is(false));
        assertThat(topicBody.hasLink("previous"), is(false));
    }

    @Test
    public void testTopics_OnePage() {
        KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(KafkaTestUtils.producerProps(embeddedKafka));
        for (Integer i = 0; i < 10; i++) {
            kafkaProducer.send(new ProducerRecord<>("test", i, "test" + i));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<BrowserConsumer> browserConsumer = restTemplate.postForEntity("/consumer", new HttpEntity<>("{\"keyDeserializer\": \"org.apache.kafka.common.serialization.IntegerDeserializer\", \"valueDeserializer\": \"org.apache.kafka.common.serialization.StringDeserializer\"}", headers), BrowserConsumer.class);
        assertThat(browserConsumer.getStatusCode(), equalTo(HttpStatus.OK));

        assertThat(() -> restTemplate.getForEntity(String.format("/consumer/%s/topic/test", browserConsumer.getBody().getConsumerId()), Topic.class).getBody().getCurrentRecords(), eventuallyEval(hasSize(10), Duration.ofSeconds(10)));
    }
}
