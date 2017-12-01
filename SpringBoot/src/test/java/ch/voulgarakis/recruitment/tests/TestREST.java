package ch.voulgarakis.recruitment.tests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.utils.SkillAndWeight;
import ch.voulgarakis.recruitment.tests.config.TestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = TestConfig.class) // Setup test with TestConfig
@ActiveProfiles("rest")
@Transactional
public class TestREST {
    Logger logger = LoggerFactory.getLogger(TestREST.class);
    String url = "http://localhost:8081/RecruitmentService";

    @Autowired
    private RestTemplate rest;

    // @Test
    public void crudSkillRestTest() {
        Skill skill = new Skill("Good");

        ///////////
        // Send a 1st POST
        ///////////
        ResponseEntity<Skill> postResponse1 = rest.postForEntity(url + "/skill", new HttpEntity<>(skill), Skill.class);
        logger.info("\n\tpostResponse1: {}", postResponse1);
        assertTrue("1st POST should have returned Created", postResponse1.getStatusCode().equals(HttpStatus.CREATED));

        ///////////
        // Send a 2nd POST
        ///////////
        ResponseEntity<Skill> postResponse2 = rest.exchange(url + "/skill", HttpMethod.POST, new HttpEntity<>(skill),
                Skill.class);
        logger.info("\n\tpostResponse2: {}", postResponse2);
        assertTrue("2nd POST should have returned Conflict", postResponse2.getStatusCode().equals(HttpStatus.CONFLICT));

        ///////////
        // Do a 1st PUT
        ///////////
        rest.put(url + "/skill/Good", skill);
        ResponseEntity<Skill> putResponse1 = rest.exchange(url + "/skill/Good", HttpMethod.PUT, new HttpEntity<>(skill),
                Skill.class);
        logger.info("\n\tputResponse1: {}", putResponse1);
        assertTrue("1st PUT should have returned Ok", putResponse1.getStatusCode().equals(HttpStatus.OK));

        ///////////
        // Do a 2nd PUT
        ///////////
        ResponseEntity<Skill> putResponse2 = rest.exchange(url + "/skill/Bad", HttpMethod.PUT, new HttpEntity<>(skill),
                Skill.class);
        logger.info("\n\tputResponse2: {}", putResponse2);
        assertTrue("2nd PUT should have returned NotFound", putResponse2.getStatusCode().equals(HttpStatus.NOT_FOUND));

        ///////////
        // Do a GET
        ///////////
        ResponseEntity<List<Skill>> getResponse = rest.exchange(url + "/skill", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Skill>>() {
                });
        logger.info("\n\tgetResponse: {}", getResponse);
        assertTrue("GET should have returned Ok", getResponse.getStatusCode().equals(HttpStatus.OK));
        assertTrue("GET should have returned a list of 1 skills after all", getResponse.getBody().size() == 1);

    }

