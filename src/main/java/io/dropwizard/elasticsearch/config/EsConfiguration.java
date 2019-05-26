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

    @JsonProperty
    @NotNull
    private int connectTimeOut = 1000;

    @JsonProperty
    @NotNull
    private int socketTimeOut = 30000;

    @JsonProperty
    private int numberOfThreads = 0;

    @JsonProperty
    private String node = "";

    @JsonProperty
    private CredentialConfiguration credential = null;

    @JsonProperty
    private KeyStoreConfiguration keystore = null;



    @JsonProperty
    private SnifferConfiguration sniffer = null;

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

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public String getNode() {
        return node;
    }

    public CredentialConfiguration getCredential() {
        return credential;
    }

    public KeyStoreConfiguration getKeystore() {
        return keystore;
    }

    public SnifferConfiguration getSniffer() {
        return sniffer;
    }
}
