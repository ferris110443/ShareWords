package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.service.WorkspaceService;

@Slf4j
@RestController
@RequestMapping("api/1.0/workspace")
public class WorkspaceRestController {

    @Autowired
    WorkspaceService workspaceService;

    @PostMapping("/createWorkspace")
    public ResponseEntity<?> createWorkspace(CreateWorkspaceForm createWorkspaceForm) {
        System.out.println("Create workspace: " + createWorkspaceForm.getWorkspaceName());
        System.out.println("Create workspace: " + createWorkspaceForm.getWorkspaceDescription());
        workspaceService.createWorkspace(createWorkspaceForm);

        return ResponseEntity.ok().build();
    }

}
