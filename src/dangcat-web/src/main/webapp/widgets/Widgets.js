/*
 PRC Service
 */
isc.PRCService = {
    getBaseUrl: function (jndiName) {
        return isc.ApplicationContext.baseUrl + "/" + jndiName;
    },

    remove: function (jndiName, data, success, error) {
        var url = this.getBaseUrl(jndiName);
        if (isc.isA.Number(data))
            return this.ajaxInvoke("DELETE", url + "/" + data, undefined, success, error);
        return this.ajaxInvoke("DELETE", url, data, success, error);
    },

    query: function (jndiName, filter, success, error) {
        var url = this.getBaseUrl(jndiName);
        return this.ajaxInvoke("GET", url, filter, success, error);
    },

    save: function (jndiName, data, isInsertState, success, error) {
        var url = this.getBaseUrl(jndiName);
        if (isInsertState)
            url += "/create";
        return this.ajaxInvoke("PUT", url, data, success, error);
    },

    select: function (jndiName, filter, success, error) {
        var url = this.getBaseUrl(jndiName);
        var parts = jndiName.split("/");
        if (parts.length <= 2)
            url += "/select"
        return this.ajaxInvoke("GET", url, filter, success, error);
    },

    view: function (jndiName, id, success, error) {
        var data;
        var url = this.getBaseUrl(jndiName);
        if (id != undefined && id != null) {
            if (isc.isA.Number(id))
                url += "/" + id;
            else if (isc.isA.Object(id))
                data = id;
        }
        return  this.ajaxInvoke("GET", url, data, success, error);
    },

    createNew: function (jndiName, success, error) {
        var url = this.getBaseUrl(jndiName);
        url += "/view";
        return  this.ajaxInvoke("GET", url, undefined, success, error);
    },

    getMethodURL: function (jndiName, methodName) {
        var url = this.getBaseUrl(jndiName);
        if (methodName)
            url += "/" + methodName;
        return url;
    },

    invoke: function (jndiName, methodName, data, success, error) {
        var url = this.getMethodURL(jndiName, methodName);
        return this.ajaxInvoke("POST", url, data, success, error);
    },

    ajaxInvoke: function (type, url, data, success, error) {
        if (data) {
            try {
                if (type == "GET") {
                    if (typeof (data) == "string")
                        data = $.evalJSON(data);
                }
                else if (typeof (data) == "object")
                    data = $.toJSON(data);
            } catch (e) {
                isc.warn("json 格式不正确：" + e.message);
                return;
            }
        }

        if (isc.ApplicationContext.isLocale() == true) {
            var title = type + ":" + url;
            if (!data)
                isc.warn(title);
            else {
                isc.Dialog.create({
                    title: title,
                    message: data,
                    buttons: [
                        isc.Dialog.OK
                    ]
                });
            }
            if (success) {
                if (typeof (data) == "string")
                    data = $.evalJSON(data);
                success(data ? data : {});
            }
        }
        else {
            $.ajax({
                type: type,
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                url: url,
                cache: false,
                data: data,
                success: function (data) {
                    if (isc.ApplicationContext.validate(data)) {
                        if (success)
                            success(data);
                    }
                },
                error: function (request, textStatus, errorThrown) {
                    if (error)
                        error(request, textStatus, errorThrown);
                }
            });
        }
    }
};

/*
 ApplicationContext
 */
isc.ApplicationContext = {
    locale: "zh_CN",
    currentSkin: "Enterprise",
    servicePrincipal: null,
    baseUrl: "REST/",
    moduleManager: null,
    isValidate: false,

    cookies: {
        queryViewFormVisible: false
    },

    i18nFiles: [
        "./isomorphic/locales/frameworkMessages",
        "./i18n/Widgets"
    ],

    jsFiles: [
    ],

    cssFiles: [
        "css/MainLayout.css",
        "css/widgets.css"
    ],

    create: function () {
        var jsFiles = isc.ApplicationContext.jsFiles;
        jsFiles[jsFiles.length] = "./scene/SceneContext.js";
        var i18nFiles = isc.ApplicationContext.i18nFiles;
        i18nFiles[i18nFiles.length] = "./scene/SceneContext";

        if (this.isLocale()) {
            isc.ApplicationContext.baseUrl = "./Modules";
            isc.ApplicationContext.sessionId = "SESSIONID";
            this.loadMainLayout();
        } else {
            $.ajax({
                type: "GET",
                url: "systemInfo",
                cache: false,
                success: function (data) {
                    isc.ApplicationContext.systemInfo = data;
                    isc.addProperties(isc.ApplicationContext, isc.ApplicationContext.systemInfo);
                    if (isc.ApplicationContext.servicePrincipal) {
                        isc.PRCService.invoke("System/SystemInfo", "loadMenus", null, function (data) {
                            isc.ApplicationContext.remoteMenus = data;
                            isc.ApplicationContext.loadMainLayout();
                        });
                    }
                    else
                        isc.ApplicationContext.loadMainLayout();
                }
            });
        }
    },

    isLocale: function () {
        return window.location.protocol == "file:";
    },

    converti18nFiles: function (i18nFiles) {
        var convertFiles = [];
        for (var i = 0; i < i18nFiles.length; i++) {
            if ($.defined(i18nFiles[i]))
                convertFiles.push(i18nFiles[i] + "_" + this.locale + ".properties");
        }
        return convertFiles;
    },

    loadMainLayout: function () {
        var applicationContext = isc.ApplicationContext;
        var i18nFiles = this.converti18nFiles(applicationContext.i18nFiles);

        applicationContext.loadJSFiles(i18nFiles, function () {
            applicationContext.loadJSFiles(applicationContext.jsFiles, function () {
                var i18nFilesExt = applicationContext.converti18nFiles(applicationContext.i18nFilesExt);
                applicationContext.loadJSFiles(i18nFilesExt, function () {
                    var themeFile = "./isomorphic/skins/" + isc.ApplicationContext.currentSkin + "/theme.js";
                    var jsFilesExt = $.concatArray(applicationContext.jsFilesExt, themeFile);
                    applicationContext.loadJSFiles(jsFilesExt, function () {
                        isc.FileLoader.loadSkin(applicationContext.currentSkin, function () {
                            var cssFiles = $.concatArray(applicationContext.cssFiles, applicationContext.cssFilesExt);
                            applicationContext.loadCssFiles(cssFiles, function () {
                                isc.addProperties(isc.ApplicationContext, isc.ApplicationContext.systemInfo);
                                document.title = applicationContext.projectTitle + applicationContext.masterTitle + applicationContext.version;
                                delete isc.ApplicationContext.systemInfo;
                                if ($.isFunction(isc.ApplicationContext.beforeCreateMainLayout))
                                    isc.ApplicationContext.beforeCreateMainLayout();
                                applicationContext.createMainLayout();
                                if ($.isFunction(isc.ApplicationContext.afterCreateMainLayout))
                                    isc.ApplicationContext.afterCreateMainLayout();
                            });
                        });
                    });
                });
            });
        });
    },

    loadJSFiles: function (jsFiles, callback) {
        if ($.defined(jsFiles) && jsFiles.length > 0) {
            isc.FileLoader.loadJSFiles(jsFiles, function () {
                isc.ApplicationContext.invokeCallback(callback);
            });
        }
        else
            isc.ApplicationContext.invokeCallback(callback);
    },

    loadCssFiles: function (cssFiles, callback) {
        if ($.defined(cssFiles) && cssFiles.length > 0) {
            var convertFiles = [];
            for (var i = 0; i < cssFiles.length; i++)
                convertFiles.push("./isomorphic/skins/" + isc.ApplicationContext.currentSkin + "/" + cssFiles[i]);
            isc.FileLoader.loadCSSFiles(convertFiles, function () {
                isc.ApplicationContext.invokeCallback(callback);
            });
        } else
            isc.ApplicationContext.invokeCallback(callback);
    },

    invokeCallback: function (callback) {
        if (callback != null) {
            if (isc.isA.String(callback))
                isc.evalSA(callback);
            else
                callback();
        }
    },

    loadMenus: function () {
        if ($.defined(this.remoteMenus)) {
            this.menus = this.remoteMenus;
            delete this.remoteMenus;
        }
        return this.menus;
    },

    validate: function (data) {
        if (typeof (data) == "string" && data.indexOf("\"id\":\"405\"") != -1)
            data = $.evalJSON(data);
        if (data && data.id && data.id == 405 && data.error) {
            if (this.isValidate) {
                this.isValidate = false;
                alert(data.error);
                this.logout();
            }
            return false;
        }
        return true;
    },

    showProcess: function (message) {
        isc.FL.showThrobber(message, null, "[SKIN]/loading.gif");
    },

    hideProcess: function () {
        isc.FL.hideThrobber();
    },

    createMainLayout: function () {
        if (this.mainLayout) {
            this.mainLayout.destroy();
            delete this.mainLayout;
        }
        if (this.servicePrincipal)
            this.mainLayout = isc.MainLayout.create();
        else
            this.mainLayout = isc.LoginLayout.create();
        this.hideProcess();
    },

    login: function () {
        this.showProcess(isc.i18nInfo.login);
        this.loginSystem(function () {
            isc.ApplicationContext.isValidate = true;
            isc.ApplicationContext.createMainLayout();
        });
    },

    loginSystem: function (callback) {
        if (this.isLocale()) {
            isc.ApplicationContext.servicePrincipal = servicePrincipal;
            if (callback)
                callback();
        }
        else {
            isc.PRCService.invoke("System/SystemInfo", "loadSystemInfo", null, function (data) {
                isc.addProperties(isc.ApplicationContext, data);
                isc.PRCService.invoke("System/SystemInfo", "loadMenus", null, function (data) {
                    isc.ApplicationContext.remoteMenus = data;
                    if (callback)
                        callback();
                });
            });
        }
    },

    logout: function () {
        if (this.isLocale())
            this.logoutSystem();
        else {
            $.ajax({
                type: "GET",
                url: "logout",
                cache: false,
                success: function () {
                    isc.ApplicationContext.logoutSystem();
                }
            });
        }
    },

    logoutSystem: function () {
        this.servicePrincipal = null;
        $.ajax({
            type: "GET",
            url: "systemInfo",
            cache: false,
            success: function (data) {
                isc.ApplicationContext.sessionId = data["sessionId"];
                isc.ApplicationContext.loadMainLayout();

            }
        })
    },

    isEnabled: function () {
        if (!this.isLocale() && $.defined(this.enabled))
            return this.enabled;
        return true;
    }
};

var pageSizeValueMap = [10, 20, 30, 40, 50, 100, 200];

if ($.defined(isc.DynamicForm)) {
    var dynamicFormPrototype = isc.DynamicForm.getPrototype();
    var _destroyItems = function (items) {
        if (this._pickListCache) {
            for (var name in this._pickListCache)
                this._pickListCache[name].destroy();
            delete this._pickListCache;
        }
        this.destroyItems(items);
    };
    if ($.defined(dynamicFormPrototype.$10k) && !$.defined(dynamicFormPrototype.destroyItems)) {
        dynamicFormPrototype.destroyItems = dynamicFormPrototype.$10k;
        dynamicFormPrototype.$10k = _destroyItems;
    }
    else if ($.defined(dynamicFormPrototype._destroyItems) && !$.defined(dynamicFormPrototype.destroyItems)) {
        dynamicFormPrototype.destroyItems = dynamicFormPrototype._destroyItems;
        dynamicFormPrototype._destroyItems = _destroyItems;
    }
}

if ($.defined(isc.ListGrid)) {
    var listGridPrototype = isc.ListGrid.getPrototype();
    var _saveLocally = function (editInfo, saveCallback) {
        this.saveLocally(editInfo, saveCallback);
    };
    if ($.defined(listGridPrototype.$336) && !$.defined(listGridPrototype.saveLocally)) {
        listGridPrototype.saveLocally = listGridPrototype.$336;
        listGridPrototype.$336 = _saveLocally;
    }
    else if ($.defined(listGridPrototype._saveLocally) && !$.defined(listGridPrototype.saveLocally)) {
        listGridPrototype.saveLocally = listGridPrototype._saveLocally;
        listGridPrototype._saveLocally = _saveLocally;
    }
}

if ($.defined(isc.MenuBar)) {
    var getButtonProperties = function (menu, index) {
        var properties = this.getButtonProperties(arguments);
        properties.autoFit = menu.autoFit;
        return properties;
    }
    var menuBarPrototype = isc.MenuBar.getPrototype();
    if ($.defined(menuBarPrototype.$36o)) {
        menuBarPrototype.getButtonProperties = menuBarPrototype.$36o;
        menuBarPrototype.$36o = getButtonProperties;
    }
    else if ($.defined(menuBarPrototype._getButtonProperties)) {
        menuBarPrototype.getButtonProperties = menuBarPrototype._getButtonProperties;
        menuBarPrototype._getButtonProperties = getButtonProperties;
    }
}
if ($.defined(isc.ViewLoader)) {
    var loadViewReply = function (rpcRequest, rpcResponse, data) {
        if (!isc.ApplicationContext.validate(data))
            return;
        if (data == undefined || data == null)
            return;
        if (data.startsWith("{\"error\":")) {
            data = $.evalJSON(data);
            isc.warn(data.error);
            return;
        }
        return this.loadViewReply(rpcRequest, rpcResponse, data);
    }
    var viewLoaderPrototype = isc.ViewLoader.getPrototype();
    if ($.defined(viewLoaderPrototype.$40p)) {
        viewLoaderPrototype.loadViewReply = viewLoaderPrototype.$40p;
        viewLoaderPrototype.$40p = loadViewReply;
    }
    else if ($.defined(viewLoaderPrototype._loadViewReply)) {
        viewLoaderPrototype.loadViewReply = viewLoaderPrototype._loadViewReply;
        viewLoaderPrototype._loadViewReply = loadViewReply
    }
}

if ($.defined(isc.ComboBoxItem)) {
    var comboBoxItemPrototype = isc.ComboBoxItem.getPrototype();
    if ($.defined(comboBoxItemPrototype.getPickListFilterCriteria)) {
        comboBoxItemPrototype._getPickListFilterCriteria = comboBoxItemPrototype.getPickListFilterCriteria;
        comboBoxItemPrototype.getPickListFilterCriteria = function () {
            if (this.pickList.filterWithValue == false)
                return {};
            return this._getPickListFilterCriteria();
        }
    }
    var overrideFilterPickList = function () {
        if (!$.defined(this.pickList) || !this.hasFocus)
            return;

        if (!$.defined(this.isEntryTooShortToFilter) || !this.isEntryTooShortToFilter()) {
            var data = this.pickList.originalData || this.pickList.data;
            if (data && isc.ResultTree && isc.isA.ResultTree(data)) {
                this._showOnFilter = true;
                this.$19i = true;
                this.filterComplete();
                return;
            }
        }
        this.filterWithValue = true;
        return this.overrideFilterPickList(arguments);
    };
    if ($.defined(comboBoxItemPrototype.$19p)) {
        comboBoxItemPrototype.overrideFilterPickList = comboBoxItemPrototype.$19p;
        comboBoxItemPrototype.$19p = overrideFilterPickList;
    } else if ($.defined(comboBoxItemPrototype._filterPickList)) {
        comboBoxItemPrototype.overrideFilterPickList = comboBoxItemPrototype._filterPickList;
        comboBoxItemPrototype._filterPickList = overrideFilterPickList;
    }

    var fireTabCompletion = function () {
        if (this.canBeUnknownValue)
            return;
        return this.fireTabCompletion(arguments);
    };
    if ($.defined(comboBoxItemPrototype.$82r)) {
        comboBoxItemPrototype.fireTabCompletion = comboBoxItemPrototype.$82r;
        comboBoxItemPrototype.$82r = fireTabCompletion;
    } else if ($.defined(comboBoxItemPrototype._fireTabCompletion)) {
        comboBoxItemPrototype.fireTabCompletion = comboBoxItemPrototype._fireTabCompletion;
        comboBoxItemPrototype._fireTabCompletion = fireTabCompletion;
    }
    isc.ComboBoxItem.addMethods({
        _pickListShown: function () {
            return this.pickListShown(arguments);
        },
        _pickListHidden: function () {
            this.pickListHidden(arguments);
        },
        keyDown: function () {
            this.Super("keyDown", arguments);
            var keyName = isc.EH.lastEvent.keyName;
            if (keyName == "Tab") {
                if (this.pickList)
                    this.pickList.hide();
            }
        }
    });
}

if ($.defined(isc.FormItem)) {
    var formItemPrototype = isc.FormItem.getPrototype();
    if (!$.defined(formItemPrototype._getTitleHTML) && $.defined(formItemPrototype.getTitleHTML)) {
        formItemPrototype._getTitleHTML = comboBoxItemPrototype.getTitleHTML;
        isc.FormItem.addMethods({
            getTitleHTML: function () {
                var title = this.getTitle();
                if ($.defined(title))
                    return this._getTitleHTML(arguments);
                return null;
            }
        });
    }
}
if ($.defined(isc.SelectItem)) {
    var selectItemPrototype = isc.SelectItem.getPrototype();
    if ($.defined(selectItemPrototype.$19j) && !$.defined(selectItemPrototype._pickListShown)) {
        isc.SelectItem.addMethods({
            _pickListShown: function () {
                return this.$19j(arguments);
            }
        });
    }

    if ($.defined(selectItemPrototype.$19h) && !$.defined(selectItemPrototype._pickListHidden)) {
        isc.SelectItem.addMethods({
            _pickListHidden: function () {
                return this.$19h(arguments);
            }
        });
    }
}

/*
 TimerEx
 */
isc.ClassFactory.defineClass("TimerEx", "Class");
isc.TimerEx.addProperties({
    valid: true
});

isc.TimerEx.addMethods({
    setInterval: function (methodName, interval, delay, args) {
        if (this.timers == undefined)
            this.timers = {};
        var timer = this.timers[methodName];
        if (!$.defined(timer)) {
            timer = {
                interval: interval,
                dateTime: new Date(),
                args: args
            };
            this.timers[methodName] = timer;
            timer.timerEvent = isc.Timer.setTimeout({
                target: this,
                methodName: "_intervalCall",
                args: [methodName]
            }, delay ? delay : interval);
        } else {
            timer.interval = interval;
            timer.delay = delay;
            timer.args = args;
        }
    },

    destroy: function () {
        this.valid = false;
        if ($.defined(this.timers)) {
            for (var methodName in this.timers)
                this.clear(methodName);
        }
    },

    clear: function (methodName) {
        var timer = this.timers[methodName];
        if ($.defined(timer)) {
            isc.Timer.clear(timer.timerEvent);
            delete this.timers[methodName];
        }
    },

    _intervalCall: function (methodName) {
        var result = false;
        var timer = this.timers[methodName];
        if (timer && this.owner[methodName] && this.valid)
            result = this.owner[methodName].apply(this.owner, timer.args);
        if (result)
            timer.timerEvent = isc.Timer.setTimeout({
                target: this,
                methodName: "_intervalCall",
                args: [methodName]
            }, timer.interval);
        else
            this.clear(methodName);
    }
});

/*
 Service
 */
isc.ClassFactory.defineClass("Service", "Class");
isc.Service.addClassProperties({
    DefaultComponentProperties: {
        extendProperties: function () {
            var overrideProperties = this["overrideProperties"];
            this.extendObjectProperties(overrideProperties, this);
        },

        extendObjectProperties: function (overrideProperties, instance) {
            if (overrideProperties) {
                for (var propertyName in overrideProperties) {
                    var overrideProperty = overrideProperties[propertyName];
                    var propertyInstance = instance[propertyName];
                    if (propertyInstance && isc.isA.Object(propertyInstance)) {
                        if (isc.isA.Array(overrideProperty)) {
                            if (isc.isA.Array(propertyInstance))
                                this.extendArrayProperties(overrideProperty, propertyInstance);
                        }
                        else if (isc.isA.Object(overrideProperty))
                            this.extendObjectProperties(overrideProperty, propertyInstance);
                    }
                    else {
                        if (isc.isA.Object(overrideProperty))
                            instance[propertyName] = isc.addProperties({}, overrideProperty);
                        else
                            instance[propertyName] = overrideProperty;
                    }
                }
            }
        },

        extendArrayProperties: function (overrideProperties, instanceArray) {
            var addInstance = [];
            for (var i = 0; i < overrideProperties.length; i++) {
                var overrideProperty = overrideProperties[i];
                var foundInstance = this.findArrayInstance(instanceArray, overrideProperty);
                if (foundInstance)
                    this.extendObjectProperties(overrideProperty, foundInstance);
                else
                    addInstance[addInstance.length] = isc.addProperties({}, overrideProperty);
            }
            for (var i = 0; i < addInstance.length; i++)
                instanceArray[instanceArray.length] = addInstance[i];
        },

        findArrayInstance: function (instanceArray, overrideProperty) {
            var keyNames = ["id", "Id", "ID", "name", "Name"];
            for (var j = 0; j < instanceArray.length; j++) {
                var instance = instanceArray[j];
                for (var k = 0; k < keyNames.length; k++) {
                    var keyName = keyNames[k];
                    if (overrideProperty[keyName] != undefined && overrideProperty[keyName] == instance[keyName])
                        return instance;
                }
            }
        },

        bindListener: function (component) {
            if (component) {
                if (component.getBindListeners) {
                    var listeners = component.getBindListeners();
                    if (listeners) {
                        if (!isc.isA.Array(listeners))
                            listeners = [listeners];
                        for (var i = 0; i < listeners.length; i++)
                            this.bindListener(listeners[i]);
                    }
                } else {
                    if (this.bindListeners)
                        this.bindListeners.push(component);
                    else
                        this.bindListeners = [component];
                    if (component.name && this[component.name] == undefined)
                        this[component.name] = component;
                }
            }
        },

        fireEvent: function (eventName) {
            if (this.bindListeners) {
                var sender = arguments[1];
                var eventNames = [ eventName.substring(0, 1).toLowerCase() + eventName.substring(1)];
                if (this.name && sender != this) {
                    if (sender && sender.name)
                        eventNames.push(this.name + "_" + sender.name + "_" + eventName);
                    eventNames.push(this.name + "_" + eventName);
                }
                if (this.getClassName != undefined && typeof(this.getClassName) == "function") {
                    var className = this.getClassName().substring(0, 1).toLowerCase() + this.getClassName().substring(1);
                    if (className != this.name) {
                        if (sender && sender.name && sender != this)
                            eventNames[eventNames.length] = className + "_" + sender.name + "_" + eventName;
                        eventNames[eventNames.length] = className + "_" + eventName;
                    }
                }

                var params = [this];
                for (var k = 1; k < arguments.length; k++)
                    params.push(arguments[k]);

                var invokeParams = {
                    eventNames: eventNames,
                    params: params,
                    foundEvent: null,
                    lastResult: null
                };
                for (var i = 0; i < this.bindListeners.length; i++) {
                    if (this.invokeEvent(this.bindListeners[i], invokeParams) == false)
                        return false;
                }
                if (invokeParams.foundEvent != null)
                    return invokeParams.lastResult;
            }
        },

        hasPermission: function (component) {
            if (this.service)
                return this.service.checkPermission(component);
            return true;
        },

        invokeEvent: function (listener, invokeParams) {
            for (var i = 0; i < invokeParams.eventNames.length; i++) {
                var event = listener[invokeParams.eventNames[i]];
                if (event != undefined && event != null) {
                    var result = event.apply(listener, invokeParams.params);
                    if (result == false)
                        return result;
                    invokeParams.foundEvent = event;
                    if (result != undefined)
                        invokeParams.lastResult = result;
                }
            }
        }
    }
});

