package org.yplin.project.data.form;

import lombok.Data;

@Data
public class CreateFileForm {
    private String fileName;
    private String fileId;
    private String fileDescription;
    private String roomId;
    private long roomNumber;
}
