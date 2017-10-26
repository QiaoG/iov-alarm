/** 
 * @author gq
 * @date 2015年10月27日 上午11:14:35 
 */
package com.hxht.iov.alarm.rule;

import java.util.List;

import com.hxht.iov.alarm.domain.AlarmRule;
import com.hxht.iov.alarm.domain.VPI;

public interface IRuleManage {
	
	public List<AlarmRule> findRoles(Object gi);
	
	
}

