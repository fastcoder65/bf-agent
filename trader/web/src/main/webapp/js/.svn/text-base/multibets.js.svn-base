var FRAME_CONTENT = "frameContent";
var FAKE_MARKET_ID = -1;
var MARKET_ID = 0;
var BET_ID = 1;
var RESULT_ID = 2;
var MARKET_NAME = 3;
var BET_NAME = 4;
var RESULT_NAME = 5;
var BACK_ODDS = 6;
var LAY_ODDS = 7;
var BACK_AMOUNT = 8;
var LAY_AMOUNT = 9;
var DECPLACES = 5;
var waitForAmountTimeout;
var maxWaitForAmountSubmit = 20;
var waitMultiBetsDataLoadingTimeout;
var maxWaitMultiBetsDataLoading = 150;
var currentWait;
var AmountAvailable = null;
var AmountUpdated = false;
var multiFormNamePattern = "multiBetsForm_";
var multiBetsDataPattern = "multiBetsData_";
var multiMarketDataPattern = "selectAllBet";
var selectedFormIds = new Array();
var selectedFormsCount = 0;
var dataLoaded = false;
var errorsReturned = false;

var currentRootMarketId = 0;
var messageUnsufficientMoney = null;

function setDataLoaded(_loaded) {
    dataLoaded = _loaded;
    if (_loaded) statusMessage(" ");
}

