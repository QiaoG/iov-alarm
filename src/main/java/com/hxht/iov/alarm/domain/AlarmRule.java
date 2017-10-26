/** 
 * @author gq
 * @date 2015年11月2日 下午3:22:33 
 */
package com.hxht.iov.alarm.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hxht.iov.alarm.domain.AlarmBean.Rule;
import com.hxht.iov.alarm.utils.CoordinateAlgorithm;

/**
 * 具体的规则需要继承此类
 *
 */
public abstract class AlarmRule {
	
	protected Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 围栏
	 */
	public static final String RULE_OF_FENCE = "FENCE";
	
	public static final String RULE_OF_FENCE_NULL = "NULL";
	
	/**
	 * 圆形
	 */
	public static final String RULE_OF_FENCE_CIRCLE = "CIRCLE";
	
	/**
	 * 矩形
	 */
	public static final String RULE_OF_FENCE_RECTANGLE = "RECTANGLE";
	
	/**
	 * 多边形
	 */
	public static final String RULE_OF_FENCE_POLYGON = "POLYGON";
	
	public static final String RULE_OF_LINE = "LINE";
	
	public static final String PARAMS_POINT_DATA = "POINT_DATA";
	
	public static final String PARAMS_IS_DETECT_MAX_SPEED = "IS_DETECT_MAX_SPEED";
	
	public static final String PARAMS_ALLOW_MAX_SPEED = "ALLOW_MAX_SPEED";
	
	public static final String PARAMS_OVER_SPEED_KEEP_TIME = "OVER_SPEED_KEEP_TIME";
	
	public static final String PARAMS_OVER_STOP_TIME = "OVER_STOP_TIME";
	
	private Rule rule;
	
	protected Map<String, RuleState> vehicleStateMap = new ConcurrentSkipListMap<String, RuleState>();
	
	private CoordinateAlgorithm alg;
	
	protected VPIPoint[] points;
	
	private boolean delete;//从cms获取所有规则时，用于标识是否需要从缓存中删除(ture:删除)
	
	public AlarmRule(Rule r){
		this.setRule(r);
	}
	
	/**
	 * 计算点位是在路线/区域里面还是外面
	 * @param p
	 * @return
	 *  0:外面
	 * 	1:(区域)里面
	 * 	>=1:(路线)所在路段的索引号
	 * -1: 验证有错误
	 */
	public int computePosition(VPIPoint p){
		return 0;
	}
	
	public RuleState getRuleStateBySim(String sim){
		return vehicleStateMap.get(sim);
	}
	
	public VPIPoint[] getPoints(){
		if(rule.getParams() == null)
			throw new IllegalArgumentException("rule("+getDescription()+") property 'params' value can not be empty!");
		String ps = rule.getParams().get(PARAMS_POINT_DATA);
		if(ps == null || ps.trim().length() == 0){
			try {
				handlePointDataIsEmpty();
			} catch (Exception e) {
				log.error(e);
			}
			points = new VPIPoint[0];
			return points;
		}
		try {
			points = analysePointData(ps);
		} catch (Exception e) {
			log.error(e);
			points = null;
		}
		return points;
		
	}
	
	protected void handlePointDataIsEmpty(){
		
	}
	
	protected VPIPoint[] analysePointData(String ps){
		String[] data = ps.split("[|]");
		String[] pp = null;
		VPIPoint[] vps = new VPIPoint[data.length];
		int index = 0;
		try {
			for(String lo : data){
				pp = lo.split(",");
				vps[index++] = new VPIPoint(Double.parseDouble(pp[0]), Double.parseDouble(pp[1]));
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("rule("+getDescription()+")params 'POINT_DATA' value format error: "+ps);
		}
		return vps;
	}
	
	/**
	 * 获取已经被移除的sim列表
	 * @return
	 */
	public List<String> getRemovedSims(){
		List<String> sims = new ArrayList<>();
		Set<String> bsims = rule.getBindingSims();
		for(String sim : this.vehicleStateMap.keySet()){
			if(bsims == null || bsims.size() == 0){
				sims.add(sim);
				continue;
			}
			if(!bsims.contains(sim)){
				sims.add(sim);
			}
		}
		
		return sims;
	}
	
	public void addSimsStateFromBindedSims(){
		Set<String> sims = rule.getBindingSims();
		if(sims == null || sims.size() == 0){
			return;
		}
		for(String sim : sims){
			this.vehicleStateMap.put(sim, new RuleState(sim));//value 不能为 null
		}
	}
	
	public void addSimsState(RuleState rt){
		this.vehicleStateMap.put(rt.getSim(), rt);
	}
	
	public void removeSimsState(List<String> sims){
		if(sims == null)
			throw new IllegalArgumentException("sims can not be null!");
		for(String sim : sims){
			this.vehicleStateMap.remove(sim);
		}
	}
	
	public void clearSimsStateMap(){
		this.vehicleStateMap.clear();
	}

//	/**
//	 * 记录车辆在此规则下的状态
//	 * key:sim
//	 * @return
//	 */
//	private Map<String, RuleState> getVehicleStateMap() {
//		return vehicleStateMap;
//	}
	
	public int getAllowMaxSpeed(){
		String ams = rule.getParams().get(PARAMS_ALLOW_MAX_SPEED);
		try {
			return Integer.parseInt(ams);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public int getOverSpeedKeepTime(){
		String oskt = rule.getParams().get(PARAMS_OVER_SPEED_KEEP_TIME);
		try {
			return Integer.parseInt(oskt);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public int getOverStopTime(){
		String oskt = rule.getParams().get(PARAMS_OVER_STOP_TIME);
		try {
			return Integer.parseInt(oskt);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public String getDescription(){
		String des = "";
		des = rule.getId()+":"+ getTypeDescription();
		return des;
	}
	
	public String getTypeDescription(){
		String des = "";
		des = rule.getRuleType()+"|"+(rule.getSubRuleType()==null?"null":rule.getSubRuleType());
		return des;
	}
	
	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public CoordinateAlgorithm getAlg() {
		return alg;
	}

	public void setAlg(CoordinateAlgorithm alg) {
		this.alg = alg;
	}

}

