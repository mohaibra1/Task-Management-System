package taskmanagement.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import taskmanagement.dto.TaskDTO;
import taskmanagement.dto.TaskListDTO;
import taskmanagement.model.Author;
import taskmanagement.model.Task;
import taskmanagement.repository.AuthorRepository;
import taskmanagement.repository.CommentRepository;
import taskmanagement.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final AuthorRepository authorRepository;
    private final CommentRepository commentRepository;

    public TaskService(TaskRepository taskRepository, AuthorRepository authorRepository, CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.authorRepository = authorRepository;
        this.commentRepository = commentRepository;
    }

    public Task createTask(String title, String description, Author author) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus("CREATED");
        task.setCreator(author);
        task.setOwner(null);
        return taskRepository.save(task);
    }

    public Optional<Task> findTaskById(Long taskId) {
        return taskRepository.findById(taskId);
    }

    public Task assignTask(Long taskId, String assigneeEmail, String requesterName) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (!task.getCreator().getName().equalsIgnoreCase(requesterName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the author can assign tasks");
        }

        if ("none".equalsIgnoreCase(assigneeEmail)) {
            task.setOwner(null);
        } else {
            Author assignee = authorRepository.findByName(assigneeEmail.toLowerCase())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
            task.setOwner(assignee);
        }

        return taskRepository.save(task);
    }

    public Task updateStatus(Long taskId, String newStatus, String requesterName) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        boolean isAuthor = task.getCreator().getName().equalsIgnoreCase(requesterName);
        boolean isAssignee = task.getOwner() != null &&
                task.getOwner().getName().equalsIgnoreCase(requesterName);

        if (!isAuthor && !isAssignee) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to change status");
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public List<TaskListDTO> getAllTasksSorted() {
        return taskRepository.findAllByOrderByIdDesc()
                .stream().map(this::toListDTO).toList();
    }

    public List<TaskListDTO> getTasksByAuthor(String author) {
        return taskRepository.findByCreatorNameIgnoreCaseOrderByIdDesc(author)
                .stream().map(this::toListDTO).toList();
    }

    public List<TaskListDTO> getTasksByAssignee(String assignee) {
        return taskRepository.findByOwnerNameIgnoreCaseOrderByIdDesc(assignee)
                .stream().map(this::toListDTO).toList();
    }

    public List<TaskListDTO> getTasksByAuthorAndAssignee(String author, String assignee) {
        return taskRepository.findByCreatorNameIgnoreCaseAndOwnerNameIgnoreCaseOrderByIdDesc(author, assignee)
                .stream().map(this::toListDTO).toList();
    }



    public TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId().toString());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setAuthor(task.getCreator().getName());
        dto.setAssignee(task.getOwner() != null ? task.getOwner().getName() : "none");
        return dto;
    }

    public TaskListDTO toListDTO(Task task) {
        TaskListDTO dto = new TaskListDTO();
        dto.setId(task.getId().toString());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setAuthor(task.getCreator().getName());
        dto.setAssignee(task.getOwner() != null ? task.getOwner().getName() : "none");
        dto.setTotal_comments(commentRepository.countByTaskId(task.getId()));
        return dto;
    }
}

