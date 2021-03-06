package net.mh.kafkabrowser.store;

import net.mh.kafkabrowser.model.BrowserConsumer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markus on 08.04.17.
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BrowserConsumerStore {

    private Map<String, BrowserConsumer> store = new HashMap<>();

    public void add(BrowserConsumer browserConsumer) {
        store.put(browserConsumer.getConsumerId(), browserConsumer);
    }

    public BrowserConsumer get(String consumerId) {
        return store.get(consumerId);
    }

    public Collection<BrowserConsumer> list() {
        return store.values();
    }
}
