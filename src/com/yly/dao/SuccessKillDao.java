package com.yly.dao;

import java.util.List;

import com.yly.model.SuccessKill;

/**
 * 成功秒杀明细表接口
 * @author yly
 *
 */
public interface SuccessKillDao {
	
	/**
	 * 查询所有的抢购记录
	 * @return
	 */
	public List<SuccessKill> selectAll();
	
	/**
	 * 根据商品ID查询该商品的抢购记录
	 * @param good_id
	 * @return
	 */
	public List<SuccessKill> select(long good_id);
	
	/**
	 * 单条抢购记录查询
	 * @param good_id
	 * @param user_phone
	 * @return
	 */
	public SuccessKill select(long good_id,long user_phone);
	
	/**
	 * 添加一条抢购记录
	 * @param sk
	 * @return
	 */
	public boolean addBuy(SuccessKill sk);
	
}
