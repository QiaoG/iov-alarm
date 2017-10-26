/** 
 * @author gq
 * @date 2015年10月29日 下午2:56:30 
 */
package com.hxht.iov.alarm.vpi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hxht.iov.alarm.domain.VPI;

/**
 * 位置信息的获取入口
 * @author gq
 *
 */
@Component
public class VpiMinaIoHandler extends IoHandlerAdapter{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Value("#{propertiesUtils.getInt('${handle_thread_count}',2)}")
	private int threadCount;
	
	private ExecutorService pool;
	
	@Autowired
	private IVpiHandler handler;
	
	private long t0;
	
	private volatile int workNum = 0;
	
	@PostConstruct
	public void init(){
		if(log.isDebugEnabled()){
			log.debug("VPI HANDLE THREAD COUNT = "+getThreadCount());
		}
		pool = Executors.newFixedThreadPool(getThreadCount());
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error("session("+session.getId()+") happen error:",cause);
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if(!(message instanceof VPI)){
			throw new IllegalArgumentException("receive message object is not VPI. maybe forget to add filter that parse byte[] to VPI object in mina filter chain.");
		}
		VPI vpi = (VPI)message;
		this.handleVpi(vpi);
	}
	
	
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.info("IDLE " + session.getIdleCount(status));
	}
	
	
	public void handleVpi(final VPI vpi) {
		if(handler == null){
			log.error("VPI HANDLER IS NOT NULL");
			return;
		}
		pool.execute(new Runnable(){

			@Override
			public void run() {
				startHandle();
				long start = System.currentTimeMillis();
				handler.handleVpi(vpi);
				completeHandle();
				if(System.currentTimeMillis() - start > 100){
					if(log.isDebugEnabled()){
						log.debug("VPI("+vpi.getSim()+") handle("+workNum+") cast " + (System.currentTimeMillis() - start) + " milliseconds");
					}
				}
			}
			
		});
	}
	
	private synchronized void startHandle(){
		workNum++;
	}
	
	private synchronized void completeHandle(){
		workNum--;
	}
	
	public int getThreadCount() {
		return threadCount;
	}
	
	public int getWorkNum() {
		return workNum;
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		log.info("session("+session.getId()+") closed!");
	}

	
}

