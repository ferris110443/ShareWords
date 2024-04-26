package org.yplin.project.service;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.JwtTokenUtil;
import org.yplin.project.data.form.MessageData;
import org.yplin.project.data.form.UserSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class SocketIOService {

    private final SocketIOServer server;
    private final Map<String, UserSession> clients = new ConcurrentHashMap<>();

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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
            } else {
                log.info("Unknown client disconnected: {}", client.getSessionId());
            }
        });

        server.addEventListener("message", MessageData.class, (client, data, ackRequest) -> {
            String userEmail = getEmailFromToken(data);
            if (userEmail != null) {
                clients.putIfAbsent(userEmail, new UserSession(client, true, data.getAccessToken(), userEmail));
                clients.get(userEmail).setOnline(true);
                log.info("User {} is now online with token {}", userEmail, data.getAccessToken());
                Map<String, Boolean> onlineUsers = listOnlineUsers();
                onlineUsers.forEach((email, online) -> System.out.println(email + " is online: " + online));
                ackRequest.sendAckData("Message received");
                client.sendEvent("onlineUsers", onlineUsers);
            }
        });
    }


    private String getEmailFromToken(MessageData data) {
        String accessToken = data.getAccessToken();
        return jwtTokenUtil.extractUserEmail(accessToken);
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