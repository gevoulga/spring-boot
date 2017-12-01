package ch.voulgarakis.icsc2018.recruitment.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import ch.voulgarakis.icsc2018.recruitment.controller.MatchController;
import ch.voulgarakis.icsc2018.recruitment.dao.ApplicantRepository;
import ch.voulgarakis.icsc2018.recruitment.dao.SkillRepository;
import ch.voulgarakis.icsc2018.recruitment.dao.VacancyRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;

@Transactional
public class RecruitmentServiceImpl implements RecruitmentService {
    @Autowired
    private SkillRepository skillRepo;
    @Autowired
    private VacancyRepository vacRepo;
    @Autowired
    private ApplicantRepository appRepo;
    @Autowired
    private MatchController matchController;

    @Override
    public Applicant saveApplicant(Applicant applicant) {
        // applicant.getSkillSet().stream().forEach(skill -> skillRepo.save(skill));
        synchronized (skillRepo) {
            applicant.getSkillSet().replaceAll(skill -> {
                Skill s = skillRepo.findByName(skill.getName());
                return s != null ? s : skillRepo.save(new Skill(skill.getName()));
            });
        }
        return appRepo.save(applicant);
    }

    @Override
    public Applicant loadApplicant(String name) {
        return appRepo.findByName(name);
    }

    @Override
    public boolean existsApplicant(Applicant applicant) {
        return appRepo.existsByName(applicant.getName());
    }

    @Override
    public Vacancy saveVacancy(Vacancy vacancy) {
        // vacancy.getRequiredSkills().stream().forEach(skill -> skillRepo.save(skill));
        synchronized (skillRepo) {
            vacancy.getRequiredSkills().replaceAll(skill -> {
                Skill s = skillRepo.findByName(skill.getName());
                return s != null ? s : skillRepo.save(new Skill(skill.getName()));
            });
        }
        return vacRepo.save(vacancy);
    }

    @Override
    public Vacancy loadVacancy(String name) {
        return vacRepo.findByName(name);
    }

    @Override
    public boolean existsVacancy(Vacancy vacancy) {
        return vacRepo.existsByName(vacancy.getName());
    }

    @Override
    public String info() {
        String ret = "";
        // Now display the Skill repository
        ret += "\n" + String.format("Skill repository contents: %d", skillRepo.count());
        for (Skill skill : skillRepo.findAll())
            ret += "\n" + String.format("\t%s", skill);
        ret += "\n" + String.format("----------------");

        // Now display the Application repository
        ret += "\n" + String.format("Application repository contents: %d", appRepo.count());
        for (Applicant app : appRepo.findAll())
            ret += "\n" + String.format("\t%s", app);
        ret += "\n" + String.format("----------------");

        // Now display the Vacancy repository
        ret += "\n" + String.format("Vacancy repository contents: %d", vacRepo.count());
        for (Vacancy vac : vacRepo.findAll())
            ret += "\n" + String.format("\t%s", vac);
        ret += "\n" + String.format("----------------");
        return ret;
    }

    /**
     * success = Sum(s(true|false)*wA(0-1)*wV(0-1)) > fitThres
     */
    @Override
    public boolean apply(Applicant applicant, Vacancy vacancy) {
        // Store the fact that the applicant applied for the vacancy (Bi-directional)
        vacancy.getApplicants().add(applicant);
        applicant.getVacancies().add(vacancy);

        List<String> sV = vacancy.getRequiredSkills().parallelStream().map(s -> s.getName())
                .collect(Collectors.toList());
        List<Double> wV = vacancy.getRequiredSkillWeights();

        List<String> sA = applicant.getSkillSet().parallelStream().map(s -> s.getName()).collect(Collectors.toList());
        List<Double> wA = applicant.getSkillStrength();

        // Whether the applicant's got what's needed!
        boolean match = IntStream.range(0, sV.size())
                .mapToDouble(i -> sA.contains(sV.get(i)) ? wV.get(i) * wA.get(i) : 0d)
                .sum() >= vacancy.getFitThreshold();

        // Notify about the application attempt and the result!
        matchController.notify(applicant, vacancy, match);

        return match;
    }

    @Override
    public boolean apply(long applicantId, long vacancyId) {
        return apply(appRepo.findOne(applicantId), vacRepo.findOne(vacancyId));
    }
}
