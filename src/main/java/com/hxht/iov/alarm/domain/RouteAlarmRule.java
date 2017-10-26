/** 
 * @author gq
 * @date 2015年11月3日 上午10:38:25 
 */
package com.hxht.iov.alarm.domain;

import java.util.Set;

import com.hxht.iov.alarm.domain.AlarmBean.Rule;
import com.hxht.iov.alarm.domain.AlarmBean.Rule.Section;

public class RouteAlarmRule extends AlarmRule {
	
	public RouteAlarmRule(Rule r) {
		super(r);
	}

	@Override
	public int computePosition(VPIPoint p){
		if(this.getRule() == null)
			throw new IllegalArgumentException("Rule in LineRule can not be null!");
		Set<Section> sections = getRule().getSection();
		if(sections == null || sections.size() == 0)
			throw new IllegalArgumentException("Set<Section> in LineRule can not be empty!");
		
		int index = 0;
		double distance = 0.0;
		boolean find = false;
		try {
			for(Section sec : getRule().getSection()){
				distance = getAlg().computeDistance(p.getLatitude(), p.getLongitude(),
						Double.parseDouble(sec.getGpsY0()), Double.parseDouble(sec.getGpsX0()),
						Double.parseDouble(sec.getGpsY1()), Double.parseDouble(sec.getGpsX1()));
				index++;
				if(distance < sec.getWidth()/2){
					find = true;
					break;
				}
			}
		} catch (NumberFormatException e) {
			log.error("COMPUTE POSITION:Sections in LineRule resolve error:",e);
			return -1;
			
		}
		return find ? index : 0;
	}

}

