var IX_ODDS_ID = 0;
var IX_AMOUNT_ID = 1;
var IX_RESULT_ID = 2;
var IX_ODDS = 3;
var IX_AMOUNT = 4;
var IX_RESULT = 5;
var AVG = "2";
var ON = "1";
var OFF = "0";
var BetWin = null;
var currentWait = null;
function blankPage() {
    return"<html><head><title>Blank page</title></head><body></body></html>";
}
function preBetsExists() {
    return(itemsExist(myBackItems) || itemsExist(myLayItems));
}
function clearPreBets() {
    clearBetItems(myBackItems);
    clearBetItems(myLayItems);
    if (top.main.clearProfitLoss) {
        top.main.clearProfitLoss();
    }
}

function do_CancelAll() {
    unHighlightCells();
    clearPreBets();
    var event_Title = top.mainBar.document.getElementById("currentEventTitle").innerHTML;
    var eventId = top.main.currentEventId;

    makeBetPageHTML(eventId, event_Title);
    doShowBetPage();
}

function setFocus(Id) {
    var _myBetIds = eval("myBetIds");
    var _elId = _myBetIds[Id][IX_ODDS_ID];
    if (_elId) {
        setTimeout("focusElement ('" + _elId + "')", 10);
    }
}

function focusElement(elementId) {
    var _betPage = eval("top.betBorder.betPage");
    var betDoc = _betPage.document;
    var element = betDoc.getElementById(elementId);
    if (element) {
       try {
        element.focus();
        element.select();
       } catch (x){}
    }
}
function do_BackAll() {
    var TDs = top.main.document.getElementsByTagName("td");

    for (var i = 0; i < TDs.length; i++) {
        var Id = TDs[i].id;
        if (Id != null && Id.indexOf("b") == 0) {
            if (top.main.document.getElementById(Id).className == "cell-left-high")
            {
                top.main.doStake(Id, true);
            }
        }
    }
    var _event_Title = top.mainBar.document.getElementById("currentEventTitle").innerHTML;
    var _eventId = top.main.currentEventId;
    makeBetPageHTML(_eventId, _event_Title);
    if (myBackItems.length > 0 && myBackItems[myBackItems.length - 1] != null) {
        var _Id = myBackItems[myBackItems.length - 1][0];
        setFocus(_Id);
    }
}
function do_LayAll()
{
    var TDs = top.main.document.getElementsByTagName("td");

    for (var i = 0; i < TDs.length; i++) {
        var Id = TDs[i].id;
        if (Id != null && Id.indexOf("l") == 0) {
            if (top.main.document.getElementById(Id).className == "cell-right-high")
            {
                top.main.doStake(Id, true);
            }
        }
    }
    var _event_Title = top.mainBar.document.getElementById("currentEventTitle").innerHTML;
    var _eventId = top.main.currentEventId;
    makeBetPageHTML(_eventId, _event_Title);
    if (myLayItems.length > 0 && myLayItems[myLayItems.length - 1] != null) {
        var _Id = myLayItems[myLayItems.length - 1][0];
        setFocus(_Id);
    }
}

function getCurrentEventTitle () {
 return ( top.mainBar.document.getElementById("currentEventTitle") != null) ? top.mainBar.document.getElementById("currentEventTitle").innerHTML : "N/A";
}

function makeBetFormContainer_start(aBetFormAction, aEventId)
{
    var a = [];
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' height='100%'>");
    a.push("\n<tr><td class='bet-content' valign='top'>");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%'>");
    a.push("\n<tr><td id='bet-border' class='bet-border' >");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' >");
    a.push("\n<form name='betForm' method='POST' action='" + aBetFormAction + "' target='mybetPage' >");
    a.push("\n<input type=\"hidden\" name=\"eventId\" value='" + aEventId + "' >");
    a.push('\n<input type=\"hidden\" name=\"betMarketName\" value="' + getCurrentEventTitle () + '">');
    a.push("\n<input type=\"hidden\" id='" + sTokenName + "' name='" + sTokenName + "' value='" + sToken + "' > ");
    return a.join('');
}
function getEventTitle(aEventTitle) {
    return sEventTitleLabel + ": <b>" + aEventTitle + "</b>";
}
function makeBetFormContainer_end(){
    return "\n</form></table></td></tr>\n</table></td></tr></table>";
}

