/** 
 * @author gq
 * @date 2015年11月3日 上午11:16:00 
 */
package com.hxht.iov.alarm.domain;

import java.io.Serializable;

/**
 * 所有告警父类
 *
 */
public class Alarm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4817450791741349325L;
	
	private String ruleId;
	
	private String name;
	
	private int value;
	
	private String sim;
	
	private String time;
	
	private int type;
	
	private double longitude;
	
	private double latitude;
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getSim() {
		return sim;
	}

	public void setSim(String sim) {
		this.sim = sim;
	}


	/**
	 * 告警类型
	 * @return
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public String getRuleId() {
		return ruleId;
	}


	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("sim:"+getSim());
		sb.append(",ruleId:"+getRuleId());
		sb.append(",type:"+getType());
		sb.append(",latitude:"+getLatitude());
		sb.append(",longitude:"+getLongitude());
		sb.append(",time:"+getTime());
		sb.append(",value:"+getValue());
		sb.append(",name:"+getName());
		sb.append("}");
		return sb.toString();
	}
	
}

