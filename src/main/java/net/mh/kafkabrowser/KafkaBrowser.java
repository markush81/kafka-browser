package net.mh.kafkabrowser;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by markus on 22/10/2016.
 */
@SpringBootApplication
public class KafkaBrowser {

    public static void main(String[] args) {
        new SpringApplicationBuilder(KafkaBrowser.class).run(args);
    }
}
