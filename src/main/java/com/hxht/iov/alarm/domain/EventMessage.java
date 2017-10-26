package com.hxht.iov.alarm.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class EventMessage {

	private String id;
	private String event;
	private String type;
	private String title;
	private List<String> content;
	private String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

	public static class Content {
		String vehicleId;
		Set<String> rules = new HashSet<String>();

		public String getVehicleId() {
			return vehicleId;
		}

		public void setVehicleId(String vehicleId) {
			this.vehicleId = vehicleId;
		}

		public Set<String> getRules() {
			return rules;
		}

		public void addRule(String rule) {
			this.rules.add(rule);
		}
	}

}
