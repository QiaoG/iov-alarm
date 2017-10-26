/** 
 * @author gq
 * @date 2015年10月27日 上午9:33:17 
 */
package com.hxht.iov.alarm;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hxht.iov.alarm.mq.RabbitMqConsumer;

@RunWith(AlarmJUnit4ClassRunner.class)
@ContextConfiguration(classes=AlarmRootConfig.class)
public class RabbitMQConsumerTest {
	
	@Autowired
	private RabbitMqConsumer mqConsumer;
	
	@Test
	public void mqConsumerNotBeNull(){
		assertNotNull(mqConsumer);
	}
}

