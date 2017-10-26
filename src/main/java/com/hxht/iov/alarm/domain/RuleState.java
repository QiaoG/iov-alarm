/** 
 * @author gq
 * @date 2015年12月22日 下午4:27:30 
 */
package com.hxht.iov.alarm.domain;

import java.util.Date;
import java.util.LinkedList;

/**
 * 车辆的基于某个规则的状态
 *
 */
public class RuleState {
	
	private String sim;
	
	private int position;
	
	private Date time;
	
	private Date vpiTime;
	
	private Date beginTimeOfStop;
	
	private Date beginTimeOfSpeed;

	//最近10个点位，间距小于10米，用于判断是否已经停止
	private LinkedList<VPIPoint> history = new LinkedList<>();
	
	private VPIPoint location ;
	
	private boolean speedAlarm;//超速告警
	
	private boolean stopAlarm;//滞留告警
	
	public RuleState(String simId){
		this.setSim(simId);
	}

	/**
	 * @return
	 * 在规则中的位置
	 * 0：外面
	 * 1：里面(围栏)
	 * >1：路段索引号(路线)
	 */
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * 当前时间(毫秒)
	 * @return
	 */
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * 停车开始时间(毫秒)
	 * @return
	 */
	public Date getBeginTimeOfStop() {
		return beginTimeOfStop;
	}

	public void setBeginTimeOfStop(Date timeOfStop) {
		this.beginTimeOfStop = timeOfStop;
	}

	/**
	 * 超速开始时间(毫秒)
	 * @return
	 */
	public Date getBeginTimeOfSpeed() {
		return beginTimeOfSpeed;
	}

	public void setBeginTimeOfSpeed(Date timeOfSpeed) {
		this.beginTimeOfSpeed = timeOfSpeed;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public String getSim() {
		return sim;
	}

	private void setSim(String sim) {
		this.sim = sim;
	}

	@Override
	public Object clone(){
		RuleState cr = new RuleState(this.getSim());
		cr.setPosition(this.getPosition());
		cr.setTime(this.getTime());
		cr.setBeginTimeOfSpeed(this.getBeginTimeOfSpeed());
		cr.setBeginTimeOfStop(this.getBeginTimeOfStop());
		if(getLocation() != null)
			cr.setLocation((VPIPoint)getLocation().clone());
		cr.setSim(this.getSim());
		cr.setSpeedAlarm(this.isSpeedAlarm());
		cr.setStopAlarm(this.isStopAlarm());
		cr.setVpiTime(this.getVpiTime());
		return cr;
	}
	
	public void clear(){
		setBeginTimeOfSpeed(null);
		setBeginTimeOfStop(null);
		setSpeedAlarm(false);
		setStopAlarm(false);
		if(getHistory() != null){
			getHistory().clear();
		}
	}

	public boolean isSpeedAlarm() {
		return speedAlarm;
	}

	public void setSpeedAlarm(boolean speedAlarm) {
		this.speedAlarm = speedAlarm;
	}

	public boolean isStopAlarm() {
		return stopAlarm;
	}

	public void setStopAlarm(boolean stopAlarm) {
		this.stopAlarm = stopAlarm;
	}

	public Date getVpiTime() {
		return vpiTime;
	}

	public void setVpiTime(Date vpiTime) {
		this.vpiTime = vpiTime;
	}

	public VPIPoint getLocation() {
		return location;
	}

	public void setLocation(VPIPoint location) {
		this.location = location;
	}

	/**
	 * 最近相邻的10个点位，相邻间距小于5米，用于判断是否已经停止
	 * 如果下一个点位和前一个点位距离超过5米，则清除以前的点位
	 * @return
	 */
	public LinkedList<VPIPoint> getHistory() {
		return history;
	}

	public void setHistory(LinkedList<VPIPoint> history) {
		this.history = history;
	}

	
}

