/**
 * Grid theme for Highcharts JS
 * @author Torstein Hønsi
 */

Highcharts.theme = {
    colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4'],
    chart: {
        backgroundColor: {
            linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
            stops: [
                [0, 'rgb(255, 255, 255)'],
                [1, 'rgb(240, 240, 255)']
            ]
        },
        borderWidth: 2,
        plotBackgroundColor: 'rgba(255, 255, 255, .9)',
        plotShadow: true,
        plotBorderWidth: 1
    },
    title: {
        style: {
            color: '#000',
            font: 'bold 16px "Trebuchet MS", Verdana, sans-serif'
        }
    },
    subtitle: {
        style: {
            color: '#666666',
            font: 'bold 12px "Trebuchet MS", Verdana, sans-serif'
        }
    },
    xAxis: {
        gridLineWidth: 1,
        lineColor: '#000',
        tickColor: '#000',
        labels: {
            style: {
                color: '#000',
                font: '11px Trebuchet MS, Verdana, sans-serif'
            }
        },
        title: {
            style: {
                color: '#333',
                fontWeight: 'bold',
                fontSize: '12px',
                fontFamily: 'Trebuchet MS, Verdana, sans-serif'

            }
        }
    },
    yAxis: {
        minorTickInterval: 'auto',
        lineColor: '#000',
        lineWidth: 1,
        tickWidth: 1,
        tickColor: '#000',
        labels: {
            style: {
                color: '#000',
                font: '11px Trebuchet MS, Verdana, sans-serif'
            }
        },
        title: {
            style: {
                color: '#333',
                fontWeight: 'bold',
                fontSize: '12px',
                fontFamily: 'Trebuchet MS, Verdana, sans-serif'
            }
        }
    },
    legend: {
        itemStyle: {
            font: '9pt Trebuchet MS, Verdana, sans-serif',
            color: 'black'

        },
        itemHoverStyle: {
            color: '#039'
        },
        itemHiddenStyle: {
            color: 'gray'
        }
    },
    labels: {
        style: {
            color: '#99b'
        }
    },

    navigation: {
        buttonOptions: {
            theme: {
                stroke: '#CCCCCC'
            }
        }
    }
};

// Apply the theme
var highchartsOptions = Highcharts.setOptions(Highcharts.theme);

/**
 * Highcharts Wrap for DangCat
 */
