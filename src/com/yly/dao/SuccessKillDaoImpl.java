package com.yly.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.yly.model.SuccessKill;


/**
 * 秒杀明细表基本操作实现类
 * @author yly
 * @see SuccessKill
 */
public class SuccessKillDaoImpl implements SuccessKillDao {

	@Override
	public List<SuccessKill> selectAll() {
		QueryRunner qr = BaseUtil.getQueryRunner();
		String sql = "select * from success_kill";
		try {
			return qr.query(sql, new BeanListHandler<SuccessKill>(SuccessKill.class));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<SuccessKill> select(long good_id) {
		QueryRunner qr = BaseUtil.getQueryRunner();
		String sql = "select * from success_kill where good_id=?";
		try {
			return qr.query(sql, new BeanListHandler<SuccessKill>(SuccessKill.class),good_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean addBuy(SuccessKill sk) {
		String sql = "insert into success_kill(good_id,user_phone,state) values(?,?,?)";
		Object[] arr= {sk.getGood_id(),sk.getUser_phone(),sk.getState()};
		return BaseUtil.update(sql, arr);
	}

	@Override
	public SuccessKill select(long good_id, long user_phone) {
		QueryRunner qr = BaseUtil.getQueryRunner();
		String sql = "select * from success_kill where good_id=? and user_phone=?";
		try {
			return qr.query(sql, new BeanHandler<SuccessKill>(SuccessKill.class),good_id,user_phone);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
