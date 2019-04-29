package com.lzc.demo.util.optimize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.util.Astar.AStarMapInfo;

public interface StartOptimizeInter {
	/**
	 * 开始优化
	 * @param workerTaskList
	 * @param workerId
	 * @return
	 */
	public void startOptimize(List<DivideHistoryInfo> workerTaskList,String workerId);
	
	/**
	 * 优化顺序的算法
	 * @param dList
	 * @param preComponentName
	 * @param startPointFlag
	 * @return
	 */
	public List<DivideHistoryInfo> divideAlgo(String workerId,List<DivideHistoryInfo> dList,boolean isEmergency);
}
