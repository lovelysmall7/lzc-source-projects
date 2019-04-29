package com.lzc.demo.dao;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import com.lzc.demo.model.ComponentStationMapping;
import com.lzc.demo.util.SqlMapInitUtil;
public class ComponentStationMappingDao extends BaseDao<ComponentStationMapping>{
	public ComponentStationMappingDao(){
		setSqlNameSpace(ComponentStationMapping.class.getName());
	}
	public List<String> findStationByComponent(String componentName){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<String> stationList = session.selectList(sqlNameSpace + "." + "findStationByComponent",componentName);
		session.close();
		return stationList;
	}
}
