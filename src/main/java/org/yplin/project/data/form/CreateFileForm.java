package org.yplin.project.data.form;

import lombok.Data;

@Data
public class CreateFileForm {
    private String fileName;
    private String fileId;
    private long workspaceId;
}
