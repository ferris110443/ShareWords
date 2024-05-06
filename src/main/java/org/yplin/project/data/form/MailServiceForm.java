package org.yplin.project.data.form;


import lombok.Data;

@Data
public class MailServiceForm {
    private String recipientEmail;
    private String fileId;
}
