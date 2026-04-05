package org.hatchery.client.services;

import org.springframework.ai.mcp.annotation.McpClient;
import org.springframework.ai.mcp.annotation.HttpClientStreamableHttpTransport;
import org.springframework.ai.mcp.annotation.CallToolRequest;
import org.springframework.ai.mcp.annotation.CallToolResult;  
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ClientCall {
 
    public void callMcpServer() {
        System.out.println("Calling MCP Server...");
        var client = McpClient.sync(
                HttpClientStreamableHttpTransport
                        .builder("http://localhost:8080").build())
                .build();

        client.initialize();

        CallToolResult weather = client.callTool(
                new CallToolRequest("getTemperature",
                        Map.of("latitude", "47.6062",
                                "longitude", "-122.3321")));
        {

        }
    }
}
