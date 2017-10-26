/** 
 * @author gq
 * @date 2015年11月16日 上午9:27:32 
 */
package com.hxht.iov.alarm.tcp;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.textline.LineDelimiter;

/**
 * 由于只解析前置机过来的vpi信息，所以没有添加encoder
 *
 */
public class VpiCodecFactory implements ProtocolCodecFactory{
	
	private VpiDecoder decoder;
	
	public VpiCodecFactory(Charset charset){
		decoder = new VpiDecoder(charset, LineDelimiter.AUTO);
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return null;
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

}

