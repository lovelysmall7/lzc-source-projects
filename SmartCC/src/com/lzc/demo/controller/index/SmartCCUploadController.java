package com.lzc.demo.controller.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.StringUtils;

import com.lzc.demo.controller.BaseServlet;
import com.lzc.demo.dao.DaoContainer;
import com.lzc.demo.dao.DivideHistoryInfoDao;
import com.lzc.demo.model.AreaInfo;
import com.lzc.demo.model.ComponentStationMapping;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.model.TaskInfo;
import com.lzc.demo.model.WorkerInfo;
import com.lzc.demo.util.CalculationUtil;
import com.lzc.demo.util.CharDataUtil;
import com.lzc.demo.util.ExcelHelper;
import com.lzc.demo.util.ExcelUtil;
import com.lzc.demo.util.MydateUtil;
import com.lzc.demo.util.SysStringUtil;
import com.lzc.demo.util.optimize.BaseOptimizeAlgo;
import com.lzc.demo.util.optimize.OptimizeAlgoFactory;
import com.lzc.demo.util.optimize.OptimizeAlgoImpl1;
import com.lzc.demo.util.optimize.StartOptimizeInter;

import jline.History;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet("/smartccupload")
public class SmartCCUploadController extends BaseServlet{
	/**
	 * 上传新的任务
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void uploadNewTaskExcel(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap<String, Object> result = new HashMap<String, Object>();
		FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding(request.getCharacterEncoding());
        ExcelHelper helper = new ExcelHelper();
        String batchNo = SysStringUtil.createBatchNo();//系统生成一个批次号 每上传一次文件 产生一个新批次号
        String currentBatch = "";
        String componentName = "";//
        String isEmergency = "0";
        int code = 0;
        String msg = "";
        try {
            List<FileItem> list = upload.parseRequest(request);
            if(list == null){
            	code = -1;
            	msg = "Can not find available parameter.";//未发现可用参数
            	result.put("code", code);
            	result.put("msg", msg);
            	writeToView(response, result);
            	return;
            }
            if(list.size() == 0 ){
            	code = -1;
            	msg = "Can not find available parameter.";//未发现可用参数
            	result.put("code", code);
            	result.put("msg", msg);
            	writeToView(response, result);
            	return;
            }
            FileItem item = null;
	           for(FileItem it : list){
	        	   if(it != null){
	        		   if(it.getSize() > 0){
	        			   if("excelData".equals(it.getFieldName())){
	        				   item = it;
	        			   }
	        			   if("currentBatch".equals(it.getFieldName())){
	        				   currentBatch = it.getString();
	        			   }
	        			   if("currentBatch1".equals(it.getFieldName())){
	        				   currentBatch = it.getString();
	        			   }
	        			   if("isEmergency".equals(it.getFieldName())){
	        				   isEmergency = it.getString();
	        			   }
	        			   if("component".equals(it.getFieldName())){
	        				   if(!StringUtils.isEmpty(it.getString())){
	        					   componentName = it.getString();
	        					   
	        				   }//if 结束
	        				   
	        			   }
	        		   }
	        	   }
	           }
	           boolean flag = false;
               if(!StringUtils.isEmpty(componentName)){
               		flag = saveOneEmengency(componentName, batchNo, isEmergency, currentBatch);
               }
               HashMap<String,Object> resultInfo = null;
              
               if(item != null){
            	// 说明是文件,不过这里最好限制一下
            	   if (item.getName().endsWith(".xls")||item.getName().endsWith(".xlsx")) {
                       resultInfo = helper.uploadNewTaskExcel(item.getInputStream(), isEmergency, batchNo, currentBatch);
                   }
               }
               if(resultInfo == null){
            	   resultInfo = new HashMap<String,Object>();
            	   List<String> successList = new ArrayList<String>();
                   List<String> failList = new ArrayList<String>();
                   resultInfo.put("successList", successList);
                   resultInfo.put("failList", failList);
               }
               msg = buildMessage(resultInfo, flag, componentName, isEmergency);
                result.put("code", 0);
                result.put("msg", msg);
            	writeToView(response, result);
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
        	result.put("code", -3);
        	result.put("msg", e.getMessage());
        	writeToView(response, result);
            e.printStackTrace();
            
        }
	}
	private String buildMessage(HashMap<String,Object> resultInfo,boolean flag,String componentName,String isEmergency){
		String msg = ""; 
		List<String> successList = (List<String>) resultInfo.get("successList");
         List<String> failList = (List<String>) resultInfo.get("failList");
         if(!StringUtils.isEmpty(componentName)){
        	 if(flag){
            	   successList.add(componentName);
               }
               else{
            	   failList.add(componentName);
               }
         }
         
         String failComponents = "";
         for(int i = 0;i < failList.size();i++){
      	   if(i == 0){
      		   failComponents += failList.get(i);
      	   }
      	   else{
      		   failComponents += "," + failList.get(i);
      	   }
         }
         String insertType = "Nomal";
         if("1".equals(isEmergency)){
      	   insertType = "Emergency";
         }else if("2".equals(isEmergency)){
      	   insertType = "Recheck";
         }
         int totalNum  = successList.size() + failList.size();
         String line1 = "Total component num : " + totalNum + ".<br>";
         String line5 = "Insert Type : " + insertType + ".<br>";
         String line2 = "Insert success num : " + successList.size() + ".<br>";
         String line3 = "Insert fail num : " + failList.size() + ".<br>";
         String line4 = "Fail message : " + failComponents + " can not find the correspondence of station.<br>";
         msg += line1;
         msg += line5;
         msg += line2;
         msg += line3;
         if(failList.size() > 0){
      	   msg += line4;
         }
		return msg;
	}
	
	private boolean saveOneEmengency(String componentName,String batchNo,String isEmergency,String currentBatch){
		StartOptimizeInter optimizeInstanse = OptimizeAlgoFactory.getAlgoInstanse();
		ComponentStationMapping csinfo = new ComponentStationMapping();
		csinfo.setComponentName(componentName);
		List<String> stationList = DaoContainer.csDao.findStationByComponent(componentName);
		stationList = new BaseOptimizeAlgo().sortComponentStations(stationList);
		if (stationList.size() == 0) {
			return false;
		}
		String stationids = "";
		for (int i = 0; i < stationList.size(); i++) {
			String stationId = stationList.get(i);
			if (i == 0) {
				stationids += stationId;
			} else {
				stationids += "," + stationId;
			}
		} // for 结束
		DivideHistoryInfo dinfo = new DivideHistoryInfo();
		Integer offset = 0;
		if(stationList.size() > 1){
			String startStation = stationList.get(0);
			String endStation = stationList.get(stationList.size() - 1);
			ArrayList<ArrayList<Integer>> mapData = MapInfo.getInstanse().getMapData();
			offset = CalculationUtil.getDistanse(mapData, startStation, endStation);
		}
		if(stationList.size() == 1){
			ComponentStationMapping csinfo1 = new ComponentStationMapping();
			csinfo1.setComponentName(componentName);
			csinfo1.setStationName(stationList.get(0));
			csinfo1 = DaoContainer.csDao.findOneByParam(csinfo1);
			if(!StringUtils.isEmpty(csinfo1.getKnockingPoint()) ){
				dinfo.setKnockingPoint(csinfo1.getKnockingPoint());//敲枪点
			}
		}
		
		dinfo.setId(UUID.randomUUID().toString());
		dinfo.setComponentName(componentName);
		dinfo.setStationIds(stationids);
		dinfo.setStationNames(stationids);
		dinfo.setCurrentBatch(currentBatch);
		dinfo.setType(Integer.parseInt(isEmergency));
		dinfo.setBatchNo(batchNo);
		dinfo.setCreateTime(new Date());
		dinfo.setState(0);
		dinfo.setOffset(offset);
		return DaoContainer.dDao.insert(dinfo);
	}
	/**
	 * 查询当前批次的任务
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void findCurrentBatchData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		String searchComponent = request.getParameter("searchComponent");
		String searchType = request.getParameter("searchType");
		HashMap<String,Object> param = new HashMap<String,Object>();
		param.put("searchComponent", searchComponent);
		param.put("searchType", searchType);
		param.put("NotSeeFinish", "1");
		List<DivideHistoryInfo> dList = DaoContainer.dDao.findCurrentBatchData(param);
		result.put("code", 0);
		result.put("rows", dList);
		writeToView(response, result);
	}
	/**
	 * 分配任务
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void assignment(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap<String, Object> result = new HashMap<String, Object>();
		String workersJson = request.getParameter("workersJson");
		String taskRowJson = request.getParameter("taskRowJson");
		JSONArray jarray = JSONArray.fromObject(workersJson);
		JSONArray tArray = JSONArray.fromObject(taskRowJson);
		int code = 0;
		String msg = "";
		if(jarray.length() == 0){
			code = -1;
			msg = "Please Choose one worker to assignment at least.";
			result.put("code", code);
			result.put("msg", msg);
			writeToView(response, result);
			return;
		}
		else{//肯定是选择了人员
			
			if(jarray.length() > 1){//说明不只选了一个人
				if(tArray.length() > 0){//这时判断如果任务也有选择 则需要返回操作错误
					code = -2;
					msg  = "Please choose just one worker in manual mode.";
					result.put("code", code);
					result.put("msg", msg);
					writeToView(response, result);
					return;
				}
			}
			
		}
		if(tArray.length() > 0){//手动强制分配
			JSONObject obj = jarray.getJSONObject(0);//获得选择那个人员
			String workerId = obj.getString("id");
			
			for(int i = 0;i < tArray.length();i++){
				JSONObject tobj = tArray.getJSONObject(i);//获得选择那个人员
				String id = tobj.getString("id");
				DivideHistoryInfo dInfo = DaoContainer.dDao.findById(id);
				dInfo.setWorkerId(workerId);
				dInfo.setState(1);
				dInfo.setDevideType(1);
				DaoContainer.dDao.update(dInfo);
			}
		}
		else{//自动按人员负责区域分配
			HashMap<String,Object> param = new HashMap<String,Object>();
			param.put("state", 0);//自动分配要查询 当前是未分配状态的任务
			List<DivideHistoryInfo> dList = DaoContainer.dDao.findCurrentBatchData(param);//将当前批次的所有零件任务查出来
			for(int i = 0;i < dList.size();i++){
				DivideHistoryInfo dInfo = dList.get(i);
				String stationIds = dInfo.getStationIds();
				String[] s = stationIds.split(",");
				String stationName = s[0];
				for(int j = 0;j < jarray.length();j++){//遍历人员集合
					JSONObject obj = jarray.getJSONObject(j);
					String workerId = obj.getString("id");
					String areaId = obj.getString("areaId");
					if(StringUtils.isEmpty(areaId)){//人员负责区域是空直接跳过
						continue;
					}
					if(CalculationUtil.isStationInArea(stationName,areaId)){//直接能够匹配上的是柱子号
						dInfo.setWorkerId(workerId);
						dInfo.setState(1);
						dInfo.setDevideType(0);
						DaoContainer.dDao.update(dInfo);
						break;
					}
					else{
						 if(stationName.length() >= 2){
							// 截取工位号前两位
							String stationNameSub = stationName.substring(0, 2);
							if (CalculationUtil.isStationInArea(stationNameSub,areaId)) {// 直接能够匹配上的是柱子号
								dInfo.setWorkerId(workerId);
								dInfo.setState(1);
								dInfo.setDevideType(0);
								DaoContainer.dDao.update(dInfo);
								break;
							}
						 }
					}
				}
			}
			
		}
		result.put("code", 0);
		writeToView(response, result);
	}
	/**
	 * 开始优化
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void startOptimize(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String,Object> param = new HashMap<String,Object>();
		param.put("selectFlag", "findReadyOptimizeData");
		List<DivideHistoryInfo> dList = DaoContainer.dDao.findCurrentBatchData(param);
		
		StartOptimizeInter algoInstanse = OptimizeAlgoFactory.getAlgoInstanse();
		HashMap<String,Object> workerMap = new HashMap<String,Object>();//为了获得dList 中涉及的无重复工人id
		for(int i = 0;i < dList.size();i++){
			DivideHistoryInfo dinfo = dList.get(i);
			workerMap.put(dinfo.getWorkerId(), dinfo.getWorkerId());
		}
		
		Set set=workerMap.keySet();  
        Iterator it=set.iterator();  
        while(it.hasNext()){  
            String workerId = (String) it.next();
            DaoContainer.dDao.resetCurrentBatchData(workerId);//先将这个工人之前的优化结果清除
            List<DivideHistoryInfo> workerTaskList = new ArrayList<DivideHistoryInfo>();//每个工人当前的任务集合
            for(int i = 0;i < dList.size();i++){
    			DivideHistoryInfo dinfo = dList.get(i);
    			String workerId1 = dinfo.getWorkerId();
    			if(workerId.equals(workerId1)){
    				workerTaskList.add(dinfo);
    			}
    		}
            algoInstanse.startOptimize(workerTaskList,workerId);//开始优化
        }
        
        result.put("code", 0);
        writeToView(response, result);
	}
	/*private Integer getTotalDistanse(List<String> stationList){
		ArrayList<ArrayList<Integer>> mapData = MapInfo.getInstanse().getMapData();
		Integer totalDistanse = 0;
		for(int i = 0;i < stationList.size();i++){
			if(i != stationList.size() - 1){
				String pStations = stationList.get(i) ;
				String nStations = stationList.get(i + 1);
				Integer pDistanse = getStationsDistanse(pStations);
				Integer nDistanse = getStationsDistanse(nStations);
				String[] pStationsArr = pStations.split(",");
				String[] nStationsArr = nStations.split(",");
				Integer bDistanse = CalculationUtil.getDistanse(mapData, pStationsArr[pStationsArr.length - 1], nStationsArr[0]);
			}
		}
		
	}*/
	public Integer getStationsDistanse(String stations){
		
		ArrayList<ArrayList<Integer>> mapData = MapInfo.getInstanse().getMapData();
		String[] aaa = stations.split(",");
		Integer totalDistanse = 0;
		for(int i = 0;i < aaa.length;i++){
			if(i != aaa.length - 1){
				totalDistanse += CalculationUtil.getDistanse(mapData, aaa[i], aaa[i+1]);
			}
		}
		return totalDistanse;
	}
	public void downUploadResult(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap<String,Object> param = new HashMap<String,Object>();
		String searchType = request.getParameter("searchType");
		String searchComponent = request.getParameter("searchComponent");
		param.put("searchType", searchType);
		param.put("searchComponent", searchComponent);
		List<HashMap<String,Object>> dataList = DaoContainer.dDao.findCurrentBatchMap(param);
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
		title4.put("property", "stationIds");
		HashMap<String,Object> title5 = new HashMap<String,Object>();
		title5.put("label", "Status");
		title5.put("property", "state");
		
		List<Object> titleArray = new ArrayList<Object>();
		titleArray.add(title1);
		titleArray.add(title2);
		titleArray.add(title3);
		titleArray.add(title4);
		//titleArray.add(title5);
		String titleJson = JSONArray.fromObject(titleArray).toString();
		ExcelUtil.exportExcel(request, response, "optimize_result.xls", titleJson, dataList);
	}
	public void getNewBatch(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap<String, Object> result = new HashMap<String, Object>();
		String currentBatch = SysStringUtil.getCurrentBatch();
		result.put("currentBatch", currentBatch);
		writeToView(response, result);
	}
}
