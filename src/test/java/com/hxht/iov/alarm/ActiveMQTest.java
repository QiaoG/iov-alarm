/** 
 * @author gq
 * @date 2015年12月22日 下午12:14:20 
 */
package com.hxht.iov.alarm;

import static org.junit.Assert.assertEquals;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;

import com.hxht.iov.alarm.mq.ActiveMQMessageListener;

@RunWith(AlarmJUnit4ClassRunner.class)
@ContextConfiguration(classes=AlarmRootConfig.class)
public class ActiveMQTest {
	
	@Autowired
	private ActiveMQMessageListener messageListener;
	
	@Test
	public void listenerMQ() {
		
		Destination d = new ActiveMQTopic("alarm.rules");
		JmsTemplate jm = messageListener.getActiveMQTemplate();
		jm.send(  
	            d,  
	            new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						String rules = "test qqqqq";
						return session.createTextMessage(rules);  
					}
				}
	        );  
		
	}

}

