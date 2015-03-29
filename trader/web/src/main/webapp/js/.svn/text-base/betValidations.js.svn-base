/*
*   validation rules for betting on JS
*
*/

// Trim whitespace from left and right sides of s.
function trim(s) {
    return s.replace( /^\s*/, "" ).replace( /\s*$/, "" );
}

function validateBackRequired(form) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    // oBackRequired = new backRequired();
  var oBackRequired = eval(" new " + form.name + "_backRequired();");
    var ctlRequireds = form.elements;
    for (var x in oBackRequired) {
       var ctlName = oBackRequired[x][0];
        for (var j = 0; j < ctlRequireds.length; j++) {
            if (ctlRequireds[j] != null && (ctlRequireds[j].name.indexOf(ctlName) != -1)) {

                var field = form[ctlRequireds[j].name];
                if (field.type == 'text' ||
                    field.type == 'textarea' ||
                    field.type == 'file' ||
                    field.type == 'select-one' ||
                    field.type == 'radio' ||
                    field.type == 'password') {

                    var value = '';
                    // get field's value
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

                } // if (field.type == 'text' ||
            } // if (ctlRequireds[j] != null
        }
        // for (j = 0; j < ctlRequireds.length; j++ )
    }
    // for (x in oBackRequired)
    if (fields.length > 0) {
        focusField.focus();
        //   alert("validateBackRequired:"+fields.join('\n'));
        alert(fields.join('\n'));
    }
    return isValid;
}


function validateLayRequired(form) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    // oLayRequired = new layRequired();
    var oLayRequired = eval(" new " + form.name + "_layRequired();");
    var ctlRequireds = form.elements;
    for (var x in oLayRequired) {
        var ctlName = oLayRequired[x][0];
        for (var j = 0; j < ctlRequireds.length; j++) {
            if (ctlRequireds[j] != null && (ctlRequireds[j].name.indexOf(ctlName) != -1)) {

                var field = form[ctlRequireds[j].name];
                if (field.type == 'text' ||
                    field.type == 'textarea' ||
                    field.type == 'file' ||
                    field.type == 'select-one' ||
                    field.type == 'radio' ||
                    field.type == 'password') {

                    var value = '';
                    // get field's value
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
                } // if (field.type == 'text' ||
            } // if (ctlRequireds[j] != null
        }
        // for (j = 0; j < ctlRequireds.length; j++ )
    }
    // for (x in oLayRequired)
    if (fields.length > 0) {
        focusField.focus();
        //    alert("validateLayRequired:"+fields.join('\n'));
        alert(fields.join('\n'));
    }
    return isValid;
}

function validateOddsRequired(form) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

   var oOddsRequired = eval(" new " + form.name + "_oddsRequired();");
    var ctlRequireds = form.elements;
    for (var x in oOddsRequired) {
        var ctlName = oOddsRequired[x][0];
        for (var j = 0; j < ctlRequireds.length; j++) {
            if (ctlRequireds[j] != null && (ctlRequireds[j].name.indexOf(ctlName) != -1)) {

                var field = form[ctlRequireds[j].name];
                if (field.type == 'text' ||
                    field.type == 'textarea' ||
                    field.type == 'file' ||
                    field.type == 'select-one' ||
                    field.type == 'radio' ||
                    field.type == 'password') {

                    var value = '';
                    // get field's value
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
                } // if (field.type == 'text' ||
            } // if (ctlRequireds[j] != null
        }
        // for (j = 0; j < ctlRequireds.length; j++ )
    }
    // for (x in oOddsRequired)
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

    var oStrAmount = eval(" new " + form.name + "_validStrAmount();");
    var ctlAmounts = form.elements;
    for (var x in oStrAmount) {
       var ctlName = oStrAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlAmounts[j].name].type == 'text' ||
                     form[ctlAmounts[j].name].type == 'textarea') &&
                    (form[ctlAmounts[j].name].value.length > 0)) {
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
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oStrAmount)
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}


function checkStrAmount(AmountStr) {
    var result = true;
    if (AmountStr == null) return result;
    result = false;
    var tooManySpaces = /\s\s+/;
    if (tooManySpaces.test(AmountStr)) return result;
    var Amount = Number(stripCommas(AmountStr));
    var _MinStakeAmount = eval("parent.iMinStakeAmount");

    if ( _MinStakeAmount == null ) {
         alert("Min Stake Amount is undefined!");
         return result;
    }

    if (isNaN(Amount) || (_MinStakeAmount != null && Amount < _MinStakeAmount)) {
        return result;
    }

    //result =  (Amount == Math.floor(Amount));
    return true;//result;
}


function validateLayAmount(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    // alert("validateLayAmount");
    // oLayAmount = new validLayAmount();
    var oLayAmount = eval(" new " + form.name + "_validLayAmount();");
    var ctlAmounts = form.elements;
    for (var x in oLayAmount) {
        var ctlName = oLayAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlAmounts[j].name].type == 'text' ||
                     form[ctlAmounts[j].name].type == 'textarea') &&
                    (form[ctlAmounts[j].name].value.length > 0)) {
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
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oLayAmount)
    if (fields.length > 0) {
        focusField.focus();
        //   alert("validateLayAmount:"+fields.join('\n'));
        alert(fields.join('\n'));
    }
    return bValid;
}

