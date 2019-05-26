package io.dropwizard.elasticsearch.managed;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.validation.Validation;
import javax.validation.Validator;

import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.elasticsearch.config.EsConfiguration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.lifecycle.Managed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ManagedEsClient}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ManagedEsClientTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ConfigurationFactory<EsConfiguration> configFactory =
            new DefaultConfigurationFactoryFactory<EsConfiguration>()
                    .create(EsConfiguration.class, validator, Jackson.newObjectMapper(), "dw");

    @Test(expected = NullPointerException.class)
    public void ensureEsConfigurationIsNotNull() {
        new ManagedEsClient((EsConfiguration) null);
    }

    @Test(expected = NullPointerException.class)
    public void ensureClientIsNotNull() {
        new ManagedEsClient((RestHighLevelClient) null);
    }

    @Test
    public void stopShouldCloseTheClient() throws Exception {
        RestHighLevelClient client =
                mock(RestHighLevelClient.class);
        Managed managed = new ManagedEsClient(client);
        // to stub a final method it is necessary to have the
        //  /src/test/resources/mockito-extensions/org.mockiot.plugins.MockMaker     file
        // with `mock-maker-inline` as context
        doNothing().when(client).close();
        assertNotNull(client);
        managed.start();
        managed.stop();
        verify(client).close();
        assertNotNull(client);
    }

    @Test
    public void highLevelRestClientShouldBeCreatedFromConfig() throws URISyntaxException, IOException, ConfigurationException {
        URL configFileUrl = this.getClass().getResource("/rest_client.yml");
        File configFile = new File(configFileUrl.toURI());
        EsConfiguration config = configFactory.build(configFile);

        ManagedEsClient managedEsClient = new ManagedEsClient(config);
        RestHighLevelClient restHighLevelClient = managedEsClient.getClient();

        assertNotNull(restHighLevelClient);

        assertEquals(3, restHighLevelClient.getLowLevelClient().getNodes().size());
        assertEquals(
                "127.0.0.1",
                restHighLevelClient.getLowLevelClient().getNodes().get(0).getHost().getHostName());

        assertEquals(
                9200,
                restHighLevelClient.getLowLevelClient().getNodes().get(0).getHost().getPort());
        assertEquals(
                "127.0.0.1",
                restHighLevelClient.getLowLevelClient().getNodes().get(1).getHost().getHostName());

        assertEquals(
                9201,
                restHighLevelClient.getLowLevelClient().getNodes().get(1).getHost().getPort());
        assertEquals(
                "127.0.0.1",
                restHighLevelClient.getLowLevelClient().getNodes().get(2).getHost().getHostName());

        assertEquals(
                9202,
                restHighLevelClient.getLowLevelClient().getNodes().get(2).getHost().getPort());
    }
}
