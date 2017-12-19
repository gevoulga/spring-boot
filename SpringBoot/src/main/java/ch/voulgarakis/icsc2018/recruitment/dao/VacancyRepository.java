package ch.voulgarakis.icsc2018.recruitment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.voulgarakis.icsc2018.recruitment.model.Vacancy;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    public Vacancy findByName(String name);

    public boolean existsByName(String name);
}
