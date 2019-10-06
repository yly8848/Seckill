package com.yly.model;

import java.sql.Date;

/**
   *  库存表模型
 * @author yly
 *
 */
public class Good {
	private long good_id;
	private String good_name;
	private int number;
	private Date start_time;
	private Date end_time;
	private Date create_time;
	
	public long getGood_id() {
		return good_id;
	}
	public void setGood_id(long good_id) {
		this.good_id = good_id;
	}
	public String getGood_name() {
		return good_name;
	}
	public void setGood_name(String good_name) {
		this.good_name = good_name;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Date getStart_time() {
		return start_time;
	}
	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	@Override
	public String toString() {
		return "Good [good_id=" + good_id + ", good_name=" + good_name + ", number=" + number + ", start_time="
				+ start_time + ", end_time=" + end_time + ", create_time=" + create_time + "]";
	}
}
