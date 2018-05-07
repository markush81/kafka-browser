# Kafka Browser

Browse your Kafka Topics.

[![Build Status](https://travis-ci.org/markush81/kafka-browser.svg?branch=master)](https://travis-ci.org/markush81/kafka-browser) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=markush81.net.mh.kafkabrowser&metric=alert_status)](https://sonarcloud.io/dashboard?id=markush81.net.mh.kafkabrowser)

***EARLY BETA PHASE***

Adjust `src/main/resources/application.yml`.

```
./gradlew bootRun
```

[http://localhost:8080](http://localhost:8080)

Just follow the [HAL](https://en.wikipedia.org/wiki/Hypertext_Application_Language) links.

![Browser Start](doc/browser_start.png)

Examples:

1. Create a `<Long,String>` Topic Browser

```
curl -XPOST -H "Content-Type:application/json" -d @requests/consumer_LS.txt http://localhost:8080/consumer
```