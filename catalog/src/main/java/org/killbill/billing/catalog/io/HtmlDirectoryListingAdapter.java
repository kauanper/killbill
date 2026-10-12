/*
 * Copyright 2026 The Billing Project, LLC
 * Licensed under the Apache License, Version 2.0.
 */
package org.killbill.billing.catalog.io;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Adapts an HTML directory listing to the ordered list of href references expected by the catalog loader. */
final class HtmlDirectoryListingAdapter {

    private static final Pattern HREF = Pattern.compile("\\bhref\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)')", Pattern.CASE_INSENSITIVE);

    List<String> extractHrefs(final String html) {
        final List<String> references = new ArrayList<>();
        final Matcher matcher = HREF.matcher(html);
        while (matcher.find()) {
            references.add(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
        }
        return references;
    }
}
