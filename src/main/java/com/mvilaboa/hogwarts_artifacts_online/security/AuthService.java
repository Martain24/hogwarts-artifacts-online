package com.mvilaboa.hogwarts_artifacts_online.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.MyUserPrincipal;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.converter.UserToUserDtoConverter;
import com.mvilaboa.hogwarts_artifacts_online.hogwartsuser.dto.UserDto;

@Service
public class AuthService {
    
    private final JwtProvider jwtProvider;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
        UserDto userDto = userToUserDtoConverter.convert(principal.getHogwartsUser());
        String token = this.jwtProvider.createToken(authentication);
        Map<String, Object> jsonLoginInfo = new HashMap<>();
        jsonLoginInfo.put("userInfo", userDto);
        jsonLoginInfo.put("token", token);
        return jsonLoginInfo;
    }

    
}
