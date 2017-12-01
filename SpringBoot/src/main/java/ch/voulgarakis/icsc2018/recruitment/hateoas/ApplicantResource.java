package ch.voulgarakis.icsc2018.recruitment.hateoas;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import ch.voulgarakis.icsc2018.recruitment.controller.CRUDApplicantController;
import ch.voulgarakis.icsc2018.recruitment.controller.CRUDSkillController;
import ch.voulgarakis.icsc2018.recruitment.controller.CRUDVacancyController;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;

public class ApplicantResource extends ResourceSupport {
    private final Applicant applicant;

    public ApplicantResource(Applicant applicant) {
        this.applicant = applicant;

        // What operations are related to applicant?
        add(linkTo(methodOn(RecruitmentControllerWithHateoas.class).loadApplicant(applicant.getName())).withSelfRel());
        add(linkTo(methodOn(CRUDApplicantController.class).listAllApplicants(null)).withRel("applicants"));
        add(linkTo(methodOn(CRUDSkillController.class).listAllSkills(null)).withRel("skills"));
        add(linkTo(methodOn(CRUDVacancyController.class).listAllVacancies(null)).withRel("vacancies"));
    }

    public Applicant getApplicant() {
        return applicant;
    }
}