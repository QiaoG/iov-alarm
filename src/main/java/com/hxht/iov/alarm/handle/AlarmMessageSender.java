/** 
 * @author gq
 * @date 2015年11月12日 下午3:07:25 
 */
package com.hxht.iov.alarm.handle;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hxht.iov.alarm.domain.Alarm;

@Component
public class AlarmMessageSender {

	private Log log = LogFactory.getLog(this.getClass());

	// thread pool for send alarms
	private ExecutorService sendPool = Executors.newFixedThreadPool(1);
	
	@Value("#{propertiesUtils.getInt('${max_alarm_cache_num}',100)}")
	private int maxCacheNum;

	/**
	 * 缓存的待处理的告警队列 线程安全
	 */
	private ConcurrentLinkedQueue<Alarm> alarmsCache = new ConcurrentLinkedQueue<Alarm>();

	@Autowired
	private IAlarmClient client;

	@PostConstruct
	public void init() {
		sendCacheAlarms();
	}
	
	private void sendCacheAlarms(){
		sendPool.execute(new Runnable() {

			@Override
			public void run() {
				notifyAlarm();
			}
		});
	}
	
	public void notifyAlarm() {
		if(log.isInfoEnabled()){
			log.info("ALARM SEND: ready to send alarms ...");
		}
		for (;;) {
			Alarm alarm = alarmsCache.peek();
			boolean s = false;
			if(alarm != null){
				if (client.getState() == 0) {
					s = client.notifyAlarm(alarm);
					if(s)
						alarmsCache.poll();
					if(log.isDebugEnabled()){
						log.debug("ALARM SEND: notify send("+alarm.getSim()+":"+alarm.getRuleId()+") cache size = " + alarmsCache.size() +" ");
					}
				}else{
					if(log.isDebugEnabled()){
						log.debug("ALARM SEND: send("+alarm.getSim()+":"+alarm.getRuleId()+") wait for tcp connecte. cache_size=" + alarmsCache.size() +" ");
					}
					client.connect();
				}
			}
			if (!s) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("****** send thread sleep happen error:", e);
				}
			}
		}
	}
	

	public void addAlarms(Alarm[] ass) {
		synchronized (alarmsCache) {
			if(ass == null || ass.length == 0)
				return;
			for (Alarm alarm : ass)
				alarmsCache.add(alarm);
			int c = alarmsCache.size() - maxCacheNum;
			for(int i = 0; i < c; i++){
				alarmsCache.poll();
			}
			if(log.isDebugEnabled() && c > 0){
				log.debug("ALARM SEND: poll cache " + c);
			}
		}
		
	}
	
	public int getAlarmCacheCount(){
		return this.alarmsCache.size();
	}

}
