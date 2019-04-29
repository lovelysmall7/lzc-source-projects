package com.lzc.demo.model;

import java.util.Date;

public class StationInfo {
	private String id;
	private String stationNo;
	private String StationName;
	private String rowNum;
	private String celNum;
	private String xAxis;
	private String yAxis;
	private Integer type;
	private String mappingValue;
	public StationInfo(){
		
	}
	public StationInfo(Integer x,Integer y){
		xAxis = x + "";
		yAxis = y  + "";
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	private Date createTime;
	
	public String getStationName() {
		return StationName;
	}
	public void setStationName(String stationName) {
		StationName = stationName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStationNo() {
		return stationNo;
	}
	public void setStationNo(String stationNo) {
		this.stationNo = stationNo;
	}
	public String getRowNum() {
		return rowNum;
	}
	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}
	public String getCelNum() {
		return celNum;
	}
	public void setCelNum(String celNum) {
		this.celNum = celNum;
	}
	public String getxAxis() {
		return xAxis;
	}
	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}
	public String getyAxis() {
		return yAxis;
	}
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}
	public String getMappingValue() {
		return mappingValue;
	}
	public void setMappingValue(String mappingValue) {
		this.mappingValue = mappingValue;
	}
	
}
