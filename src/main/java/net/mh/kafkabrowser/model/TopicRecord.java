package net.mh.kafkabrowser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

/**
 * Created by markus on 08.04.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicRecord {

    private LocalDateTime timestamp;
    private String timestampType;
    private String key;
    private String value;

    public TopicRecord() {
        //JSON
    }

    public TopicRecord(String timestampType, LocalDateTime timestamp, String key, String value) {
        this.timestampType = timestampType;
        this.timestamp = timestamp;
        this.key = key;
        this.value = value;
    }

    public String getTimestampType() {
        return timestampType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
