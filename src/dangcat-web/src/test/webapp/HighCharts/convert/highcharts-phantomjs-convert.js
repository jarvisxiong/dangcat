/**
 * @license Highcharts JS v3.0.1 (2012-11-02)
 *
 * (c) 20013-2014
 *
 * Author: Gert Vaartjes
 *
 * License: www.highcharts.com/license
 *
 * version: 2.0.1
 */
/*jslint white: true */
/*global window, require, phantom, console, $, document, Image, Highcharts, clearTimeout, clearInterval, cb,*/

(function () {
    var config = {
            /* define locations of mandatory javascript files */
            jsFiles: ['js/jquery-1.9.1.min.js', 'js/jquery.dangcat-1.0.js', 'js/highcharts-3.0.7/highcharts.min.js', 'js/highcharts-dangcat-1.0.js'],
            TIMEOUT: 2000 /* 2 seconds timout for loading images */
        },
        args,
        dpiCorrection = 1.4,
        system = require('system'),
        fs = require('fs');

    var pick = function () {
        for (var i = 0; i < arguments.length; i++) {
            var arg = arguments[i];
            if (arg !== undefined && arg !== null && arg !== 'null' && arg != '0')
                return arg;
        }
    };

    var readParamMap = function () {
        var paramMap = {};

        if (system.args.length < 1)
            console.log('Commandline Usage: highcharts-phantomjs-convert.js -infile URL -outfile filename -scale 2.5 -width 300 -constr Chart -callback callback.js');

        for (var i = 0; i < system.args.length; i++) {
            if (system.args[i].charAt(0) === '-') {
                var key = system.args[i].substr(1, i.length);
                if (key === 'infile' || key === 'callback') {
                    // get string from file
                    try {
                        paramMap[key] = fs.read(system.args[i + 1]);
                    } catch (e) {
                        console.log('Error: cannot find file, ' + system.args[i + 1]);
                        phantom.exit();
                    }
                } else
                    paramMap[key] = system.args[i + 1];
            }
        }
        return paramMap;
    };

    var render = function (params, exitCallback) {
        var page = require('webpage').create(),
            timer;

        var messages = {
            imagesLoaded: 'Highcharts.images.loaded',
            optionsParsed: 'Highcharts.options.parsed',
            callbackParsed: 'Highcharts.cb.parsed'
        };

        window.imagesLoaded = false;
        window.optionsParsed = false;
        window.callbackParsed = false;

        page.onConsoleMessage = function (message) {
            if (message === messages.imagesLoaded)
                window.imagesLoaded = true;
            /* more ugly hacks, to check options or callback are properly parsed */
            if (message === messages.optionsParsed)
                window.optionsParsed = true;
            if (message === messages.callbackParsed)
                window.callbackParsed = true;
        };

        page.onAlert = function (msg) {
            console.log(msg);
        };

        /* scale and clip the page */
        var scaleAndClipPage = function (svg) {
            // param: svg: The scg configuration object
            var zoom = 1,
                pageWidth = pick(params.width, svg.width);

            if (parseInt(pageWidth, 10) == pageWidth)
                zoom = pageWidth / svg.width;

            /*
             set this line when scale factor has a higher precedence
             scale has precedence : page.zoomFactor = params.scale  ? zoom * params.scale : zoom;

             params.width has a higher precedence over scaling, to not break backover compatibility
             */
            page.zoomFactor = params.scale && params.width == undefined ? zoom * params.scale : zoom;

            var clipWidth = svg.width * page.zoomFactor;
            var clipHeight = svg.height * page.zoomFactor;

            /*
             define the clip-rectangle
             ignored for PDF, see https://github.com/ariya/phantomjs/issues/10465
             */
            page.clipRect = {
                top: 0,
                left: 0,
                width: clipWidth,
                height: clipHeight
            };

            // for pdf we need a bit more paperspace in some cases for example (w:600,h:400), I don't know why.
            if (outType === 'pdf') {
                // changed to a multiplication with 1.333 to correct systems dpi setting
                clipWidth = clipWidth * dpiCorrection;
                clipHeight = clipHeight * dpiCorrection;
                // redefine the viewport
                page.viewportSize = { width: clipWidth, height: clipHeight};
                // make the paper a bit larger than the viewport
                page.paperSize = { width: clipWidth + 2, height: clipHeight + 2 };
            }
        };

        var exit = function (result) {
            exitCallback(result);
        };

        var convert = function (svg) {
            scaleAndClipPage(svg);
            if (output === undefined)
                output = config.tmpDir + '/chart.' + outType;
            page.render(output);
            exit(output);
        };

        var renderSVG = function (svg) {
            // From this point we have loaded/or created a SVG
            try {
                // output binary images or pdf
                if (!window.imagesLoaded) {
                    // render with interval, waiting for all images loaded
                    var interval = window.setInterval(function () {
                        console.log('waiting');
                        if (window.imagesLoaded) {
                            clearTimeout(timer);
                            clearInterval(interval);
                            convert(svg);
                        }
                    }, 50);

                    // we have a 3 second timeframe..
                    timer = window.setTimeout(function () {
                        clearInterval(interval);
                        exitCallback('ERROR: While rendering, there\'s is a timeout reached');
                    }, config.TIMEOUT);
                } else {
                    // images are loaded, render rightaway
                    convert(svg);
                }
            } catch (e) {
                console.log('ERROR: While rendering, ' + e);
            }
        };

        var createChart = function (width, constr, input, outputType, callback, messages) {
            var counter;

            // dynamic script insertion
            function loadScript(varStr, codeStr) {
                var $script = $('<script>').attr('type', 'text/javascript');
                $script.html('var ' + varStr + ' = ' + codeStr);
                document.getElementsByTagName("head")[0].appendChild($script[0]);
                if (window[varStr] !== undefined)
                    console.log('Highcharts.' + varStr + '.parsed');
            }

            function loadScriptFile(file) {
                var $script = $('<script>').attr('type', 'text/javascript');
                $script.attr('src', file);
                document.getElementsByTagName("head")[0].appendChild($script[0]);
                console.log("loadScriptFile : " + document.getElementsByTagName("head").innerHTML);
            }

            // are all images loaded in time?
            function loadingImage() {
                console.log('loading image ' + counter);
                counter -= 1;
                if (counter < 1)
                    console.log(messages.imagesLoaded);
            }

            function loadImages() {
                // are images loaded?
                var $images = $('svg image');
                if ($images.length > 0) {
                    counter = $images.length;
                    for (var i = 0; i < $images.length; i++) {
                        var image = new Image();
                        image.onload = loadingImage;
                        // force loading of images by setting the src attr.
                        image.src = $images[i].getAttribute('href');
                    }
                } else {
                    // no images set property to all images
                    // loaded
                    console.log(messages.imagesLoaded);
                }
            }

            if (input !== 'undefined')
                loadScript('inputOptions', input);

            if (callback !== 'undefined')
                loadScript('cb', callback);

            $(document.body).css('margin', '0px');

            if (outputType === 'jpeg')
                $(document.body).css('backgroundColor', 'white');

            var $container = $('<div>').appendTo(document.body);
            $container.attr('id', 'container');

            // disable animations
            Highcharts.SVGRenderer.prototype.Element.prototype.animate = Highcharts.SVGRenderer.prototype.Element.prototype.attr;

            // Load data
            var chartOptions = HighChartsFactory.createHighChart($container, inputOptions.options, inputOptions.data);

            // ensure images are all loaded
            loadImages();

            /*
             remove stroke-opacity paths, used by mouse-trackers, they turn up as
             as fully opaque in the PDF
             */
            var nodes = document.querySelectorAll('*[stroke-opacity]');
            for (var i = 0; i < nodes.length; i++) {
                var element = nodes[i];
                var opacity = element.getAttribute('stroke-opacity');
                element.removeAttribute('stroke-opacity');
                element.setAttribute('opacity', opacity);
            }

            return {
                html: $('div.highcharts-container')[0].innerHTML,
                width: chartOptions.highChart.chartWidth,
                height: chartOptions.highChart.chartHeight
            };
        }

        function beginRender() {
            // load chart in page and return svg height and width
            var svg = page.evaluate(createChart, width, constr, input, outType, callback, messages);

            if (!window.optionsParsed)
                exit('ERROR: the options variable was not available, contains the infile an syntax error? see' + input);

            if (callback !== undefined && !window.callbackParsed)
                exit('ERROR: the callback variable was not available, contains the callbackfile an syntax error? see' + callback);
            renderSVG(svg);
        };

        if (params.length < 1)
            exit("Error: Insuficient parameters");
        else {
            var outType;
            var output = params.outfile;
            if (output !== undefined)
                outType = pick(output.split('.').pop(), 'png');
            else
                outType = pick(params.type, 'png');

            var constr = pick(params.constr, 'Chart');
            var callback = params.callback;
            var width = params.width;

            var input = params.infile;
            if (input === undefined || input.length === 0)
                exit('Error: Insuficient or wrong parameters for rendering');

            page.open('about:blank', function (status) {
                // load necessary libraries
//                for (var i = 0; i < config.jsFiles.length; i++){
//                    if (!page.injectJs(config.jsFiles[i]))
//                        console.log('ERROR: inject js error ' + config.jsFiles[i]);
//                }
                //['jquery-1.9.1.min.js', 'jquery.dangcat-1.0.js', 'highcharts-3.0.7/highcharts.min.js', 'highcharts-dangcat-1.0.js'];
                var instance = this;

                console.log("includeJs1");
                page.includeJs("file:///D:/Work/Workspace/HighCharts/Tutorial/js/jquery-1.9.1.min.js", function() {
                    console.log("includeJs2");
                    page.includeJs("file:///D:/Work/Workspace/HighCharts/Tutorial/js/jquery.dangcat-1.0.js", function() {
                        console.log("includeJs3");
                        page.includeJs("file:///D:/Work/Workspace/HighCharts/Tutorial/js/highcharts-3.0.7/highcharts.min.js", function() {
                            console.log("includeJs4");
                            page.includeJs("file:///D:/Work/Workspace/HighCharts/Tutorial/js/highcharts-3.0.7/highcharts-dangcat-1.0.js", function() {
                                console.log("includeJs5");
                                page.evaluate(function() {
                                    $("body").append("<div></div>");
                                    console.log("$(\"body\").html() -> " + $("body").html());
                                });
                                phantom.exit();
                            });
                        });
                    });
                });

//                console.log("page.injectJs begin" + document.getElementsByTagName("head").innerHTML);
//                page.injectJs("jquery-1.9.1.min.js", function() {
//                    console.log("load jquery-1.9.1.min.js");
//                    page.injectJs("jquery.dangcat-1.0.js", function() {
//                        console.log("load jquery.dangcat-1.0.js");
//                        page.injectJs("highcharts-3.0.7/highcharts.min.js", function() {
//                            console.log("load highcharts-3.0.7/highcharts.min.js");
//                            page.injectJs("highcharts-dangcat-1.0.js", function() {
//                                console.log("load highcharts-dangcat-1.0.js");
//                                instance.beginRender();
//                            });
//                        });
//                    });
//                });
//                console.log("page.injectJs end");

//                var jsFiles= ['jquery-1.9.1.min.js', 'jquery.dangcat-1.0.js', 'highcharts-3.0.7/highcharts.min.js', 'highcharts-dangcat-1.0.js'];
//                for (var i = 0; i < jsFiles.length; i++)
//                {
//                    document.getElementsByTagName("head")[0].appendChild("<script type=\"text/javascript\" src=\""+jsFiles[i]+"\"/>");
//                    console.log(jsFiles[i]);
//                }
//                page.injectJs('jquery-1.9.1.min.js');
//                page.injectJs('jquery.dangcat-1.0.js');
//                page.injectJs('highcharts-3.0.7/highcharts.min.js');
//                page.injectJs('highcharts-dangcat-1.0.js');
//                console.log( $(document.body).length);
//
//                // load chart in page and return svg height and width
//                var svg = page.evaluate(createChart, width, constr, input, outType, callback, messages);
//
//                if (!window.optionsParsed)
//                    exit('ERROR: the options variable was not available, contains the infile an syntax error? see' + input);
//
//                if (callback !== undefined && !window.callbackParsed)
//                    exit('ERROR: the callback variable was not available, contains the callbackfile an syntax error? see' + callback);
//                renderSVG(svg);
                exitCallback("AAA");
            });
        }
    };

    args = readParamMap();

    // set tmpDir, for output temporary files.
    if (args.tmpdir === undefined)
        config.tmpDir = (system.env["TEMP"] || system.env["TMP"] ) + '/highcharts_tmp';
    else
        config.tmpDir = args.tmpdir;

    // exists tmpDir and is it writable?
    if (!fs.exists(config.tmpDir)) {
        try {
            fs.makeDirectory(config.tmpDir);
        } catch (e) {
            console.log('ERROR: Cannot make temp directory');
        }
    }
    console.log('INFO: current work directory ' + fs.workingDirectory);
    console.log('INFO: current temp directory ' + config.tmpDir);
    // presume commandline usage
    render(args, function (message) {
        console.log(message);
        phantom.exit();
    });
}());
