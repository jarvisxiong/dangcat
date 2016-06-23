/*
 * DangCat widgets DataSet
 */
isc.ClassFactory.defineClass("DataSet", "Class");
isc.DataSet.addProperties({
    permission: ["create", "save"],
    service: null
});

isc.DataSet.addMethods({
    initialize: function () {
        this.Super("initialize", arguments);

        if (this.dataSetProperties != undefined) {
            this.tables = [];
            for (var tableName in this.dataSetProperties) {
                if (tableName == "overrideProperties") {
                    this.overrideProperties = this.dataSetProperties["overrideProperties"];
                    continue;
                }
                var table = isc.Table.create(this.dataSetProperties[tableName], isc.Service.DefaultComponentProperties, {
                    name: tableName,
                    dataSet: this
                });
                this.tables.push(table);
                this[tableName] = table;
                table.initWidget();
                table.initialize();
            }
        }
        this.extendProperties();
        this.bindListeners(this.service);
        this.bindParent();
    },

    bindParent: function () {
        for (var i = 0; i < this.tables.length; i++) {
            var table = this.tables[i];
            if (table.childrenNames) {
                for (var j = 0; j < table.childrenNames.length; j++) {
                    var childTable = this[table.childrenNames[j]];
                    if (childTable)
                        childTable.parent = table;
                }
            }
        }
    },

    existsPermission: function (permission) {
        return this.service.existsPermission(permission);
    },

    load: function () {
        for (var i = 0; i < this.tables.length; i++)
            this.tables[i].loading = true;
        var navigate = this.getNavigate();
        if (navigate)
            navigate.load();
        for (var i = 0; i < this.tables.length; i++) {
            var table = this.tables[i];
            if (table.loading) {
                table.load();
                delete table.loading;
            }
        }
    },

    getNavigate: function () {
        if (!this.navigate) {
            for (var i = 0; i < this.tables.length; i++) {
                if (this.tables[i].navigate == true) {
                    this.navigate = this.tables[i];
                    break;
                }
            }
        }
        if (!this.navigate && this.tables.length > 0)
            this.navigate = this.tables[0];
        return this.navigate;
    },

    changeDataState: function (table) {
        if (table.isChanging()) {
            var navigate = this.getNavigate();
            if (navigate && navigate != table) {
                if (navigate.dataState == isc.Table.DataStates.Browse)
                    navigate.setDataState(isc.Table.DataStates.Modified);
            }
        }
    },

    contains: function (tableName) {
        return this[tableName] != undefined && this[tableName] != null;
    },

    bindListeners: function (component) {
        for (var i = 0; i < this.tables.length; i++)
            this.tables[i].bindListener(component);
    },

    setData: function (tableName, responseData) {
        if (this.contains(tableName)) {
            if (responseData) {
                var table = this[tableName];
                table.setData(responseData);
                if (table.hasChildren()) {
                    var currentRow;
                    if (isc.isA.Array(responseData)) {
                        if (responseData.length > 0)
                            currentRow = responseData[0];
                    }
                    else
                        currentRow = responseData;
                    for (var i = 0; i < table.childrenNames.length; i++) {
                        var childTableName = table.childrenNames[i];
                        if (!currentRow)
                            this.clear(childTableName);
                        else
                            this.setData(childTableName, currentRow[childTableName]);
                    }
                }
            } else
                this.clear(tableName);
        }
    },

    clear: function (tableName) {
        if (this.contains(tableName)) {
            var table = this[tableName];
            table.setData([]);
            if (table.hasChildren()) {
                for (var i = 0; i < table.childrenNames.length; i++) {
                    var tableName = table.childrenNames[i];
                    var childTable = this[tableName];
                    if (childTable)
                        childTable.setData([]);
                }
            }
        }
    },

    beforeSave: function (tableName) {
        if (tableName == undefined || tableName == null)
            tableName = this.getNavigate().name;

        if (this.contains(tableName)) {
            var table = this[tableName];
            if (table.beforeSave() == false)
                return false;
            if (table.hasChildren()) {
                for (var i = 0; i < table.childrenNames.length; i++) {
                    if (this.beforeSave(table.childrenNames[i]) == false)
                        return false;
                }
            }
        }
    },

    afterSave: function (tableName) {
        if (!tableName)
            tableName = this.getNavigate().name;

        if (this.contains(tableName)) {
            var table = this[tableName];
            if (!table.isEmpty()) {
                table.afterSave();
                if (table.hasChildren()) {
                    for (var i = 0; i < table.childrenNames.length; i++)
                        this.afterSave(table.childrenNames[i]);
                }
            }
        }
    },

    hasErrors: function (tableName) {
        if (!tableName)
            tableName = this.getNavigate().name;

        if (this.contains(tableName)) {
            var table = this[tableName];
            if (table.hasErrors())
                return true;
            if (table.hasChildren()) {
                for (var i = 0; i < table.childrenNames.length; i++) {
                    if (this.hasErrors(table.childrenNames[i]))
                        return true;
                }
            }
        }
        return false;
    },

    afterLoad: function (tableName) {
        if (!tableName)
            tableName = this.getNavigate().name;

        if (this.contains(tableName)) {
            var table = this[tableName];
            table.afterLoad();
            if (table.hasChildren()) {
                for (var i = 0; i < table.childrenNames.length; i++)
                    this.afterLoad(table.childrenNames[i]);
            }
        }
    },

    saveData: function (tableName) {
        if (!tableName)
            tableName = this.getNavigate().name;

        if (this.contains(tableName)) {
            var table = this[tableName];
            if (!table.isEmpty()) {
                var tableData = table.saveData();
                if (tableData) {
                    if (table.pageSize > 1)
                        return tableData;

                    var saveData = tableData[0];
                    if (table.hasChildren()) {
                        for (var i = 0; i < table.childrenNames.length; i++) {
                            var tableName = table.childrenNames[i];
                            var childTableData = this.saveData(tableName);
                            if (childTableData)
                                saveData[tableName] = childTableData;
                        }
                    }
                    return saveData;
                }
            }
        }
    },

    destroy: function () {
        if (this.tables) {
            for (var i = 0; i < this.tables.length; i++)
                this.tables[i].destroy();
        }
        this.Super("destroy", arguments);
    }
});

/*
 * DangCat widgets table
 */
