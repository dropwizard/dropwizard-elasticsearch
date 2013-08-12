package com.github.joschi.dropwizard.elasticsearch.util;

import com.google.common.net.HostAndPort;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;

import java.util.List;

/**
 */
public class TransportAddressHelper {
    private static final int DEFAULT_PORT = 9300;

    public static TransportAddress fromHostAndPort(final HostAndPort hostAndPort) {
        return new InetSocketTransportAddress(hostAndPort.getHostText(), hostAndPort.getPortOrDefault(DEFAULT_PORT));
    }

    public static TransportAddress[] fromHostAndPorts(final List<HostAndPort> hostAndPorts) {
        if (hostAndPorts == null) {
            return new TransportAddress[0];
        } else {
            TransportAddress[] transportAddresses = new TransportAddress[hostAndPorts.size()];

            for (int i = 0; i < hostAndPorts.size(); i++) {
                transportAddresses[i] = fromHostAndPort(hostAndPorts.get(i));
            }

            return transportAddresses;
        }
    }
}
