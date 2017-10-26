/** 
 * @author gq
 * @date 2015年12月22日 下午1:39:04 
 */
package com.hxht.iov.alarm.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class CustomTimeSerializer extends JsonSerializer<Date> {

	@Override
	 public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
	 SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	 String formattedDate = formatter.format(value);
	 jgen.writeString(formattedDate);
	 }


}

