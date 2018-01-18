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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import ch.voulgarakis.icsc2018.recruitment.controller.feign.VacancyFeign;
import ch.voulgarakis.icsc2018.recruitment.dao.VacancyRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;
import ch.voulgarakis.icsc2018.recruitment.utils.OperationNotSupportedException;

public class CRUDVacancyController implements VacancyFeign {
    @Autowired
    private VacancyRepository vacRepo;

    // -------------------Retrieve All Vacancies---------------------------------------------

    @Override
    public ResponseEntity<List<Vacancy>> listAllVacancies(HttpServletRequest request) {
        List<Vacancy> Vacancys = vacRepo.findAll();
        return new ResponseEntity<List<Vacancy>>(Vacancys, HttpStatus.OK);
    }

    // -------------------Retrieve Single Vacancy------------------------------------------

    @Override
    public ResponseEntity<Vacancy> getVacancy(@PathVariable("id") long id) {
        Vacancy Vacancy = vacRepo.findOne(id);
        if (Vacancy != null)
            return new ResponseEntity<Vacancy>(Vacancy, HttpStatus.FOUND);
        else
            return new ResponseEntity<Vacancy>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Vacancy> getVacancy(@RequestParam("name") String name) {
        Vacancy vacancy = vacRepo.findByName(name);
        if (vacancy != null)
            return new ResponseEntity<>(vacancy, HttpStatus.FOUND);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // -------------------Create Single Vacancies---------------------------------------------

    @Override
    public ResponseEntity<Vacancy> createVacancy(HttpServletRequest request, @RequestBody Vacancy Vacancy) {
        if (Vacancy != null)
            synchronized (vacRepo) {
                if (!vacRepo.exists(Example.of(Vacancy))) {
                    vacRepo.save(Vacancy);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                } else
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        else
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    // -------------------Update Vacancies-------------------------------------------

    @Override
    public ResponseEntity<Vacancy> createVacancies(HttpServletRequest request, @RequestBody List<Vacancy> Vacancys) {
        // List<HttpStatus> httpStatuses = Vacancys.stream().map(Vacancy -> {
        // if (Vacancy != null)
        // if (!rs.existsVacancy(Vacancy)) {
        // rs.updateVacancy(Vacancy);
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

    // ------------------- Update Single Vacancy ------------------------------------------------

    @Override
    public ResponseEntity<Vacancy> updateVacancy(@PathVariable("id") long id, @RequestBody Vacancy Vacancy) {
        if (vacRepo.exists(id)) {
            vacRepo.save(Vacancy);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------- Delete Single Vacancy-----------------------------------------

    @Override
    public ResponseEntity<Vacancy> deleteVacancy(@PathVariable("id") long id) {
        synchronized (vacRepo) {
            if (vacRepo.exists(id)) {
                vacRepo.delete(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ------------------- Delete All Vacancies-----------------------------

    @Override
    public ResponseEntity<Vacancy> deleteAllVacancies() {
        vacRepo.deleteAllInBatch();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ------------------- Delete All Vacancies-----------------------------
    @ExceptionHandler({ OperationNotSupportedException.class })
    protected ResponseEntity<String> handleUnknownException(OperationNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
