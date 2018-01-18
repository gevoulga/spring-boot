package ch.voulgarakis.icsc2018.recruitment.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ch.voulgarakis.icsc2018.recruitment.controller.MatchController;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Application;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;

@Transactional
@RefreshScope
public class RecruitmentMicroservice implements RecruitmentService {
	@Autowired
	protected RestTemplate rest;

	@Autowired
	private MatchController matchController;

	private void saveSkills(Collection<Skill> skills) {
		// The REST requests to store all the skills defined in the Applicant
		skills.stream().forEach(skill -> {
			rest.postForEntity("http://skill-service/skill", new HttpEntity<>(skill), Skill.class);
		});
	}

	@Override
	public Applicant saveApplicant(Applicant applicant) {
		// Save the skills
		saveSkills(applicant.getSkillSet());

		// The REST request to store the Applicant
		ResponseEntity<Applicant> postResponse = rest.postForEntity("http://applicant-service/applicant",
				new HttpEntity<>(applicant), Applicant.class);

		// Return the response body, which contains the applicants
		return postResponse.getBody();
	}

	@Override
	public Applicant loadApplicant(String name) {
		return rest.getForEntity("http://applicant-service/applicant?name=" + name, Applicant.class).getBody();
	}

	@Override
	public boolean existsApplicant(Applicant applicant) {
		return rest.getForEntity("http://applicant-service/applicant?name=" + applicant.getName(), Applicant.class)
				.getStatusCode().equals(HttpStatus.FOUND);
	}

	@Override
	public Vacancy saveVacancy(Vacancy vacancy) {
		saveSkills(vacancy.getRequiredSkills());

		// The REST request to store the Vacancy
		ResponseEntity<Vacancy> postResponse = rest.postForEntity("http://vacancy-service/vacancy",
				new HttpEntity<>(vacancy), Vacancy.class);

		// Return the response body, which contains the applicants
		return postResponse.getBody();
	}

	@Override
	public Vacancy loadVacancy(String name) {
		return rest.getForEntity("http://vacancy-service/vacancy?name=" + name, Vacancy.class).getBody();
	}

	@Override
	public boolean existsVacancy(Vacancy vacancy) {
		return rest.getForEntity("http://vacancy-service/vacancy?name=" + vacancy.getName(), Vacancy.class)
				.getStatusCode().equals(HttpStatus.FOUND);
	}

	@Override
	public String info() {
		String ret = "";

		List<Skill> skills = rest.exchange("http://skill-service/skill", HttpMethod.GET, HttpEntity.EMPTY,
				new ParameterizedTypeReference<List<Skill>>() {
				}).getBody();
		List<Applicant> applicants = rest.exchange("http://applicant-service/applicant", HttpMethod.GET,
				HttpEntity.EMPTY, new ParameterizedTypeReference<List<Applicant>>() {
				}).getBody();
		List<Vacancy> vacancies = rest.exchange("http://vacancy-service/vacancy", HttpMethod.GET, HttpEntity.EMPTY,
				new ParameterizedTypeReference<List<Vacancy>>() {
				}).getBody();
				// List<Application> applications =
				// rest.exchange("http://application-service/application",
				// HttpMethod.GET,
				// HttpEntity.EMPTY, new
				// ParameterizedTypeReference<List<Application>>() {
				// }).getBody();

		// Now display the Skill repository
		ret += "\n" + String.format("Skill repository contents: %d", skills.size());
		for (Skill skill : skills)
			ret += "\n" + String.format("\t%s", skill);
		ret += "\n" + String.format("----------------");

		// Now display the Application repository
		ret += "\n" + String.format("Application repository contents: %d", applicants.size());
		for (Applicant app : applicants)
			ret += "\n" + String.format("\t%s", app);
		ret += "\n" + String.format("----------------");

		// Now display the Vacancy repository
		ret += "\n" + String.format("Vacancy repository contents: %d", vacancies.size());
		for (Vacancy vac : vacancies)
			ret += "\n" + String.format("\t%s", vac);
		ret += "\n" + String.format("----------------");
		return ret;
	}

	/**
	 * success = Sum(s(true|false)*wA(0-1)*wV(0-1)) > fitThres
	 */
	@Override
	public double apply(Applicant applicant, Vacancy vacancy) {
		List<String> sV = vacancy.getRequiredSkills().parallelStream().map(s -> s.getName())
				.collect(Collectors.toList());
		List<Double> wV = vacancy.getRequiredSkillWeights();

		List<String> sA = applicant.getSkillSet().parallelStream().map(s -> s.getName()).collect(Collectors.toList());
		List<Double> wA = applicant.getSkillStrength();

		// Whether the applicant's got what's needed!
		double fitRatio = IntStream.range(0, sV.size())
				.mapToDouble(i -> sA.contains(sV.get(i)) ? wV.get(i) * wA.get(i) : 0d).average().orElse(0);

		// The application to be persisted
		Application application = new Application(applicant, vacancy, fitRatio);
		// PUT request to the application microservice
		rest.postForEntity("http://application-service/application/", new HttpEntity<>(application), Application.class);

		// Notify about the application if a good fit -> CrossCuttingConcern?
		// if (fitRatio > 0.75)
		// matchController.notify(applicant, vacancy, fitRatio);

		return fitRatio;
	}

	@Override
	public double apply(long applicantId, long vacancyId) {
		Applicant applicant = rest.getForEntity("http://applicant-service/applicant/" + applicantId, Applicant.class)
				.getBody();
		Vacancy vacancy = rest.getForEntity("http://vacancy-service/vacancy/" + vacancyId, Vacancy.class).getBody();

		return apply(applicant, vacancy);
	}
}
