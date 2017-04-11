package net.mh.kafkabrowser.resource.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by markus on 09.04.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorMessage {

    private String message;

    public ErrorMessage() {
        //JSON
    }

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
