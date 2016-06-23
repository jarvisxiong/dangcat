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
			page.injectJs("file:///D:/Work/Workspace/HighCharts/Tutorial/js/jquery.dangcat-1.0.js");
			page.injectJs("file:///D:/Work/Workspace/HighCharts/Tutorial/js/highcharts-3.0.7/highcharts.min.js");
			page.injectJs("file:///D:/Work/Workspace/HighCharts/Tutorial/js/highcharts-dangcat-1.0.js");
			page.evaluate(function() {
				var $container = $('<div>').appendTo(document.body);
				$container.attr('id', 'container');
				console.log("$(\"body\").html() -> " + $('body').html());
			});
			phantom.exit();
		});
        console.log("end");
    }
});

