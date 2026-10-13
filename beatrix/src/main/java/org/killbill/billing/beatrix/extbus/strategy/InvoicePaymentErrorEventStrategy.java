package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.InvoicePaymentErrorInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.InvoicePaymentMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class InvoicePaymentErrorEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.INVOICE_PAYMENT_ERROR; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final InvoicePaymentErrorInternalEvent realEventInvPayErr = (InvoicePaymentErrorInternalEvent) event;
        objectType = ObjectType.INVOICE;
        objectId = realEventInvPayErr.getInvoiceId();
        eventBusType = ExtBusEventType.INVOICE_PAYMENT_FAILED;
        final InvoicePaymentMetadata invoicePaymentErrorMetaDataObj = new InvoicePaymentMetadata(realEventInvPayErr.getPaymentId(), realEventInvPayErr.getPaymentAttemptId(), realEventInvPayErr.getType(), realEventInvPayErr.getPaymentDate(), realEventInvPayErr.getAmount(), realEventInvPayErr.getCurrency(), realEventInvPayErr.getLinkedInvoicePaymentId(), realEventInvPayErr.getPaymentCookieId(), realEventInvPayErr.getProcessedCurrency());
        metaData = objectMapper.writeValueAsString(invoicePaymentErrorMetaDataObj);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
