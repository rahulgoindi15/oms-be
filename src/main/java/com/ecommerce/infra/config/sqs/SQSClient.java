//package com.ecommerce.infra.config.sqs;
//
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.sqs.AmazonSQS;
//import com.amazonaws.services.sqs.AmazonSQSAsync;
//import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
//import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
//import io.awspring.cloud.messaging.config.SimpleMessageListenerContainerFactory;
//import io.awspring.cloud.messaging.listener.QueueMessageHandler;
//import io.awspring.cloud.messaging.listener.SimpleMessageListenerContainer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//@Configuration
//public class SQSClient {
//
//	@Bean
//	@Primary
//	public static AmazonSQSAsync getAmazonSQS() {
//		return AmazonSQSAsyncClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();
//	}
//
//	@Bean
//	public static AmazonSQS getAmazonSQSSync() {
//		return AmazonSQSClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();
//	}
//
//	@Bean
//	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
//		SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
//		factory.setAmazonSqs(getAmazonSQS());
//		return factory;
//	}
//
//	@Bean
//	public QueueMessageHandler messageHandler() {
//		return new QueueMessageHandler();
//	}
//
//	@Bean
//	public SimpleMessageListenerContainer simpleMessageListenerContainer(SimpleMessageListenerContainerFactory factory, QueueMessageHandler messageHandler) {
//		SimpleMessageListenerContainer container = factory.createSimpleMessageListenerContainer();
//		container.setMaxNumberOfMessages(10);
//		container.setMessageHandler(messageHandler);
//		return container;
//	}
//}
