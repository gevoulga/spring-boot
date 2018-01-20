package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

import ch.voulgarakis.icsc2018.recruitment.dao.ApplicantRepository;
import ch.voulgarakis.icsc2018.recruitment.dao.ApplicationRepository;
import ch.voulgarakis.icsc2018.recruitment.dao.SkillRepository;
import ch.voulgarakis.icsc2018.recruitment.dao.VacancyRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.utils.SkillAndWeight;
import ch.voulgarakis.recruitment.tests.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class) // Setup test with TestConfig
@ActiveProfiles("jpa")
// @Transactional // Rollback the DB when test is finished!
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
    @Autowired
    ApplicationRepository applRepo;

    @Test
    public void crudPopulateDB() {
        ///////////
        // Multiple entries of the same skill
        ///////////
        Skill skill1 = new Skill("Good");
        Skill skill2 = new Skill("Good");
        Skill skill3 = new Skill("Good");
        Skill skill4 = new Skill("Good");
        // In the end we should end up with only one skill
        skillRepo.save(skill1);
        skillRepo.save(skill2);
        skillRepo.save(skill3);
        skillRepo.save(skill4);

        logger.info("Added Good Skill...");
        logger.info(rs.info());
        assertTrue("Skill Repository ended up with more entries... Check for duplicates...", skillRepo.count() == 1);

        ///////////
        // 2 Applicants added in CRUD fashion. The corresponding skills are not added in the SkillDB!
        ///////////
        skillRepo.save(new Skill("Clumsy"));
        skillRepo.save(new Skill("Obsessed"));
        skillRepo.save(new Skill("Clever"));
        skillRepo.save(new Skill("Fast"));

        // Tom!
        appRepo.save(new Applicant("Tom", new SkillAndWeight(new Skill("Clumsy"), 0.8d),
                new SkillAndWeight(new Skill("Obsessed"), 0.2d)));
        // And Jerry!
        appRepo.save(new Applicant("Jerry", new SkillAndWeight(new Skill("Clever"), 1d),
                new SkillAndWeight(new Skill("Fast"), 1d)));

        logger.info("Added Tom & Jerry...");
        logger.info(rs.info());
        assertTrue("Skill Repository ended up with more entries... Check for duplicates...", skillRepo.count() == 5);
        assertTrue("Applicant Repository ended up with more entries... Check for duplicates...", appRepo.count() == 2);
    }

    // @Test
    public void servicePopulateDB() {
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
        rs.saveVacancy(new Vacancy("Santa", new SkillAndWeight(new Skill("Giftbearer"), 0.9d),
                new SkillAndWeight(new Skill("Aviator"), 0.9d)));

        // The job of a good guy, is to be a good guy!
        rs.saveVacancy(new Vacancy("BeAGoodGuy", new SkillAndWeight(new Skill("Good"), 0.9d)));

        logger.info("Added Johnny, Claus, Santa, Scar, BeAGoodGuy...");
        logger.info(rs.info());
        assertTrue("Skill Repository ended up with more entries... Check for duplicates...", skillRepo.count() == 6);
        assertTrue("Applicant Repository ended up with more entries... Check for duplicates...", appRepo.count() == 3);
        assertTrue("Vacancy Repository ended up with more entries... Check for duplicates...", vacRepo.count() == 2);

    }

    @Test
    public void applyClausForSanta() {
        // Populate (again) the DB
        servicePopulateDB();

        // Get Claus applicant
        Applicant claus = rs.loadApplicant("Claus");
        // Get Santa position
        Vacancy santa = rs.loadVacancy("Santa");

        logger.info("Got Santa and Claus...");
        logger.info(rs.info());
        assertNotNull("Claus should already be in the DB.", claus);
        assertNotNull("Santa should already be in the DB.", santa);

        // Claus can apply to become santa?
        double fit = rs.apply(claus, santa);

        logger.info("Applied Claus for Santa...");
        logger.info(rs.info());
        assertTrue("Claus should have gotten the job of Santa.", fit >= 0.9);
        assertTrue("Application Repository ended up with more entries... Check for duplicates...",
                applRepo.count() == 1);
    }
}
