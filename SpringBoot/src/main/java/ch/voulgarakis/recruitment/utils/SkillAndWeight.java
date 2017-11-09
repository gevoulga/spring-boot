package ch.voulgarakis.recruitment.utils;

import ch.voulgarakis.recruitment.model.Skill;

public class SkillAndWeight {
    private Skill skill;
    private double weight;

    public SkillAndWeight(Skill skill, double weight) {
        this.skill = skill;
        this.weight = weight;
    }

    public Skill getSkill() {
        return skill;
    }

    public double getWeight() {
        return weight;
    }
}