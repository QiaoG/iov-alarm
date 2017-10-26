package com.hxht.iov.alarm.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


@SuppressWarnings("serial")
//@JsonAutoDetect
public class AlarmBean implements Serializable {

	private Map<String, List<Rule>> rules = new HashMap<String, List<Rule>>();
	
	private List<Rule> ruleList = new ArrayList<>();
	
	public Iterator<String> getVehicles() {
		return rules.keySet().iterator();
	}

	public List<Rule> getVechicleRules(String sim) {
		return rules.get(sim);
	}

//	public void addRule(String sim, Rule rule) {
//		List<Rule> rs = getVechicleRules(sim);
//		if (rs == null) {
//			rs = new LinkedList<AlarmBean.Rule>();
//			rules.put(sim, rs);
//		}
//		rs.add(rule);
//	}
//
//	public void clear(String sim) {
//		List<Rule> rs = getVechicleRules(sim);
//		if (rs != null) {
//			rs.clear();
//		}
//	}
//
//	public Map<String, List<Rule>> getRules() {
//		return rules;
//	}

	public static class Rule {

		private String id;

		/**
		 * 规则类型
		 */
		private String ruleType;
		/**
		 * 规则子类型
		 */
		private String subRuleType;

		/**
		 * 有效开始日期
		 */
		private String validBeginDate;// yyyy-MM-dd
		/**
		 * 有效结束日期
		 */
		private String validEndDate;// yyyy-MM-dd
		
		/**
		 * 有效开始时间
		 */
		
		private String validBeginTime;// HH:mm:ss
		/**
		 * 有效结束时间
		 */
		private String validEndTime;// HH:mm:ss
		/**
		 * 
		 */
		private int opStatus;

		/**
		 * 参数
		 */
		private Map<String, String> params = new HashMap<String, String>();

		private Set<Section> sections = new HashSet<>();
		
		private Set<String> bindingSims = new TreeSet<>();

		public Rule() {
			super();
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return
		 * if ruleType='FENCE' 围栏,
		 * NULL,
		 * CIRCLE,
		 * RECTANGLE 矩形,
		 * POLYGON 多边形,
		 */
		public String getSubRuleType() {
			return subRuleType;
		}

		public void setSubRuleType(String subRuleType) {
			this.subRuleType = subRuleType;
		}

		public int getOpStatus() {
			return opStatus;
		}

		public void setOpStatus(int opStatus) {
			this.opStatus = opStatus;
		}

		/**
		 * @return
		 * FENCE 围栏 ,
		 * LINE 路线
		 */
		public String getRuleType() {
			return ruleType;
		}

		public void setRuleType(String ruleType) {
			this.ruleType = ruleType;
		}

		public String getValidBeginDate() {
			return validBeginDate;
		}

		public void setValidBeginDate(String validBeginDate) {
			this.validBeginDate = validBeginDate;
		}

		public String getValidEndDate() {
			return validEndDate;
		}

		public void setValidEndDate(String validEndDate) {
			this.validEndDate = validEndDate;
		}

		public String getValidBeginTime() {
			return validBeginTime;
		}

		public void setValidBeginTime(String validBeginTime) {
			this.validBeginTime = validBeginTime;
		}

		public String getValidEndTime() {
			return validEndTime;
		}

		public void setValidEndTime(String validEndTime) {
			this.validEndTime = validEndTime;
		}

		public Map<String, String> getParams() {
			return params;
		}

		public void addParam(String key, String value) {
			params.put(key, value);
		}

		public Set<Section> getSection() {
			return sections;
		}

		public void addSection(Section section) {
			sections.add(section);
		}

		public Set<String> getBindingSims() {
			return bindingSims;
		}

		public static class Section {
			String id;
			String pointId;
			String gpsX0;
			String gpsY0;

			String gpsX1;
			String gpsY1;

			String pointX0;
			String pointY0;

			double width;
			double length;
			int maxSpeed;
			int maxDrivingTime;
			int minDrivingTime;
			int overSpeedKeepTime;

			public Section() {
				super();
			}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getPointId() {
				return pointId;
			}

			public void setPointId(String pointId) {
				this.pointId = pointId;
			}

			public String getPointX0() {
				return pointX0;
			}

			public void setPointX0(String pointX0) {
				this.pointX0 = pointX0;
			}

			public String getPointY0() {
				return pointY0;
			}

			public void setPointY0(String pointY0) {
				this.pointY0 = pointY0;
			}

			public String getGpsX0() {
				return gpsX0;
			}

			public void setGpsX0(String gpsX0) {
				this.gpsX0 = gpsX0;
			}

			public String getGpsY0() {
				return gpsY0;
			}

			public void setGpsY0(String gpsY0) {
				this.gpsY0 = gpsY0;
			}

			public String getGpsX1() {
				return gpsX1;
			}

			public void setGpsX1(String gpsX1) {
				this.gpsX1 = gpsX1;
			}

			public String getGpsY1() {
				return gpsY1;
			}

			public void setGpsY1(String gpsY1) {
				this.gpsY1 = gpsY1;
			}

			public double getWidth() {
				return width;
			}

			public void setWidth(double width) {
				this.width = width;
			}

			public double getLength() {
				return length;
			}

			public void setLength(double length) {
				this.length = length;
			}

			public int getMaxSpeed() {
				return maxSpeed;
			}

			public void setMaxSpeed(int maxSpeed) {
				this.maxSpeed = maxSpeed;
			}

			public int getMaxDrivingTime() {
				return maxDrivingTime;
			}

			public void setMaxDrivingTime(int maxDrivingTime) {
				this.maxDrivingTime = maxDrivingTime;
			}

			public int getMinDrivingTime() {
				return minDrivingTime;
			}

			public void setMinDrivingTime(int minDrivingTime) {
				this.minDrivingTime = minDrivingTime;
			}

			public int getOverSpeedKeepTime() {
				return overSpeedKeepTime;
			}

			public void setOverSpeedKeepTime(int overSpeedKeepTime) {
				this.overSpeedKeepTime = overSpeedKeepTime;
			}

		}
	}


	public List<Rule> getRuleList() {
		return ruleList;
	}


	public void setRuleList(List<Rule> ruleList) {
		this.ruleList = ruleList;
	}
}
