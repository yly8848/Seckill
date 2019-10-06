package com.yly.model;

import java.sql.Date;

/**
 * 秒杀成功明细模型
 * @author yly
 *
 */
public class SuccessKill {
	private long good_id;
	private long user_phone;
	private int state;
	private Date create_time;
	
	public SuccessKill() {
		
	}
	
	
	public SuccessKill(long good_id, long user_phone, int state) {
		this.good_id = good_id;
		this.user_phone = user_phone;
		this.state = state;
	}



	public long getGood_id() {
		return good_id;
	}
	public void setGood_id(long good_id) {
		this.good_id = good_id;
	}

	public long getUser_phone() {
		return user_phone;
	}
	public void setUsers_phone(long users_phone) {
		this.user_phone = users_phone;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	@Override
	public String toString() {
		return "SuccessKill [good_id=" + good_id + ", phone=" + user_phone + ", state=" + state + ", create_time="
				+ create_time + "]";
	}
	
	
}
