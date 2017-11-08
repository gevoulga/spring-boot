package ch.voulgarakis.service;

import ch.voulgarakis.model.Applicant;
import ch.voulgarakis.model.Vacancy;

public interface RecruitmentService {
    public Applicant saveApplicant(Applicant applicant);

    public Applicant loadApplicant(String name);

    public Vacancy saveVacancy(Vacancy vacancy);

    public Vacancy loadVacancy(String name);

    public boolean apply(Applicant applicant, Vacancy vacancy);

    public String info();
}
