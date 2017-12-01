package ch.voulgarakis.icsc2018.commons;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;

public class WebsocketClient implements FlowableOnSubscribe<Message>, Disposable {
    // Is serialize() needed?
    private Flowable<Message> stream = Flowable.create(this, BackpressureStrategy.MISSING).serialize();
    private List<FlowableEmitter<Message>> emitters = new CopyOnWriteArrayList<>(); // Allows Concurrent Modification

    /**
     * Hook a reactive stream subscriber to the websocket session.
     */
    @Override
    public void subscribe(FlowableEmitter<Message> e) throws Exception {
        emitters.add(e);
    }

    /**
     * Get an reactive stream from this websocket session.
     */
    public Flowable<Message> rxStream() {
        return stream;
    }

    private AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

    /**
     * Terminate the websocket session, and close the streams at the same time!
     */
    @Override
    public void dispose() {
        if (disposed.compareAndSet(false, true)) {
            session.disconnect();
            emitters.parallelStream().forEach(e -> {
                if (e.isCancelled())
                    emitters.remove(e);
                else
                    e.onComplete();
            });
        }
    }

    public static WebsocketClient create(String url) {
        try {
            return new WebsocketClient(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    ///////////////////////////////////
    /////// The Session Handler ///////
    ///////////////////////////////////

    private final StompSession session;

    private WebsocketClient(String url) throws InterruptedException, ExecutionException {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        session = stompClient.connect(url, new StompSessionHandlerAdapter() {
            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                    byte[] payload, Throwable exception) {
                emitters.parallelStream().forEach(e -> {
                    if (e.isCancelled())
                        emitters.remove(e);
                    else
                        e.onError(exception);
                });
            }

            // Empty session handler
        }).get();
    }

    /**
     * Subscribe to the specified websocket destination (topic/queue). This will start publishing into our stream, any
     * messages incoming on this websocket.
     * 
     * @param destination: the websocket destination to start listening to.
     * @param messageClass: What type of messages we expect to receive. (Incoming JSON messages will be converted to
     *            this class.)
     */
    public void subscribe(String destination, Class<?> messageClass) {
        // The Connect handler
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return messageClass;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                emitters.parallelStream().forEach(e -> {
                    if (e.isCancelled())
                        emitters.remove(e);
                    else {
                        try {
                            e.onNext(new Message(destination, messageClass, payload));
                        } catch (ClassCastException e2) {
                            e.onError(e2);
                        }
                    }
                });
            }
        });
    };

    /**
     * Send to the specified websocket destination (topic/queue) the given message.
     * 
     * @param destination: the websocket destination to start listening to.
     * @param message: the message object to be sent. It will be converted to JSON message and then sent into the
     *            websocket.
     */
    public <T> void send(String destination, T message) {
        session.send(destination, message);
    }
}