function makeBetPageHTML(iEventId, sEventTitle) {
    document.getElementById("sc12").style.display = "block";
    document.getElementById("applyButton").disabled = false;
    document.getElementById("applyButton").disabledItem = false;
    var betDoc = top.betBorder.betPage.document;
    betDoc.clear();
    betDoc.write("<html><head><link rel='stylesheet' href='" + sMainCss + "' type='text/css'>");
    betDoc.write("<link rel='stylesheet' href='" + sBetPageCss + "' type='text/css'></head>");
    betDoc.write("<body style='background-color:#F9F9F9'>");
    var sBetsHTML = makeBetsHTML(iEventId, sEventTitle);
    betDoc.write(sBetsHTML);
    betDoc.write("</body></html>");
    betDoc.close();
    top.betBorder.document.getElementById("currentEventTitle").innerHTML = getEventTitle(sEventTitle);
    for (var i = 0; i < myBackItems.length; i++) {
        if (myBackItems[i] != null && myBackItems[i][0] != null) {
            Id = myBackItems[i][0];
            populateBets(betDoc, myBetIds[Id]);
        }
    }
    for (var i = 0; i < myLayItems.length; i++) {
        if (myLayItems[i] != null && myLayItems[i][0] != null) {
            Id = myLayItems[i][0];
            populateBets(betDoc, myBetIds[Id]);
        }
    }
    if (document.getElementById("applyButton")) {
        document.getElementById("applyButton").disabled = false;
    }

    calcTotalLiability();
    calcProfitLossLocal();
    if (preBetsExists()) document.getElementById("ivnviteToBet").innerHTML = '';
    else document.getElementById("ivnviteToBet").innerHTML = sIvnviteToBetText;

   setClearButtonDisabled ( !preBetsExists() );

}

