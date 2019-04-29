<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>Map for Logistics</title>
    
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">     
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="css/easyui.css">
	<link rel="stylesheet" type="text/css" href="css/SmartCC.css">
	<link rel="stylesheet" type="text/css" href="css/buttonStyle.css">
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="js/echarts.min.js"></script>
	<style>
		
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
		
		body {font-family:"Helvetica Neue",Helvetica,Arial,sans-serif;}
	</style>
	<script type="text/javascript">
//完整地图
var option = {
    tooltip: {
        position: 'right'
    },
    animation: false,
    grid: {
        height: '100%' 
    },
    xAxis: {
    	position:'top',
        type: 'category',
        data: [],
        splitArea: {
            show: true
        }
    },
    yAxis: {
        type: 'category',
        data: [],
        splitArea: {
            show: true
        }
    },
    series: [{
        name: 'Point info',
        type: 'heatmap',
        data: [],
        label: {
            normal: {
            	formatter:function(data){
            		return data.name;
                },
                show: false
            },
            
        },
        itemStyle: {
            emphasis: {
                shadowBlur: 50,
                shadowColor: 'rgba(0, 0, 0, 1)'
            }
        }
    }]
};		
var myPie;
var myPie1;
$(document).ready(function (){
	propertyTextBoxInit();
	myPie = echarts.init(document.getElementById('container'));
	myPie.setOption(option,true);
	$(".overlay").css({'display':'block','opacity':'0.8'});
	$(".showbox").stop(true).animate({'margin-top':'300px','opacity':'1'},200);
	$("#type").change(function(){
		  var selectVal = $("#type").val();
		  if(selectVal == "0" || selectVal == "1"){
			  $("#number").textbox("setValue", "");
			  $('#number').textbox('textbox').attr('readonly',true); 
		  }
		  else{
			  $('#number').textbox('textbox').attr('readonly',false); 
		  }
	});
	$.post( "/SmartCC/editMap", 
			{ 
				method:"loadMapInfo"
			},function (data){
			if(data.code == 0){
				option.xAxis.data = data.xAxis;
				option.yAxis.data = data.yAxis;
				option.series[0].data = data.data;
				myPie.setOption(option,true);
				
				myPie1 = echarts.init(document.getElementById('container1'));
				option.xAxis.data = data.data1.xAxis;
				option.yAxis.data = data.data1.yAxis;
				option.series[0].data = data.data1.data;
				//option.series[0].label.normal.show = true;
				myPie1.setOption(option,true);
				//响应后移除遮罩层
				$(".showbox").stop(true).animate({'margin-top':'250px','opacity':'0'},400);
				$(".overlay").css({'display':'none','opacity':'0'});
			}
	},"json" );
	myPie.on('click', function (e) {
		var valueArray = e.value;
		var xAixs = valueArray[0];
		var yAixs = valueArray[1];
		var type = valueArray[2];
		//alert(xAixs);
		$("#xAixs").textbox("setValue", xAixs);
		$("#yAixs").textbox("setValue", yAixs);
		$("#type").val(type);
		if(type == '3' || type == '2'){
			var number = valueArray[3];
			var numberArray = number.split(":");
			$("#number").textbox("setValue", numberArray[1]);
			
		}
		else{
			$("#number").textbox("setValue", "");
		}
		$.post( "/SmartCC/editMap", 
				{ 
					xAxis:xAixs,
					yAxis:yAixs,
					method:"findPoint"
				},function (data){
					
				if(data.code == 0){
					myPie1 = echarts.init(document.getElementById('container1'));
					option.xAxis.data = data.xAxis;
					option.yAxis.data = data.yAxis;
					option.series[0].data = data.data;
					//option.series[0].label.normal.show = true;
					myPie1.setOption(option,true);
				}
			},"json" );
	});
	myPie1 = echarts.init(document.getElementById('container1'));
	myPie1.setOption(option,true);
	myPie1.on('click', function (e) {
		var valueArray = e.value;
		var xAixs = valueArray[0];
		var yAixs = valueArray[1];
		var type = valueArray[2];
		//alert(xAixs);
		$("#xAixs").textbox("setValue", xAixs);
		$("#yAixs").textbox("setValue", yAixs);
		$("#type").val(type);
		if(type == '2' || type == '3'){
			var number = valueArray[3];
			var numberArray = number.split(":");
			$("#number").textbox("setValue", numberArray[1]);
		}
		else{
			$("#number").textbox("setValue", "");
		}
	});
});
//查找工位或柱子触发
function findPointHandle(){
	var searchNumber = $("#searchNumber").val();
	var xAxis = $("#xAixs").textbox("getValue", xAixs);
	var yAxis = $("#yAixs").textbox("getValue", xAixs);
	$.post( "/SmartCC/editMap", 
			{ 
				xAxis:xAxis,
				yAxis:yAxis,
				searchNumber:searchNumber,
				method:"findPoint"
			},function (data){
				
			if(data.code == 0){
				myPie1 = echarts.init(document.getElementById('container1'));
				option.xAxis.data = data.xAxis;
				option.yAxis.data = data.yAxis;
				option.series[0].data = data.data;
				//option.series[0].label.normal.show = true;
				myPie1.setOption(option,true);
			}
		},"json" );
	return;
}
//属性编辑框初始化不可编辑
function propertyTextBoxInit(){
	$('#xAixs').textbox('textbox').attr('readonly',true); 
	$('#yAixs').textbox('textbox').attr('readonly',true); 
	$("#type").attr("disabled",true);
	$('#number').textbox('textbox').attr('readonly',true); 
}
//开启编辑 点击 编辑按钮触发
function startEdit(){
	//坐标值不可修改
	//$('#xAixs').textbox('textbox').attr('readonly',false); 
	//$('#yAixs').textbox('textbox').attr('readonly',false); 
	$("#type").attr("disabled",false);
	$('#number').textbox('textbox').attr('readonly',false); 
}
//下载地图文件
function downloadHandle(){
	$.post( "/SmartCC/editMap", 
			{ 
				method:"downMapFile"
			},function (data){
				
			if(data.code == 0){
				
			}
		},"json" );
}
//保存对地图作出的变更
function saveMapChange(){
	var xAixs = $("#xAixs").textbox("getValue");
	var yAixs = $("#yAixs").textbox("getValue");
	if(xAixs == "" || yAixs == ""){
		$.messager.show({ // show error message
			title : 'Message',
			msg : "The xAixs or yAixs cannot be empty."
		});
		return;
	}
	var type = $("#type").val();
	var number = $("#number").textbox("getValue");
	$(".overlay").css({'display':'block','opacity':'0.8'});
	$(".showbox").stop(true).animate({'margin-top':'300px','opacity':'1'},200);
	$.post( "/SmartCC/editMap", 
			{ 
				xAixs:xAixs,
				yAixs:yAixs,
				type:type,
				number:number,
				method:"saveMapChange"
			},function (data){
				
			if(data.code == 0){
				//重新加载左侧全景地图
				//myPie = echarts.init(document.getElementById('container'));
				//option.xAxis.data = data.data1.xAxis;
				//option.yAxis.data = data.data1.yAxis;
				//option.series[0].data = data.data1.data;
				//myPie.setOption(option,true);
				//重新加载右侧局部地图
				
				myPie1 = echarts.init(document.getElementById('container1'));
				option.xAxis.data = data.data2.xAxis;
				option.yAxis.data = data.data2.yAxis;
				option.series[0].data = data.data2.data;
				//option.series[0].label.normal.show = true;
				myPie1.setOption(option,true);
				//响应后移除遮罩层
				$(".showbox").stop(true).animate({'margin-top':'250px','opacity':'0'},400);
				$(".overlay").css({'display':'none','opacity':'0'});
				$.messager.show({ // show error message
					title : 'Message',
					msg : "Save success."
				});
			}
		},"json" );
}
	</script>
  </head>
  
  <body style="background:#EDEDED">
  <div id="AjaxLoading" class="showbox">
			<div class="loadingWord"><img src="waiting.gif">data loading ...</div>
  </div>
  <!-- 最外层table -->
    <table width="100%" height="100%" >
    	<tr>
    		<!-- 左侧留白 -->
    		<td style="width: 1%;height: 100%"></td>
    		<!-- 中间主体 -->
    		<td>
    			<table style="width: 100%;height: 100%">
    			 	<!-- 上方header -->
    				<tr>
    					<td style="background-color: #EDEDED;width: 100%;height: 7%;vertical-align: middle;line-height: 100%" >
    						<table style="width: 100%;height: 100%">
    							<tr>
    								<!-- <td style="width: 4%"><img src="images/bmwlogo.png" width="50px" height="50px" /></td> -->
    								<td style="width: 80%">
    									 <font style="font-size: 24px;color:#333;font-weight: 500;margin-top: 5%">Map for Logistics</font>
    								</td>
    							</tr>
    						</table>
    					</td>
    				</tr>
    				<tr>
	    				<td style="width: 100%;height:90%">
	    					<!-- 真正包含内容的table -->
	    					<table style="width: 100%;height: 100%">
	    						<tr style="width: 100%;height: 25%">
	    							<!-- 上方工具栏 -->
		    						<td class="layoutTable" style="width: 40%">
										<table width="100%" height="98%">
											<tr>
												<td colspan="4" width="15%" style="text-align: left;">Point properties:</td>
											</tr>
											<tr>
												<td width="15%" style="text-align: center;">XAixs:</td>
												<td width="20%" style="text-align: center;"><input id="xAixs" name="xAixs"
														class="easyui-textbox" data-options="required:false" style="width: 100%;">
												</td>
												<td width="20%" style="text-align: center;">Type:</td>
												<td width="45%" style="text-align: center;">
													<select autocomplete="off" id="type"  name="type" style="width:100%;height:100%">
													    <option value="0" selected="selected">Obstacle</option>
													    <option value="1">Way</option>
													    <option value="2">Station</option>
													    <option value="3">Column</option>
													</select>
												</td>
											</tr>
											<tr>
												<td width="15%" style="text-align: center;">YAixs:</td>
												<td width="20%" style="text-align: center;"><input id="yAixs" name="yAixs"
														class="easyui-textbox" data-options="required:false" style="width: 100%">
												</td>
												<td width="20%" style="text-align: center;">Number:</td>
												<td width="45%" style="text-align: center;">
													<input id="number" name="number"
														class="easyui-textbox" data-options="required:false" style="width: 100%">
												</td>
											</tr>
											<tr>
												<td colspan="4" style="text-align: center;">
													<a href="####" class="easyui-linkbutton  button" style="height: 25px;width: 80px;" id="editBtn" onclick="startEdit();return false;">
														<font style="font-size: 10">Edit</font>
													</a>
													<a href="####" class="easyui-linkbutton  button" style="height: 25px;width: 80px;" id="saveBtn" onclick="saveMapChange();return false;">
														<font style="font-size: 10">Save</font>
													</a>
												</td>
											</tr>
											<tr>
												<td colspan="4" width="15%" style="text-align: left;">Data searching:</td>
											</tr>
											<tr>
												<td colspan="4" width="15%" style="text-align: left;">
													<input id="searchNumber" name="searchNumber"
															class="easyui-textbox" data-options="required:false,prompt:'Part station/Column No.'" style="width: 180px;height: 25px;">
										  			<a href="####" class="easyui-linkbutton  button" style="height: 25px;width: 80px;" id="searchBtn" onclick="findPointHandle();return false;">
														<font style="font-size: 10">Search</font>
													</a>
													<a href="/SmartCC/editMap?method=downMapFile" class="easyui-linkbutton  button" style="height: 25px;width: 80px;" id="downLoadBtn">
														<font style="font-size: 10">Download</font>
													</a>
												</td>
												
											</tr>
										</table>
									</td>
		    						<!-- 详情地图 -->
		    						<td class="layoutTable" rowspan="2" style="width: 60%">
		    							<div id="container1" style="width: 100%;height: 100%"></div>
		    						</td>
	    						</tr>
	    						<tr style="width: 100%;height: 70%">
	    						    <!-- 完整地图 -->
	    							<td class="layoutTable">
	    								<div id="container" style="width: 100%;height: 100%"></div>
	    							</td>
	    							
	    						</tr>
	    					</table>
	    				</td>
    				</tr>
    				<!-- 下方footer -->
    				<tr>
	    				<td style="width:100%;height:3%;background-color: #EDEDED;">
	    					
	    				</td>
    				</tr>
    			</table>
    		</td>
    		<!-- 右侧留白 -->
    		<td style="width: 1%;height: 100%;"></td>
    	</tr>
    </table>
    <!-- 最外层table结束 -->
  </body>
</html>