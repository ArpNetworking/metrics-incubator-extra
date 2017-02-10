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
import com.arpnetworking.metrics.Timer;
import com.arpnetworking.metrics.Unit;
import com.arpnetworking.metrics.impl.TsdMetricsFactory;
import com.arpnetworking.metrics.incubator.PeriodicMetricsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Brandon Arp (brandon dot arp at inscopemetrics dot com)
 */
public final class TsdPeriodicMetricsFactory implements PeriodicMetricsFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerPolledMetric(final Consumer<Metrics> consumer) {
        _polledMetricsRegistrations.add(consumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void record(final Consumer<Metrics> consumer) {
        _currentPeriodicMetrics.readLocked(metrics -> consumer.accept(new NonClosingMetrics(metrics)));
    }

    private void cyclePeriodMetrics() {
        _currentPeriodicMetrics.readLocked(this::recordPolledMetrics);
        final Metrics metrics = _currentPeriodicMetrics.getAndSetReference(_metricsFactory.create());
        metrics.close();
    }

    private void recordPolledMetrics(final Metrics metrics) {
        for (Consumer<Metrics> polledMetric : _polledMetricsRegistrations) {
            polledMetric.accept(metrics);
        }
    }

    private TsdPeriodicMetricsFactory(final Builder builder) {
        _metricsFactory = builder._metricsFactory;

        _currentPeriodicMetrics = new SafeRefLock<>(_metricsFactory.create());

        _closingExecutor.scheduleAtFixedRate(this::cyclePeriodMetrics, 500, 500, TimeUnit.MILLISECONDS);
    }

    private final MetricsFactory _metricsFactory;
    private final SafeRefLock<Metrics> _currentPeriodicMetrics;
    private final ScheduledExecutorService _closingExecutor = new ScheduledThreadPoolExecutor(1, (r) -> new Thread(r, "MetricsCloser"));
    private final Set<Consumer<Metrics>> _polledMetricsRegistrations = ConcurrentHashMap.newKeySet();

    private static final Logger LOGGER = LoggerFactory.getLogger(TsdPeriodicMetricsFactory.class);

    /**
     * Implementation of the Builder pattern for the {@link TsdPeriodicMetricsFactory} class.
     *
     * @author Brandon Arp (brandon dot arp at inscopemetrics dot com)
     */
    public static final class Builder implements com.arpnetworking.commons.builder.Builder<TsdPeriodicMetricsFactory> {
        /**
         * Public constructor.
         */
        public Builder() {
            this(LOGGER);
        }

        // NOTE: Package private for testing
        /* package private */ Builder(@Nullable final Logger logger) {
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
         * {@inheritDoc}
         */
        @Override
        public TsdPeriodicMetricsFactory build() {
            final List<String> failures = new ArrayList<>();

            // Defaults
            if (_metricsFactory == null) {
                _metricsFactory = new TsdMetricsFactory.Builder().build();
                _logger.warn(String.format("Defaulted null metrics factory; metricsFactory=%s", _metricsFactory));
            }

            return new TsdPeriodicMetricsFactory(this);
        }
        private MetricsFactory _metricsFactory;

        private final Logger _logger;
    }

    /* package private */ static final class NonClosingMetrics implements Metrics {
        NonClosingMetrics(final Metrics wrapped) {
            this._wrapped = wrapped;
        }

        @Override
        public Counter createCounter(@Nonnull final String name) {
            return _wrapped.createCounter(name);
        }

        @Override
        public void incrementCounter(@Nonnull final String name) {
            _wrapped.incrementCounter(name);
        }

        @Override
        public void incrementCounter(@Nonnull final String name, final long value) {
            _wrapped.incrementCounter(name, value);
        }

        @Override
        public void decrementCounter(@Nonnull final String name) {
            _wrapped.decrementCounter(name);
        }

        @Override
        public void decrementCounter(@Nonnull final String name, final long value) {
            _wrapped.decrementCounter(name, value);
        }

        @Override
        public void resetCounter(@Nonnull final String name) {
            _wrapped.resetCounter(name);
        }

        @Override
        public Timer createTimer(@Nonnull final String name) {
            return _wrapped.createTimer(name);
        }

        @Override
        public void startTimer(@Nonnull final String name) {
            _wrapped.startTimer(name);
        }

        @Override
        public void stopTimer(@Nonnull final String name) {
            _wrapped.stopTimer(name);
        }

        @Override
        public void setTimer(@Nonnull final String name, final long duration, @Nullable final TimeUnit unit) {
            _wrapped.setTimer(name, duration, unit);
        }

        @Override
        public void setTimer(@Nonnull final String name, final long duration, @Nullable final Unit unit) {
            _wrapped.setTimer(name, duration, unit);
        }

        @Override
        public void setGauge(@Nonnull final String name, final double value) {
            _wrapped.setGauge(name, value);
        }

        @Override
        public void setGauge(@Nonnull final String name, final double value, @Nullable final Unit unit) {
            _wrapped.setGauge(name, value, unit);
        }

        @Override
        public void setGauge(@Nonnull final String name, final long value) {
            _wrapped.setGauge(name, value);
        }

        @Override
        public void setGauge(@Nonnull final String name, final long value, @Nullable final Unit unit) {
            _wrapped.setGauge(name, value, unit);
        }

        @Override
        public void addAnnotation(@Nonnull final String key, @Nonnull final String value) {
            _wrapped.addAnnotation(key, value);
        }

        @Override
        public void addAnnotations(@Nonnull final Map<String, String> map) {
            _wrapped.addAnnotations(map);
        }

        @Override
        public boolean isOpen() {
            return _wrapped.isOpen();
        }

        @Override
        public void close() {
            // Don't do anything. We don't want this instance to be closeable.
        }

        @Override
        @Nullable
        public Instant getOpenTime() {
            return _wrapped.getOpenTime();
        }

        @Override
        @Nullable
        public Instant getCloseTime() {
            return _wrapped.getCloseTime();
        }

        private final Metrics _wrapped;
    }
}
