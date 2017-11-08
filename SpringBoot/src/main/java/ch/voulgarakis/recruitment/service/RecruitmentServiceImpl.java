package ch.voulgarakis.recruitment.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.voulgarakis.recruitment.controller.MatchController;
import ch.voulgarakis.recruitment.dao.ApplicantRepository;
import ch.voulgarakis.recruitment.dao.SkillRepository;
import ch.voulgarakis.recruitment.dao.VacancyRepository;
import ch.voulgarakis.recruitment.model.Applicant;
import ch.voulgarakis.recruitment.model.Skill;
import ch.voulgarakis.recruitment.model.Vacancy;

@Service
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
        List<Skill> skills = saveSkills(applicant.getSkillSet());
        applicant.getSkillSet().clear();
        applicant.getSkillSet().addAll(skills);
        return appRepo.save(applicant);
    }

    @Override
    public Applicant loadApplicant(String name) {
        return appRepo.findByName(name);
    }

    @Override
    public Vacancy saveVacancy(Vacancy vacancy) {
        List<Skill> skills = saveSkills(vacancy.getRequiredSkills());
        vacancy.getRequiredSkills().clear();
        vacancy.getRequiredSkills().addAll(skills);
        return vacRepo.save(vacancy);
    }

    @Override
    public Vacancy loadVacancy(String name) {
        return vacRepo.findByName(name);
    }

    // Make sure all the skills mentioned in the list are present in the Skill Repository. If not, add them!
    private List<Skill> saveSkills(List<Skill> skills) {
        // If you do a parallel Stream, you end up with a typical race condition!!!...
        return skills.stream().map(skill -> {
            if (!skillRepo.existsByName(skill.getName()))
                return skillRepo.save(skill);
            else
                return skillRepo.findByName(skill.getName());
        }).collect(Collectors.toList());
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

        List<Skill> sV = vacancy.getRequiredSkills();
        List<Double> wV = vacancy.getRequiredSkillWeights();

        List<Skill> sA = applicant.getSkillSet();
        List<Double> wA = applicant.getSkillStrength();

        // Whether the applicant's got what's needed!
        boolean match = IntStream.range(0, sV.size())
                .mapToDouble(i -> sA.contains(sV.get(i)) ? wV.get(i) * wA.get(i) : 0d)
                .sum() >= vacancy.getFitThreshold();

        // Notify about the application attempt and the result!
        matchController.notify(applicant, vacancy, match);

        return match;
    }
}
