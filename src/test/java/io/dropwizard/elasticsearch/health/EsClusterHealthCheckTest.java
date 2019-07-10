package io.dropwizard.elasticsearch.health;

import com.codahale.metrics.health.HealthCheck;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.ClusterClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        new EsClusterHealthCheck(mock(RestHighLevelClient.class));
    }

    @Test
    public void canHaveHealthyResultsWithFormattedMessage() throws Exception {
        RestHighLevelClient client = mock(RestHighLevelClient.class);
        ClusterClient clusterClient = mock(ClusterClient.class);
        EsClusterHealthCheck healthCheck = new EsClusterHealthCheck(client);
        ClusterHealthResponse response = mock(ClusterHealthResponse.class);

        when(client.cluster()).thenReturn(clusterClient);
        when(clusterClient.health(any(ClusterHealthRequest.class), any(RequestOptions.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(ClusterHealthStatus.GREEN);
        HealthCheck.Result result = healthCheck.check();

        assertTrue(result.isHealthy());
        assertEquals(result.getMessage(), "Last status: GREEN");
    }

    @Test
    public void canHaveUnHealthyResultsWithFormattedMessage() throws Exception {
        RestHighLevelClient client = mock(RestHighLevelClient.class);
        ClusterClient clusterClient = mock(ClusterClient.class);
        EsClusterHealthCheck healthCheck = new EsClusterHealthCheck(client);
        ClusterHealthResponse response = mock(ClusterHealthResponse.class);

        when(client.cluster()).thenReturn(clusterClient);
        when(clusterClient.health(any(ClusterHealthRequest.class), any(RequestOptions.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(ClusterHealthStatus.RED);
        HealthCheck.Result result = healthCheck.check();

        assertFalse(result.isHealthy());
        assertEquals(result.getMessage(), "Last status: RED");
    }

    @Test
    public void canHaveUnHealthyResultsOnYellowWithFormattedMessage() throws Exception {
        RestHighLevelClient client = mock(RestHighLevelClient.class);
        ClusterClient clusterClient = mock(ClusterClient.class);
        EsClusterHealthCheck healthCheck = new EsClusterHealthCheck(client, true);
        ClusterHealthResponse response = mock(ClusterHealthResponse.class);

        when(client.cluster()).thenReturn(clusterClient);
        when(clusterClient.health(any(ClusterHealthRequest.class), any(RequestOptions.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(ClusterHealthStatus.YELLOW);
        HealthCheck.Result result = healthCheck.check();

        assertFalse(result.isHealthy());
        assertEquals(result.getMessage(), "Last status: YELLOW");
    }
}
