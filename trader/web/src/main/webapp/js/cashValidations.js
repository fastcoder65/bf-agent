function validateWallet (bmPrefix, thisForm) {

   if (thisForm.byWalletByFields[0].checked) {

       var bmWalletStr = thisForm.walletId.value.trim();

       var tooManySpaces = /\s/;

       if (bmWalletStr == null || (bmWalletStr.length == 0))
           return true;

       if (tooManySpaces.test(bmWalletStr))
           return false;

       var sBmWallet = (bmWalletStr.indexOf(bmPrefix) != -1)? bmWalletStr.split(bmPrefix)[1] : bmWalletStr;

       var BmWallet = Number(sBmWallet);

       var BmWalletPat = /^(\d+)$/;

       return (sBmWallet.match(BmWalletPat) && !isNaN(BmWallet));

   } else

    return true;
}


function validatePayAmount(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();

    var oPayAmount = eval(" new " + form.name + "_payAmount();");
    var ctlAmounts = form.elements;
    for (var x in oPayAmount) {
        var ctlName = oPayAmount[x][0];
        for (var j = 0; j < ctlAmounts.length; j++) {
            if (ctlAmounts[j] != null && (ctlAmounts[j].name.indexOf(ctlName) != -1)) {
                if ((form[ctlAmounts[j].name].type == 'text' ||
                     form[ctlAmounts[j].name].type == 'textarea') &&
                    (form[ctlAmounts[j].name].value.length > 0)) {
                    if (!checkPayAmount(form[ctlAmounts[j].name].value)) {
                        if (i == 0) {
                            focusField = form[ctlAmounts[j].name];
                        }
                        var emFound = false;
                        for (var ii = 0; ii < i; ii++) {
                            if (fields[ii].indexOf(oPayAmount[x][1]) != -1) {
                                emFound = true;
                                break;
                            }
                        }
                        if (!emFound) {
                            fields[i++] = oPayAmount[x][1];
                        }
                        bValid = false;
                    }
                } //  if ((form[ctlAmounts[j]
            } // if (ctlAmounts[j]
        }
        // for (j = 0;
    }
    // for (x in oPayAmount)
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function checkPayAmount(AmountStr) {

    if (AmountStr == null) return true;

    var result = false;

    var Amount = Number(AmountStr);

    if (isNaN(Amount) || Amount <= 0) {
        return result;
    }
    //return (Amount == Math.floor(Amount));
    return true;
}

