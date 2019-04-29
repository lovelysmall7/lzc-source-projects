package com.lzc.demo.model;

import java.util.Date;

public class StationDistanceMapping {
	private String id;
	private String stationA;
	private String stationB;
	private Integer distance;
	private Date createTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStationA() {
		return stationA;
	}
	public void setStationA(String stationA) {
		this.stationA = stationA;
	}
	public String getStationB() {
		return stationB;
	}
	public void setStationB(String stationB) {
		this.stationB = stationB;
	}
	public Integer getDistance() {
		return distance;
	}
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
