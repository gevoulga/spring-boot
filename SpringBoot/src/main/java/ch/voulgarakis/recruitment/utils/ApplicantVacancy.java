package ch.voulgarakis.recruitment.utils;

import ch.voulgarakis.recruitment.model.Applicant;
import ch.voulgarakis.recruitment.model.Vacancy;

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
