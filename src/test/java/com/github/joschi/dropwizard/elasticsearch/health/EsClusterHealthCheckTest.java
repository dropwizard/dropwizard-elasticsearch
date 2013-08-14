package com.github.joschi.dropwizard.elasticsearch.health;

import org.junit.Test;

/**
 * Unit tests for {@link EsClusterHealthCheck}
 */
public class EsClusterHealthCheckTest {
    @Test(expected = NullPointerException.class)
    public void initializationWithNullClientShouldFail() {
        new EsClusterHealthCheck(null);
    }
}
