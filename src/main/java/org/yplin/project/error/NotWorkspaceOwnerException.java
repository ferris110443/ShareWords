package org.yplin.project.error;

public class NotWorkspaceOwnerException extends Exception {
    public NotWorkspaceOwnerException(String message) {
        super(message);
    }
}