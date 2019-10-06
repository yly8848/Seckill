package com.yly.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.DeliverCallback;

/**
 * RabbitMQ 基本操作工具类
 * 
 * 频繁地创建/销毁  与RabbitMQ服务端的 connectiont/channel 要承担相应的系统开销
 * 建议生产者和消费者都 保持一个或多个连接
 * 
 * 本工具类 提供了 work-queue, routing , topic 等模式的封装
 * 
 * @author yly
 *
 */
public class RabbitMQUtil {
	private static ConnectionFactory factory;
	
	static {
		factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setPort(5672);
		factory.setUsername("yly8848");
		factory.setPassword("123456");
	}
	
	public static Connection getConnection(){
		try {
			return factory.newConnection();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 发送普通消息
	 * @param queueName
	 * @param msg
	 * @return
	 */
	public static boolean send(String queueName,String msg) {
		Connection conn = getConnection();
		try {
			Channel chnnel = conn.createChannel();
			/**
			 * 声明队列,没有则创建,第二个参数为是否持久化(创建的时候生效,对于已存在的队列不能改变什么)
			 * 倒数第二个为是否自动删除(当队列没有消费者连接时自动删除)
			 */
			chnnel.queueDeclare(queueName, false, false, false, null);
			chnnel.basicPublish("", queueName, null, msg.getBytes("UTF-8"));
			
			chnnel.close();
			conn.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (TimeoutException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 发送持久化消息
	 * @param queueName
	 * @param msg
	 * @return
	 */
	public static boolean sendPersistence(String queueName,String msg) {
		Connection conn = getConnection();
		try {
			Channel chnnel = conn.createChannel();
			/**
			 * 声明队列,没有则创建,第二个参数为是否持久化(创建的时候生效,对于已存在的队列不能改变什么)
			 */
			chnnel.queueDeclare(queueName, true, false, false, null);
			chnnel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes("UTF-8"));
			chnnel.close();
			conn.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 订阅者模式, 生产者发送消息到交换机Exchange,由交换机分发到已绑定的队列
	 * 实现1条信息多个队列消费
	 * @param exchange_name
	 * @param msg
	 * @return
	 */
	public static boolean sendFanout(String exchange_name,String msg) {
		try {
			Connection conn = getConnection();
			Channel channel = conn.createChannel();
			
			/**
			 * 声明 exchange, 
			 * 第三个参数为: 是否为持久化
			 * 第四个参数为: 是否autoDelect,没有Channel连接时自动删除
			 * 第五个参数为: 是否为系统默认的exchange(系统默认有7个exchange)
			 */
			channel.exchangeDeclare(exchange_name, "fanout", false, false, false, null);
			
			/**
			 * 发送信息
			 */
			channel.basicPublish(exchange_name, "", null, msg.getBytes("UTF-8"));
			
			channel.close();
			conn.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO ERROR");
		} catch (TimeoutException e) {
			e.printStackTrace();
			System.out.println("TimeOut");
		}
		return false;
	}
	
	
	/**
	 * Routing 路由选择模式, 填入一个key让exchange进行精准投放
	 * @param exchange_name 交换机名
	 * @param routingKey key
	 * @param msg 欲发送的信息
	 * @return
	 */
	public static boolean sendRouting(String exchange_name,String routing_Key,String msg) {
		try {
			Connection conn = getConnection();
			Channel channel = conn.createChannel();
			channel.exchangeDeclare(exchange_name, "direct");
			
			channel.basicPublish(exchange_name, routing_Key, null, msg.getBytes("UTF-8"));
			
			channel.close();
			conn.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Channel getChnnel(String exchange_name) {
		try {
			Connection conn = getConnection();
			Channel channel = conn.createChannel();
			channel.exchangeDeclare(exchange_name, "direct");
			return channel;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 主题模式(通配符模式), 这里的routing_Key会被消费者进行通配符的识别进行分类
	 * @param exchange_name
	 * @param routing_Key
	 * @param msg
	 * @return
	 */
	public static boolean sendTopic(String exchange_name,String routing_Key,String msg) {
		try {
			Connection conn = getConnection();
			Channel channel = conn.createChannel();
			channel.exchangeDeclare(exchange_name, "topic");
			
			channel.basicPublish(exchange_name, routing_Key, null, msg.getBytes("UTF-8"));
			
			channel.close();
			conn.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 消费者接收消息并进行处理,采用手动的方式回复ACK
	 * @param queueName 欲使用队列名
	 * @param isPersistence 是否开启了消息的持久化
	 * @param consumer Consumer对象,用于对消息的处理
	 */
	public static void revc(String queueName,boolean isPersistence,Consumer consumer) {
		Connection conn = getConnection();
		try {
			Channel channel = conn.createChannel();
			channel.queueDeclare(queueName, isPersistence, false, false, null);
			
			/**
			 * 控制服务器每次只发一个消息给消费者, 只有回复ACK标记后才继续接收下一个消息
			 */
			channel.basicQos(1);
			
			DeliverCallback deliverCallback = (consumerTag, delivery) ->{
				String message = new String(delivery.getBody(), "UTF-8");
				try {
					consumer.dowork(message);
				} finally{
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			};
			
			/**
			 * 设置autoACK为false, 即手动确认ACK
			 */
			channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 订阅模式接收信息
	 * @param exchange_name 交换机名
	 * @param queue_name 队列名
	 * @param consumer
	 */
	public static void revcFanout(String exchange_name,String queue_name,Consumer consumer) {
		try {
			Connection conn = getConnection();
			Channel channel = conn.createChannel();
			/**
			 * 声明 队列
			 */
			channel.queueDeclare(queue_name, false, false, false, null);
			
			/**
			 * 声明 exchange, 
			 * 第三个参数为: 是否为持久化
			 * 第四个参数为: 是否autoDelect,没有Channel连接时自动删除
			 * 第五个参数为: 是否为系统默认的exchange(系统默认有7个exchange)
			 */
			channel.exchangeDeclare(exchange_name, "fanout", false, false, false, null);
			
			//创建一个临时的队列,并返回队列名字
//			String qname = channel.queueDeclare().getQueue();
			
			// 绑定队列到交换机
	        channel.queueBind(queue_name, exchange_name, "");
	        
			/**
			 * 控制服务器每次只发一个消息给消费者, 只有回复ACK标记后才继续接收下一个消息
			 */
			channel.basicQos(1);
			
			DeliverCallback deliverCallback = (consumerTag, delivery) ->{
				String message = new String(delivery.getBody(), "UTF-8");
				try {
					consumer.dowork(message);
				} finally{
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			};
			
			/**
			 * 设置autoACK为false, 即手动确认ACK
			 */
			channel.basicConsume(queue_name, false, deliverCallback, consumerTag -> { });
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 路由模式, 通过标识不同的 routing_key 来识别要接受信息的类型
	 * @param exchange_name
	 * @param queue_name
	 * @param key_list
	 * @param consumer
	 */
	public static void revcRouting(String exchange_name,String queue_name,String[] key_list,Consumer consumer) {
		try {
			Connection conn = getConnection();
			Channel channel = conn.createChannel();
			channel.exchangeDeclare(exchange_name, "direct");
			
			//声明队列
			channel.queueDeclare(queue_name, false, false, false, null);
			
			/**
			 * 绑定routingkey
			 */
			for(String key:key_list) {
				channel.queueBind(queue_name, exchange_name, key);
			}
			
			/**
			 * 控制服务器每次只发一个消息给消费者, 只有回复ACK标记后才继续接收下一个消息
			 */
			channel.basicQos(1);
			
			DeliverCallback deliverCallback = (consumerTag, delivery) ->{
				String message = new String(delivery.getBody(), "UTF-8");
				try {
					consumer.dowork(message);
				} finally{
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			};
			
			/**
			 * 设置autoACK为false, 即手动确认ACK
			 */
			channel.basicConsume(queue_name, false, deliverCallback, consumerTag -> { });
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 主题模式,  routing_key 可以出现通配符
	 * 匹配一个或多个 '#'
	 * 匹配一个 '*'
	 * @param exchange_name
	 * @param queue_name
	 * @param key_list
	 * @param consumer
	 */
	public static void revcTopic(String exchange_name,String queue_name,String[] key_list,Consumer consumer) {
		Connection conn = getConnection();
		try {
			Channel channel = conn.createChannel();
			
			channel.exchangeDeclare(exchange_name, "topic");
			
			/**
			 * 声明队列
			 */
			channel.queueDeclare(queue_name, false, false, false, null);
			
			
			/**
			 * 绑定routingkey
			 */
			for(String key:key_list) {
				channel.queueBind(queue_name, exchange_name, key);
			}
			
			/**
			 * 控制服务器每次只发一个消息给消费者, 只有回复ACK标记后才继续接收下一个消息
			 */
			channel.basicQos(1);
			
			DeliverCallback deliverCallback = (consumerTag, delivery) ->{
				String message = new String(delivery.getBody(), "UTF-8");
				try {
					consumer.dowork(message);
				} finally{
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			};
			
			/**
			 * 设置autoACK为false, 即手动确认ACK
			 */
			channel.basicConsume(queue_name, false, deliverCallback, consumerTag -> { });
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
