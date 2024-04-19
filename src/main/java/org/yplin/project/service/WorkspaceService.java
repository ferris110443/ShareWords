package org.yplin.project.service;

import org.yplin.project.data.form.CreateWorkspaceForm;

public interface WorkspaceService {

    void createWorkspace(CreateWorkspaceForm createWorkspaceForm, String creatorEmail);
}
