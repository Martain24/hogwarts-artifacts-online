package com.mvilaboa.hogwarts_artifacts_online.hogwartsuser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<HogwartsUser, Integer> {
    
    Optional<HogwartsUser> findOneById(Integer userId);

    boolean existsByUsername(String username);

    Optional<HogwartsUser> findOneByUsername(String username);
}
