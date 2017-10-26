/** 
 * @author gq
 * @date 2015年11月2日 下午3:50:42 
 */
package com.hxht.iov.alarm.vpi;

import java.io.IOException;
import java.text.SimpleDateFormat;




import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;

import com.hxht.iov.alarm.domain.VPI;

/**
 * 车辆位置信息(vpi)翻译器
 *
 */
public class VpiTranslator implements IPositionInfoTranslator{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public VpiTranslator(){
		mapper.disable(Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hxht.iov.alarm.vpi.IPositionInfoTranslator#deCode(java.lang.String)
	 * gis
	 * format:{"msg":512,"seq":1,"sim":13100000000,"alarm":11111(报警标识),"state":1234(状态)
	 * 		,"lon":11111(经度),"lat":23333(维度),"hig":512(高度),"speed":100(百米/h)
	 * 		,"direct":100(0-359),"time":"15-10-27-14-41-54(YY-MM-DD-hh-mm-ss)"
	 * 		,"att":[{"key":1,"val":[100]}(里程),{"key":2,"val":[100]}(油量),{"key":3,"val":[100]}(速度)
	 * 		,{"key":17,"val":[1,1234]}(超速报警附加信息),{"key":18,"val":[1,1234,0]}(进出区域路线报警附加信息)
	 * 		,{"key":19,"val":[1234,10,0](路段行驶不足/过长附加信息)}]
	 * 		,"ver":"1.0.0"}
	 */
	public Object deCode(String gis) {
		VPI vo = (VPI) fromJson(gis);
		return vo;
	}
	
	private Object fromJson(String gis){
		VPI vo = null;
		try {
			vo = mapper.readValue(gis, VPI.class);
		} catch (JsonParseException e) {
			log.error("JSON TO VPI(1):",e);
		} catch (JsonMappingException e) {
			log.error("JSON TO VPI(2):",e);
		} catch (IOException e) {
			log.error("JSON TO VPI(3):",e);
		}
		return vo;
	}
	
}

