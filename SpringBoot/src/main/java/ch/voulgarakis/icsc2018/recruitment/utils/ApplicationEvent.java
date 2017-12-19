package ch.voulgarakis.icsc2018.recruitment.utils;

public class ApplicationEvent {
    private String applicant;
    private String vacancy;
    private double fitRatio;

    protected ApplicationEvent() {
        // Default constructor
    }

    public ApplicationEvent(String applicant, String vacancy, double fitRatio) {
        super();
        this.applicant = applicant;
        this.vacancy = vacancy;
        this.fitRatio = fitRatio;
    }

    public String getApplicant() {
        return applicant;
    }

    public String getVacancy() {
        return vacancy;
    }

    public double getFitRatio() {
        return fitRatio;
    }

    @Override
    public String toString() {
        return "[" + applicant + "] applied for [" + vacancy + "] and is [" + fitRatio + "] qualified";
    }
}
