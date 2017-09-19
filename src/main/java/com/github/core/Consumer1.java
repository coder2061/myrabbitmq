package com.github.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

/**
 * 消费者-接收消息
 * 
 * @author jiangyf
 * @date 2017年9月8日 上午10:25:23
 */
@Component("consumer1")
public class Consumer1 implements MessageListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(Consumer1.class);
	private static final Gson GSON = new Gson();

	@Override
	public void onMessage(Message msg) {
		try {
			String message = GSON.toJson(msg);
			Integer priority = msg.getMessageProperties().getPriority();
			System.out.println("consumer receive message ---> msg:" + new String(msg.getBody(), "UTF-8")
					+ ",priority:" + priority);
			LOGGER.info("consumer receive message success ---> msg:{}, priority:", message, priority);
		} catch (Exception e) {
			LOGGER.error("consumer receive message fail ---> {}", e.getMessage());
		}
	}

}