isc.ClassFactory.defineClass("Table", "Class");
isc.Table.addClassProperties({
    DataStates: {
        Loading: "loading",
        Insert: "Insert",
        Modified: "Modified",
        Browse: "Browse"
    },

    ModeTypes: {
        Query: "query",
        Temp: "temp",
        MasterEdit: "masterEdit",
        DetailEdit: "detailEdit"
    },

    Events: {
        DataStateChanging: "DataStateChanging",
        DataStateChanged: "DataStateChanged",
        RowDataStateChanging: "RowDataStateChanging",
        RowDataStateChanged: "RowDataStateChanged",
        CurrentRowChanged: "CurrentRowChanged",
        CurrentChanged: "CurrentChanged",
        BeforeSave: "BeforeSave",
        AfterSave: "AfterSave",
        BeforeUpdate: "BeforeUpdate",
        AfterUpdate: "AfterUpdate",
        BeforeView: "BeforeView",
        BeforeLoad: "BeforeLoad",
        AfterLoad: "AfterLoad",
        BeforeDelete: "BeforeDelete",
        AfterDelete: "AfterDelete",
        BeforeInsert: "BeforeInsert",
        AfterInsert: "AfterInsert",
        BeforeTableValidate: "BeforeTableValidate",
        AfterTableValidate: "AfterTableValidate",
        OnRowValidate: "OnRowValidate",
        OnFieldValidate: "OnFieldValidate",
        OnColumnVisibleChanged: "OnColumnVisibleChanged",
        OnColumnReadOnlyChanged: "OnColumnReadOnlyChanged",
        OnCheckReadOnly: "OnCheckReadOnly",
        OnFieldErrors: "OnFieldErrors",
        OnClearFieldErrors: "OnClearFieldErrors",
        OnFieldPickData: "OnFieldPickData",
        OnRowPickData: "OnRowPickData",
        OnFieldValueMap: "OnFieldValueMap",
        OnRowValueMap: "OnRowValueMap"
    },

    DefaultColumnsProperties: {
        initialize: function () {
            for (var i = 0; i < this.fieldNames.length; i++) {
                var column = this.getColumn(this.fieldNames[i]);
                column.initialize();
            }
        },

        getPrimaryKeys: function () {
            return this.primaryKeys;
        },

        getColumn: function (fieldName) {
            if (isc.isA.Number(fieldName) && fieldName >= 0 && fieldName < this.length)
                fieldName = this.fieldNames[fieldName];
            return this[fieldName];
        },

        createField: function (fieldName) {
            var column = this.getColumn(fieldName);
            if (column && isc.isA.Object(column) && !isc.isA.Array(column)) {
                var field = {
                    autoDraw: false,
                    title: column.title,
                    name: column.name,
                    dataType: column.dataType
                };

                if (!field.type) {
                    if (column.isReadOnly())
                        field.type = "StaticText";
                    else {
                        if (column.isEnumField()) {
                            field.type = "enum";
                            if (column.nullable != false)
                                field.allowEmptyValue = true;
                        }
                        else if (column.encrypt == true || column.password != undefined)
                            field.type = "password";
                        else if (column.dataType == "string")
                            field.type = "text";
                        else
                            field.type = column.dataType;
                    }
                }
                if (column.dataType == "integer")
                    field.keyPressFilter = "[0-9]";
                else if (column.dataType == "float")
                    field.keyPressFilter = "[0-9.]";
                if (!column.isVisible())
                    field.hidden = true;
                if (column.length != undefined && column.length != 0)
                    field.length = column.length;
                else if (column.dataType == "date" || column.dataType == "datetime")
                    field.length = 19;
                else if (column.dataType == "integer" || column.dataType == "float")
                    field.length = 15;
                else if (column.dataType == "boolean") {
                    if (field.type == "enum")
                        field.length = 10;
                }
                if (column.nullable == false)
                    field.required = true;

                var fieldNames = ["editorType", "logic", "frozen", "autoFitWidth", "min", "max"];
                for (var i = 0; i < fieldNames.length; i++) {
                    if ($.defined(column[fieldNames[i]]))
                        field[fieldNames[i]] = column[fieldNames[i]];
                }

                if (column.fieldProperties)
                    isc.addProperties(field, column.fieldProperties);
                return field;
            }
        },

        getCurrentField: function () {
            return this.currentField;
        },

        setCurrentField: function (fieldName) {
            if (!fieldName)
                fieldName = this.fieldNames[0];
            var column = this.getColumn(fieldName);
            if (column) {
                this.currentField = fieldName;
                this.table.fireEvent(isc.Table.Events.CurrentChanged, column, this.table.getCurrentRow());
            }
        },

        getFieldNames: function () {
            return this.fieldNames;
        },

        getFields: function (fieldNames) {
            var fields = [];
            if (fieldNames && !isc.isAn.Array(fieldNames))
                fieldNames = [ fieldNames ];
            if (!fieldNames || fieldNames.length == 0)
                fieldNames = this.getFieldNames();
            for (var i = 0; i < fieldNames.length; i++) {
                var field = this.createField(fieldNames[i]);
                if (field != null && !(field.hidden == true))
                    fields[fields.length] = field;
            }
            return fields;
        },

        prepareLoad: function (success) {
            var fieldNames = this.getFieldNames();
            for (var i = 0; i < fieldNames.length; i++) {
                var column = this[fieldNames[i]];
                column.prepareLoad(function () {
                    if (success != undefined && success != null)
                        success();
                });
            }
        }
    },

    DefaultColumnProperties: {
        initialize: function () {
            if (!$.defined(this.valueMap)) {
                if (this.dataType == "boolean" && (this.nullable != false || this.isReadOnly()))
                    this.valueMap = isc.i18nBooleanValueMap;
            }
            if (this.table.hiddenColumnNames && this.table.hiddenColumnNames.indexOf(this.name) != -1)
                this.visible = false;
        },

        setVisible: function (visible) {
            if (this.visible != visible) {
                this.visible = visible;
                this.table.fireEvent(isc.Table.Events.OnColumnVisibleChanged, this);
            }
        },

        hasPermission: function () {
            if (this.permission)
                return this.table.dataSet.existsPermission(this.permission);
            return true;
        },

        isVisible: function (row) {
            if (!this.hasPermission() && this.permissionReadOnly != true)
                return false;
            if (this.visible != undefined && !this.visible)
                return false;
            if (row) {
                var dataStateVisible = this["dataStateVisible"];
                if (dataStateVisible) {
                    var dataState = isc.Table.DataStates.Browse;
                    if (row.dataState)
                        dataState = row.dataState;
                    for (var i = 0; i < dataStateVisible.length; i++) {
                        if (dataStateVisible[i] == dataState)
                            return true;
                    }
                    return false;
                }
            }
            return true;
        },

        isReadOnly: function (row) {
            if (!this.hasPermission() && this.permissionReadOnly == true)
                return true;
            if (this.table.readOnly == true)
                return true;
            if (this.readOnly != undefined && this.readOnly == true)
                return true;
            if (row) {
                var dataStateReadOnly = this["dataStateReadOnly"];
                if (dataStateReadOnly) {
                    var dataState = isc.Table.DataStates.Browse;
                    if (row.dataState)
                        dataState = row.dataState;
                    else if (row.table && row.table.dataState)
                        dataState = row.table.dataState;
                    for (var i = 0; i < dataStateReadOnly.length; i++) {
                        if (dataStateReadOnly[i] == dataState)
                            return true;
                    }
                }
                if (this.table.fireEvent(isc.Table.Events.OnCheckReadOnly, this, row) == true)
                    return true;
            }
            return false;
        },

        setReadOnly: function (readOnly) {
            if (this.readOnly != readOnly) {
                this.readOnly = readOnly;
                this.table.fireEvent(isc.Table.Events.OnColumnReadOnlyChanged, this);
            }
        },

        parseValue: function (value) {
            if (this.dataType != "string" && isc.isA.String(value)) {
                if (this.dataType == "integer") {
                    var fieldValue = parseInt(value);
                    value = isNaN(fieldValue) ? null : fieldValue;
                }
                else if (this.dataType == "float") {
                    var fieldValue = parseFloat(value);
                    value = isNaN(fieldValue) ? null : fieldValue;
                }
                else if (this.dataType == "boolean")
                    value = Boolean.parseText(value);
                else if (this.dataType == "datetime" || this.dataType == "date")
                    value = Date.parseValue(value, this.format);
            }
            return value;
        },

        validateValue: function (value) {
            if (this.nullable == false) {
                if (!$.defined(value))
                    return this.title + isc.i18nValidate.NullAble;
                if (isc.isA.String(value) && value.isEmpty())
                    return this.title + isc.i18nValidate.NullAble;
            }
            if (value != undefined && value != null) {
                if (isc.isA.String(value) && value.trim().length > this.length)
                    return this.title + isc.i18nValidate.MaxLength + this.length;

                if ($.defined(this.minValue) && $.defined(this.maxValue)) {
                    if (value < this.minValue || value > this.maxValue)
                        return this.title + isc.i18nValidate.InvalidRange + "[" + this.minValue + ", " + this.maxValue + "]";
                }

                if ($.defined(this.minValue) && value < this.minValue)
                    return this.title + isc.i18nValidate.MinValue + "" + this.minValue;

                if ($.defined(this.maxValue) && value > this.maxValue)
                    return this.title + isc.i18nValidate.MaxValue + "" + this.maxValue;

                var valueMap = this.getValueMap();
                if (valueMap) {
                    if (!$.defined(this.fieldProperties) || this.fieldProperties.addUnknownValues != true || this.fieldProperties.canBeUnknownValue != true) {
                        if (!$.defined(valueMap["" + value]))
                            return this.title + isc.i18nValidate.InvalidValue;
                    }
                }

                if (this.logic) {
                    var validateResult = LogicValidator.validate(this.logic, value);
                    if ($.defined(validateResult))
                        return validateResult;
                }
            }
        },

        validate: function (row, value) {
            row.clearFieldErrors(this.name);
            if (row.isVisible(this.name)) {
                var error = this.validateValue(value);
                if (error)
                    row.addFieldErrors(this.name, error);
                else {
                    error = this.table.fireEvent(isc.Table.Events.OnFieldValidate, this, row, value);
                    if (error)
                        row.addFieldErrors(this.name, error);
                }
                return error;
            }
        },

        isEnumField: function () {
            return this.valueMap != undefined || this.enumField == true || this.fieldValueMapJndiName != undefined || this.rowValueMapJndiName != undefined;
        },

        getValueMap: function (success) {
            var column = this;
            if (this.fieldProperties && this.fieldProperties.pickListFields) {
                this.getPickData(function () {
                    if (success)
                        success(column.valueMap);
                });
            }

            if (!this.isEnumField())
                return;

            if (!this.valueMap) {
                this.valueMap = this.table.fireEvent(isc.Table.Events.OnFieldValueMap, column, function (data) {
                    if (data) {
                        column.valueMap = data;
                        if (success)
                            success(data);
                    }
                });
            }
            if (!this.valueMap) {
                if (this.fieldValueMapJndiName != undefined && (!$.defined(column.executeSelectDateTime) || (new Date() - column.executeSelectDateTime) > 10000)) {
                    column.executeSelectDateTime = new Date();
                    isc.PRCService.select(this.fieldValueMapJndiName, undefined, function (responseData) {
                        if (responseData) {
                            column.valueMap = responseData;
                            delete column.executeSelectDateTime;
                            if (success)
                                success(responseData);
                        }
                    });
                }
            }
            return this.valueMap;
        },

        getPickData: function (success) {
            var column = this;
            if (!this.pickData) {
                this.pickData = this.table.fireEvent(isc.Table.Events.OnFieldPickData, column, function (data) {
                    if (data) {
                        column.setPickData(data);
                        if (success)
                            success(data);
                    }
                });
            }
            if (!this.pickData) {
                if (this.fieldPickDataJndiName != undefined && (!$.defined(column.executePickDateTime) || (new Date() - column.executePickDateTime) > 10000)) {
                    column.executePickDateTime = new Date();
                    isc.PRCService.query(this.fieldPickDataJndiName, undefined, function (responseData) {
                        if (responseData) {
                            column.setPickData(responseData);
                            delete column.executePickDateTime;
                            if (success)
                                success(responseData);
                        }
                    });
                }
            }
            return this.pickData;
        },

        setPickData: function (responseData) {
            this.pickData = responseData;
            if (responseData && this.fieldProperties && this.fieldProperties.displayField) {
                var valueMap = {},
                    valueFieldName = this.fieldProperties.valueField,
                    displayFieldName = this.fieldProperties.displayField;
                for (var i = 0; i < responseData.length; i++) {
                    var data = responseData[i];
                    valueMap[data[valueFieldName] + ""] = data[displayFieldName];
                }
                this.valueMap = valueMap;
            }
        },

        prepareLoad: function (success) {
            if (this.fieldValueMapJndiName)
                delete this.valueMap;
            if (this.fieldPickDataJndiName)
                delete this.pickData;
            this.getValueMap(success);
            this.getPickData(success);
        }
    },

    DefaultRowProperties: {
        addFieldErrors: function (fieldName, message) {
            if (this.errors) {
                for (var i = 0; i < this.errors.length; i++) {
                    if (this.errors[i].error == message && this.errors[i].fieldName == fieldName)
                        return;
                }
            }

            var error = {
                error: message
            }
            if (this.errors)
                this.errors.push(error);
            else
                this.errors = [error]
            if (fieldName) {
                error.fieldName = fieldName;
                var column = this.table.columns.getColumn(fieldName);
                this.table.fireEvent(isc.Table.Events.OnFieldErrors, column, this, message);
            }
        },

        setDataState: function (dataState) {
            if (this.dataState != dataState) {
                this.table.fireEvent(isc.Table.Events.RowDataStateChanging, this, this.dataState, dataState);
                this.dataState = dataState;
                if (this.table.isChanging(dataState)) {
                    if (this.table.dataState == isc.Table.DataStates.Browse)
                        this.table.setDataState(dataState);
                }
                this.table.fireEvent(isc.Table.Events.RowDataStateChanged, this, this.dataState);
            }
        },

        getFieldErrors: function (fieldName) {
            if (this.errors) {
                for (var i = this.errors.length - 1; i >= 0; i--) {
                    if (this.errors[i].fieldName == fieldName)
                        return this.errors[i].error;
                }
            }
            return null;
        },

        hasErrors: function (fieldName) {
            if (fieldName)
                return this.getFieldErrors(fieldName) != undefined;
            return this.errors && this.errors.length > 0;
        },

        validate: function () {
            this.clearFieldErrors();
            var columns = this.table.columns;
            var fieldNames = columns.getFieldNames();
            for (var i = 0; i < fieldNames.length; i++) {
                var column = columns[fieldNames[i]];
                if (column && column.isVisible())
                    column.validate(this, this[column.name]);
            }
            var error = this.table.fireEvent(isc.Table.Events.OnRowValidate, this);
            if (error)
                this.addFieldErrors(undefined, error);
        },

        clearFieldErrors: function (fieldName) {
            if (this.errors && this.errors.length > 0) {
                if (fieldName) {
                    var column = this.table.columns.getColumn(fieldName);
                    if (column != undefined && column != null) {
                        for (var i = this.errors.length - 1; i >= 0; i--) {
                            if (this.errors[i].fieldName == fieldName)
                                this.errors.splice(i, 1);
                        }
                        if (this.errors.length == 0)
                            this.errors = null;
                        this.table.fireEvent(isc.Table.Events.OnClearFieldErrors, column, this);
                    }
                }
                else {
                    var columns = [];
                    for (var i = this.errors.length - 1; i >= 0; i--) {
                        var fieldName = this.errors[i].fieldName;
                        var column = this.table.columns.getColumn(fieldName);
                        if (column)
                            columns[columns.length] = column;
                        else
                            columns[columns.length] = undefined;
                    }
                    this.errors = null;
                    for (var i = 0; i < columns.length; i++) {
                        var column = columns[i];
                        if (column)
                            this.table.fireEvent(isc.Table.Events.OnClearFieldErrors, column, this);
                        else
                            this.table.fireEvent(isc.Table.Events.OnClearFieldErrors, this);
                    }
                }
            }
        },

        isValid: function (filter) {
            if (filter) {
                for (var name in filter) {
                    if (filter[name] != this[name])
                        return false;
                }
            }
            return true;
        },

        update: function (fieldName, value) {
            if (this.isReadOnly(fieldName))
                return false;
            if (this[fieldName] != value) {
                var dataState = this.dataState;
                if (!dataState)
                    dataState = this.table.dataState;
                if (!dataState || dataState == isc.Table.DataStates.Browse)
                    dataState = isc.Table.DataStates.Modified;
                if (this.table.isChanging(dataState)) {
                    var column = this.table.columns.getColumn(fieldName);
                    if (this.table.fireEvent(isc.Table.Events.BeforeUpdate, column, this, value) == false)
                        return false;

                    var value = column.parseValue(value);
                    column.validate(this, value);
                    this[fieldName] = value;
                    this.setDataState(dataState);
                    this.table.fireEvent(isc.Table.Events.AfterUpdate, column, this, value);
                    return true;
                }
            }
        },

        saveData: function () {
            var data = null;
            var fieldNames = this.table.columns.getFieldNames();
            for (var i = 0; i < fieldNames.length; i++) {
                var column = this.table.columns[fieldNames[i]];
                var value = this[column.name];
                if (value != undefined && value != null) {
                    if (data == null)
                        data = {};
                    if (isc.isA.String(value))
                        value = value.trim();
                    var result = column.parseValue(value);
                    if (result != undefined && result != null) {
                        if (isc.isA.Date(result))
                            result = result.format();
                        else if (column.password) {
                            var no = this[column.password];
                            if (no == undefined || no == null)
                                no = "";
                            result = $.encryptPassword(no, result, column.encryptAlgorithm);
                        }
                        else if (column.encrypt == true)
                            result = $.encryptContent(result);
                    }
                    data[column.name] = result;
                }
            }
            if (data && this.dataState && this.dataState != isc.Table.DataStates.Browse)
                data.dataState = this.dataState;
            return data;
        },

        parseData: function (data) {
            if (data != undefined && data != null) {
                var fieldNames = this.table.columns.getFieldNames();
                for (var i = 0; i < fieldNames.length; i++) {
                    var fieldName = fieldNames[i];
                    var value = data[fieldName]
                    if (value != undefined) {
                        var column = this.table.columns[fieldName];
                        value = column.parseValue(value);
                        if (value != undefined && value != null) {
                            if (column.encrypt == true)
                                value = $.decryptContent(value);
                            else if (isc.isA.Boolean(value) && column.nullable != false)
                                value = value ? value : false;
                        }
                    }
                    this[fieldName] = (value == undefined) ? null : value;
                }
            }
        },

        getPrimaryKeyValues: function () {
            var primaryKeys = this.table.columns.getPrimaryKeys();
            if (primaryKeys.length == 1) {
                var primaryKeyValue = this[primaryKeys[0].name]
                if (primaryKeyValue == undefined || primaryKeyValue == null)
                    return;
                return primaryKeyValue;
            }
            var primaryKeyValues = {};
            for (var i = 0; i < primaryKeys.length; i++) {
                var column = primaryKeys[i];
                if (!this[column.name])
                    return;
                primaryKeyValues[column.name] = this[column.name];
            }
            return primaryKeyValues;
        },

        getValueMap: function (fieldName, success) {
            var column = this.table.columns[fieldName];
            if (!column)
                return;

            var currentRow = this;
            var valueMap = column.getValueMap(success);
            if (!valueMap)
                valueMap = this.table.fireEvent(isc.Table.Events.OnRowValueMap, column, currentRow, success);
            return valueMap;
        },

        getPickData: function (fieldName, success) {
            var column = this.table.columns[fieldName];
            if (!column)
                return;

            var currentRow = this;
            var pickData = column.getPickData(success);
            if (!pickData)
                pickData = this.table.fireEvent(isc.Table.Events.OnRowPickData, column, currentRow, success);
            return pickData;
        },

        isVisible: function (fieldName) {
            var column = this.table.columns[fieldName];
            if (column)
                return column.isVisible(this);
            return true;
        },

        isReadOnly: function (fieldName) {
            var column = this.table.columns[fieldName];
            if (column)
                return column.isReadOnly(this);
        },

        format: function (fieldName, value) {
            if (value != undefined && value != null) {
                var column = this.table.columns[fieldName];
                if (column) {
                    if (column.isEnumField()) {
                        var valueMap = this.getValueMap(fieldName);
                        if (valueMap && valueMap[value] != undefined)
                            value = valueMap[value];
                    }
                    else {
                        var data = this[fieldName];
                        if (data != undefined && data != null && data.format != undefined)
                            value = data.format(column.format);
                    }
                }
            }
            if (value == undefined || value == null)
                return "&nbsp;";
            return value;
        }
    },

    DefaultCreateProperties: {
        createDefaultRow: function () {
            var defaultRow = isc.addProperties({}, this.defaultRowValues);
            for (var i = 0; i < this.columns.length; i++) {
                var column = this.columns.getColumn(i);
                if (defaultRow[column.name] == undefined)
                    defaultRow[column.name] = null;
            }
            return defaultRow;
        },

        onCreateSuccess: function (responseData, success) {
            if (responseData) {
                if (!responseData.data)
                    responseData.data = this.createDefaultRow();
                this.dataSet.setData(this.name, responseData);
                this.afterInsert();
            }
            if (success)
                success(responseData);
        },

        afterInsert: function () {
            if (this.hasChildren()) {
                for (var i = 0; i < this.childrenNames.length; i++) {
                    var childTable = this.dataSet[this.childrenNames[i]];
                    childTable.fireEvent(isc.Table.Events.AfterInsert);
                }
            }
            this.setDataState(isc.Table.DataStates.Insert);
            this.fireEvent(isc.Table.Events.AfterInsert);
        },

        beforeInsert: function () {
            if (this.hasChildren()) {
                for (var i = 0; i < this.childrenNames.length; i++) {
                    var childTable = this.dataSet[this.childrenNames[i]];
                    if (childTable.fireEvent(isc.Table.Events.BeforeInsert) == false)
                        return false;
                }
            }
            return this.fireEvent(isc.Table.Events.BeforeInsert);
        },

        createNewRow: function (success, error) {
            if (!this.isCreateAble())
                return;

            if (this.beforeInsert() == false) {
                if (error != undefined && error != null)
                    error();
                return;
            }
            if (this.isCache()) {
                var defaultRowData = this.createDefaultRow();
                var row = this.parseRow(defaultRowData);
                this.addRow(row);
                this.setCurrentRow(row);
                row.setDataState(isc.Table.DataStates.Insert);
                this.fireEvent(isc.Table.Events.AfterInsert, row);
                if (success)
                    success(row);
                return row;
            }
            else {
                var table = this;
                isc.PRCService.createNew(this.getJndiName(), function (responseData) {
                    table.onCreateSuccess(responseData, success);
                }, function (request, textStatus, errorThrown) {
                    table.responseErrors(request, textStatus, errorThrown, error);
                });
            }
        },

        add: function (row, success, error) {
            if (!this.isCreateAble())
                return;

            if (this.fireEvent(isc.Table.Events.BeforeInsert) == false) {
                if (error)
                    error();
                return;
            }
            this.addRow(row);
            isc.addProperties(row, {table: this}, isc.Table.DefaultRowProperties);
            this.setCurrentRow(row);
            row.setDataState(isc.Table.DataStates.Insert);
            this.fireEvent(isc.Table.Events.AfterInsert, row);
            if (success)
                success(row);
            return row;
        },

        addRow: function (row) {
            if (row != null) {
                if (this.isEmpty() || this.pageSize == 1)
                    this.rows = [row];
                else
                    this.rows.push(row);
            }
        }
    },

    DefaultLoadProperties: {
        createFilter: function (filter) {
            var filterProperties = {
                pageSize: this.pageSize,
                startRow: this.startRow
            };

            if (this.relation && this.relation.getFilter) {
                var parentTable = this.dataSet[this.relation.parentName];
                if (parentTable) {
                    var relationFiler = this.relation.getFilter(parentTable);
                    if (relationFiler)
                        isc.addProperties(filterProperties, relationFiler);
                }
            }

            if (this.filter)
                isc.addProperties(filterProperties, this.filter);
            if (filter)
                isc.addProperties(filterProperties, filter);
            return filterProperties;
        },

        setFilter: function (filter) {
            this.filter = filter;
            this.load();
        },

        load: function (filter, success, error) {
            if (this.relation) {
                var parentTable = this.dataSet[this.relation.parentName];
                if (!parentTable || parentTable.isEmpty()) {
                    this.onLoadSuccess({data: []}, success);
                    return;
                }
            }

            this.setDataState(isc.Table.DataStates.Loading);
            this.beforeLoad();
            var filterProperties = this.createFilter(filter);
            var table = this;
            if (this.isLocaleLoad()) {
                this.getDataSource().fetchData(filterProperties, function (dsResponse, dataLoad, dsRequest) {
                    table.setTotalSize(dataLoad.length);
                    var rows = [];
                    for (var i = table.startRow; i < table.startRow + table.pageSize && i <= dataLoad.length; i++)
                        rows[rows.length] = dataLoad[i - 1];
                    table.onLoadSuccess({data: rows}, success);
                });
            } else {
                isc.PRCService.query(this.getJndiName(), filterProperties, function (responseData) {
                    if (responseData) {
                        if (responseData.startRow != undefined)
                            table.startRow = responseData.startRow;

                        if (responseData.totalSize != undefined)
                            table.totalSize = responseData.totalSize;

                        var id = table.getViewId(responseData);
                        if (id != undefined && id != null)
                            table.view(id, success, error);
                        else
                            table.onLoadSuccess(responseData, success);
                    }
                }, function (request, textStatus, errorThrown) {
                    table.responseErrors(request, textStatus, errorThrown, error);
                });
            }
        },

        getViewId: function (responseData) {
            if (this.mode == isc.Table.ModeTypes.MasterEdit && responseData && responseData.data) {
                if (isc.isA.Array(responseData.data) && responseData.data.length == 1) {
                    var data = responseData.data[0];
                    var columns = this.columns.getPrimaryKeys();
                    if (columns) {
                        if (columns.length == 1 && columns[0].dataType == "integer")
                            return data[columns[0].name];

                        var id = {};
                        for (var i = 0; i < columns.length; i++) {
                            var column = columns[i];
                            id[column.name] = data[column.name];
                        }
                        return id;
                    }
                }
            }
        },

        view: function (id, success, error) {
            var table = this;
            var jndiName = this.viewJndiName ? this.viewJndiName : this.getJndiName();
            this.fireEvent(isc.Table.Events.BeforeView, id);
            if (isc.isA.Number(id)) {
                isc.PRCService.view(jndiName, id, function (responseData) {
                    table.onViewSuccess(responseData, success);
                }, function (request, textStatus, errorThrown) {
                    table.responseErrors(request, textStatus, errorThrown, error);
                });
            }
            else {
                var methodName = this.viewJndiName ? undefined : "view";
                isc.PRCService.invoke(jndiName, methodName, id, function (responseData) {
                    table.onViewSuccess(responseData, success);
                }, function (request, textStatus, errorThrown) {
                    table.responseErrors(request, textStatus, errorThrown, error);
                });
            }
        },

        onLoadSuccess: function (responseData, success) {
            if (responseData) {
                this.checkResponse(responseData);
                this.dataSet.setData(this.name, responseData.data);
            }
            if (success)
                success(responseData);
            this.dataSet.afterLoad(this.name);
        },

        onViewSuccess: function (responseData, success) {
            if (responseData) {
                this.checkResponse(responseData);
                this.dataSet.setData(this.name, responseData);
            }
            if (success)
                success(responseData);
            this.dataSet.afterLoad(this.name);
        },

        beforeLoad: function () {
            var table = this;
            this.columns.prepareLoad();
            this.fireEvent(isc.Table.Events.BeforeLoad);
            if (this.hasChildren()) {
                for (var i = 0; i < this.childrenNames.length; i++) {
                    var childTable = this.dataSet[this.childrenNames[i]];
                    childTable.beforeLoad();
                }
            }
            var currentRow = this.getCurrentRow();
            if ($.defined(currentRow))
                this.previousPrimaryKeyValues = currentRow.getPrimaryKeyValues();
            else
                delete this.previousPrimaryKeyValues;
            delete table.loading;
        },

        afterLoad: function () {
            if (this.hasChildren()) {
                for (var i = 0; i < this.childrenNames.length; i++) {
                    var childTable = this.dataSet[this.childrenNames[i]];
                    if (childTable.isLocaleLoad())
                        childTable.load();
                }
            }
            for (var i = 0; i < this.dataSet.tables.length; i++) {
                var table = this.dataSet.tables[i];
                if (table.relation && table.relation.parentName == this.name)
                    table.load();
            }
            this.dataState = isc.Table.DataStates.Browse;
            this.fireEvent(isc.Table.Events.AfterLoad);
        }
    },

    DefaultLocalDataProperties: {
        getDataSource: function (fields) {
            if (!this.dataSource) {
                this.dataSource = isc.DataSource.create({
                    ID: this.name,
                    fields: fields,
                    dataFormat: "json",
                    dataURL: this.dataURL
                });
            }
            return this.dataSource;
        },

        isLocaleLoad: function () {
            return this.dataURL && this.getDataSource().dataURL;
        }
    },

    DefaultRemoveProperties: {
        remove: function (row, success, error) {
            if (!this.isRemoveAble())
                return false;

            var currentRow;
            if (!row)
                currentRow = this.getCurrentRow();
            else if (isc.isA.Number(row))
                currentRow = this.getCurrentRow(row);
            else
                currentRow = row;
            if (currentRow) {
                if (this.fireEvent(isc.Table.Events.BeforeDelete, currentRow) == false) {
                    if (error != undefined && error != null)
                        error(currentRow);
                    return false;
                }
                var table = this;
                var primaryKeyValues = currentRow.getPrimaryKeyValues();
                if (primaryKeyValues == undefined || primaryKeyValues == null || this.isCache()) {
                    var result = this.removeRow(currentRow);
                    this.fireEvent(isc.Table.Events.AfterDelete, currentRow);
                    if (success != undefined && success != null)
                        success(currentRow);
                    return result;
                }
                else {
                    isc.PRCService.remove(this.getJndiName(), primaryKeyValues, function (responseData) {
                        table.onRemoveSuccess(responseData, success, currentRow);
                    }, function (request, textStatus, errorThrown) {
                        table.responseErrors(request, textStatus, errorThrown, error);
                    });
                }
            }
        },

        onRemoveSuccess: function (responseData, success, currentRow) {
            if (responseData) {
                if (this.checkResponse(responseData)) {
                    this.fireEvent(isc.Table.Events.AfterDelete, currentRow);
                    this.load();
                }
            }
            if (success)
                success(responseData);
        },

        removeRow: function (currentRow) {
            if (currentRow) {
                if (this.rows.remove(currentRow)) {
                    if (this.dataState == isc.Table.DataStates.Browse)
                        this.setDataState(isc.Table.DataStates.Modified);
                    return true;
                }
            }
            return false;
        }
    },

    DefaultUpdateProperties: {
        update: function (fieldName, value, dstRow) {
            if (this.isUpdateAble()) {
                var row;
                if (!dstRow || isc.isA.Number(dstRow))
                    row = this.getCurrentRow(dstRow);
                else if (dstRow)
                    row = dstRow;
                if (row) {
                    var result = row.update(fieldName, value);
                    if (result == false)
                        return false;
                }
            }
        }
    },

    DefaultSaveProperties: {
        beforeSave: function () {
            if (this.fireEvent(isc.Table.Events.BeforeSave, this) == false)
                return false;
            if (!this.validate())
                return false;
            return !this.hasErrors();
        },

        save: function (success, error) {
            if (this.dataSet.beforeSave(this.name) == false)
                return;

            var data = this.dataSet.saveData(this.name);
            if (data && !this.readOnly) {
                var table = this;
				this.columns.prepareLoad();
                isc.PRCService.save(this.getJndiName(), data, this.isInsertState(), function (responseData) {
                    table.onSaveSuccess(responseData, success);
                }, function (request, textStatus, errorThrown) {
                    table.responseErrors(request, textStatus, errorThrown, error);
                });
            }
        },

        saveData: function () {
            if (!this.isEmpty()) {
                var saveData = [];
                for (var i = 0; i < this.getPageCount(); i++) {
                    var row = this.getCurrentRow(i);
                    var rowData = row.saveData();
                    if (rowData)
                        saveData[saveData.length] = rowData;
                }
                return saveData;
            }
        },

        onSaveSuccess: function (responseData, success) {
            if (responseData) {
                if (this.isErrorResponse(responseData))
                    this.checkResponse(responseData);
                else {
                    this.responseInfoes(responseData);
                    this.dataSet.setData(this.name, responseData);
                    var result = this.checkResponse(responseData);
                    if (this.totalSize < this.startRow)
                        this.totalSize = this.startRow;
                    this.dataSet.afterSave(this.name);
                    if (result && success)
                        success(responseData);
                }
            }
        },

        afterSave: function () {
            if (!this.dataSet.hasErrors(this.name))
                this.setDataState(isc.Table.DataStates.Browse);
            this.fireEvent(isc.Table.Events.AfterSave);
        }
    },

    DefaultInvokeProperties: {
        invoke: function (jndiName, methodName, data, success, error) {
            var table = this;
            isc.PRCService.invoke(jndiName, methodName, data, function (responseData) {
                table.onInvokeSuccess(responseData, success);
            }, function (request, textStatus, errorThrown) {
                table.responseErrors(request, textStatus, errorThrown, error);
            });
        },

        onInvokeSuccess: function (responseData, success) {
            if (responseData) {
                this.responseInfoes(responseData);
                var result = this.checkResponse(responseData);
                if (result && success)
                    success(responseData);
            }
        }
    },

    DefaultParseProperties: {
        parseRow: function (data) {
            if (data) {
                var row = this.createDefaultRow();
                if (row.table == undefined)
                    isc.addProperties(row, {table: this}, isc.Table.DefaultRowProperties);
                row.parseData(data);
                if (data.errors)
                    row.errors = data.errors;
                return row;
            }
        },

        parseRows: function (data) {
            if (data && isc.isA.Array(data)) {
                this.rows = [];
                for (var i = 0; i < data.length; i++) {
                    var responseData = data[i];
                    if (responseData) {
                        var row = this.parseRow(responseData);
                        this.addRow(row);
                        if (responseData.dataState)
                            row.setDataState(responseData.dataState);
                    }
                }
            }
        }
    },

    DefaultNavigateProperties: {
        first: function () {
            this.setCurrentRow(0);
        },

        firstPage: function () {
            this.startRow = 1;
            this.refresh();
        },

        prior: function () {
            this.setCurrentRow(this.currentRowIndex - 1);
        },

        priorPage: function () {
            if (this.startRow > this.pageSize)
                this.startRow -= this.pageSize;
            this.refresh();
        },

        next: function () {
            this.setCurrentRow(this.currentRowIndex + 1);
        },

        nextPage: function () {
            var startRow = this.startRow - 1 + this.pageSize;
            if (startRow < this.totalSize)
                this.startRow = this.startRow + this.pageSize;
            this.refresh();
        },

        last: function () {
            this.setCurrentRow(this.getPageCount() - 1);
        },

        lastPage: function () {
            this.calculateStartRow();
            this.refresh();
        },

        refresh: function () {
            this.calculate();
            this.load();
            this.first();
        },

        calculateStartRow: function () {
            if (this.totalSize > this.pageSize) {
                var startRow = this.totalSize;
                var lastPage = this.totalSize % this.pageSize;
                if (lastPage > 0)
                    startRow -= lastPage;
                else
                    startRow -= this.pageSize;
                this.startRow = startRow + 1;
            }
            else
                this.startRow = 1;
        },

        setTotalSize: function (value) {
            this.totalSize = value;
            if (this.startRow > this.totalSize)
                this.calculateStartRow();
            this.calculate();
        },

        calculate: function () {
            if (this.totalSize == 0) {
                this.pageNum = 1;
                this.totalPageCount = 0;
                this.currentRowIndex = -1;
            }
            else {
                this.pageNum = Math.floor((this.startRow - 1) / this.pageSize) + 1;
                this.totalPageCount = Math.floor((this.totalSize - 1) / this.pageSize) + 1;
                this.currentRowIndex = 0;
            }
        },

        setPageNum: function (value) {
            var startRow = this.pageSize * (value - 1) + 1;
            if (startRow > 0 && startRow <= this.totalSize)
                this.startRow = startRow;
            this.refresh();
        },

        setPageSize: function (value) {
            this.pageSize = value;
            this.startRow = Math.floor((this.startRow - 1) / this.pageSize) * this.pageSize + 1;
            this.refresh();
        },

        getPageCount: function () {
            if (!this.rows)
                return 0;
            return this.rows.length;
        }
    },

    DefaultResponseProperties: {
        responseErrors: function (request, textStatus, errorThrown, error) {
            alert("status: " + request.status + "\r\n" + "textStatus: " + textStatus + "\r\n" + "errorThrown: " + errorThrown);
            if (error && typeof(error) == "function")
                error(responseData);
        },

        isErrorResponse: function (responseData) {
            return responseData.id && responseData.error;
        },

        checkResponse: function (responseData) {
            var errors = responseData.errors;
            if (errors) {
                if (!isc.isA.Array(errors))
                    errors = [errors];
            } else if (responseData.error)
                errors = [responseData];

            var errorText;
            if (errors && errors.length > 0) {
                for (var i = 0; i < errors.length; i++) {
                    var error = errors[i];
                    if (error.fieldName) {
                        var currentRow = this.getCurrentRow();
                        if (currentRow)
                            currentRow.addFieldErrors(error.fieldName, error.error);
                        continue;
                    }
                    if (!errorText)
                        errorText = "";
                    else
                        errorText += "\r\n";
                    if (error.id)
                        errorText += "(" + error.id + ")";
                    errorText += error.error;
                }
            }
            if (errorText)
                isc.warn(errorText);
            return !errors || errors.length == 0;
        },

        responseInfoes: function (responseData) {
            var infoes = responseData.infoes;
            if (infoes) {
                var infoText;
                if (isc.isA.Array(infoes)) {
                    if (infoes.length > 0) {
                        for (var i = 0; i < infoes.length; i++) {
                            if (!infoText)
                                infoText = "";
                            infoText += "(" + infoes[i].id + ")" + infoes[i].info + "<br>";
                        }
                    }
                } else
                    infoText = "(" + infoes.id + ")" + infoes.info;
                isc.say(infoText);
            } else if (responseData.id != undefined && responseData.info)
                isc.say("(" + responseData.id + ")" + responseData.info);
        }
    },

    DefaultPositionProperties: {
        setCurrentRow: function (value, fieldName) {
            if (value == undefined || value == null)
                value = this.currentRowIndex;

            var currentRowIndex = value;
            if (isc.isA.Number(value)) {
                if (value < 0)
                    currentRowIndex = 0;
                else if (value >= this.getPageCount())
                    currentRowIndex = this.getPageCount() - 1;
            } else
                currentRowIndex = this.indexOf(value);
            if (currentRowIndex >= 0 && currentRowIndex < this.getPageCount()) {
                this.currentRowIndex = currentRowIndex;
                this.fireEvent(isc.Table.Events.CurrentRowChanged, this.getCurrentRow());
                if (!fieldName)
                    fieldName = this.currentField;
                this.columns.setCurrentField(fieldName);
            }
        },

        indexOf: function (row, beginIndex) {
            if (!row || this.isEmpty())
                return;
            if (isc.isA.Number(row)) {
                if (row >= 0 && row < this.rows.length)
                    return row;
            } else {
                if (beginIndex == undefined || beginIndex == null)
                    beginIndex = 0;
                return this.rows.indexOf(row, beginIndex);
            }
        },

        getCurrentPosition: function () {
            return this.startRow + this.currentRowIndex;
        },

        getCurrentColumn: function () {
            return this.columns.getColumn(this.columns.getCurrentField());
        },

        getCurrentRow: function (rowIndex) {
            var row = null;
            if (rowIndex == undefined || rowIndex == null)
                rowIndex = this.currentRowIndex;
            if (rowIndex >= 0 && rowIndex < this.getPageCount())
                row = this.rows[rowIndex];
            if (row != null) {
                if (!row.table)
                    isc.addProperties(row, {table: this}, isc.Table.DefaultRowProperties);
            }
            return row;
        },

        setCurrent: function (fieldName, row) {
            var currentRow;
            if (row && isc.isA.Number(row))
                currentRow = this.getCurrentRow(row);
            else if (!row)
                currentRow = this.getCurrentRow();
            else
                currentRow = row;
            if (currentRow)
                this.setCurrentRow(currentRow, fieldName);
        }
    },

    DefaultInitProperties: {
        initialize: function () {
            this.initColumns();
            this.Super("initialize", arguments);
        },

        initColumns: function () {
            var primaryKeys = [];
            var fieldNames = [];
            for (var name in this.columns) {
                var column = this.columns[name];
                if (column.name && column.dataType) {
                    fieldNames[fieldNames.length] = column.name;
                    if (column.primaryKey)
                        primaryKeys[primaryKeys.length] = column;
                    isc.addProperties(column, {table: this}, isc.Table.DefaultColumnProperties);
                }
            }
            this.columns.primaryKeys = primaryKeys;
            this.columns.fieldNames = fieldNames;
            this.columns.length = fieldNames.length;
            isc.addProperties(this.columns, {table: this}, isc.Table.DefaultColumnsProperties);
            this.columns.initialize();
        }
    },

    DefaultValidateProperties: {
        validate: function () {
            this.fireEvent(isc.Table.Events.BeforeTableValidate);
            if (!this.isEmpty()) {
                for (var i = 0; i < this.getPageCount(); i++) {
                    var row = this.getCurrentRow(i);
                    if (row)
                        row.validate();
                }
                this.fireEvent(isc.Table.Events.AfterTableValidate);
            }
            return !this.hasErrors();
        }
    },

    DefaultDataStateProperties: {
        isInsertState: function () {
            return this.dataState == isc.Table.DataStates.Insert;
        },

        setDataState: function (dataState) {
            if (this.dataState != dataState) {
                this.fireEvent(isc.Table.Events.DataStateChanging, this.dataState, dataState);
                this.dataState = dataState;
                this.fireEvent(isc.Table.Events.DataStateChanged, this.dataState);
                this.dataSet.changeDataState(this);
            }
        }
    },

    DefaultSearchProperties: {
        find: function (filter) {
            if (!this.isEmpty() && filter) {
                var found = [];
                for (var i = 0; i < this.rows.length; i++) {
                    var row = this.rows[i];
                    if (row.isValid(filter))
                        found.push(row);
                }
                if (found.length > 0)
                    return found;
            }
        },

        locate: function () {
            if (!this.isEmpty() && this.columns.primaryKeys.length > 0) {
                var filterData = arguments;
                if (arguments.length == 1 && $.isArray(arguments[0]))
                    filterData = arguments[0];
                if (filterData.length > 0) {
                    var filter = {};
                    for (var i = 0; i < this.columns.primaryKeys.length && i < filterData.length; i++) {
                        var fieldName = this.columns.primaryKeys[i].name;
                        filter[fieldName] = filterData[i];
                    }
                    var result = this.find(filter);
                    if (result && result.length > 0) {
                        if (this.columns.primaryKeys.length == arguments.length)
                            return result[0];
                        return result;
                    }
                }
            }
        }
    }
});

