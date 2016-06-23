/*
 * jssonformat 
 */
(function ($) {
    var space = "    ";
    var changeLine = "\r\n";

    $.defined = function (obj) {
        return obj != undefined && obj != null;
    };

    $.isArray = function (obj) {
        return obj instanceof Array;
    }

    $.isString = function (obj) {
        return typeof (obj) == "string";
    }

    $.isNumber = function (obj) {
        return typeof (obj) == "number";
    }

    $.isObject = function (obj) {
        return typeof (obj) == "object";
    }

    $.isFunction = function (obj) {
        return typeof(obj) === "function";
    }

    $.isBoolean = function (obj) {
        return typeof(obj) === "boolean";
    }

    $.formatJSON = function (data) {
        return $.formatJsonData(data, null, "");
    };

    $.formatJsonData = function (data, name, preSpace) {
        var result = "";
        if (data == null) {
            if (name != null)
                result = "\"" + name + "\" :";
            result += "null";
        }
        else if ($.isArray(data)) {
            var text = null;
            for (var i = 0; i < data.length; i++) {
                if (text == null) {
                    if (name != null)
                        text = "\"" + name + "\" : [";
                    else
                        text = "[";
                }
                else
                    text += ",";
                text += changeLine;
                text += $.formatJsonData(data[i], null, preSpace + space);
            }
            if (text != null)
                result = text + changeLine + preSpace + "]";
        }
        else if ($.isDate(data)) {
            if (name != null)
                result = "\"" + name + "\" : ";
            else
                result = space;
            result += "\"" + data.format() + "\"";
        }
        else if ($.isObject(data)) {
            var text = null;
            for (var childName in data) {
                var formatText = $.formatJsonData(data[childName], childName, preSpace + space);
                if (formatText != null && !formatText.isEmpty()) {
                    if (text == null) {
                        if (name != null)
                            text = "\"" + name + "\" : {";
                        else
                            text = "{";
                    }
                    else
                        text += ",";
                    text += changeLine + formatText;
                }
            }
            if (text != null)
                result = text + changeLine + preSpace + "}";
        }
        else {
            if (name != null)
                result = "\"" + name + "\" : ";
            else
                result = space;
            if ($.isNumber(data) || $.isBoolean(data) || $.isFunction(data))
                result += data;
            else
                result += "\"" + data + "\"";
        }
        return preSpace + result;
    };

    $.encryptPassword = function (no, password, encryptAlgorithm) {
        var decryptPassword = password;
        if (encryptAlgorithm == undefined || encryptAlgorithm == null || encryptAlgorithm == "" || encryptAlgorithm == "MD5")
            decryptPassword = CryptoJS.MD5(no + password) + "";
        return $.encryptContent(decryptPassword);
    };

    $.encryptContent = function (content) {
        var key = CryptoJS.MD5(isc.ApplicationContext.sessionId);
        var encryptConfig = {
            iv: key,
            mode: CryptoJS.mode.CFB,
            padding: CryptoJS.pad.NoPadding
        };

        return CryptoJS.AES.encrypt(content, key, encryptConfig).toString();
    };

    $.decryptContent = function (content) {
        var key = CryptoJS.MD5(isc.ApplicationContext.sessionId);
        var encryptConfig = {
            iv: key,
            mode: CryptoJS.mode.CFB,
            padding: CryptoJS.pad.NoPadding
        };
        return CryptoJS.AES.decrypt(content, key, encryptConfig).toString(CryptoJS.enc.Utf8);
    };

    $.extendObject = function () {
        var instance = {};
        if (arguments && arguments.length > 0) {
            for (var i = 0; i < arguments.length; i++)
                $.extendInstance(instance, arguments[i]);
        }
        return instance;
    };

    $.extendInstance = function () {
        var instance = arguments[0];
        for (var i = 1; i < arguments.length; i++) {
            var source = arguments[i];
            if (source && $.isObject(source)) {
                for (var name in source) {
                    var value = source[name];
                    if (!$.defined(value) || $.isArray(value) || $.isString(value) || $.isDate(value) || $.isFunction(value))
                        instance[name] = $.cloneInstance(value);
                    else if (value && $.isObject(value)) {
                        if (instance[name] == undefined || instance[name] == null)
                            instance[name] = $.cloneInstance(value);
                        else
                            $.extendInstance(instance[name], value);
                    }
                    else
                        instance[name] = $.cloneInstance(value);
                }
            }
        }
    };

    $.cloneInstance = function (instance) {
        if (!$.defined(instance) || $.isString(instance) || $.isDate(instance) || $.isFunction(instance))
            return instance;

        if (instance instanceof Array) {
            var array = [];
            for (var i = 0; i < instance.length; i++)
                array.push($.cloneInstance(instance[i]));
            return array;
        }

        if ($.isObject(instance)) {
            var newInstance = {};
            $.extendInstance(newInstance, instance);
            return newInstance;
        }
        return instance;
    };

    $.concatArray = function () {
        var array = [];
        for (var i = 0; i < arguments.length; i++) {
            if ($.defined(arguments[i])) {
                if ($.isArray(arguments[i])) {
                    for (var j = 0; j < arguments[i].length; j++) {
                        if ($.defined(arguments[i][j]))
                            array.push(arguments[i][j]);
                    }
                } else
                    array.push(arguments[i]);
            }
        }
        return array;
    }
})(jQuery);
//*/