var DataModuleFactory = {
    getDateTimeValue: function (data) {
        if ($.isArray(data))
            return data[0];
        if ($.isObject(data) && $.defined(data.x))
            return data.x;
    },

    getPointValue: function (data) {
        if ($.isArray(data))
            return data[1];
        if ($.isObject(data) && $.defined(data.y))
            return data.y;
    },

    DefaultDataModuleProperties: {
        getKeyValues: function (data, keyColumns) {
            var keyValues;
            if (keyColumns) {
                if (!$.isArray(keyColumns))
                    keyColumns = [keyColumns];
                for (var i = 0; i < keyColumns.length; i++) {
                    var column = keyColumns[i];
                    if (column) {
                        var value = data[column.name];
                        if (value != undefined && value != null) {
                            if (keyValues)
                                keyValues += " " + value;
                            else
                                keyValues = value;
                        }
                    }
                }
            }
            return keyValues;
        },

        transValue: function (value) {
            if (value == undefined || value == null)
                return this.nullValue ? 0 : null;
            else if (value == 0)
                return this.zeroValue ? 0 : null;
            return value;
        }
    },

    DefaultCategoryDataModuleProperties: {
        getCategory: function (data) {
            return this.getKeyValues(data, this.primaryKeyColumns);
        },

        load: function (data) {
            if ($.defined(data) && data.data)
                data = data.data;

            var series;
            if ($.defined(data) && $.defined(data.series))
                series = data.series;
            else
                series = this.createSeries(data);

            if (!$.defined(this.options))
                this.options = {};
            this.options.series = series;
            return this.options;
        }
    },

    DefaultColumnDataModuleProperties: {
        getLabel: function (column) {
            if (column)
                return column.title ? column.title : column.name;
        },

        initSeries: function (series, index) {
            var column = this.valueColumns[index];
            if (column) {
                $.extendInstance(series, column);
                if (!$.defined(column.logic) && this.logic)
                    series.logic = this.logic;
                if (!$.defined(series.data))
                    series.data = [];
            }
        }
    },

    CategoryColumnDataModuleProperties: {
        createSeries: function (data) {
            var series = {};
            if (this.primaryKeyColumns) {
                for (var i = 0; i < this.valueColumns.length; i++) {
                    var seriesValue = {
                        name: this.getLabel(this.valueColumns[i])
                    };
                    this.initSeries(seriesValue, i);
                    series[seriesValue.name] = seriesValue;
                }
            } else {
                var seriesValue = {};
                for (var i = 0; i < this.valueColumns.length; i++)
                    this.initSeries(seriesValue, i);
                seriesValue.name = "default";
                series[seriesValue.name] = seriesValue;
            }
            return this.loadData(data, series);
        },

        loadData: function (data, series) {
            var column, value, seriesValue, row, category;
            if ($.defined(data)) {
                if ($.isArray(data)) {
                    for (var i = 0; i < data.length; i++) {
                        row = data[i];
                        for (var j = 0; j < this.valueColumns.length; j++) {
                            column = this.valueColumns[j];
                            value = this.transValue(row[column.name]);
                            seriesValue = series[column.name];
                            category = this.getCategory(row);
                            seriesValue.data.push([category, value]);
                        }
                    }
                } else if (series["default"]) {
                    for (var i = 0; i < this.valueColumns.length; i++) {
                        column = this.valueColumns[i];
                        value = this.transValue(data[column.name]);
                        category = this.getLabel(column);
                        series["default"].data.push([category, value]);
                    }
                }
            } else {
                var emptyValue = this.transValue();
                for (var seriesName in series) {
                    for (var i = 0; i < this.valueColumns.length; i++) {
                        column = this.valueColumns[i];
                        if (seriesName == "default") {
                            category = this.getLabel(column);
                            value = [category, emptyValue];
                        } else
                            value = emptyValue;
                        series[seriesName].data.push(value);
                    }
                }
            }
            return series;
        }
    },

    DefaultRowDataModuleProperties: {
        getBaseTitle: function () {
            var columns = this.valueColumns;
            if (columns && columns.length == 1) {
                var column = columns[0];
                return column.title ? column.title : column.name;
            }
        },

        getLogic: function () {
            var column = this.valueColumns;
            if (column && column.logic)
                return column.logic;
            return this.logic;
        },

        getSeriesValue: function (columns, data) {
            var seriesValue = this.getKeyValues(data, columns);
            if (!$.defined(seriesValue))
                seriesValue = "default";
            return seriesValue;
        }
    },

    CategoryRowDataModuleProperties: {
        initSeries: function (series) {
            var logic = this.getLogic();
            if (logic)
                series.logic = logic;
            var baseTitle = this.getBaseTitle();
            if (logic)
                series.baseTitle = baseTitle;
            if (!$.defined(series.data))
                series.data = [];
            return series;
        },

        createSeries: function (data) {
            var series = {};
            if (this.categoryColumns) {
                if ($.isArray(data)) {
                    for (var i = 0; i < data.length; i++) {
                        var seriesValue = this.getSeriesValue(this.categoryColumns, data[i]);
                        if (!$.defined(series[seriesValue]))
                            series[seriesValue] = this.initSeries({ name: seriesValue});
                    }
                }
            }
            else
                series["default"] = this.initSeries({ name: "default"});
            return this.loadData(data, series);
        },

        loadData: function (data, series) {
            if ($.isArray(data)) {
                var valueColumn = this.valueColumn;
                if (!$.defined(valueColumn) && $.defined(this.valueColumns))
                    valueColumn = this.valueColumns[0];
                var row, category, seriesValue, value;
                for (var i = 0; i < data.length; i++) {
                    row = data[i];
                    if (row) {
                        category = this.getCategory(row);
                        seriesValue = this.getSeriesValue(this.categoryColumns, row);
                        value = this.transValue(row[valueColumn.name]);
                        if (!$.defined(series[seriesValue].data))
                            series[seriesValue].data = [];
                        series[seriesValue].data.push([category, value]);
                    }
                }
            }
            return series;
        }
    },

    DefaultTimeDataModuleProperties: {
        dateTimeFieldName: "dateTime",
        timeType: "Hour",

        getTimeRange: function () {
            if ($.defined(this.timeType)) {
                if (!$.defined(this.timeRange))
                    this.timeRange = new TimeRange(this.timeType, this.timePeriod);
                this.timeRange.calculate(this.currentTime);
            }
            return this.timeRange;
        },

        getTimeStep: function () {
            var timeStep = this.timeStep;
            if (!$.defined(timeStep)) {
                var timeRange = this.getTimeRange();
                if ($.defined(timeRange))
                    timeStep = timeRange.timeStep;
            }
            return timeStep;
        },

        getBeginTime: function () {
            var beginTime = this.beginTime;
            if (!$.defined(beginTime)) {
                var timeRange = this.getTimeRange();
                if ($.defined(timeRange))
                    beginTime = timeRange.beginTime;
            }
            return this.getDateTime(beginTime);
        },

        getEndTime: function () {
            var endTime = this.endTime;
            if (!$.defined(endTime)) {
                var timeRange = this.getTimeRange();
                if ($.defined(timeRange))
                    endTime = timeRange.endTime;
            }
            return this.getDateTime(endTime);
        },

        getDateTime: function (dateTime) {
            if ($.defined(dateTime)) {
                var date = Date.parseValue(dateTime);
                if (date)
                    return date.getTime();
            }
        },

        createParams: function (params) {
            if ($.defined(this.timeType)) {
                params.timeRange = { timeType: this.timeType};
                if ($.defined(this.timePeriod))
                    params.timeRange.timePeriod = this.timePeriod;
            }
            if ($.defined(this.lastTime))
                params.lastTime = this.lastTime;
        },

        load: function (data) {
            if (data) {
                if (data.data) {
                    if ($.defined(data.beginTime))
                        this.beginTime = data.beginTime;
                    if ($.defined(data.endTime))
                        this.endTime = data.endTime;
                    if ($.defined(data.timeStep))
                        this.timeStep = data.timeStep;
                }
                var markerData;
                if ($.defined(data.markerData))
                    markerData = this.createSeriesData(data.markerData);
                this.loadData(data.data || data, markerData);
            }
            else
                this.loadData();
        },

        loadData: function (data, markerData) {
            if (!$.defined(this.options)) {
                this.options = {series: {}};
                this.createSeries(this.options.series, data);
            }

            if ($.defined(data)) {
                if ($.defined(data.series))
                    this.options.series = data.series;
                else {
                    var series = this.createSeriesData(data);
                    if ($.defined(markerData))
                        this.processMarkerData(series, markerData);

                    for (var seriesName in series) {
                        this.sortSeriesData(series[seriesName]);
                        var seriesItem = this.options.series[seriesName];
                        if ($.defined(seriesItem))
                            this.mergeSeriesData(seriesItem.data, series[seriesName]);
                        else {
                            this.createSeries(this.options.series, data);
                            this.options.series[seriesName].data = series[seriesName];
                        }
                    }
                }
                this.fixSeriesTimeData(this.options.series);
            }
        },

        sortSeriesData: function (data) {
            data.sort(function (src, dst) {
                var srcValue = DataModuleFactory.getDateTimeValue(src),
                    dstValue = DataModuleFactory.getDateTimeValue(dst);
                if (srcValue > dstValue)
                    return 1;
                else if (srcValue < dstValue)
                    return -1;
                return 0;
            });
        },

        fixSeriesTimeData: function (series) {
            if ($.defined(this.lastTime))
                delete this.lastTime;

            var index, data,
                timeStep = this.getTimeStep(),
                beginTime = this.getBeginTime(),
                endTime = this.getEndTime(),
                diff, currentTime, nextTime, emptyValue = this.transValue();

            for (var seriesName in series) {
                index = 0;
                data = series[seriesName].data;
                while (index < data.length) {
                    currentTime = DataModuleFactory.getDateTimeValue(data[index]);
                    if (currentTime < beginTime || currentTime > endTime) {
                        data.splice(index, 1);
                        continue;
                    }
                    if (index < data.length - 1) {
                        nextTime = DataModuleFactory.getDateTimeValue(data[index + 1]);
                        diff = nextTime - currentTime;
                        if (diff >= timeStep * 2) {
                            data.splice(index, 0, [currentTime + timeStep, emptyValue]);
                            index++;
                            if (diff >= timeStep * 3) {
                                data.splice(index + 1, 0, [currentTime + timeStep, [nextTime - timeStep, emptyValue]]);
                                index++;
                            }
                        }
                    }
                    if (!$.defined(this.lastTime))
                        this.lastTime = currentTime;
                    else
                        this.lastTime = Math.max(this.lastTime, currentTime);
                    index++;
                }
            }
        },

        mergeSeriesData: function (chartSeriesData, optionsSeriesData) {
            var chartSeriesDataIndex = 0, optionsSeriesDataIndex = 0,
                chartData, optionsData, chartDateTime, optionsDateTime;

            while (chartSeriesDataIndex < chartSeriesData.length && optionsSeriesDataIndex < optionsSeriesData.length) {
                chartData = chartSeriesData[chartSeriesDataIndex];
                optionsData = optionsSeriesData[optionsSeriesDataIndex];
                chartDateTime = DataModuleFactory.getDateTimeValue(chartData);
                optionsDateTime = DataModuleFactory.getDateTimeValue(optionsData);
                if (chartDateTime < optionsDateTime)
                    chartSeriesDataIndex++;
                else if (chartDateTime > optionsDateTime) {
                    chartSeriesData.splice(chartSeriesDataIndex, 0, optionsData);
                    optionsSeriesDataIndex++;
                } else {
                    chartSeriesData.splice(chartSeriesDataIndex, 1, optionsData);
                    chartSeriesDataIndex++;
                    optionsSeriesDataIndex++;
                }
            }
            while (optionsSeriesDataIndex < optionsSeriesData.length)
                chartSeriesData.push(optionsSeriesData[optionsSeriesDataIndex++]);
        },

        processMarkerData: function (seriesData, markerData) {
            if (!$.defined(markerData))
                return;

            var markerDataValue, dateTime, seriesDataValue, markerDataIndex, markerProperties;
            for (var seriesName in seriesData) {
                markerDataValue = markerData[seriesName];
                if ($.defined(markerDataValue)) {
                    markerDataIndex = 0;
                    seriesDataValue = seriesData[seriesName];
                    for (var i = 0; i < seriesDataValue.length; i++) {
                        dateTime = seriesDataValue[i][0];
                        if (dateTime < markerDataValue[markerDataIndex][0])
                            continue;
                        if (dateTime == markerDataValue[markerDataIndex][0]) {
                            markerProperties = this.markerMap[markerDataValue[markerDataIndex][1] + ""];
                            if ($.defined(markerProperties)) {
                                markerProperties.enabled = true;
                                seriesDataValue[i] = {
                                    x: dateTime,
                                    y: seriesDataValue[i][1],
                                    marker: markerProperties
                                };
                            }
                            markerDataIndex++;
                        }
                        if (markerDataIndex >= markerDataValue.length)
                            break;
                    }
                }
            }
        }
    },

    TimeColumnDataModuleProperties: {
        createSeries: function (series) {
            for (var i = 0; i < this.valueColumns.length; i++) {
                var column = this.valueColumns[i];
                var seriesValue = series[column.name];
                if (!$.defined(seriesValue)) {
                    seriesValue = {
                        name: column.name,
                        data: []
                    };
                    this.initSeries(seriesValue, i);
                    series[seriesValue.name] = seriesValue;
                }
            }
        },

        createSeriesData: function (data) {
            var series = {};
            for (var i = 0; i < this.valueColumns.length; i++) {
                var column = this.valueColumns[i];
                if (!$.defined(series[column.name]))
                    series[column.name] = [];
            }
            if ($.isArray(data)) {
                for (var i = 0; i < data.length; i++) {
                    var row = data[i];
                    var dateTime = this.getDateTime(row[this.dateTimeFieldName]);
                    for (var j = 0; j < this.valueColumns.length; j++) {
                        var seriesName = this.valueColumns[j].name;
                        var value = this.transValue(row[seriesName]);
                        series[seriesName].push([dateTime, value]);
                    }
                }
            }
            return series;
        }
    },

    TimeRowDataModuleProperties: {
        createSeries: function (series, data) {
            var seriesItem;
            if ($.defined(data)) {
                for (var i = 0; i < data.length; i++) {
                    seriesItem = this.createSeriesItem(series, data[i]);
                    if (seriesItem && !$.defined(series[seriesItem.name]))
                        series[seriesItem.name] = seriesItem;
                }
            }
        },

        createSeriesItem: function (series, data) {
            var seriesItem;
            if (this.primaryKeyColumns) {
                var seriesName = this.getSeriesValue(this.primaryKeyColumns, data);
                seriesItem = series[seriesName];
                if (!$.defined(seriesItem)) {
                    seriesItem = {
                        name: seriesName,
                        logic: this.getLogic(),
                        data: []
                    };
                }
            }
            else {
                seriesItem = series["default"];
                if (!$.defined(seriesItem)) {
                    seriesItem = {
                        name: "default",
                        logic: this.getLogic(),
                        data: []
                    };
                }
            }
            return seriesItem;
        },

        createSeriesItemData: function (data, series) {
            var seriesValue, seriesName;
            if (this.primaryKeyColumns)
                seriesName = this.getSeriesValue(this.primaryKeyColumns, data);
            else
                seriesName = "default";
            if (!$.defined(series[seriesName]))
                series[seriesName] = [];
            return series[seriesName];
        },

        createSeriesData: function (data) {
            var series = {}, seriesValue,
                valueColumn = this.valueColumn;
            if ($.isArray(data)) {
                if (!$.defined(valueColumn) && $.defined(this.valueColumns))
                    valueColumn = this.valueColumns[0];
                for (var i = 0; i < data.length; i++) {
                    var row = data[i];
                    seriesValue = this.createSeriesItemData(row, series);
                    var dateTime = this.getDateTime(row[this.dateTimeFieldName]);
                    var value = this.transValue(row[valueColumn.name]);
                    seriesValue.push([dateTime, value]);
                }
            }
            return series;
        }
    },

    createDataModule: function (properties) {
        var dataModule = {};
        var dataModuleType = properties.dataModuleType;

        $.extendInstance(dataModule, this.DefaultDataModuleProperties, this["Default" + dataModuleType + "DataModuleProperties"]);
        if ($.defined(properties.valueColumn))
            $.extendInstance(dataModule, this.DefaultRowDataModuleProperties, this[dataModuleType + "RowDataModuleProperties"]);
        else
            $.extendInstance(dataModule, this.DefaultColumnDataModuleProperties, this[dataModuleType + "ColumnDataModuleProperties"]);

        var propertyNames = ["nullValue", "zeroValue", "dateTimeFieldName", "valueColumn", "valueColumns", "categoryColumns", "primaryKeyColumns", "markerMap", "markerData"];
        for (var i = 0; i < propertyNames.length; i++) {
            var property = properties[propertyNames[i]];
            if ($.defined(property) && !$.defined(dataModule[propertyNames[i]]))
                dataModule[propertyNames[i]] = property;
        }
        propertyNames = ["logic", "beginTime", "endTime", "timeStep", "timeType", "timePeriod"];
        for (var i = 0; i < propertyNames.length; i++) {
            var property = properties[propertyNames[i]];
            if ($.defined(property))
                dataModule[propertyNames[i]] = property;
        }
        return dataModule;
    }
};

