<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Data Upload</title>  
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Upload tasks">
	
	<link rel="stylesheet" type="text/css" href="css/easyui.css">
	<link rel="stylesheet" type="text/css" href="css/buttonStyle.css">
	<link rel="stylesheet" href="css/monthSlide.css" type="text/css">
	<link rel="stylesheet" type="text/css" href="css/switchStyle.css">
	<script type="text/javascript" src="js/echarData.js"></script>
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="js/jquery-form.js"></script>
	<script type="text/javascript" src="js/echarts.min.js"></script>
	<script type="text/javascript" src="js/iradio.js"></script>
	<script type="text/javascript" src="js/pathOptimization.js"></script>
	<script type="text/javascript" src="js/smartcc_upload.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			getNewBatch();
			uploadTable_load();
			workerTable_load();
			$("#uploadBtn").click(function(){
				uploadTasks();
			});
			$("#assignmentBtn").click(function(){
				assignment();
			});
			$("#emergencyBtn").click(function(){
				$('#dlg').dialog('open').dialog('setTitle', 'Emergency');
				$('#fm').form('clear');
				$("#one").prop( "checked", true );
				$("#currentBatch1").val($("#currentBatch").val());
			});
			$('#searchGroup').combobox({    
		    	onChange : function(n,o){
		    	searchPersonByGroup();
		    	}  
			});
			$("#optimizeBtn").click(function(){
				startOptimize();
			});
		});
	</script>
	<style>
		 /**table{ border:1px solid #0094ff; }**/
		.layoutTable{
			background: #FFFFFF;
			/* background: url(../images/tableBG.png); */
			background-repeat:repeat; 
		}
		.l-btn-text {
			font-size: 9px;
		}
	</style>
  </head>
  <body>
  <div id="dlg" class="easyui-dialog"
		data-options="iconCls:'icon-save',resizable:true,modal:true"
		style="width:420px;height:210px;padding:10px 20px;top:300px" closed="true"
		buttons="#dlg-buttons">
		<div class="ftitle" style="font-size: 15px"></div>
		
		
		<form id="fm" name="fm" method="post"
			action="/SmartCC/smartccupload?method=uploadNewTaskExcel" data-options="novalidate:true" enctype="multipart/form-data">
			<input type="hidden" name="method" value="uploadNewTaskExcel">
			<table style="width: 100%">
				<tr>
					<td><font style="font-size: 10px">Choose file:&nbsp; </font></td>
					<td>
						<input type="hidden" id="currentBatch1" name="currentBatch1" value="">
						<input id="fileBox1" name=excelData class="easyui-filebox"  data-options="prompt:'Choose a excel（xls、xlsx）',buttonText:'&nbsp;Choose file &nbsp;'" style="width: 200px;">
					</td>
				</tr>
				
				<tr>
					<td><font style="font-size: 10px">Component: &nbsp; </font></td><td><input id="component" name="component"
					class="easyui-textbox" data-options="required:false" style="width: 200px;"></td>
				</tr>
				<tr>
					<td colspan="2" style="width: 100%">
						<table>
							<tr>
								<td>
									<div class="checkbox-group" id="qualified" style="width:115px;float:left">
										<input type="radio" id="one" name="isEmergency" value="1"/> 
										<label for="one" class="compTitle">Emergency</label>
									</div>
								</td>
								<td>
									<div class="checkbox-group">
										<input type="radio" id="two" name="isEmergency" value="0" /> <label
											for="two" class="compTitle">Normal</label>
									</div>
								</td>
								<td>
									<div class="checkbox-group" style="float:right">
										<input type="radio" id="three" name="isEmergency" value="2" /> <label
											for="three" class="compTitle">Recheck</label>
									</div>
								</td>
							</tr>
						</table>
						
					</td>
				</tr>
			</table>
			
			
		</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton  button blue"
			data-options="" onclick="addEmergency()" style="width:90px">OK</a> <a
			href="javascript:void(0)" class="easyui-linkbutton  button blue"
			data-options="" onclick="javascript:$('#dlg').dialog('close')"
			style="width:90px">Cancel</a>
	</div>
	
	
	
   <table style="width: 100%;height: 98%;border: 1" border="0">
	   	<tr >
	   		<!-- 左侧留白 -->
	   		<td style="width: 1%;height: 100%;">
	   		</td>
	   		<!-- 中间主体 -->
	   		<td style="width: 89%;height: 100%;">
	   			<table border="0" style="width: 100%;height: 100%;">
	   				<!-- 上方留白 -->
					<tr style="width: 100%;height: 3%;">
						<td colspan="2"></td>
					</tr>
					<tr style="width: 100%;height: 5%;">
							<td colspan="2">
								<div>
									<!-- <a class="logo" href="###" onclick="reLoad();return false;"><img alt="" src="images/bmwlogo.png" style="width: 4%;" class="logo"></a> -->
									<font style="font-size: 24px;color:#333;font-weight: 500;margin-top: 5%">Data Upload</font>
								</div>
							</td>
					</tr>
					<!-- 中间表格 -->
					<tr style="width: 100%;height: 88%;">
						<td style="width: 70%;height: 100%;">
							<table style="width: 100%;height: 100%" class="layoutTable">
								<!-- 上传按钮及各种查询条件 -->
								<tr style="width: 100%;height: 6%">
									<td >
									<form id="demoForm" class="easyui-layout" style="height: 100%" method="post" enctype="multipart/form-data" action="/SmartCC/smartccupload?method=uploadNewTaskExcel" method="post">
										Current Batch:<input id="currentBatch" name="currentBatch" class="easyui-textbox" style="width: 70px;height: 28px;" value="">
										<input id="fileBox" name="excelData" class="easyui-filebox" data-options="prompt:'*.xls、*.xlsx',buttonText:'&nbsp;Choose file &nbsp;'" style="width:15%;height: 28px;font-size: 9">
							    				<a href="javascript:void(0)" id="uploadBtn" class="easyui-linkbutton button" style="width: 68px;height: 28px;">
							    					<font style="font-size:9 ">Upload</font>
							    				</a>
							    				<a href="javascript:void(0)" id="emergencyBtn" class="easyui-linkbutton button" style="width: 68px;height: 28px;">
							    					<font style="font-size:9 ">Emergency</font>
							    				</a>
							    				
							    				
							    				<a href="####" class="easyui-linkbutton  button" id="optimizeBtn" style="width: 68px;height: 28px;float: right;" onclick="return false;">
														<font style="font-size: 9px">Optimize</font>
												</a>
							    				<input id="searchComponent" name="searchComponent" class="easyui-textbox" style="width: 60px;height: 28px;" data-options="prompt:'Part'">
							    				<select  id="searchType"  class="easyui-combobox" name="searchType" style="width:100px;height: 28px;">
													    <option value="" selected="selected">Choose one</option>
													    <option value="0">Nomal</option>
													    <option value="1">Emergency</option>
													    <option value="2">Recheck</option>
												</select>
												<!-- 查询按钮 -->
								    			<a href="####" id="searchBtn" onclick="searchData();return false;" class="easyui-linkbutton  button" style="width: 60px;height: 28px;">
							    					<font style="font-size:9 ">Search</font>
							    				</a>
							    				<a href="/SmartCC/servlet/ExportDivideHistoryInfoServlet" id="downLoadBtn" onclick="downLoad()" class="easyui-linkbutton button" style="width: 68px;height: 28px;">
												<!-- <a href="/SmartCC/servlet/ExportDivideHistoryInfoServlet" onclick="downLoad()" id="downLoadBtn" class="easyui-linkbutton button" style="width: 68px;height: 28px;"> -->
							    					<font style="font-size:9 ">Download</font>
							    				</a>
							    	</form>	
							    		
									</td>
								</tr>
								<tr style="width: 100%;height: 94%">
									<td>
										<!-- 上传任务列表 -->
										<table align="center" id="uploadTable" style="width:99%;height: 99%"></table>
									</td>
								</tr>
							</table>
							
						</td>
						<td style="width: 24%;height: 100%;">
							<table style="width: 100%;height: 100%" class="layoutTable">
								<!-- 上传按钮及各种查询条件 -->
								<tr style="width: 100%;height: 6%">
									<td>
										<select id="searchGroup"  class="" name="searchGroup" style="width:110px;height: 28px;">
										    <option value="" selected="selected"> Choose one</option>
										    <option value="A">A</option>
										    <option value="B">B</option>
										</select>
										<a href="####" class="easyui-linkbutton  button" id="assignmentBtn" style="width: 68px;height: 28px;" onclick="return false;">
											<font style="font-size: 9px">Assignment</font>
										</a>
									</td>
								</tr>
								<tr style="width: 100%;height: 94%">
									<td class="layoutTable">
										<!-- 人员列表 -->
										<table align="center" id="workerTable" style="width:99%;height: 99%"></table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<!-- 下方留白结束 -->
					<!-- 下方留白 -->
					<tr style="width: 100%;height: 2%;">
						<td colspan="2"></td>
					</tr>
					<!-- 下方留白结束 -->
	   			</table>
	   		</td>
	   		<!-- 右侧留白 -->
	   		<td style="width: 1%;height: 100%;">
	   		</td>
	   	</tr>
   </table>
  </body>
</html>