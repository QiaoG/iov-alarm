/** 
 * @author gq
 * @date 2016年1月14日 下午5:10:01 
 */
package com.hxht.iov.alarm.handle;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hxht.iov.alarm.rule.AlarmRuleManager;
import com.hxht.iov.alarm.utils.CoordinateAlgorithm;
import com.hxht.iov.alarm.vpi.IVpiHandler;

@Deprecated
//@Component
public class AlarmServiceFactory {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private List<IVpiHandler> handlers = new ArrayList<IVpiHandler>();

	@Value("#{propertiesUtils.getInt('${stop_alarm_compute_points_num}',5)}")
	private int checkStopPointNum;
	
	@Value("#{propertiesUtils.getInt('${stop_alarm_compute_points_distance}',5)}")
	private int checkStopPointsDistance;
	
	@Value("#{propertiesUtils.getBoolean('${support_hard_alarm_resolve}')}")
	private boolean supportHardAlarmResolve;
	
	@Autowired
	private CoordinateAlgorithm alg;
	
	@Autowired
	private AlarmMessageSender alarmSender;
	
	@Autowired
	private AlarmRuleManager ruleManager;
	
	private int maxHandlersNum;
	
	public IVpiHandler getVpiHandler(){
		IVpiHandler handler = null;
		for (IVpiHandler h : handlers) {
			if (h.getState() == IVpiHandler.READY) {
				handler = h;
				break;
			}
		}
		
		if (handler == null) {
			if (handlers.size() >= getMaxHandlersNum()) {
				log.error("alarm service count("+handlers.size()+") can not greater than max("+getMaxHandlersNum()+")");
			} else {
				synchronized (handlers) {
					handler = createAlarmService();
					handlers.add(handler);
				}
			}
		}
		
		return handler;
	}
	
	public IVpiHandler createAlarmService(){
		AlarmService as = new AlarmService();
		as.setAlg(alg);
		as.setAlarmSender(alarmSender);
		as.setRuleManager(ruleManager);
		as.setCheckStopPointNum(checkStopPointNum);
		as.setCheckStopPointsDistance(checkStopPointsDistance);
		as.setSupportHardAlarmResolve(supportHardAlarmResolve);
		return as;
	}

	public int getMaxHandlersNum() {
		return maxHandlersNum;
	}

	public void setMaxHandlersNum(int maxHandlersNum) {
		this.maxHandlersNum = maxHandlersNum;
	}
}

