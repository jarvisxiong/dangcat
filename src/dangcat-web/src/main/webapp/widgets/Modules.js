/*
 *  UserInfo Dialog
 */
isc.ClassFactory.defineClass("UserWindow", "DialogWindow");
isc.UserWindow.addProperties({
    width: 320,
    height: 180,
    buttons: [
        isc.ToolButton.ButtonTypes.ModifyPassword,
        isc.ToolButton.ButtonTypes.Exit
    ],

    DefaultServiceProperties: {
        toolBar_modifyPassword_ItemClick: function (toolBar, item) {
            isc.ChangePasswordWindow.open(item, {
                no: isc.ApplicationContext.servicePrincipal.no,
                jndiName: "Staff/OperatorInfo"
            });
            return true;
        }
    }
});

isc.UserWindow.addMethods({
    initialize: function () {
        this.Super("initialize", arguments);
        if (isc.ApplicationContext.servicePrincipal.type != "OperatorSecurity")
            this.toolBar.modifyPassword.hide();
    },

    getLayoutProperties: function () {
        return [
            {
                name: "viewForm",
                classType: isc.DataForm,
                properties: {
                    fields: [
                        {name: "no", title: isc.i18nLogin.LoginNo},
                        {name: "name", title: isc.i18nLogin.Name},
                        {name: "roleName", title: isc.i18nLogin.RoleName},
                        {name: "loginTime", title: isc.i18nLogin.LoginTime, length: 19, formatValue: function (value, record, form, item) {
                            return $.isDate(value) ? value.format("yyyy-MM-dd HH:mm:ss") : value;
                        }},
                        {name: "clientIp", title: isc.i18nLogin.ClientIp}
                    ],
                    values: isc.ApplicationContext.servicePrincipal
                }
            }
        ];
    }
});

/*
 * QueryToolBar
 */
isc.ClassFactory.defineClass("QueryToolBar", "ToolStripBar");
isc.QueryToolBar.addProperties({
    DefaultDataProperties: [
        {
            name: "first"
        },
        {
            name: "prior"
        },
        {
            name: "next"
        },
        {
            name: "last"
        },
        "separator",
        {
            name: "refresh"
        },
        {
            name: "view",
            permission: "view",
            selected: false,
            actionType: "checkbox"
        },
        {
            name: "add",
            permission: "create"
        },
        {
            name: "detail",
            permission: "view"
        },
        {
            name: "remove",
            permission: "delete"
        },
        {
            name: "filter"
        },
        {
            name: "exportdoc",
            type: "Menu",
            permission: "export",
            data: [
                {
                    title: "XML",
                    name: "toXml"
                },
                {
                    title: "CSV",
                    name: "toCSV"
                },
                {
                    title: "Excel",
                    name: "toExcel"
                },
                {
                    title: "PDF",
                    name: "toPDF"
                },
                {
                    title: "Word",
                    name: "toWord"
                },
                {
                    title: "Plain text",
                    name: "toText"
                }
            ]
        },
        {
            name: "functions",
            type: "Menu"
        },
        "separator",
        {
            name: "exit",
            index: 0
        }
    ]
});

isc.QueryToolBar.addMethods({
    initialize: function () {
        this.Super("initialize", arguments);
        var bindTable = this.getBindTable();
        bindTable.setMode(isc.Table.ModeTypes.Query);
        bindTable.bindListener(this);
    },

    getBindTable: function () {
        return this.service.dataSet.getNavigate();
    },

    table_AfterLoad: function (table) {
        this.updateStatus();
    },

    table_DataStateChanged: function (table) {
        this.updateStatus();
    },

    itemClick: function (item) {
        var bindTable = this.getBindTable();
        switch (item.name) {
            case "exit":
                this.service.close();
                break;
            case "first":
                bindTable.firstPage();
                break;
            case "prior":
                bindTable.priorPage();
                break;
            case "next":
                bindTable.nextPage();
                break;
            case "last":
                bindTable.lastPage();
                break;
            case "refresh":
                bindTable.dataSet.load();
                break;
            case "remove":
                this.service.remove(bindTable);
                break;
            default :
                this.Super("itemClick", arguments);
        }
    },

    updateStatus: function () {
        var disable = [];
        var enable = [];
        var bindTable = this.getBindTable();
        if (bindTable.totalSize == 0) {
            disable[disable.length] = "first";
            disable[disable.length] = "prior";
            disable[disable.length] = "next";
            disable[disable.length] = "last";
            disable[disable.length] = "view";
            disable[disable.length] = "exportdoc";
        }
        else {
            if (bindTable.pageNum > 1) {
                enable[enable.length] = "first";
                enable[enable.length] = "prior";
            }
            else {
                disable[disable.length] = "first";
                disable[disable.length] = "prior";
            }
            if (bindTable.pageNum < bindTable.totalPageCount) {
                enable[enable.length] = "next";
                enable[enable.length] = "last";
            }
            else {
                disable[disable.length] = "next";
                disable[disable.length] = "last";
            }
            enable[enable.length] = "view";
            enable[enable.length] = "exportdoc";
        }
        if (bindTable.getCurrentRow() != null) {
            enable[enable.length] = "detail";
            enable[enable.length] = "remove";
        }
        else {
            disable[disable.length] = "detail";
            disable[disable.length] = "remove";
        }
        this.setButtonState(disable, enable);
    }
});

