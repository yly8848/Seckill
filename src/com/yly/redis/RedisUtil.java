package com.yly.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis 工具类
 * @author yly
 *
 */
public class RedisUtil {
	
	private static String addr="localhost";
	private static int port = 6379;
	//private static String password="";
	//最大连接数
	private static int max_total = 1024;
	//最大 jedis 空闲实例个数
	private static int max_idle = 200;
	//最大等待时间
	private static int max_wait=10000;
	//连接超时时间
	private static int timeout=10000;
	//获取jedis实例时是否进行验证
	private static boolean TEST_ON_BORROW = true;
	//连接池
	private static JedisPool jedisPool;
	private static JedisPoolConfig config;
	
	static {
		config = new JedisPoolConfig();
		config.setMaxTotal(max_total);
		config.setMaxIdle(max_idle);
		config.setMaxWaitMillis(max_wait);
		config.setTestOnBorrow(TEST_ON_BORROW);
		jedisPool = new JedisPool(config, addr, port, timeout);
	}
	
	/**
	 * 获取一个 jedis 连接对象
	 * @return
	 */
	public static Jedis getJedis() {
		if(jedisPool!=null) {
			Jedis jedis = jedisPool.getResource();
			return jedis;
		}else {
			return null;
		}
	}
	
	/**
	 * 向连接池返还 jedis
	 * @param jedis
	 */
	public static void returnResource(final Jedis jedis) {
        if(jedis != null) {
            jedis.close();
        }
	}
	
	/**
	 * 往Redis里添加一个键值对
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean set(String key,String value) {
		Jedis jedis = getJedis();
		if(jedis!=null) {
			jedis.set(key,value);
			jedis.close();
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 往Redis里添加一个键值对,并设置过期时间
	 * @param key 
	 * @param value
	 * @param time 毫秒
	 * @return
	 */
	public static boolean set(String key,String value,int time) {
		Jedis jedis = getJedis();
		if(jedis!=null) {
			jedis.setex(key,time ,value);
			jedis.close();
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 判断某个记录是否存在
	 * @param key
	 * @return
	 */
	public static boolean exists(String key) {
		Jedis jedis = getJedis();
		if(jedis!=null) {
			boolean flag = jedis.exists(key);
			jedis.close();
			return flag;
		}else {
			return false;
		}
	}
	
	/**
	 * 根据key获取记录值
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		Jedis jedis = getJedis();
		if(jedis!=null) {
			String value = jedis.get(key);
			jedis.close();
			return value;
		}else {
			return null;
		}
	}
	
	/**
	 * 删除某个记录
	 * @param key
	 */
	public static void remove(String key) {
		if (key != null && key.length() >= 1 && !key.equals("")) {
			Jedis jedis = getJedis();
			if(jedis!=null && jedis.exists(key)) {
				jedis.del(key);
				jedis.close();
			}
		}
	}
	
	/**
	 * 对某个记录进行加n操作
	 * @param key
	 * @param n
	 * @return
	 */
	public static Long add(String key,int n) {
		Jedis jedis = getJedis();
		if(jedis!=null) {
			Long value = jedis.incrBy(key, n);
			jedis.close();
			return value;
		}else {
			return null;
		}
	}
	
	/**
	 * 对某个记录进行减n的操作
	 * @param key
	 * @param n
	 * @return
	 */
	public static Long sub(String key,int n) {
		Jedis jedis = getJedis();
		if(jedis!=null) {
			Long value = jedis.decrBy(key, n);
			jedis.close();
			return value;
		}else {
			return null;
		}
	}
	
	
}
