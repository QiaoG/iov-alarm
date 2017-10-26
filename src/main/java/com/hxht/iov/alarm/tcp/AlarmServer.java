/** 
 * @author gq
 * @date 2016年1月14日 上午8:40:10 
 */
package com.hxht.iov.alarm.tcp;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlarmServer {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private NioSocketAcceptor accepter;
	
	//@PostConstruct
	public void start(){
		if(accepter != null){
			try {
				accepter.bind();
				log.info("alarm server("+accepter.getDefaultLocalAddress().getPort()+") starting ...");
			} catch (IOException e) {
				log.error("tcp server error:",e);
			}
		}else{
			log.error("NioSocketAcceptor inject fail!");
		}
	}

}

