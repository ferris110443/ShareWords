package org.yplin.project.controller;


import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.service.UserService;
import org.yplin.project.service.ValidationService;
import org.yplin.project.service.WorkspaceService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = {"https://localhost:3000", "https://localhost:8888", "https://34.230.138.53", "https://sharewords.org"})
@RequestMapping("api/1.0/validation")
public class ValidationRestController {
    private static final Logger logger = LoggerFactory.getLogger(ValidationRestController.class);
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    UserService userService;
    @Autowired
    WorkspaceService workspaceService;
    @Autowired
    ValidationService validationService;

    @CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8888", "http://34.230.138.53"})
    @GetMapping(path = "/workspace")
    public ResponseEntity<?> checkWorkspaceValidation(@RequestParam("workspaceName") String workspaceName,
                                                      @RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtTokenUtil.extractUserEmail(token);
            boolean isUserMember = validationService.checkWorkspaceValidation(workspaceName, userEmail);

            if (!isUserMember) {
                response.put("error", "You are not a member of this workspace");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (JwtException error) {
            logger.error("Error checking workspace validation due to token error : ", error);
            response.put("error", "Invalid token: " + error.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            logger.error("Error checking workspace validation fail", e);
            response.put("error", "Error checking workspace validation fail : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(path = "/user")
    public ResponseEntity<?> checkUserValidation(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtTokenUtil.extractUserEmail(token);
            boolean isUserMember = validationService.checkUserValidation(userEmail);
            if (!isUserMember) {
                response.put("error", "You are not a member. Please sign up first.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (JwtException error) {
            logger.error("Error checking workspace validation due to token error : ", error);
            response.put("error", "Invalid token: " + error.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            logger.error("Error checking workspace validation fail", e);
            response.put("error", "Error checking workspace validation fail : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
