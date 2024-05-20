package org.yplin.project.service.impl;

import kong.unirest.JsonNode;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.MGSendingEmailConfig;

@Service
public class MGServiceImp {
    public static final Logger logger = LoggerFactory.getLogger(FileContentServiceImp.class);


    @Autowired
    MGSendingEmailConfig mgSendingEmailConfig;

    public void sendSimpleMessage(String email, String filePath, String fileName) {
        try {
            JsonNode response = mgSendingEmailConfig.sendSimpleMessage(email, filePath, fileName);
            logger.info("Response: " + response);
        } catch (UnirestException e) {
            logger.error("Error sending mail from service");
            throw new UnirestException("Error sending mail from service");
        }
    }


}
