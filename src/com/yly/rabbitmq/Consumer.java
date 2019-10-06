package com.yly.rabbitmq;

/**
 * 消费者接口,主要用于对消息的处理
 * @author yly
 *
 */
public interface Consumer {
	/**
	 * 对消息的具体实现
	 * @param msg
	 */
	public void dowork(String msg);
}