function validateBackAmount(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    //    alert("validateBackAmount");
    var fields = new Array();
    //                 oBackAmount = new validBackAmount();
    var oBackAmount = eval(" new " + form.name + "_validBackAmount();");
    var ctlAmounts = form.elements;
    for (var x in oBackAmount) {
        var ctlName = oBackAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlAmounts[j].name].type == 'text' ||
                     form[ctlAmounts[j].name].type == 'textarea') &&
                    (form[ctlAmounts[j].name].value.length > 0)) {
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
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oBackAmount)
    if (fields.length > 0) {
        focusField.focus();
        //      alert("validateBackAmount:"+fields.join('\n'));
        alert(fields.join('\n'));
    }
    return bValid;
}
    // 
function checkAmount(AmountStr) {
    var result = true;
    if (AmountStr == null || AmountStr.length == 0 ) return result;

    result = false;

    var tooManySpaces = /\s/;
    if (tooManySpaces.test(AmountStr)) return result;

    //for allow x.01 to x.99 values
    var amountPat = /^(\d+)(?:|[\.]\d{0,2})$/;// /^(\d+)$/;
    if (!amountPat.test(AmountStr)) return result;

    var _MinStakeAmount = eval("iMinStakeAmount");

    if ( _MinStakeAmount == null ) {
         alert("Min Stake Amount is undefined!");
         return result;
    }

    var Amount = Number(AmountStr);

    if (isNaN(Amount) || (_MinStakeAmount != null && Amount < _MinStakeAmount)) {
        return result;
    }

    //result = (Amount == Math.floor(Amount));
    return true;//result;
}

function validatePSDayAmountLimit(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oBackAmount = eval(" new " + form.name + "_validPSDayAmountLimit();");
    var ctlAmounts = form.elements;
    for (var x in oBackAmount) {
        var ctlName = oBackAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlAmounts[j].name].type == 'text' ||
                     form[ctlAmounts[j].name].type == 'textarea') &&
                    (form[ctlAmounts[j].name].value.length > 0)) {
                    if (!checkPSDayAmountLimit(form[ctlAmounts[j].name].value)) {
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
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oBackAmount)
    if (fields.length > 0) {
        focusField.focus();
        //      alert("validateBackAmount:"+fields.join('\n'));
        alert(fields.join('\n'));
    }
    return bValid;
}
    //
function checkPSDayAmountLimit(AmountStr) {
    var result = true;
    if (AmountStr == null || AmountStr.length == 0 ) return result;

    result = false;

    var tooManySpaces = /\s/;
    if (tooManySpaces.test(AmountStr)) return result;

    //for allow x.01 to x.99 values
    var amountPat = /^(\d+)(?:|[\.]\d{0,2})$/;// /^(\d+)$/;
    if (!amountPat.test(AmountStr)) return result;

    var _MinStakeAmount = 0;//eval("iMinStakeAmount");

//    if ( _MinStakeAmount == null ) {
//         alert("Min Stake Amount is undefined!");
//         return result;
//    }
    
    var Amount = Number(AmountStr);

    if (isNaN(Amount) || (_MinStakeAmount != null && Amount < _MinStakeAmount)) {
        return result;
    }
    //result = (Amount == Math.floor(Amount));
    return true;//result;
}

function validateOdds(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oOdd = eval(" new " + form.name + "_validOdds();");
    var ctlOdds = form.elements;
    for (var x in oOdd) {
        var ctlName = oOdd[x][0];
        for (var j = 0; j < ctlOdds.length; j++) {
            if (ctlOdds[j] != null && (ctlOdds[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlOdds[j].name].type == 'text' ||
                     form[ctlOdds[j].name].type == 'textarea') &&
                    (form[ctlOdds[j].name].value.length > 0)) {
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
            } // if (ctlOdds[j]
        }
        // for (j = 0;
    }
    // for (x in oOdd)
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

    var oLayOdd = eval(" new " + form.name + "_validLayOdd();");
    var ctlOdds = form.elements;
    for (var x in oLayOdd) {
        var ctlName = oLayOdd[x][0];
        for (var j = 0; j < ctlOdds.length; j++) {
            if (ctlOdds[j] != null && (ctlOdds[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlOdds[j].name].type == 'text' ||
                     form[ctlOdds[j].name].type == 'textarea') &&
                    (form[ctlOdds[j].name].value.length > 0)) {
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
            } // if (ctlOdds[j]
        }
        // for (j = 0;
    }
    // for (x in oLayOdd)
    if (fields.length > 0) {
        focusField.focus();
        //   alert("validateLayOdd: "+fields.join('\n'));
        alert(fields.join('\n'));
    }
    return bValid;
}


function validateBackOdd(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    //  aleert("validateBackOdd");
    //  oBackOdd = new validBackOdd();
    var oBackOdd = eval(" new " + form.name + "_validBackOdd();");
    var ctlOdds = form.elements;
    for (var x in oBackOdd) {
        var ctlName = oBackOdd[x][0];
        for (var j = 0; j < ctlOdds.length; j++) {
            if (ctlOdds[j] != null && (ctlOdds[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlOdds[j].name].type == 'text' ||
                     form[ctlOdds[j].name].type == 'textarea') &&
                    (form[ctlOdds[j].name].value.length > 0)) {
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
            } // if (ctlOdds[j]
        }
        // for (j = 0;
    }
    // for (x in oBackOdd)
    if (fields.length > 0) {
        focusField.focus();
        //   alert("validateBackOdd: "+fields.join('\n'));
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
    if ( l_betOdds )
    for (var i = 0; i < l_betOdds.length; i++) {
        if (l_betOdds[i] == Odds) {
            result = true;
            break;
        }
    }
    return result;
}