/*!
 * jQuery Globalization Plugin
 * http://github.com/jquery/jquery-global
 *
 * Copyright Software Freedom Conservancy, Inc.
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 */
(function () {

    var Globalization = {}, localized = { en: {} };
    localized["default"] = localized.en;

    Globalization.extend = function (deep) {
        var target = arguments[ 1 ] || {};
        for (var i = 2, l = arguments.length; i < l; i++) {
            var source = arguments[ i ];
            if (source) {
                for (var field in source) {
                    var sourceVal = source[ field ];
                    if (typeof sourceVal !== "undefined") {
                        if (deep && (isObject(sourceVal) || isArray(sourceVal))) {
                            var targetVal = target[ field ];
                            // extend onto the existing value, or create a new one
                            targetVal = targetVal && (isObject(targetVal) || isArray(targetVal))
                                ? targetVal
                                : (isArray(sourceVal) ? [] : {});
                            target[ field ] = this.extend(true, targetVal, sourceVal);
                        }
                        else {
                            target[ field ] = sourceVal;
                        }
                    }
                }
            }
        }
        return target;
    }

    Globalization.findClosestCulture = function (name) {
        var match;
        if (!name) {
            return this.culture || this.cultures["default"];
        }
        if (isString(name)) {
            name = name.split(',');
        }
        if (isArray(name)) {
            var lang,
                cultures = this.cultures,
                list = name,
                i, l = list.length,
                prioritized = [];
            for (i = 0; i < l; i++) {
                name = trim(list[ i ]);
                var pri, parts = name.split(';');
                lang = trim(parts[ 0 ]);
                if (parts.length === 1) {
                    pri = 1;
                }
                else {
                    name = trim(parts[ 1 ]);
                    if (name.indexOf("q=") === 0) {
                        name = name.substr(2);
                        pri = parseFloat(name, 10);
                        pri = isNaN(pri) ? 0 : pri;
                    }
                    else {
                        pri = 1;
                    }
                }
                prioritized.push({ lang: lang, pri: pri });
            }
            prioritized.sort(function (a, b) {
                return a.pri < b.pri ? 1 : -1;
            });
            for (i = 0; i < l; i++) {
                lang = prioritized[ i ].lang;
                match = cultures[ lang ];
                // exact match?
                if (match) {
                    return match;
                }
            }
            for (i = 0; i < l; i++) {
                lang = prioritized[ i ].lang;
                // for each entry try its neutral language
                do {
                    var index = lang.lastIndexOf("-");
                    if (index === -1) {
                        break;
                    }
                    // strip off the last part. e.g. en-US => en
                    lang = lang.substr(0, index);
                    match = cultures[ lang ];
                    if (match) {
                        return match;
                    }
                }
                while (1);
            }
        }
        else if (typeof name === 'object') {
            return name;
        }
        return match || null;
    }
    Globalization.preferCulture = function (name) {
        this.culture = this.findClosestCulture(name) || this.cultures["default"];
    }
    Globalization.localize = function (key, culture, value) {
        // usign default culture in case culture is not provided
        if (typeof culture !== 'string') {
            culture = this.culture.name || this.culture || "default";
        }
        culture = this.cultures[ culture ] || { name: culture };

        var local = localized[ culture.name ];
        if (arguments.length === 3) {
            if (!local) {
                local = localized[ culture.name ] = {};
            }
            local[ key ] = value;
        }
        else {
            if (local) {
                value = local[ key ];
            }
            if (typeof value === 'undefined') {
                var language = localized[ culture.language ];
                if (language) {
                    value = language[ key ];
                }
                if (typeof value === 'undefined') {
                    value = localized["default"][ key ];
                }
            }
        }
        return typeof value === "undefined" ? null : value;
    }
    Globalization.format = function (value, format, culture) {
        culture = this.findClosestCulture(culture);
        if (typeof value === "number") {
            value = formatNumber(value, format, culture);
        }
        else if (value instanceof Date) {
            value = formatDate(value, format, culture);
        }
        return value;
    }
    Globalization.parseInt = function (value, radix, culture) {
        return Math.floor(this.parseFloat(value, radix, culture));
    }

    Globalization.parseFloat = function (value, radix, culture) {
        // make radix optional
        if (typeof radix === "string") {
            culture = radix;
            radix = 10;
        }

        culture = this.findClosestCulture(culture);
        var ret = NaN,
            nf = culture.numberFormat;

        if (value.indexOf(culture.numberFormat.currency.symbol) > -1) {
            // remove currency symbol
            value = value.replace(culture.numberFormat.currency.symbol, "");
            // replace decimal seperator
            value = value.replace(culture.numberFormat.currency["."], culture.numberFormat["."]);
        }

        // trim leading and trailing whitespace
        value = trim(value);

        // allow infinity or hexidecimal
        if (regexInfinity.test(value)) {
            ret = parseFloat(value, radix);
        }
        else if (!radix && regexHex.test(value)) {
            ret = parseInt(value, 16);
        }
        else {
            var signInfo = parseNegativePattern(value, nf, nf.pattern[0]),
                sign = signInfo[0],
                num = signInfo[1];
            // determine sign and number
            if (sign === "" && nf.pattern[0] !== "-n") {
                signInfo = parseNegativePattern(value, nf, "-n");
                sign = signInfo[0];
                num = signInfo[1];
            }
            sign = sign || "+";
            // determine exponent and number
            var exponent,
                intAndFraction,
                exponentPos = num.indexOf('e');
            if (exponentPos < 0) exponentPos = num.indexOf('E');
            if (exponentPos < 0) {
                intAndFraction = num;
                exponent = null;
            }
            else {
                intAndFraction = num.substr(0, exponentPos);
                exponent = num.substr(exponentPos + 1);
            }
            // determine decimal position
            var integer,
                fraction,
                decSep = nf['.'],
                decimalPos = intAndFraction.indexOf(decSep);
            if (decimalPos < 0) {
                integer = intAndFraction;
                fraction = null;
            }
            else {
                integer = intAndFraction.substr(0, decimalPos);
                fraction = intAndFraction.substr(decimalPos + decSep.length);
            }
            // handle groups (e.g. 1,000,000)
            var groupSep = nf[","];
            integer = integer.split(groupSep).join('');
            var altGroupSep = groupSep.replace(/\u00A0/g, " ");
            if (groupSep !== altGroupSep) {
                integer = integer.split(altGroupSep).join('');
            }
            // build a natively parsable number string
            var p = sign + integer;
            if (fraction !== null) {
                p += '.' + fraction;
            }
            if (exponent !== null) {
                // exponent itself may have a number patternd
                var expSignInfo = parseNegativePattern(exponent, nf, "-n");
                p += 'e' + (expSignInfo[0] || "+") + expSignInfo[1];
            }
            if (regexParseFloat.test(p)) {
                ret = parseFloat(p);
            }
        }
        return ret;
    }
    Globalization.parseDate = function (value, formats, culture) {
        culture = this.findClosestCulture(culture);

        var date, prop, patterns;
        if (formats) {
            if (typeof formats === "string") {
                formats = [ formats ];
            }
            if (formats.length) {
                for (var i = 0, l = formats.length; i < l; i++) {
                    var format = formats[ i ];
                    if (format) {
                        date = parseExact(value, format, culture);
                        if (date) {
                            break;
                        }
                    }
                }
            }
        }
        else {
            patterns = culture.calendar.patterns;
            for (prop in patterns) {
                date = parseExact(value, patterns[prop], culture);
                if (date) {
                    break;
                }
            }
        }
        return date || null;
    }

// 1.    When defining a culture, all fields are required except the ones stated as optional.
// 2.    You can use Globalization.extend to copy an existing culture and provide only the differing values,
//       a good practice since most cultures do not differ too much from the 'default' culture.
//       DO use the 'default' culture if you do this, as it is the only one that definitely
//       exists.
// 3.    Other plugins may add to the culture information provided by extending it. However,
//       that plugin may extend it prior to the culture being defined, or after. Therefore,
//       do not overwrite values that already exist when defining the baseline for a culture,
//       by extending your culture object with the existing one.
// 4.    Each culture should have a ".calendars" object with at least one calendar named "standard"
//       which serves as the default calendar in use by that culture.
// 5.    Each culture should have a ".calendar" object which is the current calendar being used,
//       it may be dynamically changed at any time to one of the calendars in ".calendars".

// To define a culture, use the following pattern, which handles defining the culture based
// on the 'default culture, extending it with the existing culture if it exists, and defining
// it if it does not exist.
// Globalization.cultures.foo = Globalization.extend(true, Globalization.extend(true, {}, Globalization.cultures['default'], fooCulture), Globalization.cultures.foo)

    var cultures = Globalization.cultures = Globalization.cultures || {};
    var en = cultures["default"] = cultures.en = Globalization.extend(true, {
        // A unique name for the culture in the form <language code>-<country/region code>
        name: "en",
        // the name of the culture in the english language
        englishName: "English",
        // the name of the culture in its own language
        nativeName: "English",
        // whether the culture uses right-to-left text
        isRTL: false,
        // 'language' is used for so-called "specific" cultures.
        // For example, the culture "es-CL" means "Spanish, in Chili".
        // It represents the Spanish-speaking culture as it is in Chili,
        // which might have different formatting rules or even translations
        // than Spanish in Spain. A "neutral" culture is one that is not
        // specific to a region. For example, the culture "es" is the generic
        // Spanish culture, which may be a more generalized version of the language
        // that may or may not be what a specific culture expects.
        // For a specific culture like "es-CL", the 'language' field refers to the
        // neutral, generic culture information for the language it is using.
        // This is not always a simple matter of the string before the dash.
        // For example, the "zh-Hans" culture is netural (Simplified Chinese).
        // And the 'zh-SG' culture is Simplified Chinese in Singapore, whose lanugage
        // field is "zh-CHS", not "zh".
        // This field should be used to navigate from a specific culture to it's
        // more general, neutral culture. If a culture is already as general as it
        // can get, the language may refer to itself.
        language: "en",
        // numberFormat defines general number formatting rules, like the digits in
        // each grouping, the group separator, and how negative numbers are displayed.
        numberFormat: {
            // [negativePattern]
            // Note, numberFormat.pattern has no 'positivePattern' unlike percent and currency,
            // but is still defined as an array for consistency with them.
            //  negativePattern: one of "(n)|-n|- n|n-|n -"
            pattern: ["-n"],
            // number of decimal places normally shown
            decimals: 2,
            // string that separates number groups, as in 1,000,000
            ',': ",",
            // string that separates a number from the fractional portion, as in 1.99
            '.': ".",
            // array of numbers indicating the size of each number group.
            // TODO: more detailed description and example
            groupSizes: [3],
            // symbol used for positive numbers
            '+': "+",
            // symbol used for negative numbers
            '-': "-",
            percent: {
                // [negativePattern, positivePattern]
                //     negativePattern: one of "-n %|-n%|-%n|%-n|%n-|n-%|n%-|-% n|n %-|% n-|% -n|n- %"
                //     positivePattern: one of "n %|n%|%n|% n"
                pattern: ["-n %", "n %"],
                // number of decimal places normally shown
                decimals: 2,
                // array of numbers indicating the size of each number group.
                // TODO: more detailed description and example
                groupSizes: [3],
                // string that separates number groups, as in 1,000,000
                ',': ",",
                // string that separates a number from the fractional portion, as in 1.99
                '.': ".",
                // symbol used to represent a percentage
                symbol: "%"
            },
            currency: {
                // [negativePattern, positivePattern]
                //     negativePattern: one of "($n)|-$n|$-n|$n-|(n$)|-n$|n-$|n$-|-n $|-$ n|n $-|$ n-|$ -n|n- $|($ n)|(n $)"
                //     positivePattern: one of "$n|n$|$ n|n $"
                pattern: ["($n)", "$n"],
                // number of decimal places normally shown
                decimals: 2,
                // array of numbers indicating the size of each number group.
                // TODO: more detailed description and example
                groupSizes: [3],
                // string that separates number groups, as in 1,000,000
                ',': ",",
                // string that separates a number from the fractional portion, as in 1.99
                '.': ".",
                // symbol used to represent currency
                symbol: "$"
            }
        },
        // calendars defines all the possible calendars used by this culture.
        // There should be at least one defined with name 'standard', and is the default
        // calendar used by the culture.
        // A calendar contains information about how dates are formatted, information about
        // the calendar's eras, a standard set of the date formats,
        // translations for day and month names, and if the calendar is not based on the Gregorian
        // calendar, conversion functions to and from the Gregorian calendar.
        calendars: {
            standard: {
                // name that identifies the type of calendar this is
                name: "Gregorian_USEnglish",
                // separator of parts of a date (e.g. '/' in 11/05/1955)
                '/': "/",
                // separator of parts of a time (e.g. ':' in 05:44 PM)
                ':': ":",
                // the first day of the week (0 = Sunday, 1 = Monday, etc)
                firstDay: 0,
                days: {
                    // full day names
                    names: ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
                    // abbreviated day names
                    namesAbbr: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
                    // shortest day names
                    namesShort: ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"]
                },
                months: {
                    // full month names (13 months for lunar calendards -- 13th month should be "" if not lunar)
                    names: ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", ""],
                    // abbreviated month names
                    namesAbbr: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", ""]
                },
                // AM and PM designators in one of these forms:
                // The usual view, and the upper and lower case versions
                //      [standard,lowercase,uppercase]
                // The culture does not use AM or PM (likely all standard date formats use 24 hour time)
                //      null
                AM: ["AM", "am", "AM"],
                PM: ["PM", "pm", "PM"],
                eras: [
                    // eras in reverse chronological order.
                    // name: the name of the era in this culture (e.g. A.D., C.E.)
                    // start: when the era starts in ticks (gregorian, gmt), null if it is the earliest supported era.
                    // offset: offset in years from gregorian calendar
                    { "name": "A.D.", "start": null, "offset": 0 }
                ],
                // when a two digit year is given, it will never be parsed as a four digit
                // year greater than this year (in the appropriate era for the culture)
                // Set it as a full year (e.g. 2029) or use an offset format starting from
                // the current year: "+19" would correspond to 2029 if the current year 2010.
                twoDigitYearMax: 2029,
                // set of predefined date and time patterns used by the culture
                // these represent the format someone in this culture would expect
                // to see given the portions of the date that are shown.
                patterns: {
                    // short date pattern
                    d: "M/d/yyyy",
                    // long date pattern
                    D: "dddd, MMMM dd, yyyy",
                    // short time pattern
                    t: "h:mm tt",
                    // long time pattern
                    T: "h:mm:ss tt",
                    // long date, short time pattern
                    f: "dddd, MMMM dd, yyyy h:mm tt",
                    // long date, long time pattern
                    F: "dddd, MMMM dd, yyyy h:mm:ss tt",
                    // month/day pattern
                    M: "MMMM dd",
                    // month/year pattern
                    Y: "yyyy MMMM",
                    // S is a sortable format that does not vary by culture
                    S: "yyyy\u0027-\u0027MM\u0027-\u0027dd\u0027T\u0027HH\u0027:\u0027mm\u0027:\u0027ss"
                }
                // optional fields for each calendar:
                /*
                 monthsGenitive:
                 Same as months but used when the day preceeds the month.
                 Omit if the culture has no genitive distinction in month names.
                 For an explaination of genitive months, see http://blogs.msdn.com/michkap/archive/2004/12/25/332259.aspx
                 convert:
                 Allows for the support of non-gregorian based calendars. This convert object is used to
                 to convert a date to and from a gregorian calendar date to handle parsing and formatting.
                 The two functions:
                 fromGregorian(date)
                 Given the date as a parameter, return an array with parts [year, month, day]
                 corresponding to the non-gregorian based year, month, and day for the calendar.
                 toGregorian(year, month, day)
                 Given the non-gregorian year, month, and day, return a new Date() object
                 set to the corresponding date in the gregorian calendar.
                 */
            }
        }
    }, cultures.en);
    en.calendar = en.calendar || en.calendars.standard;

    var regexTrim = /^\s+|\s+$/g,
        regexInfinity = /^[+-]?infinity$/i,
        regexHex = /^0x[a-f0-9]+$/i,
        regexParseFloat = /^[+-]?\d*\.?\d*(e[+-]?\d+)?$/,
        toString = Object.prototype.toString;

    var cn = cultures["zh_CN"] = cultures.cn = Globalization.extend(true, {}, cultures.en, {
        name: "zh_CN",
        englishName: "Chinese",
        nativeName: "Chinese",
        language: "cn",
        calendars: {
            standard: {
                name: "Chinese",
                firstDay: 1,
                days: {
                    names: ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
                    namesAbbr: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],
                    namesShort: ["日", "一", "二", "三", "四", "五", "六"]
                },
                months: {
                    names: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月", ""],
                    namesAbbr: ["一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", ""]
                },
                AM: ["上午", "上午", "上午"],
                PM: ["下午", "下午", "下午"]
            }
        }
    });
    cn.calendar = cn.calendars.standard;

    function startsWith(value, pattern) {
        return value.indexOf(pattern) === 0;
    }

    function endsWith(value, pattern) {
        return value.substr(value.length - pattern.length) === pattern;
    }

    function trim(value) {
        return (value + "").replace(regexTrim, "");
    }

    function zeroPad(str, count, left) {
        for (var l = str.length; l < count; l++) {
            str = (left ? ('0' + str) : (str + '0'));
        }
        return str;
    }

    function isArray(obj) {
        return toString.call(obj) === "[object Array]";
    }

    function isString(obj) {
        return toString.call(obj) === "[object String]";
    }

    function isFunction(obj) {
        return toString.call(obj) === "[object Function]";
    }

    function isObject(obj) {
        return toString.call(obj) === "[object Object]";
    }

    function arrayIndexOf(array, item) {
        if (array.indexOf) {
            return array.indexOf(item);
        }
        for (var i = 0, length = array.length; i < length; i++) {
            if (array[ i ] === item) {
                return i;
            }
        }
        return -1;
    }

// *************************************** Numbers ***************************************

    function expandNumber(number, precision, formatInfo) {
        var groupSizes = formatInfo.groupSizes,
            curSize = groupSizes[ 0 ],
            curGroupIndex = 1,
            factor = Math.pow(10, precision),
            rounded = Math.round(number * factor) / factor;
        if (!isFinite(rounded)) {
            rounded = number;
        }
        number = rounded;

        var numberString = number + "",
            right = "",
            split = numberString.split(/e/i),
            exponent = split.length > 1 ? parseInt(split[ 1 ], 10) : 0;
        numberString = split[ 0 ];
        split = numberString.split(".");
        numberString = split[ 0 ];
        right = split.length > 1 ? split[ 1 ] : "";

        var l;
        if (exponent > 0) {
            right = zeroPad(right, exponent, false);
            numberString += right.slice(0, exponent);
            right = right.substr(exponent);
        }
        else if (exponent < 0) {
            exponent = -exponent;
            numberString = zeroPad(numberString, exponent + 1);
            right = numberString.slice(-exponent, numberString.length) + right;
            numberString = numberString.slice(0, -exponent);
        }

        if (precision > 0) {
            right = formatInfo['.'] +
                ((right.length > precision) ? right.slice(0, precision) : zeroPad(right, precision));
        }
        else {
            right = "";
        }

        var stringIndex = numberString.length - 1,
            sep = formatInfo[","],
            ret = "";

        while (stringIndex >= 0) {
            if (curSize === 0 || curSize > stringIndex) {
                return numberString.slice(0, stringIndex + 1) + ( ret.length ? ( sep + ret + right ) : right );
            }
            ret = numberString.slice(stringIndex - curSize + 1, stringIndex + 1) + ( ret.length ? ( sep + ret ) : "" );

            stringIndex -= curSize;

            if (curGroupIndex < groupSizes.length) {
                curSize = groupSizes[ curGroupIndex ];
                curGroupIndex++;
            }
        }
        return numberString.slice(0, stringIndex + 1) + sep + ret + right;
    }


    function parseNegativePattern(value, nf, negativePattern) {
        var neg = nf["-"],
            pos = nf["+"],
            ret;
        switch (negativePattern) {
            case "n -":
                neg = ' ' + neg;
                pos = ' ' + pos;
            // fall through
            case "n-":
                if (endsWith(value, neg)) {
                    ret = [ '-', value.substr(0, value.length - neg.length) ];
                }
                else if (endsWith(value, pos)) {
                    ret = [ '+', value.substr(0, value.length - pos.length) ];
                }
                break;
            case "- n":
                neg += ' ';
                pos += ' ';
            // fall through
            case "-n":
                if (startsWith(value, neg)) {
                    ret = [ '-', value.substr(neg.length) ];
                }
                else if (startsWith(value, pos)) {
                    ret = [ '+', value.substr(pos.length) ];
                }
                break;
            case "(n)":
                if (startsWith(value, '(') && endsWith(value, ')')) {
                    ret = [ '-', value.substr(1, value.length - 2) ];
                }
                break;
        }
        return ret || [ '', value ];
    }

    function formatNumber(value, format, culture) {
        if (!format || format === 'i') {
            return culture.name.length ? value.toLocaleString() : value.toString();
        }
        format = format || "D";

        var nf = culture.numberFormat,
            number = Math.abs(value),
            precision = -1,
            pattern;
        if (format.length > 1) precision = parseInt(format.slice(1), 10);

        var current = format.charAt(0).toUpperCase(),
            formatInfo;

        switch (current) {
            case "D":
                pattern = 'n';
                if (precision !== -1) {
                    number = zeroPad("" + number, precision, true);
                }
                if (value < 0) number = -number;
                break;
            case "N":
                formatInfo = nf;
            // fall through
            case "C":
                formatInfo = formatInfo || nf.currency;
            // fall through
            case "P":
                formatInfo = formatInfo || nf.percent;
                pattern = value < 0 ? formatInfo.pattern[0] : (formatInfo.pattern[1] || "n");
                if (precision === -1) precision = formatInfo.decimals;
                number = expandNumber(number * (current === "P" ? 100 : 1), precision, formatInfo);
                break;
            default:
                throw "Bad number format specifier: " + current;
        }

        var patternParts = /n|\$|-|%/g,
            ret = "";
        for (; ;) {
            var index = patternParts.lastIndex,
                ar = patternParts.exec(pattern);

            ret += pattern.slice(index, ar ? ar.index : pattern.length);

            if (!ar) {
                break;
            }

            switch (ar[0]) {
                case "n":
                    ret += number;
                    break;
                case "$":
                    ret += nf.currency.symbol;
                    break;
                case "-":
                    // don't make 0 negative
                    if (/[1-9]/.test(number)) {
                        ret += nf["-"];
                    }
                    break;
                case "%":
                    ret += nf.percent.symbol;
                    break;
            }
        }

        return ret;
    }

// *************************************** Dates ***************************************

    function outOfRange(value, low, high) {
        return value < low || value > high;
    }

    function expandYear(cal, year) {
        // expands 2-digit year into 4 digits.
        var now = new Date(),
            era = getEra(now);
        if (year < 100) {
            var twoDigitYearMax = cal.twoDigitYearMax;
            twoDigitYearMax = typeof twoDigitYearMax === 'string' ? new Date().getFullYear() % 100 + parseInt(twoDigitYearMax, 10) : twoDigitYearMax;
            var curr = getEraYear(now, cal, era);
            year += curr - ( curr % 100 );
            if (year > twoDigitYearMax) {
                year -= 100;
            }
        }
        return year;
    }

    function getEra(date, eras) {
        if (!eras) return 0;
        var start, ticks = date.getTime();
        for (var i = 0, l = eras.length; i < l; i++) {
            start = eras[ i ].start;
            if (start === null || ticks >= start) {
                return i;
            }
        }
        return 0;
    }

    function toUpper(value) {
        // 'he-IL' has non-breaking space in weekday names.
        return value.split("\u00A0").join(' ').toUpperCase();
    }

    function toUpperArray(arr) {
        var results = [];
        for (var i = 0, l = arr.length; i < l; i++) {
            results[i] = toUpper(arr[i]);
        }
        return results;
    }

    function getEraYear(date, cal, era, sortable) {
        var year = date.getFullYear();
        if (!sortable && cal.eras) {
            // convert normal gregorian year to era-shifted gregorian
            // year by subtracting the era offset
            year -= cal.eras[ era ].offset;
        }
        return year;
    }

    function getDayIndex(cal, value, abbr) {
        var ret,
            days = cal.days,
            upperDays = cal._upperDays;
        if (!upperDays) {
            cal._upperDays = upperDays = [
                toUpperArray(days.names),
                toUpperArray(days.namesAbbr),
                toUpperArray(days.namesShort)
            ];
        }
        value = toUpper(value);
        if (abbr) {
            ret = arrayIndexOf(upperDays[ 1 ], value);
            if (ret === -1) {
                ret = arrayIndexOf(upperDays[ 2 ], value);
            }
        }
        else {
            ret = arrayIndexOf(upperDays[ 0 ], value);
        }
        return ret;
    }

    function getMonthIndex(cal, value, abbr) {
        var months = cal.months,
            monthsGen = cal.monthsGenitive || cal.months,
            upperMonths = cal._upperMonths,
            upperMonthsGen = cal._upperMonthsGen;
        if (!upperMonths) {
            cal._upperMonths = upperMonths = [
                toUpperArray(months.names),
                toUpperArray(months.namesAbbr)
            ];
            cal._upperMonthsGen = upperMonthsGen = [
                toUpperArray(monthsGen.names),
                toUpperArray(monthsGen.namesAbbr)
            ];
        }
        value = toUpper(value);
        var i = arrayIndexOf(abbr ? upperMonths[ 1 ] : upperMonths[ 0 ], value);
        if (i < 0) {
            i = arrayIndexOf(abbr ? upperMonthsGen[ 1 ] : upperMonthsGen[ 0 ], value);
        }
        return i;
    }

    function appendPreOrPostMatch(preMatch, strings) {
        // appends pre- and post- token match strings while removing escaped characters.
        // Returns a single quote count which is used to determine if the token occurs
        // in a string literal.
        var quoteCount = 0,
            escaped = false;
        for (var i = 0, il = preMatch.length; i < il; i++) {
            var c = preMatch.charAt(i);
            switch (c) {
                case '\'':
                    if (escaped) {
                        strings.push("'");
                    }
                    else {
                        quoteCount++;
                    }
                    escaped = false;
                    break;
                case '\\':
                    if (escaped) {
                        strings.push("\\");
                    }
                    escaped = !escaped;
                    break;
                default:
                    strings.push(c);
                    escaped = false;
                    break;
            }
        }
        return quoteCount;
    }

    function expandFormat(cal, format) {
        // expands unspecified or single character date formats into the full pattern.
        format = format || "F";
        var pattern,
            patterns = cal.patterns,
            len = format.length;
        if (len === 1) {
            pattern = patterns[ format ];
            if (!pattern) {
                throw "Invalid date format string '" + format + "'.";
            }
            format = pattern;
        }
        else if (len === 2 && format.charAt(0) === "%") {
            // %X escape format -- intended as a custom format string that is only one character, not a built-in format.
            format = format.charAt(1);
        }
        return format;
    }

    function getParseRegExp(cal, format) {
        // converts a format string into a regular expression with groups that
        // can be used to extract date fields from a date string.
        // check for a cached parse regex.
        var re = cal._parseRegExp;
        if (!re) {
            cal._parseRegExp = re = {};
        }
        else {
            var reFormat = re[ format ];
            if (reFormat) {
                return reFormat;
            }
        }

        // expand single digit formats, then escape regular expression characters.
        var expFormat = expandFormat(cal, format).replace(/([\^\$\.\*\+\?\|\[\]\(\)\{\}])/g, "\\\\$1"),
            regexp = ["^"],
            groups = [],
            index = 0,
            quoteCount = 0,
            tokenRegExp = getTokenRegExp(),
            match;

        // iterate through each date token found.
        while ((match = tokenRegExp.exec(expFormat)) !== null) {
            var preMatch = expFormat.slice(index, match.index);
            index = tokenRegExp.lastIndex;

            // don't replace any matches that occur inside a string literal.
            quoteCount += appendPreOrPostMatch(preMatch, regexp);
            if (quoteCount % 2) {
                regexp.push(match[ 0 ]);
                continue;
            }

            // add a regex group for the token.
            var m = match[ 0 ],
                len = m.length,
                add;
            switch (m) {
                case 'dddd':
                case 'ddd':
                case 'MMMM':
                case 'MMM':
                case 'gg':
                case 'g':
                    add = "(\\D+)";
                    break;
                case 'tt':
                case 't':
                    add = "(\\D*)";
                    break;
                case 'yyyy':
                case 'fff':
                case 'ff':
                case 'f':
                    add = "(\\d{" + len + "})";
                    break;
                case 'dd':
                case 'd':
                case 'MM':
                case 'M':
                case 'yy':
                case 'y':
                case 'HH':
                case 'H':
                case 'hh':
                case 'h':
                case 'mm':
                case 'm':
                case 'ss':
                case 's':
                    add = "(\\d\\d?)";
                    break;
                case 'zzz':
                    add = "([+-]?\\d\\d?:\\d{2})";
                    break;
                case 'zz':
                case 'z':
                    add = "([+-]?\\d\\d?)";
                    break;
                case '/':
                    add = "(\\" + cal["/"] + ")";
                    break;
                default:
                    throw "Invalid date format pattern '" + m + "'.";
                    break;
            }
            if (add) {
                regexp.push(add);
            }
            groups.push(match[ 0 ]);
        }
        appendPreOrPostMatch(expFormat.slice(index), regexp);
        regexp.push("$");

        // allow whitespace to differ when matching formats.
        var regexpStr = regexp.join('').replace(/\s+/g, "\\s+"),
            parseRegExp = {'regExp': regexpStr, 'groups': groups};

        // cache the regex for this format.
        return re[ format ] = parseRegExp;
    }

    function getTokenRegExp() {
        // regular expression for matching date and time tokens in format strings.
        return /\/|dddd|ddd|dd|d|MMMM|MMM|MM|M|yyyy|yy|y|hh|h|HH|H|mm|m|ss|s|tt|t|fff|ff|f|zzz|zz|z|gg|g/g;
    }

    function parseExact(value, format, culture) {
        // try to parse the date string by matching against the format string
        // while using the specified culture for date field names.
        value = trim(value);
        var cal = culture.calendar,
        // convert date formats into regular expressions with groupings.
        // use the regexp to determine the input format and extract the date fields.
            parseInfo = getParseRegExp(cal, format),
            match = new RegExp(parseInfo.regExp).exec(value);
        if (match === null) {
            return null;
        }
        // found a date format that matches the input.
        var groups = parseInfo.groups,
            era = null, year = null, month = null, date = null, weekDay = null,
            hour = 0, hourOffset, min = 0, sec = 0, msec = 0, tzMinOffset = null,
            pmHour = false;
        // iterate the format groups to extract and set the date fields.
        for (var j = 0, jl = groups.length; j < jl; j++) {
            var matchGroup = match[ j + 1 ];
            if (matchGroup) {
                var current = groups[ j ],
                    clength = current.length,
                    matchInt = parseInt(matchGroup, 10);
                switch (current) {
                    case 'dd':
                    case 'd':
                        // Day of month.
                        date = matchInt;
                        // check that date is generally in valid range, also checking overflow below.
                        if (outOfRange(date, 1, 31)) return null;
                        break;
                    case 'MMM':
                    case 'MMMM':
                        month = getMonthIndex(cal, matchGroup, clength === 3);
                        if (outOfRange(month, 0, 11)) return null;
                        break;
                    case 'M':
                    case 'MM':
                        // Month.
                        month = matchInt - 1;
                        if (outOfRange(month, 0, 11)) return null;
                        break;
                    case 'y':
                    case 'yy':
                    case 'yyyy':
                        year = clength < 4 ? expandYear(cal, matchInt) : matchInt;
                        if (outOfRange(year, 0, 9999)) return null;
                        break;
                    case 'h':
                    case 'hh':
                        // Hours (12-hour clock).
                        hour = matchInt;
                        if (hour === 12) hour = 0;
                        if (outOfRange(hour, 0, 11)) return null;
                        break;
                    case 'H':
                    case 'HH':
                        // Hours (24-hour clock).
                        hour = matchInt;
                        if (outOfRange(hour, 0, 23)) return null;
                        break;
                    case 'm':
                    case 'mm':
                        // Minutes.
                        min = matchInt;
                        if (outOfRange(min, 0, 59)) return null;
                        break;
                    case 's':
                    case 'ss':
                        // Seconds.
                        sec = matchInt;
                        if (outOfRange(sec, 0, 59)) return null;
                        break;
                    case 'tt':
                    case 't':
                        // AM/PM designator.
                        // see if it is standard, upper, or lower case PM. If not, ensure it is at least one of
                        // the AM tokens. If not, fail the parse for this format.
                        pmHour = cal.PM && ( matchGroup === cal.PM[0] || matchGroup === cal.PM[1] || matchGroup === cal.PM[2] );
                        if (!pmHour && ( !cal.AM || (matchGroup !== cal.AM[0] && matchGroup !== cal.AM[1] && matchGroup !== cal.AM[2]) )) return null;
                        break;
                    case 'f':
                    // Deciseconds.
                    case 'ff':
                    // Centiseconds.
                    case 'fff':
                        // Milliseconds.
                        msec = matchInt * Math.pow(10, 3 - clength);
                        if (outOfRange(msec, 0, 999)) return null;
                        break;
                    case 'ddd':
                    // Day of week.
                    case 'dddd':
                        // Day of week.
                        weekDay = getDayIndex(cal, matchGroup, clength === 3);
                        if (outOfRange(weekDay, 0, 6)) return null;
                        break;
                    case 'zzz':
                        // Time zone offset in +/- hours:min.
                        var offsets = matchGroup.split(/:/);
                        if (offsets.length !== 2) return null;
                        hourOffset = parseInt(offsets[ 0 ], 10);
                        if (outOfRange(hourOffset, -12, 13)) return null;
                        var minOffset = parseInt(offsets[ 1 ], 10);
                        if (outOfRange(minOffset, 0, 59)) return null;
                        tzMinOffset = (hourOffset * 60) + (startsWith(matchGroup, '-') ? -minOffset : minOffset);
                        break;
                    case 'z':
                    case 'zz':
                        // Time zone offset in +/- hours.
                        hourOffset = matchInt;
                        if (outOfRange(hourOffset, -12, 13)) return null;
                        tzMinOffset = hourOffset * 60;
                        break;
                    case 'g':
                    case 'gg':
                        var eraName = matchGroup;
                        if (!eraName || !cal.eras) return null;
                        eraName = trim(eraName.toLowerCase());
                        for (var i = 0, l = cal.eras.length; i < l; i++) {
                            if (eraName === cal.eras[ i ].name.toLowerCase()) {
                                era = i;
                                break;
                            }
                        }
                        // could not find an era with that name
                        if (era === null) return null;
                        break;
                }
            }
        }
        var result = new Date(), defaultYear, convert = cal.convert;
        defaultYear = convert ? convert.fromGregorian(result)[ 0 ] : result.getFullYear();
        if (year === null) {
            year = defaultYear;
        }
        else if (cal.eras) {
            // year must be shifted to normal gregorian year
            // but not if year was not specified, its already normal gregorian
            // per the main if clause above.
            year += cal.eras[ (era || 0) ].offset;
        }
        // set default day and month to 1 and January, so if unspecified, these are the defaults
        // instead of the current day/month.
        if (month === null) {
            month = 0;
        }
        if (date === null) {
            date = 1;
        }
        // now have year, month, and date, but in the culture's calendar.
        // convert to gregorian if necessary
        if (convert) {
            result = convert.toGregorian(year, month, date);
            // conversion failed, must be an invalid match
            if (result === null) return null;
        }
        else {
            // have to set year, month and date together to avoid overflow based on current date.
            result.setFullYear(year, month, date);
            // check to see if date overflowed for specified month (only checked 1-31 above).
            if (result.getDate() !== date) return null;
            // invalid day of week.
            if (weekDay !== null && result.getDay() !== weekDay) {
                return null;
            }
        }
        // if pm designator token was found make sure the hours fit the 24-hour clock.
        if (pmHour && hour < 12) {
            hour += 12;
        }
        result.setHours(hour, min, sec, msec);
        if (tzMinOffset !== null) {
            // adjust timezone to utc before applying local offset.
            var adjustedMin = result.getMinutes() - ( tzMinOffset + result.getTimezoneOffset() );
            // Safari limits hours and minutes to the range of -127 to 127.  We need to use setHours
            // to ensure both these fields will not exceed this range.  adjustedMin will range
            // somewhere between -1440 and 1500, so we only need to split this into hours.
            result.setHours(result.getHours() + parseInt(adjustedMin / 60, 10), adjustedMin % 60);
        }
        return result;
    }

    function formatDate(value, format, culture) {
        var cal = culture.calendar,
            convert = cal.convert;
        if (!format || !format.length || format === 'i') {
            var ret;
            if (culture && culture.name.length) {
                if (convert) {
                    // non-gregorian calendar, so we cannot use built-in toLocaleString()
                    ret = formatDate(value, cal.patterns.F, culture);
                }
                else {
                    var eraDate = new Date(value.getTime()),
                        era = getEra(value, cal.eras);
                    eraDate.setFullYear(getEraYear(value, cal, era));
                    ret = eraDate.toLocaleString();
                }
            }
            else {
                ret = value.toString();
            }
            return ret;
        }

        var eras = cal.eras,
            sortable = format === "s";
        format = expandFormat(cal, format);

        // Start with an empty string
        ret = [];
        var hour,
            zeros = ['0', '00', '000'],
            foundDay,
            checkedDay,
            dayPartRegExp = /([^d]|^)(d|dd)([^d]|$)/g,
            quoteCount = 0,
            tokenRegExp = getTokenRegExp(),
            converted;

        function padZeros(num, c) {
            var r, s = num + '';
            if (c > 1 && s.length < c) {
                r = ( zeros[ c - 2 ] + s);
                return r.substr(r.length - c, c);
            }
            else {
                r = s;
            }
            return r;
        }

        function hasDay() {
            if (foundDay || checkedDay) {
                return foundDay;
            }
            foundDay = dayPartRegExp.test(format);
            checkedDay = true;
            return foundDay;
        }

        function getPart(date, part) {
            if (converted) {
                return converted[ part ];
            }
            switch (part) {
                case 0:
                    return date.getFullYear();
                case 1:
                    return date.getMonth();
                case 2:
                    return date.getDate();
            }
        }

        if (!sortable && convert) {
            converted = convert.fromGregorian(value);
        }

        for (; ;) {
            // Save the current index
            var index = tokenRegExp.lastIndex,
            // Look for the next pattern
                ar = tokenRegExp.exec(format);

            // Append the text before the pattern (or the end of the string if not found)
            var preMatch = format.slice(index, ar ? ar.index : format.length);
            quoteCount += appendPreOrPostMatch(preMatch, ret);

            if (!ar) {
                break;
            }

            // do not replace any matches that occur inside a string literal.
            if (quoteCount % 2) {
                ret.push(ar[ 0 ]);
                continue;
            }

            var current = ar[ 0 ],
                clength = current.length;

            switch (current) {
                case "ddd":
                //Day of the week, as a three-letter abbreviation
                case "dddd":
                    // Day of the week, using the full name
                    names = (clength === 3) ? cal.days.namesAbbr : cal.days.names;
                    ret.push(names[ value.getDay() ]);
                    break;
                case "d":
                // Day of month, without leading zero for single-digit days
                case "dd":
                    // Day of month, with leading zero for single-digit days
                    foundDay = true;
                    ret.push(padZeros(getPart(value, 2), clength));
                    break;
                case "MMM":
                // Month, as a three-letter abbreviation
                case "MMMM":
                    // Month, using the full name
                    var part = getPart(value, 1);
                    ret.push((cal.monthsGenitive && hasDay())
                        ? cal.monthsGenitive[ clength === 3 ? "namesAbbr" : "names" ][ part ]
                        : cal.months[ clength === 3 ? "namesAbbr" : "names" ][ part ]);
                    break;
                case "M":
                // Month, as digits, with no leading zero for single-digit months
                case "MM":
                    // Month, as digits, with leading zero for single-digit months
                    ret.push(padZeros(getPart(value, 1) + 1, clength));
                    break;
                case "y":
                // Year, as two digits, but with no leading zero for years less than 10
                case "yy":
                // Year, as two digits, with leading zero for years less than 10
                case "yyyy":
                    // Year represented by four full digits
                    part = converted ? converted[ 0 ] : getEraYear(value, cal, getEra(value, eras), sortable);
                    if (clength < 4) {
                        part = part % 100;
                    }
                    ret.push(padZeros(part, clength));
                    break;
                case "h":
                // Hours with no leading zero for single-digit hours, using 12-hour clock
                case "hh":
                    // Hours with leading zero for single-digit hours, using 12-hour clock
                    hour = value.getHours() % 12;
                    if (hour === 0) hour = 12;
                    ret.push(padZeros(hour, clength));
                    break;
                case "H":
                // Hours with no leading zero for single-digit hours, using 24-hour clock
                case "HH":
                    // Hours with leading zero for single-digit hours, using 24-hour clock
                    ret.push(padZeros(value.getHours(), clength));
                    break;
                case "m":
                // Minutes with no leading zero  for single-digit minutes
                case "mm":
                    // Minutes with leading zero  for single-digit minutes
                    ret.push(padZeros(value.getMinutes(), clength));
                    break;
                case "s":
                // Seconds with no leading zero for single-digit seconds
                case "ss":
                    // Seconds with leading zero for single-digit seconds
                    ret.push(padZeros(value.getSeconds(), clength));
                    break;
                case "t":
                // One character am/pm indicator ("a" or "p")
                case "tt":
                    // Multicharacter am/pm indicator
                    part = value.getHours() < 12 ? (cal.AM ? cal.AM[0] : " ") : (cal.PM ? cal.PM[0] : " ");
                    ret.push(clength === 1 ? part.charAt(0) : part);
                    break;
                case "f":
                // Deciseconds
                case "ff":
                // Centiseconds
                case "fff":
                    // Milliseconds
                    ret.push(padZeros(value.getMilliseconds(), 3).substr(0, clength));
                    break;
                case "z":
                // Time zone offset, no leading zero
                case "zz":
                    // Time zone offset with leading zero
                    hour = value.getTimezoneOffset() / 60;
                    ret.push((hour <= 0 ? '+' : '-') + padZeros(Math.floor(Math.abs(hour)), clength));
                    break;
                case "zzz":
                    // Time zone offset with leading zero
                    hour = value.getTimezoneOffset() / 60;
                    ret.push((hour <= 0 ? '+' : '-') + padZeros(Math.floor(Math.abs(hour)), 2) +
                        // Hard coded ":" separator, rather than using cal.TimeSeparator
                        // Repeated here for consistency, plus ":" was already assumed in date parsing.
                        ":" + padZeros(Math.abs(value.getTimezoneOffset() % 60), 2));
                    break;
                case "g":
                case "gg":
                    if (cal.eras) {
                        ret.push(cal.eras[ getEra(value, eras) ].name);
                    }
                    break;
                case "/":
                    ret.push(cal["/"]);
                    break;
                default:
                    throw "Invalid date format pattern '" + current + "'.";
                    break;
            }
        }
        return ret.join('');
    }

// EXPORTS
    jQuery.global = Globalization;

})
    ();


