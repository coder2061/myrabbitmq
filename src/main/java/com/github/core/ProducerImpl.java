package com.github.core;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.stereotype.Service;

@Service("producer")
public class ProducerImpl implements Producer {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProducerImpl.class);

	@Resource(name = "amqpTemplate")
	private AmqpTemplate amqpTemplate;

	@Resource(name = "amqpTemplate2")
	private AmqpTemplate amqpTemplate2;

	@Override
	public void sendMsgByQueueKey(String queueKey, Object object) {
		try {
			// convertAndSend：将Java对象转换为消息发送到匹配Key的交换机中Exchange，由于配置了JSON转换，这里是将Java对象转换成JSON字符串的形式。
//			amqpTemplate.convertAndSend(queueKey, object);
			// 设置消息优先级
			MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					message.getMessageProperties().setPriority(1);
					return message;
				}
			};
			amqpTemplate.convertAndSend(queueKey, object, messagePostProcessor);
		} catch (Exception e) {
			LOGGER.error("producer send message fail by queue_key ---> {}", e.getMessage());
		}
	}

	@Override
	public void sendMsgByPattern(String pattern, Object object) {
		try {
			amqpTemplate2.convertAndSend(pattern, object);
		} catch (Exception e) {
			LOGGER.error("producer send message fail by pattern ---> {}", e.getMessage());
		}
	}
}
