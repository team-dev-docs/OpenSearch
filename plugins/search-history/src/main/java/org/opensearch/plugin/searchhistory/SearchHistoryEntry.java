package org.opensearch.plugin.searchhistory;

import java.util.List;

public class SearchHistoryEntry {
    private String id;
    private String query;
    private long timestamp;
    private List<String> indices;
    private int hitCount;
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public List<String> getIndices() {
        return indices;
    }
    
    public void setIndices(List<String> indices) {
        this.indices = indices;
    }
    
    public int getHitCount() {
        return hitCount;
    }
    
    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }
}
