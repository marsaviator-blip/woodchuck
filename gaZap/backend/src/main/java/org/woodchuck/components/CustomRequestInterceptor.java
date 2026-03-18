package org.woodchuck.components;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        
        // **Examine the request (e.g., for logging)**
        System.out.println("Intercepting request: " + request.getURI());
        
        // **Modify headers**
        request.getHeaders().add("X-Custom-Header", "Spring-RestClient-Demo");
        
        // **Execute the request to proceed to the next interceptor or the network**
        ClientHttpResponse response = execution.execute(request, body);
        
        // **Examine the response (e.g., for logging)**
        System.out.println("Received response with status: " + response.getStatusCode());

        return response;
    }
}