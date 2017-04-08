package net.mh.kafkabrowser.resource;

import net.mh.kafkabrowser.model.BrowserConsumer;
import net.mh.kafkabrowser.model.Topic;
import net.mh.kafkabrowser.model.TopicRecord;
import net.mh.kafkabrowser.store.BrowserConsumerStore;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by markus on 05.04.17.
 */

@RestController
@RequestMapping
public class TopicResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicResource.class);
    @Autowired
    private BrowserConsumerStore browserConsumerStore;

    @RequestMapping(path = {"/consumer/{consumerId}/topic/{name}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Topic> get(@PathVariable String consumerId, @PathVariable String name) {
        return get(consumerId, name, false, false);
    }

    @RequestMapping(path = {"/consumer/{consumerId}/topic/{name}/back"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Topic> getBack(@PathVariable String consumerId, @PathVariable String name) {
        return get(consumerId, name, true, false);
    }

    @RequestMapping(path = {"/consumer/{consumerId}/topic/{name}/next"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Topic> getMore(@PathVariable String consumerId, @PathVariable String name) {
        return get(consumerId, name, false, true);
    }

    private ResponseEntity<Topic> get(String consumerId, String name, Boolean back, Boolean next) {
        LOGGER.debug("Request: {}, {}, back: {}, next: {}", consumerId, name, back, next);

        if (name == null) {
            return ResponseEntity.notFound().build();
        }

        BrowserConsumer browserConsumer = browserConsumerStore.get(consumerId);
        if (browserConsumer == null) {
            throw new IllegalArgumentException(String.format("ConsumerId: %s unknown.", consumerId));
        }

        LOGGER.debug("Before: {}", browserConsumer);

        Consumer kafkaConsumer = browserConsumer.getKafkaConsumer();

        if (!kafkaConsumer.subscription().contains(name)) {
            browserConsumer.reset();
            kafkaConsumer.subscribe(Collections.singletonList(name), new ConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                    LOGGER.debug("Revoked Partitions: {}", partitions);
                }

                @Override
                public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                    LOGGER.debug("Assigned Partitions: {}", partitions);
                }
            });
        }

        if (back && browserConsumer.hasPrevious(name)) {
            Map<TopicPartition, Long> previous = new HashMap<>();
            browserConsumer.getPageStart(name).forEach((key, value) -> previous.put(key, Math.max(browserConsumer.getTopicStart(name).get(key), value - browserConsumer.getStepSize())));
            previous.forEach(kafkaConsumer::seek);
            browserConsumer.setPageStart(name, previous);
        } else if (next && browserConsumer.hasMore(name)) {
            Map<TopicPartition, Long> currentPageEnd = browserConsumer.getPageEnd(name);
            currentPageEnd.forEach(kafkaConsumer::seek);
            browserConsumer.setPageStart(name, currentPageEnd);
        } else {
            browserConsumer.getPageStart(name).forEach(kafkaConsumer::seek);
        }


        Topic topic = new Topic(name);
        topic.add(linkTo(methodOn(BrowserConsumerResource.class).getConsumer(consumerId)).withRel("consumer"));
        topic.add(linkTo(methodOn(TopicResource.class).get(consumerId, name)).withSelfRel());

        List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor(name);
        topic.setTopicPartitions(partitionInfos.stream().map(PartitionInfo::partition).collect(Collectors.toSet()));
        Set<TopicPartition> topicPartitions = partitionInfos.stream().map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition())).collect(Collectors.toSet());

        if (!browserConsumer.getPageStart(name).isEmpty()) {
            topicPartitions.forEach(tp -> LOGGER.debug("Before poll {} - {}.", tp, kafkaConsumer.position(tp)));
        }

        ConsumerRecords records = kafkaConsumer.poll(200);
        List<TopicRecord> topicRecords = new ArrayList<>();
        Iterator<ConsumerRecord> iterator = records.iterator();
        iterator.forEachRemaining(cr -> topicRecords.add(new TopicRecord(cr.timestampType().name, LocalDateTime.ofInstant(Instant.ofEpochMilli(cr.timestamp()), ZoneId.systemDefault()), String.valueOf(cr.key()), String.valueOf(cr.value()))));
        topic.setCurrentRecords(topicRecords);

        topicPartitions.forEach(tp -> LOGGER.debug("After poll {} - {}.", tp, kafkaConsumer.position(tp)));

        //keep current offsets up-to-date
        browserConsumer.setTopicStart(name, kafkaConsumer.beginningOffsets(topicPartitions));
        browserConsumer.setTopicEnd(name, kafkaConsumer.endOffsets(topicPartitions));

        //new consumer
        if (browserConsumer.getPageStart(name).isEmpty()) {
            LOGGER.debug("New Consumer invoked.");
            browserConsumer.setPageStart(name, browserConsumer.getTopicStart(name));
        }

        //get current position, which is page end
        topicPartitions.forEach(tp -> browserConsumer.setPageEnd(tp.topic(), tp, kafkaConsumer.position(tp)));

        if (browserConsumer.hasPrevious(name)) {
            topic.add(linkTo(methodOn(TopicResource.class).getBack(consumerId, name)).withRel("back"));
        }
        if (browserConsumer.hasMore(name)) {
            topic.add(linkTo(methodOn(TopicResource.class).getMore(consumerId, name)).withRel("next"));
        }

        LOGGER.debug("After: {}", browserConsumer);
        return ResponseEntity.ok(topic);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleException(IllegalArgumentException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
