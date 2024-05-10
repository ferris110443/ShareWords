package org.yplin.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.model.UserModel;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.repository.user.UserWorkspaceRepository;
import org.yplin.project.service.UserService;
import org.yplin.project.service.WorkspaceService;

import java.util.Optional;

@Service
public class ValidationServiceImp implements org.yplin.project.service.ValidationService {

    @Autowired
    UserService userService;
    @Autowired
    WorkspaceService workspaceService;
    @Autowired
    UserWorkspaceRepository userWorkspaceRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public boolean checkWorkspaceValidation(long roomNumber, String userEmail) {
        long userId = userService.getUserIdByEmail(userEmail);
        long workspaceId = workspaceService.getWorkspaceInformation(roomNumber).getId();
        return userWorkspaceRepository.checkWorkspaceValidation(workspaceId, userId);
    }

    @Override
    public boolean checkUserValidation(String userEmail) {
        Optional<UserModel> userOptional = userRepository.findByEmail(userEmail);
        return userOptional.isPresent();
    }


}
