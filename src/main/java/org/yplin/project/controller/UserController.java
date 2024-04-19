package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.form.SignInForm;
import org.yplin.project.data.form.SignupForm;
import org.yplin.project.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/1.0/user")
public class UserController {
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    UserService userService;


    @PostMapping(value = "/signup")
    @ResponseBody
    public ResponseEntity<?> signUp(@RequestBody SignupForm signupForm) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("signupForm: " + signupForm.toString());
            SignInDto dto = userService.signup(signupForm);
            response.put("data", dto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping(value = "/signIn", consumes = {"application/json"})
    @ResponseBody
    public ResponseEntity<?> signIn(@RequestBody SignInForm signInForm) {
        Map<String, Object> response = new HashMap<>();
        try {
            SignInDto dto = userService.signIn(signInForm);
            response.put("data", dto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}
