package org.yplin.project.data.dto.socketio;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageDto {
    private String accessToken;
    private String userEmail;
    private String userName;
    private String message;
    private String roomId;
}
