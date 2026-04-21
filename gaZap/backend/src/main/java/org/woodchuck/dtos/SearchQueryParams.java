package org.woodchuck.dtos;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

public class SearchQueryParams {
    private String value;
    private String returnType;

    private String search_template = """
json='{'
  "query": '{'
    "type": "terminal",
    "service": "full_text",
    "parameters": '{'
      "value": "{0}"
    '}'
  '}',
  "return_type": "{1}"
'}'
  """;

    public SearchQueryParams(String value, String returnType) {
        this.value = value;
        this.returnType = returnType;
    }

    public String getQuery() {
         return MessageFormat.format(search_template, value, returnType);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
}