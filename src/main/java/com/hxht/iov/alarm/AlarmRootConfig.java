/** 
 * @author gq
 * @date 2015年10月26日 下午2:46:53 
 */
package com.hxht.iov.alarm;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;


import com.hxht.iov.alarm.tcp.VpiCodecFactory;
import com.hxht.iov.alarm.utils.PropertiesUtils;
import com.hxht.iov.alarm.vpi.VpiMinaIoHandler;

/**
 * for ContextLoaderListener's application context to load beans 装载非@EnableWebMvc注解的@Compoment类
 */
@Configuration
// @ComponentScan(excludeFilters={@Filter(type=FilterType.ANNOTATION,value=EnableWebMvc.class)})
@ComponentScan(excludeFilters = { @Filter(type = FilterType.REGEX, pattern = "com.hxht.iov.alarm.web.*") })
@ImportResource("classpath:config/applicationContext-activemq.xml")
@PropertySource({ "classpath:config/config.properties",
		"classpath:config/rabbitmq.properties" })
public class AlarmRootConfig {
	
	@Autowired
	Environment env;

	/**
	 * 属性占位符配置对象一定要加，这样不管在Java configuration还是再xml中都可以加载@PropertySource中配置的properties文件中的属性
	 * 而且xml中不需要再配置<bean id="propertyConfigurer"class=
	 * "org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
	 * 
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
		c.setIgnoreResourceNotFound(true);
		c.setIgnoreUnresolvablePlaceholders(true);
		return c;
	}

	@Bean
	public LoggingFilter loggingFilter() {
		return new LoggingFilter();
	}

	@Bean
	public DefaultIoFilterChainBuilder filterChainBuilder() {
		DefaultIoFilterChainBuilder builder = new DefaultIoFilterChainBuilder();
		Map<String, IoFilter> map = new LinkedHashMap<String, IoFilter>();
		ProtocolCodecFilter vpifilter = new ProtocolCodecFilter(
				new VpiCodecFactory(Charset.forName("UTF-8")));
		map.put("loggingFilter", loggingFilter());
		map.put("vpiCodecFilter", vpifilter);
		builder.setFilters(map);
		return builder;
	}

	@Bean//(initMethod = "bind")
	public NioSocketAcceptor tcpServer(VpiMinaIoHandler handler) {
		NioSocketAcceptor nio = new NioSocketAcceptor();
		nio.setHandler(handler);
		int port = Integer.parseInt(env.getProperty("tcp_server_port", "9122"));
		nio.setDefaultLocalAddress(new InetSocketAddress(port));
		nio.setFilterChainBuilder(filterChainBuilder());
		return nio;
	}

	@Bean
	public PropertiesUtils propertiesUtils() {
		PropertiesUtils utils = new PropertiesUtils();
		return utils;
	}
	

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate template = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
//        messageConverters.add(new FormHttpMessageConverter());
//        messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
//        messageConverters.add(new MappingJackson2HttpMessageConverter());
		template.setMessageConverters(messageConverters);
//		String api = env.getProperty("cms_get_all_rules_api", "/restful/alarm/rules/");
//		template.getForObject(getCmsUrl()+api, String.class);
		return template;
	}
}
