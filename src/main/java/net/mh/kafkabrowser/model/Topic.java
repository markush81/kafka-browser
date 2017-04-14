package net.mh.kafkabrowser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by markus on 05.04.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic extends ResourceSupport {

    private String name;
    private Set<Integer> topicPartitions;
    private List<TopicRecord> currentRecords;

    public Topic() {
        //JSON
    }

    public Topic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Integer> getTopicPartitions() {
        return topicPartitions;
    }

    public void setTopicPartitions(Set<Integer> topicPartitions) {
        this.topicPartitions = topicPartitions;
    }

    public List<TopicRecord> getCurrentRecords() {
        return currentRecords;
    }

    public void setCurrentRecords(List<TopicRecord> currentRecords) {
        this.currentRecords = currentRecords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Topic topic = (Topic) o;
        return Objects.equals(name, topic.name) &&
                Objects.equals(topicPartitions, topic.topicPartitions) &&
                Objects.equals(currentRecords, topic.currentRecords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, topicPartitions, currentRecords);
    }
}
