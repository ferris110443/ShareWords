package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.form.CreateFileForm;
import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.model.FileContentModel;
import org.yplin.project.service.FileContentService;
import org.yplin.project.service.WorkspaceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping(path = "/workspace")
    public ResponseEntity<?> createWorkspace(@RequestBody CreateWorkspaceForm createWorkspaceForm, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String creatorEmail = jwtTokenUtil.extractUserEmail(token);
        workspaceService.createWorkspace(createWorkspaceForm, creatorEmail);


        Map<String, Object> response = new HashMap<>();
        response.put("workspaceName", createWorkspaceForm.getWorkspaceName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping(path = "/workspace")
    public ResponseEntity<?> getWorkspaceInformation(@RequestParam(value = "workspaceName") String workspaceName, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String creatorEmail = jwtTokenUtil.extractUserEmail(token);

        List<FileContentModel> fileList = fileContentService.getFileContentsByWorkspaceName(workspaceName);

        Map<String, List<FileContentModel>> response = new HashMap<>();
        response.put("data", fileList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping(path = "/file")
    public ResponseEntity<?> createNewFile(@RequestBody CreateFileForm createFileForm, @RequestHeader("Authorization") String authorizationHeader) {

        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String creatorEmail = jwtTokenUtil.extractUserEmail(token);

            if (createFileForm.getFileName() == null || createFileForm.getFileName().trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("File name must not be empty");
            }

            fileContentService.createFile(createFileForm);

            Map<String, Object> response = new HashMap<>();
            response.put("fileName", createFileForm.getFileName());
            response.put("fileDescription", createFileForm.getFileDescription());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {
            log.error("An error occurred: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }


}
