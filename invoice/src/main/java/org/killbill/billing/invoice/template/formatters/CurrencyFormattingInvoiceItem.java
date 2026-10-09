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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.api.InvoiceItem;

/**
 * Concrete Decorator providing localized currency and amount formatting.
 */
public class CurrencyFormattingInvoiceItem extends InvoiceItemDecorator {

    private final Locale locale;

    public CurrencyFormattingInvoiceItem(final InvoiceItem delegate, final Locale locale) {
        super(delegate);
        this.locale = locale != null ? locale : Locale.getDefault();
    }

    @Override
    public BigDecimal getAmount() {
        return Objects.requireNonNullElse(super.getAmount(), BigDecimal.ZERO);
    }

    @Override
    public String getFormattedAmount() {
        final Currency currency = getCurrency();
        if (currency == null) {
            return null;
        }
        final NumberFormat number = NumberFormat.getCurrencyInstance(locale);
        number.setCurrency(java.util.Currency.getInstance(currency.toString()));
        return number.format(getAmount());
    }
}
