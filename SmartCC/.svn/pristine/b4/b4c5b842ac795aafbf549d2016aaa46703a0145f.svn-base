//柱状图json------------------------------
var option1 = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    legend: {
        data: []
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis:  {
        type: 'value'
    },
    yAxis: {
        type: 'category',
        data: []
    },
    series: []
};
//仪表盘图数据-----------------------------------------------------------
var option2 = {
    tooltip : {
        formatter: "{a} <br/>{b} : {c}%"
    },
    toolbox: {
        feature: {
        }
    },
    series: [
        {
            name: 'Finish Radio',
            type: 'gauge',
            axisLine: {            // 坐标轴线
                lineStyle: {       // 属性lineStyle控制线条样式
                    width: 10
                }
            },
            splitLine: {           // 分隔线
                length: 5,         // 属性length控制线长
                lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
                    color: 'auto'
                }
            },
            pointer: {
                width:5
            },
            detail: {
            	formatter:'{value}%',
            	width:5
            },
            data: [{value: 0, name: ''}]
        }
    ]
};
//-------------------------------------------------------------------------