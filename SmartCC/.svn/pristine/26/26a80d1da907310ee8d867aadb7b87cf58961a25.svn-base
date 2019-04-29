package com.lzc.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;



public class SysStringUtil {
	public static String formatIntegerStr(String numstr) {
		String[] aaa = numstr.split("[.]");
		if (aaa.length > 0) {
			return aaa[0];
		}
		return null;
	}
	/**
	 * judge a str whether contain a letter or not 
	 * @param cardNum
	 * @return
	 */
	public static boolean judgeContainsStr(String cardNum) {  
        String regex=".*[a-zA-Z]+.*";  
        Matcher m=Pattern.compile(regex).matcher(cardNum);  
        return m.matches();  
    } 
	public static Object getParamFromFileReqeust(HttpServletRequest request, String paramName) {
		Object value = "";
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> list;
		try {
			list = upload.parseRequest(request);
			if (list == null) {
				return null;
			}
			if (list.size() == 0) {
				return null;
			}
			for (FileItem it : list) {
				if (it != null) {
					if (it.getSize() > 0) {
						if (paramName.equals(it.getFieldName())) {
							value = it.getString();
						}

					}
				}
			}
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return value;
	}
	/**
	 * 生成一个批次号 （年月日时分秒）
	 * @return
	 */
	public static String createBatchNo(){
		//得到long类型当前时间
		long l = System.currentTimeMillis();
		//new日期对象
		Date date = new Date(l);
		//转换提日期输出格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String batchNo = dateFormat.format(date);
		return batchNo;
	}
	public static String getCurrentBatch(){
		//得到long类型当前时间
		long l = System.currentTimeMillis();
		//new日期对象
		Date date = new Date(l);
		//转换提日期输出格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String currentBatch = dateFormat.format(date);
		return currentBatch;
	}
	public static void main(String[] args){
		System.out.println(createBatchNo());
	}
	
}
