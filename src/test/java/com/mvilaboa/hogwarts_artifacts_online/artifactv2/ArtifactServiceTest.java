package com.mvilaboa.hogwarts_artifacts_online.artifactv2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Optional;

import com.mvilaboa.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.mvilaboa.hogwarts_artifacts_online.artifact.Artifact;
import com.mvilaboa.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.mvilaboa.hogwarts_artifacts_online.artifact.ArtifactService;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class ArtifactServiceTest {

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    ArtifactService artifactService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {

        // Given
        String anExistingArtifactIdInTestDb = "artifact1";

        // When
        Artifact serviceArtifact = artifactService.findById(anExistingArtifactIdInTestDb);
        Artifact repositoryArtifact = artifactRepository.findOneById(anExistingArtifactIdInTestDb).orElseThrow();

        // Then
        assertThat(serviceArtifact.getId()).isEqualTo(repositoryArtifact.getId());
        assertThat(serviceArtifact.getName()).isEqualTo(repositoryArtifact.getName());
        assertThat(serviceArtifact.getDescription()).isEqualTo(repositoryArtifact.getDescription());
        assertThat(serviceArtifact.getImageUrl()).isEqualTo(repositoryArtifact.getImageUrl());
        assertThat(serviceArtifact.getOwner().getId()).isEqualTo(repositoryArtifact.getOwner().getId());

    }

    @Test
    void testFindByIdNotFound() {

        // Given
        String aNotExistingArtifactIdInTestDb = "notValidId";

        // When
        Throwable throwable = catchThrowable(() -> {
                artifactService.findById(aNotExistingArtifactIdInTestDb);
        });

        // Then
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage(
                    "Could not find artifact with Id " + aNotExistingArtifactIdInTestDb + " :(");

    }

    @Test
    void testFindAllSucess() {

        // When
        List<Artifact> artifacts = this.artifactService.findAll();
        List<Artifact> actualArtifacts = this.artifactRepository.findAllByOrderByName();

        // Then
        assertThat(artifacts.size()).isEqualTo(actualArtifacts.size());

        for (int i = 0; i < actualArtifacts.size(); i++) {
            assertThat(artifacts.get(i).getName())
                .isEqualTo(actualArtifacts.get(i).getName());
            assertThat(artifacts.get(i).getDescription())
                .isEqualTo(actualArtifacts.get(i).getDescription());
            assertThat(artifacts.get(i).getImageUrl())
                .isEqualTo(actualArtifacts.get(i).getImageUrl());
            if (artifacts.get(i).getOwner() == null) {
                assertNull(actualArtifacts.get(i).getOwner());
            } else {
                assertThat(artifacts.get(i).getOwner().getId())
                .isEqualTo(actualArtifacts.get(i).getOwner().getId());
            }
        }

    }

    @Test
    void testSaveSuccess() {

        // Given
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Nuevo artefacto");
        newArtifact.setDescription("Description");
        newArtifact.setImageUrl("Image Url");

        // When
        this.artifactService.save(newArtifact);
        Optional<Artifact> savedArtifact = artifactRepository.findOneByName("Nuevo artefacto");

        // Then
        assertThat(savedArtifact)
                .containsInstanceOf(Artifact.class);
        assertThat(savedArtifact.get().getName())
                .isEqualTo(newArtifact.getName());
        assertThat(savedArtifact.get().getDescription())
                .isEqualTo(newArtifact.getDescription());
        assertThat(savedArtifact.get().getImageUrl())
                .isEqualTo(newArtifact.getImageUrl());

    }

    @Test
    void testUpdateSuccess() {

        // Given
        String anExistingArtifactIdInDb = "artifact1";
        Artifact existingArtifact = artifactRepository
                .findOneById(anExistingArtifactIdInDb).get();
        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setName(existingArtifact.getName());
        updatedArtifact.setDescription("New Description");
        updatedArtifact.setImageUrl("New Image Url");

        // When
        Artifact savedUpdatedArtifact = artifactService
                .update(anExistingArtifactIdInDb, updatedArtifact);

        // Then
        assertThat(savedUpdatedArtifact.getId())
                .isEqualTo(anExistingArtifactIdInDb);
        assertThat(savedUpdatedArtifact.getName())
                .isEqualTo(existingArtifact.getName());
        assertThat(savedUpdatedArtifact.getDescription())
                .isEqualTo(updatedArtifact.getDescription());
        assertThat(savedUpdatedArtifact.getImageUrl())
                .isEqualTo(updatedArtifact.getImageUrl());
    }

    @Test
    void testUpdateNotFound() {

        // Given
        String aNotExistingArtifactId = "notValidArtifactId";
        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setName("New Name");
        updatedArtifact.setDescription("New Description");
        updatedArtifact.setImageUrl("New Image Url");

        // When 
        Throwable throwable = catchThrowable(() -> {
            artifactService.update(aNotExistingArtifactId, updatedArtifact);
        });

        // Then
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id " + aNotExistingArtifactId + " :(");

    }

    @Test
    void testDeleteSuccess() {

        // Given
        String anExistingArtifactIdInDb = "artifact1";

        // When
        artifactService.delete(anExistingArtifactIdInDb);
        Optional<Artifact> artifactDeleted = artifactRepository
                .findOneById(anExistingArtifactIdInDb);

        // Then
        assertThat(artifactDeleted).isEmpty();
    }

    @Test
    void testDeleteNotFound() {
        // Given
        String aNotExistingArtifactId = "notValidArtifactId";

        // When
        Throwable throwable = catchThrowable(() -> {
            artifactService.delete(aNotExistingArtifactId);
        });

        // Then
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id " + aNotExistingArtifactId + " :(");
                
    }
}
