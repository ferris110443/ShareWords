package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.form.CreateFileForm;
import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.service.FileContentService;
import org.yplin.project.service.WorkspaceService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/1.0/workspace")
public class WorkspaceRestController {

    @Autowired
    WorkspaceService workspaceService;
    @Autowired
    FileContentService fileContentService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @PostMapping(path = "/createWorkspace")
    public ResponseEntity<?> createWorkspace(@RequestBody CreateWorkspaceForm createWorkspaceForm, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String creatorEmail = jwtTokenUtil.extractUserEmail(token);
        UUID fileId = UUID.randomUUID();

        workspaceService.createWorkspace(createWorkspaceForm, creatorEmail);

        long workspaceId = fileContentService.queryWorkspaceIdFromWorkspaceName(createWorkspaceForm.getWorkspaceName());
        CreateFileForm createFileForm = new CreateFileForm();
        createFileForm.setFileName(createWorkspaceForm.getFileName());
        createFileForm.setFileId(fileId.toString());
        createFileForm.setWorkspaceId(workspaceId);
        fileContentService.createFileContent(createFileForm);


        Map<String, Object> response = new HashMap<>();
        response.put("workspaceName", createWorkspaceForm.getWorkspaceName());
        response.put("fileId", fileId.toString());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
