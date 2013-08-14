package com.github.joschi.dropwizard.elasticsearch.health;

import com.yammer.metrics.core.HealthCheck;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link HealthCheck} which checks the cluster state of an Elasticsearch cluster.
 *
 * @see <a href="http://www.elasticsearch.org/guide/reference/api/admin-cluster-health/">Admin Cluster Health</a>
 */
public class EsClusterHealthCheck extends HealthCheck {
    private final Client client;
    private final boolean failOnYellow;

    /**
     * Construct a new Elasticsearch cluster health check.
     *
     * @param name         the name of the health check. Useful if multiple clusters should be checked
     * @param client       an Elasticsearch {@link Client} instance connected to the cluster
     * @param failOnYellow whether the health check should fail if the cluster health state is yellow
     */
    public EsClusterHealthCheck(String name, Client client, boolean failOnYellow) {
        super(name);
        this.client = checkNotNull(client);
        this.failOnYellow = failOnYellow;
    }

    /**
     * Construct a new Elasticsearch cluster health check.
     *
     * @param client       an Elasticsearch {@link Client} instance connected to the cluster
     * @param failOnYellow whether the health check should fail if the cluster health state is yellow
     */
    public EsClusterHealthCheck(Client client, boolean failOnYellow) {
        this("elasticsearch-cluster", client, failOnYellow);
    }

    /**
     * Construct a new Elasticsearch cluster health check which will fail if the cluster health state is
     * {@link ClusterHealthStatus#RED}.
     *
     * @param client an Elasticsearch {@link Client} instance connected to the cluster
     */
    public EsClusterHealthCheck(Client client) {
        this(client, false);
    }

    /**
     * Perform a check of the Elasticsearch cluster health.
     *
     * @return if the Elasticsearch cluster is healthy, a healthy {@link com.yammer.metrics.core.HealthCheck.Result};
     *         otherwise, an unhealthy {@link com.yammer.metrics.core.HealthCheck.Result} with a descriptive error
     *         message or exception
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
