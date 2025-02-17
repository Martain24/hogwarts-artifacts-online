package com.mvilaboa.hogwarts_artifacts_online.artifact;

import com.mvilaboa.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mvilaboa.hogwarts_artifacts_online.artifact.utils.IdWorker;
import com.mvilaboa.hogwarts_artifacts_online.wizard.Wizard;

@ExtendWith(MockitoExtension.class)
public class ArtifactServiceTest {

    @Mock
    ArtifactRepository artifactRepository;

    @Mock
    IdWorker idWorker;

    @InjectMocks
    ArtifactService artifactService;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        Artifact a1 = new Artifact();
        a1.setId("100001");
        a1.setName("Time Turner");
        a1.setDescription("A magical device that allows the user to travel back in time for a few hours.");
        a1.setImageUrl("ImageUrl1");

        Artifact a2 = new Artifact();
        a2.setId("100002");
        a2.setName("Pensieve");
        a2.setDescription("A shallow stone basin used to review and relive memories.");
        a2.setImageUrl("ImageUrl2");

        this.artifacts = new ArrayList<>();
        this.artifacts.add(a1);
        this.artifacts.add(a2);

    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testFindByIdSuccess() {
        // Given. Arrange inputs and targets. Define the behavior of Mock object artifactRepository
        Artifact a = new Artifact();
        a.setId("125478892313");
        a.setName("Invisibility Cloak");
        a.setDescription("An invisibility cloak is used to make the wearer invisible");
        a.setImageUrl("ImageUrl");

        Wizard w = new Wizard();
        w.setId(2);
        w.setName("Harry Potter");

        a.setOwner(w);

        BDDMockito.given(artifactRepository.findOneById("125478892313")).willReturn(Optional.of(a)); // Defines the behaviour of the mock object.

        // When. Act on the target behaviour. When steps should cover the method to be tested.
        Artifact returnedArtifact = artifactService.findById("125478892313");

        // Then. Assert expected outcomes
        assertEquals(a.getId(), returnedArtifact.getId());
        assertEquals(a.getName(), returnedArtifact.getName());
        assertEquals(a.getDescription(), returnedArtifact.getDescription());
        assertEquals(a.getImageUrl(), returnedArtifact.getImageUrl());
        verify(artifactRepository, times(1)).findOneById("125478892313"); // Verify artifactRepository calls one time findById method.
    }

    @Test
    void testFindByIdNotFound() {
        // Given. Arrange inputs and targets. Define the behavior of Mock object artifactRepository
        BDDMockito.given(artifactRepository.findOneById(Mockito.anyString())).willReturn(Optional.empty()); // Defines the behaviour of the mock object.

        // When. Act on the target behaviour. When steps should cover the method to betested.
        Throwable thrown = catchThrowable(() -> {
            artifactService.findById("125478892313");
        });

        // Then. Assert expected outcomes
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 125478892313 :(");

        verify(artifactRepository, times(1)).findOneById("125478892313"); // Verify artifactRepository calls one time findById method.
    }

    @Test
    void testFindAllSuccess() {
        // Given
        BDDMockito.given(artifactRepository.findAllByOrderByName()).willReturn(this.artifacts);

        // When
        List<Artifact> actualArtifacts = this.artifactService.findAll();

        // Then
        assertEquals(this.artifacts.size(), actualArtifacts.size());
        verify(artifactRepository, times(1)).findAllByOrderByName();

    }

    @Test
    void testSaveSuccess() {
        // Given
        Artifact newArtifact = new Artifact();
        newArtifact.setName("Artifact 3");
        newArtifact.setDescription("Description");
        newArtifact.setImageUrl("Image Url");
        BDDMockito.given(idWorker.nextId()).willReturn(123456L);
        BDDMockito.given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

        // When
        Artifact savedArtifact = this.artifactService.save(newArtifact);

        // Then
        assertEquals("123456", savedArtifact.getId());
        assertEquals(newArtifact.getName(), savedArtifact.getName());
        assertEquals(newArtifact.getDescription(), savedArtifact.getDescription());
        assertEquals(newArtifact.getImageUrl(), savedArtifact.getImageUrl());
        verify(artifactRepository, times(1)).save(newArtifact);
        verify(idWorker, times(1)).nextId();
    }

    @Test
    void testUpdateSuccess() {
        // Given
        Artifact oldArtifact = new Artifact();
        oldArtifact.setId("125478892313");
        oldArtifact.setName("Invisibility Cloak");
        oldArtifact.setDescription("An invisibility cloak is used to make the wearer invisible");
        oldArtifact.setImageUrl("ImageUrl");

        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setName("A new Name");
        updatedArtifact.setDescription("A new description");
        updatedArtifact.setImageUrl("A new Image Url");

        BDDMockito.given(artifactRepository.findOneById("125478892313"))
                .willReturn(Optional.of(oldArtifact));
        
        BDDMockito.given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);

        // When
        Artifact finalArtifact = artifactService.update("125478892313", updatedArtifact);

        // Then
        assertEquals("125478892313", finalArtifact.getId());
        assertEquals(updatedArtifact.getName(), finalArtifact.getName());
        assertEquals(updatedArtifact.getDescription(), finalArtifact.getDescription());
        assertEquals(updatedArtifact.getImageUrl(), finalArtifact.getImageUrl());
        verify(artifactRepository, times(1))
                .findOneById("125478892313");
        verify(artifactRepository, times(1))
                .save(oldArtifact);
    }

    @Test
    void testUpdateNotFound() {
        // Given
        Artifact updatedArtifact = new Artifact();
        updatedArtifact.setName("Invisibility Cloak");
        updatedArtifact.setDescription("A new description");
        updatedArtifact.setImageUrl("ImageUrl");

        BDDMockito.given(artifactRepository.findOneById(Mockito.anyString()))
                .willReturn(Optional.empty());
        // When
        Throwable throwable = catchThrowable(() -> {
            artifactService.update("125478892313", updatedArtifact);
        });

        // Then
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 125478892313 :(");

        verify(artifactRepository, times(1))
                .findOneById("125478892313");
    }

    @Test
    void testDeleteSuccess() {

        // Given
        Artifact artifactToDelete = new Artifact();
        artifactToDelete.setId("3243242353");
        artifactToDelete.setName("Invisibility Cloak");
        artifactToDelete.setDescription("A new description");
        artifactToDelete.setImageUrl("ImageUrl");

        BDDMockito.given(artifactRepository.existsById("3243242353"))
                .willReturn(true);
        BDDMockito.doNothing().when(artifactRepository).deleteById("3243242353");

        // When
        artifactService.delete("3243242353");

        // Then
        verify(artifactRepository, times(1)).deleteById("3243242353");
        verify(artifactRepository, times(1)).existsById("3243242353");
    }

    @Test
    void testDeleteNotFound() {
        
        // Given
        BDDMockito.given(artifactRepository.existsById("3243242353"))
                .willReturn(false);

        // When
        assertThrows(
                ObjectNotFoundException.class,
                () -> artifactService.delete("3243242353"));

        // Then
        verify(artifactRepository, times(0)).deleteById("3243242353");
        verify(artifactRepository, times(1)).existsById("3243242353");
    }
}