(function (H) {
    var createAreaSplinePath = function () {
        if (this.options.stacking && this.closedStacks) {
            var graphPath;
            for (var i = this.index + 1; i < this.chart.series.length; i++) {
                if (this.chart.series[i].visible) {
                    graphPath = this.chart.series[i].graphPath;
                    break;
                }
            }
            if (graphPath) {
                var areaPath = [].concat(this.graphPath);
                var segmentPath = [];
                var lastIndex = graphPath.length - 1;
                for (var i = lastIndex; i >= 0; i--) {
                    var value = graphPath[i];
                    if (typeof (value) != "number") {
                        segmentPath.push(value);
                        if (value == "C")
                            segmentPath.push(graphPath[i + 3], graphPath[i + 4], graphPath[i + 1], graphPath[i + 2], graphPath[i - 2], graphPath[i - 1]);
                        else {
                            for (var j = lastIndex; j > i; j -= 2)
                                segmentPath.push(graphPath[j - 1], graphPath[j]);
                        }
                        lastIndex = i - 1;
                    }
                }
                areaPath.push('L', graphPath[graphPath.length - 2], segmentPath[graphPath.length - 1]);
                this.areaPath = areaPath.concat(segmentPath);
            }
        }
    };

    H.wrap(H.Series.prototype, 'drawGraph', function (proceed) {
        proceed.apply(this, Array.prototype.slice.call(arguments, 1));
        if (this.chart.options.chart.type == "areaspline")
            createAreaSplinePath.call(this);
    });

    H.Axis.prototype.getDataFormator = function () {
        return DataFormatorFactory.getDataFormator(this.options.logic);
    };

    H.Axis.prototype.formatValue = function (value) {
        if ($.defined(this.transRate))
            value *= this.transRate;
        return value.format(this.pattern);
    };

    H.Axis.prototype.formatTime = function (date, pattern) {
        var dateTime = date;
        if ($.isNumber(date)) {
            dateTime = new Date();
            dateTime.setTime(date);
        }
        return dateTime.format(pattern || this.pattern || this.options.pattern);
    };

    H.Axis.prototype.formatData = function (value) {
        var pattern = this.pattern ? this.pattern : this.options.pattern;
        return this.getDataFormator().format(value, pattern);
    };

    var initTitle = function () {
        if (this.options.logic) {
            var max = this.max;
            if (Math.abs(this.min) > Math.abs(max))
                max = Math.abs(this.min);
            var dataFormator = this.getDataFormator();
            this.unit = dataFormator.calculatePerfectUnit(max);
            if (this.options.initTitle)
                this.options.initTitle(this);
            this.transRate = dataFormator.calculateTransRate(this.unit);
            if (this.axisTitle)
                this.axisTitle.attr("text", this.options.title.text);
        }
    };

    var getXAxisTickInterval = function (tickInterval) {
        if (this.options.type == "datetime") {
            var secondLength = Math.round((this.max - this.min) / 1000);
            var minuteLength = Math.round(secondLength / 60);
            var hourLength = Math.round(minuteLength / 60);
            var dayLength = Math.round(hourLength / 24);
            if (dayLength >= 1) {
                if (dayLength == 365 || dayLength == 366)
                    tickInterval = 60 * 24 * 3600000;
                else if (dayLength >= 28 && dayLength <= 31)
                    tickInterval = 6 * 24 * 3600000;
                else if (dayLength == 7)
                    tickInterval = 24 * 3600000;
                else if (dayLength == 1)
                    tickInterval = 4 * 3600000;
                else
                    tickInterval = Math.round(hourLength / 6) * 3600000;
            } else if (hourLength > 1)
                tickInterval = Math.round(minuteLength / 6) * 60000;
            else if (hourLength == 1)
                tickInterval = 10 * 60000;
            else if (minuteLength >= 3)
                tickInterval = Math.round(secondLength / 6) * 1000;
            else
                tickInterval = 30 * 1000;

            delete this.lablesPattern;
            if (tickInterval >= 60 * 24 * 3600000) {
                this.pattern = "yyyy-MM";
                this.lablesPattern = "yyyy-MM-dd";
            } else if (tickInterval > 24 * 3600000) {
                if (tickInterval == 7 * 24 * 3600000) {
                    this.pattern = "dddd";
                    this.lablesPattern = "yyyy-MM-dd";
                } else if (tickInterval >= 6 * 24 * 3600000) {
                    this.pattern = "MM-dd";
                    this.lablesPattern = "MM-dd HH";
                } else
                    this.pattern = "MM-dd HH";
            }
            else if (tickInterval > 3600000)
                this.pattern = "MM-dd HH";
            else if (tickInterval > 60000)
                this.pattern = "HH:mm";
            else
                this.pattern = "HH:mm:ss";
            if (!$.defined(this.lablesPattern))
                this.lablesPattern = "MM-dd HH:mm";
        }
        return tickInterval;
    };

    var getYAxisTickInterval = function (tickInterval) {
        if (this.options.logic) {
            initTitle.apply(this);
            var max = this.max;
            if (Math.abs(this.min) > Math.abs(max))
                max = Math.abs(this.min);
            var valueLength = (max - this.min) * this.transRate;
            var transRate = 1;
            if (valueLength != 0) {
                while (valueLength > 10) {
                    valueLength /= 10;
                    transRate *= 10;
                }
                while (valueLength < 1) {
                    valueLength *= 10;
                    transRate *= 0.1;
                }
            }

            var intervalValue;
            if (valueLength > 5)
                intervalValue = 1;
            else if (valueLength > 2.5)
                intervalValue = 0.5;
            else if (valueLength > 1)
                intervalValue = 0.25;
            else
                intervalValue = 0.1;
            if (this.chart.chartHeight < 300)
                intervalValue *= 2;

            var value = intervalValue * transRate;
            if (value >= 1)
                this.pattern = "#";
            else if (value >= 0.1)
                this.pattern = "#.0";
            else if (value >= 0.01)
                this.pattern = "#.00";
            else
                this.pattern = "#.000";
            var transTickInterval = Math.round(value / this.transRate);
            if (transTickInterval > 0)
                tickInterval = transTickInterval;
            else
                tickInterval = value;
            this.options.tickInterval = tickInterval;
        }
        return tickInterval;
    };

    H.Axis.prototype.postProcessTickInterval = function (tickInterval) {
        if (this.isXAxis)
            tickInterval = getXAxisTickInterval.apply(this, arguments);
        else
            tickInterval = getYAxisTickInterval.apply(this, arguments);
        return tickInterval;
    };
}(Highcharts));

