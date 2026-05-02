package org.woodchuck.temporal.activities;

import java.io.File;
import java.net.URI;

import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Component;
import io.temporal.spring.boot.ActivityImpl;
import  org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Component
@ActivityImpl(taskQueues = "PublicationQueue")
public class PublicationActivitiesImpl implements PublicationActivities {

    private static final String BASE_URL = "https://pmc-oa-opendata.s3.amazonaws.com";
    private static final String ID_BASE_URL = "https://pmc.ncbi.nlm.nih.gov";
    private final RestClient restClient;

    public PublicationActivitiesImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public String getPublication(String doi) {

        URI idUrl = UriComponentsBuilder.fromUriString(ID_BASE_URL)
            .path("/tools/idconv/api/v1/articles/")
            .query("ids={doi}&versions=yes")
            .buildAndExpand(doi)
            .toUri();
            String pmcResponse = restClient.get()
            .uri(idUrl)
            .retrieve()
            .body(String.class);
            System.out.println("Received PMC response: " + pmcResponse);
            try{
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode rootNode = xmlMapper.readTree(pmcResponse);
            String status = rootNode.get("status").asText();
           
            JsonNode recordsNode = rootNode.path("record");
            String pmcID = recordsNode.get("pmcid").asText();
            String pmcVersion = recordsNode.path("versions").path("version").get("pmcid").asText();
        System.out.println("PublicationActivitiesImpl.getPublication called with PMCID: " + pmcID + " "+ pmcVersion+" with status: " + status);
            if(!"ok".equalsIgnoreCase(status)) {
                throw new IllegalStateException("PMC API returned non-ok status for DOI: " + doi);
            }
        URI targetUrl = UriComponentsBuilder.fromUriString(BASE_URL)
            .path("/{pmcVersion}/{pmcVersion}.xml")
            //.query("format=xml")
            .buildAndExpand(pmcVersion, pmcVersion)
            .toUri();
        System.out.println("PublicationActivitiesImpl.getPublication: targetUrl=" + targetUrl);  
        return restClient.get()
            .uri(targetUrl)
            .retrieve()
            .body(String.class);
            } catch (JsonMappingException e) {
                throw new IllegalStateException("Failed to parse PMC response for DOI: " + doi, e);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to parse PMC response for DOI: " + doi, e);
            } catch (Exception ex) {
                throw new IllegalStateException("Failed to fetch publication data for DOI: " + doi, ex);
            }
    }

}
