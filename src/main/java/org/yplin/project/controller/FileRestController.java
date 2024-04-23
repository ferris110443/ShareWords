package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.service.FileContentService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("api/1.0/upload")
public class FileRestController {
    public static final Logger logger = LoggerFactory.getLogger(FileRestController.class);


    @Autowired
    FileContentService fileContentService;

    @PostMapping(path = "/Image", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(@RequestBody ImageDataForm imageDataForm) {
        String imageURL = fileContentService.saveImageContent(imageDataForm);
        Map<String, String> response = new HashMap<>();
        response.put("imageURL", imageURL);
        return ResponseEntity.ok(response);
    }


//    @PostMapping("/File")
//    public void uploadFile() {
//
//    }

//    public void downloadFile() {
//
//    }
//
//    public void deleteFile() {
//
//    }


}
