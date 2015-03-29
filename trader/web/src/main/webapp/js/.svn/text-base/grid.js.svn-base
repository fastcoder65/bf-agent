var C_RES_ID = 0;
var C_RES_VALUE = 1;
var C_BID_TYPE = 2;
var C_ODDS = 3;
var C_AMOUNT = 4;
var GS_RESULT_ID = 0;
var GS_VALUE = 1;
var GS_COMMEMT = 2;
var ResultInfoWin = null;
var currentMarketId = null;
var currentEventId = null;
var currentBetType = 1;
var currentEventState = null;
var currentEventStatus = null;
var iCurrentTimeZoneId = null;
var iGridRefreshInterval = 60;
var refreshGridTimeout = null;
var betFrame = parent.frames["betBorder"];
var bEventLoaded = false;
var PL_RESULT_ID = 0;
var PL_VALUE = 1;
var gridStructRows = null;
var MyGridWin = null;
var oldGSIndex = null;
var oldGRIndex = null;
var oldShowPL = null;
var oldShowWhatIf = null;
var currentBetArray = new Array();
var plInfo = new Array();
var grid_wrapper = "grid_wrapper";
var grid_content = "grid_content";
var MarketStatus = "MarketStatus";
function profitLossOptionsChanged(thisControl) {
    if (betFrame.preBetsExists() && !confirm(sConfirmChangeScale)) {
        thisControl.form.showProfitLoss.checked = oldShowPL;
        return;
    }
    if (thisControl.checked && thisControl.name == "showWhatIf") {
        document.getElementById("showProfitLoss").checked = thisControl.checked;
    }
    submitGSForm(thisControl.form);
    oldShowPL = thisControl.form.showProfitLoss.checked;
    oldShowWhatIf = thisControl.form.showWhatIf.checked;
}
function submitGSForm(thisForm) {
    if (thisForm) {
        if (betFrame && betFrame.gridScaleChanged)
        {
            betFrame.gridScaleChanged();
        }
        thisForm.eventId.value = top.main.currentEventId;
        thisForm.marketId.value = top.main.currentMarketId;
        if (thisForm.name != "profitLossOptions") {
            thisForm.showProfitLoss.value = document.getElementById("showProfitLoss").checked;
            thisForm.showWhatIf.value = document.getElementById("showWhatIf").checked;
        }
        thisForm.submit();
    }
}
function gridScaleChanged(thisForm) {
    if (betFrame.preBetsExists() && !confirm(sConfirmChangeScale)) {
        thisForm.gridScale.selectedIndex = oldGSIndex;
        return;
    }
    submitGSForm(thisForm);
    oldGSIndex = thisForm.gridScale.selectedIndex;
}
function gridRefreshChanged(thisForm) {
    if (betFrame.preBetsExists() && !confirm(sConfirmChangeScale)) {
        thisForm.gridRefreshTimeout.selectedIndex = oldGRIndex;
        return;
    }
    submitGSForm(thisForm);
    oldGRIndex = thisForm.gridRefreshTimeout.selectedIndex;
}
function updateMarketData(dataRows, curr_sign)
{
    for (var iR = 0; iR < dataRows.length; iR++) {
        var currentRow = dataRows[iR];
        for (var iC = 0; iC < currentRow.length; iC++) {
            var currentCell = currentRow[iC];
            var bType = (currentCell[C_BID_TYPE] == 1) ? 'b' : 'l';
            var itemId = 'cell_' + bType + currentCell[C_RES_ID] + '_' + (iC + 1);
            if (document.getElementById(itemId)) {
                if (currentCell[C_AMOUNT] != null && currentCell[C_AMOUNT] != 0) {
                    try {
                        document.getElementById(itemId).innerHTML = '<B>' + currentCell[C_ODDS] + '</B><br>' + curr_sign + currentCell[C_AMOUNT];
                    } finally {
                        document.close();
                      //  alert("document.close();");
                    }

                }
                else {
                    try {
                        document.getElementById(itemId).innerHTML = '&nbsp;<br>&nbsp;';
                    } finally {
                        document.close();
                      // alert("document.close();");
                    }

                }
                currentBetArray[itemId] = new Array(currentCell[C_RES_ID], currentCell[C_RES_VALUE], currentCell[C_BID_TYPE], currentCell[C_ODDS], currentCell[C_AMOUNT]);
            }
        }
    }
    bEventLoaded = true;
}
function clearProfitLossBase() {
    if (gridStructRows && (gridStructRows.length > 0)) {
        for (var iR = 0; iR < gridStructRows.length; iR++) {
            var plElementId = "profitLossBase_" + gridStructRows[iR][C_RES_ID];
            if (document.getElementById(plElementId)) {
                try {
                    document.getElementById(plElementId).innerHTML = "&nbsp";
                } finally {
                    document.close();
                  //  alert("document.close();");
                }

            }
        }
    }
}
function initProfitLossInfo() {
    if (gridStructRows && (gridStructRows.length > 0)) {
        for (var iR = 0; iR < gridStructRows.length; iR++) {
            plInfo[plInfo.length] = new Array(gridStructRows[iR][C_RES_ID], null);
        }
    }
}
function clearProfitLoss() {
    var iR;
    if (plInfo != null && (plInfo.length > 0)) {
        for (iR = 0; iR < plInfo.length; iR++) {
            plInfo[iR][PL_VALUE] = null;
        }
    }
    if (gridStructRows && (gridStructRows.length > 0)) {
        for (iR = 0; iR < gridStructRows.length; iR++) {
            var plElementId = "profitLoss_" + gridStructRows[iR][C_RES_ID];
            if (document.getElementById(plElementId)) {
                try {
                    document.getElementById(plElementId).innerHTML = "&nbsp;";
                } finally {
                    document.close();
                   // alert("document.close();");
                }

            }
        }
    }
}
function updateProfitLoss(plInfo, curr_sign, sfx) {
    var fontBeginZero = "";
    var fontEndZero = "";
    var fontBeginRed = "<font color='red'>";
    var fontBeginGreen = "<font color='green'>";
    var fontEnd = "</font>";
    var wCommissionPercent;
    if (top.mainBar.document.getElementById("wCommissionPercent")) {
        wCommissionPercent = top.mainBar.document.getElementById("wCommissionPercent").checked;
    }
    var useCP = (wCommissionPercent != null && wCommissionPercent) ? 1 : 0;
    if (plInfo != null && (plInfo.length > 0)) {
        for (var iR = 0; iR < plInfo.length; iR++) {
            var plElementId = "profitLoss" + sfx + "_" + plInfo[iR][PL_RESULT_ID];
            var resultFigure = plInfo[iR][PL_VALUE];
            if (document.getElementById(plElementId)) {
                if (signDependent && resultFigure != null) {
                    var fontBegin = (resultFigure == 0) ? fontBeginZero : ((resultFigure < 0) ? fontBeginRed : fontBeginGreen);
                    fontEnd = (resultFigure == 0) ? fontEndZero : fontEnd;
                    var pfx = "";
                    var plBaseInnerHTML = document.getElementById("profitLossBase_" + plInfo[iR][PL_RESULT_ID]).innerHTML;
                    if (sfx.length == 0 && (plBaseInnerHTML != null && plBaseInnerHTML.trim().length > 0)) {
                        pfx = "&nbsp;&gt;&gt;&nbsp;";
                    }
                    resultFigure = (resultFigure > 0) ? resultFigure * (1 - commissionPercent * useCP / 100) : resultFigure;
                    try {
                        document.getElementById(plElementId).innerHTML = fontBegin + pfx + formatCurrency(curr_sign, resultFigure) + fontEnd;
                    } finally {
                        document.close();
                     //   alert("document.close();");
                    }

                }
            }
        }
    }
}
function updateProfitLossBase(_plInfo, curr_sign) {
    updateProfitLoss(_plInfo, curr_sign, "Base");
}
function refreshProfitLossBase() {
}
function updateProfitLossLocal() {
    if (top.mainBar.document.getElementById("showWhatIf") && top.mainBar.document.getElementById("showWhatIf").checked)
        updateProfitLoss(plInfo, curr_sign, "");
}
function addProfitLossLocal(resultId, betSide, amount, plValue) {
    if (plInfo != null && (plInfo.length > 0)) {
        for (var iR = 0; iR < plInfo.length; iR++) {
            if (betSide == "b") {
                if (plInfo[iR][PL_RESULT_ID] == resultId) {
                    if (plValue != null)
                        plInfo[iR][PL_VALUE] += plValue;
                } else {
                    if (amount != null)
                        plInfo[iR][PL_VALUE] -= amount;
                }
            }
            if (betSide == "l") {
                if (plInfo[iR][PL_RESULT_ID] == resultId) {
                    if (plValue != null)
                        plInfo[iR][PL_VALUE] -= plValue;
                } else {
                    if (amount != null)
                        plInfo[iR][PL_VALUE] += amount;
                }
            }
        }
    }
}
var showOnce = false;
function setShowOnce(_showOnce) {
    showOnce = _showOnce;
}
function doStake(Id, isGroup) {
    if (!bEventLoaded)return;
    var cell_Id = 'cell_' + Id;
    if (top.mainBar.document.getElementById("currentEventTitle") == null)return;
    var _event_Title = top.mainBar.document.getElementById("currentEventTitle").innerHTML;
    function intDoStake(isGroup) {
        betFrame.go_tab("sc1");
        if (currentBetArray[cell_Id])
        {
            var bidType = currentBetArray[cell_Id][C_BID_TYPE];
            var odds = currentBetArray[cell_Id][C_ODDS];
            var amount = currentBetArray[cell_Id][C_AMOUNT];
            var resultId = currentBetArray[cell_Id][C_RES_ID];
            var resultValue = currentBetArray[cell_Id][C_RES_VALUE];
            betFrame.placeBet(Id, bidType, odds, amount, resultId, resultValue);
            if (!isGroup) {
                betFrame.makeBetPageHTML(currentEventId, _event_Title);
                betFrame.setFocus(Id);
            }
        }
    }
    switch (document.getElementById(Id).className) {case"cell-right-selected":document.getElementById(Id).className = "cell-right-high";document.getElementById('x' + cell_Id).className = "cell-right-up";betFrame.removeBetItem(Id);betFrame.makeBetPageHTML(currentEventId, _event_Title);doShowBetPage();break;case"cell-right-high":document.getElementById(Id).className = "cell-right-selected";document.getElementById('x' + cell_Id).className = "cell-down-right";intDoStake(isGroup);break;case"cell-left-selected":document.getElementById(Id).className = "cell-left-high";document.getElementById('x' + cell_Id).className = "cell-left-up";betFrame.removeBetItem(Id);betFrame.makeBetPageHTML(currentEventId, _event_Title);doShowBetPage();break;case"cell-left-high":document.getElementById(Id).className = "cell-left-selected";document.getElementById('x' + cell_Id).className = "cell-down-left";intDoStake(isGroup);break;}
}
function doShowBetPage()
{
    if (betFrame && betFrame.doShowBetPage)
        betFrame.doShowBetPage();
}

