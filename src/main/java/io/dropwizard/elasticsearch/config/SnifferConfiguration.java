package io.dropwizard.elasticsearch.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SnifferConfiguration {

    @JsonProperty
    private Boolean sniffOnFailure = true;

    @JsonProperty
    private int sniffIntervalMillis = 5000;

    @JsonProperty
    private int sniffAfterFailureDelayMillis = 30000;

    public Boolean getSniffOnFailure() {
        return sniffOnFailure;
    }

    public int getSniffIntervalMillis() {
        return sniffIntervalMillis;
    }

    public int getSniffAfterFailureDelayMillis() {
        return sniffAfterFailureDelayMillis;
    }
}
