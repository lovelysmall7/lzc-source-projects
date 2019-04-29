<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>Data Analysis</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="">
<script type="text/javascript" src="js/echarts.min.js"></script>
<link rel="stylesheet" type="text/css" href="css/easyui.css">
<link rel="stylesheet" type="text/css" href="js/themes/default/pagination.css">
<link rel="stylesheet" type="text/css" href="js/themes/default/datebox.css">
<link rel="stylesheet" type="text/css" href="css/buttonStyle.css">
<link rel="stylesheet" href="css/monthSlide.css" type="text/css">
<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
<script type="text/javascript" src="js/jquery-form.js"></script>
<script type="text/javascript" src="js/part2JsonData.js"></script>
<script type="text/javascript" src="js/SmartCC_part2.js"></script>
<script type="text/javascript" src="js/dateFormatter.js"></script>
<script type="text/javascript">
	var mypie;
	var mypie1;
	$(document).ready(function() {
		loadData();
		mypie = echarts.init(document.getElementById('container'));
		mypie.setOption(option, true);

		mypie1 = echarts.init(document.getElementById('gauge'));
		mypie1.setOption(pieOption, true);
		function openSearchWin(){
			$('#searchWin').dialog('open').dialog('setTitle', 'Search data');
		}
		$("#uploadBtn").click(function() {
			uploadTasks();
		});
		$("#checkBtn").click(function() {
			
			//$('#qualified').click();
			$('#dlg').dialog('open').dialog('setTitle', 'Component Check');
			$('#fm').form('clear');
		});
		$("#okBtn").click(function() {
			//todo
			updateTasks();
		});
		$("#searchBtnCom").click(function() {
			searchComponent();
		});
		$("#searchBtnTop").click(function() {
			searchTopTen();
		});
		$("#searchTasskBtn").click(function(){
			openSearchWin();
		});
		$("#searchWinokBtn").click(function(){
			searchTaskList();
		});
		$('#taskTable1').datagrid({
                onClickRow: function (rowIndex, rowData) {
                	$('#dlg1').dialog('open').dialog('setTitle', 'Show comment');
                	$("#showComment").val(rowData.remark);
                }
		});
		$(".datebox :text").attr("readonly","readonly");
		/* $('#taskTable1').datagrid('getPager').pagination({//分页栏下方文字显示
                      displayMsg:'当前显示从第{from}条到{to}条 共{total}条记录',
                      onBeforeRefresh:function(pageNumber, pageSize){
                      $(this).pagination('loading');
                       alert('pageNumber:'+pageNumber+',pageSize:'+pageSize);
                       $(this).pagination('loaded');
                    }
          }); */
          
	});
	
</script>
<style type="text/css">
a, img {
	border: 0;
}

.overlay {
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	z-index: 998;
	width: 100%;
	height: 100%;
	_padding: 0 20px 0 0;
	background: #f6f4f5;
	display: none;
}

.showbox {
	position: fixed;
	top: 0;
	left: 50%;
	z-index: 9999;
	opacity: 0;
	filter: alpha(opacity = 0);
	margin-left: -80px;
}

* html, * html body {
	background-image: url(about:blank);
	background-attachment: fixed;
}

* html .showbox, * html .overlay {
	position: absolute;
	top: expression(eval(document.documentElement.scrollTop));
}

#AjaxLoading {
	border: 1px solid #8CBEDA;
	color: #37a;
	font-size: 12px;
	font-weight: bold;
}

#AjaxLoading div.loadingWord {
	width: 180px;
	height: 50px;
	line-height: 50px;
	border: 2px solid #D6E7F2;
	background: #fff;
}

#AjaxLoading img {
	margin: 10px 15px;
	float: left;
	display: inline;
}

.dowebok {
	list-style-type: none;
}

.dowebok li {
	display: inline-block;
}

.dowebok li {
	margin: 10px 0;
}

.dowebok input.labelauty+label {
	font: 12px "Microsoft Yahei";
}

