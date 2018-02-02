package ch.voulgarakis.recruitment.tests;

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
import ch.voulgarakis.icsc2018.recruitment.dao.SkillRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.utils.SkillAndWeight;
import ch.voulgarakis.recruitment.tests.config.TestConfig;

/**
 * -EXC 1
 * 
 * Open and study the implementation of {@link Vacancy}.
 * 
 * Open {@link Applicant}. Based on the Class Diagram, you are asked to
 * implement the {@link Applicant}. Correct implementation of {@link Applicant}
 * will result in crudPopulateDB() succeeding.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class) // Setup test with TestConfig
@ActiveProfiles("jpa")
// @Transactional // Rollback the DB when test is finished!
public class TestApplicant {
	Logger logger = LoggerFactory.getLogger(TestApplicant.class);

	@Autowired
	private RecruitmentService rs;
	@Autowired
	private SkillRepository skillRepo;
	@Autowired
	private ApplicantRepository appRepo;

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
		// 2 Applicants added in CRUD fashion. The corresponding skills are not
		/////////// added in the SkillDB!
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
}
