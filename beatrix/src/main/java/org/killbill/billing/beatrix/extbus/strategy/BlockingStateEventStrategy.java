package org.killbill.billing.beatrix.extbus.strategy;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.entitlement.api.BlockingStateType;
import org.killbill.billing.entitlement.api.DefaultEntitlementApi;
import org.killbill.billing.events.BlockingTransitionInternalEvent;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.notification.plugin.api.BlockingStateMetadata;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;
import org.killbill.billing.platform.api.KillbillService.KILLBILL_SERVICES;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class BlockingStateEventStrategy implements InternalEventConversionStrategy {
    @Override
    public BusInternalEventType getEventType() { return BusInternalEventType.BLOCKING_STATE; }
    @Override
    public ExternalEventConversionResult convert(final BusInternalEvent event, final ObjectMapper objectMapper) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

        final BlockingTransitionInternalEvent realEventBS = (BlockingTransitionInternalEvent) event;
        if (realEventBS.getBlockingType() == BlockingStateType.ACCOUNT) {
            objectType = ObjectType.ACCOUNT;
        } else if (realEventBS.getBlockingType() == BlockingStateType.SUBSCRIPTION_BUNDLE) {
            objectType = ObjectType.BUNDLE;
        } else if (realEventBS.getBlockingType() == BlockingStateType.SUBSCRIPTION) {
            objectType = ObjectType.SUBSCRIPTION;
        }
        objectId = realEventBS.getBlockableId();

        if (KILLBILL_SERVICES.ENTITLEMENT_SERVICE.getServiceName().equals(realEventBS.getService())) {
            if (DefaultEntitlementApi.ENT_STATE_START.equals(realEventBS.getStateName())) {
                eventBusType = ExtBusEventType.ENTITLEMENT_CREATION;
            } else if (DefaultEntitlementApi.ENT_STATE_BLOCKED.equals(realEventBS.getStateName())) {
                eventBusType = ExtBusEventType.BUNDLE_PAUSE;
            } else if (DefaultEntitlementApi.ENT_STATE_CLEAR.equals(realEventBS.getStateName())) {
                eventBusType = ExtBusEventType.BUNDLE_RESUME;
            } else if (DefaultEntitlementApi.ENT_STATE_CANCELLED.equals(realEventBS.getStateName())) {
                eventBusType = ExtBusEventType.ENTITLEMENT_CANCEL;
            }
        } else {
            eventBusType = ExtBusEventType.BLOCKING_STATE;

            final BlockingStateMetadata blockingStateMetadata = new BlockingStateMetadata(realEventBS.getBlockableId(), realEventBS.getService(), realEventBS.getStateName(), realEventBS.getBlockingType(), realEventBS.getEffectiveDate(),
                                                                                realEventBS.isTransitionedToBlockedBilling(), realEventBS.isTransitionedToUnblockedBilling(),
                                                                                realEventBS.isTransitionedToBlockedEntitlement(), realEventBS.isTransitionedToUnblockedEntitlement());
            metaData = objectMapper.writeValueAsString(blockingStateMetadata);
        }
        return new ExternalEventConversionResult(objectType, objectId, eventBusType, metaData, accountId);

    }
}
