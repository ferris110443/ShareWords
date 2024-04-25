package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("api/1.0/markdown")
public class MarkdownRestController {
    public static final Logger logger = LoggerFactory.getLogger(MarkdownRestController.class);

    @Autowired
    FileContentService fileContentService;

    @Autowired
    MarkdownStorageService markdownStorageService;

    @PostMapping("/markdown")

    public ResponseEntity<?> updateMarkdownContent(@RequestBody MarkdownForm markdownForm) {
        markdownStorageService.addMarkdownForm(markdownForm);
        Map<String, MarkdownForm> response = new HashMap<>();
        response.put("data", markdownForm);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/markdown")
    public ResponseEntity<?> getMarkdownContent(@RequestParam("roomId") String roomId, @RequestParam("fileId") String fileId) {

        Map<String, FileContentModel> response = new HashMap<>();
        response.put("data", fileContentService.getFileContent(fileId));
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
