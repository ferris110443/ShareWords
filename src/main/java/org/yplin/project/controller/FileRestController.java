package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yplin.project.data.form.ImageDataForm;

@Slf4j
@RestController
@RequestMapping("api/1.0/upload")
public class FileRestController {

    @PostMapping(path = "/Image", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadImage(@RequestBody ImageDataForm imageDataForm) {
        System.out.println("Received image data");
        try {
            // Assuming imageDataForm.getImage() returns a Base64 encoded String
            String imageData = imageDataForm.getImage();
            System.out.println("Image data: " + imageData);
            if (imageData != null) {
                // Decode and save image logic here
                return ResponseEntity.ok("Image uploaded successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid image data");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error uploading image: " + e.getMessage());
        }
    }


    @PostMapping("/File")
    public void uploadFile() {

    }

    public void downloadFile() {

    }

    public void deleteFile() {

    }




}
