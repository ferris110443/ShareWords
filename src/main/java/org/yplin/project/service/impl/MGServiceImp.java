package org.yplin.project.service.impl;

import kong.unirest.JsonNode;
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.MGSample;

@Service
public class MGServiceImp {

    @Autowired
    MGSample mgSample;

    public void sendSimpleMessage() {
        try {
            JsonNode response = mgSample.sendSimpleMessage();
            System.out.println("Response: " + response);
        } catch (
                UnirestException e) {
            e.printStackTrace();
        }
    }


}
