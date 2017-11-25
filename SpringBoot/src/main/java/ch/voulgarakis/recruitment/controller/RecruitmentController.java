package ch.voulgarakis.recruitment.controller;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import ch.voulgarakis.recruitment.dao.ApplicantRepository;
import ch.voulgarakis.recruitment.dao.VacancyRepository;
import ch.voulgarakis.recruitment.service.RecruitmentService;
import ch.voulgarakis.recruitment.utils.ApplicantVacancy;
import ch.voulgarakis.recruitment.utils.OperationNotSupportedException;

@RestController
@RequestMapping("/recruitment")
public class RecruitmentController {
    @Autowired
    private VacancyRepository vacRepo;
    @Autowired
    private ApplicantRepository appRepo;
    @Autowired
    private RecruitmentService rs;

    @RequestMapping(value = "/apply", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateVacancy(@RequestBody ApplicantVacancy applicantVacancy) {
        return new ResponseEntity<>(rs.apply(applicantVacancy.getApplicant(), applicantVacancy.getVacancy()),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/apply", method = RequestMethod.PUT, params = { "applicantId", "vacancyId" })
    public ResponseEntity<Boolean> updateVacancy(@PathParam("applicantId") long applicantId,
            @PathParam("vacancyId") long vacancyId) {
        return new ResponseEntity<>(rs.apply(appRepo.findOne(applicantId), vacRepo.findOne(vacancyId)), HttpStatus.OK);
    }

    // ------------------- Delete All Applicants & Vacancies-----------------------------
    @ExceptionHandler({ OperationNotSupportedException.class })
    protected ResponseEntity<String> handleUnknownException(OperationNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
