package org.yplin.project.data.form;


import lombok.Data;
import org.yplin.project.model.StatusEnum;

@Data
public class FriendRequestForm {
    private long userId;
    private long friendId;
    private StatusEnum status;
}
