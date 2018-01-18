package ch.voulgarakis.icsc2018.recruitment.controller;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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

import ch.voulgarakis.icsc2018.recruitment.controller.feign.SkillFeign;
import ch.voulgarakis.icsc2018.recruitment.dao.SkillRepository;
import ch.voulgarakis.icsc2018.recruitment.model.Skill;
import ch.voulgarakis.icsc2018.recruitment.utils.OperationNotSupportedException;

public class CRUDSkillController implements SkillFeign {
    @Autowired
    private SkillRepository skillRepo;

    // -------------------Retrieve All Skills ---------------------------------------------

    @Override
    public ResponseEntity<List<Skill>> listAllSkills(HttpServletRequest request) {
        List<Skill> Skills = skillRepo.findAll();
        return new ResponseEntity<List<Skill>>(Skills, HttpStatus.OK);
    }

    // -------------------Retrieve Single Skill ------------------------------------------

    @Override
    public ResponseEntity<Skill> getSkill(HttpServletRequest request, @PathVariable("id") String id) {
        Skill Skill = skillRepo.findOne(id);
        if (Skill != null)
            return new ResponseEntity<Skill>(Skill, HttpStatus.FOUND);
        else
            return new ResponseEntity<Skill>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Skill> getSkill(@RequestParam("name") String name) {
        Skill skill = skillRepo.findByName(name);
        if (skill != null)
            return new ResponseEntity<>(skill, HttpStatus.FOUND);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // -------------------Create Single Skills ---------------------------------------------

    @Override
    public ResponseEntity<Skill> createSkill(HttpServletRequest request, @RequestBody Skill skill) {
        if (skill != null) {
            // Assign the ID of Skill, since JSON->Skill conversion ignores the
            // @Id
            // This is necessary, since we are assigning the DB ID ourselves,
            // instead of Hibernate
            Skill skillWithId = new Skill(skill.getName());
            synchronized (skillRepo) {
                if (!skillRepo.exists(Example.of(skillWithId))) {
                    skillRepo.save(skillWithId);
                    return ResponseEntity
                            .created(fromMethodCall(on(CRUDSkillController.class).getSkill(null, skillWithId.getName()))
                                    .buildAndExpand().toUri())
                            .body(skillWithId);
                } else
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } else
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    // -------------------Update Skills -------------------------------------------

    @Override
    public ResponseEntity<Skill> createSkills(HttpServletRequest request, @RequestBody List<Skill> Skills) {
        // List<HttpStatus> httpStatuses = Skills.stream().map(Skill -> {
        // if (Skill != null)
        // if (!rs.existsSkill(Skill)) {
        // rs.updateSkill(Skill);
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

    // ------------------- Update Single Skill ------------------------------------------------

    @Override
    public ResponseEntity<Skill> updateSkill(@PathVariable("id") String id, @RequestBody Skill skill) {
        synchronized (skillRepo) {
            if (skillRepo.exists(id)) {
                // Assign the ID of Skill, since JSON->Skill conversion ignores
                // the @Id
                // This is necessary, since we are assigning the DB ID
                // ourselves, instead of Hibernate
                Skill skillWithId = new Skill(skill.getName());
                skillRepo.save(skillWithId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ------------------- Delete Single Skill-----------------------------------------

    @Override
    public ResponseEntity<Skill> deleteSkill(@PathVariable("id") String id) {
        if (skillRepo.exists(id)) {
            skillRepo.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------- Delete All Skills -----------------------------

    @Override
    public ResponseEntity<Skill> deleteAllSkills() {
        skillRepo.deleteAllInBatch();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ------------------- Delete All Skills -----------------------------
    @ExceptionHandler({ OperationNotSupportedException.class })
    protected ResponseEntity<String> handleUnknownException(OperationNotSupportedException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_IMPLEMENTED);
    }
}
