package ch.voulgarakis.icsc2018.recruitment.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;

import ch.voulgarakis.icsc2018.recruitment.controller.feign.ApplicationFeign;
import ch.voulgarakis.icsc2018.recruitment.dao.ApplicationRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Application;
import ch.voulgarakis.icsc2018.recruitment.utils.OperationNotSupportedException;

public class CRUDApplicationController implements ApplicationFeign {
    @Autowired
    private ApplicationRepository appRepo;

    // -------------------Retrieve All Applications---------------------------------------------

    @Override
    public ResponseEntity<List<Application>> listAllApplications(HttpServletRequest request) {
        List<Application> applications = appRepo.findAll();
        return new ResponseEntity<List<Application>>(applications, HttpStatus.OK);
    }

    // -------------------Retrieve Single Application------------------------------------------

    @Override
    public ResponseEntity<Application> getApplication(long id) {
        Application application = appRepo.findOne(id);
        if (application != null)
            return new ResponseEntity<Application>(application, HttpStatus.FOUND);
        else
            return new ResponseEntity<Application>(HttpStatus.NOT_FOUND);
    }

    // -------------------Create Single Applications---------------------------------------------

    @Override
    public ResponseEntity<Application> createApplication(HttpServletRequest request,
            @RequestBody Application application) {
        if (application != null)
            synchronized (appRepo) {
                if (!appRepo.exists(Example.of(application))) {
                    appRepo.save(application);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                } else
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        else
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    // -------------------Update Applications-------------------------------------------

    @Override
    public ResponseEntity<Application> createApplications(HttpServletRequest request,
            @RequestBody List<Application> applications) {
        // List<HttpStatus> httpStatuses = applications.stream().map(application
        // -> {
        // if (application != null)
        // if (!rs.existsApplication(application)) {
        // rs.updateApplication(application);
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

    // ------------------- Update Single Application ------------------------------------------------

    @Override
    public ResponseEntity<Application> updateApplication(@PathVariable("id") long id,
            @RequestBody Application application) {
        synchronized (appRepo) {
            if (appRepo.exists(id)) {
                appRepo.save(application);
                return new ResponseEntity<>(HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ------------------- Delete Single Application-----------------------------------------

    @Override
    public ResponseEntity<Application> deleteApplication(@PathVariable("id") long id) {
        if (appRepo.exists(id)) {
            appRepo.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------- Delete All Applications-----------------------------

    @Override
    public ResponseEntity<Application> deleteAllApplications() {
        appRepo.deleteAllInBatch();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ------------------- Delete All Applications-----------------------------
    @ExceptionHandler({ OperationNotSupportedException.class })
    protected ResponseEntity<String> handleUnknownException(OperationNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
