package net.mh.kafkabrowser;

import net.mh.kafkabrowser.model.BrowserConsumer;
import net.mh.kafkabrowser.model.Topic;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by markus on 15.04.17.
 */
public class SonarQubeQuietnessTest {

    @Test
    public void testTopicEqualsHashCode() {
        Topic topicOne = new Topic("test");
        Topic topicTwo = new Topic("test");

        assertThat(topicOne.equals(null), is(false));
        assertThat(topicOne.equals("test"), is(false));

        assertThat(topicOne.equals(topicOne), is(true));

        assertThat(topicOne.equals(topicTwo), is(true));
        assertThat(topicOne.hashCode(), equalTo(topicTwo.hashCode()));
    }

    @Test
    public void testBrowserConsumerEqualsHashCode() {
        BrowserConsumer consumerOne = new BrowserConsumer();
        BrowserConsumer consumerTwo = new BrowserConsumer();

        assertThat(consumerOne.equals(null), is(false));
        assertThat(consumerOne.equals("test"), is(false));

        assertThat(consumerOne.equals(consumerOne), is(true));

        assertThat(consumerOne.equals(consumerTwo), is(true));
        assertThat(consumerOne.hashCode(), equalTo(consumerTwo.hashCode()));
    }

}
