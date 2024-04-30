package org.yplin.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.repository.user.UserWorkspaceRepository;
import org.yplin.project.service.UserService;
import org.yplin.project.service.WorkspaceService;

@Service
public class ValidationServiceImp implements org.yplin.project.service.ValidationService {

    @Autowired
    UserService userService;
    @Autowired
    WorkspaceService workspaceService;
    @Autowired
    UserWorkspaceRepository userWorkspaceRepository;

    @Override
    public boolean checkWorkspaceValidation(String workspaceName, String userEmail) {
        long userId = userService.getUserIdByEmail(userEmail);
        long workspaceId = workspaceService.getWorkspaceInformation(workspaceName).getId();
        return userWorkspaceRepository.checkWorkspaceValidation(workspaceId, userId);
    }


}
