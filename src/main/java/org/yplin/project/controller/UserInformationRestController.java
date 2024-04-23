package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.data.dto.UserWorkspaceDto;
import org.yplin.project.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/1.0/user")
public class UserInformationRestController {

    @Autowired
    UserService userService;

    @GetMapping("/userInformation")
    public ResponseEntity<?> getUserInformation() {

        return null;
    }


    @PostMapping("/userWorkspace")
    public ResponseEntity<?> updateUserWorkspace(@RequestBody UserWorkspaceDto userWorkspaceDto) {

        userService.updateUserWorkspace(userWorkspaceDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Update User Workspace Success");
        return ResponseEntity.ok().body(response);
    }

}
