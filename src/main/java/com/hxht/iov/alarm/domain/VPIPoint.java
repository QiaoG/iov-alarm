/** 
 * @author gq
 * @date 2015年11月3日 上午10:32:31 
 */
package com.hxht.iov.alarm.domain;

public class VPIPoint {

	private double longitude;
	
	private double latitude;
	
	public VPIPoint(double lon,double lat){
		this.longitude = lon;
		this.latitude = lat;
	}

	/**
	 * 经度
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * 纬度
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public Object clone(){
		VPIPoint v = new VPIPoint(this.getLongitude(),this.getLatitude());
		return v;
	}
	
}

