package com.mvilaboa.hogwarts_artifacts_online.wizard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvilaboa.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;
import com.mvilaboa.hogwarts_artifacts_online.wizard.dto.WizardDto;

@Transactional
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class WizardControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	WizardService wizardService;

	@Autowired
	WizardRepository wizardRepository;

	@Autowired
	ArtifactRepository artifactRepository;

	@Autowired
	ObjectMapper objectMapper;

	String jwtToken;

	String existentArtifactId;
	String nonExistentArtifactId;
	Integer existentWizardId;
	Integer nonExistentWizardId;

	@Value("${api.endpoint.base-url}")
	String baseUrl;

	@BeforeEach
	void setUp() throws Exception {
		String jsonResponse = this.mockMvc.perform(
				MockMvcRequestBuilders.post(baseUrl + "/users/login")
						.with(SecurityMockMvcRequestPostProcessors.httpBasic("john", "123456"))
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<Map<String, Object>>>() {
			});
		});
		Result<Map<String, Object>> result = objectMapper
				.readValue(jsonResponse, new TypeReference<>() {
				});
		jwtToken = result.getData().get("token").toString();

		existentWizardId = 2;
		nonExistentWizardId = 20;
		existentArtifactId = "artifact9";
		nonExistentArtifactId = "no_existe_este_id";

		assertThat(wizardRepository.existsById(existentWizardId)).isTrue();
		assertThat(wizardRepository.existsById(nonExistentWizardId)).isFalse();
		assertThat(artifactRepository.existsById(existentArtifactId)).isTrue();
		assertThat(artifactRepository.existsById(nonExistentArtifactId)).isFalse();
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void testFindWizardByIdSuccess() throws Exception {

		// Given
		Wizard wizardToFind = wizardService.findById(existentWizardId);

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.get(baseUrl + "/wizards/" + existentWizardId)
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<WizardDto>>() {
			});
		});
		Result<WizardDto> result = objectMapper
				.readValue(jsonResponse, new TypeReference<>() {
				});

		assertThat(result.isFlag()).withFailMessage(() -> "En success el flag no puede ser false")
				.isEqualTo(true);

		assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
		assertThat(result.getMessage()).isEqualTo("Find One Success");

		assertThat(result.getData().id()).isEqualTo(wizardToFind.getId());
		assertThat(result.getData().name()).isEqualTo(wizardToFind.getName());
		assertThat(result.getData().numberOfArtifacts()).isEqualTo(wizardToFind.getNumberOfArtifacts());

	}

	@Test
	void testFindWizardByIdNotFoundException() throws Exception {

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.get(baseUrl + "/wizards/" + nonExistentWizardId)
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
			});
		});
		Result<String> result = objectMapper
				.readValue(jsonResponse, new TypeReference<>() {
				});
		assertThat(result.isFlag()).isEqualTo(false);
		assertThat(result.getCode()).isEqualTo(StatusCode.NOT_FOUND);
		assertThat(result.getMessage())
				.isEqualTo("Could not find wizard with Id " + nonExistentWizardId + " :(");
		assertThat(result.getData()).isNull();

	}

	@Test
	void testFindAllWizards() throws Exception {
		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders.get(baseUrl + "/wizards/")
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<List<WizardDto>>>() {
			});
		});
		Result<List<WizardDto>> result = objectMapper
				.readValue(jsonResponse, new TypeReference<>() {
				});
		assertThat(result.isFlag()).isEqualTo(true);
		assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
		assertThat(result.getMessage()).isEqualTo("Find All Success");
		assertThat(result.getData().size())
				.isEqualTo(wizardService.findAll().size());
	}

	@Test
	void testAddWizardSuccess() throws Exception {

		// Given
		WizardDto wizardToSave = new WizardDto(null, "Invented Name", null);
		String jsonWizard = this.objectMapper.writeValueAsString(wizardToSave);

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders.post(baseUrl + "/wizards/")
						.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonWizard)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<WizardDto>>() {
			});
		});

		Result<WizardDto> result = objectMapper
				.readValue(jsonResponse, new TypeReference<>() {
				});
		assertThat(result.isFlag()).withFailMessage(() -> "En success el flag no puede ser false")
				.isEqualTo(true);
		assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
		assertThat(result.getMessage()).isEqualTo("Add Success");

		assertThat(wizardRepository.existsByName(wizardToSave.name())).isTrue();
		assertThat(result.getData().id()).isNotNull();
		assertThat(result.getData().name()).isEqualTo(wizardToSave.name());
		assertThat(result.getData().numberOfArtifacts()).isEqualTo(0);

	}

	@Test
	void testAddWizardWithAlreadyExistingName() throws Exception {
		// Given
		Wizard existentWizard = wizardRepository.findOneById(existentWizardId).orElseThrow();
		WizardDto wizardToSave = new WizardDto(null, existentWizard.getName(), null);
		String jsonWizardToSave = objectMapper.writeValueAsString(wizardToSave);

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.post(baseUrl + "/wizards/")
						.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonWizardToSave)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
			});
		});

		Result<String> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});

		assertThat(result.isFlag()).isFalse();
		assertThat(result.getCode()).isEqualTo(StatusCode.INVALID_ARGUMENT);
		assertThat(result.getMessage()).isEqualTo(
				"Already exists a wizard with Name " + existentWizard.getName() + " :(");
		assertThat(result.getData()).isNull();
	}

	@Test
	void testAddWizardWithBlankName() throws Exception {
		// Given
		WizardDto wizardToSave = new WizardDto(null, "", null);
		String jsonWizardToSave = objectMapper.writeValueAsString(wizardToSave);

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders.post(baseUrl + "/wizards/")
						.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonWizardToSave)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<Map<String, String>>>() {
			});
		});

		Result<Map<String, String>> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});

		assertThat(result.isFlag()).isFalse();
		assertThat(result.getCode()).isEqualTo(StatusCode.INVALID_ARGUMENT);
		assertThat(result.getMessage()).isEqualTo("Provided arguments are invalid");
		assertThat(result.getData().containsKey("name")).isTrue();
	}

	@Test
	void testUpdateWizardSuccess() throws Exception {
		// Given
		WizardDto wizardToUpdate = new WizardDto(null, "new name", null);
		String jsonWizardToUpdate = objectMapper.writeValueAsString(wizardToUpdate);

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders.put(baseUrl + "/wizards/" + existentWizardId)
						.header("Authorization", "Bearer " + jwtToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonWizardToUpdate)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<WizardDto>>() {
			});
		});
		Result<WizardDto> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});

		assertThat(result.isFlag()).isTrue();
		assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
		assertThat(result.getMessage()).isEqualTo("Update Success");
		assertThat(result.getData().name()).isEqualTo(wizardToUpdate.name());
		assertThat(wizardRepository.findOneById(existentWizardId).orElseThrow().getName())
				.isEqualTo(wizardToUpdate.name());
	}

	@Test
	void testUpdateWizardWithNonExistentId() throws Exception {

		// Given
		WizardDto wizardUpdated = new WizardDto(null, "New Name", null);
		String jsonWizardUpdated = objectMapper.writeValueAsString(wizardUpdated);

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders.put(baseUrl + "/wizards/" + nonExistentWizardId)
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonWizardUpdated))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(
				() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<WizardDto>>() {
				}));
		Result<WizardDto> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});
		assertThat(result.isFlag()).isFalse();
		assertThat(result.getMessage())
				.isEqualTo("Could not find wizard with Id " + nonExistentWizardId + " :(");
		assertThat(result.getCode()).isEqualTo(StatusCode.NOT_FOUND);
		assertThat(result.getData()).isNull();

	}

	@Test
	void testDeleteWizardSuccess() throws Exception {
		// Given
		assertThat(wizardRepository.existsById(existentWizardId)).isTrue();

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders.delete(baseUrl + "/wizards/" + existentWizardId)
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
			});
		});

		Result<String> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});
		assertThat(result.isFlag()).isTrue();
		assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
		assertThat(result.getMessage()).isEqualTo("Delete Success");
		assertThat(wizardRepository.existsById(existentWizardId)).isFalse();
	}

	@Test
	void testDeleteWizardWithNonExistentId() throws Exception {
		// Given
		assertThat(wizardRepository.existsById(nonExistentWizardId)).isFalse();

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders.delete(baseUrl + "/wizards/" + nonExistentWizardId)
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
			});
		});

		Result<String> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});
		assertThat(result.isFlag()).isFalse();
		assertThat(result.getCode()).isEqualTo(StatusCode.NOT_FOUND);
		assertThat(result.getMessage()).isEqualTo("Could not find wizard with Id " + nonExistentWizardId + " :(");
		assertThat(result.getData()).isNull();
	}

	@Test
	void testAssignArtifactSuccess() throws Exception {
		// Given
		assertThat(artifactRepository.findOneById(existentArtifactId)
				.orElseThrow().getOwner().getId())
				.isNotEqualTo(existentWizardId);

		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.put(baseUrl + "/wizards/" + existentWizardId + "/artifacts/" + existentArtifactId)
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
			});
		});

		Result<String> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});
		assertThat(result.isFlag()).isTrue();
		assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
		assertThat(result.getMessage()).isEqualTo("Artifact Assignment Success");
		assertThat(result.getData()).isNull();
		assertThat(artifactRepository.findOneById(existentArtifactId)
				.orElseThrow().getOwner().getId()).isEqualTo(existentWizardId);
	}

	@Test
	void testAssignArtifactWithNonExistentWizardId() throws Exception {
		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.put(baseUrl + "/wizards/" + nonExistentWizardId + "/artifacts/" + existentArtifactId)
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
			});
		});

		Result<String> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});
		assertThat(result.isFlag()).isFalse();
		assertThat(result.getCode()).isEqualTo(StatusCode.NOT_FOUND);
		assertThat(result.getMessage()).isEqualTo("Could not find wizard with Id " + nonExistentWizardId + " :(");
		assertThat(result.getData()).isNull();
	}

	@Test
	void testAssignArtifactWithNonExistentArtifactId() throws Exception {
		// When
		String jsonResponse = mockMvc.perform(
				MockMvcRequestBuilders
						.put(baseUrl + "/wizards/" + existentWizardId + "/artifacts/" + nonExistentArtifactId)
						.header("Authorization", "Bearer " + jwtToken)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		// Then
		assertDoesNotThrow(() -> {
			objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
			});
		});

		Result<String> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
		});
		assertThat(result.isFlag()).isFalse();
		assertThat(result.getCode()).isEqualTo(StatusCode.NOT_FOUND);
		assertThat(result.getMessage()).isEqualTo("Could not find artifact with Id " + nonExistentArtifactId + " :(");
		assertThat(result.getData()).isNull();
	}

}
