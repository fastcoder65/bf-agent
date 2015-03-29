function trim(s) {
    return s.replace(/^\s*/, "").replace(/\s*$/, "");
}
function getPrefixFormName(formName) {
    var lIndex = formName.indexOf("_");
    return(lIndex == -1) ? formName : formName.substring(0, lIndex);
}
function validateBackRequired(form) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var sBackRequiredList = " new " + getPrefixFormName(form.id) + "_backRequired();";
    var oBackRequired = eval(sBackRequiredList);
    var ctlRequireds = form.elements;
    for (var x in oBackRequired) {
        var ctlName = oBackRequired[x][0];
        for (var j = 0; j < ctlRequireds.length; j++) {
            var currentCtlName = ctlRequireds[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlRequireds[j] != null && (ctlRequireds[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                var field = form[ctlRequireds[j].name];
                if (field.type == 'text' || field.type == 'textarea' || field.type == 'file' || field.type == 'select-one' || field.type == 'radio' || field.type == 'password') {
                    var value = '';
                    if (field.type == "select-one") {
                        var si = field.selectedIndex;
                        if (si >= 0) {
                            value = field.options[si].value;
                        }
                    } else {
                        value = field.value;
                    }
                    if (trim(value).length == 0) {
                        if (i == 0) {
                            focusField = field;
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oBackRequired[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oBackRequired[x][1];
                        }
                        isValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return isValid;
}
function validateLayRequired(form) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oLayRequired = eval(" new " + getPrefixFormName(form.id) + "_layRequired();");
    var ctlRequireds = form.elements;
    for (var x in oLayRequired) {
        var ctlName = oLayRequired[x][0];
        for (var j = 0; j < ctlRequireds.length; j++) {
            var currentCtlName = ctlRequireds[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlRequireds[j] != null && (ctlRequireds[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                var field = form[ctlRequireds[j].name];
                if (field.type == 'text' || field.type == 'textarea' || field.type == 'file' || field.type == 'select-one' || field.type == 'radio' || field.type == 'password') {
                    var value = '';
                    if (field.type == "select-one") {
                        var si = field.selectedIndex;
                        if (si >= 0) {
                            value = field.options[si].value;
                        }
                    } else {
                        value = field.value;
                    }
                    if (trim(value).length == 0) {
                        if (i == 0) {
                            focusField = field;
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oLayRequired[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oLayRequired[x][1];
                        }
                        isValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return isValid;
}
function validateOddsRequired(form) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oOddsRequired = eval(" new " + getPrefixFormName(form.id) + "_oddsRequired();");
    var ctlRequireds = form.elements;
    for (var x in oOddsRequired) {
        var ctlName = oOddsRequired[x][0];
        for (var j = 0; j < ctlRequireds.length; j++) {
            var currentCtlName = ctlRequireds[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlRequireds[j] != null && (ctlRequireds[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                var field = form[ctlRequireds[j].name];
                if (field.type == 'text' || field.type == 'textarea' || field.type == 'file' || field.type == 'select-one' || field.type == 'radio' || field.type == 'password') {
                    var value = '';
                    if (field.type == "select-one") {
                        var si = field.selectedIndex;
                        if (si >= 0) {
                            value = field.options[si].value;
                        }
                    } else {
                        value = field.value;
                    }
                    if (trim(value).length == 0) {
                        if (i == 0) {
                            focusField = field;
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oOddsRequired[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oOddsRequired[x][1];
                        }
                        isValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return isValid;
}
function validateStrAmount(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oStrAmount = eval(" new " + getPrefixFormName(form.id) + "_validStrAmount();");
    var ctlAmounts = form.elements;
    for (var x in oStrAmount) {
        var ctlName = oStrAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            var currentCtlName = ctlAmounts[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                if ((form[ctlAmounts[j].name].type == 'text' || form[ctlAmounts[j].name].type == 'textarea') && (form[ctlAmounts[j].name].value.length > 0)) {
                    if (!checkStrAmount(form[ctlAmounts[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlAmounts[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oStrAmount[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oStrAmount[x][1];
                        }
                        bValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function checkStrAmount(AmountStr) {
    var result = true;
    if (AmountStr == null)return result;
    result = false;
    var tooManySpaces = /\s\s+/;
    if (tooManySpaces.test(AmountStr))return result;
    var Amount = Number(stripCommas(AmountStr));
    var _MinStakeAmount = eval("parent.iMinStakeAmount");
    if (_MinStakeAmount == null) {
        alert("Min Stake Amount is undefined!");
        return result;
    }
    if (isNaN(Amount) || (_MinStakeAmount != null && Amount < _MinStakeAmount)) {
        return result;
    }
    return true;
}
function validateLayAmount(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oLayAmount = eval(" new " + getPrefixFormName(form.id) + "_validLayAmount();");
    var ctlAmounts = form.elements;
    for (var x in oLayAmount) {
        var ctlName = oLayAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            var currentCtlName = ctlAmounts[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                if ((form[ctlAmounts[j].name].type == 'text' || form[ctlAmounts[j].name].type == 'textarea') && (form[ctlAmounts[j].name].value.length > 0)) {
                    if (!checkAmount(form[ctlAmounts[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlAmounts[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oLayAmount[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oLayAmount[x][1];
                        }
                        bValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function validateBackAmount(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oBackAmount = eval(" new " + getPrefixFormName(form.id) + "_validBackAmount();");
    var ctlAmounts = form.elements;
    for (var x in oBackAmount) {
        var ctlName = oBackAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            var currentCtlName = ctlAmounts[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                if ((form[ctlAmounts[j].name].type == 'text' || form[ctlAmounts[j].name].type == 'textarea') && (form[ctlAmounts[j].name].value.length > 0)) {
                    if (!checkAmount(form[ctlAmounts[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlAmounts[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oBackAmount[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oBackAmount[x][1];
                        }
                        bValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function checkAmount(AmountStr) {
    var result = true;
    if (AmountStr == null || AmountStr.length == 0)return result;
    result = false;
    var tooManySpaces = /\s/;
    if (tooManySpaces.test(AmountStr))return result;
    var amountPat = /^(\d+)(?:|[\.]\d{0,2})$/;
    if (!amountPat.test(AmountStr))return result;
    var _MinStakeAmount = eval("iMinStakeAmount");
    if (_MinStakeAmount == null) {
        alert("Min Stake Amount is undefined!");
        return result;
    }
    var Amount = Number(AmountStr);
    if (isNaN(Amount) || (_MinStakeAmount != null && Amount < _MinStakeAmount)) {
        return result;
    }
    return true;
}
function validateOdds(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oOdd = eval(" new " + getPrefixFormName(form.id) + "_validOdds();");
    var ctlOdds = form.elements;
    for (var x in oOdd) {
        var ctlName = oOdd[x][0];
        for (var j = 0; j < ctlOdds.length; j++) {
            var currentCtlName = ctlOdds[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlOdds[j] != null && (ctlOdds[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                if ((form[ctlOdds[j].name].type == 'text' || form[ctlOdds[j].name].type == 'textarea') && (form[ctlOdds[j].name].value.length > 0)) {
                    if (!checkMyOdd(form[ctlOdds[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlOdds[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oOdd[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oOdd[x][1];
                        }
                        bValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function checkMyOdd(oddStr) {
    var result = false;
    var Odds = Number(oddStr);
    if (isNaN(Odds) || Odds < 1.01) {
        return result;
    }
    var betOdds = eval("parent.betOdds");
    if (betOdds)
        for (var i = 0; i < betOdds.length; i++) {
            if (betOdds[i] == Odds) {
                result = true;
                break;
            }
        }
    return result;
}
function validateLayOdd(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oLayOdd = eval(" new " + getPrefixFormName(form.id) + "_validLayOdd();");
    var ctlOdds = form.elements;
    for (var x in oLayOdd) {
        var ctlName = oLayOdd[x][0];
        for (var j = 0; j < ctlOdds.length; j++) {
            var currentCtlName = ctlOdds[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlOdds[j] != null && (ctlOdds[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                if ((form[ctlOdds[j].name].type == 'text' || form[ctlOdds[j].name].type == 'textarea') && (form[ctlOdds[j].name].value.length > 0)) {
                    if (!checkOdd(form[ctlOdds[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlOdds[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oLayOdd[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oLayOdd[x][1];
                        }
                        bValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function validateBackOdd(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oBackOdd = eval(" new " + getPrefixFormName(form.id) + "_validBackOdd();");
    var ctlOdds = form.elements;
    for (var x in oBackOdd) {
        var ctlName = oBackOdd[x][0];
        for (var j = 0; j < ctlOdds.length; j++) {
            var currentCtlName = ctlOdds[j].name;
            var ctlIndex = currentCtlName.substring(currentCtlName.indexOf("(") + 1, currentCtlName.indexOf(")"));
            var ctlSelected;
            if (ctlIndex) {
                ctlSelected = form.elements["selected(" + ctlIndex + ")"];
            }
            if (ctlOdds[j] != null && (ctlOdds[j].name.indexOf(ctlName) != -1) && (!ctlSelected || (ctlSelected && ctlSelected.checked))) {
                if ((form[ctlOdds[j].name].type == 'text' || form[ctlOdds[j].name].type == 'textarea') && (form[ctlOdds[j].name].value.length > 0)) {
                    if (!checkOdd(form[ctlOdds[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlOdds[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oBackOdd[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oBackOdd[x][1];
                        }
                        bValid = false;
                    }
                }
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function checkOdd(oddStr) {
    var result = false;
    var Odds = Number(oddStr);
    if (isNaN(Odds) || Odds < 1.01) {
        return result;
    }
    var l_betOdds = eval("betOdds");
    if (l_betOdds)
        for (var i = 0; i < l_betOdds.length; i++) {
            if (l_betOdds[i] == Odds) {
                result = true;
                break;
            }
        }
    return result;
}