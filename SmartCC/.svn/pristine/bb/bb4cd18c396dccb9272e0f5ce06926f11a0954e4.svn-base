<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Smart cycle counting</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
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
	<script type="text/javascript" src="js/jquery-form.js"></script>
	<script type="text/javascript" src="js/highcharts.js"></script>
	<script type="text/javascript" src="js/exporting.js"></script>
	<script type="text/javascript" src="js/echarts.min.js"></script>
	<script type="text/javascript" src="js/jquery.SuperSlide.js"></script>
	<!-- <script type="text/javascript" src="js/SmartCC_EN_01.js"></script> -->
	<style type="text/css">
*{margin:0;padding:0;list-style:none;}
img{border:0;}
a{text-decoration:none;color:#333;}
a:hover{color:#1974A1;}

/* tabBar */
.tabBar{width:100%;font-family:"Microsoft YaHei";margin:0px auto;}
.tabBar .hd ul{z-index:5;position:relative;zoom:1;}
.tabBar .hd li{float:left;height:24px;line-height:24px;margin-top:4px;padding:0px 16px 0px 16px;margin-right:5px;color:#333333;border:1px solid #c8d1d5;border-bottom:0px;cursor:pointer;}
.tabBar .hd li.on{height:27px;line-height:32px;font-weight:bold;overflow:hidden;margin-top:0px;position:relative;top:1px;border-top:2px solid #004ea2;background:#fff;}
.tabBar .hd li.on span{font-weight:bold;color:#c00;}
.tabBar .bd{border:1px solid #C8D1D5;padding:0px 40px 0 40px;clear:both;position:relative;height:90%;overflow:hidden;}
.tabBar .bd .conWrap{width:100%;height: 100%}
.tabBar .prev,.tabBar .next{position:absolute;left:10px;top:80px;}
.tabBar .next{left:auto;right:10px;}
.tabBar .prevStop,.tabBar .nextStop{display:none;}
.tabBar .bd p{padding-bottom:10px}
</style>
	<script type="text/javascript">
			//图表数据初始化
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
						        text: ''
						    },
						    xAxis: {
						        categories: []
						    },
						    yAxis: {
						        min: 0,
						        title: {
						            text: ''
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
						    exporting:{enabled:false},
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
var option1 = {
	    title: {
	        text: ''
	    },
	    
	    xAxis: {
	    	
	        show: false,
	        // type : 'category',
	        boundaryGap : false,
	        // data : axisData
	        type: 'value'
	    },
	    yAxis: {
	    	
	        show: false,
	        type: 'value'
	    },
	    legend: [{
	        /*formatter: function(name) {
	            return echarts.format.truncateText(name, 40, '14px Microsoft Yahei', '…');
	        },*/
	        tooltip: {
	            show: true
	        },
	        selectedMode: 'true',
	        top: 10,
	        //left:60,
	        //width:"90%",
	        data: []
	    }],
	    series: [{
	        type: 'graph',
	        layout: 'none',
	        coordinateSystem: 'cartesian2d',
	        symbolSize:4,
	        categories: [],
	        label: {
	            normal: {
	                show: true
	            }
	        },
	        edgeSymbol: ['circle', 'circle'],//线的样式：圆-箭头
	        edgeSymbolSize: [4, 2],//圆点和箭头的大小
	        data: [],
	        links: [],//各个点之间的指向关系
	        itemStyle: {
	            normal: {
	            	color: 'green',
	                label: {
	                    position: ['60%', '100%'],
	                   // textStyle: {
	                    //color: 'black'
	                 //}
	               }
	           }
	       },
	        lineStyle: {
	            normal: {
	               color: '#6c6c6c',//线的颜色
	                opacity: 0.5,
	                width: 0.5,
	                // curveness: 0.1
	            }
	        }
	    }, {
	        name: 'A',
	        type: 'lines',
	        coordinateSystem: 'cartesian2d',
	        categories: [],
	        // zlevel: 2,
	        effect: {
	            show: true,
	            smooth: false,
	            trailLength: 0,
	            symbol: "arrow",//流动箭头的样式
	            color:'#ff9224',//流动线颜色
	            symbolSize: 8
	        },
	        lineStyle: {
	            normal: {
	                // color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{
	                //     offset: 0,
	                //     color: 'rgb(255, 0, 0)'
	                // }, {
	                //     offset: 1,
	                //     color: 'rgb(0, 255, 0)'
	                // }]),
	                color:'red',
	               //color: function() {
	                    //return getColorPalette(3);
	                //}(),
	                width: 0.5,
	                opacity: 0.4,
	                // curveness: 0.1//贝塞尔曲线度
	            }
	        },
	        data: [
	              
	           ]
	    }]
};
//页面初始化加载数据
function loadData(){
	$(".overlay").css({'display':'block','opacity':'0.8'});
	$(".showbox").stop(true).animate({'margin-top':'300px','opacity':'1'},200);
	$.post( "/SmartCC/servlet/LoadDataServlet", {"aaa":"aaa"},function (data){
		//响应后移除遮罩层
		$(".showbox").stop(true).animate({'margin-top':'250px','opacity':'0'},400);
		$(".overlay").css({'display':'none','opacity':'0'});
		if(data.code == 0){
			$('#taskTable').datagrid('loadData', data.tList);
			$('#divideResult').datagrid('loadData', data.dList);
			var categories = data.categories;
			var series = data.series;
			options.series = series;
			options.xAxis.categories = categories;
			var chart = new Highcharts.Chart(options);
			$('#p').progressbar('setValue', data.sNum);
			
			//echart 图
			option1.series[0].data = data.points;
  		 	option1.xAxis.max = data.range.maxX;
  		 	option1.xAxis.min = data.range.minX;
  		 	option1.yAxis.max = data.range.maxY;
  		 	option1.yAxis.min =data.range.minY;
  		 	option1.legend[0].data = data.legendData;
  		 	option1.series[0].categories = data.eCharCategoriesList;
  		 	myChart.setOption(option1);
  		 	lineData = data.lineData;
  		 	arrowData = data.arrowList;
  		 	setInterval(show,500);//
		}
	},"json" );
}
var lineData;
var arrowData;
var count = 0;
//基于准备好的dom，初始化echarts实例
var myChart;
var isShow = false;
function show(){
	   var num = lineData.length;
	   if(count < num){
		   option1.series[0].links = lineData[count];
		   myChart.setOption(option1);
		   count ++;
	   }
	   else{
		   if(!isShow){
			  option1.series[1].data = arrowData;
			  myChart.setOption(option1);
			  isShow = true;
		   }
		   
	   }
}
$(document).ready(function (){
		$(".tabBar").slide({
		mainCell:".conWrap",
		effect:"left", 
		trigger:"click", 
		pnLoop:false
	});
	myChart = echarts.init(document.getElementById('main'));
	
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option1);
    myChart.on('legendselectchanged', function (params) {
    	var a = params.selected;
    	var selectedInfo = JSON.stringify(a);
    	$.post( "/SmartCC/servlet/ReLoadArrowsServlet", 
      			{
      		 		"selectedInfo":selectedInfo
      		 	},function (data){
      		 		option1.series[1].data = data.arrowList;
      		 		myChart.setOption(option1);
    			},"json" 
    	);
    	
    });
   
    /*$.post( "/SmartCC/servlet/EcharTestServlet111", 
  			{
  		 		"aaa":"aaa"
  		 	},function (data){
  		 		option1.series[0].data = data.data;
  		 	
  		 	option1.xAxis.max = data.range.maxX;
  		 	option1.xAxis.min = data.range.minX;
  		 	option1.yAxis.max = data.range.maxY;
  		 	option1.yAxis.min =data.range.minY;
  		 	lineData = data.lineData;
  		 	arrowData = data.arrowList;
  		 	option1.series[0].links = lineData[0];
  		 	myChart.setOption(option1);
  		 	setInterval(show,500);//
			},"json" 
	);*/
	
	
	
	
	var h = $(document).height();
	$(".overlay").css({"height": h });	
	//点击搜索按钮触发
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
	var chart = new Highcharts.Chart(options);//初始化柱状图表
	//上传文件按钮响应
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
	     $('#divideResult').datagrid('loadData', data.dList);
	     var categories = data.categories;
		var series = data.series;
		options.series = series;
		options.xAxis.categories = categories;
		var chart = new Highcharts.Chart(options);
		$('#p').progressbar('setValue', data.sNum);//更新上方进度条
	 });
	});
	//分配人员提交按钮响应
	$("#submitSchemeBtn").click(function(){
		var rows= $('#tt').datagrid('getSelections');
		var workersJson = JSON.stringify(rows);
		$(".overlay").css({'display':'block','opacity':'0.8'});
		$(".showbox").stop(true).animate({'margin-top':'300px','opacity':'1'},200);
		$.post( "/SmartCC/servlet/NewSubmitSchemeServlet", 
			{ 
				workersJson: workersJson,
				isAuto:'1'
			},function (data){
				//响应后移除遮罩层
				$(".showbox").stop(true).animate({'margin-top':'250px','opacity':'0'},400);
				$(".overlay").css({'display':'none','opacity':'0'});
			if(data.code == 0){
				alert("Distribution success");
				$('#tt').datagrid('reload');
				$('#taskTable').datagrid('loadData', data.data);
				$('#divideResult').datagrid('loadData', data.data1);
				var categories = data.categories;
				var series = data.series;
				options.series = series;
				options.xAxis.categories = categories;
				var chart = new Highcharts.Chart(options);
				
				
				//echart 图
				option1.series[0].data = data.points;
	  		 	option1.xAxis.max = data.range.maxX;
	  		 	option1.xAxis.min = data.range.minX;
	  		 	option1.yAxis.max = data.range.maxY;
	  		 	option1.yAxis.min =data.range.minY;
	  		 	option1.legend[0].data = data.legendData;
	  		 	option1.series[0].categories = data.eCharCategoriesList;
	  		 	myChart.setOption(option1);
	  		 	lineData = data.lineData;
	  		 	arrowData = data.arrowList;
	  		 	setInterval(show,500);//
			}
		},"json" );
	});
	//加载人员列表
	$('#tt').datagrid({
		title:'',
		singleSelect:false,
		idField:'id',
		method:'get',
		fitColumns:true,
		url:'/SmartCC/servlet/LoadWorkersService',
		columns:[[
			{field:'dh',align:'center',title:'choose',checkbox:'true',width:100},
			{field:'id',align:'center',title:'Staff No.',width:300},
			{field:'name',align:'center',title:'Name',width:300}
			
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
//编辑
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
//点击保存
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
		return '<a href="####" onclick="updateState('+index+');return false;">Start</a>';  
	} 
	else if(row.state == 2){
		return '<a href="####" onclick="updateState('+index+');return false;">Finish</a>';  
	}
	else{
		return '';
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
		return "<span style='color: red'>Undistributed</span>" 
	}
	if(val == 1){
		return "<span style='color: orange'>Assigned</span>" 
	}
	if(val == 2){
		return "<span style='color: blue'>Completing</span>" 
	}
	if(val == 3){
		return "<span style='color: green'>Completed</span>" 
	}
}
function downLoad(){
	var searchWorker = $("#cc").combobox('getValue');
	var searchState = $("#searchState").combobox('getValue');
	document.getElementById("downLoadBtn").href="/SmartCC/servlet/ExportDivideHistoryInfoServlet?searchWorker=" + searchWorker + "&searchState=" +　searchState;
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

	<body class="easyui-layout" style="width:100%;height: 100%;background-color: #acd6ff">
	
	<div class="overlay"></div>

	<div id="AjaxLoading" class="showbox">
		<div class="loadingWord"><img src="waiting.gif">data loading ...</div>
	</div>
	<form id="demoForm" class="easyui-layout" style="width: 100%;height: 100%" method="post" enctype="multipart/form-data" action="/SmartCC/servlet/HandleRequestService" method="post"> 
		    <div data-options="region:'north',title:'',split:false" style="width:100%;height: 8%;">
		    
		    	<table width="100%" height="100%" style="background-color: #acd6ff">
		    		<tr>
		    			<td style="width: 2%;height: 100%">
		    				<img alt="" src="images/bmwlogo.png" style="width: 100%">
		    			</td>
		    			<td style="width: 35%;"> 
		    				<input name="excelData" class="easyui-filebox"  data-options="prompt:'Choose a excel（xls、xlsx）',buttonText:'&nbsp;Choose file &nbsp;'" style="width:60%;height: 35px;">
		    				<a href="javascript:void(0)" id="uploadBtn" class="easyui-linkbutton" style="width: 20%;height: 35px;"> Upload </a>
		    			</td>
		    			<td width="12%">
		    				Completion ratio：
		    			</td>
		    			<td style="width: 26%;">
		    				<div id="p" class="easyui-progressbar" data-options="value:0" style="width:70%;height: 35px;background-color: #FFFFFF"></div>
		    			</td>
		    		</tr>
		    	</table>
		    	
		    	
		    </div>
		    
		    <div data-options="region:'west',title:'',split:false" style="width:50%;background-color: #acd6ff">
		    	 <div class="easyui-layout" style="width:99%;height: 98%">
		    	 	<div data-options="region:'north',title:'',split:false" style="height:45%;">
		    			<table id="taskTable" class="easyui-datagrid" title="" style="height:98%;"
						data-options="
						singleSelect:true,
						collapsible:true,
						url:'',
						method:'get',
						striped:true
						">
							<thead>
								<tr>
									<th data-options="field:'taskNo',width:50,align:'center'">TaskNo.</th>
									<th data-options="field:'componentName',width:100,align:'center'">Component</th>
									<th data-options="field:'stationName',width:100,align:'center'">Part station</th>
									<th data-options="field:'type',width:100,align:'center'">Category</th>
									<th data-options="field:'workerId',width:120,align:'center'">Staff No.</th>
									<!--  <th data-options="field:'orderNum',width:130,align:'center'">Order</th>-->
									<th data-options="field:'state',width:160,align:'center',formatter:formatterState">Status</th>
								</tr>
							</thead>
						</table>
			   		 </div>
			   		 <div data-options="region:'center',title:'',split:false" style="width:99%; height:55%;">
			   		 	<!--<div id="container" style="width:99%; height:100%;"></div>
			   		 	<div id="main"  style="width: 99%;height: 300px;display: none"></div>  -->
								<div class="tabBar">
									<div class="hd">
										<ul>
											<li class="on">Data</li>
											<li>Path</li>
										</ul>
									</div>
									
									<div class="bd">
										<div class="conWrap">
											<div class="con">
												<p><div id="container" style="width:99%; height:100%;"></div></p>
											</div>
											<div class="con">
												<p><div id="main"  style="width: 99%;height: 300px"></div></p>
											</div>
											
										</div>
									</div>
								</div><!-- tabbar -->
					 	</div>
			   		 </div>
		    	 </div>
		    	 
		    </div>
		    <div data-options="region:'center',title:'',split:false" style="background-color: #acd6ff;width: 50%;">
		    	<div class="easyui-layout" style="width:99%;height: 98%;background-color: #acd6ff">
		    		<div data-options="region:'north',title:'',split:false" style="height:45%;width:96px;background-color: #acd6ff">
		    			<a href="####" class="easyui-linkbutton" style="height: 30px;width: 100px;" id="submitSchemeBtn" onclick="return false;">Submit</a>
		    			<table id="tt" style="height: 100%;width: 100%;margin-left: 50px"></table>
			    		
		    		</div>
		    		<div data-options="region:'center',title:'',split:false" style="height:55%;width:97%">
		    			Choose person：<input id="cc" class="easyui-combobox" name="dept"
    					data-options="valueField:'id',
    					textField:'name',
    					url:'/SmartCC/servlet/LoadWorkersService',"  
    					style="width: 20%;height: 30px">
						Status： <select id="searchState" class="easyui-combobox" name="dept" style="width:20%;height: 30px;">
							    <option value="">Choose one</option>
							    <option value="1">Assigned</option>
							    <option value="2">Completing</option>
							    <option value="3">Completed</option>
							</select>
							<a href="javascript:void(0)" id="searchBtn" class="easyui-linkbutton" style="width: 15%;height: 30px;">Search</a>
		    				<a href="/SmartCC/servlet/ExportDivideHistoryInfoServlet" id="downLoadBtn" onclick="downLoad()" class="easyui-linkbutton" style="width: 15%;height: 30px;">DownLoad</a>
		    			<table id="divideResult" class="easyui-datagrid" title="Distribution result" style="height:90%;width:100%"
						data-options="singleSelect:true,collapsible:true,url:'',method:'get',fitColumns:true,">
							<thead>
								<tr>
									<th data-options="field:'workerId',width:90,align:'center'">Staff No.</th>
									<th data-options="field:'componentName',width:100,align:'center'">Component</th>
									<th data-options="field:'orderNum',width:100,align:'center'">Order</th>
									<th data-options="field:'stationNames',width:280,align:'center'">Part station</th>
									<th data-options="field:'state',width:90,align:'center',formatter:formatterState">Status</th>
									<th data-options="field:'operate',width:80,formatter:formatOper,align:'center'">Operation</th>
								</tr>
							</thead>
						</table>
		    		</div>
		    	</div>
		    </div>
	    </form>
	</body>
</html>