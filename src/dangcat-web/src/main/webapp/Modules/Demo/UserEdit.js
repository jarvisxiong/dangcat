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
            var column = table.columns.getColumn("description");
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
            var countryColumn = table.columns.getColumn("country");
            var balanceColumn = table.columns.getColumn("balance");
            var foolColumn = table.columns.getColumn("fool");
            var registeTimeColumn = table.columns.getColumn("registeTime");
            if (countryColumn.isReadOnly() == false) {
                countryColumn.setReadOnly(true);
                balanceColumn.setReadOnly(true);
                foolColumn.setReadOnly(true);
                registeTimeColumn.setReadOnly(true);
                item.setTitle("栏位可修改");
            }
            else {
                countryColumn.setReadOnly(false);
                balanceColumn.setReadOnly(false);
                foolColumn.setReadOnly(false);
                registeTimeColumn.setReadOnly(false);
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
            var column = table.columns.getColumn("country");
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
            var countryColumn = table.columns.getColumn("country");
            var balanceColumn = table.columns.getColumn("balance");
            var foolColumn = table.columns.getColumn("fool");
            var registeTimeColumn = table.columns.getColumn("registeTime");
            if (countryColumn.isReadOnly() == false) {
                countryColumn.setReadOnly(true);
                balanceColumn.setReadOnly(true);
                foolColumn.setReadOnly(true);
                registeTimeColumn.setReadOnly(true);
                item.setTitle("栏位可修改");
            }
            else {
                countryColumn.setReadOnly(false);
                balanceColumn.setReadOnly(false);
                foolColumn.setReadOnly(false);
                registeTimeColumn.setReadOnly(false);
                item.setTitle("栏位只读");
            }
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
        },
        {
            name: "treeSelect",
            classType: isc.TreeSelect,
            properties: {
                title: "树形选择",
                typeValueMap: {
                    "1": "[SKIN]/../icons/16/find.png",
                    "2": "[SKIN]/../icons/16/approved.png",
                    "3": "[SKIN]/../icons/16/chart_bar.png",
                    "4": "[SKIN]/../icons/16/export1.png"
                },

                deselectComponentProperties: {
                    values: [
                        {id: 0, parentId: null, type: 1, name: "Node 0"},
                        {id: 1, parentId: 0, type: 2, name: "Node 1"},
                        {id: 2, parentId: 0, type: 3, name: "Node 2"},
                        {id: 3, type: 2, name: "Node 3"},
                        {id: 4, parentId: 3, type: 4, name: "Node 4"}
                    ]
                },

                selectedComponentProperties: {
                    values: [
                        {id: 0, parentId: null, type: 1, name: "Node 0"},
                        {id: 1, parentId: 0, type: 2, name: "Node 1"}
                    ]
                }
            }
        },
        {
            name: "permissionSelect",
            classType: isc.TreeSelect,
            properties: {
                title: "权限管理",
                tableName: "rolePermission",
                onlySaveLeaves: true,
                dataURL: "[isomorphic]/../share/data/rolePermission.data.json",
                typeValueMap: {
                    "0": "[SKIN]/../png/module.gif",
                    "1": "[SKIN]/../png/menu.gif",
                    "2": "[SKIN]/../png/permission.gif"
                }
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
                    },
                    group: {
                        title: "账户组",
                        name: "group",
                        length: 40,
                        dataType: "string",
                        nullable: false
                    }
                }
            }
        }
    },
	
    dataFormProperties: {
		overrideProperties: {
            overrideFieldProperties: {
                rate: {
					editorType: "ComboBoxItem", 
					addUnknownValues:false,
					displayField : "itemName",
					valueField:"SKU",
					pickerIconSrc:"[SKIN]/button/about.png",
					filterFields:["SKU", "itemName"],
					pickListFields:[
						{name:"SKU", title:"币别", length:10, dataType:"String"},
						{name:"itemName", title:"产品名称", length:20, dataType:"String"},
						{name:"price", title:"价格", dataType:"float"}
					],
					pickData : [
						{ SKU:"58074602", itemName: "itemName1", description: "description1", price:123 },
						{ SKU:"58074614", itemName: "item1ame2", description: "description2", price:133 },
						{ SKU:"58074205", itemName: "ite2Name3", description: "description3", price:343 },
						{ SKU:"58073605", itemName: "it3mName4", description: "description4", price:523 },
						{ SKU:"58044605", itemName: "i4emName5", description: "description5", price:456 },
						{ SKU:"58574605", itemName: "i5emName6", description: "description6", price:8888 }
					]
				}
            }
        },
        tableName: "accountInfo",
        columnCount: 2
    },

    dataGridProperties: {
		overrideProperties: {
            overrideFieldProperties: {
                rate: {
					editorType: "ComboBoxItem", 
					addUnknownValues:false,
					valueField:"SKU",
					pickerIconSrc:"[SKIN]/button/about.png",
					filterFields:["SKU", "itemName"],
					pickListFields:[
						{name:"SKU", title:"币别", length:10, dataType:"String"},
						{name:"itemName", title:"产品名称", length:20, dataType:"String"},
						{name:"price", title:"价格", dataType:"float"}
					],
					pickData : [
						{ SKU:"58074602", itemName: "itemName1", description: "description1", price:123 },
						{ SKU:"58074614", itemName: "item1ame2", description: "description2", price:133 },
						{ SKU:"58074205", itemName: "ite2Name3", description: "description3", price:343 },
						{ SKU:"58073605", itemName: "it3mName4", description: "description4", price:523 },
						{ SKU:"58044605", itemName: "i4emName5", description: "description5", price:456 },
						{ SKU:"58574605", itemName: "i5emName6", description: "description6", price:8888 }
					]
				}
            }
        },
        tableName: "billInfo"
    },

    dataSetProperties: {
        overrideProperties: {
            billInfo: {
                defaultRowValues: {
                    name: "马英九",
                    country: "0",
                    balance: 100,
                    fool: true,
                    registeTime: new Date()
                }
            }
        },

        accountInfo: {
            dataURL: "[isomorphic]/../share/data/accountInfo.data.json",
            childrenNames: ["billInfo", "rolePermission"],
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
                "group": {
                    "title": "账户组",
                    "name": "group",
                    "length": 40,
                    "dataType": "string",
                    "nullable": false
                },
                "description": {
                    "title": "账户描述",
                    "name": "description",
                    "length": 2000,
                    "dataType": "string"
                },
                "email": {
                    "title": "邮件",
                    "name": "email",
                    "length": 40,
                    "dataType": "string",
                    "nullable": false
                },
                "tel": {
                    "title": "电话",
                    "name": "tel",
                    "dataType": "integer",
                    "nullable": false
                },
                "country": {
                    "title": "国家",
                    "valueMap": {
                        "0": "Roll",
                        "1": "Ea",
                        "2": "Pkt",
                        "3": "Set",
                        "4": "Tube",
                        "5": "Pad",
                        "6": "Ream",
                        "7": "Tin",
                        "8": "Bag",
                        "9": "Ctn",
                        "10": "Box"
                    },
                    "name": "country",
                    "dataType": "integer"
                },
                "balance": {
                    "title": "账户余额",
                    "name": "balance",
                    "dataType": "float"
                },
                "fool": {
                    "title": "是否傻瓜",
                    "name": "fool",
                    "dataType": "boolean"
                },
                "height": {
                    "title": "身高",
                    "name": "height",
                    "dataType": "float"
                },
                "registeTime": {
                    "title": "注册时间",
                    "name": "registeTime",
                    "dataType": "date"
                },
				"rate": {
					name: "rate", 
					title: "产品汇率", 
					"length": 10,
					"dataType": "string"
				},
				"point": {
					name: "point", 
					title: "小数位数", 
					"dataType": "integer",
					defaultValue: 2, 
					min: 0, 
					max: 5, 
					step: 1
				},
				"choice": {
					name: "choice", 
					title: "选择否", 
					editorType : "checkbox",
					"dataType": "boolean",
					"valueMap": {
                        "true": "选择",
                        "false": "不选择"
                    }
				}
            }
        },
        billInfo: {
            dataURL: "[isomorphic]/../share/data/billInfo.data.json",
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
                    "length": 20,
                    "dataType": "string",
                    "nullable": false
                },
                "country": {
                    "title": "国家",
                    "valueMap": {
                        "0": "Roll",
                        "1": "Ea",
                        "2": "Pkt",
                        "3": "Set",
                        "4": "Tube",
                        "5": "Pad",
                        "6": "Ream",
                        "7": "Tin",
                        "8": "Bag",
                        "9": "Ctn",
                        "10": "Box"
                    },
                    "name": "country",
                    "length": 5,
                    "dataType": "integer"
                },
                "balance": {
                    "title": "账户余额",
                    "name": "balance",
                    "dataType": "float"
                },
                "fool": {
                    "title": "是否傻瓜",
                    "name": "fool",
                    "dataType": "boolean"
                },
                "registeTime": {
                    "title": "注册时间",
                    "name": "registeTime",
                    "dataType": "date"
                },
				"rate": {
					name: "rate", 
					title: "产品汇率", 
					"length": 10,
					"dataType": "string"
				},
				"point": {
					name: "point", 
					title: "小数位数", 
					"dataType": "integer",
					defaultValue: 2, 
					min: 0, 
					max: 5, 
					step: 1
				},
				"choice": {
					name: "choice", 
					title: "选择否", 
					editorType : "checkbox",
					"dataType": "boolean",
					"valueMap": {
                        "true": "选择",
                        "false": "不选择"
                    }
				}
            }
        },
        rolePermission: {
            dataURL: "[isomorphic]/../share/data/rolePermission.selected.data.json",
            columns: {
                "id": {
                    "visible": false,
                    "primaryKey": true,
                    "name": "id",
                    "dataType": "integer"
                },
                "parentId": {
                    "visible": false,
                    "primaryKey": true,
                    "name": "parentId",
                    "dataType": "integer"
                },
                "type": {
                    "visible": false,
                    "primaryKey": true,
                    "name": "type",
                    "dataType": "integer"
                },
                "name": {
                    "title": "名称",
                    "name": "name",
                    "length": 20,
                    "dataType": "string",
                    "nullable": false
                }
            }
        }
    }
})
;
