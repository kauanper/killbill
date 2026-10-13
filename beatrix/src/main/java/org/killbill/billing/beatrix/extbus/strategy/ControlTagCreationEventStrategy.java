package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.ControlTagCreationInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;
import org.killbill.billing.notification.plugin.api.TagMetadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ControlTagCreationEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.CONTROL_TAG_CREATION; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final ControlTagCreationInternalEvent realTagEventCr = (ControlTagCreationInternalEvent) event;
        objectType = ObjectType.TAG;
        objectId = realTagEventCr.getTagId();
        eventBusType = ExtBusEventType.TAG_CREATION;
        final TagMetadata controlTagCreationMetadata = new TagMetadata(realTagEventCr.getTagDefinition().getName());
        metaData = objectMapper.writeValueAsString(controlTagCreationMetadata);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
