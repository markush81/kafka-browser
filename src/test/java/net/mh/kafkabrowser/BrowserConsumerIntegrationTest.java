package net.mh.kafkabrowser;

import net.mh.kafkabrowser.model.BrowserConsumer;
import net.mh.kafkabrowser.resource.error.ErrorMessage;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by markus on 08.04.17.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = {"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BrowserConsumerIntegrationTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateDefaultConsumer() {
        ResponseEntity<BrowserConsumer> browserConsumer = restTemplate.getForEntity("/consumer", BrowserConsumer.class);
        assertThat(browserConsumer.getStatusCode(), equalTo(HttpStatus.OK));
        BrowserConsumer body = browserConsumer.getBody();
        Link consumers = body.getLink("self");
        assertThat(consumers, notNullValue());
        assertThat(consumers.getHref(), endsWith(body.getConsumerId()));
    }

    @Test
    public void testCreateConsumer() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<BrowserConsumer> browserConsumer = restTemplate.postForEntity("/consumer", new HttpEntity<>("{\"keyDeserializer\": \"org.apache.kafka.common.serialization.LongDeserializer\", \"valueDeserializer\": \"org.apache.kafka.common.serialization.StringDeserializer\"}", headers), BrowserConsumer.class);
        assertThat(browserConsumer.getStatusCode(), equalTo(HttpStatus.OK));
        BrowserConsumer browserConsumerBody = browserConsumer.getBody();
        assertThat(browserConsumerBody.getKeyDeserializer(), equalTo(LongDeserializer.class.getName()));
        assertThat(browserConsumerBody.getValueDeserializer(), equalTo(StringDeserializer.class.getName()));
    }

    @Test
    public void testUnknownConsumer() {
        ResponseEntity<ErrorMessage> error = restTemplate.getForEntity("/consumer/101010101", ErrorMessage.class);
        assertThat(error.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(error.getBody().getMessage(), equalTo("ConsumerId: 101010101 unknown."));
    }


    @Test
    public void testWrongDeserializer() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<ErrorMessage> error = restTemplate.postForEntity("/consumer", new HttpEntity<>("{\"keyDeserializer\": \"org.apache.kafka.common.serialization.WooDoo\", \"valueDeserializer\": \"org.apache.kafka.common.serialization.StringDeserializer\"}", headers), ErrorMessage.class);
        assertThat(error.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        String message = error.getBody().getMessage();
        assertThat(message, containsString("java.lang.ClassNotFoundException"));
        assertThat(message, containsString("org.apache.kafka.common.serialization.WooDoo"));
    }
}
