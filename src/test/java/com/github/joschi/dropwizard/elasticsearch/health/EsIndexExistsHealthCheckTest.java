package com.github.joschi.dropwizard.elasticsearch.health;

import com.google.common.collect.ImmutableList;
import org.elasticsearch.client.Client;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link EsIndexExistsHealthCheck}.
 */
public class EsIndexExistsHealthCheckTest {
    @Test(expected = NullPointerException.class)
    public void initializationWithNullClientShouldFail() {
        new EsIndexExistsHealthCheck(null, "index");
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationWithoutIndicesShouldFail() {
        new EsIndexExistsHealthCheck(mock(Client.class), Collections.<String>emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void initializationWithoutIndexShouldFail() {
        new EsIndexExistsHealthCheck(mock(Client.class), (String) null);
    }

    @Test
    public void initializationWithClientAndIndexShouldSucceed() {
        new EsIndexExistsHealthCheck(mock(Client.class), "index");
    }

    @Test
    public void initializationWithClientAndIndicesShouldSucceed() {
        new EsIndexExistsHealthCheck(mock(Client.class), ImmutableList.of("index", "foobar"));
    }
}
