package taskmanagement.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import taskmanagement.model.AppUser;
import taskmanagement.repository.AppUserRepository;
import taskmanagement.security.JwtService;

import java.util.Map;

@RestController
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService, AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
    }


    @PostMapping("/api/auth/token")
    public Map<String, String> token(Authentication authentication) {
        String token = jwtService.generateToken(authentication.getName());
        return Map.of("token", token);
    }


}
