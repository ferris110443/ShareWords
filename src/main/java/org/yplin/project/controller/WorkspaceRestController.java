package org.yplin.project.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.dto.WorkspaceMemberDto;
import org.yplin.project.data.form.CreateFileForm;
import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.data.form.UserAddMemberInWorkspaceForm;
import org.yplin.project.error.UserAlreadyMemberException;
import org.yplin.project.model.FileContentModel;
import org.yplin.project.model.UserOwnWorkspaceDetailsModel;
import org.yplin.project.repository.user.UserOwnWorkspaceDetailsRepository;
import org.yplin.project.service.FileContentService;
import org.yplin.project.service.UserService;
import org.yplin.project.service.WorkspaceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/1.0/workspace")
public class WorkspaceRestController {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceRestController.class);
    @Autowired
    WorkspaceService workspaceService;
    @Autowired
    FileContentService fileContentService;
    @Autowired
    UserOwnWorkspaceDetailsRepository userOwnWorkspaceDetailsRepository;
    @Autowired
    UserService userservice;
    @Autowired
    JwtTokenUtil jwtTokenUtil;


    // createWorkspace create a new workspace
    @PostMapping(path = "/workspace")
    public ResponseEntity<?> createWorkspace(@RequestBody CreateWorkspaceForm createWorkspaceForm, @RequestHeader("Authorization") String authorizationHeader) {

        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String creatorEmail = jwtTokenUtil.extractUserEmail(token);
            workspaceService.createWorkspace(createWorkspaceForm, creatorEmail);
            Map<String, Object> response = new HashMap<>();
            response.put("workspaceName", createWorkspaceForm.getWorkspaceName());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error creating workspace", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating workspace: " + e.getMessage());
        }

    }


    // getWorkspaceInformation including containing files in the workspace
    @GetMapping(path = "/workspace")
    public ResponseEntity<?> getWorkspaceInformation(@RequestParam(value = "workspaceName") String workspaceName, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String creatorEmail = jwtTokenUtil.extractUserEmail(token);

        List<FileContentModel> fileList = fileContentService.getFileContentsByWorkspaceName(workspaceName);

        Map<String, List<FileContentModel>> response = new HashMap<>();
        response.put("data", fileList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // getUserWorkspaceInformation return one user joins how many workspaces information
    @GetMapping(path = "/userWorkspaceDetails")
    public ResponseEntity<?> getUserWorkspaceInformation(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userEmail = jwtTokenUtil.extractUserEmail(token);
        List<UserOwnWorkspaceDetailsModel> userOwnWorkspaceDetailsModelList = userservice.fetchUserOwnWorkspaceDetails(userEmail);

        Map<String, List<UserOwnWorkspaceDetailsModel>> response = new HashMap<>();
        response.put("data", userOwnWorkspaceDetailsModelList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // createNewFile create a new file in the workspace
    @PostMapping(path = "/file")
    public ResponseEntity<?> createNewFile(@RequestBody CreateFileForm createFileForm, @RequestHeader("Authorization") String authorizationHeader) {

        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String creatorEmail = jwtTokenUtil.extractUserEmail(token);

            if (createFileForm.getFileName() == null || createFileForm.getFileName().trim().isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("File name must not be empty");
            }

            fileContentService.createFile(createFileForm);

            Map<String, Object> response = new HashMap<>();
            response.put("fileName", createFileForm.getFileName());
            response.put("fileDescription", createFileForm.getFileDescription());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {
            log.error("An error occurred: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    // getWorkspaceMembers return all members in the workspace
    @GetMapping(path = "/workspaceMembers")
    public ResponseEntity<?> getWorkspaceMembers(@RequestParam(value = "workspaceName") String workspaceName, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userEmail = jwtTokenUtil.extractUserEmail(token);

        List<WorkspaceMemberDto> workspaceMembersList = userservice.fetchUserOwnWorkspaceMembers(workspaceName);

        Map<String, Object> response = new HashMap<>();
        response.put("data", workspaceMembersList);
        response.put("workspaceName", workspaceName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // update user workspace in user_workspace table when user add member into workspace
    @PostMapping(path = "/workspaceMembers")
    public ResponseEntity<?> addFriendToWorkspaceMembers(@RequestBody UserAddMemberInWorkspaceForm userAddMemberInWorkspaceForm, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String creatorEmail = jwtTokenUtil.extractUserEmail(token);

        try {
            Map<String, List<UserOwnWorkspaceDetailsModel>> response = new HashMap<>();
            userservice.addMemberToWorkspace(userAddMemberInWorkspaceForm);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UserAlreadyMemberException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User already in the workspace");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Internal error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