function calcProfitLossLocal() {
    top.main.clearProfitLoss();
    for (var BetId in myBetIds) {
        var Bet = myBetIds[BetId];
        var betSide = BetId.substring(0, 1);
        var _endPos = BetId.indexOf("_", 0);
        var resultId = BetId.substring(1, _endPos);
        top.main.addProfitLossLocal(resultId, betSide, Bet[IX_AMOUNT], Bet[IX_RESULT]);
    }
    top.main.updateProfitLossLocal();
}
function populateBets(betDoc, myBetIdItem) {
    if (betDoc == null)return;
    if (myBetIdItem == null)return;
    if (myBetIdItem[IX_ODDS] != null) {
        if (betDoc.getElementById(myBetIdItem[IX_ODDS_ID]) != null) {
            betDoc.getElementById(myBetIdItem[IX_ODDS_ID]).value = ((myBetIdItem[IX_ODDS] == null) ? "" : myBetIdItem[IX_ODDS]);
        }
    }
    if (myBetIdItem[IX_AMOUNT] != null) {
        if (betDoc.getElementById(myBetIdItem[IX_AMOUNT_ID]) != null) {
            betDoc.getElementById(myBetIdItem[IX_AMOUNT_ID]).value = ((myBetIdItem[IX_AMOUNT] == null) ? "" : myBetIdItem[IX_AMOUNT]);
        }
    }
    if (myBetIdItem[IX_RESULT_ID] != null && myBetIdItem[IX_RESULT_ID].length != 0) {
        if (betDoc.getElementById(myBetIdItem[IX_RESULT_ID]) != null) {
            betDoc.getElementById(myBetIdItem[IX_RESULT_ID]).innerHTML = ((myBetIdItem[IX_RESULT] == null) ? "" : formatCurrency(sCurrSign, myBetIdItem[IX_RESULT]));
        }
    }
}
function clearBetItems(myBetItems) {
    for (var i = 0; i < myBetItems.length; i++) {
        if (myBetItems[i] != null && myBetItems[i][0] != null) {
            Id = myBetItems[i][0];
            myBetIds[Id] = null;
            delete myBetIds[Id];
            myBetItems[i] = null;
            delete myBetItems[i];
        }
    }
    myBetItems.length = 0;
}
function cancelPreStake(Id) {
    unHighlight_OneCell(Id);
    removeBetItem(Id);
    if (top.mainBar.document.getElementById("currentEventTitle")) {
        var _event_Title = top.mainBar.document.getElementById("currentEventTitle").innerHTML;
        var _eventId = top.main.currentEventId;
        makeBetPageHTML(_eventId, _event_Title);
    }
    doShowBetPage();
}
function placeBet(Id, bidType, odds, amount, resultId, resultValue) {
    var sPx = "";
    var sCalcProfitCall = "";
    sBetCellClass = (bidType == top.main.bidTypeBACK) ? sCellBackClass : sCellLayClass;
    sOddsTitle = (bidType == top.main.bidTypeBACK) ? sBackersOdds : sLayersOdds;
    sAmountTitle = (bidType == top.main.bidTypeBACK) ? sBackersAmount : sLayersAmount;

    if (bidType == top.main.bidTypeBACK) {
        sPx = "Back";
        sOddsElementId = "new" + sPx + "Odds(" + Id + ")";
        sAmountElementId = "new" + sPx + "Amount(" + Id + ")";
        sCalcProfitCall = "parent.calcProfit('" + sOddsElementId + "','" + sAmountElementId + "', 'yourProfit(" + Id + ")');parent.calcTotalLiability();parent.calcProfitLossLocal();";
        var sYourProfitHeader = "<table style='width:100px;height:100%;' cellpadding='0' cellspacing='0' border='0'><tr><td style='height:100%;text-align:left' class='cell-title'>&nbsp;"
                                + sYourProfit + "</td></tr></table>";
        var asBackHeader = [];
        asBackHeader.push("<tr class='row-title'>");
        asBackHeader.push("<td class='cell-title' width='1%'>&nbsp;</td>");
        asBackHeader.push("<td class='cell-title' width='50%'>" + sSelection + "</td>");
        asBackHeader.push("<td class='cell-title'>" + sOddsTitle + "</td>");
        asBackHeader.push("<td class='cell-title' style='width:40px'>&nbsp;</td>");
        asBackHeader.push("<td class='cell-title'>" + sAmountTitle + "&nbsp;" + sCurrSign + "</td>");
        asBackHeader.push("<td class='cell-title' >" + sYourProfitHeader + "</td></tr>");
        sBackHeader = asBackHeader.join('');

        if (myBetIds[Id] == null) {
            myBetIds[Id] = new Array(sOddsElementId, sAmountElementId, "", null, null, null);
        }
    } else if (bidType == top.main.bidTypeLAY) {
        sPx = "Lay";
        sOddsElementId = "new" + sPx + "Odds(" + Id + ")";
        sAmountElementId = "new" + sPx + "Amount(" + Id + ")";
        sCalcProfitCall = "parent.calcProfit('" + sOddsElementId + "','" + sAmountElementId + "', 'yourProfit(" + Id + ")', 'plSwitch'); parent.calcTotalLiability(); parent.calcProfitLossLocal();";
        var sCalcLayProfitsCall = "parent.calcLayProfits('new" + sPx + "Odds','new" + sPx + "Amount', 'yourProfit', " + bidType + ", 'plSwitch');";
        var a = [];
        a.push("<table style='width:100px;' cellpadding='0' cellspacing='0' border='0'>");
        a.push("<tr>");
        a.push("<td>");
        a.push("<input type='radio' style='height:15px' class='radio' name='plSwitch' value='2' onclick=\"" + sCalcLayProfitsCall + "\">");
        a.push("</td>");
        a.push("<td align='left'>");
        a.push("<a href='#' onclick=\"parent.setPLSwitchChecked(0);" + sCalcLayProfitsCall + "\">" + sPaymentText + "</a>");
        a.push("</td>");
        a.push("</tr>");
        a.push("<tr>");
        a.push("<td>");
        a.push("<input type='radio' style='height:15px'class='radio' checked name='plSwitch' value='1' onclick=\"" + sCalcLayProfitsCall + "\">");
        a.push("</td>");
        a.push("<td>");
        a.push("<a href='#' onclick=\"parent.setPLSwitchChecked(1);" + sCalcLayProfitsCall + "\">" + sLiabilityText + "</a>");
        a.push("</td></tr></table>");
        var sPaymentLiabilityHeader = a.join('');
        var asLayHeader = [];
        asLayHeader.push("<tr class='row-title'>");
        asLayHeader.push("<td class='cell-title' width='1%'>&nbsp;</td>");
        asLayHeader.push("<td class='cell-title' width='50%'>" + sSelection + "</td>");
        asLayHeader.push("<td class='cell-title'>" + sOddsTitle + "</td>");
        asLayHeader.push("<td class='cell-title'>&nbsp;</td>");
        asLayHeader.push("<td class='cell-title'>" + sAmountTitle + "&nbsp;" + sCurrSign + "</td>");
        asLayHeader.push("<td class='cell-title'>" + sPaymentLiabilityHeader + "</td></tr>");
        sLayHeader = asLayHeader.join('');
        if (myBetIds[Id] == null)
        {
            myBetIds[Id] = new Array(sOddsElementId, sAmountElementId, "", null, null, null);
        }
    }

    var asBetItem = [];
    asBetItem.push("\n<!-- @@@@@@@@@@@@@@ START OF  bet item [" + Id + "] @@@@@@@@@@@@@ -->");
    asBetItem.push("<tr>");
    asBetItem.push("<td class='" + sBetCellClass + "'><img title='" + sCancelBtnTitle + "' src=" + sCancelBetImageSrc + " onclick='parent.cancelPreStake(\"" + Id + "\")'></td>");
    asBetItem.push("<td class='" + sBetCellClass + "' nowrap>" + resultValue + "</td>");
    asBetItem.push("<input type=\"hidden\" name=\"newResultId(" + Id + ")\"    value='" + resultId + "'>");
    asBetItem.push("<input type=\"hidden\" name=\"newBidType(" + Id + ")\"     value='" + bidType + "'>");
    asBetItem.push("<input type=\"hidden\" name=\"cellId(" + Id + ")\"     value='" + Id + "'>");
    asBetItem.push("<td class='" + sBetCellClass + "'><input  maxlength=\"5\" id='new" + sPx + "Odds(" + Id + ")' type=\"text\" name='new" + sPx + "Odds(" + Id + ")' value='" + odds + "' style='width:40px;text-align:right' onKeyUp=\"" + sCalcProfitCall + "\">");
    asBetItem.push("</td><td  class='" + sBetCellClass + "' width=\"16px\" height=\"16px\" style=\"text-align: left;\">");
    asBetItem.push("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" height=\"100%\" border=\"0\">");
    asBetItem.push("<tr><td><img src='" + sImageSpinUp + "' onclick=\"parent.oddsUp('new" + sPx + "Odds(" + Id + ")' , 'betPage');" + sCalcProfitCall + "\" ></td></tr>");
    asBetItem.push("<tr><td><img src='" + sImageSpinDown + "' onclick=\"parent.oddsDown('new" + sPx + "Odds(" + Id + ")' ,'betPage');" + sCalcProfitCall + "\" ></td></tr>");
    asBetItem.push("</table></td>");
    asBetItem.push("<td class='" + sBetCellClass + "'><input maxlength=\"9\" type=\"text\" id='new" + sPx + "Amount(" + Id + ")' name='new" + sPx + "Amount(" + Id + ")' style='width:60px;text-align:right' onKeyUp=\"" + sCalcProfitCall + "\"></td>");
    asBetItem.push("<td style='text-align:left' class='" + sBetCellClass + "' id='yourProfit(" + Id + ")' >&nbsp;</td></tr>");
    asBetItem.push("<TR><TD height=\"1px\"></TD></TR>");
    asBetItem.push("<!-- @@@@@@@@@@@@@@ END  OF  bet item [" + Id + "] @@@@@@@@@@@@@ -->");
    var sBetItem = asBetItem.join('');
    if (bidType == top.main.bidTypeBACK) {
        myBackItems[myBackItems.length] = new Array(Id, sBetItem);
    }
    if (bidType == top.main.bidTypeLAY) {
        myLayItems[myLayItems.length] = new Array(Id, sBetItem);
    }
}
function checkAmountAvailable() {
    var _amountAvailable = 0.0;
    var _topBar = top.frames["topBar"];
    if (_topBar)
        _amountAvailable = _topBar.getFreeAmount();
    var result = true;
    var _totalLiability = getTotalLiability();
    if (_amountAvailable == 0.0) {
        alert(sMoneyProblem);
        result = false;
    } else if (_totalLiability > _amountAvailable) {
        result = confirm(sAvailableAmountConfirmation);
    }
    return result;
}
function getTotalLiability() {
    var TotalLiability = 0.0;
    if (myBetIds != null) {
        for (var betId in myBetIds) {
            var rowLiability = 0.0;
            if (myBetIds[betId] != null) {
                var betItem = myBetIds[betId];
                var betSide = betId.substring(0, 1);
                var nOdds = betItem[IX_ODDS];
                var nAmount = betItem[IX_AMOUNT];
                if (betSide == "b")rowLiability += nAmount; else if (betSide == "l")rowLiability += (nOdds - 1) * nAmount;
                TotalLiability += rowLiability;
            }
        }
    }
    return TotalLiability;
}
function removeItem(myBetItems, Id) {
    for (var i = 0; i < myBetItems.length; i++) {
        if (myBetItems[i] != null && myBetItems[i][0] == Id) {
            myBetItems[i] = null;
            delete myBetItems[i];
            break;
        }
    }
}
function removeBetItem(Id) {
    if (myBetIds[Id] != null) {
        myBetIds[Id] = null;
        delete myBetIds[Id];
    }
    removeItem(myBackItems, Id);
    removeItem(myLayItems, Id);
}
function addItems(myBetItems) {
    var a = [];
    for (var i = 0; i < myBetItems.length; i++) {
        if (myBetItems[i] != null && myBetItems[i][1]) {
            a.push(myBetItems[i][1]);
        }
    }
    return a.join('');
}
function makeBetsHTML(iEventId, sEventTitle) {
    var a = [];
    a.push(makeBetFormContainer_start(sBetFormAction, iEventId));
    if (itemsExist(myBackItems)) {
        a.push(sBackHeader);
    }
    a.push(addItems(myBackItems));
    if (itemsExist(myLayItems)) {
        a.push(sLayHeader);
    }
    a.push(addItems(myLayItems));
    a.push(makeBetFormContainer_end(sProcessBtnCaption));
    return a.join('');
}
function itemsExist(myBetItems) {
    _itemsExist = false;
    for (var i = 0; i < myBetItems.length; i++) {
        if (myBetItems[i] != null) {
            _itemsExist = true;
            break;
        }
    }
    return _itemsExist;
}
function calcLayProfits(oddsElementId, amountElementId, resultElementId, bidType, plSwitchElementId) {
    for (var i = 0; i < myLayItems.length; i++) {
        if (myLayItems[i] != null && myLayItems[i][0] != null) {
            Id = myLayItems[i][0];
            calcProfit(oddsElementId + "(" + Id + ")", amountElementId + "(" + Id + ")", resultElementId + "(" + Id + ")", plSwitchElementId);
        }
    }
}
function calcTotalLiability() {
    var totalLiability = 0.0;
    var totalBackPercent = 0.0;
    var totalLayPercent = 0.0;
    document.getElementById("TotalLiabilityInfo").innerHTML = "";
    var i;
    for (i = 0; i < myBackItems.length; i++) {
        if (myBackItems[i] != null && myBackItems[i][0] != null) {
            Id = myBackItems[i][0];
            if (myBetIds[Id][IX_ODDS] > 0 && myBetIds[Id][IX_AMOUNT] > 0) {
                totalBackPercent += (1 / myBetIds[Id][IX_ODDS]);
                totalLiability += myBetIds[Id][IX_AMOUNT];
            }
        }
    }
    for (i = 0; i < myLayItems.length; i++) {
        if (myLayItems[i] != null && myLayItems[i][0] != null) {
            Id = myLayItems[i][0];
            if (myBetIds[Id][IX_ODDS] > 0 && myBetIds[Id][IX_AMOUNT] > 0) {
                totalLayPercent += (1 / myBetIds[Id][IX_ODDS]);
                totalLiability += (myBetIds[Id][IX_ODDS] - 1) * myBetIds[Id][IX_AMOUNT];
            }
        }
    }
    if (document.getElementById("TotalLiabilityInfo")) {
        document.getElementById("TotalLiabilityInfo").innerHTML = (totalLiability > 0) ? sTotalLiabilityInfo + " <b>" + sCurrSign + formatCurrency("", totalLiability) + "</b>" : "";
    }
    var isShowReservation = false;
    if (document.getElementById("showReservation")) {
        isShowReservation = document.getElementById("showReservation").checked;
    }
    if (isShowReservation) {
        if (document.getElementById("TotalBackReservationInfo")) {
            document.getElementById("TotalBackReservationInfo").innerHTML = (totalBackPercent > 0) ? sBackReservationInfo + " <b>" + formatCurrency("", (totalBackPercent * 100)) + "%</b>" : "";
        }
        if (document.getElementById("TotalLayReservationInfo")) {
            document.getElementById("TotalLayReservationInfo").innerHTML = (totalLayPercent > 0) ? sLayReservationInfo + " <b>" + formatCurrency("", (totalLayPercent * 100)) + "%</b>" : "";
        }
    }
}

