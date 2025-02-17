package com.mvilaboa.hogwarts_artifacts_online.artifactv2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvilaboa.hogwarts_artifacts_online.artifact.Artifact;
import com.mvilaboa.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.mvilaboa.hogwarts_artifacts_online.artifact.ArtifactService;
import com.mvilaboa.hogwarts_artifacts_online.artifact.dto.ArtifactDto;
import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;

@Transactional
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class ArtifactControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ArtifactRepository artifactRepository;

	@Autowired
	ArtifactService artifactService;

	String anExistingArtifactId;
	String aNotExistingArtifactId;

	@BeforeEach
	void setUp() {
		anExistingArtifactId = "artifact1";
		aNotExistingArtifactId = "NotValidArtifactId";
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testFindArtifactByIdSuccess() throws Exception {

		// Given
		Artifact artifactToFind = artifactService.findById(anExistingArtifactId);

		// When
		String jsonResponse = this.mockMvc
				.perform(
						MockMvcRequestBuilders.get("/api/v1/artifacts/" + artifactToFind.getId())
								.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		ObjectMapper objectMapper = new ObjectMapper();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<ArtifactDto>>() {});
		});

		Result<ArtifactDto> result = objectMapper
				.readValue(jsonResponse, new TypeReference<Result<ArtifactDto>>() {});

		assertThat(result.isFlag()).isEqualTo(true)
				.withFailMessage(() -> "En success el flag no puede ser false");
		assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
		assertThat(result.getMessage()).isEqualTo("Find One Success");

		assertThat(result.getData().id()).isEqualTo(artifactToFind.getId());
		assertThat(result.getData().description()).isEqualTo(artifactToFind.getDescription());
		assertThat(result.getData().name()).isEqualTo(artifactToFind.getName());
		assertThat(result.getData().imageUrl()).isEqualTo(artifactToFind.getImageUrl());

		assertThat(result.getData().owner().id())
				.isEqualTo(artifactToFind.getOwner().getId());
		assertThat(result.getData().owner().name())
				.isEqualTo(artifactToFind.getOwner().getName());
		assertThat(result.getData().owner().numberOfArtifacts())
				.isEqualTo(artifactToFind.getOwner().getNumberOfArtifacts());

	}

}