function getEventData(iEventId) {
    if (refreshGridTimeout != null)clearTimeout(refreshGridTimeout);
    if (top.frames['main'].frames['predictionManager']) {
        top.frames['main'].frames['predictionManager'].location.href = sEventDataAction + iEventId + "&showProfitLoss=" + showProfitLoss + "&showWhatIf=" + showWhatIf;
    }
}

function getTopData()
{
    if (top.frames.topBar)
    {
        var topBar = top.frames.topBar;
        if (topBar.loadSumData) {
            topBar.loadSumData();
        }
    }
}
function backRefreshEvent()
{
    mainFrame = parent.frames['main'];
    if (mainFrame && mainFrame.currentEventId) {
        getEventData(mainFrame.currentEventId);
    }
}

function setGridTimeout() {
    if (iGridRefreshInterval > 0)
        refreshGridTimeout = setTimeout("backRefreshEvent()", iGridRefreshInterval * 1000);
}

function clearGridTimeout() {
    if (refreshGridTimeout != null) {
        clearTimeout(refreshGridTimeout);
    }
}
function enableEvent() {
    var _MarketStatus = document.getElementById(MarketStatus);
    _MarketStatus.className = "";
    _MarketStatus.style.height = 0;
    _MarketStatus.style.width = 0;
    _MarketStatus.style.display = "none";
    _MarketStatus.innerHTML = "";
    var betBorder = parent.frames['betBorder'];
    if (betBorder && betBorder.setBetButtonStateDisabled) {
        betBorder.setBetButtonStateDisabled(false);
    }
}
function disableEvent(sMarketStatusMessage) {
    var gridHeight = document.getElementById(grid_content).offsetHeight;
    var gridWidth = document.getElementById(grid_content).offsetWidth;
    var scrollBarWidth = 14;
    var _MarketStatus = document.getElementById(MarketStatus);
    _MarketStatus.className = "marketNotAvailable";
    _MarketStatus.style.display = "block";
    _MarketStatus.style.height = gridHeight;
    _MarketStatus.style.width = gridWidth - scrollBarWidth;
    _MarketStatus.style.top = document.getElementById("PercentContainerTR").offsetHeight;
    try {
        _MarketStatus.innerHTML = "<table border='0' cellpadding='5' cellspacing='5' width='100%' height='100%'><tr height='100%'><td align='center' valign='top' width='100%'><br><br><br><br><br><br><font color='#888888'><h2>" + sMarketStatusMessage + "</h2></font></td></tr></table>";
    } finally {
        document.close();
     //   alert("document.close();");
    }

    var betBorder = parent.frames['betBorder'];
    if (betBorder && betBorder.setBetButtonStateDisabled) {
        betBorder.setBetButtonStateDisabled(true);
    }
}
function prepareArrayData(sGridStructRowsInfo) {
    gridStructRows = new Array();
    if (sGridStructRowsInfo.length > 0) {
        var tempGR = sGridStructRowsInfo.substring(0, sGridStructRowsInfo.length - 1).split("|");
        for (var i = 0; i < tempGR.length; i++) {
            tempGR[i] = tempGR[i].split("~");
            gridStructRows[gridStructRows.length] = new Array(Number(tempGR[i][GS_RESULT_ID]), tempGR[i][GS_VALUE], (tempGR[i][GS_COMMEMT] != null) ? tempGR[i][GS_COMMEMT] : "");
        }
    }
}
function getGridContent() {
    var a = [];
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' >");
    a.push("\n<tr><td  class='grid-border' ><form id='betForm' name='betForm' action='" + sDoBetActionURI + "' target='betPage'>");
    a.push("\n<input type='hidden' name='eventId' value='" + currentEventId + "'>");
    a.push("\n<input type='hidden' name='cellId'>");
    a.push("\n<input type='hidden' name='bidType'>");
    a.push("\n<input type='hidden' name='newOdds'>");
    a.push("\n<input type='hidden' name='newAmount'>");
    a.push("\n<input type='hidden' name='resultId'>");
    a.push("\n<input type='hidden' name='resultValue'>");
    a.push("\n <table cellpadding='0' cellspacing='0' border='0' width='100%' >");
    a.push("\n<tr><td><img src='" + sSpacerURI + "' height='0'  width='130px'></td>");
    a.push("\n<td class='sizer-left' colspan='" + cGridScale + "'><img src='" + sSpacerURI + "' height='1' width='150'></td>");
    a.push("\n<td><img src='" + sSpacerURI + "' height='1' width='5'></td>");
    a.push("\n<td class='sizer-right' colspan='" + cGridScale + "'><img src='" + sSpacerURI + "' height='1' width='150' ></td>");
    a.push("\n</tr>");
    a.push("\n<tr id='PercentContainerTR' >");
    a.push("\n<td class='title-name'><b>" + sCurrencySymbol + "</b></td>");
    a.push("\n<td class='title-left' colspan='" + cGridScale + "'>");
    a.push("\n<table cellpadding='0' cellspacing='4' border='0' width='100%' height='100%'>");
    a.push("\n<tr><td id='backPercentContainer' align='right' width='100%'>&nbsp;</td></tr>");
    a.push("\n</table>");
    a.push("\n</td>");
    a.push("\n<td class='separator'></td>");
    a.push("\n<td class='title-right' colspan='" + cGridScale + "'>");
    a.push("\n<table cellpadding='0' cellspacing='4' border='0' width='100%' height='100%'>");
    a.push("\n<tr><td id='layPercentContainer' align='left' width='100%'>&nbsp;</td></tr>");
    a.push("\n</table>");
    a.push("\n</td>");
    a.push("\n</tr>");
    a.push("\n<tr id='TotalSelectionsTR' >");
    a.push("\n<td nowrap class='title-name'><span id='totalSelectionsCount'></span></td>");
    a.push("\n<td class='title-left' colspan='" + cGridScale + "'>");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' height='100%'>");
    a.push("\n<tr>");
    a.push("\n<td class='title-back' align='left'>&nbsp;</td>");
    a.push("\n<td class='title-backArrow' align='left'><img src='" + sBackArrowURI + "'></td>");
    a.push("\n<td class='title-backArrow' align='right'><b>" + sGridBackCaption + "</b></td>");
    a.push("\n<td class='title-backArrow' align='left'>&nbsp;</td>");
    a.push("\n</tr>");
    a.push("\n</table>");
    a.push("\n</td>");
    a.push("\n<td class='separator'></td>");
    a.push("\n<td class='title-right' colspan='" + cGridScale + "'>");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' height='100%'>");
    a.push("\n<tr><td class='title-layArrow' align='left'>&nbsp;</td>");
    a.push("\n<td class='title-layArrow' align='left'><b>" + sGridLayCaption + "</b></td>");
    a.push("\n<td class='title-layArrow' align='right'><img src='" + sLayArrowURI + "'></td>");
    a.push("\n<td class='title-lay' align='left'>&nbsp;</td></tr></table></td></tr>");
    for (var iRow = 0; iRow < gridStructRows.length; iRow++) {
        var rowId = gridStructRows[iRow][GS_RESULT_ID];
        var rowValue = gridStructRows[iRow][GS_VALUE];
        var rowCommentValue = "";
        rowCommentValue = gridStructRows[iRow][GS_COMMEMT];
        rowCommentValue = rowCommentValue.trim();
        var titleValue = (rowCommentValue != null && rowCommentValue.length > 0) ? rowCommentValue : resultInfoTitle;
        titleValue = titleValue.trim();
        var titleTag = (titleValue != null && titleValue.length > 0) ? "onmouseover=\"Tip('" + titleValue + "', BGCOLOR,'" + TT_BGCOLOR + "', BORDERCOLOR,'" + TT_BORDERCOLOR + "', FADEIN, 400, FADEOUT, 400, ABOVE, true, WIDTH, 0, PADDING, 8, TEXTALIGN, 'justify', OFFSETX, -10);\"" : "";
        var cIconInfoURI = (signDependent == IS_DEPENDENT) ? sIconInfoURI : sIconInfoRedURI;
        a.push("\n<tr id='r" + rowId + "'>");
        a.push("\n<td class='cell-name'>");
        a.push("\n<table cellpadding='2' cellspacing='0' border='0'>");
        a.push("\n<tr>");
        a.push("\n<td><img " + titleTag + " src='" + cIconInfoURI + "' onClick='ResultInfoWin = openResultInfoWin(\"" + sResultInfoUrl + currentEventId + "&resultId=" + rowId + "\", ResultInfoWin, null, \"resultInfoWin\" );'></td>");
        a.push("\n<td width='150px' nowrap id='rv_" + rowId + "'><a " + titleTag + " href='#' onclick='ResultInfoWin = openResultInfoWin(\"" + sResultInfoUrl + currentEventId + "&resultId=" + rowId + "\", ResultInfoWin, null, \"resultInfoWin\");'>" + rowValue + "</a></td>");
        a.push("\n</tr>");
        a.push("\n<tr>");
        a.push("\n<td >&nbsp;</td>");
        a.push("\n<td>");
        a.push("\n<table cellpadding='1' cellspacing='0' border='0'>");
        a.push("\n<tr><td id='profitLossBase_" + rowId + "' >&nbsp;</td><td id='profitLoss_" + rowId + "'>&nbsp;</td></tr>");
        a.push("\n</table>");
        a.push("\n</td>");
        a.push("\n</tr>");
        a.push("\n</table>");
        a.push("\n</td>");
        var gridScaleLimit = cGridScale * 2 + 1;
        var stakeCount = 0;
        for (stakeCount = 1; stakeCount < gridScaleLimit; stakeCount++) {
            var cellId;
            var cellStyle;
            if (stakeCount <= cGridScale) {
                cellId = "cell_b" + rowId + "_" + stakeCount;
                if (stakeCount == cGridScale) {
                    cellStyle = "cell-left-up";
                    a.push("<td id='b" + rowId + "_" + stakeCount + "' class='cell-left-high' onclick='doStake(this.id, false);' align='center'>");
                }
                else {
                    cellStyle = "cell-up";
                    a.push("<td class='cell-left'>");
                }
            }
            else if (stakeCount > cGridScale) {
                cellId = "cell_l" + rowId + "_" + stakeCount;
                if (stakeCount == cGridScale + 1) {
                    cellStyle = "cell-right-up";
                    a.push("<td id='l" + rowId + "_" + stakeCount + "' class='cell-right-high' onclick='doStake(this.id, false );' align='center'>");
                }
                else {
                    cellStyle = "cell-up";
                    a.push("<td class='cell-right'>");
                }
            }
            a.push("<table cellpadding='0' cellspacing='0' border='0' width='100%' height='40px'>");
            a.push("<tr>");
            a.push("<td id='x" + cellId + "' class='x" + cellStyle + "'>");
            a.push("<div align='center'>");
            a.push("<table cellpadding='0' cellspacing='0' border='0' width='50px' height='100%'>");
            a.push("<tr><td id='" + cellId + "' align='center'>&nbsp;<br>&nbsp;</td></tr>");
            a.push("</table>");
            a.push("</div>");
            a.push("</td>");
            a.push("</tr>");
            a.push("</table>");
            a.push("</td>");
            if (stakeCount == cGridScale) {
                a.push("<td class='separator'></td>");
            }
        }
        a.push("\n</tr>");
    }
    a.push("\n</table></td></tr></form></table>");
    return a.join('');
}
function buildGridContent(sGridStructRowsInfo, iTotalSelectionsCount) {
    bEventLoaded = false;
    marketPage = top.frames['marketPage'];
    if (marketPage && marketPage.iCurrentMarketID) {
        currentMarketId = ('sc1' == marketPage.previoustab) ? marketPage.currentAllItem : marketPage.currentMyItem;
        currentEventId = marketPage.iCurrentMarketID;
        currentEventState = marketPage.getEventState(currentEventId);
        if (marketPage.getCurrentMarketTimeZone) {
            iCurrentTimeZoneId = marketPage.getCurrentMarketTimeZone(currentMarketId);
        }
        setCurrentMarketAndEventName(currentMarketId, currentEventId, "grid.js->buildGridContent");
    }
    var gridBorderElement = document.getElementById(grid_content);
    if (gridBorderElement) {
        prepareArrayData(sGridStructRowsInfo);
        var sHTML = getGridContent();
        gridBorderElement.style.display = "none";
        try {
            gridBorderElement.innerHTML = sHTML;
        } finally {
            gridBorderElement.style.display = "block";
            document.close();
        }
    }
    setTimeout("initSizes();", 20);
    if (document.getElementById("totalSelectionsCount")) {
        tsHTML = sTotalSelectionsCaption + "&nbsp;" + iTotalSelectionsCount;
        try {
            document.getElementById("totalSelectionsCount").innerHTML = tsHTML;
        } finally {
            document.close();
        }

    }
    var mainBar = top.mainBar;
    if (mainBar) {
        if (mainBar.document.getElementById("showProfitLoss")) {
            mainBar.document.getElementById("showProfitLoss").disabled = !signDependent;
        }
        if (mainBar.document.getElementById("showWhatIf")) {
            mainBar.document.getElementById("showWhatIf").disabled = !signDependent;
        }
        if (mainBar.document.getElementById("wCommissionPercent")) {
            mainBar.document.getElementById("wCommissionPercent").disabled = !signDependent;
        }
    }
    initProfitLossInfo();
}

