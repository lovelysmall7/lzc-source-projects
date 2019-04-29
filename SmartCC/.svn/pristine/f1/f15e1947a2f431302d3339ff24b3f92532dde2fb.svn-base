package com.lzc.demo.dao;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;

import com.lzc.demo.model.StationInfo;
import com.lzc.demo.util.SqlMapInitUtil;

public class StationInfoDao extends BaseDao<StationInfo>{
	public StationInfoDao(){
		setSqlNameSpace(StationInfo.class.getName());
	}
	public int change(HashMap<String,Object> param ) {
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.insert(sqlNameSpace + "." + "insert", param);
		session.commit();
		session.close();
		return result;
	}
}
