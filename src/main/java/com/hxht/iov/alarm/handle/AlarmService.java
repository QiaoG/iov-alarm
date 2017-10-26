/** 
 * @author gq
 * @date 2015年10月27日 上午11:21:37 
 */
package com.hxht.iov.alarm.handle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hxht.iov.alarm.domain.Alarm;
import com.hxht.iov.alarm.domain.AlarmRule;
import com.hxht.iov.alarm.domain.RuleState;
import com.hxht.iov.alarm.domain.VPI;
import com.hxht.iov.alarm.domain.VPIPoint;
import com.hxht.iov.alarm.rule.AlarmRuleManager;
import com.hxht.iov.alarm.utils.CoordinateAlgorithm;
import com.hxht.iov.alarm.vpi.IVpiHandler;

@Component
public class AlarmService implements IVpiHandler{
	
	public final int SPEED_ALARM_TYPE = 1;
	
	public final int STOP_ALARM_TYPE = 2;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Value("#{propertiesUtils.getInt('${stop_alarm_compute_points_num}',5)}")
	private int checkStopPointNum;
	
	@Value("#{propertiesUtils.getInt('${stop_alarm_compute_points_distance}',5)}")
	private int checkStopPointsDistance;
	
	@Autowired
	private CoordinateAlgorithm alg;
	
	@Autowired
	private AlarmMessageSender alarmSender;
	
	@Autowired
	private AlarmRuleManager ruleManager;
	
	private int maxHandlersNum;
	
	@Value("#{propertiesUtils.getBoolean('${support_hard_alarm_resolve}')}")
	private boolean supportHardAlarmResolve;
	
	
	@Override
	public void handleVpi(final VPI pi) {
		List<Alarm> alarms = new ArrayList<Alarm>();
		List<Alarm> li = null;
		li = analyzeHardAlarm(pi);
		if(li != null)
			alarms.addAll(li);
		li = analyzeSoftAlarm(pi);
		if(li != null)
			alarms.addAll(li);
		if(alarms.size() > 0){
			notifyAlarms(alarms.toArray(new Alarm[0]));
		}
		
	}
	
	
	/**
	 * 硬报警
	 * @param vo
	 * @return
	 */
	private List<Alarm> analyzeHardAlarm(VPI vo){
		if(vo.getAlarm() == 0 || !isSupportHardAlarmResolve()){
			return null;
		}
		char[] chars = Integer.toBinaryString(vo.getAlarm()).toCharArray();
		int len = chars.length;
		List<Integer> as = new ArrayList<>();
		for(int i = len - 1,j = 0; i >= 0; i--,j++){
			if(j == 16 || j == 17)//保留
				continue;
			if(chars[i] == '1'){
				as.add(j);
			}
		}
		if(log.isDebugEnabled()){
			log.debug("ALARM HARD:"+String.valueOf(chars)+"  "+as.size());
		}
		List<Alarm> alarms = new ArrayList<Alarm>();
		for(int a : as){
			alarms.add(createAlarm(vo,null,100+a));
		}
		return  alarms;
	}
	
	private List<Alarm> analyzeSoftAlarm(VPI vo){
		if(getRuleManager() == null){
			log.error("ALARM SERVICE:rule manager can not be null!");
			return null;
		}
		List<AlarmRule> rules = this.getRuleManager().findRoles(vo);
		if(log.isDebugEnabled()){
			log.debug("ALARM SOFT:get rule count = "+rules.size()+ " by sim="+vo.getSim());
		}
		List<Alarm> alarms = new ArrayList<Alarm>();
		Alarm[] as = null;
		for(AlarmRule rule : rules){
			as = analyze(vo, rule);
			for(Alarm a : as){
				alarms.add(a);
			}
		}
		
		return alarms;
	}
	
	private Alarm[] analyze(VPI pi, AlarmRule rule) {
		RuleState clone = null;
		RuleState old = null;
		synchronized (rule) {
			old = rule.getRuleStateBySim(pi.getSim());
			if(old != null){
				clone = (RuleState) old.clone();//基于规则的前一个状态
			}else{
				if(log.isDebugEnabled())
					log.debug("ALARM ANALYZE:not find RuleState in Rule, sim = "+ pi.getSim());
				old = new RuleState(pi.getSim());
				rule.addSimsState(old);
			}
		}
		
		List<Alarm> alarms = null;
		synchronized (old) {
			alarms = analyzeRuleState(clone,old,pi,rule);
		}
		
		return alarms.toArray(new Alarm[0]);
	}
	
