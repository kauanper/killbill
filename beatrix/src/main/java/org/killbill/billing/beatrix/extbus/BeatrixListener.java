/*
 * Copyright 2010-2013 Ning, Inc.
 * Copyright 2014-2015 Groupon, Inc
 * Copyright 2014-2015 The Billing Project, LLC
 *
 * The Billing Project licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.beatrix.extbus;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.killbill.billing.ObjectType;
import org.killbill.billing.callcontext.InternalCallContext;
import org.killbill.billing.entitlement.api.BlockingStateType;
import org.killbill.billing.entitlement.api.DefaultEntitlementApi;
import org.killbill.billing.events.AccountChangeInternalEvent;
import org.killbill.billing.events.AccountCreationInternalEvent;
import org.killbill.billing.events.BlockingTransitionInternalEvent;
import org.killbill.billing.events.BroadcastInternalEvent;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.events.ControlTagCreationInternalEvent;
import org.killbill.billing.events.ControlTagDeletionInternalEvent;
import org.killbill.billing.events.CustomFieldCreationEvent;
import org.killbill.billing.events.CustomFieldDeletionEvent;
import org.killbill.billing.events.EffectiveSubscriptionInternalEvent;
import org.killbill.billing.events.InvoiceAdjustmentInternalEvent;
import org.killbill.billing.events.InvoiceCreationInternalEvent;
import org.killbill.billing.events.InvoiceNotificationInternalEvent;
import org.killbill.billing.events.InvoicePaymentErrorInternalEvent;
import org.killbill.billing.events.InvoicePaymentInfoInternalEvent;
import org.killbill.billing.events.OverdueChangeInternalEvent;
import org.killbill.billing.events.PaymentErrorInternalEvent;
import org.killbill.billing.events.PaymentInfoInternalEvent;
import org.killbill.billing.events.PaymentPluginErrorInternalEvent;
import org.killbill.billing.events.SubscriptionInternalEvent;
import org.killbill.billing.events.TenantConfigChangeInternalEvent;
import org.killbill.billing.events.TenantConfigDeletionInternalEvent;
import org.killbill.billing.events.UserTagCreationInternalEvent;
import org.killbill.billing.events.UserTagDeletionInternalEvent;
import org.killbill.billing.lifecycle.glue.BusModule;
import org.killbill.billing.notification.plugin.api.BlockingStateMetadata;
import org.killbill.billing.notification.plugin.api.BroadcastMetadata;
import org.killbill.billing.notification.plugin.api.ExtBusEventType;
import org.killbill.billing.notification.plugin.api.InvoiceNotificationMetadata;
import org.killbill.billing.notification.plugin.api.InvoicePaymentMetadata;
import org.killbill.billing.notification.plugin.api.PaymentMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata;
import org.killbill.billing.notification.plugin.api.SubscriptionMetadata.ActionType;
import org.killbill.billing.notification.plugin.api.TagMetadata;
import org.killbill.billing.notification.plugin.api.TenantConfigMetadata;
import org.killbill.billing.platform.api.KillbillService.KILLBILL_SERVICES;
import org.killbill.billing.subscription.api.SubscriptionBaseTransitionType;
import org.killbill.billing.util.callcontext.CallOrigin;
import org.killbill.billing.util.callcontext.InternalCallContextFactory;
import org.killbill.billing.util.callcontext.TenantContext;
import org.killbill.billing.util.callcontext.UserType;
import org.killbill.bus.api.BusEvent;
import org.killbill.bus.api.PersistentBus;
import org.killbill.bus.api.PersistentBus.EventBusException;
import org.killbill.commons.eventbus.AllowConcurrentEvents;
import org.killbill.commons.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class BeatrixListener {

    private static final Logger log = LoggerFactory.getLogger(BeatrixListener.class);

    // External bus: Do not use BusOptimizer
    private final PersistentBus externalBus;
    private final InternalCallContextFactory internalCallContextFactory;

    protected ObjectMapper objectMapper;

    private interface SubscriptionTransitionStrategy {
        ExtBusEventType convert(SubscriptionBaseTransitionType transitionType);
    }

    private static final Map<SubscriptionBaseTransitionType, SubscriptionTransitionStrategy> SUBSCRIPTION_TRANSITION_STRATEGIES = createSubscriptionTransitionStrategies();

    private static Map<SubscriptionBaseTransitionType, SubscriptionTransitionStrategy> createSubscriptionTransitionStrategies() {
        final Map<SubscriptionBaseTransitionType, SubscriptionTransitionStrategy> strategies = new EnumMap<>(SubscriptionBaseTransitionType.class);
        strategies.put(SubscriptionBaseTransitionType.CREATE, transition -> ExtBusEventType.SUBSCRIPTION_CREATION);
        strategies.put(SubscriptionBaseTransitionType.TRANSFER, transition -> ExtBusEventType.SUBSCRIPTION_CREATION);
        strategies.put(SubscriptionBaseTransitionType.CANCEL, transition -> ExtBusEventType.SUBSCRIPTION_CANCEL);
        strategies.put(SubscriptionBaseTransitionType.PHASE, transition -> ExtBusEventType.SUBSCRIPTION_PHASE);
        strategies.put(SubscriptionBaseTransitionType.CHANGE, transition -> ExtBusEventType.SUBSCRIPTION_CHANGE);
        // No external UNDO_CHANGE event exists, so preserve the historical mapping.
        strategies.put(SubscriptionBaseTransitionType.UNDO_CHANGE, transition -> ExtBusEventType.SUBSCRIPTION_CHANGE);
        strategies.put(SubscriptionBaseTransitionType.UNCANCEL, transition -> ExtBusEventType.SUBSCRIPTION_UNCANCEL);
        strategies.put(SubscriptionBaseTransitionType.BCD_CHANGE, transition -> ExtBusEventType.SUBSCRIPTION_BCD_CHANGE);
        strategies.put(SubscriptionBaseTransitionType.QUANTITY_CHANGE, transition -> ExtBusEventType.SUBSCRIPTION_QUANTITY);
        strategies.put(SubscriptionBaseTransitionType.EXPIRED, transition -> ExtBusEventType.SUBSCRIPTION_EXPIRED);
        return Map.copyOf(strategies);
    }

    static ExtBusEventType mapSubscriptionTransitionType(final SubscriptionBaseTransitionType transitionType) {
        final SubscriptionTransitionStrategy strategy = SUBSCRIPTION_TRANSITION_STRATEGIES.get(transitionType);
        return strategy == null ? null : strategy.convert(transitionType);
    }

    @Inject
    public BeatrixListener(@Named(BusModule.EXTERNAL_BUS_NAMED) final PersistentBus externalBus,
                           final InternalCallContextFactory internalCallContextFactory) {
        this.externalBus = externalBus;
        this.internalCallContextFactory = internalCallContextFactory;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AllowConcurrentEvents
    @Subscribe
    public void handleAllInternalKillbillEvents(final BusInternalEvent event) {
        // No BusDispatcherOptimizer logic on purpose
        final InternalCallContext internalContext = internalCallContextFactory.createInternalCallContext(event.getSearchKey2(), event.getSearchKey1(), "BeatrixListener", CallOrigin.INTERNAL, UserType.SYSTEM, event.getUserToken());
        try {
            final BusEvent externalEvent = computeExtBusEventEntryFromBusInternalEvent(event, internalContext);
            if (externalEvent != null) {
                log.info("Sending extBusEvent='{}' from busEvent='{}'", externalEvent, event);
                externalBus.post(externalEvent);
            }
        } catch (final EventBusException e) {
            //
            // When using PersistentBus this should never be reached, because errors are caught at the 'ext' bus level and retried until either success or event row is moved to FAILED status.
            // However when using InMemoryBus, this can happen as there is no retry logic (at the 'ext' bus level) and so we should re-throw at this level to kick-in the retry logic from the 'main' bus
            // (The use of RuntimeException is somewhat arbitrary)
            //
            log.warn("Failed to post event {}", event, e);
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.warn("Failed to post event {}", event, e);
        }
    }

    private static final class ConvertedEvent {
        private final ObjectType objectType; private final UUID objectId; private final ExtBusEventType eventBusType; private final String metaData; private final UUID accountId;
        private ConvertedEvent(final ObjectType objectType, final UUID objectId, final ExtBusEventType eventBusType, final String metaData, final UUID accountId) {
            this.objectType = objectType; this.objectId = objectId; this.eventBusType = eventBusType; this.metaData = metaData; this.accountId = accountId;
        }
    }
    private interface InternalEventConversionStrategy { ConvertedEvent convert(BusInternalEvent event, InternalCallContext context) throws JsonProcessingException; }
    private final Map<BusInternalEventType, InternalEventConversionStrategy> internalEventStrategies = createInternalEventStrategies();
    private Map<BusInternalEventType, InternalEventConversionStrategy> createInternalEventStrategies() {
        final Map<BusInternalEventType, InternalEventConversionStrategy> strategies = new EnumMap<>(BusInternalEventType.class);
        strategies.put(BusInternalEventType.ACCOUNT_CREATE, (event, context) -> convertAccountCreate(event));
        strategies.put(BusInternalEventType.ACCOUNT_CHANGE, (event, context) -> convertAccountChange(event));
        strategies.put(BusInternalEventType.SUBSCRIPTION_TRANSITION, (event, context) -> convertSubscriptionTransition(event));
        strategies.put(BusInternalEventType.BLOCKING_STATE, (event, context) -> convertBlockingState(event));
        strategies.put(BusInternalEventType.INVOICE_CREATION, (event, context) -> convertInvoiceCreation(event));
        strategies.put(BusInternalEventType.INVOICE_NOTIFICATION, (event, context) -> convertInvoiceNotification(event));
        strategies.put(BusInternalEventType.INVOICE_ADJUSTMENT, (event, context) -> convertInvoiceAdjustment(event));
        strategies.put(BusInternalEventType.INVOICE_PAYMENT_INFO, (event, context) -> convertInvoicePaymentInfo(event));
        strategies.put(BusInternalEventType.INVOICE_PAYMENT_ERROR, (event, context) -> convertInvoicePaymentError(event));
        strategies.put(BusInternalEventType.PAYMENT_INFO, (event, context) -> convertPaymentInfo(event));
        strategies.put(BusInternalEventType.PAYMENT_ERROR, (event, context) -> convertPaymentError(event));
        strategies.put(BusInternalEventType.PAYMENT_PLUGIN_ERROR, (event, context) -> convertPaymentPluginError(event));
        strategies.put(BusInternalEventType.OVERDUE_CHANGE, (event, context) -> convertOverdueChange(event));
        strategies.put(BusInternalEventType.USER_TAG_CREATION, (event, context) -> convertUserTagCreation(event));
        strategies.put(BusInternalEventType.CONTROL_TAG_CREATION, (event, context) -> convertControlTagCreation(event));
        strategies.put(BusInternalEventType.USER_TAG_DELETION, (event, context) -> convertUserTagDeletion(event));
        strategies.put(BusInternalEventType.CONTROL_TAG_DELETION, (event, context) -> convertControlTagDeletion(event));
        strategies.put(BusInternalEventType.CUSTOM_FIELD_CREATION, (event, context) -> convertCustomFieldCreation(event));
        strategies.put(BusInternalEventType.CUSTOM_FIELD_DELETION, (event, context) -> convertCustomFieldDeletion(event));
        strategies.put(BusInternalEventType.TENANT_CONFIG_CHANGE, (event, context) -> convertTenantConfigChange(event));
        strategies.put(BusInternalEventType.TENANT_CONFIG_DELETION, (event, context) -> convertTenantConfigDeletion(event));
        strategies.put(BusInternalEventType.BROADCAST_SERVICE, (event, context) -> convertBroadcastService(event));
        return Map.copyOf(strategies);
    }
    private BusEvent computeExtBusEventEntryFromBusInternalEvent(final BusInternalEvent event, final InternalCallContext context) throws JsonProcessingException {
        final InternalEventConversionStrategy strategy = internalEventStrategies.get(event.getBusEventType());
        final ConvertedEvent convertedEvent = strategy == null ? null : strategy.convert(event, context);
        if (convertedEvent == null || convertedEvent.eventBusType == null) return null;
        final TenantContext tenantContext = internalCallContextFactory.createTenantContext(context);
        final UUID accountId = convertedEvent.accountId == null ? getAccountId(event.getBusEventType(), convertedEvent.objectId, convertedEvent.objectType, tenantContext) : convertedEvent.accountId;
        return new DefaultBusExternalEvent(convertedEvent.objectId, convertedEvent.objectType, convertedEvent.eventBusType, accountId, tenantContext.getTenantId(), convertedEvent.metaData, context.getAccountRecordId(), context.getTenantRecordId(), context.getUserToken());
    }

    private ConvertedEvent convertAccountCreate(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final AccountCreationInternalEvent realEventACR = (AccountCreationInternalEvent) event;
                objectType = ObjectType.ACCOUNT;
                objectId = realEventACR.getId();
                eventBusType = ExtBusEventType.ACCOUNT_CREATION;
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertAccountChange(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final AccountChangeInternalEvent realEventACH = (AccountChangeInternalEvent) event;
                objectType = ObjectType.ACCOUNT;
                objectId = realEventACH.getAccountId();
                eventBusType = ExtBusEventType.ACCOUNT_CHANGE;
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertSubscriptionTransition(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final SubscriptionInternalEvent realEventST = (SubscriptionInternalEvent) event;
                objectType = ObjectType.SUBSCRIPTION;
                objectId = realEventST.getSubscriptionId();
                eventBusType = mapSubscriptionTransitionType(realEventST.getTransitionType());

                SubscriptionMetadata.ActionType actionType = (event instanceof EffectiveSubscriptionInternalEvent) ? ActionType.EFFECTIVE : ActionType.REQUESTED;
                final boolean isSubscriptionUndo = realEventST.getTransitionType() == SubscriptionBaseTransitionType.UNDO_CHANGE ? true : false;
                final SubscriptionMetadata subscriptionMetadataObj = new SubscriptionMetadata(actionType, realEventST.getBundleExternalKey(), isSubscriptionUndo);
                metaData = objectMapper.writeValueAsString(subscriptionMetadataObj);
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertBlockingState(final BusInternalEvent event) throws JsonProcessingException {
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
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertInvoiceCreation(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final InvoiceCreationInternalEvent realEventInv = (InvoiceCreationInternalEvent) event;
                objectType = ObjectType.INVOICE;
                objectId = realEventInv.getInvoiceId();
                eventBusType = ExtBusEventType.INVOICE_CREATION;
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertInvoiceNotification(final BusInternalEvent event) throws JsonProcessingException {
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
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertInvoiceAdjustment(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final InvoiceAdjustmentInternalEvent realEventInvAdj = (InvoiceAdjustmentInternalEvent) event;
                objectType = ObjectType.INVOICE;
                objectId = realEventInvAdj.getInvoiceId();
                eventBusType = ExtBusEventType.INVOICE_ADJUSTMENT;
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertInvoicePaymentInfo(final BusInternalEvent event) throws JsonProcessingException {
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
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertInvoicePaymentError(final BusInternalEvent event) throws JsonProcessingException {
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
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertPaymentInfo(final BusInternalEvent event) throws JsonProcessingException {
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
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertPaymentError(final BusInternalEvent event) throws JsonProcessingException {
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
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertPaymentPluginError(final BusInternalEvent event) throws JsonProcessingException {
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
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertOverdueChange(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final OverdueChangeInternalEvent realEventOC = (OverdueChangeInternalEvent) event;
                objectType = ObjectType.ACCOUNT;
                objectId = realEventOC.getOverdueObjectId();
                eventBusType = ExtBusEventType.OVERDUE_CHANGE;
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertUserTagCreation(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final UserTagCreationInternalEvent realUserTagEventCr = (UserTagCreationInternalEvent) event;
                objectType = ObjectType.TAG;
                objectId = realUserTagEventCr.getTagId();
                eventBusType = ExtBusEventType.TAG_CREATION;
                final TagMetadata userTagCreationMetadata = new TagMetadata(realUserTagEventCr.getTagDefinition().getName());
                metaData = objectMapper.writeValueAsString(userTagCreationMetadata);
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertControlTagCreation(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final ControlTagCreationInternalEvent realTagEventCr = (ControlTagCreationInternalEvent) event;
                objectType = ObjectType.TAG;
                objectId = realTagEventCr.getTagId();
                eventBusType = ExtBusEventType.TAG_CREATION;
                final TagMetadata controlTagCreationMetadata = new TagMetadata(realTagEventCr.getTagDefinition().getName());
                metaData = objectMapper.writeValueAsString(controlTagCreationMetadata);
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertUserTagDeletion(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final UserTagDeletionInternalEvent realUserTagEventDel = (UserTagDeletionInternalEvent) event;
                objectType = ObjectType.TAG;
                objectId = realUserTagEventDel.getTagId();
                eventBusType = ExtBusEventType.TAG_DELETION;
                final TagMetadata userTagDeletionMetadata = new TagMetadata(realUserTagEventDel.getTagDefinition().getName());
                metaData = objectMapper.writeValueAsString(userTagDeletionMetadata);
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertControlTagDeletion(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final ControlTagDeletionInternalEvent realTagEventDel = (ControlTagDeletionInternalEvent) event;
                objectType = ObjectType.TAG;
                objectId = realTagEventDel.getTagId();
                eventBusType = ExtBusEventType.TAG_DELETION;
                final TagMetadata controlTagDeletionMetadata = new TagMetadata(realTagEventDel.getTagDefinition().getName());
                metaData = objectMapper.writeValueAsString(controlTagDeletionMetadata);
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertCustomFieldCreation(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final CustomFieldCreationEvent realCustomFieldEventCr = (CustomFieldCreationEvent) event;
                objectType = ObjectType.CUSTOM_FIELD;
                objectId = realCustomFieldEventCr.getCustomFieldId();
                eventBusType = ExtBusEventType.CUSTOM_FIELD_CREATION;
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertCustomFieldDeletion(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final CustomFieldDeletionEvent realCustomFieldEventDel = (CustomFieldDeletionEvent) event;
                objectType = ObjectType.CUSTOM_FIELD;
                objectId = realCustomFieldEventDel.getCustomFieldId();
                eventBusType = ExtBusEventType.CUSTOM_FIELD_DELETION;
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertTenantConfigChange(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final TenantConfigChangeInternalEvent realTenantConfigEventChg = (TenantConfigChangeInternalEvent) event;
                objectType = ObjectType.TENANT_KVS;
                objectId = realTenantConfigEventChg.getId();
                eventBusType = ExtBusEventType.TENANT_CONFIG_CHANGE;
                final TenantConfigMetadata tenantConfigChangeMetadata = new TenantConfigMetadata(realTenantConfigEventChg.getKey());
                metaData = objectMapper.writeValueAsString(tenantConfigChangeMetadata);
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertTenantConfigDeletion(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final TenantConfigDeletionInternalEvent realTenantConfigEventDel = (TenantConfigDeletionInternalEvent) event;
                objectType = ObjectType.TENANT_KVS;
                objectId = null;
                eventBusType = ExtBusEventType.TENANT_CONFIG_DELETION;
                final TenantConfigMetadata tenantConfigDeletionMetadata = new TenantConfigMetadata(realTenantConfigEventDel.getKey());
                metaData = objectMapper.writeValueAsString(tenantConfigDeletionMetadata);
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }

    private ConvertedEvent convertBroadcastService(final BusInternalEvent event) throws JsonProcessingException {
        ObjectType objectType = null;
        UUID objectId = null;
        ExtBusEventType eventBusType = null;
        String metaData = null;
        UUID accountId = null;

                final BroadcastInternalEvent realBroadcastEvent = (BroadcastInternalEvent) event;
                objectType = ObjectType.SERVICE_BROADCAST;
                objectId = null;
                eventBusType = ExtBusEventType.BROADCAST_SERVICE;
                final BroadcastMetadata broadcastMetadata = new BroadcastMetadata(realBroadcastEvent.getServiceName(), realBroadcastEvent.getType(), realBroadcastEvent.getJsonEvent());
                metaData = objectMapper.writeValueAsString(broadcastMetadata);
        return new ConvertedEvent(objectType, objectId, eventBusType, metaData, accountId);
    }
    private UUID getAccountId(final BusInternalEventType eventType, @Nullable final UUID objectId, final ObjectType objectType, final TenantContext context) {
        // accountRecord_id is not set for ACCOUNT_CREATE event as we are in the transaction and value is known yet
        if (eventType == BusInternalEventType.ACCOUNT_CREATE) {
            return objectId;
        } else if (eventType == BusInternalEventType.TENANT_CONFIG_CHANGE || eventType == BusInternalEventType.TENANT_CONFIG_DELETION) {
            return null;
        } else if (objectId == null) {
            return null;
        } else {
            return internalCallContextFactory.getAccountId(objectId, objectType, context);
        }
    }
}
