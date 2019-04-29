package com.lzc.demo.service;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lzc.demo.model.MapInfo;

public class SysInitServlet extends HttpServlet {

	/**
		 * Constructor of the object.
		 */
	public SysInitServlet() {
		super();
	}

	
	public void init() throws ServletException {
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
