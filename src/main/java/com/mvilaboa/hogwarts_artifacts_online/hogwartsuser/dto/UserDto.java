package com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDto(
        Integer id,
        @NotBlank(message = "username is required")
        String username,
        boolean enabled,
        @NotBlank(message = "roles are required")
        String roles) {

}
