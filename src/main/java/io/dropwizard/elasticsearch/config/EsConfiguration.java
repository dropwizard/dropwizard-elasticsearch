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

    public List<HttpHost> getServersAsHttpHosts() {
        ArrayList<HttpHost> httpHosts=new ArrayList<>();
        getServers().forEach(hostAndPort -> {
            HttpHost httpHost = HttpHost.create(hostAndPort);
            if (httpHost.getPort() < 0) {
                httpHost = new HttpHost(httpHost.getHostName(), 9200);
            }
            httpHosts.add(httpHost);
        });
        return httpHosts;
    }

}
