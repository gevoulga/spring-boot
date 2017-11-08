package ch.voulgarakis.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Skill {
    @Id
    @GeneratedValue
    private Long id;

    // @Id
    // @GeneratedValue(generator = "uuid")
    // @GenericGenerator(name = "uuid", strategy = "uuid2")
    // private String id;

    private String name;

    protected Skill() {
        // Empty contructor
    }

    public Skill(String name) {
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
