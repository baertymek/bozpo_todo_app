package eu.baertymek.bozpo_todo_app.repositories;

import eu.baertymek.bozpo_todo_app.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
