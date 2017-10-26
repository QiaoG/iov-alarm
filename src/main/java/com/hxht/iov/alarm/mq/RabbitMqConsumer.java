/** 
 * 用于从rabbitmq获取消息
 * @author gq
 * @date 2015年10月27日 上午9:10:11 
 */
package com.hxht.iov.alarm.mq;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.hxht.iov.alarm.rule.AlarmRuleManager;
import com.hxht.iov.alarm.rule.IRuleListener;
import com.hxht.iov.alarm.rule.RuleEvent;

public class RabbitMqConsumer implements MessageListener,IRuleListener{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
	private AlarmRuleManager ruleManager;
	
	@PostConstruct
	public void init(){
		this.ruleManager.addListener(this);
	}

	public void onMessage(Message arg0) {
//		String message = new String(arg0.getBody());
//		log.info(message);
	}

	@Override
	public void onReloadRules(RuleEvent event) {
		
	}

}

