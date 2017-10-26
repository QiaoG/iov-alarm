/** 
 * @author gq
 * @date 2015年12月22日 上午10:30:51 
 */
package com.hxht.iov.alarm.mq;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.hxht.iov.alarm.domain.EventMessage;
import com.hxht.iov.alarm.rule.AlarmRuleManager;

public class ActiveMQMessageListener implements MessageListener {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired
	private JmsTemplate activeMQTemplate;
	
	@Autowired
	private AlarmRuleManager ruleManager;
	
	private ObjectMapper mapper;
	
	@Value("${alarm_naming}")
	private String naming;//对应前置机的naming
	
	private Pattern PATTERN = Pattern.compile("<Device\\s+ID=\"([0-9]+)\"\\s*");
	
	@PostConstruct
	public void init(){
		mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	

	@Override
	public void onMessage(Message message) {
		//{"id":1,"event":"1", ”type”:”10”,”title”:”规则查询”, "content":{{["192.168.0.3:8081",….,…,]},
		//	"url":"http://ip:port/restful/alarm/rules/${vehicleId}?id=id1&id=id2&id=id3"}
		String msg = "";
		try {
			msg = ((ActiveMQTextMessage)message).getText();
		} catch (JMSException e) {
			log.error(e);
			return;
		}
		if(log.isInfoEnabled()){
			log.info("received from activeMQ:"+msg);
		}
		EventMessage event = resolveEvent(msg);
		if(event != null){
			this.handleEvent(event);
		}
	}
	
	public EventMessage resolveEvent(String msg){
		EventMessage event = null;
		try {
			event = mapper.readValue(msg, EventMessage.class);
		} catch (JsonParseException e) {
			log.error(e);
		} catch (JsonMappingException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
		return event;
	}
	
	private void handleEvent(EventMessage event){
		if(this.naming != null && event.getContent() != null && event.getContent().contains(this.naming)){
			ruleManager.refreshRules(null);
			response(event);
		}
	}

	private void response(final EventMessage event){
		getActiveMQTemplate().send(  
	            "alarm.rules.response",  
	            new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						String rules = "";
						for(String id : event.getContent()){
							rules += ":"+id;
						}
						return session.createTextMessage(rules.substring(1));  
					}
				}
	        );  
	}


	public JmsTemplate getActiveMQTemplate() {
		return activeMQTemplate;
	}

}

