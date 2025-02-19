package com.mvilaboa.hogwarts_artifacts_online.wizard;

import java.util.HashSet;
import java.util.List;

import com.mvilaboa.hogwarts_artifacts_online.artifact.Artifact;
import com.mvilaboa.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.mvilaboa.hogwarts_artifacts_online.system.exception.AlreadyInDbException;
import com.mvilaboa.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;
    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public Wizard findById(Integer wizardId) {
        return wizardRepository.findOneById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
    }

    public List<Wizard> findAll() {
        return wizardRepository.findAllByOrderByName();
    }

    public Wizard save(Wizard wizard) {
        if (wizardRepository.existsByName(wizard.getName())) {
            throw new AlreadyInDbException(
                    "wizard", "Name", wizard.getName());
        }
        wizard.setId(null);
        wizard.setArtifacts(new HashSet<>());
        return wizardRepository.save(wizard);
    }

    public Wizard updateById(Integer wizardId, Wizard wizard) {

        Wizard wizardToUpdate = wizardRepository.findOneById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));

        if (wizardToUpdate.getName().equals(wizard.getName())) {
            return wizardToUpdate;
        }

        if (wizardRepository.existsByName(wizard.getName())) {
            throw new AlreadyInDbException(
                    "wizard", "Name", wizard.getName());
        }

        wizardToUpdate.setName(wizard.getName());
        return wizardRepository.save(wizardToUpdate);

    }

    public void deleteById(Integer wizardId) {
        Wizard wizardToDelete = wizardRepository.findOneById(wizardId).orElseThrow(
                () -> new ObjectNotFoundException("wizard", wizardId));

        wizardToDelete.getArtifacts().forEach(a -> a.setOwner(null));
        wizardToDelete.getArtifacts().clear();

        wizardRepository.deleteById(wizardId);
    }

    public void assignArtifact(Integer wizardId, String artifactId) {

        Artifact artifactToAssign = artifactRepository.findOneById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
        Wizard wizardToUpdate = wizardRepository.findOneById(wizardId)
                .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
        Wizard assignedWizard = artifactToAssign.getOwner();
    
        if (assignedWizard != null && assignedWizard.equals(wizardToUpdate)) {
            return;
        }
    
        if (assignedWizard != null) {
            assignedWizard.removeArtifact(artifactToAssign);
        }
    
        wizardToUpdate.addArtifact(artifactToAssign);
        wizardRepository.save(wizardToUpdate);
        
    }
    

}
