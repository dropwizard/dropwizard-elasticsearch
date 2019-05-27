package io.dropwizard.elasticsearch.health;

import com.google.common.collect.ImmutableList;

import com.codahale.metrics.health.HealthCheck;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EsIndexDocsHealthCheck}.
 */
public class EsIndexDocsHealthCheckTest {
    @Test(expected = NullPointerException.class)
    public void initializationWithNullClientShouldFail() {
        new EsIndexDocsHealthCheck(null, "index");
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationWithoutIndicesShouldFail() {
        new EsIndexDocsHealthCheck(mock(RestHighLevelClient.class), Collections.<String>emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void initializationWithoutIndexShouldFail() {
        new EsIndexDocsHealthCheck(mock(RestHighLevelClient.class), (String) null);
    }

    @Test
    public void initializationWithClientAndIndicesShouldSucceed() {
        new EsIndexDocsHealthCheck(mock(RestHighLevelClient.class), ImmutableList.of("index", "foobar"));
    }

    @Test
    public void initializationWithClientAndIndexShouldSucceed() {
        new EsIndexDocsHealthCheck(mock(RestHighLevelClient.class), "index");
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationWithDocumentThresholdTooLowShouldFail() {
        new EsIndexDocsHealthCheck(mock(RestHighLevelClient.class), "index", 0L);
    }

    @Test
    public void initializationWithValidParametersShouldSucceedl() {
        new EsIndexDocsHealthCheck(mock(RestHighLevelClient.class), "index", 10L);
    }


    @Test
    public void canHaveHealthyResultsWithFormattedMessage() throws Exception {
        RestHighLevelClient client = mock(RestHighLevelClient.class);
        EsIndexDocsHealthCheck healthCheck = new EsIndexDocsHealthCheck(client, "index");
        CountResponse countResponse = mock(CountResponse.class);
        when(client.count(any(CountRequest.class), any(RequestOptions.class))).thenReturn(countResponse);
        when(countResponse.getCount()).thenReturn(10L);
        HealthCheck.Result result = healthCheck.check();
        assertTrue(result.isHealthy());
        assertEquals(result.getMessage(), "Last stats: [index (10!)]");
    }

    @Test
    public void canHaveUnhealthyResultsWithFormattedMessage() throws Exception {
        RestHighLevelClient client = mock(RestHighLevelClient.class);
        EsIndexDocsHealthCheck healthCheck = new EsIndexDocsHealthCheck(client, "index");
        CountResponse countResponse = mock(CountResponse.class);
        when(client.count(any(CountRequest.class), any(RequestOptions.class))).thenReturn(countResponse);
        when(countResponse.getCount()).thenReturn(0L);
        HealthCheck.Result result = healthCheck.check();
        assertFalse(result.isHealthy());
        assertEquals(result.getMessage(), "Last stats: [index (0)]");
    }
}
