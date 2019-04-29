package com.lzc.demo.dao;
import org.apache.ibatis.session.SqlSession;

import com.lzc.demo.model.TaskWalkLength;
import com.lzc.demo.util.SqlMapInitUtil;
/**
 * 工人任务距离信息类dao
 * @date 2018-3-22
 * @author lizc
 *
 */
public class TaskWalkLengthDao extends BaseDao<TaskWalkLength>{
	public TaskWalkLengthDao(){
		setSqlNameSpace(TaskWalkLength.class.getName());
	}
	public boolean deleteCurrentData(){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.delete(sqlNameSpace + "." + "deleteCurrentData");
		session.commit();
		session.close();
		return (result > 0);
	}
	public boolean deleteCurrentDistanseByPerson(String workerId){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.delete(sqlNameSpace + "." + "deleteCurrentDistanseByPerson",workerId);
		session.commit();
		session.close();
		return (result > 0);
	}
}