/*
 * EditToolBar
 */
isc.ClassFactory.defineClass("EditToolBar", "ToolStripBar");
isc.EditToolBar.addProperties({
    DefaultDataProperties: [
        {
            name: "first",
            title: isc.i18nButton.firstRow
        },
        {
            name: "prior",
            title: isc.i18nButton.priorRow
        },
        {
            name: "next",
            title: isc.i18nButton.nextRow
        },
        {
            name: "last",
            title: isc.i18nButton.lastRow
        },
        "separator",
        {
            name: "refresh"
        },
        {
            name: "add",
            permission: "create"
        },
        {
            name: "save",
            permission: ["save", "create"]
        },
        {
            name: "remove",
            permission: "delete"
        },
        {
            name: "exportdoc",
            permission: "export",
            type: "Menu",
            data: [
                {
                    title: "XML",
                    name: "toXml"
                },
                {
                    title: "CSV",
                    name: "toCSV"
                },
                {
                    title: "Excel",
                    name: "toExcel"
                },
                {
                    title: "PDF",
                    name: "toPDF"
                },
                {
                    title: "Word",
                    name: "toWord"
                },
                {
                    title: "Plain text",
                    name: "toText"
                }
            ]
        },
        {
            name: "functions",
            type: "Menu"
        },
        "separator",
        {
            name: "exit",
            index: 0
        }
    ]
});

isc.EditToolBar.addMethods({
    initialize: function () {
        this.Super("initialize", arguments);
        var bindTable = this.getBindTable();
        bindTable.setMode(isc.Table.ModeTypes.MasterEdit);
        bindTable.bindListener(this);
    },

    getBindTable: function () {
        return this.service.dataSet.getNavigate();
    },

    table_AfterLoad: function (table) {
        this.updateStatus();
    },

    table_DataStateChanged: function (table) {
        this.updateStatus();
    },

    exitClick: function () {
        var result = true;
        var service = this.service;
        if (this.getBindTable().isChanging()) {
            isc.ask(isc.i18nAsk.promptDataState, function (value) {
                    if (value)
                        service.close();
                }
            );
            result = false;
        }
        return result;
    },

    itemClick: function (item) {
        var bindTable = this.getBindTable();
        switch (item.name) {
            case "exit":
                if (this.exitClick())
                    this.service.close();
                break;
            case "first":
                bindTable.firstPage();
                break;
            case "prior":
                bindTable.priorPage();
                break;
            case "next":
                bindTable.nextPage();
                break;
            case "last":
                bindTable.lastPage();
                break;
            case "refresh":
                bindTable.dataSet.load();
                break;
            case "save":
                bindTable.save();
                break;
            case "add":
                bindTable.createNewRow();
                bindTable.startRow = bindTable.totalSize + 1;
                break;
            case "remove":
                this.service.remove(bindTable);
                break;
            default:
                this.Super("itemClick", arguments);
        }
    },

    setBrowseStatus: function (disable, enable) {
        var bindTable = this.getBindTable();
        if (bindTable.totalSize == 0) {
            disable[disable.length] = "first";
            disable[disable.length] = "prior";
            disable[disable.length] = "next";
            disable[disable.length] = "last";
            disable[disable.length] = "remove";
            disable[disable.length] = "exportdoc";
        }
        else {
            if (bindTable.startRow > 1) {
                enable[enable.length] = "first";
                enable[enable.length] = "prior";
            }
            else {
                disable[disable.length] = "first";
                disable[disable.length] = "prior";
            }
            if (bindTable.startRow < bindTable.totalSize) {
                enable[enable.length] = "next";
                enable[enable.length] = "last";
            }
            else {
                disable[disable.length] = "next";
                disable[disable.length] = "last";
            }
            enable[enable.length] = "remove";
            enable[enable.length] = "exportdoc";
        }
        disable[disable.length] = "save";
        enable[enable.length] = "add";
    },

    setModifyStatus: function (disable, enable) {
        var bindTable = this.getBindTable();
        disable[disable.length] = "first";
        disable[disable.length] = "prior";
        disable[disable.length] = "next";
        disable[disable.length] = "last";
        disable[disable.length] = "remove";
        disable[disable.length] = "exportdoc";
        disable[disable.length] = "remove";
        if (bindTable.isChanging()) {
            enable[enable.length] = "save";
            disable[disable.length] = "add";
        }
        else {
            disable[disable.length] = "save";
            enable[enable.length] = "add";
        }
    },

    updateStatus: function () {
        var disable = [];
        var enable = [];
        var bindTable = this.getBindTable();
        if (bindTable.dataState == isc.Table.DataStates.Browse || bindTable.isReadOnly())
            this.setBrowseStatus(disable, enable);
        else
            this.setModifyStatus(disable, enable);
        this.setButtonState(disable, enable);
    }
});

/*
 * QueryStatusBar
 */
