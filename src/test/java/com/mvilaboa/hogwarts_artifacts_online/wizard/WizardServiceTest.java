package com.mvilaboa.hogwarts_artifacts_online.wizard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.mvilaboa.hogwarts_artifacts_online.artifact.Artifact;
import com.mvilaboa.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.mvilaboa.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.mvilaboa.hogwarts_artifacts_online.wizard.exception.WizardNameAlreadyInDbException;

@Transactional
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class WizardServiceTest {

    @Autowired
    WizardRepository wizardRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    WizardService wizardService;

    Integer existentWizardId;
    Integer anotherExistentWizardId;
    Integer nonExistentWizardId;

    @BeforeEach
    void setUp() {
        existentWizardId = 1;
        anotherExistentWizardId = 2;
        nonExistentWizardId = 20;

        assertThat(wizardRepository.existsById(existentWizardId)).isTrue();
        assertThat(wizardRepository.existsById(anotherExistentWizardId)).isTrue();
        assertThat(wizardRepository.existsById(nonExistentWizardId)).isFalse();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {

        // When
        Wizard serviceWizard = wizardService.findById(existentWizardId);
        Wizard repoWizard = wizardRepository.findOneById(existentWizardId).orElseThrow();

        // Then
        assertThat(serviceWizard.getId()).isEqualTo(repoWizard.getId());
        assertThat(serviceWizard.getName()).isEqualTo(repoWizard.getName());
        assertThat(serviceWizard.getNumberOfArtifacts()).isEqualTo(repoWizard.getNumberOfArtifacts());

    }

    @Test
    void testFindByIdWizardNotFoundException() {

        // When
        Optional<Wizard> wizardRepo = wizardRepository.findOneById(nonExistentWizardId);
        Throwable throwable = catchThrowable(() -> wizardService.findById(nonExistentWizardId));

        // Then
        assertThat(wizardRepo).isEmpty();
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id " + nonExistentWizardId + " :(");

    }

    @Test
    void testFindAllSuccess() {

        // Given
        List<Wizard> serviceWizards = wizardService.findAll();
        List<Wizard> repoWizards = wizardRepository.findAllByOrderByName();

        // When and then
        assertThat(serviceWizards.size()).isEqualTo(repoWizards.size());
        for (int i = 0; i < repoWizards.size(); i++) {
            Wizard sw = serviceWizards.get(i);
            Wizard rw = repoWizards.get(i);
            assertThat(sw.getId()).isEqualTo(rw.getId());
            assertThat(sw.getName()).isEqualTo(rw.getName());
            assertThat(sw.getNumberOfArtifacts()).isEqualTo(rw.getNumberOfArtifacts());
        }

    }

    @Test
    void testSaveWizardSuccess() {
        // Given
        Wizard newWizard = new Wizard();
        newWizard.setName("Nuevo mago");

        // When
        Wizard savedWizard = wizardService.save(newWizard);

        // Then
        assertThat(wizardRepository.findOneById(savedWizard.getId())).isNotEmpty();
        assertThat(savedWizard.getId()).isNotNull();
        assertThat(newWizard.getName()).isEqualTo(savedWizard.getName());
        assertThat(savedWizard.getArtifacts()).isEmpty();

    }

    @Test
    void testSaveWizardWithANameThatAlreadyExists() {

        // Given
        Wizard wizardWithRepeatedName = wizardRepository.findOneById(existentWizardId).orElseThrow();

        // When
        Throwable throwable = catchThrowable(() -> wizardService.save(wizardWithRepeatedName));

        // Then
        assertThat(throwable)
                .isInstanceOf(WizardNameAlreadyInDbException.class)
                .hasMessage("Already exists a wizard with Name " + wizardWithRepeatedName.getName() + " :(");

    }

    @Test
    void testUpdateWizardSuccess() {

        // Given
        Wizard wizardWithNewName = new Wizard();
        wizardWithNewName.setName("New Name");

        // When
        Wizard wizardUpdatedService = wizardService.updateById(existentWizardId, wizardWithNewName);
        Wizard wizardUpdatedRepo = wizardRepository.findOneById(existentWizardId).orElseThrow();

        // Then
        assertThat(wizardUpdatedService.getId())
                .isEqualTo(existentWizardId)
                .isEqualTo(wizardUpdatedRepo.getId());

        assertThat(wizardUpdatedService.getName())
                .isEqualTo(wizardWithNewName.getName())
                .isEqualTo(wizardUpdatedRepo.getName());

        assertThat(wizardUpdatedService.getNumberOfArtifacts())
                .isEqualTo(wizardUpdatedRepo.getNumberOfArtifacts());
    }

    @Test
    void testUpdateWizardWithNonExistentId() {

        // Given
        Wizard wizard = new Wizard();
        wizard.setName("Any Name");

        // When
        Throwable throwable = catchThrowable(
                () -> wizardService.updateById(nonExistentWizardId, wizard)
        );

        // Then
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id " + nonExistentWizardId + " :(");
    }


    @Test
    void testUpdateWizardWithANameThatAlreadyExists() {

        // Given
        Wizard wizardWithNameThatExists = new Wizard();
        wizardWithNameThatExists.setName(
                wizardRepository
                        .findOneById(existentWizardId).orElseThrow().getName());
        
        // When
        Throwable throwable = catchThrowable(
                () -> wizardService.updateById(anotherExistentWizardId, wizardWithNameThatExists)
        );

        // Then
        assertThat(throwable)
                .isInstanceOf(WizardNameAlreadyInDbException.class)
                .hasMessage("Already exists a wizard with Name " + wizardWithNameThatExists.getName() + " :(");
    }

    @Test
    void testDeleteWizardSuccess() {
        // Given
        long wizardCountBefore = wizardRepository.count();
        Set<String> artifactsIds = wizardRepository.findOneById(existentWizardId)
                .orElseThrow()
                .getArtifacts()
                .stream().map(Artifact::getId)
                .collect(Collectors.toSet());

        // When
        wizardService.deleteById(existentWizardId);

        // Then
        assertThat(wizardRepository.count()).isEqualTo(wizardCountBefore - 1); // Se eliminó solo uno
        assertThat(wizardRepository.findOneById(existentWizardId)).isEmpty();

        artifactsIds.forEach((artifactId) -> {
            Optional<Artifact> optionalArtifact = artifactRepository.findOneById(artifactId);
            assertThat(optionalArtifact).isNotEmpty();
            assertThat(optionalArtifact.orElseThrow().getOwner()).isNull();
        });
    }

    @Test
    void testDeleteWizardWithNonExistentId() {
        // When
        Throwable throwable = catchThrowable(() -> wizardService.deleteById(nonExistentWizardId));

        // Then
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id " + nonExistentWizardId + " :(");
    }

}
