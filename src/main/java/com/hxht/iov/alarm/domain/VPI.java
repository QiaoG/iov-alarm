/** 
 * @author gq
 * @date 2015年10月30日 下午2:38:32 
 */
package com.hxht.iov.alarm.domain;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 车辆位置信息Vehicle Position Info
 *
 */
public class VPI {
	
	/**
	 * 根据808协议
	 */
	private String msg;
	
	private String seq;
	
	private String sim;
	
	/**
	 * 通过二进制位来标识各个告警，参考808协议
	 */
	private int alarm;
	
	/**
	 * 和alarm类似，标识车辆的运行状态的相关参数,参考808协议
	 */
	private int state;
	
	private int lon;//经度
	
	private int lat;//纬度
	
	private int hig;
	
	/**
	 * (百米/h)
	 */
	private int speed;
	
	private int direct;//方向 0~359
	
	private Date time;
	
	/**
	 * 告警附加信息
	 */
	@JsonProperty("att") 
	private List<Att> att;
	
	private String ver;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getSim() {
		return sim;
	}

	public void setSim(String sim) {
		this.sim = sim;
	}

	public int getAlarm() {
		return alarm;
	}

	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getLon() {
		return lon;
	}

	public void setLon(int lon) {
		this.lon = lon;
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getHig() {
		return hig;
	}

	public void setHig(int hig) {
		this.hig = hig;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	
	private VPIPoint point;
	
	public VPIPoint getVpiPoint(){
		if(point == null){
			double x = (double)this.lon/1000000;
			double y = (double)this.lat/1000000;
			point = new VPIPoint(x,y);
		}
		return point;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public List<Att> getAtt() {
		return att;
	}

	public void setAtt(List<Att> att) {
		this.att = att;
	}

	public static class Att{
		private int key;
		
		private List<Integer> val;
		
		public Att(){
			
		}

		public int getKey() {
			return key;
		}

		public void setKey(int key) {
			this.key = key;
		}

		public List<Integer> getVal() {
			return val;
		}

		public void setVal(List<Integer> val) {
			this.val = val;
		}
	}
	
}

