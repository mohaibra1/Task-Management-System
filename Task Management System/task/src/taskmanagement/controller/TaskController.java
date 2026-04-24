package taskmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.CommentDTO;
import taskmanagement.dto.TaskDTO;
import taskmanagement.dto.TaskListDTO;
import taskmanagement.model.Author;
import taskmanagement.model.Comment;
import taskmanagement.model.Task;
import taskmanagement.repository.AuthorRepository;
import taskmanagement.repository.CommentRepository;
import taskmanagement.service.TaskService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final AuthorRepository authorRepository;
    private final CommentRepository commentRepository;

    public TaskController(TaskService taskService, AuthorRepository authorRepository, CommentRepository commentRepository) {
        this.taskService = taskService;
        this.authorRepository = authorRepository;
        this.commentRepository = commentRepository;
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody Task task, Authentication auth) {
        String name = auth.getName();
        if (authorRepository.findByName(name).isEmpty()) {
            Author author = new Author();
            author.setName(name);
            authorRepository.save(author);
        }
        Author author = authorRepository.findByName(name).get();
        Task created = taskService.createTask(task.getTitle(), task.getDescription(), author);
        return ResponseEntity.ok(taskService.toDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<TaskListDTO>> getTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee) {

        if (author != null && assignee != null) {
            return ResponseEntity.ok(taskService.getTasksByAuthorAndAssignee(author, assignee));
        } else if (author != null) {
            return ResponseEntity.ok(taskService.getTasksByAuthor(author));
        } else if (assignee != null) {
            return ResponseEntity.ok(taskService.getTasksByAssignee(assignee));
        }
        return ResponseEntity.ok(taskService.getAllTasksSorted());
    }

    @PutMapping("/{taskId}/assign")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> body,
            Authentication auth) {

        String assigneeEmail = body.get("assignee");

        // Validate: must be "none" or a valid email format
        if (assigneeEmail == null ||
                (!assigneeEmail.equalsIgnoreCase("none") && !assigneeEmail.matches(".+@.+\\..+"))) {
            return ResponseEntity.badRequest().build();
        }

        Task updated = taskService.assignTask(taskId, assigneeEmail, auth.getName());
        return ResponseEntity.ok(taskService.toDTO(updated));
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskDTO> updateStatus(
            @PathVariable Long taskId,
            @RequestBody Map<String, String> body,
            Authentication auth) {

        String status = body.get("status");
        List<String> valid = List.of("CREATED", "IN_PROGRESS", "COMPLETED");

        if (status == null || !valid.contains(status)) {
            return ResponseEntity.badRequest().build();
        }

        Task updated = taskService.updateStatus(taskId, status, auth.getName());
        return ResponseEntity.ok(taskService.toDTO(updated));
    }

    @PostMapping("{taskId}/comments")
    public ResponseEntity<?> postComment(@PathVariable Long taskId,
                                        @Valid @RequestBody Map<String, String> body,
                                        Authentication auth){
        String text = body.get("text");

        if(text == null || text.isBlank()){
            return ResponseEntity.badRequest().build();
        }
        if (taskService.findTaskById(taskId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setTaskId(taskId);
        comment.setAuthor(auth.getName());
        commentRepository.save(comment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("{taskId}/comments")
    public ResponseEntity<List<CommentDTO>>  getComments(@PathVariable Long taskId){
        if(taskService.findTaskById(taskId).isEmpty()){
            return  ResponseEntity.notFound().build();
        }
        List<CommentDTO> comments = commentRepository.findByTaskIdOrderByIdDesc(taskId)
                .stream()
                .map(c -> {
                    CommentDTO dto = new CommentDTO();
                    dto.setId(c.getId().toString());
                    dto.setTask_id(c.getTaskId().toString());
                    dto.setText(c.getText());
                    dto.setAuthor(c.getAuthor().toString());
                    return dto;
                }).toList();
        return ResponseEntity.ok(comments);
    }
}