var DefaultYAxisProperties = {
    showEmpty: false,
    min: 0,
    labels: {
        formatter: function () {
            return this.axis.formatValue(this.value);
        }
    },
    initTitle: function (aixes) {
        if (!$.defined(aixes.options.title))
            aixes.options.title = { text: "Values"};
        if (!$.defined(this.baseTitle))
            this.baseTitle = aixes.options.title.text;
        var title = this.baseTitle;
        if (aixes.unit)
            title += "(" + aixes.unit + ")";
        aixes.options.title.text = title;
    }
};

var DefaultSeriesProperties = {};

var DefaultCategoryPlotOptionsEvents = {
    click: function (e) {
        var item = {
            point: e.point,
            series: this,
            name: (e.point.name || e.point.category || this.chart.options.xAxis[0].categories[e.point.x])
        };
        if (this.chart.options.itemClick)
            this.chart.options.itemClick(item);
    }
};

var DefaultTimePlotOptionsEvents = {
    click: function (e) {
        var item = {
            point: e.point,
            series: this,
            name: e.currentTarget.name
        };
        if (this.chart.options.itemClick)
            this.chart.options.itemClick(item);
    }
}

var DefaultChartProperties = {
    chart: {
        animation: false,
        spacing: [10, 5, 2, 2],
        borderWidth: 0
    },

    stacked: false,

    exporting: {
        enabled: false,
        url: '/chartExport',
        buttons: {
            contextButton: {
                menuItems: [
                    {
                        textKey: 'printChart',
                        onclick: function () {
                            this.print();
                        }
                    },
                    {
                        separator: true
                    },
                    {
                        textKey: 'downloadPNG',
                        onclick: function () {
                            this.options.exportChart("png");
                        }
                    },
                    {
                        textKey: 'downloadPDF',
                        onclick: function () {
                            this.options.exportChart("pdf");
                        }
                    }
                ]
            }
        }
    },

    credits: {
        enabled: false
    },

    title: {
        text: ''
    },

    createInstance: function () {
        this.clearHighChart();
        this.highChart = new Highcharts.Chart(this);
    },

    clearHighChart: function () {
        if (this.highChart) {
            this.highChart.destroy();
            delete this.highChart;
            delete this.series;
        }
    },

    reRender: function (width, height) {
        //this.clearHighChart();
        this.chart.width = width;
        this.chart.height = height;
        //this.highChart = new Highcharts.Chart(this);
        this.refresh();
    },

    refresh: function () {
        if (this.highChart)
            this.highChart.reflow();
    },

    resize: function (width, height) {
        this.chart.width = width;
        this.chart.height = height;
        if (this.highChart)
            this.highChart.setSize(width, height, false);
    },

    destroy: function () {
        this.clearHighChart();
        if (this.dataModule)
            delete this.dataModule;
    },

    initialize: function () {
    },

    legend: {
        margin: 5,
        labelFormatter: function () {
            if (this.options.title)
                return this.options.title;
            return this.name;
        }
    },

    getBaseTitle: function () {
        if (!$.defined(this.baseTitle)) {
            if (this.valueColumns && this.valueColumns.length == 1) {
                var column = this.valueColumns[0];
                return column.title ? column.title : column.name;
            }
        }
        return this.baseTitle;
    },

    getDataModule: function () {
        if (!$.defined(this.dataModule))
            this.dataModule = DataModuleFactory.createDataModule(this);
        return this.dataModule;
    },

    exportChart: function (type) {
        var $form = $("<form>");
        $form.attr({ target: '_self', method: 'post', action: this.exporting.url });
        $form.append($('<input>').attr({ type: "hidden", name: "type", value: type}));
        $form.append($('<input>').attr({ type: "hidden", name: "options", value: $.toJSON(this.chartOptions) }));
        $form.append($('<input>').attr({ type: "hidden", name: "data", value: $.toJSON(this.getDataModule().options) }));
        $form.append($('<input>').attr({ type: "hidden", name: "width", value: this.chart.width }));
        $form.append($('<input>').attr({ type: "hidden", name: "height", value: this.chart.height }));
        $form.appendTo('body').submit().remove();
    },

    removeFunctions: function (options) {
        if ($.isObject(options)) {
            for (var name in options) {
                if ($.isObject(options[name]))
                    this.removeFunctions(options[name]);
                else if ($.isFunction(options[name]))
                    delete options[name];
            }
        }
    },

    ajaxLoad: function (params, clean, success, error) {
        if (this.dataParams)
            $.extendInstance(params, this.dataParams);
        if (this.getDataModule().createParams)
            this.getDataModule().createParams(params);
        var chart = this;
        $.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: this.dataURL,
            cache: false,
            data: $.toJSON(params),
            success: function (data) {
                chart.load(data, clean);
                if (success)
                    success(data);
            },
            error: function (request, textStatus, errorThrown) {
                if (error)
                    error(request, textStatus, errorThrown);
            }
        });
    },

    load: function (data, clean) {
        var dataModule = this.getDataModule();
        if (clean)
            delete dataModule.options;
        dataModule.load(data);
        this.refresh();
    },

    createSeries: function (options) {
        if ($.defined(this.series))
            return;

        var seriesArray = [];
        if ($.defined(options)) {
            for (var name in options.series) {
                var series = options.series[name];
                $.extendInstance(series, DefaultSeriesProperties);
                if (this.seriesProperties) {
                    if ($.isArray(this.seriesProperties)) {
                        for (var i = 0; i < this.seriesProperties.length; i++) {
                            if (this.seriesProperties[i].name == name) {
                                $.extendInstance(series, this.seriesProperties[i]);
                                break;
                            }
                        }
                    }
                    else
                        $.extendInstance(series, this.seriesProperties);
                }
                seriesArray.push(series);
            }
        }
        this.series = seriesArray;
    }
};

