package com.lzc.demo.util.oldcode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.lzc.demo.dao.DivideHistoryInfoDao;
import com.lzc.demo.dao.StationInfoDao;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.StationInfo;

public class EcharTestServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public EcharTestServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//System.out.println(this.getClass().getResource("").getPath());
		response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
		StationInfoDao dao = new StationInfoDao();
		/*List<StationInfo> stationList = dao.findAll();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for(int i = 0;i < stationList.size();i++){
			Map<String,Object> aMap = new HashMap<String,Object>();
			String[] aaa = new String[2];
			StationInfo s = stationList.get(i);
			aaa[0] = s.getxAxis();
			aaa[1] = s.getyAxis();
			aMap.put("name", "");
			aMap.put("value", aaa);
			list.add(aMap);
		}*/
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,Object>> listAAA= g("P00003,P00002");
		Map<String,Integer> aa = adjustCoordinate(listAAA);
		
		ArrayList<ArrayList<Object>> lineList = buildLinesBetweenPoints(listAAA,5);
		ArrayList<Object> finalLine = lineList.get(lineList.size() - 1);// 获得最后一组连线
		ArrayList<Object> arrowList = new ArrayList();
		for(int i = 0;i < finalLine.size();i++){
			Map<String,Integer> map = (Map<String, Integer>) finalLine.get(i);
			Integer pointAIndex = map.get("source");
			Integer pointBindex = map.get("target");
			Map<String,Object> pointMapA = listAAA.get(pointAIndex);
			Map<String,Object> pointMapB = listAAA.get(pointBindex);
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
		resultMap.put("data", listAAA);
		resultMap.put("range", aa);
		resultMap.put("lineData",lineList);
		resultMap.put("arrowList",arrowList);
		String resultJson = JSONObject.fromObject(resultMap).toString();
		out.write(resultJson);
        out.flush();
        out.close(); 
		System.out.println(resultJson);
	}
	/**---
	 * 根据集合中的点 调整坐标系x、y轴的范围 以达到尽量减少空白区域提升可视效果
	 * @param pointList
	 * @return map type: maxX:num maxY:num minX:num minY:mun
	 */
	public Map<String,Integer> adjustCoordinate(List<Map<String,Object>> pointList){
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
	//---
	public List<Map<String,Object>> g(String userIds){
		List<Map<String,Object>> listA = new ArrayList<Map<String,Object>>();
		StationInfoDao daoA = new StationInfoDao();
		
		DivideHistoryInfoDao  dao = new DivideHistoryInfoDao();
		//查询多个工人的任务分配信息
		List<DivideHistoryInfo> list = dao.findByWorkerIds(userIds);
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
			aMap.put("name","");
			aMap.put("color", "green");
			aMap.put("value", aaa);
			aMap.put("category", 1);
			aMap.put("category", userStationMap.get("workerNo"));
			listA.add(aMap);
			
		}
		return listA;
	}
	public Integer getRadomMum(int max,int min){
	    Random random = new Random();
	    return random.nextInt(max)%(max-min+1) + min;
	}
	/**
	 * 
	 * 为各个点之间随机产生连线方案（用于测试）
	 * @param pointList 
	 * @param changeTimes 变换次数
	 * @return
	 */
	public ArrayList<ArrayList<Object>> buildLinesBetweenPoints(List<Map<String,Object>> pointList,int changeTimes){
		ArrayList<ArrayList<Object>> finalList = new ArrayList<ArrayList<Object>>();
		for(int k = 0;k<changeTimes;k++){
			ArrayList<Object> lineList = new ArrayList<Object>();
			for(int i = 0;i < pointList.size();i++){
				Map<String,Integer> lineMap = new HashMap<String,Integer>();
				Integer source = getRadomMum(pointList.size() - 1,0);
				Integer target = getRadomMum(pointList.size() - 1,0);
				lineMap.put("source",source);
				lineMap.put("target", target);
				lineList.add(lineMap);
			}
			finalList.add(lineList);
		}
		
		return finalList;
	}
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
