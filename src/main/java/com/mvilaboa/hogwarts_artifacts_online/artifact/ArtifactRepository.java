package com.mvilaboa.hogwarts_artifacts_online.artifact;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtifactRepository extends JpaRepository<Artifact, String> {
    
    Optional<Artifact> findOneById(String artifactId);

    Optional<Artifact> findOneByName(String name);

    List<Artifact> findAllByOrderByName();

}
