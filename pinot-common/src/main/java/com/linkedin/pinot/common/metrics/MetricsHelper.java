package com.linkedin.pinot.common.metrics;

import java.util.concurrent.TimeUnit;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.Metered;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.Sampling;
import com.yammer.metrics.core.Stoppable;


public class MetricsHelper {

  /**
   * 
   * Return an existing meter if
   *  (a) A meter already exist with the same metric name.
   * Otherwise, creates a new meter and registers
   * 
   * @param registry MetricsRegistry
   * @param name metric name
   * @param eventType Event Type
   * @param unit TimeUnit for rate determination
   * @return Meter
   */
  public static Meter newMeter(MetricsRegistry registry, MetricName name, String eventType, TimeUnit unit) {
    if (registry != null) {
      return registry.newMeter(name, eventType, unit);
    } else {
      return Metrics.newMeter(name, eventType, unit);
    }
  }

  /**
   * 
   * Return an existing aggregated meter if registry is not null and a aggregated meter already exist
   * with the same metric name. Otherwise, creates a new aggregated meter and registers (if registry not null)
   * 
   * @param registry MetricsRegistry
   * @param name metric name
   * @return AggregatedMeter
   */
  public static <T extends Metered & Stoppable> AggregatedMeter<T> newAggregatedMeter(
      AggregatedMetricsRegistry registry, MetricName name) {
    if (registry != null) {
      return registry.newAggregatedMeter(name);
    } else {
      return new AggregatedMeter<T>(); //not registered
    }
  }

  /**
   * 
   * Return an existing counter if
   *  (a) A counter already exist with the same metric name.
   * Otherwise, creates a new meter and registers
   * 
   * @param registry MetricsRegistry
   * @param name metric name
   * @return Counter
   */
  public static Counter newCounter(MetricsRegistry registry, MetricName name) {
    if (registry != null) {
      return registry.newCounter(name);
    } else {
      return Metrics.newCounter(name);
    }
  }

  /**
   * 
   * Return an existing aggregated counter if registry is not null and a aggregated counter already exist
   * with the same metric name. Otherwise, creates a new aggregated counter and registers (if registry not null)
   * 
   * @param registry MetricsRegistry
   * @param name metric name
   * @return AggregatedCounter
   */
  public static AggregatedCounter newAggregatedCounter(AggregatedMetricsRegistry registry, MetricName name) {
    if (registry != null) {
      return registry.newAggregatedCounter(name);
    } else {
      return new AggregatedCounter();
    }
  }

  /**
   * 
   * Return an existing histogram if
   *  (a) A histogram already exist with the same metric name.
   * Otherwise, creates a new meter and registers
   * 
   * @param registry MetricsRegistry
   * @param name metric name
   * @param biased (true if uniform distribution, otherwise exponential weighted)
   * @return histogram
   */
  public static Histogram newHistogram(MetricsRegistry registry, MetricName name, boolean biased) {
    if (registry != null) {
      return registry.newHistogram(name, biased);
    } else {
      return Metrics.newHistogram(name, biased);
    }
  }

  /**
   * 
   * Return an existing aggregated histogram if registry is not null and a aggregated histogram already exist
   * with the same metric name. Otherwise, creates a new aggregated histogram and registers (if registry not null)
   * 
   * @param registry MetricsRegistry
   * @param name metric name
   * @return AggregatedHistogram
   */
  public static <T extends Sampling> AggregatedHistogram<T> newAggregatedHistogram(AggregatedMetricsRegistry registry,
      MetricName name) {
    if (registry != null) {
      return registry.newAggregatedHistogram(name);
    } else {
      return new AggregatedHistogram<T>();
    }
  }

  /**
   * 
   * Return an existing gauge if
   *  (a) A gauge already exist with the same metric name.
   * Otherwise, creates a new meter and registers
   * 
   * @param registry MetricsRegistry
   * @param name metric name
   * @param gauge Underlying gauge to be tracked
   * @return gauge
   */
  public static <T> Gauge<T> newGauge(MetricsRegistry registry, MetricName name, Gauge<T> gauge) {
    if (registry != null) {
      return registry.newGauge(name, gauge);
    } else {
      return Metrics.newGauge(name, gauge);
    }
  }

  /**
   * 
   * Return an existing aggregated long gauge if registry is not null and a aggregated long gauge already exist
   * with the same metric name. Otherwise, creates a new aggregated long gauge and registers (if registry not null)
   * 
   * @param registry MetricsRegistry
   * @param name metric name
   * @return AggregatedLongGauge
   */
  public static <T extends Number, V extends Gauge<T>> AggregatedLongGauge<T, V> newAggregatedLongGauge(
      AggregatedMetricsRegistry registry, MetricName name) {
    if (registry != null) {
      return registry.newAggregatedLongGauge(name);
    } else {
      return new AggregatedLongGauge<T, V>();
    }
  }

  /**
   * Useful for measuring elapsed times.
   * 
   * Usage :
   * <pre>
   * {@code
   *   TimerContext tc = MtericsHelper.startTimer();
   *   ....
   *   Your code to be measured
   *   ....
   *   tc.stop();
   *   long elapsedTimeMs = tc.getLatencyMs();
   * 
   * }
   * </pre>
   * @return
   */
  public static TimerContext startTimer() {
    return new TimerContext();
  }

  /**
   * 
   * TimerContext to measure elapsed time
   * @author bvaradar
   *
   */
  public static class TimerContext {
    private final long _startTimeMs;
    private long _stopTimeMs;
    private boolean _isDone;

    public TimerContext() {
      _startTimeMs = System.nanoTime();
      _isDone = false;
    }

    public void stop() {
      _isDone = true;
      _stopTimeMs = System.nanoTime();
    }

    /**
     * 
     * @return
     */
    public long getLatencyMs() {
      if (!_isDone) {
        stop();
      }
      return (_stopTimeMs - _startTimeMs) / 1000000L;
    }
  }
}
