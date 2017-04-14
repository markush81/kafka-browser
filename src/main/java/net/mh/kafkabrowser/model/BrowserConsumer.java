package net.mh.kafkabrowser.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.hateoas.ResourceSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by markus on 08.04.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrowserConsumer extends ResourceSupport {

    private String consumerId;
    private Consumer consumer;
    private String keyDeserializer;
    private String valueDeserializer;
    private Integer stepSize;
    private Map<String, Map<TopicPartition, Long>> topicStart;
    private Map<String, Map<TopicPartition, Long>> topicEnd;

    private Map<String, Map<TopicPartition, Long>> pageStart;
    private Map<String, Map<TopicPartition, Long>> pageEnd;

    public BrowserConsumer() {
        //JSON
    }

    public BrowserConsumer(Consumer consumer, Integer stepSize, String keyDeserializer, String valueDeserializer) {
        this.consumerId = UUID.randomUUID().toString();
        this.topicStart = new HashMap<>();
        this.topicEnd = new HashMap<>();
        this.pageEnd = new HashMap<>();
        this.pageStart = new HashMap<>();
        this.consumer = consumer;
        this.stepSize = stepSize;
        this.keyDeserializer = keyDeserializer;
        this.valueDeserializer = valueDeserializer;
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
        Map<TopicPartition, Long> tPageEnd = getPageEnd(topic);
        Map<TopicPartition, Long> tEnd = getTopicEnd(topic);
        return tEnd.keySet().stream().anyMatch(key -> tEnd.get(key) - tPageEnd.get(key) > 0);
    }

    @JsonIgnore
    public boolean hasPrevious(String topic) {
        Map<TopicPartition, Long> tPageStart = getPageStart(topic);
        Map<TopicPartition, Long> tStart = getTopicStart(topic);
        return tPageStart.keySet().stream().anyMatch(key -> tPageStart.get(key) - tStart.get(key) > 0);
    }

    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    public String getValueDeserializer() {
        return valueDeserializer;
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
    @SuppressWarnings({"squid:S1067"})
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
        BrowserConsumer that = (BrowserConsumer) o;
        return Objects.equals(consumerId, that.consumerId) &&
                Objects.equals(consumer, that.consumer) &&
                Objects.equals(keyDeserializer, that.keyDeserializer) &&
                Objects.equals(valueDeserializer, that.valueDeserializer) &&
                Objects.equals(stepSize, that.stepSize) &&
                Objects.equals(topicStart, that.topicStart) &&
                Objects.equals(topicEnd, that.topicEnd) &&
                Objects.equals(pageStart, that.pageStart) &&
                Objects.equals(pageEnd, that.pageEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), consumerId, consumer, keyDeserializer, valueDeserializer, stepSize, topicStart, topicEnd, pageStart, pageEnd);
    }
}