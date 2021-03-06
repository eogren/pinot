package com.linkedin.thirdeye.rootcause.impl;

import com.linkedin.thirdeye.anomaly.events.EventDataProviderManager;
import com.linkedin.thirdeye.anomaly.events.EventFilter;
import com.linkedin.thirdeye.datalayer.dto.EventDTO;
import com.linkedin.thirdeye.rootcause.Entity;
import com.linkedin.thirdeye.rootcause.ExecutionContext;
import com.linkedin.thirdeye.rootcause.Pipeline;
import com.linkedin.thirdeye.rootcause.PipelineResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sample implementation of a pipeline for identifying events based on their start and end times.
 * The pipeline identifies the TimeRangeEntity in the search context and then invokes the
 * event provider manager to fetch any matching events. It then scores events based on their
 * time distance from the end of the search time window (closer is better).
 */
public class EventTimePipeline implements Pipeline {
  private static Logger LOG = LoggerFactory.getLogger(EventTimePipeline.class);

  final EventDataProviderManager provider;

  public EventTimePipeline(EventDataProviderManager manager) {
    this.provider = manager;
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  @Override
  public PipelineResult run(ExecutionContext context) {
    TimeRangeEntity current = TimeRangeEntity.getContextCurrent(context);

    EventFilter filter = new EventFilter();
    filter.setStartTime(current.getStart());
    filter.setEndTime(current.getEnd());

    List<EventDTO> events = provider.getEvents(filter);

    List<EventEntity> entities = new ArrayList<>();
    for(EventDTO e : events) {
      long distance = current.getEnd() - e.getStartTime();
      double score = -distance;
      entities.add(EventEntity.fromDTO(score, e));
    }

    return new PipelineResult(entities);
  }
}
