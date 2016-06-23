/*
 LogoLayout
 */
isc.ClassFactory.defineClass("LogoLayout", "HLayout");
isc.LogoLayout.addProperties({
    autoDraw: false,
    width: "100%",
    height: 55,
    masterTitleStyle: "masterTitle",
    secondTitleStyle: "secondTitle",
    versionTitleStyle: "versionTitle",
    //border: "1px solid white",
    projectLogo: {
        styleName: "logo",
        width: 140,
        height: 50,
        imageType: "stretch",
        src: "[SKIN]/project-logo.png"
    }
});

isc.LogoLayout.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);

        this.projectLogoImg = isc.Img.create(this.projectLogo, this.projectLogoProperties);
        this.addMember(this.projectLogoImg);
        this.titleLayout = isc.VLayout.create({
            width: "100%",
            members: [
                isc.HLayout.create({
                    width: "100%",
                    height: 24,
                    members: [
                        isc.Label.create({autoFit: true, wrap: false, contents: isc.ApplicationContext.masterTitle, styleName: this.masterTitleStyle}),
                        isc.Label.create({autoFit: true, wrap: false, contents: isc.ApplicationContext.version, styleName: this.versionTitleStyle})
                    ]
                }),
                isc.Label.create({height: 10, wrap: false, contents: isc.ApplicationContext.secondTitle, styleName: this.secondTitleStyle})
            ]
        });
        this.addMember(this.titleLayout);
    }
});

/*
 Head Layout
 */
isc.ClassFactory.defineClass("HeadLayout", "BackgroundLayout");
isc.HeadLayout.addProperties({
    autoDraw: false,
    height: 55,
    orientation: "horizontal",//"vertical"
    styleName: "headLayout",
    contentLayoutProperties: {
        layoutRightMargin: 30
    },
    portalMenuBarProperties: {
        width: "*",
        height: 25,
        layoutLeftMargin: 15,
        layoutRightMargin: 15,
        membersMargin: 10
    },
    src: "[SKIN]/background.png"
});

isc.HeadLayout.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);

        this.logoLayout = isc.LogoLayout.create({
            width: "30%"
        });
        this.addMember(this.logoLayout);

        this.mainMenu = isc.VLayout.create({
            autoDraw: false,
            width: "55%",
            height: "100%",
            align: "bottom"
        });
        this.addMember(this.mainMenu);

        this.portalMenuBar = this.createPortalMenuBar();
        this.addMember(this.portalMenuBar);
    },

    createPortalMenuBar: function () {
        return isc.HLayout.create({
            autoDraw: false,
            styleName: "portalToolBar",
            align: "right",
            buttons: [
                {
                    title: isc.i18nButton.user,
                    icon: "[SKIN]/button/user.png",
                    click: function () {
                        isc.UserWindow.open(this);
                    }
                },
                {
                    title: isc.i18nButton.logout,
                    icon: "[SKIN]/button/logout.png",
                    click: function () {
                        isc.ask(isc.i18nAsk.logout, function (value) {
                                if (value)
                                    isc.ApplicationContext.logout();
                            }
                        );
                    }
                },
                {
                    title: isc.i18nButton.about,
                    icon: "[SKIN]/button/about.png",
                    click: function () {
                        isc.AboutWindow.open(this);
                    }
                }
            ],
            initWidget: function () {
                this.Super("initWidget", arguments);

                for (var i = 0; i < this.buttons.length; i++) {
                    var button = isc.FlatButton.create(this.buttons[i]);
                    this.addMember(button);
                }
            }
        }, this.portalMenuBarProperties);
    },

    destroy: function () {
        this.logoLayout.destroy();
        this.portalMenuBar.destroy();
        this.Super("destroy", arguments);
    }
});

/*
 Menu Layout
 */
isc.ClassFactory.defineClass("MenuLayout", "HLayout");
isc.MenuLayout.addProperties({
    autoDraw: false,
    styleName: "menuLayout",
    width: "100%",
    height: 26
});

