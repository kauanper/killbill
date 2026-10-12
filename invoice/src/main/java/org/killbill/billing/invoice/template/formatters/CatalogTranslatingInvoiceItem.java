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

import org.killbill.billing.invoice.api.InvoiceItem;
import org.killbill.billing.util.template.translation.Translator;
import org.killbill.commons.utils.Strings;

/**
 * Concrete Decorator providing catalog translations for item names and descriptions.
 */
public class CatalogTranslatingInvoiceItem extends InvoiceItemDecorator {

    private final Translator translator;

    public CatalogTranslatingInvoiceItem(final InvoiceItem delegate, final Translator translator) {
        super(delegate);
        this.translator = translator;
    }

    private String translate(final String value) {
        if (translator == null) {
            return Strings.nullToEmpty(value);
        }
        return Strings.nullToEmpty(translator.getTranslation(value));
    }

    @Override
    public String getDescription() {
        return translate(super.getDescription());
    }

    @Override
    public String getProductName() {
        return translate(super.getProductName());
    }

    @Override
    public String getPrettyProductName() {
        return translate(super.getPrettyProductName());
    }

    @Override
    public String getPlanName() {
        return translate(super.getPlanName());
    }

    @Override
    public String getPrettyPlanName() {
        return translate(super.getPrettyPlanName());
    }

    @Override
    public String getPhaseName() {
        return translate(super.getPhaseName());
    }

    @Override
    public String getPrettyPhaseName() {
        return translate(super.getPrettyPhaseName());
    }

    @Override
    public String getUsageName() {
        return translate(super.getUsageName());
    }

    @Override
    public String getPrettyUsageName() {
        return translate(super.getPrettyUsageName());
    }
}
