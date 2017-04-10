package net.mh.kafkabrowser;

import net.mh.kafkabrowser.model.Application;
import net.mh.kafkabrowser.model.BrowserConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class KafkaBrowserIntegrationTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testStartup() throws InterruptedException {
        ResponseEntity<Application> application = restTemplate.getForEntity("/", Application.class);
        assertThat(application.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(application.getBody().getLink("newDefaultConsumer"), notNullValue());
    }

    @Test
    public void testLinkConsumers() {
        ResponseEntity<BrowserConsumer> browserConsumer = restTemplate.getForEntity("/consumer", BrowserConsumer.class);
        ResponseEntity<Application> application = restTemplate.getForEntity("/", Application.class);
        System.out.println(application);
        assertThat(application.getStatusCode(), equalTo(HttpStatus.OK));
        Link consumers = application.getBody().getLink("consumers");
        assertThat(consumers, notNullValue());
        assertThat(consumers.getHref(), endsWith(browserConsumer.getBody().getConsumerId()));
    }
}
