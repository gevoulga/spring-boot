package ch.voulgarakis.icsc2018.recruitment.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
// Circular JSON references will be replaced with property: "id" instead of the whole JSON string
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Application {
    @Id
    @GeneratedValue
    private Long id;

    private double fitRatio; // How good, our application is!

    @ManyToOne(targetEntity = Applicant.class, fetch = FetchType.EAGER)
    // @ManyToMany(targetEntity = Applicant.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(name = "Join_Application_Applicant",
            // This Entity id
            joinColumns = { @JoinColumn(name = "application_id", referencedColumnName = "id") },
            // The other Entity id
            inverseJoinColumns = { @JoinColumn(name = "applicant_id", referencedColumnName = "id") })
    private Applicant applicant;

    @ManyToOne(targetEntity = Vacancy.class, fetch = FetchType.EAGER)
    // @ManyToMany(targetEntity = Vacancy.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinTable(name = "Join_Application_Vacancy",
            // This Entity id
            joinColumns = { @JoinColumn(name = "application_id", referencedColumnName = "id") },
            // The other Entity id
            inverseJoinColumns = { @JoinColumn(name = "vacancy_id", referencedColumnName = "id") })
    private Vacancy vacancy;

    protected Application() {
        // Empty constructor
    }

    public Application(Applicant applicant, Vacancy vacancy, double fitRatio) {
        super();
        this.applicant = applicant;
        this.vacancy = vacancy;
        this.fitRatio = fitRatio;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public Vacancy getVacancy() {
        return vacancy;
    }

    public double getFitRatio() {
        return fitRatio;
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
