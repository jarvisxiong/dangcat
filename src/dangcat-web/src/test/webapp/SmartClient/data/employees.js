var valueMapData = [
	{id:"4", parentId:"1", name:"Charles Madigen"},
	{id:"188", parentId:"4", name:"Rogine Leger"},
	{id:"189", parentId:"4", name:"Gene Porter"},
	{id:"265", parentId:"189", name:"Olivier Doucet"},
	{id:"264", parentId:"189", name:"Cheryl Pearson"}
];
		
isc.DataSource.create({
    allowAdvancedCriteria:true,
    recordName:"employee",
    serverType:"sql",
    testFileName:"../data/employees.data.xml",
    titleField:"Name",
    tableCode:"a6744ab162eb640344ffc18c778b303d",
    ID:"employees",
    fields:[
        {
            title:"userOrder",
            hidden:true,
            columnCode:"27b0ad687779aac57fb52fac70c20d94",
            name:"userOrder",
            type:"integer",
            canEdit:false
        },
        {
            title:"Name",
            columnCode:"b068931cc450442b63f5b3d276ea4297",
            name:"Name",
            length:128,
            type:"text"
        },
        {
            title:"Employee ID",
            columnCode:"0b8b667e7722bc7e363b601ce584259d",
            primaryKey:true,
            name:"EmployeeId",
            type:"integer",
            required:true
        },
        {
            title:"Manager",
            detail:true,
            columnCode:"ff9a062e065c847a48cc468303a4ec9d",
            rootValue:"1",
            name:"ReportsTo",
            type:"integer",
            required:true,
            foreignKey:"employees.EmployeeId"
        },
        {
            title:"Title",
            columnCode:"9dddd5ce1b1375bc497feeb871842d4b",
            name:"Job",
            length:128,
            type:"text"
        },
        {
            title:"Email",
            columnCode:"0c83f57c786a0b4a39efab23731c7ebc",
            name:"Email",
            length:128,
            type:"text"
        },
        {
            title:"Employee Type",
            columnCode:"cd2d171d7836aa13e1f51444e201ea79",
            name:"EmployeeType",
            length:40,
            type:"text"
        },
        {
            title:"Status",
            columnCode:"0eb0329a5332e499805e944a698671ad",
            name:"EmployeeStatus",
            length:40,
            type:"text"
        },
        {
            title:"Salary",
            columnCode:"28aa838315633f0e44049ce88de36803",
            name:"Salary",
            type:"float"
        },
        {
            title:"Org Unit",
            columnCode:"fc7e3705381bd25f5a0abb4b7292c954",
            name:"OrgUnit",
            length:128,
            type:"text"
        },
        {
            title:"Gender",
            valueMap:[
                "male",
                "female"
            ],
            columnCode:"cc90f1913b83d255b95be0e0fea6d576",
            name:"Gender",
            length:7,
            type:"text"
        },
        {
            title:"Marital Status",
            valueMap:[
                "married",
                "single"
            ],
            columnCode:"800d2aa9bba75f0cea354691ca37597f",
            name:"MaritalStatus",
            length:10,
            type:"text"
        }
    ]
})