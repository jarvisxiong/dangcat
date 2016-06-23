isc.ClassFactory.defineClass("LogoLayout","HLayout");
isc.LogoLayout.addProperties({autoDraw:false,width:"100%",height:55,masterTitleStyle:"masterTitle",secondTitleStyle:"secondTitle",versionTitleStyle:"versionTitle",projectLogo:{styleName:"logo",width:140,height:50,imageType:"stretch",src:"[SKIN]/project-logo.png"}});
isc.LogoLayout.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.projectLogoImg=isc.Img.create(this.projectLogo,this.projectLogoProperties);
this.addMember(this.projectLogoImg);
this.titleLayout=isc.VLayout.create({width:"100%",members:[isc.HLayout.create({width:"100%",height:24,members:[isc.Label.create({autoFit:true,wrap:false,contents:isc.ApplicationContext.masterTitle,styleName:this.masterTitleStyle}),isc.Label.create({autoFit:true,wrap:false,contents:isc.ApplicationContext.version,styleName:this.versionTitleStyle})]}),isc.Label.create({height:10,wrap:false,contents:isc.ApplicationContext.secondTitle,styleName:this.secondTitleStyle})]});
this.addMember(this.titleLayout)
}});
isc.ClassFactory.defineClass("HeadLayout","BackgroundLayout");
isc.HeadLayout.addProperties({autoDraw:false,height:55,orientation:"horizontal",styleName:"headLayout",contentLayoutProperties:{layoutRightMargin:30},portalMenuBarProperties:{width:"*",height:25,layoutLeftMargin:15,layoutRightMargin:15,membersMargin:10},src:"[SKIN]/background.png"});
isc.HeadLayout.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.logoLayout=isc.LogoLayout.create({width:"30%"});
this.addMember(this.logoLayout);
this.mainMenu=isc.VLayout.create({autoDraw:false,width:"55%",height:"100%",align:"bottom"});
this.addMember(this.mainMenu);
this.portalMenuBar=this.createPortalMenuBar();
this.addMember(this.portalMenuBar)
},createPortalMenuBar:function(){return isc.HLayout.create({autoDraw:false,styleName:"portalToolBar",align:"right",buttons:[{title:isc.i18nButton.user,icon:"[SKIN]/button/user.png",click:function(){isc.UserWindow.open(this)
}},{title:isc.i18nButton.logout,icon:"[SKIN]/button/logout.png",click:function(){isc.ask(isc.i18nAsk.logout,function(a){if(a){isc.ApplicationContext.logout()
}})
}},{title:isc.i18nButton.about,icon:"[SKIN]/button/about.png",click:function(){isc.AboutWindow.open(this)
}}],initWidget:function(){this.Super("initWidget",arguments);
for(var b=0;
b<this.buttons.length;
b++){var a=isc.FlatButton.create(this.buttons[b]);
this.addMember(a)
}}},this.portalMenuBarProperties)
},destroy:function(){this.logoLayout.destroy();
this.portalMenuBar.destroy();
this.Super("destroy",arguments)
}});
isc.ClassFactory.defineClass("MenuLayout","HLayout");
isc.MenuLayout.addProperties({autoDraw:false,styleName:"menuLayout",width:"100%",height:26});
isc.MenuLayout.addMethods({initWidget:function(){this.Super("initWidget",arguments);
this.menuToolBar=isc.MenuBarEx.create({styleName:"menuToolBar",width:"100%",height:this.height-1,layoutLeftMargin:10,membersMargin:10,initWidget:function(){this.Super("initWidget",arguments);
var b=isc.ApplicationContext.loadMenus();
for(var a=0;
a<b.length;
a++){var c=isc.MenuExt.create({menuData:b[a],itemClick:function(e,d){isc.ApplicationContext.moduleTabSet.loadModule(e)
}});
this.addMenus(c,null)
}}});
this.addMember(this.menuToolBar)
}});
isc.ClassFactory.defineClass("MainLayout","VLayout");
isc.MainLayout.addProperties({width:"100%",height:"100%"});
isc.MainLayout.addMethods({initWidget:function(){this.Super("initWidget",arguments);
var b=isc.TabPanel.create({autoDraw:false,width:"100%",height:"100%",data:isc.ApplicationContext.loadMenus()});
var a=isc.HeadLayout.create({autoDraw:false});
a.mainMenu.addMember(b.header);
this.addMember(a);
this.addMember(b)
}});
isc.ClassFactory.defineClass("LoginLayout","BackgroundLayout");
isc.LoginLayout.addProperties({autoDraw:true,styleName:"login-background",src:"[SKIN]/login-background.jpg",contentLayoutProperties:{layoutTopMargin:150,defaultLayoutAlign:"center"}});
isc.LoginLayout.addMethods({initWidget:function(){this.Super("initWidget",arguments);
var a=180;
if($.defined(isc.ApplicationContext.loginInfoLayout)){a+=isc.ApplicationContext.loginInfoLayout.height|40
}var c=isc.BackgroundLayout.create({autoDraw:false,width:480,height:a,showShadow:true,styleName:"login-center",align:"top",src:"[SKIN]/login-background.png",contentLayoutProperties:{padding:0,layoutTopMargin:20,layoutLeftMargin:20,layoutRightMargin:20,align:"top",defaultLayoutAlign:"center"},members:[isc.LogoLayout.create({width:"100%",masterTitleStyle:"loginMasterTitle",secondTitleStyle:"loginSecondTitle",versionTitleStyle:"loginVersionTitle"}),isc.LoginForm.create({width:"90%",height:60})]});
if($.defined(isc.ApplicationContext.loginInfoLayout)){c.addMember(isc.ApplicationContext.loginInfoLayout)
}var b=[c,isc.LayoutSpacer.create({autoDraw:false,width:"100%",height:"100%"}),isc.Label.create({autoDraw:false,styleName:"login-tail",height:25,opacity:30,align:"center",contents:isc.ApplicationContext.copyRight})];
this.addMembers(b)
}});
isc.ClassFactory.defineClass("LoginForm","VLayout");
isc.LoginForm.addProperties({height:"100%",layoutLeftMargin:50,align:"center"});
isc.LoginForm.addMethods({initWidget:function(){this.addMembers(this.getMembers());
this.Super("initWidget",arguments)
},getMembers:function(){var c=this;
var a={autoDraw:false,width:"100%",height:90,colWidths1:["10%","90%"],formBorder:"none",titleStyle:"formTitle",cellStyle:"formCell",columnCount:1,fields:[{title:isc.i18nLogin.User,name:"no",wrapTitle:false,type:"text",length:20,displayWidth:20},{title:isc.i18nLogin.Password,name:"password",wrapTitle:false,type:"password",length:20,displayWidth:20},{showTitle:true,type:"toolbar",buttons:[isc.ToolButton.create(isc.ToolButton.ButtonTypes.Login,{enabled:isc.ApplicationContext.isEnabled(),click:function(){c.toolBar_login_ItemClick(this)
}}),isc.ToolButton.create(isc.ToolButton.ButtonTypes.Reset,{click:function(){c.toolBar_reset_ItemClick(this)
}})]}]};
if($.isFunction(isc.ApplicationContext.beforeCreateLoginForm)){a=isc.ApplicationContext.beforeCreateLoginForm(c)
}var b=isc.DataForm.create(a);
b.initialize();
this.dataForm=b;
return this.dataForm
},toolBar_login_ItemClick:function(c){var b=this.dataForm;
b.clearErrors(true);
var d=b.getField("no").getValue();
if(!d||d.isEmpty()){b.addFieldErrors("no",isc.i18nLogin.NoEmpty)
}var a=b.getField("password").getValue();
if(!a||a.isEmpty()){b.addFieldErrors("password",isc.i18nLogin.PasswordEmpty)
}if(b.hasErrors()){b.markForRedraw()
}else{if(isc.ApplicationContext.isLocale()==true){isc.ApplicationContext.login()
}else{this.remoteLogin(d,a)
}}},toolBar_reset_ItemClick:function(a){this.dataForm.setValue("no","");
this.dataForm.setValue("password","")
},remoteInvoke:function(a,b,c){isc.PRCService.ajaxInvoke("GET",a,b,function(d){if(c){c(d)
}},function(d,f,e){isc.ApplicationContext.hideProcess();
isc.warn("status: "+d.status+"\r\ntextStatus: "+f+"\r\nerrorThrown: "+e)
})
},remoteLogin:function(d,b){isc.ApplicationContext.showProcess(isc.i18nLogin.LoginPrompt);
var c={no:$.encryptContent(d),password:$.encryptPassword(d,b),locale:isc.ApplicationContext.locale};
var a=this.dataForm;
this.remoteInvoke("login",c,function(e){isc.ApplicationContext.hideProcess();
if(e.error){if(e.fieldName){a.addFieldErrors(e.fieldName,e.error);
a.markForRedraw()
}else{isc.warn(e.error)
}}else{isc.ApplicationContext.login()
}})
}});
isc.ClassFactory.defineClass("AboutWindow","DialogWindow");
isc.AboutWindow.addProperties({width:540,height:420,buttons:[isc.ToolButton.ButtonTypes.Exit]});
isc.AboutWindow.addMethods({getLayoutProperties:function(){return[{classType:isc.VPanel,properties:{styleName:"login-background"},members:[{classType:isc.VPanel,properties:{border:"1px solid #A7ABB4"},members:[{classType:isc.BackgroundLayout,properties:{styleName:"login-center",src:"[SKIN]/about-background.png",contentLayoutProperties:{layoutTopMargin:100,layoutLeftMargin:40}},members:[{classType:isc.LogoLayout,properties:{masterTitleStyle:"loginMasterTitle",secondTitleStyle:"loginSecondTitle",versionTitleStyle:"loginVersionTitle"}}]}]}]}]
}});