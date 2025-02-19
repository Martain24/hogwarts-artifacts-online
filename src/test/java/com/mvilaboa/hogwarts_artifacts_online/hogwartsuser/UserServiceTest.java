package com.mvilaboa.hogwarts_artifacts_online.hogwartsuser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.mvilaboa.hogwarts_artifacts_online.system.exception.AlreadyInDbException;
import com.mvilaboa.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    Integer existentUserId;
    Integer anotherExistentUserId;
    Integer nonExistentUserId;

    @BeforeEach
    void setUp() {
        this.existentUserId = 1;
        this.anotherExistentUserId = 2;
        this.nonExistentUserId = 20;

        assertThat(userRepository.existsById(existentUserId)).isTrue();
        assertThat(userRepository.existsById(anotherExistentUserId)).isTrue();
        assertThat(userRepository.existsById(nonExistentUserId)).isFalse();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllSuccess() {
        // When
        List<HogwartsUser> allUsers = userService.findAll();

        // Then
        assertThat(allUsers.size()).isEqualTo(userRepository.findAll().size());
    }

    @Test
    void testFindByIdSuccess() {
        // When
        HogwartsUser hogwartsUser = userService.findById(existentUserId);

        // Then
        assertThat(hogwartsUser.getId()).isEqualTo(existentUserId);
        assertThat(userRepository.findOneById(existentUserId).orElseThrow())
                .isEqualTo(hogwartsUser);
    }

    @Test
    void testSaveUserSuccess() {

        // Given
        HogwartsUser newUser = new HogwartsUser();
        newUser.setPassword("pass");
        newUser.setUsername("nuevo usuario");
        newUser.setRoles("admin user");

        // When
        HogwartsUser savedUser = userService.save(newUser);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(newUser.getUsername());
        assertThat(savedUser.getRoles()).isEqualTo(newUser.getRoles());
        assertThat(userRepository.findOneById(savedUser.getId()))
                .isNotEmpty();
        assertThat(userRepository.findOneById(savedUser.getId()).get())
                .isEqualTo(savedUser);

    }

    @Test
    void testSaveUserWithAlreadyExistingUsername() {

        // Given
        HogwartsUser newUser = new HogwartsUser();
        newUser.setPassword("pass");
        newUser.setUsername(userRepository.findOneById(existentUserId).orElseThrow().getUsername());
        newUser.setRoles("admin user");

        // When
        Throwable throwable = catchThrowable(
                () -> userService.save(newUser));

        // Then
        assertThat(throwable)
                .isInstanceOf(AlreadyInDbException.class)
                .hasMessage(getMessageOfRepeatedUsername(newUser.getUsername()));

    }

    @Test
    void testUpdateByIdSuccess() {
        // Given
        HogwartsUser user = new HogwartsUser();
        user.setUsername("nuevo usuario");
        user.setEnabled(false);
        user.setRoles("user");

        // When
        userService.updateById(existentUserId, user);

        // Then
        HogwartsUser updatedUser = userRepository.findOneById(existentUserId).orElseThrow();
        assertThat(updatedUser.getId()).isEqualTo(this.existentUserId);
        assertThat(updatedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(updatedUser.getRoles()).isEqualTo(user.getRoles());
    }

    @Test
    void testUpdateByIdWithNonExistentId() {
        // Given
        HogwartsUser user = new HogwartsUser();

        // When
        Throwable throwable = catchThrowable(
                () -> userService.updateById(this.nonExistentUserId, user));

        // Then
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage(getMessageOfUserNotFoundError(this.nonExistentUserId));
    }

    @Test
    void testUpdateByIdWithAlreadyExistentUsername() {
        // Given
        HogwartsUser newUser = new HogwartsUser();
        newUser.setPassword("pass");
        newUser.setUsername(userRepository.findOneById(anotherExistentUserId).orElseThrow().getUsername());
        newUser.setRoles("admin user");

        // When
        Throwable throwable = catchThrowable(
                () -> userService.updateById(this.existentUserId, newUser));

        // Then
        assertThat(throwable)
                .isInstanceOf(AlreadyInDbException.class)
                .hasMessage(getMessageOfRepeatedUsername(newUser.getUsername()));
    }

    @Test
    void testDeleteByIdSuccess() {
        // When
        userService.deleteById(this.existentUserId);

        // Then
        assertThat(userRepository.existsById(this.existentUserId)).isFalse();
    }

    @Test
    void testDeleteByIdWithNonExistentId() {
        // When
        Throwable throwable = catchThrowable(
                () -> userService.deleteById(this.nonExistentUserId));

        // Then
        assertThat(throwable)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage(getMessageOfUserNotFoundError(this.nonExistentUserId));
    }

    private String getMessageOfUserNotFoundError(Integer userId) {
        return "Could not find user with Id " + userId + " :(";
    }

    private String getMessageOfRepeatedUsername(String username) {
        return "Already exists a user with Username " + username + " :(";
    }
}
