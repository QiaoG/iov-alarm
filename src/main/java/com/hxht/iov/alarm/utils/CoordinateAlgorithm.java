/** 
 * @author gq
 * @date 2015年12月23日 下午12:45:01 
 */
package com.hxht.iov.alarm.utils;


import org.springframework.stereotype.Component;

import com.hxht.iov.alarm.domain.VPIPoint;

/**
 * 坐标换算算法工具类
 * @author gq
 *
 */
@Component
public class CoordinateAlgorithm {
	
	public final double EARTH_RADIUS = 6378137.0; //unit:meter
	
	/**
	 * 计算坐标点之间的距离
	 * @param lat1 from纬度
	 * @param lng1 from经度
	 * @param lat2 to纬度
	 * @param lng2 to经度
	 * @return unit:meter
	 */
	public double computeDistance(double lat1,double lng1,double lat2, double lng2){
		 double radLat1 = Math.toRadians(lat1);
	       double radLat2 = Math.toRadians(lat2);
		double dx = Math.toRadians(lng2) - Math.toRadians(lng1);
		double dy = radLat2 - radLat1;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(dy / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(dx / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
	
	/**
	 * The shortest distance between point and line.
	 * @param lat1 点的纬度
	 * @param lng1 点的经度
	 * @param lat2 直线端点1的纬度
	 * @param lng2 直线端点1的经度
	 * @param lat3 直线端点2的纬度
	 * @param lng3 直线端点2的经度
	 * @return 如果与端点夹角超过90度，则返回负值(与两个端点距离的最短值)
	 */
	public double computeDistance(double lat1, double lng1, double lat2,
			double lng2, double lat3, double lng3) {
		double len = 0;
		// 直线长度
		double len0 = computeDistance(lat2, lng2, lat3, lng3);
		// 分别计算点到直线端点的距离
		double len1 = computeDistance(lat1, lng1, lat2, lng2);
		double len2 = computeDistance(lat1, lng1, lat3, lng3);
		//两个夹角
		double angle1 = computeAngle(len0, len1, len2);
		double angle2 = computeAngle(len0, len2, len1);
		double angleMax = Math.max(angle1, angle2);
		if(angleMax > Math.PI / 2){//如果一个夹角超过90度，则取两条与端点连线中的短线,为了区别，使用负值
			len = -Math.min(len1, len2);
		}else{
			len = Math.sin(angle1)*len1;
			len = Math.round(len*10000)/10000;
		}
		return len;
	}
	
	
	/**
	 * 三角形，根据边长求夹角，
	 * len1和len2之间的夹角
	 * @param len1
	 * @param len2
	 * @param len3
	 * @return
	 */
	public double computeAngle(double len1,double len2,double len3){
		double angle = 0;
		angle = Math.acos((len1*len1+len2*len2-len3*len3)/(2*len1*len2));
		return angle;
	}
	
	/**
	 * 判断点位是否在多边形里面
	 * 算法：穿过测试点的横线与多边形相交，如果两边的交点数都是奇数则在里面，否则在外面
	 * @param p 计算点
	 * @param points 多边形端点数组
	 * @return
	 */
	public boolean inPolygon(VPIPoint p, VPIPoint[] points) {
		if(points == null || points.length < 3){
			throw new IllegalArgumentException("points number must be greater than 2!");
		}
		if(p == null)
			throw new IllegalArgumentException("VPIPoint can not be null!");
		
		double minLng=points[0].getLongitude(), minLat = points[0].getLatitude(),maxLng = 0, maxLat = 0;
		for(VPIPoint vp : points){
			if(vp.getLongitude() < minLng){
				minLng = vp.getLongitude();
			}
			if(vp.getLongitude() > maxLng){
				maxLng = vp.getLongitude();
			}
			if(vp.getLatitude() < minLat){
				minLat = vp.getLatitude();
			}
			if(vp.getLatitude() > maxLat){
				maxLat = vp.getLatitude();
			}
		}
		if(p.getLongitude() < minLng || p.getLongitude() > maxLng || p.getLatitude() < minLat || p.getLatitude() > maxLat){
			return false;
		}
		int i = 0, j = 0, c = points.length;
		boolean isOdd = false;//是否奇数
		for (i = 0, j = c - 1; i < c; j = i++) {
			if (points[i].getLatitude() > p.getLatitude() != points[j]
					.getLatitude() > p.getLatitude()) {// 有相交
				if (p.getLongitude() < (points[j].getLongitude() - points[i].getLongitude())
						* (p.getLatitude() - points[i].getLatitude())
						/ (points[j].getLatitude() - points[i].getLatitude())
						+ points[i].getLongitude())
					isOdd = !isOdd;
			}
		}
		return isOdd;
	}
	
}

