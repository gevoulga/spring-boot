package ch.voulgarakis.icsc2018.recruitment.hateoas;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.utils.ApplicantVacancy;
import ch.voulgarakis.icsc2018.recruitment.utils.ApplicationResult;
import ch.voulgarakis.icsc2018.recruitment.utils.OperationNotSupportedException;

@RestController
@RequestMapping("/recruitment-with-hateoas")
public class RecruitmentControllerWithHateoas {
    @Autowired
    private RecruitmentService rs;

    @RequestMapping(value = "/applicant/{name}", method = RequestMethod.GET)
    public ResponseEntity<ApplicantResource> loadApplicant(@PathVariable("name") String name) {
        Applicant applicant = rs.loadApplicant(name);
        ResponseEntity.notFound();
        if (applicant != null)
            return new ResponseEntity<>(new ApplicantResource(applicant), HttpStatus.FOUND);
        else
            return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/applicant", method = RequestMethod.POST)
    public ResponseEntity<ApplicantResource> saveApplicant(@RequestBody Applicant applicant) {
        Applicant app = rs.saveApplicant(applicant);
        return ResponseEntity
                .created(
                        // The URL the resource has been created at
                        // MvcUriComponentsBuilder.
                        // fromController(getClass()).path("/{id}")
                        // .buildAndExpand(app.getName()).toUri())
                        MvcUriComponentsBuilder.fromMethodCall(
                                MvcUriComponentsBuilder.on(RecruitmentControllerWithHateoas.class).loadApplicant(null))
                                .buildAndExpand(app.getName()).toUri())
                // The response body
                .body(new ApplicantResource(app));
    }

    @RequestMapping(value = "/vacancy/{name}", method = RequestMethod.GET)
    public ResponseEntity<VacancyResource> loadVacancy(@PathVariable("name") String name) {
        Vacancy vacancy = rs.loadVacancy(name);
        if (vacancy != null)
            return new ResponseEntity<>(new VacancyResource(vacancy), HttpStatus.FOUND);
        else
            return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/vacancy", method = RequestMethod.POST)
    public ResponseEntity<VacancyResource> saveVacancy(@RequestBody Vacancy vacancy) {
        Vacancy vac = rs.saveVacancy(vacancy);
        return ResponseEntity
                .created(MvcUriComponentsBuilder
                        .fromMethodCall(
                                MvcUriComponentsBuilder.on(RecruitmentControllerWithHateoas.class).loadVacancy(null))
                        .buildAndExpand(vac.getName()).toUri())
                // The response body
                .body(new VacancyResource(vac));
    }

    @RequestMapping(value = "/apply", method = RequestMethod.PUT)
    public ResponseEntity<ApplicationResult> apply(@RequestBody ApplicantVacancy applicantVacancy) {
        return ResponseEntity.ok(rs.apply(applicantVacancy.getApplicant(), applicantVacancy.getVacancy()));
    }

    @RequestMapping(value = "/apply", method = RequestMethod.PUT, params = { "applicantId", "vacancyId" })
    public ResponseEntity<ApplicationResult> apply(@PathParam("applicantId") long applicantId,
            @PathParam("vacancyId") long vacancyId) {
        return ResponseEntity.ok(rs.apply(applicantId, vacancyId));
    }

    @RequestMapping(value = "/apply", method = RequestMethod.PUT, params = { "applicantName", "vacancyName" })
    public ResponseEntity<ApplicationResult> apply(@PathParam("applicantName") String applicantName,
            @PathParam("vacancyName") String vacancyName) {
        return ResponseEntity.ok(rs.apply(rs.loadApplicant(applicantName), rs.loadVacancy(vacancyName)));
    }

    // ------------------- Exception Handler for the controller-----------------------------
    @ExceptionHandler({ OperationNotSupportedException.class })
    protected ResponseEntity<String> handleUnknownException(OperationNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
