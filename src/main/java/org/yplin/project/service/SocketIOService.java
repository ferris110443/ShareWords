package org.yplin.project.service;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.dto.socketio.MessageTokenData;
import org.yplin.project.data.form.UserSession;
import org.yplin.project.model.UserModel;
import org.yplin.project.repository.user.UserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class SocketIOService {

    private final SocketIOServer server;
    private final Map<String, UserSession> clients = new ConcurrentHashMap<>();

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

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
                log.info("Client disconnected: {} with token {}", client.getSessionId(), userSession.getAccessToken());
                clients.remove(userSession.getEmail()); // Assuming you have a method to get email from userSession
                userSession.setOnline(false);
                broadcastOnlineUsers(); // Call a method to broadcast the updated list of online users
            } else {
                log.info("Unknown client disconnected: {}", client.getSessionId());
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

        server.addEventListener("addFriendRequest", MessageTokenData.class, (client, data, ackRequest) -> {

            log.info("data: {}", data);
            String userEmail = getEmailFromToken(data);
            String userName = getNameFromToken(data);
            String friendEmail = data.getUserEmail();

            broadcastInvitationToUser(userEmail, userName, friendEmail);

        });


    }


    private void broadcastInvitationToUser(String userEmail, String userName, String friendEmail) {
        UserSession friendSession = clients.get(friendEmail); // only friend user receive the invitation pop-up
        System.out.println("friendSession: " + friendSession);
        if (friendSession != null) {
            friendSession.getClient().sendEvent("friendRequest", userEmail, userName);
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

    private String getNameFromToken(MessageTokenData data) {
        String accessToken = data.getAccessToken();
        String email = jwtTokenUtil.extractUserEmail(accessToken);

        Optional<UserModel> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get().getName();  // Assuming 'name' is the username you want to fetch
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