    @Test
    public void createApplicantsAndVacancies() {

        ///////////
        // Johnny be Good! https://youtu.be/ZFo8-JqzSCM
        // Who is also bad and ugly! https://youtu.be/h1PfrmCGFnk
        ///////////
        ResponseEntity<Applicant> postResponse1 = rest.postForEntity(url + "/recruitment/applicant",
                new HttpEntity<>(new Applicant("Johnny", new SkillAndWeight(new Skill("Good"), 0.8d),
                        new SkillAndWeight(new Skill("Bad"), 0.2d), new SkillAndWeight(new Skill("Ugly"), 0.4d))),
                Applicant.class);
        logger.info("\n\tpostResponse1: {}", postResponse1);
        assertTrue("1st POST should have returned Created", postResponse1.getStatusCode().equals(HttpStatus.CREATED));

        ///////////
        // Claus -> maybe he can apply for the santa position?
        ///////////
        ResponseEntity<Applicant> postResponse2 = rest.postForEntity(
                url + "/recruitment/applicant", new HttpEntity<>(new Applicant("Claus",
                        new SkillAndWeight(new Skill("Giftbearer"), 1d), new SkillAndWeight(new Skill("Aviator"), 1d))),
                Applicant.class);
        logger.info("\n\tpostResponse2: {}", postResponse2);
        assertTrue("2nd POST should have returned Created", postResponse2.getStatusCode().equals(HttpStatus.CREATED));

        ///////////
        // Lion King Scar... A total scum, ugly as fuck and a lazy bastard!
        ///////////
        ResponseEntity<Applicant> postResponse3 = rest.postForEntity(url + "/recruitment/applicant",
                new HttpEntity<>(new Applicant("Scar", new SkillAndWeight(new Skill("Bad"), 1d),
                        new SkillAndWeight(new Skill("Lazy"), 1d), new SkillAndWeight(new Skill("Ugly"), 1d))),
                Applicant.class);
        logger.info("\n\tpostResponse3: {}", postResponse3);
        assertTrue("3rd POST should have returned Created", postResponse3.getStatusCode().equals(HttpStatus.CREATED));

        ///////////
        // The Santa vacancy... We want somebody bearing gifts and can fly!
        ///////////
        ResponseEntity<Vacancy> postResponse4 = rest.postForEntity(url + "/recruitment/vacancy",
                new HttpEntity<>(new Vacancy("Santa", 0.8d, new SkillAndWeight(new Skill("Giftbearer"), 0.9d),
                        new SkillAndWeight(new Skill("Aviator"), 0.9d))),
                Vacancy.class);
        logger.info("\n\tpostResponse4: {}", postResponse4);
        assertTrue("4th POST should have returned Created", postResponse4.getStatusCode().equals(HttpStatus.CREATED));

        ///////////
        // The job of a good guy, is to be a good guy!
        ///////////
        ResponseEntity<Vacancy> postResponse5 = rest.postForEntity(url + "/recruitment/vacancy",
                new HttpEntity<>(new Vacancy("BeAGoodGuy", 0.8d, new SkillAndWeight(new Skill("Good"), 0.9d))),
                Vacancy.class);
        logger.info("\n\tpostResponse5: {}", postResponse5);
        assertTrue("5th POST should have returned Created", postResponse5.getStatusCode().equals(HttpStatus.CREATED));

        ///////////
        // GET from all the CRUD DBs the number of their entries
        ///////////
        logger.info("Added Johnny, Claus, Santa, Scar, BeAGoodGuy...");
        String ret = "";
        // Now display the Skill repository
        ret += "\n" + String.format("Skill repository contents: %d",
                rest.getForEntity(url + "/skill", List.class).getBody().size());
        for (Object skill : rest.getForEntity(url + "/skill", List.class).getBody())
            ret += "\n" + String.format("\t%s", skill);
        ret += "\n" + String.format("----------------");
        // Now display the Application repository
        ret += "\n" + String.format("Application repository contents: %d",
                rest.getForEntity(url + "/applicant", List.class).getBody().size());
        for (Object app : rest.getForEntity(url + "/applicant", List.class).getBody())
            ret += "\n" + String.format("\t%s", app);
        ret += "\n" + String.format("----------------");
        // Now display the Vacancy repository
        ret += "\n" + String.format("Vacancy repository contents: %d",
                rest.getForEntity(url + "/vacancy", List.class).getBody().size());
        for (Object vac : rest.getForEntity(url + "/vacancy", List.class).getBody())
            ret += "\n" + String.format("\t%s", vac);
        ret += "\n" + String.format("----------------");
        logger.info(ret);
        assertTrue("Skill Repository ended up with more entries... Check for duplicates...",
                rest.getForEntity(url + "/skill", List.class).getBody().size() == 6);
        assertTrue("Applicant Repository ended up with more entries... Check for duplicates...",
                rest.getForEntity(url + "/applicant", List.class).getBody().size() == 3);
        assertTrue("Vacancy Repository ended up with more entries... Check for duplicates...",
                rest.getForEntity(url + "/vacancy", List.class).getBody().size() == 2);

    }
}
