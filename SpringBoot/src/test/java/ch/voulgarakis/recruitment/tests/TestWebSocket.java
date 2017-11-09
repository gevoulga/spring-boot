package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.voulgarakis.recruitment.model.Applicant;
import ch.voulgarakis.recruitment.model.Skill;
import ch.voulgarakis.recruitment.model.Vacancy;
import ch.voulgarakis.recruitment.service.RecruitmentService;
import ch.voulgarakis.recruitment.tests.config.TestConfig;
import ch.voulgarakis.recruitment.utils.ApplicationResult;
import ch.voulgarakis.recruitment.utils.ChatMessage;
import ch.voulgarakis.recruitment.utils.SkillAndWeight;
import ch.voulgarakis.recruitment.utils.WebsocketClient;
import io.reactivex.disposables.Disposable;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = TestConfig.class) // Setup test with TestConfig
@Transactional
public class TestWebSocket {
    Logger logger = LoggerFactory.getLogger(TestWebSocket.class);

    @Autowired
    RecruitmentService rs;

    @Test
    public void matchWebsocketTest() {
        // Create the websocket session connection
        WebsocketClient wsClient = WebsocketClient.create("ws://localhost:8081/RecruitmentService/recruitment");

        // The rx stream
        Disposable subscription = wsClient.rxStream().subscribe(m -> {
            logger.info("\nReceived Message:\n" + m.payload() + "\n--------------------");
        });

        // Subscribe on the match topic and on the private queue
        wsClient.subscribe("/topic/applications", ApplicationResult.class);

        // Apply for a position....
        boolean result = rs.apply(
                // Applicant
                new Applicant("Jim Carrey", new SkillAndWeight(new Skill("Omnipotent"), 1d),
                        new SkillAndWeight(new Skill("Omnipresent"), 1d)),
                // Vacancy
                new Vacancy("God", 0.8d, new SkillAndWeight(new Skill("Omnipresent"), 0.9d),
                        new SkillAndWeight(new Skill("Omnipresent"), 0.9d)));

        assertTrue("Jim Carrey is supposed to become god... At least for a week!", result);

        // Wait a few seconds until the message exchange has been complete, and we can close the websocket session
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Close the connection of the client
        subscription.dispose();
        wsClient.dispose();
    }

    @Test
    public void chatWebsocketTest() {
        int n = 3; // # of clients!
        // Save the public and the private responses
        AtomicInteger topicResponses = new AtomicInteger(0);
        AtomicInteger queueResponses = new AtomicInteger(0);
        // Used to sunchronize the threads
        CyclicBarrier barrier = new CyclicBarrier(n);

        // Parallel execution of n clients!
        IntStream.range(0, 3).parallel().forEach((i) -> {

            // Create the websocket session connection
            WebsocketClient wsClient = WebsocketClient.create("ws://localhost:8081/RecruitmentService/recruitment");

            // The rx stream
            Disposable subscription = wsClient.rxStream().subscribe(m -> {
                if (m.from().equals("/topic/publicChatSession0"))
                    topicResponses.incrementAndGet();
                else if (m.from().equals("/user/queue/greetings"))
                    queueResponses.incrementAndGet();
                logger.info("\nThread: " + i + "\nReceived Message:\n" + m.payload() + "\n--------------------");
            });

            // Subscribe on the public topic and on the private queue
            wsClient.subscribe("/topic/publicChatSession0", ChatMessage.class);
            wsClient.subscribe("/user/queue/greetings", ChatMessage.class);

            // Make all threads wait here...!
            try {
                barrier.await();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            wsClient.send("/chat/with/publicChatSession0", new ChatMessage("Giasou"));
            wsClient.send("/chat/with/hello", new ChatMessage("Bonjour"));

            // Wait a few seconds until the message exchange has been complete, and we can close the websocket session
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Close the connection of the client
            subscription.dispose();
            wsClient.dispose();
        });

        assertTrue("Topic messages counted " + topicResponses.get() + " != " + n, 2 * n * n == topicResponses.get());
        assertTrue("Queue messages counted " + queueResponses.get() + " != " + n, n == queueResponses.get());
    }
}
