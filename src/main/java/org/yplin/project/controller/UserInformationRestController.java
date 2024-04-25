package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.dto.UserWorkspaceDto;
import org.yplin.project.data.form.UserAddFriendForm;
import org.yplin.project.model.FriendsModel;
import org.yplin.project.model.UserModel;
import org.yplin.project.service.UserService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/1.0/user")
public class UserInformationRestController {

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping("/userInformation")
    public ResponseEntity<?> getUserInformation(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestParam(name = "query", required = false) String query) {

        List<UserModel> userInformationList;
        userInformationList = userService.getSpecificUserInformation(query);
        List<Map<String, Object>> responseList = new ArrayList<>();
        userInformationList.forEach(userModel -> {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", userModel.getName());
            userMap.put("email", userModel.getEmail());
            userMap.put("lastOnlineTime", userModel.getLastOnlineDate().toString());
            userMap.put("id", userModel.getId());
            responseList.add(userMap);
        });
        return ResponseEntity.ok().body(responseList);
    }

    // update user workspace in user_workspace table when user create workspace
    @PostMapping("/userWorkspace")
    public ResponseEntity<?> updateUserWorkspace(@RequestBody UserWorkspaceDto userWorkspaceDto) {

        userService.updateUserWorkspace(userWorkspaceDto);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Update User Workspace Success while creating workspace");
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/friends")
    public ResponseEntity<?> addFriend(@RequestBody UserAddFriendForm userAddFriendForm, @RequestHeader("Authorization") String authorizationHeader) {
        Map<String, String> response = new HashMap<>();
        String token = authorizationHeader.replace("Bearer ", "");
        String userEmail = jwtTokenUtil.extractUserEmail(token);
        userAddFriendForm.setUserId(userService.getUserIdByEmail(userEmail));
        userAddFriendForm.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        userAddFriendForm.setStatus("pending");

        try {
            userService.addFriend(userAddFriendForm);
            response.put("message", "Friend added successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    // get friends relation status
    @GetMapping("/friendsRelationShip")
    public ResponseEntity<?> getFriendsRelationStatus(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userEmail = jwtTokenUtil.extractUserEmail(token);
        long userId = userService.getUserIdByEmail(userEmail);
        List<FriendsModel> friendsRelationStatusList = userService.getFriendsRelationStatus(userId);

        Map<String, Object> response = new HashMap<>();

        response.put("data", friendsRelationStatusList);
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }
}
