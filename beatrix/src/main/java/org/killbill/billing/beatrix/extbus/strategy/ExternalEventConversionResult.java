package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;

public final class ExternalEventConversionResult {
    public final ObjectType objectType;
    public final UUID objectId;
    public final ExtBusEventType eventBusType;
    public final String metaData;
    public final UUID accountId;

    public ExternalEventConversionResult(final ObjectType objectType, final UUID objectId, final ExtBusEventType eventBusType, final String metaData, final UUID accountId) {
        this.objectType = objectType;
        this.objectId = objectId;
        this.eventBusType = eventBusType;
        this.metaData = metaData;
        this.accountId = accountId;
    }
}
