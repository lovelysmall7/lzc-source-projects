/**
 * 加载柱状图表数据
 */
function loadBarChartData(data){
	//初始化加载柱状图表
	option.xAxis[0].data = data.xAxis;
	option.yAxis[0].max = data.maxY;
	option.yAxis[1].max = data.maxY1;
	option.series[0].data = data.okNumList;
	option.series[1].data = data.nokNumList;
	option.series[2].data = data.ratioList;
	option.dataZoom.end = data.dataXoomEnd;
	mypie.setOption(option, true);
}
/**
* 加载散图表数据
*/
function loadScffChartData(data){
	//初始化加载散点图表
	option1.series[0].data = data.scatterList;
    option1.xAxis.max = data.maxCount;
    option1.visualMap[0].max = data.maxCount * 2;
	// 使用刚指定的配置项和数据显示图表。
	mypie1.setOption(option1, true);
}
function loadPieCharData(data){
	pieOption.legend.data = data.pieCategory;
	pieOption.series[0].data = data.pieData;
	mypie1.setOption(pieOption, true);
}
/**
 * 页面初始化加载数据
 */
function loadData() {
	$.post("/SmartCC/smartCCpart2",
		{
			"method" : "loadData"
		}
		, function(data) {
			//响应后移除遮罩层
			if (data.code == 0) {
				loadBarChartData(data);
				loadPieCharData(data);
				//loadScffChartData(data);
				//$('#taskTable1').datagrid('loadData', data.tList);
				$('#top10Table').datagrid('loadData', data.pList);
			}
		}, "json");
}
/**
 * 按照起止时间更新图表
 */
function searchComponent() {
	var startTime;
	var endTime;
	startTime = $('#searchStartTime').datebox('getValue');
	endTime = $('#searchEndTime').datebox('getValue');
	//if (startTime == "" || endTime == "") {
		//$.messager.show({ // show error message
			//title : 'Message',
		//	//msg : "The starttime or endtime can not be empty."
		//});
		//return;
	//}
	$.post("/SmartCC/smartCCpart2",
		{
			method : "loadChartByTime",
			startTime : startTime,
			endTime : endTime
		}, function(data) {
			if (data.code == 0) {
				loadBarChartData(data);
				loadPieCharData(data);
			}
		}, "json")
}
/**
 * 格式化任务状态列
 */
function formatState(val, row) {
	if (val == 0) {
		return "<span style='color: #90ED7D';font-size:16px;font-weight:700;>&#10004</span>";
	}
	if (val == 1) {
		return "<span style='color: #FA8072;font-size:14px;font-weight:700;'>&#10006</span>";
	}
	if (val == -1) {
		return "<span style='color: #4DBBF1'>not checked</span>";
	}
}
/**
 * 格式化top10人员排名图标
 */
function formatterNum(val,row){
	//alert(val);
	if(val == 1){
		return "<img src='images/1st.PNG' style='height:19px;width:19px'/>";
	}
	if(val == 2){
		return "<img src='images/2nd.PNG' style='height:19px;width:19px'/>";
	}
	if(val == 3){
		return "<img src='images/3rd.PNG' style='height:19px;width:19px'/>"; 
	}
	else{
		return val;
	}
}
/**
 * 任务列表点击查看comment
 */
function formatComment(val,row){
	return "<a href='javascript:void(0)' onclick='showComment("+row+")'>Show</a>";
}
function showComment(row){
	//alert(row.remark);
}
function downLoad(){
	var component = $("#component").textbox('getValue');
	var workerId = $("#cc").combobox('getValue');
	var station = $("#station").textbox('getValue');
	var checkResult = $("#checkResult").combobox('getValue');
	document.getElementById("downLoadBtn").href="/SmartCC/smartCCpart2?method=ExportTaskInfo&component=" + component + "&workerId=" +　workerId+ "&station=" +　station+ "&checkResult=" +　checkResult;
	
}
/**
 * 更新任务合格率
 */
function updateTasks() {
	var checkResult;
	var comment;
	var rows = $('#taskTable1').datagrid('getSelections');
	var tasksJson = JSON.stringify(rows);
	var res = $('input:radio:checked').val();
	if (res == 'Qualified') {
		checkResult = 0;
	} else if (res == 'Unqualified') {
		checkResult = 1;
	}
	comment = $('#check-comment').val();
	$('#dlg').dialog('close'); // close the dialog
	$(".overlay").css({'display':'block','opacity':'0.8'});
	$(".showbox").stop(true).animate({'margin-top':'300px','opacity':'1'},200);
	$.post("/SmartCC/smartCCpart2",
		{
			method : "submitCheckResult",
			tasksJson : tasksJson,
			checkResult : checkResult,
			comment : comment
		}, function(data) {
			$(".showbox").stop(true).animate({'margin-top':'250px','opacity':'0'},400);
			$(".overlay").css({'display':'none','opacity':'0'});
			if (data.code == 0) {
				//todo 刷新dg
				//alert("Distribution success");
				
				
				$.messager.show({ // show error message
					title : 'Message',
					msg : "update success."
				});
				searchTaskList();
				searchComponent();
				//$('#taskTable1').datagrid('loadData', data.tList);
				//loadBarChartData(data);
				//loadScffChartData(data);
			} else {
				$.messager.show({ // show error message
					title : 'Message',
					msg : data.msg
				})
			}
		}, "json");
}
/**
 * 任务列表条件查询
 */
function searchTaskList() {
	var component = $("#component").textbox('getValue');
	var workerId = $("#cc").combobox('getValue');
	var station = $("#station").textbox('getValue');
	var checkResult = $("#checkResult").combobox('getValue');
	 var queryParameter = $('#taskTable1').datagrid("options").queryParams;  
     queryParameter.component = component;  
     queryParameter.workerId = workerId;  
     queryParameter.station = station;  
     queryParameter.checkResult = checkResult;  
     $("#taskTable1").datagrid("reload"); 
     $('#searchWin').dialog('close');
}
//加条件   
function getSearchConditions() {
	var component = $("#component").textbox('getValue');
	var worderId = $("#cc").combobox('getValue');
	var station = $("#station").textbox('getValue');
	var checkResult = $("#checkResult").combobox('getValue');
	
    var param = [];
    param.push({
        "component" : component,
        "worderId" : worderId,
        "station":station,
        "checkResult":checkResult
    });
    var conditions = JSON.stringify(param);
    return conditions;
}

//查询表格数据
//searchGrid 绑定的是一个javascript事件方法
function searchGrid() {
    var queryParams = {
        conditions : getSearchConditions()
    };
    $('#taskTable1').datagrid('load', queryParams);
}
/**
 * top10人员列表按时间查询
 */
function searchTopTen() {
	var searchDate;
	searchDate = $('#searchTime').datebox('getValue');
	if (searchDate == "") {
		$.messager.show({ // show error message
			title : 'Message',
			msg : "Please choose a date."
		});
		return;
	}
	$.post("/SmartCC/smartCCpart2",
		{
			method : "searchPersonByDate",
			searchDate : searchDate
		}, function(data) {
			if (data.code == 0) {
				$('#top10Table').datagrid('loadData', data.pList);
			} else {
				$.messager.show({ // show error message
					title : 'Message',
					msg : data.msg
				})
			}
		}, "json")
}