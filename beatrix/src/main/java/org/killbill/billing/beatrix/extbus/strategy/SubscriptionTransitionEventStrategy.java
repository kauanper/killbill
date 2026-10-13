package org.killbill.billing.beatrix.extbus.strategy;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.EffectiveSubscriptionInternalEvent;
import org.killbill.billing.events.SubscriptionInternalEvent;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class SubscriptionTransitionEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() {
        return BusInternalEventType.SUBSCRIPTION_TRANSITION;
    }

    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        final SubscriptionInternalEvent subscriptionEvent = (SubscriptionInternalEvent) event;
        final ExtBusEventType eventType = SubscriptionTransitionTypeStrategy.mapTransitionType(subscriptionEvent.getTransitionType());
        final ActionType actionType = event instanceof EffectiveSubscriptionInternalEvent ? ActionType.EFFECTIVE : ActionType.REQUESTED;
        final boolean undo = subscriptionEvent.getTransitionType() == org.killbill.billing.subscription.api.SubscriptionBaseTransitionType.UNDO_CHANGE;
        final SubscriptionMetadata metadata = new SubscriptionMetadata(actionType, subscriptionEvent.getBundleExternalKey(), undo);
        return new ExternalEventConversionResult(ObjectType.SUBSCRIPTION,
                                         subscriptionEvent.getSubscriptionId(),
                                         eventType,
                                         objectMapper.writeValueAsString(metadata),
                                         null);
    }
}
