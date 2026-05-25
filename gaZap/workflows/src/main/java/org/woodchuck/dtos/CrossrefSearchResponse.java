package org.woodchuck.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// 1. The main PUBLIC record (Matches the filename: CrossrefSearchResponse.java)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CrossrefSearchResponse(
    @JsonProperty("message") MessageContainer message
) {

// 2. Package-private helper records (No "public" keyword)
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageContainer(
    @JsonProperty("items") List<WorkItem> items
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkItem(
    @JsonAlias({"doi", "DOI"})@JsonProperty("DOI") String doi,
    @JsonProperty("type") String type,
    @JsonProperty("score") Double score,
    @JsonProperty("title") List<String> title,
    @JsonProperty("container-title") List<String> containerTitle,
    @JsonProperty("author") List<Author> authors,
    @JsonProperty("volume") String volume,
    @JsonProperty("issue") String issue,
    @JsonProperty("page") String page,
    @JsonProperty("issued") IssuedDate issued
) {
    public String getFirstTitle() {
        return (title != null && !title.isEmpty()) ? title.get(0) : "";
    }
    public String getFirstJournal() {
        return (containerTitle != null && !containerTitle.isEmpty()) ? containerTitle.get(0) : "";
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
public record Author(
    @JsonProperty("given") String given,
    @JsonProperty("family") String family
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssuedDate(
    @JsonProperty("date-parts") List<List<Integer>> dateParts
) {
    public String getYear() {
        if (dateParts != null && !dateParts.isEmpty() && !dateParts.get(0).isEmpty()) {
            return String.valueOf(dateParts.get(0).get(0));
        }
        return "";
    }
}

}
