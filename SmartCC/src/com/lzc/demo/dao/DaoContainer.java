package com.lzc.demo.dao;

public class DaoContainer {
	public static ComponentDao cDao;
	public static AreaInfoDao aDao;
	public static ComponentStationMappingDao csDao;
	public static DivideHistoryInfoDao dDao;
	public static GroupInfoDao gDao;
	public static StationInfoDao sDao;
	public static WorkerInfoDao wDao;
	static{
		aDao = new AreaInfoDao();
		csDao = new ComponentStationMappingDao();
		dDao = new DivideHistoryInfoDao();
		gDao = new GroupInfoDao();
		sDao = new StationInfoDao();
		wDao = new WorkerInfoDao();
	}
}