isc.ClassFactory.defineClass("QueryStatusBar", "StatusBar");
isc.QueryStatusBar.addProperties({
    data: [
        {
            name: "pageSize",
            type: "form",
            width: 140,
            fields: [
                {
                    type: "select",
                    name: "pageSize",
                    width: 60,
                    valueMap: pageSizeValueMap,
                    changed: function (form, item, value) {
                        form.table.setPageSize(value);
                    }
                }
            ]
        },
        {
            name: "pageCount",
            type: "form",
            width: 150,
            fields: [
                {
                    type: "StaticText",
                    name: "pageCount",
                    width: 80
                }
            ]
        },
        {
            name: "totalSize",
            type: "form",
            width: 140,
            fields: [
                {
                    type: "StaticText",
                    name: "totalSize",
                    width: 80
                }
            ]
        },
        {

            name: "content",
            width: "*"
        },
        {
            name: "pageNum",
            type: "form",
            width: 120,
            fields: [
                {
                    editorType: "Text",
                    mask: "##########",
                    maskPromptChar: "",
                    name: "pageNum",
                    width: 60,
                    textAlign: "right",
                    blur: function (form, item) {
                        form.table.setPageNum(item._value);
                    }
                }
            ]
        },
        {
            name: "totalPageCount",
            type: "form",
            width: 120,
            fields: [
                {
                    type: "StaticText",
                    name: "totalPageCount",
                    width: 60
                }
            ]
        }
    ]
});

isc.QueryStatusBar.addMethods({
    initialize: function () {
        this.Super("initialize", arguments);

        var bindTable = this.getBindTable();
        bindTable.bindListener(this);
        this.pageSize.table = bindTable;
        this.pageNum.table = bindTable;
    },

    getBindTable: function () {
        return this.service.dataSet.getNavigate();
    },

    table_AfterLoad: function (table) {
        this.updateStatus();
    },

    table_DataStateChanged: function (table) {
        this.updateStatus();
    },

    updateStatus: function () {
        var bindTable = this.getBindTable();
        var fieldNames = [ "pageSize", "pageNum", "totalSize", "totalPageCount" ];
        for (var i = 0; i < fieldNames.length; i++) {
            var fieldName = fieldNames[i];
            this[fieldName].setValue(fieldName, bindTable[fieldName]);
        }
        this["pageCount"].setValue("pageCount", bindTable.getPageCount());
        if (bindTable.totalSize == 0) {
            this["pageSize"].disable();
            this["pageNum"].disable();
        }
        else {
            this["pageSize"].enable();
            this["pageNum"].enable();
        }
    }
});

/*
 * DataStatusBar
 */
isc.ClassFactory.defineClass("DataStatusBar", "StatusBar");
isc.DataStatusBar.addProperties({
    data: [
        {
            name: "content",
            width: "*"
        },
        {
            name: "dataState",
            width: 80
        }
    ]
});

isc.DataStatusBar.addMethods({
    initWidget: function () {
        this.loadingMessage = "${loadingImage}&nbsp;" + isc.i18nStatus[isc.Table.DataStates.Loading];
        this.Super("initWidget", arguments);
    },

    initialize: function () {
        this.Super("initialize", arguments);
        this.service.dataSet.bindListeners(this);
    },

    getBindTable: function () {
        return this.service.dataSet.getNavigate();
    },

    table_CurrentChanged: function (table, column, row) {
        var error = row.getFieldErrors(column.name);
        if (error)
            this.setError(error);
        else {
            if (column.hint)
                this.setHint(column.hint);
            else if (column.title)
                this.setHint(column.title);
            else this.setHint("");
        }
    },

    table_AfterLoad: function (table) {
        this.updateStatus();
    },

    table_DataStateChanged: function (table) {
        this.updateStatus();
    },

    setHint: function (value) {
        if (value == undefined || value == null)
            value = "&nbps;";
        this.content.setContents(value);
    },

    setError: function (value) {
        if (value == undefined || value == null)
            value = "&nbps;";
        this.content.setContents("<font color=red>" + value + "</font>");
    },

    getLoadingMessage: function () {
        return this.loadingMessage == null ? "&nbsp;" : this.loadingMessage.evalDynamicString(this, {
            loadingImage: this.imgHTML(isc.Canvas.loadingImageSrc,
                isc.Canvas.loadingImageSize,
                isc.Canvas.loadingImageSize)
        });
    },

    updateStatus: function () {
        var bindTable = this.getBindTable();
        // 数据状态
        if (bindTable.dataState == isc.Table.DataStates.Loading)
            this.dataState.setContents(this.getLoadingMessage());
        else if (bindTable.isReadOnly())
            this.dataState.setContents(isc.i18nStatus["readOnly"]);
        else {
            var value = isc.i18nStatus[bindTable.dataState];
            if (value == isc.i18nStatus.modify || value == isc.i18nStatus.insert)
                this.dataState.setContents("<font color=red>" + value + "</font>");
            else if (value == isc.i18nStatus.browse)
                this.dataState.setContents("<font color=blue>" + value + "</font>");
            else
                this.dataState.setContents(value);
        }
    }
});

/*
 * EditStatusBar
 */
