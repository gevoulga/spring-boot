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

import ch.voulgarakis.icsc2018.chat.model.ChatMessage;
import ch.voulgarakis.icsc2018.commons.WebsocketClient;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.utils.ApplicationResult;
import ch.voulgarakis.icsc2018.recruitment.utils.SkillAndWeight;
import ch.voulgarakis.recruitment.tests.config.TestConfig;
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
    public void chatWebsocketTest() {
        int n = 3; // # of clients!

        // Save the public and the private responses
        AtomicInteger topicResponses = new AtomicInteger(0);
        AtomicInteger queueResponses = new AtomicInteger(0);
        // Used to sunchronize the threads
        CyclicBarrier barrier = new CyclicBarrier(n);

        // Parallel execution of n clients!
        IntStream.range(0, n).parallel().forEach((i) -> {

            // Create the websocket session connection
            WebsocketClient wsClient = WebsocketClient.create("ws://localhost:8081/RecruitmentService/recruitment");

            // The rx stream
            Disposable subscription = wsClient.rxStream()
                    // The how we react to a new Websocket message
                    .subscribe(m -> {
                        if (m.from().equals("/topic/publicChatSession"))
                            topicResponses.incrementAndGet();
                        else if (m.from().equals("/user/queue/privateChatSession"))
                            queueResponses.incrementAndGet();
                        logger.info(
                                "\n\tThread: " + i + "\n\tReceived Message: " + m.payload() + "\n\tFrom: " + m.from());
                    });

            // Subscribe on the public topic and on the private queue
            wsClient.subscribe("/topic/publicChatSession", ChatMessage.class);
            wsClient.subscribe("/user/queue/privateChatSession", ChatMessage.class);

            // Make all threads wait here...!
            try {
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            wsClient.send("/chat/public/publicChatSession", new ChatMessage("Giasou"));
            wsClient.send("/chat/private/privateChatSession", new ChatMessage("Bonjour"));

            // Wait a few seconds until the message exchange has been complete, and we can close the websocket session
            try {
                Thread.sleep(n * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Close the connection of the client
            subscription.dispose();
            wsClient.dispose();
        });

        assertTrue("Topic messages counted " + topicResponses.get() + " != " + n, 2 * n * n == topicResponses.get());
        assertTrue("Queue messages counted " + queueResponses.get() + " != " + n, n == queueResponses.get());
    }

    @Test
    public void matchWebsocketTest() {
        // Create the websocket session connection
        WebsocketClient wsClient = WebsocketClient.create("ws://localhost:8081/RecruitmentService/recruitment");

        // The rx stream
        Disposable subscription = wsClient.rxStream().subscribe(m -> {
            logger.info("\n\tReceived Message:" + m.payload());
        });

        // Subscribe on the match topic and on the private queue
        wsClient.subscribe("/topic/applications", ApplicationResult.class);

        // Apply for a position....
        ApplicationResult result = rs.apply(
                // Applicant
                new Applicant("Jim Carrey", new SkillAndWeight(new Skill("Omnipotent"), 1d),
                        new SkillAndWeight(new Skill("Omnipresent"), 1d)),
                // Vacancy
                new Vacancy("God", 0.8d, new SkillAndWeight(new Skill("Omnipresent"), 0.9d),
                        new SkillAndWeight(new Skill("Omnipresent"), 0.9d)));

        assertTrue("Jim Carrey is supposed to become god... At least for a week!", result.isMatch());

        // Wait a few seconds until the message exchange has been complete, and we can close the websocket session
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Close the connection of the client
        subscription.dispose();
        wsClient.dispose();
    }
}
