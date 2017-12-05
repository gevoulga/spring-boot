package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.voulgarakis.icsc2018.recruitment.events.TodoEventListener;
import ch.voulgarakis.icsc2018.recruitment.events.TodoEventProducer;
import ch.voulgarakis.recruitment.tests.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = TestConfig.class) // Setup test with TestConfig
@ActiveProfiles("jpa")
public class TestTodoEvents {
    @Autowired
    private TodoEventProducer eventProducer;
    @Autowired
    private TodoEventListener eventListener;

    @Test
    public void test() {
        eventProducer.createTodo(true);
        eventProducer.createTodo(false);

        assertTrue("First Event should be a NOW Todo...", eventListener.getEvents().get(0).isNow());
        assertFalse("First Event should be a LATER Todo...", eventListener.getEvents().get(1).isNow());
    }
}
