package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.service.WorkspaceService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/1.0/workspace")
public class WorkspaceRestController {

    @Autowired
    WorkspaceService workspaceService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PostMapping(path = "/createWorkspace")
    public ResponseEntity<?> createWorkspace(@RequestBody CreateWorkspaceForm createWorkspaceForm, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String creatorEmail = jwtTokenUtil.extractUserEmail(token);

        workspaceService.createWorkspace(createWorkspaceForm, creatorEmail);
        Map<String, Object> response = new HashMap<>();
        response.put("workspaceName", createWorkspaceForm.getWorkspaceName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
