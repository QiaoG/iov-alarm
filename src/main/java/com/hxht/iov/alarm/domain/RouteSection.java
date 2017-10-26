/** 
 * @author gq
 * @date 2015年11月3日 上午10:41:23 
 */
package com.hxht.iov.alarm.domain;

/**
 * 路段，多个路段组成路线Route
 *
 */
@Deprecated
public class RouteSection {
	
	private VPIPoint start;
	
	private VPIPoint end;
	
	public RouteSection(VPIPoint s,VPIPoint e){
		this.start = s;
		this.end = e;
	}

	public VPIPoint getStart() {
		return start;
	}

	public void setStart(VPIPoint start) {
		this.start = start;
	}

	public VPIPoint getEnd() {
		return end;
	}

	public void setEnd(VPIPoint end) {
		this.end = end;
	}

}