isc.Service.addMethods({
    initialize: function () {
        this.Super("initialize", arguments);
        // 绑定事件。
        isc.addProperties(this, isc.Service.DefaultComponentProperties);
        // 产生数据源
        this.createDataSet();
    },

    getMethodURL: function (methodName) {
        return isc.PRCService.getMethodURL(this.module.jndiName, methodName);
    },

    invoke: function (methodName, data, success, error) {
        isc.PRCService.invoke(this.module.jndiName, methodName, data, function (responseData) {
            if (success)
                success(responseData, success);
            L
        }, function (request, textStatus, errorThrown) {
            if (error)
                error(request, textStatus, errorThrown, error);
        });
    },

    createDataSet: function () {
        if (this.owner.dataSetProperties) {
            if (this.dataSet == undefined || this.dataSet == null) {
                this.dataSet = isc.DataSet.create({}, isc.Service.DefaultComponentProperties, {
                    service: this,
                    dataSetProperties: this.owner.dataSetProperties
                });
                this.dataSet.initialize();
            }
        }
    },

    initComponent: function () {
        if (this.components) {
            for (var i = 0; i < this.components.length; i++) {
                var component = this.components[i];
                if (component.extendProperties != undefined && component.extendProperties != null)
                    component.extendProperties();
                component.service = this;
                component.bindListener(this);
                if (component.tableName) {
                    var table = this.dataSet[component.tableName];
                    if (table) {
                        table.bindListener(component);
                        component.getBindTable = function () {
                            return this.service.dataSet[this.tableName];
                        }
                    }
                }
                if (component.initialize)
                    component.initialize();
            }
        }
    },

    addComponent: function (name, component) {
        if (this.components == undefined)
            this.components = [component];
        else
            this.components[this.components.length] = component;
        if (name != undefined)
            this[name] = component;
    },

    createComponent: function (classType, properties, name) {
        var component = this[name];
        if (component == undefined) {
            var componentProperties = {
                service: this
            };
            if (name)
                componentProperties.name = name;
            isc.addProperties(componentProperties, isc.Service.DefaultComponentProperties, properties);
            var overrideProperties = this.owner[componentProperties.name + "Properties"];
            if (componentProperties.name != undefined && overrideProperties != undefined)
                isc.addProperties(componentProperties, overrideProperties);
            component = classType.create(componentProperties);
            if (name != "service")
                this.addComponent(name, component);
        }
        return component;
    },

    checkPermission: function (component) {
        if (component == undefined || component == null)
            return false;

        var ownerPermissions = this.owner.permissions;
        if (ownerPermissions == undefined || ownerPermissions == null || component.permission == undefined || component.permission == null)
            return true;

        var permissions = component.permission;
        if (!isc.isA.Array(permissions))
            permissions = [permissions];
        for (var i = 0; i < permissions.length; i++) {
            if (ownerPermissions.indexOf(permissions[i]) != -1)
                return true;
        }
        return false;
    },

    existsPermission: function (permissions) {
        var ownerPermissions = this.owner.permissions;
        if (ownerPermissions == undefined || ownerPermissions == null || permissions == undefined || permissions == null)
            return true;

        if (!isc.isA.Array(permissions))
            permissions = [permissions];

        for (var i = 0; i < permissions.length; i++) {
            if (ownerPermissions.indexOf(permissions[i]) != -1)
                return true;
        }
        return false;
    },

    setInterval: function (methodName, interval, delay, args) {
        if (!$.defined(this.timers))
            this.timers = isc.TimerEx.create({owner: this});
        this.timers.setInterval(methodName, interval, delay, args);
    },

    clearTimer: function (methodName) {
        if ($.defined(this.timers))
            this.timers.clear(methodName);
    },

    destroy: function () {
        if ($.defined(this.timers)) {
            this.timers.destroy();
            delete this.timers;
        }
        if (this.components) {
            for (var i = 0; i < this.components.length; i++)
                this.components[i].destroy();
        }
        if (this.dataSet)
            this.dataSet.destroy();
        this.Super("destroy", arguments);
    }
});

/*
 * Widgets StatusBar
 */
isc.ClassFactory.defineClass("StatusBar", "HLayout");

isc.StatusBar.addClassProperties({
    DefaultItemProperties: {
        align: "left",
        className: "statusBarContent",
        layoutLeftMargin: 2,
        width: "100%",
        wrap: false,
        valign: "center",
        contents: "&nbsp;"
    },

    DefaultFormProperties: {
    },

    DefaultFieldProperties: {
        wrapTitle: false
    }
});

isc.StatusBar.addProperties({
    autoDraw: false,
    styleName: "statusBar",
    width: "100%",
    height: 25,
    overflow: "hidden",
    padding: 1,
    membersMargin: 1,
    autoFit: true,
    defaultItemProperties: {
        height: 24
    },
    defaultFormProperties: {
        border: "0px solid #D3D3D3;"
    },
    defaultFieldProperties: {
        height: 20
    },
    items: [],
    data: []
});

isc.StatusBar.addMethods({
    initialize: function () {
        if (this.data.length > 0) {
            for (var i = 0; i < this.data.length; i++) {
                var item = this.data[i];
                if (item.type == undefined || item.type == "label")
                    item = isc.Label.create({}, isc.StatusBar.DefaultItemProperties, this.defaultItemProperties, item);
                else if (item.type == "form") {
                    for (var j = 0; j < item.fields.length; j++) {
                        var field = item.fields[j];
                        if (field.title == undefined || field.title == null) {
                            var title = this.getTitle(field.name);
                            if (title != undefined && title != null)
                                field.title = title;
                        }
                        isc.addProperties(field, isc.StatusBar.DefaultFieldProperties, this.defaultFieldProperties);
                    }
                    item = isc.DynamicForm.create({}, isc.StatusBar.DefaultItemProperties, isc.StatusBar.DefaultFormProperties, this.defaultFormProperties, item);
                }
                if (item != null) {
                    this[item.name] = item;
                    this.addMembers(item);
                }
            }
        }
        else if (this.items.length > 0) {
            for (var i = 0; i < this.items.length; i++) {
                this[item.name] = item;
                this.addMembers(this.items[i]);
            }
        }
    },

    getTitle: function (name) {
        if (isc.i18nStatus[name])
            return isc.i18nStatus[name];
    }
});

/*
 * Widgets ToolStripBar
 */
isc.ClassFactory.defineClass("ToolStripBar", "ToolStrip");
isc.ToolStripBar.addClassProperties({
    Types: {
        menu: "ToolStripMenuButton",
        form: "DynamicForm",
        button: "ToolStripButton",
        selector: "selector",
        separator: "ToolStripSeparator",
        resizer: "ToolStripResizer"
    },

    MenuButtonDefaultProperties: {
        showMenuButtonImage: false,
        iconOrientation: "left",
        autoFit: true,
        iconSize: 16,
        iconHeight: 16,
        iconWidth: 16
    },

    FormDefaultProperties: {
        border: "0px solid #D3D3D3;"
    },

    selectorFieldDefaultProperties: {
        wrapTitle: false,
        showTitle: false,
        width: "*",
        changed: function (form, item, value) {
            form.toolBar.itemChanged(item, value);
        }
    }
});

isc.ToolStripBar.addProperties({
    autoDraw: false,
    service: null,
    width: "100%",
    overflow: "hidden",
    iconSize: 16,
    layoutMargin: 3,
    defaultHide: null,
    defaultDisabled: null,
    items: null,
    data: null,
    menu: null
});

isc.ToolStripBar.addMethods({
    initWidget: function () {
        if (this.data == undefined || this.data == null) {
            if (this.DefaultDataProperties != undefined)
                this.data = isc.clone(this.DefaultDataProperties);
        }
        if (this.appendDataProperties)
            this.data = this.data.concat(this.appendDataProperties);
        this.Super("initWidget", arguments);
    },

    checkPermissions: function (data) {
        if (data) {
            if (isc.isA.Array(data)) {
                var array = [];
                for (var i = 0; i < data.length; i++) {
                    var item = this.checkPermissions(data[i]);
                    if (item != undefined && item != null) {
                        if (array.length == 0 || array[array.length - 1] != item)
                            array.push(item);
                    }
                }
                if (array.length > 0)
                    return array;
            } else if (this.hasPermission(data)) {
                if (data.type && data.type.toLowerCase() == "menu") {
                    var menuData = this[data.name + "MenuData"];
                    if (menuData)
                        data.data = menuData;
                    data.data = this.checkPermissions(data.data);
                    if (data.data && data.data.length > 0)
                        return data;
                } else if (data.submenu) {
                    data.submenu = this.checkPermissions(data.submenu);
                    if (data.submenu && data.submenu.length > 0)
                        return data;
                } else
                    return data;
            }
        }
    },

    initialize: function () {
        this.data = this.checkPermissions(this.data);
        this.addMembers("separator");
        if (this.data != null && this.data.length > 0) {
            this.sortToolItemData(this.data);
            for (var i = 0; i < this.data.length; i++) {
                var item = this.createToolItem(this.data[i]);
                if (item) {
                    if (this.items == null)
                        this.items = [item];
                    else
                        this.items[this.items.length] = item;
                    this.addMembers(item);
                }
            }
        }
        else if (this.items != null && this.items.length > 0) {
            for (var i = 0; i < this.items.length; i++)
                this.addMembers(this.items[i]);
        }
        if (this.hiddenItems != null)
            this.setItemVisible(this.hiddenItems, false);
    },

    sortToolItemData: function (array) {
        if (array.length > 0) {
            var sortedArray = [];
            for (var i = 0; i < array.length; i++) {
                var data = array[i];
                var index = (array.length - i) * 10;
                if (data.index != undefined && data.index != null)
                    index = data.index;
                while (sortedArray[index] != undefined)
                    index++;
                sortedArray[index] = data;
            }
            array.length = 0;
            for (var i = sortedArray.length - 1; i >= 0; i--) {
                if (sortedArray[i] != undefined)
                    array[array.length] = sortedArray[i];
            }
        }
    },

    getTitle: function (name) {
        if (isc.i18nButton[name])
            return isc.i18nButton[name];
    },

    createToolItem: function (itemData) {
        var item = itemData;
        if (typeof(itemData) != "string") {
            if (itemData.type == undefined || itemData.type == null)
                itemData.type = "button";
            if (itemData.title == undefined || itemData.title == null) {
                var title = this.getTitle(itemData.name);
                if (title)
                    itemData.title = title;
            }
            itemData.type = itemData.type.toLowerCase();
            var itemType = isc.ToolStripBar.Types[itemData.type];
            if (itemType != null) {
                if (itemData.type == "menu" || itemData.type == "button") {
                    if (!$.defined(itemData.showIcon))
                        itemData.showIcon = true;
                    if (itemData.showIcon && !$.defined(itemData.icon)) {
                        if (itemData.type == "button")
                            itemData.icon = "[SKIN]/button/" + itemData.name + ".png";
                        else
                            itemData.icon = "[SKIN]/../button/" + itemData.name + ".png";
                    }
                }
                if (itemData.type == "menu")
                    isc.addProperties(itemData, isc.ToolStripBar.MenuButtonDefaultProperties);
                else if (itemData.type == "form")
                    this.createForm(itemData);
                if (itemData.type == "selector") {
                    var formData = {
                        type: "form",
                        width: itemData.width,
                        minWidth: 50,
                        numCols: 1,
                        fields: [itemData]
                    }
                    this.createForm(formData);
                    item = isc.DynamicForm.create(formData);
                } else
                    item = isc.ClassFactory.newInstance(itemType, item);
                var toolBar = this;
                if (itemData.type == "button") {
                    if (!$.defined(itemData.click)) {
                        item.click = function () {
                            toolBar.itemClick(this);
                        };
                    }
                    itemData.button = item;
                }
                else if (itemData.type == "menu")
                    item.menu = this.createMenu(itemData);
                else if (itemData.type == "form" || itemData.type == "selector")
                    item.toolBar = this;
                this[item.name] = item;
            }
        }
        return item;
    },

    createMenu: function (itemData) {
        return isc.MenuExt.create({
            menuData: itemData,
            toolBar: this
        });
    },

    createForm: function (itemData) {
        var formProperties = isc.addProperties(itemData, isc.ToolStripBar.FormDefaultProperties);
        if (formProperties.fields) {
            for (var i = 0; i < formProperties.fields.length; i++) {
                var fieldProperties = isc.ToolStripBar[formProperties.fields[i].type + "FieldDefaultProperties"];
                if ($.defined(fieldProperties))
                    formProperties.fields[i] = isc.addProperties(formProperties.fields[i], fieldProperties);
            }
        }
        return formProperties;
    },

    itemChanged: function (item, value) {
        var result = this.fireEvent(isc.ToolBarEx.Events.ItemChanged, item, value);
        if (result == undefined)
            isc.confirm("这个功能还没做呐！", null, { buttons: [ isc.Dialog.OK ] });
    },

    itemClick: function (item) {
        var result = this.fireEvent(isc.ToolBarEx.Events.ItemClick, item);
        if (result == undefined)
            isc.confirm("这个功能还没做呐！", null, { buttons: [ isc.Dialog.OK ] });
    },

    findItems: function (itemNames) {
        var foundItems = [];
        if (itemNames == null || itemNames == undefined)
            return found;
        if (!isc.isAn.Array(itemNames))
            itemNames = [ itemNames ];
        if (itemNames.length > 0) {
            for (var i = 0; i < this.items.length; i++) {
                var item = this.items[i];
                var found = false;
                for (var j = 0; j < itemNames.length; j++) {
                    if (this.isEquals(item, itemNames[j])) {
                        foundItems.push(item);
                        found = true;
                        break;
                    }
                }
                if (!found && item.menu) {
                    var menuItems = item.menu.findItems(itemNames);
                    for (var j = 0; j < menuItems.length; j++)
                        foundItems.push(menuItems[j]);
                }
            }
        }
        return foundItems;
    },

    setItemVisible: function (items, result) {
        var found = this.findItems(items);
        for (var i = 0; i < found.length; i++) {
            if (result)
                found[i].show();
            else
                found[i].hide();
        }
    },

    isEquals: function (item, name) {
        if (item == null || item == undefined)
            return false;
        if (name == null || name == undefined)
            return false;
        return item.name == name;
    },

    setButtonState: function (disable, enable) {
        this.fireEvent(isc.ToolBarEx.Events.CheckState, disable, enable);
        if (disable != null) {
            for (var i = 0; i < disable.length; i++) {
                var button = this[disable[i]];
                if (button)
                    button.setDisabled(true);
            }
        }
        if (enable != null) {
            for (var i = 0; i < enable.length; i++) {
                var button = this[enable[i]];
                if (button)
                    button.setDisabled(false);
            }
        }
    }
});

/**
 * 每个字符宽度。
 */
isc.WidthPerChar = 8;

var formItemErrorHtml = {
    transTableHtml: function (formItem, tableHtml) {
        if (formItem.getClassName() == "TextItem" || formItem.getClassName() == "FloatItem" || formItem.getClassName() == "TextAreaItem" || formItem.getClassName() == "PasswordItem" || formItem.getClassName() == "CheckboxItem" || formItem.getClassName() == "StaticTextItem")
            return this.transTextItem(formItem, tableHtml);
        if (formItem.getClassName() == "SpinnerItem")
            return this.transSpinnerItem(formItem, tableHtml);
        if (formItem.getClassName() == "SelectItem" || formItem.getClassName() == "ComboBoxItem")
            return this.transSelectItem(formItem, tableHtml);
        if (formItem.getClassName() == "DateItem")
            return this.transDateItem(formItem, tableHtml);
        if (formItem.getClassName() == "DateTimeItem")
            return this.transDateTimeItem(formItem, tableHtml);
        if (formItem.getClassName() == "UnitValueItem" || formItem.getClassName() == "PercentItem" || formItem.getClassName() == "BooleanItem")
            return this.transCanvasItem(formItem, tableHtml);
        alert(formItem.getClassName());
        alert(tableHtml);
        return tableHtml;
    },

    removeFormCellError: function (tableHtml) {
        var $html = $("<p></p>").append(tableHtml);
        $html.find("div.viewformCellError").css("border", "none");
        return $html[0].innerHTML;
    },

    transTextItem: function (formItem, tableHtml) {
        var $html = $("<p></p>").append(tableHtml);
        $html.children("table:first").css("width", "100%");
        $html.find("td:first").css("width", formItem.fieldWidth - 3);
        $html.find("[name=" + formItem.name + "]").css("width", "100%");
        return $html[0].innerHTML;
    },

    transSpinnerItem: function (formItem, tableHtml) {
        var $html = $("<p></p>").append(tableHtml);
        $html.children("table:first").css("width", "100%");
        $html.find("td:first").css("width", formItem.fieldWidth - 19);
        $html.find("[name=" + formItem.name + "]").css("width", "100%");
        return $html[0].innerHTML;
    },

    transCanvasItem: function (formItem, tableHtml) {
        var $html = $("<p></p>").append(tableHtml);
        $html.children("table:first").css("width", "100%");
        $html.find("td:first").css("width", formItem.fieldWidth);
        return $html[0].innerHTML;
    },

    transSelectItem: function (formItem, tableHtml) {
        var $html = $("<p></p>").append(tableHtml);
        $html.children("table:first").css("width", "100%");
        var $td = $html.find("td:first");
        $td.css("width", formItem.fieldWidth - 3);
        var $itemTable = $td.find("table:first");
        $itemTable.css("width", "100%");
        $itemTable.find("td:first").css("width", "100%");
        $itemTable.find("input").css("width", "100%");
        $itemTable.find("div").css("width", "100%");
        return $html[0].innerHTML;
    },

    transDateItem: function (formItem, tableHtml) {
        var $html = $("<p></p>").append(tableHtml);
        $html.children("table:first").css("width", "100%");
        $html.find("td.formHint").css("width", "100%");
        return $html[0].innerHTML;
    },

    transDateTimeItem: function (formItem, tableHtml) {
        var $html = $("<p></p>").append(tableHtml);
        $html.children("table:first").css("width", "100%");
        $html.find("td:first").css("width", formItem.fieldWidth);
        $html.find("td.formHint").css("width", "100%");
        return $html[0].innerHTML;
    }
}

/*
 * Widgets forms
 */
isc.ClassFactory.defineClass("ViewForm", "DynamicForm");
isc.ViewForm.addClassProperties({
    DefaultFieldProperties: {
        titleStyle: "viewformTitle",
        cellStyle: "viewformCell",
        errorMessageWidth: 200,
        showDisabled: true,
        type: "StaticText",
        titleVAlign: "top"
    },

    DefaultFormItemProperties: {
        isReadOnly: function () {
            var widget = this.form;
            var item = this;
            while (item.parentItem) {
                if (item.canEdit != null) return !item.canEdit;
                item = item.parentItem;
            }
            if (widget && widget.canEditField)
                return !widget.canEditField(item);
            return this.canEdit ? !this.canEdit : false;
        },

        setReadOnly: function (value) {
            if (value) {
                this.setCanEdit(false);
                this.disable();
            }
            else {
                this.setCanEdit(true);
                this.enable();
            }
        }
    },

    initEditorType: function (field) {
        if (!$.defined(field.editorType)) {
            if (field.type == "integer" || field.dataType == "integer") {
                if ($.defined(field.min) && $.defined(field.max))
                    field.editorType = "spinner";
            }
        }
    }
});

isc.ViewForm.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    formBorder: "1px solid #dddde5",
    columnCount: 1,
    cellPadding: 1,
    showRowNum: false,
    errorOrientation: "right",
    requiredTitlePrefix: "<font color=red>",
    requiredTitleSuffix: "&nbsp;:</font>",
    rightTitlePrefix: "</font>:&nbsp;",
    showErrorText: false
});
isc.ViewForm.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        if (!$.defined(this.service))
            this.initialize();
    },

    initialize: function () {
        this.initColumns();
        this.initFields();
        this.initForm(this);
    },

    getInnerHTML: function (printCallback) {
        var $html = $("<p></p>").append(this.Super("getInnerHTML", arguments));
        var $table = $html.find("table:first");
        if (this.formBorder) {
            $table.css("border-left", this.formBorder);
            $table.css("border-top", this.formBorder);
            $table.css("border-right", "none");
            $table.css("border-bottom", "none");
        }
        $table.css("width", "100%");
        return $html[0].innerHTML;
    },

    initForm: function (form) {
        var formItems = form.getItems();
        if (formItems != undefined) {
            for (var i = 0; i < formItems.length; i++) {
                var formItem = formItems[i];
                if (formItem.getInnerHTML != undefined && formItem._getInnerHTML == undefined) {
                    formItem._getInnerHTML = formItem.getInnerHTML;
                    formItem.getInnerHTML = function isc_FormItem__getInnerHTML(values, includeHint, includeErrors, returnArray) {
                        var tableHtml = this._getInnerHTML(values, includeHint, includeErrors, returnArray);
                        if (includeErrors && this.hasErrors() && this.getErrorOrientation() == isc.Canvas.RIGHT) {
                            tableHtml = formItemErrorHtml.transTableHtml(this, tableHtml);
                            tableHtml = formItemErrorHtml.removeFormCellError(tableHtml);
                        }
                        return tableHtml;
                    };
                }
                if (!$.defined(formItem.focus)) {
                    formItem.focus = function (form, item) {
                        form.itemFocus(item);
                    };
                }
                if (formItem.getClassName() == "TextItem" || formItem.getClassName() == "TextAreaItem" || formItem.getClassName() == "PasswordItem" || formItem.getClassName() == "FloatItem" || formItem.getClassName() == "SpinnerItem") {
                    if (!$.defined(formItem.blur)) {
                        formItem.blur = function (form, item) {
                            form.itemBlur(item);
                        };
                    }
                }
                else if (formItem.getClassName() == "CheckboxItem") {
                    formItem.requiredTitlePrefix = "";
                    formItem.requiredTitleSuffix = "";
                }
                else if (formItem.getClassName() == "SelectItem") {
                    if (!$.defined(formItem._getValueMap)) {
                        formItem._getValueMap = formItem.getValueMap;
                        formItem.getValueMap = function () {
                            return this.valueMap || form.getFormItemValueMap(this);
                        };
                    }
                } else if (formItem.getClassName() == "ComboBoxItem") {
                    if (!$.defined(formItem._getValueMap)) {
                        formItem._getValueMap = formItem.getValueMap;
                        formItem.getValueMap = function () {
                            return this.valueMap || form.getFormItemValueMap(this);
                        };
                    }
                    formItem.getPickData = function () {
                        return this.pickData || form.getFormItemPickData(this);
                    };
                }
                if (!$.defined(formItem.formatValue)) {
                    formItem.formatValue = function (value, record, form, item) {
                        if (form.formatValue)
                            return form.formatValue(value, record, form, item);
                    };
                }
                isc.addProperties(formItem, isc.ViewForm.DefaultFormItemProperties);
            }
        }
    },

    initColumns: function () {
        this.numCols = this.columnCount * 2;
        this.showErrorText = (this.columnCount == 1);

        var colWidths = this["colWidths" + this.columnCount];
        if (colWidths != undefined && colWidths != null)
            this.colWidths = colWidths;
        else {
            if (this.columnCount == 1)
                this.colWidths = ["25%", "75%"];
            else if (this.columnCount == 2)
                this.colWidths = ["15%", "35%", "15%", "35%"];
            else if (this.columnCount == 3)
                this.colWidths = ["10%", "23%", "10%", "23%", "10%", "23%"];
            else if (this.columnCount == 4)
                this.colWidths = ["5%", "20%", "5%", "20%", "5%", "20%"];
        }
    },

    initFields: function () {
        if (this.sourceFields == undefined || this.sourceFields == null) {
            if (this.fields && this.fields.length > 0)
                this.sourceFields = this.fields;
        }
        var result = this.iniFormFields(this.sourceFields);
        if (result.fields.length > 0) {
            while (result.showCount % this.columnCount != 0) {
                result.fields.push(isc.ViewForm.DefaultFieldProperties);
                result.showCount++;
            }
            this.setFields(result.fields);
        }
    },

    iniFormFields: function (sourceFields) {
        var showCount = 0;
        var fields = [];
        if (sourceFields && sourceFields.length > 0) {
            for (var i = 0; i < sourceFields.length; i++) {
                var sourceField = sourceFields[i];
                var field = this.createField(sourceField);
                this.initVisible(field);
                // 设置栏位对齐方式
                this.initAlign(field);
                // 设置栏位长度。
                this.initDisplayWidth(field);
                // 设置栏位度。
                this.initRowSpan(field);
                isc.ViewForm.initEditorType(field);
                fields.push(field);
                if (sourceField.hidden != true)
                    showCount += field.rowSpan ? field.rowSpan : 1;
            }
        }
        return {showCount: showCount, fields: fields};
    },

    createField: function (sourceField) {
        var field = isc.addProperties({}, isc.ViewForm.DefaultFieldProperties);
        if (this.titleStyle)
            field.titleStyle = this.titleStyle;
        if (this.cellStyle)
            field.cellStyle = this.cellStyle;
        isc.addProperties(field, sourceField);
        isc.LogicFieldFactory.createField(field);

        if (this.overrideFieldProperties && this.overrideFieldProperties[field.name])
            isc.addProperties(field, this.overrideFieldProperties[field.name]);
        if (field.pickListFields) {
            if (!$.defined(field.editorType))
                field.editorType = "ComboBoxItem";
            if (!$.defined(field.addUnknownValues))
                field.addUnknownValues = false;
        }
        return field;
    },

    initVisible: function (field) {
        if ($.defined(field.visible))
            field.hidden = !field.visible;
        if (field.name == "rowNum")
            field.hidden = !this.showRowNum;
    },

    initAlign: function (field) {
        // 内容对齐方式。
        if (!$.defined(field.textAlign)) {
            field.textAlign = "left";
            if (field.type != "StaticText" && field.type != "enum" && !field.displayField) {
                if (field.type == "integer" || field.dataType == "integer" || field.type == "float" || field.dataType == "float")
                    field.textAlign = "right";
            }
        }
    },

    initDisplayWidth: function (field) {
        // 设置栏位长度。
        if (field.displayWidth == undefined) {
            var displayWidth;
            if (field.length >= 30)
                displayWidth = 30;
            else if (field.length >= 20)
                displayWidth = 25;
            else if (field.length >= 15 || field.length == undefined)
                displayWidth = 20;
            else if (field.length >= 10)
                displayWidth = 15;
            else
                displayWidth = 10;
            field.displayWidth = displayWidth;
        }
        if (field.length == undefined) {
            if (field.dataType == "date" || field.dataType == "datetime")
                field.length = 19;
            else if (field.dataType == "integer" || field.dataType == "float")
                field.length = 15;
            else if (field.dataType == "boolean") {
                if (field.type == "enum")
                    field.length = 10;
            }
        }
        if (!$.defined(field.width)) {
            if (field.length == undefined)
                field.width = "100%";
            else
                field.width = field.displayWidth * isc.WidthPerChar;
        }
        if ($.defined(field.width) && isc.isA.Number(field.width))
            field.fieldWidth = field.width;
    },

    initRowSpan: function (field) {
        if (field.type != "StaticText" && field.length >= 60)
            field.type = "textArea";
        if (field.length >= 60 && field.length <= 150)
            field.rowSpan = 2;
        else if (field.length > 150 && field.length <= 300)
            field.rowSpan = 3;
        else if (field.length > 300)
            field.rowSpan = 4;
    },

    canEditField: function (field, widget) {
        if (widget && widget.canEditField)
            return widget.canEditField(field);
        if (field && field.canEdit)
            return field.canEdit;
        if (widget && widget.canEdit)
            return widget.canEdit;
        return this.canEdit ? this.canEdit : false;
    },

    setColumnCount: function (columnCount) {
        if (columnCount > 0 && columnCount <= 4 && columnCount != this.columnCount) {
            this.columnCount = columnCount;
            this.initWidget();
            this.initialize();
        }
    },

    ajaxSubmit: function (success, error) {
        var options = {
            dataType: "json",
            success: function (data) {
                if (success)
                    success(data);
                return false;
            },
            error: function () {
                if (error)
                    error();
                return false;
            }
        };
        this.saveData();
        $("#" + this.getFormID()).ajaxSubmit(options);
    }
});

