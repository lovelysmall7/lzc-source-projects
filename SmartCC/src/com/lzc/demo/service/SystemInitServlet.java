package com.lzc.demo.service;



import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebServlet;

import com.lzc.demo.model.MapInfo;



/**
 * Servlet implementation class SystemInitServlet
 */
@WebServlet("/SystemInitServlet")
public class SystemInitServlet  implements ServletContextListener{
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("loading Map ......");
		//加载地图信息
		MapInfo.getInstanse().getMapInfo();
		System.out.println("Map loaded......");
		
		System.out.println("Building map path points ......");
		MapInfo.getInstanse().getMapData();
		System.out.println("Build map path points over......");
		
		System.out.println("Building Astartmap instanse ......");
		MapInfo.getInstanse().getAstartMapInfo();
		System.out.println("Build Astartmap instanse over......");
	}
    
   
}