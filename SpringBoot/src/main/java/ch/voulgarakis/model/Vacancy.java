package ch.voulgarakis.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import ch.voulgarakis.utils.SkillAndWeight;

@Entity
public class Vacancy {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Embedded
    private SkillList requiredSkills; // The skills required for the position

    @ElementCollection
    private List<Double> requiredSkillWeights; // The importance of each skill for the position

    private double fitThreshold; // The fit threshold above which we consider a match

    @ManyToMany(targetEntity = Applicant.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(name = "Join_Vacancy_Applicant",
            // This Entity id
            joinColumns = { @JoinColumn(name = "vacancy_id", referencedColumnName = "id") },
            // The other Entity id
            inverseJoinColumns = { @JoinColumn(name = "applicant_id", referencedColumnName = "id") })
    private List<Applicant> applicants = new ArrayList<>(); // The applicants having applied for the vacancy

    protected Vacancy() {
        // Empty constructor
    }

    public Vacancy(String name, double fitThreshold, SkillAndWeight... requiredSkillsAndImportance) {
        super();
        this.name = name;
        this.requiredSkills = new SkillList(
                Stream.of(requiredSkillsAndImportance).parallel().map(e -> e.getSkill()).collect(Collectors.toList()));
        this.requiredSkillWeights = Stream.of(requiredSkillsAndImportance).parallel().map(e -> e.getWeight())
                .collect(Collectors.toList());
        this.fitThreshold = fitThreshold;
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

    public double getFitThreshold() {
        return fitThreshold;
    }

    public List<Applicant> getApplicants() {
        return applicants;
    }

    @Override
    public String toString() {
        return name + ":" + requiredSkills + (id == null ? "" : ":" + id);
    }
}
