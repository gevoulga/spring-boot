package ch.voulgarakis.icsc2018.recruitment.service;

import ch.voulgarakis.icsc2018.recruitment.model.Applicant;

public class TransactionFailRecruitmentServiceImpl extends RecruitmentServiceImpl {
    @Override
    public Applicant saveApplicant(Applicant applicant) {
        super.saveApplicant(applicant);
        throw new RuntimeException("Intentionally failed to save applicant!");
    }
}
