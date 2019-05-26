package io.dropwizard.elasticsearch.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;
import java.nio.file.Paths;

public class KeyStoreConfiguration {
    @JsonProperty
    private String type = "jks";

    @JsonProperty
    private String keyStorePath;

    @JsonProperty
    private String keyStorePass;

    public String getType() {
        return type;
    }

    public Path getKeyStorePath() {
        return Paths.get(keyStorePath);
    }

    public String getKeyStorePass() {
        return keyStorePass;
    }
}
