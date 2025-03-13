package org.opensearch.plugin.randomsort;

import org.apache.lucene.index.LeafReaderContext;
import org.opensearch.common.settings.Settings;
import org.opensearch.script.ScoreScript;
import org.opensearch.script.ScoreScript.LeafFactory;
import org.opensearch.script.ScriptContext;
import org.opensearch.script.ScriptEngine;
import org.opensearch.script.ScriptFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class RandomSortScriptEngine implements ScriptEngine {
    private static final String SOURCE = "random_sort";
    private static final String LANG = "randomsort";

    @Override
    public String getType() {
        return LANG;
    }

    @Override
    public <T> T compile(String scriptName, String scriptSource, ScriptContext<T> context, Map<String, String> params) {
        if (!SOURCE.equals(scriptSource)) {
            throw new IllegalArgumentException("Unknown script name " + scriptSource);
        }
        if (context.equals(ScoreScript.CONTEXT) == false) {
            throw new IllegalArgumentException("Cannot use " + SOURCE + " in context [" + context.name + "]");
        }

        // This is the script factory for score script
        ScoreScript.Factory factory = new RandomScoreFactory();
        return context.factoryClazz.cast(factory);
    }

    @Override
    public void close() {
        // Nothing to close
    }

    private class RandomScoreFactory implements ScoreScript.Factory, ScriptFactory {
        @Override
        public boolean isResultDeterministic() {
            return false;
        }

        @Override
        public LeafFactory newFactory(Map<String, Object> params, SearchLookup lookup) {
            final long seed;
            // Get seed parameter or use current time
            if (params.containsKey("seed")) {
                seed = ((Number) params.get("seed")).longValue();
            } else {
                seed = System.currentTimeMillis();
            }

            return new LeafFactory() {
                @Override
                public ScoreScript newInstance(LeafReaderContext ctx) throws IOException {
                    final Random random = new Random(seed + ctx.docBase);
                    return new ScoreScript(params, lookup, ctx) {
                        @Override
                        public double execute(ExplanationHolder explanation) {
                            return random.nextDouble();
                        }
                    };
                }

                @Override
                public boolean needs_score() {
                    return false;
                }
            };
        }
    }
}
