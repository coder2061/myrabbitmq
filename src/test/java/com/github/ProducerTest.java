package com.github;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.core.Producer;
import com.github.core.Result;

@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring-context.xml" })
public class ProducerTest {
	private static final String QUEUE_KEY = "info";

	@Autowired
	Producer producer;

	@Test
	public void testSendMsgToQueue() {
		Result result = new Result();
		result.setDesc("hello, rabbmitmq!");
		result.setSuccess(true);
		// producer.sendMsgByQueueKey(QUEUE_KEY, "hello, rabbmitmq!");
		producer.sendMsgByQueueKey(QUEUE_KEY, result);
	}
}