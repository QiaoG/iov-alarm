/** 
 * @author gq
 * @date 2015年11月13日 下午4:14:29 
 */
package com.hxht.iov.alarm.tcp;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
//import org.apache.mina.filter.codec.ProtocolCodecFilter;
//import org.apache.mina.filter.codec.ProtocolCodecFilter.EncodedWriteRequest;
//import org.apache.mina.filter.codec.ProtocolCodecFilter.MessageWriteRequest;
//import org.apache.mina.filter.codec.ProtocolCodecFilter.ProtocolDecoderOutputImpl;
//import org.apache.mina.filter.codec.ProtocolCodecFilter.ProtocolEncoderOutputImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxht.iov.alarm.vpi.IPositionInfoTranslator;
import com.hxht.iov.alarm.vpi.VpiTranslator;

/**
 * 由VpiDodecFactory构造的ProtocolCodecFilter替换
 *
 */
@Deprecated
public class VpiProtocolCodecFilter extends IoFilterAdapter {

	/** A logger for this class */
    private final Logger LOGGER = LoggerFactory.getLogger(VpiProtocolCodecFilter.class);


    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        LOGGER.debug("Processing a MESSAGE_RECEIVED for session {}", session.getId());

        if (!(message instanceof String)) {
            LOGGER.error("in message must is String");
            return;
        }

        String in = (String) message;
        LOGGER.debug("receive:"+in);
		analyzeIO(in);
		if (getSureTxts().size() > 0) {
			Object o = vpiTranslator.deCode(getSureTxts().get(0));
			if (o != null) {
				nextFilter.messageReceived(session, o);
			} else {
				LOGGER.error("parse to vpi object happen error:" + in);
			}
		}
    }
    
    private IPositionInfoTranslator vpiTranslator = new VpiTranslator();
	
	/**
	 * 上次收到的不完整消息
	 */
	private String preTxt="";
	
	private List<String> sureTxts = new ArrayList<String>();

	/**
	 * 每次获取的tcp信息可能不是完整的，因此需要分析处理，将每个{}对中的信息提取出来
	 * @param msg
	 */
	private void analyzeIO(String msg){
		int indexS = msg.indexOf("{");
		int indexE = msg.indexOf("}");
		int size = msg.length();
		if(indexS == -1 && indexE == -1){
			setPreTxt(getPreTxt() + msg.trim());
			return;
		}
		if(indexS > indexE){
			if(indexE == -1){
				setPreTxt(getPreTxt() + (msg.substring(indexS+1)));
				return;
			}else{
				setPreTxt(getPreTxt() + (msg.substring(0, indexE)));
				getSureTxts().add(getPreTxt());
				setPreTxt("");
			}
			analyzeIO(msg.substring(indexS));
		}
		if(indexE > indexS){
			getSureTxts().add(msg.substring(indexS+1, indexE));
			if(size-1 > indexE){
				analyzeIO(msg.substring(indexE+1));
			}
		}
	}
	
	public String getPreTxt() {
		return preTxt;
	}

	public void setPreTxt(String preTxt) {
		this.preTxt = preTxt;
	}

	public List<String> getSureTxts() {
		return sureTxts;
	}

	public void setSureTxts(List<String> sureTxts) {
		this.sureTxts = sureTxts;
	}

}

