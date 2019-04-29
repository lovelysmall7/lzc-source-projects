package com.lzc.demo.dao;
import com.lzc.demo.model.AreaInfo;
public class AreaInfoDao extends BaseDao<AreaInfo>{
	public AreaInfoDao(){
		setSqlNameSpace(AreaInfo.class.getName());
	}
}