<!--遮罩层的css代码-->
		*{margin:0;padding:0;list-style-type:none;}
		a,img{border:0;}
		.demo{margin:100px auto 0 auto;width:400px;text-align:center;font-size:18px;}
		.demo .action{color:#3366cc;text-decoration:none;font-family:"微软雅黑","宋体";}
		
		.overlay{position:fixed;top:0;right:0;bottom:0;left:0;z-index:998;width:100%;height:100%;_padding:0 20px 0 0;background:#f6f4f5;display:none;}
		.showbox{position:fixed;top:0;left:50%;z-index:9999;opacity:0;filter:alpha(opacity=0);margin-left:-80px;}
		*html,*html body{background-image:url(about:blank);background-attachment:fixed;}
		*html .showbox,*html .overlay{position:absolute;top:expression(eval(document.documentElement.scrollTop));}
		#AjaxLoading{border:1px solid #8CBEDA;color:#37a;font-size:12px;font-weight:bold;}
		#AjaxLoading div.loadingWord{width:180px;height:50px;line-height:50px;border:2px solid #D6E7F2;background:#fff;}
		#AjaxLoading img{margin:10px 15px;float:left;display:inline;}
		.l-btn-text{
			font-size: 9;
		}
		 
</style>
</head>
<div style="display: none">
	<div id="dlg" class="easyui-dialog"
		data-options="iconCls:'icon-save',closable: true,resizable:true,modal:true" style="width:340px;height:260px;padding:10px 20px;top:200px" closed="true"
		buttons="#dlg-buttons">
		<div class="ftitle" style="font-size: 15px"></div>

		<div class="overlay"></div>

		<div id="AjaxLoading" class="showbox">
			<div class="loadingWord">
				<img src="waiting.gif">data loading ...
			</div>
		</div>
		<form id="fm" name="fm" method="post"
			action="/SmartCC/smartCCIndex?method=uploadEmergencyTaskExcel"
			data-options="novalidate:true" enctype="multipart/form-data">

			<div style="border:5px solid white;margin-bottom:10px">
				<div class="checkbox-group" id="qualified" style="width:115px;float:left">
					<input type="radio" id="one" name="dan" value="Qualified"/> <label for="one" class="compTitle">Qualified</label>
				</div>
				<div class="checkbox-group" id="Unqualified" style="width:115px;float:left">
					<input type="radio" id="two" name="dan" value="Unqualified" /> <label for="two" class="compTitle">Unqualified</label>
				</div>
			</div>
			<label class="datatimeTitle" style="margin-top:10px">Comment：</label>
			<textarea id="check-comment"
				style="width: 100%;height: 50%;overflow:hidden;margin-top:10px ">
					</textarea>
		</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" id="okBtn"
			class="easyui-linkbutton  button blue" style="width: 80px;height: 30px;">OK</a>
		<a href="javascript:void(0)" class="easyui-linkbutton  button blue"
			onclick="javascript:$('#dlg').dialog('close')"
			style="width: 80px;height: 30px;">Cancel</a>
	</div>
</div>

<div style="display: none">
<div id="dlg1" class="easyui-dialog"
		data-options="iconCls:'icon-save',closable: true,resizable:true,modal:true"
		style="width:340px;height:260px;padding:10px 20px;top:200px" closed="true"
		buttons="#dlg-buttons1">
		<div class="ftitle" style="font-size: 15px"></div>

		<div class="overlay"></div>

		<div id="AjaxLoading" class="showbox">
			<div class="loadingWord">
				<img src="waiting.gif">data loading ...
			</div>
		</div>
			<label class="datatimeTitle" style="margin-top:10px">Comment：</label>
			<textarea id="showComment"
				style="width: 100%;height: 80%;overflow:hidden;margin-top:10px" readonly="readonly">
					</textarea>
		
	</div>
	<div id="dlg-buttons1">
		<a href="javascript:void(0)" class="easyui-linkbutton  button blue"
			onclick="javascript:$('#dlg1').dialog('close')"
			style="width: 80px;height: 30px;">Cancel</a>
	</div>
</div>
<div id="searchWin" class="easyui-dialog"
		data-options="iconCls:'icon-save',closable: true,resizable:true,modal:true"
		style="width:340px;height:220px;padding:10px 20px;top:200px" closed="true"
		buttons="#searchWin-buttons">
		<div class="ftitle" style="font-size: 15px"></div>

		<div class="overlay"></div>

		<div id="AjaxLoading" class="showbox">
			<div class="loadingWord">
				<img src="waiting.gif">data loading ...
			</div>
		</div>
		<table>
			<tr>
				<td>Part</td><td><input id="component" name="component" class="easyui-textbox" data-options="required:false" style="width:170px;height: 22px;margin-left: 0px"></td>
			</tr>
			<tr>
				<td>Part station</td><td><input id="station" name="station" class="easyui-textbox" data-options="required:false" style="width:170px;height: 22px;margin-left: 0px"></td>
			</tr>
			<tr>
				<td>Worker</td>
				<td>
					<input id="cc" class="easyui-combobox" name="cc" 
												data-options="
												valueField:'id',
						    					textField:'name',
						    					url:'/SmartCC/smartCCIndex?method=loadWorkers'
						    					"  
						    					style="width:170px;height: 22px">
				</td>
			</tr>
			<tr>
				<td>Status</td>
				<td>
					<select  id="checkResult" class="easyui-combobox" name="checkResult" style="width:170px;height: 22px;margin-left: 0px；font-size: 11px" class="">
													    <option value="" selected="selected" style=""> Choose one</option>
													    <option value="-1">Unchecked</option>
													    <option value="0">Qualified</option>
													    <option value="1">Unqualified</option>
					</select>
				</td>
			</tr>
		</table>	
		
	</div>
	<div id="searchWin-buttons">
		<a href="javascript:void(0)" id="searchWinokBtn"
			class="easyui-linkbutton  button blue" style="width: 80px;height: 30px;">Search</a>
		<a href="javascript:void(0)" class="easyui-linkbutton  button blue"
			onclick="javascript:$('#searchWin').dialog('close')"
			style="width: 80px;height: 30px;">Cancel</a>
	</div>
<body>

	<table style="width: 100%;height: 100%">
		<tr>
			<!-- 左侧留白 -->
			<td style="width: 1%;height: 100%"></td>
			<!-- 中间主体的最外层 -->
			<td style="width: 90%;height: 100%">
				<table style="width: 100%;height: 100%">
					<!-- 上面的留白 -->
					<tr style="width: 100%;height: 0.5%">
						<td>
							<div class="timeContainer">
								<font style="font-size: 11px;font-weight:700;margin-left: 5px">Start Time：</font>
								<input id="searchStartTime" type="text" class="easyui-datebox" style="height: 22px;" data-options="formatter:myformatter,parser:myparser"></input>
								<font style="font-size: 11px;font-weight:700;margin-left: 5px">End Time：</font>
								<input id="searchEndTime" type="text" class="easyui-datebox" style="height: 22px;" data-options="formatter:myformatter,parser:myparser"></input>
								<script type="text/javascript">
									
								</script>
								<a href="javascript:void(0)" id="searchBtnCom"
									class="easyui-linkbutton button"
									style="width:65px;height: 22px;"> <font
									style="font-size:9 ">Search</font>
								</a>
							</div>
						</td>
					</tr>
					<tr style="width: 100%;height: 53%">
						<!-- 上面的两个echarts控件 -->
						<td>
							<table style="width: 100%;height: 100%">
								<tr>
									<td class="layoutContainer" style="width: 68%;height: 30%">
										<div id="container" style="width:100%; height:100%;"></div>
									</td>
									<td class="layoutContainer" style="width: 31%;height: 30%">
										<div id="gauge" style="width:100%; height:100%;"></div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr style="width: 100%;height: 40%">
						<!-- 下面的两个datagrid -->
						<td>
							<table style="width: 100%;height: 100%">
								<tr>
								<!-- 任务列表 -->
									<td class="layoutContainer" style="width: 50%;height: 100%">
										<div style="margin-bottom:1%;height: 5%">
											<a href="javascript:void(0)" id="checkBtn"
												class="easyui-linkbutton button"
												style="width:62px;height: 22px;margin-left:0.5%;float:left;"> 
												<font style="font-size:9px ">Check</font>
											</a>
											<a href="javascript:void(0)" id="downLoadBtn" onclick="downLoad()" class="easyui-linkbutton button" style="width:62px;height: 22px;margin-left:2%">
												<font style="font-size:9px ">Download</font>
											</a>
											<a href="javascript:void(0)" id="searchTasskBtn" class="easyui-linkbutton button" style="width:62px;height: 22px;margin-left:2%">
												<font style="font-size:9px ">Search</font>
											</a>
										</div>
											
										<table align="center" id="taskTable1" pagination="true"  pageSize="10"  class="easyui-datagrid"
											title="" style="height:90%;width:100%;"
											data-options="singleSelect:false,
											collapsible:true,
											url:'/SmartCC/smartCCpart2?method=searchTaskList',
											method:'get',
											striped:true,
											fitColumns:true
											">
											<thead>
												<tr>
													<th data-options="field:'ck',width:90,align:'center',checkbox:'true'">Choose</th>
													<!-- <th data-options="field:'taskNo',width:90,align:'center'">TaskNo.</th> -->
													<th data-options="field:'componentName',width:100,align:'center'">Part</th>
													<th data-options="field:'stationName',width:90,align:'center'">Part station</th>
													<th data-options="field:'type',width:90,align:'center'">Category</th>
													<th data-options="field:'workerId',width:90,align:'center'">Staff No.</th>
													<!--  <th data-options="field:'orderNum',width:130,align:'center'">Order</th>-->
													<th data-options="field:'createTimeStr',width:90,align:'center'">Date</th>
													
													<th data-options="field:'checkResult',width:90,align:'center',formatter:formatState">Status</th>
													<th data-options="field:'remark',width:90,align:'center',formatter:formatComment">Comment</th>
													</tr>
											</thead>
										</table>
									</td>
									<!-- 任务列表结束 -->
									<td class="layoutContainer" style="width: 20%;height: 100%">
									<div style="margin-bottom:1%;;height: 10%">
										<label class="datatimeTitle">Date：</label>
										<input id="searchTime" type="text" class="easyui-datebox" style="height: 22px;" data-options="formatter:myformatter,parser:myparser"></input> 
										<a href="javascript:void(0)" id="searchBtnTop" class="easyui-linkbutton button" style="width: 65px;height: 22px;"> <font style="font-size:9">Search</font></a>
									</div>
										<table align="center"  id="top10Table" class="easyui-datagrid"
											title="" style="height:90%;width:100%;"
											data-options="
											singleSelect:true,
											collapsible:true,
											url:'',
											striped:true,
											fitColumns:true
											">
											<thead>
												<tr>
													<th data-options="field:'orderNum',width:60,align:'center',formatter:formatterNum">No.</th>
													<th data-options="field:'workerId',width:100,align:'center'">Staff No.</th>
													<th data-options="field:'name',width:80,align:'center'">Name</th>
													<th data-options="field:'componentNum',width:120,align:'center'">Part Num</th>
												</tr>
											</thead>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<!-- 下面的留白 -->
					<tr style="width: 100%;height: 1%">
						<td></td>
					</tr>
				</table>
			</td>
			<!-- 右侧留白 -->
			<td style="width: 5%;height: 100%"></td>
		</tr>
	</table>
	<script type="text/javascript">

</script>
</body>
</html>
