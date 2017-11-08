package ch.voulgarakis.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.voulgarakis.model.Applicant;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    // https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-creating-database-queries-from-method-names/
    // Return skill by name
    public Applicant findByName(String name);

    // https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-creating-database-queries-with-the-query-annotation/
    // custom query example and return a stream
    // @Query("select c from Skills c where c.name like 'skillNameBeginning%'")
    // Stream<Skill> skillsBeginningWith(@Param("skillNameBeginning") String skillNameBeginning);
}
