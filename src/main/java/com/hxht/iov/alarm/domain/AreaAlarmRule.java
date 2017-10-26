/** 
 * @author gq
 * @date 2015年11月3日 上午10:39:20 
 */
package com.hxht.iov.alarm.domain;

import com.hxht.iov.alarm.domain.AlarmBean.Rule;

public class AreaAlarmRule extends AlarmRule {
	
	private double radius = 0.0;

	public AreaAlarmRule(Rule r) {
		super(r);
	}

	@Override
	public int computePosition(VPIPoint p){
		if(this.getRule() == null)
			throw new IllegalArgumentException("Rule object in areaRule can not be null!");
		int state = -1;
		if(RULE_OF_FENCE_POLYGON.equals(getRule().getSubRuleType())){//多边形
			state = inPolygon(p);
		}
		if(RULE_OF_FENCE_CIRCLE.equals(getRule().getSubRuleType())){//圆形
			state = inCircle(p);
		}
		if(RULE_OF_FENCE_RECTANGLE.equals(getRule().getSubRuleType())){//矩形
			state = inRectangle(p);
		}
		return state;
	}
	
	private int inPolygon(VPIPoint p){
		if(points == null || points.length < 3){
			log.error("POLYGON rule points num must > 2 : "+(points==null?"null":points.length));
			return -1;
		}
		boolean isIn = false;
		try {
			isIn = getAlg().inPolygon(p, points);
		} catch (Exception e) {
			log.error(e);
			return -1;
		}
		return isIn ? 1 : 0;
	}
	
	private int inCircle(VPIPoint p) {
		if(points == null || points.length != 1){
			log.error("CIRCLE rule points num must is 1 : "+(points==null?"null":points.length));
			return -1;
		}
		double distance = this.getAlg().computeDistance(p.getLatitude(),
				p.getLongitude(), points[0].getLatitude(),
				points[0].getLongitude());
		return distance > radius ? 0 : 1;
	}
	
	private int inRectangle(VPIPoint p) {
		if(points == null || points.length != 2){
			log.error("RECTANGLE rule points num must is 2 : "+(points==null?"null":points.length));
			return -1;
		}
		boolean inLng = p.getLongitude() > Math.min(points[0].getLongitude(),
				points[1].getLatitude())
				&& p.getLongitude() < Math.max(points[0].getLongitude(),
						points[1].getLatitude());
		if(inLng){
			boolean inLat = p.getLatitude() > Math.min(points[0].getLatitude(),
					points[1].getLatitude())
					&& p.getLatitude() < Math.max(points[0].getLatitude(),
							points[1].getLatitude());
			if(inLat){
				return 1;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}
	
	protected VPIPoint[] analysePointData(String ps){
		if(RULE_OF_FENCE_CIRCLE.equals(getRule().getSubRuleType())){//圆形
			String[] data = ps.split("[|]");
			VPIPoint[] vps = new VPIPoint[1];
			try {
				String[] pp = data[0].split(",");
				vps[0] = new VPIPoint(Double.parseDouble(pp[0]), Double.parseDouble(pp[1]));
				radius = Double.parseDouble(data[1]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("CIRCLE rule("+getRule().getId()+") params 'PARAMS_POINT_DATA' value format error: "+ps);
			}
			return vps;
		}else{
			return super.analysePointData(ps);
		}
	}
	
	protected void handlePointDataIsEmpty(){
		throw new IllegalArgumentException("rule("+getDescription()+") params 'POINT_DATA' value can not be empty! ");
	}
	
}

