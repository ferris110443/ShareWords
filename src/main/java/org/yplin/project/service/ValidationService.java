package org.yplin.project.service;


public interface ValidationService {
    boolean checkWorkspaceValidation(String workspaceName, String userEmail);

    boolean checkUserValidation(String userEmail);
}
