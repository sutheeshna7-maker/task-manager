package com.sutheeshna.task_manager.controller;

import com.sutheeshna.task_manager.model.Project;
import com.sutheeshna.task_manager.model.User;
import com.sutheeshna.task_manager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectRepository projectRepository;

    @GetMapping
    public List<Project> getMyProjects(@AuthenticationPrincipal User user) {
        return projectRepository.findByOwnerId(user.getId());
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project, @AuthenticationPrincipal User user) {
        if (project.getTitle() == null || project.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body("Title cannot be empty");
        }
        project.setOwner(user);
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Project project = projectRepository.findById(id)
                .orElse(null);
        if (project == null) {
            return ResponseEntity.status(404).body("Project not found");
        }
        if (!project.getOwner().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You don't own this project");
        }
        projectRepository.deleteById(id);
        return ResponseEntity.ok("Project deleted");
    }
}