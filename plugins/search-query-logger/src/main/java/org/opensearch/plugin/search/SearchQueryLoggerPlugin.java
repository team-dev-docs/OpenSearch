/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.plugin.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.node.NodeClient;
import org.opensearch.common.settings.Setting;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.util.FeatureFlags;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.search.SearchQueryLogger;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Plugin that adds search query logging functionality to OpenSearch.
 *
 * @opensearch.plugin
 */
public class SearchQueryLoggerPlugin extends Plugin implements ActionPlugin {
    private static final Logger logger = LogManager.getLogger(SearchQueryLoggerPlugin.class);
    
    private final Settings settings;
    private SearchQueryLogger searchQueryLogger;
    
    public SearchQueryLoggerPlugin(Settings settings) {
        this.settings = settings;
        if (FeatureFlags.isEnabled(FeatureFlags.SEARCH_QUERY_LOGGING)) {
            logger.info("Search query logging feature is enabled");
            this.searchQueryLogger = new SearchQueryLogger(settings);
        } else {
            logger.debug("Search query logging feature is disabled");
        }
    }

    @Override
    public List<Setting<?>> getSettings() {
        return Arrays.asList(
            SearchQueryLogger.MIN_QUERY_TIME_TO_LOG_SETTING,
            FeatureFlags.SEARCH_QUERY_LOGGING_SETTING
        );
    }
    
    @Override
    public List<ActionPlugin.ActionHandler<?, ?>> getActions() {
        return Arrays.asList(
            new ActionPlugin.ActionHandler<>(
                SearchAction.INSTANCE,
                (ActionPlugin.TransportAction) (request, listener, client) -> {
                    long startTime = System.currentTimeMillis();
                    
                    // Call the original search action
                    ((NodeClient) client).search((SearchRequest) request, ActionPlugin.wrapActionListener(
                        SearchAction.INSTANCE,
                        (SearchRequest) request,
                        listener,
                        (r) -> {
                            if (searchQueryLogger != null) {
                                // Log the search query and its response
                                long tookInMillis = System.currentTimeMillis() - startTime;
                                searchQueryLogger.logQuery((SearchRequest) request, (SearchResponse) r, tookInMillis);
                            }
                            return r;
                        }
                    ));
                }
            )
        );
    }
}
