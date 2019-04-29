package com.lzc.demo.controller.index;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.lzc.demo.controller.BaseServlet;
import com.lzc.demo.dao.DaoContainer;
import com.lzc.demo.dao.DivideHistoryInfoDao;
import com.lzc.demo.dao.TaskInfoDao;
import com.lzc.demo.dao.TaskWalkLengthDao1;
import com.lzc.demo.dao.WorkerInfoDao;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.model.PageUtil;
import com.lzc.demo.model.TaskInfo;
import com.lzc.demo.model.TaskWalkLength1;
import com.lzc.demo.model.WorkerInfo;
import com.lzc.demo.util.CalculationUtil;
import com.lzc.demo.util.CharDataUtil;
import com.lzc.demo.util.ExcelUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@WebServlet("/dividehistory")
public class SmartCCDivideHistoryController extends BaseServlet{
	public void selectDivideHistory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap<String, Object> result = new HashMap<String, Object>();
		HashMap<String,Object> param = new HashMap<String,Object>();
		// 获取前台页数  
		String pageStr = request.getParameter("page");
		String rowsStr = request.getParameter("rows");
		if(StringUtils.isBlank(pageStr)){
			pageStr = "1";
		}
		if(StringUtils.isBlank(rowsStr)){
			rowsStr = "30";
		}
        int page = Integer.parseInt(pageStr);  
        
        // 获取前台每页显示条数  
        int row = Integer.parseInt(rowsStr);
        String componentName = request.getParameter("searchComponent");
        String workerId = request.getParameter("searchWorker");
        String state = request.getParameter("searchState");
        String searchBatch = request.getParameter("searchBatch");
        String searchType = request.getParameter("searchType");
        DivideHistoryInfo dinfo = new DivideHistoryInfo();
        if(!StringUtils.isBlank(state)){
        	dinfo.setState(Integer.parseInt(state));	
        }
        dinfo.setComponentName(componentName);
        
        dinfo.setWorkerId(workerId);
        dinfo.setCurrentBatch(searchBatch);
        if(!StringUtils.isBlank(searchType)){
        	dinfo.setType(Integer.parseInt(searchType));
        }
        
        
        PageUtil<DivideHistoryInfo> pageUtil = new PageUtil<DivideHistoryInfo>(page,row);
        param.put("pageUtil",pageUtil);
        param.put("searchParam", dinfo);
    	PageUtil<DivideHistoryInfo> resultPageUtil = DaoContainer.dDao.findByPage(param);
		List<DivideHistoryInfo> dList = resultPageUtil.getResultList();
		Integer total = resultPageUtil.getTotalRecord();
		result.put("rows", dList);
		result.put("total", total);
		result.put("code", 0);
		writeToView(response, result);
	}
	public void changeState(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap<String,Object> result = new HashMap<String,Object>();
		DivideHistoryInfoDao dao = new DivideHistoryInfoDao();
		String dataJson = request.getParameter("dataJson");
		JSONArray jarray = JSONArray.fromObject(dataJson);
		for(int l = 0;l < jarray.length();l++){
			JSONObject obj = (JSONObject) jarray.get(l);
			String id = obj.getString("id");
			String stationIds = obj.getString("stationIds");
			if(StringUtils.isEmpty(stationIds)){
				continue;
			}
	        String state = obj.getString("state");
	        if("0".equals(state)){
	            continue;
	        }
	        String workerId = obj.getString("workerId");
	        //查出该工人信息
	        DivideHistoryInfo d = dao.findById(id);
	        if("1".equals(state)){
	        	if(d.getState() != 2){
	        		d.setState(2);
	        	}
	        	 
	        }
	        else{
	        	if(d.getState() != 3){
	        		//说明是已完成 需要将该工人的当前所在工位信息进行更新
		        	String stationNames =  d.getStationIds();//获得这个工人完成这个工作需要经过的工位集合（带有顺序信息）
		        	updateWorkerDistanse(workerId,stationNames);//更新该工人行走的距离
		        	d.setState(3);
	        	}
	        	
	        }
	        DaoContainer.dDao.update(d);
		}
		
        result.put("code",0);
        writeToView(response, result);
	}
	/**
	 * 更新该工人当前行走的路程信息
	 */
	public void updateWorkerDistanse(String workerId,String currentTaskStations){
		ArrayList<ArrayList<Integer>> mapData = MapInfo.getInstanse().getMapData();
		DivideHistoryInfoDao dDao = new DivideHistoryInfoDao();
		DivideHistoryInfo dInfo = dDao.selectFinishedLastComponent(workerId);
		String startStation = "";
		ArrayList<String> stationList = new ArrayList<String>();
		if(dInfo != null){
			String stations = dInfo.getStationIds();//获得最后一个任务的所有工位
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
	public void loadChartData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HashMap<String,Object> result = new HashMap<String,Object>();
		Map<String,Object> charDataMap = CharDataUtil.newBuildCharData_1();
		Object categoriesList = charDataMap.get("categories");
		Object seriesList = charDataMap.get("series");
		result.put("categories", categoriesList);
		result.put("series", seriesList);
		Integer sNum = (Integer) charDataMap.get("sNum");
		result.put("sNum", sNum);
		result.put("code", 0);
		writeToView(response, result);
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
		String searchComponent = request.getParameter("searchComponent");
		String searchState = request.getParameter("searchState");
		String searchWorker = request.getParameter("searchWorker");
		String searchType = request.getParameter("searchType");
		String searchBatch = request.getParameter("searchBatch");
		HashMap<String,Object> param = new HashMap<String,Object>();
		if(!StringUtils.isEmpty(searchState)){
			if(!searchState.trim().equals("Choose one")){
				param.put("state", searchState);
			}
		}
		if(!StringUtils.isEmpty(searchWorker)){
			if(!searchWorker.trim().equals("Choose one")){
				param.put("workerId", searchWorker);
			}
		}
		if(!StringUtils.isEmpty(searchType)){
			if(!searchType.trim().equals("Choose one")){
				param.put("searchType", searchType);
			}
		}
		param.put("searchBatch", searchBatch);
		param.put("componentName", searchComponent);
		List<HashMap<String,Object>> dataList = DaoContainer.dDao.downDivideHistory(param);
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
		titleArray.add(title5);
		String titleJson = JSONArray.fromObject(titleArray).toString();
		ExcelUtil.exportExcel(request, response, "distribution_result.xls", titleJson, dataList);
	}
}
