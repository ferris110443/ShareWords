package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.service.FileContentService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8888", "http://34.230.138.53"})
@RequestMapping("api/1.0/upload")
public class FileRestController {
    public static final Logger logger = LoggerFactory.getLogger(FileRestController.class);
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    FileContentService fileContentService;
    @Autowired
    UserRepository userRepository;

    @PostMapping(path = "/Image", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(@RequestBody ImageDataForm imageDataForm) {
        String imageURL = fileContentService.saveImageContent(imageDataForm);
        Map<String, String> response = new HashMap<>();
        response.put("imageURL", imageURL);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/userImage")
    public ResponseEntity<Map<String, String>> uploadUserImage(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        String userEmail = jwtTokenUtil.extractUserEmail(accessToken);
        long userId = userRepository.findIdByEmail(userEmail).getId();

        Map<String, String> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("message", "Failed to upload. File is empty.");
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload. File is empty."));
        }


        String imageURL = fileContentService.saveUserImage(file, userId);
        response.put("message", "File uploaded successfully: " + file.getOriginalFilename());
        return ResponseEntity.ok(response);
    }


//    public void downloadFile() {
//
//    }
//
//    public void deleteFile() {
//
//    }


}
