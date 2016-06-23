Highcharts.theme={colors:["#058DC7","#50B432","#ED561B","#DDDF00","#24CBE5","#64E572","#FF9655","#FFF263","#6AF9C4"],chart:{backgroundColor:{linearGradient:{x1:0,y1:0,x2:1,y2:1},stops:[[0,"rgb(255, 255, 255)"],[1,"rgb(240, 240, 255)"]]},borderWidth:2,plotBackgroundColor:"rgba(255, 255, 255, .9)",plotShadow:true,plotBorderWidth:1},title:{style:{color:"#000",font:'bold 16px "Trebuchet MS", Verdana, sans-serif'}},subtitle:{style:{color:"#666666",font:'bold 12px "Trebuchet MS", Verdana, sans-serif'}},xAxis:{gridLineWidth:1,lineColor:"#000",tickColor:"#000",labels:{style:{color:"#000",font:"11px Trebuchet MS, Verdana, sans-serif"}},title:{style:{color:"#333",fontWeight:"bold",fontSize:"12px",fontFamily:"Trebuchet MS, Verdana, sans-serif"}}},yAxis:{minorTickInterval:"auto",lineColor:"#000",lineWidth:1,tickWidth:1,tickColor:"#000",labels:{style:{color:"#000",font:"11px Trebuchet MS, Verdana, sans-serif"}},title:{style:{color:"#333",fontWeight:"bold",fontSize:"12px",fontFamily:"Trebuchet MS, Verdana, sans-serif"}}},legend:{itemStyle:{font:"9pt Trebuchet MS, Verdana, sans-serif",color:"black"},itemHoverStyle:{color:"#039"},itemHiddenStyle:{color:"gray"}},labels:{style:{color:"#99b"}},navigation:{buttonOptions:{theme:{stroke:"#CCCCCC"}}}};
var highchartsOptions=Highcharts.setOptions(Highcharts.theme);
var DataModuleFactory={getDateTimeValue:function(a){if($.isArray(a)){return a[0]
}if($.isObject(a)&&$.defined(a.x)){return a.x
}},getPointValue:function(a){if($.isArray(a)){return a[1]
}if($.isObject(a)&&$.defined(a.y)){return a.y
}},DefaultDataModuleProperties:{getKeyValues:function(f,e){var a;
if(e){if(!$.isArray(e)){e=[e]
}for(var b=0;
b<e.length;
b++){var c=e[b];
if(c){var d=f[c.name];
if(d!=undefined&&d!=null){if(a){a+=" "+d
}else{a=d
}}}}}return a
},transValue:function(a){if(a==undefined||a==null){return this.nullValue?0:null
}else{if(a==0){return this.zeroValue?0:null
}}return a
}},DefaultCategoryDataModuleProperties:{getCategory:function(a){return this.getKeyValues(a,this.primaryKeyColumns)
},load:function(b){if($.defined(b)&&b.data){b=b.data
}var a;
if($.defined(b)&&$.defined(b.series)){a=b.series
}else{a=this.createSeries(b)
}if(!$.defined(this.options)){this.options={}
}this.options.series=a;
return this.options
}},DefaultColumnDataModuleProperties:{getLabel:function(a){if(a){return a.title?a.title:a.name
}},initSeries:function(b,a){var c=this.valueColumns[a];
if(c){$.extendInstance(b,c);
if(!$.defined(c.logic)&&this.logic){b.logic=this.logic
}if(!$.defined(b.data)){b.data=[]
}}}},CategoryColumnDataModuleProperties:{createSeries:function(d){var b={};
if(this.primaryKeyColumns){for(var a=0;
a<this.valueColumns.length;
a++){var c={name:this.getLabel(this.valueColumns[a])};
this.initSeries(c,a);
b[c.name]=c
}}else{var c={};
for(var a=0;
a<this.valueColumns.length;
a++){this.initSeries(c,a)
}c.name="default";
b[c.name]=c
}return this.loadData(d,b)
},loadData:function(e,f){var c,l,h,m,a;
if($.defined(e)){if($.isArray(e)){for(var g=0;
g<e.length;
g++){m=e[g];
for(var d=0;
d<this.valueColumns.length;
d++){c=this.valueColumns[d];
l=this.transValue(m[c.name]);
h=f[c.name];
a=this.getCategory(m);
h.data.push([a,l])
}}}else{if(f["default"]){for(var g=0;
g<this.valueColumns.length;
g++){c=this.valueColumns[g];
l=this.transValue(e[c.name]);
a=this.getLabel(c);
f["default"].data.push([a,l])
}}}}else{var b=this.transValue();
for(var k in f){for(var g=0;
g<this.valueColumns.length;
g++){c=this.valueColumns[g];
if(k=="default"){a=this.getLabel(c);
l=[a,b]
}else{l=b
}f[k].data.push(l)
}}}return f
}},DefaultRowDataModuleProperties:{getBaseTitle:function(){var a=this.valueColumns;
if(a&&a.length==1){var b=a[0];
return b.title?b.title:b.name
}},getLogic:function(){var a=this.valueColumns;
if(a&&a.logic){return a.logic
}return this.logic
},getSeriesValue:function(a,c){var b=this.getKeyValues(c,a);
if(!$.defined(b)){b="default"
}return b
}},CategoryRowDataModuleProperties:{initSeries:function(b){var a=this.getLogic();
if(a){b.logic=a
}var c=this.getBaseTitle();
if(a){b.baseTitle=c
}if(!$.defined(b.data)){b.data=[]
}return b
},createSeries:function(d){var b={};
if(this.categoryColumns){if($.isArray(d)){for(var a=0;
a<d.length;
a++){var c=this.getSeriesValue(this.categoryColumns,d[a]);
if(!$.defined(b[c])){b[c]=this.initSeries({name:c})
}}}}else{b["default"]=this.initSeries({name:"default"})
}return this.loadData(d,b)
},loadData:function(g,b){if($.isArray(g)){var c=this.valueColumn;
if(!$.defined(c)&&$.defined(this.valueColumns)){c=this.valueColumns[0]
}var h,e,d,f;
for(var a=0;
a<g.length;
a++){h=g[a];
if(h){e=this.getCategory(h);
d=this.getSeriesValue(this.categoryColumns,h);
f=this.transValue(h[c.name]);
if(!$.defined(b[d].data)){b[d].data=[]
}b[d].data.push([e,f])
}}}return b
}},DefaultTimeDataModuleProperties:{dateTimeFieldName:"dateTime",timeType:"Hour",getTimeRange:function(){if($.defined(this.timeType)){if(!$.defined(this.timeRange)){this.timeRange=new TimeRange(this.timeType,this.timePeriod)
}this.timeRange.calculate(this.currentTime)
}return this.timeRange
},getTimeStep:function(){var b=this.timeStep;
if(!$.defined(b)){var a=this.getTimeRange();
if($.defined(a)){b=a.timeStep
}}return b
},getBeginTime:function(){var b=this.beginTime;
if(!$.defined(b)){var a=this.getTimeRange();
if($.defined(a)){b=a.beginTime
}}return this.getDateTime(b)
},getEndTime:function(){var b=this.endTime;
if(!$.defined(b)){var a=this.getTimeRange();
if($.defined(a)){b=a.endTime
}}return this.getDateTime(b)
},getDateTime:function(b){if($.defined(b)){var a=Date.parseValue(b);
if(a){return a.getTime()
}}},createParams:function(a){if($.defined(this.timeType)){a.timeRange={timeType:this.timeType};
if($.defined(this.timePeriod)){a.timeRange.timePeriod=this.timePeriod
}}if($.defined(this.lastTime)){a.lastTime=this.lastTime
}},load:function(b){if(b){if(b.data){if($.defined(b.beginTime)){this.beginTime=b.beginTime
}if($.defined(b.endTime)){this.endTime=b.endTime
}if($.defined(b.timeStep)){this.timeStep=b.timeStep
}}var a;
if($.defined(b.markerData)){a=this.createSeriesData(b.markerData)
}this.loadData(b.data||b,a)
}else{this.loadData()
}},loadData:function(e,b){if(!$.defined(this.options)){this.options={series:{}};
this.createSeries(this.options.series,e)
}if($.defined(e)){if($.defined(e.series)){this.options.series=e.series
}else{var d=this.createSeriesData(e);
if($.defined(b)){this.processMarkerData(d,b)
}for(var a in d){this.sortSeriesData(d[a]);
var c=this.options.series[a];
if($.defined(c)){this.mergeSeriesData(c.data,d[a])
}else{this.createSeries(this.options.series,e);
this.options.series[a].data=d[a]
}}}this.fixSeriesTimeData(this.options.series)
}},sortSeriesData:function(a){a.sort(function(d,e){var c=DataModuleFactory.getDateTimeValue(d),b=DataModuleFactory.getDateTimeValue(e);
if(c>b){return 1
}else{if(c<b){return -1
}}return 0
})
},fixSeriesTimeData:function(f){if($.defined(this.lastTime)){delete this.lastTime
}var i,e,g=this.getTimeStep(),d=this.getBeginTime(),h=this.getEndTime(),k,a,c,b=this.transValue();
for(var j in f){i=0;
e=f[j].data;
while(i<e.length){a=DataModuleFactory.getDateTimeValue(e[i]);
if(a<d||a>h){e.splice(i,1);
continue
}if(i<e.length-1){c=DataModuleFactory.getDateTimeValue(e[i+1]);
k=c-a;
if(k>=g*2){e.splice(i,0,[a+g,b]);
i++;
if(k>=g*3){e.splice(i+1,0,[a+g,[c-g,b]]);
i++
}}}if(!$.defined(this.lastTime)){this.lastTime=a
}else{this.lastTime=Math.max(this.lastTime,a)
}i++
}}},mergeSeriesData:function(a,g){var c=0,b=0,e,f,h,d;
while(c<a.length&&b<g.length){e=a[c];
f=g[b];
h=DataModuleFactory.getDateTimeValue(e);
d=DataModuleFactory.getDateTimeValue(f);
if(h<d){c++
}else{if(h>d){a.splice(c,0,f);
b++
}else{a.splice(c,1,f);
c++;
b++
}}}while(b<g.length){a.push(g[b++])
}},processMarkerData:function(a,b){if(!$.defined(b)){return
}var g,j,d,e,f;
for(var h in a){g=b[h];
if($.defined(g)){e=0;
d=a[h];
for(var c=0;
c<d.length;
c++){j=d[c][0];
if(j<g[e][0]){continue
}if(j==g[e][0]){f=this.markerMap[g[e][1]+""];
if($.defined(f)){f.enabled=true;
d[c]={x:j,y:d[c][1],marker:f}
}e++
}if(e>=g.length){break
}}}}}},TimeColumnDataModuleProperties:{createSeries:function(b){for(var a=0;
a<this.valueColumns.length;
a++){var c=this.valueColumns[a];
var d=b[c.name];
if(!$.defined(d)){d={name:c.name,data:[]};
this.initSeries(d,a);
b[d.name]=d
}}},createSeriesData:function(c){var d={};
for(var e=0;
e<this.valueColumns.length;
e++){var a=this.valueColumns[e];
if(!$.defined(d[a.name])){d[a.name]=[]
}}if($.isArray(c)){for(var e=0;
e<c.length;
e++){var h=c[e];
var k=this.getDateTime(h[this.dateTimeFieldName]);
for(var b=0;
b<this.valueColumns.length;
b++){var f=this.valueColumns[b].name;
var g=this.transValue(h[f]);
d[f].push([k,g])
}}}return d
}},TimeRowDataModuleProperties:{createSeries:function(c,d){var a;
if($.defined(d)){for(var b=0;
b<d.length;
b++){a=this.createSeriesItem(c,d[b]);
if(a&&!$.defined(c[a.name])){c[a.name]=a
}}}},createSeriesItem:function(c,d){var b;
if(this.primaryKeyColumns){var a=this.getSeriesValue(this.primaryKeyColumns,d);
b=c[a];
if(!$.defined(b)){b={name:a,logic:this.getLogic(),data:[]}
}}else{b=c["default"];
if(!$.defined(b)){b={name:"default",logic:this.getLogic(),data:[]}
}}return b
},createSeriesItemData:function(d,b){var c,a;
if(this.primaryKeyColumns){a=this.getSeriesValue(this.primaryKeyColumns,d)
}else{a="default"
}if(!$.defined(b[a])){b[a]=[]
}return b[a]
},createSeriesData:function(g){var b={},d,c=this.valueColumn;
if($.isArray(g)){if(!$.defined(c)&&$.defined(this.valueColumns)){c=this.valueColumns[0]
}for(var a=0;
a<g.length;
a++){var h=g[a];
d=this.createSeriesItemData(h,b);
var f=this.getDateTime(h[this.dateTimeFieldName]);
var e=this.transValue(h[c.name]);
d.push([f,e])
}}return b
}},createDataModule:function(c){var d={};
var f=c.dataModuleType;
$.extendInstance(d,this.DefaultDataModuleProperties,this["Default"+f+"DataModuleProperties"]);
if($.defined(c.valueColumn)){$.extendInstance(d,this.DefaultRowDataModuleProperties,this[f+"RowDataModuleProperties"])
}else{$.extendInstance(d,this.DefaultColumnDataModuleProperties,this[f+"ColumnDataModuleProperties"])
}var a=["nullValue","zeroValue","dateTimeFieldName","valueColumn","valueColumns","categoryColumns","primaryKeyColumns","markerMap","markerData"];
for(var b=0;
b<a.length;
b++){var e=c[a[b]];
if($.defined(e)&&!$.defined(d[a[b]])){d[a[b]]=e
}}a=["logic","beginTime","endTime","timeStep","timeType","timePeriod"];
for(var b=0;
b<a.length;
b++){var e=c[a[b]];
if($.defined(e)){d[a[b]]=e
}}return d
}};
(function(d){var a=function(){if(this.options.stacking&&this.closedStacks){var m;
for(var k=this.index+1;
k<this.chart.series.length;
k++){if(this.chart.series[k].visible){m=this.chart.series[k].graphPath;
break
}}if(m){var h=[].concat(this.graphPath);
var f=[];
var n=m.length-1;
for(var k=n;
k>=0;
k--){var l=m[k];
if(typeof(l)!="number"){f.push(l);
if(l=="C"){f.push(m[k+3],m[k+4],m[k+1],m[k+2],m[k-2],m[k-1])
}else{for(var g=n;
g>k;
g-=2){f.push(m[g-1],m[g])
}}n=k-1
}}h.push("L",m[m.length-2],f[m.length-1]);
this.areaPath=h.concat(f)
}}};
d.wrap(d.Series.prototype,"drawGraph",function(f){f.apply(this,Array.prototype.slice.call(arguments,1));
if(this.chart.options.chart.type=="areaspline"){a.call(this)
}});
d.Axis.prototype.getDataFormator=function(){return DataFormatorFactory.getDataFormator(this.options.logic)
};
d.Axis.prototype.formatValue=function(f){if($.defined(this.transRate)){f*=this.transRate
}return f.format(this.pattern)
};
d.Axis.prototype.formatTime=function(f,h){var g=f;
if($.isNumber(f)){g=new Date();
g.setTime(f)
}return g.format(h||this.pattern||this.options.pattern)
};
d.Axis.prototype.formatData=function(g){var f=this.pattern?this.pattern:this.options.pattern;
return this.getDataFormator().format(g,f)
};
var e=function(){if(this.options.logic){var f=this.max;
if(Math.abs(this.min)>Math.abs(f)){f=Math.abs(this.min)
}var g=this.getDataFormator();
this.unit=g.calculatePerfectUnit(f);
if(this.options.initTitle){this.options.initTitle(this)
}this.transRate=g.calculateTransRate(this.unit);
if(this.axisTitle){this.axisTitle.attr("text",this.options.title.text)
}}};
var c=function(f){if(this.options.type=="datetime"){var g=Math.round((this.max-this.min)/1000);
var j=Math.round(g/60);
var h=Math.round(j/60);
var i=Math.round(h/24);
if(i>=1){if(i==365||i==366){f=60*24*3600000
}else{if(i>=28&&i<=31){f=6*24*3600000
}else{if(i==7){f=24*3600000
}else{if(i==1){f=4*3600000
}else{f=Math.round(h/6)*3600000
}}}}}else{if(h>1){f=Math.round(j/6)*60000
}else{if(h==1){f=10*60000
}else{if(j>=3){f=Math.round(g/6)*1000
}else{f=30*1000
}}}}delete this.lablesPattern;
if(f>=60*24*3600000){this.pattern="yyyy-MM";
this.lablesPattern="yyyy-MM-dd"
}else{if(f>24*3600000){if(f==7*24*3600000){this.pattern="dddd";
this.lablesPattern="yyyy-MM-dd"
}else{if(f>=6*24*3600000){this.pattern="MM-dd";
this.lablesPattern="MM-dd HH"
}else{this.pattern="MM-dd HH"
}}}else{if(f>3600000){this.pattern="MM-dd HH"
}else{if(f>60000){this.pattern="HH:mm"
}else{this.pattern="HH:mm:ss"
}}}}if(!$.defined(this.lablesPattern)){this.lablesPattern="MM-dd HH:mm"
}}return f
};
var b=function(g){if(this.options.logic){e.apply(this);
var f=this.max;
if(Math.abs(this.min)>Math.abs(f)){f=Math.abs(this.min)
}var j=(f-this.min)*this.transRate;
var l=1;
if(j!=0){while(j>10){j/=10;
l*=10
}while(j<1){j*=10;
l*=0.1
}}var i;
if(j>5){i=1
}else{if(j>2.5){i=0.5
}else{if(j>1){i=0.25
}else{i=0.1
}}}if(this.chart.chartHeight<300){i*=2
}var k=i*l;
if(k>=1){this.pattern="#"
}else{if(k>=0.1){this.pattern="#.0"
}else{if(k>=0.01){this.pattern="#.00"
}else{this.pattern="#.000"
}}}var h=Math.round(k/this.transRate);
if(h>0){g=h
}else{g=k
}this.options.tickInterval=g
}return g
};
d.Axis.prototype.postProcessTickInterval=function(f){if(this.isXAxis){f=c.apply(this,arguments)
}else{f=b.apply(this,arguments)
}return f
}
}(Highcharts));
var DefaultYAxisProperties={showEmpty:false,min:0,labels:{formatter:function(){return this.axis.formatValue(this.value)
}},initTitle:function(a){if(!$.defined(a.options.title)){a.options.title={text:"Values"}
}if(!$.defined(this.baseTitle)){this.baseTitle=a.options.title.text
}var b=this.baseTitle;
if(a.unit){b+="("+a.unit+")"
}a.options.title.text=b
}};
var DefaultSeriesProperties={};
var DefaultCategoryPlotOptionsEvents={click:function(b){var a={point:b.point,series:this,name:(b.point.name||b.point.category||this.chart.options.xAxis[0].categories[b.point.x])};
if(this.chart.options.itemClick){this.chart.options.itemClick(a)
}}};
var DefaultTimePlotOptionsEvents={click:function(b){var a={point:b.point,series:this,name:b.currentTarget.name};
if(this.chart.options.itemClick){this.chart.options.itemClick(a)
}}};
var DefaultChartProperties={chart:{animation:false,spacing:[10,5,2,2],borderWidth:0},stacked:false,exporting:{enabled:false,url:"/chartExport",buttons:{contextButton:{menuItems:[{textKey:"printChart",onclick:function(){this.print()
}},{separator:true},{textKey:"downloadPNG",onclick:function(){this.options.exportChart("png")
}},{textKey:"downloadPDF",onclick:function(){this.options.exportChart("pdf")
}}]}}},credits:{enabled:false},title:{text:""},createInstance:function(){this.clearHighChart();
this.highChart=new Highcharts.Chart(this)
},clearHighChart:function(){if(this.highChart){this.highChart.destroy();
delete this.highChart;
delete this.series
}},reRender:function(b,a){this.chart.width=b;
this.chart.height=a;
this.refresh()
},refresh:function(){if(this.highChart){this.highChart.reflow()
}},resize:function(b,a){this.chart.width=b;
this.chart.height=a;
if(this.highChart){this.highChart.setSize(b,a,false)
}},destroy:function(){this.clearHighChart();
if(this.dataModule){delete this.dataModule
}},initialize:function(){},legend:{margin:5,labelFormatter:function(){if(this.options.title){return this.options.title
}return this.name
}},getBaseTitle:function(){if(!$.defined(this.baseTitle)){if(this.valueColumns&&this.valueColumns.length==1){var a=this.valueColumns[0];
return a.title?a.title:a.name
}}return this.baseTitle
},getDataModule:function(){if(!$.defined(this.dataModule)){this.dataModule=DataModuleFactory.createDataModule(this)
}return this.dataModule
},exportChart:function(b){var a=$("<form>");
a.attr({target:"_self",method:"post",action:this.exporting.url});
a.append($("<input>").attr({type:"hidden",name:"type",value:b}));
a.append($("<input>").attr({type:"hidden",name:"options",value:$.toJSON(this.chartOptions)}));
a.append($("<input>").attr({type:"hidden",name:"data",value:$.toJSON(this.getDataModule().options)}));
a.append($("<input>").attr({type:"hidden",name:"width",value:this.chart.width}));
a.append($("<input>").attr({type:"hidden",name:"height",value:this.chart.height}));
a.appendTo("body").submit().remove()
},removeFunctions:function(b){if($.isObject(b)){for(var a in b){if($.isObject(b[a])){this.removeFunctions(b[a])
}else{if($.isFunction(b[a])){delete b[a]
}}}}},ajaxLoad:function(e,b,d,a){if(this.dataParams){$.extendInstance(e,this.dataParams)
}if(this.getDataModule().createParams){this.getDataModule().createParams(e)
}var c=this;
$.ajax({type:"POST",contentType:"application/json; charset=utf-8",dataType:"json",url:this.dataURL,cache:false,data:$.toJSON(e),success:function(f){c.load(f,b);
if(d){d(f)
}},error:function(f,h,g){if(a){a(f,h,g)
}}})
},load:function(c,a){var b=this.getDataModule();
if(a){delete b.options
}b.load(c);
this.refresh()
},createSeries:function(b){if($.defined(this.series)){return
}var e=[];
if($.defined(b)){for(var a in b.series){var d=b.series[a];
$.extendInstance(d,DefaultSeriesProperties);
if(this.seriesProperties){if($.isArray(this.seriesProperties)){for(var c=0;
c<this.seriesProperties.length;
c++){if(this.seriesProperties[c].name==a){$.extendInstance(d,this.seriesProperties[c]);
break
}}}else{$.extendInstance(d,this.seriesProperties)
}}e.push(d)
}}this.series=e
}};
var DefaultPieChartProperties={showPercent:false,chart:{type:"pie",plotBackgroundColor:null,plotBorderWidth:null,plotShadow:false},tooltip:{formatter:function(){return"<b>"+this.series.chart.options.getLabel(this)+"</b>: "+this.series.chart.options.valueFormat(this)
}},plotOptions:{pie:{allowPointSelect:true,animation:false,cursor:"pointer",dataLabels:{enabled:true,formatter:function(){return"<b>"+this.series.chart.options.getLabel(this)+"</b>: "+this.series.chart.options.valueFormat(this)
}},showInLegend:true,events:DefaultCategoryPlotOptionsEvents}},legend:{enabled:false,align:"right",layout:"vertical",verticalAlign:"middle",labelFormatter:function(){return this.series.chart.options.getLabel(this)+": "+this.series.chart.options.valueFormat(this)
}},getLabel:function(b){if(this.xAxis[0].categories){var a=b.point?b.point.x:b.x;
return this.xAxis[0].categories[a]
}return b.key?b.key:b.name
},valueFormat:function(b){var d;
if(this.showPercent){d=Highcharts.numberFormat(b.percentage,2)+"%"
}else{var a=this.logic;
if(!$.defined(a)){a=this.yAxis[0].logic
}var c=DataFormatorFactory.getDataFormator(a);
d=c.format(b.y)
}return d
},createSeries:function(a){var a=this.getDataModule().options;
var b=a.series["default"];
if(!$.defined(b.type)){b.type="pie"
}if(!$.defined(b.data)){b.data=[]
}this.series=[b]
},isChanged:function(){var c=this.getDataModule().options,e,a;
for(var d=0;
d<this.highChart.series.length;
d++){e=this.highChart.series[d];
a=c.series[e.name];
if(e.data.length!=a.data.length){return true
}for(var b=0;
b<e.data.length;
b++){if(e.data[b].name!=DataModuleFactory.getDateTimeValue(a.data[b])){return true
}}}return false
},refresh:function(){if(!$("#"+this.chart.renderTo).is(":visible")){return
}var b=this.getDataModule().options;
var d=b.series["default"];
if(!$.defined(d.data)||d.data.length==0){return
}if(!$.defined(this.highChart)||this.isChanged()){this.initialize();
this.createSeries();
this.createInstance()
}else{for(var c=0;
c<this.highChart.series.length;
c++){var e=this.highChart.series[c],a=b.series[e.name];
e.setData(a.data,false,false)
}this.highChart.reflow();
this.highChart.redraw()
}}};
var DefaultAxisChartProperties={legend:{enabled:true},createXAxis:function(a){if(!$.defined(this.xAxis)){this.xAxis={}
}if(this.dateTimePattern){this.xAxis.dateTimePattern=this.dateTimePattern
}if($.defined(a)){if(a.categories){this.xAxis.categories=a.categories
}var b=this.getDataModule();
if($.defined(b.getBeginTime)){this.xAxis.min=b.getBeginTime()
}if($.defined(b.getEndTime)){this.xAxis.max=b.getEndTime()
}}},createYAxisMaxValue:function(b,c){if($.defined(c.data)&&$.isArray(c.data)&&c.data.length>0){if(!this.stacked){var a=c.data.max(function(d,e){return DataModuleFactory.getPointValue(d)-DataModuleFactory.getPointValue(e)
});
a=$.defined(a)?DataModuleFactory.getPointValue(a):10;
b.max=$.defined(b.max)?Math.max(b.max,a):a
}}else{b.max=$.defined(b.max)?Math.max(b.max,10):10
}},createYAxis:function(d){var b=[];
if($.defined(d)&&d.series){for(var c in d.series){var f=d.series[c];
var a=this.getYAxisProperties(b,f);
a.logic=(f.logic||"value");
if(f.baseTitle){a.baseTitle=f.baseTitle
}if($.defined(f.visible)&&!f.visible){continue
}this.createYAxisMaxValue(a,f)
}}if(b.length==0){var a=this.getYAxisProperties(b,{});
a.logic="value";
a.max=10
}for(var e=0;
e<b.length;
e++){if(e>0&&!$.defined(b[e].opposite)){b[e].opposite=true
}}this.yAxis=b
},getYAxisProperties:function(b,e){var c=$.defined(e.yAxis)?e.yAxis:0;
var a;
if($.isNumber(c)){if($.defined(b[c])){a=b[c]
}else{a=$.extendObject(DefaultYAxisProperties);
b[c]=a;
var f=this.getBaseTitle();
if(f){a.baseTitle=f
}if(this.yAxisProperties){if($.isArray(this.yAxisProperties)){$.extendInstance(a,this.yAxisProperties[c])
}else{$.extendInstance(a,this.yAxisProperties)
}}}}else{if($.isString(c)){for(var d=0;
d<b.length;
d++){if(b[d].id==c){a=b[d];
break
}}if(this.baseTitle){a.baseTitle=this.baseTitle
}if(this.yAxisProperties){if($.isArray(this.yAxisProperties)){for(var d=0;
d<this.yAxisProperties.length;
d++){if(this.yAxisProperties[d].id==c){$.extendInstance(a,this.yAxisProperties[d]);
break
}}}else{$.extendInstance(a,this.yAxisProperties)
}}}}return a
},calculateAxisMaxValue:function(){var c,b,a,d,f;
for(c=0;
c<this.highChart.yAxis.length;
c++){d=this.highChart.yAxis[c];
if(this.stacked){delete d.options.max
}else{d.options.max=10;
for(b=0;
b<d.series.length;
b++){a=d.series[b].yData.max(function(g,h){return g-h
});
if($.defined(a)){d.options.max=Math.max(a,d.options.max)
}}}}var e=this.getDataModule();
for(c=0;
c<this.highChart.xAxis.length;
c++){d=this.highChart.xAxis[c];
if($.defined(e.getBeginTime)){d.options.min=e.getBeginTime()
}if($.defined(e.getEndTime)){d.options.max=e.getEndTime()
}}},refresh:function(){if(!$("#"+this.chart.renderTo).is(":visible")){return
}var b=this.getDataModule().options,e=false;
for(var a in b.series){var d=b.series[a];
if($.defined(d)){e=true;
break
}}if(!e){return
}if(!$.defined(this.highChart)){this.createXAxis(b);
this.createYAxis(b);
this.createSeries(b);
this.initialize();
this.createInstance()
}else{for(var c=0;
c<this.highChart.series.length;
c++){var f=this.highChart.series[c],g=b.series[f.name];
f.setData(g.data,false)
}this.calculateAxisMaxValue();
this.highChart.reflow();
this.highChart.redraw()
}}};
var DefaultBarChartProperties={chart:{type:"column"},tooltip:{shared:true,formatter:function(){var e=this.points;
if(e==undefined||e==null){e=[this]
}var f;
for(var d=0;
d<e.length;
d++){var a=e[d];
var b=a.series.yAxis;
if(d==0){f="<b>"+a.key+": ";
if(a.total!=undefined&&a.total!=null){f+=b.formatData(a.total)
}f+="</b>"
}var c='<span style="color:'+a.series.color+'">';
if(a.series.name&&a.series.name!="default"){f+="<br/>";
if(a.series.options.title){c+=a.series.options.title
}else{c+=a.series.name
}c+=": "
}c+=b.formatData(a.y);
c+="</span>";
f+=c
}return f
}},xAxis:{categories:[],labels:{rotation:-45,align:"right"}},plotOptions:{column:{animation:false,cursor:"pointer",dataLabels:{enabled:true,formatter:function(){return this.series.yAxis.formatValue(this.y)
},overflow:"justify",rotation:-90,align:"left",x:4},events:DefaultCategoryPlotOptionsEvents}},initialize:function(){if(this.series.length>1){this.legend.enabled=true;
this.plotOptions.column.dataLabels.enabled=false
}else{this.legend.enabled=false;
this.plotOptions.column.dataLabels.enabled=true
}if(this.stacked){this.plotOptions.series={stacking:"normal"}
}else{if(this.plotOptions.series&&this.plotOptions.series.stacking=="normal"){delete this.plotOptions.series
}}if(this.chart.inverted){delete this.xAxis.labels.rotation;
delete this.plotOptions.column.dataLabels.rotation;
delete this.plotOptions.column.dataLabels.x;
delete this.plotOptions.column.dataLabels.y
}}};
var DefaultTimeChartProperties={chart:{zoomType:"x",type:"spline"},tooltip:{crosshairs:true,shared:true,formatter:function(){var d=this.points;
if(d==undefined||d==null){d=[this]
}var e;
for(var c=0;
c<d.length;
c++){var a=d[c];
if(a.y==undefined||a.y==null||a.y==0){continue
}var b=a.series.yAxis;
if(c==0){e="<b>"+a.series.xAxis.formatTime(a.x,a.series.xAxis.lablesPattern)+": ";
if(a.total!=undefined&&a.total!=null){e+=b.formatData(a.total)
}e+="</b>"
}e+="<br/>";
e+='<span style="color:'+a.series.color+'">';
if(a.series.name&&a.series.name!="default"){if(a.series.options.title){e+=a.series.options.title
}else{e+=a.series.name
}e+=": "
}e+=b.formatData(a.y);
e+="</span>"
}return e
}},xAxis:{type:"datetime",labels:{formatter:function(){return this.axis.formatTime(this.value)
}}},plotOptions:{area:{stacking:"normal",trackByArea:true,fillOpacity:0.5},areaspline:{stacking:"normal",trackByArea:true,fillOpacity:0.5},series:{animation:false,cursor:"pointer",lineWidth:1.5,marker:{enabled:false,radius:1.5},events:DefaultTimePlotOptionsEvents}},reRender:function(b,a){this.clearHighChart();
this.chart.width=b;
this.chart.height=a;
if(this.xAxis){var c=this.getDataModule();
if(this.xAxis.min&&c.getBeginTime){this.xAxis.min=c.getBeginTime()
}if(this.xAxis.max&&c.getEndTime){this.xAxis.max=c.getEndTime()
}}this.highChart=new Highcharts.Chart(this)
}};
var HighChartsFactory={createHighChart:function(d,a,c){var b="create"+a.type+"Chart";
if(this[b]){return this[b](d,a,c)
}},prepareChartOptions:function(b,a){if(!$.defined(b.chart)){b.chart={}
}if($.defined(b.id)){b.chart.renderTo=b.id;
delete b.id
}if($.defined(b.inverted)){b.chart.inverted=b.inverted;
delete b.inverted
}if($.defined(a.exportEnabled)){b.exporting.enabled=a.exportEnabled;
delete b.exportEnabled
}if($.defined(b.width)){b.chart.width=b.width;
delete b.width
}if($.defined(b.height)){b.chart.height=b.height;
delete b.height
}if($.defined(b.showLegend)){b.legend.enabled=b.showLegend;
delete b.showLegend
}if($.defined(b.title)&&$.isString(b.title)){b.title={text:b.title}
}if($.defined(b.subtitle)&&$.isString(b.subtitle)){b.subtitle={text:b.subtitle}
}this.prepareChartLegendOptions(b)
},prepareChartLegendOptions:function(b){if($.defined(b.legendAlign)&&$.isString(b.legendAlign)){if(!$.defined(b.legend)){b.legend={}
}var a=b.legend;
if(b.legendAlign=="left"){$.extendInstance(a,{align:"left",layout:"vertical",verticalAlign:"middle"})
}else{if(b.legendAlign=="right"){$.extendInstance(a,{align:"right",layout:"vertical",verticalAlign:"middle"})
}else{if(b.legendAlign=="top"){$.extendInstance(a,{align:"center",layout:"horizontal",verticalAlign:"middle"})
}else{if(b.legendAlign=="bottom"){$.extendInstance(a,{align:"center",layout:"horizontal",verticalAlign:"middle"})
}}}}delete b.legendAlign
}},createChart:function(f,e,b,c,a){if(!$.isString(f)){return
}var d=$.extendObject({chart:{renderTo:f},dataModuleType:e},DefaultChartProperties,b);
this.prepareChartOptions(d,a);
d.chartOptions=a;
d.load(c);
return d
},createPieChart:function(d,b,c){var a=$.extendObject(DefaultPieChartProperties,b);
if($.defined(a.showDataLabels)){a.plotOptions.pie.dataLabels.enabled=a.showDataLabels;
delete a.showDataLabels
}return this.createChart(d,"Category",a,c,b)
},createBarChart:function(d,b,c){var a=$.extendObject(DefaultAxisChartProperties,DefaultBarChartProperties,b);
return this.createChart(d,"Category",a,c,b)
},createLineChart:function(d,b,c){var a=$.extendObject(DefaultAxisChartProperties,DefaultTimeChartProperties,b);
return this.createChart(d,"Time",a,c,b)
},createAreaSplineChart:function(d,b,c){var a=$.extendObject(DefaultAxisChartProperties,DefaultTimeChartProperties,b);
a.stacked=true;
a.nullValue=true;
a.chart.type="areaspline";
return this.createChart(d,"Time",a,c,b)
},createAreaChart:function(d,b,c){var a=$.extendObject(DefaultAxisChartProperties,DefaultTimeChartProperties,b);
a.stacked=true;
a.nullValue=true;
a.chart.type="area";
return this.createChart(d,"Time",a,c,b)
}};