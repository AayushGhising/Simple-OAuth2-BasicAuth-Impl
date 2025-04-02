package com.basicAuthOAuth2.basicAuthOAuth2.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExternalAPIService {

    private String api = "http://localhost:8081/api/hello";

    @Autowired
    private RestTemplate restTemplate;

    public static class ExternalAPIResponse {
        private String message;
        private String status;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
    public String getApi() {
        ExternalAPIResponse externalAPIResponse = restTemplate.getForObject(api, ExternalAPIResponse.class);
        if (externalAPIResponse != null) {
            return externalAPIResponse.getMessage();
        } else {
            return "Error: Unable to fetch data from external API";
        }

    }
}
