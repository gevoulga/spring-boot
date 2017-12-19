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
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.voulgarakis.icsc2018.recruitment.utils.SkillAndWeight;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Vacancy {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Embedded
    private SkillList requiredSkills; // The skills required for the position

    @ElementCollection
    private List<Double> requiredSkillWeights; // The importance of each skill for the position

    @OneToMany(mappedBy = "vacancy")
    private List<Application> applications = new ArrayList<>(); // The applications for the vacancy

    protected Vacancy() {
        // Empty constructor
        requiredSkills = new SkillList();
        requiredSkillWeights = new ArrayList<>();
    }

    public Vacancy(String name, SkillAndWeight... requiredSkillsAndImportance) {
        super();
        this.name = name;
        this.requiredSkills = new SkillList(
                Stream.of(requiredSkillsAndImportance).parallel().map(e -> e.getSkill()).collect(Collectors.toList()));
        this.requiredSkillWeights = Stream.of(requiredSkillsAndImportance).parallel().map(e -> e.getWeight())
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public List<Skill> getRequiredSkills() {
        return requiredSkills.getSkills();
    }

    public List<Double> getRequiredSkillWeights() {
        return requiredSkillWeights;
    }

    public List<Application> getApplications() {
        return applications;
    }

    @Override
    public String toString() {
        // return name + ":" + requiredSkills + (id == null ? "" : ":" + id);
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
