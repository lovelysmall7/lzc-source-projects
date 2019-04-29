package com.lzc.demo.model;

import java.util.List;
/**
 * 分页工具类
 * @author lizc
 * 2018-4-04
 * @param <T>
 */
public class PageUtil<T> {
    private Integer currentPage = 0;//当前页数
    private Integer pageSize = 0;//每页总数
    private Integer totalRecord = 0;//总记录数
    private Integer startCount = 0;//数据库第几个开始查
    private List<T> resultList = null;//返回结果集
    public PageUtil(Integer currentPage,Integer pageSize){
    	this.currentPage =  currentPage;
    	this.pageSize = pageSize;
    	if(currentPage != null && pageSize != null){
    		startCount = (currentPage - 1) * pageSize;
    	}
    }
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(Integer totalRecord) {
		this.totalRecord = totalRecord;
	}
	public Integer getStartCount() {
		return startCount;
	}
	public void setStartCount(Integer startCount) {
		this.startCount = startCount;
	}
	public List<T> getResultList() {
		return resultList;
	}
	public void setResultList(List<T> resultList) {
		this.resultList = resultList;
	}
    
}