isc.ClassFactory.defineClass("DataForm", "ViewForm");
isc.DataForm.addClassProperties({
    Events: {
        ItemFocus: "ItemFocus",
        ItemBlur: "ItemBlur"
    },

    TableEventsProperties: {
        table_OnFieldErrors: function (table, column, row, message) {
            if (column)
                this.showFieldErrors(column.name);
        },

        table_RowDataStateChanged: function (table, row, dataState) {
            this.refreshFormItemsVisible();
        },

        table_DataStateChanged: function (table, dataState) {
            this.refreshFormItemsVisible();
        },

        table_OnClearFieldErrors: function (table, column, row) {
            this.table_OnFieldErrors(table, column, row);
        },

        table_OnColumnVisibleChanged: function (table, column) {
            var formItem = this.getItem(column.name);
            if (formItem) {
                if (formItem.isVisible() && !column.isVisible())
                    formItem.hide();
                else if (!formItem.isVisible() && column.isVisible())
                    formItem.show();
            }
        },

        table_OnColumnReadOnlyChanged: function (table, column) {
            var formItem = this.getItem(column.name);
            if (formItem)
                formItem.setReadOnly(column.isReadOnly());
        },

        table_AfterInsert: function (table, row) {
            this.table_AfterLoad(table);
        },

        table_AfterSave: function (table) {
            this.table_AfterLoad(table);
        },

        table_AfterLoad: function (table) {
            this.setHiliteRequiredFields();
            var bindTable = this.getBindTable();
            if (bindTable) {
                var row = bindTable.getCurrentRow();
                if (row != null)
                    this.editRecord(row);
                else
                    this.editRecord({});
            }
            this.refreshFormItemsVisible();
        },

        table_CurrentChanged: function (table, column, row) {
            var focusItem = this.getFocusItem();
            if (focusItem == undefined || focusItem == null || focusItem.name != column.name) {
                var formItem = this.getField(column.name);
                if (formItem && formItem.isVisible() && !formItem.isDisabled())
                    this.focusInItem(formItem);
            }
        },

        table_CurrentRowChanged: function (table, row) {
            this.table_AfterLoad(table);
        }
    }
});

isc.DataForm.addClassProperties({
    Events: {
        ItemChange: "ItemChange",
        ItemChanged: "ItemChanged"
    }
});

isc.DataForm.addProperties({
    canEdit: true,
    hiliteRequiredFields: true
});

isc.DataForm.addMethods({
    initialize: function () {
        isc.addProperties(this, isc.DataForm.TableEventsProperties);
        this.createFields();
        this.Super("initialize", arguments);
        this.table_AfterLoad(this.getBindTable());
    },

    createFields: function () {
        var bindTable = this.getBindTable();
        if (bindTable) {
            var fields = bindTable.columns.getFields(this.fieldNames);
            if (fields.length > 0) {
                this.fields = fields;
                this.useAllDataSourceFields = false;
            }
        }
    },

    hasErrors: function () {
        if (this.hasFieldErrors())
            return true;
        return this.Super("hasErrors", arguments);
    },

    getBindTable: function () {
    },

    hasFieldErrors: function (fieldName) {
        var bindTable = this.getBindTable();
        if (bindTable) {
            var currentRow = bindTable.getCurrentRow();
            if (currentRow)
                return currentRow.hasErrors(fieldName);
        }
        return this.Super("hasFieldErrors", arguments);
    },

    canEditField: function (field, widget) {
        var bindTable = this.getBindTable();
        if (bindTable) {
            if (field == undefined || field == null)
                return !bindTable.isReadOnly();
            var currentRow = bindTable.getCurrentRow();
            if (currentRow == undefined || currentRow == null)
                return !bindTable.isReadOnly();
            return !currentRow.isReadOnly(field.name);
        }
        return this.Super("canEditField", arguments);
    },

    getFieldErrors: function (fieldName) {
        var bindTable = this.getBindTable();
        if (bindTable) {
            if (isc.isA.FormItem(fieldName))
                fieldName = fieldName.name;
            var currentRow = bindTable.getCurrentRow();
            if (currentRow)
                return currentRow.getFieldErrors(fieldName);
        }
        return this.Super("getFieldErrors", arguments);
    },

    refreshFormItemsVisible: function () {
        var formItems = this.getItems();
        if (formItems == undefined || formItems == null)
            return;
        var bindTable = this.getBindTable();
        if (bindTable == undefined || bindTable == null)
            return;
        var currentRow = bindTable.getCurrentRow();
        if (currentRow == undefined || currentRow == null)
            return;
        for (var i = 0; i < formItems.length; i++) {
            var formItem = formItems[i];
            var visible = currentRow.isVisible(formItem.name);
            if (formItem.isVisible() && !visible)
                formItem.hide();
            else if (!formItem.isVisible() && visible)
                formItem.show();
        }
    },

    refreshFormItemsReadOnly: function () {
        var formItems = this.getItems();
        if (formItems == undefined || formItems == null)
            return;
        var bindTable = this.getBindTable();
        if (bindTable == undefined || bindTable == null)
            return;
        var currentRow = bindTable.getCurrentRow();
        if (currentRow == undefined || currentRow == null)
            return;
        for (var i = 0; i < formItems.length; i++) {
            var formItem = formItems[i];
            var readOnly = currentRow.isReadOnly(formItem.name);
            if (formItem.isDisabled() && !readOnly)
                formItem.setDisabled(false);
            else if (!formItem.isDisabled() && readOnly)
                formItem.setDisabled(true);
        }
    },

    setHiliteRequiredFields: function () {
        var hiliteRequiredFields = true;
        if (this.isReadOnly())
            hiliteRequiredFields = false;
        else if (this.hitRequiredFields != undefined && this.hitRequiredFields != null)
            hiliteRequiredFields = this.hitRequiredFields;
        if (this.hiliteRequiredFields != hiliteRequiredFields)
            this.hiliteRequiredFields = hiliteRequiredFields;
    },

    getFormItemValueMap: function (formItem) {
        var bindTable = this.getBindTable();
        if (bindTable) {
            var row = bindTable.getCurrentRow();
            if (row != null)
                return row.getValueMap(formItem.name);
        }
        return this.Super("getFormItemValueMap", arguments);
    },

    getFormItemPickData: function (formItem) {
        var bindTable = this.getBindTable();
        if (bindTable) {
            var row = bindTable.getCurrentRow();
            if (row != null)
                return row.getPickData(formItem.name);
        }
    },

    formatValue: function (value, record, form, formItem) {
        var bindTable = this.getBindTable();
        if (bindTable) {
            var row = bindTable.getCurrentRow();
            if (row != null)
                return row.format(formItem.name, value);
        }
        var displayValue = this.Super("formatValue", arguments);
        return $.defined(displayValue) ? displayValue : value;
    },

    isReadOnly: function (fieldName) {
        var bindTable = this.getBindTable();
        if (bindTable) {
            if (bindTable.isReadOnly())
                return true;
            if (fieldName) {
                var currentRow = bindTable.getCurrentRow();
                if (currentRow)
                    return currentRow.isReadOnly(fieldName);
            }
            return !bindTable.isUpdateAble();
        }
        return this.readOnly;
    },

    itemChange: function (item, value, oldValue) {
        this.Super("itemChange", arguments);
        if (this.fireEvent)
            this.fireEvent(isc.DataForm.Events.ItemChange, item, value, oldValue);
        return !this.isReadOnly(item.name);
    },

    itemChanged: function (item, newValue) {
        var bindTable = this.getBindTable();
        if (bindTable != undefined && bindTable != null) {
            if (bindTable.isUpdateAble())
                bindTable.update(item.name, newValue);
        }
        else
            this.Super("itemChanged", arguments);
        if (this.fireEvent)
            this.fireEvent(isc.DataForm.Events.ItemChanged, item, newValue);
    },

    itemFocus: function (item) {
        var bindTable = this.getBindTable();
        if (bindTable)
            bindTable.setCurrent(item.name);
        else
            this.Super("itemChanged", arguments);
    },

    itemBlur: function (item) {
        var bindTable = this.getBindTable();
        if (bindTable)
            bindTable.update(item.name, item.getValue());
        else
            this.Super("itemBlur", arguments);
    }
});

/*
 * Widgets DataGridToolBar
 */
isc.ClassFactory.defineClass("DataGridToolBar", "ToolStripBar");
isc.DataGridToolBar.addProperties({
    padding: 0,
    height: 2,
    layoutTopMargin: 0,
    layoutBottomMargin: 0,
    layoutLeftMargin: 2,
    layoutRightMargin: 15,
    styleName: "dataGridToolBar",
    data: [
        {
            name: "first",
            icon: "[SKIN]/button/firstPage.png",
            prompt: isc.i18nButton.first
        },
        {
            name: "prior",
            icon: "[SKIN]/button/priorPage.png",
            prompt: isc.i18nButton.prior
        },
        {
            name: "next",
            icon: "[SKIN]/button/nextPage.png",
            prompt: isc.i18nButton.next
        },
        {
            name: "last",
            icon: "[SKIN]/button/lastPage.png",
            prompt: isc.i18nButton.last
        }
    ]
});
isc.DataGridToolBar.addMethods({
    initWidget: function () {
        isc.addProperties(this, isc.Service.DefaultComponentProperties);
        this.Super("initWidget", arguments);
        this.addMembers(this.getComponents());
    },

    getComponents: function () {
        var toolBar = this;
        this.pageNumForm = isc.DynamicForm.create({
            width: 240,
            titleWidth: "*",
            numCols: 4,
            colWidths: ["25%", "20%", "35%", "20%"],
            fields: [
                {
                    name: "pageNum",
                    title: isc.i18nStatus.pageNum,
                    width: 60,
                    valueMap: [0]
                },
                {
                    name: "pageSize",
                    title: isc.i18nStatus.pageSize,
                    width: 60,
                    valueMap: pageSizeValueMap,
                    defaultValue: "50"
                }
            ],
            itemChanged: function (item, newValue) {
                if (item.name == "pageNum")
                    toolBar.getBindTable().setPageNum(newValue);
                else if (item.name == "pageSize")
                    toolBar.getBindTable().setPageSize(newValue);
            }
        });
        var components = [this.pageNumForm];
        components.push(isc.LayoutSpacer.create({ width: "*" }));
        this.createButtons(components);
        return components;
    },

    setBindTable: function (bindTable) {
        this.bindTable = bindTable;
        bindTable.bindListener(this);
    },

    createButtons: function (components) {
        var toolBar = this;
        for (var i = 0; i < this.data.length; i++) {
            var button = isc.ToolStripButton.create(this.data[i]);
            isc.addProperties(button, {
                click: function () {
                    toolBar.itemClick(this);
                }
            })
            this[button.name] = button;
            components.push(button);
        }
    },

    getBindTable: function () {
        return this.bindTable;
    },

    table_AfterLoad: function (table) {
        var valueMap = [];
        if (table.totalPageCount == 0)
            valueMap.push(0);
        else {
            for (var i = 0; i < table.totalPageCount; i++)
                valueMap.push(i + 1);
        }
        this.pageNumForm.setValueMap("pageNum", valueMap);
        this.pageNumForm.setData({
            pageNum: table.pageNum,
            pageSize: table.pageSize
        });
        this.updateStatus();
    },

    table_DataStateChanged: function (table) {
        this.updateStatus();
    },

    itemClick: function (item) {
        var bindTable = this.getBindTable();
        switch (item.name) {
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
        }
        this.setButtonState(disable, enable);
    }
});
var ImgUtils = {
    getHtml: function (icon, width, height, attriProperties, cssProperties) {
        var $html = $("<p>" + isc.Canvas.imgHTML(icon, width, height) + "</p>");
        if (cssProperties) {
            for (var cssName in cssProperties)
                $html.children("img").css(cssName, cssProperties[cssName]);
        }
        if (attriProperties) {
            for (var attriName in attriProperties)
                $html.children("img").attr(attriName, attriProperties[attriName]);
        }
        return $html[0].innerHTML;
    }
};

/*
 * Widgets DataTileGrid
 */
isc.ClassFactory.defineClass("DataTileGrid", "TileGrid");
isc.DataTileGrid.addClassProperties({
    TableEventsProperties: {
        table_AfterLoad: function (table) {
            this.cleanData();
            if (table.rows != this.getData())
                this.setData(table.rows);
            this.selectRecord(table.getCurrentRow());
            if (this.hilites)
                this.setHilites(this.hilites);
        },
        table_CurrentRowChanged: function (table, row) {
            if (this.selectionType == "single" && this.selection)
                this.selectSingleRecord(row);
        }
    }
});

isc.DataTileGrid.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    tileWidth: 300,
    tileHeight: 200,
    columnSize: 3,
    selectionType: "single",
    selectedBorder: "2px solid #9BC8FF",
    removeDetailViewer: true
});

isc.DataTileGrid.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        if (this.removeDetailViewer)
            this.detailViewer.destroy();
        if (!$.defined(this.service))
            this.initialize();
    },

    initialize: function () {
        var bindTable = this.getBindTable();
        if (bindTable) {
            isc.addProperties(this, isc.DataTileGrid.TableEventsProperties);
            this.createFields();
        }
        else
            this.createFields();

        this.Super("initialize", arguments);
    },

    getBindTable: function () {
    },

    createFields: function () {
        var fields;
        var bindTable = this.getBindTable();
        if (bindTable)
            fields = bindTable.columns.getFields(this.fieldNames);
        if (fields && fields.length > 0)
            this.fields = fields;
    },

    setColumnSize: function (value) {
        this.columnSize = value;
        this.setTileWidth(this.getWidth() / value - this.tileMargin * 2);
    },

    makeTile: function (record, tileNum) {
        var canvas = this.Super("makeTile", arguments);
        canvas.service = this.service;
        var tileRecord = this.getTileRecord(canvas);
        if ($.defined(tileRecord))
            record = tileRecord;
        if ($.isFunction(canvas.setRecord))
            canvas.setRecord(record);
        else if ($.isFunction(canvas.setData))
            canvas.setData(record);
        else if ($.isFunction(canvas.setValues))
            canvas.setValues(record);
        canvas.addProperties({click: function () {
            var dataTileGrid = this.parentElement;
            var bindTable = dataTileGrid.getBindTable();
            if (bindTable)
                bindTable.setCurrentRow(record);
            else
                dataTileGrid.selectRecord(record);
            this.Super("click", arguments);
        }});
        if (!$.defined(canvas.setSelected))
            canvas.setSelected = function () {
            };
        return canvas;
    },

    selectionChanged: function (record, state) {
        var selectedTile = this.getTile(record);
        if (state)
            selectedTile.setBorder(this.selectedBorder)
        else
            selectedTile.setBorder(null);
        this.Super("selectionChanged", arguments);
    },

    cleanData: function () {
        for (var i = this.data.length - 1; i >= 0; i--) {
            this.removeData(this.data[i]);
            if ($.isFunction(this.tiles[i].destroy))
                this.tiles[i].destroy();
        }
    },

    resized: function () {
        this.setColumnSize(this.columnSize);
        return this.Super("resized", arguments);
    }
});

/*
 * Widgets DataGrid
 */
