package com.github.core;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class MQProdocerImpl implements MQProdocer {
	private static final Logger log = LoggerFactory.getLogger(MQProdocerImpl.class);

	@Autowired
	private ConnectionFactory connectionFactory;

	public MQProdocer buildMQProdocer(final String exchange, final String routingKey, final String queue)
			throws IOException, TimeoutException {
		Connection connection = connectionFactory.createConnection();
		// 1 构造template, exchange, routingkey等
		// buildQueue(exchange, routingKey, queue, connection);
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMandatory(true);
		rabbitTemplate.setExchange(exchange);
		rabbitTemplate.setRoutingKey(routingKey);
		// 2 设置message序列化方法
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		// 3 设置发送确认
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			@Override
			public void confirm(CorrelationData correlationData, boolean ack) {
				if (!ack) {
					log.info("send message failed : {}", correlationData.toString());
					throw new RuntimeException("send message error " + correlationData.toString());
				}
			}
		});

		// 4 构造sender方法
		return new MQProdocer() {
			@Override
			public void send(Object message) {
				try {
					rabbitTemplate.convertAndSend(message);
				} catch (RuntimeException e) {
					log.info("send message failed once : {}", e.getMessage());
					// retry
					try {
						rabbitTemplate.convertAndSend(message);
					} catch (RuntimeException ex) {
						log.info("send message failed again  : {}", ex.getMessage());
					}
				}
			}
		};
	}

	@Override
	public void send(Object message) {
		// TODO Auto-generated method stub

	}

}
