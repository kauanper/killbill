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
import java.util.Objects;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.invoice.api.InvoiceItemType;
import org.killbill.billing.invoice.api.formatters.InvoiceItemFormatter;

/**
 * Abstract GoF Decorator base class for {@link InvoiceItem} and {@link InvoiceItemFormatter}.
 * Forwards all standard calls to the encapsulated delegate.
 */
public abstract class InvoiceItemDecorator implements InvoiceItemFormatter {

    protected final InvoiceItem delegate;

    public InvoiceItemDecorator(final InvoiceItem delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate cannot be null");
    }

    public InvoiceItem getDelegate() {
        return delegate;
    }

    @Override
    public UUID getId() {
        return delegate.getId();
    }

    @Override
    public DateTime getCreatedDate() {
        return delegate.getCreatedDate();
    }

    @Override
    public DateTime getUpdatedDate() {
        return delegate.getUpdatedDate();
    }

    @Override
    public InvoiceItemType getInvoiceItemType() {
        return delegate.getInvoiceItemType();
    }

    @Override
    public UUID getInvoiceId() {
        return delegate.getInvoiceId();
    }

    @Override
    public UUID getAccountId() {
        return delegate.getAccountId();
    }

    @Override
    public UUID getChildAccountId() {
        return delegate.getChildAccountId();
    }

    @Override
    public LocalDate getStartDate() {
        return delegate.getStartDate();
    }

    @Override
    public LocalDate getEndDate() {
        return delegate.getEndDate();
    }

    @Override
    public BigDecimal getAmount() {
        return delegate.getAmount();
    }

    @Override
    public Currency getCurrency() {
        return delegate.getCurrency();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public UUID getBundleId() {
        return delegate.getBundleId();
    }

    @Override
    public UUID getSubscriptionId() {
        return delegate.getSubscriptionId();
    }

    @Override
    public String getProductName() {
        return delegate.getProductName();
    }

    @Override
    public String getPrettyProductName() {
        return delegate.getPrettyProductName();
    }

    @Override
    public String getPlanName() {
        return delegate.getPlanName();
    }

    @Override
    public String getPrettyPlanName() {
        return delegate.getPrettyPlanName();
    }

    @Override
    public String getPhaseName() {
        return delegate.getPhaseName();
    }

    @Override
    public String getPrettyPhaseName() {
        return delegate.getPrettyPhaseName();
    }

    @Override
    public String getUsageName() {
        return delegate.getUsageName();
    }

    @Override
    public String getPrettyUsageName() {
        return delegate.getPrettyUsageName();
    }

    @Override
    public BigDecimal getRate() {
        return delegate.getRate();
    }

    @Override
    public UUID getLinkedItemId() {
        return delegate.getLinkedItemId();
    }

    @Override
    public BigDecimal getQuantity() {
        return delegate.getQuantity();
    }

    @Override
    public String getItemDetails() {
        return delegate.getItemDetails();
    }

    @Override
    public DateTime getCatalogEffectiveDate() {
        return delegate.getCatalogEffectiveDate();
    }

    @Override
    public boolean matches(final Object other) {
        return delegate.matches(other);
    }

    @Override
    public String getFormattedStartDate() {
        if (delegate instanceof InvoiceItemFormatter) {
            return ((InvoiceItemFormatter) delegate).getFormattedStartDate();
        }
        return null;
    }

    @Override
    public String getFormattedEndDate() {
        if (delegate instanceof InvoiceItemFormatter) {
            return ((InvoiceItemFormatter) delegate).getFormattedEndDate();
        }
        return null;
    }

    @Override
    public String getFormattedAmount() {
        if (delegate instanceof InvoiceItemFormatter) {
            return ((InvoiceItemFormatter) delegate).getFormattedAmount();
        }
        return null;
    }
}
