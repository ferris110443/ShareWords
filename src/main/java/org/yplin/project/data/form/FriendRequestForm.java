package org.yplin.project.data.form;


import lombok.Data;
import org.yplin.project.data.ActionEnum;
import org.yplin.project.data.StatusEnum;

@Data
public class FriendRequestForm {
    private long userId;
    private long friendId;
    private StatusEnum status;
    private ActionEnum action;

}
