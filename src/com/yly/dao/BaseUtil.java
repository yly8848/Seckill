package com.yly.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 
ArrayHandler //把结果集中的第一行数据转成对象数组。
ArrayListHandler //把结果集中的每一行数据都转成一个对象数组，再存放到List中。
BeanHandler //将结果集中的第一行数据封装到一个对应的JavaBean实例中。
BeanListHandler //将结果集中的每一行数据都封装到一个对应的JavaBean实例中，存放到List里。
ColumnListHandler //将结果集中某一列的数据存放到List中。
KeyedHandler //将结果集中的每一行数据都封装到一个Map里，然后再根据指定的key把每个Map再存放到一个Map里。
MapHandler //将结果集中的第一行数据封装到一个Map里，key是列名，value就是对应的值。
MapListHandler //将结果集中的每一行数据都封装到一个Map里，然后再存放到List。
ScalarHandler //将结果集中某一条记录的其中某一列的数据存成Object。
 */


/**
 * 数据库工具类
 * @author yly
 *
 */
public class BaseUtil {
	
	//初始化c3p0
	private static ComboPooledDataSource dataSource;
	static {
		dataSource = new ComboPooledDataSource();
	}
	
	/**
	 * 获取一个查询对象
	 * @return
	 */
	public static QueryRunner getQueryRunner() {
		QueryRunner qr = new QueryRunner(dataSource);
		return qr;
	}
	
	
	/**
	 * 公共万能 修改,插入,删除 方法
	 * @param sql sql语句
	 * @param arr 参数数组
	 * @return
	 */
	public static boolean update(String sql,Object[] arr) {
		QueryRunner qr = getQueryRunner();
		try {
			int count = qr.update(sql, arr);
			if(count>0) {
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Timestamp getNowTime() {
		return new Timestamp(new Date().getTime());
	}
	
}
