package com.mvilaboa.hogwarts_artifacts_online.artifact;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mvilaboa.hogwarts_artifacts_online.artifact.utils.IdWorker;

@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;
    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId) {
        return this.artifactRepository.findOneById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }

    public List<Artifact> findAll() {
        return this.artifactRepository.findAllByOrderByName();
    }

    public Artifact save(Artifact newArtifact) {
        newArtifact.setId(String.valueOf(idWorker.nextId()));
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact updatedArtifact) {
        Artifact artifactToUpdate = this.artifactRepository.findOneById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
        artifactToUpdate.setName(updatedArtifact.getName());
        artifactToUpdate.setDescription(updatedArtifact.getDescription());
        artifactToUpdate.setImageUrl(updatedArtifact.getImageUrl());
        return this.artifactRepository.save(artifactToUpdate);
    }

    public void delete(String artifactId) {
        if (!artifactRepository.existsById(artifactId)) {
            throw new ArtifactNotFoundException(artifactId);
        }
        this.artifactRepository.deleteById(artifactId);
    }

}
