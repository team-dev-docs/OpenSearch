/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.cluster.routing;

import org.opensearch.cluster.ClusterState;
import org.opensearch.common.Priority;
import org.opensearch.common.annotation.PublicApi;
import org.opensearch.core.action.ActionListener;

/**
 * Asynchronously performs a cluster reroute, updating any shard states and rebalancing the cluster if appropriate.
 *
 * @opensearch.api
 */
@PublicApi(since = "1.0.0")
public interface RerouteService {

    /**
     * Schedule a cluster reroute.
     * @param priority the (minimum) priority at which to run this reroute. If there is already a pending reroute at a higher priority then
     *                 this reroute is batched with the pending one; if there is already a pending reroute at a lower priority then
     *                 the priority of the pending batch is raised to the given priority.
     */
    void reroute(String reason, Priority priority, ActionListener<ClusterState> listener);

    /**
     * New functionality to support the new feature.
     */
    void newFunctionality();
}
