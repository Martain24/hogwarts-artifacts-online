package com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.HogwartsUser;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.dto.UserDto;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, HogwartsUser> {

    @Override
    @Nullable
    public HogwartsUser convert(@NonNull UserDto source) {
        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setEnabled(source.enabled());
        hogwartsUser.setId(source.id());
        hogwartsUser.setUsername(source.username());
        hogwartsUser.setRoles(source.roles());
        return hogwartsUser;
    }
    
}
