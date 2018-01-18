package ch.voulgarakis.icsc2018.recruitment.controller.feign;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ch.voulgarakis.icsc2018.recruitment.model.Application;

@RestController
@FeignClient("application-service")
@RequestMapping("/application")
// Note that this annotation should typically be used only on a @Service. But
// since this is a simple CRUD
// Controller, we break the convention and place it on a @Controller
@Transactional
public interface ApplicationFeign {

    // -------------------Retrieve All Applications---------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Application>> listAllApplications(HttpServletRequest request);

    // -------------------Retrieve Single Application------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Application> getApplication(@PathVariable("id") long id);

    // -------------------Create Single Applications---------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Application> createApplication(HttpServletRequest request,
            @RequestBody Application application);

    // -------------------Update Applications-------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<Application> createApplications(HttpServletRequest request,
            @RequestBody List<Application> applications);

    // ------------------- Update Single Application ------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Application> updateApplication(@PathVariable("id") long id,
            @RequestBody Application application);

    // ------------------- Delete Single Application-----------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Application> deleteApplication(@PathVariable("id") long id);

    // ------------------- Delete All Applications-----------------------------

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Application> deleteAllApplications();
}
