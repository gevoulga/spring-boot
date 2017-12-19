package ch.voulgarakis.icsc2018.recruitment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.voulgarakis.icsc2018.recruitment.model.Applicant;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    public Applicant findByName(String name);

    public boolean existsByName(String name);
}
