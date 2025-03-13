/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.search;

import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.common.util.FeatureFlags;
import org.opensearch.search.internal.InternalSearchResponse;
import org.opensearch.test.FeatureFlagSetter;
import org.opensearch.test.OpenSearchTestCase;

import static org.mockito.Mockito.*;

public class SearchQueryLoggerTests extends OpenSearchTestCase {

    public void testSearchQueryLoggerDisabled() {
        Settings settings = Settings.builder()
            .put(SearchQueryLogger.MIN_QUERY_TIME_TO_LOG_SETTING.getKey(), "100ms")
            .build();
        
        SearchQueryLogger logger = new SearchQueryLogger(settings);
        
        // The feature is disabled by default, so this should not log anything
        SearchRequest request = mock(SearchRequest.class);
        SearchResponse response = mock(SearchResponse.class);
        
        // This should not throw an exception
        logger.logQuery(request, response, 150);
    }
    
    public void testSearchQueryLoggerEnabled() {
        try {
            // Enable the feature flag
            FeatureFlagSetter.set(FeatureFlags.SEARCH_QUERY_LOGGING);
            
            Settings settings = Settings.builder()
                .put(SearchQueryLogger.MIN_QUERY_TIME_TO_LOG_SETTING.getKey(), "100ms")
                .build();
            
            SearchQueryLogger logger = new SearchQueryLogger(settings);
            
            // Create mocks for request and response
            SearchRequest request = mock(SearchRequest.class);
            when(request.indices()).thenReturn(new String[] { "test_index" });
            
            SearchResponse response = mock(SearchResponse.class);
            when(response.getInternalResponse()).thenReturn(mock(InternalSearchResponse.class));
            
            // Test with query time below threshold
            logger.logQuery(request, response, 50);
            
            // Test with query time above threshold
            logger.logQuery(request, response, 150);
        } finally {
            // Clean up by removing feature flag
            FeatureFlagSetter.clear(FeatureFlags.SEARCH_QUERY_LOGGING);
        }
    }
}
