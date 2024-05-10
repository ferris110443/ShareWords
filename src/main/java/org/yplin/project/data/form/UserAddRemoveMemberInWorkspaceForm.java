package org.yplin.project.data.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAddRemoveMemberInWorkspaceForm {

    @Nullable
    private long userId;

    @Nullable
    private String workspaceName;

    @Nullable
    private long roomNumber;

}
