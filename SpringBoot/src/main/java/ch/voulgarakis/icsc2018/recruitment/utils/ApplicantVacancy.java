package ch.voulgarakis.icsc2018.recruitment.utils;

import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;

public class ApplicantVacancy {
    private Applicant applicant;
    private Vacancy vacancy;

    protected ApplicantVacancy() {
        // Empty constructor
    }

    public ApplicantVacancy(Applicant applicant, Vacancy vacancy) {
        super();
        this.applicant = applicant;
        this.vacancy = vacancy;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Vacancy getVacancy() {
        return vacancy;
    }
}