function calcProfit(oddsElementId, amountElementId, resultElementId, plSwitchElementId) {
    var BetPage = top.betBorder.betPage.document;
    if (!BetPage)
    {
        return;
    }
    currentOdds = Number(BetPage.getElementById(oddsElementId).value);
    currentAmount = Number(stripCommas(BetPage.getElementById(amountElementId).value));
    BetPage.getElementById(resultElementId).innerHTML = "";
    left_s = oddsElementId.indexOf("(");
    right_s = oddsElementId.indexOf(")");
    var Id = "";
    if (left_s != -1 && right_s != -1) {
        Id = oddsElementId.substring(left_s + 1, right_s);
    }
    if (myBetIds[Id] != null) {
        myBetIds[Id][IX_ODDS_ID] = oddsElementId;
        myBetIds[Id][IX_AMOUNT_ID] = amountElementId;
        myBetIds[Id][IX_RESULT_ID] = resultElementId;
        myBetIds[Id][IX_ODDS] = (currentOdds == "" || isNaN(currentOdds)) ? null : currentOdds;
        myBetIds[Id][IX_AMOUNT] = (currentAmount == "" || isNaN(currentAmount)) ? null : currentAmount;
        myBetIds[Id][IX_RESULT] = null;
    }
    if (currentOdds == "" || isNaN(currentOdds)) {
        return;
    }
    if (currentAmount == "" || isNaN(currentAmount)) {
        return;
    }
    var val = null;
    if (plSwitchElementId == null) {
        val = (currentOdds - 1) * currentAmount;
    } else {
        if (BetPage.forms.betForm.plSwitch[0].checked) {
            val = currentOdds * currentAmount;
        } else if (BetPage.forms.betForm.plSwitch[1].checked) {
            val = (currentOdds - 1) * currentAmount;
        }
    }
    if (myBetIds[Id] != null) {
        myBetIds[Id][IX_ODDS_ID] = oddsElementId;
        myBetIds[Id][IX_AMOUNT_ID] = amountElementId;
        myBetIds[Id][IX_RESULT_ID] = resultElementId;
        myBetIds[Id][IX_ODDS] = currentOdds;
        myBetIds[Id][IX_AMOUNT] = currentAmount;
        myBetIds[Id][IX_RESULT] = val;
    }
    BetPage.getElementById(resultElementId).innerHTML = formatCurrency(sCurrSign, val);
}

