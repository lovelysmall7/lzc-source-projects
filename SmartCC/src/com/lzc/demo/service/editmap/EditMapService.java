package com.lzc.demo.service.editmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.lzc.demo.dao.StationInfoDao;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.model.StationInfo;
import com.lzc.demo.util.ExcelHelper;
import com.lzc.demo.util.ExcelUtil;

public class EditMapService {
	public String loadMapInfo(HttpServletRequest request){
		//地图excel的存放路径
		String excelPath = this.getClass().getClassLoader().getResource("/").getFile() + "map.xlsx";
		excelPath = excelPath.substring(1,excelPath.length()); 
		ExcelHelper e = new ExcelHelper(excelPath);
		
		ArrayList<ArrayList<HashMap<String,Object>>> mapinfo = MapInfo.getInstanse().getMapInfo();
		if(mapinfo == null){
			e.loadMapInfo(excelPath);
		}
		StringBuilder tableHtml =  new StringBuilder();
		tableHtml.append("<table id='mapTable' bolder='0' cellpadding='0' cellspacing='0' style='width:100%;height:100%;background-color:#F4F6F6'>");
		for(int i = 0;i < mapinfo.size();i++){
			tableHtml.append("<tr>");
			ArrayList<HashMap<String,Object>> rowInfo = mapinfo.get(i);
			for(int j = 0;j < rowInfo.size();j++){
				HashMap<String,Object> hm = rowInfo.get(j);
				String colType = (String)hm.get("colType");
				String colorCode = "";
				String[] pointInfo = new String[3];
	    		pointInfo[0] = i + "";
	    		pointInfo[1] = j + "";
				//0:障碍，1路径，2：工位，3：柱子
				String title = i + "," + j + "," + colType;
				if("0".equals(colType)){
					pointInfo[2] = "x";
				}
				else if("1".equals(colType)){
					tableHtml.append("<td onmouseover='overShow(this)' title='"+title+"' onmouseout='outHide(this)' style='width:1px;height:1px;background-color:#ABEBC6'>");
					tableHtml.append(" ");
					pointInfo[2] = "o";
				}
				else if("2".equals(colType)){
					tableHtml.append("<td onmouseover='overShow(this)' title='"+title+"' onmouseout='outHide(this)' style='width:1px;height:1px;background-color:#85C1E9'>");	
					tableHtml.append(" ");
					pointInfo[2] = "工位";
				}
				else if("3".equals(colType)){
					tableHtml.append("<td onmouseover='overShow(this)' title='"+title+"' onmouseout='outHide(this)' style='width:1px;height:1px;background-color:#F7DC6F'>");
					tableHtml.append("柱子");
				}
				
				tableHtml.append("</td>");
			}
			tableHtml.append("</tr>");
			//System.out.println("共" + mapinfo.size() + "行。当前解析到：" + i + " 行");
		}
		tableHtml.append("</table>");
		return tableHtml.toString();
	}
	/**
	 * 构建支撑显示地图的echart数据
	 * @return
	 */
	public Map<String,Object> buildMapEchartData(){
		//地图excel的存放路径
		ArrayList<ArrayList<HashMap<String,Object>>> mapinfo = MapInfo.getInstanse().getMapInfo();
		ArrayList<Integer> xAxis = new ArrayList<Integer>();//x轴坐标集合
		ArrayList<Integer> yAxis = new ArrayList<Integer>();//y轴坐标集合
		ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
		int maxY = mapinfo.size() - 1;
		for(int i = 0;i < mapinfo.size();i++){
			yAxis.add(maxY + 1);
			maxY --;
			ArrayList<HashMap<String,Object>> rowInfo = mapinfo.get(i);
			for(int j = 0;j < rowInfo.size();j++){
				HashMap<String,Object> pointMap = new HashMap<String,Object>();
				String[] pointData = new String[4];
				
				if(i == 0){
					xAxis.add(j + 1);
				}
				HashMap<String,Object> hm = rowInfo.get(j);
				String colType = (String)hm.get("colType");
				String colValue = (String)hm.get("colValue");
				String[] pointInfo = new String[4];
				//excel 行列下标从0开始  坐标从1开始 需要 + 1
	    		pointInfo[0] = i + 1 + "";
	    		pointInfo[1] = j + 1 + "";
				//0:障碍，1路径，2：工位，3：柱子
				pointData[0] = j + 1 + "";
				pointData[1] = i + 1 + "";
				pointData[2] = colType;
				HashMap<String,Object> nomalMap = new HashMap<String,Object>();
				if("0".equals(colType)){
					nomalMap.put("color", "#FA8072");
					pointData[3] = "Obstacle";
				}
				if("1".equals(colType)){
					nomalMap.put("color", "#FAF0E6");	
					pointData[3] = "Way";
				}
				if("2".equals(colType)){
					nomalMap.put("color", "#85C1E9");
					pointData[3] = "Station :" + colValue;
					
				}
				if("3".equals(colType)){
					nomalMap.put("color", "#00FF00");
					pointData[3] = "Column :" + colValue;
				}
				
				HashMap<String,Object> itemMap = new HashMap<String,Object>();
				itemMap.put("normal", nomalMap);
				
				pointMap.put("itemStyle", itemMap);
				pointMap.put("value", pointData);
				pointMap.put("visualMap", false);
				data.add(pointMap);
			}
			//System.out.println("共" + mapinfo.size() + "行。当前解析到：" + i + " 行");
		}
		HashMap<String,Object> result = new HashMap<String,Object>();
		int centerX = xAxis.size() / 2;
		int centerY = yAxis.size() / 2;
		
		
		Map<String,Object> data1 = createSmallMapByCoordinate(centerX,centerY);
		result.put("xAxis",xAxis);
		result.put("yAxis",yAxis);
		result.put("data",data);
		result.put("data1",data1);
		result.put("centerX", centerX + "");
		result.put("centerY", centerY + "");
		return result;
	}
	/**
	 * 查找地图中的某个工位或柱子号
	 * @param request
	 * @return
	 */
	public Map<String,Object> findPoint(HttpServletRequest request){
		String searchNumber = request.getParameter("searchNumber");
		String xAxis = request.getParameter("xAxis");
		String yAxis = request.getParameter("yAxis");
		request.getSession().setAttribute("centerX", xAxis);
		request.getSession().setAttribute("centerY", yAxis);
		if(searchNumber != null && ! "".equals(searchNumber)){
			StationInfoDao dao = new StationInfoDao();
			StationInfo s = new StationInfo();
			s.setStationName(searchNumber);
			s = dao.findOneByParam(s);
			xAxis = s.getxAxis();
		    yAxis = s.getyAxis();
		}
		
		return createSmallMapByCoordinate(Integer.parseInt(xAxis),Integer.parseInt(yAxis));
	}
	
