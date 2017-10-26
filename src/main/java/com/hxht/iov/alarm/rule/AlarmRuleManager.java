/** 
 * @author gq
 * @date 2015年10月27日 上午11:12:22 
 */
package com.hxht.iov.alarm.rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.hxht.iov.alarm.domain.AlarmBean;
import com.hxht.iov.alarm.domain.AlarmBean.Rule;
import com.hxht.iov.alarm.domain.AlarmRule;
import com.hxht.iov.alarm.domain.AreaAlarmRule;
import com.hxht.iov.alarm.domain.RestAlarmRules;
import com.hxht.iov.alarm.domain.RouteAlarmRule;
import com.hxht.iov.alarm.domain.VPI;
import com.hxht.iov.alarm.utils.CoordinateAlgorithm;

@Component
public class AlarmRuleManager implements IRuleManage{
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private Map<String, AlarmRule> rulesCache = new ConcurrentHashMap<String, AlarmRule>();
	
	private Map<String, Set<String>> rulesIdInSimMap = new ConcurrentHashMap<String,Set<String>>();
	
	private List<IRuleListener> listeners = new ArrayList<IRuleListener>();
	
	private ExecutorService getPool = Executors.newFixedThreadPool(1);
	
	public final String SUCCESS_CODE = "000000";
	
	@Autowired
	private CoordinateAlgorithm alg;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${cms_ip}")
	private String cmsIp;
	
	@Value("#{propertiesUtils.getInt('${cms_port}',8080)}")
	private int cmsPort;
	
	@Value("${cms_all_rules_api}")
	private String allRulesApi;
	
	@Value("${cms_find_rules_api}")
	private String findRulesApi;
	
	private String allRulesUrl;
	
	private String findRulesUrl;
	
	@Value("${alarm_naming}")
	private String naming;
	
	private ObjectMapper mapper;
	
	private volatile boolean requesting;
	
	@PostConstruct
	public void init(){
		allRulesUrl =  "http://"+cmsIp+":"+cmsPort+allRulesApi;
		findRulesUrl = "http://"+cmsIp+":"+cmsPort+findRulesApi;
		mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		refreshRules(null);
	}
	
	/**
	 * 从cms获取规则
	 * @param ruleIds 如果为空则获取所有规则，否则为指定id的规则列表
	 */
	public void refreshRules(String[] ruleIds){
		if(requesting){
			log.info("requesting rules from cms ...");
			return;
		}
		String url = "";
		if(ruleIds == null || ruleIds.length==0){
			url = allRulesUrl+naming;
		}else{
			url = findRulesUrl+"?";
			for(int i = 0; i < ruleIds.length; i++){
				url += (i==0?"":"&")+"id="+ruleIds[i];
			}
		}
		final String fUrl = url;
		getPool.execute(new Runnable() {
			
			@Override
			public void run() {
				requesting = true;
				String result = findRulesFromCMS(fUrl);
				RestAlarmRules rules = parseToObject(result);
				if(rules != null){
					try {
						handleResultAll(rules);
					} catch (Exception e) {
						log.error(e);
					}
				}
				requesting = false;
			}
		});
		
	}
	
	public String findRulesFromCMS(String url){
		if(log.isInfoEnabled()){
			log.info("RULES: GET "+url);
		}
		String rules = "";
		try {
			rules = restTemplate.getForObject(url, String.class);
			if(log.isDebugEnabled()){
				log.debug("RESPONSE:"+rules);
			}
		} catch (RestClientException e) {
			log.error(e.getMessage()+":"+url);
		}
		
		return rules;
	}
	
	
	public RestAlarmRules parseToObject(String result){
		if(result == null || result.trim().length()==0 )
			return null;
		RestAlarmRules rules = null;
		try {
			rules = mapper.readValue(result, RestAlarmRules.class);
		} catch (JsonParseException e) {
			log.error("1:",e);
		} catch (JsonMappingException e) {
			log.error("2:",e);
		} catch (IOException e) {
			log.error("3:",e);
		}
		return rules;
	}
	