function setPLSwitchChecked(item) {
    var BetPage = window.frames["betPage"].document;
    if (!BetPage)return;
    BetPage.forms.betForm.plSwitch[item].checked = true;
    BetPage.forms.betForm.plSwitch[1 - item].checked = false;
}

function restoreBetView() {
    if (top.main.enableEvent)top.main.enableEvent();
    var ButtonContainer = document.getElementById("sc11");
    var BetContainer = document.getElementById("sc12");
    var TimeRestElement = document.getElementById("sc13");
    document.getElementById("timeRest").innerHTML = '';
    TimeRestElement.style.display = "none";
    ButtonContainer.style.display = "block";
    BetContainer.style.display = "block";
    try {document.getElementById("confirmBets").focus();} catch (x) {}
}

function cancelInplayWaitForSubmit() {
    if (waitForInplayTimeout) {
        clearTimeout(waitForInplayTimeout);
        waitForInplayTimeout = null;
        restoreBetView();
    }
}
function getTimeRestContent(sContent) {
    return"<span style='font-family: tahoma; font-size:12px'><b>" + sStartInplayPromptCancel + "<br><br><font color='blue'>" + sContent + "</font><br><br>" + sEndInplayPromptCancel + "</b></span>";
}
function ShowTimeRest() {
    var ButtonContainer = document.getElementById("sc11");
    var BetContainer = document.getElementById("sc12");
    var TimeRestElement = document.getElementById("sc13");
    var targetHeight = BetContainer.offsetHeight;
    var targetWidth = BetContainer.offsetWidth;
    TimeRestElement.className = "betNotAvailable";
    ButtonContainer.style.display = "none";
    BetContainer.style.display = "none";
    var _maxWaitForInplaySubmit = eval("maxWaitForInplaySubmit");
    document.getElementById("timeRest").innerHTML = getTimeRestContent(_maxWaitForInplaySubmit);
    TimeRestElement.style.display = "block";
    TimeRestElement.style.height = targetHeight;
    TimeRestElement.style.width = targetWidth;
    TimeRestElement.style.top = document.getElementById("tablist").offsetHeight;
    if (top.main.disableEvent)top.main.disableEvent("");
    var cancelInplayWaitButton = document.getElementById("cancelInplayWaitButton");
    if (cancelInplayWaitButton) {
        try {cancelInplayWaitButton.focus();} catch (x) {};
    }
}
function waitASecond() {
    currentWait--;
    document.getElementById("timeRest").innerHTML = getTimeRestContent(currentWait);
    if (currentWait > 0) {
        waitForInplayTimeout = setTimeout("waitASecond();", 1000);
    } else {
        clearTimeout(waitForInplayTimeout);
        waitForInplayTimeout = null;
        go_tab("sc1");
        restoreBetView();
        var form = frames["betPage"].document.forms["betForm"];
        if (form) {
            form.submit();
        }
    }
}
function ApplyBet(thisSubmitButton) {
    if (!isLoggedIn)
    {
        alert(sCantMakeBetAlert);
        return;
    }
    var form = frames.betPage.document.forms.betForm;
    if (!form)return;
    if (!form.eventId.value)
    {
        return;
    }
    var isVerifyBets = false;
    if (document.getElementById("confirmBets")) {
        isVerifyBets = document.getElementById("confirmBets").checked;
    }
    if (validateBetForm(form) && checkAmountAvailable()) {
        var betIframe = top.betBorder.betPage;
        if (betIframe) {
            if (betIframe.document.getElementById(sTokenName)) {
                betIframe.document.getElementById(sTokenName).value = sToken;
            }
        }
        if (isVerifyBets) {
            var confirmed = confirm(sVerifyBetsConfirmText);
            if (!confirmed)return;
        }
        if (processOnClickForSubmit(thisSubmitButton, true)) {
            if (top.marketPage.getEventState() == top.marketPage.EVENT_STATE_INPLAY) {
                currentWait = maxWaitForInplaySubmit;
                ShowTimeRest();
                waitForInplayTimeout = setTimeout("waitASecond();", 1000);
            }
            else
                form.submit();
        }
    }
}

