package com.github.joschi.dropwizard.elasticsearch.health;

import org.junit.Test;

/**
 * Unit tests for {@link ElasticsearchClusterHealthCheck}
 */
public class ElasticsearchClusterHealthCheckTest {
    @Test(expected = NullPointerException.class)
    public void initializationWithNullClientShouldFail() {
        new ElasticsearchClusterHealthCheck(null);
    }
}
