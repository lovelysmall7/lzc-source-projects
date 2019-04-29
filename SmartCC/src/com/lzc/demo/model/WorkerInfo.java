package com.lzc.demo.model;

import java.util.Date;

public class WorkerInfo {
	private String id;
	private String name;
	private Date createTime;
	private Integer taskNum;
	private String officeId;
	private String currentStation;
	private String color;//颜色代码每一种颜色代表一个工人 用于在图表中显示
	private String areaId;//区域id
	private String groupId;//班组id
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getCurrentStation() {
		return currentStation;
	}
	public void setCurrentStation(String currentStation) {
		this.currentStation = currentStation;
	}
	public Integer getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(Integer taskNum) {
		this.taskNum = taskNum;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
}