function stakesApplied() {
 //   alert("stakesApplied()");
    unHighlightCells();
    clearBetItems(myBackItems);
    clearBetItems(myLayItems);
    top.main.clearProfitLoss();
    go_tab("sc2");
}

function unHighlightCells()
{
    for (var i = 0; i < myBackItems.length; i++) {
        if (myBackItems[i] != null && myBackItems[i][0] != null) {
            Id = myBackItems[i][0];
            unHighlight_OneCell(Id)
        }
    }
    for (var i = 0; i < myLayItems.length; i++) {
        if (myLayItems[i] != null && myLayItems[i][0] != null) {
            Id = myLayItems[i][0];
            unHighlight_OneCell(Id)
        }
    }
}
function unHighlight_OneCell(Id)
{
    cell_Id = 'cell_' + Id;
    doc = top.main.document;
    if (doc.getElementById(Id) != null && doc.getElementById(Id).className.length > 0) {
        switch (doc.getElementById(Id).className)
                {case"cell-right-selected":doc.getElementById(Id).className = "cell-right-high";doc.getElementById('x' + cell_Id).className = "cell-right-up";break;case"cell-left-selected":doc.getElementById(Id).className = "cell-left-high";doc.getElementById('x' + cell_Id).className = "cell-left-up";break;}
    }
}

