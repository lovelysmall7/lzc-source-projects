package com.lzc.demo.util.oldcode;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.lzc.demo.model.MapInfo;
import com.lzc.demo.util.ExcelHelper;

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
		for(int i = 0;i < mapinfo.size()/2;i++){
			tableHtml.append("<tr>");
			ArrayList<HashMap<String,Object>> rowInfo = mapinfo.get(i);
			for(int j = 0;j < rowInfo.size()/2;j++){
				HashMap<String,Object> hm = rowInfo.get(j);
				String colType = (String)hm.get("colType");
				String colorCode = "";
				//0:障碍，1路径，2：工位，3：柱子
				String title = i + "," + j + "," + colType;
				if("0".equals(colType)){
					tableHtml.append("<td onmouseover='overShow(this)' title='"+title+"' onmouseout='outHide(this)' style='width:1px;height:1px;background-color:#F5CBA7'>");
					tableHtml.append(" ");
				}
				else if("1".equals(colType)){
					tableHtml.append("<td onmouseover='overShow(this)' title='"+title+"' onmouseout='outHide(this)' style='width:1px;height:1px;background-color:#ABEBC6'>");
					tableHtml.append(" ");
				}
				else if("2".equals(colType)){
					tableHtml.append("<td onmouseover='overShow(this)' title='"+title+"' onmouseout='outHide(this)' style='width:1px;height:1px;background-color:#85C1E9'>");	
					tableHtml.append(" ");
				}
				else if("3".equals(colType)){
					tableHtml.append("<td onmouseover='overShow(this)' title='"+title+"' onmouseout='outHide(this)' style='width:1px;height:1px;background-color:#F7DC6F'>");
					tableHtml.append(" ");
				}
				
				tableHtml.append("</td>");
			}
			tableHtml.append("</tr>");
			System.out.println("共" + mapinfo.size() + "行。当前解析到：" + i + " 行");
		}
		tableHtml.append("</table>");
		return tableHtml.toString();
	}
	
}
