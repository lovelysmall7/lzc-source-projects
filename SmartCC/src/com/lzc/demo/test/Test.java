package com.lzc.demo.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.lzc.demo.dao.AreaInfoMappingDao;
import com.lzc.demo.dao.ComponentDao;
import com.lzc.demo.dao.StationInfoDao;
import com.lzc.demo.model.AreaInfoMapping;
import com.lzc.demo.model.Component;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.model.StationInfo;
import com.lzc.demo.service.index.SmartCCIndexService;
import com.lzc.demo.util.CalculationUtil;
import com.lzc.demo.util.ExcelHelper;
import com.lzc.demo.util.PropertiesUtil;
import com.lzc.demo.util.SqlMapInitUtil;
import com.lzc.demo.util.SysStringUtil;
import com.lzc.demo.util.Astar.AStar;
import com.lzc.demo.util.Astar.AStarMapInfo;
import com.lzc.demo.util.Astar.Node;

public class Test {
	public void change(String S,String Tstr){
		StationInfoDao sDao = new StationInfoDao();
		StationInfo sInfo = sDao.findById(S);
		if(sInfo != null){
			String x = sInfo.getxAxis();
			String y = sInfo.getyAxis();
			String[] a = Tstr.split(",");
			for(int i = 0;i < a.length;i++){
				String station = a[i];
				StationInfo info = sDao.findById(station);
				if(info != null){
					info.setxAxis(x);
					info.setyAxis(y);
					sDao.update(info);
					System.out.println(station + " 更新成功！ ");
				}
				else{
					System.out.println("t: << " + station + " 数据库中不存在！");
					StationInfo insertInfo = new StationInfo();
					insertInfo.setId(station);
					insertInfo.setStationName(station);
					insertInfo.setCreateTime(new Date());
					insertInfo.setType(1);
					insertInfo.setxAxis(x);
					insertInfo.setyAxis(y);
					sDao.insert(insertInfo);
					System.out.println(station + " 插入数据库成功！");
				}
			}
		}
		else{
			System.out.println("S : << " + S + " 数据库中不存在！" );
		}
	}
	private void importAreaStationMapping(String stationArrayStr,String areaId,int mappingType){
		String aaa[] = stationArrayStr.split("/");
		AreaInfoMappingDao dao = new AreaInfoMappingDao();
		for(int i = 0;i < aaa.length;i++){
			AreaInfoMapping a = new AreaInfoMapping();
			a.setId(UUID.randomUUID().toString());
			a.setAreaId(areaId);
			a.setMappingType(mappingType);
			a.setMappingValue(aaa[i]);
			a.setCreateTime(new Date());
			dao.insert(a);
		}
	}
	private void formatStationInfo(){
		StationInfoDao sDao = new StationInfoDao();
		List<StationInfo> sList = sDao.findAll();
		for(int i =0;i < sList.size();i++){
			StationInfo s = sList.get(i);
			String id = s.getId();
			//String 
		}
	}
	public static ArrayList<ArrayList<Integer>> buildMapData( ArrayList<ArrayList<HashMap<String,Object>>> mapinfo){
		//构建地图信息=--------------------
		ArrayList<ArrayList<Integer>> mapData = new ArrayList<ArrayList<Integer>>();
		for(int i = 0 ;i < mapinfo.size();i++){
			ArrayList<HashMap<String,Object>> row = mapinfo.get(i);
			ArrayList<Integer> rowData = new ArrayList<Integer>();
			for(int j = 0 ; j < row.size() ;j++){
				HashMap<String,Object> colInfo = row.get(j);
				String colType = (String)colInfo.get("colType");
				//0路径1障碍
				if("0".equals(colType)){
					rowData.add(1);
				}
				else{
					rowData.add(0);
				}
			}
			mapData.add(rowData);
		}
		return mapData;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String aaa = "01";
		//System.out.println(aaa.substring(0,1));
		//System.out.println(aaa.substring(1,2));
		//String p = PropertiesUtil.props.getProperty("pythonPath");
		//System.out.println(p);
		//new Test().change("I132","S87,S90");
		//new Test().importAreaStationMapping("S10/S09/S08/P81/P82/P83/P30/S03/S58/P09/S99/K79/S06/K56/S76/S16/K12/S59/S60/S61/S92/S91/K13/K81/K38/K91/S53/S55/K37/S80/S54/K93/K98/S35/S36/K54/K55/K35/K34/K36/S94/S95/K14","6",1);
		/*String excelPath = "D:/map.xlsx";
		ExcelHelper e = new ExcelHelper(excelPath);
		ArrayList<ArrayList<HashMap<String,Object>>> mapinfo = e.loadMapInfo(excelPath);
		ArrayList<ArrayList<Integer>> mapData = buildMapData(mapinfo);
		System.out.println(mapinfo.size());
		StationInfo s1 = new StationInfo(13,4);
		StationInfo s2 = new StationInfo(13,7);
		StationInfo s3 = new StationInfo(13,10);
		StationInfo s4 = new StationInfo(13,13);
			
			StationInfo s11 = new StationInfo(13,17);
			StationInfo s12 = new StationInfo(17,17);
			StationInfo s13 = new StationInfo(22,15);
			StationInfo s14 = new StationInfo(27,14);
			StationInfo s15 = new StationInfo(35,12);
			StationInfo s16 = new StationInfo(41,9);
			
		StationInfo s5 = new StationInfo(13,21);
		StationInfo s6 = new StationInfo(13,24);*/
		
		//第一种走法：
		/*CalculationUtil.getDistanse(mapData, s1, s2);
		CalculationUtil.getDistanse(mapData, s2, s3);
		CalculationUtil.getDistanse(mapData, s3, s4);
		CalculationUtil.getDistanse(mapData, s4, s5);
		CalculationUtil.getDistanse(mapData, s5, s6);
		
		CalculationUtil.getDistanse(mapData, s6, s11);
		CalculationUtil.getDistanse(mapData, s11, s12);
		CalculationUtil.getDistanse(mapData, s12, s13);
		CalculationUtil.getDistanse(mapData, s13, s14);
		CalculationUtil.getDistanse(mapData, s14, s15);
		CalculationUtil.getDistanse(mapData, s15, s16);*/
		
		//第二种走法
		/*CalculationUtil.getDistanse(mapData, s1, s2);
		CalculationUtil.getDistanse(mapData, s2, s3);
		CalculationUtil.getDistanse(mapData, s3, s4);
		CalculationUtil.getDistanse(mapData, s4, s11);
		CalculationUtil.getDistanse(mapData, s11, s12);
		CalculationUtil.getDistanse(mapData, s12, s13);
		CalculationUtil.getDistanse(mapData, s13, s14);
		CalculationUtil.getDistanse(mapData, s14, s15);
		CalculationUtil.getDistanse(mapData, s15, s16);
		CalculationUtil.getDistanse(mapData, s16, s5);
		CalculationUtil.getDistanse(mapData, s5, s6);*/
		//System.out.println("---------------------------:" + getDistanse(mapData, "420021", "K26"));//8
		//System.out.println("---------------------------:" + getDistanse(mapData, "400182", "430021"));//8
		/*System.out.println("---------------------------:" + getDistanse(mapData, "051401", "051412"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "051412", "051371"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "051371", "S06"));//8
		
		System.out.println("---------------------------:" + getDistanse(mapData, "S06", "050071"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "050071", "K79"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "K79", "S51"));//8
		
		System.out.println("---------------------------:" + getDistanse(mapData, "S51", "K38"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "K38", "051401"));//8
		
		System.out.println("---------------------------:" + getDistanse(mapData, "051401", "580031"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "580031", "580092"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "580092", "090031"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "090031", "K79"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "K79", "090061"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "090061", "K91"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "K91", "P30"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "P30", "080191"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "080191", "P30"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "P30", "080091"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "080091", "P81"));//8
		
		System.out.println("---------------------------:" + getDistanse(mapData, "P81", "K81"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "K81", "080091"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "080091", "S47"));//8
		
		System.out.println("---------------------------:" + getDistanse(mapData, "S47", "580031"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "580031", "580092"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "580092", "580031"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "580031", "580092"));//8
		
		System.out.println("---------------------------:" + getDistanse(mapData, "580092", "460052"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "460052", "460052"));//8
*/		/*System.out.println("---------------------------:" + getDistanse(mapData, "020051", "010051"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "010051", "040062"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "040062", "030052"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "030052", "030042"));//8
		
		System.out.println("---------------------------:" + getDistanse(mapData, "030042", "040062"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "040062", "010051"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "010051", "300141"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "300141", "330012"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "330012", "300141"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "300141", "320012"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "320012", "320042"));//8
		System.out.println("---------------------------:" + getDistanse(mapData, "320042", "320061"));//8
		//System.out.println("---------------------------:" + getDistanse(mapData, "320012", "320042"));//8
*/		
		String stations = "400022	,400021	,400021	,400042	,400042	,400052	,400041	,400041	,400061	,400082	,400092	,400112	,400112	,400112	,400111	,400142	,400141	,400182	,430021	,430021	,430021	,430021	,430021	,420042	,420031	,420021	,420021	,420011	,420011	,420011	";	

		stations = stations.replaceAll("\t", "");
		printTotalDistanse(stations.trim());
		String[] aaa = stations.split(",");
		System.out.println(aaa.length);
		String stations1 = "400021	,400021	,400022	,400041	,400041	,400052	,400042	,400042	,400061	,400082	,400092	,400112	,400112	,400112	,400111	,400141	,400142	,400182	,430021	,430021	,430021	,430021	,430021	,420042	,420031	,420021	,420021	,420011	,420011	,420011	";
		stations1 = stations1.replaceAll("\t", "");
		String[] bbb = stations1.split(",");
		System.out.println(bbb.length);
		printTotalDistanse(stations1.trim());
	}
	public static String getDistanse(ArrayList<ArrayList<Integer>> mapData,String stationA,String stationB){
		StationInfoDao sDao = new StationInfoDao();
		StationInfo station1 = sDao.findById(stationA);
		StationInfo station2 = sDao.findById(stationB);
		String sxStr = station1.getxAxis();
		String syStr = station1.getyAxis();
		sxStr = SysStringUtil.formatIntegerStr(sxStr);
		syStr = SysStringUtil.formatIntegerStr(syStr);
		Integer sx = Integer.parseInt(sxStr) - 1;
		Integer sy = Integer.parseInt(syStr) - 1;
		Node sNode = new Node(sx,sy);
		
		String exStr = station2.getxAxis();
		String eyStr = station2.getyAxis();
		exStr = SysStringUtil.formatIntegerStr(exStr);
		eyStr = SysStringUtil.formatIntegerStr(eyStr);
		Integer ex = Integer.parseInt(exStr) - 1;
		Integer ey = Integer.parseInt(eyStr) - 1;
		Node eNode = new Node(ex,ey);
		AStarMapInfo mapInfo = new AStarMapInfo();
		mapInfo.buildMap(mapData);	
		mapInfo.setInfo(sNode, eNode);
		AStar aStar = new AStar();
		aStar.start(mapInfo);
		Integer distanse = eNode.G;//起始位置和终止位置的距离
		return distanse.toString();
	}
	public static void printTotalDistanse(String stations){
		String excelPath = "D:/map.xlsx";
		ExcelHelper e = new ExcelHelper(excelPath);
		ArrayList<ArrayList<HashMap<String,Object>>> mapinfo = e.loadMapInfo(excelPath);
		ArrayList<ArrayList<Integer>> mapData = buildMapData(mapinfo);
		String[] aaa = stations.split(",");
		Integer totalDistanse = 0;
		for(int i = 0;i < aaa.length;i++){
			if(i != aaa.length - 1){
				totalDistanse += CalculationUtil.getDistanse(mapData, aaa[i], aaa[i+1]);
			}
		}
		System.out.println("total: " + totalDistanse + "############################");
	}
}
