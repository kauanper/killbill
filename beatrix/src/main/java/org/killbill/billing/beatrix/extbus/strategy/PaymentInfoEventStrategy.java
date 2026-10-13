package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.PaymentInfoInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.PaymentMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class PaymentInfoEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.PAYMENT_INFO; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final PaymentInfoInternalEvent realEventPay = (PaymentInfoInternalEvent) event;
        objectType = ObjectType.PAYMENT;
        objectId = realEventPay.getPaymentId();
        eventBusType = ExtBusEventType.PAYMENT_SUCCESS;
        final PaymentMetadata paymentInfoMetaDataObj = new PaymentMetadata(realEventPay.getPaymentTransactionId(), realEventPay.getAmount(), realEventPay.getCurrency(), realEventPay.getStatus(), realEventPay.getTransactionType(), realEventPay.getEffectiveDate());
        metaData = objectMapper.writeValueAsString(paymentInfoMetaDataObj);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
