package io.dropwizard.elasticsearch.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.http.HttpHost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Configuration class for Elasticsearch related settings.
 */
public class EsConfiguration {
    @JsonProperty
    @NotNull
    private List<String> servers = Collections.emptyList();

    public List<String> getServers() {
        return servers;
    }

    public List<HttpHost> getHttpHosts() {
        ArrayList<HttpHost> httpHosts=new ArrayList<>();
        getServers().forEach(hostAndPort -> {
            httpHosts.add(HttpHost.create(hostAndPort));
        });
        return httpHosts;
    }

}
