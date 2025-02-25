package com.mvilaboa.hogwarts_artifacts_online.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvilaboa.hogwarts_artifacts_online.system.Result;
import com.mvilaboa.hogwarts_artifacts_online.system.StatusCode;

@RestController
@RequestMapping("${api.endpoint.base-url}/users")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Result<Object>> getLoginInfo(Authentication authentication) {
        LOGGER.debug("Authenticated user: '{}'", authentication.getName());
        return ResponseEntity.ok()
                .body(new Result<>(
                        true, StatusCode.SUCCESS,
                        "User Info and JSON Web Token",
                        this.authService.createLoginInfo(authentication)));
    }

}
