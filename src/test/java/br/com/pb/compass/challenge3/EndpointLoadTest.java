package br.com.pb.compass.challenge3;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class EndpointLoadTest {

    private static final String BASE_URL = "http://localhost:8080";

    @Test
    public void testEndpointLoad() throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        String endpoint = "/posts/{postId}";

        int numberOfRequests = 100;

        for (int i = 0; i < numberOfRequests; i++) {
            HttpPost request = new HttpPost(BASE_URL + endpoint.replace("{postId}", String.valueOf(i + 1)));

            HttpResponse response = httpClient.execute(request);

            System.out.println("Request " + (i + 1) + " - Status code: " + response.getStatusLine().getStatusCode());
        }
    }
}