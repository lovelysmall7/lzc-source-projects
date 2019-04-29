package com.lzc.demo.util.Astar;

import java.util.ArrayList;

/**
 * 
 * ClassName: MapInfo 
 * @Description: 包含地图所需的所有输入数据
 * @author kesar
 */
public class AStarMapInfo
{
	public int[][] maps; // 二维数组的地图
	public int width; // 地图的宽
	public int hight; // 地图的高
	public Node start; // 起始结点
	public Node end; // 最终结点
	public AStarMapInfo(){
		
	}
	public AStarMapInfo(int[][] maps)
	{
		this.maps = maps;
		this.width = maps.length - 1;
		this.hight = maps[0].length - 1;
		
	}
	public void setInfo(Node start, Node end){
		this.start = start;
		this.end = end;
		System.out.println("当前计算起点____" + "X:" + start.coord.x + " Y: " + start.coord.y);
		System.out.println("当前计算终点____" + "X:" + end.coord.x + " Y: " + end.coord.y);
	}
	public void buildMap(ArrayList<ArrayList<Integer>> mapData){
		maps = new int[mapData.size()][mapData.get(0).size()];
		for(int i = 0;i < mapData.size();i++){
			ArrayList<Integer> cList = mapData.get(i);
			for(int j = 0;j < cList.size();j++){
				maps[i][j] = cList.get(j);
			}
		}
		this.width = maps[0].length - 1; 
		this.hight = maps.length - 1;
	}
}
