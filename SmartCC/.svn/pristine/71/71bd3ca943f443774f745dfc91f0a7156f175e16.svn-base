<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="js/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="js/echarts.min.js"></script>
	<script type="text/javascript">
	
	$(document).ready(function (){
		
		// 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));
	

        // 指定图表的配置项和数据
      




option = {
    title: {
        text: '工位映射'
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
        data: ['P00002', 'P00003']
    }],
    series: [{
        type: 'graph',
        layout: 'none',
        coordinateSystem: 'cartesian2d',
        symbolSize:16,
        categories: [{
            name: 'P00002',
            itemStyle: {
                normal: {
                    color: "#79ff79",
                }
            }
        }, {
            name: 'P00003',
            itemStyle: {
                normal: {
                    color: "#f9f900",
                }
            }
        }],
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
                    position: ['20%', '100%'],
                   // textStyle: {
                    //color: 'black'
                 //}
               }
           }
       },
        lineStyle: {
            normal: {
               color: '#6c6c6c',//线的颜色
                opacity: 1,
                width: 2,
                // curveness: 0.1
            }
        }
    }, {
        name: 'A',
        type: 'lines',
        coordinateSystem: 'cartesian2d',
        // zlevel: 2,
        effect: {
            show: true,
            smooth: false,
            trailLength: 0,
            symbol: "arrow",//流动箭头的样式
            color:'#ff9224',//流动线颜色
            symbolSize: 12
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
                width: 1,
                opacity: 0.4,
                // curveness: 0.1//贝塞尔曲线度
            }
        },
        data: [
              
           ]
    }]
};
var lineData;
var arrowData;
var count = 0;
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
        $(document).ready(function(){
        	
          	 $.post( "/SmartCC/servlet/EcharTestServlet", 
          			{
          		 		"aaa":"aaa"
          		 	},function (data){
          		 	option.series[0].data = data.data;
          		 	
          		 	option.xAxis.max = data.range.maxX;
          		 	option.xAxis.min = data.range.minX;
          		 	option.yAxis.max = data.range.maxY;
          		 	option.yAxis.min =data.range.minY;
          		 	lineData = data.lineData;
          		 	arrowData = data.arrowList;
          		 	option.series[0].links = lineData[0];
          		 myChart.setOption(option);
          		setInterval(show,500);//
       			},"json" );
          	 
          	 
           });
        function show(){
        	   var num = lineData.length;
        	   if(count < num){
        		   option.series[0].links = lineData[count];
        		   myChart.setOption(option);
        		   count ++;
        	   }
        	   else{
        		   option.series[1].data = arrowData;
        		   myChart.setOption(option);
        	   }
        	}
	});
        
        
    </script>
  </head>
  
  <body>
   <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div id="main" style="width: 50%;height:80%;background-color: blue"></div>
     
  </body>
</html>
