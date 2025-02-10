package com.mvilaboa.hogwarts_artifacts_online.artifact;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvilaboa.hogwarts_artifacts_online.artifact.dto.ArtifactDto;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class ArtifactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ArtifactService artifactService;

    @Autowired
    ObjectMapper objectMapper;

    List<Artifact> artifacts;

    @BeforeEach
    void setUp() {
        this.artifacts = new ArrayList<>();

        Artifact a1 = new Artifact();
        a1.setId("42353225532");
        a1.setName("Deluminator");
        a1.setDescription("A Deluminator is a device invented by Albus Dumbledore.");
        a1.setImageUrl("ImageUrl1");
        this.artifacts.add(a1);

        Artifact a2 = new Artifact();
        a2.setId("87654321098");
        a2.setName("Elder Wand");
        a2.setDescription("The Elder Wand is the most powerful wand in existence.");
        a2.setImageUrl("ImageUrl2");
        this.artifacts.add(a2);

        Artifact a3 = new Artifact();
        a3.setId("12345678901");
        a3.setName("Invisibility Cloak");
        a3.setDescription("A magical cloak that makes the wearer invisible.");
        a3.setImageUrl("ImageUrl3");
        this.artifacts.add(a3);

        Artifact a4 = new Artifact();
        a4.setId("11223344556");
        a4.setName("Resurrection Stone");
        a4.setDescription("A stone that can summon the shadows of the dead.");
        a4.setImageUrl("ImageUrl4");
        this.artifacts.add(a4);

        Artifact a5 = new Artifact();
        a5.setId("99887766554");
        a5.setName("Sword of Gryffindor");
        a5.setDescription("A goblin-made sword that can absorb what makes it stronger.");
        a5.setImageUrl("ImageUrl5");
        this.artifacts.add(a5);
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void testFindArtifactByIdSuccess() throws Exception {
        // Given
        BDDMockito.given(this.artifactService.findById("42353225532"))
                .willReturn(this.artifacts.get(0));

        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts/42353225532")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Find One Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(StatusCode.SUCCESS))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value("42353225532"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("Deluminator"));

    }

    @Test
    void testFindArtifactByIdNotFound() throws Exception {
        // Given
        BDDMockito.given(this.artifactService.findById("42353225532"))
                .willThrow(new ArtifactNotFoundException("42353225532"));

        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts/42353225532")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag")
                        .value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Could not find artifact with Id 42353225532 :("))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(StatusCode.NOT_FOUND))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data")
                        .isEmpty());

    }

    @Test
    void testFindAllArtifactsSuccess() throws Exception {
        // Given
        BDDMockito.given(this.artifactService.findAll()).willReturn(this.artifacts);

        // When and then
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/artifacts/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag")
                        .value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(StatusCode.SUCCESS))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Find All Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Matchers.hasSize(this.artifacts.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id")
                        .value(this.artifacts.get(0).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].name")
                        .value(this.artifacts.get(0).getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].description")
                        .value(this.artifacts.get(0).getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id")
                        .value(this.artifacts.get(1).getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].name")
                        .value(this.artifacts.get(1).getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].description")
                        .value(this.artifacts.get(1).getDescription()));
    }

    @Test
    void testAddArtifactSuccess() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto(
                null,
                "Remembrall",
                "Description",
                "imageUrl",
                null);
        String jsonArtifact = this.objectMapper.writeValueAsString(artifactDto);

        Artifact savedArtifact = new Artifact();
        savedArtifact.setId("32732891288328");
        savedArtifact.setName("Remembrall");
        savedArtifact.setDescription("Description");
        savedArtifact.setImageUrl("imageUrl");

        BDDMockito.given(this.artifactService.save(Mockito.any(Artifact.class)))
                .willReturn(savedArtifact);

        // When and then

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/artifacts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonArtifact)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag")
                        .value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(StatusCode.SUCCESS))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Add Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id")
                        .value("32732891288328"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name")
                        .value("Remembrall"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description")
                        .value("Description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl")
                        .value("imageUrl"));

        verify(this.artifactService, times(1))
                .save(Mockito.any(Artifact.class));
    }

    @Test
    void testUpdateArtifactSuccess() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto(
                null,
                "Remembrall",
                "Description",
                "imageUrl",
                null);
        String jsonArtifact = this.objectMapper.writeValueAsString(artifactDto);

        Artifact savedArtifact = new Artifact();
        savedArtifact.setId("32732891288328");
        savedArtifact.setName("Remembrall");
        savedArtifact.setDescription("Description");
        savedArtifact.setImageUrl("imageUrl");

        BDDMockito.given(this.artifactService.update(Mockito.anyString(), Mockito.any(Artifact.class)))
                .willReturn(savedArtifact);

        // When And Then
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/artifacts/32732891288328")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonArtifact)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag")
                        .value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(StatusCode.SUCCESS))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Update Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id")
                        .value("32732891288328"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name")
                        .value("Remembrall"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description")
                        .value("Description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.imageUrl")
                        .value("imageUrl"));

        verify(this.artifactService, times(1))
                .update(Mockito.anyString(), Mockito.any(Artifact.class));
    }

    @Test
    void testUpdateArtifactWithNonExistentId() throws Exception {
        // Given
        ArtifactDto artifactDto = new ArtifactDto(
                null,
                "Remembrall",
                "Description",
                "imageUrl",
                null);
        String jsonArtifact = this.objectMapper.writeValueAsString(artifactDto);

        BDDMockito.given(this.artifactService.update(Mockito.anyString(), Mockito.any(Artifact.class)))
                .willThrow(new ArtifactNotFoundException("32732891288328"));

        // When And Then
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/artifacts/32732891288328")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonArtifact)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag")
                        .value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(StatusCode.NOT_FOUND))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Could not find artifact with Id 32732891288328 :("))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data")
                        .isEmpty());

        verify(this.artifactService, times(1))
                .update(Mockito.anyString(), Mockito.any(Artifact.class));
    }

    @Test
    void testDeleteArtifactSuccess() throws Exception {

        // Given
        BDDMockito.doNothing().when(this.artifactService).delete("32732891288328");

        // When And Then
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/artifacts/32732891288328")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag")
                        .value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(StatusCode.SUCCESS))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Delete Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data")
                        .isEmpty());
    }

    @Test
    void testDeleteArtifactErrorWithNonExistentId() throws Exception {

        // Given
        BDDMockito.doThrow(new ArtifactNotFoundException("32732891288328"))
                .when(this.artifactService).delete("32732891288328");

        // When And Then
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/artifacts/32732891288328")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flag")
                        .value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code")
                        .value(StatusCode.NOT_FOUND))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Could not find artifact with Id 32732891288328 :("))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data")
                        .isEmpty());
    }
}
