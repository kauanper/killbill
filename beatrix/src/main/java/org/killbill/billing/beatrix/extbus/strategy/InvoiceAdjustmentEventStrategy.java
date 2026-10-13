package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.InvoiceAdjustmentInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class InvoiceAdjustmentEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.INVOICE_ADJUSTMENT; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final InvoiceAdjustmentInternalEvent realEventInvAdj = (InvoiceAdjustmentInternalEvent) event;
        objectType = ObjectType.INVOICE;
        objectId = realEventInvAdj.getInvoiceId();
        eventBusType = ExtBusEventType.INVOICE_ADJUSTMENT;
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
