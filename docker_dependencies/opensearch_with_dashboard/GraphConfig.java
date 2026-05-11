package org.lim.components;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import java.util.List;

import org.lim.components.YamlPropertySourceFactory;

record Person(String id, String name){}
record Interest(String id, String title){}
record Relationship(String person, String interests){}

@ConfigurationProperties(prefix = "graph")
public record GraphConfig(Person person,
        List<Interest> interests,
        List<Relationship> relationships) {

    @ConstructorBinding
    public GraphConfig(Person person, List<Interest> interests, List<Relationship> relationships) {
        this.person = person;
        this.interests = interests;
        this.relationships = relationships;
    }

}