package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.voulgarakis.icsc2018.recruitment.dao.ApplicantRepository;
import ch.voulgarakis.icsc2018.recruitment.dao.SkillRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentServiceImpl;
import ch.voulgarakis.icsc2018.recruitment.service.TransactionFailRecruitmentServiceImpl;
import ch.voulgarakis.icsc2018.recruitment.utils.SkillAndWeight;
import ch.voulgarakis.recruitment.tests.config.TransactionTestConfig;

/**
 * EXC 4:
 * 
 * Have a look at the implementation of {@link RecruitmentServiceImpl} and
 * {@link TransactionFailRecruitmentServiceImpl}. As you can see, the idea
 * behind the code (in {@link TransactionFailRecruitmentServiceImpl}) is to
 * simulate an exception happening, when executing the {@code saveApplicant()}
 * method. Indeed, this test sets up as our backend Service the
 * {@link TransactionFailRecruitmentServiceImpl}.
 * 
 * When the failure happens, all our existing database operations should be
 * rolled back, to avoid having polluted entries entering our DB. Do NOT edit
 * anything in {@link TransactionFailRecruitmentServiceImpl}.
 * 
 * Edit the {@link RecruitmentServiceImpl} accordingly, so that we have the
 * fore-mentioned functionality. Doing so should allow correctly passing this
 * test.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// Setup test with TransactionTestConfig
@ContextConfiguration(classes = TransactionTestConfig.class)
@ActiveProfiles("jpa")
public class TestTransactional {
	Logger logger = LoggerFactory.getLogger(TestTransactional.class);

	@Autowired
	RecruitmentService rs;
	@Autowired
	SkillRepository skillRepo;
	@Autowired
	ApplicantRepository appRepo;

	@Test
	public void testTransactional() {
		///////////
		// Write an Applicant!
		///////////
		try {
			rs.saveApplicant(new Applicant("Johnny", new SkillAndWeight(new Skill("Good"), 0.8d),
					new SkillAndWeight(new Skill("Bad"), 0.2d), new SkillAndWeight(new Skill("Ugly"), 0.4d)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("Threw exception while saving Johnny...");
		logger.info(rs.info());
		assertTrue("Skill Repository ended up with entries... Transaction should have rolled back...",
				skillRepo.count() == 0);
		assertTrue("Applicant Repository ended up with entries... Transaction should have rolled back...",
				appRepo.count() == 0);
	}
}
