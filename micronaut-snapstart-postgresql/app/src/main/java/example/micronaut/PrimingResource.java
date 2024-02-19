package example.micronaut;

//import io.micronaut.crac.OrderedResource;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;
//import org.crac.Context;
//import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
class PrimingResource { // implements OrderedResource  {
    private static final Logger LOG = LoggerFactory.getLogger(PrimingResource.class);

    private final JsonMapper jsonMapper;
    private final MessageRepository messageRepository;

    PrimingResource(JsonMapper jsonMapper, MessageRepository messageRepository) {
        this.jsonMapper = jsonMapper;
        this.messageRepository = messageRepository;
    }

    //@Override
//    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
//        List<Long> ids = new ArrayList<>();
//        LOG.info("saving 1000 messages and then deleting them to primer the function");
//        for (int i = 0; i < 1000; i++) {
//            APIGatewayProxyResponseEventUtils.responseWith(jsonMapper, Collections.singletonMap("message", "Hello Moon"));
//            ids.add(messageRepository.save(new Message(null, null, "Foo")).id());
//        }
//        for (Long id : ids) {
//            messageRepository.deleteById(id);
//        }
//    }

    //@Override
//    public void afterRestore(Context<? extends Resource> context) throws Exception {
//
//    }
}
