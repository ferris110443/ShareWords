package org.yplin.project.error;

public class UserAlreadyMemberException extends Exception {
    public UserAlreadyMemberException(String message) {
        super(message);
    }
}