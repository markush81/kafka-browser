package net.mh.kafkabrowser.resource;

import net.mh.kafkabrowser.resource.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by markus on 09.04.17.
 */
public class AbstractResource {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorMessage> handleException(IllegalArgumentException exception) {
        return new ResponseEntity<>(new ErrorMessage(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
