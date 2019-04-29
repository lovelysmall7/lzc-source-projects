package com.lzc.demo.service.index;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.StringUtils;

import com.lzc.demo.dao.AreaInfoDao;
import com.lzc.demo.dao.ComponentStationMappingDao;
import com.lzc.demo.dao.DivideHistoryInfoDao;
import com.lzc.demo.dao.StationInfoDao;
import com.lzc.demo.dao.TaskInfoDao;
import com.lzc.demo.dao.TaskWalkLengthDao;
import com.lzc.demo.dao.TaskWalkLengthDao1;
import com.lzc.demo.dao.WorkerInfoDao;
import com.lzc.demo.model.AreaInfo;
import com.lzc.demo.model.ComponentStationMapping;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.model.StationInfo;
import com.lzc.demo.model.TaskInfo;
import com.lzc.demo.model.TaskWalkLength;
import com.lzc.demo.model.TaskWalkLength1;
import com.lzc.demo.model.WorkerInfo;
import com.lzc.demo.util.CalculationUtil;
import com.lzc.demo.util.CharDataUtil;
import com.lzc.demo.util.ExcelHelper;
import com.lzc.demo.util.PropertiesUtil;
import com.lzc.demo.util.PythonUtil;
import com.lzc.demo.util.SysStringUtil;
import com.lzc.demo.util.Astar.AStar;
import com.lzc.demo.util.Astar.AStarMapInfo;
import com.lzc.demo.util.Astar.Node;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SmartCCIndexService {
	/**
	 * 
	 * 单个/批量更改零件任务的状态
	 * @param request
	 * @return
	 */
	public String changeState(HttpServletRequest request){
		String searchWorker = request.getParameter("searchWorker");
		String searchState = request.getParameter("searchState");
		DivideHistoryInfoDao dao = new DivideHistoryInfoDao();
		WorkerInfoDao wDao = new WorkerInfoDao();
		TaskInfoDao tDao = new TaskInfoDao();
		String dataJson = request.getParameter("dataJson");
		JSONArray jarray = JSONArray.fromObject(dataJson);
		for(int l = 0;l < jarray.length();l++){
			JSONObject obj = (JSONObject) jarray.get(l);
			String id = obj.getString("id");
			String stationIds = obj.getString("stationIds");
			if(StringUtils.isEmpty(stationIds)){
				continue;
			}
	        String taskIds = obj.getString("taskIds");
	        String state = obj.getString("state");
	        String workerId = obj.getString("workerId");
	        //查出该工人信息
	        WorkerInfo workerinfo = wDao.findById(workerId);
	        DivideHistoryInfo d = dao.findById(id);
	        boolean flag = false;
	        if("1".equals(state)){
	        	if(d.getState() != 2){
	        		d.setState(2);
	        		flag = true;
	        	}
	        	 
	        }
	        else{
	        	if(d.getState() != 3){
	        		//说明是已完成 需要将该工人的当前所在工位信息进行更新
		        	String stationNames = d.getStationNames();//获得这个工人完成这个工作需要经过的工位集合（带有顺序信息）
		        	if(StringUtils.isEmpty(stationNames)){
		        		stationNames =  d.getStationIds();
		        	}
		        	if(stationNames != null){
		        		/*String[] stationArray = stationNames.split(",");
		        		if(stationArray.length > 0){
		        			//获得当前工位
		        			String currentStation = stationArray[stationArray.length - 1];
		        			workerinfo.setCurrentStation(currentStation);
		        			wDao.update(workerinfo);//将这个工人的当前工位信息更新
		        		}*/
		        	}
		        	updateWorkerDistanse(workerId,stationNames);//更新该工人行走的距离
		        	d.setState(3);
		        	flag = true;
	        	}
	        	
	        }
	        if(flag){
	        	 //更新任务分配记录表状态
		        if(dao.update(d)){
		        	
		        	//将对应的这些任务表数据状态也更新
		        	String[] taskidArray = taskIds.split(",");
		        	for(int i = 0;i < taskidArray.length;i++){
		        		TaskInfo t = tDao.findById(taskidArray[i]);
		        		t.setState(d.getState());
		        		tDao.update(t);
		        	}
		        }
	        }
	       
		}
        
        TaskInfo param = new TaskInfo();
        List<TaskInfo> data = tDao.findAllOnCurrentDay(param);
        DivideHistoryInfo dInfo = new DivideHistoryInfo();
        dInfo.setWorkerId(searchWorker);
        if(!StringUtils.isEmpty(searchState)){
        	if(!searchState.trim().equals("Choose one")){
        		 dInfo.setState(Integer.parseInt(searchState));
        	}
        }
       
        List<DivideHistoryInfo> data1 = dao.findAllOnCurrentDay(dInfo);
        Map<String,Object> resultHM = new HashMap<String,Object>();
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
        String resultJson = JSONObject.fromObject(resultHM).toString();
        return resultJson;
	}
	/**
	 * 更新该工人当前行走的路程信息
	 */
	public void updateWorkerDistanse(String workerId,String currentTaskStations){
		ArrayList<ArrayList<Integer>> mapData = buildMapData();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		DivideHistoryInfo dInfo = dDao.selectFinishedLastComponent(workerId);
		String startStation = "";
		ArrayList<String> stationList = new ArrayList<String>();
		if(dInfo != null){
			String stations = dInfo.getStationNames();//获得最后一个任务的所有工位
			String[] stationArr = stations.split(",");
			startStation = stationArr[stationArr.length - 1];
			stationList.add(startStation);
		}
		
		String[] currentTaskStationsArr = currentTaskStations.split(",");
		for(int i = 0;i < currentTaskStationsArr.length;i++){
			stationList.add(currentTaskStationsArr[i]);
		}
		Integer distanse = 0;
		for(int i = 0;i < stationList.size();i++){
			if(i != stationList.size() - 1){
				distanse += CalculationUtil.getDistanse(mapData, stationList.get(i), stationList.get(i+1));
			}
			
		}
		TaskWalkLengthDao1 twDao = new TaskWalkLengthDao1();
		TaskWalkLength1 tw1 = new TaskWalkLength1();
		tw1.setWorkerId(workerId);
		List<TaskWalkLength1> twList = twDao.findAllOnCurrentDay(tw1);
		if(twList.size() > 0){
			tw1 = twList.get(0);
			tw1.setDistanse(tw1.getDistanse() + distanse);
			double dMeter = tw1.getDistanse() * 1.8; 
			dMeter = new BigDecimal(dMeter).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
			tw1.setDistanseByMeter(dMeter);
			twDao.update(tw1);
		}
		else{
			tw1.setId(UUID.randomUUID().toString());
			tw1.setCreateTime(new Date());
			tw1.setDistanse(distanse);
			double dMeter = tw1.getDistanse() * 1.8; 
			dMeter = new BigDecimal(dMeter).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
			tw1.setDistanseByMeter(dMeter);
			twDao.insert(tw1);
		}
	}
	/**
	 * 添加临时紧急任务
	 * @param request
	 * @return
	 */
	public String uploadEmergencyTaskExcel(FileItem item,String isEmergency,String component){
		String batchNo = SysStringUtil.createBatchNo();//每次上传任务生成一个批次号
		System.out.println("handle a request for post");
        WorkerInfoDao wDao = new WorkerInfoDao();
        TaskInfoDao tDao = new TaskInfoDao();
        ExcelHelper helper = new ExcelHelper();
        HashMap<String,Object> result = new HashMap<String,Object>();
        try {
           
	           //插入一条紧急临时任务
                if(!"".equals(component) && component!= null){
                	ComponentStationMappingDao csDao = new ComponentStationMappingDao();
                	//根据零件工位映射关系 把改零件的所有工位查出来 每次组合作为一个任务
                	List<String> csList = csDao.findStationByComponent(component);
                	for(int i = 0;i < csList.size();i++){
                		TaskInfo t = new TaskInfo();
                    	t.setId(UUID.randomUUID().toString());
                    	t.setComponentName(component);
                    	t.setStationId(csList.get(i));
                    	t.setStationName(csList.get(i));
                    	t.setCreateTime(new Date());
                    	t.setCheckResult(-1);
                    	t.setTaskType(Integer.parseInt(isEmergency));
                    	t.setState(0);
                    	t.setIsEmergency(Integer.parseInt(isEmergency) );
                    	int maxTaskNo = tDao.selectMaxTaskNum();
                    	maxTaskNo = maxTaskNo + 1;
                    	t.setTaskNo(maxTaskNo + "");
                    	t.setOrderNum(maxTaskNo);
                    	t.setBatchNo(batchNo);
                    	tDao.insert(t);
                	}
                	
                }
                if(item != null){
                	 if (item.getName().endsWith(".xls")||item.getName().endsWith(".xlsx")) {
 	                    // 说明是文件,不过这里最好限制一下
 	                    List<TaskInfo> dataList = helper.analysis(item.getInputStream(),"open",isEmergency,batchNo);
 	                    result.put("data", dataList);
 	                    wDao.initAllWorkerStation();
 	                    List<WorkerInfo> wList = wDao.findAll();
 	                    result.put("wList", wList);
 	                    
 	                } else {
 	                    // 说明文件格式不符合要求
 	                	
 	                    return "-1";
 	                }
                }
                result.put("code", "0");
                Map<String,Object> charDataMap = CharDataUtil.newBuildCharData();
                Object categoriesList = charDataMap.get("categories");
        		Object seriesList = charDataMap.get("series");
        		Integer sNum = (Integer) charDataMap.get("sNum");
        		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
        		List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(null);
        		result.put("sNum", sNum);
        		result.put("dList", dList);
        		result.put("categories", categoriesList);
        		result.put("series", seriesList);
        		String resultJson = JSONObject.fromObject(result).toString();
        		return resultJson;
			}catch(Exception e){
				e.printStackTrace();
				return "-2";
			}
	}
	/**
	 * 上传当日任务
	 * @param request
	 * @return
	 */
	public String uploadTaskExcel(HttpServletRequest request){
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding(request.getCharacterEncoding());
        ExcelHelper helper = new ExcelHelper();
        String batchNo = SysStringUtil.createBatchNo();//系统生成一个批次号 每上传一次文件 产生一个新批次号
        
        HashMap<String,Object> result = new HashMap<String,Object>();
        try {
            List<FileItem> list = upload.parseRequest(request);
            if(list == null){
            	return null;
            }
            if(list.size() == 0 ){
            	return null;
            }
            FileItem item = null;
	           for(FileItem it : list){
	        	   if(it != null){
	        		   if(it.getSize() > 0){
	        			   if("excelData".equals(it.getFieldName())){
	        				   item = it;
	        			   }
	        			  
	        		   }
	        	   }
	           }
                
                if(item == null){
                	return null;
                }
                if (item.getName().endsWith(".xls")||item.getName().endsWith(".xlsx")) {
                    // 说明是文件,不过这里最好限制一下
                    List<TaskInfo> dataList = helper.analysis(item.getInputStream(),"close","0",batchNo);
                    result.put("code", "0");
                    result.put("data", dataList);
                    WorkerInfoDao wDao = new WorkerInfoDao();
                    wDao.initAllWorkerStation();
                    List<WorkerInfo> wList = wDao.findAll();
                    Map<String,Object> charDataMap = CharDataUtil.newBuildCharData();
                    Object categoriesList = charDataMap.get("categories");
            		Object seriesList = charDataMap.get("series");
            		Integer sNum = (Integer) charDataMap.get("sNum");
            		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
            		List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(null);
            		result.put("sNum", sNum);
            		result.put("dList", dList);
            		result.put("categories", categoriesList);
            		result.put("series", seriesList);
                    result.put("wList", wList);
                    List<HashMap<String,Object>> pList = selectCurrentTaskStation();
                    result.put("pList", pList);
                    ArrayList<ArrayList<Integer>> mapData = buildMapData();
                    result.put("mapData", mapData);
                    //为了初始化的时候计算出各个区域内的所有任务工位的最优路径
                    //1先查出所有的区域信息
                    AreaInfoDao aDao = new AreaInfoDao();
                    List<AreaInfo> areaList = aDao.findAll();
                    //2遍历areaList 得到一个每个区域对应的任务工位的集合
                    for(int i = 0;i < areaList.size();i++){
                    	String areaId = areaList.get(i).getId();
                    	List<String> stationListtt = CalculationUtil.findStationFromTaskByAreaId(areaId);
                    	List<HashMap<String,Object>> ppList = getStationsPointList(stationListtt);
                    	result.put("ppList" + (i+1), ppList);
                    }
                    
                } else {
                    
                    return "-1";
                }
            String resultJson = JSONObject.fromObject(result).toString();
            System.out.println(resultJson);
            return resultJson;
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "-2";
        }
	}
	/**
	 * 查询当天任务涉及的所有工位的无重复集合
	 * @return
	 */
	public List<HashMap<String,Object>> selectCurrentTaskStation(){
		TaskInfoDao dao = new TaskInfoDao();
		List<HashMap<String,Object>> cStationList = dao.selectTaskStation();
		List<HashMap<String,Object>> rList = new ArrayList<HashMap<String,Object>>();
		if(cStationList != null){
			Iterator<HashMap<String,Object>> it = cStationList.iterator();
			while(it.hasNext()){
				HashMap<String,Object> hm = it.next();
				String x = (String) hm.get("x");
				String y = (String) hm.get("y");
				String[] aaa = x.split("[.]");
				if(aaa.length > 0){
					x = aaa[0];
				}
				String[] bbb = y.split("[.]");
				if(bbb.length > 0){
					y = bbb[0];
				}
				hm.put("x", Integer.parseInt(x) - 1);
				hm.put("y", Integer.parseInt(y) - 1);
				rList.add(hm);
			}
		}
		return rList;
	}
	/**
	 * 传入一个无重复station 集合 得到一个把station的点存到一起的集合 方便调用前端的js 代码
	 * @return
	 */
	public List<HashMap<String,Object>> getStationsPointList(List<String> stationList){
		StationInfoDao sDao = new StationInfoDao();
		List<HashMap<String, Object>> rList = new ArrayList<HashMap<String, Object>>();// 这个是最终需要传到前端的点坐标集合
		for (int i = 0;i < stationList.size();i++) {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			String stationId = stationList.get(i);// 得到每一个工位号
			StationInfo sInfo = sDao.findById(stationId);// 得到每一个工位对象
			String x = sInfo.getxAxis();
			String y = sInfo.getyAxis();
			x = SysStringUtil.formatIntegerStr(x);
			y = SysStringUtil.formatIntegerStr(y);
			hm.put("x", Integer.parseInt(x) - 1);
			hm.put("y", Integer.parseInt(y) - 1);
			rList.add(hm);
		}
		return rList;
	}
	public ArrayList<ArrayList<Integer>> buildMapData(){
		ArrayList<ArrayList<HashMap<String,Object>>> mapinfo = MapInfo.getInstanse().getMapInfo();
		//构建地图信息=--------------------
		ArrayList<ArrayList<Integer>> mapData = new ArrayList<ArrayList<Integer>>();
		for(int i = 0 ;i < mapinfo.size();i++){
			ArrayList<HashMap<String,Object>> row = mapinfo.get(i);
			ArrayList<Integer> rowData = new ArrayList<Integer>();
			for(int j = 0 ; j < row.size() ;j++){
				HashMap<String,Object> colInfo = row.get(j);
				String colType = (String)colInfo.get("colType");
				//0路径1障碍
				if("0".equals(colType)){
					rowData.add(1);
				}
				else{
					rowData.add(0);
				}
			}
			mapData.add(rowData);
		}
		return mapData;
	}
	/**
	 * 初始化加载数据
	 * @param request
	 * @return
	 */
	public String loadData(HttpServletRequest request){
		String searchWorker = request.getParameter("searchWorker");
		String searchState = request.getParameter("searchState");
		TaskInfoDao tDao = new TaskInfoDao();
		List<TaskInfo> tList = tDao.findAllOnCurrentDay(null);
		Map<String,Object> resultMap = new HashMap<String,Object>();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		DivideHistoryInfo dParam = new DivideHistoryInfo();
		dParam.setWorkerId(searchWorker);
		if(!StringUtils.isEmpty(searchState)){
			if(!searchState.trim().equals("Choose one")){
				dParam.setState(Integer.parseInt(searchState));
			}
		}
		
		List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(dParam);
		Map<String,Object> charDataMap = CharDataUtil.newBuildCharData();
		Object categoriesList = charDataMap.get("categories");
		Object seriesList = charDataMap.get("series");
		List<Map<String,Object>> listA = CharDataUtil.getPointsData();
		if(listA.size() > 0){
			Map<String,Integer> coorDinateDate =CharDataUtil.adjustCoordinate(listA);
			List<String> legendData = CharDataUtil.getlegendData(listA);
			ArrayList<Map<String,Object>> eCharCategoriesList = CharDataUtil.getCategoriesList(legendData);
			
			Map<String,Object> personLMap = CharDataUtil.buildParamForPython();
			String paramJson = JSONObject.fromObject(personLMap).toString();
			//paramJson = paramJson.replace('"', '#');
			//20171225 优化，先从session获取findPathJson 若不存在  再重新通过python计算
			String findPathJson = (String) request.getSession().getAttribute("findPathJson");
			/*if(findPathJson == null || "findPathJson".equals(findPathJson)){
				findPathJson = new PythonUtil().findPathProcessByPython(paramJson);
				 request.getSession().setAttribute("findPathJson", findPathJson);
			}*/
			if(findPathJson != null){
				//生成各个点之间的连线
				ArrayList<ArrayList<Object>> finalLineList = CharDataUtil.buildLineList(findPathJson, personLMap, listA);
				System.out.println("buildLineOver");
				ArrayList<Object> finalLine = finalLineList.get(finalLineList.size() - 1);// 获得最后一组连线
				System.out.println("find final line over");
				request.getSession().setAttribute("finalLine", finalLine);
				ArrayList<Object> arrowList = CharDataUtil.getArrowList(finalLine,listA,null);//获得箭头指向关系
				System.out.println("build arrowList over");
				//构建echarts（工位点） 的图表
				resultMap.put("range", coorDinateDate);
				resultMap.put("points", listA);
				resultMap.put("legendData", legendData);
				resultMap.put("eCharCategoriesList", eCharCategoriesList);
				resultMap.put("lineData",finalLineList);
				resultMap.put("arrowList",arrowList);
				//构建echarts图表结束
			}
		
			
		}
		
		resultMap.put("categories", categoriesList);
		resultMap.put("series", seriesList);
		resultMap.put("code", 0);
		resultMap.put("tList", tList);
		resultMap.put("dList", dList);
		Integer sNum = (Integer) charDataMap.get("sNum");
		resultMap.put("sNum", sNum);
		
		Integer componentNum = tDao.selectCurrentComponentNum();
		resultMap.put("componentNum", componentNum);
		String resultJson = JSONObject.fromObject(resultMap).toString();
		return resultJson;
	}
	public void divisionTask_003(JSONArray parray,AStarMapInfo mapInfo,String isEmergency){
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		//得到所有当日未分配无重复零件名称集合 
		HashMap<String,String> componentMap = CalculationUtil.findComponentFromTaskByAreaId(null, isEmergency);
		if(componentMap == null){
			return;
		}
		if(componentMap.size() == 0){
			return;
		}
		Map<String,List<HashMap<String,Object>>> paramList = new HashMap<String,List<HashMap<String,Object>>>();
		for (String key : componentMap.keySet()) { 
			  String componentName = key;
			  List<HashMap<String,Object>> stationList = taskInfoDao.getStationByComponent(componentName);
				//map 结构： C01:工位1、工位2、工位3...{0005596-01=[{stationName=810061, id=3494f4a6-e6ea-4e6e-8de1-f2ba89f922f7}, {stationName=800011, id=3fad6712-7cbe-419b-ab49-03e74870e244}, {stationName=800061, id=593f056d-9758-4c10-b8ff-f565eaba9538}, {stationName=820061, id=7374e3d3-9ae3-4a2d-94fb-f4731ce9c6d2}], 000542
			  paramList.put(componentName, stationList);
		} 
		for(int i = 0;i < parray.length();i++){
			JSONObject obj = (JSONObject) parray.get(i);
			String workerId = obj.getString("id");//员工id
			//先检查一下该工人是否有已经分配过的任务
			DivideHistoryInfo dInfo = dDao.selectLastComponent(workerId);
			if(dInfo != null){
				//如果之前有任务 最大序号应该从最后一个紧急任务开始 + 1
				obj.put("maxIndex", dInfo.getOrderNum() + 1);
				obj.put("preComponentName", dInfo.getComponentName());
				obj.put("isHasPre", true);
			}
		}
		//最新的自动分配算法 2018-05-14
		ArrayList<ArrayList<HashMap<String,Object>>> resultList = newDivideAlgo_003(paramList, mapInfo, parray);
		for(int i = 0;i < parray.length();i++){
			JSONObject pobj = (JSONObject) parray.get(i);//parray :前端传来的工人集合
			String workerId = pobj.getString("id");
			//获取每一个人员的分配结果 （一个集合 包含零件、距离、工位等信息）
			ArrayList<HashMap<String,Object>> oneWorkerResult = resultList.get(i);
			if(pobj.has("isHasPre")){//如果之前有紧急任务或者未完成任务则本次需要移除  不进行分配
				resultList.remove(0);
			}
			for(int l = 0;l < oneWorkerResult.size();l++){
				HashMap<String,Object> pHM1 = oneWorkerResult.get(l);
				//String componentName = (String) pHM1.get("componentName");
				CalculationUtil.divitionComponentToPerson(pHM1, workerId, l+1);
			}
			
		}
	}
	
	private void setCurrentDistanse(HashMap<String,Object> element,HashMap<String,Object> preElement,AStarMapInfo mapInfo){
		ArrayList<ArrayList<Integer>> mapData = buildMapData();
		Integer currentDistanse = 0;
		if(preElement != null){//说明当前零件有前置零件
			Integer preDistanse = (Integer) preElement.get("currentDistanse");
			currentDistanse += preDistanse;
			List<HashMap<String,Object>>  stationLists = (List<HashMap<String, Object>>) preElement.get("stations");
			HashMap<String,Object> sMap  = stationLists.get(stationLists.size() - 1);
			String preStationName = (String) sMap.get("stationName");
			String basicStationName = (String) element.get("basicStationName");
			currentDistanse += CalculationUtil.getDistanse(mapData, preStationName, basicStationName);
			
		}
		List<HashMap<String,Object>> stations = (List<HashMap<String,Object>>) element.get("stations");
		//计算零件内工位距离
		for(int i = 0;i < stations.size();i++){
			if(i != stations.size() - 1){
				HashMap<String,Object> sMap = stations.get(i);
				HashMap<String,Object> eMap = stations.get(i + 1);
				currentDistanse += CalculationUtil.getDistanse(mapData, (String)sMap.get("stationName"), (String)eMap.get("stationName"));
			}
			
		}
		element.put("currentDistanse", currentDistanse);
	}
	
	private ArrayList<ArrayList<HashMap<String,Object>>> newDivideAlgo_003(Map<String,List<HashMap<String,Object>>> paramList,AStarMapInfo mapInfo,JSONArray parray){
		//创建一个用来存放待分配零件集合的队列
		ArrayList<HashMap<String,Object>> queue = createQueue(paramList);
		ArrayList<ArrayList<HashMap<String,Object>>> resultList = new ArrayList<ArrayList<HashMap<String,Object>>>();
		//这是第一次分配 
		for(int i = 0;i < parray.length();i++){
			ArrayList<HashMap<String,Object>> newQueue = new ArrayList<HashMap<String,Object>>();
			JSONObject obj = parray.getJSONObject(i);
			String preComponentName = null;
			HashMap<String,Object> element = new HashMap<String,Object>();
			if(obj.has("preComponentName")){
				preComponentName = obj.getString("preComponentName");
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
				element.put("currentDistanse", 0);
			}
			else{
				element = findNextComponent(preComponentName,queue, mapInfo,"0");
				sortComponentStations(element, mapInfo);//对该零件内工位进行排序
				setCurrentDistanse(element, null, mapInfo);
			}
			newQueue.add(element);
			resultList.add(newQueue);
		}
		while(queue.size() > 0){//说明队列中还没未分配的任务 需要继续分配
			int minIndex = 0;
			int minDistanse = -1;
			//这个for循环就为了找到当前哪个人的行走距离最小  谁最小 下一个就应该分配给谁 所以记录minIndex
			for(int i = 0;i < resultList.size();i++){
				ArrayList<HashMap<String,Object>> q = resultList.get(i);//每个人员的分配集合
				HashMap<String,Object> cement = q.get(q.size() - 1);//获取该人员当前所在零件
				int cDistanse = (int) cement.get("currentDistanse");
				if(minDistanse == -1){
					minDistanse = cDistanse;
				}
				else if(cDistanse < minDistanse){
					minDistanse = cDistanse;
					minIndex = i;
				}
			}
			//for 结束
			//找到这个距离最小的人的分配集合
			ArrayList<HashMap<String,Object>> q = resultList.get(minIndex);
			HashMap<String,Object> cement = q.get(q.size() - 1);//获取该人员当前所在零件
			List<HashMap<String,Object>> stationList = (List<HashMap<String, Object>>) cement.get("stations");
			HashMap<String,Object> sMap = stationList.get(stationList.size() - 1);
			String preStationName = (String) sMap.get("stationName");
			HashMap<String,Object> nextElement = findNextComponent(preStationName,queue, mapInfo,"0");
			sortComponentStations(nextElement, mapInfo);//对该零件内工位进行排序
			setCurrentDistanse(nextElement,cement, mapInfo);
			q.add(nextElement);
		}
		return resultList;
	}
	public HashMap<String,Object> submitScheme_003(String workersJson){
		//**********************分配任务之前的准备工作********************************
		HashMap<String,Object> resultHM = new HashMap<String,Object>();
		TaskInfoDao taskInfoDao = new TaskInfoDao();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		
		//若已经分配过 需要删除当天数据重新分配
		//DivideHistoryInfo dParam = new DivideHistoryInfo();
		//20180418修改 只删除未完成的分配记录 已完成的要排除
		//List<DivideHistoryInfo> dList = dDao.findAllOnCurrentDay(dParam);
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
					taskInfoDao.update(t);
				}
			}
		}
		// 以上的代码就是为了把已分配未完成的分配记录删除掉 并把任务状态更新为未分配
		
		//为A* 算法提供地图数据
		ArrayList<ArrayList<Integer>> mapData = buildMapData();
		AStarMapInfo mapInfo = new AStarMapInfo();
		mapInfo.buildMap(mapData);
		List<String> list = taskInfoDao.getTaskComponentList();
		Map<String,List<HashMap<String,Object>>> paramList = new HashMap<String,List<HashMap<String,Object>>>();
		DivideHistoryInfoDao divideHistoryInfoDao = new DivideHistoryInfoDao();
		//将人员json字符串转为json对象集合
		JSONArray parray = JSONArray.fromObject(workersJson);
		//只存放零件名称的集合 方便后面取值
		List<String> componentList = new ArrayList<String>();
		DivideHistoryInfo f = new DivideHistoryInfo();
		if(list != null){
			for(int i = 0;i < list.size();i++){
				String componentName = list.get(i);
				List<HashMap<String,Object>> stationList = taskInfoDao.getStationByComponent(componentName);
				//map 结构： C01:工位1、工位2、工位3...
				paramList.put(list.get(i), stationList);
				componentList.add(componentName);
			}
		}
		//divisionCoreAlgor是核心分配算法
		ArrayList<ArrayList<HashMap<String,Object>>> resultList = divisionCoreAlgor(paramList,parray,mapInfo);
		
		for(int i = 0;i < parray.length();i++){
			TaskWalkLength tw = new TaskWalkLength();
			JSONObject pobj = (JSONObject) parray.get(i);//parray :前端传来的工人集合
			String workerId = pobj.getString("id");
			//获取每一个人员的分配结果 （一个集合 包含零件、距离、工位等信息）
			ArrayList<HashMap<String,Object>> oneWorkerResult = resultList.get(i);
			
			for(int l = 0;l < oneWorkerResult.size();l++){
				HashMap<String,Object> pHM1 = oneWorkerResult.get(l);
				//String componentName = (String) pHM1.get("componentName");
				CalculationUtil.divitionComponentToPerson(pHM1, workerId, l+1);
			}
			
		}
		TaskInfo t1 = new TaskInfo();
		List<TaskInfo> tList = taskInfoDao.findAllOnCurrentDay(t1);
		resultHM.put("code", 0); 
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
		return resultHM;
	}
	/**
	 * 分配任务的核心算法 
	 * @param paramList
	 * @param pNum
	 * @param mapInfo
	 * @return
	 */
	public ArrayList<ArrayList<HashMap<String,Object>>> divisionCoreAlgor(Map<String,List<HashMap<String,Object>>> paramList,JSONArray parray,AStarMapInfo mapInfo){
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		ArrayList<ArrayList<HashMap<String,Object>>> resultList =  new ArrayList<ArrayList<HashMap<String,Object>>>();
		//创建一个用来存放待分配零件集合的队列
		ArrayList<HashMap<String,Object>> queue = createQueue(paramList);
		//对前端传的人员数组进行遍历
		for(int i = 0;i < parray.length();i++){
			JSONObject obj = (JSONObject) parray.get(i);
			String workerId = obj.getString("id");//获得每一个工人的id
			//检查该工人当天是否有已经完成的任务，有的他的开始工位就应该是最后一个完成的零件的最后一个工位
			DivideHistoryInfo ddd = dDao.selectFinishedLastComponent(workerId);
			//初始化每一个工人的分配队列
			ArrayList<HashMap<String,Object>> newQueue = new ArrayList<HashMap<String,Object>>();
			
		}
		return resultList;
	}
	/**
	 * 构建一个零件工位队列
	 * @param paramList
	 * @return
	 */
	public ArrayList<HashMap<String,Object>> createQueue(Map<String,List<HashMap<String,Object>>> paramList){
		//创建一个用来存放待分配零件集合的队列
		ArrayList<HashMap<String,Object>> queue = new ArrayList<HashMap<String,Object>>();
		for (Map.Entry<String,List<HashMap<String,Object>>> entry : paramList.entrySet()) { 
		 // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
		  String componentName = entry.getKey();//零件名称
		  List<HashMap<String,Object>> stations = entry.getValue();//该了零件对应的所有工位
		  HashMap<String,Object> aMap = new HashMap<String,Object>();
		  aMap.put("componentName", componentName);
		  aMap.put("stations", stations);
		  queue.add(aMap);
		}
		return queue;
	}
	/**
	 * 获取下一个最近的零件和工位信息
	 * @param preStationName
	 * @param queue
	 * @param mapInfo
	 * @return
	 */
	public HashMap<String,Object> findNextComponent(String preStationName,ArrayList<HashMap<String,Object>> queue,AStarMapInfo mapInfo,String startPointFlag){
		AStar aStar = new AStar();
		StationInfoDao sDao = new StationInfoDao();
		
		int minZoomPotinIndex = 0;
		int minPointDistanse = -1;
		String basicStationName = "";
		//**************队列元素迭代为了找到坐标最小的点位置
		String initxStr = "0";
		String inityStr = "0";
		if("1".equals(startPointFlag)){
			initxStr = "267";
			inityStr = "385";
		}
		Integer x = Integer.parseInt(initxStr);
		Integer y = Integer.parseInt(inityStr);
		//如果上一个工位信息不空 说明不是第一个点了 需要通过它去寻找下一个零件
		if(!StringUtils.isEmpty(preStationName)){
			StationInfo preStationInfo = sDao.findById(preStationName);
			String preX = preStationInfo.getxAxis();
			String preY = preStationInfo.getyAxis();
			preX = SysStringUtil.formatIntegerStr(preX);
			preY = SysStringUtil.formatIntegerStr(preY);
			//excel 索引从1 开始  地图索引从0开始  坐标值需要 -1
			x = Integer.parseInt(preX) - 1;
			y = Integer.parseInt(preY) - 1;
		}
		Iterator<HashMap<String,Object>> it = queue.iterator();
		Node ssNode = new Node(x,y);
		int ecount = 0;
		while(it.hasNext()){
			HashMap<String,Object> hm = it.next();
			List<HashMap<String,Object>> stations = (List<HashMap<String,Object>>) hm.get("stations");
			for(int i = 0;i < stations.size();i++){
				HashMap<String,Object> stationMap = stations.get(i);
				String stationName = (String) stationMap.get("stationName");
				StationInfo sinfo = sDao.findById(stationName);
				String xstr = sinfo.getxAxis();
				String ystr = sinfo.getyAxis();
				xstr = SysStringUtil.formatIntegerStr(xstr)	;
				ystr = SysStringUtil.formatIntegerStr(ystr)	;
				Node eNode = new Node(Integer.parseInt(xstr) - 1,Integer.parseInt(ystr) - 1);
				mapInfo.setInfo(ssNode, eNode);
				aStar.start(mapInfo);
				Integer distanse_1 = eNode.G;//起始位置和终止位置的距离
				if(minPointDistanse == -1){
					minPointDistanse = distanse_1;
					minZoomPotinIndex = ecount;
					basicStationName = stationName;
				}
				else{
					if(distanse_1 < minPointDistanse){
						minPointDistanse = distanse_1;
						minZoomPotinIndex = ecount;
						basicStationName = stationName;
					}
					//20180510 若出现同样距离最小的工位 则判断其一组工位数目 数目是1的 优先，其余任何数目都一样
					//例如 01,02,03     01  可以避免先走 01,02,03  再回到01   而是：01,01,02,03 这样就省去了03-01的距离
					else{
						if(distanse_1 == minPointDistanse){
							if(stations.size() == 1){
								minZoomPotinIndex = ecount;
								basicStationName = stationName;
							}
						}
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
				hm_1.put("basicStationName", basicStationName );
				return hm_1;
			}
			count++;
		}
		return null;
	}
	/**
	 * 根据基准点 对零件内工位按寻径距离进行排序
	 */
	public void sortComponentStations(HashMap<String,Object> element,AStarMapInfo mapInfo){
		StationInfoDao sDao = new StationInfoDao();
		String basicStationName = (String) element.get("basicStationName");
		System.out.println("basicStationName : " + basicStationName + "======================");
		StationInfo station1 = sDao.findById(basicStationName);
		
		String sxStr = station1.getxAxis();
		String syStr = station1.getyAxis();
		sxStr = SysStringUtil.formatIntegerStr(sxStr);
		syStr = SysStringUtil.formatIntegerStr(syStr);
		Integer sx = Integer.parseInt(sxStr) - 1;
		Integer sy = Integer.parseInt(syStr) - 1;
		System.out.println("工位号：" + basicStationName + "  起始点坐标：（" + sx + "," + sy + "）");
		AStar aStar = new AStar();
		Node sNode = new Node(sx,sy);
		List<HashMap<String,Object>> stations = (List<HashMap<String,Object>>) element.get("stations");
		List<HashMap<String,Object>> sortStations = new ArrayList<HashMap<String,Object>>();
		
		while(stations.size() > 0){
			Iterator<HashMap<String,Object>> it = stations.iterator();
			Integer minDistanse = 0;
			int count = 0;
			Integer index = 0;
			while(it.hasNext()){
				HashMap<String,Object> stationMap = it.next();
				String stationName = (String) stationMap.get("stationName");
				StationInfo station2 = sDao.findById(stationName);
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
				if(count == 0){
					minDistanse = distanse;
					index = count;
				}
				else{
					if(distanse < minDistanse){
						minDistanse = distanse;
						index = count;
					}
				}
				count++;
			}
			Iterator<HashMap<String,Object>> ittt = stations.iterator();
			int count1 = 0;//记录queue 循环次数
			//这次的循环目的就是根据上面的minDistanseQueueIndex 结果 取出queue中的元素分配给这个人 并且从queue中删除
			while(ittt.hasNext()){
				HashMap<String,Object> stationMap = ittt.next();
				//String stationName = (String) stationMap.get("stationName");
				if(count1 == index){
					ittt.remove();
					sortStations.add(stationMap);
					break;
				}
				count1++;
			}
		}
		//将排好序的工位集合重新放回map中
		element.put("stations", sortStations);
	}
}
