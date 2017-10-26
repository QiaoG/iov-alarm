/** 
 * @author gq
 * @date 2015年10月26日 下午3:52:10 
 */
package com.hxht.iov.alarm;

import static org.junit.Assert.*;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.hxht.iov.alarm.domain.Alarm;
import com.hxht.iov.alarm.domain.VPI;
import com.hxht.iov.alarm.tcp.AlarmTcpClient;
import com.hxht.iov.alarm.vpi.VpiMinaIoHandler;
import com.hxht.iov.alarm.vpi.VpiTranslator;

@RunWith(AlarmJUnit4ClassRunner.class)
@ContextConfiguration(classes=AlarmRootConfig.class)
public class MinaTcpServerTest {
	
//	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private NioSocketAcceptor ioAcceptor;
	
	@Autowired
	private AlarmTcpClient tcpClient;
	
//	@Test
	public void tcpServerState() {
		assertEquals(ioAcceptor.isActive(), true);
		while(true){
			
		}
	}
	
//	@Test
	public void testTcpClient(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		Alarm a = new Alarm();
//		a.setId("23456");
		a.setName("test");
		a.setValue(6);
//		assertEquals(tcpClient.getState(), 99);
		tcpClient.notifyAlarm(a);
		while(true){
			
		}
	}
	
	@Test
	public void assertProperties(){
		//assertNotNull(((MinaIoHandler)ioAcceptor.getHandler()).getGisHanlder());
		assertEquals(((VpiMinaIoHandler)ioAcceptor.getHandler()).getThreadCount(), 10);
	}
	
	//@Test
	public void analyzeIO() throws Exception{
//		String gis = "{\"msg\":512,\"seq\":1,\"sim\":13100000000,\"alarm\":11111,\"state\":1234,\"lon\":11111,\"lat\":23333"
//				+ ",\"hig\":512,\"speed\":100,\"direct\":100,\"time\":\"15-10-27-14-41-54\""
//				+ ",\"att\":[{\"key\":1,\"val\":[100]},{\"key\":2,\"val\":[100]},{\"key\":3,\"val\":[100]},{\"key\":17,\"val\":[1,1234]},{\"key\":18,\"val\":[1,1234,0]},{\"key\":19,\"val\":[1234,10,0]}]"
//				+ ",\"ver\":\"1.0.0\"}";
		String gis = "{\"msg\":512,\"seq\":20,\"sim\":15833333333,\"alarm\":0,\"state\":19,\"lon\":116375198,\"lat\":39974960,\"hig\":500,\"speed\":610,\"direct\":12,\"time\":\"16-01-15-13-30-30\",\"att\":[{\"key\":1,\"val\":[51200]},{\"key\":2,\"val\":[199]},{\"key\":3,\"val\":[701]}],\"ver\":\"1.0.0\"}";
		VpiTranslator vt = new VpiTranslator();
		VPI vpi = (VPI) vt.deCode(gis);
		assertNotNull(vpi);
	}

}

