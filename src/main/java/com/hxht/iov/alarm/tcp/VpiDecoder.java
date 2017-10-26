/** 
 * @author gq
 * @date 2015年11月16日 上午9:25:11 
 */
package com.hxht.iov.alarm.tcp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hxht.iov.alarm.vpi.IPositionInfoTranslator;
import com.hxht.iov.alarm.vpi.VpiTranslator;

/**
 * 用于将位置信息转换成VPI对象 每条位置信息用换行符结束
 */
public class VpiDecoder extends TextLineDecoder {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private IPositionInfoTranslator vpiTranslator = new VpiTranslator();

	/**
	 * 上次收到的不完整消息
	 */
	private String preTxt = "";

	private List<String> sureTxts = new ArrayList<String>();

	public VpiDecoder(Charset charset, LineDelimiter delimiter) {
		super(charset, delimiter);
	}

	@Override
	protected void writeText(IoSession session, String message,
			ProtocolDecoderOutput out) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(
					"VPI DECODER:Processing a MESSAGE_RECEIVED for session {}",
					session.getId());

		String in = (String) message;
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("VPI DECODER: " + in);
		Object o = vpiTranslator.deCode(in);
		if (o != null) {
			out.write(o);
		} else {
			LOGGER.error("VPI DECODER: parse to vpi object happen error:" + in);
		}
	}

	/**
	 * 每次获取的tcp信息可能不是完整的，因此需要分析处理，将每个{}对中的信息提取出来
	 * 
	 * @param msg
	 */
	private void analyzeIO(String msg) {
		int indexS = msg.indexOf("{");
		int indexE = msg.indexOf("}");
		int size = msg.length();
		if (indexS == -1 && indexE == -1) {
			setPreTxt(getPreTxt() + msg.trim());
			return;
		}
		if (indexS > indexE) {
			if (indexE == -1) {
				setPreTxt(getPreTxt() + (msg.substring(indexS + 1)));
				return;
			} else {
				setPreTxt(getPreTxt() + (msg.substring(0, indexE)));
				getSureTxts().add(getPreTxt());
				setPreTxt("");
			}
			analyzeIO(msg.substring(indexS));
		}
		if (indexE > indexS) {
			getSureTxts().add(msg.substring(indexS + 1, indexE));
			if (size - 1 > indexE) {
				analyzeIO(msg.substring(indexE + 1));
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
