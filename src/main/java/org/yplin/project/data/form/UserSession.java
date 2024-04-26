package org.yplin.project.data.form;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.Data;
import lombok.Setter;

@Data
public class UserSession {
    private final SocketIOClient client;
    private final String accessToken;
    private final String email;
    @Setter
    private boolean online;

    public UserSession(SocketIOClient client, boolean online, String accessToken, String email) {
        this.client = client;
        this.online = online;
        this.accessToken = accessToken;
        this.email = email;
    }

}
