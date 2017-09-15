package com.github.core;

/**
 * 消息处理
 * 
 * @author jiangyf
 * @date 2017年9月13日 下午5:21:37
 */
public interface MessageProcess<T> {

	Result process(T message);

}
