package com.yly.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import com.yly.servic.SeckillServic;

/**
 * 本类用于在 Tomcat 启动时进行相关业务的初始化
 */
@WebServlet("/Init")
public class Init extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Override
	public void init() throws ServletException {
		super.init();
		SeckillServic.initRedis();
		SeckillServic.initConsumer();
	}

}
