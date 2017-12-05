package ch.voulgarakis.icsc2018.recruitment.controller;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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

import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.service.RecruitmentService;
import ch.voulgarakis.icsc2018.recruitment.utils.ApplicantVacancy;
import ch.voulgarakis.icsc2018.recruitment.utils.ApplicationResult;
import ch.voulgarakis.icsc2018.recruitment.utils.OperationNotSupportedException;

@RestController
@RequestMapping("/recruitment")
public class RecruitmentController {
    @Autowired
    private RecruitmentService rs;

    @RequestMapping(value = "/applicant/{name}", method = RequestMethod.GET)
    public ResponseEntity<Applicant> loadApplicant(@PathVariable("name") String name) {
        Applicant applicant = rs.loadApplicant(name);
        if (applicant != null)
            return new ResponseEntity<>(applicant, HttpStatus.FOUND);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/applicant", method = RequestMethod.POST)
    public ResponseEntity<Applicant> saveApplicant(@RequestBody Applicant applicant) {
        Applicant newApplicant = rs.saveApplicant(applicant);
        return ResponseEntity
                .created(fromMethodCall(on(RecruitmentController.class).loadApplicant(newApplicant.getName()))
                        .buildAndExpand().toUri())
                .body(newApplicant);
    }

    @RequestMapping(value = "/vacancy/{name}", method = RequestMethod.GET)
    public ResponseEntity<Vacancy> loadVacancy(@PathVariable("name") String name) {
        Vacancy vacancy = rs.loadVacancy(name);
        if (vacancy != null)
            return new ResponseEntity<>(vacancy, HttpStatus.FOUND);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/vacancy", method = RequestMethod.POST)
    public ResponseEntity<Vacancy> saveVacancy(@RequestBody Vacancy vacancy) {
        Vacancy newVacancy = rs.saveVacancy(vacancy);
        return ResponseEntity.created(fromMethodCall(on(RecruitmentController.class).loadVacancy(newVacancy.getName()))
                .buildAndExpand().toUri()).body(newVacancy);
    }

    @RequestMapping(value = "/apply", method = RequestMethod.PUT)
    public ResponseEntity<ApplicationResult> apply(@RequestBody ApplicantVacancy applicantVacancy) {
        return new ResponseEntity<>(rs.apply(applicantVacancy.getApplicant(), applicantVacancy.getVacancy()),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/apply", method = RequestMethod.PUT, params = { "applicantId", "vacancyId" })
    public ResponseEntity<ApplicationResult> apply(@PathParam("applicantId") long applicantId,
            @PathParam("vacancyId") long vacancyId) {
        return new ResponseEntity<>(rs.apply(applicantId, vacancyId), HttpStatus.OK);
    }

    @RequestMapping(value = "/apply", method = RequestMethod.PUT, params = { "applicantName", "vacancyName" })
    public ResponseEntity<ApplicationResult> apply(@PathParam("applicantName") String applicantName,
            @PathParam("vacancyName") String vacancyName) {
        return new ResponseEntity<>(rs.apply(rs.loadApplicant(applicantName), rs.loadVacancy(vacancyName)),
                HttpStatus.OK);
    }

    // ------------------- Exception Handler for the controller-----------------------------
    @ExceptionHandler({ OperationNotSupportedException.class })
    protected ResponseEntity<String> handleUnknownException(OperationNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