/*
 * jQuery Date
 *
 * Copyright 2010 Marc Grabanski
 * Licensed under the MIT license
 *
 *
 * Depends:
 *	jquery.glob.js
 */
(function ($, undefined) {

    if (typeof( $.global.culture ) == "undefined") {
        $.global.culture = $.global.cultures[ "zh_CN" ];
    }

    $.isDate = function (obj) {
        return typeof obj == 'object' && $.defined(obj) && obj.constructor == Date;
    };

    function replaceFormat(format) {
        if (format != undefined && format != null) {
            var found = ["S", "SS", "SSS"];
            var replace = ["f", "ff", "fff"];
            for (var i = 0; i < found.length; i++)
                format = format.replace(found[i], replace[i]);
        }
        return format;
    };

    $.date = function (datestring, formatstring) {
        formatstring = replaceFormat(formatstring);
        var calendar = $.global.culture.calendar,
            format = formatstring ? formatstring : calendar.patterns.d;
        var date;
        if ($.isDate(datestring))
            date = datestring;
        else
            date = datestring ? $.global.parseDate(datestring, format) : new Date();
        return {
            refresh: function () {
                calendar = $.global.culture.calendar;
                format = formatstring || calendar.patterns.d;
                return this;
            },
            setFormat: function (formatstring) {
                if (formatstring) {
                    format = formatstring;
                }
                return this;
            },
            setDay: function (day) {
                date = new Date(date.getFullYear(), date.getMonth(), day);
                return this;
            },
            adjust: function (period, offset) {
                var day = period == "D" ? date.getDate() + offset : date.getDate(),
                    month = period == "M" ? date.getMonth() + offset : date.getMonth(),
                    year = period == "Y" ? date.getFullYear() + offset : date.getFullYear();
                date = new Date(year, month, day);
                return this;
            },
            daysInMonth: function (year, month) {
                year = year || date.getFullYear();
                month = month || date.getMonth();
                return 32 - new Date(year, month, 32).getDate();
            },
            monthname: function () {
                return calendar.months.names[ date.getMonth() ];
            },
            year: function () {
                return date.getFullYear();
            },
            weekdays: function () {
                // TODO take firstDay into account
                var result = [];
                for (var dow = 0; dow < 7; dow++) {
                    var day = ( dow + calendar.firstDay ) % 7;
                    result.push({
                        shortname: calendar.days.namesShort[ day ],
                        fullname: calendar.days.names[ day ]
                    });
                }
                return result;
            },
            days: function () {
                var result = [],
                    firstDayOfMonth = new Date(this.year(), date.getMonth(), 1).getDay(),
                    leadDays = ( firstDayOfMonth - calendar.firstDay + 7 ) % 7,
                    rows = Math.ceil(( leadDays + this.daysInMonth() ) / 7),
                    printDate = new Date(this.year(), date.getMonth(), 1 - leadDays);
                for (var row = 0; row < rows; row++) {
                    var week = result[ result.length ] = {
                        number: this.iso8601Week(printDate),
                        days: []
                    };
                    for (var dayx = 0; dayx < 7; dayx++) {
                        var day = week.days[ week.days.length ] = {
                            lead: printDate.getMonth() != date.getMonth(),
                            date: printDate.getDate(),
                            current: this.selected && this.selected.equal(printDate),
                            today: today.equal(printDate)
                        };
                        day.render = day.selectable = !day.lead;
                        this.eachDay(day);
                        // TODO use adjust("D", 1)?
                        printDate.setDate(printDate.getDate() + 1);
                    }
                }
                return result;
            },
            iso8601Week: function (date) {
                var checkDate = new Date(date.getTime());
                // Find Thursday of this week starting on Monday
                checkDate.setDate(checkDate.getDate() + 4 - ( checkDate.getDay() || 7 ));
                var time = checkDate.getTime();
                checkDate.setMonth(0); // Compare with Jan 1
                checkDate.setDate(1);
                return Math.floor(Math.round(( time - checkDate ) / 86400000) / 7) + 1;
            },
            select: function () {
                this.selected = this.clone();
                return this;
            },
            // TODO create new Date with year, month, day instead
            clone: function () {
                return $.date(this.format(), format);
            },
            // TODO compare year, month, day each for better performance
            equal: function (other) {
                function format(date) {
                    return $.global.format(date, "d");
                }

                return format(date) == format(other);
            },
            date: function () {
                return date;
            },
            format: function (formatstring) {
                return $.global.format(date, formatstring ? formatstring : format);
            },
            calendar: function (newcalendar) {
                if (newcalendar) {
                    calendar = newcalendar;
                    return this;
                }
                return calendar;
            }
        }
    }
}(jQuery));

