package net.mh.kafkabrowser.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by markus on 10.04.17.
 */
@Configuration
public class KafkaConfiguration {

    private KafkaProperties kafkaProperties;

    public KafkaConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    public Map<String, Object> consumerConfigs() {
        return kafkaProperties.buildConsumerProperties();
    }
}
