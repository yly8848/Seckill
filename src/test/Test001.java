package test;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.jupiter.api.Test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.yly.dao.GoodDaoImpl;
import com.yly.dao.SuccessKillDaoImpl;
import com.yly.model.Good;
import com.yly.model.SuccessKill;
import com.yly.redis.RedisUtil;

import redis.clients.jedis.Jedis;



public class Test001 {
	
	@Test
	public void test001() throws Exception {
		
		//1.创建连接池核心工具类
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		
		//2.使用连接池获取数据库连接
		Connection con=dataSource.getConnection();
		String sql = "select * from good;";
		
		PreparedStatement ps=con.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		
		List<Good> list=new ArrayList<Good>();
		
		while(rs.next()) {
			Good g = new Good();
			g.setGood_id(rs.getLong("good_id"));
			g.setGood_name(rs.getString("good_name"));
			g.setNumber(rs.getInt("number"));
			g.setStart_time(rs.getDate("start_time"));
			g.setEnd_time(rs.getDate("end_time"));
			g.setCreate_time(rs.getDate("create_time"));
			list.add(g);
		}
		
		System.out.println(list);
	}
	
	
	@Test
	void test002() throws Exception {
		//1.加载c3p0连接池
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		
		//2.创建 QueryRunner 对象
		QueryRunner qr= new QueryRunner(dataSource);
		String sql ="select * from good;";
		List<Good> list = qr.query(sql, new BeanListHandler<Good>(Good.class));
		for(Good i:list) {
			System.out.println(i);
		}
	}
	
	@Test
	void test003() throws Exception {
		GoodDaoImpl g = new GoodDaoImpl();
		List<Good> list = g.selectNow();
		for(Good i:list) {
			System.out.println(i);
		}
	}
	
	@Test
	void test004() throws Exception {
		GoodDaoImpl g = new GoodDaoImpl();
		
		if(g.buyGoodOne(1000)) {
			System.out.println("OK");
		}else {
			System.out.println("Error");
		}
		
		System.out.println(g.select(1000));
	}
	
	@Test
	void test005() throws Exception {
		SuccessKillDaoImpl skd = new SuccessKillDaoImpl();
		SuccessKill sk = new SuccessKill();
		
		sk.setGood_id(1000);
		sk.setUsers_phone(1312113412L);
		sk.setState(0);
		if(skd.addBuy(sk)) {
			List<SuccessKill> list = skd.selectAll();
			for(SuccessKill i:list) {
				System.out.println(i);
			}
		}else {
			System.out.println("ERROR");
		}
	}
	
	@Test
	void test006() throws Exception {
		// redis test
		
		Jedis jedis = new Jedis("localhost", 6379); // 连接Redis
//        jedis.auth(""); //如果需密码
        int i = 0;
        try {
            long start = System.currentTimeMillis(); // 开始毫秒数
            while (true) {
                long end = System.currentTimeMillis();
                if (end - start >= 1000) {
                    // 当大于等于1000毫秒（相当于1秒）时，结束操作
                    break;
                }
                i++;
                jedis.set("test" + i, i + "");
            }
        } finally {
            // 关闭连接
            jedis.close();
        }
        // 打印1秒内对Redis的操作次数
        System.out.println("reids每秒操作：" + i + "次");
		
	}
	@Test
	void test007() throws Exception {
//		RedisUtil.set("a", "aaa", 3);
		System.out.println(RedisUtil.get("hello"));
	}
}
