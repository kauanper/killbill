/*
 * Copyright 2010-2013 Ning, Inc.
 * Copyright 2014-2015 Groupon, Inc
 * Copyright 2014-2015 The Billing Project, LLC
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

package org.killbill.billing.account.api;

import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.killbill.billing.account.AccountTestSuiteNoDB;
import org.killbill.billing.catalog.api.Currency;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestDefaultAccount extends AccountTestSuiteNoDB {

    @Test(groups = "fast", description = "Test if Account constructor can accept null values")
    public void testConstructorAcceptsNullValues() throws Exception {
        final AccountData accountData = getNullAccountData();
        final Account account = new DefaultAccount(UUID.randomUUID(), accountData);

        Assert.assertNull(account.getExternalKey());
        Assert.assertNull(account.getEmail());
        Assert.assertNull(account.getName());
        Assert.assertNull(account.getFirstNameLength());
        Assert.assertNull(account.getCurrency());
        Assert.assertEquals(account.getBillCycleDayLocal(), (Integer) 0);
        Assert.assertNull(account.getPaymentMethodId());
        Assert.assertNull(account.getTimeZone());
        Assert.assertNull(account.getLocale());
        Assert.assertNull(account.getAddress1());
        Assert.assertNull(account.getAddress2());
        Assert.assertNull(account.getCompanyName());
        Assert.assertNull(account.getCity());
        Assert.assertNull(account.getStateOrProvince());
        Assert.assertNull(account.getCountry());
        Assert.assertNull(account.getPostalCode());
        Assert.assertNull(account.getPhone());
        Assert.assertNull(account.isMigrated());
    }

    @Test(groups = "fast", description = "Test mergeWithDelegate Account api")
    public void testMergeWithDelegate() throws Exception {
        final AccountData accountData = getNullAccountData();
        final Account account = new DefaultAccount(UUID.randomUUID(), accountData);

        // Update all updatable fields
        final AccountData accountDataUpdates1 = getAccountData(account.getBillCycleDayLocal(), account.getCurrency(), account.getExternalKey());
        final Account accountUpdates1 = new DefaultAccount(UUID.randomUUID(), accountDataUpdates1);

        final Account updatedAccount1 = accountUpdates1.mergeWithDelegate(account);
        checkAccountEquals(updatedAccount1, accountUpdates1);

        // Update some fields
        final AccountData accountDataUpdates2 = Mockito.mock(AccountData.class);
        Mockito.when(accountDataUpdates2.getEmail()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(accountDataUpdates2.getName()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(accountDataUpdates2.getFirstNameLength()).thenReturn(12);
        final Account accountUpdates2 = new DefaultAccount(UUID.randomUUID(), accountDataUpdates2);

        final Account updatedAccount2 = accountUpdates2.mergeWithDelegate(updatedAccount1);
        Assert.assertEquals(updatedAccount2.getEmail(), accountUpdates2.getEmail());
        Assert.assertEquals(updatedAccount2.getName(), accountUpdates2.getName());
        Assert.assertEquals(updatedAccount2.getFirstNameLength(), updatedAccount2.getFirstNameLength());
        Assert.assertEquals(updatedAccount2.getExternalKey(), updatedAccount1.getExternalKey());
        Assert.assertEquals(updatedAccount2.getCurrency(), updatedAccount1.getCurrency());
        Assert.assertEquals(updatedAccount2.getBillCycleDayLocal(), updatedAccount1.getBillCycleDayLocal());
        Assert.assertEquals(updatedAccount2.getPaymentMethodId(), updatedAccount1.getPaymentMethodId());
        Assert.assertEquals(updatedAccount2.getTimeZone(), updatedAccount1.getTimeZone());
        Assert.assertEquals(updatedAccount2.getLocale(), updatedAccount1.getLocale());
        Assert.assertEquals(updatedAccount2.getAddress1(), updatedAccount1.getAddress1());
        Assert.assertEquals(updatedAccount2.getAddress2(), updatedAccount1.getAddress2());
        Assert.assertEquals(updatedAccount2.getCompanyName(), updatedAccount1.getCompanyName());
        Assert.assertEquals(updatedAccount2.getCity(), updatedAccount1.getCity());
        Assert.assertEquals(updatedAccount2.getStateOrProvince(), updatedAccount1.getStateOrProvince());
        Assert.assertEquals(updatedAccount2.getCountry(), updatedAccount1.getCountry());
        Assert.assertEquals(updatedAccount2.getPostalCode(), updatedAccount1.getPostalCode());
        Assert.assertEquals(updatedAccount2.getPhone(), updatedAccount1.getPhone());
        Assert.assertEquals(updatedAccount2.isMigrated(), updatedAccount1.isMigrated());
    }

    @Test(groups = "fast", description = "Test Account BCD merge")
    public void testBCDMerges() throws Exception {
        final UUID accountId = UUID.randomUUID();
        final Currency currency = Currency.BRL;
        final String externalKey = UUID.randomUUID().toString();

        final AccountData accountDataWithNullBCD = getAccountData(null, currency, externalKey);
        final Account accountWithNullBCD = new DefaultAccount(accountId, accountDataWithNullBCD);
        // Null BCD -> 0 BCD
        Assert.assertEquals(accountWithNullBCD.getBillCycleDayLocal(), (Integer) 0);

        final DefaultMutableAccountData accountDataWithZeroBCD = new DefaultMutableAccountData(accountDataWithNullBCD);
        accountDataWithZeroBCD.setBillCycleDayLocal(0);
        final Account accountWithZeroBCD = new DefaultAccount(accountId, accountDataWithZeroBCD);
        // Null BCD and 0 BCD -> 0 BCD
        Assert.assertEquals(accountWithNullBCD.mergeWithDelegate(accountWithZeroBCD).getBillCycleDayLocal(), (Integer) 0);

        final DefaultMutableAccountData accountDataWithRealBCD = new DefaultMutableAccountData(accountDataWithNullBCD);
        accountDataWithRealBCD.setBillCycleDayLocal(12);
        final Account accountWithRealBCD = new DefaultAccount(accountId, accountDataWithRealBCD);
        // Null BCD and real BCD -> real BCD
        Assert.assertEquals(accountWithNullBCD.mergeWithDelegate(accountWithRealBCD).getBillCycleDayLocal(), (Integer) 12);

        final DefaultMutableAccountData accountDataWithAnotherRealBCD = new DefaultMutableAccountData(accountDataWithNullBCD);
        accountDataWithAnotherRealBCD.setBillCycleDayLocal(20);
        final Account accountWithAnotherBCD = new DefaultAccount(accountId, accountDataWithAnotherRealBCD);
        // Same BCD
        Assert.assertEquals(accountWithAnotherBCD.mergeWithDelegate(accountWithAnotherBCD).getBillCycleDayLocal(), (Integer) 20);
        try {
            // Different BCD
            Assert.assertEquals(accountWithAnotherBCD.mergeWithDelegate(accountWithRealBCD).getBillCycleDayLocal(), (Integer) 20);
            Assert.fail();
        } catch (final IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test(groups = "fast", description = "Test DefaultAccount.Builder fluent creation")
    public void testBuilderCreationAndFluentSetters() throws Exception {
        final UUID id = UUID.randomUUID();
        final DateTime createdDate = new DateTime(2023, 1, 1, 0, 0, DateTimeZone.UTC);
        final DateTime updatedDate = new DateTime(2023, 1, 2, 0, 0, DateTimeZone.UTC);
        final UUID parentAccountId = UUID.randomUUID();
        final UUID paymentMethodId = UUID.randomUUID();
        final DateTime referenceTime = new DateTime(2023, 1, 3, 0, 0, DateTimeZone.UTC);
        final DateTimeZone timeZone = DateTimeZone.forID("America/New_York");

        final DefaultAccount account = DefaultAccount.builder()
                .id(id)
                .createdDate(createdDate)
                .updatedDate(updatedDate)
                .externalKey("ext-123")
                .email("test@example.com")
                .name("John Doe")
                .firstNameLength(4)
                .currency(Currency.USD)
                .parentAccountId(parentAccountId)
                .isPaymentDelegatedToParent(true)
                .billCycleDayLocal(15)
                .paymentMethodId(paymentMethodId)
                .referenceTime(referenceTime)
                .timeZone(timeZone)
                .locale("en_US")
                .address1("123 Main St")
                .address2("Apt 4B")
                .companyName("Acme Corp")
                .city("San Francisco")
                .stateOrProvince("CA")
                .country("USA")
                .postalCode("94105")
                .phone("555-1234")
                .notes("VIP customer")
                .isMigrated(true)
                .build();

        Assert.assertEquals(account.getId(), id);
        Assert.assertEquals(account.getCreatedDate(), createdDate);
        Assert.assertEquals(account.getUpdatedDate(), updatedDate);
        Assert.assertEquals(account.getExternalKey(), "ext-123");
        Assert.assertEquals(account.getEmail(), "test@example.com");
        Assert.assertEquals(account.getName(), "John Doe");
        Assert.assertEquals(account.getFirstNameLength(), (Integer) 4);
        Assert.assertEquals(account.getCurrency(), Currency.USD);
        Assert.assertEquals(account.getParentAccountId(), parentAccountId);
        Assert.assertTrue(account.isPaymentDelegatedToParent());
        Assert.assertEquals(account.getBillCycleDayLocal(), (Integer) 15);
        Assert.assertEquals(account.getPaymentMethodId(), paymentMethodId);
        Assert.assertEquals(account.getReferenceTime(), referenceTime);
        Assert.assertEquals(account.getTimeZone(), timeZone);
        Assert.assertEquals(account.getLocale(), "en_US");
        Assert.assertEquals(account.getAddress1(), "123 Main St");
        Assert.assertEquals(account.getAddress2(), "Apt 4B");
        Assert.assertEquals(account.getCompanyName(), "Acme Corp");
        Assert.assertEquals(account.getCity(), "San Francisco");
        Assert.assertEquals(account.getStateOrProvince(), "CA");
        Assert.assertEquals(account.getCountry(), "USA");
        Assert.assertEquals(account.getPostalCode(), "94105");
        Assert.assertEquals(account.getPhone(), "555-1234");
        Assert.assertEquals(account.getNotes(), "VIP customer");
        Assert.assertTrue(account.isMigrated());
    }

    @Test(groups = "fast", description = "Test DefaultAccount.Builder default values")
    public void testBuilderDefaultValues() throws Exception {
        final DefaultAccount account = DefaultAccount.builder().build();

        Assert.assertNull(account.getId());
        Assert.assertNull(account.getExternalKey());
        Assert.assertNull(account.getEmail());
        Assert.assertNull(account.getName());
        Assert.assertNull(account.getCurrency());
        Assert.assertFalse(account.isPaymentDelegatedToParent());
        Assert.assertEquals(account.getBillCycleDayLocal(), (Integer) 0);
        Assert.assertNull(account.getPaymentMethodId());
        Assert.assertNull(account.getTimeZone());
        Assert.assertNull(account.getLocale());
        Assert.assertNull(account.isMigrated());
    }

    @Test(groups = "fast", description = "Test toBuilder for cloning and mutating account")
    public void testBuilderToBuilderCopyAndMutation() throws Exception {
        final DefaultAccount original = DefaultAccount.builder()
                .id(UUID.randomUUID())
                .name("Alice")
                .email("alice@example.com")
                .currency(Currency.EUR)
                .billCycleDayLocal(10)
                .build();

        final DefaultAccount mutated = original.toBuilder()
                .email("alice.new@example.com")
                .billCycleDayLocal(25)
                .build();

        Assert.assertEquals(mutated.getId(), original.getId());
        Assert.assertEquals(mutated.getName(), original.getName());
        Assert.assertEquals(mutated.getCurrency(), original.getCurrency());
        Assert.assertEquals(mutated.getEmail(), "alice.new@example.com");
        Assert.assertEquals(mutated.getBillCycleDayLocal(), (Integer) 25);
    }

    @Test(groups = "fast", description = "Test equality between Builder and legacy constructor instances")
    public void testBuilderEqualsLegacyConstructor() throws Exception {
        final UUID id = UUID.randomUUID();
        final String externalKey = "key-1";
        final String email = "test@test.com";
        final String name = "Test";
        final Integer firstNameLength = 4;
        final Currency currency = Currency.BRL;
        final UUID parentAccountId = UUID.randomUUID();
        final Boolean isPaymentDelegatedToParent = false;
        final Integer billCycleDayLocal = 5;
        final UUID paymentMethodId = UUID.randomUUID();
        final DateTime referenceTime = new DateTime(DateTimeZone.UTC);
        final DateTimeZone timeZone = DateTimeZone.UTC;
        final String locale = "pt_BR";
        final String address1 = "Rua A";
        final String address2 = "Apto 1";
        final String companyName = "Empresa";
        final String city = "SP";
        final String stateOrProvince = "SP";
        final String country = "Brasil";
        final String postalCode = "01000-000";
        final String phone = "11999999999";
        final String notes = "nota";
        final Boolean isMigrated = false;

        final DefaultAccount legacy = new DefaultAccount(id, null, null, externalKey, email, name,
                firstNameLength, currency, parentAccountId, isPaymentDelegatedToParent, billCycleDayLocal,
                paymentMethodId, referenceTime, timeZone, locale, address1, address2, companyName,
                city, stateOrProvince, country, postalCode, phone, notes, isMigrated);

        final DefaultAccount fromBuilder = DefaultAccount.builder()
                .id(id)
                .externalKey(externalKey)
                .email(email)
                .name(name)
                .firstNameLength(firstNameLength)
                .currency(currency)
                .parentAccountId(parentAccountId)
                .isPaymentDelegatedToParent(isPaymentDelegatedToParent)
                .billCycleDayLocal(billCycleDayLocal)
                .paymentMethodId(paymentMethodId)
                .referenceTime(referenceTime)
                .timeZone(timeZone)
                .locale(locale)
                .address1(address1)
                .address2(address2)
                .companyName(companyName)
                .city(city)
                .stateOrProvince(stateOrProvince)
                .country(country)
                .postalCode(postalCode)
                .phone(phone)
                .notes(notes)
                .isMigrated(isMigrated)
                .build();

        Assert.assertEquals(fromBuilder, legacy);
        Assert.assertEquals(fromBuilder.hashCode(), legacy.hashCode());
    }

    private void checkAccountEquals(final Account finalAccount, final Account delegateAccount) {
        Assert.assertEquals(finalAccount.getExternalKey(), delegateAccount.getExternalKey());
        Assert.assertEquals(finalAccount.getEmail(), delegateAccount.getEmail());
        Assert.assertEquals(finalAccount.getName(), delegateAccount.getName());
        Assert.assertEquals(finalAccount.getFirstNameLength(), delegateAccount.getFirstNameLength());
        Assert.assertEquals(finalAccount.getCurrency(), delegateAccount.getCurrency());
        Assert.assertEquals(finalAccount.getBillCycleDayLocal(), delegateAccount.getBillCycleDayLocal());
        Assert.assertEquals(finalAccount.getPaymentMethodId(), delegateAccount.getPaymentMethodId());
        Assert.assertEquals(finalAccount.getTimeZone(), delegateAccount.getTimeZone());
        Assert.assertEquals(finalAccount.getLocale(), delegateAccount.getLocale());
        Assert.assertEquals(finalAccount.getAddress1(), delegateAccount.getAddress1());
        Assert.assertEquals(finalAccount.getAddress2(), delegateAccount.getAddress2());
        Assert.assertEquals(finalAccount.getCompanyName(), delegateAccount.getCompanyName());
        Assert.assertEquals(finalAccount.getCity(), delegateAccount.getCity());
        Assert.assertEquals(finalAccount.getStateOrProvince(), delegateAccount.getStateOrProvince());
        Assert.assertEquals(finalAccount.getCountry(), delegateAccount.getCountry());
        Assert.assertEquals(finalAccount.getPostalCode(), delegateAccount.getPostalCode());
        Assert.assertEquals(finalAccount.getPhone(), delegateAccount.getPhone());
        Assert.assertEquals(finalAccount.getNotes(), delegateAccount.getNotes());
        Assert.assertEquals(finalAccount.isMigrated(), delegateAccount.isMigrated());
    }

    private AccountData getAccountData(final Integer bcd, final Currency currency, final String externalKey) {
        final AccountData secondAccountData = Mockito.mock(AccountData.class);
        Mockito.when(secondAccountData.getExternalKey()).thenReturn(externalKey);
        Mockito.when(secondAccountData.getEmail()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getName()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getFirstNameLength()).thenReturn(Integer.MAX_VALUE);
        Mockito.when(secondAccountData.getCurrency()).thenReturn(currency);
        Mockito.when(secondAccountData.getBillCycleDayLocal()).thenReturn(bcd);
        Mockito.when(secondAccountData.getPaymentMethodId()).thenReturn(UUID.randomUUID());
        Mockito.when(secondAccountData.getTimeZone()).thenReturn(DateTimeZone.forID("EST"));
        Mockito.when(secondAccountData.getLocale()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getAddress1()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getAddress2()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getCompanyName()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getCity()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getStateOrProvince()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getCountry()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getPostalCode()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.getPhone()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(secondAccountData.isMigrated()).thenReturn(false);
        return secondAccountData;
    }

    private AccountData getNullAccountData() {
        // Make Mockito return null for all values
        return Mockito.mock(AccountData.class, new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                return null;
            }
        });
    }
}
