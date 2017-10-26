/** 
 * @author gq
 * @date 2015年11月4日 上午10:03:05 
 */
package com.hxht.iov.alarm.tcp;


import net.sf.json.JSONObject;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.hxht.iov.alarm.domain.Alarm;

public class AlarmEncoder implements ProtocolEncoder {

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		Alarm alarm = (Alarm)message;
		IoBuffer.setUseDirectBuffer(true);
		IoBuffer buffer = IoBuffer.allocate(200);  
        buffer.setAutoExpand(true);  
        buffer.setAutoShrink(true); 
        JSONObject json = JSONObject.fromObject(alarm);
        buffer.put(json.toString().getBytes());
        buffer.flip();
        out.write(buffer);
		
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		
	}
	

}

