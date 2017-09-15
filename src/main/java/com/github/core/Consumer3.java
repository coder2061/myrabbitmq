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
@Component("consumer3")
public class Consumer3 implements MessageListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(Consumer3.class);
	private static final Gson GSON = new Gson();

	@Override
	public void onMessage(Message msg) {
		try {
			String message = GSON.toJson(msg);
			System.out.print("consumer receive message ---> " + new String(msg.getBody(), "UTF-8"));
			System.out.print("consumer receive message success --->" + message);
			LOGGER.info("consumer receive message success ---> {}", message);
		} catch (Exception e) {
			LOGGER.error("consumer receive message fail ---> {}", e.getMessage());
		}
	}

}
