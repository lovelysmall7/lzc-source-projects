package com.lzc.demo.model;

import java.util.Date;
/**
 * 班组信息实体类
 * 2018-5-2
 * @author lizc
 *
 */
public class GroupInfo {
	private String id;
	private String groupName;
	private Date createTime;
	private String createTimeStr;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
