var valueColumns = [
    {
        name: "Firefox",
        title: "火狐"
    },
    {
        name: "IE"
    },
    {
        name: "Chrome"
    },
    {
        name: "Safari"
    },
    {
        name: "Opera"
    },
    {
        name: "Pig",
        title: "Ding"
    },
    {
        name: "Dog"
    },
    {
        name: "Cat"
    },
    {
        name: "Sheep"
    },
    {
        name: "Others"
    }
];

var primaryKeyColumns = [
    {
        name: "name"
    }
];

var valueColumn = {
    name: "value",
    title: "弱智值",
    logic: "octets"
};

var yAxisProperties = {
    baseTitle: "涨薪率"
};

var yAxisPropertyArray = [
    {
        baseTitle: "流量"
    },
    {
        baseTitle: "包数"
    },
    {
        baseTitle: "在线数"
    }
];

var cityPrimaryKeyColumns = [
    {
        name: "city"
    },
    {
        name: "area"
    }
];

var categoryColumn = {
    name: "name"
};

function itemClick(item) {
    var text = item.name;
    if (item.series + item.series.name && item.series.name != "default")
        text += " " + item.series.name;
    console.info(text + " click.");
};

var octetsValueColumns = [
    {
        name: "userOnline",
        title: "在线人数",
        yAxis: 2,
        stack: "userOnline"
    },
    {
        name: "downPackets",
        title: "下行包数",
        yAxis: 1,
        stack: "packets"
    },
    {
        name: "upPackets",
        title: "上行包数",
        yAxis: 1,
        stack: "packets"
    },
    {
        name: "downOctets",
        title: "下行流量",
        logic: "octets",
        stack: "octets"
    },
    {
        name: "upOctets",
        title: "上行流量",
        logic: "octets",
        stack: "octets"
    }
];

var packetsValueColumns = [
    {
        name: "userOnline",
        title: "在线人数"
    },
    {
        name: "downPackets",
        title: "下行包数"
    },
    {
        name: "upPackets",
        title: "上行包数"
    },
    {
        name: "downOctets",
        title: "下行流量",
        logic: "octets"
    },
    {
        name: "upOctets",
        title: "上行流量",
        logic: "octets"
    }
];

var columnCount = 3;
var pieChartOptions = [
    {
        type: "Pie",
        primaryKeyColumns: primaryKeyColumns,
        valueColumn: valueColumn,
        showPercent: true,
        showLegend: true,
        itemClick: itemClick
    },
    {
        type: "Pie",
        logic: "octets",
        valueColumns: valueColumns,
        itemClick: itemClick
    },
    {
        type: "Pie",
        logic: "octets",
        valueColumns: valueColumns,
        showLegend: true,
        showDataLabels: false,
        itemClick: itemClick
    }
];
var barChartOptions = [
    {
        type: "Bar",
        primaryKeyColumns: primaryKeyColumns,
        valueColumn: valueColumn,
        itemClick: itemClick
    },
    {
        type: "Bar",
        logic: "octets",
        valueColumns: valueColumns,
        yAxisProperties: yAxisProperties,
        itemClick: itemClick
    },
    {
        type: "Bar",
        logic: "octets",
        inverted: true,
        valueColumns: valueColumns,
        title: "超级无敌福利彩票获利比较图",
        subtitle: "The horizontal alignment of the subtitle",
        yAxisProperties: yAxisProperties,
        itemClick: itemClick
    }
];
var stackedBarChartOptions = [
    {
        type: "Bar",
        stacked: true,
        primaryKeyColumns: cityPrimaryKeyColumns,
        categoryColumns: [ categoryColumn],
        valueColumn: valueColumn,
        itemClick: itemClick
    },
    {
        type: "Bar",
        stacked: true,
        primaryKeyColumns: cityPrimaryKeyColumns,
        valueColumns: octetsValueColumns,
        yAxisProperties: yAxisPropertyArray,
        itemClick: itemClick
    },
    {
        type: "Bar",
        stacked: true,
        inverted: true,
        primaryKeyColumns: cityPrimaryKeyColumns,
        valueColumns: octetsValueColumns,
        yAxisProperties: yAxisPropertyArray,
        itemClick: itemClick
    }
]
var lineChartOptions = [
    {
        type: "Line",
        primaryKeyColumns: primaryKeyColumns,
        valueColumn: valueColumn,
        itemClick: itemClick
    },
    {
        type: "Line",
        logic: "octets",
        valueColumns: packetsValueColumns,
        yAxisProperties: yAxisProperties,
        itemClick: itemClick
    },
    {
        type: "Line",
        logic: "octets",
        inverted: true,
        valueColumns: packetsValueColumns,
        yAxisProperties: yAxisProperties,
        itemClick: itemClick
    }
];
var areSplineChartOptions = [
    {
        type: "AreaSpline",
        primaryKeyColumns: primaryKeyColumns,
        valueColumn: valueColumn,
        itemClick: itemClick
    },
    {
        type: "AreaSpline",
        logic: "octets",
        valueColumns: packetsValueColumns,
        yAxisProperties: yAxisProperties,
        itemClick: itemClick
    },
    {
        type: "AreaSpline",
        logic: "octets",
        inverted: true,
        valueColumns: packetsValueColumns,
        yAxisProperties: yAxisProperties,
        itemClick: itemClick
    }
];
var areChartOptions = [
    {
        type: "Area",
        primaryKeyColumns: primaryKeyColumns,
        valueColumn: valueColumn,
        itemClick: itemClick
    },
    {
        type: "Area",
        logic: "octets",
        valueColumns: packetsValueColumns,
        yAxisProperties: yAxisProperties,
        itemClick: itemClick
    },
    {
        type: "Area",
        logic: "octets",
        inverted: true,
        valueColumns: packetsValueColumns,
        yAxisProperties: yAxisProperties,
        itemClick: itemClick
    }
];

