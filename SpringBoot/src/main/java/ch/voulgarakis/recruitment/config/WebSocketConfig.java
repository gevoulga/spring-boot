package ch.voulgarakis.recruitment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * {@link https://docs.spring.io/spring/docs/4.0.1.RELEASE/spring-framework-reference/html/websocket.html}
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // The web socket RECEIVE end point of the server (wsRx)
        registry.setApplicationDestinationPrefixes("/chat");

        // The web socket SEND end point of the server (wsTx)
        registry.enableSimpleBroker("/topic", "/queue");

        // Disable simple broker and use instead relay, if you want to use relay to another Full-Feature Message Broker
        // registry.enableStompBrokerRelay("/topic/", "/queue/")
        // .setUserDestinationBroadcast("/topic/unresolved-user-destination");

        // The web socket prefix for asking user specific sessions
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                // Connect to Sock JS end point
                .addEndpoint("/recruitment") // ws:localhost:port/contextPath/sockJSendpoint
                .setAllowedOrigins("*") // We we need this?
                .withSockJS();
    }
}