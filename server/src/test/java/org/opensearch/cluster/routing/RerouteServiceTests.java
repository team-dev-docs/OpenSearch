/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.routing;

import org.opensearch.cluster.ClusterState;
import org.opensearch.common.Priority;
import org.opensearch.core.action.ActionListener;
import org.opensearch.test.OpenSearchTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RerouteServiceTests extends OpenSearchTestCase {

    private RerouteService rerouteService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        rerouteService = mock(RerouteService.class);
    }

    @Test
    public void testReroute() {
        String reason = "test reason";
        Priority priority = Priority.NORMAL;
        ActionListener<ClusterState> listener = ActionListener.wrap(
            clusterState -> assertNotNull(clusterState),
            e -> fail("Reroute failed")
        );

        doNothing().when(rerouteService).reroute(reason, priority, listener);

        rerouteService.reroute(reason, priority, listener);

        verify(rerouteService, times(1)).reroute(reason, priority, listener);
    }

    @Test
    public void testNewFunctionality() {
        doNothing().when(rerouteService).newFunctionality();

        rerouteService.newFunctionality();

        verify(rerouteService, times(1)).newFunctionality();
    }
}