var DefaultPieChartProperties = {
    showPercent: false,

    chart: {
        type: 'pie',
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false
    },

    tooltip: {
        formatter: function () {
            return "<b>" + this.series.chart.options.getLabel(this) + "</b>: " + this.series.chart.options.valueFormat(this);
        }
    },

    plotOptions: {
        pie: {
            allowPointSelect: true,
            animation: false,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                formatter: function () {
                    return "<b>" + this.series.chart.options.getLabel(this) + "</b>: " + this.series.chart.options.valueFormat(this);
                }
            },
            showInLegend: true,
            events: DefaultCategoryPlotOptionsEvents
        }
    },

    legend: {
        enabled: false,
        align: "right",
        layout: 'vertical',
        verticalAlign: 'middle',
        labelFormatter: function () {
            return this.series.chart.options.getLabel(this) + ": " + this.series.chart.options.valueFormat(this);
        }
    },

    getLabel: function (item) {
        if (this.xAxis[0].categories) {
            var x = item.point ? item.point.x : item.x;
            return this.xAxis[0].categories[x];
        }
        return item.key ? item.key : item.name;
    },

    valueFormat: function (item) {
        var text;
        if (this.showPercent)
            text = Highcharts.numberFormat(item.percentage, 2) + "%";
        else {
            var logic = this.logic;
            if (!$.defined(logic))
                logic = this.yAxis[0].logic;
            var dataFormator = DataFormatorFactory.getDataFormator(logic);
            text = dataFormator.format(item.y);
        }
        return text;
    },

    createSeries: function (options) {
        var options = this.getDataModule().options;
        var series = options.series["default"];
        if (!$.defined(series.type))
            series.type = "pie";
        if (!$.defined(series.data))
            series.data = [];
        this.series = [ series ];
    },

    isChanged: function () {
        var options = this.getDataModule().options,
            chartSeries, optionSeries;
        for (var i = 0; i < this.highChart.series.length; i++) {
            chartSeries = this.highChart.series[i];
            optionSeries = options.series[chartSeries.name];
            if (chartSeries.data.length != optionSeries.data.length)
                return true;
            for (var j = 0; j < chartSeries.data.length; j++) {
                if (chartSeries.data[j].name != DataModuleFactory.getDateTimeValue(optionSeries.data[j]))
                    return true;
            }
        }
        return false;
    },

    refresh: function () {
        if (!$("#" + this.chart.renderTo).is(":visible"))
            return;

        var options = this.getDataModule().options;
        var series = options.series["default"];
        if (!$.defined(series.data) || series.data.length == 0)
            return;

        if (!$.defined(this.highChart) || this.isChanged()) {
            this.initialize();
            this.createSeries();
            this.createInstance();
        }
        else {
            for (var i = 0; i < this.highChart.series.length; i++) {
                var chartSeries = this.highChart.series[i],
                    optionSeries = options.series[chartSeries.name];
                chartSeries.setData(optionSeries.data, false, false);
            }
            this.highChart.reflow();
            this.highChart.redraw();
        }
    }
};

