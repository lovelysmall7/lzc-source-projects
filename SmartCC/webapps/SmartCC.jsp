<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>人员任务分配</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="css/easyui.css">
	<link rel="stylesheet" type="text/css" href="http://www.jeasyui.net/Public/js/easyui/themes/icon.css">
	
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="js/test.js"></script>
	<script type="text/javascript" src="js/jquery-form.js"></script>
	<script type="text/javascript" src="js/highcharts.js"></script>
	<script type="text/javascript" src="js/exporting.js"></script>
	
	<script type="text/javascript">
	var options = {
						    chart: {
						    	renderTo: 'container',
						        type: 'column'
						    },
						    credits: {
				            	text: '',
				            	href: ''
				        	},
						    title: {
						        text: '工作完成情况分析图'
						    },
						    xAxis: {
						        categories: []
						    },
						    yAxis: {
						        min: 0,
						        title: {
						            text: 'Total fruit consumption'
						        },
						        stackLabels: {
						            enabled: true,
						            style: {
						                fontWeight: 'bold',
						                color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
						            }
						        }
						    },
						    legend: {
						        align: 'right',
						        x: -30,
						        verticalAlign: 'top',
						        y: 25,
						        floating: true,
						        backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
						        borderColor: '#CCC',
						        borderWidth: 1,
						        shadow: false
						    },
						    tooltip: {
						        headerFormat: '<b>{point.x}</b><br/>',
						        pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
						    },
						    plotOptions: {
						        column: {
						            stacking: 'normal',
						            dataLabels: {
						                enabled: true,
						                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white'
						            }
						        }
						    },
						    series: []
						}
		function loadData(){
			$.post( "/SmartCC/servlet/LoadDataServlet", {"aaa":"aaa"},function (data){
					if(data.code == 0){
						$('#taskTable').datagrid('loadData', data.tList);
						$('#divideResult').datagrid('loadData', data.dList);
						var categories = data.categories;
						var series = data.series;
						options.series = series;
						options.xAxis.categories = categories;
						var chart = new Highcharts.Chart(options);
						$('#p').progressbar('setValue', data.sNum);
					}
				},"json" );
		}
		//点击搜索按钮触发
		$(document).ready(function (){
			var h = $(document).height();
			$(".overlay").css({"height": h });	
			$("#searchBtn").click(function(){
				var searchWorker = $("#cc").combobox('getValue');
				var searchState = $("#searchState").combobox('getValue');
				$.post( "/SmartCC/servlet/SearchDivideResultServlet", 
				{ 
					searchWorker: searchWorker,
					searchState:searchState
				},
				function (data){
					if(data.code == 0){
						$('#divideResult').datagrid('loadData', data.data);
					}
				},"json" );
			});
			loadData();//加载数据
			var chart = new Highcharts.Chart(options);
			$("#uploadBtn").click(function (){
			//添加加载遮罩层
					$(".overlay").css({'display':'block','opacity':'0.8'});
					$(".showbox").stop(true).animate({'margin-top':'300px','opacity':'1'},200);
					$('#demoForm').ajaxSubmit(function (data) {
					//响应后移除遮罩层
					$(".showbox").stop(true).animate({'margin-top':'250px','opacity':'0'},400);
					$(".overlay").css({'display':'none','opacity':'0'});
                     $('#taskTable').datagrid('loadData', data.data);
                     $("#tt").datagrid('loadData',data.wList);
                     var categories = data.categories;
					var series = data.series;
					options.series = series;
					options.xAxis.categories = categories;
					var chart = new Highcharts.Chart(options);
					$('#p').progressbar('setValue', data.sNum);
                 });
			});
			$("#submitSchemeBtn").click(function(){
				var rows= $('#tt').datagrid('getSelections');
				var workersJson = JSON.stringify(rows);
				$.post( "/SmartCC/servlet/SubmitScheme", { workersJson: workersJson},function (data){
					if(data.code == 0){
						alert("分配成功");
						$('#tt').datagrid('reload');
						$('#taskTable').datagrid('loadData', data.data);
						$('#divideResult').datagrid('loadData', data.data1);
						var categories = data.categories;
						var series = data.series;
						options.series = series;
						options.xAxis.categories = categories;
						var chart = new Highcharts.Chart(options);
					}
				},"json" );
			});
			$('#tt').datagrid({
				title:'工作人员列表',
				singleSelect:false,
				idField:'id',
				method:'get',
				url:'/SmartCC/servlet/LoadWorkersService',
				columns:[[
					{field:'dh',align:'center',title:'选择',checkbox:'true',width:80},
					{field:'id',align:'center',title:'人员编号',width:180},
					{field:'name',align:'center',title:'姓名',width:120},
					{field:'currentStation',align:'center',title:'当前位置',width:60,
						formatter:function(value,row,index){
							if (row.currentStation == "" || row.currentStation == null){
								return row.officeId;
							} 
							return row.currentStation;
						}
						
					},
					{field:'taskNum',align:'center',title:'任务数量',width:100,editor:{type:'numberbox',options:{precision:0}}},
					{field:'action',align:'center',title:'操作',width:100,align:'center',
						formatter:function(value,row,index){
							if (row.editing){
								var s = '<pan class="icon-save">&nbsp&nbsp&nbsp&nbsp&nbsp</pan><a href="javascript:void(0)" class="easyui-linkbutton"  onclick="saverow(this)">保存</a> ';
								return s;
							} else {
								var e = '<pan class="icon-edit">&nbsp&nbsp&nbsp&nbsp&nbsp</pan><a href="javascript:void(0)" class="easyui-linkbutton"  onclick="editrow(this)">编辑</a> ';
								return e;
							}
						}
					}
				]],
				onBeforeEdit:function(index,row){
					row.editing = true;
					updateActions(index);
				},
				onAfterEdit:function(index,row){
					row.editing = false;
					updateActions(index);
				},
				onCancelEdit:function(index,row){
					row.editing = false;
					updateActions(index);
				},
				onClickRow: function (rowIndex)  
                {  
                    var row = $('#tt').datagrid('getSelected');  
                    if (row){
						var index = $('#tt').datagrid('getRowIndex', row);
					} else {
						index = 0;
					}
					//$('#tt').datagrid('selectRow',index);
					$('#tt').datagrid('beginEdit',index);
					$('#tt').datagrid('endEdit', getRowIndex(index));
                }  
			});//datagrid over
			
		});
		function updateActions(index){
			$('#tt').datagrid('updateRow',{
				index: index,
				row:{}
			});
		}
		function getRowIndex(target){
			var tr = $(target).closest('tr.datagrid-row');
			return parseInt(tr.attr('datagrid-row-index'));
		}
		function editrow(target){
			$('#tt').datagrid('beginEdit', getRowIndex(target));
		}
		function deleterow(target){
			$.messager.confirm('Confirm','Are you sure?',function(r){
				if (r){
					$('#tt').datagrid('deleteRow', getRowIndex(target));
				}
			});
		}
		function saverow(target){
			$('#tt').datagrid('endEdit', getRowIndex(target));
		}
		function cancelrow(target){
			$('#tt').datagrid('cancelEdit', getRowIndex(target));
		}
		function insert(){
			var row = $('#tt').datagrid('getSelected');
			if (row){
				var index = $('#tt').datagrid('getRowIndex', row);
				
			} else {
				index = 0;
			}
			$('#tt').datagrid('insertRow', {
				index: index,
				row:{
					status:'P'
				}
			});
			$('#tt').datagrid('selectRow',index);
			$('#tt').datagrid('beginEdit',index);
		}
		function formatOper(val,row,index){ 
			if(row.state == 1){
				return '<a href="javascript:void(0)" onclick="updateState('+index+')">开始</a>';  
			} 
    		else{
    			return '<a href="javascript:void(0)" onclick="updateState('+index+')">完成</a>';  
    		}
		} 
		function updateState(index){
			$('#divideResult').datagrid('selectRow',index);// 关键在这里  
    		var row = $('#divideResult').datagrid('getSelected');
    		var id = row.id;
    		var taskIds = row.taskIds;  
    		var state = row.state;
    		var workerId = row.workerId;
    		$.post( "/SmartCC/servlet/HandleChangeStateService", { id:id,taskIds:taskIds,state:state,workerId:workerId},function (data){
					if(data.code == 0){
						$('#taskTable').datagrid('loadData', data.data);
						$('#divideResult').datagrid('loadData', data.data1);
						$('#tt').datagrid('loadData', data.data2);
						var categories = data.categories;
						var series = data.series;
						options.series = series;
						options.xAxis.categories = categories;
						var chart = new Highcharts.Chart(options);
						$('#p').progressbar('setValue', data.sNum);
					}
				},"json" );
		}
		//格式化每一行的状态列
		function formatterState(val,row){
			if(val == 0){
				return "<span style='color: red'>未分配</span>" 
			}
			if(val == 1){
				return "<span style='color: orange'>已分配</span>" 
			}
			if(val == 2){
				return "<span style='color: blue'>完成中</span>" 
			}
			if(val == 3){
				return "<span style='color: green'>已完成</span>" 
			}
		}
	</script>
	
	<style>
		.progressbar-value .progressbar-text{
			background-color: #c1ffe4;
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
	</style>
</head>

	<body class="easyui-layout" style="width:100%;height: 800px;background-color: #acd6ff">
	<div class="overlay"></div>

	<div id="AjaxLoading" class="showbox">
		<div class="loadingWord"><img src="waiting.gif">加载中，请稍候...</div>
	</div>
	<form id="demoForm"  method="post" enctype="multipart/form-data" action="/SmartCC/servlet/HandleRequestService" method="post"> 
		    <div data-options="region:'north',title:'',split:false" style="height:55px;">
		    
		    	<table width="100%" style="background-color: #acd6ff">
		    		<tr>
		    			<td style="width: 4%;height: 55px">
		    				<img alt="" src="images/bmwlogo.png" style="width: 50px;">
		    			</td>
		    			<td style="width: 20%;height: 55px;"> 
		    				<input name="excelData" class="easyui-filebox"  data-options="prompt:'选择excel文件（xls、xlsx）',buttonText:'&nbsp;选&nbsp;择&nbsp;文&nbsp;件&nbsp;'" style="width:400px;height: 35px">
		    				<a href="javascript:void(0)" id="uploadBtn" class="easyui-linkbutton" style="width: 100px;height: 35px;">解  析</a>
		    			</td>
		    			<td width="4%">
		    				完成比例：
		    			</td>
		    			<td style="width: 26%;height: 55px;">
		    				<div id="p" class="easyui-progressbar" data-options="value:0" style="width:400px;height: 35px;background-color: #FFFFFF"></div>
		    			</td>
		    		</tr>
		    	</table>
		    	
		    	
		    </div>
		    
		    <div data-options="region:'west',title:'',split:false" style="width:1000px;background-color: #acd6ff">
		    	 <div class="easyui-layout" style="width:99%;height: 740px">
		    	 	<div data-options="region:'north',title:'',split:false" style="height:400px;">
		    			<table id="taskTable" class="easyui-datagrid" title="当日工作内容" style="height:390px;"
						data-options="singleSelect:true,collapsible:true,url:'',method:'get'">
							<thead>
								<tr>
									<th data-options="field:'id',width:160,align:'center'">任务编号</th>
									<th data-options="field:'componentName',width:160,align:'center'">零件信息</th>
									<th data-options="field:'stationName',width:200,align:'center'">工位信息</th>
									<th data-options="field:'orderNum',width:100,align:'center'">类别</th>
									<th data-options="field:'workerId',width:140,align:'center'">staff No</th>
									<th data-options="field:'orderNum',width:120,align:'center'">顺序</th>
									<th data-options="field:'state',width:80,align:'center',formatter:formatterState">状态</th>
								</tr>
							</thead>
						</table>
			   		 </div>
			   		 <div id="container" data-options="region:'center',title:'',split:false" style="width:99%; height:300px;">
			    			
			   		 </div>
		    	 </div>
		    	 
		    </div>
		    <div data-options="region:'center',title:'',split:false" style="background-color: #acd6ff">
		    	<div class="easyui-layout" style="width:100%;height: 740px;background-color: #acd6ff">
		    		<div data-options="region:'north',title:'',split:false" style="height:400px;background-color: #acd6ff">
		    			<input type="button" class="easyui-linkbutton" style="height: 35px;width: 100px;" id="submitSchemeBtn" value="确定分配">
		    			<table id="tt" style="height: 390px"></table>
			    		
		    		</div>
		    		<div data-options="region:'center',title:'',split:false" style="height:340px;">
		    			选择人员：<input id="cc" class="easyui-combobox" name="dept"
    					data-options="valueField:'id',textField:'name',url:'/SmartCC/servlet/LoadWorkersService'" style="width: 180px;height: 30px">
						  完成状态： <select id="searchState" class="easyui-combobox" name="dept" style="width:150px;height: 30px;">
							    <option value="1">已分配</option>
							    <option value="2">已开始</option>
							    <option value="3">已完成</option>
							</select>
							<a href="javascript:void(0)" id="searchBtn" class="easyui-linkbutton" style="width: 100px;height: 30px;">搜  索</a>
		    			<table id="divideResult" class="easyui-datagrid" title="分配结果" style="height:100%"
						data-options="singleSelect:true,collapsible:true,url:'',method:'get'">
							<thead>
								<tr>
									<th data-options="field:'componentName',width:130,align:'center'">零件信息</th>
									<th data-options="field:'stationNames',width:150,align:'center'">工位信息</th>
									<th data-options="field:'workerId',width:130,align:'center'">staff No</th>
									<th data-options="field:'state',width:100,align:'center',formatter:formatterState">当前状态</th>
									<th data-options="field:'operate',width:120,formatter:formatOper,align:'center'">操作</th>
								</tr>
							</thead>
						</table>
		    		</div>
		    	</div>
		    </div>
	    </form>
	</body>
</html>