package io.dropwizard.elasticsearch.health;

import com.google.common.collect.ImmutableList;

import com.codahale.metrics.health.HealthCheck;

import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EsIndexExistsHealthCheck}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EsIndexExistsHealthCheckTest {
    @Test(expected = NullPointerException.class)
    public void initializationWithNullClientShouldFail() {
        new EsIndexExistsHealthCheck(null, "index");
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationWithoutIndicesShouldFail() {
        new EsIndexExistsHealthCheck(mock(RestHighLevelClient.class), Collections.<String>emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void initializationWithoutIndexShouldFail() {
        new EsIndexExistsHealthCheck(mock(RestHighLevelClient.class), (String) null);
    }

    @Test
    public void initializationWithClientAndIndexShouldSucceed() {
        new EsIndexExistsHealthCheck(mock(RestHighLevelClient.class), "index");
    }

    @Test
    public void initializationWithClientAndIndicesShouldSucceed() {
        new EsIndexExistsHealthCheck(mock(RestHighLevelClient.class), ImmutableList.of("index", "foobar"));
    }

    @Test
    public void canHaveHealthyResults() throws Exception {
        RestHighLevelClient client = mock(RestHighLevelClient.class);
        IndicesClient indicesClient = mock(IndicesClient.class);
        EsIndexExistsHealthCheck healthCheck = new EsIndexExistsHealthCheck(client, "index");

        when(client.indices()).thenReturn(indicesClient);
        when(indicesClient.exists(any(GetIndexRequest.class), any(RequestOptions.class))).thenReturn(true);
        HealthCheck.Result result = healthCheck.check();
        assertTrue(result.isHealthy());
    }

    @Test
    public void canHaveUnhealthyResultsWithFormattedMessage() throws Exception {
        RestHighLevelClient client = mock(RestHighLevelClient.class);
        IndicesClient indicesClient = mock(IndicesClient.class);
        EsIndexExistsHealthCheck healthCheck = new EsIndexExistsHealthCheck(client, "index");

        when(client.indices()).thenReturn(indicesClient);
        when(indicesClient.exists(any(GetIndexRequest.class), any(RequestOptions.class))).thenReturn(false);
        HealthCheck.Result result = healthCheck.check();
        assertFalse(result.isHealthy());
        assertEquals(result.getMessage(), "One or more indices do not exist.");
    }
}
