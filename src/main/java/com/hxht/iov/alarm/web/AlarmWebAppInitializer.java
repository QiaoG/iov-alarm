/** 
 * @author gq
 * @date 2015年10月28日 下午3:32:41 
 */
package com.hxht.iov.alarm.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.hxht.iov.alarm.AlarmRootConfig;

/**
 * servlet容器必须3.0以上，比如tomcat7.0以上
 * 替代 web.xml
 *
 */
public class AlarmWebAppInitializer extends
		AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { AlarmRootConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { AlarmWebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		super.onStartup(servletContext);
		servletContext.addListener("org.springframework.web.util.Log4jConfigListener");
		servletContext.setInitParameter("log4jConfigLocation", "/WEB-INF/classes/config/log4j.properties");
	}

}

