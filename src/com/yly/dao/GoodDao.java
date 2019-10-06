package com.yly.dao;

import java.util.List;

import com.yly.model.Good;

/**
 * 秒杀表基本操作接口
 * @author yly
 *
 */
public interface GoodDao {
	
	/**
	 * 查询所有的秒杀库存信息
	 * @return
	 */
	public List<Good> selectAllGood();
	
	/**
	 * 查询某个秒杀信息
	 * @param good_id 商品ID
	 * @return
	 */
	public Good select(long good_id);
	
	/**
	 * 查询当前有效的,已开始的秒杀活动
	 * @return
	 */
	public List<Good> selectNow();
	
	/**
	 * 对秒杀商品进行减库存的操作, (购买)
	 * @param good_id 商品ID
	 * @return
	 */
	public boolean buyGoodOne(long good_id);
	
}
