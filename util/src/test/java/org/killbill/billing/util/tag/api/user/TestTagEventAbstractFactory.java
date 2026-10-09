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
import org.killbill.billing.events.ControlTagCreationInternalEvent;
import org.killbill.billing.events.ControlTagDefinitionCreationInternalEvent;
import org.killbill.billing.events.ControlTagDefinitionDeletionInternalEvent;
import org.killbill.billing.events.ControlTagDeletionInternalEvent;
import org.killbill.billing.events.TagDefinitionInternalEvent;
import org.killbill.billing.events.TagInternalEvent;
import org.killbill.billing.events.UserTagCreationInternalEvent;
import org.killbill.billing.events.UserTagDefinitionCreationInternalEvent;
import org.killbill.billing.events.UserTagDefinitionDeletionInternalEvent;
import org.killbill.billing.events.UserTagDeletionInternalEvent;
import org.killbill.billing.util.UtilTestSuiteNoDB;
import org.killbill.billing.util.tag.ControlTagType;
import org.killbill.billing.util.tag.DefaultTagDefinition;
import org.killbill.billing.util.tag.TagDefinition;
import org.killbill.billing.util.tag.dao.TagDefinitionModelDao;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTagEventAbstractFactory extends UtilTestSuiteNoDB {

    @Test(groups = "fast")
    public void testUserTagEventFactory() {
        final TagEventAbstractFactory factory = new UserTagEventFactory();
        final UUID tagDefinitionId = UUID.randomUUID();
        final TagDefinition tagDefinition = new DefaultTagDefinition(tagDefinitionId, "user-tag", "User tag desc", false);
        final TagDefinitionModelDao modelDao = new TagDefinitionModelDao(tagDefinition);
        final UUID userToken = UUID.randomUUID();

        // 1. Definition Creation Event
        final TagDefinitionInternalEvent defCreation = factory.createTagDefinitionCreationEvent(tagDefinitionId, modelDao, 1L, 2L, userToken);
        Assert.assertTrue(defCreation instanceof UserTagDefinitionCreationInternalEvent);
        Assert.assertFalse(defCreation.getTagDefinition().isControlTag());
        Assert.assertEquals(defCreation.getTagDefinitionId(), tagDefinitionId);

        // 2. Definition Deletion Event
        final TagDefinitionInternalEvent defDeletion = factory.createTagDefinitionDeletionEvent(tagDefinitionId, modelDao, 1L, 2L, userToken);
        Assert.assertTrue(defDeletion instanceof UserTagDefinitionDeletionInternalEvent);
        Assert.assertFalse(defDeletion.getTagDefinition().isControlTag());
        Assert.assertEquals(defDeletion.getTagDefinitionId(), tagDefinitionId);

        final UUID tagId = UUID.randomUUID();
        final UUID objectId = UUID.randomUUID();

        // 3. Tag Creation Event
        final TagInternalEvent tagCreation = factory.createTagCreationEvent(tagId, objectId, ObjectType.ACCOUNT, modelDao, 1L, 2L, userToken);
        Assert.assertTrue(tagCreation instanceof UserTagCreationInternalEvent);
        Assert.assertFalse(tagCreation.getTagDefinition().isControlTag());
        Assert.assertEquals(tagCreation.getTagId(), tagId);
        Assert.assertEquals(tagCreation.getObjectId(), objectId);

        // 4. Tag Deletion Event
        final TagInternalEvent tagDeletion = factory.createTagDeletionEvent(tagId, objectId, ObjectType.ACCOUNT, modelDao, 1L, 2L, userToken);
        Assert.assertTrue(tagDeletion instanceof UserTagDeletionInternalEvent);
        Assert.assertFalse(tagDeletion.getTagDefinition().isControlTag());
        Assert.assertEquals(tagDeletion.getTagId(), tagId);
        Assert.assertEquals(tagDeletion.getObjectId(), objectId);
    }

    @Test(groups = "fast")
    public void testControlTagEventFactory() {
        final TagEventAbstractFactory factory = new ControlTagEventFactory();
        final UUID tagDefinitionId = ControlTagType.AUTO_PAY_OFF.getId();
        final TagDefinition tagDefinition = new DefaultTagDefinition(tagDefinitionId, ControlTagType.AUTO_PAY_OFF.name(), "Control tag desc", true);
        final TagDefinitionModelDao modelDao = new TagDefinitionModelDao(tagDefinition);
        final UUID userToken = UUID.randomUUID();

        // 1. Definition Creation Event
        final TagDefinitionInternalEvent defCreation = factory.createTagDefinitionCreationEvent(tagDefinitionId, modelDao, 1L, 2L, userToken);
        Assert.assertTrue(defCreation instanceof ControlTagDefinitionCreationInternalEvent);
        Assert.assertTrue(defCreation.getTagDefinition().isControlTag());
        Assert.assertEquals(defCreation.getTagDefinitionId(), tagDefinitionId);

        // 2. Definition Deletion Event
        final TagDefinitionInternalEvent defDeletion = factory.createTagDefinitionDeletionEvent(tagDefinitionId, modelDao, 1L, 2L, userToken);
        Assert.assertTrue(defDeletion instanceof ControlTagDefinitionDeletionInternalEvent);
        Assert.assertTrue(defDeletion.getTagDefinition().isControlTag());
        Assert.assertEquals(defDeletion.getTagDefinitionId(), tagDefinitionId);

        final UUID tagId = UUID.randomUUID();
        final UUID objectId = UUID.randomUUID();

        // 3. Tag Creation Event
        final TagInternalEvent tagCreation = factory.createTagCreationEvent(tagId, objectId, ObjectType.ACCOUNT, modelDao, 1L, 2L, userToken);
        Assert.assertTrue(tagCreation instanceof ControlTagCreationInternalEvent);
        Assert.assertTrue(tagCreation.getTagDefinition().isControlTag());
        Assert.assertEquals(tagCreation.getTagId(), tagId);
        Assert.assertEquals(tagCreation.getObjectId(), objectId);

        // 4. Tag Deletion Event
        final TagInternalEvent tagDeletion = factory.createTagDeletionEvent(tagId, objectId, ObjectType.ACCOUNT, modelDao, 1L, 2L, userToken);
        Assert.assertTrue(tagDeletion instanceof ControlTagDeletionInternalEvent);
        Assert.assertTrue(tagDeletion.getTagDefinition().isControlTag());
        Assert.assertEquals(tagDeletion.getTagId(), tagId);
        Assert.assertEquals(tagDeletion.getObjectId(), objectId);
    }

    @Test(groups = "fast")
    public void testTagEventBuilderFactorySelector() {
        final TagEventBuilder builder = new TagEventBuilder();
        Assert.assertTrue(builder.getFactory(false) instanceof UserTagEventFactory);
        Assert.assertTrue(builder.getFactory(true) instanceof ControlTagEventFactory);
    }
}
