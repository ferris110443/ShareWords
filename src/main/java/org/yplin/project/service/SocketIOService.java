package org.yplin.project.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.data.dto.InitEventData;
import org.yplin.project.data.dto.MessageData;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SocketIOService {
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
        server.addEventListener("message", MessageData.class, onMessageReceived());
    }

    private ConnectListener onConnected() {
        return client -> {
            String roomId = client.getHandshakeData().getSingleUrlParam("roomId");
            if (roomId != null) {
                client.joinRoom(roomId);
                System.out.println("Client connected to room: " + roomId);
            }


            System.out.println("Client connected: " + client.getSessionId());
            int userId = nextUserId.getAndIncrement();
            System.out.println("Connection - Assigning ID: " + userId);
            InitEventData initData = new InitEventData(userId);
            System.out.println("Sending init data: " + initData);
            client.sendEvent("init", new InitEventData(userId));
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            System.out.println("Client disconnected: " + client.getSessionId());
        };
    }

    private DataListener<MessageData> onMessageReceived() {
        return (client, data, ackSender) -> {
            System.out.println("Message received: " + data);
            // Broadcast the message to all clients after a delay
            int delayMilliseconds = 0;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Message received in room: " + data.getRoomId() + " with content: " + data);
                    server.getRoomOperations(data.getRoomId()).sendEvent("message", data);
                    // send the message to all clients;
                    // if need to send to specific clients https://medium.com/folksdev/spring-boot-netty-socket-io-example-3f21fcc1147d
                }
            }, delayMilliseconds);
        };
    }
}
