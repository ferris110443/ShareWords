package org.yplin.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.model.WorkspaceModel;
import org.yplin.project.repository.WorkspaceRepository;
import org.yplin.project.service.WorkspaceService;

import java.sql.Timestamp;


@Service
public class WorkspaceServiceImp implements WorkspaceService {

    @Autowired
    WorkspaceRepository workspaceRepository;

    @Override
    public void createWorkspace(CreateWorkspaceForm createWorkspaceForm) {
        WorkspaceModel workspaceModel = new WorkspaceModel();
        workspaceModel.setWorkspaceName(createWorkspaceForm.getWorkspaceName());
        workspaceModel.setWorkspaceDescription(createWorkspaceForm.getWorkspaceDescription());
        workspaceModel.setWorkspaceOwner("Test@gmail.com");
        workspaceModel.setWorkspaceCreatedAt(new Timestamp(System.currentTimeMillis()));
        workspaceRepository.save(workspaceModel);
    }
}
