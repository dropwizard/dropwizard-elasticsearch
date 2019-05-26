package io.dropwizard.elasticsearch.health;

import com.google.common.collect.ImmutableList;

import org.elasticsearch.client.RestHighLevelClient;
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
}