isc.ClassFactory.defineClass("DataGrid", "ListGrid");
isc.DataGrid.addClassProperties({
    DefaultGridProperties: {
        useAllDataSourceFields: false,
        baseStyle: "dataGrid",
        showRowNumbers: true,
        rowNumberStyle: "dataGrid",
        rowNumberFieldProperties: {
            showAlternateStyle: true
        },
        headerAutoFitEvent: "none",
        canSort: false,
        canAutoFitFields: false,
        wrapCells: true,
        fixedRecordHeights: false,
        alternateRowStyles: true,
        autoFetchData: false,
        cellPadding: 1,
        showRecordComponents: true,
        showRecordComponentsByCell: true,
        canHover: true,
        showHover: true,
        editEvent: "click",
        enterKeyEditAction: "nextCell",
        cellErrorCSSText: "background-color:#FF8A37;",
        showErrorIcons: false,
        removeIcon: "[SKIN]/button/remove.gif",
        editFormProperties: {
            itemChanged: function (item, value) {
                this.grid.setEditValue(item.rowNum, item.colNum, value);
                this.Super("itemChanged", arguments);
            }
        }
    },

    Events: {
        RowClick: "RowClick",
        RowDoubleClick: "RowDoubleClick",
        RowButtonClick: "RowButtonClick",
        RecordClick: "RecordClick"
    },

    TableEventsProperties: {
        table_OnFieldErrors: function (table, column, row) {
            var rowNum = this.getRecordIndex(row);
            var colNum = this.getFieldNum(column.name);
            if (rowNum != -1)
                this.refreshCell(rowNum, colNum);
        },

        table_AfterUpdate: function (table, column, row, value) {
            var rowNum = this.getRecordIndex(row);
            if (rowNum != -1)
                this.setEditValue(rowNum, column.name, value);
        },

        table_OnColumnVisibleChanged: function (table, column) {
            if (column.isVisible())
                this.showField(column.name);
            else
                this.hideField(column.name);
        },

        table_OnColumnReadOnlyChanged: function (table, column) {
            this.setFieldProperties(column.name, {disabled: column.isReadOnly()});
        },

        table_AfterLoad: function (table) {
            if (table.rows != this.getData())
                this.setData(table.rows);
            this.selectSingleRecord(table.getCurrentRow());
        },

        table_AfterSave: function (table) {
            this.table_AfterLoad(table);
        },

        table_AfterDelete: function (table, row) {
            this.table_AfterLoad(table);
        },

        table_AfterInsert: function (table, row) {
            this.table_AfterLoad(table);
        },

        table_CurrentRowChanged: function (table, row) {
            if (this.selectionType == "single" && this.selection)
                this.selectSingleRecord(row);
        },

        table_BeforeTableValidate: function (table) {
            this.endEditing();
        }
    },

    DefaultGridMethods: {
        initialize: function () {
            var bindTable = this.getBindTable();
            if (bindTable) {
                isc.addProperties(this, isc.DataGrid.TableEventsProperties);
                this.canEdit = bindTable.isCreateAble() || bindTable.isUpdateAble();
                if (this.canEdit) {
                    this.alternateRowStyles = false;
                    if (bindTable.isCreateAble())
                        this.listEndEditAction = "next";
                }
                if (bindTable.isRemoveAble())
                    this.canRemoveRecords = true;
                if (this.body)
                    this.body.alternateRowStyles = this.alternateRowStyles;
                this.createFields();
                if (this.toolBar)
                    this.toolBar.setBindTable(bindTable);
            }
            else
                this.createFields();

            this.expansionEditorDefaults._constructor = "DataForm";
            this.Super("initialize", arguments);
            this.changeRemoveImgHTML();
        },

        saveLocally: function (editInfo, saveCallback) {
            var bindTable = this.getBindTable();
            if (bindTable) {
                var values = {};
                var fieldNames = bindTable.columns.fieldNames;
                for (var i = 0; i < fieldNames.length; i++)
                    values[fieldNames[i]] = editInfo.values[fieldNames[i]];
                editInfo.values = values;
            } else
                delete editInfo.values.errors;
            this.Super("saveLocally", arguments);
        },

        loadData: function () {
            if (this.values != undefined && this.values != null)
                this.setData(this.values);
        },

        createFields: function () {
            var fields;
            var bindTable = this.getBindTable();
            if (bindTable)
                fields = bindTable.columns.getFields(this.fieldNames);
            if (fields == undefined || fields.length == 0)
                fields = this.getAllFields();
            if (fields && fields.length > 0)
                this.setFields(fields);
        },

        setFields: function (fields) {
            if (fields) {
                var totalDisplayWidth = this.initFields(fields);
                if (this.formItem && !$.defined(this.formItem.pickListWidth))
                    this.formItem.pickListWidth = Math.max(totalDisplayWidth, this.formItem.width);
                if (this.autoFitWidth)
                    this.setWidth(totalDisplayWidth);
            }
            this.Super("setFields", arguments);
        },

        isReadOnly: function (rowNum, colNum) {
            var field = this.getField(colNum);
            var bindTable = this.getBindTable();
            if (bindTable && field && field.name) {
                var currentRow = bindTable.getCurrentRow(rowNum);
                if (currentRow == undefined || currentRow == null)
                    return bindTable.isReadOnly();
                return currentRow.isReadOnly(field.name);
            }

            var readOnly = false;
            if (field) {
                if (field.canEdit && !field.canEdit)
                    readOnly = true;
                if (field.disabled && field.disabled)
                    readOnly = true;
            }
            return readOnly;
        },

        initFields: function (fields) {
            var totalDisplayWidth = 0;
            for (var i = 0; i < fields.length; i++) {
                var field = fields[i];
                isc.LogicFieldFactory.createGridField(field);
                this.initVisible(field);
                // 设置栏位对齐方式
                this.initAlign(field);
                // 设置栏位长度。
                this.initDisplayWidth(field);
                if (!field.hidden)
                    totalDisplayWidth += field.displayWidth;
                if (this.overrideFieldProperties && this.overrideFieldProperties[field.name])
                    isc.addProperties(field, this.overrideFieldProperties[field.name]);
                this.initFieldPickList(field);
                this.initFieldEditor(field);
                if (field.frozen)
                    this.wrapCells = false;
            }
            this.initFieldFrozen(fields);
            this.createOperateToolBar(fields);
            return this.initFieldWidth(fields, totalDisplayWidth);
        },

        initFieldWidth: function (fields, totalDisplayWidth) {
            if (this.showRowNumbers)
                totalDisplayWidth += 40;
            if (totalDisplayWidth > 0) {
                var percentWidth = this.autoFitWidth;
                if (!$.defined(percentWidth))
                    percentWidth = $.defined(this.getWidth) && totalDisplayWidth < this.getWidth();
                for (var i = 0; i < fields.length; i++) {
                    var field = fields[i];
                    if (field.hidden)
                        continue;
                    if (percentWidth)
                        field.width = field.displayWidth / totalDisplayWidth * 100 + "%";
                    else
                        field.width = field.displayWidth;
                }
            }
            return totalDisplayWidth;
        },

        initFieldFrozen: function (fields) {
            var frozen;
            for (var i = fields.length - 1; i >= 0; i--) {
                if (fields[i].frozen)
                    frozen = true;
                if ($.defined(frozen))
                    fields[i].frozen = frozen;
            }
        },

        initFieldEditor: function (field) {
            if (!$.defined(field.editorProperties))
                field.editorProperties = {};
            var editorProperties = field.editorProperties;
            var fieldNames = ["dataType", "editorType", "length", "min", "max", "logic", "keyPressFilter", "valueMap"];
            for (var i = 0; i < fieldNames.length; i++) {
                var fieldName = fieldNames[i];
                if ($.defined(field[fieldName]))
                    editorProperties[fieldName] = field[fieldName];
            }
            isc.ViewForm.initEditorType(editorProperties);
        },

        initFieldPickList: function (field) {
            if (field && field.pickListFields) {
                if (!$.defined(field.editorProperties))
                    field.editorProperties = {};
                var editorProperties = field.editorProperties;
                for (var fieldName in field) {
                    if (fieldName.startsWith("pick"))
                        editorProperties[fieldName] = field[fieldName];
                }
                var fieldNames = ["displayField", "valueField", "pickList", "pickListProperties", "filterFields", "addUnknownValues" ];
                for (var i = 0; i < fieldNames.length; i++) {
                    var fieldName = fieldNames[i];
                    editorProperties[fieldName] = field[fieldName];
                }
                if (!$.defined(field.editorType))
                    editorProperties.editorType = "ComboBoxItem";
                if (!$.defined(field.addUnknownValues))
                    editorProperties.addUnknownValues = false;
                editorProperties.autoFitWidth = true;
                editorProperties.getPickData = function () {
                    return this.pickData || this.form.grid.getFormItemPickData(this.form, this);
                };
                editorProperties.pickListWidth = this.initFields(editorProperties.pickListFields);
            }
        },

        createOperateToolBar: function (fields) {
            if (this.operateToolBar && $.defined(this.operateField)) {
                this.operateField = isc.addProperties({
                    name: this.operateToolBar.fieldName,
                    title: this.operateToolBar.title,
                    displayWidth: this.operateToolBar.getDisplayWidth(),
                    align: "center",
                    cellAlign: "center"
                });
                this.operateToolBar.dataGrid = this;
                this.initDisplayWidth(this.operateField);
                fields.push(this.operateField);
            }
        },

        initVisible: function (field) {
            if ($.defined(field.visible))
                field.hidden = !field.visible;
            if (field.name == "rowNum") {
                field.length = 3;
                if (!$.defined(field.title))
                    field.title = " ";
                field.cellAlign = "left";
                this.showRowNumbers = false;
            }
        },

        initAlign: function (field) {
            field.align = "center";
            if (!$.defined(field.cellAlign)) {
                if (field.type == "boolean" || field.dataType == "boolean" || field.type == "image")
                    field.cellAlign = "center";
                else if (field.type == "integer" || field.type == "float" || field.dataType == "integer" || field.dataType == "float")
                    field.cellAlign = "right";
                else
                    field.cellAlign = "left";
                if (field.displayField)
                    field.cellAlign = "left";
            }
        },

        initDisplayWidth: function (field) {
            if ($.defined(field.displayWidth))
                return;

            if ($.defined(field.width)) {
                if (field.title && field.width < field.title.getBytesLength() * isc.WidthPerChar)
                    field.displayWidth = field.title.getBytesLength() * isc.WidthPerChar;
                else
                    field.displayWidth = field.width;
            } else {
                var displayWidth = field.length;
                if (!$.defined(displayWidth)) {
                    if (field.type == "integer" || field.dataType == "integer" || field.type == "float" || field.dataType == "float")
                        displayWidth = 15;
                    else if (field.type == "boolean" || field.dataType == "boolean")
                        displayWidth = 5;
                    else
                        displayWidth = 15;
                } else if (displayWidth > 40)
                    displayWidth = 40;
                else if (field.title && displayWidth < field.title.length)
                    displayWidth = field.title.length;

                if (field.title) {
                    var titleLength = field.title.getBytesLength();
                    if (displayWidth < titleLength)
                        displayWidth = titleLength;
                }
                field.displayWidth = Math.max(2, displayWidth) * isc.WidthPerChar;
            }
        },

        changeRemoveImgHTML: function () {
            if (this.completeFields) {
                var removeFieldNum = this.completeFields.findIndex("isRemoveField", true);
                if (removeFieldNum >= 0) {
                    var removeField = this.completeFields[removeFieldNum];
                    if (removeField)
                        removeField.removeIconHTML = this.getButtonImgHTML(this.removeIcon, this.removeIconSize, isc.i18nButton.remove);
                }
            }
        },

        getButtonImgHTML: function (icon, iconSize, title) {
            return ImgUtils.getHtml(icon, iconSize, iconSize, {title: title}, {cursor: "pointer"});
        },

        cellHasErrors: function (rowNum, fieldID) {
            var row = this.getCurrentRow(rowNum);
            if (row && $.isFunction(row.hasErrors)) {
                var field = this.getField(fieldID)
                if (field)
                    return row.hasErrors(field.name);
            }
            return false;
        },

        cellHoverHTML: function (record, rowNum, colNum) {
            var row = this.getCurrentRow(rowNum);
            if (row && row.hasErrors) {
                var field = this.getField(colNum)
                if (field && row.hasErrors(field.name))
                    return "<b>" + field.title + "</b>: " + row.getFieldErrors(field.name);
            }
        },

        getCellCSSText: function (record, rowNum, colNum) {
            if (record && record.hasErrors) {
                var field = this.getField(colNum);
                if (field && record.hasErrors(field.name))
                    return this.cellErrorCSSText;
            }
            return this.Super("getCellCSSText", arguments);
        },

        getCurrentRow: function (rowNum) {
            var row = this.getRecord(rowNum);
            if (row == undefined || row == null)
                row = this.getBindTable().getCurrentRow(rowNum);
            return row;
        },

        getCellErrors: function (rowNum, colNum) {
            var row = this.getCurrentRow(rowNum);
            if (row) {
                var field = this.getField(colNum)
                if (field && row.getFieldErrors)
                    return row.getFieldErrors(field.name);
            }
            return null;
        },

        rowHasErrors: function (rowNum) {
            var row = this.getCurrentRow(rowNum);
            if (row)
                return row.hasErrors();
            return false;
        },

        getRowErrors: function (rowNum) {
            var row = this.getCurrentRow(rowNum);
            if (row && row.getFieldErrors)
                return row.getFieldErrors();
            return null;
        },

        getBindTable: function () {
        },

        getEmptyMessage: function () {
            var bindTable = this.getBindTable();
            if (bindTable && bindTable.dataState == isc.Table.DataStates.Loading) {
                return this.loadingDataMessage == null ? "&nbsp;" :
                    this.loadingDataMessage.evalDynamicString(this, {
                        loadingImage: this.imgHTML(isc.Canvas.loadingImageSrc,
                            isc.Canvas.loadingImageSize,
                            isc.Canvas.loadingImageSize)
                    });
            }
            this.Super("getEmptyMessage", arguments);
        },

        startEditing: function (rowNum, colNum, suppressFocus) {
            if (this.isReadOnly(rowNum, colNum))
                return false;
            return this.Super("startEditing", arguments);
        },

        editorEnter: function (record, value, rowNum, colNum) {
            this.Super("editorEnter", arguments);
            var bindTable = this.getBindTable();
            if (bindTable) {
                var fieldName = this.getFieldName(colNum);
                var currentRow = this.getCurrentRow(rowNum);
                bindTable.setCurrent(fieldName, currentRow);
            }
        },

        editorExit: function (editCompletionEvent, record, newValue, rowNum, colNum) {
            return this.Super("editorExit", arguments);
        },

        setEditValue: function (rowNum, colNum, value) {
            var bindTable = this.getBindTable();
            if (bindTable && this.getField(colNum)) {
                var fieldName = this.getField(colNum).name;
                var record = this.getRecord(rowNum);
                bindTable.update(fieldName, value, record);
            }
            this.Super("setEditValue", arguments);
        },

        removeRecordClick: function (rowNum) {
            var bindTable = this.getBindTable();
            if (bindTable) {
                var record = this.getCurrentRow(rowNum);
                if (record) {
                    var dataGrid = this;
                    this.endEditing();
                    if (this.service) {
                        this.service.remove(bindTable, record, function () {
                            dataGrid.Super("removeRecordClick", arguments);
                        });
                    } else {
                        bindTable.remove(record, function () {
                            dataGrid.Super("removeRecordClick", arguments);
                        });
                    }
                    return;
                }
            }
            this.Super("removeRecordClick", arguments);
        },

        initializeEditValues: function (rowNum, colNum, displayNewValues) {
            var row;
            var record = this.getCurrentRow(rowNum);
            if (record == null)
                record = this.getBindTable().createNewRow();
            this.setEditValues([rowNum, colNum], record, displayNewValues);
        },

        getEditorValueMap: function (field, values) {
            if (values.getValueMap) {
                var valueMap = values.getValueMap(field.name);
                if (valueMap)
                    return valueMap;
            } else if (field.valueMap)
                return field.valueMap;
            this.Super("getEditorValueMap", arguments);
        },

        formatDisplayValue: function (value, record, rowNum, colNum) {
            if (record) {
                var field = this.getField(colNum);
                if (record.components) {
                    var component = record.components[field.name];
                    if (component && component.formatDisplayValue)
                        return component.formatDisplayValue(value, record, rowNum, colNum);
                }
                if (field.formatDisplayValue)
                    return field.formatDisplayValue(value);
                if (record.format)
                    return record.format(field.name, value);
            }
            return value;
        },

        click: function () {
            var bindTable = this.getBindTable();
            if (bindTable) {
                if (this.getData().length == 0 && bindTable.isCreateAble())
                    this.initializeEditValues(0, 0, null);
            }
            this.Super("click", arguments);
        },

        recordDoubleClick: function (dataGrid, record, recordNum, field, fieldNum, value, rawValue) {
            if (this.fireEvent && this.fireEvent(isc.DataGrid.Events.RowDoubleClick, record) == false)
                return false;

            var bindTable = this.getBindTable();
            if (bindTable)
                bindTable.setCurrentRow(record);

            return this.Super("recordDoubleClick", arguments);
        },

        recordClick: function (viewer, record, recordNum, field, fieldNum, value, rawValue) {
            if (this.fireEvent && this.fireEvent(isc.DataGrid.Events.RowClick, record) == false)
                return false;

            var bindTable = this.getBindTable();
            if (bindTable)
                bindTable.setCurrentRow(record);

            return this.Super("recordClick", arguments);
        },

        cloneRecord: function (record) {
            if (record) {
                var cloneRecord = {};
                for (var name in record) {
                    var value = record[name];
                    if (isc.isA.Number(value) || isc.isA.String(value) || isc.isA.Date(value) || isc.isA.Boolean(value))
                        cloneRecord[name] = value;
                }
                return cloneRecord;
            }
        },

        clearData: function () {
            var bindTable = this.getBindTable();
            if (bindTable) {
                if (!bindTable.isRemoveAble())
                    return false;
                bindTable.setData([]);
            }
            if (this.values != undefined || this.values != null)
                delete this.values;
            this.setData([]);
        },

        _removeRecords: function (records) {
            if (records) {
                var bindTable = this.getBindTable();
                if (bindTable && !bindTable.isRemoveAble())
                    return false;

                if (!isc.isA.Array(records))
                    records = [records];
                for (var i = 0; i < records.length; i++) {
                    var record = records [i];
                    if (bindTable)
                        bindTable.remove(record);
                    else if (this.values)
                        this.values.remove(record);
                }
                return false;
            }
        },

        addRecord: function (record) {
            if (record) {
                var bindTable = this.getBindTable();
                if (bindTable && !bindTable.isCreateAble())
                    return false;

                var cloneRecord = this.cloneRecord(record);
                if (bindTable)
                    bindTable.add(cloneRecord);
                else {
                    if (this.values == undefined || this.values == null)
                        this.values = [];
                    this.values.push(cloneRecord);
                }
            }
        },

        addRecords: function (records) {
            if (records) {
                if (!isc.isA.Array(records))
                    records = [records];
                for (var i = 0; i < records.length; i++)
                    this.addRecord(records [i]);
            }
        },

        getGridMembers: function () {
            var gridMembers = this.Super("getGridMembers", arguments);
            if (this.toolBar) {
                gridMembers.push(isc.HLayout.create({
                    width: "100%",
                    height: this.toolBar.height + 1,
                    layoutTopMargin: 1,
                    align: "center",
                    members: [this.toolBar]
                }));
            }
            return gridMembers;
        },

        createRecordComponent: function (record, colNum) {
            var field = this.getField(colNum);
            var component = isc.GridRecordCompomentFactory.createComponent(this, field, record, colNum);
            if (component) {
                if (record.components == undefined || record.components == null)
                    record.components = {};
                record.components[field.name] = component;
                return component;
            }
        },

        showRecordComponent: function (record, colNum) {
            if (this.getEditForm() && this.getEditRecord() == record) {
                if (!this.isReadOnly(colNum))
                    return false;
            }
            return true;
        },

        canEditField: function (field) {
            var bindTable = this.getBindTable();
            if (bindTable)
                return !bindTable.isReadOnly();

            if (field && field.canEdit)
                return field.canEdit;
            return this.canEdit ? this.canEdit : false;
        },

        getFormItemPickData: function (form, formItem) {
            var bindTable = this.getBindTable();
            if (bindTable) {
                var row = bindTable.getCurrentRow();
                if (row != null)
                    return row.getPickData(formItem.name);
            }
        }
    }
});

isc.DataGrid.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%"
});

isc.DataGrid.addMethods({
    initWidget: function () {
        isc.addProperties(this, isc.DataGrid.DefaultGridProperties);
        isc.addProperties(this, isc.DataGrid.DefaultGridMethods);
        this.Super("initWidget", arguments);
        if (!$.defined(this.service))
            this.initialize();
    },

    removeRecords: function (records) {
        return this._removeRecords(records);
    },

    appendRecords: function (source, records, removeSource) {
        if (records == undefined || records == null)
            records = [source.getSelectedRecord()];
        else if (!isc.isA.Array(records))
            records = [records];
        for (var i = 0; i < records.length; i++)
            this.appendRecord(source, records[i], removeSource);
    },

    appendRecord: function (source, record, removeSource) {
        if (removeSource == undefined || removeSource)
            source.removeRecords(record);
        this.addRecord(record);
        this.loadData();
    }
});

/*
 * Widgets GridRecordCompomentFactory
 */
isc.ClassFactory.defineClass("GridRecordCompomentFactory");
isc.GridRecordCompomentFactory.addClassProperties({
    createComponent: function (grid, field, record) {
        if (grid.operateToolBar && field.name == grid.operateToolBar.fieldName)
            return this.createOperateToolBar(grid, field, record);
    },

    createOperateToolBar: function (grid, field, record) {
        var component = isc.HLayout.create({
            autoDraw: false,
            width: "100%",
            height: "100%",
            align: "center"
        });
        var components = grid.operateToolBar.getRecordComponents(record);
        if (components) {
            for (var i = 0; i < components.length; i++)
                component.addMember(components[i]);
        }
        return component;
    }
});

/*
 * Widgets OperateToolBar
 */
isc.ClassFactory.defineClass("OperateToolBar", "Class");
isc.OperateToolBar.addProperties({
    fieldName: "operateField",
    title: isc.i18nButton.operate,
    showRecordComponentsByCell: true
});
isc.OperateToolBar.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
    },

    getDisplayWidth: function () {
        if (this.buttons.length < 2)
            return 5 * 9;
        return this.buttons.length * 2 * 9;
    },

    getRecordComponents: function (record) {
        var components = [];
        if (this.buttons) {
            for (var i = 0; i < this.buttons.length; i++) {
                var imgButton = this.createImgButton(this.buttons[i], record);
                components.push(imgButton);
            }
        }
        return components;
    },

    createImgButton: function (properties, record) {
        var imgButton = isc.ImgButton.create({
            showDown: false,
            showRollOver: false,
            layoutAlign: "center",
            prompt: properties.title,
            height: 16,
            width: 16,
            dataGrid: this.dataGrid,
            click: function () {
                if (this.dataGrid.fireEvent) {
                    var record = this.dataGrid.getSelectedRecord();
                    this.dataGrid.fireEvent(isc.DataGrid.Events.RowButtonClick, this, record);
                }
            }
        }, properties);
        this[properties.name] = imgButton;
        return imgButton;
    }
});

/*
 * Widgets TreeDataGrids
 */
isc.ClassFactory.defineClass("TreeDataGrid", "TreeGrid");
isc.TreeDataGrid.addClassProperties({
    DefaultProperties: {
        alternateRowStyles: false,
        showRowNumbers: false
    }
});
isc.TreeDataGrid.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    showConnectors: false,
    openerImage: "[SKIN]/../tree/opener.gif",
    openerIconSize: 16,
    showFullConnectors: true,
    showOpenIcons: false,
    showDropIcons: false,
    closedIconSuffix: "",
    modelType: "parent",
    nameProperty: "name",
    idField: "id",
    parentIdField: "parentId",
    openAllWhenDataArrived: true,
    loadDataOnDemand: false
});

isc.TreeDataGrid.addMethods({
    initWidget: function () {
        this.initDefaultProperties();
        this.Super("initWidget", arguments);
        if (!$.defined(this.service))
            this.initialize();
        if (this.values)
            this.setData(this.values);
    },

    initDefaultProperties: function () {
        isc.addProperties(this, isc.DataGrid.DefaultGridProperties);
        isc.addProperties(this, isc.DataGrid.DefaultGridMethods);
        isc.addProperties(this, isc.TreeDataGrid.DefaultProperties);
    },

    setData: function (data) {
        if (this.treeData && this.treeData != data && this.treeData.destroy)
            this.treeData.destroy();
        if (isc.isA.Array(data) && data.length > 0) {
            this.treeData = isc.Tree.create({
                    modelType: this.modelType,
                    nameProperty: this.nameProperty,
                    idField: this.idField,
                    parentIdField: this.parentIdField,
                    data: data
                }
            )
            this.treeData.sortByProperty(this.idField, "ascending");
            if (this.openAllWhenDataArrived)
                this.treeData.openAll();
        } else
            this.treeData = isc.Tree.create({});
        this.Super("setData", this.treeData);
    },

    dataArrived: function (parentNode) {
        this.Super("dataArrived", arguments);
        if (this.openAllWhenDataArrived)
            this.getData().openAll();
    },

    getIcon: function (node) {
        if (this.typeValueMap && node.type != undefined) {
            var iconValue = this.typeValueMap[node.type + ""];
            if (iconValue)
                return iconValue;
        }
        return this.Super("getIcon", arguments);
    },

    removeRecords: function (records) {
        if (records) {
            if (!isc.isA.Array(records))
                records = [records];
            var dataTree = this.getData();
            for (var i = 0; i < records.length; i++)
                this.removeRecord(records[i]);
        }
        return records;
    },

    removeRecord: function (record) {
        if (record) {
            var dataTree = this.getData();
            var removeRecord = dataTree.findById(record[dataTree.idField]);
            if (removeRecord) {
                while (1) {
                    var parentRecord = dataTree.getParent(removeRecord);
                    if (dataTree.isRoot(parentRecord) || parentRecord.children.length > 1 || this.removeEmptyParent != true)
                        break;
                    removeRecord = parentRecord;
                }
                var removeRecords = [removeRecord];
                var children = dataTree.getAllNodes(removeRecord);
                if (children)
                    removeRecords = removeRecords.concat(children);
                dataTree.remove(removeRecord);
                this._removeRecords(removeRecords);
            }
        }
    },

    appendRecords: function (source, records, removeSource) {
        if (records == undefined || records == null)
            records = [source.getSelectedRecord()];
        else if (records == source.getData())
            records = [].concat(source.getData().data);
        else if (!isc.isA.Array(records))
            records = [records];
        if (records) {
            for (var i = 0; i < records.length; i++)
                this.appendRecord(source, records[i], removeSource);
        }
    },

    searchRecords: function (source, record, removeRecords, appendRecords) {
        var sourceTree = source.getData();
        var sourceRecord = sourceTree.findById(record[sourceTree.idField]);
        if (sourceRecord) {
            removeRecords.push(sourceRecord);
            var parents = sourceTree.getParents(sourceRecord);
            var dataTree = this.getData();
            if (parents) {
                for (var i = 0; i < parents.length; i++) {
                    var parentRecord = parents[i];
                    if (sourceTree.isRoot(parentRecord))
                        continue;
                    if (dataTree.findById(parentRecord[sourceTree.idField]) == null)
                        appendRecords.push(parentRecord);
                }
            }
            var nodes = sourceTree.getAllNodes(sourceRecord);
            if (nodes) {
                for (var i = 0; i < nodes.length; i++) {
                    var node = nodes[i];
                    if (dataTree.findById(node[dataTree.idField]) == null)
                        appendRecords.push(node);
                }
            }
            if (dataTree.findById(record[dataTree.idField]) == null && appendRecords.indexOf(record) == -1)
                appendRecords.push(record);
        }
    },

    appendRecord: function (source, record, removeSource) {
        if (record) {
            var appendRecords = [];
            var removeRecords = [];
            this.searchRecords(source, record, removeRecords, appendRecords);

            if (removeSource == undefined || removeSource == null)
                source.removeRecords(removeRecords);

            for (var i = 0; i < appendRecords.length; i++)
                this.addRecord(appendRecords[i]);
            this.loadData();
        }
    }
})
;

/*
 * Widgets TreeDataGrids
 */
isc.ClassFactory.defineClass("TreeList", "TreeDataGrid");
isc.TreeList.addClassProperties({
    DefaultProperties: {
        baseStyle: "treeCell",
        showSortArrow: false,
        showHeaderMenuButton: false,
        showHeaderContextMenu: false
    }
});
isc.TreeList.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    removeEmptyParent: true
});
isc.TreeList.addMethods({
    initWidget: function () {
        if (this.fields == undefined || this.fields == null) {
            this.fields = [
                {
                    name: this.nameProperty,
                    title: this.title
                }
            ];
        }
        this.Super("initWidget", arguments);
    },

    initDefaultProperties: function () {
        this.Super("initDefaultProperties", arguments);
        isc.addProperties(this, isc.TreeList.DefaultProperties);
    }
});

/*
 * Widgets Panels
 */
isc.ClassFactory.defineClass("VPanel", "VLayout");
isc.VPanel.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    membersMargin: 1
});

isc.ClassFactory.defineClass("VBorderPanel", "VPanel");
isc.VBorderPanel.addProperties({
    border: "1px solid #ffffff"
});

isc.ClassFactory.defineClass("HPanel", "HLayout");
isc.HPanel.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    membersMargin: 1
});

isc.ClassFactory.defineClass("HBorderPanel", "HPanel");
isc.HBorderPanel.addProperties({
    border: "1px solid #A7ABB4"
});

/*
 * Widgets GridPanel
 */
isc.ClassFactory.defineClass("GridPanel", "VLayout");
isc.GridPanel.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    membersMargin: 0,
    columnSize: 3,
    rowSize: 3
});

isc.GridPanel.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        this.createCellMembers();
    },

    createCellMembers: function () {
        var members = [];
        for (var i = 0; i < this.rowSize; i++)
            this.createColumnMembers(members, i);
        this.addMember(members);
        this.components = members;
    },

    createColumnMembers: function (members, row) {
        var hLayoutProperties = {
            autoDraw: false,
            width: "100%",
            membersMargin: 0,
            height: (100 / this.rowSize) + "%",
            membersMargin: this.membersMargin
        };
        if (this.cellHeight != undefined && this.cellHeight != null)
            hLayoutProperties.height = this.cellHeight;

        var columnMembers = [];
        for (var i = 0; i < this.columnSize; i++) {
            var cellLayoutProperties = {
                autoDraw: false,
                width: Math.floor(100 / this.columnSize - 10) + "%",
                height: "100%",
                border: this.cellBorder
            };
            if (this.cellWidth) {
                if (isc.isA.Array(this.cellWidth))
                    hLayoutProperties.width = this.cellWidth[i];
                else
                    hLayoutProperties.width = this.cellWidth;
            }
            if (this.cellMembers && this.cellMembers[row][i])
                cellLayoutProperties.members = [this.cellMembers[row][i]];
            columnMembers.push(isc.Layout.create(cellLayoutProperties));
        }
        hLayoutProperties.members = columnMembers;
        var hLayout = isc.HLayout.create(hLayoutProperties);
        members.push(hLayout);
        return hLayout;
    },

    addCellMember: function (row, column, member) {
        var layout;
        if (row < this.components.length)
            layout = this.components[row].members[column];
        else {
            var component = this.createColumnMembers(this.components, this.components.length);
            if (component) {
                this.addMember(component);
                layout = component.members[column];
            }
        }
        if (layout)
            layout.addMember(member);
    }
});

