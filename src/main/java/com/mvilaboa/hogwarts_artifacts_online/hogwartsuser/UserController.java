package com.mvilaboa.hogwarts_artifacts_online.hogwartsuser;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.converter.UserDtoToUserConverter;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.converter.UserToUserDtoConverter;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.dto.UserDto;
import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;

import jakarta.validation.Valid;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class UserController {

    private final UserService userService;

    private final UserDtoToUserConverter userDtoToUserConverter;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public UserController(UserService userService, UserDtoToUserConverter userDtoToUserConverter,
            UserToUserDtoConverter userToUserDtoConverter) {
        this.userService = userService;
        this.userDtoToUserConverter = userDtoToUserConverter;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    @GetMapping({ "", "/" })
    public ResponseEntity<Result<List<UserDto>>> findAllUsers() {
        List<UserDto> listOfDtos = userService.findAll().stream()
                .map(userToUserDtoConverter::convert).toList();
        Result<List<UserDto>> resultToSend = new Result<>();
        resultToSend.setFlag(true);
        resultToSend.setCode(StatusCode.SUCCESS);
        resultToSend.setMessage("Find All Success");
        resultToSend.setData(listOfDtos);
        return ResponseEntity.ok().body(resultToSend);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Result<UserDto>> findUserById(@PathVariable Integer userId) {
        UserDto userDto = userToUserDtoConverter.convert(userService.findById(userId));
        Result<UserDto> resultToSend = new Result<>();
        resultToSend.setFlag(true);
        resultToSend.setCode(StatusCode.SUCCESS);
        resultToSend.setMessage("Find One Success");
        resultToSend.setData(userDto);
        return ResponseEntity.ok().body(resultToSend);
    }

    @PostMapping({ "/", "" })
    public ResponseEntity<Result<UserDto>> addUser(@Valid @RequestBody HogwartsUser hogwartsUser) {
        UserDto savedUser = userToUserDtoConverter.convert(userService.save(hogwartsUser));
        Result<UserDto> resultToSend = new Result<>();
        resultToSend.setFlag(true);
        resultToSend.setCode(StatusCode.SUCCESS);
        resultToSend.setMessage("Add Success");
        resultToSend.setData(savedUser);
        return ResponseEntity.ok().body(resultToSend);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Result<UserDto>> updateUser(
            @PathVariable Integer userId, @Valid @RequestBody UserDto userDto) {

        UserDto updatedUser = userToUserDtoConverter
                .convert(userService
                        .updateById(userId, this.userDtoToUserConverter.convert(userDto)));
        Result<UserDto> resultToSend = new Result<>();
        resultToSend.setFlag(true);
        resultToSend.setCode(StatusCode.SUCCESS);
        resultToSend.setMessage("Update Success");
        resultToSend.setData(updatedUser);
        return ResponseEntity.ok().body(resultToSend);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Result<String>> deleteUser(@PathVariable Integer userId) {
        userService.deleteById(userId);
        Result<String> resultToSend = new Result<>();
        resultToSend.setFlag(true);
        resultToSend.setCode(StatusCode.SUCCESS);
        resultToSend.setMessage("Delete Success");
        return ResponseEntity.ok().body(resultToSend);
    }

}