var RandomUtils = {
    interval: 30000,
    categories: ["Firefox", "IE", "Chrome", "Safari", "Opera", "Pig", "Dog", "Cat", "Sheep", "Others"],
    stackedDatas: [
        {
            city: "北京",
            area: ["中南海1", "中南海2"]
        },
        {
            city: "南京",
            area: ["白下区", "建邺区", "秦淮区", "雨花区"]
        },
        {
            city: "上海",
            area: ["黄埔区", "卢湾区", "闵行区", "徐汇区"]
        },
        {
            city: "天津",
            area: ["嘴皮子区"]
        }
    ],

    getRandomValue: function (baseValue) {
        return Math.round(Math.random() * baseValue);
    },

    refreshCharts: function (highCharts) {
        if ($.defined(highCharts) && highCharts.length > 0) {
            for (var i = 0; i < highCharts.length; i++) {
                var chartData = this.loadData(highCharts[i].chartOptions);
                highCharts[i].load(chartData);
            }
        }
    },

    destroyCharts: function (highCharts) {
        if ($.defined(highCharts) && highCharts.length > 0) {
            for (var i = 0; i < highCharts.length; i++) {
                if (highCharts[i]) {
                    highCharts[i].destroy();
                    delete highCharts[i].chartOptions.lastTime;
                    delete highCharts.highChart;
                }
            }
            for (var i = 0; i < chartOptions.length; i++) 
                delete chartOptions[i].lastTime;
        }
    },

    loadData: function (chartOption) {
        if (chartOption.type == "Line" || chartOption.type == "AreaSpline" || chartOption.type == "Area") 
            return this.loadTimeData(chartOption);
        if (chartOption.type == "Bar" && chartOption.stacked)
            return this.loadStackedData(chartOption);
        if (chartOption.type == "Pie" || chartOption.type == "Bar")
            return this.loadCategoryData(chartOption);
    },

    loadStackedData: function (chartOption) {
        var data = [];

        for (var i = 0; i < this.stackedDatas.length; i++) {
            var stackedData = this.stackedDatas[i];
            for (var j = 0; j < stackedData.area.length; j++) {
                var value = {
                    city: stackedData.city,
                    area: stackedData.area[j]
                };
                if ($.defined(chartOption.valueColumns)) {
                    for (var k = 0; k < chartOption.valueColumns.length; k++) {
                        var valueName = chartOption.valueColumns[k].name;
                        value[valueName] = this.getRandomValue((k + 1) * 100000000);
                    }
                    data.push(value);
                }
                else if ($.defined(chartOption.valueColumn)) {
                    for (var k = 0; k < packetsValueColumns.length; k++) {
                        var item = { };
                        $.extendInstance(item, value);
                        item[chartOption.categoryColumns[0].name] = packetsValueColumns[k].title;
                        item[chartOption.valueColumn.name] = this.getRandomValue((k + 1) * 100000000);
                        data.push(item);
                    }
                }
            }
        }
        return data;
    },

    loadCategoryData: function (chartOption, properties) {
        var data;
        if (!$.defined(chartOption.primaryKeyColumns)) {
            data = {};
            for (var i = 0; i < this.categories.length; i++)
                data[this.categories[i]] = this.getRandomValue((i + 1) * 100000000);
            $.extendInstance(data, properties);
            return data;
        }

        data = [];
        for (var i = 0; i < this.categories.length; i++) {
            var value = {
                name: this.categories[i],
                value: this.getRandomValue((i + 1) * 100000000)
            };
            $.extendInstance(value, properties);
            data.push(value);
        }
        return data;
    },

    loadTimeData: function (chartOption) {
        chartOption.timeRange.calculate();
        var result = {
            beginTime: chartOption.timeRange.beginTime,
            endTime: chartOption.timeRange.endTime,
            data: []
        };

        var valueColumns = chartOption.valueColumns;
        if (!$.defined(valueColumns) && $.defined(chartOption.valueColumn))
            valueColumns = [chartOption.valueColumn];

        var dateTime = chartOption.lastTime;
        if (!$.defined(dateTime))
            dateTime = chartOption.timeRange.beginTime;

        var primaryKeyData = this.createColumnsPrimaryKeyData(chartOption);
        for (var i = 0; i < primaryKeyData.length; i++)
            this.createRadomData(chartOption, valueColumns, dateTime, result, primaryKeyData[i])

        chartOption.lastTime = chartOption.timeRange.endTime;
        return result;
    },

    createColumnsPrimaryKeyData: function (chartOption) {
        var data = [], item;
        if ($.defined(chartOption.primaryKeyColumns)) {
            for (var i = 0; i < packetsValueColumns.length; i++) {
                var value = {};
                if ($.defined(packetsValueColumns[i].title))
                    value[chartOption.primaryKeyColumns[0].name] = packetsValueColumns[i].title;
                else if ($.defined(packetsValueColumns[i].name))
                    value[chartOption.primaryKeyColumns[0].name] = packetsValueColumns[i].name;
                data.push(value);
            }
        } else
            data.push("");
        return data;
    },

    createRadomData: function (chartOption, valueColumns, dateTime, result, primaryKeyData) {
        while (dateTime < result.endTime) {
            var value = {
                dateTime: dateTime
            };
            if ($.isObject(primaryKeyData))
                $.extendInstance(value, primaryKeyData);
            for (var i = 0; i < valueColumns.length; i++)
                value[valueColumns[i].name] = this.getRandomValue((i + 1) * 100000000);
            result.data.push(value);
            dateTime = dateTime.addMilliseconds(chartOption.timeRange.timeStep);
        }
    },
    
    extendChartProperties: {
        markerMap: {
            "1": { symbol: 'url(http://www.highcharts.com/demo/gfx/sun.png)' },
            "2": { symbol: 'url(http://www.highcharts.com/demo/gfx/snow.png)' }
        }
    },
    
    createHighCharts: function(chartOptions, initData) {
        var highCharts = [];
        this.createTable(chartOptions.length)
        for (var i = 0; i < chartOptions.length; i++) {
            var chartOption = chartOptions[i];
            if (chartOption.type == "Line" || chartOption.type == "AreaSpline" || chartOption.type == "Area") 
                chartOption.timeRange = new TimeRange("minute", -60);
            $.extendInstance(chartOption, this.extendChartProperties);
            var chartData;
            if (initData)
                chartData = this.loadData(chartOption);
            else  if ($.defined(chartOption.timeRange)) {
                chartOption.timeRange.calculate();
                chartOption.lastTime = chartOption.timeRange.endTime;
            }
            highCharts.push(HighChartsFactory.createHighChart("container" + i, chartOption, chartData));
        }
        return highCharts;
    },
    
    createTable : function (length) {
        var rowCount = Math.ceil(length / columnCount);
        for (var i = 0; i < rowCount; i++) {
            var html = "<tr>";
            for (var j = 0; j < columnCount; j++) {
                var id = "container" + (i * columnCount + j);
                html += "<td><div id=\"" + id + "\" > </div></td>";
            }
            html += "</tr>";
            $("#container").append(html);
        }
    }
};

