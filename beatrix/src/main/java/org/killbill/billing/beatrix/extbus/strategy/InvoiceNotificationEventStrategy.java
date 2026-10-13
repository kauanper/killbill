package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.InvoiceNotificationInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.InvoiceNotificationMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class InvoiceNotificationEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.INVOICE_NOTIFICATION; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final InvoiceNotificationInternalEvent realEventInvNotification = (InvoiceNotificationInternalEvent) event;
        objectType = ObjectType.INVOICE;
        objectId = null;
        accountId = realEventInvNotification.getAccountId(); // has to be set here because objectId is null with a dryRun Invoice
        eventBusType = ExtBusEventType.INVOICE_NOTIFICATION;

        final InvoiceNotificationMetadata invoiceNotificationMetadata = new InvoiceNotificationMetadata(realEventInvNotification.getTargetDate(),
                                                                                        realEventInvNotification.getAmountOwed(),
                                                                                        realEventInvNotification.getCurrency());
        metaData = objectMapper.writeValueAsString(invoiceNotificationMetadata);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
