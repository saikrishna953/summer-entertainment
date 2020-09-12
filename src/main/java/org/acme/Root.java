package org.acme;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Root {

    @JsonProperty("body")
    private String body;
}