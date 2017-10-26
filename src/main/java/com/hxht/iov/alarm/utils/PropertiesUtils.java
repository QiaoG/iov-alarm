/** 
 * @author gq
 * @date 2015年11月4日 上午11:20:06 
 */
package com.hxht.iov.alarm.utils;

public class PropertiesUtils {
	
	public int getInt(String v,int def){
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException e) {
			return def;
		}
	}
	
	public boolean getBoolean(String v){
		return "true".equals(v);
	}
	
	public String getPropertiesWithDefault(String v,String def){
		if(v == null || v.trim().length()==0){
			return def;
		}
		return v;
	}
	
}

