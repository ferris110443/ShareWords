package org.yplin.project.service;

import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.dto.UserWorkspaceDto;
import org.yplin.project.data.dto.WorkspaceMemberDto;
import org.yplin.project.data.form.*;
import org.yplin.project.model.FriendsModel;
import org.yplin.project.model.UserModel;
import org.yplin.project.model.UserOwnWorkspaceDetailsModel;

import java.util.List;

public interface UserService {


    SignInDto signup(SignupForm signupForm) throws UserExistException;

    SignInDto signIn(SignInForm signInForm) throws UserExistException, UserPasswordMismatchException;

    String updateUserWorkspace(UserWorkspaceDto userWorkspaceDto);

    List<UserOwnWorkspaceDetailsModel> fetchUserOwnWorkspaceDetails(String userEmail);


    List<UserModel> getSpecificUserInformation(String query);

    void addFriend(UserAddFriendForm userAddFriendForm);

    Long getUserIdByEmail(String userEmail);

    List<FriendsModel> getFriendsRelationStatus(long userEmail);

    List<WorkspaceMemberDto> fetchUserOwnWorkspaceMembers(String workspaceName);

    void addMemberToWorkspace(UserAddMemberInWorkspaceForm userAddMemberInWorkspaceForm);

    void acceptFriendRequest(FriendRequestForm friendRequestForm, String userEmail);

    void rejectFriendRequest(FriendRequestForm friendRequestForm, String userEmail);

    void removeFriendRequest(FriendRequestForm friendRequestForm, String userEmail);

    void removeMemberFromWorkspace(UserAddMemberInWorkspaceForm userAddMemberInWorkspaceForm);


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
