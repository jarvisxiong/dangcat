isc.changeTheme = function (theWindow) {
    if (theWindow == null) theWindow = window;
    with (theWindow) {
        if (isc.StatusBar) {
            isc.StatusBar.addProperties({
                height: 30,
                padding: 2,
                membersMargin: 2,
                defaultItemProperties: {
                    layoutLeftMargin: 10,
                    height: 24
                },
                defaultFormProperties: {
                    border: "1px solid #D3D3D3;"
                },
                defaultFieldProperties: {
                    height: 18
                }
            });
        }
        if (isc.ViewForm) {
            isc.ViewForm.addProperties({
                formBorder: "1px solid #A6ABB4"
            });
        }
        if (isc.DataGridToolBar) {
            isc.DataGridToolBar.addProperties({
                height: 26
            });
        }
        if (isc.VBorderPanel) {
            isc.VBorderPanel.addProperties({
                border: "1px solid #A7ABB4"
            });
        }
        if (isc.UserWindow) {
            isc.UserWindow.addProperties({
                height: 170
            });
        }
        if (isc.LogoLayout) {
            isc.LogoLayout.addProperties({
                projectLogoProperties: {
                    width: 120,
                    height: 50
                }
            });
        }
        if (isc.HeadLayout) {
            isc.HeadLayout.addProperties({
                contentLayoutProperties: {
                    layoutRightMargin: 30
                },
                portalMenuBarProperties: {
                    width: "*",
                    height: 25,
                    layoutLeftMargin: 15,
                    layoutRightMargin: 15,
                    membersMargin: 10,
                }
            });
        }
    }
}
isc.changeTheme();