package taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taskmanagement.model.Task;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Requirement: Arrange the task list so that the most recent tasks show up first.
     * Spring Data JPA interprets "ByOrderByCreatedAtDesc" as
     * "SELECT * FROM Task ORDER BY created_at DESC".
     */
    List<Task> findAllByOrderByCreatedAtDesc();
    List<Task> findByCreatorNameOrderByCreatedAtDesc(String email);
}
