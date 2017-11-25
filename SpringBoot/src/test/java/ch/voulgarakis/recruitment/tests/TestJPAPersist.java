package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertTrue;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.voulgarakis.recruitment.dao.SkillRepository;
import ch.voulgarakis.recruitment.model.Skill;
import ch.voulgarakis.recruitment.tests.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class) // Setup test with TestConfig
@Transactional // Rollback the DB when test is finished!
public class TestJPAPersist {
    Logger logger = LoggerFactory.getLogger(TestJPAPersist.class);

    @Autowired
    private SkillRepository skillRepo;

    @Test
    public void populateDB() {
        Skill skill1 = new Skill("Good");
        Skill skill2 = new Skill("Good");
        Skill skill3 = new Skill("Good");
        Skill skill4 = new Skill("Good");

        skillRepo.save(skill1);
        skillRepo.save(skill2);
        skillRepo.save(skill3);
        skillRepo.save(skill4);

        ///////////
        // Now display the Repositories
        ///////////
        String ret = "";
        // Now display the Skill repository
        ret += "\n" + String.format("Skill repository contents: %d", skillRepo.count());
        for (Skill skill : skillRepo.findAll())
            ret += "\n" + String.format("\t%s", skill);
        ret += "\n" + String.format("----------------");
        System.out.println(ret);
    }

    @After
    public void cleanupDB() {
        // Now display the Application repository
        skillRepo.deleteAll();
        // Check the test
        assertTrue("Skill Repository ended up with more entries... Check for duplicates...", skillRepo.count() == 0);
    }
}
