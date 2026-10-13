package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.PaymentErrorInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.PaymentMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class PaymentErrorEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.PAYMENT_ERROR; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final PaymentErrorInternalEvent realEventPayErr = (PaymentErrorInternalEvent) event;
        objectType = ObjectType.PAYMENT;
        objectId = realEventPayErr.getPaymentId();
        eventBusType = ExtBusEventType.PAYMENT_FAILED;
        accountId = realEventPayErr.getAccountId();
        final PaymentMetadata paymentErrorMetaDataObj = new PaymentMetadata(realEventPayErr.getPaymentTransactionId(), realEventPayErr.getAmount(), realEventPayErr.getCurrency(), realEventPayErr.getStatus(), realEventPayErr.getTransactionType(), realEventPayErr.getEffectiveDate());
        metaData = objectMapper.writeValueAsString(paymentErrorMetaDataObj);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
