package org.yplin.project.service.impl;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.data.form.UpdateWorkspaceForm;
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
    public long createWorkspace(CreateWorkspaceForm createWorkspaceForm, String creatorEmail) {
        WorkspaceModel workspaceModel = new WorkspaceModel();
        workspaceModel.setWorkspaceName(createWorkspaceForm.getWorkspaceName());
        workspaceModel.setWorkspaceDescription(createWorkspaceForm.getWorkspaceDescription());
        workspaceModel.setWorkspaceOwner(creatorEmail);
        workspaceModel.setWorkspaceCreatedAt(new Timestamp(System.currentTimeMillis()));
        workspaceRepository.save(workspaceModel);
        return workspaceModel.getId();
    }

    @Override
    public void deleteWorkspace(long roomNumber, String userEmail) throws IllegalArgumentException, NotWorkspaceOwnerException {
        boolean isOwner = workspaceRepository.isUserOwnerOfWorkspace(roomNumber, userEmail);
        if (isOwner) {
            workspaceRepository.deleteByWorkspaceNameAndWorkspaceOwner(roomNumber, userEmail);
        } else {
            throw new NotWorkspaceOwnerException("You are not the owner of this workspace");
        }
    }

    @Override
    public WorkspaceModel getWorkspaceInformation(long roomNumber) {
        try {
            return workspaceRepository.findById(roomNumber).orElseThrow(() -> new ServiceException("Workspace not found"));
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve workspace information for " + roomNumber, e);
        }
    }

    @Override
    public void updateWorkspaceInformation(UpdateWorkspaceForm updateWorkspaceForm) {
        if (updateWorkspaceForm.getOldWorkspaceName() == null || updateWorkspaceForm.getOldWorkspaceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Old workspace name must not be empty");
        }
        if (updateWorkspaceForm.getNewWorkspaceName() == null || updateWorkspaceForm.getNewWorkspaceName().trim().isEmpty()) {
            throw new IllegalArgumentException("New workspace name must not be empty");
        }

        WorkspaceModel workspaceModel = workspaceRepository.findById(updateWorkspaceForm.getOldWorkspaceId()).orElse(null);
        if (workspaceModel == null) {
            throw new IllegalArgumentException("Workspace does not exist");
        }
        workspaceModel.setWorkspaceName(updateWorkspaceForm.getNewWorkspaceName());
        workspaceModel.setWorkspaceDescription(updateWorkspaceForm.getNewWorkspaceNameDescription());
        workspaceRepository.save(workspaceModel);
    }
}
