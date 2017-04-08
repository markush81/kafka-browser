package net.mh.kafkabrowser;

import net.mh.kafkabrowser.model.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by markus on 08.04.17.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = {"test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KafkaBrowserIntegrationTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private TestRestTemplate client;

    @Test
    public void testStartup() throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<Application> application = client.getForEntity("/",Application.class);
        assertThat(application.getStatusCode(), equalTo(HttpStatus.OK));
    }
}