var DefaultAxisChartProperties = {
    legend: {
        enabled: true
    },

    createXAxis: function (options) {
        if (!$.defined(this.xAxis))
            this.xAxis = {};
        if (this.dateTimePattern)
            this.xAxis.dateTimePattern = this.dateTimePattern;
        if ($.defined(options)) {
            if (options.categories)
                this.xAxis.categories = options.categories;
            var dataModule = this.getDataModule();
            if ($.defined(dataModule.getBeginTime))
                this.xAxis.min = dataModule.getBeginTime();
            if ($.defined(dataModule.getEndTime))
                this.xAxis.max = dataModule.getEndTime();
        }
    },

    createYAxisMaxValue: function (yAxis, series) {
        if ($.defined(series.data) && $.isArray(series.data) && series.data.length > 0) {
            if (!this.stacked) {
                var max = series.data.max(function (src, dst) {
                    return DataModuleFactory.getPointValue(src) - DataModuleFactory.getPointValue(dst);
                });
                max = $.defined(max) ? DataModuleFactory.getPointValue(max) : 10;
                yAxis.max = $.defined(yAxis.max) ? Math.max(yAxis.max, max) : max;
            }
        } else
            yAxis.max = $.defined(yAxis.max) ? Math.max(yAxis.max, 10) : 10;
    },

    createYAxis: function (options) {
        var yAxis = [];
        if ($.defined(options) && options.series) {
            for (var name in options.series) {
                var series = options.series[name];
                var yAxisProperties = this.getYAxisProperties(yAxis, series);
                yAxisProperties.logic = (series.logic || "value");
                if (series.baseTitle)
                    yAxisProperties.baseTitle = series.baseTitle;
                if ($.defined(series.visible) && !series.visible)
                    continue;
                this.createYAxisMaxValue(yAxisProperties, series);
            }
        }
        if (yAxis.length == 0) {
            var yAxisProperties = this.getYAxisProperties(yAxis, {});
            yAxisProperties.logic = "value";
            yAxisProperties.max = 10;
        }

        for (var i = 0; i < yAxis.length; i++) {
            if (i > 0 && !$.defined(yAxis[i].opposite))
                yAxis[i].opposite = true;
        }
        this.yAxis = yAxis;
    },

    getYAxisProperties: function (yAxis, series) {
        var yAxisId = $.defined(series.yAxis) ? series.yAxis : 0;
        var yAxisProperties;
        if ($.isNumber(yAxisId)) {
            if ($.defined(yAxis[yAxisId]))
                yAxisProperties = yAxis[yAxisId];
            else {
                yAxisProperties = $.extendObject(DefaultYAxisProperties);
                yAxis[yAxisId] = yAxisProperties;
                var baseTitle = this.getBaseTitle();
                if (baseTitle)
                    yAxisProperties.baseTitle = baseTitle;
                if (this.yAxisProperties) {
                    if ($.isArray(this.yAxisProperties))
                        $.extendInstance(yAxisProperties, this.yAxisProperties[yAxisId]);
                    else
                        $.extendInstance(yAxisProperties, this.yAxisProperties);
                }
            }
        }
        else if ($.isString(yAxisId)) {
            for (var i = 0; i < yAxis.length; i++) {
                if (yAxis[i].id == yAxisId) {
                    yAxisProperties = yAxis[i];
                    break;
                }
            }
            if (this.baseTitle)
                yAxisProperties.baseTitle = this.baseTitle;
            if (this.yAxisProperties) {
                if ($.isArray(this.yAxisProperties)) {
                    for (var i = 0; i < this.yAxisProperties.length; i++) {
                        if (this.yAxisProperties[i].id == yAxisId) {
                            $.extendInstance(yAxisProperties, this.yAxisProperties[i]);
                            break;
                        }
                    }
                }
                else
                    $.extendInstance(yAxisProperties, this.yAxisProperties);
            }
        }
        return yAxisProperties;
    },

    calculateAxisMaxValue: function () {
        var i, j, max, axis, maxValue;
        for (i = 0; i < this.highChart.yAxis.length; i++) {
            axis = this.highChart.yAxis[i];
            if (this.stacked)
                delete axis.options.max;
            else {
                axis.options.max = 10;
                for (j = 0; j < axis.series.length; j++) {
                    max = axis.series[j].yData.max(function (src, dst) {
                        return src - dst;
                    });
                    if ($.defined(max))
                        axis.options.max = Math.max(max, axis.options.max);
                }
            }
        }
        var dataModule = this.getDataModule();
        for (i = 0; i < this.highChart.xAxis.length; i++) {
            axis = this.highChart.xAxis[i];
            if ($.defined(dataModule.getBeginTime))
                axis.options.min = dataModule.getBeginTime();
            if ($.defined(dataModule.getEndTime))
                axis.options.max = dataModule.getEndTime();
        }
    },

    refresh: function () {
        if (!$("#" + this.chart.renderTo).is(":visible"))
            return;

        var options = this.getDataModule().options, dataValid = false;
        for (var seriesName in options.series) {
            var series = options.series[seriesName];
            if ($.defined(series)) {
                dataValid = true;
                break;
            }
        }
        if (!dataValid)
            return;

        if (!$.defined(this.highChart)) {
            this.createXAxis(options);
            this.createYAxis(options);
            this.createSeries(options);
            this.initialize();
            this.createInstance();
        }
        else {
            for (var i = 0; i < this.highChart.series.length; i++) {
                var chartSeries = this.highChart.series[i],
                    optionsSeries = options.series[chartSeries.name];
                chartSeries.setData(optionsSeries.data, false);
            }
            this.calculateAxisMaxValue();
            this.highChart.reflow();
            this.highChart.redraw();
        }
    }
};

