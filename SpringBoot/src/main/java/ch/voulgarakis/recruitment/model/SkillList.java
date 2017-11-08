package ch.voulgarakis.recruitment.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

/**
 * To make definition of Skills a little easier (in terms of JPA syntax), we have this wrapper class. Then we can embed
 * this class to the JPA class which should hold a list of Skills.
 * 
 * @author Georgios Voulgarakis
 */
@Embeddable
public class SkillList {
    @OneToMany(targetEntity = Skill.class, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH })
    // @JoinColumn(name = "skill_Id")
    private List<Skill> skills;

    protected SkillList() {
        // Empty Constructor
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
