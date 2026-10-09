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

import org.killbill.billing.invoice.InvoiceTestSuiteNoDB;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.util.template.translation.Translator;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCatalogTranslatingInvoiceItem extends InvoiceTestSuiteNoDB {

    @Test(groups = "fast")
    public void testCatalogTranslations() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getDescription()).thenReturn("desc_key");
        Mockito.when(mockItem.getPlanName()).thenReturn("plan_key");
        Mockito.when(mockItem.getPrettyPlanName()).thenReturn("pretty_plan_key");
        Mockito.when(mockItem.getProductName()).thenReturn("prod_key");
        Mockito.when(mockItem.getPrettyProductName()).thenReturn("pretty_prod_key");
        Mockito.when(mockItem.getPhaseName()).thenReturn("phase_key");
        Mockito.when(mockItem.getPrettyPhaseName()).thenReturn("pretty_phase_key");
        Mockito.when(mockItem.getUsageName()).thenReturn("usage_key");
        Mockito.when(mockItem.getPrettyUsageName()).thenReturn("pretty_usage_key");

        final Translator mockTranslator = Mockito.mock(Translator.class);
        Mockito.when(mockTranslator.getTranslation("desc_key")).thenReturn("Translated Description");
        Mockito.when(mockTranslator.getTranslation("plan_key")).thenReturn("Translated Plan");
        Mockito.when(mockTranslator.getTranslation("pretty_plan_key")).thenReturn("Translated Pretty Plan");
        Mockito.when(mockTranslator.getTranslation("prod_key")).thenReturn("Translated Product");
        Mockito.when(mockTranslator.getTranslation("pretty_prod_key")).thenReturn("Translated Pretty Product");
        Mockito.when(mockTranslator.getTranslation("phase_key")).thenReturn("Translated Phase");
        Mockito.when(mockTranslator.getTranslation("pretty_phase_key")).thenReturn("Translated Pretty Phase");
        Mockito.when(mockTranslator.getTranslation("usage_key")).thenReturn("Translated Usage");
        Mockito.when(mockTranslator.getTranslation("pretty_usage_key")).thenReturn("Translated Pretty Usage");

        final CatalogTranslatingInvoiceItem decorated = new CatalogTranslatingInvoiceItem(mockItem, mockTranslator);

        Assert.assertEquals(decorated.getDescription(), "Translated Description");
        Assert.assertEquals(decorated.getPlanName(), "Translated Plan");
        Assert.assertEquals(decorated.getPrettyPlanName(), "Translated Pretty Plan");
        Assert.assertEquals(decorated.getProductName(), "Translated Product");
        Assert.assertEquals(decorated.getPrettyProductName(), "Translated Pretty Product");
        Assert.assertEquals(decorated.getPhaseName(), "Translated Phase");
        Assert.assertEquals(decorated.getPrettyPhaseName(), "Translated Pretty Phase");
        Assert.assertEquals(decorated.getUsageName(), "Translated Usage");
        Assert.assertEquals(decorated.getPrettyUsageName(), "Translated Pretty Usage");
    }

    @Test(groups = "fast")
    public void testNullStringsReturnEmptyString() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getDescription()).thenReturn(null);
        Mockito.when(mockItem.getPlanName()).thenReturn(null);

        final Translator mockTranslator = Mockito.mock(Translator.class);
        Mockito.when(mockTranslator.getTranslation(null)).thenReturn(null);

        final CatalogTranslatingInvoiceItem decorated = new CatalogTranslatingInvoiceItem(mockItem, mockTranslator);

        Assert.assertEquals(decorated.getDescription(), "");
        Assert.assertEquals(decorated.getPlanName(), "");
    }

    @Test(groups = "fast")
    public void testNullTranslator() {
        final InvoiceItem mockItem = Mockito.mock(InvoiceItem.class);
        Mockito.when(mockItem.getDescription()).thenReturn("raw_desc");
        Mockito.when(mockItem.getPlanName()).thenReturn(null);

        final CatalogTranslatingInvoiceItem decorated = new CatalogTranslatingInvoiceItem(mockItem, null);

        Assert.assertEquals(decorated.getDescription(), "raw_desc");
        Assert.assertEquals(decorated.getPlanName(), "");
    }
}
