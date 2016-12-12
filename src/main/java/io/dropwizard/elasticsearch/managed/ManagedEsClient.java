package io.dropwizard.elasticsearch.managed;

import com.google.common.io.Resources;
import io.dropwizard.elasticsearch.config.EsConfiguration;
import io.dropwizard.elasticsearch.util.TransportAddressHelper;
import io.dropwizard.lifecycle.Managed;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.node.internal.InternalSettingsPreparer;
import org.elasticsearch.transport.Netty4Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * A Dropwizard managed Elasticsearch {@link Client}. Depending on the {@link EsConfiguration} a Node Client or
 * a {@link TransportClient} a is being created and its lifecycle is managed by Dropwizard.
 *
 * @see <a href="http://www.elasticsearch.org/guide/reference/java-api/client/#nodeclient">Node Client</a>
 * @see <a href="http://www.elasticsearch.org/guide/reference/java-api/client/#transportclient">Transport Client</a>
 */
public class ManagedEsClient implements Managed {
    private Node node = null;
    private Client client = null;

    /**
     * Create a new managed Elasticsearch {@link Client}. If {@link EsConfiguration#nodeClient} is {@literal true}, a
     * Node Client is being created, otherwise a {@link TransportClient} is being created with {@link EsConfiguration#servers}
     * as transport addresses.
     *
     * @param config a valid {@link EsConfiguration} instance
     */
    public ManagedEsClient(final EsConfiguration config) throws IOException {
        checkNotNull(config, "EsConfiguration must not be null");

        final Settings.Builder settingsBuilder = Settings.builder();
        if (!isNullOrEmpty(config.getSettingsFile())) {
            Path path = Paths.get(config.getSettingsFile());
            if (!path.toFile().exists()) {
                try {
                    final URL url = Resources.getResource(config.getSettingsFile());
                    path = new File(url.toURI()).toPath();
                } catch (URISyntaxException | NullPointerException e) {
                    throw new IllegalArgumentException("settings file cannot be found", e);
                }
            }
            settingsBuilder.loadFromPath(path);
        }

        final Settings settings = settingsBuilder
                .put(config.getSettings())
                .put("cluster.name", config.getClusterName())
                .put(Node.NODE_DATA_SETTING.getKey(), false)
                .put(Node.NODE_MASTER_SETTING.getKey(), false)
                .put(Node.NODE_INGEST_SETTING.getKey(), false)
                .build();

        if (config.isNodeClient()) {
            this.node = new PluginNode(settings);
            this.client = this.node.client();
        } else {
            final TransportAddress[] addresses = TransportAddressHelper.fromHostAndPorts(config.getServers());
            this.client = new PreBuiltTransportClient(settings).addTransportAddresses(addresses);
        }
    }

    /**
     * Create a new managed Elasticsearch {@link Client} from the provided {@link Node}.
     *
     * @param node a valid {@link Node} instance
     */
    public ManagedEsClient(final Node node) {
        this.node = checkNotNull(node, "Elasticsearch node must not be null");
        this.client = node.client();
    }


    /**
     * Create a new managed Elasticsearch {@link Client} from the provided {@link Client}.
     *
     * @param client an initialized {@link Client} instance
     */
    public ManagedEsClient(Client client) {
        this.client = checkNotNull(client, "Elasticsearch client must not be null");
    }

    /**
     * Starts the Elasticsearch {@link Node} (if appropriate). Called <i>before</i> the service becomes available.
     *
     * @throws Exception if something goes wrong; this will halt the service startup.
     */
    @Override
    public void start() throws Exception {
        startNode();
    }

    /**
     * Stops the Elasticsearch {@link Client} and (if appropriate) {@link Node} objects. Called <i>after</i> the service
     * is no longer accepting requests.
     *
     * @throws Exception if something goes wrong.
     */
    @Override
    public void stop() throws Exception {
        closeClient();
        closeNode();
    }

    /**
     * Get the managed Elasticsearch {@link Client} instance.
     *
     * @return a valid Elasticsearch {@link Client} instance
     */
    public Client getClient() {
        return client;
    }

    private Node startNode() throws NodeValidationException {
        if (null != node) {
            return node.start();
        }

        return null;
    }

    private void closeNode() throws IOException {
        if (null != node && !node.isClosed()) {
            node.close();
        }
    }

    private void closeClient() {
        if (null != client) {
            client.close();
        }
    }

    static class PluginNode extends Node {
        PluginNode(Settings settings) {
            super(InternalSettingsPreparer.prepareEnvironment(settings, null), Collections.singletonList(Netty4Plugin.class));
        }
    }
}
