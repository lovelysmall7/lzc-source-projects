
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