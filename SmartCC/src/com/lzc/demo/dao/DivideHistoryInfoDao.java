package com.lzc.demo.dao;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.util.SqlMapInitUtil;
public class DivideHistoryInfoDao extends BaseDao<DivideHistoryInfo>{
	public DivideHistoryInfoDao(){
		setSqlNameSpace(DivideHistoryInfo.class.getName());
	}
	public boolean deleteByTaskId(String taskId){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.delete(sqlNameSpace + "." + "deleteByTaskId", taskId);
		session.commit();
		session.close();
		return (result > 0);
	}
	/**
	 * 查多个员工的任务分配信息
	 * @param workerIds
	 * @return
	 */
	public List<DivideHistoryInfo> findByWorkerIds(String workerIds){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<DivideHistoryInfo> list = session.selectList(sqlNameSpace + "." + "findByWorkIds",workerIds);
		session.close();
		return list;
	}
	public boolean deleteCurrentData(){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.delete(sqlNameSpace + "." + "deleteCurrentData");
		session.commit();
		session.close();
		return (result > 0);
	}
	public boolean resetCurrentBatchData(String workerId){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.update(sqlNameSpace + "." + "resetCurrentBatchData", workerId);
		session.commit();
		session.close();
		return (result > 0);
	}
	public List<DivideHistoryInfo> findUnfinishOnCurrentDay(){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<DivideHistoryInfo> list = session.selectList(sqlNameSpace + "." + "findUnfinishOnCurrentDay");
		session.close();
		return list;
	}
	public List<DivideHistoryInfo> findCurrentBatchData(HashMap<String,Object> param){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<DivideHistoryInfo> list = session.selectList(sqlNameSpace + "." + "findCurrentBatchData",param);
		session.close();
		return list;
	}
	public List<HashMap<String,Object>> downDivideHistory(HashMap<String,Object> param){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<HashMap<String,Object>> list = session.selectList(sqlNameSpace + "." + "downDivideHistory",param);
		session.close();
		return list;
	}
	public List<HashMap<String,Object>> findCurrentBatchMap(HashMap<String,Object> param){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<HashMap<String,Object>> list = session.selectList(sqlNameSpace + "." + "findCurrentBatchMap",param);
		session.close();
		return list;
	}
	public DivideHistoryInfo selectLastComponent(String workerId){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		DivideHistoryInfo dInfo = session.selectOne(sqlNameSpace + "." + "selectLastComponent",workerId);
		session.close();
		return dInfo;
	}
	public DivideHistoryInfo selectLastComponentCurrentBatch(String workerId){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		DivideHistoryInfo dInfo = session.selectOne(sqlNameSpace + "." + "selectLastComponentCurrentBatch",workerId);
		session.close();
		return dInfo;
	}
	public DivideHistoryInfo selectFinishedLastComponent(String workerId){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		DivideHistoryInfo dInfo = session.selectOne(sqlNameSpace + "." + "selectFinishedLastComponent",workerId);
		session.close();
		return dInfo;
	}
	public String findMaxBatch(){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		String maxBatch = session.selectOne(sqlNameSpace + "." + "findMaxBatch");
		session.close();
		return maxBatch;
	}
	public List<DivideHistoryInfo> selectDivideHistory(HashMap<String,Object> param){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<DivideHistoryInfo> list = session.selectList(sqlNameSpace + "." + "selectDivideHistory",param);
		session.close();
		return list;
	}
	public List<String> selectCurrentOptimezeResult(){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<String> list = session.selectList(sqlNameSpace + "." + "selectCurrentOptimezeResult");
		session.close();
		return list;
	}
}
