package com.lzc.demo.util;
import java.util.Map;

import com.lzc.demo.dao.DaoContainer;
import com.lzc.demo.dao.DivideHistoryInfoDao;
import com.lzc.demo.dao.StationInfoDao;
import com.lzc.demo.dao.TaskInfoDao;
import com.lzc.demo.dao.WorkerInfoDao;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.StationInfo;
import com.lzc.demo.model.TaskInfo;
import com.lzc.demo.model.WorkerInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
public class CharDataUtil {
	/**
	 * 暂时废弃 这个统计是按照任务数量统计
	 * 后来改为按照零件数目统计，即方法：newBuildCharData
	 * @return
	 */
	public static Map<String,Object> buildCharData(){
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		TaskInfo tt = new TaskInfo();
		List<TaskInfo> tList = taskInfoDao.findAllOnCurrentDay(tt);
		Map<String,String> idMap = new HashMap<String,String>();
		Map<String,Object> resultMap = new HashMap<String,Object>();//最终的结果集
		Integer totalFinishNum = 0;//总完成数目，用于给进度条
		//构建了一个包含所有当前有任务的人员id集合
		for(TaskInfo t:tList){
			if(t.getState() == 3){
				totalFinishNum ++;
			}
			if(t.getWorkerId() != null && !"".equals(t.getWorkerId()) ){
				idMap.put(t.getWorkerId(), t.getWorkerId());
			}
		}
		idMap.put("Total", "Total");
		//这个list中包含各个工人的 总任务数、未完成、完成、完成中 数目
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (Map.Entry<String, String> entry : idMap.entrySet()) {  
		    String workerId = entry.getKey(); 
		    Map<String,Object> hm = getNumFromTaskList(workerId);
		    list.add(hm);
		}  
		
		
		List<String> categoriesList = new ArrayList<String>();
		List<Integer> unFinishList = new ArrayList<Integer>();//未完成数量集合
		List<Integer> finishList = new ArrayList<Integer>();//已完成数量集合
		List<Integer> doingList = new ArrayList<Integer>();//进行中数量集合
		for (Map.Entry<String, String> entry : idMap.entrySet()) {  
			categoriesList.add(entry.getKey());//有多少类别（柱的数量）
			for(Map<String,Object> hm : list){
				if(entry.getKey().equals(hm.get("workerId"))){
					unFinishList.add((Integer) hm.get("unFinishNum"));
					finishList.add((Integer) hm.get("finishNum"));
					doingList.add((Integer) hm.get("doingNum"));
				}
			}
		}  
		
		Map<String,Object> unFinishListMap = new HashMap<String,Object>();
		unFinishListMap.put("name", "Unfinished");
		unFinishListMap.put("data", unFinishList);
		unFinishListMap.put("type", "bar");
		//unFinishListMap.put("color", "#FFAF60");
		
		Map<String,Object> doingListMap = new HashMap<String,Object>();
		doingListMap.put("name", "Completing");
		doingListMap.put("data", doingList);
		doingListMap.put("type", "bar");
		//doingListMap.put("color", "#97CBFF");
		
		Map<String,Object> finishListMap = new HashMap<String,Object>();
		finishListMap.put("name", "Completed");
		finishListMap.put("data", finishList);
		finishListMap.put("type", "bar");
		//finishListMap.put("color", "#79FF79");
		
		ArrayList<Map<String,Object>> seriesList = new ArrayList<Map<String,Object>>();
		seriesList.add(unFinishListMap);
		seriesList.add(doingListMap);
		seriesList.add(finishListMap);
		
		resultMap.put("categories", categoriesList);
		resultMap.put("series", seriesList);
		
		//----后加进度条数据------------------
		Integer sNum = 0;
		if(tList != null){
			if(tList.size() > 0){
				sNum = totalFinishNum * 100 / tList.size();
			}
		}
		
		resultMap.put("sNum", sNum);
		return resultMap;
	}
	/**
	 * 构建柱状图数据
	 * @return
	 */
	public static Map<String,Object> newBuildCharData(){
		DivideHistoryInfoDao dao = new DivideHistoryInfoDao();
		List<DivideHistoryInfo> dList = dao.findAllOnCurrentDay(new DivideHistoryInfo());
		
		Map<String,String> idMap = new LinkedHashMap<String,String>();
		Map<String,Object> resultMap = new HashMap<String,Object>();//最终的结果集
		Integer totalFinishNum = 0;//总完成数目，用于给进度条
		//构建了一个包含所有当前有任务的人员id集合
		for(DivideHistoryInfo t:dList){
			if(t.getState() == 3){
				totalFinishNum ++;
			}
			if(t.getWorkerId() != null && !"".equals(t.getWorkerId()) ){
				idMap.put(t.getWorkerId(), t.getWorkerId());
			}
		}
		idMap.put("Total", "Total");
		//这个list中包含各个工人的 总任务数、未完成、完成、完成中 数目
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (Map.Entry<String, String> entry : idMap.entrySet()) {  
		    String workerId = entry.getKey(); 
		    Map<String,Object> hm = getNumFromDivitionInfoList(workerId);
		    list.add(hm);
		}  
		
		
		List<String> categoriesList = new ArrayList<String>();
		List<Object> unFinishList = new ArrayList<Object>();//未完成数量集合
		List<Object> finishList = new ArrayList<Object>();//已完成数量集合
		List<Object> doingList = new ArrayList<Object>();//进行中数量集合
		for (Map.Entry<String, String> entry : idMap.entrySet()) {  
			categoriesList.add(entry.getKey());//有多少类别（柱的数量）
			for(Map<String,Object> hm : list){
				if(entry.getKey().equals(hm.get("workerId"))){
					int unFinishNum = (Integer) hm.get("unFinishNum");
					int finishNum = (Integer) hm.get("finishNum");
					int doingNum = (Integer) hm.get("doingNum");
					if(unFinishNum == 0){
						unFinishList.add("");
					}
					else{
						unFinishList.add(unFinishNum);
					}
					if(finishNum == 0){
						finishList.add("");
					}
					else{
						finishList.add(finishNum);
					}
					if(doingNum == 0){
						doingList.add("");
					}
					else{
						doingList.add(doingNum);
					}
					
				}
			}
		}  
		HashMap<String,Object> nomalMap = new HashMap<String,Object>();
		nomalMap.put("show", true);
		nomalMap.put("position", "insideRight");
		HashMap<String,Object> labelMap = new HashMap<String,Object>();
		labelMap.put("normal", nomalMap);
		
		
		
		
		Map<String,Object> unFinishListMap = new HashMap<String,Object>();
		unFinishListMap.put("name", "Unfinished");
		unFinishListMap.put("type", "bar");
		unFinishListMap.put("stack", "总量");
		unFinishListMap.put("label",labelMap);
		unFinishListMap.put("data", unFinishList);
		
		Map<String,Object> UnFinishNormalMap = new HashMap<String,Object>();
		UnFinishNormalMap.put("color", "#FFB90F");
		Map<String,Object> UnFinishItemStyleMap  = new HashMap<String,Object>();
		UnFinishItemStyleMap.put("normal", UnFinishNormalMap);
		unFinishListMap.put("itemStyle", UnFinishItemStyleMap);
		//unFinishListMap.put("color", "#FFAF60");
		
		Map<String,Object> doingListMap = new HashMap<String,Object>();
		doingListMap.put("name", "Completing");
		doingListMap.put("type", "bar");
		doingListMap.put("stack", "总量");
		doingListMap.put("label",labelMap);
		doingListMap.put("data", doingList);
		
		Map<String,Object> doingNormalMap = new HashMap<String,Object>();
		doingNormalMap.put("color", "#3daae9");
		Map<String,Object> doingItemStyleMap  = new HashMap<String,Object>();
		doingItemStyleMap.put("normal", doingNormalMap);
		doingListMap.put("itemStyle", doingItemStyleMap);
		
		//doingListMap.put("color", "#97CBFF");
		
		Map<String,Object> finishListMap = new HashMap<String,Object>();
		finishListMap.put("name", "Completed");
		finishListMap.put("type", "bar");
		finishListMap.put("stack", "总量");
		finishListMap.put("label",labelMap);
		finishListMap.put("data", finishList);
		
		Map<String,Object> finishNormalMap = new HashMap<String,Object>();
		finishNormalMap.put("color", "#86ca7e");
		Map<String,Object> finishItemStyleMap  = new HashMap<String,Object>();
		finishItemStyleMap.put("normal", finishNormalMap);
		finishListMap.put("itemStyle", finishItemStyleMap);
		
		//finishListMap.put("color", "#79FF79");
		
		ArrayList<Map<String,Object>> seriesList = new ArrayList<Map<String,Object>>();
		seriesList.add(unFinishListMap);
		seriesList.add(doingListMap);
		seriesList.add(finishListMap);
		
		resultMap.put("categories", categoriesList);
		resultMap.put("series", seriesList);
		
		//----后加进度条数据------------------
		Integer sNum = 0;
		if(dList != null){
			if(dList.size() > 0){
				sNum = totalFinishNum * 100 / dList.size();
			}
		}
		
		resultMap.put("sNum", sNum);
		return resultMap;
	}
	private static Map<String,Object> getNumFromDivitionInfoList(String workerId){
		DivideHistoryInfoDao dao = new DivideHistoryInfoDao();
		List<DivideHistoryInfo> dList = null;
		Map<String,Object> hm = new HashMap<String,Object>();
		if("Total".equals(workerId)){//查出所有的task
			DivideHistoryInfo tt = new DivideHistoryInfo();
			dList = dao.findAllOnCurrentDay(tt);
		}
		else{//查出指定人的task
			DivideHistoryInfo t = new DivideHistoryInfo();
			t.setWorkerId(workerId);
			dList = dao.findAllOnCurrentDay(t);
		}
		Integer totalNum = dList.size();
		Integer doingNum = 0;
		Integer finishNum = 0;
		Integer unFinishNum = 0;
		for(DivideHistoryInfo t: dList){
			if(t.getState() == 3){
				finishNum ++;
			}
			if(t.getState() == 2){
				doingNum ++;
			}
		}
		unFinishNum = totalNum - finishNum - doingNum;
		
		hm.put("workerId", workerId);
		hm.put("totalNum", totalNum);
		hm.put("finishNum", finishNum);
		hm.put("doingNum", doingNum);
		hm.put("unFinishNum", unFinishNum);
		return hm;
	}
	/**
	 * 获得该工人的 总任务数 未完成  完成 完成中 的数目
	 * 废弃：见getNumFromDivitionInfoList
	 * @param workerId
	 * @return
	 */
	private static Map<String,Object> getNumFromTaskList(String workerId){
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		List<TaskInfo> taskList = null;
		Map<String,Object> hm = new HashMap<String,Object>();
		if("Total".equals(workerId)){//查出所有的task
			TaskInfo tt = new TaskInfo();
			taskList = taskInfoDao.findAllOnCurrentDay(tt);
		}
		else{//查出指定人的task
			TaskInfo t = new TaskInfo();
			t.setWorkerId(workerId);
			taskList = taskInfoDao.findAllOnCurrentDay(t);
		}
		Integer totalNum = taskList.size();
		Integer doingNum = 0;
		Integer finishNum = 0;
		Integer unFinishNum = 0;
		for(TaskInfo t: taskList){
			if(t.getState() == 3){
				finishNum ++;
			}
			if(t.getState() == 2){
				doingNum ++;
			}
		}
		unFinishNum = totalNum - finishNum - doingNum;
		
		hm.put("workerId", workerId);
		hm.put("totalNum", totalNum);
		hm.put("finishNum", finishNum);
		hm.put("doingNum", doingNum);
		hm.put("unFinishNum", unFinishNum);
		return hm;
	}
	/**
	 * 
	 * 获取当前所有已分配的工位点
	 * @return
	 */
	public static List<Map<String,Object>> getPointsData(){
		List<Map<String,Object>> listA = new ArrayList<Map<String,Object>>();
		StationInfoDao daoA = new StationInfoDao();
		DivideHistoryInfoDao  dao = new DivideHistoryInfoDao();
		//查询当天工人的任务分配信息
		List<DivideHistoryInfo> list = dao.findAllOnCurrentDay(null);
		//存储每个工位与工人的对应关系list  P00001:01_01_02,P00002:03_98_09...
		List<Map<String,String>> l = new ArrayList<Map<String,String>>();
		for(int i = 0;i < list.size(); i++){
			String stationNames = list.get(i).getStationNames();
			String workerId = list.get(i).getWorkerId();
			if(stationNames != null){
				String[] a = stationNames.split(",");
				for(int j=0;j<a.length;j++){
					Map<String,String> userStationMap = new HashMap<String,String>();
					userStationMap.put("workerNo", workerId);
					userStationMap.put("stationName", a[j]);
					l.add(userStationMap);
				}
			}
		}
		for(int i=0;i<l.size();i++){
			StationInfo s = new StationInfo();
			Map<String,String> userStationMap = l.get(i);
			s.setStationName(userStationMap.get("stationName"));
			List<StationInfo> listS = daoA.findAll(s);
			s = listS.get(0);
			Map<String,Object> aMap = new HashMap<String,Object>();
			String[] aaa = new String[2];
			aaa[0] = s.getxAxis();
			aaa[1] = s.getyAxis();
			aMap.put("stationName",s.getStationName());
			aMap.put("color", "green");
			aMap.put("value", aaa);
			//aMap.put("category", 1);
			aMap.put("category", userStationMap.get("workerNo"));
			listA.add(aMap);
			
		}
		return listA;
	}
	/**---
	 * 根据集合中的点 调整坐标系x、y轴的范围 以达到尽量减少空白区域提升可视效果
	 * @param pointList
	 * @return map type: maxX:num maxY:num minX:num minY:mun
	 */
	public static Map<String,Integer> adjustCoordinate(List<Map<String,Object>> pointList){
		List<Integer> xList = new ArrayList<Integer>();
		List<Integer> yList = new ArrayList<Integer>();
		if(pointList != null){
			Iterator<Map<String,Object>> it = pointList.iterator();
			if(it != null){
				while(it.hasNext()){
					Map<String,Object> point = it.next();
					String[] a = (String[]) point.get("value");
					if(a != null){
						if(a.length == 2){
							String x = a[0];//x轴坐标
							String y = a[1];//y轴坐标
							xList.add((int)Float.parseFloat(x));
							yList.add((int)Float.parseFloat(y));
						}
					}
				}
				Collections.sort(xList);
				Collections.sort(yList);
			}
		}
		Map<String,Integer> result = new HashMap<String,Integer>();
		result.put("minX",xList.get(0));
		result.put("maxX",xList.get(xList.size() - 1));
		result.put("minY",yList.get(0));
		
		result.put("maxY",yList.get(yList.size() - 1));
		return result;
		
	}
	public static ArrayList<String> getlegendData(List<Map<String,Object>> points){
		Map<String,String> cateGoryMap = new HashMap<String,String>();
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0;i < points.size();i++){
			Map<String,Object> m = points.get(i);
			String category = (String) m.get("category");
			cateGoryMap.put(category, category);
		}
		for (Map.Entry<String,String> entry : cateGoryMap.entrySet()) {  
			list.add(entry.getKey());
		} 
		return list;
	}
	public static ArrayList<Map<String,Object>> getCategoriesList(List<String> list){
		ArrayList<Map<String,Object>> categoryList = new ArrayList<Map<String,Object>>();
		WorkerInfoDao dao = new WorkerInfoDao();
		for(int i = 0;i < list.size();i++){
			String workerId = list.get(i);
			//根据id查出一个工人信息（为了得到颜色代码）
			WorkerInfo worker = dao.findById(workerId);
			Map<String,String> aMap = new HashMap<String,String>();
			aMap.put("color", worker.getColor());
			Map<String,Object> bMap = new HashMap<String,Object>();
			bMap.put("normal", aMap);
			Map<String,Object> cMap = new HashMap<String,Object>();
			cMap.put("itemStyle", bMap);
			cMap.put("name", workerId);
			categoryList.add(cMap);
		}
		return categoryList;
	}
	public static Map<String,Object> buildParamForPython(){
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		//这里构建参数调用python
		List<TaskInfo> bList = taskInfoDao.findFirstComponentStation(null);
		//存放workerId 的map
		Map<String,Object> presonMap = new HashMap<String,Object>();
		if(bList != null){
			Iterator<TaskInfo> it = bList.iterator();
			while(it.hasNext()){
				TaskInfo t = it.next();
				//workerId去重存入map
				presonMap.put(t.getWorkerId(), t.getWorkerId());
			}
			
		}
		  
		for (Map.Entry<String, Object> entry : presonMap.entrySet()) {  
		    String workerId = entry.getKey();//获得工人id
		    ArrayList<String> stationList = new ArrayList<String>();
		    for(int k = 0;k < bList.size();k++){
		    	TaskInfo t = bList.get(k);
		    	if(t.getWorkerId() != null){
		    		if(t.getWorkerId().equals(workerId)){
			    		stationList.add(t.getStationName());
			    	}
		    	}
		    }
		    presonMap.put(workerId, stationList);
		} 
		return presonMap;
	}
	
	
	public static ArrayList<ArrayList<Object>>  buildLineList(String findPathJson,Map<String,Object> personLMap,List<Map<String,Object>> listA){
		JSONObject jObj = JSONObject.fromObject(findPathJson);
		Iterator iterator = jObj.keys();//获取返回json对象所有的key
		ArrayList<ArrayList<Object>> finalLineList = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> preLineList = new ArrayList<Object>();
		while(iterator.hasNext()){
			
		     String key = (String) iterator.next();
		     System.out.println(key);
		     String value = jObj.getString(key);
		     //personLMap是传给python时的 参数 里面有每个工人每个工位的顺序  stations是一个工人的工位编号集合
		     ArrayList<String> stations  = (ArrayList<String>) personLMap.get(key);
		     //这个数组的长度就是演示变换的次数
		     JSONArray jr1 = JSONArray.fromString(value);
		    
		     
		     for(int i = 0;i < jr1.length();i++){//这个fornew HashMap<String,Object>();代表每一个人员的变换次数
		    	 JSONArray a =  (JSONArray) jr1.get(i);//这个for代表每一个人员的每一次变化的具体内容
		    	 ArrayList<Object> lineList = new ArrayList<Object>();//这个集合代表每个员工的一组连线
		    	 addPreLineToThis(preLineList, lineList);
		    	 for(int j = 0;j < a.length();j++){
		    		 if(j < a.length() - 1){//按数组元素代表1连2 2连3 所以最后一个元素就没有表示连接信息了
		    			 Integer sindex = (Integer) a.get(j);
		    			 Integer tindex = (Integer) a.get(j + 1);
		    			 //表示每一次的连接情况 （从A点到B点） 
			    		 Map<String,Object> lineMap = new HashMap<String,Object>();
			    		 //获得当前序号对应的工位编号
			    		 if(sindex >= stations.size()){
			    			 continue;
			    		 }
			    		 if(tindex >= stations.size()){
			    			 continue;
			    		 }
			    		 String stationName_s = stations.get(sindex);
			    		 String stationName_t = stations.get(tindex);
						 Integer source = getIndexOfStation(listA,stationName_s);
						 Integer target = getIndexOfStation(listA,stationName_t);
						 if(source != null && target != null){
							 lineMap.put("source",source);
							 lineMap.put("target", target);
							 lineList.add(lineMap);//一个人的一组连线
						 }
						 
		    		 }
		    		 
		    	 }
		    	 if(i == jr1.length() - 1){
					 preLineList = lineList;
				 }
		    	 finalLineList.add(lineList);
		     }
		}
		return finalLineList;
	}
	private static void addPreLineToThis(ArrayList<Object> preLineList,ArrayList<Object> currentLine){
		for(int i = 0;i < preLineList.size();i++){
			currentLine.add(preLineList.get(i));
		}
	}
	private static Integer getIndexOfStation(List<Map<String,Object>> pointList,String stationName){
		for(int i = 0; i< pointList.size();i++){
			Map<String,Object> map = pointList.get(i);
			String stationName1 = (String) map.get("stationName");
			if(stationName.equals(stationName1)){
				return i;
			}
		}
		return null;
	}
	public static ArrayList<Object> getArrowList(ArrayList<Object> finalLine,List<Map<String,Object>> listA,String personFilter){
		ArrayList<Object> arrowList = new ArrayList();
			for(int i = 0;i < finalLine.size();i++){
				@SuppressWarnings("unchecked")
				Map<String,Integer> map = (Map<String, Integer>) finalLine.get(i);
				Integer pointAIndex = map.get("source");
				Integer pointBindex = map.get("target");
				Map<String,Object> pointMapA = listA.get(pointAIndex);
				Map<String,Object> pointMapB = listA.get(pointBindex);
				String workerIdA = (String) pointMapA.get("category");
				String workerIdB = (String) pointMapB.get("category");
				boolean flag = true;
				if(personFilter != null){
					JSONObject jObj = JSONObject.fromObject(personFilter);
					Iterator iterator = jObj.keys();//获取返回json对象所有的key
					while(iterator.hasNext()){
						String workerId = (String) iterator.next();
						String seledted = jObj.getString(workerId);
						if(seledted.equals("false")){
							if(workerIdA.equals(workerId) || workerIdB.equals(workerId)){
								flag = false;
							}
						}
					}
				}
				if(flag){
					String[] aaa = (String[]) pointMapA.get("value");
					String[] bbb = (String[]) pointMapB.get("value");
					Map<String,Object> aMap = new HashMap<String,Object>();
					Map<String,Object> bMap = new HashMap<String,Object>();
					aMap.put("coord", aaa);
					bMap.put("coord", bbb);
					ArrayList<Map<String,Object>> eachArrow = new ArrayList<Map<String,Object>>();//每一个箭头
					eachArrow.add(aMap);
					eachArrow.add(bMap);
					arrowList.add(eachArrow);
				}
			}
		return arrowList;
	}
	/**
	 * SmartCC_part2 页面的柱状图数据
	 */
	public void buildPart2Char1(){
		
	}
	/**
	 * SmartCC_part2 页面的散点图数据
	 */
	public void buildPart2Char2(){
		
	}
	public static Map<String,Object> newBuildCharData_1(){
		List<DivideHistoryInfo> dList = DaoContainer.dDao.findCurrentBatchData(null);
		Map<String,String> idMap = new LinkedHashMap<String,String>();
		Map<String,Object> resultMap = new HashMap<String,Object>();//最终的结果集
		Integer totalFinishNum = 0;//总完成数目，用于给进度条
		//构建了一个包含所有当前有任务的人员id集合
		for(DivideHistoryInfo t:dList){
			if(t.getState() == 3){
				totalFinishNum ++;
			}
			if(t.getWorkerId() != null && !"".equals(t.getWorkerId()) ){
				idMap.put(t.getWorkerId(), t.getWorkerId());
			}
		}
		idMap.put("Total", "Total");
		//这个list中包含各个工人的 总任务数、未完成、完成、完成中 数目
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (Map.Entry<String, String> entry : idMap.entrySet()) {  
		    String workerId = entry.getKey(); 
		    Map<String,Object> hm = getNumFromDivitionInfoList_1(workerId);
		    list.add(hm);
		}  
		
		
		List<String> categoriesList = new ArrayList<String>();
		List<Object> unFinishList = new ArrayList<Object>();//未完成数量集合
		List<Object> finishList = new ArrayList<Object>();//已完成数量集合
		List<Object> doingList = new ArrayList<Object>();//进行中数量集合
		for (Map.Entry<String, String> entry : idMap.entrySet()) {  
			categoriesList.add(entry.getKey());//有多少类别（柱的数量）
			for(Map<String,Object> hm : list){
				if(entry.getKey().equals(hm.get("workerId"))){
					int unFinishNum = (Integer) hm.get("unFinishNum");
					int finishNum = (Integer) hm.get("finishNum");
					int doingNum = (Integer) hm.get("doingNum");
					if(unFinishNum == 0){
						unFinishList.add("");
					}
					else{
						unFinishList.add(unFinishNum);
					}
					if(finishNum == 0){
						finishList.add("");
					}
					else{
						finishList.add(finishNum);
					}
					if(doingNum == 0){
						doingList.add("");
					}
					else{
						doingList.add(doingNum);
					}
					
				}
			}
		}  
		HashMap<String,Object> nomalMap = new HashMap<String,Object>();
		nomalMap.put("show", true);
		nomalMap.put("position", "insideRight");
		HashMap<String,Object> labelMap = new HashMap<String,Object>();
		labelMap.put("normal", nomalMap);
		
		
		
		
		Map<String,Object> unFinishListMap = new HashMap<String,Object>();
		unFinishListMap.put("name", "Unfinished");
		unFinishListMap.put("type", "bar");
		unFinishListMap.put("stack", "总量");
		unFinishListMap.put("label",labelMap);
		unFinishListMap.put("data", unFinishList);
		
		Map<String,Object> UnFinishNormalMap = new HashMap<String,Object>();
		UnFinishNormalMap.put("color", "#FFB90F");
		Map<String,Object> UnFinishItemStyleMap  = new HashMap<String,Object>();
		UnFinishItemStyleMap.put("normal", UnFinishNormalMap);
		unFinishListMap.put("itemStyle", UnFinishItemStyleMap);
		//unFinishListMap.put("color", "#FFAF60");
		
		Map<String,Object> doingListMap = new HashMap<String,Object>();
		doingListMap.put("name", "Completing");
		doingListMap.put("type", "bar");
		doingListMap.put("stack", "总量");
		doingListMap.put("label",labelMap);
		doingListMap.put("data", doingList);
		
		Map<String,Object> doingNormalMap = new HashMap<String,Object>();
		doingNormalMap.put("color", "#3daae9");
		Map<String,Object> doingItemStyleMap  = new HashMap<String,Object>();
		doingItemStyleMap.put("normal", doingNormalMap);
		doingListMap.put("itemStyle", doingItemStyleMap);
		
		//doingListMap.put("color", "#97CBFF");
		
		Map<String,Object> finishListMap = new HashMap<String,Object>();
		finishListMap.put("name", "Completed");
		finishListMap.put("type", "bar");
		finishListMap.put("stack", "总量");
		finishListMap.put("label",labelMap);
		finishListMap.put("data", finishList);
		
		Map<String,Object> finishNormalMap = new HashMap<String,Object>();
		finishNormalMap.put("color", "#86ca7e");
		Map<String,Object> finishItemStyleMap  = new HashMap<String,Object>();
		finishItemStyleMap.put("normal", finishNormalMap);
		finishListMap.put("itemStyle", finishItemStyleMap);
		
		//finishListMap.put("color", "#79FF79");
		
		ArrayList<Map<String,Object>> seriesList = new ArrayList<Map<String,Object>>();
		seriesList.add(unFinishListMap);
		seriesList.add(doingListMap);
		seriesList.add(finishListMap);
		
		resultMap.put("categories", categoriesList);
		resultMap.put("series", seriesList);
		
		//----后加进度条数据------------------
		Integer sNum = 0;
		if(dList != null){
			if(dList.size() > 0){
				sNum = totalFinishNum * 100 / dList.size();
			}
		}
		
		resultMap.put("sNum", sNum);
		return resultMap;
	}
	private static Map<String,Object> getNumFromDivitionInfoList_1(String workerId){
		DivideHistoryInfoDao dao = new DivideHistoryInfoDao();
		List<DivideHistoryInfo> dList = null;
		Map<String,Object> hm = new HashMap<String,Object>();
		if("Total".equals(workerId)){//查出所有的task
			dList = dao.findCurrentBatchData(null);
		}
		else{//查出指定人的task
			HashMap<String,Object> param = new HashMap<String,Object>();
			param.put("workerId", workerId);
			dList = dao.findCurrentBatchData(param);
		}
		Integer totalNum = dList.size();
		Integer doingNum = 0;
		Integer finishNum = 0;
		Integer unFinishNum = 0;
		for(DivideHistoryInfo t: dList){
			if(t.getState() == 3){
				finishNum ++;
			}
			if(t.getState() == 2){
				doingNum ++;
			}
		}
		unFinishNum = totalNum - finishNum - doingNum;
		
		hm.put("workerId", workerId);
		hm.put("totalNum", totalNum);
		hm.put("finishNum", finishNum);
		hm.put("doingNum", doingNum);
		hm.put("unFinishNum", unFinishNum);
		return hm;
	}
}