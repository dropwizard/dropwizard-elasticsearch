package io.dropwizard.elasticsearch.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class BasicAuthenticationConfiguration {
    @JsonProperty
    @NotNull
    private String user="";

    @JsonProperty
    @NotNull
    private String password="";

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
