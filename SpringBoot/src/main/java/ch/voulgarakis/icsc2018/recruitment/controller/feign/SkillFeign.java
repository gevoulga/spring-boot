package ch.voulgarakis.icsc2018.recruitment.controller.feign;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.voulgarakis.icsc2018.recruitment.model.Skill;

@RestController
// @FeignClient("skill")
@RequestMapping("/skill")
// Note that this annotation should typically be used only on a @Service. But
// since this is a simple CRUD
// Controller, we break the convention and place it on a @Controller
@Transactional
public interface SkillFeign {

    // -------------------Retrieve All Skills ---------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<Skill>> listAllSkills(HttpServletRequest request);

    // -------------------Retrieve Single Skill ------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Skill> getSkill(HttpServletRequest request, @PathVariable("id") String id);

    @RequestMapping(value = "/", params = "name", method = RequestMethod.GET)
    public ResponseEntity<Skill> getSkill(@RequestParam("name") String name);

    // -------------------Create Single Skills ---------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Skill> createSkill(HttpServletRequest request, @RequestBody Skill skill);

    // -------------------Update Skills -------------------------------------------

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<Skill> createSkills(HttpServletRequest request, @RequestBody List<Skill> Skills);

    // ------------------- Update Single Skill ------------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Skill> updateSkill(@PathVariable("id") String id, @RequestBody Skill skill);

    // ------------------- Delete Single Skill &
    // Skill-----------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Skill> deleteSkill(@PathVariable("id") String id);

    // ------------------- Delete All Skills -----------------------------

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<Skill> deleteAllSkills();
}