/*
 * Widgets Menus
 */
isc.defineClass("MenuExt", "Menu");
isc.MenuExt.addClassProperties({
    MenuItemDefaultProperties: {
        setTitle: function (title) {
            this.menu.setItemTitle(this, title);
        }
    },

    SeparatorProperties: {
        autoFit: false,
        isSeparator: true,
        cssText: "height: 2px;"
    }
});
isc.MenuExt.addProperties({
    autoDraw: false,
    autoFit: true,
    showShadow: true,
    shadowDepth: 10,
    cellPadding: 5
});
isc.MenuExt.addMethods({
    initWidget: function () {
        this.initMenuData(this.menuData.data);
        isc.addProperties(this, this.menuData);
        this.Super("initWidget", arguments);
        this.initMenuItems(this);
    },

    initMenuData: function (data) {
        if (isc.isA.Array(data)) {
            for (var i = 0; i < data.length; i++) {
                if (data[i] == "separator" || data[i].isSeparator)
                    data[i] = isc.MenuExt.SeparatorProperties;
                else if (data[i].submenu)
                    this.initMenuData(data[i].submenu);
            }
        }
    },

    initMenuItems: function (menu) {
        var menuItems = menu.getItems();
        if (menuItems) {
            for (var i = 0; i < menuItems.length; i++) {
                var menuItem = menuItems[i];
                menuItem.owner = this;
                if (this.toolBar)
                    menuItem.toolBar = this.toolBar;
                var subMenu = menu.getSubmenu(menuItem);
                if (subMenu)
                    this.initMenuItems(subMenu);
                else
                    isc.addProperties(menuItem, {menu: menu}, isc.MenuExt.MenuItemDefaultProperties);
            }
        }
    },

    setEnabled: function (name, value) {
        var menuItems = this.getItems();
        for (var i = 0; i < menuItems.length; i++) {
            var menuItem = menuItems[i];
            if (menuItem.name == name) {
                this.setItemEnabled(menuItem, value);
                return;
            }
        }
    },

    getBaseStyle: function (record, rowNum, colNum) {
        if (record && record[this.isSeparatorProperty])
            return "menuSeparator";
        return this.Super("getBaseStyle", arguments);
    },

    getCellValue: function (record, recordNum, fieldNum, gridBody) {
        if (record && record[this.isSeparatorProperty])
            return "&nbsp;";
        return this.Super("getCellValue", arguments);
    },

    itemClick: function (item, colNum) {
        if (item.toolBar) {
            var items = item.owner.findItems(item.name);
            if (items.length > 0) {
                var button = items[0];
                if (button.actionType == "checkbox") {
                    button.selected = !button.selected;
                    item.menu.markForRedraw();
                }
                else if (button.checked != undefined && button.checked != null) {
                    button.checked = !button.checked;
                    item.menu.markForRedraw();
                }
                item.toolBar.itemClick(item, colNum);
            }
        }
        else
            this.Super("itemClick", arguments);
    },

    findItems: function (itemNames, menu) {
        if (menu == undefined)
            menu = this;

        if (!isc.isA.Array(itemNames))
            itemNames = [itemNames];

        var found = [];
        var menuItems = menu.getItems();
        if (menuItems) {
            for (var i = 0; i < menuItems.length; i++) {
                var menuItem = menuItems[i];
                var subMenu = menu.getSubmenu(menuItem);
                if (subMenu) {
                    var children = this.findItems(itemNames, subMenu);
                    for (var j = 0; j < children.length; j++)
                        found[found.length] = children[j];
                } else {
                    for (var j = 0; j < itemNames.length; j++) {
                        if (menuItem.name == itemNames[j]) {
                            found[found.length] = menuItem;
                            break;
                        }
                    }
                }
            }
        }
        return found;
    }
});

/*
 * Widgets MenuBar
 */
isc.ClassFactory.defineClass("MenuBarEx", "MenuBar");

/*
 * Widgets FlatButton
 */
isc.ClassFactory.defineClass("FlatButton", "Button");
isc.FlatButton.addProperties({
    autoDraw: false,
    baseStyle: "flatbutton",
    autoFit: true
});

/*
 * Widgets ToolButton
 */
isc.ClassFactory.defineClass("ToolButton", "AutoFitButton");
isc.ToolButton.addClassProperties({
    ButtonTypes: {
        ModifyPassword: {
            name: "modifyPassword",
            permission: "changePassword",
            title: isc.i18nButton.modifyPassword
        },
        ResetPassword: {
            name: "resetPassword",
            permission: "resetPassword",
            title: isc.i18nButton.resetPassword
        },
        Config: {
            name: "config",
            permission: "config",
            title: isc.i18nButton.config
        },
        Login: {
            name: "login"
        },
        Reset: {
            name: "reset"
        },
        Ok: {
            name: "ok"
        },
        Filter: {
            name: "filter"
        },
        Save: {
            name: "save"
        },
        Cancel: {
            name: "cancel",
            click: function () {
                this.service.close();
            }
        },
        Stop: {
            name: "stop"
        },
        Upload: {
            name: "upload"
        },
        Exit: {
            name: "exit",
            click: function () {
                this.service.close();
            }
        }
    }
});
isc.ToolButton.addProperties({
    autoDraw: false,
    showIcon: true,
    baseStyle: "toolButton"
});

isc.ToolButton.addMethods({
    initWidget: function () {
        if (this.title == "Untitled Button") {
            var title = isc.i18nButton[this.name];
            if (title != undefined && title != null)
                this.title = title;
        }
        if (this.showIcon && !$.defined(this.icon))
            this.icon = "[SKIN]/button/" + this.name + ".png";
        this.Super("initWidget", arguments);
    }
});

/*
 * Widgets MenuButtonEx
 */
isc.ClassFactory.defineClass("MenuButtonEx", "MenuButton");
isc.MenuButtonEx.addProperties({
    autoDraw: false,
    baseStyle: "toolButton",
    showMenuButtonImage: false,
    iconOrientation: "left",
    autoFit: true,
    iconSize: 16,
    iconHeight: 16,
    iconWidth: 16
});

isc.MenuButtonEx.addMethods({
    initWidget: function () {
        if (this.data) {
            this.menu = isc.MenuExt.create({
                menuData: {
                    data: this.data
                },
                toolBar: this.toolBar
            });
        }
        this.Super("initWidget", arguments);
    }
});

/*
 * Widgets ToolBarEx
 */
isc.ClassFactory.defineClass("ToolBarEx", "Toolbar");
isc.ToolBarEx.addClassProperties({
    Events: {
        ItemClick: "ItemClick",
        CheckState: "CheckState",
        ItemChanged: "ItemChanged"
    }
});
isc.ToolBarEx.addProperties({
    autoDraw: false,
    width: "100%",
    height: 32,
    styleName: "toolBar",
    membersMargin: 5,
    layoutRightMargin: 4,
    layoutLeftMargin: 4,
    layoutTopMargin: 4,
    layoutBottomMargin: 4,
    align: "right"
});

isc.ToolBarEx.addMethods({
    initialize: function () {
        this.Super("initialize", arguments);

        this.data = this.checkPermissions(this.data);
        if (this.data != null) {
            for (var i = 0; i < this.data.length; i++) {
                var buttonData = isc.addProperties({
                    autoDraw: false,
                    service: this.service,
                    toolBar: this
                }, this.data[i]);
                var type = buttonData.type ? buttonData.type : isc.ToolButton;
                var button = isc.ClassFactory.newInstance(type, this.buttonDefaults, this.buttonProperties, buttonData);
                if (button != null) {
                    this.addButtons(button);
                    this[button.name] = button;
                }
            }
        }
    },

    checkPermissions: function (data) {
        if (data) {
            if (isc.isA.Array(data)) {
                var array = [];
                for (var i = 0; i < data.length; i++) {
                    var item = this.checkPermissions(data[i]);
                    if (item) {
                        if (array.length == 0 || array[array.length - 1] != item)
                            array.push(item);
                    }
                }
                if (array.length > 0)
                    return array;
            } else if (this.hasPermission(data)) {
                if (data.type == isc.MenuButtonEx) {
                    var menuData = this[data.name + "MenuData"];
                    if (menuData)
                        data.data = menuData;
                    data.data = this.checkPermissions(data.data);
                    if (data.data && data.data.length > 0)
                        return data;
                } else if (data.submenu) {
                    data.submenu = this.checkPermissions(data.submenu);
                    if (data.submenu && data.submenu.length > 0)
                        return data;
                } else
                    return data;
            }
        }
    },

    itemChanged: function (item, itemNum) {
        this.fireEvent(isc.ToolBarEx.Events.ItemChanged, item);
        this.Super("itemChanged", arguments);
    },

    itemClick: function (item, itemNum) {
        this.fireEvent(isc.ToolBarEx.Events.ItemClick, item);
        this.Super("itemClick", arguments);
    }
});

/*
 * Widgets ModuleLoader
 */
isc.ClassFactory.defineClass("ModuleLoader", "ViewLoader");
isc.ModuleLoader.addProperties({
    module: null,
    autoDraw: false
});

isc.ModuleLoader.addMethods({
    initWidget: function () {
        this.title = this.module.title;
        this.viewURL = this.module.getUrl();

        this.loadingMessage = "${loadingImage}&nbsp;&nbsp;<span class=\"throbberText\">" + isc.i18nInfo.loading;
        if (this.title != undefined)
            this.loadingMessage += this.title;
        this.loadingMessage += "...</span>";

        this.Super("initWidget", arguments);
    },

    handleError: function (rpcRequest, rpcResponse) {
        this.Super("handleError", arguments);
    },

    viewLoaded: function (view) {
        this.Super("viewLoaded", arguments);
        view.module = this.module;
        if (this.loadedCallBack != undefined)
            this.loadedCallBack(view);
        if (view.initialize != undefined)
            view.initialize();
    }
});

/*
 * Widgets TabLayout
 */
isc.ClassFactory.defineClass("TabLayout", "TabSet");
isc.TabLayout.addClassProperties({
    DefaultTabProperties: {
        iconSize: 16,
        canClose: false
    }
});
isc.TabLayout.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    paneMargin: 1
});
isc.TabLayout.addMethods({
    addMembers: function (members) {
        if (members) {
            for (var i = 0; i < members.length; i++) {
                var member = members[i];
                var tab = isc.addProperties({
                    autoDraw: false,
                    title: member.title,
                    name: member.name,
                    icon: member.icon
                }, isc.TabLayout.DefaultTabProperties);
                tab.pane = member;
                this.addTabs(tab);
            }
        }
    },

    findTab: function (tab) {
        if (tab != undefined && tab != null) {
            if (isc.isA.Number(tab))
                return this.getTabObject(tab);
            if (isc.isA.String(tab)) {
                for (var i = 0; i < this.tabs.length; i++) {
                    if (this.tabs[i].name == tab)
                        return this.tabs[i];
                }
            }
        }
        return tab;
    },

    findVisibleTab: function (tabIndex) {
        for (var i = tabIndex; i < this.tabs.length; i++) {
            var button = this.tabBar.getButton(i);
            if (button && button.isVisible())
                return this.tabs[i];
        }
        if (tabIndex != 0)
            return this.findVisibleTab(0);
    },

    selectTab: function (tab) {
        var tabIndex = this.getTabNumber(tab);
        var button = this.tabBar.getButton(tabIndex);
        if (button && !button.isVisible())
            tab = this.findVisibleTab(tabIndex);
        this.Super("selectTab", tab);
    },

    hideTab: function (tab) {
        var foundTab = this.findTab(tab);
        if (foundTab) {
            var num = this.getTabNumber(foundTab);
            var button = this.tabBar.getButton(num);
            if (button) {
                button.hide();
                this.getTabPane(foundTab).hide();
            }
            if (this.getSelectedTab() == foundTab)
                this.selectTab(foundTab);
        }
    },

    showTab: function (tab) {
        var foundTab = this.findTab(tab);
        if (foundTab) {
            var num = this.getTabNumber(foundTab);
            var button = this.tabBar.getButton(num);
            if (button)
                button.show();
        }
    }
});

isc.ClassFactory.defineClass("ModuleTabSet", "TabLayout");
isc.ModuleTabSet.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);

        // home Tab
        if (this.homeModule) {
            this.homeModule = isc.ModuleManager.createModule(this.homeModule);
            this.createModuleTab(this.homeModule);
        }
    },

    findModuleTab: function (module) {
        for (var i = 0; i < this.tabs.length; i++) {
            var tab = this.tabs[i];
            if (tab.title == module.title)
                return tab;
        }
        return null;
    },

    createModuleTab: function (module) {
        var properties = isc.addProperties({ module: module }, module.params);
        var classType = isc.ClassFactory.getClass(module.url);
        if (classType) {
            classType.open(module, properties);
            return;
        }

        var tab = isc.addProperties({autoDraw: false}, isc.TabLayout.DefaultTabProperties);
        isc.addProperties(tab, {
            title: module.title,
            icon: module.icon,
            module: module
        });
        tab.pane = isc.ModuleLoader.create(properties);
        this.addTabs(tab);
        return tab;
    },

    loadModule: function (menuItem) {
        if ($.defined(menuItem.submenu))
            return;
        var moduleTabSet = this;
        var moduleTab = this.findModuleTab(menuItem);
        if (moduleTab == null) {
            var module = {
                title: menuItem.title,
                name: menuItem.name,
                icon: menuItem.icon,
                jndiName: menuItem.jndiName,
                baseUrl: isc.ApplicationContext.baseUrl,
                url: menuItem.url,
                close: function (success) {
                    moduleTabSet.closeModule(this, success);
                }
            };
            if (menuItem.params)
                module.params = menuItem.params;
            module = isc.ModuleManager.createModule(module);
            moduleTab = this.createModuleTab(module);
        }
        if (moduleTab)
            this.selectTab(moduleTab);
    },

    closeModule: function (module, success) {
        var moduleTabSet = this;
        var moduleTab = this.findModuleTab(module);
        if (moduleTab) {
            isc.ask(isc.i18nAsk.exit, function (value) {
                    if (value) {
                        this.moduleTabSet.closeClick(moduleTab);
                        if (success)
                            success();
                    }
                },
                {
                    moduleTabSet: this,
                    moduleTab: moduleTab
                }
            );
        }
    }
});

/*
 * Widgets ModuleManager
 */
isc.ClassFactory.defineClass("ModuleManager");
isc.ModuleManager.addClassProperties({
    ModuleDefaultProperties: {
        getUrl: function () {
            var url = "";
            if (this.jndiName) {
                if (this.baseUrl)
                    url = this.baseUrl + "/";
                if (!url.endsWith("/"))
                    url += "/";
                url += this.jndiName + "/";
            }
            if (this.url) {
                if (this.url.indexOf("/") != -1)
                    url = this.url;
                else
                    url += this.url;
            }
            else if (this.name)
                url += this.name + "Main.jsf";
            if (url.length > 0)
                return url;
        }
    },

    DefaultLayoutProperties: {
        initialize: function () {
            this.service.module = this.module;
            this.service.initialize();
            this.initComponent();
        },

        getComponents: function () {
            var components = [];
            var layoutProperties = this.getLayoutProperties();
            if (layoutProperties)
                components = this.createComponents(layoutProperties);
            return components;
        },

        getLayoutProperties: function () {
            return this.DefaultLayoutProperties;
        },

        initComponent: function () {
            this.service.initComponent();
        },

        createComponents: function (layoutProperties) {
            if (isc.isA.Array(layoutProperties)) {
                var components;
                for (var i = 0; i < layoutProperties.length; i++) {
                    var component = this.createComponents(layoutProperties[i]);
                    if (component) {
                        if (components == undefined || components == null)
                            components = [component];
                        else
                            components[components.length] = component;
                    }
                }
                if (components)
                    return components;
            }
            else if (layoutProperties.classType != undefined) {
                var component = this.createComponent(layoutProperties.classType, layoutProperties.properties, layoutProperties.name);
                if (component) {
                    if (layoutProperties.members) {
                        var members = this.createComponents(layoutProperties.members);
                        if (members)
                            component.addMembers(members)
                    }
                    return component;
                }
            }
        },

        getDefaultServiceProperties: function (classType, defaultServiceProperties) {
            if (classType && classType.getSuperClass() != isc.Class) {
                defaultServiceProperties = this.getDefaultServiceProperties(classType.getSuperClass(), defaultServiceProperties);
                if (classType.DefaultServiceProperties) {
                    if (defaultServiceProperties == undefined || defaultServiceProperties == null)
                        defaultServiceProperties = {};
                    defaultServiceProperties = isc.addProperties(defaultServiceProperties, classType.DefaultServiceProperties);
                }
            }
            return defaultServiceProperties;
        },

        createService: function (properties) {
            var defaultServiceProperties = this.getDefaultServiceProperties(this.getClass());
            var serviceProperties = isc.addProperties({owner: this }, defaultServiceProperties);
            if (this.DefaultServiceProperties)
                isc.addProperties(serviceProperties, this.DefaultServiceProperties);
            if (this.serviceProperties)
                isc.addProperties(serviceProperties, this.serviceProperties);
            return isc.Service.create(serviceProperties);
        },

        createComponent: function (classType, properties, name) {
            var component = this.service.createComponent(classType, properties, name);
            if (name && this[name] == undefined)
                this[name] = component;
            return component;
        },

        destroy: function () {
            this.service.destroy();
            this.Super("destroy", arguments);
        }
    }
});

isc.ModuleManager.addClassMethods({
    createModule: function (module) {
        isc.addProperties(module, isc.ModuleManager.ModuleDefaultProperties);
        if (module.getUrl() == undefined) {
            module = isc.addProperties({
                name: "User",
                title: module.title,
                baseUrl: "./Modules/",
                jndiName: "Demo",
                url: "UserMain.js",
                close: module.close
            }, isc.ModuleManager.ModuleDefaultProperties);
        }
        //if (!module.icon)
        //module.icon = "../images/button/modify.png";
        return module;
    }
});

/*
 * Widgets windows
 */
isc.ClassFactory.defineClass("DialogWindow", "Window");
isc.DialogWindow.addClassProperties({
    DefaultServiceProperties: {
        close: function () {
            if (this.onClosing) {
                var service = this;
                this.onClosing(function () {
                    service._closeService();
                })
            }
            else
                this._closeService();
        },

        _closeService: function () {
            if (this.owner) {
                this.owner.hide();
                if (this.result && this.owner.success)
                    this.owner.success();
                this.destroy();
            }
        }
    }
});
isc.DialogWindow.addClassMethods({
    open: function (sender, windowProperties) {
        var properties = {
            title: sender.title,
            icon: sender.icon
        };
        if (windowProperties)
            isc.addProperties(properties, windowProperties);
        var window = this.create(properties);
        window.show();
        return window;
    }
});
isc.DialogWindow.addProperties({
    autoDraw: false,
    autoSize: false,
    autoCenter: true,
    isModal: true,
    showModalMask: false,
    bodyProperties: {
        layoutMargin: 1,
        membersMargin: 1
    },
    icon: null,
    buttons: [],
    items: []
});
isc.DialogWindow.addMethods(isc.ModuleManager.DefaultLayoutProperties);
isc.DialogWindow.addMethods({
    initWidget: function () {
        this.service = this.createService();
        if (this.icon)
            this.headerIconDefaults.src = this.icon;
        this.Super("initWidget", arguments);
        this.items = this.createItems();

        this.initialize();
    },

    show: function () {
        this.Super("show");
        if (this.onShown)
            this.onShown();
    },

    close: function () {
        if (this.service)
            this.service.close();
        this.Super("close");
    },

    createItems: function () {
        var components = this.getComponents();
        if (this.buttons.length > 0) {
            this.toolBar = this.createComponent(isc.ToolBarEx, {
                data: this.buttons,
                service: this.service,
                window: this
            }, "toolBar");
            components[components.length] = this.toolBar;
        }
        return components;
    }
});

/*
 ModuleWindow
 */
isc.ClassFactory.defineClass("ModuleWindow", "Window");
isc.ModuleWindow.addClassMethods({
    open: function (sender, url, title, moduleProperties, windowProperties) {
        var module;
        if (sender.module)
            module = isc.addProperties({}, sender.module);
        else if (sender.owner && sender.owner.module)
            module = isc.addProperties({}, sender.owner.module);
        if (module) {
            module.url = url;
            var properties = {
                title: module.title + "-" + title,
                module: module,
                moduleProperties: moduleProperties
            };
            if (windowProperties)
                isc.addProperties(properties, windowProperties);
            isc.ModuleWindow.create(properties).show();
            return true;
        }
    }
});

isc.ModuleWindow.addProperties({
    autoDraw: false,
    autoSize: false,
    autoCenter: true,
    isModal: true,
    showModalMask: false,
    width: 800,
    height: 600,
    bodyProperties: {
        layoutMargin: 1,
        membersMargin: 1
    },
    canDragResize: true,
    showResizer: true
});

isc.ModuleWindow.addMethods({
    initWidget: function () {
        this.addItems(this.getComponents());
        this.Super("initWidget", arguments);
        if (this.icon)
            this.headerIconDefaults.src = this.icon;
    },

    getComponents: function () {
        if (!this.panel) {
            var moduleProperties = {window: this};
            if (this.moduleProperties)
                isc.addProperties(moduleProperties, this.moduleProperties);
            this.panel = isc.ModuleLoader.create({
                module: this.module,
                loadedCallBack: function (view) {
                    isc.addProperties(view, moduleProperties);
                }
            });
        }
        return this.panel;
    }
});

/*
 ModuleForm
 */
isc.ClassFactory.defineClass("ModuleForm", "VPanel");
isc.ModuleForm.addClassProperties({
    DefaultServiceProperties: {
        remove: function (table, row, success, error) {
            if (table == this.dataSet.getNavigate()) {
                isc.ask(isc.i18nAsk.remove, function (value) {
                        if (value) {
                            table.remove(row, success, error);
                        }
                    }
                );
            }
            else
                table.remove(row, success, error);
        },

        close: function () {
            if (this.onClosing) {
                var service = this;
                this.onClosing(function () {
                    service._closeService();
                })
            }
            else
                this._closeService();
        },

        _closeService: function () {
            var owner = this.owner,
                moduleForm = this;

            if (owner.window) {
                owner.window.closeClick();
                if (moduleForm.result && owner.success)
                    owner.success();
                moduleForm.destroy();
            }
            else if (owner.module) {
                owner.module.close(function () {
                    if (moduleForm.result && owner.success)
                        owner.success();
                    moduleForm.destroy();
                });
            }
        }
    }
});

isc.ModuleForm.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    service: null
});

isc.ModuleForm.addMethods(isc.ModuleManager.DefaultLayoutProperties);
isc.ModuleForm.addMethods({
    initWidget: function () {
        this.service = this.createService();
        this.addMembers(this.getComponents());
        this.Super("initWidget", arguments);
    }
});

/*
 SelectPanel
 */
