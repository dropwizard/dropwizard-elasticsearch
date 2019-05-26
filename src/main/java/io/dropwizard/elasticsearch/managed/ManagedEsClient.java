package io.dropwizard.elasticsearch.managed;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.client.sniff.SnifferBuilder;

import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.util.Iterator;

import javax.net.ssl.SSLContext;

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
    private Sniffer sniffer = null;
    public ManagedEsClient(final EsConfiguration config) {
        checkNotNull(config, "EsConfiguration must not be null");

        RestClientBuilder restClientBuilder = RestClient.builder(config.getServersAsHttpHosts().toArray(new HttpHost[0]));
        setRequest(restClientBuilder, config);

        if (config.getNumberOfThreads()>0) {
            setThreads(restClientBuilder, config);
        }

        if (config.getNode()!= null && !config.getNode().isEmpty()) {
            setNodeSelector(restClientBuilder, config);
        }

        if (config.getCredential() != null) {
            setCredential(restClientBuilder, config);
        }

        if (config.getKeystore()!= null) {
            setCredential(restClientBuilder, config);
        }

        if (config.getSniffer()!=null) {
            if (config.getSniffer().getSniffOnFailure()) {
                SniffOnFailureListener sniffOnFailureListener =
                        new SniffOnFailureListener();
                restClientBuilder.setFailureListener(sniffOnFailureListener);
                this.client = new RestHighLevelClient(restClientBuilder);
                this.sniffer = Sniffer.builder(this.client.getLowLevelClient())
                        .setSniffAfterFailureDelayMillis(config.getSniffer().getSniffAfterFailureDelayMillis())
                        .build();
                sniffOnFailureListener.setSniffer(this.sniffer);

            } else {
                this.client = new RestHighLevelClient(restClientBuilder);
                this.sniffer = Sniffer.builder(this.client.getLowLevelClient())
                        .setSniffIntervalMillis(config.getSniffer().getSniffIntervalMillis())
                        .build();
            }

        } else {
            this.client = new RestHighLevelClient(restClientBuilder);
        }
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
        if (null != sniffer) {
            sniffer.close();
        }
    }

    private void setRequest(RestClientBuilder restClientBuilder, EsConfiguration config) {
        restClientBuilder.setRequestConfigCallback(
                new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(
                            RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder
                                .setConnectTimeout(config.getConnectTimeOut())
                                .setSocketTimeout(config.getSocketTimeOut());
                    }
                });
    }

    private void setThreads(RestClientBuilder restClientBuilder, EsConfiguration config) {
        restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultIOReactorConfig(
                        IOReactorConfig.custom()
                                .setIoThreadCount(config.getNumberOfThreads())
                                .build());
            }
        });
    }

    private void setCredential(RestClientBuilder restClientBuilder, EsConfiguration config) {
        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(config.getCredential().getUser(), config.getCredential().getPassword()));
        restClientBuilder
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        httpClientBuilder.disableAuthCaching();
                        return httpClientBuilder
                                .setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
    }

    private void setKeyStore(RestClientBuilder restClientBuilder, EsConfiguration config) throws Exception {
        KeyStore truststore = KeyStore.getInstance(config.getKeystore().getType());
        try (InputStream is = Files.newInputStream(config.getKeystore().getKeyStorePath())) {
            truststore.load(is, config.getKeystore().getKeyStorePass().toCharArray());
        }
        SSLContextBuilder sslBuilder = SSLContexts.custom()
                .loadTrustMaterial(truststore, null);
        final SSLContext sslContext = sslBuilder.build();
        restClientBuilder
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setSSLContext(sslContext);
                    }
                });
    }

    private void setNodeSelector(RestClientBuilder restClientBuilder, EsConfiguration config) {
        restClientBuilder.setNodeSelector(new NodeSelector() {
            @Override
            public void select(Iterable<Node> nodes) {
                /*
                 * Prefer any node that belongs to rack_one. If none is around
                 * we will go to another rack till it's time to try and revive
                 * some of the nodes that belong to rack_one.
                 */
                boolean foundOne = false;
                for (Node node : nodes) {
                    String rackId = node.getAttributes().get("rack_id").get(0);
                    if (config.getNode().equals(rackId)) {
                        foundOne = true;
                        break;
                    }
                }
                if (foundOne) {
                    Iterator<Node> nodesIt = nodes.iterator();
                    while (nodesIt.hasNext()) {
                        Node node = nodesIt.next();
                        String rackId = node.getAttributes().get("rack_id").get(0);
                        if (config.getNode().equals(rackId) == false) {
                            nodesIt.remove();
                        }
                    }
                }
            }
        });
    }

    private Sniffer setSniffer(RestClient restClient, EsConfiguration config) {
        SnifferBuilder snifferBuilder = Sniffer.builder(restClient)
                .setSniffIntervalMillis(config.getSniffer().getSniffIntervalMillis());

        if (config.getSniffer().getSniffOnFailure()) {
            snifferBuilder.setSniffAfterFailureDelayMillis(config.getSniffer().getSniffAfterFailureDelayMillis())
                    .build();
        }
        return sniffer;
    }
}