isc.ClassFactory.defineClass("EditStatusBar", "DataStatusBar");
isc.EditStatusBar.addProperties({
    data: [
        {
            name: "totalSize",
            type: "form",
            width: 140,
            fields: [
                {
                    type: "StaticText",
                    name: "totalSize",
                    width: 80
                }
            ]
        },
        {
            name: "startRow",
            type: "form",
            width: 140,
            fields: [
                {
                    type: "StaticText",
                    name: "startRow",
                    width: 80
                }
            ]
        },
        {
            name: "content",
            width: "*"
        },
        {
            name: "dataState",
            width: 60
        }
    ]
});

isc.EditStatusBar.addMethods({
    initialize: function () {
        this.Super("initialize", arguments);

        var bindTable = this.getBindTable();
        this.totalSize.table = bindTable;
        this.startRow.table = bindTable;
    },

    updateStatus: function () {
        this.Super("updateStatus", arguments);

        var bindTable = this.getBindTable();
        var fields = [ "totalSize", "startRow" ];
        for (var i = 0; i < fields.length; i++) {
            var field = fields[i];
            this[field].setValue(field, bindTable[field]);
        }
        this.Super("updateStatus", arguments);
    }
});

/*
 * HomeForm
 */
isc.ClassFactory.defineClass("HomeForm", "Layout");
isc.HomeForm.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    columnSize: 2
});

isc.HomeForm.addMethods({
    initWidget: function () {
        this.addChild(this.getBackgroundLayout());
        this.Super("initWidget", arguments);
    },

    getData: function () {
    },

    getBackgroundLayout: function () {
        this.gridPanel = isc.DataTileGrid.create({
            tileHeight: 350,
            data: this.getData(),
            columnSize: this.columnSize,
            tileConstructor: "PortletWindow",
            tileProperties: {
                autoSize: false,
                canDragReposition: false,
                canDragResize: false
            }
        });
        return this.gridPanel;
    }
});

/*
 * PortletWindow
 */
isc.defineClass("PortletWindow", "Window");
isc.PortletWindow.addProperties({
    autoDraw: false,
    autoSize: false,
    showShadow: false,
    headerIcon: "[SKIN]/button/view.png",
    animateMinimize: true,
    showFooter: false,
    timeStep: 5000
});

isc.PortletWindow.addMethods({
    initWidget: function () {
        this.headerIconDefaults.src = this.headerIcon;
        this.Super("initWidget", arguments);
    },

    setData: function (data) {
        this.data = data;
        this.setTitle(data.title);
        this.createChart();
    },

    loadData: function () {
        if (this.chart)
            this.chart.load(this.data[0].createData());
    },

    closeClick: function () {
        this.Super("closeClick", arguments);
        this.chart.destroy();
        delete this.chart;
    },

    createChart: function () {
        if (!$.defined(this.chart)) {
            this.chart = isc.HighChart.create({
                border: "none",
                chartOptions: this.data.chartOptions,
                data: this.data.chartData
            });
            this.addItem(this.chart);
            var portletWindow = this;
            window.setInterval(function () {
                portletWindow.loadData();
            }, this.timeStep);
        }
        return this.chart;
    }
});

/*
 * ChartWindow
 */
isc.defineClass("ChartWindow", "Window");
isc.ChartWindow.addProperties({
    autoDraw: false,
    autoSize: false,
    showShadow: false,
    headerIcon: "[SKIN]/button/view.png",
    showFooter: false,
    timeStep: 5000
});

isc.ChartWindow.addMethods({
    initWidget: function () {
        this.headerIconDefaults.src = this.headerIcon;
        this.Super("initWidget", arguments);
    },

    setData: function (data) {
        var record = $.isArray(data) ? data[0] : data;
        if (record != this.record) {
            if ($.defined(this.chart)) {
                this.removeItem(this.chart);
                this.chart.destroy();
                delete this.chart;
            }
            this.setTitle(record.title);
            this.record = record;
        }
        this.createChart();
    },

    getRecord: function () {
        return $.isArray(this.record) ? this.record[0] : this.record;
    },

    getChartOptions: function () {
        return this.service.owner.getChartOptions(this.getRecord());
    },

    loadData: function () {
        return this.service.owner.getChartData(this.getRecord());
    },

    createChart: function () {
        if (!$.defined(this.chart)) {
            var chartWindow = this;
            this.chart = isc.HighChart.create({
                border: "none",
                chartOptions: this.getChartOptions(),
                loadData: function () {
                    return chartWindow.loadData();
                },
                data: this.loadData()
            });
            this.addItem(this.chart);
            this.chart.click = function (e) {
                chartWindow.click(e);
            };
        }
        return this.chart;
    },

    destroy: function () {
        if ($.defined(this.service.owner.clearChart))
            this.service.owner.clearChart(this.chart);
        if ($.defined(this.chart))
            this.chart.destroy();
        this.Super("destroy", arguments);
    }
});
/*
 * QueryForm
 */