function refreshEvent()
{
    frame = top.main;
    if (frame)
    {
        if (frame.backRefreshEvent)
        {
            frame.backRefreshEvent();
            frame.getTopData();
        }
    }
}

function getEventAuxInfo()
{
    if (frames['infoPage']) {
        var _marketInfoURI = eval("sMarketInfoURI");
        var currentEventId = parent.frames['marketPage'].iCurrentMarketID;
        if (currentEventId)
            frames['infoPage'].location.replace(_marketInfoURI + currentEventId);
    }
}
function getMyBets() {
    var _myBetsURI = eval("sMyBetsURI");
    var currentEventId = parent.frames['marketPage'].iCurrentMarketID;
    var myBetsManager = top.betBorder.mybetPage.myBetsManager;
    var myBetPage = top.betBorder.mybetPage;
    if (!myBetsManager || !myBetsManager.currentEventId || (currentEventId != myBetsManager.currentEventId)) {
        frames['mybetPage'].location.replace(_myBetsURI + currentEventId);
    }
}
function setChecks() {
    var _betBorder = eval("top.betBorder");
    if (_betBorder) {
        _betBorder.document.getElementById("matchedBets").checked = (sMatchedBets == ON);
        _betBorder.document.getElementById("consolidated").checked = false;
        _betBorder.document.getElementById("averageOdds").checked = false;
        _betBorder.document.getElementById("consolidated").checked = (sBetView == ON);
        _betBorder.document.getElementById("averageOdds").checked = (sBetView == AVG);
    }
}
function changeBetSetting(thisCheckBox) {
    if (thisCheckBox.name == "matchedBets") {
        sMatchedBets = thisCheckBox.checked ? ON : OFF;
    } else if (thisCheckBox.name == "consolidated") {
        if (thisCheckBox.checked) {
            sBetView = ON;
            top.betBorder.document.getElementById("averageOdds").checked = false;
        } else if (!thisCheckBox.checked) {
            sBetView = thisCheckBox.checked ? ON : OFF;
        }
    } else if (thisCheckBox.name == "averageOdds") {
        if (thisCheckBox.checked) {
            sBetView = AVG;
            top.betBorder.document.getElementById("consolidated").checked = false;
        } else if (!thisCheckBox.checked) {
            sBetView = thisCheckBox.checked ? ON : OFF;
        }
    }
    var myBetPage = top.betBorder.mybetPage;
    if (myBetPage) {
        myBetPage.RefreshMyBets();
    }
}
function gridScaleChanged() {
    do_CancelAll();
}

