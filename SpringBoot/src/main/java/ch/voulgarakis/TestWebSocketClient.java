package ch.voulgarakis;

import java.util.concurrent.CyclicBarrier;
import java.util.stream.IntStream;

import ch.voulgarakis.utils.ChatMessage;
import ch.voulgarakis.utils.WebsocketClient;

public class TestWebSocketClient {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3);

        IntStream.range(0, 3).parallel().forEach((i) -> {
            // Create the websocket session
            WebsocketClient wsClient = WebsocketClient.create("ws://localhost:8081/RecruitmentService/recruitment");

            // The rx stream
            wsClient.rxStream().subscribe(m -> {
                System.out
                        .println("Thread: " + i + "\nReceived Message:\n" + m.getPayload() + "\n--------------------");
            });

            // Subscribe
            wsClient.subscribe("/topic/publicChatSession0", ChatMessage.class);
            wsClient.subscribe("/user/queue/greetings", ChatMessage.class);

            try {
                barrier.await();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // wsClient.send("/chat/with/publicChatSession0", new ChatMessage("Giasou"));
            wsClient.send("/chat/with/hello", new ChatMessage("Bonjour"));

            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}
