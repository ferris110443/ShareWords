package org.yplin.project.service.impl;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.error.NotWorkspaceOwnerException;
import org.yplin.project.model.WorkspaceModel;
import org.yplin.project.repository.WorkspaceRepository;
import org.yplin.project.service.WorkspaceService;

import java.sql.Timestamp;


@Service
public class WorkspaceServiceImp implements WorkspaceService {
    public static final Logger logger = LoggerFactory.getLogger(WorkspaceServiceImp.class);

    @Autowired
    WorkspaceRepository workspaceRepository;

    @Override
    public void createWorkspace(CreateWorkspaceForm createWorkspaceForm, String creatorEmail) {
        WorkspaceModel workspaceModel = new WorkspaceModel();
        workspaceModel.setWorkspaceName(createWorkspaceForm.getWorkspaceName());
        workspaceModel.setWorkspaceDescription(createWorkspaceForm.getWorkspaceDescription());
        workspaceModel.setWorkspaceOwner(creatorEmail);
        workspaceModel.setWorkspaceCreatedAt(new Timestamp(System.currentTimeMillis()));
        workspaceRepository.save(workspaceModel);
    }

    @Override
    public void deleteWorkspace(String workspaceName, String userEmail) throws IllegalArgumentException, NotWorkspaceOwnerException {
        if (workspaceName == null || workspaceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name must not be empty");
        }
        boolean isOwner = workspaceRepository.isUserOwnerOfWorkspace(workspaceName, userEmail);
        if (isOwner) {
            workspaceRepository.deleteByWorkspaceNameAndWorkspaceOwner(workspaceName, userEmail);
        } else {
            throw new NotWorkspaceOwnerException("You are not the owner of this workspace");
        }
    }

    @Override
    public WorkspaceModel getWorkspaceInformation(String workspaceName) {
        if (workspaceName == null || workspaceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name must not be empty");
        }
        try {
            return workspaceRepository.findByWorkspaceName(workspaceName);
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve workspace information for " + workspaceName, e);
        }
    }
}
