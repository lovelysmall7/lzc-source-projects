<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Map editor</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<style>
	table{
		border-style: solid;
	}
</style>
  </head>
  
  <body>
  <!-- 最外层table -->
    <table width="100%" height="100%" >
    	<tr>
    		<!-- 左侧留白 -->
    		<td style="width: 10%;height: 100%">1</td>
    		<!-- 中间主体 -->
    		<td>
    			<table style="width: 100%;height: 100%">
    			 	<!-- 上方header -->
    				<tr>
    					<td style="background-color: #4dbbf1;width: 100%;height: 7%;vertical-align: middle;line-height: 100%" >
    						<table style="width: 100%;height: 100%">
    							<tr>
    								<td style="width: 4%"><img src="images/bmwlogo.png" style="height: 25%" /></td>
    								<td style="width: 80%">
    									 <font style="height:100%;color: #FFFFFF;text-align:left ;font-weight: bold;font-size: 30px;vertical-align: middle;">Map editor</font>
    								</td>
    							</tr>
    						</table>
    					</td>
    				</tr>
    				<tr>
	    				<td style="width: 100%;height:90%">
	    					<!-- 真正包含内容的table -->
	    					<table style="width: 100%;height: 100%">
	    						<tr>
	    							<!-- 上方工具栏 -->
		    						<td>01</td>
		    						<!-- 下方小地图 -->
		    						<td>02</td>
	    						</tr>
	    						<tr>
	    						    <!-- 大地图 -->
	    							<td rowspan="2">03</td>
	    						</tr>
	    					</table>
	    				</td>
    				</tr>
    				<!-- 下方footer -->
    				<tr>
	    				<td style="width:100%;height:3%;background-color: #4dbbf1;">
	    					3
	    				</td>
    				</tr>
    			</table>
    		</td>
    		<!-- 右侧留白 -->
    		<td style="width: 10%;height: 100%;">2</td>
    	</tr>
    </table>
    <!-- 最外层table结束 -->
  </body>
</html>
