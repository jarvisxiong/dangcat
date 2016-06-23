isc.QueryForm.create({
    detailUrl: "./Modules/Demo/UserEdit.js",

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

    getDataGridProperties: function () {
        if (this.dataTileGridProperties)
            return {
                name: "dataTileGrid",
                classType: isc.DataTileGrid
            };
    },

    dataTileGridProperties: {
        tableName: "accountInfo",
        tileConstructor: "DataForm",
        tileProperties: {
            canDragReposition: false,
            canDragResize: false,
            fields:[
                {name:"name"},
                {name:"group"},
                {name:"country"},
                {name:"balance"},
                {name:"email"},
                {name:"tel"},
                {name:"fool"},
                {name:"height"}
            ]
        }
    },

    dataFormProperties: {
        tableName: "accountInfo"
    },

    dataSetProperties: {
        accountInfo: {
            dataURL: "[isomorphic]/../share/data/accountInfo.data.json",
            pageSize: 10,
            columns: {
                "id": {
                    "visible": false,
                    "name": "id",
                    "dataType": "integer"
                },
                "name": {
                    "title": "账户名称",
                    "primaryKey": true,
                    "name": "name",
                    "length": 40,
                    "dataType": "string",
                    "nullable": false,
                    "frozen": true
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
					"visible": false,
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
                "height": {
                    "title": "身高",
                    "name": "height",
                    "dataType": "float"
                },
                "registeTime": {
                    "title": "注册时间",
                    "name": "registeTime",
                    "dataType": "date"
                }
            }
        }
    }
});
