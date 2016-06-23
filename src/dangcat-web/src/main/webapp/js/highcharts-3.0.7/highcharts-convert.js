var page = require('webpage').create();
var system = require('system');

var exportChart = function (url, exportFileName, width, height) {
    var outType = exportFileName.substr(exportFileName.length - 4);
    if (outType === '.pdf') {
        var dpiCorrection = 1.4;
        // changed to a multiplication with 1.333 to correct systems dpi setting
        width = width * dpiCorrection;
        height = height * dpiCorrection;
        // redefine the viewport
        page.viewportSize = { width: width, height: height};
        // make the paper a bit larger than the viewport
        page.paperSize = { width: width + 2, height: height + 2 };
    }
    else {
        page.clipRect = { top: 0, left: 0, width: width, height: height };
        page.viewportSize = { width: width, height: height };
    }
    console.log('Open the url: ' + url);
    page.open(url, function () {
        console.log('Convert to: ' + exportFileName);
        page.render(exportFileName);
        console.log('Convert successful.');
        phantom.exit();
    });
}

if (system.args.length < 3) {
    console.log('Commandline Usage: highcharts-convert.js URL exportFileName width height');
    phantom.exit();
} else {
    var url = system.args[1];
    var exportFileName = system.args[2];

    var width = 800;
    if (system.args.length > 3) {
        var chartWidth = parseInt(system.args[3]);
        if (chartWidth > 0)
            width = chartWidth;
    }

    var height = 600;
    if (system.args.length > 4) {
        var chartHeight = parseInt(system.args[4]);
        if (chartHeight > 0)
            height = chartHeight;
    }

    exportChart(url, exportFileName, width, height);
}
