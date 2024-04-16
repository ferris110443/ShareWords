package org.yplin.project.data.form;

import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

@Data
public class CreateWorkspaceForm {
    private String workspaceName;
    private String workspacePassword;
    private String workspaceDescription;
    private boolean isPublic;
}