isc.Table.addProperties({
    name: null,
    readOnly: false,
    dataState: isc.Table.DataStates.Browse,
    createAble: true,
    removeAble: true,
    updateAble: true,
    totalSize: 0,
    startRow: 1,
    currentRowIndex: -1,
    pageSize: 50,
    pageNum: 1,
    totalPageCount: 0
});

isc.Table.addMethods({
    initWidget: function () {
        this.addProperties(isc.Table.DefaultInitProperties);
        this.addProperties(isc.Table.DefaultCreateProperties);
        this.addProperties(isc.Table.DefaultParseProperties);
        this.addProperties(isc.Table.DefaultLoadProperties);
        this.addProperties(isc.Table.DefaultLocalDataProperties);
        this.addProperties(isc.Table.DefaultUpdateProperties);
        this.addProperties(isc.Table.DefaultSaveProperties);
        this.addProperties(isc.Table.DefaultInvokeProperties);
        this.addProperties(isc.Table.DefaultRemoveProperties);
        this.addProperties(isc.Table.DefaultNavigateProperties);
        this.addProperties(isc.Table.DefaultResponseProperties);
        this.addProperties(isc.Table.DefaultPositionProperties);
        this.addProperties(isc.Table.DefaultValidateProperties);
        this.addProperties(isc.Table.DefaultDataStateProperties);
        this.addProperties(isc.Table.DefaultSearchProperties);
        this.Super("initWidget", arguments);
    },

    isReadOnly: function () {
        if (this.parent)
            return this.parent.isReadOnly();
        if (this.dataState == isc.Table.DataStates.Insert) {
            if (!this.dataSet.existsPermission("create"))
                return true;
        }
        else if (!this.dataSet.existsPermission("save"))
            return true;
        return this.readOnly;
    },

    isCreateAble: function () {
        var result = this.createAble;
        if (result) {
            if (this.parent)
                result = !this.isReadOnly();
            else if (this.readOnly || !this.dataSet.existsPermission("create"))
                result = false;
        }
        return result;
    },

    isUpdateAble: function () {
        return !this.isReadOnly() && this.updateAble;
    },

    isRemoveAble: function () {
        var result = this.removeAble;
        if (result) {
            if (this.parent)
                result = !this.isReadOnly();
            else if (!this.dataSet.existsPermission("delete"))
                result = false;
        }
        return result;
    },

    setData: function (data) {
        if (!isc.isA.Array(data))
            data = [data];
        if (data != this.rows)
            this.parseRows(data);
        this.calculate();
        var foundRow;
        if (data.length > 1 && $.defined(this.previousPrimaryKeyValues))
            foundRow = this.locate(this.previousPrimaryKeyValues);
        this.setCurrentRow(foundRow);
    },

    hasChildren: function () {
        return this.childrenNames;
    },

    isCache: function () {
        if (this.cache != undefined && this.cache != null)
            return this.cache;
        return this.pageSize > 1;
    },

    getJndiName: function () {
        var jndiName = this.jndiName;
        if (!jndiName)
            jndiName = this.dataSet.service.module.jndiName;
        return jndiName;
    },

    isEmpty: function () {
        return this.getPageCount() == 0;
    },

    hasErrors: function () {
        if (!this.isEmpty()) {
            for (var i = 0; i < this.getPageCount(); i++) {
                var row = this.getCurrentRow(i);
                if (row && row.hasErrors())
                    return true;
            }
        }
        return false;
    },

    isChanging: function (dataState) {
        if (!dataState)
            dataState = this.dataState;
        return dataState == isc.Table.DataStates.Insert || dataState == isc.Table.DataStates.Modified;
    },

    setMode: function (mode) {
        if (mode) {
            if (mode == isc.Table.ModeTypes.Query) {
                this.readOnly = true;
                this.cache = false;
            } else if (mode == isc.Table.ModeTypes.MasterEdit) {
                this.readOnly = false;
                this.pageSize = 1;
                delete this.cache;
            } else if (mode == isc.Table.ModeTypes.DetailEdit) {
                this.readOnly = false;
                this.cache = true;
            } else if (mode == isc.Table.ModeTypes.Temp) {
                this.pageSize = 1;
                this.cache = true;
            }
            this.mode = mode;
            this.columns.initialize();
        }
    }
});

