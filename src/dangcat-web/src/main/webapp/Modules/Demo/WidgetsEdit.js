isc.EditForm.create({
    serviceProperties: {
        toolBar_showDataFormError_ItemClick: function (toolBar, item) {
            var table = this.dataSet.accountInfo;
            var row = table.getCurrentRow();
            if (row != undefined) {
                for (var i = 0; i < table.columns.length; i++) {
                    var column = table.columns.getColumn(i);
                    row.addFieldErrors(column.name, column.name + " error message.");
                }
            }
            return true;
        },

        toolBar_clearDataFormError_ItemClick: function (toolBar, item) {
            var table = this.dataSet.accountInfo;
            var row = table.getCurrentRow();
            if (row != undefined)
                row.clearFieldErrors();
            return true;
        },

        toolBar_showDataFormOne_ItemClick: function (toolBar, item) {
            var table = this.dataSet.accountInfo;
            this.dataForm.setColumnCount(1);
            return true;
        },

        toolBar_showDataFormTwo_ItemClick: function (toolBar, item) {
            var table = this.dataSet.accountInfo;
            this.dataForm.setColumnCount(2);
            return true;
        },

        toolBar_showDataFormThree_ItemClick: function (toolBar, item) {
            this.dataForm.setColumnCount(3);
            return true;
        },

        toolBar_showDataFormFour_ItemClick: function (toolBar, item) {
            this.dataForm.setColumnCount(4);
            return true;
        },

        toolBar_showDataFormDiscription_ItemClick: function (toolBar, item) {
            var table = this.dataSet.accountInfo;
            var column = table.columns.getColumn("octets");
            if (column.isVisible() == false) {
                column.setVisible(true);
                item.setTitle("隐藏描述栏位");
            }
            else {
                column.setVisible(false);
                item.setTitle("显示描述栏位");
            }
            return true;
        },

        toolBar_readOnlyDataFormField_ItemClick: function (toolBar, item) {
            var table = this.dataSet.accountInfo;
            var octetsColumn = table.columns.getColumn("octets");
            var octetsVelocityColumn = table.columns.getColumn("octetsVelocity");
            var velocityColumn = table.columns.getColumn("velocity");
            var valueColumn = table.columns.getColumn("value");
            var rateColumn = table.columns.getColumn("rate");
            var timeLengthColumn = table.columns.getColumn("timeLength");
            var useAbleColumn = table.columns.getColumn("useAble");
            if (octetsColumn.isReadOnly() == false) {
                octetsColumn.setReadOnly(true);
                octetsVelocityColumn.setReadOnly(true);
                velocityColumn.setReadOnly(true);
                valueColumn.setReadOnly(true);
                rateColumn.setReadOnly(true);
                timeLengthColumn.setReadOnly(true);
                useAbleColumn.setReadOnly(true);
                item.setTitle("栏位可修改");
            }
            else {
                octetsColumn.setReadOnly(false);
                octetsVelocityColumn.setReadOnly(false);
                velocityColumn.setReadOnly(false);
                valueColumn.setReadOnly(false);
                rateColumn.setReadOnly(false);
                timeLengthColumn.setReadOnly(false);
                useAbleColumn.setReadOnly(false);
                item.setTitle("栏位只读");
            }
            return true;
        },

        toolBar_focusDataFormName_ItemClick: function (toolBar, item) {
            var table = this.dataSet.accountInfo;
            table.setCurrent("name");
            return true;
        },

        toolBar_showDataGridError_ItemClick: function (toolBar, item) {
            var table = this.dataSet.billInfo;
            for (var k = 0; k < table.getPageCount(); k++) {
                var row = table.getCurrentRow(k);
                if (row != undefined) {
                    for (var i = 0; i < table.columns.length; i++) {
                        var column = table.columns.getColumn(i);
                        row.addFieldErrors(column.name, k + "." + column.name + " error message.");
                    }
                }
            }
            return true;
        },

        toolBar_showDataGridError2_ItemClick: function (toolBar, item) {
            var table = this.dataSet.billInfo;
            var row = table.getCurrentRow(1);
            if (row != undefined) {
                for (var i = 2; i < table.columns.length - 1; i++) {
                    var column = table.columns.getColumn(i);
                    row.addFieldErrors(column.name, i + "." + column.name + " error message.");
                }
            }
            return true;
        },

        toolBar_clearDataGridError_ItemClick: function (toolBar, item) {
            var table = this.dataSet.billInfo;
            for (var k = 0; k < table.getPageCount(); k++) {
                var row = table.getCurrentRow(k);
                if (row != undefined)
                    row.clearFieldErrors();
            }
            return true;
        },

        toolBar_showDataGridCountry_ItemClick: function (toolBar, item) {
            var table = this.dataSet.billInfo;
            var column = table.columns.getColumn("octets");
            if (column.isVisible() == false) {
                column.setVisible(true);
                item.setTitle("隐藏国家栏位");
            }
            else {
                column.setVisible(false);
                item.setTitle("显示国家栏位");
            }
            return true;
        },

        toolBar_readOnlyDataGridField_ItemClick: function (toolBar, item) {
            var table = this.dataSet.billInfo;
            var octetsColumn = table.columns.getColumn("octets");
            var octetsVelocityColumn = table.columns.getColumn("octetsVelocity");
            var velocityColumn = table.columns.getColumn("velocity");
            var rateColumn = table.columns.getColumn("rate");
            var timeLengthColumn = table.columns.getColumn("timeLength");
            if (octetsColumn.isReadOnly() == false) {
                octetsColumn.setReadOnly(true);
                octetsVelocityColumn.setReadOnly(true);
                velocityColumn.setReadOnly(true);
                rateColumn.setReadOnly(true);
                timeLengthColumn.setReadOnly(true);
                item.setTitle("栏位可修改");
            }
            else {
                octetsColumn.setReadOnly(false);
                octetsVelocityColumn.setReadOnly(false);
                velocityColumn.setReadOnly(false);
                rateColumn.setReadOnly(false);
                timeLengthColumn.setReadOnly(false);
                item.setTitle("栏位只读");
            }
            return true;
        },
		
        accountInfo_octets_AfterUpdate : function(table, column, row, value) {
			row.addFieldErrors("octets", "octets field error");
			row.update("value", value * 2);
            return true;
        },
		
        accountInfo_octetsVelocity_AfterUpdate : function(table, column, row, value) {
			row.addFieldErrors("octetsVelocity", "octetsVelocity field error");
			row.update("rate", 70);
			row.update("useAble", !row["useAble"]);
            return true;
        },
		
        billInfo_octets_AfterUpdate : function(table, column, row, value) {
			row.addFieldErrors("octets", "octets field error");
			row.update("value", value * 10);
            return true;
        },
		
        billInfo_octetsVelocity_AfterUpdate : function(table, column, row, value) {
			row.addFieldErrors("octetsVelocity", "octetsVelocity field error");
			row.update("rate", 70);
			row.update("useAble", !row["useAble"]);
            return true;
        }
    },

    detailMembers: [
        {
            name: "dataGrid",
            classType: isc.DataGrid,
            properties: {
                title: "明细表"
            }
        }
    ],

    toolBarProperties: {
        functionsMenuData: [
            {
                name: "dataForm",
                title: "Form",
                submenu: [
                    {
                        title: "显示错误",
                        name: "showDataFormError"
                    },
                    {
                        title: "清除错误",
                        name: "clearDataFormError"
                    },
                    {
                        title: "分栏显示",
                        submenu: [
                            {
                                title: "显示一栏",
                                name: "showDataFormOne"
                            },
                            {
                                title: "显示二栏",
                                name: "showDataFormTwo"
                            },
                            {
                                title: "显示三栏",
                                name: "showDataFormThree"
                            },
                            {
                                title: "显示四栏",
                                name: "showDataFormFour"
                            }
                        ]
                    },
                    {
                        title: "隐藏描述栏位",
                        name: "showDataFormDiscription"
                    },
                    {
                        title: "栏位只读",
                        name: "readOnlyDataFormField"
                    },
                    {
                        title: "焦点落在名称上",
                        name: "focusDataFormName"
                    }
                ]
            },
            {
                name: "dataGrid",
                title: "Grid",
                submenu: [
                    {
                        title: "显示错误",
                        name: "showDataGridError"
                    },
                    {
                        title: "清除错误",
                        name: "clearDataGridError"
                    },
                    {
                        title: "隐藏国家栏位",
                        name: "showDataGridCountry"
                    },
                    {
                        title: "栏位只读",
                        name: "readOnlyDataGridField"
                    },
                    {
                        title: "第二笔错误",
                        name: "showDataGridError2"
                    }
                ]
            }
        ]
    },

    dataFilterProperties: {
        dataSetProperties: {
            dataFilter: {
                columns: {
                    name: {
                        title: "账户名称",
                        name: "name",
                        length: 40,
                        dataType: "string",
                        nullable: false
                    }
                }
            }
        }
    },

    dataFormProperties: {
        tableName: "accountInfo",
        columnCount: 2
    },

    dataGridProperties: {
        tableName: "billInfo"
    },

    dataSetProperties: {
        accountInfo: {
            dataURL: "[isomorphic]/../share/data/widgets.data.json",
            childrenNames: ["billInfo"],
            columns: {
                "id": {
                    "visible": false,
                    "primaryKey": true,
                    "name": "id",
                    "dataType": "integer"
                },
                "name": {
                    "title": "账户名称",
                    "name": "name",
                    "length": 40,
                    "dataType": "string",
                    "nullable": false
                },
                "octets": {
                    "title": "流量",
                    "name": "octets",
                    "logic" : "octets",
                    "dataType": "integer"
                },
                "octetsVelocity": {
                    "title": "带宽",
                    "name": "octetsVelocity",
                    "logic" : "octetsVelocity",
                    "dataType": "integer"
                },
                "velocity": {
                    "title": "速度",
                    "name": "velocity",
                    "logic" : "velocity",
                    "dataType": "integer"
                },
                "value": {
                    "title": "上线次数",
                    "name": "value",
                    "logic" : "value",
                    "dataType": "integer"
                },
                "timeLength": {
                    "title": "在线时长",
                    "name": "timeLength",
                    "logic" : "timeLength",
                    "dataType": "integer"
                },
                "rate": {
                    "title": "占有率",
                    "name": "rate",
                    "logic" : "percent",
                    "dataType": "float"
                },
                "useAble": {
                    "title": "是否有效",
                    "name": "useAble",
                    "logic" : "boolean",
                    "dataType": "boolean"
                }
            }
        },
        billInfo: {
            dataURL: "[isomorphic]/../share/data/widgets.data.json",
            columns: {
                "id": {
                    "visible": false,
                    "primaryKey": true,
                    "name": "id",
                    "dataType": "integer"
                },
                "octets": {
                    "title": "流量",
                    "name": "octets",
                    "logic" : "octets",
                    "dataType": "integer"
                },
                "octetsVelocity": {
                    "title": "带宽",
                    "name": "octetsVelocity",
                    "logic" : "octetsVelocity",
                    "dataType": "integer"
                },
                "velocity": {
                    "title": "速度",
                    "name": "velocity",
                    "logic" : "velocity",
                    "dataType": "integer"
                },
                "value": {
                    "title": "上线次数",
                    "name": "value",
                    "logic" : "value",
                    "dataType": "integer"
                },
                "timeLength": {
                    "title": "在线时长",
                    "name": "timeLength",
                    "logic" : "timeLength",
                    "dataType": "integer"
                },
                "rate": {
                    "title": "占有率",
                    "name": "rate",
                    "logic" : "percent",
                    "dataType": "float"
                },
                "useAble": {
                    "title": "是否有效",
                    "name": "useAble",
                    "logic" : "boolean",
                    "dataType": "boolean"
                }
            }
        }
    }
});
