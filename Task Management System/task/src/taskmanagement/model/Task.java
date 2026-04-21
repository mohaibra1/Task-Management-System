package taskmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Description cannot be blank")
    private String description;

    private String status; // Requirement: Status field

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Author creator; // Requirement: User who created it

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Author owner;   // Requirement: User who owns it

    private LocalDateTime createdAt; // Requirement: For sorting by most recent

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "CREATED"; // Default status
        }
    }

    // Getters and Setters ...
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Author getOwner() {
        return owner;
    }

    public void setOwner(Author author) {
        this.owner = author;
    }

    public Author getCreator() {
        return creator;
    }

    public void setCreator(Author creator) {
        this.creator = creator;
    }
}

