package com.lzc.demo.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.lzc.demo.model.WorkerInfo;
import com.lzc.demo.util.SqlMapInitUtil;
public class WorkerInfoDao extends BaseDao<WorkerInfo>{
	public WorkerInfoDao(){
		setSqlNameSpace(WorkerInfo.class.getName());
	}
	public void initAllWorkerStation(){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		session.update(sqlNameSpace + "." + "initAllWorkerStation");
		session.commit();
		session.close();
	}
	public List<WorkerInfo> selectCurrentDividedPerson(){
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<WorkerInfo> list = session.selectList(sqlNameSpace + "." + "selectCurrentDividedPerson");
		session.close();
		return list;
		
	}
	public List<String> findOtherWorkersOnSameGroup(String workerId){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<String> list = session.selectList(sqlNameSpace + "." + "findOtherWorkersOnSameGroup",workerId);
		session.close();
		return list;
	}
	public List<HashMap<String,Object>> selectWorkerFullInfo(HashMap<String,Object> param){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<HashMap<String,Object>> list = session.selectList(sqlNameSpace + "." + "selectWorkerFullInfo",param);
		session.close();
		return list;
	}
}