	private List<Alarm> analyzeRuleState(RuleState clone,RuleState old,VPI pi,AlarmRule rule){
		List<Alarm> alarms =  new ArrayList<>();
		Date cur = new Date();
		old.setTime(cur);
		old.setVpiTime(pi.getTime());
		if(pi.getVpiPoint() != null){
			old.setLocation((VPIPoint)pi.getVpiPoint().clone());
		}
		int state = -1;
		try {
			state = rule.computePosition(pi.getVpiPoint());
			old.setPosition(state);
		} catch (Exception e) {
			old.setPosition(state);
			log.error("ALARM ANALYZE:compute position error:"+rule.getDescription(),e);
			
		}
		if(state == -1)
			return alarms;
		
		if (log.isDebugEnabled()) {
			log.debug("ALARM ANALYZE:positioin is "+(state==0?"out":state)+" be based on("+pi.getSim()+":"+ pi.getLat() + "|" + pi.getLon() + "), "
					+ rule.getDescription() );
		}
		if(state > 0 && clone != null){//在里面
			
			Alarm alarm = checkSpeedAlarm(pi,rule,clone,old,cur);
			if(alarm != null)
				alarms.add(alarm);
			alarm = checkStopAlarm(pi,rule,clone,old,cur);
			if(alarm != null)
				alarms.add(alarm);
		}
		if(state == 0){
			old.clear();
		}
		return alarms;
	}
	
	private String logCheckSpeed(VPI pi, AlarmRule rule, RuleState old,
			RuleState cur, Date curT) {
		int max = rule.getAllowMaxSpeed();
		int speed = pi.getSpeed();
		int keep = rule.getOverSpeedKeepTime();
		String s = "ALARM ANALYZE SPEED:("
				+ (cur.isSpeedAlarm() ? "true" : "false") + ") " + pi.getSim()
				+ " maxspeed=" + max;
		s += " speed=" + speed + " keep=" + keep + " begin=";
		s += (cur.getBeginTimeOfSpeed() == null ? "null" : format.format(cur.getBeginTimeOfSpeed()));
		s += " current=" + format.format(curT);
		s += " vpi time="+ (pi.getTime() == null ? "null" : format.format(pi.getTime()));
		return s;
	}
	
	private Alarm checkSpeedAlarm(VPI pi,AlarmRule rule,RuleState old, RuleState cur, Date curT){
		int max = rule.getAllowMaxSpeed();
		int speed = pi.getSpeed();
		int keep = rule.getOverSpeedKeepTime();
		if (log.isDebugEnabled()) {
			log.debug(logCheckSpeed(pi, rule, old, cur, curT));
		}
		Alarm alarm = null;
		if(!cur.isSpeedAlarm() && max > 0 && speed > max){
			if(old != null && old.getBeginTimeOfSpeed() != null){
				long det = curT.getTime() - old.getBeginTimeOfSpeed().getTime();
				if(keep > -1 && det > keep){//alarm
					alarm = createAlarm(pi,rule,SPEED_ALARM_TYPE);
					cur.setBeginTimeOfSpeed(curT);
					cur.setSpeedAlarm(true);
				}
			}else{
				cur.setBeginTimeOfSpeed(new Date());
			}
		}
		if (alarm != null && log.isDebugEnabled()) {
			log.debug("ALARM ANALYZE SPEED:create a alarm:"+alarm);
		}
		return alarm;
	}
	
	private String logLocationOfRuleState(RuleState rt){
		if(rt.getLocation() == null){
			return "null";
		}else{
			return rt.getLocation().getLongitude()+"|"+rt.getLocation().getLatitude();
		}
	}
	
