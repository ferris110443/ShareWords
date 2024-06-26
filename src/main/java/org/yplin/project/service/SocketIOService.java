package org.yplin.project.service;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.StatusEnum;
import org.yplin.project.data.dto.socketio.ChatMessageDto;
import org.yplin.project.data.dto.socketio.JoinRoomMessageDto;
import org.yplin.project.data.dto.socketio.MemberWorkspaceDto;
import org.yplin.project.data.dto.socketio.MessageTokenData;
import org.yplin.project.data.form.UserSession;
import org.yplin.project.model.FriendsModel;
import org.yplin.project.model.UserModel;
import org.yplin.project.repository.FriendsRepository;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.service.impl.WorkspaceServiceImp;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class SocketIOService {
    public static final Logger logger = LoggerFactory.getLogger(SocketIOService.class);
    private final SocketIOServer server;
    private final Map<String, UserSession> clients = new ConcurrentHashMap<>();

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private WorkspaceServiceImp workspaceServiceImp;

    @Autowired
    public SocketIOService(SocketIOServer server) {
        this.server = server;
        initializeEventListeners();
    }

    private void initializeEventListeners() {
        server.addConnectListener(client -> {
            log.info("Client connected: {}", client.getSessionId());
        });

        server.addDisconnectListener(client -> {
            UserSession userSession = clients.values().stream()
                    .filter(us -> us.getClient().getSessionId().equals(client.getSessionId()))
                    .findFirst()
                    .orElse(null);
            if (userSession != null) {
                log.info("client disconnected: {} with token {}", client.getSessionId(), userSession.getAccessToken());
                clients.remove(userSession.getEmail());
                userSession.setOnline(false);
                broadcastOnlineUsers();
            } else {
                log.info("client disconnected: {}", client.getSessionId());
            }

        });

        server.addEventListener("message", MessageTokenData.class, (client, data, ackRequest) -> {
            String userEmail = getEmailFromToken(data);
            if (userEmail != null) {
                clients.putIfAbsent(userEmail, new UserSession(client, true, data.getAccessToken(), userEmail));
                broadcastOnlineUsers(); // Update and broadcast online status when user sends a message
                ackRequest.sendAckData("Message received");
            }
        });


        server.addEventListener("leftRoom", JoinRoomMessageDto.class, (client, data, ackRequest) -> {
            log.info("Received data: {}", data);
            client.leaveRoom(data.getRoomNumber());
            String userEmail = getEmailFromToken(data);
            data.setUserEmail(userEmail);
            ackRequest.sendAckData(data);
            broadcastUserLeftMessageToRoom(data);
            logger.info("broadcastUserLeftMessageToRoom: {}", data);
        });

        server.addEventListener("joinRoom", JoinRoomMessageDto.class, (client, data, ackRequest) -> {
            log.info("Received data: {}", data);
            client.joinRoom(data.getRoomNumber());
            String userEmail = getEmailFromToken(data);
            data.setUserEmail(userEmail);
            ackRequest.sendAckData(data);
            broadcastUserJoinMessageToRoom(data);
            logger.info("broadcastUserJoinMessageToRoom: {}", data);
        });

        server.addEventListener("chatMessage", ChatMessageDto.class, (client, data, ackRequest) -> {
            String userEmail = getEmailFromToken(data);
            data.setUserEmail(userEmail);
            ackRequest.sendAckData("Message received from clients");
            broadcastMessageToRoom(data);

        });


        server.addEventListener("addFriendRequest", MessageTokenData.class, (client, data, ackRequest) -> {

            log.info("data: {}", data);
            String userEmail = getEmailFromToken(data);
            String userName = getNameFromToken(data);
            String friendEmail = data.getUserEmail();

            broadcastInvitationToUser(userEmail, userName, friendEmail);

        });

        server.addEventListener("confirmFriendRequest", MessageTokenData.class, (client, data, ackRequest) -> {

            log.info("data: {}", data);
            String userEmail = getEmailFromToken(data);
            String userName = getNameFromToken(data);
            String friendEmail = data.getRequestUserEmail();
            broadcastConfirmToUser(userEmail, userName, friendEmail);

        });


        server.addEventListener("acceptFriendRequestWS", MessageTokenData.class, (client, data, ackRequest) -> {
            String userEmail = getEmailFromToken(data);
            String requestUserEmail = data.getRequestUserEmail();
            long userId = userRepository.findByEmail(userEmail).get().getId();
            long requestUserId = userRepository.findByEmail(requestUserEmail).get().getId();

            Map<String, Object> response = new HashMap<>();
            response.put("userEmail", userEmail);
            response.put("requestUserEmail", requestUserEmail);
            response.put("userId", userId);
            response.put("requestUserId", requestUserId);
            ackRequest.sendAckData(response);

            // update database
            FriendsModel existingFriendship = friendsRepository.findByUserIdAndFriendId(userId, requestUserId);
            FriendsModel existingFriendship2 = friendsRepository.findByUserIdAndFriendId(requestUserId, userId);
            if (existingFriendship != null && (existingFriendship2.getStatus() == StatusEnum.pending || existingFriendship2.getStatus() == StatusEnum.accepted)) {
                existingFriendship.setStatus(StatusEnum.accepted);
                existingFriendship.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                friendsRepository.save(existingFriendship);
                log.info("Friend request accepted updated through websocket successfully");
            } else if (existingFriendship2 != null && (existingFriendship2.getStatus() == StatusEnum.pending || existingFriendship2.getStatus() == StatusEnum.accepted)) {
                existingFriendship2.setStatus(StatusEnum.accepted);
                existingFriendship2.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                friendsRepository.save(existingFriendship2);
                log.info("Friend request accepted updated through websocket successfully");
            } else {
                throw new IllegalStateException("No pending friend request found or request already accepted");
            }


        });


        server.addEventListener("rejectFriendRequestWS", MessageTokenData.class, (client, data, ackRequest) -> {
            String userEmail = getEmailFromToken(data);
            String requestUserEmail = data.getRequestUserEmail();
            long userId = userRepository.findByEmail(userEmail).get().getId();
            long requestUserId = userRepository.findByEmail(requestUserEmail).get().getId();

            Map<String, Object> response = new HashMap<>();
            response.put("userEmail", userEmail);
            response.put("requestUserEmail", requestUserEmail);
            response.put("userId", userId);
            response.put("requestUserId", requestUserId);
            ackRequest.sendAckData(response);

            // update database
            FriendsModel existingFriendship = friendsRepository.findByUserIdAndFriendId(userId, requestUserId);
            FriendsModel existingFriendship2 = friendsRepository.findByUserIdAndFriendId(requestUserId, userId);
            if (existingFriendship != null && (existingFriendship2.getStatus() == StatusEnum.pending || existingFriendship2.getStatus() == StatusEnum.declined)) {
                existingFriendship.setStatus(StatusEnum.declined);
                existingFriendship.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                friendsRepository.save(existingFriendship);
                log.info("Friend request declined updated through websocket successfully");
            } else if (existingFriendship2 != null && (existingFriendship2.getStatus() == StatusEnum.pending || existingFriendship2.getStatus() == StatusEnum.declined)) {
                existingFriendship2.setStatus(StatusEnum.declined);
                existingFriendship2.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                friendsRepository.save(existingFriendship2);
                log.info("Friend request declined updated through websocket successfully");
            } else {
                throw new IllegalStateException("No pending friend request found or request already accepted");
            }


        });

        server.addEventListener("addMemberRequest", MemberWorkspaceDto.class, (client, data, ackRequest) -> {
            long memberId = data.getUserId();
            String memberEmail = userRepository.findEmailById(memberId).getEmail();
            long roomNumber = data.getRoomNumber();
            broadcastAddMemberToUser(memberEmail, memberId, roomNumber);

        });
    }

    private void broadcastConfirmToUser(String userEmail, String userName, String friendEmail) {
        UserSession friendSession = clients.get(friendEmail); // only friend user receive the invitation pop-up
        logger.info("Friend session: {}", friendSession);
        if (friendSession != null) {
            friendSession.getClient().sendEvent("confirmFriendRequest", userEmail, userName, friendEmail);
        }
    }


    private void broadcastUserLeftMessageToRoom(JoinRoomMessageDto data) {
        log.info("Broadcasting broadcastUserLeftMessage to room: " + data);
        server.getRoomOperations(data.getRoomNumber()).sendEvent("leftRoom", data);
    }

    private void broadcastUserJoinMessageToRoom(JoinRoomMessageDto data) {
        log.info("Broadcasting broadcastUserJoinMessage to room: " + data);
        server.getRoomOperations(data.getRoomNumber()).sendEvent("joinRoom", data);
    }

    private void broadcastMessageToRoom(ChatMessageDto data) {
        log.info("Broadcasting message to room: " + data);
        server.getRoomOperations(data.getRoomNumber()).sendEvent("chatMessage", data);
    }


    private void broadcastInvitationToUser(String userEmail, String userName, String friendEmail) {
        UserSession friendSession = clients.get(friendEmail); // only friend user receive the invitation pop-up
        logger.info("Friend session: {}", friendSession);
        if (friendSession != null) {
            friendSession.getClient().sendEvent("friendRequest", userEmail, userName);
        }
    }

    private void broadcastAddMemberToUser(String memberEmail, long memberId, long roomNumber) {
        UserSession friendSession = clients.get(memberEmail); // only friend user receive the invitation pop-up
        logger.info("Friend session: {}", friendSession);
        if (friendSession != null) {
            friendSession.getClient().sendEvent("addMemberRequest", memberEmail, memberId, roomNumber);
        }
    }


    private void broadcastOnlineUsers() {
        Map<String, Boolean> onlineUsers = listOnlineUsers();
        onlineUsers.forEach((email, online) -> log.info(email + " is online: " + online));
        server.getAllClients().forEach(client -> {
            client.sendEvent("onlineUsers", onlineUsers);
        });
    }

    private String getEmailFromToken(MessageTokenData data) {
        String accessToken = data.getAccessToken();
        return jwtTokenUtil.extractUserEmail(accessToken);
    }

    private String getEmailFromToken(JoinRoomMessageDto data) {
        String accessToken = data.getAccessToken();
        return jwtTokenUtil.extractUserEmail(accessToken);
    }

    private String getEmailFromToken(ChatMessageDto data) {
        String accessToken = data.getAccessToken();
        return jwtTokenUtil.extractUserEmail(accessToken);
    }

    private String getEmailFromToken(MemberWorkspaceDto data) {
        String accessToken = data.getAccessToken();
        return jwtTokenUtil.extractUserEmail(accessToken);
    }


    private String getNameFromToken(MessageTokenData data) {
        String accessToken = data.getAccessToken();
        String email = jwtTokenUtil.extractUserEmail(accessToken);

        Optional<UserModel> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get().getName();
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }


    public Map<String, Boolean> listOnlineUsers() {
        Map<String, Boolean> onlineUsers = new ConcurrentHashMap<>();
        clients.forEach((email, session) -> {
            if (session.isOnline()) {
                onlineUsers.put(email, true);
            }
        });
        return onlineUsers;
    }
}