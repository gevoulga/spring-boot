package ch.voulgarakis.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import ch.voulgarakis.ApplicationConfig;
import ch.voulgarakis.utils.ChatMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Import(ApplicationConfig.class) // Setup with TestConfig
public class TestWebSocket {
    Logger logger = LoggerFactory.getLogger(TestWebSocket.class);

    @Test
    public void testWebsocket() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect("ws://localhost:8080/stomp", new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/chat/client0Message", this);
                session.send("/app/chat", new ChatMessage("I've just connected!"));
                logger.info("Just connected and sent a ChatMessage!");
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // ChatMessage msg = (ChatMessage) payload;
                logger.info("Received : header:[" + headers + "] payload : " + payload);
            }
        });
    }
}
