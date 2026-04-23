package taskmanagement.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import taskmanagement.model.AppUser;
import taskmanagement.repository.AppUserRepository;

@RestController
public class AccountController {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/accounts")
    public ResponseEntity<?> addUser(@Valid @RequestBody RegistrationRequest request) {
        String username = request.email;
        if (appUserRepository.findAppUserByUsername(username.toLowerCase()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        var appUser = new AppUser();
        appUser.setUsername(username.toLowerCase());
        appUser.setPassword(passwordEncoder.encode(request.password));
        appUser.setAuthority("ROLE_USER");

        appUserRepository.save(appUser);

        return ResponseEntity.ok().build();
    }

    record RegistrationRequest(
            @NotBlank @Pattern(regexp = ".+@.+\\..+") String email,
            @NotBlank @Size(min = 6) String password,
            String authority
    ) {}
}
