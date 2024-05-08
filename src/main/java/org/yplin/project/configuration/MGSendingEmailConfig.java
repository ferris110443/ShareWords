package org.yplin.project.configuration;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class MGSendingEmailConfig {

    @Value("${mailgun.api.key}")
    private String API_KEY;

    @Value("${mailgun.domain}")
    private String YOUR_DOMAIN_NAME;

    public JsonNode sendSimpleMessage(String email, String filePath, String fileName) throws UnirestException {
        System.out.println("email: " + email);
        System.out.println("filePath: " + filePath);
        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .queryString("from", "ferris110443@gmail.com")
//                .queryString("from", "ferris110443@gmail.com<ferris110443@" + YOUR_DOMAIN_NAME + ">")
//                .queryString("from", email + "<ferris110443@hotmail.com.tw>")
                .queryString("to", email)
                .queryString("text", "testing")
                .field("subject", "[ShareWords] : New Files Shared With You " + fileName)
                .field("template", "sharewords")
                .field("h:X-Mailgun-Variables", "{\"test\": \"test\"}")
                .field("attachment", new File(filePath))
                .asJson();
        if (request.getStatus() != 200) {
            System.out.println("Failed to send email: " + request.getStatusText());
            System.out.println("Response body: " + request.getBody());
        }

        return request.getBody();
    }

//    public JsonNode sendSimpleMessage(String email, String filePath, String fileName) {
//        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
//                .basicAuth("api", API_KEY)
//                .field("from", "ShareWords <ferris110443@" + YOUR_DOMAIN_NAME + ">")
//                .field("to", email)
//                .field("subject", "[ShareWords] : New Files Shared With You " + fileName)
//                .field("template", "sharewords")
//                .field("h:X-Mailgun-Variables", "{\"test\": \"test\"}")
//                .field("attachment", new File(filePath))
//                .asJson();
//
//        return response.getBody();
//    }


}