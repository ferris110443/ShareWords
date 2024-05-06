package org.yplin.project.configuration;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MGSample {

    @Value("${mailgun.api.key}")
    private String API_KEY;

    @Value("${mailgun.domain}")
    private String YOUR_DOMAIN_NAME;

    public JsonNode sendSimpleMessage() throws UnirestException {
        System.out.println(API_KEY);
        System.out.println(YOUR_DOMAIN_NAME);
        System.out.println("test send message");
        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .queryString("from", "Excited User <ferris110443@" + YOUR_DOMAIN_NAME + ">")
                .queryString("to", "ferris110443@hotmail.com.tw")
                .queryString("subject", "hello")
                .queryString("text", "testing")
//                .field("attachment", new File("C:\\Users\\USER\\OneDrive\\Programming\\JavaProject\\AppWorks\\Personal project\\POC\\websocketPoc\\src\\main\\java\\org\\example\\websocketpoc\\test.pdf")) // 放在與 MGSample folder
                .asJson();
        if (request.getStatus() != 200) {
            System.out.println("Failed to send email: " + request.getStatusText());
            System.out.println("Response body: " + request.getBody());
        }

        return request.getBody();
    }
}