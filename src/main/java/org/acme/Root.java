package org.acme;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Root {

    @JsonProperty("body")
    private String body;
}