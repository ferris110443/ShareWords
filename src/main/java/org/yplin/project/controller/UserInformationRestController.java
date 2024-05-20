package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.ActionEnum;
import org.yplin.project.data.dto.UserWorkspaceDto;
import org.yplin.project.data.form.FriendRequestForm;
import org.yplin.project.data.form.UserAddFriendForm;
import org.yplin.project.error.UserNotFoundException;
import org.yplin.project.model.FriendsModel;
import org.yplin.project.model.UserModel;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.service.UserService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8888", "https://34.230.138.53", "https://sharewords.org"})
@RequestMapping("api/1.0/user")
public class UserInformationRestController {

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;


    @GetMapping("/userPersonalInformation")
    public ResponseEntity<?> queryUserPrivateInformation(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userEmail = jwtTokenUtil.extractUserEmail(token);
        UserModel userModel = userService.getUserPrivateInformation(userEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("name", userModel.getName());
        response.put("email", userModel.getEmail());
        response.put("picture", userModel.getUserImageUrl());
        response.put("AccountCreatedDate", userModel.getAccountCreatedDate().toString());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/userInformation")
    public ResponseEntity<?> queryUserInformation(@RequestHeader("Authorization") String authorizationHeader,
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
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    // update user workspace in user_workspace table when user create workspace
    @PostMapping("/userWorkspace")
    public ResponseEntity<?> updateUserWorkspace(@RequestBody UserWorkspaceDto userWorkspaceDto) {
        try {
            userService.updateUserWorkspace(userWorkspaceDto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Update User Workspace Success while creating workspace");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/friends")
    public ResponseEntity<?> addFriend(@RequestBody UserAddFriendForm userAddFriendForm, @RequestHeader("Authorization") String authorizationHeader) {
        Map<String, String> response = new HashMap<>();

        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtTokenUtil.extractUserEmail(token);
            userAddFriendForm.setUserId(userService.getUserIdByEmail(userEmail));
            userAddFriendForm.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            userAddFriendForm.setStatus("pending");
            userService.addFriend(userAddFriendForm);
            response.put("message", "Friend added successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UserNotFoundException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    // get friends relation status
    @GetMapping("/friendsRelationShip")
    public ResponseEntity<?> getFriendsRelationStatus(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userEmail = jwtTokenUtil.extractUserEmail(token);
        Map<String, Object> response = new HashMap<>();

        try {
            long userId = userService.getUserIdByEmail(userEmail);
            List<FriendsModel> friendsRelationStatusList = userService.getFriendsRelationStatus(userId);
            response.put("data", friendsRelationStatusList);
            response.put("userId", userId);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "FileNotFoundException" + e));

        }


    }

    @PostMapping("/handleFriendRequest")
    public ResponseEntity<?> handleFriendRequest(@RequestBody FriendRequestForm friendRequestForm, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtTokenUtil.extractUserEmail(token);

            ActionEnum action = friendRequestForm.getAction();
            switch (action) {
                case ACCEPT:
                    userService.acceptFriendRequest(friendRequestForm, userEmail);
                    break;
                case REJECT:
                    userService.rejectFriendRequest(friendRequestForm, userEmail);
                    break;
                case REMOVE:
                    userService.removeFriendRequest(friendRequestForm, userEmail);
                    break;
            }
            String requestUserEmail = userRepository.findEmailById(friendRequestForm.getFriendId()).getEmail();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Friend request handled successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }


    // accept friend request button
    @PostMapping("/acceptFriendRequest")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody FriendRequestForm friendRequestForm, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtTokenUtil.extractUserEmail(token);

            userService.acceptFriendRequest(friendRequestForm, userEmail);

            Map<String, Object> response = new HashMap<>();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/rejectFriendRequest")
    public ResponseEntity<?> rejectFriendRequest(@RequestBody FriendRequestForm friendRequestForm, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtTokenUtil.extractUserEmail(token);

            userService.rejectFriendRequest(friendRequestForm, userEmail);

            Map<String, Object> response = new HashMap<>();
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }

    }


    @PostMapping("/removeFriendRequest")
    public ResponseEntity<?> removeFriendRequest(@RequestBody FriendRequestForm friendRequestForm, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = jwtTokenUtil.extractUserEmail(token);
            userService.removeFriendRequest(friendRequestForm, userEmail);
            Map<String, Object> response = new HashMap<>();
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }

    }


}