var DefaultDateFormators = ["yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"];
Date.prototype.format = function (format) {
    if (format == undefined || format == null)
        format = DefaultDateFormators[1];
    return $.date(this, format).format();
};

Date.parseText = function (text, format) {
    if (!$.defined(format)) {
        for (var i = 0; i < DefaultDateFormators.length; i++) {
            if (text.length == DefaultDateFormators[i].length) {
                format = DefaultDateFormators[i];
                if (text.indexOf("/") != -1)
                    format = format.replace(/-/g, "/");
                break;
            }
        }
    }
    if ($.defined(text) && $.defined(format)) {
        for (var i = 0; i < DefaultDateFormators.length; i++) {
            if (format == DefaultDateFormators[i] && text.length > format.length) {
                text = text.substring(0, format.length);
                break;
            }
        }
    }
    return $.date(text, format).date();
};

Date.parseValue = function (dateTime, format) {
    if ($.defined(dateTime)) {
        if ($.isString(dateTime))
            dateTime = Date.parseText(dateTime, format);
        if ($.isNumber(dateTime)) {
            var date = new Date();
            date.setTime(dateTime);
            dateTime = date;
        }
        if ($.isDate(dateTime))
            return dateTime;
    }
}

Date.prototype.getFirstTimeOfDay = function () {
    return new Date(this.getFullYear(), this.getMonth(), this.getDate());
}

