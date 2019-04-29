package com.lzc.demo.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
/**
 * 所有servlet的基类，所有请求的入口
 * 根据method参数派发到指定方法
 * @author lizc
 *
 */
public class BaseServlet extends HttpServlet {

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        //利用反射得到要执行的方法
        HttpServletRequest request=(HttpServletRequest) req;
        HttpServletResponse response=(HttpServletResponse) res;
        
        String method=request.getParameter("method");
        try {
            //得到名称为method的方法
            Method m = this.getClass().getDeclaredMethod(method, HttpServletRequest.class,HttpServletResponse.class);
            m.invoke(this, request,response);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
    }
    /**
     * 
     * 传送json到前端
     * @param response
     * @param jsonStr
     */
    protected void writeToView(HttpServletResponse response,String jsonStr){
    	response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        PrintWriter out;
		try {
			out = response.getWriter();
			out.write(jsonStr);
	        out.flush();
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    /**
     * 
     * 传送json到前端
     * @param response
     * @param jsonStr
     */
    protected void writeToView(HttpServletResponse response,HashMap<String,Object> resultMap){
    	String jsonStr = JSONObject.fromObject(resultMap).toString();
    	response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        PrintWriter out;
		try {
			out = response.getWriter();
			out.write(jsonStr);
	        out.flush();
	        out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
}