function doShowBetPage() {
    if (!itemsExist(myBackItems) && !itemsExist(myLayItems)) {
        document.getElementById("sc11").style.display = "block";
        document.getElementById("sc12").style.display = "none";
        document.getElementById("sc13").style.display = "none";
        document.getElementById("ivnviteToBet").innerHTML = sIvnviteToBetText;
        setClearButtonDisabled ( true );
    } else {
        setClearButtonDisabled ( false );
    }
  setBetButtonStateDisabled ( EVENT_STATUS_OPENED == null || top.main.currentEventStatus != EVENT_STATUS_OPENED);
}

function setBetButtonStateDisabled(_disabled) {
    var btnBackAll = document.getElementById("btnBackAll");
    if (btnBackAll) btnBackAll.disabled = _disabled;

    var btnLayAll = document.getElementById("btnLayAll");
    if (btnLayAll) btnLayAll.disabled = _disabled;
}

function setClearButtonDisabled (_disabled) {
  var btnClearAll = document.getElementById("btnClearAll");
  if (btnClearAll) btnClearAll.disabled = _disabled;
}

function showHelpInfoPage() {
    var helpInfoPage = eval("top.betBorder.inviteBetPage");
    var _betType = eval("currentBetType");
    var _helpInfoURI = eval("sHelpInfoURI");
    var slocation = _helpInfoURI + _betType;
    helpInfoPage.location.replace(slocation);
}

function clickTab1(thisObject) {

    if (document.getElementById("sc1").style.display == "none") {
        doShowBetPage();
        expandcontent('sc1', thisObject);
    }
}
function clickTab2(thisObject) {
    if (!isLoggedIn) {
        alert(sNotLoggedInAlert);
        return;
    }
    if (document.getElementById("sc2").style.display == "none") {
        getMyBets();
        expandcontent('sc2', thisObject);
    }
}
function clickTab3(thisObject) {
    if (document.getElementById("sc3").style.display == "none") {
        getEventAuxInfo();
        expandcontent('sc3', thisObject);
    }
}
function clickTab4(thisObject) {
    if (document.getElementById("sc4").style.display == "none") {
        showHelpInfoPage();
        expandcontent('sc4', thisObject);
    }
}
function refreshMyBetsClick() {
    if (frames.mybetPage) {
        frames.mybetPage.refreshOn = true;
        if (frames.mybetPage.RefreshMyBets) {
            frames.mybetPage.RefreshMyBets();
        }
    }
}
function cancelVerifiedBets() {
}
function submitVerifiedBets() {
}
function changeReservation(thisCheck, isOnlyReservation) {
    if (thisCheck.checked) {
        calcTotalLiability(isOnlyReservation);
    }
    else {
        document.getElementById("TotalBackReservationInfo").innerHTML = "";
        document.getElementById("TotalLayReservationInfo").innerHTML = "";
    }
}
function CancelAllStakesClick(thisSubmitButton) {
    if (processOnClickForSubmit(thisSubmitButton, true)) {
        if (frames.mybetPage.deleteStake) {
            frames.mybetPage.deleteStake(frames.mybetPage.iBetCancelAll, -1, frames.mybetPage.currentEventId);
        }
    }
    return false;
}
function clearStatusMessage() {
    document.getElementById("statusMessage").innerHTML = "";
}
function enableApplyButton() {
    var applyButton = document.getElementById("applyButton");
    if (applyButton) {
        var disabled = applyButton.disabled;
        applyButton.disabled = !disabled;
    }
}
function ajustStyles() {
    if (IE) {
        document.getElementById("allcontent").style.height = "100%";
        document.getElementById("tabcontent").style.height = "100%";
        document.getElementById("sc1").style.height = "100%";
        document.getElementById("sc2").style.height = "100%";
        document.getElementById("sc3").style.height = "100%";
        document.getElementById("sc4").style.height = "100%";
    }
}

function ajustDivSize() {
    if (bReDraw && (isMozilla() || isOpera())) {
        bReDraw = false;
        if (document.body) {
            if (document.body.offsetHeight > 0) {
                var newHeight = document.body.offsetHeight
                        - 10
                        - document.getElementById("tablist").offsetHeight;
                if (newHeight > 0) {
                    setDivHeight("sc1", newHeight);
                    setDivHeight("sc2", newHeight);
                    setDivHeight("sc3", newHeight);
                    setDivHeight("sc4", newHeight);
                }
            }
        }
        setTimeout("bReDraw = true", 250);
    }
}
function betBorderOnLoad() {
    do_onload();
    initSizes();
}
function initSizes() {
    ajustStyles();
    if (window.addEventListener) {
        ajustDivSize();
        window.addEventListener("resize", ajustDivSize, false);
    }
}