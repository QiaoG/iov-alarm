/** 
 * @author gq
 * @date 2015年11月3日 上午9:28:14 
 */
package com.hxht.iov.alarm;

import java.io.FileNotFoundException;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

public class AlarmJUnit4ClassRunner extends SpringJUnit4ClassRunner {
	
	static {  
        try {  
            Log4jConfigurer.initLogging("classpath:config/log4j.properties");  
        } catch (FileNotFoundException ex) {  
            System.err.println("Cannot Initialize log4j");  
        }  
    }  

	public AlarmJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

}