	/**
	 * 以目标点为中心返回一个小块地图
	 * @param xAxis
	 * @param yAxis
	 * @return
	 */
	public Map<String,Object> createSmallMapByCoordinate(Integer xAxis,Integer yAxis){
		int maxxAxis = xAxis + 10;
		int minxAxis = xAxis - 10;
		
		int maxyAxis = yAxis + 10;
		int minyAxis = yAxis - 10;
		ArrayList<ArrayList<HashMap<String,Object>>> mapinfo = MapInfo.getInstanse().getMapInfo();
		ArrayList<Integer> xAxisList = new ArrayList<Integer>();//x轴坐标集合
		ArrayList<Integer> yAxisList = new ArrayList<Integer>();//y轴坐标集合
		ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
		
		int rowNum = mapinfo.size();//总行数
		int colNum = mapinfo.get(0).size();//总列数
		
		if(maxxAxis > colNum){
			maxxAxis = colNum;
		}
		if(minxAxis < 0){
			minxAxis = 0;
		}
		if(maxyAxis > rowNum){
			maxyAxis = rowNum;
		}
		if(minyAxis < 0){
			minyAxis = 0;
		}
		int maxY = maxyAxis - 1;
		for(int i = minyAxis;i < maxyAxis;i++){
			yAxisList.add(maxY + 1);
			maxY --;
			ArrayList<HashMap<String,Object>> rowInfo = mapinfo.get(i);
			for(int j = minxAxis;j < maxxAxis;j++){
				HashMap<String,Object> pointMap = new HashMap<String,Object>();
				String[] pointData = new String[4];
				
				if(i == minyAxis){
					xAxisList.add(j + 1);
				}
				HashMap<String,Object> hm = rowInfo.get(j);
				String colType = (String)hm.get("colType");
				String colValue = (String)hm.get("colValue");
				String[] pointInfo = new String[4];
	    		pointInfo[0] = i + 1 + "";
	    		pointInfo[1] = j + 1 + "";
				//0:障碍，1路径，2：工位，3：柱子
				pointData[0] = j + 1 + "";
				pointData[1] = i + 1 + "";
				pointData[2] = colType;
				HashMap<String,Object> nomalMap = new HashMap<String,Object>();
				if("0".equals(colType)){
					nomalMap.put("color", "#FA8072");
					pointData[3] = "Obstacle";
					pointMap.put("name", "");
				}
				if("1".equals(colType)){
					nomalMap.put("color", "#FAF0E6");	
					pointData[3] = "Way";
					pointMap.put("name", "");
				}
				if("2".equals(colType)){
					nomalMap.put("color", "#85C1E9");
					pointData[3] = "Station :" + colValue;
					pointMap.put("name",colValue);
				}
				if("3".equals(colType)){
					nomalMap.put("color", "#00FF00");
					pointData[3] = "Column :" + colValue;
					pointMap.put("name",colValue);
				}
				
				HashMap<String,Object> itemMap = new HashMap<String,Object>();
				itemMap.put("normal", nomalMap);
				
				pointMap.put("itemStyle", itemMap);
				
				pointMap.put("value", pointData);
				pointMap.put("visualMap", false);
				data.add(pointMap);
			}
			//System.out.println("共" + mapinfo.size() + "行。当前解析到：" + i + " 行");
		}
		HashMap<String,Object> result = new HashMap<String,Object>();
		result.put("xAxis",xAxisList);
		result.put("yAxis",yAxisList);
		result.put("data",data);
		return result;
	}
	/**
	 * 更改地图信息
	 * 执行更改地图操作可能会涉及的改变：
	 * 1更改excel地图文件（增加障碍和去掉障碍）
	 * 2更改excel工位sheet（新增和删除工位）
	 * 3修改数据库station_info表（新增或删除工位）
	 * @param request
	 */
	public int saveMapChange(HttpServletRequest request)throws Exception{
		String xAixs = request.getParameter("xAixs");
		String yAixs = request.getParameter("yAixs");
		String type = request.getParameter("type");
		String number = request.getParameter("number");
		if(StringUtils.isEmpty(xAixs) || StringUtils.isEmpty(yAixs)){
			return -1;
		}
		if("0".equals(type) || "1".equals(type)){
			if(!StringUtils.isEmpty(number)){
				return -2;
			}
		}
		else{ 
			if(StringUtils.isEmpty(number)){
				return -3;
			}
		}
		//获取内存中的地图信息
		ArrayList<ArrayList<HashMap<String,Object>>> mapinfo = MapInfo.getInstanse().getMapInfo();
		Integer rowIndex = Integer.parseInt(yAixs) - 1;
		//获得该点所在行信息
		ArrayList<HashMap<String,Object>> rowInfo = mapinfo.get(rowIndex);
		Integer colIndex = Integer.parseInt(xAixs) - 1;
		//通过横坐标从其所在行获取单元格信息
		HashMap<String,Object> colInfo = rowInfo.get(colIndex);
		//获取目标修改点类型 0障碍1路径点2工位3柱子
		String colType = (String) colInfo.get("colType");
		String colName = (String) colInfo.get("colValue");
		if(!colType.equals(type)){
			if("0".equals(colType) || "0".equals(type)){
				String optCode = "";
				//原先的点和新改的点不一样 并且其中一个是障碍 则需要更改 excel地图文件
				if("0".equals(colType)){//移除障碍
					optCode = "D";
				}
				if("0".equals(type)){//添加障碍 原先的点是工位的话 还需要删除
					optCode = "A";
					if("2".equals(colType) || "3".equals(colType)){
						MapInfo.getInstanse().removeStation(colName);
					}
				}
				MapInfo.getInstanse().changeMapByCoordinate(Integer.parseInt(yAixs), Integer.parseInt(xAixs), optCode);
				System.out.println("更改地图文件：" + optCode + "  障碍");
			}
			//将工位或者柱子改为路径点 需要将数据库和excel中的工位信息删除
			//之前将这种情况漏掉了 20180227更正
			if("1".equals(type)){
				MapInfo.getInstanse().removeStation(colName);
			}
		}
		if("2".equals(type) || "3".equals(type)){
			//if(!number.equals(colName)){
			//这里需要再判断一下是不是需要删除原来的工位信息
			if("2".equals(colType) || "3".equals(colType)){
				//删除sheet数据  删除station_info 中的数据
				MapInfo.getInstanse().removeStation(colName);
				System.out.println("删除一条工位信息 ： " + colName);
			}
			//}
			//如果新点是工位 那么只要和原来的值不一样 就需要插入sheet一条数据  插入station_info 一条数据
			MapInfo.getInstanse().addOneStation(xAixs, yAixs, number, type);
			System.out.println("新增一条工位信息 ： " + number);
			
		}
		return 0;
	}
	public void downMapFile(HttpServletRequest request,HttpServletResponse response,String fileName) throws Exception{
		new ExcelUtil().downMapFile(request, response, fileName);
	}
}
