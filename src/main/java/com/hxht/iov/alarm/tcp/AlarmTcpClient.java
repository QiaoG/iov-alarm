/** 
 * @author gq
 * @date 2015年11月4日 上午10:31:32 
 */
package com.hxht.iov.alarm.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hxht.iov.alarm.domain.Alarm;
import com.hxht.iov.alarm.handle.IAlarmClient;

@Component
public class AlarmTcpClient extends IoHandlerAdapter implements IAlarmClient{

	private Log log = LogFactory.getLog(this.getClass());

	@Value("${alarm_tcp_accept_host}")
	private String host;

	@Value("#{propertiesUtils.getInt('${alarm_tcp_accept_port}',9122)}")
	private int port;

//	@Value("#{propertiesUtils.getInt('${alarm_tcp_connect_timeout}',60)}")
//	private int timeout;

	private IoConnector connector;

	private IoSession session;
	
	private ExecutorService connectPool = Executors.newFixedThreadPool(1);//thread pool for connect 
	
	/**
	 * 0:ready
	 * 97:request error
	 * 98:caught error
	 * 99:connect error
	 * 1:close
	 * 2:sleep
	 */
	private volatile int state = -1;
	
	private volatile boolean connecting = false;

	@PostConstruct
	public void init() {
		connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new AlarmCodecFactory()));
		connector.setHandler(this);
		
		
	}

	@Override
	public void connect() {
		if(isConnecting()){
			return;
		}
		connectPool.execute(new Runnable() {

			@Override
			public void run() {
				doConnect();
			}
		});
	}
	
	/**
	 * 由于连接可能需要较长时间，建议放在单独的线程中执行
	 */
	public void doConnect() {
		
		setConnecting(true);
		for (;;) {
			if (log.isInfoEnabled())
				log.info("TCP SEND: will connecting (" + host + ":" + port + ") ...");
			try {
				ConnectFuture connFuture = connector
						.connect(new InetSocketAddress(host, port));
				connFuture.awaitUninterruptibly();
				setSession(connFuture.getSession());
				setState(0);
				
				setConnecting(false);
				break;
			} catch (Exception e) {
				setState(99);
				log.error("TCP SEND: failed to connect(" + host + ":" + port
						+ ") alarm storage server.");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					log.error("TCP SEND: Failed to thread sleep!");
				}
			}
			
		}
	}
	
	@Override
	public boolean notifyAlarm(Alarm alarm){
		if(getState()==0){
			getSession().write(alarm);
			return true;
		}
		return false;
	}

	@Override
	public void messageReceived(IoSession iosession, Object message)
			throws Exception {
		IoBuffer bbuf = (IoBuffer) message;
		byte[] byten = new byte[bbuf.limit()];
		bbuf.get(byten, bbuf.position(), bbuf.limit());
		log.info("TCP SEND: accept message:" + new String(byten));
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error("TCP SEND: TCP client happen error: "+session.getId()+" "+cause.getMessage());
//		super.exceptionCaught(session, cause);
		setState(98);
	}

	@Override
	public void messageSent(IoSession iosession, Object obj) throws Exception {
		if(log.isDebugEnabled())
			log.debug("TCP SEND:send message complement:"+iosession.getId()+" "+obj);
		super.messageSent(iosession, obj);
	}

	@Override
	public void sessionClosed(IoSession iosession) throws Exception {
		log.info("TCP SEND:TCP session closed!");
		super.sessionClosed(iosession);
		setState(1);
	}

	@Override
	public void sessionCreated(IoSession iosession) throws Exception {
		log.info("TCP SEND:TCP session("+iosession.getId()+") created!");
		super.sessionCreated(iosession);
	}

	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus)
			throws Exception {
		log.info("TCP SEND:TCP session idle(sleep)!");
		super.sessionIdle(iosession, idlestatus);
		setState(2);
	}

	@Override
	public void sessionOpened(IoSession iosession) throws Exception {
		log.info("TCP SEND:TCP session("+iosession.getId()+") opened!");
		super.sessionOpened(iosession);
	}

	@Override
	public int getState() {
		return state;
	}

	private void setState(int state) {
		this.state = state;
	}

	/**
	 * 后台线程正在连接
	 * @return
	 */
	public synchronized boolean isConnecting() {
		return connecting;
	}

	private synchronized void setConnecting(boolean connecting) {
		this.connecting = connecting;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

}
