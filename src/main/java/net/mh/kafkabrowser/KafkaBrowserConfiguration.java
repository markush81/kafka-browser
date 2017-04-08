package net.mh.kafkabrowser;

import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class KafkaBrowserConfiguration {

}
