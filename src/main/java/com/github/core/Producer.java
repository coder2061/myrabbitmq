package com.github.core;

/**
 * 生产者-发布消息
 * 
 * @author jiangyf
 * @date 2017年9月8日 上午10:12:46
 */
public interface Producer {

	/**
	 * 发布消息到指定队列
	 * 
	 * @param queueKey
	 *            路由键
	 * @param object
	 *            消息对象
	 */
	public void sendMsgByQueueKey(String queueKey, Object object);

	/**
	 * 发布消息到指定队列
	 * 
	 * @param pattern
	 *            topic主题
	 * @param object
	 *            消息对象
	 */
	public void sendMsgByPattern(String pattern, Object object);

}
