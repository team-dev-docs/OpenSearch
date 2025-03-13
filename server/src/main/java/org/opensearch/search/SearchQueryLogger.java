/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.common.settings.Setting;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.common.util.FeatureFlags;

/**
 * Service for logging search queries and their metadata. This is gated by the SEARCH_QUERY_LOGGING feature flag.
 *
 * @opensearch.internal
 */
public class SearchQueryLogger {
    private static final Logger logger = LogManager.getLogger(SearchQueryLogger.class);
    
    /**
     * Setting to control the minimum query execution time to log
     */
    public static final Setting<TimeValue> MIN_QUERY_TIME_TO_LOG_SETTING = Setting.timeSetting(
        "opensearch.search.query.logging.min_time",
        TimeValue.timeValueMillis(0),
        Setting.Property.Dynamic,
        Setting.Property.NodeScope
    );
    
    private final boolean enabled;
    private final TimeValue minTimeToLog;
    
    public SearchQueryLogger(Settings settings) {
        this.enabled = FeatureFlags.isEnabled(FeatureFlags.SEARCH_QUERY_LOGGING);
        this.minTimeToLog = MIN_QUERY_TIME_TO_LOG_SETTING.get(settings);
    }
    
    /**
     * Logs a search request and its response if the feature is enabled and the query time meets the threshold
     *
     * @param request The search request
     * @param response The search response
     * @param tookInMillis Time taken to execute the query in ms
     */
    public void logQuery(SearchRequest request, SearchResponse response, long tookInMillis) {
        if (!enabled) {
            return;
        }
        
        if (tookInMillis < minTimeToLog.getMillis()) {
            return;
        }
        
        // Log the query details with timing information
        logger.info(
            "Search query executed: indices=[{}], query=[{}], took=[{}ms], hits=[{}], total_hits=[{}]",
            String.join(",", request.indices()),
            request.source() != null ? request.source().toString() : "N/A", 
            tookInMillis,
            response.getHits().getHits().length,
            response.getHits().getTotalHits().value
        );
    }
}
