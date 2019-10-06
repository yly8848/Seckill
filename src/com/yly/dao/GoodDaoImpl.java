package com.yly.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.yly.model.Good;


/**
 * 库存表操作实现类
 * @author yly
 * @see GoodDao
 */
public class GoodDaoImpl implements GoodDao {

	public GoodDaoImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Good> selectAllGood() {
		QueryRunner qr = BaseUtil.getQueryRunner();
		String sql = "select * from good";
		try {
			return qr.query(sql, new BeanListHandler<Good>(Good.class));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Good select(long good_id) {
		QueryRunner qr = BaseUtil.getQueryRunner();
		String sql = "select * from good where good_id=?";
		try {
			return qr.query(sql, new BeanHandler<Good>(Good.class),good_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Good> selectNow() {
		QueryRunner qr = BaseUtil.getQueryRunner();
		String sql = "select * from good where end_time>?";
		try {
			Timestamp t = BaseUtil.getNowTime();
			return qr.query(sql, new BeanListHandler<Good>(Good.class),t);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean buyGoodOne(long good_id) {
		String sql = "update good set number=number-1 where good_id=? and number>0 and start_time<=? and end_time>=?";
		Timestamp t = BaseUtil.getNowTime();
		Object[] arr = {good_id,t,t};
		return BaseUtil.update(sql, arr);
	}
	
}
