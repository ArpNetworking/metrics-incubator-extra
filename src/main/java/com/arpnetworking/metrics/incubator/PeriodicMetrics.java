/*
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

import com.arpnetworking.metrics.Counter;
import com.arpnetworking.metrics.Metrics;
import com.arpnetworking.metrics.Timer;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A type of Metrics that is for use in a periodic context.  Unlike the {@link Metrics} class,
 * there is no close method as the {@link PeriodicMetrics} is flushed regularly on a schedule.  Similar to the
 * {@link Metrics} class, there are createTimer and createCounter methods that return an object that can be used
 * to record timers and counters respectively.  However, unlike the {@link Timer} and {@link Counter} instances from
 * {@link Metrics}, a {@link Timer} and {@link Counter} will not be bound to the time interval that they were created
 * in; the timers and counters will be recorded in the interval that is open when the timer or counter is closed.
 *
 * @author Brandon Arp (brandon dot arp at inscopemetrics dot io)
 */
public interface PeriodicMetrics {
    /**
     * Register a consumer to be polled periodically.  This callback will be executed on a periodic basis
     * once registered.  The instance provided should only be used inside the {@link Consumer} and should not be
     * closed.  Closing and flushing of the {@link Metrics} object will be handled for you.
     *
     * @param consumer A consumer to call to get metrics
     */
    void registerPolledMetric(Consumer<PeriodicMetrics> consumer);

    /**
     * Create a new sample for the counter.
     *
     * @param name The name of the counter.
     * @param value The value of the counter.
     */
    void recordCounter(String name, long value);

    /**
     * Set the timer to the specified value. This is most commonly used to
     * record timers from external sources that are not directly integrated with
     * metrics.
     *
     * @param name The name of the timer.
     * @param duration The duration of the timer.
     * @param unit The time unit of the timer.
     */
    void recordTimer(String name, long duration, Optional<TimeUnit> unit);

    /**
     * Set the specified gauge reading.
     *
     * @param name The name of the gauge.
     * @param value The reading on the gauge
     */
    void recordGauge(String name, double value);

    /**
     * Set the specified gauge reading.
     *
     * @param name The name of the gauge.
     * @param value The reading on the gauge
     */
    void recordGauge(String name, long value);
}
