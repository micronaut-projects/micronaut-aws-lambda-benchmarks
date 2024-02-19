package example.micronaut;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public class MessageHandler {
    private final MessageRepository messageRepository;
    private final JsonMapper jsonMapper;

    public MessageHandler(MessageRepository messageRepository,
                          JsonMapper jsonMapper) {
        this.messageRepository = messageRepository;
        this.jsonMapper = jsonMapper;
    }

    public void saveMessage(APIGatewayProxyRequestEvent request) throws IOException {
        String body = request.getBody();
        MessageSave messageSave = jsonMapper.readValue(body, MessageSave.class);
        messageRepository.save(new Message(null, null, messageSave.message()));
    }
}
