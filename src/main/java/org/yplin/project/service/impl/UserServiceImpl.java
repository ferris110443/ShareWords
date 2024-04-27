package org.yplin.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.dto.UserWorkspaceDto;
import org.yplin.project.data.dto.WorkspaceMemberDto;
import org.yplin.project.data.form.*;
import org.yplin.project.error.UserAlreadyMemberException;
import org.yplin.project.model.*;
import org.yplin.project.repository.FriendsRepository;
import org.yplin.project.repository.WorkspaceRepository;
import org.yplin.project.repository.user.UserOwnWorkspaceDetailsRepository;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.repository.user.UserWorkspaceRepository;
import org.yplin.project.service.UserService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WorkspaceRepository workspaceRepository;

    @Autowired
    UserWorkspaceRepository userWorkspaceRepository;
    @Autowired
    UserOwnWorkspaceDetailsRepository userOwnWorkspaceDetailsRepository;
    @Autowired
    FriendsRepository friendsRepository;
    @Autowired
    org.yplin.project.repository.WorkspaceMemberRepository workspaceMemberRepository;


    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private @Value("${jwt.signKey}") String jwtSignKey;


    @Override
    public SignInDto signup(SignupForm signupForm) throws UserExistException {
        if (userRepository.findUserByEmail(signupForm.getEmail()) != null) {
            throw new UserExistException(signupForm.getEmail() + " is already exist");
        }

        String token = jwtTokenUtil.generateToken(signupForm.getEmail());
        UserModel user = new UserModel();
        user.setName(signupForm.getName());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setAccountCreatedDate(new Timestamp(System.currentTimeMillis()));
        user.setLastOnlineDate(new Timestamp(System.currentTimeMillis()));
        user.setAccessToken(token);
        user.setAccessExpired(jwtTokenUtil.getExpirationDateFromToken(token).getTime());

        userRepository.save(user);

        return SignInDto.from(user);
    }

    @Override
    public SignInDto signIn(SignInForm signInForm) throws UserPasswordMismatchException {

        UserModel userDB = userRepository.findUserByEmail(signInForm.getEmail());
        if (userDB == null) {
            throw new UsernameNotFoundException("User not found with email: " + signInForm.getEmail());
        }

        if (!passwordEncoder.matches(signInForm.getPassword(), userDB.getPassword())) {
            throw new UserPasswordMismatchException("Wrong Password");
        }

        String token = jwtTokenUtil.generateToken(signInForm.getEmail());

        userDB.setLastOnlineDate(new Timestamp(System.currentTimeMillis()));
        userDB.setAccessToken(token);
        userDB.setAccessExpired(jwtTokenUtil.getExpirationDateFromToken(token).getTime());

        userRepository.save(userDB);

        return SignInDto.from(userDB);
    }


    // update user workspace in user_workspace table when user create workspace
    // transfer the workspace name to workspace id
    @Override
    public String updateUserWorkspace(UserWorkspaceDto userWorkspaceDto) {
        String userToken = userWorkspaceDto.getAccessToken();
        String workspaceName = userWorkspaceDto.getWorkspaceName();
        String userEmailFromToken = jwtTokenUtil.extractUserEmail(userToken);

        long userId = userRepository.findIdByEmail(userEmailFromToken).getId();
        long workspaceId = workspaceRepository.findIdByWorkspaceName(workspaceName);


        UserWorkspaceModel userWorkspaceModel = new UserWorkspaceModel();
        userWorkspaceModel.setWorkspaceId(workspaceId);
        userWorkspaceModel.setUserId(userId);

        userWorkspaceRepository.save(userWorkspaceModel);


        return null;
    }

    @Override
    public List<UserOwnWorkspaceDetailsModel> fetchUserOwnWorkspaceDetails(String userEmail) {
        System.out.println(userOwnWorkspaceDetailsRepository.fetchWorkspaceDetailsWithNativeQuery(userEmail));
        return userOwnWorkspaceDetailsRepository.fetchWorkspaceDetailsWithNativeQuery(userEmail);
    }

    @Override
    public List<UserModel> getSpecificUserInformation(String query) {
        return userRepository.findByNameContaining(query);
    }


    @Override
    public void addFriend(UserAddFriendForm userAddFriendForm) {
        Long userId = userAddFriendForm.getUserId();
        Long friendId = userAddFriendForm.getFriendId();

        // Check for existing friendship in both directions
        boolean exists = friendsRepository.existsFriendship(userId, friendId)
                || friendsRepository.existsFriendship(friendId, userId);

        if (!exists) {
            FriendsModel friendsModel = new FriendsModel();
            friendsModel.setUserId(userId);
            friendsModel.setFriendId(friendId);
            friendsModel.setStatus(StatusEnum.valueOf(userAddFriendForm.getStatus()));
            friendsModel.setCreatedAt(userAddFriendForm.getCreatedAt());
            friendsRepository.save(friendsModel);
        } else {
            // Handle the case where the friendship already exists
            throw new RuntimeException("Friend Request already exists.");
        }
    }

    @Override
    public Long getUserIdByEmail(String userEmail) {
        return userRepository.findIdByEmail(userEmail).getId();
    }


    @Override
    public List<FriendsModel> getFriendsRelationStatus(long userId) {
        List<FriendsModel> friendsModelList = friendsRepository.fetchByUserId(userId);
        friendsModelList.forEach(friendsModel -> {
            UserEmailNameProjection friendEmailName = userRepository.findEmailById(friendsModel.getFriendId());
            UserEmailNameProjection userEmailName = userRepository.findEmailById(friendsModel.getUserId());
            friendsModel.setFriendEmail(friendEmailName.getEmail());
            friendsModel.setFriendName(friendEmailName.getName());
            friendsModel.setUserEmail(userEmailName.getEmail());
            friendsModel.setUserName(userEmailName.getName());
        });
        System.out.println(friendsModelList);


        return friendsModelList;
    }

    @Override
    public List<WorkspaceMemberDto> fetchUserOwnWorkspaceMembers(String workspaceName) {
        return workspaceMemberRepository.findWorkspaceMembersByWorkspaceName(workspaceName);
    }

    @Override
    public void addMemberToWorkspace(UserAddRemoveMemberInWorkspaceForm userAddMemberInWorkspaceForm) {
        long userId = userAddMemberInWorkspaceForm.getUserId();
        String workspaceName = userAddMemberInWorkspaceForm.getWorkspaceName();
        long workspaceId = workspaceRepository.findIdByWorkspaceName(workspaceName);

        Optional<UserWorkspaceModel> existingEntry = userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId);
        if (existingEntry.isPresent()) {
            throw new UserAlreadyMemberException("User is already a member of the workspace.");
        } else {
            UserWorkspaceModel userWorkspaceModel = new UserWorkspaceModel();
            userWorkspaceModel.setUserId(userId);
            userWorkspaceModel.setWorkspaceId(workspaceId);
            userWorkspaceRepository.save(userWorkspaceModel);
        }
    }


    @Override
    public void removeMemberFromWorkspace(UserAddRemoveMemberInWorkspaceForm userAddMemberInWorkspaceForm) {
        long userId = userAddMemberInWorkspaceForm.getUserId();
        String workspaceName = userAddMemberInWorkspaceForm.getWorkspaceName();
        long workspaceId = workspaceRepository.findIdByWorkspaceName(workspaceName);
        Optional<UserWorkspaceModel> existingEntry = userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId);
        if (existingEntry.isEmpty()) {
            throw new UserAlreadyMemberException("User is not a member of the workspace.");
        } else {
            userWorkspaceRepository.delete(existingEntry.get());
        }
    }


    @Override
    public void acceptFriendRequest(FriendRequestForm friendRequestForm, String userEmail) {
        Long userId = userRepository.findIdByEmail(userEmail).getId();
        Long friendId = friendRequestForm.getFriendId();

        // Retrieve the existing friendship model from the database
        FriendsModel existingFriendship = friendsRepository.findByUserIdAndFriendId(userId, friendId);
        FriendsModel existingFriendship2 = friendsRepository.findByUserIdAndFriendId(friendId, userId);

        // Check if any friendship exists and is in a 'pending' state
        if (existingFriendship != null && existingFriendship.getStatus() == StatusEnum.pending) {
            existingFriendship.setStatus(StatusEnum.accepted);
            existingFriendship.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Save the updated model to the database
            friendsRepository.save(existingFriendship);
        } else if (existingFriendship2 != null && existingFriendship2.getStatus() == StatusEnum.pending) {
            existingFriendship2.setStatus(StatusEnum.accepted);
            existingFriendship2.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Save the updated model to the database
            friendsRepository.save(existingFriendship2);
        } else {
            // Handle the case where no pending friendship request exists
            throw new IllegalStateException("No pending friend request found or request already accepted");
        }
    }

    @Override
    public void rejectFriendRequest(FriendRequestForm friendRequestForm, String userEmail) {
        Long userId = userRepository.findIdByEmail(userEmail).getId();
        Long friendId = friendRequestForm.getFriendId();

        FriendsModel existingFriendship = friendsRepository.findByUserIdAndFriendId(userId, friendId);
        FriendsModel existingFriendship2 = friendsRepository.findByUserIdAndFriendId(friendId, userId);

        if (existingFriendship != null) {
            existingFriendship.setStatus(StatusEnum.declined);
            existingFriendship.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            friendsRepository.save(existingFriendship);
        } else if (existingFriendship2 != null) {
            existingFriendship2.setStatus(StatusEnum.declined);
            existingFriendship2.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            friendsRepository.save(existingFriendship2);
        } else {
            throw new IllegalStateException("No existing friend request found to reject");
        }
    }

    @Override
    public void removeFriendRequest(FriendRequestForm friendRequestForm, String userEmail) {
        Long userId = userRepository.findIdByEmail(userEmail).getId();
        Long friendId = friendRequestForm.getFriendId();

        FriendsModel existingFriendship = friendsRepository.findByUserIdAndFriendId(userId, friendId);
        FriendsModel existingFriendship2 = friendsRepository.findByUserIdAndFriendId(friendId, userId);
        if (existingFriendship != null) {
            existingFriendship.setStatus(StatusEnum.pending);
            existingFriendship.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            friendsRepository.delete(existingFriendship);
        } else if (existingFriendship2 != null) {
            existingFriendship2.setStatus(StatusEnum.pending);
            existingFriendship2.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            friendsRepository.delete(existingFriendship2);
        } else {
            throw new IllegalStateException("No existing friend request found to remove");
        }
    }


}
