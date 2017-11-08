package ch.voulgarakis.recruitment.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
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
    private List<FlowableEmitter<Message>> emitters = new ArrayList<>();

    @Override
    public void subscribe(FlowableEmitter<Message> e) throws Exception {
        emitters.add(e);
    }

    public Flowable<Message> rxStream() {
        return stream;
    }

    private AtomicBoolean disposed = new AtomicBoolean(false);

    @Override
    public boolean isDisposed() {
        return disposed.get();
    }

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
            // Empty session handler
        }).get();
    }

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

    public <T> void send(String destination, T message) {
        session.send(destination, message);
    }
}
