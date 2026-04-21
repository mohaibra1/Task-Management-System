package taskmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.TaskDTO;
import taskmanagement.model.Author;
import taskmanagement.model.Task;
import taskmanagement.repository.AuthorRepository;
import taskmanagement.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthorRepository authorRepository;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody Task task, Authentication auth) {
        String name = auth.getName();
        Author author;
        if(authorRepository.findByName(name).isEmpty()) {
            author = new Author();
            author.setName(name);
            authorRepository.save(author);
        }
        author = authorRepository.findByName(name).get();

        Task createdTask = taskService.createTask(task.getTitle(),task.getDescription(), author);
        TaskDTO createdTaskDTO = new TaskDTO();
        createdTaskDTO.setId(createdTask.getId().toString());
        createdTaskDTO.setTitle(createdTask.getTitle());
        createdTaskDTO.setDescription(createdTask.getDescription());
        createdTaskDTO.setStatus(createdTask.getStatus());
        createdTaskDTO.setAuthor(createdTask.getCreator().getName());

        return ResponseEntity.ok(createdTaskDTO);
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getTasks(@RequestParam(required = false) String author) {
        if (author != null && !author.isBlank()) {
            // Requirement: Filter by the provided email
            return ResponseEntity.ok().body(taskService.getTasksByAuthor(author));
        }
        return ResponseEntity.ok().body(taskService.getAllTasksSorted());
    }
}
