package com.mvilaboa.hogwarts_artifacts_online.wizard;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WizardRepository extends JpaRepository<Wizard, Integer> {

    Optional<Wizard> findOneById(Integer id);

    List<Wizard> findAllByOrderByName();

    boolean existsByName(String name);
}
