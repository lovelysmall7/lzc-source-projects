package com.lzc.demo.dao;

import com.lzc.demo.model.Component;

public class ComponentDao extends BaseDao<Component>{
	public ComponentDao(){
		setSqlNameSpace(Component.class.getName());
	}
}
