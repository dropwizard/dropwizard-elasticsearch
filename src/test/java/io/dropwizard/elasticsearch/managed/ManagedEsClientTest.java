package io.dropwizard.elasticsearch.managed;

import io.dropwizard.elasticsearch.config.EsConfiguration;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.lifecycle.Managed;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.node.Node;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ManagedEsClient}.
 */
public class ManagedEsClientTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ConfigurationFactory<EsConfiguration> configFactory =
            new ConfigurationFactory<>(EsConfiguration.class, validator, Jackson.newObjectMapper(), "dw");

    @Test(expected = NullPointerException.class)
    public void ensureEsConfigurationIsNotNull() {
        new ManagedEsClient((EsConfiguration) null);
    }

    @Test(expected = NullPointerException.class)
    public void ensureNodeIsNotNull() {
        new ManagedEsClient((Node) null);
    }

    @Test(expected = NullPointerException.class)
    public void ensureClientIsNotNull() {
        new ManagedEsClient((Client) null);
    }

    @Test
    public void stopShouldCloseTheClient() throws Exception {
        Client client = mock(Client.class);
        Managed managed = new ManagedEsClient(client);

        managed.start();
        managed.stop();

        verify(client).close();
    }

    @Test
    public void lifecycleMethodsShouldStartAndCloseTheNode() throws Exception {
        Node node = mock(Node.class);
        when(node.isClosed()).thenReturn(false);
        Managed managed = new ManagedEsClient(node);

        managed.start();
        managed.stop();

        verify(node).start();
        verify(node).close();
    }

    @Test
    public void managedEsClientWithNodeShouldReturnClient() throws Exception {
        Client client = mock(Client.class);
        Node node = mock(Node.class);
        when(node.client()).thenReturn(client);

        ManagedEsClient managed = new ManagedEsClient(node);

        assertSame(client, managed.getClient());
    }

    @Test
    public void nodeClientShouldBeCreatedFromConfig() throws URISyntaxException, IOException, ConfigurationException {
        URL configFileUrl = this.getClass().getResource("/node_client.yml");
        File configFile = new File(configFileUrl.toURI());
        EsConfiguration config = configFactory.build(configFile);

        ManagedEsClient managedEsClient = new ManagedEsClient(config);
        Client client = managedEsClient.getClient();

        assertNotNull(client);
        assertTrue(client instanceof NodeClient);
    }

    @Test
    public void transportClientShouldBeCreatedFromConfig() throws URISyntaxException, IOException, ConfigurationException {
        URL configFileUrl = this.getClass().getResource("/transport_client.yml");
        File configFile = new File(configFileUrl.toURI());
        EsConfiguration config = configFactory.build(configFile);

        ManagedEsClient managedEsClient = new ManagedEsClient(config);
        Client client = managedEsClient.getClient();

        assertNotNull(client);
        assertTrue(client instanceof TransportClient);
        assertEquals(3, ((TransportClient) client).transportAddresses().size());
    }
}
