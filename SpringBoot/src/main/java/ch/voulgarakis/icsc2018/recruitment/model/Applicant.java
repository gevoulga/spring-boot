package ch.voulgarakis.icsc2018.recruitment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.voulgarakis.icsc2018.recruitment.utils.SkillAndWeight;

@Entity
// Circular JSON references will be replaced with property: "name" instead of the whole JSON string
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Applicant {
    @Id
    @GeneratedValue
    private Long id;

    private String name; // The name of the applicant

    @Embedded
    private SkillList skillSet; // The competences of the application

    @ElementCollection
    private List<Double> skillStrength; // The strength of each skill

    @ManyToMany(mappedBy = "applicants")
    private List<Vacancy> vacancies = new ArrayList<>(); // The vacancies the applicant has applied to

    protected Applicant() {
        // Empty constructor
        skillSet = new SkillList();
        skillStrength = new ArrayList<>();
    }

    public Applicant(String name, SkillAndWeight... skillsAndStrength) {
        this.name = name;
        this.skillSet = new SkillList(
                Stream.of(skillsAndStrength).parallel().map(e -> e.getSkill()).collect(Collectors.toList()));
        this.skillStrength = Stream.of(skillsAndStrength).parallel().map(e -> e.getWeight())
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public List<Skill> getSkillSet() {
        return skillSet.getSkills();
    }

    public List<Double> getSkillStrength() {
        return skillStrength;
    }

    public List<Vacancy> getVacancies() {
        return vacancies;
    }

    @Override
    public String toString() {
        // return name + ":" + skillSet + (id == null ? "" : ":" + id);
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
