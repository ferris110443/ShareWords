package org.yplin.project.service;

import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.dto.UserWorkspaceDto;
import org.yplin.project.data.form.SignInForm;
import org.yplin.project.data.form.SignupForm;

public interface UserService {


    SignInDto signup(SignupForm signupForm) throws UserExistException;

    SignInDto signIn(SignInForm signInForm) throws UserExistException, UserPasswordMismatchException;

    String updateUserWorkspace(UserWorkspaceDto userWorkspaceDto);

    sealed class UserException extends
            Exception permits UserExistException, UserNotExistException, UserPasswordMismatchException {

        public UserException(String message) {
            super(message);
        }

    }

    final class UserExistException extends UserException {

        public UserExistException(String message) {
            super(message);
        }

    }

    final class UserNotExistException extends UserException {

        public UserNotExistException(String message) {
            super(message);
        }

    }

    final class UserPasswordMismatchException extends UserException {

        public UserPasswordMismatchException(String message) {
            super(message);
        }

    }


}
