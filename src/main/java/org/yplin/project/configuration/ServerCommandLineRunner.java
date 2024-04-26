package org.yplin.project.configuration;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ServerCommandLineRunner implements CommandLineRunner {

    public static final Logger logger = LoggerFactory.getLogger(ServerCommandLineRunner.class);
    private final SocketIOServer server;

    @Override
    public void run(String... args) {
        server.start();
    }
}