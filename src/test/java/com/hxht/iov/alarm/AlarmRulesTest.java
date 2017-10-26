/** 
 * @author gq
 * @date 2015年12月22日 下午12:13:59 
 */
package com.hxht.iov.alarm;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;

import com.hxht.iov.alarm.domain.Alarm;
import com.hxht.iov.alarm.domain.RestAlarmRules;
import com.hxht.iov.alarm.domain.VPI;
import com.hxht.iov.alarm.rule.AlarmRuleManager;

@RunWith(AlarmJUnit4ClassRunner.class)
@ContextConfiguration(classes=AlarmRootConfig.class)
public class AlarmRulesTest {
	
	@Autowired
	private AlarmRuleManager ruleManager;
	
	//@Test
	public void assertRuleManager(){
		ruleManager.refreshRules(null);
		assertTrue(ruleManager.getRulesCache().size()>1);
	}
	
	@Test
	public void jsonParse(){
		String str = mockAllRules();
		RestAlarmRules ra = ruleManager.parseToObject(str);
		ruleManager.handleResultAll(ra);
		VPI vpi = new VPI();
//		vpi.setSim(13100000001);
		assertTrue(ruleManager.getRulesCache().size()==3);
		assertTrue(ruleManager.findRoles(vpi).size() == 2);
	}
	
	public void testAlarmSend(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Alarm alarm = new Alarm();
		alarm.setName("test_alarm");
		alarm.setSim("13561391789");
		alarm.setTime(format.format(new Date()));
		alarm.setType(2);
		alarm.setValue(55);
		JSONObject json = JSONObject.fromObject(alarm);
		System.out.println(json.toString());
	}
	
	private String mockAllRules(){
		StringBuffer sb = new StringBuffer();
		sb.append("{\"code\":\"000000\",\"desc\":\"鎴愬姛\",\"alarmBean\":{\"ruleList\":[");
		sb.append("{\"id\":\"105\",\"ruleType\":\"FENCE\",\"subRuleType\":\"RECTANGLE\",\"validBeginDate\":\"2015-11-20\",\"validEndDate\":\"2015-11-30\",\"validBeginTime\":\"10:00:00\",\"validEndTime\":\"15:00:00\",\"opStatus\":0,");
		sb.append("\"params\":{\"GIS_POINT_DATA\":\"116.239772,40.230136|116.245651,40.234788\",\"IS_IN_AREA_ALARM\":\"true\",\"OVER_SPEED_KEEP_TIME\":\"10\",\"POINT_DATA\":\"116.2336967175707,40.22889376917155|116.23957893835558,40.2335464558991\",\"IS_OUT_AREA_ALARM\":\"false\",\"IS_DETECT_MAX_SPEED\":\"true\",\"ALLOW_MAX_SPEED\":\"120\"},");
		sb.append("\"bindingSims\":[\"18681892548\",\"13100000001\",\"15800000000\",\"15112390004\",\"18301059249\",\"15855555555\",\"150101235\"],\"section\":[]},");
		
		sb.append("{\"id\":\"136\",\"ruleType\":\"FENCE\",\"subRuleType\":\"CIRCLE\",\"validBeginDate\":\"2015-12-08\",\"validEndDate\":\"2015-12-30\",\"validBeginTime\":\"09:42:00\",\"validEndTime\":\"09:44:00\",\"opStatus\":0,");
		sb.append("\"params\":{\"IS_IN_AREA_ALARM\":\"true\",\"POINT_DATA\":\"104.11703551791535,30.677678692740585|111.2476942036376\",\"IS_OUT_AREA_ALARM\":\"true\"},\"bindingSims\":[\"18681892548\",\"15112390004\"],\"section\":[]},");
		sb.append("{\"id\":\"137\",\"ruleType\":\"LINE\",\"subRuleType\":\"NULL\",\"validBeginDate\":\"2015-12-08\",\"validEndDate\":\"2015-12-22\",\"validBeginTime\":\"09:44:30\",\"validEndTime\":\"09:46:00\",\"opStatus\":0,");
		sb.append("\"params\":{\"POINT_DATA\":\"104.10622164867868,30.681587786455143|104.11304757310552,30.683104076814203|104.1070801182013,30.677050237716415|104.113778259381,30.677829002166106|104.10716571706203,30.686347054753153|104.10583458857391,30.68059106911643\",\"END_STATION\":null,\"MILEAGE\":\"mm\",\"START_STATION\":null},"
				+ "\"bindingSims\":[\"15112390004\",\"1868189254\"],");
		sb.append("\"section\":[");
		sb.append("{\"id\":\"2014\",\"pointId\":\"2015\",\"gpsX0\":\"104.10716571706203\",\"gpsY0\":\"30.686347054753153\",\"gpsX1\":\"104.10583458857391\",\"gpsY1\":\"30.68059106911643\",\"pointX0\":\"104.10583458857391\",\"pointY0\":\"30.68059106911643\",\"width\":0.0,\"length\":0.0,\"maxSpeed\":0,\"maxDrivingTime\":0,\"minDrivingTime\":0,\"overSpeedKeepTime\":0},");
		sb.append("{\"id\":\"2010\",\"pointId\":\"2010\",\"gpsX0\":\"104.10622164867868\",\"gpsY0\":\"30.681587786455143\",\"gpsX1\":\"104.11304757310552\",\"gpsY1\":\"30.683104076814203\",\"pointX0\":\"104.10622164867868\",\"pointY0\":\"30.681587786455143\",\"width\":0.0,\"length\":0.0,\"maxSpeed\":0,\"maxDrivingTime\":0,\"minDrivingTime\":0,\"overSpeedKeepTime\":0},");
		sb.append("{\"id\":\"2012\",\"pointId\":\"2013\",\"gpsX0\":\"104.1070801182013\",\"gpsY0\":\"30.677050237716415\",\"gpsX1\":\"104.113778259381\",\"gpsY1\":\"30.677829002166106\",\"pointX0\":\"104.113778259381\",\"pointY0\":\"30.677829002166106\",\"width\":0.0,\"length\":0.0,\"maxSpeed\":0,\"maxDrivingTime\":0,\"minDrivingTime\":0,\"overSpeedKeepTime\":0},");
		sb.append("{\"id\":\"2013\",\"pointId\":\"2014\",\"gpsX0\":\"104.113778259381\",\"gpsY0\":\"30.677829002166106\",\"gpsX1\":\"104.10716571706203\",\"gpsY1\":\"30.686347054753153\",\"pointX0\":\"104.10716571706203\",\"pointY0\":\"30.686347054753153\",\"width\":0.0,\"length\":0.0,\"maxSpeed\":0,\"maxDrivingTime\":0,\"minDrivingTime\":0,\"overSpeedKeepTime\":0},");
		sb.append("{\"id\":\"2011\",\"pointId\":\"2012\",\"gpsX0\":\"104.11304757310552\",\"gpsY0\":\"30.683104076814203\",\"gpsX1\":\"104.1070801182013\",\"gpsY1\":\"30.677050237716415\",\"pointX0\":\"104.1070801182013\",\"pointY0\":\"30.677050237716415\",\"width\":0.0,\"length\":0.0,\"maxSpeed\":0,\"maxDrivingTime\":0,\"minDrivingTime\":0,\"overSpeedKeepTime\":0}");
		sb.append("]}],");
		sb.append("\"rules\":{},\"vehicles\":[]}}");
		return sb.toString();
	}
}

