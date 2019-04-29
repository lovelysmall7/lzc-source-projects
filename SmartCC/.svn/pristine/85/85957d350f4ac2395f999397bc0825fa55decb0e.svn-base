package com.lzc.demo.controller.eidtmap;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzc.demo.controller.BaseServlet;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.service.editmap.EditMapService;
import com.lzc.demo.service.index.SmartCCIndexService;
import com.lzc.demo.util.ExcelHelper;

import net.sf.json.JSONObject;

@WebServlet("/editMap")
public class EditMapController extends BaseServlet{
	/**
	 * 加载地图信息
	 * @param request 
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void loadMapInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("loading Map ......");
		//加载地图信息
		MapInfo.getInstanse().getMapInfo();
		System.out.println("Map loaded......");
		//System.out.println(new EditMapService().aaa());
		//String tableHtml = new EditMapService().loadMapInfo(request);
		//request.setAttribute("tableHtml", tableHtml);
		//request.getRequestDispatcher("/views/editMap.jsp").forward(request, response);  
		Map<String,Object> resultMap = new EditMapService().buildMapEchartData();
		String centerX = (String) resultMap.get("centerX");
		String centerY = (String) resultMap.get("centerY");
		request.getSession().setAttribute("centerX", centerX);
		request.getSession().setAttribute("centerY", centerY);
		resultMap.put("code", 0);
		String jsonStr = JSONObject.fromObject(resultMap).toString();
		
		writeToView(response, jsonStr);
	}
	/**
	 * 定位一个工位的信息 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void findPoint(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String,Object> resultMap = new EditMapService().findPoint(request);
		resultMap.put("code", 0);
		String jsonStr = JSONObject.fromObject(resultMap).toString();
		writeToView(response, jsonStr);
	}
	/**
	 * 保存对地图的更改
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void saveMapChange(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		String centerx = (String) request.getSession().getAttribute("centerX");
		String centery = (String) request.getSession().getAttribute("centerY");
		
		int centerX = Integer.parseInt(centerx);
		int centerY = Integer.parseInt(centery);
		int result = 0;
		Date ASdate = new Date();
		String xAixs = request.getParameter("xAixs");
		String yAixs = request.getParameter("yAixs");
		String type = request.getParameter("type");
		String number = request.getParameter("number");
		try {
			Date BSdate = new Date();
			result = new EditMapService().saveMapChange(request);
			Date BEdate = new Date();
			printTimeWast(BSdate,BEdate,"保存地图信息 ");
			if(result == 0){
				//修改后重新加载地图
				Date CSdate = new Date();
				//MapInfo.getInstanse().reLoadMap();
				MapInfo.getInstanse().ChangeMemoryMapInfo(xAixs,yAixs,type,number);
				Date CEdate = new Date();
				printTimeWast(CSdate,CEdate,"同步内存地图信息  ");
				//修改地图成功 重新将图表信息推到前端
				Map<String,Object> map1 = new EditMapService().buildMapEchartData();//左侧全景地图
				Map<String,Object> map2 = new EditMapService().createSmallMapByCoordinate(centerX,centerY);//右侧局部地图
				resultMap.put("data1", map1);
				resultMap.put("data2", map2);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = -3;//-3 程序错误
		} 
		resultMap.put("code", result);
		String jsonStr = JSONObject.fromObject(resultMap).toString();
		writeToView(response, jsonStr);
		Date AEdate = new Date();
		printTimeWast(ASdate,AEdate,"保存地图总共 ");
		
	}
	private void printTimeWast(Date sdate,Date edate,String opn){
		System.out.println(opn + "操作耗时：" + (edate.getTime() - sdate.getTime())/1000 + "秒");
	}
	/**
	 * 下载地图文件
	 * @param request
	 * @param response
	 */
	public void downMapFile(HttpServletRequest request, HttpServletResponse response){
		try {
			new EditMapService().downMapFile(request,response,"map.xlsx");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
