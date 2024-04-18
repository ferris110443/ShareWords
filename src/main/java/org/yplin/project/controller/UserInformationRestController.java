package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/1.0/user")
public class UserInformationRestController {

    @GetMapping("/userInformation")
    public ResponseEntity<?> getUserInformation() {

        return null;
    }

}
