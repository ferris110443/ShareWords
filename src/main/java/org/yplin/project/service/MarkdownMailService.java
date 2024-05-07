package org.yplin.project.service;

import org.yplin.project.data.form.MailServiceForm;


public interface MarkdownMailService {

    void sendMarkdownFiles(MailServiceForm mailServiceForm);
}
