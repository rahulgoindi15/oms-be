package com.ecommerce.sqs.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.ecommerce.exceptions.CustomException;
import com.ecommerce.sqs.model.OrderMessage;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SqsProducer {
    @Value("${aws.sqs.order-queue:https://sqs.us-east-1.amazonaws.com/897120551968/orders.fifo}")
    private String orderQueueUrl;
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendMessage (OrderMessage orderMessage) {
        log.info("Sending message to SQS..");
        Gson gson = new Gson();
        String messageBody = gson.toJson(orderMessage);
        try {
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(orderQueueUrl)
                    .withMessageDeduplicationId(UUID.randomUUID().toString())
                    .withMessageGroupId(UUID.randomUUID().toString())
                    .withMessageBody(messageBody);
            getAmazonSqs().sendMessage(sendMessageRequest);
            log.info("Message sent successfully to SQS.");
        } catch (Exception e) {
            log.error("Failed to send message to SQS", e);
            throw new CustomException("SQS Send Message Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Value("${aws.sqs.region:us-east-1}")
    private String region;
    public AmazonSQS getAmazonSqs() {
        return AmazonSQSClientBuilder
                .standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .withRegion(region)
                .build();
    }
}
