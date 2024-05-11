package org.yplin.project.data.dto.socketio;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberWorkspaceDto {

    private long userId;
    private long roomNumber;
    private String accessToken;

}
