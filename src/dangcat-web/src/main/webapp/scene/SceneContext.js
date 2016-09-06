var servicePrincipal = {
    no: isc.params.user,
    name: "软件技术",
    roleName: "管理员",
    loginTime: new Date(2014, 11, 21, 12, 21, 12, 21),
    type: "OperatorSecurity",
    clientIp: "127.0.0.1"
};

isc.ApplicationContext.i18nFilesExt = [];
isc.ApplicationContext.cssFilesExt = [];
isc.ApplicationContext.jsFilesExt = [];
isc.ApplicationContext.loginInfoLayout = isc.Label.create({
    autoDraw: false,
    styleName: "login-tail",
    height: 40,
    opacity: 30,
    align: "center",
    color: "red",
    contents: isc.ApplicationContext.loginInfo
});
isc.ApplicationContext.beforeCreateMainLayout = function () {
    if ($.defined(this.extendInfos))
        this.loginInfoLayout.setContents(this.extendInfos.loginInfo);
};isc.ApplicationContext.menus = [
    {
        name: "Home",
        title: "首页",
        icon: "[SKIN]/icons/16/world.png",
        url: "[APP]Modules/home/BoardMain.js"
    },
    {
        name: "Monitor",
        title: "运行监控",
        icon: "[SKIN]/icons/16/chart_bar.png",
        url: "[APP]Modules/home/HomeMain.js",
        data: [
            {
                title: "账户管理",
                submenu: [
                    { title: "账户组管理", url: "./Modules/Demo/WidgetsMain.js" },
                    { title: "账户管理", url: "./Modules/Demo/TileMain.js" },
                    { title: "账户批量管理" },
                    { title: "在线用户管理" }
                ]
            },
            {
                title: "服务管理",
                submenu: [
                    { title: "服务组应用统计查询" },
                    { title: "服务组异常状况查询" },
                    { title: "服务应用趋势查询" },
                    { title: "服务应用分布统计查询" }
                ]
            },
            {
                title: "策略管理",
                submenu: [
                    { title: "按时段控制策略管理" },
                    { title: "在线数限制策略管理" },
                    { title: "按流量配额策略管理" },
                    { title: "按时长配额策略管理" },
                    { title: "授权绑定策略管理" },
                    { title: "接入ACL策略管理" },
                    { title: "代理策略管理" },
                    { title: "黑名单策略管理" },
                    { title: "失败重定向策略管理" },
                    { title: "链接账号策略管理" },
                    { title: "策略使用日志查询" },
                    { title: "策略应用统计查询" },
                    { title: "策略使用异常查询" }
                ]
            },
            {
                title: "账务管理",
                submenu: [
                    { title: "固定资费管理" },
                    { title: "按周期记费管理" },
                    { title: "按流量记费管理" },
                    { title: "按时长记费管理" },
                    { title: "资费套餐管理" },
                    { title: "资费使用状况统计查询" },
                    { title: "操作员收款统计查询" },
                    { title: "操作员收款日志查询" },
                    { title: "账户余额告警查询" }
                ]
            }
        ]
    },
    {
        name: "Report",
        title: "报表管理",
        icon: "[SKIN]/icons/16/printer3.png",
        data: [
            {
                title: "账户报表",
                submenu: [
                    { title: "账户组分布状态报表" },
                    { title: "账户状态报表" },
                    { title: "账户使用日志报表" },
                    { title: "账户异常状状态报表" },
                    { title: "账户异常状况报表" },
                    { title: "账户操作日志报表" },
                    { title: "账户用量统计报表" }
                ]
            },
            {
                title: "账务报表",
                submenu: [
                    { title: "固定资费报表" },
                    { title: "按周期记费报表" },
                    { title: "按流量记费报表" },
                    { title: "按时长记费报表" },
                    { title: "资费套餐报表" },
                    { title: "资费使用状况统计报表" },
                    { title: "操作员收款统计报表" },
                    { title: "操作员收款日志报表" },
                    { title: "账户余额告警报表" }
                ]
            }
        ]
    },
    {
        name: "Config",
        title: "业务配置",
        icon: "[SKIN]/icons/16/detail.png",
        data: [
            {
                title: "账户配置",
                submenu: [
                    { title: "账户组配置", url: "./Modules/Demo/WidgetsMain.js" },
                    { title: "账户配置" },
                    { title: "账户批量配置" },
                    { title: "在线用户配置" }
                ]
            },
            {
                title: "策略配置",
                submenu: [
                    { title: "按时段控制策略配置" },
                    { title: "在线数限制策略配置" },
                    { title: "按流量配额策略配置" },
                    { title: "按时长配额策略配置" },
                    { title: "授权绑定策略配置" },
                    { title: "接入ACL策略配置" },
                    { title: "代理策略配置" },
                    { title: "黑名单策略配置" },
                    { title: "失败重定向策略配置" },
                    { title: "链接账号策略配置" },
                    { title: "策略使用日志查询" },
                    { title: "策略应用统计查询" },
                    { title: "策略使用异常查询" }
                ]
            },
            {
                title: "账务配置",
                submenu: [
                    { title: "固定资费配置" },
                    { title: "按周期记费配置" },
                    { title: "按流量记费配置" },
                    { title: "按时长记费配置" },
                    { title: "资费套餐配置" },
                    { title: "资费使用状况统计查询" },
                    { title: "操作员收款统计查询" },
                    { title: "操作员收款日志查询" },
                    { title: "账户余额告警查询" }
                ]
            },
            {
                title: "操作员配置",
                baseUrl: "./Modules/",
                name: "Staff",
                submenu: [
                    { title: "角色配置" },
                    { title: "操作员组配置" },
                    { title: "操作员配置" },
                    { title: "操作员操作日志查询" },
                    { title: "操作员异常状况查询" }
                ]
            },
            {
                title: "业务配置",
                submenu: [
                    { title: "标准属性配置" },
                    { title: "厂商属性配置" },
                    { title: "接入类型配置" },
                    { title: "动态绑定属性配置" },
                    { title: "在线授权属性配置" }
                ]
            }
        ]
    },
    {
        name: "System",
        title: "系统配置",
        icon: "[SKIN]/icons/16/download.png",
        data: [
            {
                title: "服务配置",
                submenu: [
                    { title: "服务配置配置" },
                    { title: "服务组配置" },
                    { title: "服务组应用统计查询" },
                    { title: "服务组异常状况查询" },
                    { title: "服务应用趋势查询" },
                    { title: "服务应用分布统计查询" }
                ]
            },
            {
                title: "服务器配置",
                submenu: [
                    { title: "服务器配置配置" },
                    { title: "服务器在线状态查询" },
                    { title: "服务器异常告警查询" },
                    { title: "服务器操作日志查询" },
                    { title: "卓越空喊口号查询" },
                    { title: "节操碎了一地查询" }
                ]
            },
            {
                title: "系统配置",
                submenu: [
                    { title: "数据备份配置" },
                    { title: "数据还原配置" },
                    { title: "过期数据清理配置" },
                    { title: "系统配置配置" },
                    { title: "许可证配置" },
                    { title: "数据备份还原操作日志查询" },
                    { title: "数据清理日志查询" }
                ]
            }
        ]
    }
];