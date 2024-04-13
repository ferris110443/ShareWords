package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/1.0/upload")
public class FileRestController {

    public static final Logger logger = LoggerFactory.getLogger(FileRestController.class);
    @PostMapping("/uploadImage")
    public void uploadImage() {

    }


    @PostMapping("/uploadFile")
    public void uploadFile() {

    }

    public void downloadFile() {

    }

    public void deleteFile() {

    }




}
