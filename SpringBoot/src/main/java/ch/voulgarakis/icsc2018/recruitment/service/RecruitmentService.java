package ch.voulgarakis.icsc2018.recruitment.service;

import org.springframework.stereotype.Service;

import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.utils.ApplicationResult;

@Service
public interface RecruitmentService {
    public boolean existsApplicant(Applicant applicant);

    public Applicant loadApplicant(String name);

    public Applicant saveApplicant(Applicant applicant);

    public boolean existsVacancy(Vacancy vacancy);

    public Vacancy loadVacancy(String name);

    public Vacancy saveVacancy(Vacancy vacancy);

    public ApplicationResult apply(Applicant applicant, Vacancy vacancy);

    public ApplicationResult apply(long applicantId, long vacancyId);

    public String info();
}
