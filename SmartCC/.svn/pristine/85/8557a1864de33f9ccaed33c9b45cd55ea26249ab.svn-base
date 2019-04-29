package com.lzc.demo.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.lzc.demo.model.PageUtil;
import com.lzc.demo.util.SqlMapInitUtil;

public class BaseDao<T> implements baseDaoInter<T>{
	protected String sqlNameSpace;
	/**
	 * 子类调用这个方法把命名空间传入
	 * @param sqlNameSpace
	 */
	protected void setSqlNameSpace(String sqlNameSpace){
		this.sqlNameSpace = sqlNameSpace;
	}
	@Override
	public List<T> findAll() {
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<T> list = session.selectList(sqlNameSpace + "." + "findAll");
		session.close();
		return list;
	}
	@Override
	public boolean insert(T t) {
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.insert(sqlNameSpace + "." + "insert", t);
		session.commit();
		session.close();
		return (result > 0);
	}
	@Override
	public List<T> findAll(T t) {
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<T> list = session.selectList(sqlNameSpace + "." + "findAll",t);
		session.close();
		return list;
	}
	/*public List<T> findByPage(T t,String sqlId,int page,in){
		
	}*/
	@Override
	public T findById(String id) {
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		T t = session.selectOne(sqlNameSpace + "." + "findById", id);
		session.close();
		return t;
	}
	@Override
	public boolean update(T t) {
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.update(sqlNameSpace + "." + "update", t);
		session.commit();
		session.close();
		return (result > 0);
	}
	@Override
	public List<T> findAllOnCurrentDay(T t) {
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<T> list = session.selectList(sqlNameSpace + "." + "findAllOnCurrentDay",t);
		session.close();
		return list;
	}
	@Override
	public boolean deleteByID(String id) {
		SqlSession session = SqlMapInitUtil.getSqlSession();
		int result = session.delete(sqlNameSpace + "." + "deleteByID", id);
		session.commit();
		session.close();
		return (result > 0);
	}
	@Override
	public List<HashMap<String,Object>> selectMapByParam(HashMap<String, Object> param) {
		// TODO Auto-generated method stub
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<HashMap<String,Object>> list = session.selectList(sqlNameSpace + "." + "selectMapByParam",param);
		session.close();
		return list;
	}
	/**
	 * 根据参数查询当日一条数据
	 * @param t
	 * @return
	 */
	public T findOneByParamToday(T t){
		List<T> list = findAllOnCurrentDay(t);
		if(list != null){
			if(list.size() > 0){
				return list.get(0);
			}
			return null;
		}
		return null;
	}
	/**
	 * 根据参数查询一条数据
	 * @param t
	 * @return
	 */
	public T findOneByParam(T t){
		List<T> list = findAll(t);
		if(list != null){
			if(list.size() > 0){
				return list.get(0);
			}
			return null;
		}
		return null;
	}
	public PageUtil<T> findByPage(HashMap<String,Object> param){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		List<T> list = session.selectList(sqlNameSpace + "." + "findByPage",param);
		Integer total = findTotal(param);
		PageUtil<T> pageUtil = (PageUtil<T>) param.get("pageUtil");
		pageUtil.setTotalRecord(total);
		pageUtil.setResultList(list);
		session.close();
		return pageUtil;
	}
	public Integer findTotal(HashMap<String,Object> param){
		SqlSession session = SqlMapInitUtil.getSqlSession();
		Integer total = session.selectOne(sqlNameSpace + "." + "findTotal",param);
		session.close();
		return total;
	}
}
