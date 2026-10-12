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

import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.killbill.billing.invoice.InvoiceTestSuiteNoDB;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestDateFormattingInvoiceItem extends InvoiceTestSuiteNoDB {

    @Test(groups = "fast")
    public void testDateFormattingWithDates() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getStartDate()).thenReturn(new LocalDate(2023, 5, 10));
        Mockito.when(mockItem.getEndDate()).thenReturn(new LocalDate(2023, 6, 10));

        final DateTimeFormatter formatter = DateTimeFormat.mediumDate().withLocale(Locale.US);
        final DateFormattingInvoiceItem decorated = new DateFormattingInvoiceItem(mockItem, formatter);

        Assert.assertEquals(decorated.getFormattedStartDate(), "May 10, 2023");
        Assert.assertEquals(decorated.getFormattedEndDate(), "Jun 10, 2023");
        Assert.assertEquals(decorated.getStartDate(), new LocalDate(2023, 5, 10));
        Assert.assertEquals(decorated.getEndDate(), new LocalDate(2023, 6, 10));
    }

    @Test(groups = "fast")
    public void testDateFormattingWithNullDates() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getStartDate()).thenReturn(null);
        Mockito.when(mockItem.getEndDate()).thenReturn(null);

        final DateTimeFormatter formatter = DateTimeFormat.mediumDate().withLocale(Locale.US);
        final DateFormattingInvoiceItem decorated = new DateFormattingInvoiceItem(mockItem, formatter);

        Assert.assertNull(decorated.getFormattedStartDate());
        Assert.assertNull(decorated.getFormattedEndDate());
    }

    @Test(groups = "fast")
    public void testDateFormattingWithNullFormatter() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getStartDate()).thenReturn(new LocalDate(2023, 5, 10));

        final DateFormattingInvoiceItem decorated = new DateFormattingInvoiceItem(mockItem, null);

        Assert.assertNull(decorated.getFormattedStartDate());
        Assert.assertNull(decorated.getFormattedEndDate());
    }
}
