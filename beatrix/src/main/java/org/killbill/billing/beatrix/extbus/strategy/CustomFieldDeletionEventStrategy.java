package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.CustomFieldDeletionEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class CustomFieldDeletionEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.CUSTOM_FIELD_DELETION; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final CustomFieldDeletionEvent realCustomFieldEventDel = (CustomFieldDeletionEvent) event;
        objectType = ObjectType.CUSTOM_FIELD;
        objectId = realCustomFieldEventDel.getCustomFieldId();
        eventBusType = ExtBusEventType.CUSTOM_FIELD_DELETION;
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
