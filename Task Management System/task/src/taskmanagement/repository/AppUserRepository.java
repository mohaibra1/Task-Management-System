package taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taskmanagement.model.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findAppUserByUsername(String username);
}
