isc.PRCService={getBaseUrl:function(a){return isc.ApplicationContext.baseUrl+"/"+a
},remove:function(e,c,d,b){var a=this.getBaseUrl(e);
if(isc.isA.Number(c)){return this.ajaxInvoke("DELETE",a+"/"+c,undefined,d,b)
}return this.ajaxInvoke("DELETE",a,c,d,b)
},query:function(e,c,d,b){var a=this.getBaseUrl(e);
return this.ajaxInvoke("GET",a,c,d,b)
},save:function(f,d,a,e,c){var b=this.getBaseUrl(f);
if(a){b+="/create"
}return this.ajaxInvoke("PUT",b,d,e,c)
},select:function(f,c,e,b){var a=this.getBaseUrl(f);
var d=f.split("/");
if(d.length<=2){a+="/select"
}return this.ajaxInvoke("GET",a,c,e,b)
},view:function(f,e,d,b){var c;
var a=this.getBaseUrl(f);
if(e!=undefined&&e!=null){if(isc.isA.Number(e)){a+="/"+e
}else{if(isc.isA.Object(e)){c=e
}}}return this.ajaxInvoke("GET",a,c,d,b)
},createNew:function(d,c,b){var a=this.getBaseUrl(d);
a+="/view";
return this.ajaxInvoke("GET",a,undefined,c,b)
},getMethodURL:function(c,a){var b=this.getBaseUrl(c);
if(a){b+="/"+a
}return b
},invoke:function(f,a,d,e,c){var b=this.getMethodURL(f,a);
return this.ajaxInvoke("POST",b,d,e,c)
},ajaxInvoke:function(c,b,d,h,a){if(d){try{if(c=="GET"){if(typeof(d)=="string"){d=$.evalJSON(d)
}}else{if(typeof(d)=="object"){d=$.toJSON(d)
}}}catch(f){isc.warn("json 格式不正确："+f.message);
return
}}if(isc.ApplicationContext.isLocale()==true){var g=c+":"+b;
if(!d){isc.warn(g)
}else{isc.Dialog.create({title:g,message:d,buttons:[isc.Dialog.OK]})
}if(h){if(typeof(d)=="string"){d=$.evalJSON(d)
}h(d?d:{})
}}else{$.ajax({type:c,contentType:"application/json; charset=utf-8",dataType:"json",url:b,cache:false,data:d,success:function(e){if(isc.ApplicationContext.validate(e)){if(h){h(e)
}}},error:function(e,j,i){if(a){a(e,j,i)
}}})
}}};
isc.ApplicationContext={locale:"zh_CN",currentSkin:"Enterprise",servicePrincipal:null,baseUrl:"REST/",moduleManager:null,isValidate:false,cookies:{queryViewFormVisible:false},i18nFiles:["./isomorphic/locales/frameworkMessages","./i18n/Widgets"],jsFiles:[],cssFiles:["css/MainLayout.css","css/widgets.css"],create:function(){var a=isc.ApplicationContext.jsFiles;
a[a.length]="./scene/SceneContext.js";
var b=isc.ApplicationContext.i18nFiles;
b[b.length]="./scene/SceneContext";
if(this.isLocale()){isc.ApplicationContext.baseUrl="./Modules";
isc.ApplicationContext.sessionId="SESSIONID";
this.loadMainLayout()
}else{$.ajax({type:"GET",url:"systemInfo",cache:false,success:function(c){isc.ApplicationContext.systemInfo=c;
isc.addProperties(isc.ApplicationContext,isc.ApplicationContext.systemInfo);
if(isc.ApplicationContext.servicePrincipal){isc.PRCService.invoke("System/SystemInfo","loadMenus",null,function(d){isc.ApplicationContext.remoteMenus=d;
isc.ApplicationContext.loadMainLayout()
})
}else{isc.ApplicationContext.loadMainLayout()
}}})
}},isLocale:function(){return window.location.protocol=="file:"
},converti18nFiles:function(c){var a=[];
for(var b=0;
b<c.length;
b++){if($.defined(c[b])){a.push(c[b]+"_"+this.locale+".properties")
}}return a
},loadMainLayout:function(){var a=isc.ApplicationContext;
var b=this.converti18nFiles(a.i18nFiles);
a.loadJSFiles(b,function(){a.loadJSFiles(a.jsFiles,function(){var c=a.converti18nFiles(a.i18nFilesExt);
a.loadJSFiles(c,function(){var e="./isomorphic/skins/"+isc.ApplicationContext.currentSkin+"/theme.js";
var d=$.concatArray(a.jsFilesExt,e);
a.loadJSFiles(d,function(){isc.FileLoader.loadSkin(a.currentSkin,function(){var f=$.concatArray(a.cssFiles,a.cssFilesExt);
a.loadCssFiles(f,function(){isc.addProperties(isc.ApplicationContext,isc.ApplicationContext.systemInfo);
document.title=a.projectTitle+a.masterTitle+a.version;
delete isc.ApplicationContext.systemInfo;
if($.isFunction(isc.ApplicationContext.beforeCreateMainLayout)){isc.ApplicationContext.beforeCreateMainLayout()
}a.createMainLayout();
if($.isFunction(isc.ApplicationContext.afterCreateMainLayout)){isc.ApplicationContext.afterCreateMainLayout()
}})
})
})
})
})
})
},loadJSFiles:function(a,b){if($.defined(a)&&a.length>0){isc.FileLoader.loadJSFiles(a,function(){isc.ApplicationContext.invokeCallback(b)
})
}else{isc.ApplicationContext.invokeCallback(b)
}},loadCssFiles:function(a,d){if($.defined(a)&&a.length>0){var b=[];
for(var c=0;
c<a.length;
c++){b.push("./isomorphic/skins/"+isc.ApplicationContext.currentSkin+"/"+a[c])
}isc.FileLoader.loadCSSFiles(b,function(){isc.ApplicationContext.invokeCallback(d)
})
}else{isc.ApplicationContext.invokeCallback(d)
}},invokeCallback:function(a){if(a!=null){if(isc.isA.String(a)){isc.evalSA(a)
}else{a()
}}},loadMenus:function(){if($.defined(this.remoteMenus)){this.menus=this.remoteMenus;
delete this.remoteMenus
}return this.menus
},validate:function(a){if(typeof(a)=="string"&&a.indexOf('"id":"405"')!=-1){a=$.evalJSON(a)
}if(a&&a.id&&a.id==405&&a.error){if(this.isValidate){this.isValidate=false;
alert(a.error);
this.logout()
}return false
}return true
},showProcess:function(a){isc.FL.showThrobber(a,null,"[SKIN]/loading.gif")
},hideProcess:function(){isc.FL.hideThrobber()
},createMainLayout:function(){if(this.mainLayout){this.mainLayout.destroy();
delete this.mainLayout
}if(this.servicePrincipal){this.mainLayout=isc.MainLayout.create()
}else{this.mainLayout=isc.LoginLayout.create()
}this.hideProcess()
},login:function(){this.showProcess(isc.i18nInfo.login);
this.loginSystem(function(){isc.ApplicationContext.isValidate=true;
isc.ApplicationContext.createMainLayout()
})
},loginSystem:function(a){if(this.isLocale()){isc.ApplicationContext.servicePrincipal=servicePrincipal;
if(a){a()
}}else{isc.PRCService.invoke("System/SystemInfo","loadSystemInfo",null,function(b){isc.addProperties(isc.ApplicationContext,b);
isc.PRCService.invoke("System/SystemInfo","loadMenus",null,function(c){isc.ApplicationContext.remoteMenus=c;
if(a){a()
}})
})
}},logout:function(){if(this.isLocale()){this.logoutSystem()
}else{$.ajax({type:"GET",url:"logout",cache:false,success:function(){isc.ApplicationContext.logoutSystem()
}})
}},logoutSystem:function(){this.servicePrincipal=null;
$.ajax({type:"GET",url:"systemInfo",cache:false,success:function(a){isc.ApplicationContext.sessionId=a.sessionId;
isc.ApplicationContext.loadMainLayout()
}})
},isEnabled:function(){if(!this.isLocale()&&$.defined(this.enabled)){return this.enabled
}return true
}};
var pageSizeValueMap=[10,20,30,40,50,100,200];
if($.defined(isc.DynamicForm)){var dynamicFormPrototype=isc.DynamicForm.getPrototype();
var _destroyItems=function(a){if(this._pickListCache){for(var b in this._pickListCache){this._pickListCache[b].destroy()
}delete this._pickListCache
}this.destroyItems(a)
};
if($.defined(dynamicFormPrototype.$10k)&&!$.defined(dynamicFormPrototype.destroyItems)){dynamicFormPrototype.destroyItems=dynamicFormPrototype.$10k;
dynamicFormPrototype.$10k=_destroyItems
}else{if($.defined(dynamicFormPrototype._destroyItems)&&!$.defined(dynamicFormPrototype.destroyItems)){dynamicFormPrototype.destroyItems=dynamicFormPrototype._destroyItems;
dynamicFormPrototype._destroyItems=_destroyItems
}}}if($.defined(isc.ListGrid)){var listGridPrototype=isc.ListGrid.getPrototype();
var _saveLocally=function(a,b){this.saveLocally(a,b)
};
if($.defined(listGridPrototype.$336)&&!$.defined(listGridPrototype.saveLocally)){listGridPrototype.saveLocally=listGridPrototype.$336;
listGridPrototype.$336=_saveLocally
}else{if($.defined(listGridPrototype._saveLocally)&&!$.defined(listGridPrototype.saveLocally)){listGridPrototype.saveLocally=listGridPrototype._saveLocally;
listGridPrototype._saveLocally=_saveLocally
}}}if($.defined(isc.MenuBar)){var getButtonProperties=function(c,a){var b=this.getButtonProperties(arguments);
b.autoFit=c.autoFit;
return b
};
var menuBarPrototype=isc.MenuBar.getPrototype();
if($.defined(menuBarPrototype.$36o)){menuBarPrototype.getButtonProperties=menuBarPrototype.$36o;
menuBarPrototype.$36o=getButtonProperties
}else{if($.defined(menuBarPrototype._getButtonProperties)){menuBarPrototype.getButtonProperties=menuBarPrototype._getButtonProperties;
menuBarPrototype._getButtonProperties=getButtonProperties
}}}if($.defined(isc.ViewLoader)){var loadViewReply=function(a,c,b){if(!isc.ApplicationContext.validate(b)){return
}if(b==undefined||b==null){return
}if(b.startsWith('{"error":')){b=$.evalJSON(b);
isc.warn(b.error);
return
}return this.loadViewReply(a,c,b)
};
var viewLoaderPrototype=isc.ViewLoader.getPrototype();
if($.defined(viewLoaderPrototype.$40p)){viewLoaderPrototype.loadViewReply=viewLoaderPrototype.$40p;
viewLoaderPrototype.$40p=loadViewReply
}else{if($.defined(viewLoaderPrototype._loadViewReply)){viewLoaderPrototype.loadViewReply=viewLoaderPrototype._loadViewReply;
viewLoaderPrototype._loadViewReply=loadViewReply
}}}if($.defined(isc.ComboBoxItem)){var comboBoxItemPrototype=isc.ComboBoxItem.getPrototype();
if($.defined(comboBoxItemPrototype.getPickListFilterCriteria)){comboBoxItemPrototype._getPickListFilterCriteria=comboBoxItemPrototype.getPickListFilterCriteria;
comboBoxItemPrototype.getPickListFilterCriteria=function(){if(this.pickList.filterWithValue==false){return{}
}return this._getPickListFilterCriteria()
}
}var overrideFilterPickList=function(){if(!$.defined(this.pickList)||!this.hasFocus){return
}if(!$.defined(this.isEntryTooShortToFilter)||!this.isEntryTooShortToFilter()){var a=this.pickList.originalData||this.pickList.data;
if(a&&isc.ResultTree&&isc.isA.ResultTree(a)){this._showOnFilter=true;
this.$19i=true;
this.filterComplete();
return
}}this.filterWithValue=true;
return this.overrideFilterPickList(arguments)
};
if($.defined(comboBoxItemPrototype.$19p)){comboBoxItemPrototype.overrideFilterPickList=comboBoxItemPrototype.$19p;
comboBoxItemPrototype.$19p=overrideFilterPickList
}else{if($.defined(comboBoxItemPrototype._filterPickList)){comboBoxItemPrototype.overrideFilterPickList=comboBoxItemPrototype._filterPickList;
comboBoxItemPrototype._filterPickList=overrideFilterPickList
}}var fireTabCompletion=function(){if(this.canBeUnknownValue){return
}return this.fireTabCompletion(arguments)
};
if($.defined(comboBoxItemPrototype.$82r)){comboBoxItemPrototype.fireTabCompletion=comboBoxItemPrototype.$82r;
comboBoxItemPrototype.$82r=fireTabCompletion
}else{if($.defined(comboBoxItemPrototype._fireTabCompletion)){comboBoxItemPrototype.fireTabCompletion=comboBoxItemPrototype._fireTabCompletion;
comboBoxItemPrototype._fireTabCompletion=fireTabCompletion
}}isc.ComboBoxItem.addMethods({_pickListShown:function(){return this.pickListShown(arguments)
},_pickListHidden:function(){this.pickListHidden(arguments)
},keyDown:function(){this.Super("keyDown",arguments);
var a=isc.EH.lastEvent.keyName;
if(a=="Tab"){if(this.pickList){this.pickList.hide()
}}}})
}if($.defined(isc.FormItem)){var formItemPrototype=isc.FormItem.getPrototype();
if(!$.defined(formItemPrototype._getTitleHTML)&&$.defined(formItemPrototype.getTitleHTML)){formItemPrototype._getTitleHTML=comboBoxItemPrototype.getTitleHTML;
isc.FormItem.addMethods({getTitleHTML:function(){var a=this.getTitle();
if($.defined(a)){return this._getTitleHTML(arguments)
}return null
}})
}}if($.defined(isc.SelectItem)){var selectItemPrototype=isc.SelectItem.getPrototype();
if($.defined(selectItemPrototype.$19j)&&!$.defined(selectItemPrototype._pickListShown)){isc.SelectItem.addMethods({_pickListShown:function(){return this.$19j(arguments)
}})
}if($.defined(selectItemPrototype.$19h)&&!$.defined(selectItemPrototype._pickListHidden)){isc.SelectItem.addMethods({_pickListHidden:function(){return this.$19h(arguments)
}})
}}isc.ClassFactory.defineClass("TimerEx","Class");
isc.TimerEx.addProperties({valid:true});
isc.TimerEx.addMethods({setInterval:function(b,a,d,c){if(this.timers==undefined){this.timers={}
}var e=this.timers[b];
if(!$.defined(e)){e={interval:a,dateTime:new Date(),args:c};
this.timers[b]=e;
e.timerEvent=isc.Timer.setTimeout({target:this,methodName:"_intervalCall",args:[b]},d?d:a)
}else{e.interval=a;
e.delay=d;
e.args=c
}},destroy:function(){this.valid=false;
if($.defined(this.timers)){for(var a in this.timers){this.clear(a)
}}},clear:function(a){var b=this.timers[a];
if($.defined(b)){isc.Timer.clear(b.timerEvent);
delete this.timers[a]
}},_intervalCall:function(b){var a=false;
var c=this.timers[b];
if(c&&this.owner[b]&&this.valid){a=this.owner[b].apply(this.owner,c.args)
}if(a){c.timerEvent=isc.Timer.setTimeout({target:this,methodName:"_intervalCall",args:[b]},c.interval)
}else{this.clear(b)
}}});
isc.ClassFactory.defineClass("Service","Class");
isc.Service.addClassProperties({DefaultComponentProperties:{extendProperties:function(){var a=this["overrideProperties"];
this.extendObjectProperties(a,this)
},extendObjectProperties:function(d,a){if(d){for(var b in d){var c=d[b];
var e=a[b];
if(e&&isc.isA.Object(e)){if(isc.isA.Array(c)){if(isc.isA.Array(e)){this.extendArrayProperties(c,e)
}}else{if(isc.isA.Object(c)){this.extendObjectProperties(c,e)
}}}else{if(isc.isA.Object(c)){a[b]=isc.addProperties({},c)
}else{a[b]=c
}}}}},extendArrayProperties:function(e,f){var d=[];
for(var c=0;
c<e.length;
c++){var b=e[c];
var a=this.findArrayInstance(f,b);
if(a){this.extendObjectProperties(b,a)
}else{d[d.length]=isc.addProperties({},b)
}}for(var c=0;
c<d.length;
c++){f[f.length]=d[c]
}},findArrayInstance:function(g,d){var f=["id","Id","ID","name","Name"];
for(var c=0;
c<g.length;
c++){var a=g[c];
for(var b=0;
b<f.length;
b++){var e=f[b];
if(d[e]!=undefined&&d[e]==a[e]){return a
}}}},bindListener:function(a){if(a){if(a.getBindListeners){var c=a.getBindListeners();
if(c){if(!isc.isA.Array(c)){c=[c]
}for(var b=0;
b<c.length;
b++){this.bindListener(c[b])
}}}else{if(this.bindListeners){this.bindListeners.push(a)
}else{this.bindListeners=[a]
}if(a.name&&this[a.name]==undefined){this[a.name]=a
}}}},fireEvent:function(b){if(this.bindListeners){var e=arguments[1];
var h=[b.substring(0,1).toLowerCase()+b.substring(1)];
if(this.name&&e!=this){if(e&&e.name){h.push(this.name+"_"+e.name+"_"+b)
}h.push(this.name+"_"+b)
}if(this.getClassName!=undefined&&typeof(this.getClassName)=="function"){var f=this.getClassName().substring(0,1).toLowerCase()+this.getClassName().substring(1);
if(f!=this.name){if(e&&e.name&&e!=this){h[h.length]=f+"_"+e.name+"_"+b
}h[h.length]=f+"_"+b
}}var g=[this];
for(var a=1;
a<arguments.length;
a++){g.push(arguments[a])
}var d={eventNames:h,params:g,foundEvent:null,lastResult:null};
for(var c=0;
c<this.bindListeners.length;
c++){if(this.invokeEvent(this.bindListeners[c],d)==false){return false
}}if(d.foundEvent!=null){return d.lastResult
}}},hasPermission:function(a){if(this.service){return this.service.checkPermission(a)
}return true
},invokeEvent:function(e,c){for(var b=0;
b<c.eventNames.length;
b++){var d=e[c.eventNames[b]];
if(d!=undefined&&d!=null){var a=d.apply(e,c.params);
if(a==false){return a
}c.foundEvent=d;
if(a!=undefined){c.lastResult=a
}}}}}});
isc.Service.addMethods({initialize:function(){this.Super("initialize",arguments);
isc.addProperties(this,isc.Service.DefaultComponentProperties);
this.createDataSet()
},getMethodURL:function(a){return isc.PRCService.getMethodURL(this.module.jndiName,a)
},invoke:function(a,c,d,b){isc.PRCService.invoke(this.module.jndiName,a,c,function(e){if(d){d(e,d)
}L
},function(e,g,f){if(b){b(e,g,f,b)
}})
},createDataSet:function(){if(this.owner.dataSetProperties){if(this.dataSet==undefined||this.dataSet==null){this.dataSet=isc.DataSet.create({},isc.Service.DefaultComponentProperties,{service:this,dataSetProperties:this.owner.dataSetProperties});
this.dataSet.initialize()
}}},initComponent:function(){if(this.components){for(var b=0;
b<this.components.length;
b++){var a=this.components[b];
if(a.extendProperties!=undefined&&a.extendProperties!=null){a.extendProperties()
}a.service=this;
a.bindListener(this);
if(a.tableName){var c=this.dataSet[a.tableName];
if(c){c.bindListener(a);
a.getBindTable=function(){return this.service.dataSet[this.tableName]
}
}}if(a.initialize){a.initialize()
}}}},addComponent:function(b,a){if(this.components==undefined){this.components=[a]
}else{this.components[this.components.length]=a
}if(b!=undefined){this[b]=a
}},createComponent:function(f,c,b){var a=this[b];
if(a==undefined){var d={service:this};
if(b){d.name=b
}isc.addProperties(d,isc.Service.DefaultComponentProperties,c);
var e=this.owner[d.name+"Properties"];
if(d.name!=undefined&&e!=undefined){isc.addProperties(d,e)
}a=f.create(d);
if(b!="service"){this.addComponent(b,a)
}}return a
},checkPermission:function(a){if(a==undefined||a==null){return false
}var d=this.owner.permissions;
if(d==undefined||d==null||a.permission==undefined||a.permission==null){return true
}var c=a.permission;
if(!isc.isA.Array(c)){c=[c]
}for(var b=0;
b<c.length;
b++){if(d.indexOf(c[b])!=-1){return true
}}return false
},existsPermission:function(b){var c=this.owner.permissions;
if(c==undefined||c==null||b==undefined||b==null){return true
}if(!isc.isA.Array(b)){b=[b]
}for(var a=0;
a<b.length;
a++){if(c.indexOf(b[a])!=-1){return true
}}return false
},setInterval:function(b,a,d,c){if(!$.defined(this.timers)){this.timers=isc.TimerEx.create({owner:this})
}this.timers.setInterval(b,a,d,c)
},clearTimer:function(a){if($.defined(this.timers)){this.timers.clear(a)
}},destroy:function(){if($.defined(this.timers)){this.timers.destroy();
delete this.timers
}if(this.components){for(var a=0;
a<this.components.length;
a++){this.components[a].destroy()
}}if(this.dataSet){this.dataSet.destroy()
}this.Super("destroy",arguments)
}});
isc.ClassFactory.defineClass("StatusBar","HLayout");
isc.StatusBar.addClassProperties({DefaultItemProperties:{align:"left",className:"statusBarContent",layoutLeftMargin:2,width:"100%",wrap:false,valign:"center",contents:"&nbsp;"},DefaultFormProperties:{},DefaultFieldProperties:{wrapTitle:false}});
isc.StatusBar.addProperties({autoDraw:false,styleName:"statusBar",width:"100%",height:25,overflow:"hidden",padding:1,membersMargin:1,autoFit:true,defaultItemProperties:{height:24},defaultFormProperties:{border:"0px solid #D3D3D3;"},defaultFieldProperties:{height:20},items:[],data:[]});
isc.StatusBar.addMethods({initialize:function(){if(this.data.length>0){for(var b=0;
b<this.data.length;
b++){var c=this.data[b];
if(c.type==undefined||c.type=="label"){c=isc.Label.create({},isc.StatusBar.DefaultItemProperties,this.defaultItemProperties,c)
}else{if(c.type=="form"){for(var a=0;
a<c.fields.length;
a++){var d=c.fields[a];
if(d.title==undefined||d.title==null){var e=this.getTitle(d.name);
if(e!=undefined&&e!=null){d.title=e
}}isc.addProperties(d,isc.StatusBar.DefaultFieldProperties,this.defaultFieldProperties)
}c=isc.DynamicForm.create({},isc.StatusBar.DefaultItemProperties,isc.StatusBar.DefaultFormProperties,this.defaultFormProperties,c)
}}if(c!=null){this[c.name]=c;
this.addMembers(c)
}}}else{if(this.items.length>0){for(var b=0;
b<this.items.length;
b++){this[c.name]=c;
this.addMembers(this.items[b])
}}}},getTitle:function(a){if(isc.i18nStatus[a]){return isc.i18nStatus[a]
}}});
isc.ClassFactory.defineClass("ToolStripBar","ToolStrip");
isc.ToolStripBar.addClassProperties({Types:{menu:"ToolStripMenuButton",form:"DynamicForm",button:"ToolStripButton",selector:"selector",separator:"ToolStripSeparator",resizer:"ToolStripResizer"},MenuButtonDefaultProperties:{showMenuButtonImage:false,iconOrientation:"left",autoFit:true,iconSize:16,iconHeight:16,iconWidth:16},FormDefaultProperties:{border:"0px solid #D3D3D3;"},selectorFieldDefaultProperties:{wrapTitle:false,showTitle:false,width:"*",changed:function(b,a,c){b.toolBar.itemChanged(a,c)
}}});
isc.ToolStripBar.addProperties({autoDraw:false,service:null,width:"100%",overflow:"hidden",iconSize:16,layoutMargin:3,defaultHide:null,defaultDisabled:null,items:null,data:null,menu:null});
isc.ToolStripBar.addMethods({initWidget:function(){if(this.data==undefined||this.data==null){if(this.DefaultDataProperties!=undefined){this.data=isc.clone(this.DefaultDataProperties)
}}if(this.appendDataProperties){this.data=this.data.concat(this.appendDataProperties)
}this.Super("initWidget",arguments)
},checkPermissions:function(d){if(d){if(isc.isA.Array(d)){var e=[];
for(var a=0;
a<d.length;
a++){var c=this.checkPermissions(d[a]);
if(c!=undefined&&c!=null){if(e.length==0||e[e.length-1]!=c){e.push(c)
}}}if(e.length>0){return e
}}else{if(this.hasPermission(d)){if(d.type&&d.type.toLowerCase()=="menu"){var b=this[d.name+"MenuData"];
if(b){d.data=b
}d.data=this.checkPermissions(d.data);
if(d.data&&d.data.length>0){return d
}}else{if(d.submenu){d.submenu=this.checkPermissions(d.submenu);
if(d.submenu&&d.submenu.length>0){return d
}}else{return d
}}}}}},initialize:function(){this.data=this.checkPermissions(this.data);
this.addMembers("separator");
if(this.data!=null&&this.data.length>0){this.sortToolItemData(this.data);
for(var a=0;
a<this.data.length;
a++){var b=this.createToolItem(this.data[a]);
if(b){if(this.items==null){this.items=[b]
}else{this.items[this.items.length]=b
}this.addMembers(b)
}}}else{if(this.items!=null&&this.items.length>0){for(var a=0;
a<this.items.length;
a++){this.addMembers(this.items[a])
}}}if(this.hiddenItems!=null){this.setItemVisible(this.hiddenItems,false)
}},sortToolItemData:function(e){if(e.length>0){var c=[];
for(var b=0;
b<e.length;
b++){var d=e[b];
var a=(e.length-b)*10;
if(d.index!=undefined&&d.index!=null){a=d.index
}while(c[a]!=undefined){a++
}c[a]=d
}e.length=0;
for(var b=c.length-1;
b>=0;
b--){if(c[b]!=undefined){e[e.length]=c[b]
}}}},getTitle:function(a){if(isc.i18nButton[a]){return isc.i18nButton[a]
}},createToolItem:function(a){var c=a;
if(typeof(a)!="string"){if(a.type==undefined||a.type==null){a.type="button"
}if(a.title==undefined||a.title==null){var f=this.getTitle(a.name);
if(f){a.title=f
}}a.type=a.type.toLowerCase();
var e=isc.ToolStripBar.Types[a.type];
if(e!=null){if(a.type=="menu"||a.type=="button"){if(!$.defined(a.showIcon)){a.showIcon=true
}if(a.showIcon&&!$.defined(a.icon)){if(a.type=="button"){a.icon="[SKIN]/button/"+a.name+".png"
}else{a.icon="[SKIN]/../button/"+a.name+".png"
}}}if(a.type=="menu"){isc.addProperties(a,isc.ToolStripBar.MenuButtonDefaultProperties)
}else{if(a.type=="form"){this.createForm(a)
}}if(a.type=="selector"){var d={type:"form",width:a.width,minWidth:50,numCols:1,fields:[a]};
this.createForm(d);
c=isc.DynamicForm.create(d)
}else{c=isc.ClassFactory.newInstance(e,c)
}var b=this;
if(a.type=="button"){if(!$.defined(a.click)){c.click=function(){b.itemClick(this)
}
}a.button=c
}else{if(a.type=="menu"){c.menu=this.createMenu(a)
}else{if(a.type=="form"||a.type=="selector"){c.toolBar=this
}}}this[c.name]=c
}}return c
},createMenu:function(a){return isc.MenuExt.create({menuData:a,toolBar:this})
},createForm:function(a){var b=isc.addProperties(a,isc.ToolStripBar.FormDefaultProperties);
if(b.fields){for(var c=0;
c<b.fields.length;
c++){var d=isc.ToolStripBar[b.fields[c].type+"FieldDefaultProperties"];
if($.defined(d)){b.fields[c]=isc.addProperties(b.fields[c],d)
}}}return b
},itemChanged:function(b,c){var a=this.fireEvent(isc.ToolBarEx.Events.ItemChanged,b,c);
if(a==undefined){isc.confirm("这个功能还没做呐！",null,{buttons:[isc.Dialog.OK]})
}},itemClick:function(b){var a=this.fireEvent(isc.ToolBarEx.Events.ItemClick,b);
if(a==undefined){isc.confirm("这个功能还没做呐！",null,{buttons:[isc.Dialog.OK]})
}},findItems:function(g){var f=[];
if(g==null||g==undefined){return e
}if(!isc.isAn.Array(g)){g=[g]
}if(g.length>0){for(var c=0;
c<this.items.length;
c++){var d=this.items[c];
var e=false;
for(var a=0;
a<g.length;
a++){if(this.isEquals(d,g[a])){f.push(d);
e=true;
break
}}if(!e&&d.menu){var b=d.menu.findItems(g);
for(var a=0;
a<b.length;
a++){f.push(b[a])
}}}}return f
},setItemVisible:function(b,a){var d=this.findItems(b);
for(var c=0;
c<d.length;
c++){if(a){d[c].show()
}else{d[c].hide()
}}},isEquals:function(b,a){if(b==null||b==undefined){return false
}if(a==null||a==undefined){return false
}return b.name==a
},setButtonState:function(b,a){this.fireEvent(isc.ToolBarEx.Events.CheckState,b,a);
if(b!=null){for(var d=0;
d<b.length;
d++){var c=this[b[d]];
if(c){c.setDisabled(true)
}}}if(a!=null){for(var d=0;
d<a.length;
d++){var c=this[a[d]];
if(c){c.setDisabled(false)
}}}}});
isc.WidthPerChar=8;
var formItemErrorHtml={transTableHtml:function(b,a){if(b.getClassName()=="TextItem"||b.getClassName()=="FloatItem"||b.getClassName()=="TextAreaItem"||b.getClassName()=="PasswordItem"||b.getClassName()=="CheckboxItem"||b.getClassName()=="StaticTextItem"){return this.transTextItem(b,a)
}if(b.getClassName()=="SpinnerItem"){return this.transSpinnerItem(b,a)
}if(b.getClassName()=="SelectItem"||b.getClassName()=="ComboBoxItem"){return this.transSelectItem(b,a)
}if(b.getClassName()=="DateItem"){return this.transDateItem(b,a)
}if(b.getClassName()=="DateTimeItem"){return this.transDateTimeItem(b,a)
}if(b.getClassName()=="UnitValueItem"||b.getClassName()=="PercentItem"||b.getClassName()=="BooleanItem"){return this.transCanvasItem(b,a)
}alert(b.getClassName());
alert(a);
return a
},removeFormCellError:function(b){var a=$("<p></p>").append(b);
a.find("div.viewformCellError").css("border","none");
return a[0].innerHTML
},transTextItem:function(c,b){var a=$("<p></p>").append(b);
a.children("table:first").css("width","100%");
a.find("td:first").css("width",c.fieldWidth-3);
a.find("[name="+c.name+"]").css("width","100%");
return a[0].innerHTML
},transSpinnerItem:function(c,b){var a=$("<p></p>").append(b);
a.children("table:first").css("width","100%");
a.find("td:first").css("width",c.fieldWidth-19);
a.find("[name="+c.name+"]").css("width","100%");
return a[0].innerHTML
},transCanvasItem:function(c,b){var a=$("<p></p>").append(b);
a.children("table:first").css("width","100%");
a.find("td:first").css("width",c.fieldWidth);
return a[0].innerHTML
},transSelectItem:function(d,b){var a=$("<p></p>").append(b);
a.children("table:first").css("width","100%");
var e=a.find("td:first");
e.css("width",d.fieldWidth-3);
var c=e.find("table:first");
c.css("width","100%");
c.find("td:first").css("width","100%");
c.find("input").css("width","100%");
c.find("div").css("width","100%");
return a[0].innerHTML
},transDateItem:function(c,b){var a=$("<p></p>").append(b);
a.children("table:first").css("width","100%");
a.find("td.formHint").css("width","100%");
return a[0].innerHTML
},transDateTimeItem:function(c,b){var a=$("<p></p>").append(b);
a.children("table:first").css("width","100%");
a.find("td:first").css("width",c.fieldWidth);
a.find("td.formHint").css("width","100%");
return a[0].innerHTML
}};
isc.ClassFactory.defineClass("ViewForm","DynamicForm");
isc.ViewForm.addClassProperties({DefaultFieldProperties:{titleStyle:"viewformTitle",cellStyle:"viewformCell",errorMessageWidth:200,showDisabled:true,type:"StaticText",titleVAlign:"top"},DefaultFormItemProperties:{isReadOnly:function(){var b=this.form;
var a=this;
while(a.parentItem){if(a.canEdit!=null){return !a.canEdit
}a=a.parentItem
}if(b&&b.canEditField){return !b.canEditField(a)
}return this.canEdit?!this.canEdit:false
},setReadOnly:function(a){if(a){this.setCanEdit(false);
this.disable()
}else{this.setCanEdit(true);
this.enable()
}}},initEditorType:function(a){if(!$.defined(a.editorType)){if(a.type=="integer"||a.dataType=="integer"){if($.defined(a.min)&&$.defined(a.max)){a.editorType="spinner"
}}}}});
isc.ViewForm.addProperties({autoDraw:false,width:"100%",height:"100%",formBorder:"1px solid #dddde5",columnCount:1,cellPadding:1,showRowNum:false,errorOrientation:"right",requiredTitlePrefix:"<font color=red>",requiredTitleSuffix:"&nbsp;:</font>",rightTitlePrefix:"</font>:&nbsp;",showErrorText:false});
isc.ViewForm.addMethods({initWidget:function(){this.Super("initWidget",arguments);
if(!$.defined(this.service)){this.initialize()
}},initialize:function(){this.initColumns();
this.initFields();
this.initForm(this)
},getInnerHTML:function(c){var a=$("<p></p>").append(this.Super("getInnerHTML",arguments));
var b=a.find("table:first");
if(this.formBorder){b.css("border-left",this.formBorder);
b.css("border-top",this.formBorder);
b.css("border-right","none");
b.css("border-bottom","none")
}b.css("width","100%");
return a[0].innerHTML
},initForm:function(c){var a=c.getItems();
if(a!=undefined){for(var b=0;
b<a.length;
b++){var e=a[b];
if(e.getInnerHTML!=undefined&&e._getInnerHTML==undefined){e._getInnerHTML=e.getInnerHTML;
e.getInnerHTML=function d(f,j,h,i){var g=this._getInnerHTML(f,j,h,i);
if(h&&this.hasErrors()&&this.getErrorOrientation()==isc.Canvas.RIGHT){g=formItemErrorHtml.transTableHtml(this,g);
g=formItemErrorHtml.removeFormCellError(g)
}return g
}
}if(!$.defined(e.focus)){e.focus=function(g,f){g.itemFocus(f)
}
}if(e.getClassName()=="TextItem"||e.getClassName()=="TextAreaItem"||e.getClassName()=="PasswordItem"||e.getClassName()=="FloatItem"||e.getClassName()=="SpinnerItem"){if(!$.defined(e.blur)){e.blur=function(g,f){g.itemBlur(f)
}
}}else{if(e.getClassName()=="CheckboxItem"){e.requiredTitlePrefix="";
e.requiredTitleSuffix=""
}else{if(e.getClassName()=="SelectItem"){if(!$.defined(e._getValueMap)){e._getValueMap=e.getValueMap;
e.getValueMap=function(){return this.valueMap||c.getFormItemValueMap(this)
}
}}else{if(e.getClassName()=="ComboBoxItem"){if(!$.defined(e._getValueMap)){e._getValueMap=e.getValueMap;
e.getValueMap=function(){return this.valueMap||c.getFormItemValueMap(this)
}
}e.getPickData=function(){return this.pickData||c.getFormItemPickData(this)
}
}}}}if(!$.defined(e.formatValue)){e.formatValue=function(i,f,h,g){if(h.formatValue){return h.formatValue(i,f,h,g)
}}
}isc.addProperties(e,isc.ViewForm.DefaultFormItemProperties)
}}},initColumns:function(){this.numCols=this.columnCount*2;
this.showErrorText=(this.columnCount==1);
var a=this["colWidths"+this.columnCount];
if(a!=undefined&&a!=null){this.colWidths=a
}else{if(this.columnCount==1){this.colWidths=["25%","75%"]
}else{if(this.columnCount==2){this.colWidths=["15%","35%","15%","35%"]
}else{if(this.columnCount==3){this.colWidths=["10%","23%","10%","23%","10%","23%"]
}else{if(this.columnCount==4){this.colWidths=["5%","20%","5%","20%","5%","20%"]
}}}}}},initFields:function(){if(this.sourceFields==undefined||this.sourceFields==null){if(this.fields&&this.fields.length>0){this.sourceFields=this.fields
}}var a=this.iniFormFields(this.sourceFields);
if(a.fields.length>0){while(a.showCount%this.columnCount!=0){a.fields.push(isc.ViewForm.DefaultFieldProperties);
a.showCount++
}this.setFields(a.fields)
}},iniFormFields:function(e){var c=0;
var a=[];
if(e&&e.length>0){for(var b=0;
b<e.length;
b++){var d=e[b];
var f=this.createField(d);
this.initVisible(f);
this.initAlign(f);
this.initDisplayWidth(f);
this.initRowSpan(f);
isc.ViewForm.initEditorType(f);
a.push(f);
if(d.hidden!=true){c+=f.rowSpan?f.rowSpan:1
}}}return{showCount:c,fields:a}
},createField:function(a){var b=isc.addProperties({},isc.ViewForm.DefaultFieldProperties);
if(this.titleStyle){b.titleStyle=this.titleStyle
}if(this.cellStyle){b.cellStyle=this.cellStyle
}isc.addProperties(b,a);
isc.LogicFieldFactory.createField(b);
if(this.overrideFieldProperties&&this.overrideFieldProperties[b.name]){isc.addProperties(b,this.overrideFieldProperties[b.name])
}if(b.pickListFields){if(!$.defined(b.editorType)){b.editorType="ComboBoxItem"
}if(!$.defined(b.addUnknownValues)){b.addUnknownValues=false
}}return b
},initVisible:function(a){if($.defined(a.visible)){a.hidden=!a.visible
}if(a.name=="rowNum"){a.hidden=!this.showRowNum
}},initAlign:function(a){if(!$.defined(a.textAlign)){a.textAlign="left";
if(a.type!="StaticText"&&a.type!="enum"&&!a.displayField){if(a.type=="integer"||a.dataType=="integer"||a.type=="float"||a.dataType=="float"){a.textAlign="right"
}}}},initDisplayWidth:function(b){if(b.displayWidth==undefined){var a;
if(b.length>=30){a=30
}else{if(b.length>=20){a=25
}else{if(b.length>=15||b.length==undefined){a=20
}else{if(b.length>=10){a=15
}else{a=10
}}}}b.displayWidth=a
}if(b.length==undefined){if(b.dataType=="date"||b.dataType=="datetime"){b.length=19
}else{if(b.dataType=="integer"||b.dataType=="float"){b.length=15
}else{if(b.dataType=="boolean"){if(b.type=="enum"){b.length=10
}}}}}if(!$.defined(b.width)){if(b.length==undefined){b.width="100%"
}else{b.width=b.displayWidth*isc.WidthPerChar
}}if($.defined(b.width)&&isc.isA.Number(b.width)){b.fieldWidth=b.width
}},initRowSpan:function(a){if(a.type!="StaticText"&&a.length>=60){a.type="textArea"
}if(a.length>=60&&a.length<=150){a.rowSpan=2
}else{if(a.length>150&&a.length<=300){a.rowSpan=3
}else{if(a.length>300){a.rowSpan=4
}}}},canEditField:function(b,a){if(a&&a.canEditField){return a.canEditField(b)
}if(b&&b.canEdit){return b.canEdit
}if(a&&a.canEdit){return a.canEdit
}return this.canEdit?this.canEdit:false
},setColumnCount:function(a){if(a>0&&a<=4&&a!=this.columnCount){this.columnCount=a;
this.initWidget();
this.initialize()
}},ajaxSubmit:function(c,b){var a={dataType:"json",success:function(d){if(c){c(d)
}return false
},error:function(){if(b){b()
}return false
}};
this.saveData();
$("#"+this.getFormID()).ajaxSubmit(a)
}});
isc.ClassFactory.defineClass("DataForm","ViewForm");
isc.DataForm.addClassProperties({Events:{ItemFocus:"ItemFocus",ItemBlur:"ItemBlur"},TableEventsProperties:{table_OnFieldErrors:function(c,a,d,b){if(a){this.showFieldErrors(a.name)
}},table_RowDataStateChanged:function(a,c,b){this.refreshFormItemsVisible()
},table_DataStateChanged:function(a,b){this.refreshFormItemsVisible()
},table_OnClearFieldErrors:function(b,a,c){this.table_OnFieldErrors(b,a,c)
},table_OnColumnVisibleChanged:function(b,a){var c=this.getItem(a.name);
if(c){if(c.isVisible()&&!a.isVisible()){c.hide()
}else{if(!c.isVisible()&&a.isVisible()){c.show()
}}}},table_OnColumnReadOnlyChanged:function(b,a){var c=this.getItem(a.name);
if(c){c.setReadOnly(a.isReadOnly())
}},table_AfterInsert:function(a,b){this.table_AfterLoad(a)
},table_AfterSave:function(a){this.table_AfterLoad(a)
},table_AfterLoad:function(a){this.setHiliteRequiredFields();
var b=this.getBindTable();
if(b){var c=b.getCurrentRow();
if(c!=null){this.editRecord(c)
}else{this.editRecord({})
}}this.refreshFormItemsVisible()
},table_CurrentChanged:function(b,a,e){var d=this.getFocusItem();
if(d==undefined||d==null||d.name!=a.name){var c=this.getField(a.name);
if(c&&c.isVisible()&&!c.isDisabled()){this.focusInItem(c)
}}},table_CurrentRowChanged:function(a,b){this.table_AfterLoad(a)
}}});
isc.DataForm.addClassProperties({Events:{ItemChange:"ItemChange",ItemChanged:"ItemChanged"}});
isc.DataForm.addProperties({canEdit:true,hiliteRequiredFields:true});
isc.DataForm.addMethods({initialize:function(){isc.addProperties(this,isc.DataForm.TableEventsProperties);
this.createFields();
this.Super("initialize",arguments);
this.table_AfterLoad(this.getBindTable())
},createFields:function(){var b=this.getBindTable();
if(b){var a=b.columns.getFields(this.fieldNames);
if(a.length>0){this.fields=a;
this.useAllDataSourceFields=false
}}},hasErrors:function(){if(this.hasFieldErrors()){return true
}return this.Super("hasErrors",arguments)
},getBindTable:function(){},hasFieldErrors:function(c){var b=this.getBindTable();
if(b){var a=b.getCurrentRow();
if(a){return a.hasErrors(c)
}}return this.Super("hasFieldErrors",arguments)
},canEditField:function(d,b){var c=this.getBindTable();
if(c){if(d==undefined||d==null){return !c.isReadOnly()
}var a=c.getCurrentRow();
if(a==undefined||a==null){return !c.isReadOnly()
}return !a.isReadOnly(d.name)
}return this.Super("canEditField",arguments)
},getFieldErrors:function(c){var b=this.getBindTable();
if(b){if(isc.isA.FormItem(c)){c=c.name
}var a=b.getCurrentRow();
if(a){return a.getFieldErrors(c)
}}return this.Super("getFieldErrors",arguments)
},refreshFormItemsVisible:function(){var a=this.getItems();
if(a==undefined||a==null){return
}var e=this.getBindTable();
if(e==undefined||e==null){return
}var b=e.getCurrentRow();
if(b==undefined||b==null){return
}for(var c=0;
c<a.length;
c++){var d=a[c];
var f=b.isVisible(d.name);
if(d.isVisible()&&!f){d.hide()
}else{if(!d.isVisible()&&f){d.show()
}}}},refreshFormItemsReadOnly:function(){var a=this.getItems();
if(a==undefined||a==null){return
}var e=this.getBindTable();
if(e==undefined||e==null){return
}var b=e.getCurrentRow();
if(b==undefined||b==null){return
}for(var c=0;
c<a.length;
c++){var d=a[c];
var f=b.isReadOnly(d.name);
if(d.isDisabled()&&!f){d.setDisabled(false)
}else{if(!d.isDisabled()&&f){d.setDisabled(true)
}}}},setHiliteRequiredFields:function(){var a=true;
if(this.isReadOnly()){a=false
}else{if(this.hitRequiredFields!=undefined&&this.hitRequiredFields!=null){a=this.hitRequiredFields
}}if(this.hiliteRequiredFields!=a){this.hiliteRequiredFields=a
}},getFormItemValueMap:function(a){var b=this.getBindTable();
if(b){var c=b.getCurrentRow();
if(c!=null){return c.getValueMap(a.name)
}}return this.Super("getFormItemValueMap",arguments)
},getFormItemPickData:function(a){var b=this.getBindTable();
if(b){var c=b.getCurrentRow();
if(c!=null){return c.getPickData(a.name)
}}},formatValue:function(e,a,c,d){var f=this.getBindTable();
if(f){var g=f.getCurrentRow();
if(g!=null){return g.format(d.name,e)
}}var b=this.Super("formatValue",arguments);
return $.defined(b)?b:e
},isReadOnly:function(c){var b=this.getBindTable();
if(b){if(b.isReadOnly()){return true
}if(c){var a=b.getCurrentRow();
if(a){return a.isReadOnly(c)
}}return !b.isUpdateAble()
}return this.readOnly
},itemChange:function(b,c,a){this.Super("itemChange",arguments);
if(this.fireEvent){this.fireEvent(isc.DataForm.Events.ItemChange,b,c,a)
}return !this.isReadOnly(b.name)
},itemChanged:function(a,c){var b=this.getBindTable();
if(b!=undefined&&b!=null){if(b.isUpdateAble()){b.update(a.name,c)
}}else{this.Super("itemChanged",arguments)
}if(this.fireEvent){this.fireEvent(isc.DataForm.Events.ItemChanged,a,c)
}},itemFocus:function(a){var b=this.getBindTable();
if(b){b.setCurrent(a.name)
}else{this.Super("itemChanged",arguments)
}},itemBlur:function(a){var b=this.getBindTable();
if(b){b.update(a.name,a.getValue())
}else{this.Super("itemBlur",arguments)
}}});
isc.ClassFactory.defineClass("DataGridToolBar","ToolStripBar");
isc.DataGridToolBar.addProperties({padding:0,height:2,layoutTopMargin:0,layoutBottomMargin:0,layoutLeftMargin:2,layoutRightMargin:15,styleName:"dataGridToolBar",data:[{name:"first",icon:"[SKIN]/button/firstPage.png",prompt:isc.i18nButton.first},{name:"prior",icon:"[SKIN]/button/priorPage.png",prompt:isc.i18nButton.prior},{name:"next",icon:"[SKIN]/button/nextPage.png",prompt:isc.i18nButton.next},{name:"last",icon:"[SKIN]/button/lastPage.png",prompt:isc.i18nButton.last}]});
isc.DataGridToolBar.addMethods({initWidget:function(){isc.addProperties(this,isc.Service.DefaultComponentProperties);
this.Super("initWidget",arguments);
this.addMembers(this.getComponents())
},getComponents:function(){var a=this;
this.pageNumForm=isc.DynamicForm.create({width:240,titleWidth:"*",numCols:4,colWidths:["25%","20%","35%","20%"],fields:[{name:"pageNum",title:isc.i18nStatus.pageNum,width:60,valueMap:[0]},{name:"pageSize",title:isc.i18nStatus.pageSize,width:60,valueMap:pageSizeValueMap,defaultValue:"50"}],itemChanged:function(c,d){if(c.name=="pageNum"){a.getBindTable().setPageNum(d)
}else{if(c.name=="pageSize"){a.getBindTable().setPageSize(d)
}}}});
var b=[this.pageNumForm];
b.push(isc.LayoutSpacer.create({width:"*"}));
this.createButtons(b);
return b
},setBindTable:function(a){this.bindTable=a;
a.bindListener(this)
},createButtons:function(d){var a=this;
for(var c=0;
c<this.data.length;
c++){var b=isc.ToolStripButton.create(this.data[c]);
isc.addProperties(b,{click:function(){a.itemClick(this)
}});
this[b.name]=b;
d.push(b)
}},getBindTable:function(){return this.bindTable
},table_AfterLoad:function(b){var c=[];
if(b.totalPageCount==0){c.push(0)
}else{for(var a=0;
a<b.totalPageCount;
a++){c.push(a+1)
}}this.pageNumForm.setValueMap("pageNum",c);
this.pageNumForm.setData({pageNum:b.pageNum,pageSize:b.pageSize});
this.updateStatus()
},table_DataStateChanged:function(a){this.updateStatus()
},itemClick:function(a){var b=this.getBindTable();
switch(a.name){case"first":b.firstPage();
break;
case"prior":b.priorPage();
break;
case"next":b.nextPage();
break;
case"last":b.lastPage();
break
}},updateStatus:function(){var b=[];
var a=[];
var c=this.getBindTable();
if(c.totalSize==0){b[b.length]="first";
b[b.length]="prior";
b[b.length]="next";
b[b.length]="last"
}else{if(c.pageNum>1){a[a.length]="first";
a[a.length]="prior"
}else{b[b.length]="first";
b[b.length]="prior"
}if(c.pageNum<c.totalPageCount){a[a.length]="next";
a[a.length]="last"
}else{b[b.length]="next";
b[b.length]="last"
}}this.setButtonState(b,a)
}});
var ImgUtils={getHtml:function(f,e,c,g,d){var b=$("<p>"+isc.Canvas.imgHTML(f,e,c)+"</p>");
if(d){for(var h in d){b.children("img").css(h,d[h])
}}if(g){for(var a in g){b.children("img").attr(a,g[a])
}}return b[0].innerHTML
}};
isc.ClassFactory.defineClass("DataTileGrid","TileGrid");
isc.DataTileGrid.addClassProperties({TableEventsProperties:{table_AfterLoad:function(a){this.cleanData();
if(a.rows!=this.getData()){this.setData(a.rows)
}this.selectRecord(a.getCurrentRow());
if(this.hilites){this.setHilites(this.hilites)
}},table_CurrentRowChanged:function(a,b){if(this.selectionType=="single"&&this.selection){this.selectSingleRecord(b)
}}}});
isc.DataTileGrid.addProperties({autoDraw:false,width:"100%",height:"100%",tileWidth:300,tileHeight:200,columnSize:3,selectionType:"single",selectedBorder:"2px solid #9BC8FF",removeDetailViewer:true});
isc.DataTileGrid.addMethods({initWidget:function(){this.Super("initWidget",arguments);
if(this.removeDetailViewer){this.detailViewer.destroy()
}if(!$.defined(this.service)){this.initialize()
}},initialize:function(){var a=this.getBindTable();
if(a){isc.addProperties(this,isc.DataTileGrid.TableEventsProperties);
this.createFields()
}else{this.createFields()
}this.Super("initialize",arguments)
},getBindTable:function(){},createFields:function(){var a;
var b=this.getBindTable();
if(b){a=b.columns.getFields(this.fieldNames)
}if(a&&a.length>0){this.fields=a
}},setColumnSize:function(a){this.columnSize=a;
this.setTileWidth(this.getWidth()/a-this.tileMargin*2)
},makeTile:function(a,d){var c=this.Super("makeTile",arguments);
c.service=this.service;
var b=this.getTileRecord(c);
if($.defined(b)){a=b
}if($.isFunction(c.setRecord)){c.setRecord(a)
}else{if($.isFunction(c.setData)){c.setData(a)
}else{if($.isFunction(c.setValues)){c.setValues(a)
}}}c.addProperties({click:function(){var e=this.parentElement;
var f=e.getBindTable();
if(f){f.setCurrentRow(a)
}else{e.selectRecord(a)
}this.Super("click",arguments)
}});
if(!$.defined(c.setSelected)){c.setSelected=function(){}
}return c
},selectionChanged:function(a,c){var b=this.getTile(a);
if(c){b.setBorder(this.selectedBorder)
}else{b.setBorder(null)
}this.Super("selectionChanged",arguments)
},cleanData:function(){for(var a=this.data.length-1;
a>=0;
a--){this.removeData(this.data[a]);
if($.isFunction(this.tiles[a].destroy)){this.tiles[a].destroy()
}}},resized:function(){this.setColumnSize(this.columnSize);
return this.Super("resized",arguments)
}});
isc.ClassFactory.defineClass("DataGrid","ListGrid");
isc.DataGrid.addClassProperties({DefaultGridProperties:{useAllDataSourceFields:false,baseStyle:"dataGrid",showRowNumbers:true,rowNumberStyle:"dataGrid",rowNumberFieldProperties:{showAlternateStyle:true},headerAutoFitEvent:"none",canSort:false,canAutoFitFields:false,wrapCells:true,fixedRecordHeights:false,alternateRowStyles:true,autoFetchData:false,cellPadding:1,showRecordComponents:true,showRecordComponentsByCell:true,canHover:true,showHover:true,editEvent:"click",enterKeyEditAction:"nextCell",cellErrorCSSText:"background-color:#FF8A37;",showErrorIcons:false,removeIcon:"[SKIN]/button/remove.gif",editFormProperties:{itemChanged:function(a,b){this.grid.setEditValue(a.rowNum,a.colNum,b);
this.Super("itemChanged",arguments)
}}},Events:{RowClick:"RowClick",RowDoubleClick:"RowDoubleClick",RowButtonClick:"RowButtonClick",RecordClick:"RecordClick"},TableEventsProperties:{table_OnFieldErrors:function(c,b,e){var d=this.getRecordIndex(e);
var a=this.getFieldNum(b.name);
if(d!=-1){this.refreshCell(d,a)
}},table_AfterUpdate:function(b,a,e,c){var d=this.getRecordIndex(e);
if(d!=-1){this.setEditValue(d,a.name,c)
}},table_OnColumnVisibleChanged:function(b,a){if(a.isVisible()){this.showField(a.name)
}else{this.hideField(a.name)
}},table_OnColumnReadOnlyChanged:function(b,a){this.setFieldProperties(a.name,{disabled:a.isReadOnly()})
},table_AfterLoad:function(a){if(a.rows!=this.getData()){this.setData(a.rows)
}this.selectSingleRecord(a.getCurrentRow())
},table_AfterSave:function(a){this.table_AfterLoad(a)
},table_AfterDelete:function(a,b){this.table_AfterLoad(a)
},table_AfterInsert:function(a,b){this.table_AfterLoad(a)
},table_CurrentRowChanged:function(a,b){if(this.selectionType=="single"&&this.selection){this.selectSingleRecord(b)
}},table_BeforeTableValidate:function(a){this.endEditing()
}},DefaultGridMethods:{initialize:function(){var a=this.getBindTable();
if(a){isc.addProperties(this,isc.DataGrid.TableEventsProperties);
this.canEdit=a.isCreateAble()||a.isUpdateAble();
if(this.canEdit){this.alternateRowStyles=false;
if(a.isCreateAble()){this.listEndEditAction="next"
}}if(a.isRemoveAble()){this.canRemoveRecords=true
}if(this.body){this.body.alternateRowStyles=this.alternateRowStyles
}this.createFields();
if(this.toolBar){this.toolBar.setBindTable(a)
}}else{this.createFields()
}this.expansionEditorDefaults._constructor="DataForm";
this.Super("initialize",arguments);
this.changeRemoveImgHTML()
},saveLocally:function(c,f){var e=this.getBindTable();
if(e){var a={};
var d=e.columns.fieldNames;
for(var b=0;
b<d.length;
b++){a[d[b]]=c.values[d[b]]
}c.values=a
}else{delete c.values.errors
}this.Super("saveLocally",arguments)
},loadData:function(){if(this.values!=undefined&&this.values!=null){this.setData(this.values)
}},createFields:function(){var a;
var b=this.getBindTable();
if(b){a=b.columns.getFields(this.fieldNames)
}if(a==undefined||a.length==0){a=this.getAllFields()
}if(a&&a.length>0){this.setFields(a)
}},setFields:function(b){if(b){var a=this.initFields(b);
if(this.formItem&&!$.defined(this.formItem.pickListWidth)){this.formItem.pickListWidth=Math.max(a,this.formItem.width)
}if(this.autoFitWidth){this.setWidth(a)
}}this.Super("setFields",arguments)
},isReadOnly:function(e,a){var d=this.getField(a);
var c=this.getBindTable();
if(c&&d&&d.name){var b=c.getCurrentRow(e);
if(b==undefined||b==null){return c.isReadOnly()
}return b.isReadOnly(d.name)
}var f=false;
if(d){if(d.canEdit&&!d.canEdit){f=true
}if(d.disabled&&d.disabled){f=true
}}return f
},initFields:function(b){var a=0;
for(var c=0;
c<b.length;
c++){var d=b[c];
isc.LogicFieldFactory.createGridField(d);
this.initVisible(d);
this.initAlign(d);
this.initDisplayWidth(d);
if(!d.hidden){a+=d.displayWidth
}if(this.overrideFieldProperties&&this.overrideFieldProperties[d.name]){isc.addProperties(d,this.overrideFieldProperties[d.name])
}this.initFieldPickList(d);
this.initFieldEditor(d);
if(d.frozen){this.wrapCells=false
}}this.initFieldFrozen(b);
this.createOperateToolBar(b);
return this.initFieldWidth(b,a)
},initFieldWidth:function(b,a){if(this.showRowNumbers){a+=40
}if(a>0){var c=this.autoFitWidth;
if(!$.defined(c)){c=$.defined(this.getWidth)&&a<this.getWidth()
}for(var d=0;
d<b.length;
d++){var e=b[d];
if(e.hidden){continue
}if(c){e.width=e.displayWidth/a*100+"%"
}else{e.width=e.displayWidth
}}}return a
},initFieldFrozen:function(a){var c;
for(var b=a.length-1;
b>=0;
b--){if(a[b].frozen){c=true
}if($.defined(c)){a[b].frozen=c
}}},initFieldEditor:function(d){if(!$.defined(d.editorProperties)){d.editorProperties={}
}var a=d.editorProperties;
var c=["dataType","editorType","length","min","max","logic","keyPressFilter","valueMap"];
for(var b=0;
b<c.length;
b++){var e=c[b];
if($.defined(d[e])){a[e]=d[e]
}}isc.ViewForm.initEditorType(a)
},initFieldPickList:function(d){if(d&&d.pickListFields){if(!$.defined(d.editorProperties)){d.editorProperties={}
}var a=d.editorProperties;
for(var e in d){if(e.startsWith("pick")){a[e]=d[e]
}}var c=["displayField","valueField","pickList","pickListProperties","filterFields","addUnknownValues"];
for(var b=0;
b<c.length;
b++){var e=c[b];
a[e]=d[e]
}if(!$.defined(d.editorType)){a.editorType="ComboBoxItem"
}if(!$.defined(d.addUnknownValues)){a.addUnknownValues=false
}a.autoFitWidth=true;
a.getPickData=function(){return this.pickData||this.form.grid.getFormItemPickData(this.form,this)
};
a.pickListWidth=this.initFields(a.pickListFields)
}},createOperateToolBar:function(a){if(this.operateToolBar&&$.defined(this.operateField)){this.operateField=isc.addProperties({name:this.operateToolBar.fieldName,title:this.operateToolBar.title,displayWidth:this.operateToolBar.getDisplayWidth(),align:"center",cellAlign:"center"});
this.operateToolBar.dataGrid=this;
this.initDisplayWidth(this.operateField);
a.push(this.operateField)
}},initVisible:function(a){if($.defined(a.visible)){a.hidden=!a.visible
}if(a.name=="rowNum"){a.length=3;
if(!$.defined(a.title)){a.title=" "
}a.cellAlign="left";
this.showRowNumbers=false
}},initAlign:function(a){a.align="center";
if(!$.defined(a.cellAlign)){if(a.type=="boolean"||a.dataType=="boolean"||a.type=="image"){a.cellAlign="center"
}else{if(a.type=="integer"||a.type=="float"||a.dataType=="integer"||a.dataType=="float"){a.cellAlign="right"
}else{a.cellAlign="left"
}}if(a.displayField){a.cellAlign="left"
}}},initDisplayWidth:function(c){if($.defined(c.displayWidth)){return
}if($.defined(c.width)){if(c.title&&c.width<c.title.getBytesLength()*isc.WidthPerChar){c.displayWidth=c.title.getBytesLength()*isc.WidthPerChar
}else{c.displayWidth=c.width
}}else{var a=c.length;
if(!$.defined(a)){if(c.type=="integer"||c.dataType=="integer"||c.type=="float"||c.dataType=="float"){a=15
}else{if(c.type=="boolean"||c.dataType=="boolean"){a=5
}else{a=15
}}}else{if(a>40){a=40
}else{if(c.title&&a<c.title.length){a=c.title.length
}}}if(c.title){var b=c.title.getBytesLength();
if(a<b){a=b
}}c.displayWidth=Math.max(2,a)*isc.WidthPerChar
}},changeRemoveImgHTML:function(){if(this.completeFields){var a=this.completeFields.findIndex("isRemoveField",true);
if(a>=0){var b=this.completeFields[a];
if(b){b.removeIconHTML=this.getButtonImgHTML(this.removeIcon,this.removeIconSize,isc.i18nButton.remove)
}}}},getButtonImgHTML:function(b,a,c){return ImgUtils.getHtml(b,a,a,{title:c},{cursor:"pointer"})
},cellHasErrors:function(d,a){var c=this.getCurrentRow(d);
if(c&&$.isFunction(c.hasErrors)){var b=this.getField(a);
if(b){return c.hasErrors(b.name)
}}return false
},cellHoverHTML:function(a,e,b){var d=this.getCurrentRow(e);
if(d&&d.hasErrors){var c=this.getField(b);
if(c&&d.hasErrors(c.name)){return"<b>"+c.title+"</b>: "+d.getFieldErrors(c.name)
}}},getCellCSSText:function(a,d,b){if(a&&a.hasErrors){var c=this.getField(b);
if(c&&a.hasErrors(c.name)){return this.cellErrorCSSText
}}return this.Super("getCellCSSText",arguments)
},getCurrentRow:function(b){var a=this.getRecord(b);
if(a==undefined||a==null){a=this.getBindTable().getCurrentRow(b)
}return a
},getCellErrors:function(d,a){var c=this.getCurrentRow(d);
if(c){var b=this.getField(a);
if(b&&c.getFieldErrors){return c.getFieldErrors(b.name)
}}return null
},rowHasErrors:function(b){var a=this.getCurrentRow(b);
if(a){return a.hasErrors()
}return false
},getRowErrors:function(b){var a=this.getCurrentRow(b);
if(a&&a.getFieldErrors){return a.getFieldErrors()
}return null
},getBindTable:function(){},getEmptyMessage:function(){var a=this.getBindTable();
if(a&&a.dataState==isc.Table.DataStates.Loading){return this.loadingDataMessage==null?"&nbsp;":this.loadingDataMessage.evalDynamicString(this,{loadingImage:this.imgHTML(isc.Canvas.loadingImageSrc,isc.Canvas.loadingImageSize,isc.Canvas.loadingImageSize)})
}this.Super("getEmptyMessage",arguments)
},startEditing:function(c,a,b){if(this.isReadOnly(c,a)){return false
}return this.Super("startEditing",arguments)
},editorEnter:function(a,d,f,b){this.Super("editorEnter",arguments);
var e=this.getBindTable();
if(e){var g=this.getFieldName(b);
var c=this.getCurrentRow(f);
e.setCurrent(g,c)
}},editorExit:function(c,a,d,e,b){return this.Super("editorExit",arguments)
},setEditValue:function(e,b,c){var d=this.getBindTable();
if(d&&this.getField(b)){var f=this.getField(b).name;
var a=this.getRecord(e);
d.update(f,c,a)
}this.Super("setEditValue",arguments)
},removeRecordClick:function(d){var c=this.getBindTable();
if(c){var b=this.getCurrentRow(d);
if(b){var a=this;
this.endEditing();
if(this.service){this.service.remove(c,b,function(){a.Super("removeRecordClick",arguments)
})
}else{c.remove(b,function(){a.Super("removeRecordClick",arguments)
})
}return
}}this.Super("removeRecordClick",arguments)
},initializeEditValues:function(d,b,e){var c;
var a=this.getCurrentRow(d);
if(a==null){a=this.getBindTable().createNewRow()
}this.setEditValues([d,b],a,e)
},getEditorValueMap:function(b,a){if(a.getValueMap){var c=a.getValueMap(b.name);
if(c){return c
}}else{if(b.valueMap){return b.valueMap
}}this.Super("getEditorValueMap",arguments)
},formatDisplayValue:function(d,a,f,b){if(a){var e=this.getField(b);
if(a.components){var c=a.components[e.name];
if(c&&c.formatDisplayValue){return c.formatDisplayValue(d,a,f,b)
}}if(e.formatDisplayValue){return e.formatDisplayValue(d)
}if(a.format){return a.format(e.name,d)
}}return d
},click:function(){var a=this.getBindTable();
if(a){if(this.getData().length==0&&a.isCreateAble()){this.initializeEditValues(0,0,null)
}}this.Super("click",arguments)
},recordDoubleClick:function(c,b,h,g,a,e,d){if(this.fireEvent&&this.fireEvent(isc.DataGrid.Events.RowDoubleClick,b)==false){return false
}var f=this.getBindTable();
if(f){f.setCurrentRow(b)
}return this.Super("recordDoubleClick",arguments)
},recordClick:function(h,b,g,f,a,d,c){if(this.fireEvent&&this.fireEvent(isc.DataGrid.Events.RowClick,b)==false){return false
}var e=this.getBindTable();
if(e){e.setCurrentRow(b)
}return this.Super("recordClick",arguments)
},cloneRecord:function(a){if(a){var c={};
for(var b in a){var d=a[b];
if(isc.isA.Number(d)||isc.isA.String(d)||isc.isA.Date(d)||isc.isA.Boolean(d)){c[b]=d
}}return c
}},clearData:function(){var a=this.getBindTable();
if(a){if(!a.isRemoveAble()){return false
}a.setData([])
}if(this.values!=undefined||this.values!=null){delete this.values
}this.setData([])
},_removeRecords:function(b){if(b){var d=this.getBindTable();
if(d&&!d.isRemoveAble()){return false
}if(!isc.isA.Array(b)){b=[b]
}for(var c=0;
c<b.length;
c++){var a=b[c];
if(d){d.remove(a)
}else{if(this.values){this.values.remove(a)
}}}return false
}},addRecord:function(a){if(a){var c=this.getBindTable();
if(c&&!c.isCreateAble()){return false
}var b=this.cloneRecord(a);
if(c){c.add(b)
}else{if(this.values==undefined||this.values==null){this.values=[]
}this.values.push(b)
}}},addRecords:function(a){if(a){if(!isc.isA.Array(a)){a=[a]
}for(var b=0;
b<a.length;
b++){this.addRecord(a[b])
}}},getGridMembers:function(){var a=this.Super("getGridMembers",arguments);
if(this.toolBar){a.push(isc.HLayout.create({width:"100%",height:this.toolBar.height+1,layoutTopMargin:1,align:"center",members:[this.toolBar]}))
}return a
},createRecordComponent:function(a,b){var d=this.getField(b);
var c=isc.GridRecordCompomentFactory.createComponent(this,d,a,b);
if(c){if(a.components==undefined||a.components==null){a.components={}
}a.components[d.name]=c;
return c
}},showRecordComponent:function(a,b){if(this.getEditForm()&&this.getEditRecord()==a){if(!this.isReadOnly(b)){return false
}}return true
},canEditField:function(b){var a=this.getBindTable();
if(a){return !a.isReadOnly()
}if(b&&b.canEdit){return b.canEdit
}return this.canEdit?this.canEdit:false
},getFormItemPickData:function(a,b){var c=this.getBindTable();
if(c){var d=c.getCurrentRow();
if(d!=null){return d.getPickData(b.name)
}}}}});
isc.DataGrid.addProperties({autoDraw:false,width:"100%",height:"100%"});
isc.DataGrid.addMethods({initWidget:function(){isc.addProperties(this,isc.DataGrid.DefaultGridProperties);
isc.addProperties(this,isc.DataGrid.DefaultGridMethods);
this.Super("initWidget",arguments);
if(!$.defined(this.service)){this.initialize()
}},removeRecords:function(a){return this._removeRecords(a)
},appendRecords:function(c,a,d){if(a==undefined||a==null){a=[c.getSelectedRecord()]
}else{if(!isc.isA.Array(a)){a=[a]
}}for(var b=0;
b<a.length;
b++){this.appendRecord(c,a[b],d)
}},appendRecord:function(b,a,c){if(c==undefined||c){b.removeRecords(a)
}this.addRecord(a);
this.loadData()
}});
isc.ClassFactory.defineClass("GridRecordCompomentFactory");
isc.GridRecordCompomentFactory.addClassProperties({createComponent:function(b,c,a){if(b.operateToolBar&&c.name==b.operateToolBar.fieldName){return this.createOperateToolBar(b,c,a)
}},createOperateToolBar:function(d,f,a){var b=isc.HLayout.create({autoDraw:false,width:"100%",height:"100%",align:"center"});
var e=d.operateToolBar.getRecordComponents(a);
if(e){for(var c=0;
c<e.length;
c++){b.addMember(e[c])
}}return b
}});
isc.ClassFactory.defineClass("OperateToolBar","Class");
isc.OperateToolBar.addProperties({fieldName:"operateField",title:isc.i18nButton.operate,showRecordComponentsByCell:true});
isc.OperateToolBar.addMethods({initWidget:function(){this.Super("initWidget",arguments)
},getDisplayWidth:function(){if(this.buttons.length<2){return 5*9
}return this.buttons.length*2*9
},getRecordComponents:function(a){var d=[];
if(this.buttons){for(var c=0;
c<this.buttons.length;
c++){var b=this.createImgButton(this.buttons[c],a);
d.push(b)
}}return d
},createImgButton:function(c,a){var b=isc.ImgButton.create({showDown:false,showRollOver:false,layoutAlign:"center",prompt:c.title,height:16,width:16,dataGrid:this.dataGrid,click:function(){if(this.dataGrid.fireEvent){var d=this.dataGrid.getSelectedRecord();
this.dataGrid.fireEvent(isc.DataGrid.Events.RowButtonClick,this,d)
}}},c);
this[c.name]=b;
return b
}});
isc.ClassFactory.defineClass("TreeDataGrid","TreeGrid");
isc.TreeDataGrid.addClassProperties({DefaultProperties:{alternateRowStyles:false,showRowNumbers:false}});
isc.TreeDataGrid.addProperties({autoDraw:false,width:"100%",height:"100%",showConnectors:false,openerImage:"[SKIN]/../tree/opener.gif",openerIconSize:16,showFullConnectors:true,showOpenIcons:false,showDropIcons:false,closedIconSuffix:"",modelType:"parent",nameProperty:"name",idField:"id",parentIdField:"parentId",openAllWhenDataArrived:true,loadDataOnDemand:false});
isc.TreeDataGrid.addMethods({initWidget:function(){this.initDefaultProperties();
this.Super("initWidget",arguments);
if(!$.defined(this.service)){this.initialize()
}if(this.values){this.setData(this.values)
}},initDefaultProperties:function(){isc.addProperties(this,isc.DataGrid.DefaultGridProperties);
isc.addProperties(this,isc.DataGrid.DefaultGridMethods);
isc.addProperties(this,isc.TreeDataGrid.DefaultProperties)
},setData:function(a){if(this.treeData&&this.treeData!=a&&this.treeData.destroy){this.treeData.destroy()
}if(isc.isA.Array(a)&&a.length>0){this.treeData=isc.Tree.create({modelType:this.modelType,nameProperty:this.nameProperty,idField:this.idField,parentIdField:this.parentIdField,data:a});
this.treeData.sortByProperty(this.idField,"ascending");
if(this.openAllWhenDataArrived){this.treeData.openAll()
}}else{this.treeData=isc.Tree.create({})
}this.Super("setData",this.treeData)
},dataArrived:function(a){this.Super("dataArrived",arguments);
if(this.openAllWhenDataArrived){this.getData().openAll()
}},getIcon:function(b){if(this.typeValueMap&&b.type!=undefined){var a=this.typeValueMap[b.type+""];
if(a){return a
}}return this.Super("getIcon",arguments)
},removeRecords:function(a){if(a){if(!isc.isA.Array(a)){a=[a]
}var c=this.getData();
for(var b=0;
b<a.length;
b++){this.removeRecord(a[b])
}}return a
},removeRecord:function(b){if(b){var f=this.getData();
var c=f.findById(b[f.idField]);
if(c){while(1){var e=f.getParent(c);
if(f.isRoot(e)||e.children.length>1||this.removeEmptyParent!=true){break
}c=e
}var a=[c];
var d=f.getAllNodes(c);
if(d){a=a.concat(d)
}f.remove(c);
this._removeRecords(a)
}}},appendRecords:function(c,a,d){if(a==undefined||a==null){a=[c.getSelectedRecord()]
}else{if(a==c.getData()){a=[].concat(c.getData().data)
}else{if(!isc.isA.Array(a)){a=[a]
}}}if(a){for(var b=0;
b<a.length;
b++){this.appendRecord(c,a[b],d)
}}},searchRecords:function(b,h,m,l){var a=b.getData();
var j=a.findById(h[a.idField]);
if(j){m.push(j);
var k=a.getParents(j);
var d=this.getData();
if(k){for(var g=0;
g<k.length;
g++){var e=k[g];
if(a.isRoot(e)){continue
}if(d.findById(e[a.idField])==null){l.push(e)
}}}var c=a.getAllNodes(j);
if(c){for(var g=0;
g<c.length;
g++){var f=c[g];
if(d.findById(f[d.idField])==null){l.push(f)
}}}if(d.findById(h[d.idField])==null&&l.indexOf(h)==-1){l.push(h)
}}},appendRecord:function(e,b,f){if(b){var d=[];
var a=[];
this.searchRecords(e,b,a,d);
if(f==undefined||f==null){e.removeRecords(a)
}for(var c=0;
c<d.length;
c++){this.addRecord(d[c])
}this.loadData()
}}});
isc.ClassFactory.defineClass("TreeList","TreeDataGrid");
isc.TreeList.addClassProperties({DefaultProperties:{baseStyle:"treeCell",showSortArrow:false,showHeaderMenuButton:false,showHeaderContextMenu:false}});
isc.TreeList.addProperties({autoDraw:false,width:"100%",height:"100%",removeEmptyParent:true});
isc.TreeList.addMethods({initWidget:function(){if(this.fields==undefined||this.fields==null){this.fields=[{name:this.nameProperty,title:this.title}]
}this.Super("initWidget",arguments)
},initDefaultProperties:function(){this.Super("initDefaultProperties",arguments);
isc.addProperties(this,isc.TreeList.DefaultProperties)
}});
isc.ClassFactory.defineClass("VPanel","VLayout");
isc.VPanel.addProperties({autoDraw:false,width:"100%",height:"100%",membersMargin:1});
isc.ClassFactory.defineClass("VBorderPanel","VPanel");
isc.VBorderPanel.addProperties({border:"1px solid #ffffff"});
isc.ClassFactory.defineClass("HPanel","HLayout");
isc.HPanel.addProperties({autoDraw:false,width:"100%",height:"100%",membersMargin:1});
isc.ClassFactory.defineClass("HBorderPanel","HPanel");
isc.HBorderPanel.addProperties({border:"1px solid #A7ABB4"});
isc.ClassFactory.defineClass("GridPanel","VLayout");
isc.GridPanel.addProperties({autoDraw:false,width:"100%",height:"100%",membersMargin:0,columnSize:3,rowSize:3});
isc.GridPanel.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.createCellMembers()
},createCellMembers:function(){var a=[];
for(var b=0;
b<this.rowSize;
b++){this.createColumnMembers(a,b)
}this.addMember(a);
this.components=a
},createColumnMembers:function(b,g){var c={autoDraw:false,width:"100%",membersMargin:0,height:(100/this.rowSize)+"%",membersMargin:this.membersMargin};
if(this.cellHeight!=undefined&&this.cellHeight!=null){c.height=this.cellHeight
}var f=[];
for(var d=0;
d<this.columnSize;
d++){var e={autoDraw:false,width:Math.floor(100/this.columnSize-10)+"%",height:"100%",border:this.cellBorder};
if(this.cellWidth){if(isc.isA.Array(this.cellWidth)){c.width=this.cellWidth[d]
}else{c.width=this.cellWidth
}}if(this.cellMembers&&this.cellMembers[g][d]){e.members=[this.cellMembers[g][d]]
}f.push(isc.Layout.create(e))
}c.members=f;
var a=isc.HLayout.create(c);
b.push(a);
return a
},addCellMember:function(d,b,e){var c;
if(d<this.components.length){c=this.components[d].members[b]
}else{var a=this.createColumnMembers(this.components,this.components.length);
if(a){this.addMember(a);
c=a.members[b]
}}if(c){c.addMember(e)
}}});
isc.defineClass("MenuExt","Menu");
isc.MenuExt.addClassProperties({MenuItemDefaultProperties:{setTitle:function(a){this.menu.setItemTitle(this,a)
}},SeparatorProperties:{autoFit:false,isSeparator:true,cssText:"height: 2px;"}});
isc.MenuExt.addProperties({autoDraw:false,autoFit:true,showShadow:true,shadowDepth:10,cellPadding:5});
isc.MenuExt.addMethods({initWidget:function(){this.initMenuData(this.menuData.data);
isc.addProperties(this,this.menuData);
this.Super("initWidget",arguments);
this.initMenuItems(this)
},initMenuData:function(b){if(isc.isA.Array(b)){for(var a=0;
a<b.length;
a++){if(b[a]=="separator"||b[a].isSeparator){b[a]=isc.MenuExt.SeparatorProperties
}else{if(b[a].submenu){this.initMenuData(b[a].submenu)
}}}}},initMenuItems:function(e){var c=e.getItems();
if(c){for(var b=0;
b<c.length;
b++){var d=c[b];
d.owner=this;
if(this.toolBar){d.toolBar=this.toolBar
}var a=e.getSubmenu(d);
if(a){this.initMenuItems(a)
}else{isc.addProperties(d,{menu:e},isc.MenuExt.MenuItemDefaultProperties)
}}}},setEnabled:function(a,e){var c=this.getItems();
for(var b=0;
b<c.length;
b++){var d=c[b];
if(d.name==a){this.setItemEnabled(d,e);
return
}}},getBaseStyle:function(a,c,b){if(a&&a[this.isSeparatorProperty]){return"menuSeparator"
}return this.Super("getBaseStyle",arguments)
},getCellValue:function(b,d,a,c){if(b&&b[this.isSeparatorProperty]){return"&nbsp;"
}return this.Super("getCellValue",arguments)
},itemClick:function(d,b){if(d.toolBar){var a=d.owner.findItems(d.name);
if(a.length>0){var c=a[0];
if(c.actionType=="checkbox"){c.selected=!c.selected;
d.menu.markForRedraw()
}else{if(c.checked!=undefined&&c.checked!=null){c.checked=!c.checked;
d.menu.markForRedraw()
}}d.toolBar.itemClick(d,b)
}}else{this.Super("itemClick",arguments)
}},findItems:function(c,d){if(d==undefined){d=this
}if(!isc.isA.Array(c)){c=[c]
}var k=[];
var b=d.getItems();
if(b){for(var h=0;
h<b.length;
h++){var a=b[h];
var g=d.getSubmenu(a);
if(g){var e=this.findItems(c,g);
for(var f=0;
f<e.length;
f++){k[k.length]=e[f]
}}else{for(var f=0;
f<c.length;
f++){if(a.name==c[f]){k[k.length]=a;
break
}}}}}return k
}});
isc.ClassFactory.defineClass("MenuBarEx","MenuBar");
isc.ClassFactory.defineClass("FlatButton","Button");
isc.FlatButton.addProperties({autoDraw:false,baseStyle:"flatbutton",autoFit:true});
isc.ClassFactory.defineClass("ToolButton","AutoFitButton");
isc.ToolButton.addClassProperties({ButtonTypes:{ModifyPassword:{name:"modifyPassword",permission:"changePassword",title:isc.i18nButton.modifyPassword},ResetPassword:{name:"resetPassword",permission:"resetPassword",title:isc.i18nButton.resetPassword},Config:{name:"config",permission:"config",title:isc.i18nButton.config},Login:{name:"login"},Reset:{name:"reset"},Ok:{name:"ok"},Filter:{name:"filter"},Save:{name:"save"},Cancel:{name:"cancel",click:function(){this.service.close()
}},Stop:{name:"stop"},Upload:{name:"upload"},Exit:{name:"exit",click:function(){this.service.close()
}}}});
isc.ToolButton.addProperties({autoDraw:false,showIcon:true,baseStyle:"toolButton"});
isc.ToolButton.addMethods({initWidget:function(){if(this.title=="Untitled Button"){var a=isc.i18nButton[this.name];
if(a!=undefined&&a!=null){this.title=a
}}if(this.showIcon&&!$.defined(this.icon)){this.icon="[SKIN]/button/"+this.name+".png"
}this.Super("initWidget",arguments)
}});
isc.ClassFactory.defineClass("MenuButtonEx","MenuButton");
isc.MenuButtonEx.addProperties({autoDraw:false,baseStyle:"toolButton",showMenuButtonImage:false,iconOrientation:"left",autoFit:true,iconSize:16,iconHeight:16,iconWidth:16});
isc.MenuButtonEx.addMethods({initWidget:function(){if(this.data){this.menu=isc.MenuExt.create({menuData:{data:this.data},toolBar:this.toolBar})
}this.Super("initWidget",arguments)
}});
isc.ClassFactory.defineClass("ToolBarEx","Toolbar");
isc.ToolBarEx.addClassProperties({Events:{ItemClick:"ItemClick",CheckState:"CheckState",ItemChanged:"ItemChanged"}});
isc.ToolBarEx.addProperties({autoDraw:false,width:"100%",height:32,styleName:"toolBar",membersMargin:5,layoutRightMargin:4,layoutLeftMargin:4,layoutTopMargin:4,layoutBottomMargin:4,align:"right"});
isc.ToolBarEx.addMethods({initialize:function(){this.Super("initialize",arguments);
this.data=this.checkPermissions(this.data);
if(this.data!=null){for(var c=0;
c<this.data.length;
c++){var b=isc.addProperties({autoDraw:false,service:this.service,toolBar:this},this.data[c]);
var d=b.type?b.type:isc.ToolButton;
var a=isc.ClassFactory.newInstance(d,this.buttonDefaults,this.buttonProperties,b);
if(a!=null){this.addButtons(a);
this[a.name]=a
}}}},checkPermissions:function(d){if(d){if(isc.isA.Array(d)){var e=[];
for(var a=0;
a<d.length;
a++){var c=this.checkPermissions(d[a]);
if(c){if(e.length==0||e[e.length-1]!=c){e.push(c)
}}}if(e.length>0){return e
}}else{if(this.hasPermission(d)){if(d.type==isc.MenuButtonEx){var b=this[d.name+"MenuData"];
if(b){d.data=b
}d.data=this.checkPermissions(d.data);
if(d.data&&d.data.length>0){return d
}}else{if(d.submenu){d.submenu=this.checkPermissions(d.submenu);
if(d.submenu&&d.submenu.length>0){return d
}}else{return d
}}}}}},itemChanged:function(a,b){this.fireEvent(isc.ToolBarEx.Events.ItemChanged,a);
this.Super("itemChanged",arguments)
},itemClick:function(a,b){this.fireEvent(isc.ToolBarEx.Events.ItemClick,a);
this.Super("itemClick",arguments)
}});
isc.ClassFactory.defineClass("ModuleLoader","ViewLoader");
isc.ModuleLoader.addProperties({module:null,autoDraw:false});
isc.ModuleLoader.addMethods({initWidget:function(){this.title=this.module.title;
this.viewURL=this.module.getUrl();
this.loadingMessage='${loadingImage}&nbsp;&nbsp;<span class="throbberText">'+isc.i18nInfo.loading;
if(this.title!=undefined){this.loadingMessage+=this.title
}this.loadingMessage+="...</span>";
this.Super("initWidget",arguments)
},handleError:function(a,b){this.Super("handleError",arguments)
},viewLoaded:function(a){this.Super("viewLoaded",arguments);
a.module=this.module;
if(this.loadedCallBack!=undefined){this.loadedCallBack(a)
}if(a.initialize!=undefined){a.initialize()
}}});
isc.ClassFactory.defineClass("TabLayout","TabSet");
isc.TabLayout.addClassProperties({DefaultTabProperties:{iconSize:16,canClose:false}});
isc.TabLayout.addProperties({autoDraw:false,width:"100%",height:"100%",paneMargin:1});
isc.TabLayout.addMethods({addMembers:function(a){if(a){for(var b=0;
b<a.length;
b++){var d=a[b];
var c=isc.addProperties({autoDraw:false,title:d.title,name:d.name,icon:d.icon},isc.TabLayout.DefaultTabProperties);
c.pane=d;
this.addTabs(c)
}}},findTab:function(b){if(b!=undefined&&b!=null){if(isc.isA.Number(b)){return this.getTabObject(b)
}if(isc.isA.String(b)){for(var a=0;
a<this.tabs.length;
a++){if(this.tabs[a].name==b){return this.tabs[a]
}}}}return b
},findVisibleTab:function(a){for(var c=a;
c<this.tabs.length;
c++){var b=this.tabBar.getButton(c);
if(b&&b.isVisible()){return this.tabs[c]
}}if(a!=0){return this.findVisibleTab(0)
}},selectTab:function(c){var a=this.getTabNumber(c);
var b=this.tabBar.getButton(a);
if(b&&!b.isVisible()){c=this.findVisibleTab(a)
}this.Super("selectTab",c)
},hideTab:function(d){var c=this.findTab(d);
if(c){var a=this.getTabNumber(c);
var b=this.tabBar.getButton(a);
if(b){b.hide();
this.getTabPane(c).hide()
}if(this.getSelectedTab()==c){this.selectTab(c)
}}},showTab:function(d){var c=this.findTab(d);
if(c){var a=this.getTabNumber(c);
var b=this.tabBar.getButton(a);
if(b){b.show()
}}}});
isc.ClassFactory.defineClass("ModuleTabSet","TabLayout");
isc.ModuleTabSet.addMethods({initWidget:function(){this.Super("initWidget",arguments);
if(this.homeModule){this.homeModule=isc.ModuleManager.createModule(this.homeModule);
this.createModuleTab(this.homeModule)
}},findModuleTab:function(b){for(var a=0;
a<this.tabs.length;
a++){var c=this.tabs[a];
if(c.title==b.title){return c
}}return null
},createModuleTab:function(b){var a=isc.addProperties({module:b},b.params);
var d=isc.ClassFactory.getClass(b.url);
if(d){d.open(b,a);
return
}var c=isc.addProperties({autoDraw:false},isc.TabLayout.DefaultTabProperties);
isc.addProperties(c,{title:b.title,icon:b.icon,module:b});
c.pane=isc.ModuleLoader.create(a);
this.addTabs(c);
return c
},loadModule:function(d){if($.defined(d.submenu)){return
}var a=this;
var b=this.findModuleTab(d);
if(b==null){var c={title:d.title,name:d.name,icon:d.icon,jndiName:d.jndiName,baseUrl:isc.ApplicationContext.baseUrl,url:d.url,close:function(e){a.closeModule(this,e)
}};
if(d.params){c.params=d.params
}c=isc.ModuleManager.createModule(c);
b=this.createModuleTab(c)
}if(b){this.selectTab(b)
}},closeModule:function(c,d){var a=this;
var b=this.findModuleTab(c);
if(b){isc.ask(isc.i18nAsk.exit,function(e){if(e){this.moduleTabSet.closeClick(b);
if(d){d()
}}},{moduleTabSet:this,moduleTab:b})
}}});
isc.ClassFactory.defineClass("ModuleManager");
isc.ModuleManager.addClassProperties({ModuleDefaultProperties:{getUrl:function(){var a="";
if(this.jndiName){if(this.baseUrl){a=this.baseUrl+"/"
}if(!a.endsWith("/")){a+="/"
}a+=this.jndiName+"/"
}if(this.url){if(this.url.indexOf("/")!=-1){a=this.url
}else{a+=this.url
}}else{if(this.name){a+=this.name+"Main.jsf"
}}if(a.length>0){return a
}}},DefaultLayoutProperties:{initialize:function(){this.service.module=this.module;
this.service.initialize();
this.initComponent()
},getComponents:function(){var b=[];
var a=this.getLayoutProperties();
if(a){b=this.createComponents(a)
}return b
},getLayoutProperties:function(){return this.DefaultLayoutProperties
},initComponent:function(){this.service.initComponent()
},createComponents:function(d){if(isc.isA.Array(d)){var e;
for(var c=0;
c<d.length;
c++){var b=this.createComponents(d[c]);
if(b){if(e==undefined||e==null){e=[b]
}else{e[e.length]=b
}}}if(e){return e
}}else{if(d.classType!=undefined){var b=this.createComponent(d.classType,d.properties,d.name);
if(b){if(d.members){var a=this.createComponents(d.members);
if(a){b.addMembers(a)
}}return b
}}}},getDefaultServiceProperties:function(b,a){if(b&&b.getSuperClass()!=isc.Class){a=this.getDefaultServiceProperties(b.getSuperClass(),a);
if(b.DefaultServiceProperties){if(a==undefined||a==null){a={}
}a=isc.addProperties(a,b.DefaultServiceProperties)
}}return a
},createService:function(b){var a=this.getDefaultServiceProperties(this.getClass());
var c=isc.addProperties({owner:this},a);
if(this.DefaultServiceProperties){isc.addProperties(c,this.DefaultServiceProperties)
}if(this.serviceProperties){isc.addProperties(c,this.serviceProperties)
}return isc.Service.create(c)
},createComponent:function(d,c,b){var a=this.service.createComponent(d,c,b);
if(b&&this[b]==undefined){this[b]=a
}return a
},destroy:function(){this.service.destroy();
this.Super("destroy",arguments)
}}});
isc.ModuleManager.addClassMethods({createModule:function(a){isc.addProperties(a,isc.ModuleManager.ModuleDefaultProperties);
if(a.getUrl()==undefined){a=isc.addProperties({name:"User",title:a.title,baseUrl:"./Modules/",jndiName:"Demo",url:"UserMain.js",close:a.close},isc.ModuleManager.ModuleDefaultProperties)
}return a
}});
isc.ClassFactory.defineClass("DialogWindow","Window");
isc.DialogWindow.addClassProperties({DefaultServiceProperties:{close:function(){if(this.onClosing){var a=this;
this.onClosing(function(){a._closeService()
})
}else{this._closeService()
}},_closeService:function(){if(this.owner){this.owner.hide();
if(this.result&&this.owner.success){this.owner.success()
}this.destroy()
}}}});
isc.DialogWindow.addClassMethods({open:function(b,d){var a={title:b.title,icon:b.icon};
if(d){isc.addProperties(a,d)
}var c=this.create(a);
c.show();
return c
}});
isc.DialogWindow.addProperties({autoDraw:false,autoSize:false,autoCenter:true,isModal:true,showModalMask:false,bodyProperties:{layoutMargin:1,membersMargin:1},icon:null,buttons:[],items:[]});
isc.DialogWindow.addMethods(isc.ModuleManager.DefaultLayoutProperties);
isc.DialogWindow.addMethods({initWidget:function(){this.service=this.createService();
if(this.icon){this.headerIconDefaults.src=this.icon
}this.Super("initWidget",arguments);
this.items=this.createItems();
this.initialize()
},show:function(){this.Super("show");
if(this.onShown){this.onShown()
}},close:function(){if(this.service){this.service.close()
}this.Super("close")
},createItems:function(){var a=this.getComponents();
if(this.buttons.length>0){this.toolBar=this.createComponent(isc.ToolBarEx,{data:this.buttons,service:this.service,window:this},"toolBar");
a[a.length]=this.toolBar
}return a
}});
isc.ClassFactory.defineClass("ModuleWindow","Window");
isc.ModuleWindow.addClassMethods({open:function(e,b,g,a,f){var d;
if(e.module){d=isc.addProperties({},e.module)
}else{if(e.owner&&e.owner.module){d=isc.addProperties({},e.owner.module)
}}if(d){d.url=b;
var c={title:d.title+"-"+g,module:d,moduleProperties:a};
if(f){isc.addProperties(c,f)
}isc.ModuleWindow.create(c).show();
return true
}}});
isc.ModuleWindow.addProperties({autoDraw:false,autoSize:false,autoCenter:true,isModal:true,showModalMask:false,width:800,height:600,bodyProperties:{layoutMargin:1,membersMargin:1},canDragResize:true,showResizer:true});
isc.ModuleWindow.addMethods({initWidget:function(){this.addItems(this.getComponents());
this.Super("initWidget",arguments);
if(this.icon){this.headerIconDefaults.src=this.icon
}},getComponents:function(){if(!this.panel){var a={window:this};
if(this.moduleProperties){isc.addProperties(a,this.moduleProperties)
}this.panel=isc.ModuleLoader.create({module:this.module,loadedCallBack:function(b){isc.addProperties(b,a)
}})
}return this.panel
}});
isc.ClassFactory.defineClass("ModuleForm","VPanel");
isc.ModuleForm.addClassProperties({DefaultServiceProperties:{remove:function(b,d,c,a){if(b==this.dataSet.getNavigate()){isc.ask(isc.i18nAsk.remove,function(e){if(e){b.remove(d,c,a)
}})
}else{b.remove(d,c,a)
}},close:function(){if(this.onClosing){var a=this;
this.onClosing(function(){a._closeService()
})
}else{this._closeService()
}},_closeService:function(){var a=this.owner,b=this;
if(a.window){a.window.closeClick();
if(b.result&&a.success){a.success()
}b.destroy()
}else{if(a.module){a.module.close(function(){if(b.result&&a.success){a.success()
}b.destroy()
})
}}}}});
isc.ModuleForm.addProperties({autoDraw:false,width:"100%",height:"100%",service:null});
isc.ModuleForm.addMethods(isc.ModuleManager.DefaultLayoutProperties);
isc.ModuleForm.addMethods({initWidget:function(){this.service=this.createService();
this.addMembers(this.getComponents());
this.Super("initWidget",arguments)
}});
isc.ClassFactory.defineClass("SelectPanel","HPanel");
isc.SelectPanel.addClassProperties({DefaultComponentProperties:{autoDraw:false,width:"50%",height:"100%"},Events:{OnSelect:"OnSelect",OnSelectAll:"OnSelectAll"}});
isc.SelectPanel.addProperties({autoDraw:false,width:"100%",height:"100%"});
isc.SelectPanel.addMethods({initWidget:function(){this.addProperties(isc.Table.DefaultLocalDataProperties);
this.addMembers(this.getComponents());
this.Super("initWidget",arguments)
},table_BeforeTableValidate:function(a){a.setData(this.getSelectedData())
},table_BeforeLoad:function(a){this.refreshDeselectData()
},table_BeforeInsert:function(a){this.refreshDeselectData()
},table_AfterLoad:function(a){this.setSelectedData(a.rows)
},table_AfterInsert:function(a){this.setSelectedData(a.rows)
},table_AfterSave:function(a){this.loadDeselectData();
this.setSelectedData(a.rows)
},refreshDeselectData:function(){delete this.selectedData;
delete this.deselectData;
this.loadDeselectData()
},getDataLoader:function(){if(this.dataLoader==undefined||this.dataLoader==null){this.dataLoader=isc.DataLoader.create({jndiName:this.jndiName,dataURL:this.dataURL})
}return this.dataLoader
},getBindTable:function(){},loadDeselectData:function(){var a=this.getDataLoader();
if(a){var b=this;
a.load(function(c){b.setDeselectData(c)
})
}},isReadOnly:function(){var a=this.getBindTable();
if(a){return a.isReadOnly()
}return false
},changeDataState:function(){var a=this.getBindTable();
if(a){if(a.dataState==isc.Table.DataStates.Browse){a.setDataState(isc.Table.DataStates.Modified)
}}},setSelectedData:function(a){this.selectedData=a;
this.loadSelectedData()
},setDeselectData:function(a){this.deselectData=a;
this.loadSelectedData()
},getSelectedData:function(){return this.selectedComponent.getData()
},loadSelectedData:function(){var b=this.selectedData;
var a=this.deselectData;
if(b&&a){this.deselectComponent.clearData();
this.deselectComponent.values=a;
this.deselectComponent.setData(a);
this.selectedComponent.clearData();
this.selectedComponent.appendRecords(this.deselectComponent,b)
}},getComponents:function(){var a=[];
var b=this.getDeselectComponent();
a.push(b);
a.push(this.getButtonsPanel());
var c=this.getSelectedComponent();
a.push(c);
return a
},getButtonsPanel:function(){return isc.VBorderPanel.create({width:"40",height:"100%",align:"center",defaultLayoutAlign:"center",layoutBottomMargin:30,membersMargin:10,members:this.getButtons()})
},getDeselectComponent:function(){var a=isc.addProperties({name:"deselectComponent",title:isc.i18nTitle.Deselect},isc.clone(this.deselectComponentProperties));
return this.createComponent(a)
},getSelectedComponent:function(){var a=isc.addProperties({name:"selectedComponent",title:isc.i18nTitle.Selected},isc.clone(this.selectedComponentProperties));
return this.createComponent(a)
},createComponent:function(c){if(c){var b=isc.addProperties({typeValueMap:this.typeValueMap},isc.SelectPanel.DefaultComponentProperties,this.componentProperties,c);
var a=this.service.createComponent(this.componentType,b);
a.bindListener(this);
this[a.name]=a;
return a
}},itemClick:function(a){if(!this.isReadOnly()){if(a.name=="selectAll"){this.selectedComponent.appendRecords(this.deselectComponent,this.deselectComponent.getData())
}else{if(a.name=="select"){this.selectedComponent.appendRecords(this.deselectComponent)
}else{if(a.name=="deselectAll"){var b=this.fireEvent(isc.SelectPanel.Events.OnSelectAll,this.selectedComponent.getData());
if(b==undefined||b==true){this.deselectComponent.appendRecords(this.selectedComponent,this.selectedComponent.getData())
}}else{if(a.name=="deselect"){var b=this.fireEvent(isc.SelectPanel.Events.OnSelect,this.selectedComponent.getData(),this.selectedComponent.getSelectedRecord());
if(b==undefined||b==true){this.deselectComponent.appendRecords(this.selectedComponent)
}}}}}this.changeDataState()
}},deselectComponent_RowDoubleClick:function(b,a){if(!this.isReadOnly()){this.selectedComponent.appendRecord(b,a);
this.changeDataState()
}return false
},selectedComponent_RowDoubleClick:function(b,a){var c=this.fireEvent(isc.SelectPanel.Events.OnSelect,this.selectedComponent.getData(),this.selectedComponent.getSelectedRecord());
if(!this.isReadOnly()&&(c==undefined||c==true)){this.deselectComponent.appendRecord(b,a);
this.changeDataState()
}return false
},createButton:function(a){var b=this;
return isc.Img.create({width:24,height:24,cursor:"pointer",click:function(){b.itemClick(this)
}},a)
},getButtons:function(){var a=[];
a.push(this.createButton({name:"selectAll",src:"[SKIN]/button/selectAll.png"}));
a.push(this.createButton({name:"select",src:"[SKIN]/button/select.png"}));
a.push(this.createButton({name:"deselect",src:"[SKIN]/button/deselect.png"}));
a.push(this.createButton({name:"deselectAll",src:"[SKIN]/button/deselectAll.png"}));
return a
}});
isc.ClassFactory.defineClass("TreeSelect","SelectPanel");
isc.TreeSelect.addProperties({autoDraw:false,width:"100%",height:"100%",componentType:isc.TreeList});
isc.TreeSelect.addMethods({getSelectedData:function(){var a=this.selectedComponent.getData();
if(a!=undefined&&a!=null){var d=a.data;
if(this.onlySaveLeaves==true){var e=[];
for(var b=0;
b<d.length;
b++){var c=d[b];
if(a.isLeaf(c)){e.push(c)
}}return e
}return d
}}});
isc.ClassFactory.defineClass("UnitValueCompoment");
isc.UnitValueCompoment.addClassProperties({create:function(d){if(d.unitValueProperties!=undefined&&d.unitValueProperties.getUnitValue!=undefined){return d.unitValueProperties
}var a=isc.addProperties({},isc.UnitValueCompoment.DefaultProperties);
var c=d;
if(d.editorProperties){c=d.editorProperties
}if(c.unitValueProperties){isc.addProperties(a,c.unitValueProperties)
}for(var b in isc.UnitValueCompoment.DefaultProperties){var e=c[b];
if(e!=undefined&&e!=null){a[b]=e
}}d.unitValueProperties=a;
return a
},DefaultProperties:{autoDraw:false,format:"#.###",unitAlign:"right",minUnit:null,maxUnit:null,defaultUnit:null,saveUnit:null,createValues:function(f){var b;
var a;
if(f!=undefined&&f!=null){var c=this.getUnitValues();
if(isc.isA.String(f)){f=parseFloat(f)
}if(!isNaN(f)){var g=this.getSaveUnit();
if(g){f=f*g
}for(var d=0;
d<c.length;
d++){var e=c[d];
if(b==undefined||b==undefined){b=f/e;
a=e
}else{if(Math.floor(f/e)!=0){b=f/e;
a=e
}else{break
}}}}}if(b!=undefined&&b!=null){b=parseFloat(b.format(this.format))
}if(a==undefined||a==null){a=this.getDefaultUnit()
}return{value:b,unit:a}
},createFormatValues:function(d){var b="";
var c="";
var a=this.createValues(d);
if(a){if(a.value!=undefined&&a.value!=null){b=a.value.format(this.format)
}if(a.unit){c=this.valueMap[a.unit]
}}return{value:b,unit:c}
},calculateValue:function(a){if(a){var b=a.value;
if(b!=undefined&&b!=null){if(isc.isA.String(b)){b=parseFloat(b)
}if(a.unit){b=b*a.unit
}return this.calculateSaveValue(b)
}}},calculateSaveValue:function(a){if(a!=undefined&&a!=null){var b=this.getSaveUnit();
if(b!=undefined&&b!=null){a=a/b
}a=Math.round(a)
}return a
},getUnitWidth:function(){if(this.unitWidth!=undefined&&this.unitWidth!=null){return this.unitWidth
}return this.defaultUnitWidth
},getUnitName:function(b){if(b!=undefined&&b!=null){if(isc.isA.Number(b)){b=b+""
}for(var a in this.valueMap){if(a==b){return this.valueMap[a]
}}}},getUnitValue:function(b){if(b!=undefined&&b!=null){if(isc.isA.String(b)){for(var a in this.valueMap){if(this.valueMap[a]==b){return parseInt(a)
}}}else{if(isc.isA.Number(b)){return b
}}}},getMinUnit:function(){return this.getUnitValue(this.minUnit)
},getMaxUnit:function(){return this.getUnitValue(this.maxUnit)
},getSaveUnit:function(){return this.getUnitValue(this.saveUnit)
},getUnitValueMap:function(){var a=this.getUnitValues();
var d={};
for(var b=0;
b<a.length;
b++){var c=a[b]+"";
d[c]=this.valueMap[c]
}return d
},getUnitValues:function(){var b=[];
var e=this.getMinUnit();
var a=this.getMaxUnit();
if(e||a){for(var c in this.valueMap){var d=parseInt(c);
if(a&&d>a){continue
}if(e&&d<e){continue
}b.push(d)
}}else{for(var c in this.valueMap){b.push(parseInt(c))
}}b.sort(function(g,f){return g>f?1:-1
});
return b
},getDefaultUnit:function(){return this.getUnitValue(this.defaultUnit)
}}});
isc.ClassFactory.defineClass("ComponentItem","CanvasItem");
isc.ComponentItem.addProperties({autoDraw:false,canFocus:true,shouldSaveValue:true,height:22});
isc.ComponentItem.addMethods({createCanvas:function(){var a=this.getComponent();
if(this.hidden){this.hide()
}return this.getComponent()
},_itemChange:function(b,d,a){if(this.form.itemChange){var e=this.getItemValue(b,d);
var c=this.getItemValue(b,a);
return this.form.itemChange(this,e,c)
}return true
},_itemChanged:function(c,d){var e=this.getItemValue(c,d);
var a=this.getDisplayValue(e);
var b=this.getDisplayValue(this.getValue());
if(a!=b){this.storeValue(e);
this.setValue(e);
if(this.form.itemChanged){this.form.itemChanged(this,e)
}}return false
},_itemBlur:function(a){this._itemChanged(a,a.getValue())
},isReadOnly:function(){if(this.canEdit&&!this.canEdit){return true
}return this.Super("isReadOnly")
},getItemValue:function(a,b){return b
},getDisplayValue:function(a){return a
},getComponent:function(){},setCanEdit:function(b){this.Super("setCanEdit",b);
var a=this.getComponent();
if(a&&a.updateState){a.updateState()
}},setValue:function(a){this.getComponent().setComponentValue(a);
this.Super("setValue",a)
},destroy:function(){var a=this.getComponent();
if(a){a.destroy()
}this.Super("destroy",arguments)
}});
isc.ClassFactory.defineClass("UnitValueBar","HLayout");
isc.UnitValueBar.addProperties({autoDraw:false,defaultLayoutAlign:"center",layoutLeftMargin:2,layoutRightMargin:2,canEdit:true,unitAlign:"Right",defaultUnitValueFormProperties:{autoDraw:false,margin:0,padding:0,width:"100%",cellPadding:1,itemChange:function(b,c,a){return this.unitValueItem._itemChange(b,c,a)
},itemChanged:function(a,b){if(a.name=="unit"){return this.unitValueItem._itemChanged(a,b)
}},isReadOnly:function(){return this.unitValueItem.isReadOnly()
},itemBlur:function(a){return this.unitValueItem._itemBlur(a)
},itemKeyPress:function(c,b,a){if(b=="Tab"||b=="Enter"||b=="Arrow_Up"||b=="Arrow_Down"){c.elementBlur()
}return true
},getComponentValue:function(){var a=this.getValues();
return this.unitValueProperties.calculateValue(a)
},setComponentValue:function(b){var a=this.unitValueProperties.createValues(b);
if(a.value==undefined||a.value==null||a.value==0){this.setData({value:a.value?a.value:null,unit:this.getValue("unit")})
}else{this.setData(a)
}},updateState:function(){this.setCanEdit(this.unitValueItem.getCanEdit())
}},defaultValueFieldProperties:{name:"value",type:"float",showTitle:false,textAlign:"right",keyPressFilter:"[0-9.]",blur:function(b,a){this.Super("blur",arguments);
b.itemBlur(a)
},focus:function(b,a){this.selectValue()
},_formatValue:function(a){if(a!=undefined&&a!=null){if(isc.isA.String(a)){a=parseFloat(a)
}return parseFloat(a.format(this.form.unitValueProperties.format))
}},formatEditorValue:function(d,a,c,b){if(d!=undefined&&d!=null){return this._formatValue(d)
}return this.Super("formatEditorValue",arguments)
},formatValue:function(d,a,c,b){if(d!=undefined&&d!=null){return this._formatValue(d)
}return this.Super("formatValue",arguments)
}},defaultUnitFieldProperties:{name:"unit",type:"integer",showTitle:false}});
isc.UnitValueBar.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.addMember(this.getUnitValueForm());
this.setComponentValue(this.value);
this.updateState()
},getUnitValueForm:function(){if(this.unitValueForm){return this.unitValueForm
}var b=isc.UnitValueCompoment.create(this);
if(this.unitValueItem&&this.unitValueItem.unitValueProperties){isc.addProperties(b,this.unitValueItem.unitValueProperties)
}var d=b.getUnitWidth();
var a=b.getDefaultUnit();
var f=b.getUnitValueMap();
var c=isc.addProperties({unitValueItem:this.unitValueItem,width:this.width-d},b.valueFieldProperties,this.defaultValueFieldProperties,this.valueFieldProperties);
var e=isc.addProperties({},b.unitFieldProperties,{unitValueItem:this.unitValueItem,width:d,value:a,valueMap:f},this.defaultUnitFieldProperties,this.unitFieldProperties);
this.unitValueForm=isc.DynamicForm.create(this.defaultUnitValueFormProperties,{unitValueItem:this.unitValueItem,unitValueProperties:b},b.unitValueFormProperties,this.unitValueFormProperties);
if(this.isReadOnly()){c.type="StaticText";
c.textAlign="left";
e.type="StaticText"
}if(this.unitAlign.toLowerCase()=="left"){this.unitValueForm.setFields([e,c])
}else{this.unitValueForm.setFields([c,e])
}return this.unitValueForm
},isReadOnly:function(){if(this.unitValueItem){return this.unitValueItem.isReadOnly()
}return false
},getItemValue:function(a,b){var d;
if($.defined(b)){if(a.name=="value"){var c=a.form.getValue("unit");
if($.defined(c)){d=b*c
}}else{if(a.name=="unit"){var e=a.form.getValue("value");
if($.defined(e)){d=b*e
}}}d=this.unitValueProperties.calculateSaveValue(d)
}return d
},getComponentValue:function(){return this.unitValueForm.getComponentValue()
},setComponentValue:function(a){this.unitValueForm.setComponentValue(a)
},updateState:function(){this.unitValueItem.updateState();
this.markForRedraw()
},getDisplayValue:function(b){var a=this.unitValueProperties.createValues(b);
return a?a.value+" "+a.unit:null
}});
isc.ClassFactory.defineClass("UnitValueItem","ComponentItem");
isc.UnitValueItem.addClassProperties({defaultWidth:130});
isc.UnitValueItem.addClassMethods({formatDisplayValue:function(d){var a=isc.UnitValueCompoment.create(this);
var b=a.createValues(d);
var d=b.value;
if(d==undefined||d==null||isNaN(d)){d="&nbsp;"
}var c=a.getUnitName(b.unit);
if(c==undefined||c==null||c.isEmpty()){c="&nbsp;"
}return"<table><tr><td>"+d+'</td><td style="width:'+a.minUnitWidth+'px;">'+c+"</td></tr></table>"
}});
isc.UnitValueItem.addMethods({getComponent:function(){if(!this.unitValueBar){this.unitValueBar=isc.UnitValueBar.create({autoDraw:false,unitValueItem:this,width:this.width},this.unitValueBarProperties)
}return this.unitValueBar
},getItemValue:function(a,b){return this.getComponent().getItemValue(a,b)
},getDisplayValue:function(a){return this.getComponent().getDisplayValue(a)
}});
isc.ClassFactory.defineClass("LogicFieldFactory");
isc.LogicFieldFactory.addClassProperties({octetsDefaultProperties:{editorType:"UnitValueItem",minUnitWidth:30,defaultUnitWidth:60,valueMap:{"1":"Byte","1024":"KB","1048576":"MB","1073741824":"GB","1099511627776":"PB"}},octetsVelocityDefaultProperties:{editorType:"UnitValueItem",minUnitWidth:30,defaultUnitWidth:60,valueMap:{"1":"Bps","8192":"KBps","8388608":"MBps","8589934592":"GBps","8796093022208":"PBps"}},velocityDefaultProperties:{editorType:"UnitValueItem",minUnitWidth:25,defaultUnitWidth:60,valueMap:{"1":"/S","1000":"K/S","1000000":"M/S","1000000000":"G/S"}},valueDefaultProperties:{editorType:"UnitValueItem",minUnitWidth:20,defaultUnitWidth:60,valueMap:{"1":"","1000":"K","1000000":"M","1000000000":"G"}},timeLengthDefaultProperties:{editorType:"UnitValueItem",minUnitWidth:30,defaultUnitWidth:60,saveUnit:1000,minUnit:1000,maxUnit:86400000,valueMap:{"1":isc.i18nTime.Millisecond,"1000":isc.i18nTime.Second,"60000":isc.i18nTime.Minute,"3600000":isc.i18nTime.Hour,"86400000":isc.i18nTime.Day}},connectDefaultProperties:{editorType:"BooleanItem",showDisabled:false,valueImgMap:{"true":{width:16,height:16,title:isc.i18nTitle.Connect,src:"[SKIN]/button/connect.gif"},"false":{width:16,height:16,title:isc.i18nTitle.Disconnect,src:"[SKIN]/button/connect_Disabled.gif"}}}});
isc.LogicFieldFactory.addClassMethods({createField:function(b){var a=this.getLogicProperties(b);
if(a){if(b.editorType=="UnitValueItem"){b.unitValueProperties=a
}else{isc.addProperties(b,a)
}}this.createLogicField(b)
},createGridField:function(d){var a=this.getLogicProperties(d);
if(a){if(d.editorType=="UnitValueItem"){if(!$.defined(d.editorProperties)){d.editorProperties={unitValueProperties:a}
}else{d.editorProperties.unitValueProperties=a
}for(var b in isc.UnitValueCompoment.DefaultProperties){var c=d[b];
if(c!=undefined&&c!=null){a[b]=c
}}}else{isc.addProperties(d,a)
}}this.createLogicField(d)
},createLogicField:function(c){if(c.logic){var a=c.editorType;
if(c.editorType==undefined||c.editorType==null){a=c.logic.substring(0,1).toUpperCase()+c.logic.substring(1)+"Item"
}var b=isc.ClassFactory.getClass(a);
if(b!=null){delete c.type;
c.editorType=a;
if(b.defaultWidth!=undefined){c.width=b.defaultWidth
}if(b.formatDisplayValue){c.formatDisplayValue=b.formatDisplayValue
}}}},getLogicProperties:function(b){if(b.logic){var a=this[b.logic+"DefaultProperties"];
if(a){b.editorType=a.editorType;
b.formatDisplayValue=isc.UnitValueItem.formatDisplayValue;
return isc.addProperties({},a)
}}}});
isc.ClassFactory.defineClass("PercentBar","HLayout");
isc.PercentBar.addProperties({autoDraw:false,format:"#.00",defaultLayoutAlign:"center",membersMargin:2,layoutLeftMargin:2,layoutRightMargin:2,minValue:0.01,maxValue:100,value:0,canEdit:true,progressBarProperties:{autoDraw:false,width:"100%",height:"60%",mouseMove:function(){this.trackEvent()
},mouseOut:function(){this.trackEvent()
},trackEvent:function(){if(EventHandler.leftButtonDown()&&!this.percentBar.isReadOnly()){var a=100*(this.getOffsetX()/this.getWidth());
if(a>99){a=100
}else{if(a<=0){a=0
}}a=Math.round(a);
if(this.percentBar._itemChange(this,a,this.getValue())){this.percentBar._itemChanged(this,a)
}}},getValue:function(){return this.percentDone
},setValue:function(a){if(a!=undefined&&a!=null){if(isc.isA.String(a)){a=parseFloat(a)
}this.setPercentDone(a)
}},updateState:function(){if(this.percentBar.isReadOnly()){delete this.cursor
}else{this.cursor="pointer"
}}},percentFormProperties:{autoDraw:false,width:"*",margin:0,padding:0,cellPadding:1,itemChange:function(b,c,a){return this.percentBar._itemChange(b,c,a)
},itemChanged:function(a,b){return this.percentBar._itemChanged(a,b)
},isReadOnly:function(){return this.percentBar.isReadOnly()
},itemBlur:function(a){return this.percentBar._itemBlur(a)
},itemKeyPress:function(c,b,a){if(b=="Tab"||b=="Enter"||b=="Arrow_Up"||b=="Arrow_Down"){c.elementBlur();
this.percentBar._itemKeyPress(c,b,a)
}return true
},setFocusItem:function(a){this.Super("setFocusItem",arguments);
a.selectValue()
},updateState:function(){this.getField("value").setCanEdit(!this.percentBar.isReadOnly())
}},valueFieldProperties:{autoDraw:false,name:"value",width:50,type:"float",showTitle:false,textAlign:"right",keyPressFilter:"[0-9.]",value:0,blur:function(b,a){this.Super("blur",arguments);
b.itemBlur(a)
},formatEditorValue:function(d,a,c,b){if(d!=undefined&&d!=null){return this.percentBar.formatValue(d)
}return this.Super("formatEditorValue",arguments)
},formatValue:function(d,a,c,b){if(d!=undefined&&d!=null){if(isc.isA.String(d)){d=parseFloat(d)
}if(!isNaN(d)&&d!=0){if(d>this.percentBar.maxValue){return">"+this.percentBar.maxValue
}if(d<this.percentBar.minValue){return"<"+this.percentBar.minValue
}}}else{d=0
}return d.format(this.percentBar.format)
}},unitFieldProperties:{autoDraw:false,name:"unit",width:5,type:"StaticText",showTitle:false,value:"%"}});
isc.PercentBar.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.addMembers([this.getProgressBar(),this.getPercentForm()]);
this.setValue(this.value);
this.updateState()
},getProgressBar:function(){if(!this.progressBar){var a=this;
this.progressBar=isc.Progressbar.create({percentBar:this,isDisabled:function(){return false
}},this.progressBarProperties)
}return this.progressBar
},getPercentForm:function(){if(!this.percentForm){var b=this;
var a=isc.addProperties({percentBar:this},this.valueFieldProperties);
var c=isc.addProperties({},this.unitFieldProperties);
this.percentForm=isc.DynamicForm.create({percentBar:this},this.percentFormProperties);
if(this.isReadOnly()){a.type="StaticText";
a.width=40
}this.percentForm.setFields([a,c])
}return this.percentForm
},_itemChange:function(b,c,a){if(this.percentItem){return this.percentItem._itemChange(b,c,a)
}return true
},_itemChanged:function(a,b){if(this.percentItem){this.percentItem._itemChanged(a,b)
}else{this.getProgressBar().setValue(b)
}},_itemKeyPress:function(c,b,a){if(this.percentItem&&this.percentItem.form&&this.percentItem.form.itemKeyPress){return this.percentItem.form.itemKeyPress(this.percentItem,b,a)
}},_itemBlur:function(a){if(this.percentItem){this.percentItem._itemBlur(a)
}else{this.getProgressBar().setValue(a.getValue())
}},isReadOnly:function(){if(this.percentItem){return this.percentItem.isReadOnly()
}if(this.canEdit==undefined||this.canEdit==null){return false
}return !this.canEdit
},setCanEdit:function(a){this.Super("setCanEdit",arguments);
this.updateState()
},updateState:function(){this.getPercentForm().updateState();
this.getProgressBar().updateState();
this.markForRedraw()
},setFocus:function(b,a){this.getPercentForm().focusInItem("value")
},getValue:function(){return this.getComponentValue()
},getComponentValue:function(){return this.getPercentForm().getValue("value")
},setValue:function(a){this.setComponentValue(a)
},setComponentValue:function(a){this.getProgressBar().setPercentDone(a);
this.getPercentForm().setValue("value",a)
},formatValue:function(a){if(a!=undefined&&a!=null){if(isc.isA.String(a)){a=parseFloat(a)
}return parseFloat(a.format(this.format))
}},destroy:function(){this.getProgressBar().destroy();
this.getPercentForm().destroy();
this.Super("destroy",arguments)
}});
isc.ClassFactory.defineClass("PercentItem","ComponentItem");
isc.PercentItem.addClassProperties({defaultWidth:130,startImg:"[SKIN]Progressbar/progressbar_h_start.gif",stretchImg:"[SKIN]Progressbar/progressbar_h_stretch.gif",endImg:"[SKIN]Progressbar/progressbar_h_end.gif",emptyStartImg:"[SKIN]Progressbar/progressbar_h_empty_start.gif",emptyStretchImg:"[SKIN]Progressbar/progressbar_h_empty_stretch.gif",emptyEndImg:"[SKIN]Progressbar/progressbar_h_empty_end.gif",defaultPercentFormat:"#.00"});
isc.PercentItem.addClassMethods({formatDisplayValue:function(i){var a=0;
var d="0.00";
if(i!=undefined&&i!=null){if(isc.isA.String(i)){i=parseFloat(i)
}if(!isNaN(i)&&i!=0){var e=this.maxValue?this.maxValue:100;
var g=this.minValue?this.minValue:0.01;
if(i>e){d=">"+e;
a=100
}else{if(i<g){d="<"+g
}else{var h=this.format?this.format:isc.PercentItem.defaultPercentFormat;
d=i.format(h);
a=parseFloat(i)
}}}}var j=13;
var f=55;
var c=0;
var b='<table style="width:100%;"><tr><td>';
b+='<div style="height:'+j+'px;width:100%;"><nobr>';
if(a<=0||a>=100){c=4
}if(a>0){b+=isc.Canvas.imgHTML(isc.PercentItem.startImg,2,j);
b+=isc.Canvas.imgHTML(isc.PercentItem.stretchImg,a+"%",j);
if(c>0){b+=isc.Canvas.imgHTML(isc.PercentItem.stretchImg,c,j)
}b+=isc.Canvas.imgHTML(isc.PercentItem.endImg,2,j)
}if(a<100){b+=isc.Canvas.imgHTML(isc.PercentItem.emptyStartImg,2,j);
b+=isc.Canvas.imgHTML(isc.PercentItem.emptyStretchImg,(100-a)+"%",j);
if(c>0){b+=isc.Canvas.imgHTML(isc.PercentItem.emptyStretchImg,c,j)
}b+=isc.Canvas.imgHTML(isc.PercentItem.emptyEndImg,2,j)
}b+="</nobr></div>";
b+='</td><td style="width:'+f+'px;text-align: right;">';
b+=d+"%";
b+="</td></tr></table>";
return b
}});
isc.PercentItem.addMethods({getComponent:function(){if(!this.percentBar){this.percentBar=isc.PercentBar.create({autoDraw:false,width:this.width,height:this.height,percentItem:this},this.percentBarProperties)
}return this.percentBar
},getDisplayValue:function(a){return this.percentBar.formatValue(a)
}});
isc.ClassFactory.defineClass("BooleanButton","HLayout");
isc.BooleanButton.addClassProperties({DefaultValueImgMap:{"true":{width:45,height:15,src:"[SKIN]/button/on.png"},"false":{width:45,height:15,src:"[SKIN]/button/off.png"}},DefaultImgProperties:{autoDraw:false,imageType:"normal",cursor:"pointer",click:function(){if(this.booleanButton){this.booleanButton.click()
}return false
}}});
isc.BooleanButton.addProperties({autoDraw:false,height:22,align:"center",layoutTopMargin:3,defaultLayoutAlign:"center"});
isc.BooleanButton.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.addMembers([this.getTrueImg(),this.getFalseImg()]);
this.setValue(this.value);
this.updateState()
},getTrueImg:function(){if(!this.trueImg){this.trueImg=this.createImg(true)
}return this.trueImg
},getFalseImg:function(){if(!this.falseImg){this.falseImg=this.createImg(false)
}return this.falseImg
},createImg:function(c){var b=this.valueImgMap?this.valueImgMap:isc.BooleanButton.DefaultValueImgMap;
var a=b[c+""];
return isc.Img.create({booleanButton:this,width:a.width+this.layoutTopMargin*2,showFocused:true,showDisabled:this.showDisabled,imageWidth:a.width,imageHeight:a.height,prompt:a.title,src:a.src},isc.BooleanButton.DefaultImgProperties)
},click:function(){if(!this.isReadOnly()){if(this.valueChange){if(this.valueChange(this,!this.value,this.value)==false){return
}}this.setValue(!this.value);
if(this.booleanItem){this.booleanItem.storeValue(this.value)
}if(this.valueChanged){this.valueChanged(this,this.value)
}this.setFocus()
}},isReadOnly:function(){if(this.booleanItem){return this.booleanItem.isReadOnly()
}if(this.canEdit==undefined||this.canEdit==null){return false
}return !this.canEdit
},setCanEdit:function(a){this.canEdit=a;
this.updateState()
},getValue:function(){return this.getComponentValue()
},getComponentValue:function(){return this.value
},setFocus:function(b,a){if(this.value){this.trueImg.setFocus()
}else{this.falseImg.setFocus()
}},setValue:function(a){this.setComponentValue(a)
},setComponentValue:function(a){if(a!=undefined&&a!=null&&isc.isA.String(a)){a=Boolean.parseText(a)
}this.value=a;
if(this.value){this.falseImg.hide();
this.trueImg.show();
if(!this.isReadOnly()){this.falseImg.setCanFocus(false);
this.trueImg.setCanFocus(true)
}}else{this.trueImg.hide();
this.falseImg.show();
if(!this.isReadOnly()){this.trueImg.setCanFocus(false);
this.falseImg.setCanFocus(true)
}}},updateState:function(){if(this.isReadOnly()){delete this.falseImg.cursor;
delete this.trueImg.cursor;
this.trueImg.setCanFocus(false);
this.falseImg.setCanFocus(false)
}else{this.falseImg.cursor="pointer";
this.trueImg.cursor="pointer"
}this.markForRedraw()
},destroy:function(){this.trueImg.destroy();
this.falseImg.destroy();
this.Super("destroy",arguments)
}});
isc.ClassFactory.defineClass("BooleanItem","ComponentItem");
isc.BooleanItem.addClassProperties({defaultWidth:45});
isc.BooleanItem.addClassMethods({formatDisplayValue:function(d){if(d!=undefined&&d!=null&&isc.isA.String(d)){d=Boolean.parseText(d)
}var c=this.valueImgMap?this.valueImgMap:isc.BooleanButton.DefaultValueImgMap;
var b=c[d+""];
var a=ImgUtils.getHtml(b.src,b.width,b.height,{title:b.title});
return'<div style="width:100%;height:16px;padding-top:3px;">'+a+"</div>"
}});
isc.BooleanItem.addProperties({textAlign:"center"});
isc.BooleanItem.addMethods({getComponent:function(){if(!this.booleanButton){var a=this;
this.booleanButton=isc.BooleanButton.create({width:this.width,height:this.height,booleanItem:this,showDisabled:this.showDisabled,valueImgMap:a.valueImgMap,valueChange:function(c,d,b){return a._itemChange(c,d,b)
},valueChanged:function(b,c){return a._itemChanged(b,c)
}},this.percentBarProperties)
}return this.booleanButton
}});
isc.UploadItem.addProperties({elementHeight:(isc.Browser.isMoz?22:null)});
isc.UploadItem.addMethods({getElementStyleHTML:function(){return this.Super("getElementStyleHTML")+this.getElementPropertiesHTML()
},getElementHTML:function(){var a=$("<p></p>").append(this.Super("getElementHTML"));
a.find("input").css("width","100%");
a.find("input").css("padding","0px");
return a[0].innerHTML
},validate:function(d){if(d!=undefined&&d!=null){if(this.fileSuffix){var c=false;
var e=d.toLowerCase();
var a=this.fileSuffix.toLowerCase().split(";");
for(var b=0;
b<a.length;
b++){if(e.endsWith("."+a[b])){c=true;
break
}}return c
}}return true
},saveValue:function(b,a){if(this.saving){return
}this.saving=true;
if(this.validate(b)){this.Super("saveValue",arguments);
this.form.itemChanged(this,b)
}else{this.setElementValue(null);
this.form.setValue(this.name,null);
this.form.itemChanged(this,null);
isc.say(isc.i18nValidate.FileSuffix+this.fileSuffix)
}delete this.saving
}});
var overridePickListProperties={getClientPickListData:function(){if(this.getPickData){var a=this.getPickData();
if(a){return a
}}return this.pickData||isc.PickList.optionsFromValueMap(this)
},_$substring:"substring",filterClientPickListData:function(){if(this.isEntryTooShortToFilter&&this.isEntryTooShortToFilter()){return null
}var f=this.getClientPickListData();
var n=this.getPickListFilterCriteria();
if(n==null||isc.isA.emptyObject(n)||!$.defined(n.criteria)){return f
}var m=this.filterDisplayValue;
if(this.displayField){m=(this.filterFields.indexOf(this.displayField)==-1)
}var h=[],e;
if(this.showAllOptions){e=this.separatorRows.duplicate()
}var b=false;
var a=f.getLength(),p=this.getDisplayFieldName(),j=this.getValueFieldName();
for(var d=0;
d<a;
d++){var g=false;
for(var l=0;
l<n.criteria.length&&!g;
l++){var c=n.criteria[l];
if(!c||isc.isA.emptyString(c.value)){continue
}b=true;
var q=c.value;
if(!isc.isA.String(q)){q+=isc.emptyString
}q=q.toLowerCase();
var o=c.fieldName;
var k=f[d][o];
if(m&&o==j){if(p){k=f[d][p]
}else{k=this.mapValueToDisplay(k)
}}if(!isc.isA.String(k)){k+=""
}k=k.toLowerCase();
if((this.textMatchStyle==this._$substring&&k.contains(q))||(this.textMatchStyle!=this._$substring&&isc.startsWith(k,q))){g=true
}}if(g){h.add(f[d])
}else{if(this.showAllOptions){e.add(f[d])
}}}if(!b){h=f.duplicate()
}if(this.showAllOptions&&e.length>1){h.addList(e)
}return h
},selectDefaultItem:function(){if(this.pickList==null||this.pickList.destroyed){return
}var c=this.pickList.selection;
var a;
if(this.pickList.findRecord){var b=this.getValueFieldName(),d=this.getElementValue();
a=this.pickList.findRecord(b,d)
}if(!$.defined(a)){var e=this.$824||this._pendingEnteredValue||this.getValue();
if(!this.addUnknownValues&&e!=null){a=this.getPickListRecordForValue(e)
}else{a=this.getSelectedRecord()
}}if(a){this.pickList.clearLastHilite();
this.delayCall("selectItemFromValue",[a[this.valueField]]);
return
}a=this.pickList.getRecord(0);
if(a==null||Array.isLoading(a)||a[this.pickList.isSeparatorProperty]){return
}c.selectSingle(a);
this.pickList.clearLastHilite();
this.pickList.scrollRecordIntoView(0)
}};
isc.ComboBoxItem.addMethods(overridePickListProperties);
isc.TextItem.addMethods(overridePickListProperties);
isc.FormItem.addMethods({getElementPropertiesHTML:function(){var a=isc.SB.create();
if(this.elementProperties){for(var b in this.elementProperties){var c=this.elementProperties[b];
if(isc.isA.String(c)&&!c.isEmpty()){a.append(b,"='",c,"'")
}}}return a.toString()
}});
isc.ClassFactory.defineClass("ImgChart","Img");
isc.ImgChart.addProperties({width:"100%",height:"100%",baseStyle:"imgChart",activeAreaMethodName:null,srcMethodName:null});
isc.ImgChart.addMethods({load:function(c){var b=this;
var d=isc.addProperties({width:this.width,height:this.height},c);
var a=this.service;
a.invoke(this.activeAreaMethodName,d,function(e){var f=a.getMethodURL(b.srcMethodName);
b.src=f+"?_"+new Date().getTime();
if(e.value){b.activeAreaHTML=$(e.value).html()
}b.markForRedraw()
})
}});
isc.ClassFactory.defineClass("HighChart","VBorderPanel");
isc.HighChart.addProperties({autoDraw:false,width:"100%",height:"100%"});
isc.HighChart.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.getChart()
},setData:function(b){var a=this.getChart();
if(a){a.load(b)
}},ajaxLoad:function(e,b,d,a){var c=this.getChart();
if(c){c.dataURL=this.service.getMethodURL(this.loadMethodName);
c.ajaxLoad(e,b,d,a)
}},refresh:function(){if(!isc.ApplicationContext.isLocale()&&$.defined(this.loadMethodName)){this.ajaxLoad(this.params)
}else{if($.defined(this.loadData)){this.load(this.loadData())
}}return true
},load:function(b){var a=this.getChart();
if(a){a.load(b)
}},getHandleDiv:function(){return this._handle||this.$q3
},getChart:function(){if(!$.defined(this.chart)){this.chart=this.createHighChart()
}return this.chart
},createHighChart:function(){var b,d=this.getHandleDiv();
if(d){var c=this;
this.chartOptions.width=this.getChartWidth();
this.chartOptions.height=this.getChartHeight();
this.chartOptions.chart={events:{click:function(f){c.click(f)
}}};
b=HighChartsFactory.createHighChart(d.id,this.chartOptions,this.data);
if($.defined(this.chartOptions.loadInterval)){this.timer=isc.TimerEx.create({owner:this});
this.timer.setInterval("refresh",this.chartOptions.loadInterval,this.chartOptions.loadInterval)
}}else{var a=this;
window.setTimeout(function(){a.getChart()
},500)
}return b
},resized:function(){var b=this.getHandleDiv();
if(b&&b.childElementCount==0){this.clearChart()
}var a=this.getChart();
if(a){a.resize(this.getChartWidth(),this.getChartHeight())
}},clearChart:function(){if(this.timer){this.timer.clear("refresh")
}if(this.chart){this.chart.destroy();
delete this.chart
}},getChartWidth:function(){return this.getWidth()-2
},getChartHeight:function(){return this.getHeight()-2
},redraw:function(){this.Super("redraw",arguments);
this.redrawChart()
},redrawChart:function(){if(!$.defined(this.callLator)){var a=this;
this.callLator=function(){if(a.isVisible()){a.resized()
}delete a.callLator
};
window.setTimeout(this.callLator,500)
}},visibilityChanged:function(a){this.Super("visibilityChanged",arguments);
if(a){this.redrawChart()
}},destroy:function(){this.clearChart();
this.Super("destroy",arguments)
}});
isc.ClassFactory.defineClass("TabPanel","Layout");
isc.TabPanel.addProperties({autoDraw:false,width:"100%",height:"100%",vertical:true,tabs:[],defaultTabProperties:{autoDraw:false,width:"100%",height:"100%",vertical:false,styleName:"tabPanel",visibility:"hidden"}});
isc.TabPanel.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.header=isc.TabHeader.create();
this.tabs.clear();
this.addMember(this.header);
var c=this;
this.header.itemClick=function(d){c.select(d.name)
};
if(this.data){for(var b=0;
b<this.data.length;
b++){var a=this.data[b].name;
if(!$.defined(a)){a="tab"+b
}this.addTab(a,this.data[b])
}}this.select()
},select:function(b){var d;
if(isc.isA.Number(b)&&b>=0&&b<=this.panels.length){d=this.tabs[b]
}else{if(isc.isA.String(b)){for(var c=0;
c<this.tabs.length;
c++){if(this.tabs[c].name==b){d=this.tabs[c];
break
}}}}if(this.tabs.length>0){if(d==undefined||d==null){d=this.tabs[0]
}for(var c=0;
c<this.tabs.length;
c++){if(this.tabs[c]!=d){this.tabs[c].panel.hide()
}}}if(d){d.panel.show();
var e=d.panel.members[0];
if($.defined(e)&&$.isFunction(e.getView)){var a=e.getView();
if(a&&a.onActive){a.onActive()
}}this.header.select(b);
this.currentTab=d
}},addTab:function(c,d){var a;
if(d.data){a=isc.MenuModuleLayout.create({data:d.data,icon:d.icon,url:d.url})
}else{if(d.url){a=isc.Layout.create(this.defaultTabProperties,d);
var b=isc.addProperties({url:d.url},isc.ModuleManager.ModuleDefaultProperties);
a.addMember(isc.ModuleLoader.create({module:b}))
}}if(a){this.addMember(a);
this.tabs.push({name:c,panel:a});
this.header.addTab(c,d)
}}});
isc.ClassFactory.defineClass("TabButton","Button");
isc.TabButton.addProperties({autoDraw:false,baseStyle:"tabButton",height:"100%",canFocus:false,actionType:"radio",radioGroup:"tab",autoFit:true});
isc.ClassFactory.defineClass("TabHeader","HLayout");
isc.TabHeader.addProperties({autoDraw:false,styleName:"tabHeader",width:"100%",align:"center",height:15,buttons:[],membersMargin:15});
isc.TabHeader.addMethods({initWidget:function(){this.Super("initWidget",arguments);
if(this.data){for(var b=0;
b<this.data.length;
b++){var a=this.data[b].name;
if($.defined(a)){a="tab"+b
}this.addTab(a,this.data[b])
}}this.select()
},select:function(a){var c;
if(isc.isA.Number(a)&&a>=0&&a<=this.panels.length){c=this.buttons[a]
}else{if(isc.isA.String(a)){for(var b=0;
b<this.buttons.length;
b++){if(this.buttons[b].name==a){c=this.buttons[b];
break
}}}}if(this.buttons.length>0){if(c==undefined||c==null){c=this.buttons[0]
}}if(c){c.select()
}},addTab:function(a,d){var c=this;
var b=isc.TabButton.create(d);
b.name=a;
b.click=function(){if(c.itemClick){c.itemClick(this)
}};
this.addMember(b);
this.buttons.push(b)
}});
isc.ClassFactory.defineClass("MenuSection","SectionStack");
isc.MenuSection.addProperties({autoDraw:false,overflow:"auto",width:"100%",height:"100%",visibilityMode:"mutex",showResizeBar:true,styleName:"menuSection",treeGridProperties:{autoDraw:false,width:"100%",border:"none",autoFitData:"vertical",bodyBackgroundColor:"none",showHeader:false,nodeIcon:null,folderIcon:null,showOpenIcons:false,showDropIcons:false,showConnectors:false,openerImage:"[SKIN]/../tree/opener.gif",openerIconSize:16,closedIconSuffix:"",bodyProperties:{overflow:"hidden"}},treeProperties:{modelType:"children",nameProperty:"title",childrenProperty:"submenu"}});
isc.MenuSection.addMethods({initWidget:function(){this.Super("initWidget",arguments);
if(this.data){var a=this;
for(var b=0;
b<this.data.length;
b++){var d={title:this.data[b].title,expanded:true,items:[]};
var c=isc.TreeGrid.create(this.treeGridProperties,{data:isc.Tree.create(this.treeProperties,{root:{submenu:this.data[b].submenu}}),nodeClick:function(g,e,f){if(a.itemClick){a.itemClick(e)
}}});
d.items.push(c);
this.addSection(d)
}}this.expandSection(0)
}});
isc.ClassFactory.defineClass("MenuModuleLayout","HLayout");
isc.MenuModuleLayout.addProperties({autoDraw:false,overflow:"auto",width:"100%",height:"100%",padding:1,resizeBarSize:2});
isc.MenuModuleLayout.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.addMember(this.createMenuSection());
this.addMember(this.createModuleTabSet())
},createMenuSection:function(){var a=this;
return isc.MenuSection.create({data:this.data,width:200,itemClick:function(b){a.moduleTabSet.loadModule(b)
}})
},createModuleTabSet:function(){var a;
if(this.url){a={homeModule:{title:isc.i18nButton.home,icon:this.icon,url:this.url}}
}this.moduleTabSet=isc.ModuleTabSet.create(a);
return this.moduleTabSet
}});
isc.ClassFactory.defineClass("BackgroundLayout","Img");
isc.BackgroundLayout.addProperties({autoDraw:false,height:"100%",width:"100%",align:"center",imageType:"stretch",orientation:"vertical",contentLayoutProperties:null});
isc.BackgroundLayout.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.contentLayout=isc.Layout.create({autoDraw:false,width:"100%",height:"100%",orientation:this.orientation,members:this.members},this.contentLayoutProperties);
this.addChild(this.contentLayout)
},addMember:function(a){this.contentLayout.addMember(a)
},addMembers:function(a){this.contentLayout.addMembers(a)
}});
isc.PickList.addClassMethods({optionsFromValueMap:function(p){var b=p.getValueMap(),c=[];
if(b==null){b=[]
}var q=p.getValueFieldName(),g=p.getDisplayFieldName();
if(isc.isAn.Array(b)){for(var e=0;
e<b.length;
e++){c[e]={};
c[e][q]=b[e];
if(g!=null){c[e][g]=b[e]
}}}else{if(isc.isAn.Object(b)){var e=0;
var l=p.dataType||p.getType(),f,k,a;
if(l!=null){if(isc.SimpleType.inheritsFrom(l,"integer")){f=true
}else{if(isc.SimpleType.inheritsFrom(l,"float")){k=true
}else{if(isc.SimpleType.inheritsFrom(l,"boolean")){a=true
}}}}for(var d in b){c[e]={};
var o=d;
if(f){var h=parseInt(o);
if(h==o){o=h
}}else{if(k){var n=parseFloat(o);
if(n==o){o=n
}}else{if(a){var m=(o=="true"?true:(o=="false"?false:null));
if(m!=null){o=m
}}}}c[e][q]=o;
if(g!=null){c[e][g]=b[d]
}e++
}c.$882=true;
c._derivedFromValueMapObject=true
}}return c
}});
var ScrollingMenuProperties={useBackMask:true,canFocus:true,showHeader:false,showEdges:false,autoDraw:false,className:"scrollingMenu",bodyStyleName:"scrollingMenuBody",selectionType:"single",leaveScrollbarGap:false,generateClickOnSpace:false,generateDoubleClickOnEnter:false,generateClickOnEnter:true,showModal:true,arrowKeyAction:"select",enableSelectOnRowOver:true,filterOnKeypress:true,show:function(){if(this.showModal){this.showClickMask({target:this,methodName:"cancel"},false,[this])
}this.Super("show",arguments);
if(this.showModal){this.body.focus()
}},recordClick:function(g,b,f,e,a,d,c){this.hide();
if(b!=null){this.itemClick(b)
}},itemClick:function(a){},rowOver:function(a,c,b){if(this.enableSelectOnRowOver){this.selection.selectSingle(a)
}},createSelectionModel:function(g,f,k,j,i){var h=this.invokeSuper("ScrollingMenu","createSelectionModel",g,f,k,j,i);
this.selection.addProperties({selectOnRowOver:function(a){this.selectSingle(a);
this.selectionFromMouse=true
},setSelected:function(a,b){this.selectionFromMouse=false;
return this.Super("setSelected",arguments)
}});
return h
},bodyKeyPress:function(c,d){var b=c.keyName;
if(b==this._$Enter){var a=this.selection;
if(a&&a.selectionFromMouse){this.cancel();
return false
}}if(b=="Escape"){this.cancel();
return false
}return this.Super("bodyKeyPress",arguments)
},cancel:function(){this.hide()
},hide:function(){this.hideClickMask();
return this.Super("hide",arguments)
},_selectFirstOnDataChanged:true,dataChanged:function(){var a=this.Super("dataChanged",arguments);
if(!this._selectFirstOnDataChanged){return
}if(this.data&&this.data.getLength()>0&&this.selection&&!this.selection.anySelected()&&(isc.isA.ResultSet==null||!isc.isA.ResultSet(this.data)||this.data.rowIsLoaded(0))){this.selection.selectItem(0)
}return a
}};
var filterEditorDefaults={backgroundColor:"white",editorKeyPress:function(c,b,a){if(b=="Arrow_Down"){this.sourceWidget._navigateToNextRecord(1);
return false
}if(b=="Arrow_Up"){this.sourceWidget._navigateToNextRecord(-1);
return false
}if(b=="Enter"){this.sourceWidget._generateFocusRecordClick();
return
}return this.Super("editorKeyPress",arguments)
}};
var dataGridPickerClassProperties={_cachedDSPickLists:{},pickListCacheLimit:50,DefaultPickListProperties:{enableSelectOnRowOver:false},getInstance:function(a){if($.defined(a.form._pickListCache)){return a.form._pickListCache[a.name]
}},createPickListProperties:function(a){if(!$.defined(a.pickListProperties)){a.pickListProperties={}
}isc.addProperties(a.pickListProperties,this.DefaultPickListProperties);
if(a.pickListFields.length==1){a.pickListProperties.baseStyle="dataGridPicker"
}else{a.pickListProperties.bodyStyleName="dataGridPickerBody"
}},createInstance:function(c,b){this.createPickListProperties(c);
if($.defined(c.grid)){var d=c.grid.getField(c.name);
if(d){if(!$.defined(d.editorProperties)){d.editorProperties={}
}this.createPickListProperties(d.editorProperties)
}}var a=this.create(c.pickListProperties,b);
if(!$.defined(c.form._pickListCache)){c.form._pickListCache={}
}c.form._pickListCache[c.name]=a;
return a
}};
var dataGridPickerProperties={width:20,height:300,autoDraw:false,useAllDataSourceFields:false,autoFitWidth:true,tabIndex:-1,canResizeFields:false,canFreezeFields:false,normalCellHeight:16,showOverAsSelected:true,scrollToCellXPosition:"left",scrollToCellYPosition:"top",getValueIcon:function(g,f,a){var e=this.formItem;
var c=e&&!e.suppressValueIcons&&(e.valueIcons!=null||e.getValueIcon!=null);
if(c){var b=e.getValueFieldName(),d=e.valueIconField||e.getDisplayFieldName()||b;
if(this.getFieldName(g)==d){return e._getValueIcon(a[b])
}}return this.Super("getValueIcon",arguments)
},getArrowKeyAction:function(){return this.allowMultiSelect?"focus":"select"
},rowClick:function(c,d,b,a){this._keyboardRowClick=a;
this.Super("rowClick",arguments);
delete this._keyboardRowClick
},recordClick:function(c,e,d,h,g,i,b){var a=!this.allowMultiSelect;
if(this._keyboardRowClick){var f=(isc.EH.getKey()=="Enter");
if(!f){return
}a=true
}if(a){this.hide()
}if(e!=null){this.itemClick(e)
}},selectOnGeneratedCellClick:function(b,d,c,a){if(this.allowMultiSelect&&isc.EH.getKey()=="Enter"){return false
}return this.Super("selectOnGeneratedCellClick",arguments)
},headerClick:function(a,d){var c=this.Super("headerClick",arguments);
var b=this.getField(a);
if(this.isCheckboxField(b)&&this.allowMultiSelect){this.multiSelectChanged()
}return c
},multiSelectChanged:function(){var f=this.formItem,g=f.getValueFieldName(),e=this.getSelection(),d=true,a=[];
for(var c=0;
c<e.length;
c++){d=false;
var b=e[c];
a.add(b[g])
}f.pickValue(d?null:a)
},itemClick:function(a){if(this.allowMultiSelect){this.multiSelectChanged()
}else{var c=this.formItem,d=c.getValueFieldName();
var b=a[d];
c.pickValue(b)
}},hide:function(g,f,i,h){var e=this.isVisible()&&this.isDrawn();
this.Super("hide",g,f,i,h);
if(!this.formItem){return
}if(e&&this.showModal){this.formItem.focusInItem()
}this.formItem._showingPickList=null;
this.formItem.$19g=null;
if(e){this.formItem._pickListHidden()
}delete this.formItem._showOnFilter;
delete this.formItem.$19i;
this.clearLastHilite()
},show:function(){var a=this.isVisible()&&this.isDrawn();
this.generateClickOnEnter=true;
this.generateClickOnSpace=this.allowMultiSelect;
this.bringToFront();
this.Super("show",arguments);
if(!a){this.formItem._pickListShown()
}},showClickMask:function(){if(!this.clickMaskUp(this.getID())){var b=this.Super("showClickMask",arguments);
if(this.formItem){var c=this.formItem.form,a=isc.EH.clickMaskRegistry.find("ID",b);
if(a._maskedFocusCanvas){a._maskedFocusCanvas=null
}}}},_$_backgroundColor:"background-color:",_$_color:"color:",getCellCSSText:function(a,d,b){if(this.selection!=null&&a==this.selection.getSelectedRecord()){var c=[];
if(this.hiliteColor!=null){c[0]=this._$_backgroundColor
}c[1]=this.hiliteColor;
c[2]=isc._semi;
if(this.hiliteTextColor!=null){c[3]=this._$_color
}c[4]=this.hiliteTextColor;
c[5]=isc.semi;
return c.join(isc.emptyString)
}},keyDown:function(){var a=isc.EH.lastEvent.keyName;
if(a=="Tab"){this.hide();
return false
}},_formatCellValue:function(c,a,d,e,b){if(this.formItem==null){return this.Super("_formatCellValue",arguments)
}var f=this.getFieldName(b);
if($.defined(this.pickList)){c=this.formItem.formatPickListValue(c,f,a)
}return this.Super("_formatCellValue",[c,a,d,e,b])
},bodyKeyPress:function(a,d){var i=isc.EH.lastEvent.keyName;
if(isc.Browser.isSafari){if(i=="Tab"){this.hide();
return false
}}var e=isc.EH.getKeyEventCharacterValue();
if(e!=null){var b=this.formItem.getAllLocalOptions();
if(isc.isAn.Array(b)&&b.length>1){var k=String.fromCharCode(e),k=k.toLowerCase(),f=this.formItem,j=f.getValueFieldName(),c=b.indexOf(this.getSelectedRecord()),h=c<(b.length-1)?c+1:0;
while(h!=c){if(c<0){c=0
}var g=b[h][j];
g=f.mapValueToDisplay(g);
if(isc.isA.String(g)&&g.length>0&&g.charAt(0).toLowerCase()==k){this.scrollRecordIntoView(h);
this._hiliteRecord(h);
return
}h+=1;
if(h>=b.length){h=0
}}}}if(this.getFocusRow()==null&&i=="Enter"){this.cancel();
return false
}return this.Super("bodyKeyPress",arguments)
},dataChanged:function(a,c,h,e){var b=this.data;
if(!b){return
}var b=this.requestVisibleRows();
if(b&&Array.isLoading(b[0])){return
}this.Super("dataChanged",arguments);
var f=this.formItem;
if(c&&this.getSelectedRecord()==c&&f){var d=this.data.indexOf(c),i=d==-1?null:this.data.get(d);
if(i){var g=f.getValueFieldName();
f.setValue(i[g])
}else{f.clearValue()
}}},findRecord:function(a,h){if(this.filterWithValue!=false||!$.defined(h)){return
}var f=h;
if(!isc.isA.String(h)){f=(h+"").trim()
}if(f==""){return
}var c;
if(isc.isA.ResultSet(this.data)){c=this.data.localData
}else{if(isc.isA.Tree(this.data)){c=this.data.getAllNodes()
}else{if(isc.isA.Array(this.data)){c=this.data
}}}if(c){var g=a;
if(!isc.isA.Array(a)){g=[a]
}var e;
for(var d=0;
d<c.length;
d++){for(var b=0;
b<g.length;
b++){var k=c[d][g[b]];
if(!isc.isA.String(k)){k=(k+"").trim()
}if(k==f){e=c[d];
break
}else{if(!$.defined(e)&&k.indexOf(f)!=-1){e=c[d]
}}}}if(e){return e
}if(this.formItem.filterFields&&this.formItem.filterFields!=g){return this.findRecord(this.formItem.filterFields,h)
}}},createBodies:function(){if(this.body&&this.body._reused){delete this.body._reused
}this.Super("createBodies",arguments)
}};
var bodyDefaults={remapOverStyles:[0,2,2,2,4,6,6,6,8,10,10,10,12],getCellStyleName:function(d,a,c,b){if(this.grid&&this.grid.showOverAsSelected){d=this.remapOverStyles[d]
}return this.Super("getCellStyleName",[d,a,c,b],arguments)
}};
isc.ClassFactory.defineClass("ScrollingDataMenu","DataGrid");
isc.ScrollingDataMenu.addProperties(ScrollingMenuProperties);
isc.ScrollingDataMenu.changeDefaults("filterEditorDefaults",filterEditorDefaults);
isc.ClassFactory.defineClass("DataGridPicker","ScrollingDataMenu");
isc.DataGridPicker.addClassProperties(dataGridPickerClassProperties);
isc.DataGridPicker.addProperties(dataGridPickerProperties);
isc.DataGridPicker.changeDefaults("bodyDefaults",bodyDefaults);
var treeDataGridPickerProperties={wrapCells:false,nodeIcon:null,folderIcon:null,filterWithValue:false,updateDataModel:function(c,a,b){return this.getData()
},dataArrived:function(a){this.Super("dataArrived",arguments);
if(this.formItem&&this.formItem.selectDefaultItem){this.formItem.selectDefaultItem()
}}};
isc.ClassFactory.defineClass("ScrollingTreeMenu","TreeDataGrid");
isc.ScrollingTreeMenu.addProperties(ScrollingMenuProperties);
isc.ScrollingTreeMenu.changeDefaults("filterEditorDefaults",filterEditorDefaults);
isc.ClassFactory.defineClass("TreeDataGridPicker","ScrollingTreeMenu");
isc.TreeDataGridPicker.addClassProperties(dataGridPickerClassProperties);
isc.TreeDataGridPicker.addProperties(dataGridPickerProperties,treeDataGridPickerProperties);
isc.TreeDataGridPicker.changeDefaults("bodyDefaults",bodyDefaults);