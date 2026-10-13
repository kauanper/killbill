package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.UserTagDeletionInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;
import org.killbill.billing.notification.plugin.api.TagMetadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class UserTagDeletionEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.USER_TAG_DELETION; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final UserTagDeletionInternalEvent realUserTagEventDel = (UserTagDeletionInternalEvent) event;
        objectType = ObjectType.TAG;
        objectId = realUserTagEventDel.getTagId();
        eventBusType = ExtBusEventType.TAG_DELETION;
        final TagMetadata userTagDeletionMetadata = new TagMetadata(realUserTagEventDel.getTagDefinition().getName());
        metaData = objectMapper.writeValueAsString(userTagDeletionMetadata);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
