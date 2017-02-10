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
import com.arpnetworking.metrics.Units;
import com.arpnetworking.metrics.incubator.impl.TsdPeriodicMetricsFactory.NonClosingMetrics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Tests for the {@link NonClosingMetrics} class.
 * @author Brandon Arp (brandon dot arp at inscopemetrics dot com)
 */
public class NonClosingMetricsTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConstruct() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        Assert.assertNotNull(nonClosingMetrics);
    }

    @Test
    public void testProxiesCreateCounter() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.createCounter("foo");
        Mockito.verify(_metrics).createCounter("foo");
    }

    @Test
    public void testProxiesIncrementCounter() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.incrementCounter("foo");
        Mockito.verify(_metrics).incrementCounter("foo");
    }

    @Test
    public void testProxiesIncrementCounterByAmount() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.incrementCounter("foo", 4);
        Mockito.verify(_metrics).incrementCounter("foo", 4);
    }

    @Test
    public void testProxiesDecrementCounter() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.decrementCounter("foo");
        Mockito.verify(_metrics).decrementCounter("foo");
    }

    @Test
    public void testProxiesDecrementCounterByAmount() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.decrementCounter("foo", 4);
        Mockito.verify(_metrics).decrementCounter("foo", 4);
    }

    @Test
    public void testProxiesResetCounter() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.resetCounter("foo");
        Mockito.verify(_metrics).resetCounter("foo");
    }

    @Test
    public void testProxiesCreateTimer() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.createTimer("foo");
        Mockito.verify(_metrics).createTimer("foo");
    }

    @Test
    public void testProxiesStartTimer() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.startTimer("foo");
        Mockito.verify(_metrics).startTimer("foo");
    }

    @Test
    public void testProxiesStopTimer() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.stopTimer("foo");
        Mockito.verify(_metrics).stopTimer("foo");
    }

    @Test
    public void testProxiesSetTimer() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.setTimer("foo", 100, TimeUnit.NANOSECONDS);
        Mockito.verify(_metrics).setTimer("foo", 100, TimeUnit.NANOSECONDS);
    }

    @Test
    public void testProxiesSetTimer2() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.setTimer("foo", 100, Units.NANOSECOND);
        Mockito.verify(_metrics).setTimer("foo", 100, Units.NANOSECOND);
    }

    @Test
    public void testProxiesSetGaugeDouble() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.setGauge("foo", 100d);
        Mockito.verify(_metrics).setGauge("foo", 100d);
    }

    @Test
    public void testProxiesSetGaugeDouble2() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.setGauge("foo", 100d, Units.BYTE);
        Mockito.verify(_metrics).setGauge("foo", 100d, Units.BYTE);
    }

    @Test
    public void testProxiesSetGaugeLong() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.setGauge("foo", 100);
        Mockito.verify(_metrics).setGauge("foo", 100);
    }

    @Test
    public void testProxiesSetGaugeLong2() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.setGauge("foo", 100, Units.BYTE);
        Mockito.verify(_metrics).setGauge("foo", 100, Units.BYTE);
    }

    @Test
    public void testProxiesAddAnnotation() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.addAnnotation("foo", "bar");
        Mockito.verify(_metrics).addAnnotation("foo", "bar");
    }

    @Test
    public void testProxiesAddAnnotations() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        final Map<String, String> values = Collections.emptyMap();
        nonClosingMetrics.addAnnotations(values);
        Mockito.verify(_metrics).addAnnotations(values);
    }

    @Test
    public void testProxiesIsOpen() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.isOpen();
        Mockito.verify(_metrics).isOpen();
    }

    @Test
    public void testDoesNotProxyClose() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.close();
        Mockito.verify(_metrics, Mockito.never()).close();
    }

    @Test
    public void testProxiesGetOpenTime() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.getOpenTime();
        Mockito.verify(_metrics).getOpenTime();
    }

    @Test
    public void testProxiesGetCloseTime() {
        final NonClosingMetrics nonClosingMetrics = new NonClosingMetrics(_metrics);
        nonClosingMetrics.getCloseTime();
        Mockito.verify(_metrics).getCloseTime();
    }

    @Mock
    private Metrics _metrics;
}
