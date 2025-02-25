package com.mvilaboa.hogwarts_artifacts_online.hogwartsuser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.dto.UserDto;
import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Transactional
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    Integer existentUserId;
    Integer nonExistentUserId;

    @Value("${api.endpoint.base-url}")
    String baseUrl;

    @BeforeEach
    void setUp() {
        existentUserId = 1;
        nonExistentUserId = 20;

        assertThat(userRepository.existsById(existentUserId)).isTrue();
        assertThat(userRepository.existsById(nonExistentUserId)).isFalse();
    }


    @Test
    void testFindAllUsersSuccess() throws Exception {
        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.get(baseUrl + "/users")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<List<UserDto>>>() {
        }));
        Result<List<UserDto>> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(result.isFlag()).isTrue();
        assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
        assertThat(result.getMessage()).isEqualTo("Find All Success");
        assertThat(result.getData().size()).isEqualTo(userService.findAll().size());
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        HogwartsUser userToFind = userService.findById(existentUserId);

        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.get(baseUrl + "/users/" + existentUserId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<UserDto>>() {
        }));
        Result<UserDto> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(result.isFlag()).isTrue();
        assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
        assertThat(result.getMessage()).isEqualTo("Find One Success");
        assertThat(result.getData().id()).isEqualTo(userToFind.getId());
        assertThat(result.getData().username()).isEqualTo(userToFind.getUsername());
        assertThat(result.getData().roles()).isEqualTo(userToFind.getRoles());
    }

    @Test
    void testFindUserByIdNotFound() throws Exception {
        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.get(baseUrl + "/users/" + nonExistentUserId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<UserDto>>() {
        }));
        Result<UserDto> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(result.isFlag()).isFalse();
        assertThat(result.getCode()).isEqualTo(StatusCode.NOT_FOUND);
        assertThat(result.getMessage()).isEqualTo("Could not find user with Id " + nonExistentUserId + " :(");
        assertThat(result.getData()).isNull();
    }

    @Test
    void testAddUserSuccess() throws Exception {
        HogwartsUser userToAdd = new HogwartsUser();
        userToAdd.setUsername("newUser");
        userToAdd.setPassword("password");
        userToAdd.setRoles("student");
        userToAdd.setEnabled(true);

        String jsonUser = objectMapper.writeValueAsString(userToAdd);

        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.post(baseUrl + "/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonUser)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<UserDto>>() {
        }));
        Result<UserDto> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(result.isFlag()).isTrue();
        assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
        assertThat(result.getMessage()).isEqualTo("Add Success");
        assertThat(userRepository.existsByUsername(userToAdd.getUsername())).isTrue();
        assertThat(result.getData().id()).isNotNull();
        assertThat(result.getData().username()).isEqualTo(userToAdd.getUsername());
        assertThat(result.getData().roles()).isEqualTo(userToAdd.getRoles());
    }

    @Test
    void testAddUserWithExistingUsername() throws Exception {
        HogwartsUser existentUser = userRepository.findById(existentUserId).orElseThrow();
        HogwartsUser userToAdd = new HogwartsUser();
        userToAdd.setUsername(existentUser.getUsername());
        userToAdd.setPassword("password");
        userToAdd.setRoles("student");
        userToAdd.setEnabled(true);


        String jsonUser = objectMapper.writeValueAsString(userToAdd);

        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.post(baseUrl + "/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonUser)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<Map<String, String>>>() {
        }));
        Result<Map<String, String>> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(result.isFlag()).isFalse();
        assertThat(result.getCode()).isEqualTo(StatusCode.INVALID_ARGUMENT);
        assertThat(result.getMessage()).isEqualTo("Already exists a user with Username " + userToAdd.getUsername() + " :(");
        assertThat(result.getData()).isNull();
    }

    @Test
    void testAddUserWithBlankUsername() throws Exception {
        HogwartsUser userToAdd = new HogwartsUser();
        userToAdd.setUsername("");
        userToAdd.setPassword("password");
        userToAdd.setRoles("student");
        userToAdd.setEnabled(true);

        String jsonUser = objectMapper.writeValueAsString(userToAdd);

        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.post(baseUrl + "/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonUser)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<Map<String, String>>>() {
        }));
        Result<Map<String, String>> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(result.isFlag()).isFalse();
        assertThat(result.getCode()).isEqualTo(StatusCode.INVALID_ARGUMENT);
        assertThat(result.getMessage()).isEqualTo("Provided arguments are invalid");
        assertThat(result.getData().containsKey("username")).isTrue();
    }


    @Test
    void testUpdateUserSuccess() throws Exception {
        UserDto userToUpdate = new UserDto(null, "updatedUser", true, "teacher");
        String jsonUser = objectMapper.writeValueAsString(userToUpdate);

        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.put(baseUrl + "/users/" + existentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<UserDto>>() {
        }));
        Result<UserDto> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(result.isFlag()).isTrue();
        assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
        assertThat(result.getMessage()).isEqualTo("Update Success");
        assertThat(result.getData().username()).isEqualTo(userToUpdate.username());
        assertThat(result.getData().roles()).isEqualTo(userToUpdate.roles());
        assertThat(userRepository.findById(existentUserId).orElseThrow().getUsername()).isEqualTo(userToUpdate.username());
        assertThat(userRepository.findById(existentUserId).orElseThrow().getRoles()).isEqualTo(userToUpdate.roles());
    }

    @Test
    void testUpdateUserNotFound() throws Exception {
        UserDto userToUpdate = new UserDto(null, "updatedUser", true, "teacher");
        String jsonUser = objectMapper.writeValueAsString(userToUpdate);

        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.put(baseUrl + "/users/" + nonExistentUserId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonUser)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<UserDto>>() {
        }));
        Result<UserDto> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });

        assertThat(result.isFlag()).isFalse();
        assertThat(result.getCode()).isEqualTo(StatusCode.NOT_FOUND);
        assertThat(result.getMessage()).isEqualTo("Could not find user with Id " + nonExistentUserId + " :(");
        assertThat(result.getData()).isNull();
    }


    @Test
    void testDeleteUserSuccess() throws Exception {
        assertThat(userRepository.existsById(existentUserId)).isTrue();

        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.delete(baseUrl + "/users/" + existentUserId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
        }));

        Result<String> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertThat(result.isFlag()).isTrue();
        assertThat(result.getCode()).isEqualTo(StatusCode.SUCCESS);
        assertThat(result.getMessage()).isEqualTo("Delete Success");
        assertThat(userRepository.existsById(existentUserId)).isFalse();
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
        assertThat(userRepository.existsById(nonExistentUserId)).isFalse();

        String jsonResponse = mockMvc.perform(
                        MockMvcRequestBuilders.delete(baseUrl + "/users/" + nonExistentUserId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        assertDoesNotThrow(() -> objectMapper.readValue(jsonResponse, new TypeReference<Result<String>>() {
        }));

        Result<String> result = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertThat(result.isFlag()).isFalse();
        assertThat(result.getCode()).isEqualTo(StatusCode.NOT_FOUND);
        assertThat(result.getMessage()).isEqualTo("Could not find user with Id " + nonExistentUserId + " :(");
        assertThat(result.getData()).isNull();
    }

}