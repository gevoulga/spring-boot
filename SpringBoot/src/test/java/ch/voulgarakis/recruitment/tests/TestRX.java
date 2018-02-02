package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

/**
 * EXC 6:
 * 
 * a)
 * 
 * Have a look at the countStreamExample() method, in this Test.
 * 
 * What happens is that we create a stream, which emits an incrementing value,
 * every 1 second. This stream runs until the takeUntil() kicks in, after 20
 * seconds (see javadoc), which will terminate the stream.
 * 
 * You will have to modify this stream, in the countStreamExample(), so we only
 * take the 10 first emissions, from the original stream. Doing so should allow
 * correctly passing this test.
 * 
 * b)
 * 
 * Have a look at the skillDBPopulator() method, in this Test.
 * 
 * There is a file in our resources called "words.txt". We process it, and we
 * would like to store all its contents to our Skills DB. At the moment, we load
 * the contents of the file, and we directly send, one by one, each word to the
 * DB. This operation "holds" the DB resources for too long.
 * 
 * What we would like instead is to:
 * 
 * -ONLY write the LAST 500 entries
 * 
 * -Write in "batches" of 50 each time
 * 
 * -With 5 seconds interval between each batch write operation
 * 
 */

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
	public void countStreamExample() {
		// The count of the items we receive from the Stream
		AtomicInteger count = new AtomicInteger(0);

		Flowable.range(1, 100)
				// Emit an item every 1 second
				.zipWith(Flowable.interval(1, TimeUnit.SECONDS), (w, t) -> w)
				
				// Log message to be printed from the emitter
				.doOnNext(i -> logger.info("Emitting " + i + " on thread " + Thread.currentThread().getName()))

				// Offload the subscription and onNext operations on a new thread
				.subscribeOn(Schedulers.newThread())

				// Take the 10 first emissions

				// Do NOT let this run forever...!
				.takeUntil(Flowable.interval(20, TimeUnit.SECONDS))

				// Subscribe to start the procedure (sets observation thread the main)
				.blockingSubscribe(
						// Log message to be printed from the subscriber
						w -> {
							logger.info("Received " + w + " on thread " + Thread.currentThread().getName());
							count.incrementAndGet();
						});

		assertTrue("We counted too many emissions... Check RxJava implementation.", count.get() == 10);
	}

	@Test
	public void skillDBPopulator() throws IOException, URISyntaxException {
		List<String> words = Files.readAllLines(Paths.get(this.getClass().getResource("/words.txt").toURI()));

		// Create a stream of the words list
		Flowable.fromIterable(words)
				
				// Offload the subscription and onNext operations on a new thread
				.subscribeOn(Schedulers.newThread())
				
				// Take the last 500 entries
				
				// convert to skill
				
				// bunch together every 50 words
				
				
				// Every 5 seconds, we send a bunch of 50 words (from previous step)
				.zipWith(Flowable.interval(5, TimeUnit.SECONDS), (w, t) -> w)
				
				// Subscribe to start the procedure (sets observation thread the main)
				.blockingSubscribe(skillList -> {
					// Store to the DB
					
				});

		logger.info("SkillRepo: " + skillRepo.count());
		logger.info("words: " + words.size());
		assertTrue("Skill Repository ended up with wrong entries... ", skillRepo.count() == 500);
	}
}
