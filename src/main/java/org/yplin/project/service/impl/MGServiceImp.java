package org.yplin.project.service.impl;

import kong.unirest.JsonNode;
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.MGSendingEmailConfig;

@Service
public class MGServiceImp {

    @Autowired
    MGSendingEmailConfig mgSendingEmailConfig;

    public void sendSimpleMessage(String email, String filePath, String fileName) {
        try {
            JsonNode response = mgSendingEmailConfig.sendSimpleMessage(email, filePath, fileName);
            System.out.println("Response: " + response);
        } catch (
                UnirestException e) {
            e.printStackTrace();
        }
    }


}
