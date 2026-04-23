package taskmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import taskmanagement.model.AppUser;
import taskmanagement.repository.AppUserRepository;
import taskmanagement.service.AppUserAdapter;
//import org.springframework.stereotype.Service;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AppUserRepository appUserRepository;

    // Inject your filter here
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AppUserRepository appUserRepository) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.appUserRepository = appUserRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(sessions ->
                        sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/actuator/shutdown", "/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/accounts").permitAll()
                        // New: Allow the token endpoint to be accessed via Basic Auth
                        .requestMatchers("/api/auth/token").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/tasks").authenticated()
                        // Changed from denyAll() to authenticated() to allow Bearer tokens
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                // New: Add the JWT filter before the standard authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            AppUser user = appUserRepository.findAppUserByUsername(username.toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return new AppUserAdapter(user);
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

