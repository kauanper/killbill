package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.PaymentPluginErrorInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.PaymentMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class PaymentPluginErrorEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.PAYMENT_PLUGIN_ERROR; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final PaymentPluginErrorInternalEvent realEventPayPluginErr = (PaymentPluginErrorInternalEvent) event;
        objectType = ObjectType.PAYMENT;
        objectId = realEventPayPluginErr.getPaymentId();
        eventBusType = ExtBusEventType.PAYMENT_FAILED;
        final PaymentMetadata pluginErrorMetaDataObj = new PaymentMetadata(realEventPayPluginErr.getPaymentTransactionId(), realEventPayPluginErr.getAmount(), realEventPayPluginErr.getCurrency(), realEventPayPluginErr.getStatus(), realEventPayPluginErr.getTransactionType(), realEventPayPluginErr.getEffectiveDate());
        metaData = objectMapper.writeValueAsString(pluginErrorMetaDataObj);
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
