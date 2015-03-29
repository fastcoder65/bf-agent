/*
*  common validation rules on JS
*
*/

// Trim whitespace from left and right sides of s.
function trim(s) {
    return s.replace(/^\s*/, "").replace(/\s*$/, "");
}

function validateRequired(form) {
    return validateRequired_Name(form, form.name);
}

function validateRequired_Name(form, name) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    //              oRequired = new required();
    var oRequired = eval(" new " + name + "_required();");
    var ctlRequireds = form.elements;
    for (var x in oRequired) {
        var ctlName = oRequired[x][0];
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
                            if (fields[ii].indexOf(oRequired[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oRequired[x][1];
                        }


                        isValid = false;
                    }

                } // if (field.type == 'text' ||
            } // if (ctlRequireds[j] != null
        }
        // for (j = 0; j < ctlRequireds.length; j++ )
    }
    // for (x in oRequired)
    if (fields.length > 0) {
        focusField.focus();
        //  alert("validateRequired:"+fields.join('\n'));
        alert(fields.join('\n'));
    }
    return isValid;
}

function validateEventNames(form) {
    return validateEventNames_Name(form, form.name);
}

function validateEventNames_Name(form, name) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oEventNames = eval(" new " + name + "_eventNames();");
    var ctlEventNames = form.elements;
    for (var x in oEventNames) {
        var ctlName = oEventNames[x][0];
        for (var j = 0; j < ctlEventNames.length; j++) {
            if (ctlEventNames[j] != null && (ctlEventNames[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlEventNames[j].name].type == 'text' ||
                     form[ctlEventNames[j].name].type == 'textarea') &&
                    (form[ctlEventNames[j].name].value.length > 0)) {

                    if (!checkEventNames(form[ctlEventNames[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlEventNames[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oEventNames[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oEventNames[x][1];
                        }
                        bValid = false;
                    } //  if (!checkStrAmount(form[ctlAmounts[j].name].value))
                } // if ((form[ctlEventNames[j].name].type == 'text' ||
            } // if (ctlEventNames[j] != null && (ctlEventNames[j].name.indexOf(ctlName) != -1))
        } // for (j = 0; j < ctlEventNames.length; j++) {
    } // for (var x in oEventNames) {

    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function checkEventNames(eventNameStr) {
  var validEventNamesPattern = /^([^\{\}\;\:\|\f\n\r\t\v\'\"\\\*\^\~\[\]]*)$/;
  var tooManySpaces = /\s\s+/;
  var startSpaces = /^\s+/;
  var endSpaces = /\s+$/;
  return (validEventNamesPattern.test(eventNameStr) && !tooManySpaces.test(eventNameStr) && !startSpaces.test(eventNameStr) && !endSpaces.test(eventNameStr));
}

function validateBetNames(form) {
    return validateBetNames_Name(form, form.name);
}

function validateBetNames_Name(form, name) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oEventNames = eval(" new " + name + "_betNames();");
    var ctlEventNames = form.elements;
    for (var x in oEventNames) {
        var ctlName = oEventNames[x][0];
        for (var j = 0; j < ctlEventNames.length; j++) {
            if (ctlEventNames[j] != null && (ctlEventNames[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlEventNames[j].name].type == 'text' ||
                     form[ctlEventNames[j].name].type == 'textarea') &&
                    (form[ctlEventNames[j].name].value.length > 0)) {

                    if (!checkBetNames(form[ctlEventNames[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlEventNames[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oEventNames[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oEventNames[x][1];
                        }
                        bValid = false;
                    } //  if (!checkStrAmount(form[ctlAmounts[j].name].value))
                } // if ((form[ctlEventNames[j].name].type == 'text' ||
            } // if (ctlEventNames[j] != null && (ctlEventNames[j].name.indexOf(ctlName) != -1))
        } // for (j = 0; j < ctlEventNames.length; j++) {
    } // for (var x in oEventNames) {

    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}
function checkBetNames(eventNameStr) {
    var validEventNamesPattern = /^([^\{\}\;\:\|\f\n\r\t\v\'\"\\\*\^\~\[\]]*)$/;
    var tooManySpaces = /\s\s+/;
    var tooManySpacesEnd = /\s\s\s\s+$/;
    var spacesEnd = /\s+$/;
    if (!tooManySpacesEnd.test(eventNameStr) && spacesEnd.test(eventNameStr)) {
        eventNameStr = eventNameStr.replace(/(\s*$)/, "");
    }
    return (validEventNamesPattern.test(eventNameStr) && !tooManySpaces.test(eventNameStr));
}

function validateUserNames(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oUserNames = eval(" new " + form.name + "_userNames();");
    var ctlUserNames = form.elements;
    for (var x in oUserNames) {
        var ctlName = oUserNames[x][0];
        for (var j = 0; j < ctlUserNames.length; j++) {
            if (ctlUserNames[j] != null && (ctlUserNames[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlUserNames[j].name].type == 'text' ||
                     form[ctlUserNames[j].name].type == 'password' ||
                     form[ctlUserNames[j].name].type == 'textarea') &&
                    (form[ctlUserNames[j].name].value.length > 0)) {

                    if (!checkUserNames(form[ctlUserNames[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlUserNames[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oUserNames[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oUserNames[x][1];
                        }
                        bValid = false;
                    } //  if (!checkStrAmount(form[ctlAmounts[j].name].value))
                } // if ((form[ctlEventNames[j].name].type == 'text' ||
            } // if (ctlEventNames[j] != null && (ctlEventNames[j].name.indexOf(ctlName) != -1))
        } // for (j = 0; j < ctlEventNames.length; j++) {
    } // for (var x in oEventNames) {

    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function checkUserNames(userNameStr) {
  var validEventNamesPattern = /^([^\<\>\&\|\f\n\r\t\v\'\"\\\*\^\~\[\]]*)$/;
  var tooManySpaces = /\s\s+/;
  return (validEventNamesPattern.test(userNameStr) && !tooManySpaces.test(userNameStr));
}

function validatePaySystemName(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oUserNames = eval(" new " + form.name + "_cashierPSName();");
    var ctlUserNames = form.elements;
    for (var x in oUserNames) {
        var ctlName = oUserNames[x][0];
        for (var j = 0; j < ctlUserNames.length; j++) {
            if (ctlUserNames[j] != null && (ctlUserNames[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlUserNames[j].name].type == 'text' ||
                     form[ctlUserNames[j].name].type == 'textarea') &&
                    (form[ctlUserNames[j].name].value.length > 0)) {

                    if (!checkPaySystemName(form[ctlUserNames[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlUserNames[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oUserNames[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oUserNames[x][1];
                        }
                        bValid = false;
                    } //  if (!checkStrAmount(form[ctlAmounts[j].name].value))
                } // if ((form[ctlEventNames[j].name].type == 'text' ||
            } // if (ctlEventNames[j] != null && (ctlEventNames[j].name.indexOf(ctlName) != -1))
        } // for (j = 0; j < ctlEventNames.length; j++) {
    } // for (var x in oEventNames) {

    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function checkPaySystemName(userNameStr) {
  var validEventNamesPattern = /^([^\<\>\&\|\f\n\r\t\v\'\\*\^\~\[\]]*)$/;
  var tooManySpaces = /\s\s+/;
  return (validEventNamesPattern.test(userNameStr) && !tooManySpaces.test(userNameStr));
}

function validateEmail(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oEmail = eval(" new " + form.name + "_email();");
    for (var x in oEmail) {
        if ((form[oEmail[x][0]].type == 'text' ||
             form[oEmail[x][0]].type == 'textarea') &&
            (form[oEmail[x][0]].value.length > 0)) {
            if (!checkEmail(form[oEmail[x][0]].value)) {
                if (i == 0) {
                    focusField = form[oEmail[x][0]];
                }
                fields[i++] = oEmail[x][1];
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

/**
 * Reference: Eugene Glyantsev (glyantsev_e@mail.ru),
 * http://javascript.internet.com
 */


function checkEmail(emailStr) {
    if (emailStr.length == 0) {
        return true;
    }

    var emailPat = /^(.+)@(.+)$/;
    var specialChars = "\\+\\/\\-\\?=\\{\\}";

    var validChars = "[A-Za-z_0-9" + specialChars + "]";
    var domainValidChars = "[A-Za-z_0-9" + specialChars + "]";

    var quotedUser = "(\"[^\"]*\")";
    var ipDomainPat = /^(\d{1,3})[\.](\d{1,3})[\.](\d{1,3})[\.](\d{1,3})$/;

    var atom = validChars + '+';
    var domainAtom = domainValidChars + '+';

    var word = "(" + atom + "|" + quotedUser + ")";
    var userPat = new RegExp("^" + word + "(\\." + word + ")*$");
    var domainPat = new RegExp("^" + domainAtom + "(\\." + domainAtom + ")+$");

    var tooManyDots = /([\&\'\*\+\/\-\?\=\^\{\}\~]{2,})/;
    var tooManyNumbers = /(\d{3,})/;

    var containsLetter = /[A-Za-z]+/;
    var containsNumber = /\d+/;

    var matchArray = emailStr.match(emailPat);
    if (matchArray == null) {
        return false;
    }

    var user = matchArray[1];
    var domain = matchArray[2];

    if (user.match(userPat) == null) {
        return false;
    }

    if (tooManyDots.test(user) || !containsLetter.test(user))
        return false;


    var IPArray = domain.match(ipDomainPat);

    if (IPArray) {
        for (var i = 1; i <= 4; i++) {
            if (IPArray[i] > 255) {
               return false;
            }
        }
        return true;
    }

    if (tooManyDots.test(domain) || tooManyNumbers.test(domain) || !containsLetter.test(domain))
        return false;

    var domainArray = domain.match(domainPat);
    var domainLength = (domainArray != null)?domainArray [domainArray.length-1] .length-1:0;

    return ( domainArray != null && (domainLength > 1 && domainLength < 5)
              && !containsNumber.test(domainArray[domainArray.length-1]));

}

function maxlength_value(input)
{
	var maxLenght = 500;
	var str = input.value;
	var length = str.length;
	if (length > maxLenght) input.value = str.substring(0, maxLenght);
}

function validateComission(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oComission = eval(" new " + form.name + "_comission();");
    for (var x in oComission) {
        if (form[oComission[x][0]] == null) continue;
        if ((form[oComission[x][0]].type == 'text') &&
            (form[oComission[x][0]].value.length > 0)) {
            if (!checkComission(form[oComission[x][0]].value)) {
                if (i == 0) {
                    focusField = form[oComission[x][0]];
                }
                fields[i++] = oComission[x][1];
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

function checkComission(comissionStr) {
    if (comissionStr.length == 0 || comissionStr.length > 3) {
        return false;
    }

    if (comissionStr == "0.0" || comissionStr == "0" || comissionStr == ".0" || comissionStr == ".") {
        return false;
    }

    var comissionPat = /^(\d{0,1})(?:|[\.]\d{0,1})$/;

    var matchArray = comissionStr.match(comissionPat);
    return matchArray != null;
}

function validateComissionPS(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oComission = eval(" new " + form.name + "_comission();");
    for (var x in oComission) {
        if (form[oComission[x][0]] == null) continue;
        if ((form[oComission[x][0]].type == 'text') &&
            (form[oComission[x][0]].value.length > 0)) {
            if (!checkComissionPS(form[oComission[x][0]].value)) {
                if (i == 0) {
                    focusField = form[oComission[x][0]];
                }
                fields[i++] = oComission[x][1];
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

function checkComissionPS(comissionStr) {
    if (comissionStr.length == 0 || comissionStr.length > 3) {
        return false;
    }

    if (comissionStr == ".") {
        return false;
    }

    var comissionPat = /^(\d{0,1})(?:|[\.]\d{0,1})$/;

    var matchArray = comissionStr.match(comissionPat);
    return matchArray != null;
}


function validateMoneyAmount(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;

    var fields = new Array();

    var oMoneyAmount = eval(" new " + form.name + "_validMoneyAmount();");
    var ctlAmounts = form.elements;
    for (var x in oMoneyAmount) {
        var ctlName = oMoneyAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlAmounts[j].name].type == 'text' ||
                     form[ctlAmounts[j].name].type == 'textarea') &&
                    (form[ctlAmounts[j].name].value.length > 0)) {
                    if (!checkMoneyAmount(form[ctlAmounts[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlAmounts[j].name];
                        }

                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oMoneyAmount[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oMoneyAmount[x][1];
                        }
                        bValid = false;
                    }
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oMoneyAmount)
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}


function checkMoneyAmount(AmountStr) {
    var result = false;
    var tooManySpaces = /\s/;
    if (tooManySpaces.test(AmountStr)) return result;

    var Amount = Number(AmountStr);
    if (isNaN(Amount) ||  Amount <= 0 ) {
        return result;
    }
    var moneyAmountPat = /^(\d+)([\.]*)(\d{0,2})$/;
    var matchArray = AmountStr.match(moneyAmountPat);
    return matchArray != null;
}

function validateWmId (form) {
    var bValid = true;
    var focusField = null;
    var i = 0;

    var fields = new Array();

    var oWmWallet = eval(" new " + form.name + "_validWmId();");
    var ctlWmWallets = form.elements;
    for (var x in oWmWallet) {
        var ctlName = oWmWallet[x][0];
        for (var j = 0; j < ctlWmWallets.length; j++) {
            if ( ctlWmWallets[j] != null && ( ctlWmWallets[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlWmWallets[j].name].type == 'text' ||
                     form[ctlWmWallets[j].name].type == 'textarea') &&
                    (form[ctlWmWallets[j].name].value.length > 0)) {
                    if (!checkWmId(form[ctlWmWallets[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlWmWallets[j].name];
                        }

                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oWmWallet[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oWmWallet[x][1];
                        }
                        bValid = false;
                    }
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oMoneyAmount)
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function checkWmId(WmIdStr) {
    var result = false;
    var tooManySpaces = /\s/;
    if (tooManySpaces.test(WmIdStr)) return result;

    var WmIdPat = /^(\d{12})$/;

    var matchArray = WmIdStr.match(WmIdPat);
    return matchArray != null;
}

function validateMarathonWallet (form) {
    var bValid = true;
    var focusField = null;
    var i = 0;

    var fields = new Array();

    var oMarathonWallet = eval(" new " + form.name + "_validMarathonWallet();");
    var ctlMarathonWallets = form.elements;
    for (var x in oMarathonWallet) {
        var ctlName = oMarathonWallet[x][0];
        for (var j = 0; j < ctlMarathonWallets.length; j++) {
            if ( ctlMarathonWallets[j] != null && ( ctlMarathonWallets[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlMarathonWallets[j].name].type == 'text' ||
                     form[ctlMarathonWallets[j].name].type == 'textarea') &&
                    (form[ctlMarathonWallets[j].name].value.length > 0)) {
                    if (!checkMarathonWallet(form[ctlMarathonWallets[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlMarathonWallets[j].name];
                        }

                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oMarathonWallet[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oMarathonWallet[x][1];
                        }
                        bValid = false;
                    }
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oMoneyAmount)
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function checkMarathonWallet( MarathonWalletStr) {
    var result = false;
    var tooManySpaces = /\s/;
    if (tooManySpaces.test(MarathonWalletStr)) return result;

    var MarathonWallet = Number(MarathonWalletStr);
    if (isNaN(MarathonWallet) || !( MarathonWallet >= 100000 && MarathonWallet <= 999999) ) {
        return result;
    }

    var MarathonWalletPat = /^(\d{6})$/;

    var matchArray = MarathonWalletStr.match(MarathonWalletPat);
    return matchArray != null;
}



function  validateWmWallet(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;

    var fields = new Array();

    var oWmWallet = eval(" new " + form.name + "_validWmWallet();");
    var ctlWmWallets = form.elements;
    for (var x in oWmWallet) {
        var ctlName = oWmWallet[x][0];
        for (var j = 0; j < ctlWmWallets.length; j++) {
            if ( ctlWmWallets[j] != null && ( ctlWmWallets[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlWmWallets[j].name].type == 'text' ||
                     form[ctlWmWallets[j].name].type == 'textarea') &&
                    (form[ctlWmWallets[j].name].value.length > 0)) {
                    if (!checkWmWallet(form[ctlWmWallets[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlWmWallets[j].name];
                        }

                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oWmWallet[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oWmWallet[x][1];
                        }
                        bValid = false;
                    }
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oMoneyAmount)
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function checkWmWallet(WmWalletStr) {
    var result = false;
    var tooManySpaces = /\s/;
    if (tooManySpaces.test(WmWalletStr)) return result;

    var WmWalletPat = /^([R|Z|E|U|B|Y]{1})(\d{12})$/;

    var matchArray = WmWalletStr.match(WmWalletPat);
    return matchArray != null;
}

//- for cashier (currency rates)
function validateRateAmount(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;

    var fields = new Array();

    var oMoneyAmount = eval(" new " + form.name + "_validRateAmount();");
    var ctlAmounts = form.elements;
    for (var x in oMoneyAmount) {
        var ctlName = oMoneyAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlAmounts[j].name].type == 'text' ||
                     form[ctlAmounts[j].name].type == 'textarea') &&
                    (form[ctlAmounts[j].name].value.length > 0)) {
                    if (!checkRateAmount(form[ctlAmounts[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlAmounts[j].name];
                        }

                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oMoneyAmount[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oMoneyAmount[x][1];
                        }
                        bValid = false;
                    }
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oMoneyAmount)
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}


function checkRateAmount(AmountStr) {
    var result = false;
    var tooManySpaces = /\s/;
    if (tooManySpaces.test(AmountStr)) return result;

    var Amount = Number(AmountStr);
    if (isNaN(Amount) ||  Amount <= 0 ) {
        return result;
    }
    var moneyAmountPat = /^(\d+)([\.]*)(\d{0,4})$/;
    var matchArray = AmountStr.match(moneyAmountPat);
    return matchArray != null;
}

function validateRequiredSpace(form) {
    return validateRequiredSpace_Name(form, form.name);
}

function validateRequiredSpace_Name(form, name) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var oRequired = eval(" new " + name + "_requiredSpace();");
    var ctlRequireds = form.elements;
    for (var x in oRequired) {
        var ctlName = oRequired[x][0];
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

                    if (value.length == 0) {

                        if (i == 0) {
                            focusField = field;
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oRequired[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oRequired[x][1];
                        }


                        isValid = false;
                    }

                } // if (field.type == 'text' ||
            } // if (ctlRequireds[j] != null
        }
        // for (j = 0; j < ctlRequireds.length; j++ )
    }
    // for (x in oRequired)
    if (fields.length > 0) {
        focusField.focus();
        //  alert("validateRequired:"+fields.join('\n'));
        alert(fields.join('\n'));
    }
    return isValid;
}

function validateInteger(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oComission = eval(" new " + form.name + "_integer();");
    for (var x in oComission) {
        if (form[oComission[x][0]] == null) continue;
        if ((form[oComission[x][0]].type == 'text') &&
            (form[oComission[x][0]].value.length > 0)) {
            if (!checkInteger(form[oComission[x][0]].value)) {
                if (i == 0) {
                    focusField = form[oComission[x][0]];
                }
                fields[i++] = oComission[x][1];
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

function checkInteger(IntStr) {
    var result = false;
    var tooManySpaces = /\s/;
    if (tooManySpaces.test(IntStr)) return result;

    var IntPat = /^(\d+)$/;

    var matchArray = IntStr.match(IntPat);
    return matchArray != null;
}

function validateFloat(form) {
        var bValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
         var formName = form.getAttributeNode("name");

        oFloat = eval('new ' + formName.value + '_float()');
        for (x in oFloat) {
                var field = form[oFloat[x][0]];

            if ((field.type == 'text') &&
                !field.disabled) {

                var value = field.value;

                if (value.length > 0) {
                    // remove '.' before checking digits
                    var tempArray = value.split('.');
                    var joinedString= tempArray.join('');

                    if (!checkInteger(joinedString)) {
                        bValid = false;
                        if (i == 0) {
                            focusField = field;
                        }
                        fields[i++] = oFloat[x][1];

                    } else {
                        var iValue = parseFloat(value);
                        if (isNaN(iValue) || iValue == 0) {
                            if (i == 0) {
                                focusField = field;
                            }
                            fields[i++] = oFloat[x][1];
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

function validateIntRange(form) {
        var isValid = true;
        var focusField = null;
        var i = 0;
        var fields = new Array();
        var formName = form.getAttributeNode("name");

        oRange = eval('new ' + formName.value + '_intRange()');
        for (x in oRange) {
            var field = form[oRange[x][0]];
            if (!field.disabled) {
                var value = '';
                if (field.type == 'text') {
                    value = field.value;
                }
                if (value.length > 0) {
                    var iMin = parseInt(oRange[x][2]("min"));
                    var iMax = parseInt(oRange[x][2]("max"));
                    var iValue = parseInt(value);
                    if (!(iValue >= iMin && iValue <= iMax)) {
                        if (i == 0) {
                            focusField = field;
                        }
                        fields[i++] = oRange[x][1];
                        isValid = false;
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

function validateIntOdd(form) {
    var isValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var formName = form.getAttributeNode("name");

    oRange = eval('new ' + formName.value + '_intOdd()');
    for (x in oRange) {
        var field = form[oRange[x][0]];
        if (!field.disabled) {
            var value = '';
            if (field.type == 'text') {
                value = field.value;
            }
            if (value.length > 0) {
                var iValue = parseInt(value);
                if ((iValue & 1) == 0) {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oRange[x][1];
                    isValid = false;
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

function validateWalletIdOrUserId(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oComission = eval(" new " + form.name + "_integer();");
    for (var x in oComission) {
        if (form[oComission[x][0]] == null) continue;
        if ((form[oComission[x][0]].type == 'text') &&
            (form[oComission[x][0]].value.length > 0)) {
            if (form[oComission[x][0]].name=="userId" && !document.getElementsByName("byWalletByFields")[0].checked)
            if (!checkInteger(form[oComission[x][0]].value)) {
                if (i == 0) {
                    focusField = form[oComission[x][0]];
                }
                fields[i++] = oComission[x][1];
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

//-