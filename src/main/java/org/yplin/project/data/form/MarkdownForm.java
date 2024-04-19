package org.yplin.project.data.form;

import lombok.Data;

@Data
public class MarkdownForm {
    private String markdownText;
    private String title;
    private String roomId;
    private String fileId;
}
