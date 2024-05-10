package org.yplin.project.service;


public interface ValidationService {
    boolean checkWorkspaceValidation(long roomNumber, String userEmail);

    boolean checkUserValidation(String userEmail);
}
