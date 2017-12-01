package ch.voulgarakis.icsc2018.recruitment.utils;

public class ApplicationResult {
    private String applicant;
    private String vacancy;
    private boolean match;

    protected ApplicationResult() {
        // Default constructor
    }

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

    @Override
    public String toString() {
        return "[" + applicant + "] applied for [" + vacancy + "] and was [" + match + "]";
    }
}
