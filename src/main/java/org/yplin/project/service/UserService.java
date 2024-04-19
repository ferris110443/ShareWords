package org.yplin.project.service;

import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.dto.UserDto;
import org.yplin.project.data.form.SignInForm;
import org.yplin.project.data.form.SignupForm;
import org.yplin.project.model.UserModel;

public interface UserService {

    UserDto getUserDtoByToken(String token);

    UserModel getUserByToken(String token);

    SignInDto signup(SignupForm signupForm) throws UserExistException;

    SignInDto signIn(SignInForm signInForm) throws UserExistException, UserPasswordMismatchException;

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
