package ch.voulgarakis.recruitment.tests.other;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import ch.voulgarakis.icsc2018.chat.model.ChatMessage;

public class TestWebSocketClient2 {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3);
        IntStream.range(0, 3).parallel().forEach((i) -> {

            List<Transport> transports = new ArrayList<>(2);
            transports.add(new WebSocketTransport(new StandardWebSocketClient()));
            transports.add(new RestTemplateXhrTransport());
            SockJsClient sockJsClient = new SockJsClient(transports);
            WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            String url = "ws://localhost:8081/RecruitmentService/recruitment";
            StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return ChatMessage.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    // ChatMessage msg = (ChatMessage) payload;
                    System.out.println(
                            "Received :\nheader: " + headers + "\npayload : " + payload + "\n----------------");
                }
            };
            try {
                StompSession session = stompClient.connect(url, sessionHandler).get();

                session.subscribe("/topic/publicChatSession0", new StompFrameHandler() {
                    // session.subscribe("/user/queue/greetings", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return ChatMessage.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        // ChatMessage msg = (ChatMessage) payload;
                        System.out.println(
                                "Received :\nheader: " + headers + "\npayload : " + payload + "\n----------------");
                    }
                });

                try {
                    barrier.await();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                session.send("/chat/with/publicChatSession0", new ChatMessage("Giasou"));
                session.send("/chat/with/hello", new ChatMessage("Bonjour"));

                Thread.sleep(10000);
            } catch (InterruptedException | ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    public void ttt() {

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect("ws://localhost:8081/RecruitmentService/recruitment/websocket/", new StompSessionHandler() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                // Subscribe on public chat session and the personalised greetings
                Subscription s1 = session.subscribe("/topic/publicChatSession0", this);
                Subscription s2 = session.subscribe("/user/queue/greetings", this);

                System.out.println("Subscription 1 id: " + s1.getSubscriptionId());
                System.out.println("Subscription 2 id: " + s2.getSubscriptionId());
                System.out.println("Session Id: " + session.getSessionId());

                // Send a message to the websocket!
                session.send("/chat/with/publicChatSession0", new ChatMessage("Giasou"));
                session.send("/chat/with/hello/user/adddd", new ChatMessage("Bonjour"));
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // ChatMessage msg = (ChatMessage) payload;
                System.out.println("Received :");
                System.out.println("header: " + headers);
                System.out.println("payload : " + payload);
                System.out.println("----------------");
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                    byte[] payload, Throwable exception) {
                System.err.println(session);
                System.err.println(command);
                System.err.println(headers);
                System.err.println(payload);
                exception.printStackTrace();
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println(session);
                System.err.println(session.getSessionId());
                exception.printStackTrace();
            }
        });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
