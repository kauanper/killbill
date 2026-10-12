/*
 * Copyright 2010-2013 Ning, Inc.
 * Copyright 2014-2024 Groupon, Inc
 * Copyright 2020-2024 Equinix, Inc
 * Copyright 2014-2024 The Billing Project, LLC
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

package org.killbill.billing.invoice.template.formatters;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.killbill.billing.invoice.api.InvoiceItem;

/**
 * Concrete Decorator providing localized date formatting for start and end dates.
 */
public class DateFormattingInvoiceItem extends InvoiceItemDecorator {

    private final DateTimeFormatter dateFormatter;

    public DateFormattingInvoiceItem(final InvoiceItem delegate, final DateTimeFormatter dateFormatter) {
        super(delegate);
        this.dateFormatter = dateFormatter;
    }

    @Override
    public String getFormattedStartDate() {
        final LocalDate startDate = getStartDate();
        return (startDate == null || dateFormatter == null) ? null : startDate.toString(dateFormatter);
    }

    @Override
    public String getFormattedEndDate() {
        final LocalDate endDate = getEndDate();
        return (endDate == null || dateFormatter == null) ? null : endDate.toString(dateFormatter);
    }
}
