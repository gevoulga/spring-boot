package ch.voulgarakis.icsc2018;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import ch.voulgarakis.icsc2018.chat.model.ChatMessage;
import ch.voulgarakis.icsc2018.commons.WebsocketClient;

public class TestWebSocketClient {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3);

        IntStream.range(0, 3).parallel().forEach((i) -> {
            // Create the websocket session
            WebsocketClient wsClient = WebsocketClient.create("ws://localhost:8081/RecruitmentService/recruitment");

            // The rx stream
            wsClient.rxStream().delay(4, TimeUnit.SECONDS).subscribe(m -> {
                System.out.println("Thread: " + i + "\nReceived Message:\n" + m.payload() + "\n--------------------");
            });

            // Subscribe
            wsClient.subscribe("/topic/publicChatSession", ChatMessage.class);
            wsClient.subscribe("/user/queue/privateChatSession", ChatMessage.class);

            try {
                barrier.await();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            wsClient.send("/chat/public/publicChatSession", new ChatMessage("Giasou"));
            wsClient.send("/chat/private/privateChatSession", new ChatMessage("Bonjour"));

            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}
