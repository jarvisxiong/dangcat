var valueMapData=  [
	{ SKU:58074602, itemName: "itemName1", description: "description1" },
	{ SKU:58074614, itemName: "item1ame2", description: "description2" },
	{ SKU:58074205, itemName: "ite2Name3", description: "description3" },
	{ SKU:58073605, itemName: "it3mName4", description: "description4" },
	{ SKU:58044605, itemName: "i4emName5", description: "description5" },
	{ SKU:58174602, itemName: "itemName1", description: "description1" },
	{ SKU:58174614, itemName: "item1ame2", description: "description2" },
	{ SKU:58174205, itemName: "ite2Name3", description: "description3" },
	{ SKU:58173605, itemName: "it3mName4", description: "description4" },
	{ SKU:58144605, itemName: "i4emName5", description: "description5" },
	{ SKU:58274602, itemName: "itemName1", description: "description1" },
	{ SKU:58274614, itemName: "item1ame2", description: "description2" },
	{ SKU:58274205, itemName: "ite2Name3", description: "description3" },
	{ SKU:58273605, itemName: "it3mName4", description: "description4" },
	{ SKU:58244605, itemName: "i4emName5", description: "description5" },
	{ SKU:58574605, itemName: "i5emName6", description: "description6" }
];

isc.DataSource.create({
    allowAdvancedCriteria:true,
    testFileName:"../data/supplyItem.data.xml",
    titleField:"itemName",
    tableCode:"e2884702604b0a04589e04df8e4cab00",
    ID:"supplyItem",
    fields:[
        {
            hidden:true,
            columnCode:"34b4b714f3fc6b61d9ba907f4d1fdc73",
            primaryKey:true,
            name:"itemID",
            type:"sequence"
        },
        {
            title:"Item",
            columnCode:"3e3640c36bf50aec770a87493828e76d",
            name:"itemName",
            length:128,
            type:"text",
            required:true
        },
        {
            title:"SKU",
            columnCode:"f8c461fd1f0a234e5df4bb9d6fecc69a",
            name:"SKU",
            length:10,
            type:"integer",
            required:true
        },
        {
            title:"Description",
            columnCode:"67daf92c833c41c95db874e18fcb2786",
            name:"description",
            length:2000,
            type:"text"
        },
        {
            title:"Category",
            columnCode:"c4ef352f74e502ef5e7bc98e6f4e493d",
            name:"category",
            length:128,
            type:"text",
            required:true,
            foreignKey:"supplyCategory.categoryName"
        },
        {
            title:"Units",
            valueMap:[
                "Roll",
                "Ea",
                "Pkt",
                "Set",
                "Tube",
                "Pad",
                "Ream",
                "Tin",
                "Bag",
                "Ctn",
                "Box"
            ],
            columnCode:"b98b3dfbd27e710e6c3ceeae58770b52",
            name:"units",
            length:5,
            type:"enum"
        },
        {
            title:"Unit Cost",
            validators:[
                {
                    errorMessage:"Please enter a valid (positive) cost",
                    min:0.0,
                    type:"floatRange"
                },
                {
                    errorMessage:"The maximum allowed precision is 2",
                    precision:2,
                    type:"floatPrecision"
                }
            ],
            columnCode:"76607b9a6a5e033d4d1e96784fbd51ec",
            name:"unitCost",
            type:"float",
            required:true
        },
        {
            title:"In Stock",
            columnCode:"ffc89a82f900f437b5b5323b9091618d",
            name:"inStock",
            type:"boolean"
        },
        {
            title:"Next Shipment",
            columnCode:"2bd0c01cc850d2b21dd96f2cc3284120",
            name:"nextShipment",
            type:"date"
        }
    ]
})
