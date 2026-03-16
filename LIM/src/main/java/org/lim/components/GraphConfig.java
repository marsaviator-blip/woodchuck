package org.lim.components;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import java.util.List;

import org.lim.components.YamlPropertySourceFactory;

@Configuration
@PropertySource(value = "classpath:personInterests.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "personInterests")
public class GraphConfig {
    private String person;
    private List<String> interests;
    private List<Relationship> relationships;  
    public String getPerson() {
        return person;
    }
    public void setPerson(String person) {
        this.person = person;
    }
    public List<String> getInterests() {
        return interests;
    }
    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }
    public List<Relationship> getRelationships() {
        return relationships;
    }
    public static class  Person {
 
        private String id;
        private String name;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    public static class Interest {
       private String id;
       private String title;
       public String getId() {
           return id;
       }
       public void setId(String id) {
           this.id = id;
       }
       public String getTitle() {
           return title;
       }
       public void setTitle(String title) {
           this.title = title;
       }
    }
    public static class Relationship {
        private String personId;
        private List<String> interestIds;
        public String getPersonId() {
            return personId;
        }
        public void setPersonId(String personId) {
            this.personId = personId;
        }
        public List<String> getInterestIds() {
            return interestIds;
        }
        public void setInterestIds(List<String> interestIds) {
            this.interestIds = interestIds;
        }
    }
}