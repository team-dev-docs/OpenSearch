package org.opensearch.plugin.searchhistory;

import org.opensearch.client.node.NodeClient;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestResponse;
import org.opensearch.rest.RestStatus;
import org.opensearch.rest.action.RestBuilderListener;
import org.opensearch.search.SearchHit;

import java.io.IOException;
import java.util.List;

import static org.opensearch.rest.RestRequest.Method.GET;

public class RestSearchHistoryAction extends BaseRestHandler {

    @Override
    public String getName() {
        return "search_history_action";
    }

    @Override
    public List<Route> routes() {
        return List.of(new Route(GET, "/_search_history"));
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        final int from = request.paramAsInt("from", 0);
        final int size = request.paramAsInt("size", 20);
        final String userId = getUser(request);

        return channel -> {
            SearchHistoryService service = new SearchHistoryService(client);
            service.getSearchHistory(userId, from, size, new RestBuilderListener<>(channel) {
                @Override
                public RestResponse buildResponse(List<SearchHistoryEntry> historyEntries, XContentBuilder builder) throws Exception {
                    builder.startObject();
                    builder.startArray("history");
                    
                    for (SearchHistoryEntry entry : historyEntries) {
                        builder.startObject();
                        builder.field("id", entry.getId());
                        builder.field("query", entry.getQuery());
                        builder.field("timestamp", entry.getTimestamp());
                        builder.field("indices", entry.getIndices());
                        builder.field("hit_count", entry.getHitCount());
                        builder.endObject();
                    }
                    
                    builder.endArray();
                    builder.endObject();
                    return new BytesRestResponse(RestStatus.OK, builder);
                }
            });
        };
    }
    
    private String getUser(RestRequest request) {
        // In a real implementation, this would extract the authenticated user
        // For now, returning a placeholder
        return "current_user";
    }
}
