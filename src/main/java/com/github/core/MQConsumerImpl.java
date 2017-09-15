package com.github.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.rabbitmq.client.ShutdownSignalException;

public class MQConsumerImpl implements MQConsumer {
	private static final Logger log = LoggerFactory.getLogger(MQProdocerImpl.class);

	@Autowired
	private ConnectionFactory connectionFactory;

	@Override
	public Result consume() {
		return null;
	}

	public <T> MQConsumer buildMQConsumer(String exchange, String routingKey, final String queue,
			final MessageProcess<T> messageProcess) throws IOException {
		// 1 创建连接和channel
		final Connection connection = connectionFactory.createConnection();
		// buildQueue(exchange, routingKey, queue, connection);
		// 2 设置message序列化方法
		final MessagePropertiesConverter messagePropertiesConverter = new DefaultMessagePropertiesConverter();
		final MessageConverter messageConverter = new Jackson2JsonMessageConverter();
		// 3 构造consumer
		return new MQConsumer() {
			QueueingConsumer consumer;
			{
				consumer = buildQueueConsumer(connection, queue);
			}

			@Override
			public Result consume() {
				QueueingConsumer.Delivery delivery = null;
				Channel channel = consumer.getChannel();
				try {
					// 1 通过delivery获取原始数据
					delivery = consumer.nextDelivery();
					Message message = new Message(delivery.getBody(), messagePropertiesConverter
							.toMessageProperties(delivery.getProperties(), delivery.getEnvelope(), "UTF-8"));
					// 2 将原始数据转换为特定类型的包
					@SuppressWarnings("unchecked")
					T messageBean = (T) messageConverter.fromMessage(message);
					// 3 处理数据
					Result result = messageProcess.process(messageBean);
					// 4 手动发送ack确认
					if (result.isSuccess()) {
						channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
					} else {
						log.info("send message failed: " + result.getDesc());
						channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
					}
					return result;
				} catch (InterruptedException e) {
					e.printStackTrace();
					return new Result(false, "interrupted exception " + e.toString());
				} catch (IOException e) {
					e.printStackTrace();
					retry(delivery, channel);
					log.info("io exception : " + e);
					return new Result(false, "io exception " + e.toString());
				} catch (ShutdownSignalException e) {
					e.printStackTrace();
					try {
						channel.close();
					} catch (IOException io) {
						io.printStackTrace();
					}
					consumer = buildQueueConsumer(connection, queue);
					return new Result(false, "shutdown exception " + e.toString());
				} catch (Exception e) {
					e.printStackTrace();
					log.info("exception : " + e);
					retry(delivery, channel);
					return new Result(false, "exception " + e.toString());
				}
			}

			private void retry(Delivery delivery, Channel channel) {

			}

			private QueueingConsumer buildQueueConsumer(Connection connection, String queue) {
				return null;
			}
		};
	}

}
