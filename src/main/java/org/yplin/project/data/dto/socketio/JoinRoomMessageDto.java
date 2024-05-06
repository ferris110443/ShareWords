package org.yplin.project.data.dto.socketio;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JoinRoomMessageDto {
    private String accessToken;
    private String roomId;
}
