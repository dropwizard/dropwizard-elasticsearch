package io.dropwizard.elasticsearch.managed;

import org.apache.http.HttpHost;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.node.Node;

import io.dropwizard.elasticsearch.config.EsConfiguration;
import io.dropwizard.lifecycle.Managed;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * A Dropwizard managed Elasticsearch {@link RestHighLevelClient}.
 * Depending on the {@link EsConfiguration} a High Level Rest Client
 * a {@link RestHighLevelClient} a is being created and its lifecycle is managed by Dropwizard.
 *
 */
public class ManagedEsClient implements Managed {
    private RestHighLevelClient client = null;
    public ManagedEsClient(final EsConfiguration config) {
        checkNotNull(config, "EsConfiguration must not be null");
        this.client =
                new RestHighLevelClient(
                        RestClient.builder(config.getServersAsHttpHosts().toArray(new HttpHost[0]))
                );
    }

    /**
     * Create a new managed Elasticsearch {@link Client} from the provided {@link Client}.
     *
     * @param client an initialized {@link Client} instance
     */
    public ManagedEsClient(RestHighLevelClient client) {
        this.client = checkNotNull(client, "Elasticsearch client must not be null");
    }

    /**
     * Starts the Elasticsearch {@link Node} (if appropriate). Called <i>before</i> the service becomes available.
     *
     * @throws Exception if something goes wrong; this will halt the service startup.
     */
    @Override
    public void start() throws Exception {
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
    }

    /**
     * Get the managed Elasticsearch {@link Client} instance.
     *
     * @return a valid Elasticsearch {@link Client} instance
     */
    public RestHighLevelClient getClient() {
        return client;
    }


    private void closeClient() throws Exception {
        if (null != client) {
            client.close();
        }
    }
}
