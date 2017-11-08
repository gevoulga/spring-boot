package ch.voulgarakis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import ch.voulgarakis.utils.ChatMessage;

@RestController
@MessageMapping("/with")
public class ChatController {
    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Using this method, the responses will be broadcasted!
     * {@link https://spring.io/guides/gs/messaging-stomp-websocket/}
     */
    @MessageMapping("/{chatId}")
    @SendTo("/topic/{chatId}")
    public ChatMessage validate(ChatMessage message, @DestinationVariable String chatId) {
        template.convertAndSend("/topic/" + chatId, new ChatMessage(chatId + ": Greetings..."));
        return new ChatMessage(chatId + ": Greetings... from template.");
    }

    /**
     * Using this method, the responses will be sent to specific user!
     * {@link https://stackoverflow.com/questions/33720386/how-does-spring-websocket-send-message-to-a-specific-user}
     */
    @MessageMapping("/hello")
    @SendToUser("/queue/greetings")
    public ChatMessage personalUserMessage(SimpMessageHeaderAccessor headerAccessor, ChatMessage message) {
        String username = "mrAnderson";
        // template.convertAndSendToUser(username, "/topic/greetings",
        // new ChatMessage("Greetings to you, sir... " + username + " from template."));
        return new ChatMessage("Greetings to you, sir... " + username);
    }
}
