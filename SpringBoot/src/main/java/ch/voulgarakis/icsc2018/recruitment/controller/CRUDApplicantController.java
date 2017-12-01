package ch.voulgarakis.icsc2018.recruitment.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import ch.voulgarakis.icsc2018.recruitment.dao.ApplicantRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.utils.OperationNotSupportedException;

@RestController
@RequestMapping("/applicant")
// Note that this annotation should typically be used only on a @Service. But since this is a simple CRUD
// Controller, we break the convention and place it on a @Controller
@Transactional
public class CRUDApplicantController {
    @Autowired
    private ApplicantRepository appRepo;

    // -------------------Retrieve All Applicants & Vacancies---------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Applicant>> listAllApplicants(HttpServletRequest request) {
        List<Applicant> applicants = appRepo.findAll();
        return new ResponseEntity<List<Applicant>>(applicants, HttpStatus.OK);
    }

    // -------------------Retrieve Single Applicant & Vacancy------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Applicant> getApplicant(@PathVariable("id") long id) {
        Applicant applicant = appRepo.findOne(id);
        if (applicant != null)
            return new ResponseEntity<Applicant>(applicant, HttpStatus.FOUND);
        else
            return new ResponseEntity<Applicant>(HttpStatus.NOT_FOUND);
    }

    // -------------------Create Single Applicants & Vacancies---------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Applicant> createApplicant(HttpServletRequest request, @RequestBody Applicant applicant) {
        if (applicant != null)
            synchronized (appRepo) {
                if (!appRepo.exists(Example.of(applicant))) {
                    appRepo.save(applicant);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                } else
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        else
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    // -------------------Update Applicants & Vacancies-------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<Applicant> createApplicants(HttpServletRequest request,
            @RequestBody List<Applicant> applicants) {
        // List<HttpStatus> httpStatuses = applicants.stream().map(applicant -> {
        // if (applicant != null)
        // if (!rs.existsApplicant(applicant)) {
        // rs.updateApplicant(applicant);
        // return HttpStatus.OK;
        // } else
        // return HttpStatus.NOT_FOUND;
        // else
        // return HttpStatus.OK;
        // }).collect(Collectors.toList());
        //
        // return httpStatuses.stream().noneMatch(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND))
        // ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);

        throw new OperationNotSupportedException("I am sorry but bulk updates are not supported yet.");
    }

    // ------------------- Update Single Applicant & Vacancy ------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Applicant> updateApplicant(@PathVariable("id") long id, @RequestBody Applicant applicant) {
        synchronized (appRepo) {
            if (appRepo.exists(id)) {
                appRepo.save(applicant);
                return new ResponseEntity<>(HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ------------------- Delete Single Applicant & Vacancy-----------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Applicant> deleteApplicant(@PathVariable("id") long id) {
        if (appRepo.exists(id)) {
            appRepo.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------- Delete All Applicants & Vacancies-----------------------------

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Applicant> deleteAllApplicants() {
        appRepo.deleteAllInBatch();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ------------------- Delete All Applicants & Vacancies-----------------------------
    @ExceptionHandler({ OperationNotSupportedException.class })
    protected ResponseEntity<String> handleUnknownException(OperationNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
