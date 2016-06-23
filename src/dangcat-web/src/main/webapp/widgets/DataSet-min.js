isc.ClassFactory.defineClass("DataSet","Class");
isc.DataSet.addProperties({permission:["create","save"],service:null});
isc.DataSet.addMethods({initialize:function(){this.Super("initialize",arguments);
if(this.dataSetProperties!=undefined){this.tables=[];
for(var a in this.dataSetProperties){if(a=="overrideProperties"){this.overrideProperties=this.dataSetProperties.overrideProperties;
continue
}var b=isc.Table.create(this.dataSetProperties[a],isc.Service.DefaultComponentProperties,{name:a,dataSet:this});
this.tables.push(b);
this[a]=b;
b.initWidget();
b.initialize()
}}this.extendProperties();
this.bindListeners(this.service);
this.bindParent()
},bindParent:function(){for(var c=0;
c<this.tables.length;
c++){var d=this.tables[c];
if(d.childrenNames){for(var b=0;
b<d.childrenNames.length;
b++){var a=this[d.childrenNames[b]];
if(a){a.parent=d
}}}}},existsPermission:function(a){return this.service.existsPermission(a)
},load:function(){for(var b=0;
b<this.tables.length;
b++){this.tables[b].loading=true
}var a=this.getNavigate();
if(a){a.load()
}for(var b=0;
b<this.tables.length;
b++){var c=this.tables[b];
if(c.loading){c.load();
delete c.loading
}}},getNavigate:function(){if(!this.navigate){for(var a=0;
a<this.tables.length;
a++){if(this.tables[a].navigate==true){this.navigate=this.tables[a];
break
}}}if(!this.navigate&&this.tables.length>0){this.navigate=this.tables[0]
}return this.navigate
},changeDataState:function(b){if(b.isChanging()){var a=this.getNavigate();
if(a&&a!=b){if(a.dataState==isc.Table.DataStates.Browse){a.setDataState(isc.Table.DataStates.Modified)
}}}},contains:function(a){return this[a]!=undefined&&this[a]!=null
},bindListeners:function(a){for(var b=0;
b<this.tables.length;
b++){this.tables[b].bindListener(a)
}},setData:function(a,d){if(this.contains(a)){if(d){var e=this[a];
e.setData(d);
if(e.hasChildren()){var b;
if(isc.isA.Array(d)){if(d.length>0){b=d[0]
}}else{b=d
}for(var c=0;
c<e.childrenNames.length;
c++){var f=e.childrenNames[c];
if(!b){this.clear(f)
}else{this.setData(f,b[f])
}}}}else{this.clear(a)
}}},clear:function(b){if(this.contains(b)){var d=this[b];
d.setData([]);
if(d.hasChildren()){for(var c=0;
c<d.childrenNames.length;
c++){var b=d.childrenNames[c];
var a=this[b];
if(a){a.setData([])
}}}}},beforeSave:function(a){if(a==undefined||a==null){a=this.getNavigate().name
}if(this.contains(a)){var c=this[a];
if(c.beforeSave()==false){return false
}if(c.hasChildren()){for(var b=0;
b<c.childrenNames.length;
b++){if(this.beforeSave(c.childrenNames[b])==false){return false
}}}}},afterSave:function(a){if(!a){a=this.getNavigate().name
}if(this.contains(a)){var c=this[a];
if(!c.isEmpty()){c.afterSave();
if(c.hasChildren()){for(var b=0;
b<c.childrenNames.length;
b++){this.afterSave(c.childrenNames[b])
}}}}},hasErrors:function(a){if(!a){a=this.getNavigate().name
}if(this.contains(a)){var c=this[a];
if(c.hasErrors()){return true
}if(c.hasChildren()){for(var b=0;
b<c.childrenNames.length;
b++){if(this.hasErrors(c.childrenNames[b])){return true
}}}}return false
},afterLoad:function(a){if(!a){a=this.getNavigate().name
}if(this.contains(a)){var c=this[a];
c.afterLoad();
if(c.hasChildren()){for(var b=0;
b<c.childrenNames.length;
b++){this.afterLoad(c.childrenNames[b])
}}}},saveData:function(b){if(!b){b=this.getNavigate().name
}if(this.contains(b)){var f=this[b];
if(!f.isEmpty()){var e=f.saveData();
if(e){if(f.pageSize>1){return e
}var a=e[0];
if(f.hasChildren()){for(var d=0;
d<f.childrenNames.length;
d++){var b=f.childrenNames[d];
var c=this.saveData(b);
if(c){a[b]=c
}}}return a
}}}},destroy:function(){if(this.tables){for(var a=0;
a<this.tables.length;
a++){this.tables[a].destroy()
}}this.Super("destroy",arguments)
}});
isc.ClassFactory.defineClass("Table","Class");
isc.Table.addClassProperties({DataStates:{Loading:"loading",Insert:"Insert",Modified:"Modified",Browse:"Browse"},ModeTypes:{Query:"query",Temp:"temp",MasterEdit:"masterEdit",DetailEdit:"detailEdit"},Events:{DataStateChanging:"DataStateChanging",DataStateChanged:"DataStateChanged",RowDataStateChanging:"RowDataStateChanging",RowDataStateChanged:"RowDataStateChanged",CurrentRowChanged:"CurrentRowChanged",CurrentChanged:"CurrentChanged",BeforeSave:"BeforeSave",AfterSave:"AfterSave",BeforeUpdate:"BeforeUpdate",AfterUpdate:"AfterUpdate",BeforeView:"BeforeView",BeforeLoad:"BeforeLoad",AfterLoad:"AfterLoad",BeforeDelete:"BeforeDelete",AfterDelete:"AfterDelete",BeforeInsert:"BeforeInsert",AfterInsert:"AfterInsert",BeforeTableValidate:"BeforeTableValidate",AfterTableValidate:"AfterTableValidate",OnRowValidate:"OnRowValidate",OnFieldValidate:"OnFieldValidate",OnColumnVisibleChanged:"OnColumnVisibleChanged",OnColumnReadOnlyChanged:"OnColumnReadOnlyChanged",OnCheckReadOnly:"OnCheckReadOnly",OnFieldErrors:"OnFieldErrors",OnClearFieldErrors:"OnClearFieldErrors",OnFieldPickData:"OnFieldPickData",OnRowPickData:"OnRowPickData",OnFieldValueMap:"OnFieldValueMap",OnRowValueMap:"OnRowValueMap"},DefaultColumnsProperties:{initialize:function(){for(var a=0;
a<this.fieldNames.length;
a++){var b=this.getColumn(this.fieldNames[a]);
b.initialize()
}},getPrimaryKeys:function(){return this.primaryKeys
},getColumn:function(a){if(isc.isA.Number(a)&&a>=0&&a<this.length){a=this.fieldNames[a]
}return this[a]
},createField:function(e){var b=this.getColumn(e);
if(b&&isc.isA.Object(b)&&!isc.isA.Array(b)){var d={autoDraw:false,title:b.title,name:b.name,dataType:b.dataType};
if(!d.type){if(b.isReadOnly()){d.type="StaticText"
}else{if(b.isEnumField()){d.type="enum";
if(b.nullable!=false){d.allowEmptyValue=true
}}else{if(b.encrypt==true||b.password!=undefined){d.type="password"
}else{if(b.dataType=="string"){d.type="text"
}else{d.type=b.dataType
}}}}}if(b.dataType=="integer"){d.keyPressFilter="[0-9]"
}else{if(b.dataType=="float"){d.keyPressFilter="[0-9.]"
}}if(!b.isVisible()){d.hidden=true
}if(b.length!=undefined&&b.length!=0){d.length=b.length
}else{if(b.dataType=="date"||b.dataType=="datetime"){d.length=19
}else{if(b.dataType=="integer"||b.dataType=="float"){d.length=15
}else{if(b.dataType=="boolean"){if(d.type=="enum"){d.length=10
}}}}}if(b.nullable==false){d.required=true
}var c=["editorType","logic","frozen","autoFitWidth","min","max"];
for(var a=0;
a<c.length;
a++){if($.defined(b[c[a]])){d[c[a]]=b[c[a]]
}}if(b.fieldProperties){isc.addProperties(d,b.fieldProperties)
}return d
}},getCurrentField:function(){return this.currentField
},setCurrentField:function(b){if(!b){b=this.fieldNames[0]
}var a=this.getColumn(b);
if(a){this.currentField=b;
this.table.fireEvent(isc.Table.Events.CurrentChanged,a,this.table.getCurrentRow())
}},getFieldNames:function(){return this.fieldNames
},getFields:function(d){var a=[];
if(d&&!isc.isAn.Array(d)){d=[d]
}if(!d||d.length==0){d=this.getFieldNames()
}for(var b=0;
b<d.length;
b++){var c=this.createField(d[b]);
if(c!=null&&!(c.hidden==true)){a[a.length]=c
}}return a
},prepareLoad:function(d){var c=this.getFieldNames();
for(var a=0;
a<c.length;
a++){var b=this[c[a]];
b.prepareLoad(function(){if(d!=undefined&&d!=null){d()
}})
}}},DefaultColumnProperties:{initialize:function(){if(!$.defined(this.valueMap)){if(this.dataType=="boolean"&&(this.nullable!=false||this.isReadOnly())){this.valueMap=isc.i18nBooleanValueMap
}}if(this.table.hiddenColumnNames&&this.table.hiddenColumnNames.indexOf(this.name)!=-1){this.visible=false
}},setVisible:function(a){if(this.visible!=a){this.visible=a;
this.table.fireEvent(isc.Table.Events.OnColumnVisibleChanged,this)
}},hasPermission:function(){if(this.permission){return this.table.dataSet.existsPermission(this.permission)
}return true
},isVisible:function(d){if(!this.hasPermission()&&this.permissionReadOnly!=true){return false
}if(this.visible!=undefined&&!this.visible){return false
}if(d){var b=this["dataStateVisible"];
if(b){var c=isc.Table.DataStates.Browse;
if(d.dataState){c=d.dataState
}for(var a=0;
a<b.length;
a++){if(b[a]==c){return true
}}return false
}}return true
},isReadOnly:function(d){if(!this.hasPermission()&&this.permissionReadOnly==true){return true
}if(this.table.readOnly==true){return true
}if(this.readOnly!=undefined&&this.readOnly==true){return true
}if(d){var a=this["dataStateReadOnly"];
if(a){var c=isc.Table.DataStates.Browse;
if(d.dataState){c=d.dataState
}else{if(d.table&&d.table.dataState){c=d.table.dataState
}}for(var b=0;
b<a.length;
b++){if(a[b]==c){return true
}}}if(this.table.fireEvent(isc.Table.Events.OnCheckReadOnly,this,d)==true){return true
}}return false
},setReadOnly:function(a){if(this.readOnly!=a){this.readOnly=a;
this.table.fireEvent(isc.Table.Events.OnColumnReadOnlyChanged,this)
}},parseValue:function(b){if(this.dataType!="string"&&isc.isA.String(b)){if(this.dataType=="integer"){var a=parseInt(b);
b=isNaN(a)?null:a
}else{if(this.dataType=="float"){var a=parseFloat(b);
b=isNaN(a)?null:a
}else{if(this.dataType=="boolean"){b=Boolean.parseText(b)
}else{if(this.dataType=="datetime"||this.dataType=="date"){b=Date.parseValue(b,this.format)
}}}}}return b
},validateValue:function(b){if(this.nullable==false){if(!$.defined(b)){return this.title+isc.i18nValidate.NullAble
}if(isc.isA.String(b)&&b.isEmpty()){return this.title+isc.i18nValidate.NullAble
}}if(b!=undefined&&b!=null){if(isc.isA.String(b)&&b.trim().length>this.length){return this.title+isc.i18nValidate.MaxLength+this.length
}if($.defined(this.minValue)&&$.defined(this.maxValue)){if(b<this.minValue||b>this.maxValue){return this.title+isc.i18nValidate.InvalidRange+"["+this.minValue+", "+this.maxValue+"]"
}}if($.defined(this.minValue)&&b<this.minValue){return this.title+isc.i18nValidate.MinValue+""+this.minValue
}if($.defined(this.maxValue)&&b>this.maxValue){return this.title+isc.i18nValidate.MaxValue+""+this.maxValue
}var c=this.getValueMap();
if(c){if(!$.defined(this.fieldProperties)||this.fieldProperties.addUnknownValues!=true||this.fieldProperties.canBeUnknownValue!=true){if(!$.defined(c[""+b])){return this.title+isc.i18nValidate.InvalidValue
}}}if(this.logic){var a=LogicValidator.validate(this.logic,b);
if($.defined(a)){return a
}}}},validate:function(c,b){c.clearFieldErrors(this.name);
if(c.isVisible(this.name)){var a=this.validateValue(b);
if(a){c.addFieldErrors(this.name,a)
}else{a=this.table.fireEvent(isc.Table.Events.OnFieldValidate,this,c,b);
if(a){c.addFieldErrors(this.name,a)
}}return a
}},isEnumField:function(){return this.valueMap!=undefined||this.enumField==true||this.fieldValueMapJndiName!=undefined||this.rowValueMapJndiName!=undefined
},getValueMap:function(b){var a=this;
if(this.fieldProperties&&this.fieldProperties.pickListFields){this.getPickData(function(){if(b){b(a.valueMap)
}})
}if(!this.isEnumField()){return
}if(!this.valueMap){this.valueMap=this.table.fireEvent(isc.Table.Events.OnFieldValueMap,a,function(c){if(c){a.valueMap=c;
if(b){b(c)
}}})
}if(!this.valueMap){if(this.fieldValueMapJndiName!=undefined&&(!$.defined(a.executeSelectDateTime)||(new Date()-a.executeSelectDateTime)>10000)){a.executeSelectDateTime=new Date();
isc.PRCService.select(this.fieldValueMapJndiName,undefined,function(c){if(c){a.valueMap=c;
delete a.executeSelectDateTime;
if(b){b(c)
}}})
}}return this.valueMap
},getPickData:function(b){var a=this;
if(!this.pickData){this.pickData=this.table.fireEvent(isc.Table.Events.OnFieldPickData,a,function(c){if(c){a.setPickData(c);
if(b){b(c)
}}})
}if(!this.pickData){if(this.fieldPickDataJndiName!=undefined&&(!$.defined(a.executePickDateTime)||(new Date()-a.executePickDateTime)>10000)){a.executePickDateTime=new Date();
isc.PRCService.query(this.fieldPickDataJndiName,undefined,function(c){if(c){a.setPickData(c);
delete a.executePickDateTime;
if(b){b(c)
}}})
}}return this.pickData
},setPickData:function(c){this.pickData=c;
if(c&&this.fieldProperties&&this.fieldProperties.displayField){var f={},b=this.fieldProperties.valueField,d=this.fieldProperties.displayField;
for(var a=0;
a<c.length;
a++){var e=c[a];
f[e[b]+""]=e[d]
}this.valueMap=f
}},prepareLoad:function(a){if(this.fieldValueMapJndiName){delete this.valueMap
}if(this.fieldPickDataJndiName){delete this.pickData
}this.getValueMap(a);
this.getPickData(a)
}},DefaultRowProperties:{addFieldErrors:function(e,d){if(this.errors){for(var b=0;
b<this.errors.length;
b++){if(this.errors[b].error==d&&this.errors[b].fieldName==e){return
}}}var a={error:d};
if(this.errors){this.errors.push(a)
}else{this.errors=[a]
}if(e){a.fieldName=e;
var c=this.table.columns.getColumn(e);
this.table.fireEvent(isc.Table.Events.OnFieldErrors,c,this,d)
}},setDataState:function(a){if(this.dataState!=a){this.table.fireEvent(isc.Table.Events.RowDataStateChanging,this,this.dataState,a);
this.dataState=a;
if(this.table.isChanging(a)){if(this.table.dataState==isc.Table.DataStates.Browse){this.table.setDataState(a)
}}this.table.fireEvent(isc.Table.Events.RowDataStateChanged,this,this.dataState)
}},getFieldErrors:function(b){if(this.errors){for(var a=this.errors.length-1;
a>=0;
a--){if(this.errors[a].fieldName==b){return this.errors[a].error
}}}return null
},hasErrors:function(a){if(a){return this.getFieldErrors(a)!=undefined
}return this.errors&&this.errors.length>0
},validate:function(){this.clearFieldErrors();
var c=this.table.columns;
var e=c.getFieldNames();
for(var b=0;
b<e.length;
b++){var d=c[e[b]];
if(d&&d.isVisible()){d.validate(this,this[d.name])
}}var a=this.table.fireEvent(isc.Table.Events.OnRowValidate,this);
if(a){this.addFieldErrors(undefined,a)
}},clearFieldErrors:function(d){if(this.errors&&this.errors.length>0){if(d){var c=this.table.columns.getColumn(d);
if(c!=undefined&&c!=null){for(var b=this.errors.length-1;
b>=0;
b--){if(this.errors[b].fieldName==d){this.errors.splice(b,1)
}}if(this.errors.length==0){this.errors=null
}this.table.fireEvent(isc.Table.Events.OnClearFieldErrors,c,this)
}}else{var a=[];
for(var b=this.errors.length-1;
b>=0;
b--){var d=this.errors[b].fieldName;
var c=this.table.columns.getColumn(d);
if(c){a[a.length]=c
}else{a[a.length]=undefined
}}this.errors=null;
for(var b=0;
b<a.length;
b++){var c=a[b];
if(c){this.table.fireEvent(isc.Table.Events.OnClearFieldErrors,c,this)
}else{this.table.fireEvent(isc.Table.Events.OnClearFieldErrors,this)
}}}}},isValid:function(b){if(b){for(var a in b){if(b[a]!=this[a]){return false
}}}return true
},update:function(d,b){if(this.isReadOnly(d)){return false
}if(this[d]!=b){var c=this.dataState;
if(!c){c=this.table.dataState
}if(!c||c==isc.Table.DataStates.Browse){c=isc.Table.DataStates.Modified
}if(this.table.isChanging(c)){var a=this.table.columns.getColumn(d);
if(this.table.fireEvent(isc.Table.Events.BeforeUpdate,a,this,b)==false){return false
}var b=a.parseValue(b);
a.validate(this,b);
this[d]=b;
this.setDataState(c);
this.table.fireEvent(isc.Table.Events.AfterUpdate,a,this,b);
return true
}}},saveData:function(){var e=null;
var f=this.table.columns.getFieldNames();
for(var b=0;
b<f.length;
b++){var c=this.table.columns[f[b]];
var d=this[c.name];
if(d!=undefined&&d!=null){if(e==null){e={}
}if(isc.isA.String(d)){d=d.trim()
}var a=c.parseValue(d);
if(a!=undefined&&a!=null){if(isc.isA.Date(a)){a=a.format()
}else{if(c.password){var g=this[c.password];
if(g==undefined||g==null){g=""
}a=$.encryptPassword(g,a,c.encryptAlgorithm)
}else{if(c.encrypt==true){a=$.encryptContent(a)
}}}}e[c.name]=a
}}if(e&&this.dataState&&this.dataState!=isc.Table.DataStates.Browse){e.dataState=this.dataState
}return e
},parseData:function(d){if(d!=undefined&&d!=null){var e=this.table.columns.getFieldNames();
for(var a=0;
a<e.length;
a++){var f=e[a];
var c=d[f];
if(c!=undefined){var b=this.table.columns[f];
c=b.parseValue(c);
if(c!=undefined&&c!=null){if(b.encrypt==true){c=$.decryptContent(c)
}else{if(isc.isA.Boolean(c)&&b.nullable!=false){c=c?c:false
}}}}this[f]=(c==undefined)?null:c
}}},getPrimaryKeyValues:function(){var a=this.table.columns.getPrimaryKeys();
if(a.length==1){var d=this[a[0].name];
if(d==undefined||d==null){return
}return d
}var e={};
for(var b=0;
b<a.length;
b++){var c=a[b];
if(!this[c.name]){return
}e[c.name]=this[c.name]
}return e
},getValueMap:function(e,c){var b=this.table.columns[e];
if(!b){return
}var a=this;
var d=b.getValueMap(c);
if(!d){d=this.table.fireEvent(isc.Table.Events.OnRowValueMap,b,a,c)
}return d
},getPickData:function(e,d){var c=this.table.columns[e];
if(!c){return
}var b=this;
var a=c.getPickData(d);
if(!a){a=this.table.fireEvent(isc.Table.Events.OnRowPickData,c,b,d)
}return a
},isVisible:function(b){var a=this.table.columns[b];
if(a){return a.isVisible(this)
}return true
},isReadOnly:function(b){var a=this.table.columns[b];
if(a){return a.isReadOnly(this)
}},format:function(e,c){if(c!=undefined&&c!=null){var a=this.table.columns[e];
if(a){if(a.isEnumField()){var d=this.getValueMap(e);
if(d&&d[c]!=undefined){c=d[c]
}}else{var b=this[e];
if(b!=undefined&&b!=null&&b.format!=undefined){c=b.format(a.format)
}}}}if(c==undefined||c==null){return"&nbsp;"
}return c
}},DefaultCreateProperties:{createDefaultRow:function(){var c=isc.addProperties({},this.defaultRowValues);
for(var a=0;
a<this.columns.length;
a++){var b=this.columns.getColumn(a);
if(c[b.name]==undefined){c[b.name]=null
}}return c
},onCreateSuccess:function(a,b){if(a){if(!a.data){a.data=this.createDefaultRow()
}this.dataSet.setData(this.name,a);
this.afterInsert()
}if(b){b(a)
}},afterInsert:function(){if(this.hasChildren()){for(var b=0;
b<this.childrenNames.length;
b++){var a=this.dataSet[this.childrenNames[b]];
a.fireEvent(isc.Table.Events.AfterInsert)
}}this.setDataState(isc.Table.DataStates.Insert);
this.fireEvent(isc.Table.Events.AfterInsert)
},beforeInsert:function(){if(this.hasChildren()){for(var b=0;
b<this.childrenNames.length;
b++){var a=this.dataSet[this.childrenNames[b]];
if(a.fireEvent(isc.Table.Events.BeforeInsert)==false){return false
}}}return this.fireEvent(isc.Table.Events.BeforeInsert)
},createNewRow:function(e,a){if(!this.isCreateAble()){return
}if(this.beforeInsert()==false){if(a!=undefined&&a!=null){a()
}return
}if(this.isCache()){var b=this.createDefaultRow();
var d=this.parseRow(b);
this.addRow(d);
this.setCurrentRow(d);
d.setDataState(isc.Table.DataStates.Insert);
this.fireEvent(isc.Table.Events.AfterInsert,d);
if(e){e(d)
}return d
}else{var c=this;
isc.PRCService.createNew(this.getJndiName(),function(f){c.onCreateSuccess(f,e)
},function(f,h,g){c.responseErrors(f,h,g,a)
})
}},add:function(c,b,a){if(!this.isCreateAble()){return
}if(this.fireEvent(isc.Table.Events.BeforeInsert)==false){if(a){a()
}return
}this.addRow(c);
isc.addProperties(c,{table:this},isc.Table.DefaultRowProperties);
this.setCurrentRow(c);
c.setDataState(isc.Table.DataStates.Insert);
this.fireEvent(isc.Table.Events.AfterInsert,c);
if(b){b(c)
}return c
},addRow:function(a){if(a!=null){if(this.isEmpty()||this.pageSize==1){this.rows=[a]
}else{this.rows.push(a)
}}}},DefaultLoadProperties:{createFilter:function(a){var d={pageSize:this.pageSize,startRow:this.startRow};
if(this.relation&&this.relation.getFilter){var c=this.dataSet[this.relation.parentName];
if(c){var b=this.relation.getFilter(c);
if(b){isc.addProperties(d,b)
}}}if(this.filter){isc.addProperties(d,this.filter)
}if(a){isc.addProperties(d,a)
}return d
},setFilter:function(a){this.filter=a;
this.load()
},load:function(b,f,a){if(this.relation){var d=this.dataSet[this.relation.parentName];
if(!d||d.isEmpty()){this.onLoadSuccess({data:[]},f);
return
}}this.setDataState(isc.Table.DataStates.Loading);
this.beforeLoad();
var e=this.createFilter(b);
var c=this;
if(this.isLocaleLoad()){this.getDataSource().fetchData(e,function(k,j,g){c.setTotalSize(j.length);
var l=[];
for(var h=c.startRow;
h<c.startRow+c.pageSize&&h<=j.length;
h++){l[l.length]=j[h-1]
}c.onLoadSuccess({data:l},f)
})
}else{isc.PRCService.query(this.getJndiName(),e,function(g){if(g){if(g.startRow!=undefined){c.startRow=g.startRow
}if(g.totalSize!=undefined){c.totalSize=g.totalSize
}var h=c.getViewId(g);
if(h!=undefined&&h!=null){c.view(h,f,a)
}else{c.onLoadSuccess(g,f)
}}},function(g,i,h){c.responseErrors(g,i,h,a)
})
}},getViewId:function(d){if(this.mode==isc.Table.ModeTypes.MasterEdit&&d&&d.data){if(isc.isA.Array(d.data)&&d.data.length==1){var e=d.data[0];
var b=this.columns.getPrimaryKeys();
if(b){if(b.length==1&&b[0].dataType=="integer"){return e[b[0].name]
}var f={};
for(var a=0;
a<b.length;
a++){var c=b[a];
f[c.name]=e[c.name]
}return f
}}}},view:function(f,d,b){var c=this;
var e=this.viewJndiName?this.viewJndiName:this.getJndiName();
this.fireEvent(isc.Table.Events.BeforeView,f);
if(isc.isA.Number(f)){isc.PRCService.view(e,f,function(g){c.onViewSuccess(g,d)
},function(g,i,h){c.responseErrors(g,i,h,b)
})
}else{var a=this.viewJndiName?undefined:"view";
isc.PRCService.invoke(e,a,f,function(g){c.onViewSuccess(g,d)
},function(g,i,h){c.responseErrors(g,i,h,b)
})
}},onLoadSuccess:function(a,b){if(a){this.checkResponse(a);
this.dataSet.setData(this.name,a.data)
}if(b){b(a)
}this.dataSet.afterLoad(this.name)
},onViewSuccess:function(a,b){if(a){this.checkResponse(a);
this.dataSet.setData(this.name,a)
}if(b){b(a)
}this.dataSet.afterLoad(this.name)
},beforeLoad:function(){var d=this;
this.columns.prepareLoad();
this.fireEvent(isc.Table.Events.BeforeLoad);
if(this.hasChildren()){for(var c=0;
c<this.childrenNames.length;
c++){var a=this.dataSet[this.childrenNames[c]];
a.beforeLoad()
}}var b=this.getCurrentRow();
if($.defined(b)){this.previousPrimaryKeyValues=b.getPrimaryKeyValues()
}else{delete this.previousPrimaryKeyValues
}delete d.loading
},afterLoad:function(){if(this.hasChildren()){for(var b=0;
b<this.childrenNames.length;
b++){var a=this.dataSet[this.childrenNames[b]];
if(a.isLocaleLoad()){a.load()
}}}for(var b=0;
b<this.dataSet.tables.length;
b++){var c=this.dataSet.tables[b];
if(c.relation&&c.relation.parentName==this.name){c.load()
}}this.dataState=isc.Table.DataStates.Browse;
this.fireEvent(isc.Table.Events.AfterLoad)
}},DefaultLocalDataProperties:{getDataSource:function(a){if(!this.dataSource){this.dataSource=isc.DataSource.create({ID:this.name,fields:a,dataFormat:"json",dataURL:this.dataURL})
}return this.dataSource
},isLocaleLoad:function(){return this.dataURL&&this.getDataSource().dataURL
}},DefaultRemoveProperties:{remove:function(g,f,c){if(!this.isRemoveAble()){return false
}var b;
if(!g){b=this.getCurrentRow()
}else{if(isc.isA.Number(g)){b=this.getCurrentRow(g)
}else{b=g
}}if(b){if(this.fireEvent(isc.Table.Events.BeforeDelete,b)==false){if(c!=undefined&&c!=null){c(b)
}return false
}var d=this;
var e=b.getPrimaryKeyValues();
if(e==undefined||e==null||this.isCache()){var a=this.removeRow(b);
this.fireEvent(isc.Table.Events.AfterDelete,b);
if(f!=undefined&&f!=null){f(b)
}return a
}else{isc.PRCService.remove(this.getJndiName(),e,function(h){d.onRemoveSuccess(h,f,b)
},function(h,j,i){d.responseErrors(h,j,i,c)
})
}}},onRemoveSuccess:function(b,c,a){if(b){if(this.checkResponse(b)){this.fireEvent(isc.Table.Events.AfterDelete,a);
this.load()
}}if(c){c(b)
}},removeRow:function(a){if(a){if(this.rows.remove(a)){if(this.dataState==isc.Table.DataStates.Browse){this.setDataState(isc.Table.DataStates.Modified)
}return true
}}return false
}},DefaultUpdateProperties:{update:function(e,c,b){if(this.isUpdateAble()){var d;
if(!b||isc.isA.Number(b)){d=this.getCurrentRow(b)
}else{if(b){d=b
}}if(d){var a=d.update(e,c);
if(a==false){return false
}}}}},DefaultSaveProperties:{beforeSave:function(){if(this.fireEvent(isc.Table.Events.BeforeSave,this)==false){return false
}if(!this.validate()){return false
}return !this.hasErrors()
},save:function(d,a){if(this.dataSet.beforeSave(this.name)==false){return
}var c=this.dataSet.saveData(this.name);
if(c&&!this.readOnly){var b=this;
this.columns.prepareLoad();
isc.PRCService.save(this.getJndiName(),c,this.isInsertState(),function(e){b.onSaveSuccess(e,d)
},function(e,g,f){b.responseErrors(e,g,f,a)
})
}},saveData:function(){if(!this.isEmpty()){var a=[];
for(var b=0;
b<this.getPageCount();
b++){var d=this.getCurrentRow(b);
var c=d.saveData();
if(c){a[a.length]=c
}}return a
}},onSaveSuccess:function(b,c){if(b){if(this.isErrorResponse(b)){this.checkResponse(b)
}else{this.responseInfoes(b);
this.dataSet.setData(this.name,b);
var a=this.checkResponse(b);
if(this.totalSize<this.startRow){this.totalSize=this.startRow
}this.dataSet.afterSave(this.name);
if(a&&c){c(b)
}}}},afterSave:function(){if(!this.dataSet.hasErrors(this.name)){this.setDataState(isc.Table.DataStates.Browse)
}this.fireEvent(isc.Table.Events.AfterSave)
}},DefaultInvokeProperties:{invoke:function(f,a,d,e,b){var c=this;
isc.PRCService.invoke(f,a,d,function(g){c.onInvokeSuccess(g,e)
},function(g,i,h){c.responseErrors(g,i,h,b)
})
},onInvokeSuccess:function(b,c){if(b){this.responseInfoes(b);
var a=this.checkResponse(b);
if(a&&c){c(b)
}}}},DefaultParseProperties:{parseRow:function(a){if(a){var b=this.createDefaultRow();
if(b.table==undefined){isc.addProperties(b,{table:this},isc.Table.DefaultRowProperties)
}b.parseData(a);
if(a.errors){b.errors=a.errors
}return b
}},parseRows:function(c){if(c&&isc.isA.Array(c)){this.rows=[];
for(var a=0;
a<c.length;
a++){var b=c[a];
if(b){var d=this.parseRow(b);
this.addRow(d);
if(b.dataState){d.setDataState(b.dataState)
}}}}}},DefaultNavigateProperties:{first:function(){this.setCurrentRow(0)
},firstPage:function(){this.startRow=1;
this.refresh()
},prior:function(){this.setCurrentRow(this.currentRowIndex-1)
},priorPage:function(){if(this.startRow>this.pageSize){this.startRow-=this.pageSize
}this.refresh()
},next:function(){this.setCurrentRow(this.currentRowIndex+1)
},nextPage:function(){var a=this.startRow-1+this.pageSize;
if(a<this.totalSize){this.startRow=this.startRow+this.pageSize
}this.refresh()
},last:function(){this.setCurrentRow(this.getPageCount()-1)
},lastPage:function(){this.calculateStartRow();
this.refresh()
},refresh:function(){this.calculate();
this.load();
this.first()
},calculateStartRow:function(){if(this.totalSize>this.pageSize){var a=this.totalSize;
var b=this.totalSize%this.pageSize;
if(b>0){a-=b
}else{a-=this.pageSize
}this.startRow=a+1
}else{this.startRow=1
}},setTotalSize:function(a){this.totalSize=a;
if(this.startRow>this.totalSize){this.calculateStartRow()
}this.calculate()
},calculate:function(){if(this.totalSize==0){this.pageNum=1;
this.totalPageCount=0;
this.currentRowIndex=-1
}else{this.pageNum=Math.floor((this.startRow-1)/this.pageSize)+1;
this.totalPageCount=Math.floor((this.totalSize-1)/this.pageSize)+1;
this.currentRowIndex=0
}},setPageNum:function(b){var a=this.pageSize*(b-1)+1;
if(a>0&&a<=this.totalSize){this.startRow=a
}this.refresh()
},setPageSize:function(a){this.pageSize=a;
this.startRow=Math.floor((this.startRow-1)/this.pageSize)*this.pageSize+1;
this.refresh()
},getPageCount:function(){if(!this.rows){return 0
}return this.rows.length
}},DefaultResponseProperties:{responseErrors:function(b,d,c,a){alert("status: "+b.status+"\r\ntextStatus: "+d+"\r\nerrorThrown: "+c);
if(a&&typeof(a)=="function"){a(responseData)
}},isErrorResponse:function(a){return a.id&&a.error
},checkResponse:function(e){var f=e.errors;
if(f){if(!isc.isA.Array(f)){f=[f]
}}else{if(e.error){f=[e]
}}var a;
if(f&&f.length>0){for(var d=0;
d<f.length;
d++){var c=f[d];
if(c.fieldName){var b=this.getCurrentRow();
if(b){b.addFieldErrors(c.fieldName,c.error)
}continue
}if(!a){a=""
}else{a+="\r\n"
}if(c.id){a+="("+c.id+")"
}a+=c.error
}}if(a){isc.warn(a)
}return !f||f.length==0
},responseInfoes:function(c){var a=c.infoes;
if(a){var d;
if(isc.isA.Array(a)){if(a.length>0){for(var b=0;
b<a.length;
b++){if(!d){d=""
}d+="("+a[b].id+")"+a[b].info+"<br>"
}}}else{d="("+a.id+")"+a.info
}isc.say(d)
}else{if(c.id!=undefined&&c.info){isc.say("("+c.id+")"+c.info)
}}}},DefaultPositionProperties:{setCurrentRow:function(b,c){if(b==undefined||b==null){b=this.currentRowIndex
}var a=b;
if(isc.isA.Number(b)){if(b<0){a=0
}else{if(b>=this.getPageCount()){a=this.getPageCount()-1
}}}else{a=this.indexOf(b)
}if(a>=0&&a<this.getPageCount()){this.currentRowIndex=a;
this.fireEvent(isc.Table.Events.CurrentRowChanged,this.getCurrentRow());
if(!c){c=this.currentField
}this.columns.setCurrentField(c)
}},indexOf:function(b,a){if(!b||this.isEmpty()){return
}if(isc.isA.Number(b)){if(b>=0&&b<this.rows.length){return b
}}else{if(a==undefined||a==null){a=0
}return this.rows.indexOf(b,a)
}},getCurrentPosition:function(){return this.startRow+this.currentRowIndex
},getCurrentColumn:function(){return this.columns.getColumn(this.columns.getCurrentField())
},getCurrentRow:function(b){var a=null;
if(b==undefined||b==null){b=this.currentRowIndex
}if(b>=0&&b<this.getPageCount()){a=this.rows[b]
}if(a!=null){if(!a.table){isc.addProperties(a,{table:this},isc.Table.DefaultRowProperties)
}}return a
},setCurrent:function(c,b){var a;
if(b&&isc.isA.Number(b)){a=this.getCurrentRow(b)
}else{if(!b){a=this.getCurrentRow()
}else{a=b
}}if(a){this.setCurrentRow(a,c)
}}},DefaultInitProperties:{initialize:function(){this.initColumns();
this.Super("initialize",arguments)
},initColumns:function(){var a=[];
var d=[];
for(var b in this.columns){var c=this.columns[b];
if(c.name&&c.dataType){d[d.length]=c.name;
if(c.primaryKey){a[a.length]=c
}isc.addProperties(c,{table:this},isc.Table.DefaultColumnProperties)
}}this.columns.primaryKeys=a;
this.columns.fieldNames=d;
this.columns.length=d.length;
isc.addProperties(this.columns,{table:this},isc.Table.DefaultColumnsProperties);
this.columns.initialize()
}},DefaultValidateProperties:{validate:function(){this.fireEvent(isc.Table.Events.BeforeTableValidate);
if(!this.isEmpty()){for(var a=0;
a<this.getPageCount();
a++){var b=this.getCurrentRow(a);
if(b){b.validate()
}}this.fireEvent(isc.Table.Events.AfterTableValidate)
}return !this.hasErrors()
}},DefaultDataStateProperties:{isInsertState:function(){return this.dataState==isc.Table.DataStates.Insert
},setDataState:function(a){if(this.dataState!=a){this.fireEvent(isc.Table.Events.DataStateChanging,this.dataState,a);
this.dataState=a;
this.fireEvent(isc.Table.Events.DataStateChanged,this.dataState);
this.dataSet.changeDataState(this)
}}},DefaultSearchProperties:{find:function(b){if(!this.isEmpty()&&b){var c=[];
for(var a=0;
a<this.rows.length;
a++){var d=this.rows[a];
if(d.isValid(b)){c.push(d)
}}if(c.length>0){return c
}}},locate:function(){if(!this.isEmpty()&&this.columns.primaryKeys.length>0){var d=arguments;
if(arguments.length==1&&$.isArray(arguments[0])){d=arguments[0]
}if(d.length>0){var c={};
for(var b=0;
b<this.columns.primaryKeys.length&&b<d.length;
b++){var e=this.columns.primaryKeys[b].name;
c[e]=d[b]
}var a=this.find(c);
if(a&&a.length>0){if(this.columns.primaryKeys.length==arguments.length){return a[0]
}return a
}}}}}});
isc.Table.addProperties({name:null,readOnly:false,dataState:isc.Table.DataStates.Browse,createAble:true,removeAble:true,updateAble:true,totalSize:0,startRow:1,currentRowIndex:-1,pageSize:50,pageNum:1,totalPageCount:0});
isc.Table.addMethods({initWidget:function(){this.addProperties(isc.Table.DefaultInitProperties);
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
this.Super("initWidget",arguments)
},isReadOnly:function(){if(this.parent){return this.parent.isReadOnly()
}if(this.dataState==isc.Table.DataStates.Insert){if(!this.dataSet.existsPermission("create")){return true
}}else{if(!this.dataSet.existsPermission("save")){return true
}}return this.readOnly
},isCreateAble:function(){var a=this.createAble;
if(a){if(this.parent){a=!this.isReadOnly()
}else{if(this.readOnly||!this.dataSet.existsPermission("create")){a=false
}}}return a
},isUpdateAble:function(){return !this.isReadOnly()&&this.updateAble
},isRemoveAble:function(){var a=this.removeAble;
if(a){if(this.parent){a=!this.isReadOnly()
}else{if(!this.dataSet.existsPermission("delete")){a=false
}}}return a
},setData:function(a){if(!isc.isA.Array(a)){a=[a]
}if(a!=this.rows){this.parseRows(a)
}this.calculate();
var b;
if(a.length>1&&$.defined(this.previousPrimaryKeyValues)){b=this.locate(this.previousPrimaryKeyValues)
}this.setCurrentRow(b)
},hasChildren:function(){return this.childrenNames
},isCache:function(){if(this.cache!=undefined&&this.cache!=null){return this.cache
}return this.pageSize>1
},getJndiName:function(){var a=this.jndiName;
if(!a){a=this.dataSet.service.module.jndiName
}return a
},isEmpty:function(){return this.getPageCount()==0
},hasErrors:function(){if(!this.isEmpty()){for(var a=0;
a<this.getPageCount();
a++){var b=this.getCurrentRow(a);
if(b&&b.hasErrors()){return true
}}}return false
},isChanging:function(a){if(!a){a=this.dataState
}return a==isc.Table.DataStates.Insert||a==isc.Table.DataStates.Modified
},setMode:function(a){if(a){if(a==isc.Table.ModeTypes.Query){this.readOnly=true;
this.cache=false
}else{if(a==isc.Table.ModeTypes.MasterEdit){this.readOnly=false;
this.pageSize=1;
delete this.cache
}else{if(a==isc.Table.ModeTypes.DetailEdit){this.readOnly=false;
this.cache=true
}else{if(a==isc.Table.ModeTypes.Temp){this.pageSize=1;
this.cache=true
}}}}this.mode=a;
this.columns.initialize()
}}});
isc.ClassFactory.defineClass("DataLoader","Class");
isc.DataLoader.addClassProperties({DefaultIndexProperties:{locate:function(){if(arguments.length==this.fieldNames.length){var a=this;
for(var b=0;
b<arguments.length;
b++){var c=arguments[b];
a=a[c+""];
if(!a){break
}}if(a){if(a.data){if(isc.isA.Array(a.data)){return a
}}}}},getValueMap:function(){if(this.idField&&this.nameField){var a=this.locate.apply(this,arguments);
if(a){if(a.valueMap){return a.valueMap
}var d={};
for(var b=0;
b<a.data.length;
b++){var c=a.data[b];
d[c[this.idField]]=c[this.nameField]
}a.valueMap=d;
return d
}}}}});
isc.DataLoader.addMethods({getDataSource:function(a){if(this.dataSource==undefined){this.dataSource=isc.DataSource.create({ID:this.name,fields:a,dataFormat:"json",dataURL:this.dataURL})
}return this.dataSource
},isLocaleLoad:function(){return this.dataURL&&this.getDataSource().dataURL
},load:function(b){var a=this;
if(this.isLocaleLoad()){this.getDataSource().fetchData(null,function(e,d,c){if(!d){d=[]
}a.setData(d);
b(d)
})
}else{isc.PRCService.query(this.jndiName,undefined,function(c){if(!c){c=[]
}a.setData(c);
b(c)
})
}},setData:function(a){this.data=a
},createIndex:function(){if(this.data&&arguments.length>0){var g=[];
for(var b=0;
b<arguments.length;
b++){var h=arguments[b];
if(h){g.push(h)
}}var f=isc.addProperties({fieldNames:g,idField:this.idField,nameField:this.nameField},isc.DataLoader.DefaultIndexProperties);
for(var d=0;
d<this.data.length;
d++){var e=this.data[d];
var a=f;
for(var b=0;
b<g.length;
b++){var h=g[b];
var c=e[h]+"";
if(!a[c]){a[c]={}
}a=a[c]
}if(!a.data){a.data=[e]
}else{a.data.push(e)
}}return f
}}});