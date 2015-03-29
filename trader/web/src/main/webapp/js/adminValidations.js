/*
*  common validation rules on JS
*
*/

function validateHelpNames(form) {
    return validateHelpNames_Name(form, form.name);
}

function validateHelpNames_Name(form, name) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oHelpNames = eval(" new " + name + "_helpNames();");
    var ctlHelpNames = form.elements;
    for (var x in oHelpNames) {
        var ctlName = oHelpNames[x][0];
        for (var j = 0; j < ctlHelpNames.length; j++) {
            if (ctlHelpNames[j] != null && (ctlHelpNames[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlHelpNames[j].name].type == 'text' ||
                     form[ctlHelpNames[j].name].type == 'textarea') &&
                    (form[ctlHelpNames[j].name].value.length > 0)) {

                    if (!checkHelpNames(form[ctlHelpNames[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlHelpNames[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oHelpNames[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oHelpNames[x][1];
                        }
                        bValid = false;
                    } //  if (!checkStrAmount(form[ctlAmounts[j].name].value))
                } // if ((form[ctlHelpNames[j].name].type == 'text' ||
            } // if (ctlHelpNames[j] != null && (ctlHelpNames[j].name.indexOf(ctlName) != -1))
        } // for (j = 0; j < ctlHelpNames.length; j++) {
    } // for (var x in oHelpNames) {

    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function checkHelpNames(eventNameStr) {
  var validHelpNamesPattern = /^([^\{\}\|\\\^\'\~\*\[\]]*)$/;
  var tooManySpaces = /\s\s+/;
  var startSpaces = /^\s+/;
  var endSpaces = /\s+$/;
  return (validHelpNamesPattern.test(eventNameStr) && !tooManySpaces.test(eventNameStr) && !startSpaces.test(eventNameStr) && !endSpaces.test(eventNameStr));
}


//-