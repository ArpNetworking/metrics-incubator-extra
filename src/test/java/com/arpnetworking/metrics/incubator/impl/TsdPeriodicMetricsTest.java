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

import com.arpnetworking.metrics.Counter;
import com.arpnetworking.metrics.Metrics;
import com.arpnetworking.metrics.MetricsFactory;
import com.arpnetworking.metrics.Units;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * Tests for the {@link TsdPeriodicMetrics} class.
 *
 * @author Brandon Arp (brandon dot arp at inscopemetrics dot com)
 */
public class TsdPeriodicMetricsTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    public void testWarnsOnNullFactory() throws Exception {
        final Logger logger = Mockito.mock(Logger.class);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder(logger)
                .setMetricsFactory(null)
                .build();
        Mockito.verify(logger).warn(Mockito.anyString());
    }

    @Test
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    public void testWarnsOnNullExecutor() throws Exception {
        final Logger logger = Mockito.mock(Logger.class);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder(logger)
                .setMetricsFactory(_factory)
                .setPollingExecutor(null)
                .build();
        Mockito.verify(logger).warn(Mockito.anyString());
    }

    @Test
    public void testCallsFactoryCreateForInitialMetricInstance() throws Exception {
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();
        Mockito.verify(_factory).create();
    }

    @Test
    public void testRecordCounter() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        final Counter counterMock = Mockito.mock(Counter.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock);
        Mockito.when(metricsMock.createCounter(Mockito.anyString())).thenReturn(counterMock);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();

        final String name = "foo";
        final int value = 1;

        factory.recordCounter(name, value);
        Mockito.verify(metricsMock).createCounter(name);
        Mockito.verify(counterMock).increment(value);
    }

    @Test
    public void testRecordTimerUnit() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();

        final String name = "foo";
        final long value = 1;

        factory.recordTimer(name, value, Optional.of(Units.MILLISECOND));
        Mockito.verify(metricsMock).setTimer(name, value, Units.MILLISECOND);
    }

    @Test
    public void testRecordGaugeLongNoUnit() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();

        final String name = "foo";
        final long value = 1;

        factory.recordGauge(name, value);
        Mockito.verify(metricsMock).setGauge(name, value);
    }

    @Test
    public void testRecordGaugeLongUnit() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();

        final String name = "foo";
        final long value = 1;

        factory.recordGauge(name, value, Optional.of(Units.BIT));
        Mockito.verify(metricsMock).setGauge(name, value, Units.BIT);
    }

    @Test
    public void testRecordGaugeDoubleNoUnit() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();

        final String name = "foo";
        final double value = 1;

        factory.recordGauge(name, value);
        Mockito.verify(metricsMock).setGauge(name, value);
    }

    @Test
    public void testRecordGaugeDoubleUnit() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();

        final String name = "foo";
        final double value = 1;

        factory.recordGauge(name, value, Optional.of(Units.BIT));
        Mockito.verify(metricsMock).setGauge(name, value, Units.BIT);
    }

    @Test
    public void testCallsRegisteredMetrics() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        final Metrics newMetricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock, newMetricsMock);
        Mockito.when(metricsMock.createCounter(Mockito.anyString())).thenAnswer(Answers.RETURNS_MOCKS.get());
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();

        factory.registerPolledMetric(metrics -> metrics.recordCounter("bar", 1));
        factory.run();
        Mockito.verify(metricsMock).createCounter("bar");
        Mockito.verify(metricsMock).close();
        factory.run();
        Mockito.verifyNoMoreInteractions(metricsMock);
        Mockito.verify(newMetricsMock).createCounter("bar");
        Mockito.verify(newMetricsMock).close();
    }

    @Test
    public void testRegisterManyCallbacks() throws Exception {
        final Metrics metricsMock = Mockito.mock(Metrics.class);
        final Metrics newMetricsMock = Mockito.mock(Metrics.class);
        Mockito.when(_factory.create()).thenReturn(metricsMock, newMetricsMock);
        final TsdPeriodicMetrics factory = new TsdPeriodicMetrics.Builder()
                .setMetricsFactory(_factory)
                .build();

        for (int x = 0; x < 1000; x++) {
            final Integer val = x;
            factory.registerPolledMetric(metrics -> metrics.recordCounter("bar" + val, 1));
        }

    }

    @Mock(answer = Answers.RETURNS_MOCKS)
    private MetricsFactory _factory;

}
