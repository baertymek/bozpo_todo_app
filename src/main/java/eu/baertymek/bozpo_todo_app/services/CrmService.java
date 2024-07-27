package eu.baertymek.bozpo_todo_app.services;

import eu.baertymek.bozpo_todo_app.entities.Task;
import eu.baertymek.bozpo_todo_app.repositories.TaskRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrmService {
    private final TaskRepository taskRepository;

    public CrmService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> findTasksByPage(Integer page) {
        return taskRepository.findAll(PageRequest.of(page-1, 10)).stream().toList();
    }

    public long countTasks() {
        return taskRepository.count();
    }

    public void saveTask(Task task) {
        if (task == null) {
            System.out.println("Task is null. Something is probably not correct");
            return;
        }
        taskRepository.save(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }


}
