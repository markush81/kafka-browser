package net.mh.kafkabrowser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by markus on 09.04.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrowserConsumerRequest {

    private String keyDeserializer;
    private String valueDeserializer;

    public BrowserConsumerRequest() {
        //JSON
    }

    public BrowserConsumerRequest(String keyDeserializer, String valueDeserializer) {
        this.keyDeserializer = keyDeserializer;
        this.valueDeserializer = valueDeserializer;
    }

    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    public void setKeyDeserializer(String keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    public String getValueDeserializer() {
        return valueDeserializer;
    }

    public void setValueDeserializer(String valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }
}