	private Alarm checkStopAlarm(VPI pi,AlarmRule rule,RuleState old, RuleState cur, Date curT){
		int threshold = rule.getOverStopTime();
		if (log.isDebugEnabled()) {
			log.debug("ALARM ANALYZE STOP:("+old.isStopAlarm()+") " + pi.getSim() + " threshold=" + threshold
					+ " curP=" + cur.getLocation().getLongitude()+"|"+cur.getLocation().getLatitude()
					+ " preP=" + logLocationOfRuleState(old) 
					+ " begin=" + (old.getBeginTimeOfStop() == null ? "null" : format.format(old.getBeginTimeOfStop()))
					+ " current=" + format.format(curT)
					+ " history=" + cur.getHistory().size() + "|" + getCheckStopPointNum()
					+ " vpi-time="+(pi.getTime()==null?"null":format.format(pi.getTime())));
		}
		threshold = threshold == -1 ? 0 : threshold;
		Alarm alarm = null;
		if(old.getLocation() == null){
			log.info("ALARM ANALYZE STOP: pre location point is null!");
			return alarm;
		}
		double distance = getAlg().computeDistance(old.getLocation().getLatitude(),
				old.getLocation().getLongitude(), cur.getLocation().getLatitude(),
				cur.getLocation().getLongitude());
		if(log.isDebugEnabled()){
			log.debug("ALARM ANALYZE STOP: distance between pre and cur = " + distance + "/"+ getCheckStopPointsDistance());
		}
		//如果和上次位置的距离小于5米，则加入最近相邻位置列表，当相邻位置列表位置数为10时，计算头尾两位置的距离，如果小于5米，则认为已经停止
		//如果和上次位置的距离大于5米，则清除以前的位置
		if(distance > getCheckStopPointsDistance()){//行驶中
			cur.getHistory().clear();
			cur.setStopAlarm(false);//行驶
			cur.setBeginTimeOfStop(null);
		}
		cur.getHistory().add(cur.getLocation());
		if(cur.getHistory().size() >= getCheckStopPointNum()){
			if(!cur.isStopAlarm()){//还没有告警过
				distance = getAlg().computeDistance(cur.getHistory().getFirst().getLatitude(),
						cur.getHistory().getFirst().getLongitude(), cur.getLocation().getLatitude(),
						cur.getLocation().getLongitude());
				boolean recordBegin = false;
				long time = 0;
				if(distance < getCheckStopPointsDistance()){
					if(!old.isStopAlarm()){//还没有告过警
						if(cur.getBeginTimeOfStop() == null){//车辆停止，开始计时
							recordBegin = true;
							cur.setBeginTimeOfStop(curT);
						}else{
							time = curT.getTime()/1000 - cur.getBeginTimeOfStop().getTime()/1000;
							if(time > threshold){
								alarm = createAlarm(pi,rule,STOP_ALARM_TYPE);
								cur.setStopAlarm(true);
							}
						}
						
					}
				}else{//慢速行驶中
					cur.setStopAlarm(false);//行驶
					cur.setBeginTimeOfStop(null);
				}
				if (log.isDebugEnabled()) {
					log.debug("ALARM ANALYZE STOP: "+distance+"/"+getCheckStopPointsDistance()+" "+cur.getHistory().size()+"/"+getCheckStopPointNum()
							+ " "+(recordBegin?"stoped!":time+"/"+threshold));
				}
			}
			cur.getHistory().removeFirst();
		}
		return alarm;
	}
	
	/**
	 * @param pi
	 * @param rule
	 * @param type 1:超速  2:滞留
	 * @return
	 */
	private Alarm createAlarm(VPI pi,AlarmRule rule, int type){
		Alarm alarm = new Alarm();
		alarm.setTime(format.format(new Date()));
		if(rule != null){
			alarm.setRuleId(rule.getRule().getId());
			alarm.setName(rule.getTypeDescription() + (type==SPEED_ALARM_TYPE?" SPEED ALARM":" STOP ALARM"));
		}
		else
			alarm.setRuleId("-1");
		alarm.setLatitude(pi.getVpiPoint().getLatitude());
		alarm.setLongitude(pi.getVpiPoint().getLongitude());
		alarm.setSim(pi.getSim());
		alarm.setType(type);
		alarm.setValue(pi.getSpeed());
		
		return alarm;
	}

	public void notifyAlarms(Alarm[] alarms) {
		getAlarmSender().addAlarms(alarms);
	}

	private AlarmRuleManager getRuleManager() {
		return ruleManager;
	}

	public void setRuleManager(AlarmRuleManager ruleManager) {
		this.ruleManager = ruleManager;
	}

	public AlarmMessageSender getAlarmSender() {
		return alarmSender;
	}

	public void setAlarmSender(AlarmMessageSender alarmSender) {
		this.alarmSender = alarmSender;
	}

	public CoordinateAlgorithm getAlg() {
		return alg;
	}

	public void setAlg(CoordinateAlgorithm alg) {
		this.alg = alg;
	}

	public int getCheckStopPointNum() {
		return checkStopPointNum;
	}

	public void setCheckStopPointNum(int checkStopPointNum) {
		this.checkStopPointNum = checkStopPointNum;
	}

	public int getCheckStopPointsDistance() {
		return checkStopPointsDistance;
	}

	public void setCheckStopPointsDistance(int checkStopPointsDistance) {
		this.checkStopPointsDistance = checkStopPointsDistance;
	}

	public int getMaxHandlersNum() {
		return maxHandlersNum;
	}

	public void setMaxHandlersNum(int maxHandlersNum) {
		this.maxHandlersNum = maxHandlersNum;
	}

	public boolean isSupportHardAlarmResolve() {
		return supportHardAlarmResolve;
	}

	@Override
	public int getState() {
		return 0;
	}

	public void setSupportHardAlarmResolve(boolean supportHardAlarmResolve2) {
		this.supportHardAlarmResolve = supportHardAlarmResolve2;
		
	}

}