Date.prototype.getLastTimeOfDay = function () {
    var date = this.addDays(1);
    return new Date(date.getFullYear(), date.getMonth(), date.getDate()).addMilliseconds(-1);
}

Date.prototype.getFirstTimeOfWeek = function () {
    var diffDay = this.getDay() - $.global.culture.calendar.firstDay;
    return this.addDays(diffDay * -1);
}

Date.prototype.getLastTimeOfWeek = function () {
    var diffDay = 7 - (this.getDay() - $.global.culture.calendar.firstDay);
    var date = this.addDays(diffDay);
    return new Date(date.getFullYear(), date.getMonth(), date.getDate()).addMilliseconds(-1);
}

Date.prototype.getFirstTimeOfMonth = function () {
    return new Date(this.getFullYear(), this.getMonth(), 1);
}

Date.prototype.getLastTimeOfMonth = function () {
    var year = this.getFullYear();
    var month = this.getMonth() + 1;
    if (month >= 12) {
        month -= 12;
        year++;
    }
    return new Date(year, month, 1).addMilliseconds(-1);
}

Date.prototype.getFirstTimeOfYear = function () {
    return new Date(this.getFullYear(), 0, 1);
}

Date.prototype.getLastTimeOfYear = function () {
    var year = this.getFullYear() + 1;
    return new Date(year, 0, 1).addMilliseconds(-1);
}