isc.ClassFactory.defineClass("SelectPanel", "HPanel");
isc.SelectPanel.addClassProperties({
    DefaultComponentProperties: {
        autoDraw: false,
        width: "50%",
        height: "100%"
    },
    Events: {
        OnSelect: "OnSelect",
        OnSelectAll: "OnSelectAll"
    }
});
isc.SelectPanel.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%"
});
isc.SelectPanel.addMethods({
    initWidget: function () {
        this.addProperties(isc.Table.DefaultLocalDataProperties);
        this.addMembers(this.getComponents());
        this.Super("initWidget", arguments);
    },

    table_BeforeTableValidate: function (table) {
        table.setData(this.getSelectedData());
    },

    table_BeforeLoad: function (table) {
        this.refreshDeselectData();
    },

    table_BeforeInsert: function (table) {
        this.refreshDeselectData();
    },

    table_AfterLoad: function (table) {
        this.setSelectedData(table.rows);
    },

    table_AfterInsert: function (table) {
        this.setSelectedData(table.rows);
    },

    table_AfterSave: function (table) {
        this.loadDeselectData();
        this.setSelectedData(table.rows);
    },

    refreshDeselectData: function () {
        delete this.selectedData;
        delete this.deselectData;
        this.loadDeselectData();
    },

    getDataLoader: function () {
        if (this.dataLoader == undefined || this.dataLoader == null)
            this.dataLoader = isc.DataLoader.create({
                jndiName: this.jndiName,
                dataURL: this.dataURL
            });
        return this.dataLoader;
    },

    getBindTable: function () {
    },

    loadDeselectData: function () {
        var dataLoader = this.getDataLoader();
        if (dataLoader) {
            var component = this;
            dataLoader.load(function (responseData) {
                component.setDeselectData(responseData);
            });
        }
    },

    isReadOnly: function () {
        var bindTable = this.getBindTable();
        if (bindTable)
            return bindTable.isReadOnly();
        return false;
    },

    changeDataState: function () {
        var bindTable = this.getBindTable();
        if (bindTable) {
            if (bindTable.dataState == isc.Table.DataStates.Browse)
                bindTable.setDataState(isc.Table.DataStates.Modified);
        }
    },

    setSelectedData: function (selectedData) {
        this.selectedData = selectedData;
        this.loadSelectedData();
    },

    setDeselectData: function (deselectData) {
        this.deselectData = deselectData;
        this.loadSelectedData();
    },

    getSelectedData: function () {
        return this.selectedComponent.getData();
    },

    loadSelectedData: function () {
        var selectedData = this.selectedData;
        var deselectData = this.deselectData;
        if (selectedData && deselectData) {
            this.deselectComponent.clearData();
            this.deselectComponent.values = deselectData;
            this.deselectComponent.setData(deselectData);
            this.selectedComponent.clearData();
            this.selectedComponent.appendRecords(this.deselectComponent, selectedData);
        }
    },

    getComponents: function () {
        var members = [];
        var deselectComponent = this.getDeselectComponent();
        members.push(deselectComponent);
        members.push(this.getButtonsPanel());
        var selectedComponent = this.getSelectedComponent();
        members.push(selectedComponent);
        return members;
    },

    getButtonsPanel: function () {
        return isc.VBorderPanel.create({
            width: "40",
            height: "100%",
            align: "center",
            defaultLayoutAlign: "center",
            layoutBottomMargin: 30,
            membersMargin: 10,
            members: this.getButtons()
        });
    },

    getDeselectComponent: function () {
        var componentProperties = isc.addProperties({
            name: "deselectComponent",
            title: isc.i18nTitle.Deselect
        }, isc.clone(this.deselectComponentProperties));
        return this.createComponent(componentProperties);
    },

    getSelectedComponent: function () {
        var componentProperties = isc.addProperties({
            name: "selectedComponent",
            title: isc.i18nTitle.Selected
        }, isc.clone(this.selectedComponentProperties));
        return this.createComponent(componentProperties);
    },

    createComponent: function (componentProperties) {
        if (componentProperties) {
            var properties = isc.addProperties({
                typeValueMap: this.typeValueMap
            }, isc.SelectPanel.DefaultComponentProperties, this.componentProperties, componentProperties);
            var component = this.service.createComponent(this.componentType, properties);
            component.bindListener(this);
            this[component.name] = component;
            return component;
        }
    },

    itemClick: function (button) {
        if (!this.isReadOnly()) {
            if (button.name == "selectAll")
                this.selectedComponent.appendRecords(this.deselectComponent, this.deselectComponent.getData());
            else if (button.name == "select")
                this.selectedComponent.appendRecords(this.deselectComponent);
            else if (button.name == "deselectAll") {
                var canSelect = this.fireEvent(isc.SelectPanel.Events.OnSelectAll, this.selectedComponent.getData());
                if (canSelect == undefined || canSelect == true)
                    this.deselectComponent.appendRecords(this.selectedComponent, this.selectedComponent.getData());
            }
            else if (button.name == "deselect") {
                var canSelect = this.fireEvent(isc.SelectPanel.Events.OnSelect, this.selectedComponent.getData(), this.selectedComponent.getSelectedRecord());
                if (canSelect == undefined || canSelect == true)
                    this.deselectComponent.appendRecords(this.selectedComponent);
            }
            this.changeDataState();
        }
    },

    deselectComponent_RowDoubleClick: function (component, record) {
        if (!this.isReadOnly()) {
            this.selectedComponent.appendRecord(component, record);
            this.changeDataState();
        }
        return false;
    },

    selectedComponent_RowDoubleClick: function (component, record) {
        var canSelect = this.fireEvent(isc.SelectPanel.Events.OnSelect, this.selectedComponent.getData(), this.selectedComponent.getSelectedRecord());
        if (!this.isReadOnly() && (canSelect == undefined || canSelect == true)) {
            this.deselectComponent.appendRecord(component, record);
            this.changeDataState();
        }
        return false;
    },

    createButton: function (properties) {
        var parent = this;
        return isc.Img.create({
            width: 24,
            height: 24,
            cursor: "pointer",
            click: function () {
                parent.itemClick(this);
            }
        }, properties)
    },

    getButtons: function () {
        var buttons = [];
        buttons.push(this.createButton({
            name: "selectAll",
            src: "[SKIN]/button/selectAll.png"
        }));
        buttons.push(this.createButton({
            name: "select",
            src: "[SKIN]/button/select.png"
        }));
        buttons.push(this.createButton({
            name: "deselect",
            src: "[SKIN]/button/deselect.png"
        }));
        buttons.push(this.createButton({
            name: "deselectAll",
            src: "[SKIN]/button/deselectAll.png"
        }));
        return buttons;
    }
});
/*
 * TreeSelect
 */
isc.ClassFactory.defineClass("TreeSelect", "SelectPanel");
isc.TreeSelect.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    componentType: isc.TreeList
});
isc.TreeSelect.addMethods({
    getSelectedData: function () {
        var tree = this.selectedComponent.getData();
        if (tree != undefined && tree != null) {
            var data = tree.data;
            if (this.onlySaveLeaves == true) {
                var leaves = [];
                for (var i = 0; i < data.length; i++) {
                    var node = data[i]
                    if (tree.isLeaf(node))
                        leaves.push(node);
                }
                return leaves;
            }
            return data;
        }
    }
});
/*
 * UnitValueCompoment
 */
isc.ClassFactory.defineClass("UnitValueCompoment");
isc.UnitValueCompoment.addClassProperties({
    create: function (compoment) {
        if (compoment.unitValueProperties != undefined && compoment.unitValueProperties.getUnitValue != undefined)
            return compoment.unitValueProperties;

        var unitValueProperties = isc.addProperties({}, isc.UnitValueCompoment.DefaultProperties);
        var editorProperties = compoment;
        if (compoment.editorProperties)
            editorProperties = compoment.editorProperties;

        if (editorProperties.unitValueProperties)
            isc.addProperties(unitValueProperties, editorProperties.unitValueProperties);

        for (var name in isc.UnitValueCompoment.DefaultProperties) {
            var property = editorProperties[name];
            if (property != undefined && property != null)
                unitValueProperties[name] = property;
        }
        compoment.unitValueProperties = unitValueProperties;
        return unitValueProperties;
    },

    DefaultProperties: {
        autoDraw: false,
        format: "#.###",
        unitAlign: "right",
        minUnit: null,
        maxUnit: null,
        defaultUnit: null,
        saveUnit: null,

        createValues: function (value) {
            var valueResult;
            var unitResult;
            if (value != undefined && value != null) {
                var unitValues = this.getUnitValues();
                if (isc.isA.String(value))
                    value = parseFloat(value);
                if (!isNaN(value)) {
                    var saveUnit = this.getSaveUnit();
                    if (saveUnit)
                        value = value * saveUnit;

                    for (var i = 0; i < unitValues.length; i++) {
                        var rate = unitValues[i];
                        if (valueResult == undefined || valueResult == undefined) {
                            valueResult = value / rate;
                            unitResult = rate;
                        } else if (Math.floor(value / rate) != 0) {
                            valueResult = value / rate;
                            unitResult = rate;
                        }
                        else
                            break;
                    }
                }
            }
            if (valueResult != undefined && valueResult != null)
                valueResult = parseFloat(valueResult.format(this.format));
            if (unitResult == undefined || unitResult == null)
                unitResult = this.getDefaultUnit();
            return {
                value: valueResult,
                unit: unitResult
            };
        },

        createFormatValues: function (value) {
            var valueText = "";
            var unitText = "";
            var values = this.createValues(value);
            if (values) {
                if (values.value != undefined && values.value != null)
                    valueText = values.value.format(this.format);
                if (values.unit)
                    unitText = this.valueMap[values.unit];
            }
            return {
                value: valueText,
                unit: unitText
            };
        },

        calculateValue: function (values) {
            if (values) {
                var value = values.value;
                if (value != undefined && value != null) {
                    if (isc.isA.String(value))
                        value = parseFloat(value);
                    if (values.unit)
                        value = value * values.unit;
                    return this.calculateSaveValue(value);
                }
            }
        },

        calculateSaveValue: function (value) {
            if (value != undefined && value != null) {
                var saveUnit = this.getSaveUnit();
                if (saveUnit != undefined && saveUnit != null)
                    value = value / saveUnit;
                value = Math.round(value);
            }
            return value;
        },

        getUnitWidth: function () {
            if (this.unitWidth != undefined && this.unitWidth != null)
                return this.unitWidth;
            return this.defaultUnitWidth;
        },

        getUnitName: function (value) {
            if (value != undefined && value != null) {
                if (isc.isA.Number(value))
                    value = value + "";
                for (var name in this.valueMap) {
                    if (name == value)
                        return this.valueMap[name];
                }
            }
        },

        getUnitValue: function (value) {
            if (value != undefined && value != null) {
                if (isc.isA.String(value)) {
                    for (var name in this.valueMap) {
                        if (this.valueMap[name] == value)
                            return parseInt(name);
                    }
                } else if (isc.isA.Number(value))
                    return value;
            }
        },

        getMinUnit: function () {
            return this.getUnitValue(this.minUnit);
        },

        getMaxUnit: function () {
            return this.getUnitValue(this.maxUnit);
        },

        getSaveUnit: function () {
            return this.getUnitValue(this.saveUnit);
        },

        getUnitValueMap: function () {
            var unitValues = this.getUnitValues();
            var valueMap = {};
            for (var i = 0; i < unitValues.length; i++) {
                var value = unitValues[i] + "";
                valueMap[value] = this.valueMap[value];
            }
            return valueMap;
        },

        getUnitValues: function () {
            var unitValues = [];
            var minUnit = this.getMinUnit();
            var maxUnit = this.getMaxUnit();
            if (minUnit || maxUnit) {
                for (var name in this.valueMap) {
                    var value = parseInt(name);
                    if (maxUnit && value > maxUnit)
                        continue;
                    if (minUnit && value < minUnit)
                        continue;
                    unitValues.push(value);
                }
            }
            else {
                for (var name in this.valueMap)
                    unitValues.push(parseInt(name));
            }
            unitValues.sort(function (a, b) {
                return a > b ? 1 : -1;
            });
            return unitValues;
        },

        getDefaultUnit: function () {
            return this.getUnitValue(this.defaultUnit);
        }
    }
});

/*
 * CustomItem
 */
isc.ClassFactory.defineClass("ComponentItem", "CanvasItem");
isc.ComponentItem.addProperties({
    autoDraw: false,
    canFocus: true,
    shouldSaveValue: true,
    height: 22
});
isc.ComponentItem.addMethods({
    createCanvas: function () {
        var component = this.getComponent();
        if (this.hidden)
            this.hide();
        return this.getComponent();
    },

    _itemChange: function (item, value, oldValue) {
        if (this.form.itemChange) {
            var itemValue = this.getItemValue(item, value);
            var oldItemValue = this.getItemValue(item, oldValue);
            return this.form.itemChange(this, itemValue, oldItemValue);
        }
        return true;
    },

    _itemChanged: function (item, value) {
        var itemValue = this.getItemValue(item, value);
        var displayValue = this.getDisplayValue(itemValue);
        var currentValue = this.getDisplayValue(this.getValue());
        if (displayValue != currentValue) {
            this.storeValue(itemValue);
            this.setValue(itemValue);
            if (this.form.itemChanged)
                this.form.itemChanged(this, itemValue);
        }
        return false;
    },

    _itemBlur: function (item) {
        this._itemChanged(item, item.getValue());
    },

    isReadOnly: function () {
        if (this.canEdit && !this.canEdit)
            return true;
        return this.Super("isReadOnly");
    },

    getItemValue: function (item, value) {
        return value;
    },

    getDisplayValue: function (value) {
        return value;
    },

    getComponent: function () {
    },

    setCanEdit: function (value) {
        this.Super("setCanEdit", value);
        var component = this.getComponent();
        if (component && component.updateState)
            component.updateState();
    },

    setValue: function (value) {
        this.getComponent().setComponentValue(value);
        this.Super("setValue", value);
    },

    destroy: function () {
        var component = this.getComponent();
        if (component)
            component.destroy();
        this.Super("destroy", arguments);
    }
});

isc.ClassFactory.defineClass("UnitValueBar", "HLayout");
isc.UnitValueBar.addProperties({
    autoDraw: false,
    defaultLayoutAlign: "center",
    layoutLeftMargin: 2,
    layoutRightMargin: 2,
    canEdit: true,
    unitAlign: "Right",

    defaultUnitValueFormProperties: {
        autoDraw: false,
        margin: 0,
        padding: 0,
        width: "100%",
        cellPadding: 1,

        itemChange: function (item, value, oldValue) {
            return this.unitValueItem._itemChange(item, value, oldValue);
        },

        itemChanged: function (item, newValue) {
            if (item.name == "unit")
                return this.unitValueItem._itemChanged(item, newValue);
        },

        isReadOnly: function () {
            return this.unitValueItem.isReadOnly();
        },

        itemBlur: function (item) {
            return this.unitValueItem._itemBlur(item);
        },

        itemKeyPress: function (item, keyName, characterValue) {
            if (keyName == "Tab" || keyName == "Enter" || keyName == "Arrow_Up" || keyName == "Arrow_Down")
                item.elementBlur();
            return true;
        },

        getComponentValue: function () {
            var values = this.getValues();
            return this.unitValueProperties.calculateValue(values);
        },

        setComponentValue: function (value) {
            var values = this.unitValueProperties.createValues(value);
            if (values.value == undefined || values.value == null || values.value == 0)
                this.setData({
                    value: values.value ? values.value : null,
                    unit: this.getValue("unit")
                });
            else
                this.setData(values);
        },

        updateState: function () {
            this.setCanEdit(this.unitValueItem.getCanEdit());
        }
    },

    defaultValueFieldProperties: {
        name: "value",
        type: "float",
        showTitle: false,
        textAlign: "right",
        keyPressFilter: "[0-9.]",

        blur: function (form, item) {
            this.Super("blur", arguments);
            form.itemBlur(item);
        },

        focus: function (form, item) {
            this.selectValue();
        },

        _formatValue: function (value) {
            if (value != undefined && value != null) {
                if (isc.isA.String(value))
                    value = parseFloat(value);
                return parseFloat(value.format(this.form.unitValueProperties.format));
            }
        },

        formatEditorValue: function (value, record, form, item) {
            if (value != undefined && value != null)
                return this._formatValue(value);
            return this.Super("formatEditorValue", arguments);
        },

        formatValue: function (value, record, form, item) {
            if (value != undefined && value != null)
                return this._formatValue(value);
            return this.Super("formatValue", arguments);
        }
    },

    defaultUnitFieldProperties: {
        name: "unit",
        type: "integer",
        showTitle: false
    }
});
isc.UnitValueBar.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        this.addMember(this.getUnitValueForm());
        this.setComponentValue(this.value);
        this.updateState();
    },

    getUnitValueForm: function () {
        if (this.unitValueForm)
            return this.unitValueForm;

        var unitValueProperties = isc.UnitValueCompoment.create(this);
        if (this.unitValueItem && this.unitValueItem.unitValueProperties)
            isc.addProperties(unitValueProperties, this.unitValueItem.unitValueProperties);
        var unitWidth = unitValueProperties.getUnitWidth();
        var defaultUnit = unitValueProperties.getDefaultUnit();
        var valueMap = unitValueProperties.getUnitValueMap();

        var valueField = isc.addProperties({
                unitValueItem: this.unitValueItem,
                width: this.width - unitWidth
            }, unitValueProperties.valueFieldProperties,
            this.defaultValueFieldProperties, this.valueFieldProperties);

        var unitField = isc.addProperties({}, unitValueProperties.unitFieldProperties,
            {
                unitValueItem: this.unitValueItem,
                width: unitWidth,
                value: defaultUnit,
                valueMap: valueMap
            }, this.defaultUnitFieldProperties, this.unitFieldProperties);

        this.unitValueForm = isc.DynamicForm.create(this.defaultUnitValueFormProperties,
            {
                unitValueItem: this.unitValueItem,
                unitValueProperties: unitValueProperties
            },
            unitValueProperties.unitValueFormProperties, this.unitValueFormProperties);

        if (this.isReadOnly()) {
            valueField.type = "StaticText";
            valueField.textAlign = "left";
            unitField.type = "StaticText";
        }
        if (this.unitAlign.toLowerCase() == "left")
            this.unitValueForm.setFields([unitField, valueField]);
        else
            this.unitValueForm.setFields([valueField, unitField]);
        return this.unitValueForm;
    },

    isReadOnly: function () {
        if (this.unitValueItem)
            return this.unitValueItem.isReadOnly();
        return false;
    },

    getItemValue: function (item, value) {
        var itemValue;
        if ($.defined(value)) {
            if (item.name == "value") {
                var unitInForm = item.form.getValue("unit");
                if ($.defined(unitInForm))
                    itemValue = value * unitInForm;
            }
            else if (item.name == "unit") {
                var valueInForm = item.form.getValue("value");
                if ($.defined(valueInForm))
                    itemValue = value * valueInForm;
            }
            itemValue = this.unitValueProperties.calculateSaveValue(itemValue);
        }
        return itemValue;
    },

    getComponentValue: function () {
        return this.unitValueForm.getComponentValue();
    },

    setComponentValue: function (value) {
        this.unitValueForm.setComponentValue(value);
    },

    updateState: function () {
        this.unitValueItem.updateState();
        this.markForRedraw();
    },

    getDisplayValue: function (value) {
        var values = this.unitValueProperties.createValues(value);
        return values ? values.value + " " + values.unit : null;
    }
});

/*
 * UnitValueItem
 */
isc.ClassFactory.defineClass("UnitValueItem", "ComponentItem");

isc.UnitValueItem.addClassProperties({
    defaultWidth: 130
});

isc.UnitValueItem.addClassMethods({
    formatDisplayValue: function (value) {
        var unitValueProperties = isc.UnitValueCompoment.create(this);
        var values = unitValueProperties.createValues(value);
        var value = values.value;
        if (value == undefined || value == null || isNaN(value))
            value = "&nbsp;";
        var unit = unitValueProperties.getUnitName(values.unit);
        if (unit == undefined || unit == null || unit.isEmpty())
            unit = "&nbsp;";
        return "<table><tr><td>" + value + "</td><td style=\"width:" + unitValueProperties.minUnitWidth + "px;\">" + unit + "</td></tr></table>"
    }
});
isc.UnitValueItem.addMethods({
    getComponent: function () {
        if (!this.unitValueBar) {
            this.unitValueBar = isc.UnitValueBar.create({
                autoDraw: false,
                unitValueItem: this,
                width: this.width
            }, this.unitValueBarProperties);
        }
        return this.unitValueBar;
    },

    getItemValue: function (item, value) {
        return this.getComponent().getItemValue(item, value);
    },

    getDisplayValue: function (value) {
        return this.getComponent().getDisplayValue(value);
    }
});

/*
 * LogicFieldFactory
 */
isc.ClassFactory.defineClass("LogicFieldFactory");
isc.LogicFieldFactory.addClassProperties({
    octetsDefaultProperties: {
        editorType: "UnitValueItem",
        minUnitWidth: 30,
        defaultUnitWidth: 60,
        valueMap: {
            "1": "Byte",
            "1024": "KB",
            "1048576": "MB",
            "1073741824": "GB",
            "1099511627776": "PB"
        }
    },

    octetsVelocityDefaultProperties: {
        editorType: "UnitValueItem",
        minUnitWidth: 30,
        defaultUnitWidth: 60,
        valueMap: {
            "1": "Bps",
            "8192": "KBps",
            "8388608": "MBps",
            "8589934592": "GBps",
            "8796093022208": "PBps"
        }
    },

    velocityDefaultProperties: {
        editorType: "UnitValueItem",
        minUnitWidth: 25,
        defaultUnitWidth: 60,
        valueMap: {
            "1": "/S",
            "1000": "K/S",
            "1000000": "M/S",
            "1000000000": "G/S"
        }
    },

    valueDefaultProperties: {
        editorType: "UnitValueItem",
        minUnitWidth: 20,
        defaultUnitWidth: 60,
        valueMap: {
            "1": "",
            "1000": "K",
            "1000000": "M",
            "1000000000": "G"
        }
    },

    timeLengthDefaultProperties: {
        editorType: "UnitValueItem",
        minUnitWidth: 30,
        defaultUnitWidth: 60,
        saveUnit: 1000,
        minUnit: 1000,
        maxUnit: 86400000,
        valueMap: {
            "1": isc.i18nTime.Millisecond,
            "1000": isc.i18nTime.Second,
            "60000": isc.i18nTime.Minute,
            "3600000": isc.i18nTime.Hour,
            "86400000": isc.i18nTime.Day
        }
    },

    connectDefaultProperties: {
        editorType: "BooleanItem",
        showDisabled: false,
        valueImgMap: {
            "true": {
                width: 16,
                height: 16,
                title: isc.i18nTitle.Connect,
                src: "[SKIN]/button/connect.gif"
            },
            "false": {
                width: 16,
                height: 16,
                title: isc.i18nTitle.Disconnect,
                src: "[SKIN]/button/connect_Disabled.gif"
            }
        }
    }
});
isc.LogicFieldFactory.addClassMethods({
    createField: function (field) {
        var logicProperties = this.getLogicProperties(field);
        if (logicProperties) {
            if (field.editorType == "UnitValueItem")
                field.unitValueProperties = logicProperties;
            else
                isc.addProperties(field, logicProperties);
        }
        this.createLogicField(field);
    },

    createGridField: function (field) {
        var logicProperties = this.getLogicProperties(field);
        if (logicProperties) {
            if (field.editorType == "UnitValueItem") {
                if (!$.defined(field.editorProperties))
                    field.editorProperties = { unitValueProperties: logicProperties };
                else
                    field.editorProperties["unitValueProperties"] = logicProperties;
                for (var name in isc.UnitValueCompoment.DefaultProperties) {
                    var property = field[name];
                    if (property != undefined && property != null)
                        logicProperties[name] = property;
                }
            }
            else
                isc.addProperties(field, logicProperties);
        }
        this.createLogicField(field);
    },

    createLogicField: function (field) {
        if (field.logic) {
            var className = field.editorType;
            if (field.editorType == undefined || field.editorType == null)
                className = field.logic.substring(0, 1).toUpperCase() + field.logic.substring(1) + "Item";
            var classType = isc.ClassFactory.getClass(className);
            if (classType != null) {
                delete field.type;
                field.editorType = className;
                if (classType.defaultWidth != undefined)
                    field.width = classType.defaultWidth;
                if (classType.formatDisplayValue)
                    field.formatDisplayValue = classType.formatDisplayValue;
            }
        }
    },

    getLogicProperties: function (field) {
        if (field.logic) {
            var defaultProperties = this[field.logic + "DefaultProperties"];
            if (defaultProperties) {
                field.editorType = defaultProperties.editorType;
                field.formatDisplayValue = isc.UnitValueItem.formatDisplayValue;
                return isc.addProperties({}, defaultProperties);
            }
        }
    }
});

/*
 * PercentBar
 */
