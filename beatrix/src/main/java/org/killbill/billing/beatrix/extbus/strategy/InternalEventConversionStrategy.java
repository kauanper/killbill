package org.killbill.billing.beatrix.extbus.strategy;

import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface InternalEventConversionStrategy {
    BusInternalEventType getEventType();
    ExternalEventConversionResult convert(BusInternalEvent event, ObjectMapper objectMapper) throws JsonProcessingException;
}
