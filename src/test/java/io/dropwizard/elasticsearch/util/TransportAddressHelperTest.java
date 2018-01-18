package io.dropwizard.elasticsearch.util;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;
import org.elasticsearch.common.transport.TransportAddress;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link TransportAddressHelper}.
 */
public class TransportAddressHelperTest {

    private static final int ES_DEFAULT_PORT = 9300;

    @Test(expected = NullPointerException.class)
    public void fromHostAndPortWithNullShouldFail() {
        TransportAddressHelper.fromHostAndPort(null);
    }

    @Test
    public void fromHostAndPortsWithNullShouldReturnEmptyArray() {
        TransportAddress[] result = TransportAddressHelper.fromHostAndPorts(null);

        assertEquals(0, result.length);
    }

    @Test
    public void fromHostAndPortsWithEmptyListShouldReturnEmptyArray() {
        TransportAddress[] result = TransportAddressHelper.fromHostAndPorts(Collections.<HostAndPort>emptyList());

        assertEquals(0, result.length);
    }

    @Test
    public void fromHostAndPortWithoutPortShouldUseDefaultPort() {
        TransportAddress result = TransportAddressHelper.fromHostAndPort(HostAndPort.fromString("localhost"));

        assertEquals("localhost", result.address().getHostName());
        assertEquals(ES_DEFAULT_PORT, result.address().getPort());
    }

    @Test
    public void fromHostAndPortWithCorrectDataShouldSucceed() {
        TransportAddress result = TransportAddressHelper.fromHostAndPort(HostAndPort.fromParts("localhost", 1234));

        assertEquals("localhost", result.address().getHostName());
        assertEquals(1234, result.address().getPort());
    }

    @Test
    public void fromHostAndPostWithCorrectDataShouldSucceed() {
        final List<HostAndPort> hostAndPorts = ImmutableList.of(
                HostAndPort.fromParts("example.net", 1234),
                HostAndPort.fromParts("example.com", 5678),
                HostAndPort.fromString("example.org")
        );
        final TransportAddress[] result = TransportAddressHelper.fromHostAndPorts(hostAndPorts);

        assertEquals(3, result.length);

        for (int i = 0; i < result.length; i++) {
            final TransportAddress transportAddress = result[i];
            assertEquals(hostAndPorts.get(i).getHost(), transportAddress.address().getHostName());
            assertEquals(hostAndPorts.get(i).getPortOrDefault(ES_DEFAULT_PORT), transportAddress.address().getPort());
        }
    }
}
