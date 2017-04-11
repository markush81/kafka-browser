package net.mh.kafkabrowser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by markus on 08.04.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application extends ResourceSupport {
}
