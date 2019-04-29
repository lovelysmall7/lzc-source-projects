package com.lzc.demo.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	public static void exportExcel(
			HttpServletRequest request,
			HttpServletResponse resp
			,String fileName,
			String titleJson,
			List<HashMap<String,Object>> dataList) throws UnsupportedEncodingException
	{
		HSSFWorkbook wb = new HSSFWorkbook(); 
		request.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/x-download");
		fileName = URLEncoder.encode(fileName, "UTF-8");
		resp.addHeader("Content-Disposition", "attachment;filename=" + fileName);
		HSSFSheet sheet = wb.createSheet("数据导出");//工作表的名称
		sheet.setDefaultRowHeight((short) (2 * 200));
		sheet.setColumnWidth(0, 50 * 160);
		HSSFFont font = wb.createFont();//控制字体对象
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 16);
		HSSFRow row = sheet.createRow((int) 0);//这行是表头
		HSSFCellStyle style = wb.createCellStyle();//设置样式
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		JSONArray a = JSONArray.fromObject(titleJson);
		//第一个for循环打印出所有表头属性
		for(int i = 0;i < a.length();i++){
			JSONObject obj = (JSONObject) a.get(i);
			String label = obj.getString("label");
			String property = obj.getString("property");
			HSSFCell cell = row.createCell(i);
			if(!"operate".equals(property)){
				cell.setCellValue(label);
				style.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
				  // 前景色的设定    
				style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);    
				    // 填充模式    
				style.setFillPattern(HSSFCellStyle.FINE_DOTS);    
				// 设置字体    
			    HSSFFont headfont =wb.createFont();    
			    headfont.setFontName("黑体");    
			   headfont.setFontHeightInPoints((short) 13);// 字体大小    
			   headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗 
			   style.setFont(headfont);
				cell.setCellStyle(style);
			}
		}
		HSSFCellStyle dataStyle = wb.createCellStyle();//设置样式
		dataStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		if(dataList != null){
			Iterator it = dataList.iterator();
			int i = 1;
			while(it.hasNext()){//对数据每一行遍历
				HashMap<String,Object> obj = (HashMap<String, Object>) it.next();
				HSSFRow rowData = sheet.createRow(i);//创建一个行对象
				for(int j = 0;j < a.length();j++){//对表头集合遍历
					JSONObject obj1 = (JSONObject) a.get(j);//表头的每一列
					String property = obj1.getString("property");
					if("operate".equals(property)){//不导出操作列
						continue;
					}
					//为每一个新行写入列数据
					HSSFCell cell = rowData.createCell(j);
					if(obj1.has("width")){//获取每一列的列宽属性
						BigDecimal width = new BigDecimal(obj1.getString("width"));
						Integer fwidth = width.intValue();
						sheet.setColumnWidth(j, fwidth * 2000);
					}
					else{
						sheet.setColumnWidth(j, 10000);
					}
					Object valueObj = obj.get(property);
					String value = "";
					if(valueObj != null){
						value = valueObj.toString();
						if("state".equals(property)){
							if(value.equals("1")){
								value = "Assigned";
							}
							if(value.equals("2")){
								value="Completing";						
														}
							if(value.equals("3")){
								value="Completed";
							}
						}
					}
					cell.setCellValue(value);
					cell.setCellStyle(dataStyle);
				}
				i++;
			}
		}
		OutputStream out;
		try {
			out = resp.getOutputStream();
			wb.write(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 下载地图文件
	 * @param request
	 * @param resp
	 * @param fileName 下载后的文件名称
	 * @throws Exception 
	 */
	public void downMapFile(HttpServletRequest request,
			HttpServletResponse resp,String fileName) throws Exception{
		request.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/x-download");
		fileName = URLEncoder.encode(fileName, "UTF-8");
		resp.addHeader("Content-Disposition", "attachment;filename=" + fileName);
		String excelPath = this.getClass().getClassLoader().getResource("/").getFile() + "map.xlsx";
		//String excelPath ="D:/map.xlsx";
		excelPath = excelPath.substring(1,excelPath.length()); 
		FileInputStream excelFileInputStream = new FileInputStream(excelPath);
		// XSSFWorkbook 就代表一个 Excel 文件
		// 创建其对象，就打开这个 Excel 文件
		XSSFWorkbook wb = new XSSFWorkbook(excelFileInputStream);
		OutputStream out;
		try {
			out = resp.getOutputStream();
			wb.write(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
