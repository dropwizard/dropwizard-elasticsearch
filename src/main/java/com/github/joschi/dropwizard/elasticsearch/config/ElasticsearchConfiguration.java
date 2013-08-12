package com.github.joschi.dropwizard.elasticsearch.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.HostAndPort;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 */
public class ElasticsearchConfiguration {
    @JsonProperty
    @NotNull
    private List<HostAndPort> servers = Collections.emptyList();

    @JsonProperty
    @NotEmpty
    private String clusterName = "elasticsearch";

    @JsonProperty
    private boolean nodeClient = true;

    @JsonProperty
    @NotNull
    private Map<String, String> settings = Collections.emptyMap();

    public List<HostAndPort> getServers() {
        return servers;
    }

    public String getClusterName() {
        return clusterName;
    }

    public boolean isNodeClient() {
        return nodeClient;
    }

    public Map<String, String> getSettings() {
        return settings;
    }
}
