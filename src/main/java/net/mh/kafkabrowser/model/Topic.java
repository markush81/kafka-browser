package net.mh.kafkabrowser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.kafka.common.TopicPartition;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by markus on 05.04.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Topic extends ResourceSupport {

    private String name;
    private Set<Integer> topicPartitions;
    private List<TopicRecord> currentRecords;
    private Map<TopicPartition, Long> begin;
    private Map<TopicPartition, Long> end;

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

    @JsonIgnore
    public Map<TopicPartition, Long> getBegin() {
        return begin;
    }

    public void setBegin(Map<TopicPartition, Long> begin) {
        this.begin = begin;
    }

    @JsonIgnore
    public Map<TopicPartition, Long> getEnd() {
        return end;
    }

    public void setEnd(Map<TopicPartition, Long> end) {
        this.end = end;
    }
}
