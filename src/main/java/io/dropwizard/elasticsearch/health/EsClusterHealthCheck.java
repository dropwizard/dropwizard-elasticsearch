package io.dropwizard.elasticsearch.health;

import com.codahale.metrics.health.HealthCheck;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.health.ClusterHealthStatus;

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
     * @param client       an Elasticsearch {@link Client} instance connected to the cluster
     * @param failOnYellow whether the health check should fail if the cluster health state is yellow
     */
    public EsClusterHealthCheck(Client client, boolean failOnYellow) {
        this.client = checkNotNull(client);
        this.failOnYellow = failOnYellow;
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
     * @return if the Elasticsearch cluster is healthy, a healthy {@link com.codahale.metrics.health.HealthCheck.Result};
     *         otherwise, an unhealthy {@link com.codahale.metrics.health.HealthCheck.Result} with a descriptive error
     *         message or exception
     * @throws Exception if there is an unhandled error during the health check; this will result in
     *                   a failed health check
     */
    @Override
    protected Result check() throws Exception {
        final ClusterHealthStatus status = client.admin().cluster().prepareHealth().get().getStatus();

        if (status == ClusterHealthStatus.RED || (failOnYellow && status == ClusterHealthStatus.YELLOW)) {
            return Result.unhealthy("Last status: %s", status.name());
        } else {
            return Result.healthy("Last status: %s", status.name());
        }
    }
}