isc.ClassFactory.defineClass("QueryForm", "ModuleForm");
isc.QueryForm.addProperties({
    DefaultServiceProperties: {
        initialize: function () {
            this.Super("initialize", arguments);
            // 载入数据。
            this.dataSet.load();
        },

        dataGrid_RowDoubleClick: function (dataGrid, record, rowNum, colNum) {
            if (this.toolBar.detail)
                this.toolBar.detail.click();
            return false;
        },

        toolBar_view_ItemClick: function (toolBar, item) {
            if (item.selected)
                this.mainLayout.showMember(this.dataFormLayout);
            else
                this.mainLayout.hideMember(this.dataFormLayout);
            return true;
        },

        toolBar_config_ItemClick: function (toolBar, item) {
            var config = this.owner.config;
            config.table.hiddenColumnNames = ["rowNum"];
            var jndiName = this.module.jndiName;
            isc.EditWindow.open(item, {
                table: config.table,
                data: config.data,
                success: function () {
                    isc.PRCService.invoke(jndiName, "config", this.service.resultData[0], function (responseData) {
                        config.data = responseData;
                    });
                }
            });
            return true;
        },

        toolBar_filter_ItemClick: function (toolBar, item) {
            var properties = isc.addProperties({
                bindTable: toolBar.getBindTable()
            }, this.owner.dataFilterProperties);
            isc.DataFilterWindow.open(item, properties);
            return true;
        },

        toolBar_detail_ItemClick: function (toolBar, item) {
            var bindTable = toolBar.getBindTable();
            isc.ModuleWindow.open(this, item.url, item.title, {
                dataFilter: bindTable.filter,
                currentPosition: bindTable.getCurrentPosition(),
                dataState: isc.Table.DataStates.Browse,
                success: function () {
                    bindTable.load();
                }
            }, {
                icon: item.icon
            });
            return true;
        },

        toolBar_add_ItemClick: function (toolBar, item) {
            var bindTable = toolBar.getBindTable();
            isc.ModuleWindow.open(this, item.url, item.title, {
                dataFilter: bindTable.filter,
                currentPosition: bindTable.totalSize + 1,
                totalSize: bindTable.totalSize,
                dataState: isc.Table.DataStates.Insert,
                success: function () {
                    bindTable.load();
                }
            }, {
                icon: item.icon
            });
            return true;
        }
    },

    DefaultLayoutProperties: [
        {
            name: "toolBar",
            classType: isc.QueryToolBar
        },
        {
            classType: isc.HPanel,
            name: "mainLayout",
            properties: {
                resizeBarSize: 2
            },
            members: [
                {
                    classType: isc.VPanel,
                    properties: {
                        width: "70%",
                        showResizeBar: false
                    },
                    members: [
                        {
                            name: "dataGrid",
                            classType: isc.DataGrid
                        },
                        {
                            name: "queryStatusBar",
                            classType: isc.QueryStatusBar
                        }
                    ]
                },
                {
                    classType: isc.VPanel,
                    name: "dataFormLayout",
                    properties: {
                        width: "30%"
                    },
                    members: [
                        {
                            name: "dataForm",
                            classType: isc.DataForm
                        },
                        {
                            name: "dataStatusBar",
                            classType: isc.DataStatusBar
                        }
                    ]
                }
            ]
        }
    ]
});

isc.QueryForm.addMethods({
    initWidget: function () {
        this.initToolBar();
        this.Super("initWidget", arguments);
    },

    initComponent: function () {
        this.Super("initComponent", arguments);
        if (this.toolBar.view)
            this.toolBar.view.click();
    },

    initToolBar: function () {
        if (this.detailUrl) {
            var toolBarData = [
                {
                    name: "add",
                    url: this.detailUrl
                },
                {
                    name: "detail",
                    url: this.detailUrl
                }
            ];
            if (!this.toolBarProperties)
                this.toolBarProperties = {};
            if (!this.toolBarProperties.overrideProperties)
                this.toolBarProperties.overrideProperties = {};
            var overrideProperties = this.toolBarProperties.overrideProperties;
            if (!overrideProperties.data)
                overrideProperties.data = toolBarData;
            else
                overrideProperties.data.push(toolBarData);
        }
    },

    getDataFormProperties: function () {
        if (this.dataFormProperties)
            return {
                name: "dataForm",
                classType: isc.DataForm
            };
    },

    getDataGridProperties: function () {
        if (this.dataGridProperties)
            return {
                name: "dataGrid",
                classType: isc.DataGrid
            };
    },

    getDataGridLayoutMembers: function () {
        var members = [];
        var dataGridProperties = this.getDataGridProperties();
        if (dataGridProperties)
            members.push(dataGridProperties);
        members.push({
            name: "queryStatusBar",
            classType: isc.QueryStatusBar
        });
        return members;
    },

    getDataFormLayoutMembers: function () {
        var members = [];
        var dataFormProperties = this.getDataFormProperties();
        if (dataFormProperties)
            members.push(dataFormProperties);
        members.push({
            name: "dataStatusBar",
            classType: isc.DataStatusBar
        });
        return members;
    },

    getLayoutProperties: function () {
        var dataGridLayoutMembers = this.getDataGridLayoutMembers();
        var dataFormLayoutMembers = this.getDataFormLayoutMembers();
        return [
            {
                name: "toolBar",
                classType: isc.QueryToolBar
            },
            {
                classType: isc.HPanel,
                name: "mainLayout",
                properties: {
                    resizeBarSize: 2
                },
                members: [
                    {
                        classType: isc.VPanel,
                        properties: {
                            width: "70%",
                            showResizeBar: true,
                            resizeBarTarget: "next"
                        },
                        members: dataGridLayoutMembers
                    },
                    {
                        classType: isc.VPanel,
                        name: "dataFormLayout",
                        properties: {
                            width: "30%"
                        },
                        members: dataFormLayoutMembers
                    }
                ]
            }
        ];
    }
});

