package ch.voulgarakis.icsc2018.recruitment.events;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebsocketSessionTracker {
    private Logger logger = LoggerFactory.getLogger(WebsocketSessionTracker.class);
    private List<String> sessions = new ArrayList<>();

    @EventListener
    void connect(SessionConnectedEvent connect) {
        // StompHeaderAccessor sha = StompHeaderAccessor.wrap(connect.getMessage());
        SimpMessageHeaderAccessor sha = SimpMessageHeaderAccessor.wrap(connect.getMessage());
        sessions.add(sha.getSessionId());
        // List<String> company = sha.getNativeHeader("company");
        // logger.info("Connect event [sessionId: " + sha.getSessionId() + "; company: " + company + " ]");
        logger.info("Connect event: {}", connect);
    }

    @EventListener
    void disconnect(SessionDisconnectEvent disconnect) {
        SimpMessageHeaderAccessor sha = SimpMessageHeaderAccessor.wrap(disconnect.getMessage());
        sessions.remove(sha.getSessionId());
        // List<String> company = sha.getNativeHeader("company");
        // logger.info("Connect event [sessionId: " + sha.getSessionId() + "; company: " + company + " ]");
        logger.info("Disconnect event: {}", disconnect);
    }

    /**
     * Returns a copy of the list of currently active sessions.
     */
    public List<String> sessions() {
        return new ArrayList<>(sessions);
    }
}