function setCurrentMarketAndEventName(_currentMarketId, _currentEventId) {
    var marketPage = parent.frames['marketPage'];
    if (marketPage && marketPage.getCurrentMarketAndEventName) {
        if (marketPage.getCurrentMarketAndEventName) {
            var sHTML = marketPage.getCurrentMarketAndEventName(_currentMarketId, _currentEventId);
            var mainBar = parent.frames['mainBar'];
            if (mainBar && mainBar.document.getElementById("CurrentMarketAndEventName")) {
                try {
                    mainBar.document.getElementById("CurrentMarketAndEventName").innerHTML = sHTML;
                } finally {
                    document.close();
                   // alert("document.close();");
                }

            }
        }
    }
}
function showWhatIfChanged() {
    if (top.betBorder.calcProfitLossLocal) {
        top.betBorder.calcProfitLossLocal();
    }
}
function mainBarOnLoad() {
    if (top.main) {
        if (top.main.currentEventId && top.main.setCurrentMarketAndEventName && document.forms.changeGridScale && document.forms.changeGridScale.marketId.value)
            top.main.setCurrentMarketAndEventName(document.forms.changeGridScale.marketId.value, top.main.currentEventId);
    }
    if (document.getElementById("gridScale"))
        oldGSIndex = document.getElementById("gridScale").selectedIndex;
    if (document.getElementById("gridRefreshTimeout"))
        oldGRIndex = document.getElementById("gridRefreshTimeout").selectedIndex;
    if (document.getElementById("showProfitLoss"))
        oldShowPL = document.getElementById("showProfitLoss").checked;
    if (document.getElementById("showWhatIf"))
        oldShowWhatIf = document.getElementById("showWhatIf").checked;
}

