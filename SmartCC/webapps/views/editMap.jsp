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
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="stylesheet" type="text/css" href="css/easyui.css">
	<link type="text/css" href="css/style.css" rel="stylesheet" />
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/echarts.min.js"></script>
	
	<script type="text/javascript" src="js/jquery.easyui.min.js"></script>

	<script type="text/javascript">


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
                show: false
            }
        },
        itemStyle: {
            emphasis: {
                shadowBlur: 50,
                shadowColor: 'rgba(0, 0, 0, 1)'
            }
        }
        
    }]
};
//查找工位或柱子触发
function findPointHandle(){
	var searchNumber = $("#searchNumber").val();
	$.post( "/SmartCC/editMap", 
			{ 
				searchNumber:searchNumber,
				method:"findPoint"
			},function (data){
				
			if(data.code == 0){
				var myPie = echarts.init(document.getElementById('container'));
				option.xAxis.data = data.xAxis;
				option.yAxis.data = data.yAxis;
				option.series[0].data = data.data;
				myPie.setOption(option,true);
			}
		},"json" );
	return;
	
}
$(document).ready(function (){
	var myPie = echarts.init(document.getElementById('container'));
	myPie.setOption(option,true);
	
	$.post( "/SmartCC/editMap", 
			{ 
				method:"loadMapInfo"
			},function (data){
				
			if(data.code == 0){
				option.xAxis.data = data.xAxis;
				option.yAxis.data = data.yAxis;
				option.series[0].data = data.data;
				

				myPie.setOption(option,true);
			}
		},"json" );
	myPie.on('click', function (e) {
    	$('#dlg').dialog('open').dialog('setTitle', 'Emergency');
		$('#fm').form('clear');
		var valueArray = e.value;
		var xAixs = valueArray[0];
		var yAixs = valueArray[1];
		var type = valueArray[2];
		
		$("#xAixs").textbox("setValue", xAixs);
		$("#yAixs").textbox("setValue", yAixs);
		$("#type").val(type);
		if(type == '3' || type == '4'){
			var number = valueArray[3];
			var numberArray = number.split(":");
			$("#number").textbox("setValue", numberArray[1]);
		}
		
    	//alert(valueArray[0]);
	});
});

	</script>
	<style>
	</style>
  </head>
  
  <body>
 <!--<div id="AjaxLoading" class="showbox">
			<div class="loadingWord"><img src="waiting.gif">data loading ...</div>
		</div>  --> 
  <div id="dlg" class="easyui-dialog"
		data-options="iconCls:'icon-save',resizable:true,modal:true"
		style="width:280px;height:200px;padding:10px 20px;top:200px" closed="true"
		buttons="#dlg-buttons">
		<div class="ftitle" style="font-size: 15px"></div>
		
		<div class="overlay"></div>

		
		<form id="fm" name="fm" method="post"
			action="/SmartCC/smartCCIndex?method=uploadEmergencyTaskExcel" data-options="novalidate:true" enctype="multipart/form-data">
			<input type="hidden" name="method" value="uploadEmergencyTaskExcel">
			
			<table>
				
				<tr>
					<td><font style="font-size: 12px">XAixs:&nbsp; </font></td>
					<td>
						<input id="xAixs" name="xAixs"
					class="easyui-textbox" data-options="required:false" style="width: 120px;"  readonly='true'>
					</td>
				</tr>
				<tr>
					<td><font style="font-size: 12px">YAixs: &nbsp; </font></td>
					<td>
					<input id="yAixs" name="yAixs"
					class="easyui-textbox" data-options="required:false" style="width: 120px;" readonly='true'>
					</td>
				</tr>
				<tr>
					<td><font style="font-size: 12px">Type:&nbsp;  </font></td>
					<td>
						<select autocomplete="off" id="type"  name="type" style="width:120px;height: 25px;">
							    <option value="0">障碍</option>
							    <option value="1">路径点</option>
							    <option value="2">工位</option>
							    <option value="3">柱子</option>
						</select>
					</td>
				</tr>
				<tr>
					<td><font style="font-size: 12px">编号: &nbsp; </font></td>
					<td>
					<input id="number" name="number"
					class="easyui-textbox" data-options="required:false" style="width: 120px;" readonly='true'>
					</td>
				</tr>
			</table>
			
			
		</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton  button blue"
			data-options="iconCls:'icon-ok'" onclick="addEmergency()" style="width:90px">确定</a> <a
			href="javascript:void(0)" class="easyui-linkbutton  button blue"
			data-options="iconCls:'icon-cancel'" onclick="javascript:$('#dlg').dialog('close')"
			style="width:90px">取消</a>
	</div>
        <!-- 工具栏区域 -->
  		<div id="toolDiv" style="width: 100%">
  			<input id="searchNumber" name="searchNumber"
					class="easyui-textbox" data-options="required:false,prompt:'Part station/column No.'" style="width: 180px;height: 30px;">
  			<a href="####" class="easyui-linkbutton  button blue" style="height: 30px;width: 60px;" id="searchBtn" onclick="findPointHandle();return false;">
				<font style="font-size: 10">Search</font>
			</a>
			<div class="User_ratings User_grade" id="div_fraction0">
			缩放
		<div class="ratings_bars">
			<span id="title0">0</span>
			<span class="bars_10">0</span>
			<div class="scale" id="bar0">
				<div></div>
				<span id="btn0"></span>
			</div>
			<span class="bars_10">10</span>
		</div>
		<script type="text/javascript">
			scale = function (btn, bar, title) {
				this.btn = document.getElementById(btn);
				this.bar = document.getElementById(bar);
				this.title = document.getElementById(title);
				this.step = this.bar.getElementsByTagName("DIV")[0];
				this.init();
			};
			scale.prototype = {
				init: function () {
					var f = this, g = document, b = window, m = Math;
					f.btn.onmousedown = function (e) {
						var x = (e || b.event).clientX;
						var l = this.offsetLeft;
						var max = f.bar.offsetWidth - this.offsetWidth;
						g.onmousemove = function (e) {
							var thisX = (e || b.event).clientX;
							var to = m.min(max, m.max(-2, l + (thisX - x)));
							f.btn.style.left = to + 'px';
							f.ondrag(m.round(m.max(0, to / max) * 100), to);
							b.getSelection ? b.getSelection().removeAllRanges() : g.selection.empty();
						};
						g.onmouseup = new Function('this.onmousemove=null');
					};
				},
				ondrag: function (pos, x) {
					this.step.style.width = Math.max(0, x) + 'px';
					this.title.innerHTML = pos / 10 + '';
				}
			}
			new scale('btn0', 'bar0', 'title0');
		</script>
	</div>
  		</div>
  		<!-- 显示地图区域 -->
  		<div style="width: 90%;height: 180%">
  			<div id="container" style="width: 100%;height: 100%"></div>
  		</div>
  		
  </body>
</html>
