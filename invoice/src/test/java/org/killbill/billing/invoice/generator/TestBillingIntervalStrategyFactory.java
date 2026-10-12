/*
 * Copyright 2010-2014 Ning, Inc.
 * Copyright 2014-2020 Groupon, Inc
 * Copyright 2020-2022 Equinix, Inc
 * Copyright 2014-2022 The Billing Project, LLC
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

package org.killbill.billing.invoice.generator;

import org.joda.time.LocalDate;
import org.killbill.billing.catalog.api.BillingMode;
import org.killbill.billing.catalog.api.BillingPeriod;
import org.killbill.billing.invoice.InvoiceTestSuiteNoDB;
import org.killbill.billing.util.config.definition.InvoiceConfig.InArrearMode;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestBillingIntervalStrategyFactory extends InvoiceTestSuiteNoDB {

    @Test(groups = "fast")
    public void testStrategyResolution() {
        final BillingIntervalStrategy inAdvanceDefault = BillingIntervalStrategyFactory.create(BillingMode.IN_ADVANCE, InArrearMode.DEFAULT);
        Assert.assertTrue(inAdvanceDefault instanceof InAdvanceBillingIntervalStrategy);

        final BillingIntervalStrategy inAdvanceGreedy = BillingIntervalStrategyFactory.create(BillingMode.IN_ADVANCE, InArrearMode.GREEDY);
        Assert.assertTrue(inAdvanceGreedy instanceof InAdvanceBillingIntervalStrategy);

        final BillingIntervalStrategy inArrearDefault = BillingIntervalStrategyFactory.create(BillingMode.IN_ARREAR, InArrearMode.DEFAULT);
        Assert.assertTrue(inArrearDefault instanceof InArrearBillingIntervalStrategy);

        final BillingIntervalStrategy inArrearGreedy = BillingIntervalStrategyFactory.create(BillingMode.IN_ARREAR, InArrearMode.GREEDY);
        Assert.assertTrue(inArrearGreedy instanceof InArrearGreedyBillingIntervalStrategy);
    }

    @Test(groups = "fast")
    public void testBillingIntervalDetailStrategyWiring() {
        final LocalDate start = new LocalDate("2022-11-07");
        final LocalDate targetDate = new LocalDate("2022-12-01");
        final int bcd = 7;

        final BillingIntervalDetail detailInAdvance = new BillingIntervalDetail(start, null, targetDate, bcd, BillingPeriod.MONTHLY, BillingMode.IN_ADVANCE, InArrearMode.DEFAULT);
        Assert.assertTrue(detailInAdvance.getStrategy() instanceof InAdvanceBillingIntervalStrategy);
        Assert.assertFalse(detailInAdvance.isInArrearGreedy());

        final BillingIntervalDetail detailInArrear = new BillingIntervalDetail(start, null, targetDate, bcd, BillingPeriod.MONTHLY, BillingMode.IN_ARREAR, InArrearMode.DEFAULT);
        Assert.assertTrue(detailInArrear.getStrategy() instanceof InArrearBillingIntervalStrategy);
        Assert.assertFalse(detailInArrear.isInArrearGreedy());

        final BillingIntervalDetail detailInArrearGreedy = new BillingIntervalDetail(start, null, targetDate, bcd, BillingPeriod.MONTHLY, BillingMode.IN_ARREAR, InArrearMode.GREEDY);
        Assert.assertTrue(detailInArrearGreedy.getStrategy() instanceof InArrearGreedyBillingIntervalStrategy);
        Assert.assertTrue(detailInArrearGreedy.isInArrearGreedy());
    }

    @Test(groups = "fast")
    public void testCustomStrategyInjection() {
        final LocalDate start = new LocalDate("2022-11-07");
        final LocalDate targetDate = new LocalDate("2022-12-01");
        final LocalDate customEndDate = new LocalDate("2022-12-15");
        final int bcd = 7;

        final BillingIntervalStrategy customStrategy = new BillingIntervalStrategy() {
            @Override
            public LocalDate calculateEffectiveEndDate(final BillingIntervalDetail context) {
                return customEndDate;
            }
        };

        final BillingIntervalDetail detailCustom = new BillingIntervalDetail(start, null, targetDate, bcd, BillingPeriod.MONTHLY, BillingMode.IN_ADVANCE, customStrategy);
        Assert.assertSame(detailCustom.getStrategy(), customStrategy);
        Assert.assertEquals(detailCustom.getEffectiveEndDate(), customEndDate);
    }
}
