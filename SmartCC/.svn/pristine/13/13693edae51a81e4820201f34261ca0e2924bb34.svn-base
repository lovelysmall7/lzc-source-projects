var myGauge;
var myPie;
//格式化每一行的状态列
function formatterState(val,row){
	if(val == 0){
		return "<span style='color: red'>UnAssigned</span>" 
	}
	if(val == 1){
		return "<span style='color: orange'>Assigned</span>" 
	}
	if(val == 2){
		return "<span style='color: blue'>In progress</span>" 
	}
	if(val == 3){
		return "<span style='color: green'>Completed</span>" 
	}
}
//格式化分配方式列
function devideTypeformatter(val,row){
	if(val == 0){
		return "<span style=''>Auto</span>" 
	}
	if(val == 1){
		return "<span style=''>Manual</span>" 
	}
	
}
//格式化任务类型列
function typeFormatter(val,row){
	if(val == 0){
		return "<span style=''>Nomal</span>" 
	}
	if(val == 1){
		return "<span style='color:red'>Emenergency</span>" 
	}
	if(val == 2){
		return "<span style='color:blue'>Recheck</span>" 
	}
}
/**
 * 任务列表条件查询
 */
function searchTaskList() {
	var searchComponent = $("#searchComponent").textbox('getValue');
	var searchWorker = $("#searchWorker").combobox('getValue');
	var searchState = $("#searchState").combobox('getValue');
	var searchBatch = $("#searchBatch").textbox('getValue');
	var searchType = $("#searchType").combobox('getValue');
	 var queryParameter = $('#divideHistoryTable').datagrid("options").queryParams;  
     queryParameter.searchComponent = searchComponent;  
     queryParameter.searchState = searchState;  
     queryParameter.searchWorker = searchWorker; 
     queryParameter.searchBatch = searchBatch; 
     queryParameter.searchType = searchType; 
     $("#divideHistoryTable").datagrid("reload"); 
}
function updateState(index){
	if(index != -1){
		$('#divideHistoryTable').datagrid('selectRow',index);// 关键在这里  
	}
	var rows= $('#divideHistoryTable').datagrid('getSelections');
	var dataJson = JSON.stringify(rows);
	$.post( "/SmartCC/dividehistory", 
			{ 
				dataJson:dataJson,
				method:'changeState'
			},
			function (data){
			if(data.code == 0){
				searchTaskList();
				loadCharData();
			}
			else{
				$.messager.show({ // show error message
					title : 'Message',
					msg : data.msg
				});
			}
		},"json" );
}
function initCharData(){
	myPie = echarts.init(document.getElementById('container'));
	myGauge = echarts.init(document.getElementById('gauge'));
	myPie.setOption(option1,true);
	myGauge.setOption(option2,true);
}
function loadCharData(){
	
	$.post( "/SmartCC/dividehistory", 
			{ 
				method:'loadChartData'
			},
			function (data){
			if(data.code == 0){
				option1.series = data.series;
				option1.yAxis.data = data.categories;
				// 使用刚指定的配置项和数据显示图表。
				myPie.setOption(option1,true);
				option2.series[0].data[0].value = data.sNum;
				myGauge.setOption(option2,true);
			}
			
		},"json" );
	
}
function downLoad(){
	var searchComponent = $("#searchComponent").textbox('getValue');
	var searchWorker = $("#searchWorker").combobox('getValue');
	var searchState = $("#searchState").combobox('getValue');
	var searchBatch = $("#searchBatch").textbox('getValue');
	var searchType = $("#searchType").combobox('getValue');
	document.getElementById("downLoadBtn").href="/SmartCC/dividehistory?method=ExportDivideHistoryInfo&searchComponent="+searchComponent+"&searchWorker=" + searchWorker + "&searchState=" +　searchState+"&searchBatch="+searchBatch+"&searchType="+searchType;
}
