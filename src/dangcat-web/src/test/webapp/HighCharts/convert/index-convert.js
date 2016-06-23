var page = require('webpage').create();
page.viewportSize = { width: 1440, height: 2000 };
page.clipRect = { width: 1440, height: 2000 };
page.open('../index.html', function () {
    page.render('index.png');
    phantom.exit();
});