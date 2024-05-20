package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.error.UserNotFoundException;
import org.yplin.project.model.UserModel;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.service.FileContentService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8888", "https://34.230.138.53", "https://sharewords.org"})
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
        try {
            if (imageDataForm.getImage() == null || imageDataForm.getImage().isEmpty()) {
                throw new IllegalArgumentException("Image data is empty.");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Image data is empty error: ", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        String imageURL = fileContentService.saveImageContent(imageDataForm);
        Map<String, String> response = new HashMap<>();
        response.put("imageURL", imageURL);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/userImage")
    public ResponseEntity<Map<String, String>> uploadUserImage(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        Map<String, String> response = new HashMap<>();
        try {
            String accessToken = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtTokenUtil.extractUserEmail(accessToken);
            UserModel userModel = userRepository.findIdByEmail(userEmail);
            if (userModel == null) {
                throw new UserNotFoundException("User not found for email: " + userEmail);
            }

            if (file.isEmpty()) {
                response.put("error", "Failed to upload. File is empty.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            fileContentService.saveUserImage(file, userModel.getId());
        } catch (UserNotFoundException e) {
            logger.error("User not found error: ", e);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IOException e) {
            logger.error("File save error: ", e);
            response.put("error", "Failed to save file due to an error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("General error: ", e);
            response.put("error", "An unexpected error occurred.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", "File uploaded successfully: " + file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }


}
