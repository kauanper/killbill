package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.TenantConfigDeletionInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;
import org.killbill.billing.notification.plugin.api.TenantConfigMetadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TenantConfigDeletionEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.TENANT_CONFIG_DELETION; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final TenantConfigDeletionInternalEvent realTenantConfigEventDel = (TenantConfigDeletionInternalEvent) event;
        objectType = ObjectType.TENANT_KVS;
        objectId = null;
        eventBusType = ExtBusEventType.TENANT_CONFIG_DELETION;
        final TenantConfigMetadata tenantConfigDeletionMetadata = new TenantConfigMetadata(realTenantConfigEventDel.getKey());
        metaData = objectMapper.writeValueAsString(tenantConfigDeletionMetadata);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
