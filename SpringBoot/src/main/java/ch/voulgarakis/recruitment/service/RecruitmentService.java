package ch.voulgarakis.recruitment.service;

import org.springframework.stereotype.Service;

import ch.voulgarakis.recruitment.model.Applicant;
import ch.voulgarakis.recruitment.model.Vacancy;

@Service
public interface RecruitmentService {
    public boolean existsApplicant(Applicant applicant);
    public Applicant loadApplicant(String name);
    public Applicant saveApplicant(Applicant applicant);

    public boolean existsVacancy(Vacancy vacancy);
    public Vacancy loadVacancy(String name);
    public Vacancy saveVacancy(Vacancy vacancy);

    public boolean apply(Applicant applicant, Vacancy vacancy);

    public String info();
}
