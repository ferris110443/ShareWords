package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.form.WorkspaceCheckValidationForm;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/1.0/validation")
public class ValidationRestController {
    private static final Logger logger = LoggerFactory.getLogger(ValidationRestController.class);

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping(path = "/workspace")
    public ResponseEntity<?> workspaceCheckValidation(@RequestBody WorkspaceCheckValidationForm workspaceCheckValidationForm,
                                                      @RequestHeader("Authorization") String authorizationHeader) {

        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String creatorEmail = jwtTokenUtil.extractUserEmail(token);
            System.out.println("creatorEmail : " + creatorEmail);
            Map<String, Object> response = new HashMap<>();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error checking workspace validation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking workspace validation: " + e.getMessage());
        }

//        return null;
    }
}
