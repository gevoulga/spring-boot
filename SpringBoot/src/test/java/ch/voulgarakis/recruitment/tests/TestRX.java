package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.voulgarakis.icsc2018.recruitment.dao.SkillRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.recruitment.tests.config.TestConfig;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class) // Setup test with TestConfig
@ActiveProfiles("jpa")
public class TestRX {
    Logger logger = LoggerFactory.getLogger(TestRX.class);

    @Autowired
    SkillRepository skillRepo;

    @Test
    public void timedStreamExample() {
        Flowable
                // Every 1 second
                .interval(1, TimeUnit.SECONDS)
                // Log message to be printed from the emitter
                .doOnNext(i -> logger.info("Emitting " + i + " on thread " + Thread.currentThread().getName()))

                .subscribeOn(Schedulers.newThread()) // The subscription thread
                .observeOn(Schedulers.computation())// The observation thread

                // Log message to be printed from the subscriber
                .doOnNext(w -> logger.info("Received " + w + " on thread " + Thread.currentThread().getName()))
                // Take the 10 first emissions
                .take(10)
                // Subscribe to start the procedure (blocking used to make the main thread wait)
                .blockingSubscribe();
    }

    @Test
    public void skillDBPopulator() throws IOException, URISyntaxException {
        List<String> words = Files.readAllLines(Paths.get(this.getClass().getResource("/words.txt").toURI()));

        // Create a stream of the words list
        Flowable.fromIterable(words)
                // Convert
                .subscribeOn(Schedulers.newThread()) // The subscription thread
                .observeOn(Schedulers.computation())// The observation thread
                // convert to skill
                .map(w -> new Skill(w))
                // bunch together every 50000 words
                .buffer(50000)
                //
                .zipWith(Flowable.interval(20, TimeUnit.SECONDS), (w, t) -> w)
                // Store to the DB
                .doOnNext(skillList -> skillRepo.save(skillList))
                // Subscribe to start the procedure (blocking used to make the main thread wait)
                .blockingSubscribe();

        logger.info("SkillRepo: " + skillRepo.count());
        logger.info("words: " + words.size());
        assertTrue("Skill Repository ended up with wrong entries... ", skillRepo.count() == words.size());
    }
}
