isc.QueryForm.create({
    detailUrl: "./Modules/Demo/UserEdit.js",

    serviceProperties: {
        toolBar_ItemChanged: function (sender, item, value) {
            this.dataTileGrid.setColumnSize(value);
            return true;
        }
    },

    toolBarProperties: {
        appendDataProperties: [
            {
                type: isc.ToolStripBar.Types.selector,
                width: 100,
                valueMap: isc.i18nColumnSize,
                defaultValue: 2
            }
        ],
        hiddenItems: ["exportdoc", "remove", "view", "exit" ]
    },

    dataFilterProperties: {
        dataSetProperties: {
            dataFilter: {
                columns: {
                    name: {
                        title: "名称",
                        name: "name",
                        length: 40,
                        dataType: "string",
                        nullable: false
                    }
                }
            }
        }
    },

    getDataGridProperties: function () {
        if (this.dataTileGridProperties)
            return {
                name: "dataTileGrid",
                classType: isc.DataTileGrid
            };
    },

    dataTileGridProperties: {
        tableName: "chartInfo",
        tileHeight: 335,
        columnSize: 2,
        tileConstructor: "ChartWindow",
        tileProperties: {
            autoSize: false,
            canDragReposition: false,
            canDragResize: false
        }
    },

    dataSetProperties: {
        chartInfo: {
            dataURL: "[isomorphic]/../share/data/chartInfo.data.json",
            pageSize: 4,
            columns: {
                "title": {
                    "title": "名称",
                    "primaryKey": true,
                    "name": "title",
                    "length": 40,
                    "dataType": "string",
                    "nullable": false
                },
                "chartOptions": {
                    "title": "选项",
                    "name": "chartOptions",
                    "length": 40,
                    "dataType": "integer",
                    "nullable": false
                }
            }
        }
    },

    clearChart: function (chart) {
        if ($.defined(chart) && $.defined(chart.chartOptions))
            delete chart.chartOptions.lastTime;
    },

    getChartOptions: function (record) {
        if (!$.defined(this.chartOptions))
            this.chartOptions = [
                {
                    type: "Pie",
                    primaryKeyColumns: this.primaryKeyColumns,
                    valueColumn: this.valueColumn,
                    showPercent: true,
                    showLegend: true,
                    itemClick: this.itemClick
                },
                {
                    type: "Pie",
                    logic: "octets",
                    valueColumns: this.valueColumns,
                    itemClick: this.itemClick
                },
                {
                    type: "Pie",
                    logic: "octets",
                    valueColumns: this.valueColumns,
                    showLegend: true,
                    showDataLabels: false,
                    itemClick: this.itemClick
                },
                {
                    type: "Bar",
                    primaryKeyColumns: this.primaryKeyColumns,
                    valueColumn: this.valueColumn,
                    itemClick: this.itemClick
                },
                {
                    type: "Bar",
                    logic: "octets",
                    valueColumns: this.valueColumns,
                    yAxisProperties: this.yAxisProperties,
                    itemClick: this.itemClick
                },
                {
                    type: "Bar",
                    logic: "octets",
                    inverted: true,
                    valueColumns: this.valueColumns,
                    title: "超级无敌福利彩票获利比较图",
                    subtitle: "The horizontal alignment of the subtitle",
                    yAxisProperties: this.yAxisProperties,
                    itemClick: this.itemClick
                },
                {
                    type: "Bar",
                    stacked: true,
                    primaryKeyColumns: this.cityPrimaryKeyColumns,
                    categoryColumns: [ this.categoryColumn],
                    valueColumn: this.valueColumn,
                    itemClick: this.itemClick
                },
                {
                    type: "Bar",
                    stacked: true,
                    primaryKeyColumns: this.cityPrimaryKeyColumns,
                    valueColumns: this.octetsValueColumns,
                    yAxisProperties: this.yAxisPropertyArray,
                    itemClick: this.itemClick
                },
                {
                    type: "Bar",
                    stacked: true,
                    inverted: true,
                    primaryKeyColumns: this.cityPrimaryKeyColumns,
                    valueColumns: this.octetsValueColumns,
                    yAxisProperties: this.yAxisPropertyArray,
                    itemClick: this.itemClick
                },
                {
                    type: "Line",
                    primaryKeyColumns: this.primaryKeyColumns,
                    valueColumn: this.valueColumn,
                    itemClick: this.itemClick
                },
                {
                    type: "Line",
                    logic: "octets",
                    valueColumns: this.packetsValueColumns,
                    yAxisProperties: this.yAxisProperties,
                    itemClick: this.itemClick
                },
                {
                    type: "Line",
                    logic: "octets",
                    inverted: true,
                    valueColumns: this.packetsValueColumns,
                    yAxisProperties: this.yAxisProperties,
                    itemClick: this.itemClick
                },
                {
                    type: "AreaSpline",
                    primaryKeyColumns: this.primaryKeyColumns,
                    valueColumn: this.valueColumn,
                    itemClick: this.itemClick
                },
                {
                    type: "AreaSpline",
                    logic: "octets",
                    valueColumns: this.packetsValueColumns,
                    yAxisProperties: this.yAxisProperties,
                    itemClick: this.itemClick
                },
                {
                    type: "AreaSpline",
                    logic: "octets",
                    inverted: true,
                    valueColumns: this.packetsValueColumns,
                    yAxisProperties: this.yAxisProperties,
                    itemClick: this.itemClick
                },
                {
                    type: "Area",
                    primaryKeyColumns: this.primaryKeyColumns,
                    valueColumn: this.valueColumn,
                    itemClick: this.itemClick
                },
                {
                    type: "Area",
                    logic: "octets",
                    valueColumns: this.packetsValueColumns,
                    yAxisProperties: this.yAxisProperties,
                    itemClick: this.itemClick
                },
                {
                    type: "Area",
                    logic: "octets",
                    inverted: true,
                    valueColumns: this.packetsValueColumns,
                    yAxisProperties: this.yAxisProperties,
                    itemClick: this.itemClick
                }
            ];
        var chartOption = this.chartOptions[record.chartOptions];
        if (!$.defined(chartOption.timeRange)) {
            if (chartOption.type == "Line" || chartOption.type == "AreaSpline" || chartOption.type == "Area") { 
                chartOption.timeRange = new TimeRange("minute", -60);
                chartOption.timeRange.calculate();
                chartOption.lastTime = chartOption.timeRange.endTime;
            }
        }
        if (!$.defined(chartOption.loadInterval))
            chartOption.loadInterval = 30000;
        return chartOption;
    },

    getChartData: function (record) {
        var chartOption = this.getChartOptions(record);
        return this.loadData(chartOption);
    },

    valueColumns: [
        { name: "Firefox", title: "火狐" },
        { name: "IE" },
        { name: "Chrome" },
        { name: "Safari" },
        { name: "Opera" },
        { name: "Tom", title: "Tom Mouse"},
        { name: "Jerry"},
        { name: "Dog"},
        { name: "Pig"},
        { name: "Others"}
    ],

    primaryKeyColumns: [
        { name: "name" }
    ],

    valueColumn: {
        name: "value",
        title: "Kiss Times",
        logic: "octets"
    },

    yAxisProperties: {
        baseTitle: "Money"
    },

    yAxisPropertyArray: [
        { baseTitle: "Octets"},
        { baseTitle: "Dollar"},
        { baseTitle: "Mount"}
    ],

    cityPrimaryKeyColumns: [
        { name: "city" },
        { name: "area" }
    ],

    categoryColumn: { name: "name" },

    octetsValueColumns: [
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
    ],

    packetsValueColumns: [
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
    ],

    interval: 30000,
    categories: ["Firefox", "IE", "Chrome", "Safari", "Opera", "Tom", "Jerry", "Pig", "Dog", "Others" ],
    stackedDatas: [
        {
            city: "北京",
            area: ["中南海1", "中南海2"]
        },
        {
            city: "南京",
            area: ["白下区", "建邺区", "秦淮区", "建邺区"]
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

    refreshChart: function (chartOptions) {
        var chartData, i;
        for (i = 0; i < chartOptions.length; i++) {
            chartData = this.loadData(chartOptions[i]);
            chartOptions[i].highChart.load(chartData);
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
                    for (var k = 0; k < this.packetsValueColumns.length; k++) {
                        var item = { };
                        $.extendInstance(item, value);
                        item[chartOption.categoryColumns[0].name] = this.packetsValueColumns[k].title;
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
            for (var i = 0; i < this.packetsValueColumns.length; i++) {
                var value = {};
                if ($.defined(this.packetsValueColumns[i].title))
                    value[chartOption.primaryKeyColumns[0].name] = this.packetsValueColumns[i].title;
                else if ($.defined(this.packetsValueColumns[i].name))
                    value[chartOption.primaryKeyColumns[0].name] = this.packetsValueColumns[i].name;
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

    itemClick: function (item) {
        var text = item.name;
        if (item.series + item.series.name && item.series.name != "default")
            text += " " + item.series.name;
        console.info(text + " click.");
    }
});
