package net.mh.kafkabrowser.resource;

import net.mh.kafkabrowser.model.Application;
import net.mh.kafkabrowser.store.BrowserConsumerStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by markus on 08.04.17.
 */
@RestController
@RequestMapping
public class ApplicationResource extends AbstractResource {

    @Autowired
    private BrowserConsumerStore browserConsumerStore;

    @RequestMapping(path = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Application> get() {
        Application application = new Application();
        application.add(linkTo(methodOn(ApplicationResource.class).get()).withSelfRel());
        application.add(linkTo(methodOn(BrowserConsumerResource.class).createConsumer()).withRel("newDefaultConsumer"));
        browserConsumerStore.list().forEach(bc -> application.add(linkTo(methodOn(BrowserConsumerResource.class).getConsumer(bc.getConsumerId())).withRel("consumers")));
        return ResponseEntity.ok(application);
    }
}
