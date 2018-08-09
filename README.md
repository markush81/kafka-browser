# Kafka Browser

Browse your Kafka Topics.

[![Build Status](https://travis-ci.org/markush81/kafka-browser.svg?branch=master)](https://travis-ci.org/markush81/kafka-browser) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/d447741dab7b49c8a8807225b25e8faf)](https://www.codacy.com/app/markush81/kafka-browser?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=markush81/kafka-browser&amp;utm_campaign=Badge_Grade)

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