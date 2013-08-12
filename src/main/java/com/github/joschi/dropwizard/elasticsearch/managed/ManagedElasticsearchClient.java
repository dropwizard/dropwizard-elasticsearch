package com.github.joschi.dropwizard.elasticsearch.managed;

import com.github.joschi.dropwizard.elasticsearch.config.ElasticsearchConfiguration;
import com.github.joschi.dropwizard.elasticsearch.util.TransportAddressHelper;
import com.yammer.dropwizard.lifecycle.Managed;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 */
public class ManagedElasticsearchClient implements Managed {
    private Node node = null;
    private Client client = null;

    public ManagedElasticsearchClient(final ElasticsearchConfiguration config) {
        final Settings settings = ImmutableSettings.settingsBuilder()
                .put(config.getSettings())
                .put("cluster.name", config.getClusterName())
                .build();

        if (config.isNodeClient()) {
            this.node = nodeBuilder()
                    .client(true)
                    .settings(settings)
                    .build();
            this.client = this.node.client();
        } else {
            final TransportAddress[] addresses = TransportAddressHelper.fromHostAndPorts(config.getServers());
            this.client = new TransportClient(settings).addTransportAddresses(addresses);
        }
    }

    public ManagedElasticsearchClient(final Node node) {
        this.node = node;
        this.client = node.client();
    }


    public ManagedElasticsearchClient(Client client) {
        this.client = client;
    }

    @Override
    public void start() throws Exception {
        startNode();
    }

    @Override
    public void stop() throws Exception {
        closeClient();
        closeNode();
    }

    public Client getClient() {
        return client;
    }

    private Node startNode() {
        if (null != node) {
            return node.start();
        }

        return null;
    }

    private void closeNode() {
        if (null != node && !node.isClosed()) {
            node.close();
        }
    }

    private void closeClient() {
        if (null != client) {
            client.close();
        }
    }
}
