package org.opensearch.plugin.searchhistory;

import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.IndexScopedSettings;
import org.opensearch.common.settings.Setting;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.SettingsFilter;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class SearchHistoryPlugin extends Plugin implements ActionPlugin {

    public static final Setting<Integer> HISTORY_MAX_SIZE = Setting.intSetting(
            "search.history.max_size", 100, 1, Setting.Property.Dynamic, Setting.Property.NodeScope);
    
    public static final Setting<Integer> HISTORY_RETENTION_DAYS = Setting.intSetting(
            "search.history.retention_days", 30, 1, Setting.Property.Dynamic, Setting.Property.NodeScope);

    @Override
    public List<Setting<?>> getSettings() {
        return Arrays.asList(HISTORY_MAX_SIZE, HISTORY_RETENTION_DAYS);
    }

    @Override
    public List<RestHandler> getRestHandlers(
            Settings settings,
            RestController restController,
            ClusterSettings clusterSettings,
            IndexScopedSettings indexScopedSettings,
            SettingsFilter settingsFilter,
            IndexNameExpressionResolver indexNameExpressionResolver,
            Supplier<DiscoveryNodes> nodesInCluster) {
        
        return Arrays.asList(
            new RestSearchHistoryAction(),
            new RestSaveSearchHistoryAction(),
            new RestDeleteSearchHistoryAction()
        );
    }
}
