package com.lzc.demo.util.optimize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lzc.demo.dao.DaoContainer;
import com.lzc.demo.dao.StationInfoDao;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.model.StationInfo;
import com.lzc.demo.util.CalculationUtil;
/**
 * 零件内的工位排序(上传任务的时候调用)
 * @param stationList
 * @return
 */
public class BaseOptimizeAlgo {
	public List<String> sortComponentStations(List<String> stationList) {
		// TODO Auto-generated method stub
		if(stationList == null){
			return stationList;
		}
		if(stationList.size() == 1){//只有一个工位不需要排序哈哈
			return stationList;
		}
		List<String> newStationList = new ArrayList<String>();
		
		while(stationList.size() > 0){
			
			String smallStation = null;
			int minIndex = -1;
			for(int i = 0;i < stationList.size();i++){
				if(smallStation == null){
					smallStation = stationList.get(i);
					minIndex = 0;
				}
				else{
					if(judgeStation(stationList.get(i),smallStation)){
						smallStation = stationList.get(i);
						minIndex = i;
					}
				}
			}
			
			Iterator<String> it = stationList.iterator();
			int count = 0;
			while(it.hasNext()){
				String station = it.next();
				if(count == minIndex){
					it.remove();
					newStationList.add(station);
					break;
				}
				count++;
			}
			
			smallStation = null;
		}
		return newStationList;
	}

	/**
	 * 判断两个工位谁在前面 根据工位号判断 
	 * @param A
	 * @param B
	 * @return true 前者有限 false 后者优先
	 */
	private boolean judgeStation(String A,String B){
		if(A == null || B == null){
			return false;
		}
		if(A.equals(B)){
			return false;
		}
		//如果存在纵区  这种判断方式不可用  直接返回false 先
		if(A.length() < 6 || B.length() < 6){
			return false;
		}
		String a01 = A.substring(0,2);
		String b01 = B.substring(0,2);
		if(Integer.parseInt(a01) < Integer.parseInt(b01)){
			return true;
		}
		if(Integer.parseInt(a01) > Integer.parseInt(b01)){
			return false;
		}
		String a02 = A.substring(2,4);
		String b02 = B.substring(2,4);
		if(Integer.parseInt(a02) < Integer.parseInt(b02)){
			return true;
		}
		if(Integer.parseInt(a02) > Integer.parseInt(b02)){
			return false;
		}
		String a03 = A.substring(3,6);
		String b03 = B.substring(3,6);
		if(Integer.parseInt(a03) < Integer.parseInt(b03)){
			return true;
		}
		if(Integer.parseInt(a03) > Integer.parseInt(b03)){
			return false;
		}
		return false;
	}
	/**
	 * 更新任务序号
	 * @param resultList
	 * @param startIndex
	 */
	protected void updateOrderNum(List<DivideHistoryInfo> resultList,Integer startIndex,boolean isHasPre){
		if(startIndex < 1){
			startIndex = 1;
		}
		if(isHasPre){
			resultList.remove(0);
		}
		for(int i = 0;i < resultList.size();i++){
			DivideHistoryInfo d = resultList.get(i);
			d.setOrderNum(startIndex);
			DaoContainer.dDao.update(d);
			startIndex++;
		}
	}
	/**
	 * 
	 * @param dInfo 之前的完成或紧急任务
	 * @param dList 当前待优化集合
	 * @return 第一位代表是否之前有任务  第二位代表从左侧还是右侧算起始点
	 */
	protected String judgeIsHasPre(DivideHistoryInfo dInfo,List<DivideHistoryInfo> dList){
		if(dInfo == null){
			return "00";
		}
		ArrayList<ArrayList<Integer>> mapData = MapInfo.getInstanse().getMapData();
		StationInfo ls = new StationInfo(1,1);//地图左上角点
		StationInfo rs = new StationInfo(267,385);//地图右下角点
		StationInfoDao sDao = new StationInfoDao();
		String stationNames = dInfo.getStationIds();
		String[] stationArr = stationNames.split(",");
		String preStation = stationArr[stationArr.length - 1];//获取之前最后一个完成或紧急任务的 最后一个工位
		
		StationInfo preStationInfo = sDao.findById(preStation);//将这个工位的信息查出来
		
		Integer leftDistanse = CalculationUtil.getDistanse(mapData, preStationInfo, ls);
		Integer rightDistanse = CalculationUtil.getDistanse(mapData, preStationInfo, rs);
		
		Integer minLeftDistanse = -1;
		Integer minRightDistanse = -1;
		
		
		StationInfo minLeftStation = null;
		StationInfo minRightStation = null;
		//这个循环为了找到 当前工位集合中 距离最左上角最近点：minLeftStation 和距离最右下角最近点：minRightStation
		for (int c = 0;c < dList.size();c++) { 
			 // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
			  DivideHistoryInfo d = dList.get(c);
			  String stationIdsStr = d.getStationIds();
			  String[] stations = stationIdsStr.split(",");
			  for(int i = 0;i < stations.length;i++){
				  String stationName = stations[i];
				  StationInfo sinfo = sDao.findById(stationName);
				  Integer l = CalculationUtil.getDistanse(mapData, ls, sinfo);
				  Integer r = CalculationUtil.getDistanse(mapData, rs, sinfo);
				  if(minLeftDistanse == -1){
					  minLeftStation = sinfo;
					  minLeftDistanse = l;
				  }
				  
				  else{
					  if(l < minLeftDistanse){
						  minLeftStation = sinfo;
						  minLeftDistanse = l;
					  }
				  }
				  if(minRightDistanse == -1){
					  minRightStation = sinfo;
					  minRightDistanse = r;
				  }
				  else{
					  if(r < minRightDistanse){
						  minRightStation = sinfo;
						  minRightDistanse = r;
					  }
				  }
			  }
		}
		//判断之前的点离当前工位集合 最左上方点近还是最右下方点近
		Integer distanse_l = CalculationUtil.getDistanse(mapData,preStationInfo,minLeftStation);
		Integer distanse_r = CalculationUtil.getDistanse(mapData,preStationInfo,minRightStation);
		if(distanse_l <= distanse_r){
			if(leftDistanse < minLeftDistanse){
				return "10";
			}
			else{
				return "00";
			}
		}
		else{
			if(rightDistanse < minRightDistanse){
				return "11";
			}
			else{
				return "01";
			}
		}
	}
}
