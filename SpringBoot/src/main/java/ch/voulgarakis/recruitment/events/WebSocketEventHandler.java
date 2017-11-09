package ch.voulgarakis.recruitment.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEventHandler implements ApplicationListener<AbstractSubProtocolEvent> {
    Logger logger = LoggerFactory.getLogger(WebSocketEventHandler.class);

    @Override
    public void onApplicationEvent(AbstractSubProtocolEvent event) {
        // Connected event
        if (SessionConnectedEvent.class.isAssignableFrom(event.getClass())) {
            logger.info("WebSocketEventHandler connected: {}", event);
        }
        // Disconnected event
        else if (SessionConnectedEvent.class.isAssignableFrom(event.getClass())) {
            logger.info("WebSocketEventHandler disconnected: {}", event);
        }
    }
}