/*
 * EditForm
 */
isc.ClassFactory.defineClass("EditForm", "ModuleForm");
isc.EditForm.addClassProperties({
    DefaultServiceProperties: {
        table_AfterSave: function (table) {
            this.result = true;
        },

        table_AfterDelete: function (table) {
            this.result = true;
        }
    }
});
isc.EditForm.addMethods({
    initComponent: function () {
        this.Super("initComponent", arguments);

        var toolBar = this.service.toolBar;
        if ($.defined(this.window)) {
            this.window.onCloseClick = function () {
                return toolBar.exitClick();
            };
        }
        var bindTable = toolBar.getBindTable();
        bindTable.startRow = this.currentPosition;
        if (this.totalSize != undefined && this.totalSize != null)
            bindTable.totalSize = this.totalSize;
        bindTable.filter = this.dataFilter;
        if (this.dataState == isc.Table.DataStates.Insert)
            bindTable.createNewRow();
        else
            this.service.dataSet.load();
    },

    getContentMembers: function () {
        var members = [];
        if (this.contentMembers && this.contentMembers.length > 0)
            members.push(this.createTabs(this.contentMembers));
        else {
            var dataFormProperties = this.getDataFormProperties();
            if (dataFormProperties)
                members.push(dataFormProperties);
            if (this.detailMember)
                members.push(this.detailMember);
            else if (this.detailMembers && this.detailMembers.length > 0)
                members.push(this.createTabs(this.detailMembers));
            else {
                var dataGridProperties = this.getDataGridProperties();
                if (dataGridProperties)
                    members.push(dataGridProperties);
            }
        }
        return members;
    },

    createTabs: function (members) {
        var tabLayout = {
            name: "layoutTab",
            classType: isc.TabLayout,
            members: members
        };
        return tabLayout;
    },

    getDataFormProperties: function () {
        if (this.dataFormProperties)
            return {
                name: "dataForm",
                classType: isc.DataForm,
                properties: {
                    height: "*"
                }
            };
    },

    getDataGridProperties: function () {
        if (this.dataGridProperties)
            return {
                name: "dataGrid",
                classType: isc.DataGrid
            };
    },

    getLayoutProperties: function () {
        return [
            {
                name: "toolBar",
                classType: isc.EditToolBar
            },
            {
                classType: isc.VPanel,
                members: this.getContentMembers()
            },
            {
                name: "dataStatusBar",
                classType: isc.EditStatusBar
            }
        ];
    }
});

/*
 *  Data Filter Dialog
 */
isc.ClassFactory.defineClass("DataFilterWindow", "DialogWindow");
isc.DataFilterWindow.addProperties({
    width: 600,
    height: 400,
    buttons: [
        isc.ToolButton.ButtonTypes.Ok,
        isc.ToolButton.ButtonTypes.Reset,
        isc.ToolButton.ButtonTypes.Cancel
    ],

    DefaultServiceProperties: {
        initialize: function () {
            this.Super("initialize", arguments);
            this.initFilterData();
        },

        initFilterData: function () {
            var dataFilter = this.dataSet.dataFilter;
            dataFilter.setMode(isc.Table.ModeTypes.Temp);
            var defaultRow = dataFilter.createDefaultRow();
            var filter = isc.addProperties(defaultRow, this.owner.bindTable.filter);
            dataFilter.setData(filter);
        },

        toolBar_ok_ItemClick: function (toolBar, item) {
            this.close();
            var dataFilter = this.dataSet.dataFilter;
            var currentRow = dataFilter.getCurrentRow();
            this.owner.bindTable.setFilter(currentRow.saveData());
            return true;
        },

        toolBar_reset_ItemClick: function (toolBar, item) {
            this.dataSet.dataFilter.createNewRow();
            return true;
        }
    },

    DefaultLayoutProperties: [
        {
            name: "dataForm",
            classType: isc.DataForm,
            properties: {
                tableName: "dataFilter",
                hitRequiredFields: false
            }
        }
    ]
});

/*
 *  Data ChangePassword Dialog
 */
