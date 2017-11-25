package ch.voulgarakis.recruitment.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
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
        return name + (id == null ? "" : ":" + id);
    }
}