var DefaultBarChartProperties = {
    chart: {
        type: 'column'
    },

    tooltip: {
        shared: true,
        formatter: function () {
            var points = this.points;
            if (points == undefined || points == null)
                points = [this];
            var text;
            for (var i = 0; i < points.length; i++) {
                var point = points[i];
                var yAxis = point.series.yAxis;
                if (i == 0) {
                    text = "<b>" + point.key + ": ";
                    if (point.total != undefined && point.total != null)
                        text += yAxis.formatData(point.total);
                    text += "</b>";
                }
                var valueText = '<span style="color:' + point.series.color + '">';
                if (point.series.name && point.series.name != "default") {
                    text += "<br/>";
                    if (point.series.options.title)
                        valueText += point.series.options.title;
                    else
                        valueText += point.series.name;
                    valueText += ": ";
                }
                valueText += yAxis.formatData(point.y);
                valueText += '</span>';
                text += valueText;
            }
            return text;
        }
    },

    xAxis: {
        categories: [],
        labels: {
            rotation: -45,
            align: 'right'
        }
    },

    plotOptions: {
        column: {
            animation: false,
            cursor: 'pointer',
            dataLabels: {
                enabled: true,
                formatter: function () {
                    return this.series.yAxis.formatValue(this.y);
                },
                overflow: "justify",
                rotation: -90,
                align: "left",
                x: 4
            },
            events: DefaultCategoryPlotOptionsEvents
        }
    },

    initialize: function () {
        if (this.series.length > 1) {
            this.legend.enabled = true;
            this.plotOptions.column.dataLabels.enabled = false;
        }
        else {
            this.legend.enabled = false;
            this.plotOptions.column.dataLabels.enabled = true;
        }
        if (this.stacked)
            this.plotOptions.series = { stacking: "normal" };
        else {
            if (this.plotOptions.series && this.plotOptions.series.stacking == "normal")
                delete this.plotOptions.series;
        }

        if (this.chart.inverted) {
            delete this.xAxis.labels.rotation;
            delete this.plotOptions.column.dataLabels.rotation;
            delete this.plotOptions.column.dataLabels.x;
            delete this.plotOptions.column.dataLabels.y;
        }
    }
};

