package net.mh.kafkabrowser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.hateoas.ResourceSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by markus on 08.04.17.
 */
public class BrowserConsumer extends ResourceSupport {

    private final String consumerId;
    private final Consumer consumer;
    private Integer stepSize;
    private Map<String, Map<TopicPartition, Long>> topicStart;
    private Map<String, Map<TopicPartition, Long>> topicEnd;

    private Map<String, Map<TopicPartition, Long>> pageStart;
    private Map<String, Map<TopicPartition, Long>> pageEnd;

    public BrowserConsumer(Consumer consumer, Integer stepSize) {
        this.consumerId = UUID.randomUUID().toString();
        this.topicStart = new HashMap<>();
        this.topicEnd = new HashMap<>();
        this.pageEnd = new HashMap<>();
        this.pageStart = new HashMap<>();
        this.consumer = consumer;
        this.stepSize = stepSize;
    }

    @JsonIgnore
    public Consumer getKafkaConsumer() {
        return consumer;
    }

    public String getConsumerId() {
        return consumerId;
    }


    public void setTopicStart(String topic, Map<TopicPartition, Long> positions) {
        Map<TopicPartition, Long> copy = new HashMap<>();
        positions.forEach(copy::put);
        topicStart.put(topic, copy);
    }

    public void setTopicEnd(String topic, Map<TopicPartition, Long> positions) {
        Map<TopicPartition, Long> copy = new HashMap<>();
        positions.forEach(copy::put);
        topicEnd.put(topic, copy);
    }

    public void setPageEnd(String topic, TopicPartition partition, Long position) {
        pageEnd.computeIfAbsent(topic, k -> new HashMap<>());
        pageEnd.get(topic).put(partition, position);
    }

    public void setPageStart(String topic, Map<TopicPartition, Long> positions) {
        Map<TopicPartition, Long> copy = new HashMap<>();
        positions.forEach(copy::put);
        pageStart.put(topic, copy);
    }


    @JsonIgnore
    public Map<TopicPartition, Long> getTopicStart(String topic) {
        return this.topicStart.getOrDefault(topic, new HashMap<>());
    }

    @JsonIgnore
    public Map<TopicPartition, Long> getTopicEnd(String topic) {
        return this.topicEnd.getOrDefault(topic, new HashMap<>());
    }

    @JsonIgnore
    public Map<TopicPartition, Long> getPageStart(String topic) {
        return this.pageStart.getOrDefault(topic, new HashMap<>());
    }

    @JsonIgnore
    public Map<TopicPartition, Long> getPageEnd(String topic) {
        return this.pageEnd.getOrDefault(topic, new HashMap<>());
    }

    @JsonIgnore
    public boolean hasMore(String topic) {
        Map<TopicPartition, Long> pageEnd = getPageEnd(topic);
        Map<TopicPartition, Long> topicEnd = getTopicEnd(topic);
        return topicEnd.keySet().stream().anyMatch(key -> topicEnd.get(key) - pageEnd.get(key) > 0);
    }

    @JsonIgnore
    public boolean hasPrevious(String topic) {
        Map<TopicPartition, Long> pageStart = getPageStart(topic);
        Map<TopicPartition, Long> topicStart = getTopicStart(topic);
        return pageStart.keySet().stream().anyMatch(key -> pageStart.get(key) - topicStart.get(key) > 0);
    }

    @JsonIgnore
    public Integer getStepSize() {
        return stepSize;
    }

    public void reset() {
        this.consumer.unsubscribe();
        this.topicStart.clear();
        this.topicEnd.clear();
        this.pageEnd.clear();
        this.pageStart.clear();
    }

    @Override
    public String toString() {
        return "BrowserConsumer{" +
                "topicStart=" + topicStart +
                ", topicEnd=" + topicEnd +
                ", pageStart=" + pageStart +
                ", pageEnd=" + pageEnd +
                '}';
    }
}
