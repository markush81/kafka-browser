package net.mh.kafkabrowser;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by markus on 22/10/2016.
 */
@SpringBootApplication
public class KafkaBrowser {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(KafkaBrowser.class)
                .listeners((ApplicationListener<ApplicationFailedEvent>) event -> System.exit(-1))
                .run(args);
    }
}
