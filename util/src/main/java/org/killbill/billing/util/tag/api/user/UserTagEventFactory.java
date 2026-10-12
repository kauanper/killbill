/*
 * Copyright 2010-2012 Ning, Inc.
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

package org.killbill.billing.util.tag.api.user;

import java.util.UUID;

import org.killbill.billing.ObjectType;
import org.killbill.billing.events.UserTagCreationInternalEvent;
import org.killbill.billing.events.UserTagDefinitionCreationInternalEvent;
import org.killbill.billing.events.UserTagDefinitionDeletionInternalEvent;
import org.killbill.billing.events.UserTagDeletionInternalEvent;
import org.killbill.billing.util.tag.DefaultTagDefinition;
import org.killbill.billing.util.tag.dao.TagDefinitionModelDao;

public class UserTagEventFactory implements TagEventAbstractFactory {

    @Override
    public UserTagDefinitionCreationInternalEvent createTagDefinitionCreationEvent(final UUID tagDefinitionId, final TagDefinitionModelDao tagDefinition, final Long searchKey1, final Long searchKey2, final UUID userToken) {
        return new DefaultUserTagDefinitionCreationEvent(tagDefinitionId, new DefaultTagDefinition(tagDefinition, false), searchKey1, searchKey2, userToken);
    }

    @Override
    public UserTagDefinitionDeletionInternalEvent createTagDefinitionDeletionEvent(final UUID tagDefinitionId, final TagDefinitionModelDao tagDefinition, final Long searchKey1, final Long searchKey2, final UUID userToken) {
        return new DefaultUserTagDefinitionDeletionEvent(tagDefinitionId, new DefaultTagDefinition(tagDefinition, false), searchKey1, searchKey2, userToken);
    }

    @Override
    public UserTagCreationInternalEvent createTagCreationEvent(final UUID tagId, final UUID objectId, final ObjectType objectType, final TagDefinitionModelDao tagDefinition, final Long searchKey1, final Long searchKey2, final UUID userToken) {
        return new DefaultUserTagCreationEvent(tagId, objectId, objectType, new DefaultTagDefinition(tagDefinition, false), searchKey1, searchKey2, userToken);
    }

    @Override
    public UserTagDeletionInternalEvent createTagDeletionEvent(final UUID tagId, final UUID objectId, final ObjectType objectType, final TagDefinitionModelDao tagDefinition, final Long searchKey1, final Long searchKey2, final UUID userToken) {
        return new DefaultUserTagDeletionEvent(tagId, objectId, objectType, new DefaultTagDefinition(tagDefinition, false), searchKey1, searchKey2, userToken);
    }
}
