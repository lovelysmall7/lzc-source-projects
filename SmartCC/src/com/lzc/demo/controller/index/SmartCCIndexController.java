package com.lzc.demo.controller.index;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.StringUtils;

import com.lzc.demo.dao.DivideHistoryInfoDao;
import com.lzc.demo.dao.StationInfoDao;
import com.lzc.demo.dao.TaskInfoDao;
import com.lzc.demo.dao.TaskWalkLengthDao;
import com.lzc.demo.dao.WorkerInfoDao;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.model.StationInfo;
import com.lzc.demo.model.TaskInfo;
import com.lzc.demo.model.TaskWalkLength;
import com.lzc.demo.model.WorkerInfo;
import com.lzc.demo.service.index.SmartCCIndexService;
import com.lzc.demo.controller.BaseServlet;
import com.lzc.demo.util.CalculationUtil;
import com.lzc.demo.util.CharDataUtil;
import com.lzc.demo.util.ExcelHelper;
import com.lzc.demo.util.ExcelUtil;
import com.lzc.demo.util.PropertiesUtil;
import com.lzc.demo.util.PythonUtil;
import com.lzc.demo.util.SysStringUtil;
import com.lzc.demo.util.Astar.AStar;
import com.lzc.demo.util.Astar.AStarMapInfo;
import com.lzc.demo.util.Astar.Node;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@WebServlet("/smartCCIndex")
public class SmartCCIndexController extends BaseServlet{
	private SmartCCIndexService service = new SmartCCIndexService();
	public void uploadEmergencyTaskExcel(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();	
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		String isEmergency = "";//是否勾选了紧急
		String component = "";//
		FileItem item = null;//
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> list;
		try {
			list = upload.parseRequest(request);
			for(FileItem it : list){
		     	   if(it != null){
		     		   if(it.getSize() > 0){
		     			   if("emergencyExcelData".equals(it.getFieldName())){
		     				   item = it;
		     			   }
		     			   if("component".equals(it.getFieldName())){
		     				   component = it.getString();
		     			   }
		     			   if("isEmergency".equals(it.getFieldName())){
		     				  isEmergency = it.getString();
		     			   }
		     		   }
		     	   }
		        }
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		WorkerInfoDao workerDao = new WorkerInfoDao();
		//上传任务后会对删除当前任务与分配结果 然后再进行重新分配 所以需要先将当前已经分配任务的人员查出来
		List<WorkerInfo> wList = workerDao.selectCurrentDividedPerson();
		String resultStr = new SmartCCIndexService().uploadEmergencyTaskExcel(item,isEmergency,component);
		if ("-1".equals(resultStr)) {
			result.put("code", "-1");
			result.put("data", null);
			String resultJson = JSONObject.fromObject(result).toString();
			writeToView(response, resultJson);
		}
		if(("-2").equals(resultStr)){
			result.put("code", "-2");
			result.put("data", null);
			String resultJson = JSONObject.fromObject(result).toString();
			writeToView(response, resultJson);
		}
		else{
			if(wList.size() > 0){//需要重新分配
				TaskWalkLengthDao twDao = new TaskWalkLengthDao();
				
				//若已经分配过 需要删除当天数据重新分配
				//DivideHistoryInfo dParam = new DivideHistoryInfo();
				//20180418修改 只删除未完成的分配记录 已完成的要排除
				//List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(dParam);
				List<DivideHistoryInfo> ddList = dDao.findUnfinishOnCurrentDay();
				if(ddList.size() > 0){
					dDao.deleteCurrentData();
					twDao.deleteCurrentData();
					TaskInfo tParam1 = new TaskInfo();
					tParam1.setState(1);
					//将之前更新为已分配状态的任务 复原回未分配
					//20180418修改 只回复未完成的任务 已完成的要排除
					//List<TaskInfo> tList_1 = taskInfoDao.findAllOnCurrentDay(tParam1);
					List<TaskInfo> tList_1 = taskInfoDao.findCurrentNotFinishList();
					if(tList_1 != null){
						for(int i = 0;i < tList_1.size();i++){
							TaskInfo t = tList_1.get(i);
							t.setState(0);
							t.setWorkerId(null);
							taskInfoDao.update(t);
						}
					}
				}
				//为A* 算法提供地图数据
				ArrayList<ArrayList<Integer>> mapData = service.buildMapData();
				AStarMapInfo mapInfo = new AStarMapInfo();
				mapInfo.buildMap(mapData);		
				for(int i = 0;i < wList.size();i++){
					WorkerInfo wi = wList.get(i);
					String workerId = wi.getId();
					String areaId = wi.getAreaId();
					//如果是紧急任务 就先分配紧急任务
					boolean hasEmergencyTask = divisionEmergencyTaskByWorker(workerId, areaId, mapInfo, "1",false);
					//然后再分配普通任务
					divisionEmergencyTaskByWorker(workerId, areaId, mapInfo, "0",hasEmergencyTask);
					
					
				}
			}
			result.put("code", 0);
			TaskInfo t1 = new TaskInfo();
			List<TaskInfo> tList = taskInfoDao.findAllOnCurrentDay(t1);
			result.put("data", tList); 
			List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(new DivideHistoryInfo());
		
			result.put("data1", dList); 
			//-------------构建图表数据---------------------
			Map<String,Object> charDataMap = CharDataUtil.newBuildCharData();
			Object categoriesList = charDataMap.get("categories");
			Object seriesList = charDataMap.get("series");
			result.put("categories", categoriesList);
			result.put("series", seriesList);
			Integer sNum = (Integer) charDataMap.get("sNum");
			result.put("sNum", sNum);
			Integer componentNum = taskInfoDao.selectCurrentComponentNum();
			result.put("componentNum", componentNum);
			writeToView(response, result);
		}
	}
	 /**
	  * 上传任务列表Excel
	  * @param request
	  * @param response
	  * @throws ServletException
	  * @throws IOException
	  */
	 public void uploadTaskExcel(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 TaskInfoDao taskInfoDao = new TaskInfoDao();
		TaskWalkLengthDao twDao = new TaskWalkLengthDao();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();	
		 HashMap<String, Object> result = new HashMap<String, Object>();
			WorkerInfoDao workerDao = new WorkerInfoDao();
			//上传任务后会对删除当前任务与分配结果 然后再进行重新分配 所以需要先将当前已经分配任务的人员查出来
			List<WorkerInfo> wList = workerDao.selectCurrentDividedPerson();
			//这里是
		 	String resultStr = new SmartCCIndexService().uploadTaskExcel(request);
			if ("-1".equals(resultStr)) {
				result.put("code", "-1");
				result.put("data", null);
				String resultJson = JSONObject.fromObject(result).toString();
				writeToView(response, resultJson);
			}
			if(("-2").equals(resultStr)){
				result.put("code", "-2");
				result.put("data", null);
				String resultJson = JSONObject.fromObject(result).toString();
				writeToView(response, resultJson);
			}
			else{ 
				if(wList.size() > 0){//需要重新分配
					//为A* 算法提供地图数据
					ArrayList<ArrayList<Integer>> mapData = service.buildMapData();
					AStarMapInfo mapInfo = new AStarMapInfo();
					mapInfo.buildMap(mapData);		
					for(int i = 0;i < wList.size();i++){
						WorkerInfo wi = wList.get(i);
						String workerId = wi.getId();
						String areaId = wi.getAreaId();
						//divisionByWorker(workerId, areaId, mapInfo);
						//如果是紧急任务 就先分配紧急任务
						boolean hasEmergencyTask = divisionEmergencyTaskByWorker(workerId, areaId, mapInfo, "1",false);
						//然后再分配普通任务
						divisionEmergencyTaskByWorker(workerId, areaId, mapInfo, "0",hasEmergencyTask);
					}
				}
				TaskInfo t1 = new TaskInfo();
				List<TaskInfo> tList = taskInfoDao.findAllOnCurrentDay(t1);
				result.put("data", tList); 
				List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(new DivideHistoryInfo());
			
				result.put("dList", dList); 
				//-------------构建图表数据---------------------
				Map<String,Object> charDataMap = CharDataUtil.newBuildCharData();
				Object categoriesList = charDataMap.get("categories");
				Object seriesList = charDataMap.get("series");
				result.put("categories", categoriesList);
				result.put("series", seriesList);
				Integer sNum = (Integer) charDataMap.get("sNum");
				result.put("sNum", sNum);
				Integer componentNum = taskInfoDao.selectCurrentComponentNum();
				result.put("componentNum", componentNum);
				writeToView(response, result);
			}
	 }
	 /**
	  * 加载数据
	  * @param request
	  * @param response
	  * @throws ServletException
	  * @throws IOException
	  */
	 public void loadData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String resultJson =  new SmartCCIndexService().loadData(request);
		System.out.println(resultJson);
		
		writeToView(response, resultJson);
	 }
		
		
	private Integer getIndexOfStation(List<Map<String,Object>> pointList,String stationName){
		for(int i = 0; i< pointList.size();i++){
			Map<String,Object> map = pointList.get(i);
			String stationName1 = (String) map.get("stationName");
			if(stationName.equals(stationName1)){
				return i;
			}
		}
		return null;
	}
	/**
	 * 处理人员分配任务请求
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void submitScheme(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		TaskWalkLengthDao twDao = new TaskWalkLengthDao();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		HashMap<String,Object> resultHM = new HashMap<String,Object>();
		//String isManual = request.getParameter("isManual");
		String workersJson = request.getParameter("workersJson");
		
		//System.out.println(workersJson);
		DivideHistoryInfoDao divideHistoryInfoDao = new DivideHistoryInfoDao();
		//将前端传递的选择人员集合字符串转为json数组
		JSONArray jarray = JSONArray.fromObject(workersJson);
		
	
		
		//为A* 算法提供地图数据
		/**ArrayList<ArrayList<Integer>> mapData1 = service.buildMapData();
		AStarMapInfo mapInfo1 = new AStarMapInfo();
		mapInfo1.buildMap(mapData1);
		//地图数据构建结束
		//test Code
		Node sNode = new Node(211,272);
		Node eNode = new Node(231,243);
		mapInfo1.setInfo(sNode, eNode);
		  //用两个相邻工位去调用A* 算法 计算距离
		AStar aStar = new AStar();
		aStar.start(mapInfo1);
		System.out.println(eNode.G);**/
		
		
		//若已经分配过 需要删除当天数据重新分配
		//DivideHistoryInfo dParam = new DivideHistoryInfo();
		//20180418修改 只删除未完成的分配记录 已完成的要排除
		//List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(dParam);
		List<DivideHistoryInfo> ddList = dDao.findUnfinishOnCurrentDay();
		if(ddList.size() > 0){
			dDao.deleteCurrentData();
			twDao.deleteCurrentData();
			TaskInfo tParam1 = new TaskInfo();
			tParam1.setState(1);
			//将之前更新为已分配状态的任务 复原回未分配
			//20180418修改 只回复未完成的任务 已完成的要排除
			//List<TaskInfo> tList_1 = taskInfoDao.findAllOnCurrentDay(tParam1);
			List<TaskInfo> tList_1 = taskInfoDao.findCurrentNotFinishList();
			if(tList_1 != null){
				for(int i = 0;i < tList_1.size();i++){
					TaskInfo t = tList_1.get(i);
					t.setState(0);
					t.setWorkerId(null);
					taskInfoDao.update(t);
				}
			}
		}
		//为A* 算法提供地图数据
		ArrayList<ArrayList<Integer>> mapData = service.buildMapData();
		AStarMapInfo mapInfo = new AStarMapInfo();
		mapInfo.buildMap(mapData);		
		
		
        
		
		DivideHistoryInfo f = new DivideHistoryInfo();
		for(int i = 0; i < jarray.length() ; i ++){//人员集合遍历
			JSONObject obj = (JSONObject) jarray.get(i);
			String workerId = obj.getString("id");
			String areaId = obj.getString("areaId");
			//如果是紧急任务 就先分配紧急任务
			boolean hasEmergencyTask = divisionEmergencyTaskByWorker(workerId, areaId, mapInfo, "1",false);
			//然后再分配普通任务
			divisionEmergencyTaskByWorker(workerId, areaId, mapInfo, "0",hasEmergencyTask);
			//divisionByWorker(workerId, areaId, mapInfo);;
			
		}
		
