package com.sutheeshna.task_manager.controller;

import com.sutheeshna.task_manager.model.Project;
import com.sutheeshna.task_manager.model.Task;
import com.sutheeshna.task_manager.model.TaskStatus;
import com.sutheeshna.task_manager.repository.ProjectRepository;
import com.sutheeshna.task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/project/{projectId}")
    public List<Task> getTasks(@PathVariable Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Map<String, Object> body) {
        if (body.get("title") == null || body.get("title").toString().isBlank()) {
            return ResponseEntity.badRequest().body("Title cannot be empty");
        }
        if (body.get("projectId") == null) {
            return ResponseEntity.badRequest().body("projectId is required");
        }
        Long projectId = Long.valueOf(body.get("projectId").toString());
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return ResponseEntity.status(404).body("Project not found");
        }
        Task task = new Task();
        task.setTitle((String) body.get("title"));
        task.setDescription((String) body.get("description"));
        task.setStatus(TaskStatus.TODO);
        task.setProject(project);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found");
        }Initial commit
        try {
            task.setStatus(TaskStatus.valueOf(body.get("status")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }
        return ResponseEntity.ok(taskRepository.save(task));
    }
}