/** 
 * @author gq
 * @date 2015年12月22日 下午1:21:20 
 */
package com.hxht.iov.alarm.domain;

public class RestAlarmRules {

	private String code;
	
	private String desc;
	
	private AlarmBean alarmBean;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public AlarmBean getAlarmBean() {
		return alarmBean;
	}

	public void setAlarmBean(AlarmBean alarmBean) {
		this.alarmBean = alarmBean;
	}
}

