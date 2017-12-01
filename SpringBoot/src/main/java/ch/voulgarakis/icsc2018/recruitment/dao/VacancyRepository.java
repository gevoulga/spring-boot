package ch.voulgarakis.icsc2018.recruitment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    // https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-creating-database-queries-from-method-names/
    // Return skill by name
    public Vacancy findByName(String name);

    public boolean existsByName(String name);

    // https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-creating-database-queries-with-the-query-annotation/
    // custom query example and return a stream
    // @Query("select c from Skills c where c.name like 'skillNameBeginning%'")
    // Stream<Skill> skillsBeginningWith(@Param("skillNameBeginning") String skillNameBeginning);
}
