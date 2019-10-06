package com.yly.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yly.servic.SeckillServic;

/**
 * 商品秒杀接口,采用 RabbitMQ + Redis 方式
 */
@WebServlet("/MQKill")
public class MQKill extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String good_id = request.getParameter("good_id");
		String user_phone = request.getParameter("user_phone");
		
		response.setContentType("text/html;charset=utf-8");
		
		if(good_id==null||user_phone==null||good_id.equals("")||user_phone.equals("")) {
			response.getWriter().append("{'code':-1,'msg':'参数错误'}");
		}else {
			if(SeckillServic.buyOnMQ(Long.valueOf(good_id), Long.valueOf(user_phone))) {
				response.getWriter().append("{'code':1,'msg':'抢购成功!'}");
			}else {
				response.getWriter().append("{'code':0,'msg':'抢购失败!'}");
			}
		}
	}

}
