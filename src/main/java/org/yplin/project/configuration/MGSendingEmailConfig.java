package org.yplin.project.configuration;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class MGSendingEmailConfig {

    public static final Logger logger = LoggerFactory.getLogger(MGSendingEmailConfig.class);

    @Value("${mailgun.api.key}")
    private String API_KEY;

    @Value("${mailgun.domain}")
    private String YOUR_DOMAIN_NAME;

    public JsonNode sendSimpleMessage(String email, String filePath, String fileName) throws UnirestException {
        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .queryString("from", "ferris110443@gmail.com")
                .queryString("to", email)
                .queryString("text", "testing")
                .field("subject", "[ShareWords] : New Files Shared With You " + fileName)
                .field("template", "sharewords")
                .field("h:X-Mailgun-Variables", "{\"test\": \"test\"}")
                .field("attachment", new File(filePath))
                .asJson();
        if (request.getStatus() != 200) {
            logger.error("Failed to send email: " + request.getStatusText());
            logger.error("Response body: " + request.getBody());
            throw new UnirestException("Failed to send email: " + request.getStatusText());
        }

        return request.getBody();
    }

}