	public void handleResultAll(RestAlarmRules result){
		if(SUCCESS_CODE.equals(result.getCode())){
			if(result.getAlarmBean() == null){
				log.error("RULES: alarmBean is null!");
				return;
			}
			readyRefresh();
			int suleCount = analyseResult(result.getAlarmBean());
			completeRefresh();
			if(log.isInfoEnabled()){
				log.info("RULES:refresh all rules("+naming+") complement, sim count = "+ rulesIdInSimMap.size() + " rules count = "+suleCount);
				log.info("RULES: "+logOfRules());
				log.info("RULES MAP SIM: "+logOfRulesInSim());
			}
		}else{
			log.error("RULES:refresh all rules("+naming+") failed:"+(result.getDesc()==null?"":result.getDesc()));
		}
	}
	
	
	private int analyseResult(AlarmBean result) {
		int count = 0;
		List<Rule> rules = result.getRuleList();
		if(rules == null || rules.size() == 0){
			return count;
		}
		AlarmRule ar = null;
		for(Rule rule : rules){
			ar = this.rulesCache.get(rule.getId());
			if(ar != null){
				ar.setDelete(false);
				ar.setRule(rule);
			}else{
				ar = createAlarmRule(rule);
				if(ar != null){
					rulesCache.put(rule.getId(), ar);
				}
			}
			if(ar != null){
				ar.getPoints();
				handleSimsInRule(rule);
				count++;
			}
		}
		return count;
	}
	
	private void handleSimsInRule(Rule rule){
		Set<String> sims = rule.getBindingSims();
		if(sims == null || sims.size() == 0){
			if(log.isInfoEnabled())
				log.info("RULES:binded sims is empty, rule is = "+rule.getId());
			return;
		}
		Set<String> rules = null;
		for(String sim : sims){
			rules = this.rulesIdInSimMap.get(sim);
			if(rules == null){
				rules = new TreeSet<>();
				rulesIdInSimMap.put(sim, rules);
			}
			rules.add(rule.getId());
		}
	}
	
	private void readyRefresh(){
		rulesIdInSimMap.clear();
		for(AlarmRule ar : rulesCache.values()){
			ar.setDelete(true);
		}
	}
	
	private void completeRefresh(){
		List<String> dels = new ArrayList<>();
		for(AlarmRule ar : rulesCache.values()){
			if(!ar.isDelete()){
				ar.clearSimsStateMap();
				ar.addSimsStateFromBindedSims();
			}else{
				dels.add(ar.getRule().getId());
			}
		}
		for(String id : dels){
			rulesCache.remove(id);
		}
	}
	
	private AlarmRule createAlarmRule(Rule r){
		AlarmRule ar = null;
		if(AlarmRule.RULE_OF_FENCE.equals(r.getRuleType())){
			ar = new AreaAlarmRule(r);
		}
		if(AlarmRule.RULE_OF_LINE.equals(r.getRuleType())){
			ar = new RouteAlarmRule(r);
		}
		if(ar != null){
			ar.setAlg(this.alg);
		}
		return ar;
	}
	
	@Override
	public List<AlarmRule> findRoles(Object gi) {
		VPI gio = (VPI)gi;
		
		Set<String> ids = this.rulesIdInSimMap.get(gio.getSim().toString());
		if(ids == null){
			return new ArrayList<AlarmRule>();
		}
		List<AlarmRule> rules = new ArrayList<AlarmRule>();
		AlarmRule rule = null;
		for(String id: ids){
			rule = this.rulesCache.get(id);
			if(rule == null){
				log.error("RULES:not find rule, rule id = "+id+", sim = "+gio.getSim());
			}else{
				rules.add(rule);
			}
		}
		return rules;
	}
	
	private String logOfRulesInSim(){
		String log = "";
		for(String sim : rulesIdInSimMap.keySet()){
			log += "{"+sim + ":"+"[";
			for(String r : rulesIdInSimMap.get(sim)){
				log += r + " ";
			}
			log += "]}";
		}
		return log;
	}
	
	private String logOfRules(){
		String log = "";
		log += "[";
		for(String id : rulesCache.keySet()){
			log += id +" ";
		}
		log += "]";
		return log;
	}
	
//	/**
//	 * 刷新规则后需要通知cms
//	 * @param event
//	 */
//	private void fireRuleEvent(RuleEvent event){
//		for(IRuleListener lis : this.listeners){
//			lis.onReloadRules(event);
//		}
//	}
	
	public synchronized void addListener(IRuleListener lis){
		listeners.add(lis);
	}
	
	@PreDestroy
	public void despose(){
		this.listeners.clear();
	}

	/**
	 * 规则缓存
	 * key:规则id
	 * value:规则实例
	 * @return
	 */
	public Map<String, AlarmRule> getRulesCache() {
		return rulesCache;
	}

	/**
	 * 车辆 -规则映射表
	 * key:手机号
	 * value:规则id列表
	 * @return
	 */
	public Map<String, Set<String>> getRulesIdInSimMap() {
		return rulesIdInSimMap;
	}


}

