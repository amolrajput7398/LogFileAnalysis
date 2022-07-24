package com.cs.dto;
/**
 * 
 */

/**
 * @author DELL
 *
 */
public class LogEventsDO {

	private String id;
	private Integer duration;
	private String type;
	private String host;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String toString() {
		return "LogEventsDO [id=" + id + "]";
	}

}
