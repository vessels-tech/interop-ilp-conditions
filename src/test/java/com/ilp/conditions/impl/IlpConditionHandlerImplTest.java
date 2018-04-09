package com.ilp.conditions.impl;

import com.ilp.conditions.models.pdp.*;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.interledger.codecs.CodecContext;
import org.interledger.codecs.CodecContextFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IlpConditionHandlerImplTest {

    private static Logger logger = Logger.getLogger(IlpConditionHandlerImpl.class.getName());

    private byte[] secret = "secret".getBytes(); //Base64.getEncoder().encode("secret".getBytes());;

    @Test
    public void testGetIlpPacket() throws Exception {
        IlpConditionHandlerImpl i = new IlpConditionHandlerImpl();
        String ilpPacket = i.getILPPacket("private.bob", 10L, getMockTransaction());
        assertEquals("AQAAAAAAAAAKC3ByaXZhdGUuYm9iggRieyJ0cmFuc2FjdGlvbklkIjoiYTgzMjNiYzYtYzIyOC00ZGYyLWFlODItZTVhOTk3YmFmODk5IiwicXVvdGVJZCI6ImI1MWVjNTM0LWVlNDgtNDU3NS1iNmE5LWVhZDI5NTViODA2OSIsInBheWVlIjp7InBhcnR5SWRJbmZvIjp7InBhcnR5SWRUeXBlIjoiTVNJU0ROIiwicGFydHlJZGVudGlmaWVyIjoiMTYxMzU1NTEyMTIiLCJwYXJ0eVN1YklkT3JUeXBlIjoiUEFTU1BPUlQiLCJmc3BJZCI6IjEyMzQifSwibWVyY2hhbnRDbGFzc2lmaWNhdGlvbkNvZGUiOiIxMjM0IiwibmFtZSI6Ikp1c3RpbiBUcnVkZWF1IiwicGVyc29uYWxJbmZvIjp7ImNvbXBsZXhOYW1lIjp7ImZpcnN0TmFtZSI6Ikp1c3RpbiIsIm1pZGRsZU5hbWUiOiJQaWVycmUiLCJsYXN0TmFtZSI6IlRydWRlYXUifSwiZGF0ZU9mQmlydGgiOiIxOTcxLTEyLTI1In19LCJwYXllciI6eyJwYXJ0eUlkSW5mbyI6eyJwYXJ0eUlkVHlwZSI6Ik1TSVNETiIsInBhcnR5SWRlbnRpZmllciI6IjE2MTM1NTUxMjEyIiwicGFydHlTdWJJZE9yVHlwZSI6IlBBU1NQT1JUIiwiZnNwSWQiOiIxMjM0In0sIm1lcmNoYW50Q2xhc3NpZmljYXRpb25Db2RlIjoiNTY3OCIsIm5hbWUiOiJNaWNoYWVsIEpvcmRhbiIsInBlcnNvbmFsSW5mbyI6eyJjb21wbGV4TmFtZSI6eyJmaXJzdE5hbWUiOiJNaWNoYWVsIiwibWlkZGxlTmFtZSI6IkplZmZyZXkiLCJsYXN0TmFtZSI6IkpvcmRhbiJ9LCJkYXRlT2ZCaXJ0aCI6IjE5NjMtMDItMTcifX0sImFtb3VudCI6eyJjdXJyZW5jeSI6IlVTRCIsImFtb3VudCI6IjEwIn0sInRyYW5zYWN0aW9uVHlwZSI6eyJzY2VuYXJpbyI6IlRSQU5TRkVSIiwic3ViU2NlbmFyaW8iOiJsb2NhbGx5IGRlZmluZWQgc3ViLXNjZW5hcmlvIiwiaW5pdGlhdG9yIjoiUEFZRUUiLCJpbml0aWF0b3JUeXBlIjoiQ09OU1VNRVIiLCJyZWZ1bmRJbmZvIjp7Im9yaWdpbmFsVHJhbnNhY3Rpb25JZCI6ImI1MWVjNTM0LWVlNDgtNDU3NS1iNmE5LWVhZDI5NTViODA2OSJ9LCJiYWxhbmNlT2ZQYXltZW50cyI6IjEyMyJ9LCJub3RlIjoiU29tZSBub3RlLiIsImV4dGVuc2lvbkxpc3QiOnsiZXh0ZW5zaW9uIjpbeyJrZXkiOiJrZXkxIiwidmFsdWUiOiJ2YWx1ZTEifSx7ImtleSI6ImtleTIiLCJ2YWx1ZSI6InZhbHVlMiJ9XX19",ilpPacket);

    }

    @Test
    public void testGetTransactionFromIlpPacket() throws Exception {

        IlpConditionHandlerImpl i = new IlpConditionHandlerImpl();
        String ilpPacket = i.getILPPacket("private.bob", 100L, getMockTransaction());
        Transaction t =  i.getTransactionFromIlpPacket(ilpPacket);

        assertEquals("a8323bc6-c228-4df2-ae82-e5a997baf899",t.getTransactionId());
        assertEquals("b51ec534-ee48-4575-b6a9-ead2955b8069",t.getQuoteId());
        assertEquals("Justin Trudeau",t.getPayee().getName());
        assertEquals("Michael Jordan",t.getPayer().getName());
    }

    @Test
    public void testGenerateFulfillment() throws Exception {
        String expectedValue = "139D8XCB00mEST_DJCejrtNCB3Ya0eiuYEzVBrp_3ks";

        IlpConditionHandlerImpl i = new IlpConditionHandlerImpl();
        String ilpPacket = i.getILPPacket("private.bob", 100L, getMockTransaction());

        String fulfillment = i.generateFulfillment(ilpPacket, secret);

        assertThat(fulfillment, is(equalTo(expectedValue)));
    }

    @Test
    public void testGenerateCondition() throws Exception {
        String expectedValue = "IVBsIsXg5owJYXAMGqzJTxD02_4OUYi3HHnQk31I6_o";

        IlpConditionHandlerImpl i = new IlpConditionHandlerImpl();
        String ilpPacket = i.getILPPacket("private.bob", 100L, getMockTransaction());

        String condition = i.generateCondition(ilpPacket, secret);

        assertThat(condition, is(equalTo(expectedValue)));

    }

    @Test
    public void testValidateFulfillmentAgainstCondition() throws Exception {
        boolean expectedValue = true;

        IlpConditionHandlerImpl i = new IlpConditionHandlerImpl();
        String ilpPacket = i.getILPPacket("private.bob", 100L, getMockTransaction());
        String fulfillment = i.generateFulfillment(ilpPacket,secret);
        String condition = i.generateCondition(ilpPacket,secret);
        boolean actualValue = i.validateFulfillmentAgainstCondition(fulfillment, condition);

        assertThat(actualValue, is(equalTo(expectedValue)));

    }





    public Transaction getMockTransaction() {


        Transaction manuallyBuiltTransaction = new Transaction();

        manuallyBuiltTransaction = new Transaction();
        manuallyBuiltTransaction.setTransactionId("a8323bc6-c228-4df2-ae82-e5a997baf899");
        manuallyBuiltTransaction.setQuoteId("b51ec534-ee48-4575-b6a9-ead2955b8069");

        Party pe = new Party();
        pe.setMerchantClassificationCode("1234");
        pe.setName("Justin Trudeau");
        PartyIdInfo pii1 = new PartyIdInfo();
        pii1.setFspId("1234");
        pii1.setPartyIdentifier("16135551212");
        pii1.setPartyIdType("MSISDN");
        pii1.setPartySubIdOrType("PASSPORT");
        pe.setPartyIdInfo(pii1);
        PartyPersonalInfo ppi1  = new PartyPersonalInfo();
        PartyComplexName pcn1 =  new PartyComplexName();
        pcn1.setFirstName("Justin");
        pcn1.setLastName("Trudeau");
        pcn1.setMiddleName("Pierre");
        ppi1.setComplexName(pcn1);
        ppi1.setDateOfBirth("1971-12-25");
        pe.setPersonalInfo(ppi1);
        manuallyBuiltTransaction.setPayee(pe);

        Party pr = new Party();
        pr.setMerchantClassificationCode("5678");
        pr.setName("Michael Jordan");
        PartyIdInfo pii2 = new PartyIdInfo();
        pii2.setFspId("1234");
        pii2.setPartyIdentifier("16135551212");
        pii2.setPartyIdType("MSISDN");
        pii2.setPartySubIdOrType("PASSPORT");
        pr.setPartyIdInfo(pii2);
        PartyPersonalInfo ppi2  = new PartyPersonalInfo();
        PartyComplexName pcn2 =  new PartyComplexName();
        pcn2.setFirstName("Michael");
        pcn2.setLastName("Jordan");
        pcn2.setMiddleName("Jeffrey");
        ppi2.setComplexName(pcn2);
        ppi2.setDateOfBirth("1963-02-17");
        pr.setPersonalInfo(ppi2);
        manuallyBuiltTransaction.setPayer(pr);

        Money m = new Money();
        m.setAmount(String.valueOf(10L));
        m.setCurrency(Currency.USD.getValue());
        manuallyBuiltTransaction.setAmount(m);

        TransactionType tt = new TransactionType();
        tt.setBalanceOfPayments("123");
        tt.setInitiator(TransactionInitiator.PAYEE.getValue());
        tt.setInitiatorType(TransactionInitiatorType.CONSUMER.getValue());
        Refund ri = new Refund();
        ri.setOriginalTransactionId("b51ec534-ee48-4575-b6a9-ead2955b8069");
        tt.setRefundInfo(ri);
        tt.setScenario(TransactionScenario.TRANSFER.getValue());
        tt.setSubScenario("locally defined sub-scenario");
        manuallyBuiltTransaction.setTransactionType(tt);

        manuallyBuiltTransaction.setNote("Some note.");

        ExtensionList el = new ExtensionList();
        Extension e1 = new Extension();
        e1.setKey("key1");
        e1.setValue("value1");
        Extension e2 = new Extension();
        e2.setKey("key2");
        e2.setValue("value2");
        List<Extension> le = new ArrayList<>();
        le.add(e1);
        le.add(e2);
        el.setExtension(le);
        manuallyBuiltTransaction.setExtensionList(el);

        return manuallyBuiltTransaction;
    }

}