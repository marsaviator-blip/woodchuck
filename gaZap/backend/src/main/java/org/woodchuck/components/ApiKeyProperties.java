package org.woodchuck.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyProperties {
    
    @Value("${MP_API_KEY:}")
    private String API_KEY;

    public String getMpApiKey() {
        return API_KEY;
    }

    @Value("${SERP_API_KEY:}")
    private String SERP_API_KEY;

    public String getSerpApiKey() {
        return SERP_API_KEY;
    }
}
