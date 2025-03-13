package org.opensearch.plugin.randomsort;

import org.opensearch.plugins.Plugin;
import org.opensearch.plugins.ScriptPlugin;
import org.opensearch.script.ScriptContext;
import org.opensearch.script.ScriptEngine;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class RandomSortPlugin extends Plugin implements ScriptPlugin {

    @Override
    public ScriptEngine getScriptEngine(Settings settings, Collection<ScriptContext<?>> contexts) {
        return new RandomSortScriptEngine();
    }
    
    @Override
    public String name() {
        return "random-sort";
    }

    @Override
    public String description() {
        return "Plugin that provides random sorting capability for search results";
    }
}
