package com.lzc.demo.model;

import java.util.Date;
/**
 * 零件检查记录实体类
 * @author lizc
 * 2018-03-23
 *
 */
public class ComponentCheckInfo {
	private String id;
	private String taskNo;
	private String componentName;
	private String componentId;
	private String workerId;
	private String stationName;
	private Integer checkResult;
	private String comment;
	private Date createTime;
	private String category;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTaskNo() {
		return taskNo;
	}
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getComponentId() {
		return componentId;
	}
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}
	public String getWorkerId() {
		return workerId;
	}
	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
	public Integer getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(Integer checkResult) {
		this.checkResult = checkResult;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
}
