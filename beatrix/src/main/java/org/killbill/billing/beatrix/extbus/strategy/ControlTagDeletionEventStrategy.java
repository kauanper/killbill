package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.ControlTagDeletionInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;
import org.killbill.billing.notification.plugin.api.TagMetadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class ControlTagDeletionEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.CONTROL_TAG_DELETION; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final ControlTagDeletionInternalEvent realTagEventDel = (ControlTagDeletionInternalEvent) event;
        objectType = ObjectType.TAG;
        objectId = realTagEventDel.getTagId();
        eventBusType = ExtBusEventType.TAG_DELETION;
        final TagMetadata controlTagDeletionMetadata = new TagMetadata(realTagEventDel.getTagDefinition().getName());
        metaData = objectMapper.writeValueAsString(controlTagDeletionMetadata);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
