package io.dropwizard.elasticsearch.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CredentialConfiguration {
    @JsonProperty
    private String user = null;

    @JsonProperty
    private String password = null;

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
