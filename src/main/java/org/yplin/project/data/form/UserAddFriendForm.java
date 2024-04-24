package org.yplin.project.data.form;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAddFriendForm {

    @Nullable
    private Long friendId;

    @Nullable
    private Long userId;

    @Nullable
    private String status;

    @Nullable
    private Timestamp createdAt;
}