function gridPageOnLoad(isLogged) {
    doExpandRight();
    doExpandMainBar(isLogged);
    initSizes();
    if (initUnloadEvent) {
        initUnloadEvent();
    }
    try {
        buildGridContent(sGridStructRows, iTotalSelectionsCount);
    } finally {
        document.close();
    }
    iGridRefreshInterval = iGridRefreshTimeout;
    clearGridTimeout();
    setTimeout("backRefreshEvent();getTopData();", 50);
}

function ajustDivSize() {
    if (bReDraw && (isMozilla() || isOpera())) {
            if (document.body && document.body.offsetHeight > 0) {
                bReDraw = false;
                var newHeight = document.body.offsetHeight - 10;
                if (newHeight > 0) {
                    setDivHeight(grid_content, newHeight);
                    setDivHeight(grid_wrapper, newHeight);
                }
              setTimeout("bReDraw = true", 50);
            }
    }
}

function ajustStyles() {
    if (isIE()) {
        setStyleHeight(grid_content, "100%");
        setStyleHeight(grid_wrapper, "100%");
        document.getElementById(grid_content).style.position = "absolute";
    }
}
function initSizes() {
    ajustStyles();
    if (window.addEventListener) {
        ajustDivSize();
        window.addEventListener("resize", ajustDivSize, false);
    }
}