package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.data.form.MailServiceForm;
import org.yplin.project.service.MarkdownMailService;

import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8888", "https://34.230.138.53", "https://sharewords.org"})
@RequestMapping("api/1.0/mailService")
public class MGRestController {

    @Autowired
    MarkdownMailService markdownMailService;

    @PostMapping("/markdown")
    public ResponseEntity<?> sendMarkdownFiles(@RequestBody MailServiceForm mailServiceForm) {
        markdownMailService.sendMarkdownFiles(mailServiceForm);


        return ResponseEntity.ok().body(Map.of("message", "Mail sent successfully"));
    }
}