function getDocumentHeader() {
    var DocHeader = [];
    var i;
    DocHeader.push("<html><head>");
    DocHeader.push("\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
    if (window.cssLinks != null && window.cssLinks.length > 0) {
        for ( i = 0; i < window.cssLinks.length; i++) {
            DocHeader.push("\n<link rel='stylesheet' href='" + cssLinks[i] + "' type='text/css'>");
        }
    }
    DocHeader.push('\n<script type="text/javascript">');
    DocHeader.push('\n var sImgPath = parent.sImgPath;');
    DocHeader.push('\n</script>');
    DocHeader.push("\n<head><body>");
    if (window.bodyScriptLinks != null && window.bodyScriptLinks.length > 0) {
        for ( i = 0; i < window.bodyScriptLinks.length; i++) {
            DocHeader.push('\n<script type="text/javascript" src="' + bodyScriptLinks[i] + '"></script>');
        }
    }
    DocHeader.push('\n<script type="text/javascript">');
    DocHeader.push('\nfunction showTip(titleText) {');
    DocHeader.push('\n  Tip(titleText, BGCOLOR, "' + TT_BGCOLOR + '", BORDERCOLOR, "' + TT_BORDERCOLOR + '",  FADEIN, 400, FADEOUT, 400, ABOVE, true, WIDTH, 0, PADDING, 8, TEXTALIGN, "justify", OFFSETX, 0);');
    DocHeader.push('\n }');
    DocHeader.push('\n</script>');
    return DocHeader.join('');
}
function getTableCaptions() {
    var aHeader = [];
    aHeader.push('\n<td class="row-title-free" nowrap>' + sCapMarketName + "/" + sCapBetName + '</td>');
    aHeader.push('\n<td class="row-title">' + sCapResultName + '</td>');
    aHeader.push('\n<td class="row-title-free" nowrap>');
    aHeader.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%">');
    aHeader.push('\n<tr style="font-size: 10px; font-weight:bold;">');
    aHeader.push('\n<td>' + sCapOdds + '/' + sCapAmount + '</td>');
    aHeader.push('\n<td align="right">' + sCapBack + '</td>');
    aHeader.push('\n</tr></table>');
    aHeader.push('\n</td>');
    aHeader.push('\n<td class="row-title-free" nowrap>');
    aHeader.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%">');
    aHeader.push('\n<tr style="font-size: 10px; font-weight:bold;">');
    aHeader.push('\n<td>' + sCapLay + '</td>');
    aHeader.push('\n<td align="right">' + sCapOdds + '/' + sCapAmount + '</td>');
    aHeader.push('\n</tr></table>');
    aHeader.push('\n</td>');
    aHeader.push('\n<td class="row-title" onmouseover="showTip(\'' + sCbTitleSelectAll + '\')"  nowrap align="center" width="10px">');
    aHeader.push('\n<input class="mainBar-content" style="background-color: #FFFFFF;" type="checkbox" id="cbSelectAll" name="cbSelectAll" onclick="swSelectCheckBox(this);"></td>');
    return aHeader.join('');
}
function getTableHeader() {
    var aHeader = [];
    aHeader.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%">');
    aHeader.push('\n<tr>');
    aHeader.push('\n<td class="rows-border" valign="top" id="table_title">');
    aHeader.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%" >');
    aHeader.push('\n<tr>');
    aHeader.push('\n<td id="titles">');
    aHeader.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%">');
    aHeader.push(getSpacerRow());
    aHeader.push('\n<tr>');
    if (getTableCaptions)
        aHeader.push(getTableCaptions());
    aHeader.push('\n</tr>');
    aHeader.push('\n</table><td></tr>');
    return aHeader.join('');
}
function getTableFooter() {
    return'\n</table></td></tr></table>';
}
function getSpacerRow() {
    var SpacerRow = [];
    SpacerRow.push('\n<tr><td height="0px" width="100%"></td>');
    SpacerRow.push('\n<td><img src="' + sSpacer + '" height="0" width="200px" alt=""></td>');
    SpacerRow.push('\n<td><img src="' + sSpacer + '" height="0" width="168px" alt=""></td>');
    SpacerRow.push('\n<td><img src="' + sSpacer + '" height="0" width="168px" alt=""></td>');
    SpacerRow.push('\n<td><img src="' + sSpacer + '" height="0" width="10px" alt=""></td>');
    SpacerRow.push('\n</tr>');
    return SpacerRow.join('');
}
function getTableBody(betRows) {
    var aTableBody = [];
    var iOrder = 0;
    var _marketId = "";
    var _betId = "";
    var _multiFormName = "";
    var _multiBetsDataName = "";
    var _order = "";
    var _resultId = "";
    var marketAndBetId = "";
    function getSpinTable(resultId, sSide) {
        var SpinTable = [];
        SpinTable.push('\n<table cellpadding="0" cellspacing="0" border="0" height="100%">');
        SpinTable.push('\n<tr><td><img alt="" src="' + sSpinUp + '" onclick=\'parent.oddsUp("new' + sSide + 'Odds(' + resultId + ')","' + FRAME_CONTENT + '");\'></td></tr>');
        SpinTable.push('\n<tr><td><img alt="" src="' + sSpinDown + '" onclick=\'parent.oddsDown("new' + sSide + 'Odds(' + resultId + ')","' + FRAME_CONTENT + '");\'></td></tr></table>');
        return SpinTable.join('');
    }
    if (betRows != null && betRows.length > 0) {
        for (var i = 0; i < betRows.length; i++) {
            if (iOrder == 0)
                _betId = "1nah";
            _multiFormName = multiFormNamePattern + betRows[i][MARKET_ID] + "~" + betRows[i][BET_ID];
            iOrder++;
            if (iOrder == 1 || _betId != betRows[i][BET_ID]) {
                if (iOrder > 1) {
                    aTableBody.push('\n</form></table></td></tr></table></td></tr>');
                }
                if (_marketId != betRows[i][MARKET_ID]) {
                    _marketId = betRows[i][MARKET_ID];
                    aTableBody.push('\n<tr><td><table cellpadding="0" cellspacing="0" border="0" width="100%">');
                    aTableBody.push('\n<tr><td  width="100%" class="row-free">');
                    aTableBody.push('\n<a href="javascript:parent.gotoMarket(' + betRows[i][MARKET_ID] + ',' + betRows[i][BET_ID] + ');"><b>' + betRows[i][MARKET_NAME] + '</b></a></td>');
                    aTableBody.push('\n<td class="row" nowrap align="center" width="10px">');
                    var sSelectAllMarketTip = sCbTitleSelectAllMarketFor + ' `' + betRows[i][MARKET_NAME] + '`';
                    aTableBody.push('\n<input class="mainBar-content" type="checkbox" onmouseover="showTip(\'' + sSelectAllMarketTip + '\')" name="selectAllMarket(' + betRows[i][MARKET_ID] + ')"  id="selectAllMarket(' + betRows[i][MARKET_ID] + ')" onclick=\'parent.selectAllMarket(this, "' + betRows[i][MARKET_ID] + '");\' ></td></tr>');
                    aTableBody.push('\n</table></td></tr>');
                }
                aTableBody.push('\n<tr><td><table cellpadding="0" cellspacing="0" border="0"  width="100%"><tr><td>');
                _multiBetsDataName = multiBetsDataPattern + betRows[i][MARKET_ID] + "~" + betRows[i][BET_ID];
                aTableBody.push('\n<iframe name="' + _multiBetsDataName + '" id="' + _multiBetsDataName + '" style="width:0px; height:0px; overflow:hidden; border:none"></iframe>');
                aTableBody.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%">');
                aTableBody.push('\n<form method="POST" action="' + sMultiBetAction + '" id="' + _multiFormName + '" target="' + _multiBetsDataName + '" >');
                aTableBody.push('\n<input type="hidden" name="marketId" value="' + parent.currentRootMarketId + '">');
                aTableBody.push('\n<input type="hidden" name="betMarketName" value="' + betRows[i][MARKET_NAME] + "/" + betRows[i][BET_NAME] + '">');
                aTableBody.push(getSpacerRow());
            }
            _order = formatNumber(iOrder, DECPLACES);
            _resultId = _order + '_' + betRows[i][RESULT_ID];
            aTableBody.push('\n<tr  id="rowid_' + _resultId + '">');
            if (_betId != betRows[i][BET_ID]) {
                aTableBody.push('\n<td class="cell-row" width="100%"><table width="100%" height="100%" border="0"><tr><td width="100%">&nbsp;<a href="javascript:parent.gotoMarket(' + betRows[i][MARKET_ID] + ',' + betRows[i][BET_ID] + ');"><i>' + betRows[i][BET_NAME] + '</i></a></td>');
                var sSelectAllBetTip = sCbTitleSelectAllBetFor + ' `' + betRows[i][MARKET_NAME] + ' - <i>' + betRows[i][BET_NAME] + '</i>`';
                marketAndBetId = betRows[i][MARKET_ID] + '~' + betRows[i][BET_ID];
                aTableBody.push('\n<td><input class="mainBar-content" onmouseover="showTip(\'' + sSelectAllBetTip + '\')" type="checkbox" name="' + multiMarketDataPattern + '(' + marketAndBetId + ')" id="' + multiMarketDataPattern + '(' + marketAndBetId + ')" onclick=\'parent.selectAllBet(this, "' + betRows[i][BET_ID] + '");\' ></td></tr></table>');
                aTableBody.push('\n</td>');
            } else {
                aTableBody.push('\n<td class="cell-row" width="100%">&nbsp;</td>');
            }
            aTableBody.push('\n<td class="cell-row" width="200px" >&nbsp;' + betRows[i][RESULT_NAME] + '<input type="hidden" id="resultName(' + _resultId + ')" name="resultName(' + _resultId + ')" value="' + betRows[i][RESULT_NAME] + '"></td>');
            aTableBody.push('\n<td class="cell-row" align="right"  width="168px">');
            aTableBody.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%">');
            aTableBody.push('\n<tr><td width="116px"><table cellpadding="1" cellspacing="0" border="0">');
            aTableBody.push('\n<tr><td><input maxlength="5" id="newBackOdds(' + _resultId + ')" type="text" name="newBackOdds(' + _resultId + ')" style="width:40px;text-align:right"></td>');
            aTableBody.push('\n<td width="16px" height="16px" style="text-align: left;">' + getSpinTable(_resultId, "Back") + '</td>');
            aTableBody.push('\n<td><input maxlength="9" type="text" id="newBackAmount(' + _resultId + ')" name="newBackAmount(' + _resultId + ')" style="width:60px;text-align:right"></td>');
            aTableBody.push('\n</tr></table></td>');
            aTableBody.push('\n<td class="cell-left-high-free" width="50px" height="35px">');
            if (betRows[i][BACK_ODDS] || betRows[i][BACK_AMOUNT]) {
                aTableBody.push('<b>' + betRows[i][BACK_ODDS] + '</b><br>' + ((betRows[i][BACK_AMOUNT]) ? currSymbol + betRows[i][BACK_AMOUNT] : ''));
            } else {
                aTableBody.push('&nbsp;<br>&nbsp;');
            }
            aTableBody.push('\n</td></tr></table></td>');
            aTableBody.push('\n<td class="cell-row" align="right"  width="168px">');
            aTableBody.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%"><tr>');
            aTableBody.push('\n<td class="cell-right-high-free" width="50px" height="35px">');
            if (betRows[i][LAY_ODDS] || betRows[i][LAY_AMOUNT]) {
                aTableBody.push('<b>' + betRows[i][LAY_ODDS] + '</b><br>' + ((betRows[i][LAY_AMOUNT]) ? currSymbol + betRows[i][LAY_AMOUNT] : ''));
            } else {
                aTableBody.push('&nbsp;<br>&nbsp;');
            }
            aTableBody.push('\n</td><td width="116px">');
            aTableBody.push('\n<table cellpadding="1" cellspacing="0" border="0"><tr><td>');
            aTableBody.push('\n<input maxlength="5" id="newLayOdds(' + _resultId + ')" type="text" name="newLayOdds(' + _resultId + ')"  style="width:40px;text-align:right"></td>');
            aTableBody.push('\n<td width="16px" height="16px" style="text-align: left;">' + getSpinTable(_resultId, "Lay") + '</td>');
            aTableBody.push('\n<td><input maxlength="9" type="text" id="newLayAmount(' + _resultId + ')" name="newLayAmount(' + _resultId + ')" style="width:60px;text-align:right"></td>');
            aTableBody.push('\n</tr></table></td></tr></table></td>');
            aTableBody.push('\n<td class="row" nowrap align="center" width="10px">');
            aTableBody.push('\n<input class="mainBar-content" type="checkbox" name="selected(' + _resultId + ')" id="selected(' + _resultId + ')"></td></tr>');
            if (_betId != betRows[i][BET_ID]) {
                _betId = betRows[i][BET_ID];
            }
        }
        aTableBody.push('\n</form></table></td></tr></table></td></tr>');
    } else {
        aTableBody.push('\n<tr>');
        aTableBody.push('\n<td align="middle" valign="middle" colspan="5">');
        aTableBody.push('\n<table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">');
        aTableBody.push('\n<tr>');
        aTableBody.push('\n<td align="center" valign="middle">' + sNoActiveEventsFound + '</td>');
        aTableBody.push('\n</tr></table></td></tr>');
    }
    return aTableBody.join('');
}
function multiBetsDataLoading(form) {
    var _form = (form) ? form : document.forms.multiBetSelectorForm;
    if (_form) {
        hideContent();
        statusMessage(sStatusLoadingDataMessage);
        _form.submit();
    }
    var marketSelector = document.getElementById("marketId");
    if (marketSelector) {
        marketSelector.disabled = true;
    }
}

function collectSelectedFormIds() {
    var result = false;
    selectedFormIds = new Array();
    selectedFormsCount = 0;
    var INPUTs = frames[FRAME_CONTENT].document.getElementsByTagName("input");
    if (INPUTs != null && INPUTs.length > 0) {
        for (var i = 0; i < INPUTs.length; i++) {
            if ((INPUTs[i].type == "checkbox") && (INPUTs[i].name.indexOf("selected") != -1)) {
                if ((INPUTs[i].checked != null) && INPUTs[i].checked && (!(INPUTs[i].form.id in selectedFormIds))) {
                    selectedFormIds[INPUTs[i].form.id] = INPUTs[i].form;
                    selectedFormsCount++;
                }
            }
        }
    }
    if (selectedFormsCount == 0) {
        alert(sNoChecksFound);
    }
}
function doMultiBet(thisSubmitButton) {
    if (!processOnClickForSubmit(thisSubmitButton, true))return;
    collectSelectedFormIds();
    var dataIsValid = (selectedFormsCount > 0);
    if (dataIsValid) {
        for (var formId in selectedFormIds) {
            var aForm = selectedFormIds[formId];
            if (aForm == null || !validateChecks(aForm) || !validateMultiBetsForm(aForm)) {
                dataIsValid = false;
                enableApplyButton();
                break;
            }
        }
        if (dataIsValid) {
            currentWait = maxWaitForAmountSubmit;
            loadAmountData();
            var callProc = "waitForAmount();";
            waitForAmountTimeout = setTimeout(callProc, 100);
        }
    } else {
        enableApplyButton();
    }
}
function applyBackSide(thisForm) {
    if (thisForm && validateDefBackMultiBetsForm(thisForm)) {
        var side = "Back";
        var odds = document.getElementById("def" + side + "Odds").value;
        var amount = document.getElementById("def" + side + "Amount").value;
        populateBets(side, odds, amount);
    }
}
function applyLaySide(thisForm) {
    if (thisForm && validateDefLayMultiBetsForm(thisForm)) {
        var side = "Lay";
        var odds = document.getElementById("def" + side + "Odds").value;
        var amount = document.getElementById("def" + side + "Amount").value;
        populateBets(side, odds, amount);
    }
}
function enableApplyButton() {
    var applyButton = document.getElementById("submitMultiBetsButton");
    if (applyButton)applyButton.disabled = false;
    var marketSelector = document.getElementById("marketId");
    if (marketSelector)marketSelector.disabled = false;
}
function populateBets(side, odds, amount) {
    var _doc = frames[FRAME_CONTENT].document;
    var Rows = _doc.getElementsByTagName("tr");
    var selectedRows = 0;
    for (var i = 0; i < Rows.length; i++) {
        var RowId = Rows[i].id;
        if (RowId.indexOf("rowid_") != -1) {
            var ids = RowId.split("_");
            var resultId = ids[1] + "_" + ids[2];
            if (resultId && _doc.getElementById("selected(" + resultId + ")").checked) {
                _doc.getElementById("new" + side + "Odds(" + resultId + ")").value = odds;
                _doc.getElementById("new" + side + "Amount(" + resultId + ")").value = amount;
                selectedRows++;
            }
        }
    }
    if (selectedRows == 0) {
        alert(sNoChecksFound);
    }
}
function getTotalAmount() {
    var TotalAmount = 0.0;
    var _doc = frames[FRAME_CONTENT].document;
    var Rows = (_doc) ? _doc.getElementsByTagName("tr") : new Array();
    for (var i = 0; i < Rows.length; i++) {
        var RowId = Rows[i].id;
        if (RowId.indexOf("rowid_") != -1) {
            var ids = RowId.split("_");
            var resultId = ids[1] + "_" + ids[2];
            if (resultId && _doc.getElementById("selected(" + resultId + ")").checked) {
                var rowLiability = 0.0;
                var sBackOdds = _doc.getElementById("newBackOdds(" + resultId + ")").value;
                var sBackAmount = _doc.getElementById("newBackAmount(" + resultId + ")").value;
                var sLayOdds = _doc.getElementById("newLayOdds(" + resultId + ")").value;
                var sLayAmount = _doc.getElementById("newLayAmount(" + resultId + ")").value;
                var nBackOdds = (sBackOdds && sBackOdds.length > 0 && !isNaN(Number(sBackOdds))) ? Number(sBackOdds) : 0.0;
                var nBackAmount = (sBackAmount && sBackAmount.length > 0 && !isNaN(Number(sBackAmount))) ? Number(sBackAmount) : 0.0;
                var nLayOdds = (sLayOdds && sLayOdds.length > 0 && !isNaN(Number(sLayOdds))) ? Number(sLayOdds) : 0.0;
                var nLayAmount = (sLayAmount && sLayAmount.length > 0 && !isNaN(Number(sLayAmount))) ? Number(sLayAmount) : 0.0;
                rowLiability = nBackAmount + (nLayOdds - 1) * nLayAmount;
                TotalAmount += rowLiability;
            }
        }
    }
    return TotalAmount;
}
function gotoMarket(iMarketId, iEventId, bCoupon, sOrigin) {
    var win = null;
    if (parent.opener)
    {
        try {
            if (parent.opener.top.frames['marketPage']) {
                win = parent.opener.top.frames['marketPage'];
            }
        } catch(x) {
            x.toString();
        }
    }
    if (win == null)
        if (opener)
        {
            try {
                if (opener.top.frames['marketPage']) {
                    win = opener.top.frames['marketPage'];
                }
            } catch(x) {
                x.toString();
            }
        }
    if (win && win.goMarket) {
        win.goMarket(iEventId, null, true, iMarketId, null, sOrigin);
        win.focus();
    } else {
        alert(sOpenAgainMessage);
        close();
    }
}
function updateSumData(currencySign, freeAmount, blockedAmount) {
    AmountAvailable = freeAmount;
    AmountUpdated = true;
}
function loadAmountData() {
    AmountUpdated = false;
    if (frames['sumManager'])
        frames['sumManager'].location.replace(sLoadAmountDataAction);
}
function enableRows() {
    var _doc = frames[FRAME_CONTENT].document;
    var INPUTs = _doc.getElementsByTagName("input");
    for (var i = 0; i < INPUTs.length; i++) {
        if ((INPUTs[i].type == "checkbox") && (INPUTs[i].id.indexOf("selected") != -1)) {
            var rowId = INPUTs[i].id.substring(INPUTs[i].id.indexOf("(") + 1, INPUTs[i].id.indexOf(")"));
            _doc.getElementById("newBackOdds(" + rowId + ")").disabled = false;
            _doc.getElementById("newBackAmount(" + rowId + ")").disabled = false;
            _doc.getElementById("newLayOdds(" + rowId + ")").disabled = false;
            _doc.getElementById("newLayAmount(" + rowId + ")").disabled = false;
            _doc.getElementById("selected(" + rowId + ")").disabled = false;
            _doc.getElementById("resultName(" + rowId + ")").disabled = false;
        }
    }
}
function disableUncheckedRows() {
    var _doc = frames[FRAME_CONTENT].document;
    var INPUTs = _doc.getElementsByTagName("input");
    for (var i = 0; i < INPUTs.length; i++) {
        if ((INPUTs[i].type == "checkbox") && (INPUTs[i].id.indexOf("selected") != -1)) {
            if (!INPUTs[i].checked) {
                var rowId = INPUTs[i].id.substring(INPUTs[i].id.indexOf("(") + 1, INPUTs[i].id.indexOf(")"));
                _doc.getElementById("newBackOdds(" + rowId + ")").disabled = true;
                _doc.getElementById("newBackAmount(" + rowId + ")").disabled = true;
                _doc.getElementById("newLayOdds(" + rowId + ")").disabled = true;
                _doc.getElementById("newLayAmount(" + rowId + ")").disabled = true;
                _doc.getElementById("selected(" + rowId + ")").disabled = true;
                _doc.getElementById("resultName(" + rowId + ")").disabled = true;
            }
        }
    }
}
function selectAllMarket(thisControl, marketId) {
    var targetForms = new Array();
    var _doc = frames[FRAME_CONTENT].document;
    for (var fN = 0; fN < _doc.forms.length; fN++) {
        if (_doc.forms[fN].id.indexOf(multiFormNamePattern) != -1 && _doc.forms[fN].id.indexOf(marketId) != -1) {
            targetForms[targetForms.length] = _doc.forms[fN];
        }
        selectMultiMarket(_doc.forms[fN], marketId, thisControl.checked);
    }
    if (targetForms.length > 0) {
        for (var i = 0; i < targetForms.length; i++) {
            selectForm(targetForms[i], thisControl.checked);
        }
    }
}
function selectAllBet(thisControl, betId) {
    var _doc = frames[FRAME_CONTENT].document;
    var targetForm = null;
    for (var fN = 0; fN < _doc.forms.length; fN++) {
        if (_doc.forms[fN].id.indexOf(multiFormNamePattern) != -1 && _doc.forms[fN].id.indexOf(betId) != -1) {
            targetForm = _doc.forms[fN];
        }
    }
    if (targetForm)
        selectForm(targetForm, thisControl.checked);
}
function selectForm(form, value) {
    var formElements = form.elements;
    for (var i = 0; i < formElements.length; i++) {
        if (formElements[i].type == "checkbox" && formElements[i].name.indexOf("selected") != -1) {
            formElements[i].checked = value;
        }
    }
}
function selectMultiMarket(form, marketId, value) {
    var formElements = form.elements;
    for (var i = 0; i < formElements.length; i++) {
        if (formElements[i].type == "checkbox" && formElements[i].id.indexOf(marketId) != -1) {
            formElements[i].checked = value;
        }
    }
}
function waitForAmount() {
    currentWait--;
    if (!AmountUpdated && currentWait > 0) {
        waitForAmountTimeout = setTimeout("waitForAmount();", 100);
    } else {
        clearTimeout(waitForAmountTimeout);
        waitForAmountTimeout = null;
        if (!checkDoBet()) {
            enableApplyButton();
        } else {
            disableUncheckedRows();
            dataLoaded = false;
            errorsReturned = false;
            for (var formId in selectedFormIds) {
                if (formId) {
                    var aForm = selectedFormIds[formId];
                    if (aForm) {
                        with (aForm) {
                            marketId.value = FAKE_MARKET_ID;
                            submit();
                        }
                    }
                    currentWait = maxWaitMultiBetsDataLoading;
                    statusMessage(sStatusProcessing + "\"" + aForm.betMarketName.value + "\" ...");
                    setTimeout("waitMultiBetsDataLoading('" + formId + "')", 200);
                }
                break;
            }
        }
    }
}
function getCurrentIndexByValue(thisCombo, currentValue) {
    var selectedIndex = -1;
    for (var i = 0; i < thisCombo.options.length; i++) {
        if (thisCombo.options[i].selected || currentValue == thisCombo.options[i].value) {
            selectedIndex = thisCombo.selectedIndex;
            break;
        }
    }
    return selectedIndex;
}

function waitMultiBetsDataLoading(formId) {
    currentWait--;
    var aForm = selectedFormIds[formId];
    if (aForm && !dataLoaded && currentWait > 0) {
        if ((currentWait % 5) == 0) {
            var restSeconds = currentWait / 5;
            statusMessage(sStatusProcessing + "\"" + aForm.betMarketName.value + "\"," + restSeconds + ' ' + sSecondsRemain);
        }
        waitMultiBetsDataLoadingTimeout = setTimeout("waitMultiBetsDataLoading('" + formId + "');", 200);
    } else {
        if (!errorsReturned && !messageUnsufficientMoney) {
            selectedFormIds[formId] = null;
            delete selectedFormIds[formId];
            dataLoaded = false;
            errorsReturned = false;
            for (var _formId in selectedFormIds) {
                if (_formId) {
                    var _aForm = selectedFormIds[_formId];
                    if (_aForm) {
                        with (_aForm) {
                            marketId.value = FAKE_MARKET_ID;
                            submit();
                        }
                    }
                    currentWait = maxWaitMultiBetsDataLoading;
                    statusMessage(sStatusProcessing + "\"" + _aForm.betMarketName.value + "\" ...");
                    setTimeout("waitMultiBetsDataLoading('" + _formId + "')", 200);
                    return;
                } else {
                    break;
                }
             }
            multiBetsComplete();
           } else {
            enableApplyButton();
            enableRows();
        }
    }
}

function multiBetsComplete() {
    clearTimeout(waitMultiBetsDataLoadingTimeout);
    waitMultiBetsDataLoadingTimeout = null;
    var form = document.forms['multiBetSelectorForm'];
    selectedFormIds = new Array();
    selectedFormsCount = 0;
    messageUnsufficientMoney = null;
    multiBetsDataLoading(form);
}

function statusMessage(sMessage) {
    if (window.status) {
        window.status = sMessage;
    }
    if (document.getElementById('statusMessage')) {
        document.getElementById('statusMessage').innerHTML = sMessage;
    }
}

function checkDoBet() {
    var result = true;
    var totalAmount = getTotalAmount();
    if (!AmountAvailable || totalAmount > AmountAvailable) {
        result = confirm(sAvailableAmountConfirmation);
    }
    return result;
}