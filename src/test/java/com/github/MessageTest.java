package com.github;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.core.Producer;

public class MessageTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageTest.class);
	private static final String[] QUEUE_KEYS = { "debug", "info", "warning", "error" };
	private ApplicationContext context = null;

	@Before
	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext("spring-context.xml");
	}

	@Test
	public void should_send_a_amq_message() throws Exception {
		Producer producer = (Producer) context.getBean("producer");
		int num = 10;
		while (num > 0) {
			producer.sendMsgByQueueKey(QUEUE_KEYS[num % 4], "producer send message success, NO:" + num--);
			producer.sendMsgByPattern("a.info", "producer send message success, NO:" + num--);
			try {
				// 暂停一下，好让消费者去取消息打印出来
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.error("producer send message fail ---> {}", e.getMessage());
			}
		}
	}
}
