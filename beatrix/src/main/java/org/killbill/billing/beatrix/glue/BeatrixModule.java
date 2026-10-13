/*
 * Copyright 2010-2013 Ning, Inc.
 * Copyright 2014 Groupon, Inc
 * Copyright 2014 The Billing Project, LLC
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

package org.killbill.billing.beatrix.glue;

import org.killbill.billing.beatrix.DefaultBeatrixService;
import org.killbill.billing.beatrix.bus.api.BeatrixService;
import org.killbill.billing.beatrix.extbus.BeatrixListener;
import org.killbill.billing.beatrix.extbus.strategy.AccountChangeEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.AccountCreateEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.BlockingStateEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.BroadcastServiceEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.ControlTagCreationEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.ControlTagDeletionEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.CustomFieldCreationEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.CustomFieldDeletionEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.InternalEventConversionStrategy;
import org.killbill.billing.beatrix.extbus.strategy.InvoiceAdjustmentEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.InvoiceCreationEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.InvoiceNotificationEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.InvoicePaymentErrorEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.InvoicePaymentInfoEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.OverdueChangeEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.PaymentErrorEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.PaymentInfoEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.PaymentPluginErrorEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.SubscriptionTransitionEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.TenantConfigChangeEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.TenantConfigDeletionEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.UserTagCreationEventStrategy;
import org.killbill.billing.beatrix.extbus.strategy.UserTagDeletionEventStrategy;
import org.killbill.billing.platform.api.KillbillConfigSource;
import org.killbill.billing.util.glue.KillBillModule;

import com.google.inject.multibindings.Multibinder;

public class BeatrixModule extends KillBillModule {

    public BeatrixModule(final KillbillConfigSource configSource) {
        super(configSource);
    }

    @Override
    protected void configure() {
        installExternalBus();
        bindEventConversionStrategies();
    }

    private void bindEventConversionStrategies() {
        final Multibinder<InternalEventConversionStrategy> strategies = Multibinder.newSetBinder(binder(), InternalEventConversionStrategy.class);
        strategies.addBinding().to(AccountCreateEventStrategy.class);
        strategies.addBinding().to(AccountChangeEventStrategy.class);
        strategies.addBinding().to(SubscriptionTransitionEventStrategy.class);
        strategies.addBinding().to(BlockingStateEventStrategy.class);
        strategies.addBinding().to(InvoiceCreationEventStrategy.class);
        strategies.addBinding().to(InvoiceNotificationEventStrategy.class);
        strategies.addBinding().to(InvoiceAdjustmentEventStrategy.class);
        strategies.addBinding().to(InvoicePaymentInfoEventStrategy.class);
        strategies.addBinding().to(InvoicePaymentErrorEventStrategy.class);
        strategies.addBinding().to(PaymentInfoEventStrategy.class);
        strategies.addBinding().to(PaymentErrorEventStrategy.class);
        strategies.addBinding().to(PaymentPluginErrorEventStrategy.class);
        strategies.addBinding().to(OverdueChangeEventStrategy.class);
        strategies.addBinding().to(UserTagCreationEventStrategy.class);
        strategies.addBinding().to(ControlTagCreationEventStrategy.class);
        strategies.addBinding().to(UserTagDeletionEventStrategy.class);
        strategies.addBinding().to(ControlTagDeletionEventStrategy.class);
        strategies.addBinding().to(CustomFieldCreationEventStrategy.class);
        strategies.addBinding().to(CustomFieldDeletionEventStrategy.class);
        strategies.addBinding().to(TenantConfigChangeEventStrategy.class);
        strategies.addBinding().to(TenantConfigDeletionEventStrategy.class);
        strategies.addBinding().to(BroadcastServiceEventStrategy.class);
    }

    protected void installExternalBus() {
        bind(BeatrixService.class).to(DefaultBeatrixService.class);
        bind(DefaultBeatrixService.class).asEagerSingleton();

        bind(BeatrixListener.class).asEagerSingleton();
    }
}
