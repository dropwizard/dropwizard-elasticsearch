package io.dropwizard.elasticsearch.health;

import org.elasticsearch.client.Client;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link EsClusterHealthCheck}
 */
public class EsClusterHealthCheckTest {
    @Test(expected = NullPointerException.class)
    public void initializationWithNullClientShouldFail() {
        new EsClusterHealthCheck(null);
    }

    @Test
    public void initializationWithClientShouldSucceed() {
        new EsClusterHealthCheck(mock(Client.class));
    }
}
