function getNewBatch(){
	$.post( "/SmartCC/smartccupload", 
			{ 
				method:"getNewBatch"
			},function (data){
				$("#currentBatch").textbox('setValue',data.currentBatch);
		},"json" );
}
function searchData(){
	var searchComponent = $("#searchComponent").textbox('getValue');
	var searchType = $("#searchType").combobox('getValue');
	$.post( "/SmartCC/smartccupload", 
			{ 
				searchComponent:searchComponent,
				searchType:searchType,
				method:"findCurrentBatchData"
			},function (data){
				$("#uploadTable").datagrid('loadData',data.rows);
		},"json" );
}
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
function workerTable_load(){
	$('#workerTable').datagrid({
		title:'',
		singleSelect:false,
		idField:'id',
		method:'post',
		fitColumns:true,
		striped:true,
		url:'/SmartCC/dataConfig',
		queryParams:{
			method:"loadWorkers"
		},
		columns:[[
		    {field:'dh',align:'center',title:'choose',checkbox:'true',width:80},
			{field:'id',align:'center',title:'Staff No.',width:120},
			{field:'name',align:'center',title:'Name',width:120},
			{field:'groupName',align:'center',title:'Group',width:80},
			{field:'areaName',align:'center',title:'Area',width:300}
		]]
	});
}
function uploadTable_load(){
	$('#uploadTable').datagrid({
		title:'',
		singleSelect:false,
		idField:'id',
		method:'post',
		fitColumns:true,
		striped:true,
		rownumbers:true,
		url:'/SmartCC/smartccupload',
		
		queryParams:{
			method:"findCurrentBatchData"
		},
		columns:[[
		    {field:'dh',align:'center',title:'choose',checkbox:'true',width:80},
			{field:'componentName',align:'center',title:'Part',width:120},
			{field:'orderNum',align:'center',title:'Order',width:60},
			{field:'stationIds',align:'center',title:'Part Stations',width:230},
			{field:'knockingPoint',align:'center',title:'Knocking Point',width:80},
			{field:'type',align:'center',title:'Type',width:100,formatter:typeFormatter},
			{field:'devideType',align:'center',title:'Dispatch Mode',width:110,formatter:devideTypeformatter},
			{field:'state',align:'center',title:'Status',width:100,formatter:formatterState},
			{field:'workerId',align:'center',title:'Staff No.',width:120},
			{field:'batchNo',align:'center',title:'Batch No',width:120}
			
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
	     	
	    }  
	});
}
//上传excel加载任务列表
function uploadTasks(){
	
	var fileBoxValue = $('#fileBox').textbox('getValue');
	if(fileBoxValue == ""){
		$.messager.show({ // show error message
			title : 'Message',
			msg : "Please choose a Excel to upload."
		});
		//alert("");
		return;
	}
	
	$.messager.progress({});
	$('#demoForm').ajaxSubmit({
	data:{
		
	},
	success:function (data) {
			if(data.code == 0){
				$.messager.progress('close');
				//响应后移除遮罩层
				$(".showbox").stop(true).animate({'margin-top':'250px','opacity':'0'},400);
				$(".overlay").css({'display':'none','opacity':'0'});
			    $('#uploadTable').datagrid('reload');
			    $.messager.alert('Message', data.msg);
			}
			else{
				$.messager.progress('close');
				$.messager.show({ // show error message
					title : 'Message',
					msg : "System error."
				});
			}
		 }
	});
}
//为人员分配任务
function assignment(){
	$.messager.progress({});
	var workerRows = $('#workerTable').datagrid('getSelections');
	var taskRows = $('#uploadTable').datagrid('getSelections');
	var workersJson = JSON.stringify(workerRows);
	var taskRowJson = JSON.stringify(taskRows);
	$.post( "/SmartCC/smartccupload", 
			{ 
				method:"assignment",
				workersJson: workersJson,
				taskRowJson:taskRowJson
			},function (data){
				$.messager.progress('close');
				if(data.code == 0){
					$('#uploadTable').datagrid('reload');
					$.messager.show({ // show error message
						title : 'Message',
						msg : "Distribution success."
					});
				}
				else{
					$.messager.show({ // show error message
						title : 'Message',
						msg : data.msg
					});
				}
		},"json" );
}
/**
添加一条紧急任务
**/		
function addEmergency(){
$('#dlg').dialog('close'); // close the dialog
$.messager.progress({});
$('#fm').ajaxSubmit({
	data:{
		
	},
	success:function (data) {
		$.messager.progress('close');
		//响应后移除遮罩层
		
		if(data.code == 0){
			$.messager.alert('Message', data.msg);
			$('#uploadTable').datagrid('reload');
		}	
		else{
			$.messager.show({ // show error message
				title : 'Message',
				msg : data.msg
			});
		}
	}
});
}
function searchPersonByGroup(){
	var searchGroup = $("#searchGroup").combobox('getValue');
	$.post( "/SmartCC/dataConfig", 
			{ 
				searchGroup: searchGroup,
				method:'loadWorkers'
			},
			function (data){
				$('#workerTable').datagrid('loadData', data);
	},"json" );
}
function startOptimize(){
	$.messager.progress({});
	$.post( "/SmartCC/smartccupload", 
			{ 
				method:'startOptimize'
			},
			function (data){
				$.messager.progress('close');
				$('#uploadTable').datagrid('reload');
	},"json" );
}
function downLoad(){
	var searchComponent = $("#searchComponent").textbox('getValue');
	var searchType = $("#searchType").combobox('getValue');
	document.getElementById("downLoadBtn").href="/SmartCC/smartccupload?method=downUploadResult&searchComponent=" + searchComponent + "&searchType=" + searchType;
}