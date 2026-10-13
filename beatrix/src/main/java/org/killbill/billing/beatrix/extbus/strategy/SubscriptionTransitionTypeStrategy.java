package org.killbill.billing.beatrix.extbus.strategy;

import java.util.EnumMap;
import java.util.Map;

import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.subscription.api.SubscriptionBaseTransitionType;

public final class SubscriptionTransitionTypeStrategy {
    private static final Map<SubscriptionBaseTransitionType, ExtBusEventType> MAPPINGS = createMappings();
    private SubscriptionTransitionTypeStrategy() { }
    private static Map<SubscriptionBaseTransitionType, ExtBusEventType> createMappings() {
        final Map<SubscriptionBaseTransitionType, ExtBusEventType> mappings = new EnumMap<>(SubscriptionBaseTransitionType.class);
        mappings.put(SubscriptionBaseTransitionType.CREATE, ExtBusEventType.SUBSCRIPTION_CREATION);
        mappings.put(SubscriptionBaseTransitionType.TRANSFER, ExtBusEventType.SUBSCRIPTION_CREATION);
        mappings.put(SubscriptionBaseTransitionType.CANCEL, ExtBusEventType.SUBSCRIPTION_CANCEL);
        mappings.put(SubscriptionBaseTransitionType.PHASE, ExtBusEventType.SUBSCRIPTION_PHASE);
        mappings.put(SubscriptionBaseTransitionType.CHANGE, ExtBusEventType.SUBSCRIPTION_CHANGE);
        mappings.put(SubscriptionBaseTransitionType.UNDO_CHANGE, ExtBusEventType.SUBSCRIPTION_CHANGE);
        mappings.put(SubscriptionBaseTransitionType.UNCANCEL, ExtBusEventType.SUBSCRIPTION_UNCANCEL);
        mappings.put(SubscriptionBaseTransitionType.BCD_CHANGE, ExtBusEventType.SUBSCRIPTION_BCD_CHANGE);
        mappings.put(SubscriptionBaseTransitionType.QUANTITY_CHANGE, ExtBusEventType.SUBSCRIPTION_QUANTITY);
        mappings.put(SubscriptionBaseTransitionType.EXPIRED, ExtBusEventType.SUBSCRIPTION_EXPIRED);
        return Map.copyOf(mappings);
    }
    public static ExtBusEventType mapTransitionType(final SubscriptionBaseTransitionType type) { return MAPPINGS.get(type); }
}