Date.prototype.addYears = function (value) {
    var date = new Date();
    date.setTime(this.getTime());
    date.setFullYear(this.getFullYear() + value);
    return date;
}

Date.prototype.addMonths = function (value) {
    var month = this.getMonth() + value;
    var year = this.getFullYear() + Math.floor((month + 1) / 12);
    month = month % 12;
    var date = new Date();
    date.setTime(this.getTime());
    date.setFullYear(year);
    date.setMonth(month);
    return date;
}

Date.prototype.addWeeks = function (value) {
    return this.addDays(value * 7);
}

Date.prototype.addDays = function (value) {
    return this.addHours(value * 24);
}

Date.prototype.addHours = function (value) {
    return this.addMinutes(value * 60);
}

Date.prototype.addMinutes = function (value) {
    return this.addSeconds(value * 60);
}

Date.prototype.addSeconds = function (value) {
    return this.addMilliseconds(value * 1000);
}

Date.prototype.addMilliseconds = function (value) {
    return new Date(this.getTime() + value);
}

// 去掉字符前后空白
String.prototype.trim = function () {
    return this.replace(/(^\s*)|(\s*$)/g, "");
}

// 去掉字符左端的的空白字符
String.prototype.leftTrim = function () {
    return this.replace(/(^[\\s]*)/g, "");
}

