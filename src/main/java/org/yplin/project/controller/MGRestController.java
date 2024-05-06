package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.data.form.MailServiceForm;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8888", "https://34.230.138.53", "https://sharewords.org"})
@RequestMapping("api/1.0/mailgun")
public class MGRestController {


    @PostMapping("/markdown")
    public ResponseEntity<?> sendMarkdownFiles(@RequestBody MailServiceForm mailServiceForm) {
        

        return ResponseEntity.ok().body("test");
    }
}
