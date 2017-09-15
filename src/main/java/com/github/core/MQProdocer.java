package com.github.core;

/**
 * 生产者-发送消息
 * 
 * @author jiangyf
 * @date 2017年9月13日 下午4:57:05
 */
public interface MQProdocer {

	void send(Object message);

}
