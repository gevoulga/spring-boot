package ch.voulgarakis.icsc2018.recruitment.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;

import ch.voulgarakis.icsc2018.recruitment.controller.feign.ApplicantFeign;
import ch.voulgarakis.icsc2018.recruitment.dao.ApplicantRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Applicant;
import ch.voulgarakis.icsc2018.recruitment.utils.OperationNotSupportedException;

public class CRUDApplicantController implements ApplicantFeign {
    @Autowired
    private ApplicantRepository appRepo;

    // -------------------Retrieve All Applicants---------------------------------------------

    @Override
    public ResponseEntity<List<Applicant>> listAllApplicants(HttpServletRequest request) {
        List<Applicant> applicants = appRepo.findAll();
        return new ResponseEntity<List<Applicant>>(applicants, HttpStatus.OK);
    }

    // -------------------Retrieve Single Applicant------------------------------------------

    @Override
    public ResponseEntity<Applicant> getApplicant(long id) {
        Applicant applicant = appRepo.findOne(id);
        if (applicant != null)
            return new ResponseEntity<Applicant>(applicant, HttpStatus.FOUND);
        else
            return new ResponseEntity<Applicant>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Applicant> getApplicant(String name) {
        Applicant applicant = appRepo.findByName(name);
        if (applicant != null)
            return new ResponseEntity<Applicant>(applicant, HttpStatus.FOUND);
        else
            return new ResponseEntity<Applicant>(HttpStatus.NOT_FOUND);
    }

    // -------------------Create Single Applicants---------------------------------------------

    @Override
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

    // -------------------Update Applicants-------------------------------------------

    @Override
    public ResponseEntity<Applicant> createApplicants(HttpServletRequest request,
            @RequestBody List<Applicant> applicants) {
        // List<HttpStatus> httpStatuses = applicants.stream().map(applicant ->
        // {
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
        // return httpStatuses.stream().noneMatch(httpStatus ->
        // httpStatus.equals(HttpStatus.NOT_FOUND))
        // ? new ResponseEntity<>(HttpStatus.OK) : new
        // ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);

        throw new OperationNotSupportedException("I am sorry but bulk updates are not supported yet.");
    }

    // ------------------- Update Single Applicant ------------------------------------------------

    @Override
    public ResponseEntity<Applicant> updateApplicant(long id, @RequestBody Applicant applicant) {
        synchronized (appRepo) {
            if (appRepo.exists(id)) {
                appRepo.save(applicant);
                return new ResponseEntity<>(HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ------------------- Delete Single Applicant-----------------------------------------

    @Override
    public ResponseEntity<Applicant> deleteApplicant(long id) {
        if (appRepo.exists(id)) {
            appRepo.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------- Delete All Applicants-----------------------------

    @Override
    public ResponseEntity<Applicant> deleteAllApplicants() {
        appRepo.deleteAllInBatch();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ------------------- Delete All Applicants-----------------------------
    @ExceptionHandler({ OperationNotSupportedException.class })
    protected ResponseEntity<String> handleUnknownException(OperationNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
