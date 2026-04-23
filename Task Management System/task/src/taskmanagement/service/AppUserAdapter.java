package taskmanagement.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import taskmanagement.model.AppUser;

import java.util.Collection;
import java.util.List;

public class AppUserAdapter implements UserDetails {
    private final AppUser appUser;

    public AppUserAdapter(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Make sure the role is not null and has a prefix like "ROLE_"
        // If your database stores "USER", use "ROLE_" + user.getRole()
        String role = appUser.getAuthority();

        if (role == null || role.isBlank()) {
            role = "USER"; // Fallback default
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
