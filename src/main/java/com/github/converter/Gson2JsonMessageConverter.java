package com.github.converter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractJsonMessageConverter;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.MessageConversionException;

import com.google.gson.Gson;

/**
 * json转换器
 * 
 * RabbitMQ已经实现了Jackson的消息转换（Jackson2JsonMessageConverter），由于考虑到效率，
 * 如下使用Gson实现消息转换。如下消息的转换类的接口MessageConverter，
 * Jackson2JsonMessageConverter的父类AbstractJsonMessageConverter针对json转换的基类。
 * 
 * @author jiangyf
 * @date 2017年9月15日 下午4:00:46
 */
public class Gson2JsonMessageConverter extends AbstractJsonMessageConverter {
	private static final Gson GSON = new Gson();
	private static final Logger LOGGER = LoggerFactory.getLogger(Gson2JsonMessageConverter.class);
	private static ClassMapper classMapper = new DefaultClassMapper();

	public Gson2JsonMessageConverter() {
		super();
	}

	@Override
	protected Message createMessage(Object object, MessageProperties messageProperties) {
		byte[] bytes = null;
		try {
			String jsonString = GSON.toJson(object);
			bytes = jsonString.getBytes(getDefaultCharset());
		} catch (IOException e) {
			throw new MessageConversionException("Failed to convert Message content", e);
		}
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		messageProperties.setContentEncoding(getDefaultCharset());
		if (bytes != null) {
			messageProperties.setContentLength(bytes.length);
		}
		classMapper.fromClass(object.getClass(), messageProperties);
		return new Message(bytes, messageProperties);
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		Object content = null;
		MessageProperties properties = message.getMessageProperties();
		if (properties != null) {
			String contentType = properties.getContentType();
			if (contentType != null && contentType.contains("json")) {
				String encoding = properties.getContentEncoding();
				if (encoding == null) {
					encoding = getDefaultCharset();
				}
				try {
					Class<?> targetClass = getClassMapper().toClass(message.getMessageProperties());
					content = convertBytesToObject(message.getBody(), encoding, targetClass);
				} catch (IOException e) {
					throw new MessageConversionException("Failed to convert message content", e);
				}
			} else {
				LOGGER.warn("Could not convert incoming message with content-type [{}]", contentType);
			}
		}
		if (content == null) {
			content = message.getBody();
		}
		return content;
	}

	private Object convertBytesToObject(byte[] body, String encoding, Class<?> clazz)
			throws UnsupportedEncodingException {
		String contentAsString = new String(body, encoding);
		return GSON.fromJson(contentAsString, clazz);
	}
}
