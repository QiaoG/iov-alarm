/** 
 * @author gq
 * @date 2016年1月13日 上午9:29:52 
 */
package com.hxht.iov.alarm;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppMain {

	public static void main(String[] args) {
		String pre = "";
		if(args != null && args.length > 0){
			pre = args[0];
		}else{
			pre = "config/";
		}
		String log4jConfPath = pre+"log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		ApplicationContext context = new AnnotationConfigApplicationContext(
				AlarmRootConfig.class);
	}

}

