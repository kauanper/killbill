package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BroadcastInternalEvent;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.notification.plugin.api.BroadcastMetadata;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class BroadcastServiceEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.BROADCAST_SERVICE; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final BroadcastInternalEvent realBroadcastEvent = (BroadcastInternalEvent) event;
        objectType = ObjectType.SERVICE_BROADCAST;
        objectId = null;
        eventBusType = ExtBusEventType.BROADCAST_SERVICE;
        final BroadcastMetadata broadcastMetadata = new BroadcastMetadata(realBroadcastEvent.getServiceName(), realBroadcastEvent.getType(), realBroadcastEvent.getJsonEvent());
        metaData = objectMapper.writeValueAsString(broadcastMetadata);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