isc.MenuLayout.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);

        this.menuToolBar = isc.MenuBarEx.create({
            styleName: "menuToolBar",
            width: "100%",
            height: this.height - 1,
            layoutLeftMargin: 10,
            membersMargin: 10,
            initWidget: function () {
                this.Super("initWidget", arguments);

                var menus = isc.ApplicationContext.loadMenus();
                for (var i = 0; i < menus.length; i++) {
                    var menu = isc.MenuExt.create({
                        menuData: menus[i],
                        itemClick: function (item, colNum) {
                            isc.ApplicationContext.moduleTabSet.loadModule(item);
                        }
                    });
                    this.addMenus(menu, null);
                }
            }
        });
        this.addMember(this.menuToolBar);
    }
});

/*
 Body Layout
 */
isc.ClassFactory.defineClass("MainLayout", "VLayout");
isc.MainLayout.addProperties({
    width: "100%",
    height: "100%"
});

isc.MainLayout.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);

        var mainPanel = isc.TabPanel.create({
            autoDraw: false,
            width: "100%",
            height: "100%",
            data: isc.ApplicationContext.loadMenus()
        })
        var headLayout = isc.HeadLayout.create({
            autoDraw: false
        });
        headLayout.mainMenu.addMember(mainPanel.header);
        this.addMember(headLayout);
        this.addMember(mainPanel);

        //this.addMember(isc.MenuLayout.create());
        //isc.ApplicationContext.moduleTabSet = isc.ModuleTabSet.create();
        //this.addMember(isc.ApplicationContext.moduleTabSet);
    }
});

/*
 Login Layout
 */
isc.ClassFactory.defineClass("LoginLayout", "BackgroundLayout");
isc.LoginLayout.addProperties({
    autoDraw: true,
    styleName: "login-background",
    src: "[SKIN]/login-background.jpg",
    contentLayoutProperties: {
        layoutTopMargin: 150,
        defaultLayoutAlign: "center"
    }
});

isc.LoginLayout.addMethods({
    initWidget: function () {
        this.Super("initWidget", arguments);

        var height = 180;
        if ($.defined(isc.ApplicationContext.loginInfoLayout))
            height += isc.ApplicationContext.loginInfoLayout.height | 40;

        var backgroundLayout = isc.BackgroundLayout.create({
            autoDraw: false,
            width: 480,
            height: height,
            showShadow: true,
            styleName: "login-center",
            align: "top",
            src: "[SKIN]/login-background.png",
            contentLayoutProperties: {
                padding: 0,
                layoutTopMargin: 20,
                layoutLeftMargin: 20,
                layoutRightMargin: 20,
                align: "top",
                defaultLayoutAlign: "center"
            },
            members: [
                isc.LogoLayout.create({
                    width: "100%",
                    masterTitleStyle: "loginMasterTitle",
                    secondTitleStyle: "loginSecondTitle",
                    versionTitleStyle: "loginVersionTitle"
                }) ,
                isc.LoginForm.create({
                    width: "90%",
                    height: 60
                })
            ]
        });

        if ($.defined(isc.ApplicationContext.loginInfoLayout))
            backgroundLayout.addMember(isc.ApplicationContext.loginInfoLayout);

        var members = [
            backgroundLayout,
            isc.LayoutSpacer.create({
                autoDraw: false,
                width: "100%",
                height: "100%"
            }),
            isc.Label.create({
                autoDraw: false,
                styleName: "login-tail",
                height: 25,
                opacity: 30,
                align: "center",
                contents: isc.ApplicationContext.copyRight
            })
        ];
        this.addMembers(members);
    }
});

/*
 Login Layout
 */
