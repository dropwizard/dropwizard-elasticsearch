package com.github.joschi.dropwizard.elasticsearch.health;

import com.google.common.collect.ImmutableList;
import org.elasticsearch.client.Client;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;

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
        new EsIndexDocsHealthCheck(mock(Client.class), Collections.<String>emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void initializationWithoutIndexShouldFail() {
        new EsIndexDocsHealthCheck(mock(Client.class), (String) null);
    }

    @Test
    public void initializationWithClientAndIndicesShouldSucceed() {
        new EsIndexDocsHealthCheck(mock(Client.class), ImmutableList.of("index", "foobar"));
    }

    @Test
    public void initializationWithClientAndIndexShouldSucceed() {
        new EsIndexDocsHealthCheck(mock(Client.class), "index");
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationWithDocumentThresholdTooLowShouldFail() {
        new EsIndexDocsHealthCheck(mock(Client.class), "index", 0L);
    }

    @Test
    public void initializationWithValidParametersShouldSucceedl() {
        new EsIndexDocsHealthCheck(mock(Client.class), "index", 10L);
    }
}