isc.ClassFactory.defineClass("PercentBar", "HLayout");
isc.PercentBar.addProperties({
    autoDraw: false,
    format: "#.00",
    defaultLayoutAlign: "center",
    membersMargin: 2,
    layoutLeftMargin: 2,
    layoutRightMargin: 2,
    minValue: 0.01,
    maxValue: 100,
    value: 0,
    canEdit: true,

    progressBarProperties: {
        autoDraw: false,
        width: "100%",
        height: "60%",
        mouseMove: function () {
            this.trackEvent();
        },

        mouseOut: function () {
            this.trackEvent();
        },

        trackEvent: function () {
            if (EventHandler.leftButtonDown() && !this.percentBar.isReadOnly()) {
                var value = 100 * (this.getOffsetX() / this.getWidth());
                if (value > 99)
                    value = 100;
                else if (value <= 0)
                    value = 0;
                value = Math.round(value);
                if (this.percentBar._itemChange(this, value, this.getValue()))
                    this.percentBar._itemChanged(this, value);
            }
        },

        getValue: function () {
            return this.percentDone;
        },

        setValue: function (value) {
            if (value != undefined && value != null) {
                if (isc.isA.String(value))
                    value = parseFloat(value);
                this.setPercentDone(value);
            }
        },

        updateState: function () {
            if (this.percentBar.isReadOnly())
                delete this.cursor;
            else
                this.cursor = "pointer";
        }
    },

    percentFormProperties: {
        autoDraw: false,
        width: "*",
        margin: 0,
        padding: 0,
        cellPadding: 1,

        itemChange: function (item, value, oldValue) {
            return this.percentBar._itemChange(item, value, oldValue);
        },

        itemChanged: function (item, newValue) {
            return this.percentBar._itemChanged(item, newValue);
        },

        isReadOnly: function () {
            return this.percentBar.isReadOnly();
        },

        itemBlur: function (item) {
            return this.percentBar._itemBlur(item);
        },

        itemKeyPress: function (item, keyName, characterValue) {
            if (keyName == "Tab" || keyName == "Enter" || keyName == "Arrow_Up" || keyName == "Arrow_Down") {
                item.elementBlur();
                this.percentBar._itemKeyPress(item, keyName, characterValue);
            }
            return true;
        },

        setFocusItem: function (item) {
            this.Super("setFocusItem", arguments);
            item.selectValue();
        },

        updateState: function () {
            this.getField("value").setCanEdit(!this.percentBar.isReadOnly());
        }
    },

    valueFieldProperties: {
        autoDraw: false,
        name: "value",
        width: 50,
        type: "float",
        showTitle: false,
        textAlign: "right",
        keyPressFilter: "[0-9.]",
        value: 0,

        blur: function (form, item) {
            this.Super("blur", arguments);
            form.itemBlur(item);
        },

        formatEditorValue: function (value, record, form, item) {
            if (value != undefined && value != null)
                return this.percentBar.formatValue(value);
            return this.Super("formatEditorValue", arguments);
        },

        formatValue: function (value, record, form, item) {
            if (value != undefined && value != null) {
                if (isc.isA.String(value))
                    value = parseFloat(value);
                if (!isNaN(value) && value != 0) {
                    if (value > this.percentBar.maxValue)
                        return ">" + this.percentBar.maxValue;
                    if (value < this.percentBar.minValue)
                        return "<" + this.percentBar.minValue;
                }
            }
            else
                value = 0;
            return value.format(this.percentBar.format);
        }
    },

    unitFieldProperties: {
        autoDraw: false,
        name: "unit",
        width: 5,
        type: "StaticText",
        showTitle: false,
        value: "%"
    }
});

isc.PercentBar.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        this.addMembers([this.getProgressBar(), this.getPercentForm()]);
        this.setValue(this.value);
        this.updateState();
    },

    getProgressBar: function () {
        if (!this.progressBar) {
            var component = this;
            this.progressBar = isc.Progressbar.create({
                percentBar: this,
                isDisabled: function () {
                    return false;
                }
            }, this.progressBarProperties);
        }
        return this.progressBar;
    },

    getPercentForm: function () {
        if (!this.percentForm) {
            var component = this;
            var valueField = isc.addProperties({percentBar: this}, this.valueFieldProperties);
            var unitField = isc.addProperties({}, this.unitFieldProperties);
            this.percentForm = isc.DynamicForm.create({
                percentBar: this
            }, this.percentFormProperties);
            if (this.isReadOnly()) {
                valueField.type = "StaticText";
                valueField.width = 40;
            }
            this.percentForm.setFields([valueField, unitField]);
        }
        return this.percentForm;
    },

    _itemChange: function (item, value, oldValue) {
        if (this.percentItem)
            return this.percentItem._itemChange(item, value, oldValue);
        return true;
    },

    _itemChanged: function (item, value) {
        if (this.percentItem)
            this.percentItem._itemChanged(item, value);
        else
            this.getProgressBar().setValue(value);
    },

    _itemKeyPress: function (item, keyName, characterValue) {
        if (this.percentItem && this.percentItem.form && this.percentItem.form.itemKeyPress)
            return this.percentItem.form.itemKeyPress(this.percentItem, keyName, characterValue);
    },

    _itemBlur: function (item) {
        if (this.percentItem)
            this.percentItem._itemBlur(item);
        else
            this.getProgressBar().setValue(item.getValue());
    },

    isReadOnly: function () {
        if (this.percentItem)
            return this.percentItem.isReadOnly();

        if (this.canEdit == undefined || this.canEdit == null)
            return false;
        return !this.canEdit;
    },

    setCanEdit: function (value) {
        this.Super("setCanEdit", arguments);
        this.updateState();
    },

    updateState: function () {
        this.getPercentForm().updateState();
        this.getProgressBar().updateState();
        this.markForRedraw();
    },

    setFocus: function (newState, reason) {
        this.getPercentForm().focusInItem("value");
    },

    getValue: function () {
        return this.getComponentValue();
    },

    getComponentValue: function () {
        return this.getPercentForm().getValue("value");
    },

    setValue: function (value) {
        this.setComponentValue(value);
    },

    setComponentValue: function (value) {
        this.getProgressBar().setPercentDone(value);
        this.getPercentForm().setValue("value", value);
    },

    formatValue: function (value) {
        if (value != undefined && value != null) {
            if (isc.isA.String(value))
                value = parseFloat(value);
            return parseFloat(value.format(this.format));
        }
    },

    destroy: function () {
        this.getProgressBar().destroy();
        this.getPercentForm().destroy();
        this.Super("destroy", arguments);
    }
});

/*
 * PercentItem
 */
isc.ClassFactory.defineClass("PercentItem", "ComponentItem");

isc.PercentItem.addClassProperties({
    defaultWidth: 130,
    startImg: "[SKIN]Progressbar/progressbar_h_start.gif",
    stretchImg: "[SKIN]Progressbar/progressbar_h_stretch.gif",
    endImg: "[SKIN]Progressbar/progressbar_h_end.gif",
    emptyStartImg: "[SKIN]Progressbar/progressbar_h_empty_start.gif",
    emptyStretchImg: "[SKIN]Progressbar/progressbar_h_empty_stretch.gif",
    emptyEndImg: "[SKIN]Progressbar/progressbar_h_empty_end.gif",
    defaultPercentFormat: "#.00"
});

isc.PercentItem.addClassMethods({
    formatDisplayValue: function (value) {
        var percentValue = 0;
        var percentText = "0.00";
        if (value != undefined && value != null) {
            if (isc.isA.String(value))
                value = parseFloat(value);
            if (!isNaN(value) && value != 0) {
                var maxValue = this.maxValue ? this.maxValue : 100;
                var minValue = this.minValue ? this.minValue : 0.01;
                if (value > maxValue) {
                    percentText = ">" + maxValue;
                    percentValue = 100;
                }
                else if (value < minValue)
                    percentText = "<" + minValue;
                else {
                    var format = this.format ? this.format : isc.PercentItem.defaultPercentFormat;
                    percentText = value.format(format);
                    percentValue = parseFloat(value);
                }
            }
        }
        var height = 13;
        var numberWidth = 55;
        var offset = 0;

        var imgHtml = "<table style=\"width:100%;\"><tr><td>";
        imgHtml += "<div style=\"height:" + height + "px;width:100%;\"><nobr>";
        if (percentValue <= 0 || percentValue >= 100)
            offset = 4;
        if (percentValue > 0) {
            imgHtml += isc.Canvas.imgHTML(isc.PercentItem.startImg, 2, height);
            imgHtml += isc.Canvas.imgHTML(isc.PercentItem.stretchImg, percentValue + "%", height);
            if (offset > 0)
                imgHtml += isc.Canvas.imgHTML(isc.PercentItem.stretchImg, offset, height);
            imgHtml += isc.Canvas.imgHTML(isc.PercentItem.endImg, 2, height);
        }
        if (percentValue < 100) {
            imgHtml += isc.Canvas.imgHTML(isc.PercentItem.emptyStartImg, 2, height);
            imgHtml += isc.Canvas.imgHTML(isc.PercentItem.emptyStretchImg, (100 - percentValue) + "%", height);
            if (offset > 0)
                imgHtml += isc.Canvas.imgHTML(isc.PercentItem.emptyStretchImg, offset, height);
            imgHtml += isc.Canvas.imgHTML(isc.PercentItem.emptyEndImg, 2, height);
        }
        imgHtml += "</nobr></div>";
        imgHtml += "</td><td style=\"width:" + numberWidth + "px;text-align: right;\">";
        imgHtml += percentText + "%";
        imgHtml += "</td></tr></table>";
        return imgHtml;
    }
});

isc.PercentItem.addMethods({
    getComponent: function () {
        if (!this.percentBar)
            this.percentBar = isc.PercentBar.create({
                autoDraw: false,
                width: this.width,
                height: this.height,
                percentItem: this
            }, this.percentBarProperties);
        return this.percentBar;
    },

    getDisplayValue: function (value) {
        return this.percentBar.formatValue(value);
    }
});

/*
 * BooleanButton
 */
isc.ClassFactory.defineClass("BooleanButton", "HLayout");
isc.BooleanButton.addClassProperties({
    DefaultValueImgMap: {
        "true": {
            width: 45,
            height: 15,
            src: "[SKIN]/button/on.png"
        },
        "false": {
            width: 45,
            height: 15,
            src: "[SKIN]/button/off.png"
        }
    },

    DefaultImgProperties: {
        autoDraw: false,
        imageType: "normal",
        cursor: "pointer",
        click: function () {
            if (this.booleanButton)
                this.booleanButton.click();
            return false;
        }
    }
});

isc.BooleanButton.addProperties({
    autoDraw: false,
    height: 22,
    align: "center",
    layoutTopMargin: 3,
    defaultLayoutAlign: "center"
});

isc.BooleanButton.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        this.addMembers([this.getTrueImg(), this.getFalseImg()]);
        this.setValue(this.value);
        this.updateState();
    },

    getTrueImg: function () {
        if (!this.trueImg)
            this.trueImg = this.createImg(true);
        return this.trueImg;
    },

    getFalseImg: function () {
        if (!this.falseImg)
            this.falseImg = this.createImg(false);
        return this.falseImg;
    },

    createImg: function (value) {
        var valueImgMap = this.valueImgMap ? this.valueImgMap : isc.BooleanButton.DefaultValueImgMap;
        var imgProperties = valueImgMap[value + ""];
        return isc.Img.create({
            booleanButton: this,
            width: imgProperties.width + this.layoutTopMargin * 2,
            showFocused: true,
            showDisabled: this.showDisabled,
            imageWidth: imgProperties.width,
            imageHeight: imgProperties.height,
            prompt: imgProperties.title,
            src: imgProperties.src
        }, isc.BooleanButton.DefaultImgProperties);
    },

    click: function () {
        if (!this.isReadOnly()) {
            if (this.valueChange) {
                if (this.valueChange(this, !this.value, this.value) == false)
                    return;
            }
            this.setValue(!this.value);
            if (this.booleanItem)
                this.booleanItem.storeValue(this.value);
            if (this.valueChanged)
                this.valueChanged(this, this.value);
            this.setFocus();
        }
    },

    isReadOnly: function () {
        if (this.booleanItem)
            return this.booleanItem.isReadOnly();

        if (this.canEdit == undefined || this.canEdit == null)
            return false;
        return !this.canEdit;
    },

    setCanEdit: function (value) {
        this.canEdit = value;
        this.updateState();
    },

    getValue: function () {
        return this.getComponentValue();
    },

    getComponentValue: function () {
        return this.value;
    },

    setFocus: function (newState, reason) {
        if (this.value)
            this.trueImg.setFocus();
        else
            this.falseImg.setFocus();
    },

    setValue: function (value) {
        this.setComponentValue(value);
    },

    setComponentValue: function (value) {
        if (value != undefined && value != null && isc.isA.String(value))
            value = Boolean.parseText(value);

        this.value = value;
        if (this.value) {
            this.falseImg.hide();
            this.trueImg.show();
            if (!this.isReadOnly()) {
                this.falseImg.setCanFocus(false);
                this.trueImg.setCanFocus(true);
            }
        }
        else {
            this.trueImg.hide();
            this.falseImg.show();
            if (!this.isReadOnly()) {
                this.trueImg.setCanFocus(false);
                this.falseImg.setCanFocus(true);
            }
        }
    },

    updateState: function () {
        if (this.isReadOnly()) {
            delete this.falseImg.cursor;
            delete this.trueImg.cursor;
            this.trueImg.setCanFocus(false);
            this.falseImg.setCanFocus(false);
        } else {
            this.falseImg.cursor = "pointer";
            this.trueImg.cursor = "pointer";
        }
        this.markForRedraw();
    },

    destroy: function () {
        this.trueImg.destroy();
        this.falseImg.destroy();
        this.Super("destroy", arguments);
    }
});

/*
 * BooleanItem
 */
isc.ClassFactory.defineClass("BooleanItem", "ComponentItem");
isc.BooleanItem.addClassProperties({
    defaultWidth: 45
});
isc.BooleanItem.addClassMethods({
    formatDisplayValue: function (value) {
        if (value != undefined && value != null && isc.isA.String(value))
            value = Boolean.parseText(value);
        var valueImgMap = this.valueImgMap ? this.valueImgMap : isc.BooleanButton.DefaultValueImgMap;
        var imgProperties = valueImgMap[value + ""];
        var imgHTML = ImgUtils.getHtml(imgProperties.src, imgProperties.width, imgProperties.height, {title: imgProperties.title});
        return "<div style=\"width:100%;height:16px;padding-top:3px;\">" + imgHTML + "</div>";
    }
});
isc.BooleanItem.addProperties({
    textAlign: "center"
});
isc.BooleanItem.addMethods({
    getComponent: function () {
        if (!this.booleanButton) {
            var booleanItem = this;
            this.booleanButton = isc.BooleanButton.create({
                width: this.width,
                height: this.height,
                booleanItem: this,
                showDisabled: this.showDisabled,
                valueImgMap: booleanItem.valueImgMap,
                valueChange: function (item, value, oldValue) {
                    return booleanItem._itemChange(item, value, oldValue);
                },
                valueChanged: function (item, value) {
                    return booleanItem._itemChanged(item, value);
                }
            }, this.percentBarProperties);
        }
        return this.booleanButton;
    }
});

isc.UploadItem.addProperties({
    elementHeight: (isc.Browser.isMoz ? 22 : null)
});

isc.UploadItem.addMethods({
    getElementStyleHTML: function () {
        return this.Super("getElementStyleHTML") + this.getElementPropertiesHTML();
    },

    getElementHTML: function () {
        var $html = $("<p></p>").append(this.Super("getElementHTML"));
        $html.find("input").css("width", "100%");
        $html.find("input").css("padding", "0px");
        return $html[0].innerHTML;
    },

    validate: function (value) {
        if (value != undefined && value != null) {
            if (this.fileSuffix) {
                var valid = false;
                var fieldName = value.toLowerCase();
                var suffixs = this.fileSuffix.toLowerCase().split(";");
                for (var i = 0; i < suffixs.length; i++) {
                    if (fieldName.endsWith("." + suffixs[i])) {
                        valid = true;
                        break;
                    }
                }
                return valid;
            }
        }
        return true;
    },

    saveValue: function (value, isDefault) {
        if (this.saving)
            return;

        this.saving = true;
        if (this.validate(value)) {
            this.Super("saveValue", arguments);
            this.form.itemChanged(this, value);
        }
        else {
            this.setElementValue(null);
            this.form.setValue(this.name, null);
            this.form.itemChanged(this, null);
            isc.say(isc.i18nValidate.FileSuffix + this.fileSuffix);
        }
        delete this.saving;
    }
});

/**
 * Override method for support the data from getPickData.
 * @type {{getClientPickListData: Function, _$substring: string, filterClientPickListData: Function}}
 */
var overridePickListProperties = {
    getClientPickListData: function () {
        if (this.getPickData) {
            var data = this.getPickData();
            if (data)
                return data;
        }
        return this.pickData || isc.PickList.optionsFromValueMap(this);
    },

    _$substring: "substring",
    /**
     * Fix bug: The filter is incorrect when the data form getPickData.
     * @returns {*}
     */
    filterClientPickListData: function () {
        // If the user has not entered enough characters to filter return no matched data
        if (this.isEntryTooShortToFilter && this.isEntryTooShortToFilter()) return null;

        var data = this.getClientPickListData();
        var criteria = this.getPickListFilterCriteria();

        if (criteria == null || isc.isA.emptyObject(criteria) || !$.defined(criteria.criteria))
            return data;

        var filterDisplayValue = this.filterDisplayValue;
        if (this.displayField)
            filterDisplayValue = (this.filterFields.indexOf(this.displayField) == -1);

        var matches = [],
            nonMatches;

        if (this.showAllOptions)
            nonMatches = this.separatorRows.duplicate();

        var validCriterion = false;
        var dataLength = data.getLength(),
            displayFieldName = this.getDisplayFieldName(),
            valueFieldName = this.getValueFieldName();
        for (var i = 0; i < dataLength; i++) {
            var match = false;
            for (var index = 0; index < criteria.criteria.length && !match; index++) {
                var fieldCriterion = criteria.criteria[index];
                if (!fieldCriterion || isc.isA.emptyString(fieldCriterion.value))
                    continue;

                validCriterion = true;

                var searchString = fieldCriterion.value;
                if (!isc.isA.String(searchString))
                    searchString += isc.emptyString;
                searchString = searchString.toLowerCase();

                var fieldName = fieldCriterion.fieldName;
                var possibleMatch = data[i][fieldName];
                if (filterDisplayValue && fieldName == valueFieldName) {
                    if (displayFieldName)
                        possibleMatch = data[i][displayFieldName];
                    else
                        possibleMatch = this.mapValueToDisplay(possibleMatch);
                }

                if (!isc.isA.String(possibleMatch))
                    possibleMatch += "";

                possibleMatch = possibleMatch.toLowerCase();
                // Remove any mismatches from the list of options
                if ((this.textMatchStyle == this._$substring && possibleMatch.contains(searchString)) ||
                    (this.textMatchStyle != this._$substring && isc.startsWith(possibleMatch, searchString))) {
                    match = true;
                }
            }
            if (match)
                matches.add(data[i]);
            else if (this.showAllOptions)
                nonMatches.add(data[i]);
        }

        if (!validCriterion)
            matches = data.duplicate();
        if (this.showAllOptions && nonMatches.length > 1)
            matches.addList(nonMatches);
        return matches;
    },

    // Override selectDefaultItem to always select the first item in the list.
    // This will happen on every re-filter.
    selectDefaultItem: function () {
        if (this.pickList == null || this.pickList.destroyed) return;

        var selection = this.pickList.selection;

        // If we have a record matching our value (or our pending, user-typed value)
        // select it as the default. This ensures that if the user hits tab or enter
        // we auto-complete correctly
        var record;

        if (this.pickList.findRecord) {
            var name = this.getValueFieldName(),
                value = this.getElementValue();
            record = this.pickList.findRecord(name, value);
        }

        if (!$.defined(record)) {
            var pendingEnteredValue = this.$824 || this._pendingEnteredValue || this.getValue();
            if (!this.addUnknownValues && pendingEnteredValue != null)
                record = this.getPickListRecordForValue(pendingEnteredValue)
            else
                record = this.getSelectedRecord();
        }

        if (record) {
            this.pickList.clearLastHilite();
            this.delayCall("selectItemFromValue", [record[this.valueField]]);
            return;
        }

        // Otherwise select the first record in the list by default.
        record = this.pickList.getRecord(0);
        // Don't attempt to select null / loading / separator rows
        if (record == null || Array.isLoading(record) ||
            record[this.pickList.isSeparatorProperty]) return;

        selection.selectSingle(record);
        // Clear last hilite - required so keyboard navigatioin will pick up the current position
        // from the selection, not the last hilite position.
        this.pickList.clearLastHilite();
        this.pickList.scrollRecordIntoView(0);
    }
};

isc.ComboBoxItem.addMethods(overridePickListProperties);
isc.TextItem.addMethods(overridePickListProperties);

isc.FormItem.addMethods({
    getElementPropertiesHTML: function () {
        var output = isc.SB.create();
        if (this.elementProperties) {
            for (var name in this.elementProperties) {
                var value = this.elementProperties[name];
                if (isc.isA.String(value) && !value.isEmpty())
                    output.append(name, "='", value, "'");
            }
        }
        return output.toString();
    }
});

/*
 * ImgChart
 */
isc.ClassFactory.defineClass("ImgChart", "Img");
isc.ImgChart.addProperties({
    width: "100%",
    height: "100%",
    baseStyle: "imgChart",
    activeAreaMethodName: null,
    srcMethodName: null
});

isc.ImgChart.addMethods({
    load: function (data) {
        var imgChart = this;
        var params = isc.addProperties({
            width: this.width,
            height: this.height
        }, data);
        var service = this.service;
        service.invoke(this.activeAreaMethodName, params, function (responseData) {
            var methodURL = service.getMethodURL(imgChart.srcMethodName);
            imgChart.src = methodURL + "?_" + new Date().getTime();
            if (responseData.value)
                imgChart.activeAreaHTML = $(responseData.value).html();
            imgChart.markForRedraw();
        });
    }
});

/*
 * Widgets HighCharts
 */
isc.ClassFactory.defineClass("HighChart", "VBorderPanel");
isc.HighChart.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%"
});
isc.HighChart.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        this.getChart();
    },

    setData: function (data) {
        var chart = this.getChart();
        if (chart)
            chart.load(data);
    },

    ajaxLoad: function (params, clean, success, error) {
        var chart = this.getChart();
        if (chart) {
            chart.dataURL = this.service.getMethodURL(this.loadMethodName);
            chart.ajaxLoad(params, clean, success, error);
        }
    },

    refresh: function () {
        if (!isc.ApplicationContext.isLocale() && $.defined(this.loadMethodName))
            this.ajaxLoad(this.params);
        else if ($.defined(this.loadData))
            this.load(this.loadData());
        return true;
    },

    load: function (data) {
        var chart = this.getChart();
        if (chart)
            chart.load(data);
    },

    getHandleDiv: function () {
        return this._handle || this.$q3;
    },

    getChart: function () {
        if (!$.defined(this.chart))
            this.chart = this.createHighChart();
        return this.chart;
    },

    createHighChart: function () {
        var chart,
            div = this.getHandleDiv();
        if (div) {
            var highCharts = this;
            this.chartOptions.width = this.getChartWidth();
            this.chartOptions.height = this.getChartHeight();
            this.chartOptions.chart = {
                events: {
                    click: function (e) {
                        highCharts.click(e);
                    }
                }
            };
            chart = HighChartsFactory.createHighChart(div.id, this.chartOptions, this.data);
            if ($.defined(this.chartOptions.loadInterval)) {
                this.timer = isc.TimerEx.create({owner: this});
                this.timer.setInterval("refresh", this.chartOptions.loadInterval, this.chartOptions.loadInterval);
            }
        } else {
            var hightChart = this;
            window.setTimeout(function () {
                hightChart.getChart();
            }, 500);
        }
        return chart;
    },

    resized: function () {
        var div = this.getHandleDiv();
        if (div && div.childElementCount == 0)
            this.clearChart();
        var chart = this.getChart();
        if (chart)
            chart.resize(this.getChartWidth(), this.getChartHeight());
    },

    clearChart: function () {
        if (this.timer)
            this.timer.clear("refresh");
        if (this.chart) {
            this.chart.destroy();
            delete this.chart;
        }
    },

    getChartWidth: function () {
        return this.getWidth() - 2;
    },

    getChartHeight: function () {
        return this.getHeight() - 2;
    },

    redraw: function () {
        this.Super("redraw", arguments);
        this.redrawChart();
    },

    redrawChart: function () {
        if (!$.defined(this.callLator)) {
            var highChart = this;
            this.callLator = function () {
                if (highChart.isVisible())
                    highChart.resized();
                delete highChart.callLator;
            }
            window.setTimeout(this.callLator, 500);
        }
    },

    visibilityChanged: function (isVisible) {
        this.Super("visibilityChanged", arguments);
        if (isVisible)
            this.redrawChart();
    },

    destroy: function () {
        this.clearChart();
        this.Super("destroy", arguments);
    }
});

/*
 * Widgets TabPanel
 */
isc.ClassFactory.defineClass("TabPanel", "Layout");
isc.TabPanel.addProperties({
    autoDraw: false,
    width: "100%",
    height: "100%",
    vertical: true,
    tabs: [],
    defaultTabProperties: {
        autoDraw: false,
        width: "100%",
        height: "100%",
        vertical: false,
        styleName: "tabPanel",
        visibility: "hidden"
    }
});

