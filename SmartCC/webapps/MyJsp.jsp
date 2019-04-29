<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'MyJsp.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="http://www.jeasyui.net/Public/js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="http://www.jeasyui.net/Public/js/easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="http://www.jeasyui.net/Public/js/easyui/demo/demo.css">
	<script type="text/javascript" src="http://code.jquery.com/jquery-1.6.1.min.js"></script>
	<script type="text/javascript" src="http://www.jeasyui.net/Public/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="js/test.js"></script>
	<script type="text/javascript" src="js/jquery-form.js"></script>
	<script type="text/javascript" src="js/highcharts.js"></script>
	<script type="text/javascript" src="js/exporting.js"></script>
	<script type="text/javascript">
	
		</script>
  </head>
  
 <body class="easyui-layout" style="width:100%;height: 100%;">
	<div data-options="region:'north',title:'',split:true" style="width:100%;height: 8%;background-color: blue"></div>
	<div data-options="region:'center',title:'',split:true" style="width:100%;height: 84%;background-color: red">
		<div class="easyui-layout" style="width:100%;height: 100%">
			<div data-options="region:'west',title:'',split:true" style="width:10%;height: 100%;background-color: green"></div>
			<div data-options="region:'center',title:'',split:true" style="width:85%;height: 100%;background-color: yellow"></div>
		</div>
	</div>
	<div data-options="region:'south',title:'',split:true" style="width:100%;height: 8%;background-color: blue"></div>
</body>
</html>
