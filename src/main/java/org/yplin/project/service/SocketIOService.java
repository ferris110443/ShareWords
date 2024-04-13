package org.yplin.project.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.data.form.InitEventDataForm;
import org.yplin.project.data.form.MessageDataForm;
import org.yplin.project.service.impl.FileContentServiceImp;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
public class SocketIOService {
    public static final Logger logger = LoggerFactory.getLogger(SocketIOService.class);
    private final SocketIOServer server;
    private final AtomicInteger nextUserId = new AtomicInteger(1);
    //perform atomic (i.e., thread-safe) operations on an integer value.

    @Autowired
    public SocketIOService(SocketIOServer server) {
        this.server = server;
    }

    @PostConstruct // This method will be called after the bean is created
    public void init() {
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("message", MessageDataForm.class, onMessageReceived());
    }

    private ConnectListener onConnected() {
        return client -> {
            String roomId = client.getHandshakeData().getSingleUrlParam("roomId");
            if (roomId != null) {
                client.joinRoom(roomId);
                logger.info("Client connected to room: " + roomId);
            }

            logger.info("Client connected: " + client.getSessionId());
            int userId = nextUserId.getAndIncrement();
            logger.info("Connection - Assigning ID: " + userId);
            InitEventDataForm initData = new InitEventDataForm(userId);
            logger.info("Sending init data: " + initData);
            client.sendEvent("init", new InitEventDataForm(userId));
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            logger.error("Client disconnected: " + client.getSessionId());
        };
    }

    private DataListener<MessageDataForm> onMessageReceived() {
        return (client, data, ackSender) -> {
            logger.info("Message received: " + data);
            // Broadcast the message to all clients after a delay
            int delayMilliseconds = 0;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    logger.info("Message received in room: " + data.getRoomId() + " with content: " + data);
                    server.getRoomOperations(data.getRoomId()).sendEvent("message", data);
                    // send the message to all clients;
                    // if need to send to specific clients https://medium.com/folksdev/spring-boot-netty-socket-io-example-3f21fcc1147d
                }
            }, delayMilliseconds);
        };
    }
}