// 去掉字符右端的空白字符
String.prototype.rightTrim = function () {
    return this.replace(/([\\s]*$)/g, "");
}

// 判断字符串是否为空
String.prototype.isEmpty = function () {
    return this.trim().length == 0;
}

// 判断字符串是否以指定的字符串结束
String.prototype.endsWith = function (text) {
    if (this.length < text.length)
        return false;
    return this.substr(this.length - text.length) == text;
}

// 判断字符串是否以指定的字符串开始
String.prototype.startsWith = function (text) {
    if (this.length < text.length)
        return false;
    return this.substr(0, text.length) == text;
}

String.prototype.getBytesLength = function () {
    return this.replace(/[^\x00-\xff]/gi, "--").length;
}

Boolean.parseText = function (text) {
    if (text != undefined && text != null) {
        text = text.trim();
        if (text.toLowerCase() == "true")
            return true;
        else if (text.toLowerCase() == "false")
            return false;
    }
    return null;
}

Array.prototype.indexOf = function (value, beginIndex) {
    if (beginIndex == undefined || beginIndex == null)
        beginIndex = 0;
    for (var i = beginIndex; i < this.length; i++) {
        if (this[i] == value)
            return i;
    }
    return -1;
}

Array.prototype.remove = function (value) {
    var index = this.indexOf(value);
    if (index >= 0 && index < this.length) {
        this.splice(index, 1);
        return true;
    }
    return false;
}

Array.prototype.min = function (compare) {
    var i = this.length,
        min = this[0];

    while (i--) {
        if (compare && compare(this[i], min) < 0)
            min = this[i];
        else if (this[i] < min)
            min = this[i];
    }
    return min;
}

Array.prototype.max = function (compare) {
    var max = this[0];
    for (var i = 1; i < this.length; i++) {
        if (compare) {
            if (compare(this[i], max) > 0)
                max = this[i];
        } else if (this[i] > max)
            max = this[i];
    }
    return max;
}

Number.prototype.format = function (pattern) {
    if (pattern == undefined || pattern == null)
        pattern = "#.###";
    var text = this.toString();
    var intPart;
    var intFormat
    var floatPart;
    var floatFormat;
    if (/\./g.test(pattern)) {
        intFormat = pattern.split('.')[0];
        floatFormat = pattern.split('.')[1];
    } else {
        intFormat = pattern;
        floatFormat = null;
    }

    if (/\./g.test(text)) {
        if (floatFormat != null) {
            var tempFloat = Math.round(parseFloat('0.' + text.split('.')[1]) * Math.pow(10, floatFormat.length)) / Math.pow(10, floatFormat.length);
            intPart = (Math.floor(this) + Math.floor(tempFloat)).toString();
            floatPart = /\./g.test(tempFloat.toString()) ? tempFloat.toString().split('.')[1] : '0';
        } else {
            intPart = Math.round(this).toString();
            floatPart = '0';
        }
    } else {
        intPart = text;
        floatPart = '';
    }
    if (intFormat != null) {
        var outputInt = '';
        var zero = intFormat.match(/0*$/)[0].length;
        var comma = null;
        if (/,/g.test(intFormat))
            comma = intFormat.match(/,[^,]*/)[0].length - 1;
        var newReg = new RegExp('(\\d{' + comma + '})', 'g');

        if (intPart.length < zero) {
            outputInt = new Array(zero + 1).join('0') + intPart;
            outputInt = outputInt.substr(outputInt.length - zero, zero)
        } else
            outputInt = intPart;

        var outputInt = outputInt.substr(0, outputInt.length % comma) + outputInt.substring(outputInt.length % comma).replace(newReg, (comma != null ? ',' : '') + '$1')
        outputInt = outputInt.replace(/^,/, '');
        intPart = outputInt;
    }

    if (floatFormat != null) {
        var outputFloat = '';
        var zeroIndex = floatFormat.indexOf('0');
        var zero = zeroIndex == -1 ? 0 : floatFormat.length - zeroIndex;
        if (zero > 0 && floatPart.length < floatFormat.length) {
            outputFloat = floatPart + new Array(zero + 1).join('0');
            var outputFloat1 = outputFloat.substring(0, zeroIndex);
            var outputFloat2 = outputFloat.substring(zeroIndex, floatFormat.length);
            outputFloat = outputFloat1 + outputFloat2;
        } else
            outputFloat = floatPart.substring(0, floatFormat.length);

        floatPart = outputFloat;
    } else {
        if (pattern != '' || (pattern == '' && floatPart == '0'))
            floatPart = '';
    }

    return intPart + (floatPart == '' ? '' : '.' + floatPart);
}

var LogicValidator = {
    validate: function (name, value) {
        var validator = this[name];
        if (validator != undefined && validator != null)
            return validator(value);
    },

    "No": function (value) {
        if (value != undefined && value != null) {
            for (var i = 0; i < value.length; i++) {
                var charValue = value.charAt(i);
                if (charValue >= '0' && charValue <= '9' || charValue == '_' || charValue == '.')
                    continue;
                if (charValue >= 'a' && charValue <= 'z' || charValue >= 'A' && charValue <= 'Z')
                    continue;
                return isc.i18nValidate.NoValidator;
            }
        }
    },

    "Email": function (value) {
        if (value != undefined && value != null) {
            if (value.charAt(0) == "." || value.charAt(0) == "@" || value.indexOf('@', 0) == -1
                || value.indexOf('.', 0) == -1 || value.lastIndexOf("@") == value.length - 1 || value.lastIndexOf(".") == value.length - 1)
                return isc.i18nValidate.EmailValidator;
        }
    },

    "Tel": function (value) {
        if (value != undefined && value != null) {
            var countBracketsLeft = 0;
            var countBracketsRight = 0;
            var countPlusSign = 0;
            for (var i = 0; i < value.length; i++) {
                var charValue = value.charAt(i);
                if (charValue >= '0' && charValue <= '9' || charValue == '-')
                    continue;
                if (charValue == '+') {
                    countPlusSign++;
                    continue;
                }
                if (charValue == '(') {
                    countBracketsLeft++;
                    continue;
                }
                if (charValue == ')') {
                    countBracketsRight++;
                    continue;
                }
                return isc.i18nValidate.TelValidator;
            }
            if (countPlusSign > 1 || countBracketsLeft != countBracketsRight || countBracketsRight > 1)
                return isc.i18nValidate.TelValidator;
        }
    },

    "Mobile": function (value) {
        if (value != undefined && value != null) {
            for (var i = 0; i < value.length; i++) {
                var charValue = value.charAt(i);
                if (charValue >= '0' && charValue <= '9' || charValue == '-')
                    continue;
                return isc.i18nValidate.MobileValidator;
            }
        }
    },
    "Ipv4": function (value) {
        if (value != undefined && value != null) {
            for (var i = 0; i < value.length; i++) {
                var charValue = value.charAt(i);
                if (charValue >= '0' && charValue <= '9' || charValue == '.')
                    continue;
                return isc.i18nValidate.Ipv4Validator;
            }
        }
    },
    "Ipv6": function (value) {
        if (value != undefined && value != null) {
            for (var i = 0; i < value.length; i++) {
                var charValue = value.charAt(i);
                if ((charValue >= '0' && charValue <= '9') || (charValue >= 'a' && charValue <= 'f') || (charValue >= 'A' && charValue <= 'F') || charValue == '.' || charValue == ':')
                    continue;
                return isc.i18nValidate.Ipv6Validator;
            }
        }
    }
};

