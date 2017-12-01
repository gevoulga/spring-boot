package ch.voulgarakis.icsc2018.recruitment.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Skill {
    @Id
    // @GeneratedValue
    private String id;

    // @Id
    // @GeneratedValue(generator = "uuid")
    // @GenericGenerator(name = "uuid", strategy = "uuid2")
    // private String id;

    private String name;

    protected Skill() {
        // Empty contructor
        // id = name;
    }

    public Skill(String name) {
        id = name;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        // return name + (id == null ? "" : ":" + id);
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
