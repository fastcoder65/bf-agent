function trim(s) {
    return s.replace(/^\s*/, "").replace(/\s*$/, "");
}
function validatePassword(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oPassword = eval(" new " + form.name + "_password();");
    for (var x in oPassword) {
        if ((form[oPassword[x][0]].type == 'text' || form[oPassword[x][0]].type == 'password') && (form[oPassword[x][0]].value.length > 0)) {
            if (!checkPassword(form[oPassword[x][0]].value)) {
                if (i == 0) {
                    focusField = form[oPassword[x][0]];
                }
                fields[i++] = oPassword[x][1];
                bValid = false;
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function checkPassword(pwdStr) {
    var specialChars = "\\-";
    var lengthPat = /^\S{6,}$/;
    return checkLoginPassword(pwdStr, specialChars, lengthPat);
}

function checkLoginPassword(pwdStr, specialChars, lengthPat) {
    if (pwdStr.length == 0)return false;
    if (!lengthPat.test(pwdStr))return false;
    var pwdPatternStr = "^[A-Za-z0-9][a-zA-Z0-9_" + specialChars + "]*$";
    var pwdPattern = new RegExp(pwdPatternStr);
    var letterExistsPattern = /[a-zA-Z]+/;
    return pwdPattern.test(pwdStr); // && pwdStr.match(letterExistsPattern);
}

function validateAccountLogin(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var ologin = eval(" new " + form.name + "_accountLogin();");
    for (var x in ologin) {
        if ((form[ologin[x][0]].type == 'text' || form[ologin[x][0]].type == 'textarea') && (form[ologin[x][0]].value.length > 0)) {
            if (!checkAccountLogin(form[ologin[x][0]].value)) {
                if (i == 0) {
                    focusField = form[ologin[x][0]];
                }
                fields[i++] = ologin[x][1];
                bValid = false;
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function checkAccount(AccountStr) {
    var result = false;
    var tooManySpaces = /\s/;
    if (tooManySpaces.test(AccountStr))return result;
    var AccountId = Number(AccountStr);
    var lowMargin = 100000
    var highMargin = 2000000 - 1;
    if (isNaN(AccountId) || !(AccountId >= lowMargin && AccountId <= highMargin)) {
        return result;
    }
    var AccountIdPat = /^(\d{6,9})$/;
    var matchArray = AccountStr.match(AccountIdPat);
    return matchArray != null;
}
function checkAccountLogin(loginStr) {
    var specialChars = "\\-";
    var lengthPat = /^\S{4,}$/;
    return(checkLoginPassword(loginStr, specialChars, lengthPat) || checkAccount(loginStr));
}
function validateLogin(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var ologin = eval(" new " + form.name + "_login();");
    for (var x in ologin) {
        if ((form[ologin[x][0]].type == 'text' || form[ologin[x][0]].type == 'textarea') && (form[ologin[x][0]].value.length > 0)) {
            if (!checkLogon(form[ologin[x][0]].value)) {
                if (i == 0) {
                    focusField = form[ologin[x][0]];
                }
                fields[i++] = ologin[x][1];
                bValid = false;
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function checkLogon(logonStr) {
    var specialChars = "\\-";
    var lengthPat = /^\S{4,}$/;
    return checkLoginPassword(logonStr, specialChars, lengthPat);
}
function validateDate(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var formName = form.getAttributeNode("name");
    var oDate = eval('new ' + formName.value + '_date()');
    var dateRegexp;
    for (var x in oDate) {
        var field = form[oDate[x][0]];
        var value = field.value;
        var datePattern = oDate[x][2]("datePatternStrict");
        if (datePattern == null)
            datePattern = oDate[x][2]("datePattern");
        if ((field.type == 'hidden' || field.type == 'text' || field.type == 'textarea') && (value.length > 0) && (datePattern.length > 0) && !field.disabled) {
            var MONTH = "MM";
            var DAY = "dd";
            var YEAR = "yyyy";
            var orderMonth = datePattern.indexOf(MONTH);
            var orderDay = datePattern.indexOf(DAY);
            var orderYear = datePattern.indexOf(YEAR);
            if ((orderMonth < orderYear && orderMonth > orderDay)) {
                var iDelim1 = orderDay + DAY.length;
                var iDelim2 = orderMonth + MONTH.length;
                var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                if (iDelim1 == orderMonth && iDelim2 == orderYear) {
                    dateRegexp = new RegExp("^(\\d{2})(\\d{2})(\\d{4})$");
                } else if (iDelim1 == orderMonth) {
                    dateRegexp = new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$");
                } else if (iDelim2 == orderYear) {
                    dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$");
                } else {
                    dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$");
                }
                var matched = dateRegexp.exec(value);
                if (matched != null) {
                    var iValidResult = isValidDate(matched[1], matched[2], matched[3]);
                    if (-1 != iValidResult) {
                        if (i == 0) {
                            focusField = field;
                        }
                        if (1 != iValidResult) {
                            fields[i++] = oDate[x][1];
                        }
                        bValid = false;
                    }
                } else {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oDate[x][1];
                    bValid = false;
                }
            } else {
                if (i == 0) {
                    focusField = field;
                }
                fields[i++] = oDate[x][1];
                bValid = false;
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function isLeapYear(year) {
    return(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
}
function isValidDate(_day, _month, _year) {
    var day = (_day) ? Number(_day) : 1;
    var month = (_month) ? Number(_month) : 1;
    var year = (_year) ? Number(_year) : 1000;
    if (month < 1 || month > 12) {
        return 0;
    }
    if (day < 1 || day > 31) {
        return 0;
    }
    if ((month == 4 || month == 6 || month == 9 || month == 11) && (day == 31)) {
        return 0;
    }
    if (month == 2) {
        if (day > 29 || (day == 29 && !isLeapYear(year))) {
            return 0;
        }
    }
    var curD = new Date();
    var curD2 = new Date(year, month - 1, day);
    var curD18 = new Date(curD.getYear() - 18, curD.getMonth(), (isLeapYear(curD.getYear()) && curD.getMonth() == 1 && curD.getDate() == 29) ? curD.getDate() - 1 : curD.getDate());
    var curD100 = new Date(curD.getYear() - 100, curD.getMonth(), curD.getDate());
    if (curD100 > curD2 || curD2 > curD) {
        var _sInvalidBirthDay = eval("sInvalidBirthDay");
        if (_sInvalidBirthDay)
            alert(_sInvalidBirthDay);
        return 1;
    }
    if (curD2 > curD18) {
        var _sNot18 = eval("sNot18");
        if (_sNot18)
            alert(_sNot18);
        return 1;
    }
    return-1;
}