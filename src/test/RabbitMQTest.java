package test;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yly.rabbitmq.RabbitMQUtil;

public class RabbitMQTest {
	
	@Test
	void send() throws Exception {
		//创建连接工厂
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setPort(5672);
		factory.setUsername("yly8848");
		factory.setPassword("123456");
		
		String QUEUE_NAME = "testhost";
		
		// 选择虚拟主机
//		factory.setVirtualHost(vhost);
        
        //获取连接
        Connection conn = factory.newConnection();
        
        //创建连接通道
        Channel channel =conn.createChannel();
        
        //声明(创建)队列, 没有就创建一个队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        //添加消息
        String msg = "hello MQ21";
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
        System.out.println("send "+ msg);
        
        //关闭通道和连接
        channel.close();
        conn.close();
	}
	
	@Test
	void consumer() throws Exception {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setPort(5672);
		factory.setUsername("yly8848");
		factory.setPassword("123456");
		
        Connection conn = factory.newConnection();
        Channel channel =conn.createChannel();
        
        String QUEUE_NAME ="testhost";
        

		//声明队列, 消费者和生产者的声明必须要一致, 不如消费者不能进行消费
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        // 定义队列的消费者
        DefaultConsumer  consumer = new DefaultConsumer(channel) {
        	@Override
        	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
        			throws IOException {
        		String message = new String(body, "UTF-8");
        		try {
                    System.out.println(" [x] Received '" + message);
                } finally {
                    //手动确认ack
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
        	}
        };
        // 设置autoAck为false
        channel.basicConsume(QUEUE_NAME, false, consumer);
	}
	
	
	@Test
	void test03() throws Exception {
		/**
		 * revc 为线程池绑定并执行, 所以在项目中不必开线程去阻塞等待消息
		 */
		System.out.println("start_ "+Thread.currentThread().getName());
		for(int i=0;i<10;i++) {
			RabbitMQUtil.send("testhost", "this is_"+i);
		}
		
		RabbitMQUtil.revc("testhost", false, (msg)-> {
			System.out.println(msg);
			System.out.println("work_ "+Thread.currentThread().getName());
		});
	}
	
	@Test
	void test04() throws Exception {
		/**
		 * exchange 交换机模式测试, 并不理想
		 */
		RabbitMQUtil.revcFanout("exchange02", "xxx1", (msg)->{
			System.out.println("xxx1_' "+msg);
		});

		RabbitMQUtil.revcFanout("exchange02", "ooo1", (msg)->{
			System.out.println("ooo1_' "+msg);
		});
		
		Thread.sleep(2000);
		RabbitMQUtil.sendFanout("exchange02", "echange_test_01");
		Thread.sleep(20000);
	}
	
	@Test
	void test05() throws Exception {
		/**
		 * routing 模式测试
		 */
		RabbitMQUtil.revcRouting("exchange05", "queue05",new String[] {"key1"},(msg)->{
			System.out.println(msg);
		});
		Thread.sleep(2000);
		RabbitMQUtil.sendRouting("exchange05", "key1", "hello!");
	}
	
	@Test
	void test06() throws Exception {
		/**
		 * 主题模式测试
		 */
		RabbitMQUtil.revcTopic("exchange06", "queue06", new String[] {"*.xxx"}, (msg)->{
			System.out.println(msg);
		});
		RabbitMQUtil.revcTopic("exchange06", "queue006", new String[] {"#"}, (msg)->{
			System.out.println("#匹配一个或多个-->"+msg);
		});
		Thread.sleep(2000);
		RabbitMQUtil.sendTopic("exchange06", "key06.xxx", "hello!");
	}
	
}
