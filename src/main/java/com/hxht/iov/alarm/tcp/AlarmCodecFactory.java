/** 
 * @author gq
 * @date 2015年11月9日 上午10:19:41 
 */
package com.hxht.iov.alarm.tcp;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class AlarmCodecFactory implements ProtocolCodecFactory {
	
	private ProtocolEncoder encoder;
	
	public AlarmCodecFactory(){
		encoder = new AlarmEncoder();
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return null;
	}

}

