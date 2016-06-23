isc.ClassFactory.defineClass("UserWindow","DialogWindow");
isc.UserWindow.addProperties({width:320,height:180,buttons:[isc.ToolButton.ButtonTypes.ModifyPassword,isc.ToolButton.ButtonTypes.Exit],DefaultServiceProperties:{toolBar_modifyPassword_ItemClick:function(a,b){isc.ChangePasswordWindow.open(b,{no:isc.ApplicationContext.servicePrincipal.no,jndiName:"Staff/OperatorInfo"});
return true
}}});
isc.UserWindow.addMethods({initialize:function(){this.Super("initialize",arguments);
if(isc.ApplicationContext.servicePrincipal.type!="OperatorSecurity"){this.toolBar.modifyPassword.hide()
}},getLayoutProperties:function(){return[{name:"viewForm",classType:isc.DataForm,properties:{fields:[{name:"no",title:isc.i18nLogin.LoginNo},{name:"name",title:isc.i18nLogin.Name},{name:"roleName",title:isc.i18nLogin.RoleName},{name:"loginTime",title:isc.i18nLogin.LoginTime,length:19,formatValue:function(d,a,c,b){return $.isDate(d)?d.format("yyyy-MM-dd HH:mm:ss"):d
}},{name:"clientIp",title:isc.i18nLogin.ClientIp}],values:isc.ApplicationContext.servicePrincipal}}]
}});
isc.ClassFactory.defineClass("QueryToolBar","ToolStripBar");
isc.QueryToolBar.addProperties({DefaultDataProperties:[{name:"first"},{name:"prior"},{name:"next"},{name:"last"},"separator",{name:"refresh"},{name:"view",permission:"view",selected:false,actionType:"checkbox"},{name:"add",permission:"create"},{name:"detail",permission:"view"},{name:"remove",permission:"delete"},{name:"filter"},{name:"exportdoc",type:"Menu",permission:"export",data:[{title:"XML",name:"toXml"},{title:"CSV",name:"toCSV"},{title:"Excel",name:"toExcel"},{title:"PDF",name:"toPDF"},{title:"Word",name:"toWord"},{title:"Plain text",name:"toText"}]},{name:"functions",type:"Menu"},"separator",{name:"exit",index:0}]});
isc.QueryToolBar.addMethods({initialize:function(){this.Super("initialize",arguments);
var a=this.getBindTable();
a.setMode(isc.Table.ModeTypes.Query);
a.bindListener(this)
},getBindTable:function(){return this.service.dataSet.getNavigate()
},table_AfterLoad:function(a){this.updateStatus()
},table_DataStateChanged:function(a){this.updateStatus()
},itemClick:function(a){var b=this.getBindTable();
switch(a.name){case"exit":this.service.close();
break;
case"first":b.firstPage();
break;
case"prior":b.priorPage();
break;
case"next":b.nextPage();
break;
case"last":b.lastPage();
break;
case"refresh":b.dataSet.load();
break;
case"remove":this.service.remove(b);
break;
default:this.Super("itemClick",arguments)
}},updateStatus:function(){var b=[];
var a=[];
var c=this.getBindTable();
if(c.totalSize==0){b[b.length]="first";
b[b.length]="prior";
b[b.length]="next";
b[b.length]="last";
b[b.length]="view";
b[b.length]="exportdoc"
}else{if(c.pageNum>1){a[a.length]="first";
a[a.length]="prior"
}else{b[b.length]="first";
b[b.length]="prior"
}if(c.pageNum<c.totalPageCount){a[a.length]="next";
a[a.length]="last"
}else{b[b.length]="next";
b[b.length]="last"
}a[a.length]="view";
a[a.length]="exportdoc"
}if(c.getCurrentRow()!=null){a[a.length]="detail";
a[a.length]="remove"
}else{b[b.length]="detail";
b[b.length]="remove"
}this.setButtonState(b,a)
}});
isc.ClassFactory.defineClass("EditToolBar","ToolStripBar");
isc.EditToolBar.addProperties({DefaultDataProperties:[{name:"first",title:isc.i18nButton.firstRow},{name:"prior",title:isc.i18nButton.priorRow},{name:"next",title:isc.i18nButton.nextRow},{name:"last",title:isc.i18nButton.lastRow},"separator",{name:"refresh"},{name:"add",permission:"create"},{name:"save",permission:["save","create"]},{name:"remove",permission:"delete"},{name:"exportdoc",permission:"export",type:"Menu",data:[{title:"XML",name:"toXml"},{title:"CSV",name:"toCSV"},{title:"Excel",name:"toExcel"},{title:"PDF",name:"toPDF"},{title:"Word",name:"toWord"},{title:"Plain text",name:"toText"}]},{name:"functions",type:"Menu"},"separator",{name:"exit",index:0}]});
isc.EditToolBar.addMethods({initialize:function(){this.Super("initialize",arguments);
var a=this.getBindTable();
a.setMode(isc.Table.ModeTypes.MasterEdit);
a.bindListener(this)
},getBindTable:function(){return this.service.dataSet.getNavigate()
},table_AfterLoad:function(a){this.updateStatus()
},table_DataStateChanged:function(a){this.updateStatus()
},exitClick:function(){var b=true;
var a=this.service;
if(this.getBindTable().isChanging()){isc.ask(isc.i18nAsk.promptDataState,function(c){if(c){a.close()
}});
b=false
}return b
},itemClick:function(a){var b=this.getBindTable();
switch(a.name){case"exit":if(this.exitClick()){this.service.close()
}break;
case"first":b.firstPage();
break;
case"prior":b.priorPage();
break;
case"next":b.nextPage();
break;
case"last":b.lastPage();
break;
case"refresh":b.dataSet.load();
break;
case"save":b.save();
break;
case"add":b.createNewRow();
b.startRow=b.totalSize+1;
break;
case"remove":this.service.remove(b);
break;
default:this.Super("itemClick",arguments)
}},setBrowseStatus:function(b,a){var c=this.getBindTable();
if(c.totalSize==0){b[b.length]="first";
b[b.length]="prior";
b[b.length]="next";
b[b.length]="last";
b[b.length]="remove";
b[b.length]="exportdoc"
}else{if(c.startRow>1){a[a.length]="first";
a[a.length]="prior"
}else{b[b.length]="first";
b[b.length]="prior"
}if(c.startRow<c.totalSize){a[a.length]="next";
a[a.length]="last"
}else{b[b.length]="next";
b[b.length]="last"
}a[a.length]="remove";
a[a.length]="exportdoc"
}b[b.length]="save";
a[a.length]="add"
},setModifyStatus:function(b,a){var c=this.getBindTable();
b[b.length]="first";
b[b.length]="prior";
b[b.length]="next";
b[b.length]="last";
b[b.length]="remove";
b[b.length]="exportdoc";
b[b.length]="remove";
if(c.isChanging()){a[a.length]="save";
b[b.length]="add"
}else{b[b.length]="save";
a[a.length]="add"
}},updateStatus:function(){var b=[];
var a=[];
var c=this.getBindTable();
if(c.dataState==isc.Table.DataStates.Browse||c.isReadOnly()){this.setBrowseStatus(b,a)
}else{this.setModifyStatus(b,a)
}this.setButtonState(b,a)
}});
isc.ClassFactory.defineClass("QueryStatusBar","StatusBar");
isc.QueryStatusBar.addProperties({data:[{name:"pageSize",type:"form",width:140,fields:[{type:"select",name:"pageSize",width:60,valueMap:pageSizeValueMap,changed:function(b,a,c){b.table.setPageSize(c)
}}]},{name:"pageCount",type:"form",width:150,fields:[{type:"StaticText",name:"pageCount",width:80}]},{name:"totalSize",type:"form",width:140,fields:[{type:"StaticText",name:"totalSize",width:80}]},{name:"content",width:"*"},{name:"pageNum",type:"form",width:120,fields:[{editorType:"Text",mask:"##########",maskPromptChar:"",name:"pageNum",width:60,textAlign:"right",blur:function(b,a){b.table.setPageNum(a._value)
}}]},{name:"totalPageCount",type:"form",width:120,fields:[{type:"StaticText",name:"totalPageCount",width:60}]}]});
isc.QueryStatusBar.addMethods({initialize:function(){this.Super("initialize",arguments);
var a=this.getBindTable();
a.bindListener(this);
this.pageSize.table=a;
this.pageNum.table=a
},getBindTable:function(){return this.service.dataSet.getNavigate()
},table_AfterLoad:function(a){this.updateStatus()
},table_DataStateChanged:function(a){this.updateStatus()
},updateStatus:function(){var c=this.getBindTable();
var b=["pageSize","pageNum","totalSize","totalPageCount"];
for(var a=0;
a<b.length;
a++){var d=b[a];
this[d].setValue(d,c[d])
}this["pageCount"].setValue("pageCount",c.getPageCount());
if(c.totalSize==0){this["pageSize"].disable();
this["pageNum"].disable()
}else{this["pageSize"].enable();
this["pageNum"].enable()
}}});
isc.ClassFactory.defineClass("DataStatusBar","StatusBar");
isc.DataStatusBar.addProperties({data:[{name:"content",width:"*"},{name:"dataState",width:80}]});
isc.DataStatusBar.addMethods({initWidget:function(){this.loadingMessage="${loadingImage}&nbsp;"+isc.i18nStatus[isc.Table.DataStates.Loading];
this.Super("initWidget",arguments)
},initialize:function(){this.Super("initialize",arguments);
this.service.dataSet.bindListeners(this)
},getBindTable:function(){return this.service.dataSet.getNavigate()
},table_CurrentChanged:function(c,b,d){var a=d.getFieldErrors(b.name);
if(a){this.setError(a)
}else{if(b.hint){this.setHint(b.hint)
}else{if(b.title){this.setHint(b.title)
}else{this.setHint("")
}}}},table_AfterLoad:function(a){this.updateStatus()
},table_DataStateChanged:function(a){this.updateStatus()
},setHint:function(a){if(a==undefined||a==null){a="&nbps;"
}this.content.setContents(a)
},setError:function(a){if(a==undefined||a==null){a="&nbps;"
}this.content.setContents("<font color=red>"+a+"</font>")
},getLoadingMessage:function(){return this.loadingMessage==null?"&nbsp;":this.loadingMessage.evalDynamicString(this,{loadingImage:this.imgHTML(isc.Canvas.loadingImageSrc,isc.Canvas.loadingImageSize,isc.Canvas.loadingImageSize)})
},updateStatus:function(){var b=this.getBindTable();
if(b.dataState==isc.Table.DataStates.Loading){this.dataState.setContents(this.getLoadingMessage())
}else{if(b.isReadOnly()){this.dataState.setContents(isc.i18nStatus.readOnly)
}else{var a=isc.i18nStatus[b.dataState];
if(a==isc.i18nStatus.modify||a==isc.i18nStatus.insert){this.dataState.setContents("<font color=red>"+a+"</font>")
}else{if(a==isc.i18nStatus.browse){this.dataState.setContents("<font color=blue>"+a+"</font>")
}else{this.dataState.setContents(a)
}}}}}});
isc.ClassFactory.defineClass("EditStatusBar","DataStatusBar");
isc.EditStatusBar.addProperties({data:[{name:"totalSize",type:"form",width:140,fields:[{type:"StaticText",name:"totalSize",width:80}]},{name:"startRow",type:"form",width:140,fields:[{type:"StaticText",name:"startRow",width:80}]},{name:"content",width:"*"},{name:"dataState",width:60}]});
isc.EditStatusBar.addMethods({initialize:function(){this.Super("initialize",arguments);
var a=this.getBindTable();
this.totalSize.table=a;
this.startRow.table=a
},updateStatus:function(){this.Super("updateStatus",arguments);
var d=this.getBindTable();
var a=["totalSize","startRow"];
for(var b=0;
b<a.length;
b++){var c=a[b];
this[c].setValue(c,d[c])
}this.Super("updateStatus",arguments)
}});
isc.ClassFactory.defineClass("HomeForm","Layout");
isc.HomeForm.addProperties({autoDraw:false,width:"100%",height:"100%",columnSize:2});
isc.HomeForm.addMethods({initWidget:function(){this.addChild(this.getBackgroundLayout());
this.Super("initWidget",arguments)
},getData:function(){},getBackgroundLayout:function(){this.gridPanel=isc.DataTileGrid.create({tileHeight:350,data:this.getData(),columnSize:this.columnSize,tileConstructor:"PortletWindow",tileProperties:{autoSize:false,canDragReposition:false,canDragResize:false}});
return this.gridPanel
}});
isc.defineClass("PortletWindow","Window");
isc.PortletWindow.addProperties({autoDraw:false,autoSize:false,showShadow:false,headerIcon:"[SKIN]/button/view.png",animateMinimize:true,showFooter:false,timeStep:5000});
isc.PortletWindow.addMethods({initWidget:function(){this.headerIconDefaults.src=this.headerIcon;
this.Super("initWidget",arguments)
},setData:function(a){this.data=a;
this.setTitle(a.title);
this.createChart()
},loadData:function(){if(this.chart){this.chart.load(this.data[0].createData())
}},closeClick:function(){this.Super("closeClick",arguments);
this.chart.destroy();
delete this.chart
},createChart:function(){if(!$.defined(this.chart)){this.chart=isc.HighChart.create({border:"none",chartOptions:this.data.chartOptions,data:this.data.chartData});
this.addItem(this.chart);
var a=this;
window.setInterval(function(){a.loadData()
},this.timeStep)
}return this.chart
}});
isc.defineClass("ChartWindow","Window");
isc.ChartWindow.addProperties({autoDraw:false,autoSize:false,showShadow:false,headerIcon:"[SKIN]/button/view.png",showFooter:false,timeStep:5000});
isc.ChartWindow.addMethods({initWidget:function(){this.headerIconDefaults.src=this.headerIcon;
this.Super("initWidget",arguments)
},setData:function(b){var a=$.isArray(b)?b[0]:b;
if(a!=this.record){if($.defined(this.chart)){this.removeItem(this.chart);
this.chart.destroy();
delete this.chart
}this.setTitle(a.title);
this.record=a
}this.createChart()
},getRecord:function(){return $.isArray(this.record)?this.record[0]:this.record
},getChartOptions:function(){return this.service.owner.getChartOptions(this.getRecord())
},loadData:function(){return this.service.owner.getChartData(this.getRecord())
},createChart:function(){if(!$.defined(this.chart)){var a=this;
this.chart=isc.HighChart.create({border:"none",chartOptions:this.getChartOptions(),loadData:function(){return a.loadData()
},data:this.loadData()});
this.addItem(this.chart);
this.chart.click=function(b){a.click(b)
}
}return this.chart
},destroy:function(){if($.defined(this.service.owner.clearChart)){this.service.owner.clearChart(this.chart)
}if($.defined(this.chart)){this.chart.destroy()
}this.Super("destroy",arguments)
}});
isc.ClassFactory.defineClass("QueryForm","ModuleForm");
isc.QueryForm.addProperties({DefaultServiceProperties:{initialize:function(){this.Super("initialize",arguments);
this.dataSet.load()
},dataGrid_RowDoubleClick:function(b,a,d,c){if(this.toolBar.detail){this.toolBar.detail.click()
}return false
},toolBar_view_ItemClick:function(a,b){if(b.selected){this.mainLayout.showMember(this.dataFormLayout)
}else{this.mainLayout.hideMember(this.dataFormLayout)
}return true
},toolBar_config_ItemClick:function(a,c){var b=this.owner.config;
b.table.hiddenColumnNames=["rowNum"];
var d=this.module.jndiName;
isc.EditWindow.open(c,{table:b.table,data:b.data,success:function(){isc.PRCService.invoke(d,"config",this.service.resultData[0],function(e){b.data=e
})
}});
return true
},toolBar_filter_ItemClick:function(a,c){var b=isc.addProperties({bindTable:a.getBindTable()},this.owner.dataFilterProperties);
isc.DataFilterWindow.open(c,b);
return true
},toolBar_detail_ItemClick:function(a,b){var c=a.getBindTable();
isc.ModuleWindow.open(this,b.url,b.title,{dataFilter:c.filter,currentPosition:c.getCurrentPosition(),dataState:isc.Table.DataStates.Browse,success:function(){c.load()
}},{icon:b.icon});
return true
},toolBar_add_ItemClick:function(a,b){var c=a.getBindTable();
isc.ModuleWindow.open(this,b.url,b.title,{dataFilter:c.filter,currentPosition:c.totalSize+1,totalSize:c.totalSize,dataState:isc.Table.DataStates.Insert,success:function(){c.load()
}},{icon:b.icon});
return true
}},DefaultLayoutProperties:[{name:"toolBar",classType:isc.QueryToolBar},{classType:isc.HPanel,name:"mainLayout",properties:{resizeBarSize:2},members:[{classType:isc.VPanel,properties:{width:"70%",showResizeBar:false},members:[{name:"dataGrid",classType:isc.DataGrid},{name:"queryStatusBar",classType:isc.QueryStatusBar}]},{classType:isc.VPanel,name:"dataFormLayout",properties:{width:"30%"},members:[{name:"dataForm",classType:isc.DataForm},{name:"dataStatusBar",classType:isc.DataStatusBar}]}]}]});
isc.QueryForm.addMethods({initWidget:function(){this.initToolBar();
this.Super("initWidget",arguments)
},initComponent:function(){this.Super("initComponent",arguments);
if(this.toolBar.view){this.toolBar.view.click()
}},initToolBar:function(){if(this.detailUrl){var a=[{name:"add",url:this.detailUrl},{name:"detail",url:this.detailUrl}];
if(!this.toolBarProperties){this.toolBarProperties={}
}if(!this.toolBarProperties.overrideProperties){this.toolBarProperties.overrideProperties={}
}var b=this.toolBarProperties.overrideProperties;
if(!b.data){b.data=a
}else{b.data.push(a)
}}},getDataFormProperties:function(){if(this.dataFormProperties){return{name:"dataForm",classType:isc.DataForm}
}},getDataGridProperties:function(){if(this.dataGridProperties){return{name:"dataGrid",classType:isc.DataGrid}
}},getDataGridLayoutMembers:function(){var a=[];
var b=this.getDataGridProperties();
if(b){a.push(b)
}a.push({name:"queryStatusBar",classType:isc.QueryStatusBar});
return a
},getDataFormLayoutMembers:function(){var a=[];
var b=this.getDataFormProperties();
if(b){a.push(b)
}a.push({name:"dataStatusBar",classType:isc.DataStatusBar});
return a
},getLayoutProperties:function(){var a=this.getDataGridLayoutMembers();
var b=this.getDataFormLayoutMembers();
return[{name:"toolBar",classType:isc.QueryToolBar},{classType:isc.HPanel,name:"mainLayout",properties:{resizeBarSize:2},members:[{classType:isc.VPanel,properties:{width:"70%",showResizeBar:true,resizeBarTarget:"next"},members:a},{classType:isc.VPanel,name:"dataFormLayout",properties:{width:"30%"},members:b}]}]
}});
isc.ClassFactory.defineClass("EditForm","ModuleForm");
isc.EditForm.addClassProperties({DefaultServiceProperties:{table_AfterSave:function(a){this.result=true
},table_AfterDelete:function(a){this.result=true
}}});
isc.EditForm.addMethods({initComponent:function(){this.Super("initComponent",arguments);
var a=this.service.toolBar;
if($.defined(this.window)){this.window.onCloseClick=function(){return a.exitClick()
}
}var b=a.getBindTable();
b.startRow=this.currentPosition;
if(this.totalSize!=undefined&&this.totalSize!=null){b.totalSize=this.totalSize
}b.filter=this.dataFilter;
if(this.dataState==isc.Table.DataStates.Insert){b.createNewRow()
}else{this.service.dataSet.load()
}},getContentMembers:function(){var a=[];
if(this.contentMembers&&this.contentMembers.length>0){a.push(this.createTabs(this.contentMembers))
}else{var c=this.getDataFormProperties();
if(c){a.push(c)
}if(this.detailMember){a.push(this.detailMember)
}else{if(this.detailMembers&&this.detailMembers.length>0){a.push(this.createTabs(this.detailMembers))
}else{var b=this.getDataGridProperties();
if(b){a.push(b)
}}}}return a
},createTabs:function(b){var a={name:"layoutTab",classType:isc.TabLayout,members:b};
return a
},getDataFormProperties:function(){if(this.dataFormProperties){return{name:"dataForm",classType:isc.DataForm,properties:{height:"*"}}
}},getDataGridProperties:function(){if(this.dataGridProperties){return{name:"dataGrid",classType:isc.DataGrid}
}},getLayoutProperties:function(){return[{name:"toolBar",classType:isc.EditToolBar},{classType:isc.VPanel,members:this.getContentMembers()},{name:"dataStatusBar",classType:isc.EditStatusBar}]
}});
isc.ClassFactory.defineClass("DataFilterWindow","DialogWindow");
isc.DataFilterWindow.addProperties({width:600,height:400,buttons:[isc.ToolButton.ButtonTypes.Ok,isc.ToolButton.ButtonTypes.Reset,isc.ToolButton.ButtonTypes.Cancel],DefaultServiceProperties:{initialize:function(){this.Super("initialize",arguments);
this.initFilterData()
},initFilterData:function(){var b=this.dataSet.dataFilter;
b.setMode(isc.Table.ModeTypes.Temp);
var c=b.createDefaultRow();
var a=isc.addProperties(c,this.owner.bindTable.filter);
b.setData(a)
},toolBar_ok_ItemClick:function(a,c){this.close();
var d=this.dataSet.dataFilter;
var b=d.getCurrentRow();
this.owner.bindTable.setFilter(b.saveData());
return true
},toolBar_reset_ItemClick:function(a,b){this.dataSet.dataFilter.createNewRow();
return true
}},DefaultLayoutProperties:[{name:"dataForm",classType:isc.DataForm,properties:{tableName:"dataFilter",hitRequiredFields:false}}]});
isc.ClassFactory.defineClass("ChangePasswordWindow","DialogWindow");
isc.ChangePasswordWindow.addProperties({width:600,height:160,methodName:"changePassword",buttons:[isc.ToolButton.ButtonTypes.Ok,isc.ToolButton.ButtonTypes.Cancel],DefaultServiceProperties:{initialize:function(){this.Super("initialize",arguments);
var a=this.dataSet.password;
a.setMode(isc.Table.ModeTypes.Temp);
a.createNewRow();
a.getCurrentRow().no=this.owner.no
},password_password2_OnFieldValidate:function(b,a,d,c){if(c&&d.password1!=c){return isc.i18nValidate.PasswordNotEquals
}},toolBar_ok_ItemClick:function(b,d){var c=this.dataSet.password;
if(c.validate()){var e=c.saveData()[0];
var f={orgPassword:e.password,newPassword:e.password1};
var a=this;
c.invoke(this.owner.jndiName,this.owner.methodName,f,function(g){if(g&&g.value==true){isc.say(isc.i18nLogin.ChangeSuccess);
a.close()
}})
}return true
}},dataSetProperties:{password:{columns:{no:{title:isc.i18nLogin.No,name:"no",length:32,dataType:"string",readOnly:true},password:{title:isc.i18nLogin.orgPassword,name:"password",length:32,dataType:"string",password:"no",nullable:false},password1:{title:isc.i18nLogin.password1,name:"password1",length:32,dataType:"string",password:"no",nullable:false},password2:{title:isc.i18nLogin.password2,name:"password2",length:32,dataType:"string",password:"no",nullable:false}}}},DefaultLayoutProperties:[{name:"dataForm",classType:isc.DataForm,properties:{tableName:"password",colWidths1:["20%","80%"],hitRequiredFields:false}}]});
isc.ClassFactory.defineClass("ResetPasswordWindow","ChangePasswordWindow");
isc.ResetPasswordWindow.addProperties({height:143,methodName:"resetPassword",DefaultServiceProperties:{initialize:function(){this.Super("initialize",arguments);
var a=this.dataSet.password;
if(this.owner.encryptAlgorithm){a.columns.password1.encryptAlgorithm=this.owner.encryptAlgorithm;
a.columns.password2.encryptAlgorithm=this.owner.encryptAlgorithm
}else{delete a.columns.password1.encryptAlgorithm;
delete a.columns.password2.encryptAlgorithm
}a.setMode(isc.Table.ModeTypes.Temp);
a.createNewRow();
a.getCurrentRow().no=this.owner.no
},password_password2_OnFieldValidate:function(b,a,d,c){if(c&&d.password1!=c){return isc.i18nValidate.PasswordNotEquals
}},toolBar_ok_ItemClick:function(b,d){var c=this.dataSet.password;
if(c.validate()){var e=c.saveData()[0];
var f={no:$.encryptContent(this.owner.no),password:e.password1};
var a=this;
c.invoke(this.owner.jndiName,this.owner.methodName,f,function(g){if(g&&g.value==true){isc.say(isc.i18nLogin.ChangeSuccess);
a.close()
}})
}return true
}},dataSetProperties:{password:{columns:{no:{title:isc.i18nLogin.No,name:"no",length:32,dataType:"string",readOnly:true},password1:{title:isc.i18nLogin.password1,name:"password1",length:32,dataType:"string",password:"no",nullable:false},password2:{title:isc.i18nLogin.password2,name:"password2",length:32,dataType:"string",password:"no",nullable:false}}}}});
isc.ClassFactory.defineClass("ViewWindow","DialogWindow");
isc.ViewWindow.addProperties({width:800,height:600,buttons:[isc.ToolButton.ButtonTypes.Exit],DefaultServiceProperties:{initialize:function(){this.owner.dataSetProperties={overrideProperties:{viewTable:{readOnly:true}},viewTable:this.owner.table};
this.Super("initialize",arguments)
}},DefaultLayoutProperties:[{name:"dataForm",classType:isc.DataForm,properties:{tableName:"viewTable"}}]});
isc.ClassFactory.defineClass("EditWindow","DialogWindow");
isc.EditWindow.addProperties({width:600,height:300,buttons:[isc.ToolButton.ButtonTypes.Ok,isc.ToolButton.ButtonTypes.Exit],DefaultServiceProperties:{initialize:function(){this.owner.dataSetProperties={editTable:this.owner.table};
this.Super("initialize",arguments);
var a=isc.addProperties({},this.owner.data);
this.dataSet.editTable.setData(a);
this.dataSet.editTable.afterLoad()
},toolBar_ok_ItemClick:function(a,b){this.result=true;
this.resultData=this.dataSet.editTable.saveData();
this.close();
return true
}},DefaultLayoutProperties:[{name:"dataForm",classType:isc.DataForm,properties:{tableName:"editTable"}}]});
isc.ClassFactory.defineClass("UploadWindow","DialogWindow");
isc.UploadWindow.addProperties({width:600,height:150,buttons:[isc.ToolButton.ButtonTypes.Upload,isc.ToolButton.ButtonTypes.Exit],DefaultServiceProperties:{initialize:function(){this.Super("initialize",arguments);
this.dataForm.action=isc.PRCService.getBaseUrl(this.owner.action);
this.dataForm.getItem("fileName").fileSuffix=this.owner.fileSuffix
},initComponent:function(){this.Super("initComponent",arguments);
this.dataForm_ItemChanged()
},toolBar_upload_ItemClick:function(a,c){this.result=true;
var b=this.dataForm;
var d=b.getValue("fileName");
if(!d||d.isEmpty()){isc.say(isc.i18nValidate.FileNameEmpty);
return
}isc.ProcessWindow.open(c,{action:this.owner.action,onShown:function(){var e=this;
b.ajaxSubmit(function(f){if(!e.service.invokeStop){isc.say(f.info)
}});
e.service.beginInvoke()
}});
return true
},dataForm_ItemChanged:function(b,a,c){this.toolBar.upload.setDisabled(c?c.isEmpty():true)
}},DefaultLayoutProperties:[{name:"dataForm",classType:isc.DataForm,properties:{fields:[{name:"fileName",title:isc.i18nTitle.FileName,type:"upload"}]}}]});
isc.ClassFactory.defineClass("ProcessWindow","DialogWindow");
isc.ProcessWindow.addProperties({width:500,height:300,buttons:[isc.ToolButton.ButtonTypes.Stop,isc.ToolButton.ButtonTypes.Exit],DefaultServiceProperties:{initialize:function(){this.Super("initialize",arguments)
},initComponent:function(){this.dataGrid.initFields(this.dataGrid.fields);
this.Super("initComponent",arguments);
this.toolBar.stop.setDisabled(true)
},beginInvoke:function(){delete this.invokeStop;
this.invoking=true;
this.setInterval("queryInvokeProcess",2000);
this.toolBar.stop.setDisabled(false)
},endInvoke:function(){this.toolBar.stop.setDisabled(true);
delete this.invoking
},onClosing:function(b){if(this.invoking){var a=this;
isc.ask(isc.i18nAsk.stop,function(c){if(c){a.stopInvoke(b)
}})
}else{if(b){b()
}}},toolBar_stop_ItemClick:function(a,b){this.stopInvoke();
return true
},stopInvoke:function(a){this.endInvoke();
this.invokeStop=true;
this.queryInvokeProcess(a)
},queryInvokeProcess:function(d){var a=this;
var b=this.dataGrid;
var c={jndiName:this.owner.action};
if(this.invokeStop==true){c.cancel=true
}if(this.invoking){isc.PRCService.ajaxInvoke("GET","/invokeProcess",c,function(g){if(g){if(g.error){isc.warn(g.error);
a.endInvoke()
}else{if(isc.isA.Array(g)){b.setData(g);
var f=true;
for(var e=0;
e<g.length;
e++){if(!g[e].finished){f=false;
break
}}if(f){a.endInvoke()
}}}if(d){d()
}}})
}return this.invoking
}},DefaultLayoutProperties:[{name:"dataGrid",classType:isc.DataGrid,properties:{fields:[{name:"name",title:isc.i18nTitle.StepName,type:"text",width:100},{name:"process",title:isc.i18nTitle.Process,type:"float",logic:"percent"},{name:"status",title:isc.i18nTitle.Status,type:"image",align:"center",width:60,showValueIconOnly:true,valueIcons:{"1":"[SKIN]/../process/1.gif","2":"[SKIN]/../process/2.gif","3":"[SKIN]/../process/3.gif","4":"[SKIN]/../process/4.gif"}}]}}]});