function ValueFormator() {
    this.units = ["", "K", "M", "G"];
    this.transConsts = [1, 1000, 1000, 1000];
}

ValueFormator.prototype.calculatePerfectUnit = function (value) {
    if (value == undefined || value == null || typeof (value) != "number")
        return this.units[0];
    var perfectValue = value;
    var perfectUnit = this.units[0];
    for (var i = 1; i < this.units.length; i++) {
        if (Math.floor(perfectValue / this.transConsts[i]) == 0)
            break;
        perfectValue /= this.transConsts[i];
        perfectUnit = this.units[i];
    }
    return perfectUnit;
};

ValueFormator.prototype.calculatePerfectValue = function (value) {
    if (value == undefined || value == null || typeof (value) != "number")
        return value;
    var perfectValue = value;
    for (var i = 1; i < this.units.length; i++) {
        if (Math.floor(perfectValue / this.transConsts[i]) == 0)
            break;
        perfectValue /= this.transConsts[i];
    }
    return perfectValue;
};

ValueFormator.prototype.calculateTransRate = function (unit) {
    return 1.0 / this.getTransRate(unit);
};

ValueFormator.prototype.getTransRate = function (unit) {
    if (unit == undefined || unit == null)
        return 1;
    var transRate = 1;
    for (var i = 0; i < this.units.length; i++) {
        transRate *= this.transConsts[i];
        if (this.units[i] == unit)
            break;
    }
    return transRate;
};

ValueFormator.prototype.format = function (value, pattern) {
    var perfectUnit = this.calculatePerfectUnit(value);
    var perfectValue = value * this.calculateTransRate(perfectUnit);
    return perfectValue.format(pattern) + perfectUnit;
};

function VelocityFormator() {
    ValueFormator.call(this);
    this.units = ["/S", "K/S", "M/S", "G/S"];
};
VelocityFormator.prototype = new ValueFormator();

function OctetsFormator() {
    ValueFormator.call(this);
    this.units = ["Byte", "KB", "MB", "GB", "TB", "PB"];
    this.transConsts = [1, 1024, 1024, 1024, 1024, 1024];
};
OctetsFormator.prototype = new ValueFormator();

function OctetsVelocityFormator() {
    ValueFormator.call(this);
    this.units = ["bps", "Kbps", "Mbps", "Gbps", "Tbps", "Pbps"];
    this.transConsts = [1, 1000, 1000, 1000, 1000, 1000];
};
OctetsVelocityFormator.prototype = new ValueFormator();

function PercentFormator() {
    ValueFormator.call(this);
    this.units = ["%"];
    this.transConsts = [1];
};
PercentFormator.prototype = new ValueFormator();
PercentFormator.prototype.format = function (value, pattern) {
    if (value == undefined || value == null || typeof (value) != "number")
        return value;
    if (pattern == undefined || pattern == null)
        pattern = "0.00";
    var text;
    var absValue = Math.abs(value);
    if (absValue != 0.0 && absValue < 0.01) {
        if (value < 0.0) {
            text = ">";
            value = -0.01;
        } else {
            text = "<";
            value = 0.01;
        }
        text += value.format(pattern) + "%";
    } else {
        var perfectUnit = this.calculatePerfectUnit(value);
        var perfectValue = value * this.calculateTransRate(perfectUnit);
        text = perfectValue.format(pattern) + perfectUnit;
    }
    return text;
};

function TimeLengthFormator() {
    ValueFormator.call(this);
    this.units = ["ms", "Sec", "Min", "Hour"];
    this.transConsts = [1, 1000, 60, 60];
};
TimeLengthFormator.prototype = new ValueFormator();

var DataFormatorFactory = {
    octetsFormator: new OctetsFormator(),
    octetsVelocityFormator: new OctetsVelocityFormator(),
    timeLengthFormator: new TimeLengthFormator(),
    velocityFormator: new VelocityFormator(),
    percentFormator: new PercentFormator(),
    valueFormator: new ValueFormator(),

    getDataFormator: function (logic) {
        var dataFormator;
        if (logic != undefined || logic != null)
            dataFormator = this[logic + "Formator"];
        if (dataFormator == undefined || dataFormator == null)
            dataFormator = this.valueFormator;
        return dataFormator;
    }
};

function TimeRange(timeType, timePeriod) {
    this.timeType = timeType;
    this.timePeriod = timePeriod;
}
TimeRange.prototype = {
    TIMESTEP_DAY: 12 * 60000,
    TIMESTEP_HOUR: 30 * 1000,
    TIMESTEP_MONTH: 8 * 3600000,
    TIMESTEP_WEEK: 72 * 60000,
    TIMESTEP_YEAR: 72 * 3600000,

    calculate: function (currentTime) {
        if (!$.defined(this.timeType))
            return;
        var baseTime = currentTime || new Date(),
            timeType = this.timeType.toLowerCase();

        baseTime.setMilliseconds(0);
        if ("year" == timeType)
            this.calculateYear(baseTime);
        else if ("month" == timeType)
            this.calculateMonth(baseTime);
        else if ("week" == timeType)
            this.calculateWeek(baseTime);
        else if ("day" == timeType)
            this.calculateDay(baseTime);
        else if ("hour" == timeType)
            this.calculateHour(baseTime);
        else if ("minute" == timeType)
            this.calculateMinute(baseTime);
        this.timeLength = this.endTime.getTime() - this.beginTime.getTime();
    },

    calculateDay: function (baseTime) {
        if ($.defined(this.timePeriod)) {
            this.beginTime = baseTime.addYears(this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = TimeRange.TIMESTEP_DAY * Math.abs(this.timePeriod);
        }
        else {
            this.beginTime = baseTime.getFirstTimeOfDay();
            this.endTime = baseTime.getLastTimeOfDay();
            this.timeStep = TimeRange.TIMESTEP_DAY;
        }
    },

    calculateHour: function (baseTime) {
        if ($.defined(this.timePeriod)) {
            this.beginTime = baseTime.addHours(this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = TimeRange.TIMESTEP_HOUR * Math.abs(this.timePeriod);
        }
        else {
            this.beginTime = baseTime.addHours(-1);
            this.endTime = baseTime;
            this.timeStep = TimeRange.TIMESTEP_HOUR;
        }
    },

    calculateMinute: function (baseTime) {
        if ($.defined(this.timePeriod)) {
            this.beginTime = baseTime.addMinutes(this.timePeriod);
            this.endTime = baseTime;
        }
        else {
            this.beginTime = baseTime.addMinutes(-1);
            this.endTime = baseTime;
        }
        this.timeStep = (this.endTime.getTime() - this.beginTime.getTime()) / 120;
    },

    calculateMonth: function (baseTime) {
        if ($.defined(this.timePeriod)) {
            this.beginTime = baseTime.addMonths(this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = TimeRange.TIMESTEP_MONTH * Math.abs(this.timePeriod);
        }
        else {
            this.beginTime = baseTime.getFirstTimeOfMonth();
            this.endTime = baseTime.getLastTimeOfMonth();
            this.timeStep = TimeRange.TIMESTEP_MONTH;
        }
    },

    calculateWeek: function (baseTime) {
        if ($.defined(this.timePeriod)) {
            this.beginTime = baseTime.addWeeks(this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = TimeRange.TIMESTEP_WEEK * Math.abs(this.timePeriod);
        }
        else {
            this.beginTime = baseTime.getFirstTimeOfWeek();
            this.endTime = baseTime.getLastTimeOfWeek();
            this.timeStep = TimeRange.TIMESTEP_WEEK;
        }
    },

    calculateYear: function (baseTime) {
        if ($.defined(this.timePeriod)) {
            this.beginTime = baseTime.addYears(this.timePeriod);
            this.endTime = baseTime;
            this.timeStep = TimeRange.TIMESTEP_YEAR * Math.abs(this.timePeriod);
        }
        else {
            this.beginTime = baseTime.getFirstTimeOfYear();
            this.endTime = baseTime.getLastTimeOfYear();
            this.timeStep = TimeRange.TIMESTEP_YEAR;
        }
    }
};