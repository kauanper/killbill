package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.InvoicePaymentInfoInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.InvoicePaymentMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class InvoicePaymentInfoEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.INVOICE_PAYMENT_INFO; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final InvoicePaymentInfoInternalEvent realEventInvPay = (InvoicePaymentInfoInternalEvent) event;
        objectType = ObjectType.INVOICE;
        objectId = realEventInvPay.getInvoiceId();
        eventBusType = ExtBusEventType.INVOICE_PAYMENT_SUCCESS;
        final InvoicePaymentMetadata invoicePaymentInfoMetaDataObj = new InvoicePaymentMetadata(realEventInvPay.getPaymentId(), realEventInvPay.getPaymentAttemptId(), realEventInvPay.getType(), realEventInvPay.getPaymentDate(), realEventInvPay.getAmount(), realEventInvPay.getCurrency(), realEventInvPay.getLinkedInvoicePaymentId(), realEventInvPay.getPaymentCookieId(), realEventInvPay.getProcessedCurrency());
        metaData = objectMapper.writeValueAsString(invoicePaymentInfoMetaDataObj);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
