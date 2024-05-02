package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.model.FileContentModel;
import org.yplin.project.service.FileContentService;
import org.yplin.project.service.WorkspaceFileContentProjection;
import org.yplin.project.service.impl.MarkdownStorageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = {"https://localhost:3000", "https://localhost:8888", "https://34.230.138.53"})
@RequestMapping("api/1.0/markdown")
public class MarkdownRestController {
    public static final Logger logger = LoggerFactory.getLogger(MarkdownRestController.class);

    @Autowired
    FileContentService fileContentService;

    @Autowired
    MarkdownStorageService markdownStorageService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;


    @PostMapping("/markdown")
    // update markdown content batch insert
    public ResponseEntity<?> updateMarkdownContent(@RequestBody MarkdownForm markdownForm) {
        markdownStorageService.addMarkdownForm(markdownForm);
        Map<String, MarkdownForm> response = new HashMap<>();
        response.put("data", markdownForm);
        return ResponseEntity.ok().body(response);
    }


    // Get markdown content by fileId
    @GetMapping("/markdown")
    public ResponseEntity<?> getMarkdownContent(@RequestParam("roomId") String roomId, @RequestParam("fileId") String fileId) {
        Map<String, FileContentModel> response = new HashMap<>();
        response.put("data", fileContentService.getFileContent(fileId));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/markdownInfo")
    public ResponseEntity<?> getMarkdownInfo(@RequestParam("fileId") String fileId, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userEmail = jwtTokenUtil.extractUserEmail(token);

        Map<String, Object> response = new HashMap<>();
        response.put("data", fileContentService.getFileContent(fileId));
        response.put("userEmail", userEmail);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/workspaceFiles")
    public ResponseEntity<?> getWorkspaceFiles(@RequestParam("roomId") String roomId) {

        fileContentService.getWorkspaceFilesContent(roomId);
        Map<String, List<WorkspaceFileContentProjection>> response = new HashMap<>();
        response.put("data", fileContentService.getWorkspaceFilesContent(roomId));
        return ResponseEntity.ok().body(response);
    }


}
