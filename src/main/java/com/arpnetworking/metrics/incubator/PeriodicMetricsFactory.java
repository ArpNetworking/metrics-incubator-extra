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
package com.arpnetworking.metrics.incubator;

import com.arpnetworking.metrics.Metrics;

import java.util.function.Consumer;

/**
 * Provides {@link Metrics} instances suitable for recording data that is not tied to a unit of work.  For example,
 * recording data for a guage that is periodically sampled.
 *
 * @author Brandon Arp (brandon dot arp at inscopemetrics dot com)
 */
public interface PeriodicMetricsFactory {
    /**
     * Register a consumer to be polled periodically.  This callback will be executed on a periodic basis
     * once registered.  The instance provided should only be used inside the {@link Consumer} and should not be
     * closed.  Closing and flushing of the {@link Metrics} object will be handled for you.
     *
     * @param consumer A consumer to call to get metrics
     */
    void registerPolledMetric(Consumer<Metrics> consumer);

    /**
     * Get a reference to a {@link Metrics} to record metrics. The instance provided should only be used
     * inside the {@link Consumer} and should not be closed. Closing and flushing of the {@link Metrics}
     * object will be handled for you.
     *
     * @param consumer a {@link Consumer}
     */
    void record(Consumer<Metrics> consumer);
}
