package org.yplin.project.error;

public class NotWorkspaceOwnerException extends RuntimeException {
    public NotWorkspaceOwnerException(String message) {
        super(message);
    }
}