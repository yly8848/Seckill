package com.yly.servic;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.yly.dao.BaseUtil;
import com.yly.dao.GoodDaoImpl;
import com.yly.dao.SuccessKillDaoImpl;
import com.yly.model.Good;
import com.yly.model.SuccessKill;
import com.yly.rabbitmq.RabbitMQUtil;
import com.yly.redis.RedisUtil;

/**
 * 秒杀业务实现类
 * @author yly
 *
 */
public class SeckillServic {
	private static GoodDaoImpl good;
	private static SuccessKillDaoImpl kill;
	private static Channel channel;
	static {
		good = new GoodDaoImpl();
		kill = new SuccessKillDaoImpl();
		channel = RabbitMQUtil.getChnnel("seckillExchange");
	}
	
	/**
	 * 把库存加载到Redis中
	 */
	public static void initRedis() {
		List<Good> list = good.selectNow();
		Timestamp t =BaseUtil.getNowTime();
		for(Good i:list) {
			long time = i.getEnd_time().getTime()-t.getTime();
			RedisUtil.set(""+i.getGood_id(), ""+i.getNumber(), (int)(time/1000));
		}
	}
	
	/**
	 * 初始化消息队列消费者
	 */
	public static void initConsumer() {
		
		/**
		 * 抢购信息监听
		 */
		RabbitMQUtil.revcRouting("seckillExchange", "successQueue",new String[] {"success"},(msg)->{
			String[] arr = msg.split("_");
			kill.addBuy(new SuccessKill(Long.valueOf(arr[0]),Long.valueOf(arr[1]),0));
		});
		
		/**
		 * 库存监听
		 */
		RabbitMQUtil.revcRouting("seckillExchange", "goodQueue",new String[] {"good"},(msg)->{
			good.buyGoodOne(Long.valueOf(msg));
		});
		System.out.println("rabbit init success");
	}
	
	/**
	 * 抢购商品 普通方法
	 * @param good_id
	 * @param user_phone
	 * @return
	 */
	public static boolean buy(long good_id,long user_phone) {
		//判断是否重复购买
		if(kill.select(good_id, user_phone)==null) {
			if(good.buyGoodOne(good_id) && kill.addBuy(new SuccessKill(good_id,user_phone,0))) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	
	/**
	 * 商品抢购,Redis方式
	 * @param good_id
	 * @param user_phone
	 * @return
	 */
	public static boolean buyOnRedis(long good_id,long user_phone) {
		String key = ""+good_id+"_"+user_phone;
		
		//判断重复购买,商品售卖时间合法性
		if(RedisUtil.exists(""+good_id)&&!RedisUtil.exists(key)) {
			
			//抢票,只要票号不为负数都视为秒杀成功
			if(RedisUtil.sub(""+good_id, 1)>=0) {
				RedisUtil.set(key, "1");//redis记录抢购过一次
				kill.addBuy(new SuccessKill(good_id,user_phone,0));
				good.buyGoodOne(good_id);
				return true;
			}else {
				return false;
			}
			
		}else {
			return false;
		}
		
	}
	
	/**
	 * 商品抢购,使用 MQ 中间件
	 * @param good_id
	 * @param user_phone
	 * @return
	 */
	public static boolean buyOnMQ(long good_id,long user_phone) {
		String key = ""+good_id+"_"+user_phone;
		//一种商品只能抢购购一次
		if(RedisUtil.exists(""+good_id)&&!RedisUtil.exists(key)) {
			
			//抢票,只要票号不为负数都视为秒杀成功
			if(RedisUtil.sub(""+good_id, 1)>=0) {
				RedisUtil.set(key, "1");//redis记录抢购过一次
				
				try {
					channel.basicPublish("seckillExchange", "success", null, key.getBytes());
					channel.basicPublish("seckillExchange", "good", null, (""+good_id).getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return true;
			}else {
				return false;
			}
			
		}else {
			return false;
		}
	}
	
}
