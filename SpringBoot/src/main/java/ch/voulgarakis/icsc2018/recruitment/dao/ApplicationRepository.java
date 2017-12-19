package ch.voulgarakis.icsc2018.recruitment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.voulgarakis.icsc2018.recruitment.model.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    public boolean existsById(Long id);
}
