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

import java.util.UUID;

import javax.annotation.Nullable;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.killbill.billing.ObjectType;
import org.killbill.billing.beatrix.extbus.strategy.ExternalEventConversionResult;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionFactory;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionStrategy;
import org.killbill.billing.callcontext.InternalCallContext;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.lifecycle.glue.BusModule;
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

import java.util.UUID;

import javax.annotation.Nullable;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.killbill.billing.ObjectType;
import org.killbill.billing.beatrix.extbus.strategy.ExternalEventConversionResult;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionFactory;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionStrategy;
import org.killbill.billing.callcontext.InternalCallContext;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.lifecycle.glue.BusModule;
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

import java.util.UUID;

import javax.annotation.Nullable;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.killbill.billing.ObjectType;
import org.killbill.billing.beatrix.extbus.strategy.ExternalEventConversionResult;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionFactory;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionStrategy;
import org.killbill.billing.callcontext.InternalCallContext;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.lifecycle.glue.BusModule;
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

import java.util.UUID;

import javax.annotation.Nullable;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.killbill.billing.ObjectType;
import org.killbill.billing.beatrix.extbus.strategy.ExternalEventConversionResult;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionFactory;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionStrategy;
import org.killbill.billing.callcontext.InternalCallContext;
import org.killbill.billing.events.BusInternalEvent;
import org.killbill.billing.events.BusInternalEvent.BusInternalEventType;
import org.killbill.billing.lifecycle.glue.BusModule;
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

    private final InternalEventConversionFactory conversionFactory;

    @Inject
    public BeatrixListener(@Named(BusModule.EXTERNAL_BUS_NAMED) final PersistentBus externalBus,
                           final InternalCallContextFactory internalCallContextFactory,
                           final InternalEventConversionFactory conversionFactory) {
        this.externalBus = externalBus;
        this.internalCallContextFactory = internalCallContextFactory;
        this.conversionFactory = conversionFactory;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Convenience constructor for focused unit tests; production obtains the factory from Guice.
    BeatrixListener(final PersistentBus externalBus, final InternalCallContextFactory internalCallContextFactory) {
        this(externalBus, internalCallContextFactory, InternalEventConversionFactory.createDefault());
    }

    private BusEvent computeExtBusEventEntryFromBusInternalEvent(final BusInternalEvent event, final InternalCallContext context) throws JsonProcessingException {
        final InternalEventConversionStrategy strategy = conversionFactory.getStrategy(event.getBusEventType());
        final ExternalEventConversionResult converted = strategy == null ? null : strategy.convert(event, objectMapper);
        if (converted == null || converted.eventBusType == null) return null;
        final TenantContext tenantContext = internalCallContextFactory.createTenantContext(context);
        final UUID accountId = converted.accountId == null ? getAccountId(event.getBusEventType(), converted.objectId, converted.objectType, tenantContext) : converted.accountId;
        return new DefaultBusExternalEvent(converted.objectId, converted.objectType, converted.eventBusType, accountId, tenantContext.getTenantId(), converted.metaData, context.getAccountRecordId(), context.getTenantRecordId(), context.getUserToken());
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
