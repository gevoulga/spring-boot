package ch.voulgarakis.icsc2018.recruitment.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.voulgarakis.icsc2018.recruitment.utils.SkillListDeserializer;

/**
 * To make definition of Skills a little easier (in terms of JPA syntax), we have this wrapper class. Then we can embed
 * this class to the JPA class which should hold a list of Skills.
 * 
 * @author Georgios Voulgarakis
 */
@Embeddable
// This is needed otherwise the string->object conversion fails. JSON considers that this is a List...
@JsonDeserialize(using = SkillListDeserializer.class)
public class SkillList {
    // @OneToMany(targetEntity = Skill.class, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
    // @OneToMany(targetEntity = Skill.class, cascade = CascadeType.ALL)
    @ManyToMany(targetEntity = Skill.class)
    // @JoinColumn(name = "skill_Id")
    private List<Skill> skills;

    protected SkillList() {
        // Empty Constructor
        skills = new ArrayList<>();
    }

    public SkillList(List<Skill> skills) {
        this.skills = skills;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    @Override
    public String toString() {
        return skills.toString();
    }
}
