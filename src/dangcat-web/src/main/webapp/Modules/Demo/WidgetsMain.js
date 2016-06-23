isc.QueryForm.create({

    detailUrl: "./Modules/Demo/WidgetsEdit.js",

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

    dataGridProperties: {
        tableName: "accountInfo",
        fieldNames: [
            "name",
            "octets",
            "octetsVelocity",
            "velocity",
            "value",
            "rate",
            "timeLength",
            "useAble"
        ]
    },

    dataFormProperties: {
        tableName: "accountInfo"
    },

    dataSetProperties: {
        accountInfo: {
            dataURL: "[isomorphic]/../share/data/widgets.data.json",
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
                    "length": 30,
                    "dataType": "string",
                    "nullable": false
                },
                "description": {
                    "title": "账户描述",
                    "name": "description",
                    "length": 2000,
                    "dataType": "string"
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
                    "title": "有效否",
                    "name": "useAble",
                    "logic" : "boolean",
                    "dataType": "boolean"
                }
            }
        }
    }
});
