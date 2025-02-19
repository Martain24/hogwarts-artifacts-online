package com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.HogwartsUser;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.dto.UserDto;

@Component
public class UserToUserDtoConverter implements Converter<HogwartsUser, UserDto> {

    @Override
    @Nullable
    public UserDto convert(@NonNull HogwartsUser source) {
        return new UserDto(
                source.getId(), source.getUsername(), source.isEnabled(), source.getRoles());
    }

}
