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
import org.killbill.billing.util.bcd.BillCycleDayCalculator;

public class InAdvanceBillingIntervalStrategy implements BillingIntervalStrategy {

    @Override
    public LocalDate calculateEffectiveEndDate(final BillingIntervalDetail context) {
        final LocalDate endDate = context.getEndDate();
        final LocalDate targetDate = context.getTargetDate();
        final LocalDate firstBillingCycleDate = context.getFirstBillingCycleDate();

        // We have an endDate and the targetDate is greater or equal to our endDate => return it
        if (endDate != null && !targetDate.isBefore(endDate)) {
            return endDate;
        }

        if (targetDate.isBefore(firstBillingCycleDate)) {
            return firstBillingCycleDate;
        }

        int numberOfPeriods = 0;
        LocalDate proposedDate = firstBillingCycleDate;

        while (!proposedDate.isAfter(targetDate)) {
            proposedDate = context.getFutureBillingDateFor(numberOfPeriods);
            numberOfPeriods += 1;
        }
        proposedDate = BillCycleDayCalculator.alignProposedBillCycleDate(proposedDate, context.getBillingCycleDay(), context.getBillingPeriod());

        // The proposedDate is greater to our endDate => return it
        if (endDate != null && endDate.isBefore(proposedDate)) {
            return endDate;
        } else {
            return proposedDate;
        }
    }
}
