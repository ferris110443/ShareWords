package org.yplin.project.service;

import org.yplin.project.data.form.MailServiceForm;
import org.yplin.project.error.MarkdownFileSendingFailException;


public interface MarkdownMailService {

    void sendMarkdownFiles(MailServiceForm mailServiceForm) throws MarkdownFileSendingFailException;
}
