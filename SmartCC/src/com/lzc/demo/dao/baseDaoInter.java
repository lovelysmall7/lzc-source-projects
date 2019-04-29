package com.lzc.demo.dao;

import java.util.HashMap;
import java.util.List;

public interface baseDaoInter<T> {
	/**
	 * 无条件查询所有数据
	 * @return
	 */
	public List<T> findAll();
	/**
	 * 查询当天的所有数据
	 * @return
	 */
	public List<T> findAllOnCurrentDay(T t);
	/**
	 * 插入一条数据
	 * @param t
	 * @return
	 */
	public boolean insert(T t);
	/**
	 * 按当前对象为条件参数查询数据
	 * @param t
	 * @return
	 */
	public List<T> findAll(T t);
	/**
	 * 根据id查询单条数据
	 * @param id
	 * @return
	 */
	public T findById(String id);
	/**
	 * 更新一条数据
	 * @param t 带有更新条件的对象
	 * @return
	 */
	public boolean update(T t);
	/**
	 * 根据id删除单条数据
	 * @param id
	 * @return
	 */
	public boolean deleteByID(String id);
	/**
	 * 根据条件查询返回Map形式数据
	 * @param param
	 * @return
	 */
	public List<HashMap<String,Object>> selectMapByParam(HashMap<String,Object> param);
}
