package eu.baertymek.bozpo_todo_app.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "TASKS")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description = "";

    @CreationTimestamp
    private LocalDateTime creationTime;

    private Boolean isFinished = false;


    public Task() {
    }

    public Task(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public Boolean getFinished() {
        return isFinished;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }
}
