/**
 * Copyright 2017 Inscope Metrics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arpnetworking.metrics.incubator.impl;

import com.arpnetworking.metrics.Metrics;
import com.arpnetworking.metrics.MetricsFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Tests for the {@link TsdPeriodicMetricsFactory} class.
 *
 * @author Brandon Arp (brandon dot arp at inscopemetrics dot com)
 */
public class TsdPeriodicMetricsFactoryTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    public void testWarnsOnNullFactory() throws Exception {
        final Logger logger = Mockito.mock(Logger.class);
        final TsdPeriodicMetricsFactory factory = new TsdPeriodicMetricsFactory.Builder(logger)
                .setMetricsFactory(null)
                .setCloseExecutor(new ScheduledThreadPoolExecutor(1))
                .build();
        Mockito.verify(logger).warn(Mockito.anyString());
    }

    @Test
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    public void testWarnsOnNullExecutor() throws Exception {
        final Logger logger = Mockito.mock(Logger.class);
        final TsdPeriodicMetricsFactory factory = new TsdPeriodicMetricsFactory.Builder(logger)
                .setMetricsFactory(_factory)
                .setCloseExecutor(null)
                .build();
        Mockito.verify(logger).warn(Mockito.anyString());
    }

    @Test
    public void testCallsFactoryCreateForInitialMetricInstance() throws Exception {
        final TsdPeriodicMetricsFactory factory = new TsdPeriodicMetricsFactory.Builder()
                .setMetricsFactory(_factory)
                .build();
        Mockito.verify(_factory).create();
    }

    @Test
    public void testCallsConsumerWithMetrics() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock);
        final TsdPeriodicMetricsFactory factory = new TsdPeriodicMetricsFactory.Builder()
                .setMetricsFactory(_factory)
                .build();

        factory.record(metrics -> metrics.incrementCounter("foo"));
        Mockito.verify(metricsMock).incrementCounter("foo");
    }

    @Test
    public void testCallsRegisteredMetrics() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        final Metrics newMetricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock, newMetricsMock);
        final TsdPeriodicMetricsFactory factory = new TsdPeriodicMetricsFactory.Builder()
                .setMetricsFactory(_factory)
                .build();

        factory.registerPolledMetric(metrics -> metrics.incrementCounter("bar"));

        Mockito.verify(metricsMock, Mockito.timeout(1000)).incrementCounter("bar");
    }

    @Test
    public void testRegisterManyCallbacks() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        final Metrics newMetricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock, newMetricsMock);
        final TsdPeriodicMetricsFactory factory = new TsdPeriodicMetricsFactory.Builder()
                .setMetricsFactory(_factory)
                .build();

        for (int x = 0; x < 1000; x++) {
            final Integer val = x;
            factory.registerPolledMetric(metrics -> metrics.incrementCounter("bar" + val));
        }

    }

    @Mock(answer = Answers.RETURNS_MOCKS)
    private MetricsFactory _factory;

}
