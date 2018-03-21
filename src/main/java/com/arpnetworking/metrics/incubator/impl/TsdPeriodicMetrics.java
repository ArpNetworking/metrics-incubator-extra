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
import com.arpnetworking.metrics.Unit;
import com.arpnetworking.metrics.impl.TsdMetricsFactory;
import com.arpnetworking.metrics.incubator.PeriodicMetrics;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementation of a {@link PeriodicMetrics} backed by a {@link TsdMetricsFactory}.
 *
 * NOTE: This class must be scheduled with an executor in order for metrics to be recorded.
 *
 * @author Brandon Arp (brandon dot arp at inscopemetrics dot com)
 */
public final class TsdPeriodicMetrics implements PeriodicMetrics, Runnable {
    @Override
    public void registerPolledMetric(final Consumer<PeriodicMetrics> consumer) {
        _polledMetricsRegistrations.add(consumer);
    }

    @Override
    public void recordCounter(final String name, final long value) {
        _currentPeriodicMetrics.readLocked(m -> m.createCounter(name).increment(value));
    }

    @Override
    public void recordTimer(final String name, final long duration, final Optional<Unit> unit) {
        _currentPeriodicMetrics.readLocked(m -> m.setTimer(name, duration, unit.orElse(null)));
    }

    @Override
    public void recordGauge(final String name, final double value) {
        _currentPeriodicMetrics.readLocked(m -> m.setGauge(name, value));
    }

    @Override
    public void recordGauge(final String name, final double value, final Optional<Unit> unit) {
        _currentPeriodicMetrics.readLocked(m -> m.setGauge(name, value, unit.orElse(null)));
    }

    @Override
    public void recordGauge(final String name, final long value) {
        _currentPeriodicMetrics.readLocked(m -> m.setGauge(name, value));
    }

    @Override
    public void recordGauge(final String name, final long value, final Optional<Unit> unit) {
        _currentPeriodicMetrics.readLocked(m -> m.setGauge(name, value, unit.orElse(null)));
    }

    @Override
    public void run() {
        cyclePeriodMetrics();
    }

    private void cyclePeriodMetrics() {
        recordPolledMetrics();
        final Metrics metrics = _currentPeriodicMetrics.getAndSetReference(_metricsFactory.create());
        metrics.close();
    }

    private CompletableFuture<Void> recordPolledMetrics() {
        final List<CompletableFuture<?>> futures = Lists.newArrayList();
        for (Consumer<PeriodicMetrics> polledMetric : _polledMetricsRegistrations) {
            futures.add(CompletableFuture.runAsync(() -> polledMetric.accept(this), _pollingExecutor));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()]));
    }

    private TsdPeriodicMetrics(final Builder builder) {
        _metricsFactory = builder._metricsFactory;
        _pollingExecutor = builder._pollingExecutor;

        _currentPeriodicMetrics = new ReadWriteLockedReference<>(_metricsFactory.create());
    }

    private final MetricsFactory _metricsFactory;
    private final ReadWriteLockedReference<Metrics> _currentPeriodicMetrics;
    private final Executor _pollingExecutor;
    private final Set<Consumer<PeriodicMetrics>> _polledMetricsRegistrations = ConcurrentHashMap.newKeySet();

    private static final Logger LOGGER = LoggerFactory.getLogger(TsdPeriodicMetrics.class);

    /**
     * Implementation of the Builder pattern for the {@link TsdPeriodicMetrics} class.
     *
     * @author Brandon Arp (brandon dot arp at inscopemetrics dot com)
     */
    public static final class Builder implements com.arpnetworking.commons.builder.Builder<TsdPeriodicMetrics> {
        /**
         * Public constructor.
         */
        public Builder() {
            this(LOGGER);
        }

        // NOTE: Package private for testing
        /* package private */ Builder(final Logger logger) {
            _logger = logger;
        }

        /**
         * Sets metrics factory. Required. Cannot be null.
         *
         * @param value The metrics factory.
         * @return This instance of {@link Builder}.
         */
        public Builder setMetricsFactory(final MetricsFactory value) {
            _metricsFactory = value;
            return this;
        }

        /**
         * Sets the executor where the polled metrics collection will be executed.  Cannot be null.
         *
         * @param value The metrics factory.
         * @return This instance of {@link Builder}.
         */
        public Builder setPollingExecutor(final Executor value) {
            _pollingExecutor = value;
            return this;
        }

        @Override
        public TsdPeriodicMetrics build() {
            // Defaults
            if (_metricsFactory == null) {
                _metricsFactory = new TsdMetricsFactory.Builder().build();
                _logger.warn(String.format("Defaulted null metrics factory; metricsFactory=%s", _metricsFactory));
            }

            if (_pollingExecutor == null) {
                _pollingExecutor = DEFAULT_POLLING_EXECUTOR_SUPPLIER.get();
                _logger.warn(String.format("Defaulted null polling executor; pollingExecutor=%s", _pollingExecutor));
            }

            return new TsdPeriodicMetrics(this);
        }
        private MetricsFactory _metricsFactory;
        private Executor _pollingExecutor = DEFAULT_POLLING_EXECUTOR_SUPPLIER.get();

        private final Logger _logger;

        private static final Supplier<Executor> DEFAULT_POLLING_EXECUTOR_SUPPLIER = MoreExecutors::directExecutor;
    }
}
