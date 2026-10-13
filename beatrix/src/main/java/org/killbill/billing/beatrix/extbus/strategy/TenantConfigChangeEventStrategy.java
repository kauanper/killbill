package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.TenantConfigChangeInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;
import org.killbill.billing.notification.plugin.api.TenantConfigMetadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TenantConfigChangeEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.TENANT_CONFIG_CHANGE; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final TenantConfigChangeInternalEvent realTenantConfigEventChg = (TenantConfigChangeInternalEvent) event;
        objectType = ObjectType.TENANT_KVS;
        objectId = realTenantConfigEventChg.getId();
        eventBusType = ExtBusEventType.TENANT_CONFIG_CHANGE;
        final TenantConfigMetadata tenantConfigChangeMetadata = new TenantConfigMetadata(realTenantConfigEventChg.getKey());
        metaData = objectMapper.writeValueAsString(tenantConfigChangeMetadata);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
