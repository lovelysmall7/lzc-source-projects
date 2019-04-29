package com.lzc.demo.service.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.lzc.demo.dao.DivideHistoryInfoDao;
import com.lzc.demo.dao.WorkerInfoDao;
import com.lzc.demo.model.ComponentCheckInfo;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.TaskInfo;
import com.lzc.demo.model.WorkerInfo;
import com.lzc.demo.util.CharDataUtil;
import com.lzc.demo.util.ExcelHelper;

import net.sf.json.JSONObject;

public class SmartCCPart2Service {
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
                    List<ComponentCheckInfo> dataList = helper.analysis1(item.getInputStream(),"close");
                    result.put("code", "0");
                    result.put("data", dataList);
                    
                } else {
                    
                    return "-1";
                }
            String resultJson = JSONObject.fromObject(result).toString();
            return resultJson;
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "-2";
        }
	}
}
