package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.CustomFieldCreationEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class CustomFieldCreationEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.CUSTOM_FIELD_CREATION; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final CustomFieldCreationEvent realCustomFieldEventCr = (CustomFieldCreationEvent) event;
        objectType = ObjectType.CUSTOM_FIELD;
        objectId = realCustomFieldEventCr.getCustomFieldId();
        eventBusType = ExtBusEventType.CUSTOM_FIELD_CREATION;
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
