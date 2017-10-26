/** 
 * @author gq
 * @date 2015年11月13日 上午11:19:21 
 */
package com.hxht.iov.alarm.handle;

import com.hxht.iov.alarm.domain.Alarm;

public interface IAlarmClient {
	
	public void connect();
	
	public int getState();
	
	public boolean notifyAlarm(Alarm alarm);

}

