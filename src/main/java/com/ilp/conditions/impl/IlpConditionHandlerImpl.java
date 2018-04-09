package com.ilp.conditions.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilp.conditions.intf.IlpConditionHandler;
import com.ilp.conditions.models.pdp.Transaction;
import org.interledger.Condition;
import org.interledger.Fulfillment;
import org.interledger.InterledgerAddress;
import org.interledger.codecs.CodecContext;
import org.interledger.codecs.CodecContextFactory;
import org.interledger.ilp.InterledgerPayment;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

public class IlpConditionHandlerImpl implements IlpConditionHandler {

    private Logger log = Logger.getLogger(this.getClass().getName());

    @Override
    public String getILPPacket(String ilpAddress, long amount, byte[] transaction) throws IOException {

        InterledgerAddress address = InterledgerAddress.builder().value(ilpAddress).build();

        InterledgerPayment payment = InterledgerPayment.builder()
                .destinationAccount(address)
                .destinationAmount(amount)
                .data(transaction)
                .build();


        CodecContext context = CodecContextFactory.interledger();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        context.write(InterledgerPayment.class, payment, outputStream);


        String encodedILPPacket = Base64.getUrlEncoder().encodeToString(outputStream.toByteArray());

        return encodedILPPacket;
    }

    @Override
    public String getILPPacket(String ilpAddress, long amount, Transaction transaction) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String transactionJson = mapper.writeValueAsString(transaction);
        byte[] serializedTransaction = transactionJson.getBytes();


        InterledgerAddress address = InterledgerAddress.builder().value(ilpAddress).build();

        InterledgerPayment payment = InterledgerPayment.builder()
                .destinationAccount(address)
                .destinationAmount(amount)
                .data(serializedTransaction)
                .build();

        CodecContext context = CodecContextFactory.interledger();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        context.write(InterledgerPayment.class, payment, outputStream);

        String encodedILPPacket = Base64.getUrlEncoder().encodeToString(outputStream.toByteArray());

        return encodedILPPacket;
    }

    @Override
    public Transaction getTransactionFromIlpPacket(String ilpPacket) throws IOException {

        Transaction retTransaction;

        byte[] decodedILPPacket = Base64.getUrlDecoder().decode(ilpPacket);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedILPPacket);
        CodecContext context = CodecContextFactory.interledger();
        InterledgerPayment ip = context.read(InterledgerPayment.class, inputStream);

        byte[] decodedTxn = ip.getData();

        ObjectMapper mapper = new ObjectMapper();
        retTransaction = mapper.readValue(decodedTxn, Transaction.class);

        return retTransaction;

    }

    @Override
    public String generateFulfillment(String ilpPacket, byte[] secret) {
        byte[] bFulfillment = getFulfillmentBytes(ilpPacket, secret);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bFulfillment);

    }

    @Override
    public String generateCondition(String ilpPacket, byte[] secret) {
        byte[] bFulfillment = getFulfillmentBytes(ilpPacket, secret);
        Fulfillment fulfillment = Fulfillment.builder()
                .preimage(bFulfillment)
                .build();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(fulfillment.getCondition().getHash());
    }

    @Override
    public boolean validateFulfillmentAgainstCondition(String strFulfillment, String strCondition) {
        byte[] bFulfillment = Base64.getUrlDecoder().decode(strFulfillment);
        Fulfillment fulfillment = Fulfillment.of(bFulfillment);

        byte[] bCondition = Base64.getUrlDecoder().decode(strCondition);
        Condition condition = Condition.of(bCondition);

        return fulfillment.validate(condition);
    }


    public String generateIPR(String destAddr, long destAmnt, ZonedDateTime expAt, byte[] recSecret) {

//        InterledgerAddress destinationAddress = InterledgerAddressBuilder.builder().value(destAddr).build();
//        long destinationAmount = destAmnt;
//        ZonedDateTime expiresAt = expAt;
//        byte[] receiverSecret = recSecret;
//        String paymentId = UUID.randomUUID().toString();
//
//        InterledgerPaymentRequestBuilder builder = new InterledgerPaymentRequestBuilder(
//                destinationAddress,
//                destinationAmount,
//                expiresAt,
//                receiverSecret);
//
//        builder.setEncrypted(false);
//        builder.getPskMessageBuilder().addPublicHeader("Payment-Id", paymentId);
//
//        CodecContext context = CodecContextFactory.interledger();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        try {
//            context.write(InterledgerPaymentRequest.class, builder.build(), outputStream);
//        } catch (IOException e) {
//            throw new RuntimeException("Error encoding Interledger Packet Request.", e);
//        }
//
//        String cryptoEncode = Base64.getUrlEncoder().encodeToString(outputStream.toByteArray());
//
//        log.info("returning the following crypto encode value: " + cryptoEncode);
//
//        return cryptoEncode;
        return null;
    }

    private byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    private byte[] getFulfillmentBytes(String ilpPacket, byte[] secret) {
        try {
            String HMAC_ALGORITHM = "HmacSHA256";
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return mac.doFinal(ilpPacket.getBytes());

        } catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException e) {
            throw new RuntimeException("Error getting HMAC", e);
        }
    }

}
