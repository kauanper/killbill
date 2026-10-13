package org.killbill.billing.beatrix.extbus.strategy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;

@Singleton
public final class InternalEventConversionFactory {
    private final Set<InternalEventConversionStrategy> strategies;
    @Inject public InternalEventConversionFactory(final Set<InternalEventConversionStrategy> strategies) { this.strategies = strategies; }
    public static InternalEventConversionFactory createDefault() {
        return new InternalEventConversionFactory(new HashSet<>(Arrays.asList(
            new AccountCreateEventStrategy(),
            new AccountChangeEventStrategy(),
            new SubscriptionTransitionEventStrategy(),
            new BlockingStateEventStrategy(),
            new InvoiceCreationEventStrategy(),
            new InvoiceNotificationEventStrategy(),
            new InvoiceAdjustmentEventStrategy(),
            new InvoicePaymentInfoEventStrategy(),
            new InvoicePaymentErrorEventStrategy(),
            new PaymentInfoEventStrategy(),
            new PaymentErrorEventStrategy(),
            new PaymentPluginErrorEventStrategy(),
            new OverdueChangeEventStrategy(),
            new UserTagCreationEventStrategy(),
            new ControlTagCreationEventStrategy(),
            new UserTagDeletionEventStrategy(),
            new ControlTagDeletionEventStrategy(),
            new CustomFieldCreationEventStrategy(),
            new CustomFieldDeletionEventStrategy(),
            new TenantConfigChangeEventStrategy(),
            new TenantConfigDeletionEventStrategy(),
            new BroadcastServiceEventStrategy())));
    }
    public InternalEventConversionStrategy getStrategy(final BusInternalEventType type) {
        return strategies.stream().filter(strategy -> strategy.getEventType() == type).findFirst().orElse(null);
    }
}
