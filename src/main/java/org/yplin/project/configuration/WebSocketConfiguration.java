package org.yplin.project.configuration;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Slf4j
@org.springframework.context.annotation.Configuration
public class WebSocketConfiguration {

    public static final Logger logger = LoggerFactory.getLogger(WebSocketConfiguration.class);

    @Value("${socket-server.host}")
    private String host;


    @Value("${socket-server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host); // Listen on all IPv4 addresses
        config.setPort(port);

        return new SocketIOServer(config);
    }


}
