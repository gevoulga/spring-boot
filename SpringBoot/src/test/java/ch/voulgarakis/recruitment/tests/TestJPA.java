package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.voulgarakis.recruitment.dao.ApplicantRepository;
import ch.voulgarakis.recruitment.dao.SkillRepository;
import ch.voulgarakis.recruitment.dao.VacancyRepository;
import ch.voulgarakis.recruitment.model.Applicant;
import ch.voulgarakis.recruitment.model.Skill;
import ch.voulgarakis.recruitment.model.Vacancy;
import ch.voulgarakis.recruitment.service.RecruitmentService;
import ch.voulgarakis.recruitment.tests.config.TestConfig;
import ch.voulgarakis.recruitment.utils.SkillAndWeight;

@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class) // Setup test with TestConfig
@Transactional // Rollback the DB when test is finished!
public class TestJPA {
    Logger logger = LoggerFactory.getLogger(TestJPA.class);

    @Autowired
    RecruitmentService rs;
    @Autowired
    SkillRepository skillRepo;
    @Autowired
    ApplicantRepository appRepo;
    @Autowired
    VacancyRepository vacRepo;

    @Before
    public void populateDB() {

        ///////////
        // Write a few Applicants!
        ///////////
        // Johnny be Good! https://youtu.be/ZFo8-JqzSCM
        // Who is also bad and ugly! https://youtu.be/h1PfrmCGFnk
        rs.saveApplicant(new Applicant("Johnny", new SkillAndWeight(new Skill("Good"), 0.8d),
                new SkillAndWeight(new Skill("Bad"), 0.2d), new SkillAndWeight(new Skill("Ugly"), 0.4d)));

        // Claus -> maybe he can apply for the santa position?
        rs.saveApplicant(new Applicant("Claus", new SkillAndWeight(new Skill("Giftbearer"), 1d),
                new SkillAndWeight(new Skill("Aviator"), 1d)));

        // Lion King Scar... A total scum, ugly as fuck and a lazy bastard!
        rs.saveApplicant(new Applicant("Scar", new SkillAndWeight(new Skill("Bad"), 1d),
                new SkillAndWeight(new Skill("Lazy"), 1d), new SkillAndWeight(new Skill("Ugly"), 1d)));

        ///////////
        // Write a few Vacancies!
        ///////////
        // The Santa vacancy... We want somebody bearing gifts and can fly!
        rs.saveVacancy(new Vacancy("Santa", 0.8d, new SkillAndWeight(new Skill("Giftbearer"), 0.9d),
                new SkillAndWeight(new Skill("Aviator"), 0.9d)));

        // The job of a good guy, is to be a good guy!
        rs.saveVacancy(new Vacancy("BeAGoodGuy", 0.8d, new SkillAndWeight(new Skill("Good"), 0.9d)));

        ///////////
        // Now display the Repositories
        ///////////
        logger.info(rs.info());

        // Check the test
        assertTrue("Skill Repository ended up with more entries... Check for duplicates...", skillRepo.count() == 6);
        assertTrue("Applicant Repository ended up with more entries... Check for duplicates...", appRepo.count() == 3);
        assertTrue("Vacancy Repository ended up with more entries... Check for duplicates...", vacRepo.count() == 2);

    }

    @Test
    public void applyClausForSanta() {
        // Claus can apply to become santa?
        Applicant claus = rs.loadApplicant("Claus");
        Vacancy santa = rs.loadVacancy("Santa");
        assertNotNull("Claus should already be in the DB.", claus);
        assertNotNull("Santa should already be in the DB.", santa);

        boolean success = rs.apply(claus, santa);
        assertTrue("Claus should have gotten the job of Santa.", success);
    }

    @After
    public void cleanupDB() {

        // Now display the Application repository
        appRepo.deleteAll();
        vacRepo.deleteAll();
        skillRepo.deleteAll();

        // Check the test
        assertTrue("Skill Repository ended up with more entries... Check for duplicates...", skillRepo.count() == 0);
        assertTrue("Applicant Repository ended up with more entries... Check for duplicates...", appRepo.count() == 0);
        assertTrue("Vacancy Repository ended up with more entries... Check for duplicates...", vacRepo.count() == 0);

    }
}
