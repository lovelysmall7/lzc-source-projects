package com.lzc.demo.dao;

import com.lzc.demo.model.AreaInfoMapping;
import com.lzc.demo.util.SqlMapInitUtil;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
public class AreaInfoMappingDao extends BaseDao< AreaInfoMapping>{
	public AreaInfoMappingDao(){
		setSqlNameSpace(AreaInfoMapping.class.getName());
	}
	public List<HashMap<String,Object>> selectAllAreaStations(HashMap<String,Object> param){
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<HashMap<String,Object>> list = session.selectList(sqlNameSpace + "." + "selectAllAreaStations",param);
		session.close();
		return list;
	}
	public void deleteByAreaId(String areaId){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		session.delete(sqlNameSpace + "." + "deleteByAreaId",areaId);
		session.commit();
		session.close();
	}
	public List<HashMap<String,Object>> selectStationsByAreaId(HashMap<String,Object> param){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<HashMap<String,Object>> list = session.selectList(sqlNameSpace + "." + "selectStationsByAreaId",param);
		session.close();
		return list;
	}
}