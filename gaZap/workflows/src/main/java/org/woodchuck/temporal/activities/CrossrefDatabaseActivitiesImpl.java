package org.woodchuck.temporal.activities;

import org.springframework.stereotype.Component;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.woodchuck.dtos.CitedReferencesResult;
import org.woodchuck.dtos.CrossrefSearchResponse;
import org.woodchuck.dtos.CrossrefXmlResponse;
import org.woodchuck.temporal.activities.CrossrefDatabaseActivities;

import io.temporal.spring.boot.ActivityImpl;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.xml.XmlMapper;

@Component
@ActivityImpl(taskQueues = "CrossrefQueue")
public class CrossrefDatabaseActivitiesImpl implements CrossrefDatabaseActivities {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper(); // Ensure namespace configuration matching earlier steps
    
    public CrossrefDatabaseActivitiesImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isRecordCached(String doi) {
        String sql = "SELECT EXISTS(SELECT 1 FROM crossref_responses WHERE doi = ?)";
        try {
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, doi));
        } catch (Exception e) {
            return false; // Fallback to safe false on DB glitches
        }
    }

    @Override
    public CitedReferencesResult findCachedRecord(String citeKey, String doi) {
        String sql = "SELECT raw_xml, raw_json FROM crossref_responses WHERE doi = ?";
        
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                String xml = rs.getString("raw_xml");
                String json = rs.getString("raw_json");

                // 1. Reconstruct from XML source if it exists
                if (xml != null && !xml.isEmpty()) {
                    try {
                        CrossrefXmlResponse xmlDto = xmlMapper.readValue(xml, CrossrefXmlResponse.class);
                        return CitedReferencesResult.fromXml(citeKey, xml, xmlDto);
                    } catch (Exception e) {
                        throw new RuntimeException("Cached XML re-parsing failed for DOI: " + doi, e);
                    }
                }
                
                // 2. Reconstruct from JSON source if XML is missing
                if (json != null && !json.isEmpty()) {
                    try {
                        CrossrefSearchResponse jsonDto = jsonMapper.readValue(json, CrossrefSearchResponse.class);
                        return CitedReferencesResult.fromJson(citeKey, json, jsonDto);
                    } catch (Exception e) {
                        throw new RuntimeException("Cached JSON re-parsing failed for DOI: " + doi, e);
                    }
                }

                return null;
            }, doi);
            
        } catch (EmptyResultDataAccessException e) {
            return null; // Explicit cache miss
        }
    }
    
    @Override
    public void saveResponse(String doi, String journalTitle, String rawXml, String rawJson) {
        // Implement the logic to save the response to the database
    String sql = """
        INSERT INTO crossref_responses (doi, journal_title, raw_xml, raw_json) 
        VALUES (?, ?, CAST(? AS xml), CAST(? AS jsonb)) 
        ON CONFLICT (doi) DO UPDATE SET 
            journal_title = COALESCE(EXCLUDED.journal_title, crossref_responses.journal_title),
            raw_xml = COALESCE(EXCLUDED.raw_xml, crossref_responses.raw_xml),
            raw_json = COALESCE(EXCLUDED.raw_json, crossref_responses.raw_json);
    """;

        // Spring handles connection opening/closing automatically here
        jdbcTemplate.update(sql, doi, journalTitle, rawXml, rawJson);
    }
}
