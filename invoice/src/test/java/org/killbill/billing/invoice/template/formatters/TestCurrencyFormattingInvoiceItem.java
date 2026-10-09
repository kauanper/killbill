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
import java.util.Locale;

import org.killbill.billing.catalog.api.Currency;
import org.killbill.billing.invoice.InvoiceTestSuiteNoDB;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCurrencyFormattingInvoiceItem extends InvoiceTestSuiteNoDB {

    @Test(groups = "fast")
    public void testCurrencyFormatting() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getAmount()).thenReturn(new BigDecimal("123.45"));
        Mockito.when(mockItem.getCurrency()).thenReturn(Currency.USD);

        final CurrencyFormattingInvoiceItem decorated = new CurrencyFormattingInvoiceItem(mockItem, Locale.US);

        Assert.assertEquals(decorated.getAmount(), new BigDecimal("123.45"));
        Assert.assertEquals(decorated.getCurrency(), Currency.USD);
        Assert.assertEquals(decorated.getFormattedAmount(), "$123.45");
    }

    @Test(groups = "fast")
    public void testNullAmountDefaultsToZero() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getAmount()).thenReturn(null);
        Mockito.when(mockItem.getCurrency()).thenReturn(Currency.USD);

        final CurrencyFormattingInvoiceItem decorated = new CurrencyFormattingInvoiceItem(mockItem, Locale.US);

        Assert.assertEquals(decorated.getAmount(), BigDecimal.ZERO);
        Assert.assertEquals(decorated.getFormattedAmount(), "$0.00");
    }

    @Test(groups = "fast")
    public void testNullCurrencyReturnsNull() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getAmount()).thenReturn(BigDecimal.TEN);
        Mockito.when(mockItem.getCurrency()).thenReturn(null);

        final CurrencyFormattingInvoiceItem decorated = new CurrencyFormattingInvoiceItem(mockItem, Locale.US);

        Assert.assertNull(decorated.getFormattedAmount());
    }
}
