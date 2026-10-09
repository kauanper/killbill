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

public class InArrearBillingIntervalStrategy implements BillingIntervalStrategy {

    @Override
    public LocalDate calculateEffectiveEndDate(final BillingIntervalDetail context) {
        final LocalDate endDate = context.getEndDate();
        final LocalDate targetDate = context.getTargetDate();
        final LocalDate firstBillingCycleDate = context.getFirstBillingCycleDate();

        //
        // If we have an event mid-billing period (CHANGE, CANCELLATION) that aligns
        // with the target date, we bill immediately for the period instead of waiting for
        // the next billing cycle date, a.k.a firstBillingCycleDate. See #1907
        //
        // The following condition may be even more generic, but targetDate will typically align with the event so perhaps unnecessary:
        // if (endDate != null && targetDate.compareTo(endDate) >= 0 && targetDate.isBefore(cutoffStartDt)) { ...}
        if (endDate != null && targetDate.compareTo(endDate) == 0) {
            return targetDate;
        }

        if (targetDate.isBefore(firstBillingCycleDate)) {
            // Nothing to bill for, hasSomethingToBill will return false
            return null;
        }

        if (endDate != null && endDate.isBefore(firstBillingCycleDate)) {
            return endDate;
        }

        int numberOfPeriods = 0;
        LocalDate proposedDate = firstBillingCycleDate;
        LocalDate nextProposedDate = context.getFutureBillingDateFor(numberOfPeriods);
        while (!nextProposedDate.isAfter(targetDate)) {
            proposedDate = nextProposedDate;
            numberOfPeriods += 1;
            nextProposedDate = context.getFutureBillingDateFor(numberOfPeriods);
        }

        // We honor the endDate as long as it does not go beyond our targetDate (by construction this cannot be after the nextProposedDate neither.
        if (endDate != null && !endDate.isAfter(targetDate)) {
            return endDate;
        } else {
            return proposedDate;
        }
    }
}
