package com.lzc.demo.dao;
import org.apache.ibatis.session.SqlSession;
import com.lzc.demo.model.StationDistanceMapping;
import com.lzc.demo.util.SqlMapInitUtil;
public class StationDistanceMappingDao extends BaseDao<StationDistanceMapping>{
	public StationDistanceMappingDao(){
		setSqlNameSpace(StationDistanceMapping.class.getName());
	}
	public Integer findByTowPoint(StationDistanceMapping sdm){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		Integer distance = session.selectOne(sqlNameSpace + "." + "findByTowPoint",sdm);
		session.close();
		return distance;
		
	}
}