var DefaultTimeChartProperties = {
    chart: {
        zoomType: 'x',
        type: 'spline'
    },

    tooltip: {
        crosshairs: true,
        shared: true,
        formatter: function () {
            var points = this.points;
            if (points == undefined || points == null)
                points = [this];
            var text;
            for (var i = 0; i < points.length; i++) {
                var point = points[i];
                if (point.y == undefined || point.y == null || point.y == 0)
                    continue;
                var yAxis = point.series.yAxis;
                if (i == 0) {
                    text = "<b>" + point.series.xAxis.formatTime(point.x, point.series.xAxis.lablesPattern) + ": ";
                    if (point.total != undefined && point.total != null)
                        text += yAxis.formatData(point.total);
                    text += "</b>";
                }
                text += "<br/>";
                text += '<span style="color:' + point.series.color + '">';
                if (point.series.name && point.series.name != "default") {
                    if (point.series.options.title)
                        text += point.series.options.title;
                    else
                        text += point.series.name;
                    text += ": ";
                }
                text += yAxis.formatData(point.y);
                text += '</span>';
            }
            return text;
        }
    },

    xAxis: {
        type: 'datetime',
        labels: {
            formatter: function () {
                return this.axis.formatTime(this.value);
            }
        }
    },

    plotOptions: {
        area: {
            stacking: "normal",
            trackByArea: true,
            fillOpacity: 0.5
        },
        areaspline: {
            stacking: "normal",
            trackByArea: true,
            fillOpacity: 0.5
        },
        series: {
            animation: false,
            cursor: 'pointer',
            lineWidth: 1.5,
            marker: {
                enabled: false,
                radius: 1.5
            },
            events: DefaultTimePlotOptionsEvents
        }
    },

    reRender: function (width, height) {
        this.clearHighChart();
        this.chart.width = width;
        this.chart.height = height;
        if (this.xAxis) {
            var dataModule = this.getDataModule();
            if (this.xAxis.min && dataModule.getBeginTime)
                this.xAxis.min = dataModule.getBeginTime();
            if (this.xAxis.max && dataModule.getEndTime)
                this.xAxis.max = dataModule.getEndTime();
        }
        this.highChart = new Highcharts.Chart(this);
    }
};

var HighChartsFactory = {
    createHighChart: function (id, properties, data) {
        var functionName = "create" + properties.type + "Chart";
        if (this[functionName])
            return this[functionName](id, properties, data);
    },

    prepareChartOptions: function (chartOptions, properties) {
        if (!$.defined(chartOptions.chart))
            chartOptions.chart = {}

        if ($.defined(chartOptions.id)) {
            chartOptions.chart.renderTo = chartOptions.id;
            delete chartOptions.id;
        }

        if ($.defined(chartOptions.inverted)) {
            chartOptions.chart.inverted = chartOptions.inverted;
            delete chartOptions.inverted;
        }

        if ($.defined(properties.exportEnabled)) {
            chartOptions.exporting.enabled = properties.exportEnabled;
            delete chartOptions.exportEnabled;
        }

        if ($.defined(chartOptions.width)) {
            chartOptions.chart.width = chartOptions.width;
            delete chartOptions.width;
        }

        if ($.defined(chartOptions.height)) {
            chartOptions.chart.height = chartOptions.height;
            delete chartOptions.height;
        }

        if ($.defined(chartOptions.showLegend)) {
            chartOptions.legend.enabled = chartOptions.showLegend;
            delete chartOptions.showLegend;
        }

        if ($.defined(chartOptions.title) && $.isString(chartOptions.title))
            chartOptions.title = { text: chartOptions.title };

        if ($.defined(chartOptions.subtitle) && $.isString(chartOptions.subtitle))
            chartOptions.subtitle = { text: chartOptions.subtitle };

        this.prepareChartLegendOptions(chartOptions);
    },

    prepareChartLegendOptions: function (chartOptions) {
        if ($.defined(chartOptions.legendAlign) && $.isString(chartOptions.legendAlign)) {
            if (!$.defined(chartOptions.legend))
                chartOptions.legend = {};
            var legend = chartOptions.legend;
            if (chartOptions.legendAlign == "left") {
                $.extendInstance(legend, {
                    align: "left",
                    layout: "vertical",
                    verticalAlign: "middle"
                });
            } else if (chartOptions.legendAlign == "right") {
                $.extendInstance(legend, {
                    align: "right",
                    layout: "vertical",
                    verticalAlign: "middle"
                });
            } else if (chartOptions.legendAlign == "top") {
                $.extendInstance(legend, {
                    align: "center",
                    layout: "horizontal",
                    verticalAlign: "middle"
                });
            } else if (chartOptions.legendAlign == "bottom") {
                $.extendInstance(legend, {
                    align: "center",
                    layout: "horizontal",
                    verticalAlign: "middle"
                });
            }
            delete chartOptions.legendAlign;
        }
    },

    createChart: function (id, dataModuleType, chartProperties, data, properties) {
        if (!$.isString(id))
            return;

        var chartOptions = $.extendObject({
            chart: {
                renderTo: id
            },
            dataModuleType: dataModuleType
        }, DefaultChartProperties, chartProperties);

        this.prepareChartOptions(chartOptions, properties);
        chartOptions.chartOptions = properties;
        chartOptions.load(data);
        return chartOptions;
    },

    createPieChart: function (id, properties, data) {
        var chartProperties = $.extendObject(DefaultPieChartProperties, properties);
        if ($.defined(chartProperties.showDataLabels)) {
            chartProperties.plotOptions.pie.dataLabels.enabled = chartProperties.showDataLabels;
            delete chartProperties.showDataLabels;
        }
        return this.createChart(id, "Category", chartProperties, data, properties);
    },

    createBarChart: function (id, properties, data) {
        var chartProperties = $.extendObject(DefaultAxisChartProperties, DefaultBarChartProperties, properties);
        return this.createChart(id, "Category", chartProperties, data, properties);
    },

    createLineChart: function (id, properties, data) {
        var chartProperties = $.extendObject(DefaultAxisChartProperties, DefaultTimeChartProperties, properties);
        return this.createChart(id, "Time", chartProperties, data, properties);
    },

    createAreaSplineChart: function (id, properties, data) {
        var chartProperties = $.extendObject(DefaultAxisChartProperties, DefaultTimeChartProperties, properties);
        chartProperties.stacked = true;
        chartProperties.nullValue = true;
        chartProperties.chart.type = 'areaspline';
        return this.createChart(id, "Time", chartProperties, data, properties);
    },

    createAreaChart: function (id, properties, data) {
        var chartProperties = $.extendObject(DefaultAxisChartProperties, DefaultTimeChartProperties, properties);
        chartProperties.stacked = true;
        chartProperties.nullValue = true;
        chartProperties.chart.type = 'area';
        return this.createChart(id, "Time", chartProperties, data, properties);
    }
};
