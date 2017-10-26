/** 
 * @author gq
 * @date 2015年10月27日 下午3:58:31 
 */
package com.hxht.iov.alarm.vpi;

import com.hxht.iov.alarm.domain.VPI;

public interface IVpiHandler {

	public static int READY = 0;

	public static int WORK = 1;
	
	public int getState();
	
	public void handleVpi(VPI vpi);
	

}
