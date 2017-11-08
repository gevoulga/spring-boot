package ch.voulgarakis.recruitment.utils;

public class ApplicationResult {
    private String applicant;
    private String vacancy;
    private boolean match;

    public ApplicationResult(String applicant, String vacancy, boolean match) {
        super();
        this.applicant = applicant;
        this.vacancy = vacancy;
        this.match = match;
    }

    public String getApplicant() {
        return applicant;
    }

    public String getVacancy() {
        return vacancy;
    }

    public boolean isMatch() {
        return match;
    }
}
