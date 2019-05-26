package io.dropwizard.elasticsearch.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.collect.ImmutableList;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link HealthCheck} which checks if one or more indices in Elasticsearch contain a given number of documents
 * in their primaries.
 *
 * @see <a href="http://www.elasticsearch.org/guide/reference/api/admin-indices-stats/">Admin Indices Stats</a>
 */
public class EsIndexDocsHealthCheck extends HealthCheck {
    private static final String HEALTH_CHECK_NAME = "elasticsearch-index-documents";
    private static final long DEFAULT_DOCUMENT_THRESHOLD = 1L;
    private final RestHighLevelClient client;
    private final String[] indices;
    private final long documentThreshold;

    /**
     * Construct a new Elasticsearch index document count health check.
     *
     * @param client            an Elasticsearch {@link Client} instance connected to the cluster
     * @param indices           a {@link List} of indices in Elasticsearch which should be checked
     * @param documentThreshold the minimal number of documents in an index
     * @throws IllegalArgumentException if {@code indices} was {@literal null} or empty,
     *                                  or {@code documentThreshold} was less than 1
     */
    public EsIndexDocsHealthCheck(RestHighLevelClient client, List<String> indices, long documentThreshold) {
        checkArgument(!indices.isEmpty(), "At least one index must be given");
        checkArgument(documentThreshold > 0L, "The document threshold must at least be 1");

        this.client = checkNotNull(client);
        this.indices = checkNotNull(indices.toArray(new String[indices.size()]));
        this.documentThreshold = documentThreshold;
    }


    /**
     * Construct a new Elasticsearch index document count health check.
     *
     * @param client  an Elasticsearch {@link Client} instance connected to the cluster
     * @param indices a {@link List} of indices in Elasticsearch which should be checked
     */
    public EsIndexDocsHealthCheck(RestHighLevelClient client, List<String> indices) {
        this(client, indices, DEFAULT_DOCUMENT_THRESHOLD);
    }

    /**
     * Construct a new Elasticsearch index document count health check.
     *
     * @param client            an Elasticsearch {@link Client} instance connected to the cluster
     * @param indexName         the index in Elasticsearch which should be checked
     * @param documentThreshold the minimal number of documents in an index
     */
    public EsIndexDocsHealthCheck(RestHighLevelClient client, String indexName, long documentThreshold) {
        this(client, ImmutableList.of(indexName), documentThreshold);
    }

    /**
     * Construct a new Elasticsearch index document count health check.
     *
     * @param client    an Elasticsearch {@link Client} instance connected to the cluster
     * @param indexName the index in Elasticsearch which should be checked
     */
    public EsIndexDocsHealthCheck(RestHighLevelClient client, String indexName) {
        this(client, indexName, DEFAULT_DOCUMENT_THRESHOLD);
    }

    /**
     * Perform a check of the number of documents in the Elasticsearch indices.
     *
     * @return if the Elasticsearch indices contain the minimal number of documents, a healthy
     *         {@link com.codahale.metrics.health.HealthCheck.Result}; otherwise, an unhealthy
     *         {@link com.codahale.metrics.health.HealthCheck.Result} with a descriptive error message or exception
     * @throws Exception if there is an unhandled error during the health check; this will result in
     *                   a failed health check
     */
    @Override
    protected Result check() throws Exception {
        final List<String> indexDetails = new ArrayList<String>(indices.length);
        boolean healthy = true;

        for (String index: indices) {
            long documentCount = count(index);
            if (documentCount < documentThreshold) {
                healthy = false;
                indexDetails.add(String.format("%s (%d)", index, documentCount));
            } else {
                indexDetails.add(String.format("%s (%d!)", index, documentCount));
            }
        }
        final String resultDetails = String.format("Last stats: %s", indexDetails);
        if (healthy) {
            return Result.healthy(resultDetails);
        } else {
            return Result.unhealthy(resultDetails);
        }
    }

    private long count(String index) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        CountRequest countRequest = new CountRequest().source(searchSourceBuilder);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        return countResponse.getCount();
    }
}
