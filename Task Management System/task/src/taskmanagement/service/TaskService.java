package taskmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import taskmanagement.dto.TaskDTO;
import taskmanagement.model.Author;
import taskmanagement.model.Task;
import taskmanagement.repository.AuthorRepository;
import taskmanagement.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AuthorRepository authorRepository;

    /**
     * Requirement: Any authenticated user can create a task.
     * The logged-in user is automatically set as the 'creator'.
     */
    public Task createTask(String title, String description, Author owner) {
        // 1. Get the current logged-in username from Spring Security context
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Find the Author entity for that username (creator)
        Author creator = authorRepository.findByName(currentUsername)
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus("CREATED");
        task.setCreator(creator); // Authenticated user is the creator
        task.setOwner(owner);     // Provided user is the owner

        return taskRepository.save(task);
    }

    /**
     * Requirement: Arrange the list so most recent tasks show up first.
     */
    public List<TaskDTO> getAllTasksSorted() {
        List<Task> tasks =  taskRepository.findAllByOrderByCreatedAtDesc();
        return convertToDTO(tasks);
    }

    public List<TaskDTO> getTasksByAuthor(String email) {
        List<Task> tasks = taskRepository.findByCreatorNameOrderByCreatedAtDesc(email);
        return convertToDTO(tasks);
    }

    public List<TaskDTO> convertToDTO(List<Task> tasks) {
        List<TaskDTO>  taskDTOs = new ArrayList<>();
        for(Task createdTask : tasks) {
            TaskDTO createdTaskDTO = new TaskDTO();
            createdTaskDTO.setId(createdTask.getId().toString());
            createdTaskDTO.setTitle(createdTask.getTitle());
            createdTaskDTO.setDescription(createdTask.getDescription());
            createdTaskDTO.setStatus(createdTask.getStatus());
            createdTaskDTO.setAuthor(createdTask.getCreator().getName());

            taskDTOs.add(createdTaskDTO);
        }
        return taskDTOs;
    }
}