isc.TabPanel.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        this.header = isc.TabHeader.create();
        this.tabs.clear();
        this.addMember(this.header);

        var tabPanel = this;
        this.header.itemClick = function (item) {
            tabPanel.select(item.name);
        }

        if (this.data) {
            for (var i = 0; i < this.data.length; i++) {
                var name = this.data[i].name;
                if (!$.defined(name))
                    name = "tab" + i;
                this.addTab(name, this.data[i]);
            }
        }
        this.select();
    },

    select: function (name) {
        var tab;
        if (isc.isA.Number(name) && name >= 0 && name <= this.panels.length)
            tab = this.tabs[name];
        else if (isc.isA.String(name)) {
            for (var i = 0; i < this.tabs.length; i++) {
                if (this.tabs[i].name == name) {
                    tab = this.tabs[i];
                    break;
                }
            }
        }
        if (this.tabs.length > 0) {
            if (tab == undefined || tab == null)
                tab = this.tabs[0];

            for (var i = 0; i < this.tabs.length; i++) {
                if (this.tabs[i] != tab)
                    this.tabs[i].panel.hide();
            }
        }
        if (tab) {
            tab.panel.show();
            var member = tab.panel.members[0];
            if ($.defined(member) && $.isFunction(member.getView)) {
                var moduleLayout = member.getView();
                if (moduleLayout && moduleLayout.onActive)
                    moduleLayout.onActive();
            }
            this.header.select(name);
            this.currentTab = tab;
        }
    },

    addTab: function (name, properties) {
        var panel;
        if (properties.data) {
            panel = isc.MenuModuleLayout.create({
                data: properties.data,
                icon: properties.icon,
                url: properties.url
            });
        } else if (properties.url) {
            panel = isc.Layout.create(this.defaultTabProperties, properties);
            var moduleProperties = isc.addProperties({
                url: properties.url
            }, isc.ModuleManager.ModuleDefaultProperties)
            panel.addMember(isc.ModuleLoader.create({module: moduleProperties}));
        }
        if (panel) {
            this.addMember(panel);
            this.tabs.push({
                name: name,
                panel: panel
            });
            this.header.addTab(name, properties);
        }
    }
});

/*
 * Widgets TabButton
 */
isc.ClassFactory.defineClass("TabButton", "Button");
isc.TabButton.addProperties({
    autoDraw: false,
    baseStyle: "tabButton",
    height: "100%",
    canFocus: false,
    actionType: "radio",
    radioGroup: "tab",
    autoFit: true
});

/*
 * Widgets TabHeader
 */
isc.ClassFactory.defineClass("TabHeader", "HLayout");
isc.TabHeader.addProperties({
    autoDraw: false,
    styleName: "tabHeader",
    width: "100%",
    align: "center",
    height: 15,
    buttons: [],
    membersMargin: 15
});

isc.TabHeader.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        if (this.data) {
            for (var i = 0; i < this.data.length; i++) {
                var name = this.data[i].name;
                if ($.defined(name))
                    name = "tab" + i;
                this.addTab(name, this.data[i]);
            }
        }
        this.select();
    },

    select: function (name) {
        var button;
        if (isc.isA.Number(name) && name >= 0 && name <= this.panels.length)
            button = this.buttons[name];
        else if (isc.isA.String(name)) {
            for (var i = 0; i < this.buttons.length; i++) {
                if (this.buttons[i].name == name) {
                    button = this.buttons[i];
                    break;
                }
            }
        }
        if (this.buttons.length > 0) {
            if (button == undefined || button == null)
                button = this.buttons[0];
        }
        if (button)
            button.select();
    },

    addTab: function (name, properties) {
        var tabHeader = this;
        var button = isc.TabButton.create(properties);
        button.name = name;
        button.click = function () {
            if (tabHeader.itemClick)
                tabHeader.itemClick(this);
        };
        this.addMember(button);
        this.buttons.push(button);
    }
});

/*
 * Widgets MenuSection
 */
isc.ClassFactory.defineClass("MenuSection", "SectionStack");
isc.MenuSection.addProperties({
    autoDraw: false,
    overflow: "auto",
    width: "100%",
    height: "100%",
    visibilityMode: "mutex",
    showResizeBar: true,
    styleName: "menuSection",
    treeGridProperties: {
        autoDraw: false,
        width: "100%",
        border: "none",
        autoFitData: "vertical",
        bodyBackgroundColor: "none",
        showHeader: false,
        nodeIcon: null,
        folderIcon: null,
        showOpenIcons: false,
        showDropIcons: false,
        showConnectors: false,
        openerImage: "[SKIN]/../tree/opener.gif",
        openerIconSize: 16,
        closedIconSuffix: "",
        bodyProperties: {
            overflow: "hidden"
        }
    },
    treeProperties: {
        modelType: "children",
        nameProperty: "title",
        childrenProperty: "submenu"
    }
});
isc.MenuSection.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        if (this.data) {
            var menuSection = this;
            for (var i = 0; i < this.data.length; i++) {
                var section = { title: this.data[i].title, expanded: true, items: [] };
                var treeGrid = isc.TreeGrid.create(this.treeGridProperties, {
                    data: isc.Tree.create(this.treeProperties, {
                        root: { submenu: this.data[i].submenu}
                    }),
                    nodeClick: function (viewer, node, recordNum) {
                        if (menuSection.itemClick)
                            menuSection.itemClick(node);
                    }
                });
                section.items.push(treeGrid);
                this.addSection(section);
            }
        }
        this.expandSection(0);
    }
});

/*
 * Widgets MenuLayout
 */
isc.ClassFactory.defineClass("MenuModuleLayout", "HLayout");
isc.MenuModuleLayout.addProperties({
    autoDraw: false,
    overflow: "auto",
    width: "100%",
    height: "100%",
    padding: 1,
    resizeBarSize: 2
});
isc.MenuModuleLayout.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);
        this.addMember(this.createMenuSection());
        this.addMember(this.createModuleTabSet());
    },

    createMenuSection: function () {
        var menuLayout = this;
        return isc.MenuSection.create({
            data: this.data,
            width: 200,
            itemClick: function (menuItem) {
                menuLayout.moduleTabSet.loadModule(menuItem);
            }
        });
    },

    createModuleTabSet: function () {
        var properties;
        if (this.url)
            properties = {
                homeModule: {
                    title: isc.i18nButton.home,
                    icon: this.icon,
                    url: this.url
                }
            }
        this.moduleTabSet = isc.ModuleTabSet.create(properties);
        return this.moduleTabSet;
    }
});
/**
 *  BackgroundLayout
 */
isc.ClassFactory.defineClass("BackgroundLayout", "Img");
isc.BackgroundLayout.addProperties({
    autoDraw: false,
    height: "100%",
    width: "100%",
    align: "center",
    imageType: "stretch",
    orientation: "vertical", //"horizontal"
    contentLayoutProperties: null
});

isc.BackgroundLayout.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);

        this.contentLayout = isc.Layout.create({
            autoDraw: false,
            width: "100%",
            height: "100%",
            orientation: this.orientation,
            members: this.members
        }, this.contentLayoutProperties);
        this.addChild(this.contentLayout);
    },

    addMember: function (member) {
        this.contentLayout.addMember(member);
    },

    addMembers: function (members) {
        this.contentLayout.addMembers(members);
    }
});

isc.PickList.addClassMethods({
    // optionsFromValueMap()
    // Method to determine set of data to display in the pickList based on the valueMap of
    // a (non databound) pickList item.
    optionsFromValueMap: function (item) {
        var valueMap = item.getValueMap(),
            records = [];

        if (valueMap == null) valueMap = [];

        // We have to turn the valueMap into an array of record type objects.
        var valueField = item.getValueFieldName(),
            displayField = item.getDisplayFieldName();

        if (isc.isAn.Array(valueMap)) {
            for (var i = 0; i < valueMap.length; i++) {
                records[i] = {}
                records[i][valueField] = valueMap[i];
                if (displayField != null) records[i][displayField] = valueMap[i];
            }

        } else if (isc.isAn.Object(valueMap)) {
            var i = 0;
            var type = item.dataType || item.getType(),
                convertToInt, convertToFloat, convertToBoolean;

            if (type != null) {
                if (isc.SimpleType.inheritsFrom(type, "integer"))
                    convertToInt = true;
                else if (isc.SimpleType.inheritsFrom(type, "float"))
                    convertToFloat = true;
                else if (isc.SimpleType.inheritsFrom(type, "boolean"))
                    convertToBoolean = true;
            }

            for (var j in valueMap) {
                records[i] = {};
                var key = j;
                if (convertToInt) {
                    var intVal = parseInt(key);
                    if (intVal == key) key = intVal;
                } else if (convertToFloat) {
                    var floatVal = parseFloat(key);
                    if (floatVal == key) key = floatVal;
                } else if (convertToBoolean) {
                    var boolVal = (key == "true" ? true : (key == "false" ? false : null));
                    if (boolVal != null) key = boolVal;
                }

                records[i][valueField] = key;
                if (displayField != null) records[i][displayField] = valueMap[j];
                i++;
            }
            records.$882 = true;
            records._derivedFromValueMapObject = true;
        }
        return records;
    }
});

var ScrollingMenuProperties = {
    useBackMask: true,
    canFocus: true,
    showHeader: false,
    showEdges: false,
    autoDraw: false,
    className: "scrollingMenu",
    bodyStyleName: "scrollingMenuBody",
    selectionType: "single",
    leaveScrollbarGap: false,
    generateClickOnSpace: false,
    generateDoubleClickOnEnter: false,
    generateClickOnEnter: true,
    showModal: true,
    arrowKeyAction: "select",
    enableSelectOnRowOver: true,
    filterOnKeypress: true,
    show: function () {
        if (this.showModal)
            this.showClickMask({target: this, methodName: "cancel"}, false, [this]);

        this.Super("show", arguments);
        if (this.showModal) this.body.focus();
    },

    recordClick: function (viewer, record, recordNum, field, fieldNum, value, rawValue) {
        this.hide();
        if (record != null)
            this.itemClick(record);
    },

    itemClick: function (record) {

    },

    rowOver: function (record, rowNum, colNum) {
        if (this.enableSelectOnRowOver) this.selection.selectSingle(record);
    },

    createSelectionModel: function (a, b, c, d, e) {
        var returnVal = this.invokeSuper("ScrollingMenu", "createSelectionModel", a, b, c, d, e);
        this.selection.addProperties({
            selectOnRowOver: function (record) {
                this.selectSingle(record);
                this.selectionFromMouse = true;
            },

            setSelected: function (record, state) {
                this.selectionFromMouse = false;
                return this.Super("setSelected", arguments);
            }
        });
        return returnVal;
    },

    bodyKeyPress: function (event, eventInfo) {
        var keyName = event.keyName;
        if (keyName == this._$Enter) {
            var selection = this.selection;
            if (selection && selection.selectionFromMouse) {
                this.cancel();
                return false;
            }
        }
        if (keyName == "Escape") {
            this.cancel();
            return false;
        }
        return this.Super("bodyKeyPress", arguments);
    },

    cancel: function () {
        this.hide();
    },

    hide: function () {
        this.hideClickMask();
        return this.Super("hide", arguments);
    },

    _selectFirstOnDataChanged: true,
    dataChanged: function () {
        var returnVal = this.Super("dataChanged", arguments);
        if (!this._selectFirstOnDataChanged) return;

        if (this.data && this.data.getLength() > 0 && this.selection && !this.selection.anySelected() &&
            (isc.isA.ResultSet == null || !isc.isA.ResultSet(this.data) || this.data.rowIsLoaded(0)))
            this.selection.selectItem(0);
        return returnVal;
    }
};

var filterEditorDefaults = {
    backgroundColor: "white",
    editorKeyPress: function (item, keyName, characterValue) {
        if (keyName == "Arrow_Down") {
            this.sourceWidget._navigateToNextRecord(1);
            return false;
        }
        if (keyName == "Arrow_Up") {
            this.sourceWidget._navigateToNextRecord(-1);
            return false;
        }
        if (keyName == "Enter") {
            this.sourceWidget._generateFocusRecordClick();
            return;
        }
        return this.Super("editorKeyPress", arguments);
    }
};

var dataGridPickerClassProperties = {
    _cachedDSPickLists: {},
    pickListCacheLimit: 50,
    DefaultPickListProperties: {
        enableSelectOnRowOver: false
    },
    getInstance: function (formItem) {
        if ($.defined(formItem.form._pickListCache))
            return formItem.form._pickListCache[formItem.name];
    },

    createPickListProperties: function (formItem) {
        if (!$.defined(formItem.pickListProperties))
            formItem.pickListProperties = {};
        isc.addProperties(formItem.pickListProperties, this.DefaultPickListProperties);
        if (formItem.pickListFields.length == 1)
            formItem.pickListProperties.baseStyle = "dataGridPicker";
        else
            formItem.pickListProperties.bodyStyleName = "dataGridPickerBody";
    },

    createInstance: function (formItem, pickListProperties) {
        this.createPickListProperties(formItem);
        if ($.defined(formItem.grid)) {
            var field = formItem.grid.getField(formItem.name);
            if (field) {
                if (!$.defined(field.editorProperties))
                    field.editorProperties = {};
                this.createPickListProperties(field.editorProperties);
            }
        }
        var pickList = this.create(formItem.pickListProperties, pickListProperties);
        if (!$.defined(formItem.form._pickListCache))
            formItem.form._pickListCache = {};
        formItem.form._pickListCache[formItem.name] = pickList;
        return pickList;
    }
};

var dataGridPickerProperties = {
    width: 20,
    height: 300,
    autoDraw: false,
    useAllDataSourceFields: false,
    autoFitWidth: true,
    tabIndex: -1,
    canResizeFields: false,
    canFreezeFields: false,
    normalCellHeight: 16,
    showOverAsSelected: true,
    scrollToCellXPosition: "left",
    scrollToCellYPosition: "top",
    getValueIcon: function (field, value, record) {
        var formItem = this.formItem;
        var hasCustomValueIcons = formItem && !formItem.suppressValueIcons &&
            (formItem.valueIcons != null || formItem.getValueIcon != null);
        if (hasCustomValueIcons) {
            var valueField = formItem.getValueFieldName(),
                valueIconField = formItem.valueIconField ||
                    formItem.getDisplayFieldName() || valueField;
            if (this.getFieldName(field) == valueIconField)
                return formItem._getValueIcon(record[valueField]);
        }
        return this.Super("getValueIcon", arguments);
    },

    getArrowKeyAction: function () {
        return this.allowMultiSelect ? "focus" : "select";
    },

    rowClick: function (record, recordNum, fieldNum, keyboardGenerated) {
        this._keyboardRowClick = keyboardGenerated;
        this.Super("rowClick", arguments);
        delete this._keyboardRowClick;
    },

    recordClick: function (viewer, record, recordNum, field, fieldNum, value, rawValue) {
        var shouldDismiss = !this.allowMultiSelect;
        if (this._keyboardRowClick) {
            var isEnter = (isc.EH.getKey() == "Enter");
            if (!isEnter)
                return;
            shouldDismiss = true;
        }

        if (shouldDismiss)
            this.hide();
        if (record != null)
            this.itemClick(record);
    },

    selectOnGeneratedCellClick: function (record, rowNum, colNum, body) {
        if (this.allowMultiSelect && isc.EH.getKey() == "Enter")
            return false;
        return this.Super("selectOnGeneratedCellClick", arguments);
    },

    headerClick: function (fieldNum, header) {
        var rv = this.Super("headerClick", arguments);
        var field = this.getField(fieldNum);
        if (this.isCheckboxField(field) && this.allowMultiSelect)
            this.multiSelectChanged();
        return rv;
    },

    multiSelectChanged: function () {
        var formItem = this.formItem,
            fieldName = formItem.getValueFieldName(),
            sel = this.getSelection(),
            empty = true,
            values = [];

        for (var i = 0; i < sel.length; i++) {
            empty = false;
            var currSel = sel[i];
            values.add(currSel[fieldName]);
        }
        formItem.pickValue(empty ? null : values);
    },

    itemClick: function (record) {
        if (this.allowMultiSelect) {
            this.multiSelectChanged();
        } else {
            var formItem = this.formItem,
                fieldName = formItem.getValueFieldName();
            var value = record[fieldName];
            formItem.pickValue(value);
        }
    },

    hide: function (a, b, c, d) {
        var isVisible = this.isVisible() && this.isDrawn();
        this.Super("hide", a, b, c, d);
        if (!this.formItem)
            return;

        if (isVisible && this.showModal)
            this.formItem.focusInItem();

        this.formItem._showingPickList = null;
        this.formItem.$19g = null;
        if (isVisible)
            this.formItem._pickListHidden();
        delete this.formItem._showOnFilter;
        delete this.formItem.$19i;
        this.clearLastHilite();
    },

    show: function () {
        var alreadyShowing = this.isVisible() && this.isDrawn();
        this.generateClickOnEnter = true;
        this.generateClickOnSpace = this.allowMultiSelect;
        this.bringToFront();
        this.Super("show", arguments);
        if (!alreadyShowing)
            this.formItem._pickListShown();
    },

    showClickMask: function () {
        if (!this.clickMaskUp(this.getID())) {
            var cmID = this.Super("showClickMask", arguments);
            if (this.formItem) {
                var form = this.formItem.form,
                    mask = isc.EH.clickMaskRegistry.find("ID", cmID);
                if (mask._maskedFocusCanvas) mask._maskedFocusCanvas = null;
            }
        }
    },

    _$_backgroundColor: "background-color:",
    _$_color: "color:",
    getCellCSSText: function (record, rowNum, colNum) {
        if (this.selection != null && record == this.selection.getSelectedRecord()) {
            var cssText = [];
            if (this.hiliteColor != null)
                cssText[0] = this._$_backgroundColor
            cssText[1] = this.hiliteColor
            cssText[2] = isc._semi;

            if (this.hiliteTextColor != null)
                cssText[3] = this._$_color;
            cssText[4] = this.hiliteTextColor;
            cssText[5] = isc.semi;
            return cssText.join(isc.emptyString);
        }
    },

    keyDown: function () {
        var keyName = isc.EH.lastEvent.keyName;
        if (keyName == "Tab") {
            this.hide();
            return false;
        }
    },

    _formatCellValue: function (value, record, field, rowNum, colNum) {
        if (this.formItem == null)
            return this.Super("_formatCellValue", arguments);

        var fieldName = this.getFieldName(colNum);
        if ($.defined(this.pickList))
            value = this.formItem.formatPickListValue(value, fieldName, record);

        return this.Super("_formatCellValue", [value, record, field, rowNum, colNum]);
    },

    bodyKeyPress: function (event, eventInfo) {
        var keyName = isc.EH.lastEvent.keyName;
        if (isc.Browser.isSafari) {
            if (keyName == "Tab") {
                this.hide();
                return false;
            }
        }

        var charVal = isc.EH.getKeyEventCharacterValue();
        if (charVal != null) {
            var data = this.formItem.getAllLocalOptions();
            if (isc.isAn.Array(data) && data.length > 1) {
                var typedChar = String.fromCharCode(charVal),
                    typedChar = typedChar.toLowerCase(),
                    formItem = this.formItem,
                    valueField = formItem.getValueFieldName(),
                    currentIndex = data.indexOf(this.getSelectedRecord()),
                    newIndex = currentIndex < (data.length - 1) ? currentIndex + 1 : 0;
                while (newIndex != currentIndex) {
                    if (currentIndex < 0)
                        currentIndex = 0;

                    var value = data[newIndex][valueField];
                    value = formItem.mapValueToDisplay(value);
                    if (isc.isA.String(value) && value.length > 0 &&
                        value.charAt(0).toLowerCase() == typedChar) {
                        this.scrollRecordIntoView(newIndex);
                        this._hiliteRecord(newIndex);
                        return;
                    }
                    newIndex += 1;
                    if (newIndex >= data.length)
                        newIndex = 0;
                }
            }
        }
        if (this.getFocusRow() == null && keyName == "Enter") {
            this.cancel();
            return false;
        }
        return this.Super("bodyKeyPress", arguments);
    },

    dataChanged: function (operation, record, row, lastUpdateData) {
        var data = this.data;
        if (!data) return;

        var data = this.requestVisibleRows();
        if (data && Array.isLoading(data[0]))
            return;
        this.Super("dataChanged", arguments);

        var formItem = this.formItem;
        if (record && this.getSelectedRecord() == record && formItem) {
            var index = this.data.indexOf(record),
                modifiedRecord = index == -1 ? null : this.data.get(index);
            if (modifiedRecord) {
                var fieldName = formItem.getValueFieldName();
                formItem.setValue(modifiedRecord[fieldName]);
            } else {
                formItem.clearValue();
            }
        }
    },

    findRecord: function (name, value) {
        if (this.filterWithValue != false || !$.defined(value))
            return;

        var target = value;
        if (!isc.isA.String(value))
            target = (value + "").trim();
        if (target == "")
            return;

        var data;
        if (isc.isA.ResultSet(this.data))
            data = this.data.localData;
        else if (isc.isA.Tree(this.data))
            data = this.data.getAllNodes();
        else if (isc.isA.Array(this.data))
            data = this.data;

        if (data) {
            var filterFields = name;
            if (!isc.isA.Array(name))
                filterFields = [name];
            var record;
            for (var i = 0; i < data.length; i++) {
                for (var j = 0; j < filterFields.length; j++) {
                    var currentValue = data[i][filterFields[j]];
                    if (!isc.isA.String(currentValue))
                        currentValue = (currentValue + "").trim();
                    if (currentValue == target) {
                        record = data[i];
                        break;
                    } else if (!$.defined(record) && currentValue.indexOf(target) != -1)
                        record = data[i];
                }
            }
            if (record)
                return record;

            if (this.formItem.filterFields && this.formItem.filterFields != filterFields)
                return this.findRecord(this.formItem.filterFields, value);
        }
    },

    createBodies: function () {
        if (this.body && this.body._reused)
            delete this.body._reused;
        this.Super("createBodies", arguments);
    }
};

var bodyDefaults = {
    remapOverStyles: [
        0, // 0 = baseStyle
        2, // 1 = Over(1) --> "Selected"
        2, // 2 = Selected(2)
        2, // 3 = Selected(2) + Over(1) --> "Selected"
        4, // 4 = Disabled(4)
        6, // 5 = Disabled(4) + Over(1) --> "Disabled + Selected"
        6, // 6 = Disabled(4) + Selected(2)
        6, // 7 = Disabled(4) + Selected(2) + Over(1) --> "Disabled + Selected"
        8, // 8 = Dark(8)
        10, // 9 = Dark(8) + Over(1) --> "Dark + Selected"
        10, // 10 = Dark(8) + Selected(2)
        10, // 11 = Dark(8) + Selected(2) + Over(1) --> "Dark + Selected"
        12 // 12 = Dark(8) + Disabled(4)
    ],
    getCellStyleName: function (styleIndex, record, rowNum, colNum) {
        if (this.grid && this.grid.showOverAsSelected) {
            styleIndex = this.remapOverStyles[styleIndex];
        }
        return this.Super("getCellStyleName", [styleIndex, record, rowNum, colNum], arguments);
    }
};
/**
 * DataGridPicker Widgets
 */

isc.ClassFactory.defineClass("ScrollingDataMenu", "DataGrid");
isc.ScrollingDataMenu.addProperties(ScrollingMenuProperties);
isc.ScrollingDataMenu.changeDefaults("filterEditorDefaults", filterEditorDefaults);

isc.ClassFactory.defineClass("DataGridPicker", "ScrollingDataMenu");
isc.DataGridPicker.addClassProperties(dataGridPickerClassProperties);
isc.DataGridPicker.addProperties(dataGridPickerProperties);
isc.DataGridPicker.changeDefaults("bodyDefaults", bodyDefaults);

/**
 * TreeDataGridPicker Widgets
 */
var treeDataGridPickerProperties = {
    wrapCells: false,
    nodeIcon: null,
    folderIcon: null,
    filterWithValue: false,
    updateDataModel: function (filterCriteria, operation, context) {
        return this.getData();
    },

    dataArrived: function (parentNode) {
        this.Super("dataArrived", arguments);
        if (this.formItem && this.formItem.selectDefaultItem)
            this.formItem.selectDefaultItem();
    }
};

isc.ClassFactory.defineClass("ScrollingTreeMenu", "TreeDataGrid");
isc.ScrollingTreeMenu.addProperties(ScrollingMenuProperties);
isc.ScrollingTreeMenu.changeDefaults("filterEditorDefaults", filterEditorDefaults);

isc.ClassFactory.defineClass("TreeDataGridPicker", "ScrollingTreeMenu");
isc.TreeDataGridPicker.addClassProperties(dataGridPickerClassProperties);
isc.TreeDataGridPicker.addProperties(dataGridPickerProperties, treeDataGridPickerProperties);
isc.TreeDataGridPicker.changeDefaults("bodyDefaults", bodyDefaults);