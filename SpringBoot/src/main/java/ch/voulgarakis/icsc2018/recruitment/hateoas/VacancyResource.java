package ch.voulgarakis.icsc2018.recruitment.hateoas;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import ch.voulgarakis.icsc2018.recruitment.controller.CRUDApplicantController;
import ch.voulgarakis.icsc2018.recruitment.controller.CRUDSkillController;
import ch.voulgarakis.icsc2018.recruitment.controller.CRUDVacancyController;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;

public class VacancyResource extends ResourceSupport {
    private final Vacancy vacancy;

    public VacancyResource(Vacancy vacancy) {
        this.vacancy = vacancy;

        add(linkTo(methodOn(RecruitmentControllerWithHateoas.class).loadVacancy(vacancy.getName())).withSelfRel());
        add(linkTo(methodOn(CRUDVacancyController.class).listAllVacancies(null)).withRel("vacancies"));
        add(linkTo(methodOn(CRUDApplicantController.class).listAllApplicants(null)).withRel("applicants"));
        add(linkTo(methodOn(CRUDSkillController.class).listAllSkills(null)).withRel("skills"));
    }

    public Vacancy getVacancy() {
        return vacancy;
    }
}