isc.ClassFactory.defineClass("ChangePasswordWindow", "DialogWindow");
isc.ChangePasswordWindow.addProperties({
    width: 600,
    height: 160,
    methodName: "changePassword",
    buttons: [
        isc.ToolButton.ButtonTypes.Ok,
        isc.ToolButton.ButtonTypes.Cancel
    ],

    DefaultServiceProperties: {
        initialize: function () {
            this.Super("initialize", arguments);
            var table = this.dataSet.password;
            table.setMode(isc.Table.ModeTypes.Temp);
            table.createNewRow();
            table.getCurrentRow().no = this.owner.no;
        },

        password_password2_OnFieldValidate: function (table, column, row, value) {
            if (value && row.password1 != value)
                return isc.i18nValidate.PasswordNotEquals;
        },

        toolBar_ok_ItemClick: function (toolBar, item) {
            var table = this.dataSet.password;
            if (table.validate()) {
                var data = table.saveData()[0];
                var params = {
                    orgPassword: data.password,
                    newPassword: data.password1
                };
                var service = this;
                table.invoke(this.owner.jndiName, this.owner.methodName, params, function (responseData) {
                    if (responseData && responseData.value == true) {
                        isc.say(isc.i18nLogin.ChangeSuccess);
                        service.close();
                    }
                });
            }
            return true;
        }
    },

    dataSetProperties: {
        password: {
            columns: {
                "no": {
                    "title": isc.i18nLogin.No,
                    "name": "no",
                    "length": 32,
                    "dataType": "string",
                    "readOnly": true
                },
                "password": {
                    "title": isc.i18nLogin.orgPassword,
                    "name": "password",
                    "length": 32,
                    "dataType": "string",
                    "password": "no",
                    "nullable": false
                },
                "password1": {
                    "title": isc.i18nLogin.password1,
                    "name": "password1",
                    "length": 32,
                    "dataType": "string",
                    "password": "no",
                    "nullable": false
                },
                "password2": {
                    "title": isc.i18nLogin.password2,
                    "name": "password2",
                    "length": 32,
                    "dataType": "string",
                    "password": "no",
                    "nullable": false
                }
            }
        }
    },

    DefaultLayoutProperties: [
        {
            name: "dataForm",
            classType: isc.DataForm,
            properties: {
                tableName: "password",
                colWidths1: ["20%", "80%"],
                hitRequiredFields: false
            }
        }
    ]
});

/*
 *  Data ResetPassword Dialog
 */
isc.ClassFactory.defineClass("ResetPasswordWindow", "ChangePasswordWindow");
isc.ResetPasswordWindow.addProperties({
    height: 143,
    methodName: "resetPassword",

    DefaultServiceProperties: {
        initialize: function () {
            this.Super("initialize", arguments);
            var table = this.dataSet.password;
            if (this.owner.encryptAlgorithm) {
                table.columns["password1"].encryptAlgorithm = this.owner.encryptAlgorithm;
                table.columns["password2"].encryptAlgorithm = this.owner.encryptAlgorithm;
            } else {
                delete table.columns["password1"].encryptAlgorithm;
                delete table.columns["password2"].encryptAlgorithm;
            }
            table.setMode(isc.Table.ModeTypes.Temp);
            table.createNewRow();
            table.getCurrentRow().no = this.owner.no;
        },

        password_password2_OnFieldValidate: function (table, column, row, value) {
            if (value && row.password1 != value)
                return isc.i18nValidate.PasswordNotEquals;
        },

        toolBar_ok_ItemClick: function (toolBar, item) {
            var table = this.dataSet.password;
            if (table.validate()) {
                var data = table.saveData()[0];
                var params = {
                    no: $.encryptContent(this.owner.no),
                    password: data.password1
                };
                var service = this;
                table.invoke(this.owner.jndiName, this.owner.methodName, params, function (responseData) {
                    if (responseData && responseData.value == true) {
                        isc.say(isc.i18nLogin.ChangeSuccess);
                        service.close();
                    }
                });
            }
            return true;
        }
    },

    dataSetProperties: {
        password: {
            columns: {
                "no": {
                    "title": isc.i18nLogin.No,
                    "name": "no",
                    "length": 32,
                    "dataType": "string",
                    "readOnly": true
                },
                "password1": {
                    "title": isc.i18nLogin.password1,
                    "name": "password1",
                    "length": 32,
                    "dataType": "string",
                    "password": "no",
                    "nullable": false
                },
                "password2": {
                    "title": isc.i18nLogin.password2,
                    "name": "password2",
                    "length": 32,
                    "dataType": "string",
                    "password": "no",
                    "nullable": false
                }
            }
        }
    }
});

/*
 *  View DataForm Dialog
 */
isc.ClassFactory.defineClass("ViewWindow", "DialogWindow");
isc.ViewWindow.addProperties({
    width: 800,
    height: 600,
    buttons: [
        isc.ToolButton.ButtonTypes.Exit
    ],

    DefaultServiceProperties: {
        initialize: function () {
            this.owner.dataSetProperties = {
                overrideProperties: {
                    viewTable: {
                        readOnly: true
                    }
                },
                viewTable: this.owner.table
            };
            this.Super("initialize", arguments);
        }
    },

    DefaultLayoutProperties: [
        {
            name: "dataForm",
            classType: isc.DataForm,
            properties: {
                tableName: "viewTable"
            }
        }
    ]
});

/*
 *  Edit DataForm Dialog
 */
isc.ClassFactory.defineClass("EditWindow", "DialogWindow");
isc.EditWindow.addProperties({
    width: 600,
    height: 300,
    buttons: [
        isc.ToolButton.ButtonTypes.Ok,
        isc.ToolButton.ButtonTypes.Exit
    ],

    DefaultServiceProperties: {
        initialize: function () {
            this.owner.dataSetProperties = {
                editTable: this.owner.table
            };
            this.Super("initialize", arguments);
            var data = isc.addProperties({}, this.owner.data);
            this.dataSet.editTable.setData(data);
            this.dataSet.editTable.afterLoad();
        },

        toolBar_ok_ItemClick: function (toolBar, item) {
            this.result = true;
            this.resultData = this.dataSet.editTable.saveData();
            this.close();
            return true;
        }
    },

    DefaultLayoutProperties: [
        {
            name: "dataForm",
            classType: isc.DataForm,
            properties: {
                tableName: "editTable"
            }
        }
    ]
});