isc.ClassFactory.defineClass("LoginForm", "VLayout");
isc.LoginForm.addProperties({
    height: "100%",
    layoutLeftMargin: 50,
    //border: "1px solid red",
    align: "center"
});
isc.LoginForm.addMethods({
    initWidget: function () {
        this.addMembers(this.getMembers());
        this.Super("initWidget", arguments);
    },

    getMembers: function () {
        var loginForm = this;
        var loginFormProperties = {
            autoDraw: false,
            width: "100%",
            height: 90,
            colWidths1: ["10%", "90%"],
            formBorder: "none",
            titleStyle: "formTitle",
            cellStyle: "formCell",
            columnCount: 1,
            fields: [
                {title: isc.i18nLogin.User, name: "no", wrapTitle: false, type: "text", length: 20, displayWidth: 20},
                {title: isc.i18nLogin.Password, name: "password", wrapTitle: false, type: "password", length: 20, displayWidth: 20},
                {showTitle: true, type: "toolbar", buttons: [
                    isc.ToolButton.create(isc.ToolButton.ButtonTypes.Login, {
                        enabled: isc.ApplicationContext.isEnabled(),
                        click: function () {
                            loginForm.toolBar_login_ItemClick(this);
                        }
                    }),
                    isc.ToolButton.create(isc.ToolButton.ButtonTypes.Reset, {
                        click: function () {
                            loginForm.toolBar_reset_ItemClick(this);
                        }
                    })
                ]}
            ]
        };
        if ($.isFunction(isc.ApplicationContext.beforeCreateLoginForm))
            loginFormProperties = isc.ApplicationContext.beforeCreateLoginForm(loginForm);
        var dataForm = isc.DataForm.create(loginFormProperties);
        dataForm.initialize();
        this.dataForm = dataForm;
        return this.dataForm;
    },

    toolBar_login_ItemClick: function (item) {
        var dataForm = this.dataForm;
        dataForm.clearErrors(true);
        var no = dataForm.getField("no").getValue();
        if (!no || no.isEmpty())
            dataForm.addFieldErrors("no", isc.i18nLogin.NoEmpty);
        var password = dataForm.getField("password").getValue();
        if (!password || password.isEmpty())
            dataForm.addFieldErrors("password", isc.i18nLogin.PasswordEmpty);
        if (dataForm.hasErrors())
            dataForm.markForRedraw();
        else {
            if (isc.ApplicationContext.isLocale() == true)
                isc.ApplicationContext.login();
            else
                this.remoteLogin(no, password);
        }
    },

    toolBar_reset_ItemClick: function (item) {
        this.dataForm.setValue("no", "");
        this.dataForm.setValue("password", "");
    },

    remoteInvoke: function (url, data, success) {
        isc.PRCService.ajaxInvoke("GET", url, data, function (responseData) {
                if (success)
                    success(responseData);
            },
            function (request, textStatus, errorThrown) {
                isc.ApplicationContext.hideProcess();
                isc.warn("status: " + request.status + "\r\n" + "textStatus: " + textStatus + "\r\n" + "errorThrown: " + errorThrown);
            });
    },

    remoteLogin: function (no, password) {
        isc.ApplicationContext.showProcess(isc.i18nLogin.LoginPrompt);

        var data = {
            no: $.encryptContent(no),
            password: $.encryptPassword(no, password),
            locale: isc.ApplicationContext.locale
        };

        var dataForm = this.dataForm;
        this.remoteInvoke("login", data, function (responseData) {
            isc.ApplicationContext.hideProcess();
            if (responseData.error) {
                if (responseData.fieldName) {
                    dataForm.addFieldErrors(responseData.fieldName, responseData.error);
                    dataForm.markForRedraw();
                }
                else
                    isc.warn(responseData.error);
            }
            else
                isc.ApplicationContext.login();
        });
    }
});

/*
 * About Dialog
 */
isc.ClassFactory.defineClass("AboutWindow", "DialogWindow");
isc.AboutWindow.addProperties({
    width: 540,
    height: 420,
    buttons: [isc.ToolButton.ButtonTypes.Exit]
});

isc.AboutWindow.addMethods({
    getLayoutProperties: function () {
        return [
            {
                classType: isc.VPanel,
                properties: {
                    styleName: "login-background"
                },
                members: [
                    {
                        classType: isc.VPanel,
                        properties: {
                            border: "1px solid #A7ABB4"
                        },
                        members: [
                            {
                                classType: isc.BackgroundLayout,
                                properties: {
                                    styleName: "login-center",
                                    src: "[SKIN]/about-background.png",
                                    contentLayoutProperties: {
                                        layoutTopMargin: 100,
                                        layoutLeftMargin: 40
                                    }
                                },
                                members: [
                                    {
                                        classType: isc.LogoLayout,
                                        properties: {
                                            masterTitleStyle: "loginMasterTitle",
                                            secondTitleStyle: "loginSecondTitle",
                                            versionTitleStyle: "loginVersionTitle"
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ];
    }
});