		resultHM.put("code", 0);
		TaskInfo t1 = new TaskInfo();
		List<TaskInfo> tList = taskInfoDao.findAllOnCurrentDay(t1);
		resultHM.put("data", tList); 
		List<DivideHistoryInfo> dList = divideHistoryInfoDao.findAllOnCurrentDay(f);
	
		resultHM.put("data1", dList); 
		//-------------构建图表数据---------------------
		Map<String,Object> charDataMap = CharDataUtil.newBuildCharData();
		Object categoriesList = charDataMap.get("categories");
		Object seriesList = charDataMap.get("series");
		resultHM.put("categories", categoriesList);
		resultHM.put("series", seriesList);
		Integer sNum = (Integer) charDataMap.get("sNum");
		resultHM.put("sNum", sNum);
		writeToView(response, resultHM);
		System.out.println("--");
	}
	/**
	 * 为每个人员按区域分配任务
	 * @param workerId
	 * @param areaId
	 * @param mapInfo
	 */
	private void divisionByWorker(String workerId,String areaId,AStarMapInfo mapInfo){
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		if("0".equals(areaId)){
			return;
		}
		//得到属于该区域的无重复零件名称集合
		HashMap<String,String> componentMap = CalculationUtil.findComponentFromTaskByAreaId(areaId,"0");
		if(componentMap == null){
			return;
		}
		if(componentMap.size() == 0){
			return;
		}
		Map<String,List<HashMap<String,Object>>> paramList = new HashMap<String,List<HashMap<String,Object>>>();
		for (String key : componentMap.keySet()) { 
			  String componentName = key;
			  //20180509改 查工位同时 把taskid也查出来 防止出现同样的零件名称-工位名称  影响数据
			  List<HashMap<String,Object>> stationList = taskInfoDao.getStationByComponent(componentName);
				//map 结构： C01:工位1、工位2、工位3...
			  paramList.put(componentName, stationList);
		}
		ArrayList<HashMap<String,Object>> resultList = null;
		//查找该工人是否有已经完成的任务 有的话 就以最后一个完成的任务的最后一个工位作为起始点
		DivideHistoryInfo ddd = dDao.selectFinishedLastComponent(workerId);
		if(ddd != null){
			resultList = newDivideAlgo(paramList, mapInfo,ddd.getComponentName(),"0");
		}
		else{//还没有完成的任务 那就系统计算第一个点
			resultList = newDivideAlgo(paramList, mapInfo,null,"0");
		}
		
		
		for(int j = 0;j < resultList.size();j++){
			HashMap<String,Object> pHM1 = resultList.get(j);
			CalculationUtil.divitionComponentToPerson(pHM1, workerId, j+1);
		}
	}
	private ArrayList<HashMap<String,Object>> divideTest(String workerId,Map<String,List<String>> paramList,AStarMapInfo mapInfo){
		AStar aStar = new AStar();
		StationInfoDao sDao = new StationInfoDao();
		String resultJson = "";
		//创建一个用来存放待分配零件集合的队列
		ArrayList<HashMap<String,Object>> queue = new ArrayList<HashMap<String,Object>>();
		for (Map.Entry<String,List<String>> entry : paramList.entrySet()) { 
		 // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
		  String componentName = entry.getKey();//零件名称
		  List<String> stations = entry.getValue();//该了零件对应的所有工位
		  HashMap<String,Object> aMap = new HashMap<String,Object>();
		  int distanse = 0;
		  //对每个零件下的工位进行遍历
		  for(int l=0;l < stations.size();l++){
			  String stationId = stations.get(l);
			  StationInfo s = sDao.findById(stationId);
			  if(s == null){
				  System.out.println("!!the station info  is not in database : " + stationId);
				  return null;
			  }
			  String x = SysStringUtil.formatIntegerStr(s.getxAxis());
			  String y = SysStringUtil.formatIntegerStr(s.getyAxis());
			  Integer sx = Integer.parseInt(x) - 1;
			  Integer sy = Integer.parseInt(y) - 1;
			  if(l + 1 < stations.size()){
				  String eStationId = stations.get(l + 1);
				  StationInfo estation = sDao.findById(eStationId);
				  if(estation == null){
					  System.out.println("!!the station info is not in database : " + eStationId);
					  return null;
				  }
				  Integer ex = Integer.parseInt(SysStringUtil.formatIntegerStr(estation.getxAxis())) - 1;
				  Integer ey = Integer.parseInt(SysStringUtil.formatIntegerStr(estation.getyAxis())) - 1;
				  
				  Node sNode = new Node(sx,sy);
				  Node eNode = new Node(ex,ey);
				  mapInfo.setInfo(sNode, eNode);
				  //用两个相邻工位去调用A* 算法 计算距离
				  aStar.start(mapInfo);
				  distanse += eNode.G;
			  }
		  }
		  aMap.put("componentName", componentName);
		  aMap.put("distanse", distanse);
		  aMap.put("stations", stations);
		  queue.add(aMap);
		}
		System.out.println("当前队列待分配零件数量 ： " + queue.size());
		
		//以上代码计算了每个零件对应的工位的距离总代价
		
		ArrayList<HashMap<String,Object>> resultList = new ArrayList<HashMap<String,Object>>();
		//此一次分配 找出坐标最小的点对应的工位分配给这个人
		Iterator<HashMap<String,Object>> it = queue.iterator();
		int minZoomPotinIndex = 0;
		int minPointDistanse = 0;
		//**************队列元素迭代为了找到坐标最小的点位置
		Node ssNode = new Node(0,0);
		int ecount = 0;
		while(it.hasNext()){
			HashMap<String,Object> hm = it.next();
			List<String> stations = (List<String>) hm.get("stations");
			for(int i = 0;i < stations.size();i++){
				String stationName = stations.get(i);
				StationInfo sinfo = sDao.findById(stationName);
				String xstr = sinfo.getxAxis();
				String ystr = sinfo.getyAxis();
				xstr = SysStringUtil.formatIntegerStr(xstr)	;
				ystr = SysStringUtil.formatIntegerStr(ystr)	;
				Node eNode = new Node(Integer.parseInt(xstr) - 1,Integer.parseInt(ystr) - 1);
				mapInfo.setInfo(ssNode, eNode);
				aStar.start(mapInfo);
				Integer distanse_1 = eNode.G;//起始位置和终止位置的距离
				if(minPointDistanse == 0){
					minPointDistanse = distanse_1;
				}
				else{
					if(distanse_1 < minPointDistanse){
						minPointDistanse = distanse_1;
						minZoomPotinIndex = ecount;
					}
				}
			}
			ecount++;
		}
		//找最小点方法没确定 先当做第一个点
		//*********************找到最小的点位置结束
		Iterator<HashMap<String,Object>> itt = queue.iterator();
		int count = 0;//记录queue 循环次数
		//这次的循环目的就是根据上面的minZoomPotinIndex 结果 取出queue中的元素分配给这个人 并且从queue中删除
		while(itt.hasNext()){
			HashMap<String,Object> hm_1 = itt.next();
			if(count == minZoomPotinIndex){
				itt.remove();
				resultList.add(hm_1);
				break;
			}
			count++;
		}
		//************第一个点确定了之后 下面开始正式分配***********************
		while(queue.size() > 0){
			//拿到当前的这个人分配的最新的一个零件
			HashMap<String,Object> hm = resultList.get(resultList.size() - 1);
			//拿到当前零件对应的所有工位集合
			List<String> stations = (List<String>) hm.get("stations");
			//从工位集合中取出最后一个工位
			String startStationId = stations.get(stations.size() - 1);
			//通过dao查出该工位坐标信息
			StationInfo sStationInfo = sDao.findById(startStationId);
			System.out.println(" 起始点id：" + startStationId);
			Node sNode = new Node(Integer.parseInt(sStationInfo.getxAxis()) - 1,Integer.parseInt(sStationInfo.getyAxis()) - 1);
			//当前人员要走的总距离
			Integer distanse = (Integer) hm.get("distanse");
			Integer minDistanse_1 = 0;
			Integer minDistanse_2 = 0;//20180418后加 只比对起始零件内部距离 + 起止距离  不加 终止零件内部距离 但最终计算总距离需要加上 所以minDistanse_1保留
			Integer minDistanseQueueIndex = 0;//路程最小的点再queue中的位置
			for(int i = 0;i < queue.size();i++){
				HashMap<String,Object> cInfo = queue.get(i);
				ArrayList<String> eStations = (ArrayList<String>) cInfo.get("stations");
				//得到这个零件的第一个工位id
				String eStationId = eStations.get(0);
				System.out.println(" 终止点id：" + eStationId);
				StationInfo eStationInfo = sDao.findById(eStationId);//通过dao查出该工位坐标信息
				Integer distanse1 = (Integer) cInfo.get("distanse");//零件内部工位总距离
				Node eNode = new Node(Integer.parseInt(SysStringUtil.formatIntegerStr(eStationInfo.getxAxis())) - 1,Integer.parseInt(SysStringUtil.formatIntegerStr(eStationInfo.getyAxis())) - 1);
				mapInfo.setInfo(sNode, eNode);
				aStar.start(mapInfo);
				Integer distanse_1 = eNode.G;//起始位置和终止位置的距离
				if(minDistanse_1 == 0){
					minDistanse_1 = distanse + distanse_1 + distanse1;
					minDistanse_2 = distanse + distanse_1;
				}
				else{
					if(distanse_1 + distanse < minDistanse_2){
						minDistanse_1 = distanse + distanse_1 + distanse1;
						minDistanse_2 = distanse_1 + distanse;
						minDistanseQueueIndex = i;
					}
				}
			}
			Iterator<HashMap<String,Object>> ittt = queue.iterator();
			int count1 = 0;//记录queue 循环次数
			//这次的循环目的就是根据上面的minDistanseQueueIndex 结果 取出queue中的元素分配给这个人 并且从queue中删除
			while(ittt.hasNext()){
				HashMap<String,Object> hm_1 = ittt.next();
				if(count1 == minDistanseQueueIndex){
					ittt.remove();
					hm_1.put("distanse", distanse + minDistanse_1);
					resultList.add(hm_1);
					break;
				}
				count1++;
			}
			
		}
		
		return resultList;
	}
	
	/**
	 * 新的分配算法
	 * @param workerId
	 * @param firstStation
	 * @param paramList
	 * @param mapInfo
	 * @return
	 */
	private ArrayList<HashMap<String,Object>> newDivideAlgo(Map<String,List<HashMap<String,Object>>> paramList,AStarMapInfo mapInfo,String preComponentName,String startPointFlag){
		//创建一个用来存放待分配零件集合的队列
		ArrayList<HashMap<String,Object>> queue = service.createQueue(paramList);
		//
		int count = 0;
		ArrayList<HashMap<String,Object>> newQueue = new ArrayList<HashMap<String,Object>>();
		while(queue.size() > 0){//队列中有元素就继续
			if(count == 0){
				HashMap<String,Object> element = new HashMap<String,Object>();
				
				if(preComponentName != null){//说明刚刚分配完紧急任务 preComponentName是最后一个分完的紧急任务零件  需要继续分配普通任务
					DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
					DivideHistoryInfo dInfo = new DivideHistoryInfo();
					dInfo.setComponentName(preComponentName);
					dInfo = dDao.findOneByParamToday(dInfo);//将这个分配结果查出来
					String stations = dInfo.getStationNames();//工位集合
					String ids = dInfo.getTaskIds();
					List<HashMap<String,Object>> stationList = new ArrayList<HashMap<String,Object>>();
					String[] stationArray = stations.split(",");
					String[] idArray = ids.split(",");
					for(int c = 0;c < stationArray.length;c++){
						HashMap<String,Object> sMap = new HashMap<String,Object>();
						sMap.put("stationName", stationArray[c]);
						sMap.put("id", idArray[c]);
						stationList.add(sMap);
					}
					element.put("componentName", preComponentName);
					element.put("stations", stationList);
					element.put("basicStationName", stationList.get(stationList.size() - 1));
				}
				else{
					//count == 0 时是查找第一个工位点 这个工位点确定之后就可以以它为基准一直分配下去
					//注意此时第一个零件已经从queue中移除 element 中包含基准点 和该零件工位集合
					element = service.findNextComponent(null,queue, mapInfo,startPointFlag);
					service.sortComponentStations(element,mapInfo);
				}
				newQueue.add(element);//将这个零件工位排好序的对象存到新的队列里面
			}
			else{
				System.out.println("count:" + count + "---------------------------");
				//newqueue 中最后一个零件 即为当前需要对比的上一个零件
				HashMap<String,Object> preElement = newQueue.get(newQueue.size() - 1);
				//newqueue 中的零件工位信息都是排好序的
				List<HashMap<String,Object>> stations = (List<HashMap<String,Object>>) preElement.get("stations");
				//用上一个零件的最后一个工位点 做为本次参照的基准点
				HashMap<String,Object> sMap = stations.get(stations.size() - 1);
				String basicStationName = (String) sMap.get("stationName");
				HashMap<String,Object> element = service.findNextComponent(basicStationName,queue, mapInfo,startPointFlag);
				service.sortComponentStations(element,mapInfo);
				newQueue.add(element);//将这个零件工位排好序的对象存到新的队列里面
			}
			count++;
		}
		//setTotalDistanse(newQueue,mapInfo);
		return newQueue;
	}
	/**
	 * 计算出总距离
	 */
	private void setTotalDistanse(ArrayList<HashMap<String,Object>> newQueue,AStarMapInfo mapInfo){
		Integer totalDistanse = 0;
		AStar aStar = new AStar();
		StationInfoDao sDao = new StationInfoDao();
		for(int i = 0;i < newQueue.size();i++){
			HashMap<String,Object> hm = newQueue.get(i);
			List<HashMap<String,Object>> sList = (List<HashMap<String,Object>>) hm.get("stations");
			if(i != newQueue.size() - 1){//计算零件间距离
				HashMap<String,Object> hm1 = newQueue.get(i + 1);
				List<HashMap<String,Object>> eList = (List<HashMap<String,Object>>) hm1.get("stations");
				HashMap<String,Object> sMap = sList.get(sList.size() - 1);//获得前一个零件最后一个工位
				String prestationName = (String) sMap.get("stationName");
				HashMap<String,Object> eMap = eList.get(0);//获得前一个零件最后一个工位
				String nexstationName = (String) eMap.get("stationName");
				StationInfo station1 = sDao.findById(prestationName);
				StationInfo station2 = sDao.findById(nexstationName);
				String sxStr = station1.getxAxis();
				String syStr = station1.getyAxis();
				sxStr = SysStringUtil.formatIntegerStr(sxStr);
				syStr = SysStringUtil.formatIntegerStr(syStr);
				Integer sx = Integer.parseInt(sxStr) - 1;
				Integer sy = Integer.parseInt(syStr) - 1;
				Node sNode = new Node(sx,sy);
				
				String exStr = station2.getxAxis();
				String eyStr = station2.getyAxis();
				exStr = SysStringUtil.formatIntegerStr(exStr);
				eyStr = SysStringUtil.formatIntegerStr(eyStr);
				Integer ex = Integer.parseInt(exStr) - 1;
				Integer ey = Integer.parseInt(eyStr) - 1;
				Node eNode = new Node(ex,ey);
				mapInfo.setInfo(sNode, eNode);
				aStar.start(mapInfo);
				Integer distanse = eNode.G;//起始位置和终止位置的距离
				totalDistanse += distanse;
			}
			for(int j = 0;j < sList.size();j++){
				if(j != sList.size() - 1){//计算每个零件内距离
					HashMap<String,Object> sMap = sList.get(j);
					HashMap<String,Object> eMap = sList.get(j + 1);
					String stationName1 = (String) sMap.get("stationName");
					String stationName2 = (String) eMap.get("stationName");
					StationInfo station1 = sDao.findById(stationName1);
					StationInfo station2 = sDao.findById(stationName2);
					String sxStr = station1.getxAxis();
					String syStr = station1.getyAxis();
					sxStr = SysStringUtil.formatIntegerStr(sxStr);
					syStr = SysStringUtil.formatIntegerStr(syStr);
					Integer sx = Integer.parseInt(sxStr) - 1;
					Integer sy = Integer.parseInt(syStr) - 1;
					Node sNode = new Node(sx,sy);
					
					String exStr = station2.getxAxis();
					String eyStr = station2.getyAxis();
					exStr = SysStringUtil.formatIntegerStr(exStr);
					eyStr = SysStringUtil.formatIntegerStr(eyStr);
					Integer ex = Integer.parseInt(exStr) - 1;
					Integer ey = Integer.parseInt(eyStr) - 1;
					Node eNode = new Node(ex,ey);
					mapInfo.setInfo(sNode, eNode);
					aStar.start(mapInfo);
					Integer distanse = eNode.G;//起始位置和终止位置的距离
					totalDistanse += distanse;
				}
			}
			
		}
		newQueue.get(newQueue.size() - 1).put("distanse", totalDistanse);
	}
	
	
	
	
	/**
	 * 分配任务算法java重写版 2018-04-08 lizc
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void submitScheme_003(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		String workersJson = request.getParameter("workersJson");
		JSONArray jarray = JSONArray.fromObject(workersJson);
		List<DivideHistoryInfo> ddList = dDao.findUnfinishOnCurrentDay();
		if(ddList.size() > 0){
			dDao.deleteCurrentData();
			TaskInfo tParam1 = new TaskInfo();
			tParam1.setState(1);
			//将之前更新为已分配状态的任务 复原回未分配
			//20180418修改 只回复未完成的任务 已完成的要排除
			//List<TaskInfo> tList_1 = taskInfoDao.findAllOnCurrentDay(tParam1);
			List<TaskInfo> tList_1 = taskInfoDao.findCurrentNotFinishList();
			if(tList_1 != null){
				for(int i = 0;i < tList_1.size();i++){
					TaskInfo t = tList_1.get(i);
					t.setState(0);
					t.setWorkerId(null);
					taskInfoDao.update(t);
				}
			}
		}
		
		ArrayList<ArrayList<Integer>> mapData = service.buildMapData();
		AStarMapInfo mapInfo = new AStarMapInfo();
		mapInfo.buildMap(mapData);		
		service.divisionTask_003(jarray, mapInfo, "1");
		service.divisionTask_003(jarray, mapInfo, "0");
		
		
		HashMap<String,Object> resultHM = new HashMap<String,Object>();
		resultHM.put("code", 0);
		TaskInfo t1 = new TaskInfo();
		List<TaskInfo> tList = taskInfoDao.findAllOnCurrentDay(t1);
		resultHM.put("data", tList); 
		List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(null);
	
		resultHM.put("data1", dList); 
		//-------------构建图表数据---------------------
		Map<String,Object> charDataMap = CharDataUtil.newBuildCharData();
		Object categoriesList = charDataMap.get("categories");
		Object seriesList = charDataMap.get("series");
		resultHM.put("categories", categoriesList);
		resultHM.put("series", seriesList);
		Integer sNum = (Integer) charDataMap.get("sNum");
		resultHM.put("sNum", sNum);
		writeToView(response, resultHM);
	}
	
	/**
	 * 将python返回的结果的序号更新到数据库
	 * @param dList
	 * @param findPathJson
	 * @param personLMap
	 */
	private void updateOrderNumFromPython(List<DivideHistoryInfo> dList,String findPathJson,Map<String,Object> personLMap){
		JSONObject jObj = JSONObject.fromObject(findPathJson);
		Iterator iterator = jObj.keys();//获取返回json对象所有的key
		TaskInfoDao dao = new TaskInfoDao();
		DivideHistoryInfoDao dao1 = new DivideHistoryInfoDao();
		while(iterator.hasNext()){
		     String key = (String) iterator.next();
		     String value = jObj.getString(key);
		     //这个数组的最后一个元素就是最终的顺序
		     /**
		      * 比如这个数组最后一个元素为P00004工人的[5,3,2,4,1](数组的每一个元素还是一个数组类型)
		      * 那么从personLMap中找到该P00004员工的工位集合：比如为05_001_2,05_001_3,05_001_4,05_001_5,05_001_6
		      * 那么05_001_6这个工位代表的零件的序号就是1同理05_001_4是2，05_001_3是3，05_001_5是4，05_001_2是5
		      * 将工位对应的零件编号的顺序更新到divide_history_info表中
		      */
		     JSONArray jr1 = JSONArray.fromString(value);
		     JSONArray lastPath =  (JSONArray) jr1.get(jr1.length() - 1);
		     ArrayList<String> stations  = (ArrayList<String>) personLMap.get(key);
		     for(int i = 0;i < lastPath.length();i++){
		    	 int order = i + 1;//从1开始 所以+1
		    	 Integer index = lastPath.getInt(i);//这个是为了从stations拿到指定位置的工位
		    	 if(index < stations.size()){
		    		 String stationName = stations.get(index);
			    	 TaskInfo t = new TaskInfo();
			    	 t.setStationName(stationName);
			    	 List<TaskInfo> tList = dao.findAllOnCurrentDay(t);
			    	 if(tList.size() > 0){
			    		 String componentName = tList.get(0).getComponentName();
			    		 for(int k = 0;k < dList.size();k++){
			    			 DivideHistoryInfo d = dList.get(k);
			    			 if(d.getComponentName().equals(componentName)){
			    				 d.setOrderNum(order);
			    				 dao1.update(d);
			    			 }
			    		 }
			    	 }
		    	 }
		    	 
		     }
		}
	}
	/**
	 * 改变状态
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void changeState(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String resultJson = new SmartCCIndexService().changeState(request);
        writeToView(response, resultJson);
	}
	public void deleteDivision(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DivideHistoryInfoDao dao = new DivideHistoryInfoDao();
		WorkerInfoDao wDao = new WorkerInfoDao();
		TaskInfoDao tDao = new TaskInfoDao();
		String dataJson = request.getParameter("dataJson");
		JSONArray jarray = JSONArray.fromObject(dataJson);
		for(int l = 0;l < jarray.length();l++){
			JSONObject obj = (JSONObject) jarray.get(l);
			String id = obj.getString("id");
	        String taskIds = obj.getString("taskIds");
	        Integer state = obj.getInt("state");
	        if(state != 3){//只能删除未完成状态的结果
	        	if(dao.deleteByID(id)){
	        		//将对应的这些任务表数据状态也删除
		        	String[] taskidArray = taskIds.split(",");
		        	for(int i = 0;i < taskidArray.length;i++){
		        		tDao.deleteByID(taskidArray[i]);
		        	}
	        	}
	        }
		}
        
        TaskInfo param = new TaskInfo();
        List<TaskInfo> data = tDao.findAllOnCurrentDay(param);
        List<DivideHistoryInfo> data1 = dao.findAllOnCurrentDay(null);
        HashMap<String,Object> resultHM = new HashMap<String,Object>();
      //-------------构建图表数据---------------------
		Map<String,Object> charDataMap = CharDataUtil.newBuildCharData();
		Object categoriesList = charDataMap.get("categories");
		Object seriesList = charDataMap.get("series");
		
		
		List<WorkerInfo> workerList = wDao.findAll();
		resultHM.put("categories", categoriesList);
		resultHM.put("series", seriesList);
        resultHM.put("code", 0);
        resultHM.put("data", data);
        resultHM.put("data1", data1);
        resultHM.put("data2", workerList);
        Integer sNum = (Integer) charDataMap.get("sNum");
        resultHM.put("sNum", sNum);
        writeToView(response, resultHM);
	}
	/**
	 * 查询分配结果
	 */
	public void searchDivideResult(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
		Integer state = 0;
		String searchWorker = request.getParameter("searchWorker");
		String searchState = request.getParameter("searchState");
		if(searchState != null && !"".equals(searchState)){
			if(!searchState.trim().equals("Choose one")){
				state = Integer.parseInt(searchState);
			}
			
		}
		DivideHistoryInfoDao dao = new DivideHistoryInfoDao();
		DivideHistoryInfo d = new DivideHistoryInfo();
		d.setWorkerId(searchWorker);
		d.setState(state);
		List<DivideHistoryInfo> list = dao.findAllOnCurrentDay(d);
		Map<String,Object> resultHM = new HashMap<String,Object>();
		resultHM.put("code", 0);
        resultHM.put("data", list);
        String resultJson = JSONObject.fromObject(resultHM).toString();
        out.write(resultJson);
        out.flush();
        out.close();
	}
	/**
	 * 导出分配结果excel
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void ExportDivideHistoryInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		DivideHistoryInfoDao dao = new DivideHistoryInfoDao();
		String searchWorker = request.getParameter("searchWorker");
		String searchState = request.getParameter("searchState");
		HashMap<String,Object> param = new HashMap<String,Object>();
		param.put("workerId", searchWorker);
		if(!StringUtils.isEmpty(searchState)){
			if(!searchState.trim().equals("Choose one")){
				param.put("state", searchState);
			}
		}
		
		List<HashMap<String,Object>> dataList = dao.selectMapByParam(param);
		HashMap<String,Object> title1 = new HashMap<String,Object>();
		title1.put("label", "Staff No.");
		title1.put("property", "workerId");
		HashMap<String,Object> title2 = new HashMap<String,Object>();
		title2.put("label", "Component");
		title2.put("property", "componentName");
		HashMap<String,Object> title3 = new HashMap<String,Object>();
		title3.put("label", "Order");
		title3.put("property", "orderNum");
		HashMap<String,Object> title4 = new HashMap<String,Object>();
		title4.put("label", "Part station");
		title4.put("property", "stationNames");
		HashMap<String,Object> title5 = new HashMap<String,Object>();
		title5.put("label", "Status");
		title5.put("property", "state");
		
		List<Object> titleArray = new ArrayList<Object>();
		titleArray.add(title1);
		titleArray.add(title2);
		titleArray.add(title3);
		titleArray.add(title4);
		titleArray.add(title5);
		String titleJson = JSONArray.fromObject(titleArray).toString();
		ExcelUtil.exportExcel(request, response, "distribution_result.xls", titleJson, dataList);
	}
	/**
	 * 加载人员列表
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void loadWorkers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String searchGroup = request.getParameter("searchGroup");
		
		WorkerInfo wi = new WorkerInfo();
		if(!StringUtils.isEmpty(searchGroup)){
			if(!"Choose one".equals(searchGroup.trim())){
				wi.setGroupId(searchGroup);
			}
		}
		String loadType = request.getParameter("loadType");
		response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
		WorkerInfoDao dao = new WorkerInfoDao();
		List<WorkerInfo> list = dao.findAll(wi);
		WorkerInfo w = new WorkerInfo();
		w.setId("");
		w.setName("Choose one");
		if(!"datagrid".equals(loadType)){
			list.add(0, w);
		}
		
		String resultJson = JSONArray.fromObject(list).toString();
        out.write(resultJson);
        out.flush();
        out.close();
	}
	/**
	 * 获取演示寻径过程数据调用此函数
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void viewPath(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		SmartCCIndexService service = new SmartCCIndexService();
		ArrayList<ArrayList<Integer>> mapData = service.buildMapData();
		//构建地图信息结束-------------------------
		
		//构建人员任务分配结果json----------------------
		List<HashMap<String,Object>> pList = service.selectCurrentTaskStation();//buildViewPathData();
		//构建人员分配结果json结束-----------------------
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("code","0");
		result.put("mapData",mapData);
		result.put("pList",pList);
		String resultJson = JSONObject.fromObject(result).toString();
		System.out.println(resultJson);
		writeToView(response, resultJson);
	}
	private ArrayList<HashMap<String,Object>> buildViewPathData(){
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		StationInfoDao sDao = new StationInfoDao();
		ArrayList<DivideHistoryInfo> dList = (ArrayList<DivideHistoryInfo>) dDao.findAllOnCurrentDay(new DivideHistoryInfo());
		Map<String,String> idMap = new LinkedHashMap<String,String>();
		for(int i = 0;i < dList.size();i++){
			idMap.put(dList.get(i).getWorkerId(), dList.get(i).getWorkerId());
		}
		ArrayList<HashMap<String,Object>> pList = new ArrayList<HashMap<String,Object>>();
		for (Map.Entry<String, String> entry : idMap.entrySet()) {  
		    String workerId = entry.getKey(); 
		    ArrayList<HashMap<String,Object>> stationList = new ArrayList<HashMap<String,Object>>();
		    for(int i = 0;i < dList.size();i++){
		    	DivideHistoryInfo d = dList.get(i);
		    	String wId = d.getWorkerId();
		    	if(wId.equals(workerId)){
		    		String stationNames = d.getStationNames();
		    		String[] sArray = stationNames.split(",");
		    		String stationName = sArray[0];
		    		StationInfo sinfo = sDao.findById(stationName);
		    		String x = sinfo.getxAxis();
		    		String y = sinfo.getyAxis();
		    		HashMap<String,Object> pInfo = new HashMap<String,Object>();
		    		pInfo.put("name", stationName);
		    		pInfo.put("x", Integer.parseInt(x));
		    		pInfo.put("y", Integer.parseInt(y));
		    		stationList.add(pInfo);
		    	}
		    }
		    HashMap<String,Object> pMap = new HashMap<String,Object>();
		    pMap.put(workerId, stationList);
		    pList.add(pMap);
		}  
		return pList;
	}
	/**
	 * 保存最优路径的步数
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void saveBestPath(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String stepsStr = request.getParameter("steps");
		String stepsPoint = request.getParameter("stepsPoint");
		
		String stepsPoint1 = request.getParameter("stepsPoint1");
		String stepsPoint2 = request.getParameter("stepsPoint2");
		String stepsPoint3 = request.getParameter("stepsPoint3");
		String stepsPoint4 = request.getParameter("stepsPoint4");
		String stepsPoint5 = request.getParameter("stepsPoint5");
		String stepsPoint6 = request.getParameter("stepsPoint6");
		request.getSession().setAttribute("stepsPoint", stepsPoint);
		
		request.getSession().setAttribute("stepsPoint1", stepsPoint1);
		request.getSession().setAttribute("stepsPoint2", stepsPoint2);
		request.getSession().setAttribute("stepsPoint3", stepsPoint3);
		request.getSession().setAttribute("stepsPoint4", stepsPoint4);
		request.getSession().setAttribute("stepsPoint5", stepsPoint5);
		request.getSession().setAttribute("stepsPoint6", stepsPoint6);
		Integer steps = Integer.parseInt(stepsStr);
		TaskWalkLengthDao twDao = new TaskWalkLengthDao();
		TaskWalkLength tw = new TaskWalkLength();
		tw.setId(UUID.randomUUID().toString());
		tw.setState(1);
		tw.setWorkerId("ALL");
		tw.setDistanse(steps);
		tw.setCreateTime(new Date());
		twDao.insert(tw);
		System.out.println(steps);
	}
	
	
	
	
	/**
	 * 为每个人员按区域分配任务
	 * @param workerId
	 * @param areaId
	 * @param mapInfo
	 */
	private boolean divisionEmergencyTaskByWorker(String workerId,String areaId,AStarMapInfo mapInfo,String isEmergency,boolean hasEmergencyTask){
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		if("0".equals(areaId)){
			return false;
		}
		//得到属于该区域的无重复零件名称集合
		HashMap<String,String> componentMap = CalculationUtil.findComponentFromTaskByAreaId(areaId, isEmergency);
		if(componentMap == null){
			return false;
		}
		if(componentMap.size() == 0){
			return false;
		}
		Map<String,List<HashMap<String,Object>>> paramList = new HashMap<String,List<HashMap<String,Object>>>();
		for (String key : componentMap.keySet()) { 
			  String componentName = key;
			  List<HashMap<String,Object>> stationList = taskInfoDao.getStationByComponent(componentName);
				//map 结构： C01:工位1、工位2、工位3...
			  paramList.put(componentName, stationList);
		} 
		String preComponentName = null;
		Integer maxIndex = -1;
		
		boolean isHasPre = false;
		DivideHistoryInfo dInfo = dDao.selectLastComponent(workerId);
		ArrayList<ArrayList<Integer>> mapData = service.buildMapData();
		String b = "0";
		if(dInfo != null){
			String resultStr = judgeIsHasPre(mapData, dInfo, paramList);
			String a = resultStr.substring(0,1);//是否以上一个点为起始点
			b = resultStr.substring(1,2);//以左上角起始还是右上角起始
			//如果之前有紧急任务 最大序号应该从最后一个紧急任务开始 + 1
			maxIndex = dInfo.getOrderNum() + 1;
			
			if("1".equals(a)){
				isHasPre = true;
				preComponentName = dInfo.getComponentName();
			}
			
		}
		ArrayList<HashMap<String,Object>> resultList = newDivideAlgo(paramList, mapInfo,preComponentName,b);
		//说明本次分配的第一个点是已经分配过的紧急任务 不需要在分配  只是做参考点 
		//hasEmergencyTask:在执行此方法之前是分配紧急任务 如果这个人分配过紧急任务 那么hasEmergencyTask 就是true
		if(isHasPre){
			resultList.remove(0);
		}
		//ArrayList<HashMap<String,Object>> resultList = divideTest(workerId, paramList, mapInfo);
		HashMap<String,Object> pHM = resultList.get(resultList.size() - 1);//最后一个零件任务的距离就是该工人的总距离
		for(int j = 0;j < resultList.size();j++){
			HashMap<String,Object> pHM1 = resultList.get(j);
			if(maxIndex != -1){
				CalculationUtil.divitionComponentToPerson(pHM1, workerId, maxIndex);
				maxIndex ++;
			}
			else{
				CalculationUtil.divitionComponentToPerson(pHM1, workerId, j + 1);
			}
			
		}
		return true;
	}
	/**
	 * 判断是否将上一个已完成或紧急任务作为起始点
	 * 目的是为了避免盲目找距离上个完成任务最近的点而造成漏点而折返的情况
	 * @param mapData
	 * @param dInfo
	 * @param paramList
	 * @return
	 */
	private String judgeIsHasPre(ArrayList<ArrayList<Integer>> mapData,DivideHistoryInfo dInfo,Map<String,List<HashMap<String,Object>>> paramList){
		if(dInfo == null){
			return "00";
		}
		StationInfo ls = new StationInfo(1,1);//地图左上角点
		StationInfo rs = new StationInfo(267,385);//地图右下角点
		StationInfoDao sDao = new StationInfoDao();
		String stationNames = dInfo.getStationNames();
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
		for (Map.Entry<String,List<HashMap<String,Object>>> entry : paramList.entrySet()) { 
			 // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
			  List<HashMap<String,Object>> stations = entry.getValue();//该了零件对应的所有工位
			  for(int i = 0;i < stations.size();i++){
				  HashMap<String,Object> stationMap = stations.get(i);
				  String stationName = (String) stationMap.get("stationName");
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
	public void loadBatchNos (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		TaskInfoDao tDao = new TaskInfoDao();
		List<HashMap<String,Object>> list = tDao.selectBatchNos();
		
		HashMap<String,Object> chooseone = new HashMap<String,Object>();
		chooseone.put("batchNo", "Choose one");
		//if(!"datagrid".equals(loadType)){
			list.add(0, chooseone);
		//}
		
		String resultJson = JSONArray.fromObject(list).toString();
        writeToView(response, resultJson);
	}
	public void searchTaskInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		TaskInfoDao tDao = new TaskInfoDao();
		String searchBatchNo = request.getParameter("searchBatchNo");
		String searchType = request.getParameter("searchType");
		TaskInfo param = new TaskInfo();
		if(!StringUtils.isEmpty(searchBatchNo)){
			if(!searchBatchNo.trim().equals("Choose one")){
				param.setBatchNo(searchBatchNo);
			}
		}
		if(!StringUtils.isEmpty(searchType)){
			if(!searchType.trim().equals("Choose one")){
				param.setTaskType(Integer.parseInt(searchType));
			}
		}
		List<TaskInfo> tList = tDao.findAllOnCurrentDay(param);
		HashMap<String,Object> result = new HashMap<String,Object>();
		result.put("code", 0);
		result.put("data", tList);
		writeToView(response, result);
	}
}