/*
 * UploadWindow Dialog
 */
isc.ClassFactory.defineClass("UploadWindow", "DialogWindow");
isc.UploadWindow.addProperties({
    width: 600,
    height: 150,
    buttons: [isc.ToolButton.ButtonTypes.Upload, isc.ToolButton.ButtonTypes.Exit],
    DefaultServiceProperties: {
        initialize: function () {
            this.Super("initialize", arguments);
            this.dataForm.action = isc.PRCService.getBaseUrl(this.owner.action);
            this.dataForm.getItem("fileName").fileSuffix = this.owner.fileSuffix;
        },

        initComponent: function () {
            this.Super("initComponent", arguments);
            this.dataForm_ItemChanged();
        },

        toolBar_upload_ItemClick: function (toolBar, item) {
            this.result = true;
            var dataForm = this.dataForm;
            var fileName = dataForm.getValue("fileName");
            if (!fileName || fileName.isEmpty()) {
                isc.say(isc.i18nValidate.FileNameEmpty);
                return;
            }
            isc.ProcessWindow.open(item, {
                action: this.owner.action,
                onShown: function () {
                    var processWindow = this;
                    dataForm.ajaxSubmit(function (data) {
                        if (!processWindow.service.invokeStop)
                            isc.say(data.info);
                    });
                    processWindow.service.beginInvoke();
                }
            });
            return true;
        },

        dataForm_ItemChanged: function (form, item, newValue) {
            this.toolBar.upload.setDisabled(newValue ? newValue.isEmpty() : true);
        }
    },

    DefaultLayoutProperties: [
        {
            name: "dataForm",
            classType: isc.DataForm,
            properties: {
                fields: [
                    {name: "fileName", title: isc.i18nTitle.FileName, type: "upload"}
                ]
            }
        }
    ]
});

/*
 * ProcessWindow Dialog
 */
isc.ClassFactory.defineClass("ProcessWindow", "DialogWindow");
isc.ProcessWindow.addProperties({
    width: 500,
    height: 300,
    buttons: [isc.ToolButton.ButtonTypes.Stop, isc.ToolButton.ButtonTypes.Exit],
    DefaultServiceProperties: {
        initialize: function () {
            this.Super("initialize", arguments);
        },

        initComponent: function () {
            this.dataGrid.initFields(this.dataGrid.fields);
            this.Super("initComponent", arguments);
            this.toolBar.stop.setDisabled(true);
        },

        beginInvoke: function () {
            delete this.invokeStop;
            this.invoking = true;
            this.setInterval("queryInvokeProcess", 2000);
            this.toolBar.stop.setDisabled(false);
        },

        endInvoke: function () {
            this.toolBar.stop.setDisabled(true);
            delete this.invoking;
        },

        onClosing: function (success) {
            if (this.invoking) {
                var service = this;
                isc.ask(isc.i18nAsk.stop, function (value) {
                        if (value)
                            service.stopInvoke(success);
                    }
                );
            } else if (success)
                success();
        },

        toolBar_stop_ItemClick: function (toolBar, item) {
            this.stopInvoke();
            return true;
        },

        stopInvoke: function (success) {
            this.endInvoke();
            this.invokeStop = true;
            this.queryInvokeProcess(success);
        },

        queryInvokeProcess: function (success) {
            var service = this;
            var dataGrid = this.dataGrid;
            var params = { jndiName: this.owner.action };
            if (this.invokeStop == true)
                params.cancel = true;
            if (this.invoking) {
                isc.PRCService.ajaxInvoke("GET", "/invokeProcess", params, function (data) {
                    if (data) {
                        if (data.error) {
                            isc.warn(data.error);
                            service.endInvoke();
                        }
                        else if (isc.isA.Array(data)) {
                            dataGrid.setData(data);
                            var finishedAll = true;
                            for (var i = 0; i < data.length; i++) {
                                if (!data[i].finished) {
                                    finishedAll = false;
                                    break;
                                }
                            }
                            if (finishedAll)
                                service.endInvoke();
                        }
                        if (success)
                            success();
                    }
                });
            }
            return this.invoking;
        }
    },

    DefaultLayoutProperties: [
        {
            name: "dataGrid",
            classType: isc.DataGrid,
            properties: {
                fields: [
                    {name: "name", title: isc.i18nTitle.StepName, type: "text", width: 100},
                    {name: "process", title: isc.i18nTitle.Process, type: "float", logic: "percent"},
                    {name: "status", title: isc.i18nTitle.Status, type: "image", align: "center", width: 60, showValueIconOnly: true,
                        valueIcons: {
                            "1": "[SKIN]/../process/1.gif",
                            "2": "[SKIN]/../process/2.gif",
                            "3": "[SKIN]/../process/3.gif",
                            "4": "[SKIN]/../process/4.gif"
                        }
                    }
                ]
            }
        }
    ]
});