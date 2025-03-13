package org.opensearch.plugin.searchhistory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.ActionListener;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.create.CreateIndexResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.Client;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SearchHistoryService {
    private static final Logger logger = LogManager.getLogger(SearchHistoryService.class);
    private static final String HISTORY_INDEX = ".search_history";
    
    private final Client client;
    
    public SearchHistoryService(Client client) {
        this.client = client;
        ensureHistoryIndexExists();
    }
    
    private void ensureHistoryIndexExists() {
        client.admin().indices().exists(r -> r.indices(HISTORY_INDEX), ActionListener.wrap(
            exists -> {
                if (!exists) {
                    CreateIndexRequest request = new CreateIndexRequest(HISTORY_INDEX);
                    Map<String, Object> mappings = new HashMap<>();
                    // Add mappings here
                    
                    client.admin().indices().create(request, ActionListener.wrap(
                        response -> logger.info("Created search history index"),
                        e -> logger.error("Failed to create search history index", e)
                    ));
                }
            },
            e -> logger.error("Failed to check if search history index exists", e)
        ));
    }
    
    public void saveSearchHistory(String userId, String query, List<String> indices, int hitCount, ActionListener<IndexResponse> listener) {
        Map<String, Object> source = new HashMap<>();
        source.put("user_id", userId);
        source.put("query", query);
        source.put("indices", indices);
        source.put("hit_count", hitCount);
        source.put("timestamp", System.currentTimeMillis());
        
        IndexRequest indexRequest = new IndexRequest(HISTORY_INDEX)
                .source(source, XContentType.JSON);
        
        client.index(indexRequest, listener);
    }
    
    public void getSearchHistory(String userId, int from, int size, ActionListener<List<SearchHistoryEntry>> listener) {
        SearchRequest searchRequest = new SearchRequest(HISTORY_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("user_id", userId));
                
        searchSourceBuilder.query(boolQuery)
                .from(from)
                .size(size)
                .sort("timestamp", SortOrder.DESC);
                
        searchRequest.source(searchSourceBuilder);
        
        client.search(searchRequest, ActionListener.wrap(
            response -> {
                List<SearchHistoryEntry> entries = new ArrayList<>();
                for (SearchHit hit : response.getHits().getHits()) {
                    Map<String, Object> source = hit.getSourceAsMap();
                    
                    SearchHistoryEntry entry = new SearchHistoryEntry();
                    entry.setId(hit.getId());
                    entry.setQuery((String) source.get("query"));
                    entry.setTimestamp((Long) source.get("timestamp"));
                    entry.setIndices((List<String>) source.get("indices"));
                    entry.setHitCount((Integer) source.get("hit_count"));
                    
                    entries.add(entry);
                }
                
                listener.onResponse(entries);
            },
            listener::onFailure
        ));
    }
}
