/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
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
import java.util.ResourceBundle;
import java.util.UUID;

import org.joda.time.format.DateTimeFormatter;
import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.util.template.translation.DefaultCatalogTranslator;

/**
 * Format invoice item fields
 */
public class DefaultInvoiceItemFormatter extends InvoiceItemDecorator {

    public DefaultInvoiceItemFormatter(final String defaultLocale,
                                       final String catalogBundlePath,
                                       final InvoiceItem item,
                                       final DateTimeFormatter dateFormatter,
                                       final Locale locale,
                                       final ResourceBundle bundle,
                                       final ResourceBundle defaultBundle) {
        super(createDecoratedChain(item, dateFormatter, locale, bundle, defaultBundle));
    }

    private static InvoiceItem createDecoratedChain(final InvoiceItem item,
                                                    final DateTimeFormatter dateFormatter,
                                                    final Locale locale,
                                                    final ResourceBundle bundle,
                                                    final ResourceBundle defaultBundle) {
        InvoiceItem current = item;
        current = new CatalogTranslatingInvoiceItem(current, new DefaultCatalogTranslator(bundle, defaultBundle));
        current = new CurrencyFormattingInvoiceItem(current, locale);
        current = new DateFormattingInvoiceItem(current, dateFormatter);
        return current;
    }

    @Override
    public BigDecimal getRate() {
        return BigDecimal.ZERO;
    }

    @Override
    public UUID getLinkedItemId() {
        return null;
    }

    @Override
    public boolean matches(final Object other) {
        throw new UnsupportedOperationException();
    }
}
