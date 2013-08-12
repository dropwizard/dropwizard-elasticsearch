package com.github.joschi.dropwizard.elasticsearch.health;

import com.yammer.metrics.core.HealthCheck;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 */
public class ElasticsearchClusterHealthCheck extends HealthCheck {
    private final Client client;
    private final boolean failOnYellow;

    public ElasticsearchClusterHealthCheck(String name, Client client, boolean failOnYellow) {
        super(name);
        this.client = checkNotNull(client);
        this.failOnYellow = failOnYellow;
    }

    public ElasticsearchClusterHealthCheck(Client client, boolean failOnYellow) {
        this("elasticsearch-cluster", client, failOnYellow);
    }

    public ElasticsearchClusterHealthCheck(Client client) {
        this(client, false);
    }

    /**
     * Perform a check of the application component.
     *
     * @return if the component is healthy, a healthy {@link com.yammer.metrics.core.HealthCheck.Result}; otherwise, an unhealthy
     *         {@link com.yammer.metrics.core.HealthCheck.Result} with a descriptive error message or exception
     * @throws Exception if there is an unhandled error during the health check; this will result in
     *                   a failed health check
     */
    @Override
    protected Result check() throws Exception {
        final ClusterHealthResponse healthResponse = client.admin()
                .cluster()
                .prepareHealth()
                .setWaitForYellowStatus()
                .execute()
                .actionGet();
        final ClusterHealthStatus status = healthResponse.getStatus();

        if (status == ClusterHealthStatus.RED || (failOnYellow && status == ClusterHealthStatus.YELLOW)) {
            return Result.unhealthy("Last status: %s", healthResponse.getStatus().name());
        } else {
            return Result.healthy("Last status: %s", healthResponse.getStatus().name());
        }
    }
}
