// Read the Phantom webpage '#intro' element text using jQuery and "includeJs"

var page = require('webpage').create();

page.onConsoleMessage = function(msg) {
    console.log(msg);
};

page.open("about:blank", function(status) {
    if ( status === "success" ) {
		console.log("includeJs1");
		page.includeJs("file:///D:/Work/Workspace/HighCharts/Tutorial/js/jquery-1.9.1.min.js", function() {
			console.log("includeJs2");
			page.evaluate(function() {
				$("body").append("<script type=\"text/javascript\" src=\"file:///D:/Work/Workspace/HighCharts/Tutorial/js/jquery.dangcat-1.0.js\"></script>");
				$("body").append("<script type=\"text/javascript\" src=\"file:///D:/Work/Workspace/HighCharts/Tutorial/js/highcharts-3.0.7/highcharts.min.js\"></script>");
				$("body").append("<script type=\"text/javascript\" src=\"file:///D:/Work/Workspace/HighCharts/Tutorial/js/highcharts-dangcat-1.0.js\"></script>");
				page.evaluate(function() {
					console.log("$(\"body\").html() -> " + window.HighChartsFactory);
					phantom.exit();
				});
			});
		});
        console.log("end");
    }
});