/*
 * DangCat widgets DataLoader
 */
isc.ClassFactory.defineClass("DataLoader", "Class");
isc.DataLoader.addClassProperties({
    DefaultIndexProperties: {
        locate: function () {
            if (arguments.length == this.fieldNames.length) {
                var indexData = this;
                for (var i = 0; i < arguments.length; i++) {
                    var data = arguments[i];
                    indexData = indexData[data + ""];
                    if (!indexData)
                        break;
                }
                if (indexData) {
                    if (indexData.data) {
                        if (isc.isA.Array(indexData.data))
                            return indexData;
                    }
                }
            }
        },

        getValueMap: function () {
            if (this.idField && this.nameField) {
                var indexData = this.locate.apply(this, arguments);
                if (indexData) {
                    if (indexData.valueMap)
                        return indexData.valueMap;

                    var valueMap = {};
                    for (var i = 0; i < indexData.data.length; i++) {
                        var data = indexData.data[i];
                        valueMap[data[this.idField]] = data[this.nameField];
                    }
                    indexData.valueMap = valueMap;
                    return valueMap;
                }
            }
        }
    }
});
isc.DataLoader.addMethods({
    getDataSource: function (fields) {
        if (this.dataSource == undefined) {
            this.dataSource = isc.DataSource.create({
                ID: this.name,
                fields: fields,
                dataFormat: "json",
                dataURL: this.dataURL
            });
        }
        return this.dataSource;
    },

    isLocaleLoad: function () {
        return this.dataURL && this.getDataSource().dataURL;
    },

    load: function (success) {
        var dataLoader = this;
        if (this.isLocaleLoad()) {
            this.getDataSource().fetchData(null, function (dsResponse, responseData, dsRequest) {
                if (!responseData)
                    responseData = [];
                dataLoader.setData(responseData);
                success(responseData);
            });
        } else {
            isc.PRCService.query(this.jndiName, undefined, function (responseData) {
                if (!responseData)
                    responseData = [];
                dataLoader.setData(responseData);
                success(responseData);
            });
        }
    },

    setData: function (data) {
        this.data = data;
    },

    createIndex: function () {
        if (this.data && arguments.length > 0) {
            var fieldNames = [];
            for (var k = 0; k < arguments.length; k++) {
                var fieldName = arguments[k];
                if (fieldName)
                    fieldNames.push(fieldName);
            }

            var indexRoot = isc.addProperties({
                fieldNames: fieldNames,
                idField: this.idField,
                nameField: this.nameField
            }, isc.DataLoader.DefaultIndexProperties);
            for (var i = 0; i < this.data.length; i++) {
                var data = this.data[i];
                var indexData = indexRoot;
                for (var k = 0; k < fieldNames.length; k++) {
                    var fieldName = fieldNames[k];
                    var fieldData = data[fieldName] + "";
                    if (!indexData[fieldData])
                        indexData[fieldData] = {};
                    indexData = indexData[fieldData];
                }
                if (!indexData.data)
                    indexData.data = [data];
                else
                    indexData.data.push(data);
            }
            return indexRoot;
        }
    }
});
