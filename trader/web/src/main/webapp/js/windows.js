var defWinObjects = new Array();

function saveWinObject(winName, winObject, winObjectsArray) {
    if (winObjectsArray[winName] != null) {
        var win = winObjectsArray[winName];
        try { win.close(); } catch (x) {};
        winObjectsArray[winName] = null;
        delete winObjectsArray[winName];
    }
    winObjectsArray[winName] = winObject;
}

function clearWinObjects(winObjectsArray) {
    for (var winObjectName in  winObjectsArray) {
        var winObject = winObjectsArray[winObjectName];
        if (winObject != null) {
           try { winObject.close(); } catch (x) {};
            winObject = null;
        }
    }
    winObjectsArray = new Array();
}

function openWin(url, width, height, winObject, winOptions, winAlias, winObjectsArray) {
    var bReplace = true;
    //  URL replaces the current document in the history list

    var _winObjects = (winObjectsArray != null) ? winObjectsArray : defWinObjects;
    var _winName = (winAlias != null) ? winAlias : url;

    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;

    var defWindowOptions = "toolbar=0,location=0,directories=0," +
                           "statusbar=0,menubar=0,scrollbars=0,resizable=0," +
                           "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                           "border=thin,help=0,maximize=0";

    var windowOptions = (winOptions != null && winOptions.length > 0) ? winOptions : defWindowOptions;

    if (!winObject || winObject.closed) {
        winObject = window.open(url, "", windowOptions, bReplace);
        saveWinObject(_winName, winObject, _winObjects);
        winObject.focus();
    }
    else {
    // window has already been opened, bring it in front
        winObject.location.replace(url);
        winObject.focus();
    }
    return winObject;
}

function openStatusWin(url, width, height, status)
{
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var bReplace = true;
    //  URL replaces the current document in the history list

    return    window.open(
            url,
            "",
            "toolbar=0,location=0,directories=0," +
            "statusbar=0,menubar=0,scrollbars=0,resizable=no," +
            "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
            "border='none',help=0,maximize=0", bReplace);
}

function openBigDialog(url, bigDialogWin)
{
    return openWin(url, 800, 600, bigDialogWin);
}

function openDialog(url, dialogWin)
{
    return openWin(url, 600, 400, dialogWin);
}

function openRestorePasswordWin(url, restorePasswordWin)
{
    return openDialog(url, restorePasswordWin);
}

function openCurrencyRates(url, currencyRatesWin)
{
    return openDialog(url, currencyRatesWin);
}

function openWhereIsMoney(url, whereIsMoneyWin)
{
    return openAutoWin(url, whereIsMoneyWin);
}

function openMultiBetsWin (url, whereIsMoneyWin)
{
   // return openWin(url, 900, 700, whereIsMoneyWin);
    return openAutoWin(url, whereIsMoneyWin); 
}

function CustomizeMyMarkets(url, myMarketsWin)
{
    return openWin(url, 548, 600, myMarketsWin);
}

function openResultInfoWin(url, resultInfoWin)
{
    return openWin(url, 700, 410, resultInfoWin);
}

function openProfileWin(url, profileWin)
{
 return openTraderInfoWin( url, profileWin );
}

function openTraderInfoWin( url, winObj, winAlias) {
    var width = 1050;
    var height = 700;
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=yes," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    return openWin(url, width, height, winObj, cWindowOptions, winAlias);
}

function openAutoWin(url, win)
{
    var width = 900;
    var height = 700;
    var left = (screen.availWidth - width) / 2;
    var top = (screen.availHeight - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=yes," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    return openWin(url, width, height, win, cWindowOptions);
}


function openAutoScrollWin(url, win)
{
    var width = 900;
    var height = 700;
    var left = (screen.availWidth - width) / 2;
    var top = (screen.availHeight - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=yes,resizable=yes," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    return openWin(url, width, height, win, cWindowOptions);
}

function openDetailsPopup(url, detailsPopupWin)
{

    var width = 250;
    var height = 400;
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "status=0,menubar=0,scrollbars=1,resizable=0," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border=thin,help=0,maximize=0";
    return openWin(url, width, height, detailsPopupWin, cWindowOptions);
}

function openRegWin(url, win)
{

    var width = 800;
    var height = 780;
    var left = (screen.availWidth - width) / 2;
    var top = (screen.availHeight - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=yes," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    return openWin(url, width, height, win, cWindowOptions);
}

function openWinRe(url, width, height, winHandle, winOptions)
{
    var bReplace = true;
    //  URL replaces the current document in the history list
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var defWindowOptions = "toolbar=0,location=0,directories=0," +
                           "status=0,menubar=0,scrollbars=0,resizable=0," +
                           "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                           "border=thin,help=0,maximize=0";
    var windowOptions = (winOptions != null && winOptions.length > 0) ? winOptions : defWindowOptions;
    if (!winHandle || winHandle.closed) {
        winHandle = window.open(
                url,
                "",
                windowOptions,
                bReplace);
    } else {
        // window has already been opened, bring it in front
        winHandle.location.replace(url);
        winHandle.focus();
    }
    return winHandle;
}

function openDirectoryWin(url, win, winAlias)
{
    var width = 900;
    var height = 700;
    var left = (screen.availWidth - width) / 2;
    var top = (screen.availHeight - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=yes," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    if (isMozilla()) cWindowOptions = "toolbar=0,location=0,directories=0," +
                                      "statusbar=0,menubar=0,scrollbars=0,resizable=0,dependent=yes," +
                                      "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                                      "border='none'";
    return openWin(url, width, height, win, cWindowOptions, winAlias);
}

function openModalWin(url, win)
{
    var width = 500;
    var height = 600;
    var left = 50;
    var top = 50;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=0," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    if (isMozilla()) cWindowOptions = "toolbar=0,location=0,directories=0," +
                                      "statusbar=0,menubar=0,scrollbars=0,resizable=0,dependent=yes," +
                                      "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                                      "border='none'";
    return openWin(url, width, height, win, cWindowOptions, "Modal");
}

function openUserInfoWin(url, winObject){
    var width = 320;
    var height = 200;
    var left = (screen.availWidth - width) / 2;
    var top = (screen.availHeight - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=no," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    winObject = openWin(url, width, height, winObject, cWindowOptions, "userInfo");
    return winObject;
}

function openDetailsWin(winAlias, url, winObject, winObjectsArray) {
    var width = 600;
    var height = 300;
    var left = (screen.availWidth - width) / 2;
    var top = (screen.availHeight - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=no," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    winObject = openWin(url, width, height, winObject, cWindowOptions, winAlias, winObjectsArray);
    return winObject;
}

function openCashierHistoryWin(url, profileWin)
{
    var width = 1000;
    var height = 700;
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=yes," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    return openWin(url, width, height, profileWin, cWindowOptions);
}

function doUnloadChidren() {
    clearWinObjects(defWinObjects);
}

function openWinReEx(url, width, height, winObject, winOptions, winAlias, winObjectsArray)
{
    var bReplace = true;

    var _winObjects = (winObjectsArray != null) ? winObjectsArray : defWinObjects;
    var _winName = (winAlias != null) ? winAlias : url;

    //  URL replaces the current document in the history list
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var defWindowOptions = "toolbar=0,location=0,directories=0," +
                           "status=0,menubar=0,scrollbars=0,resizable=0," +
                           "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                           "border=thin,help=0,maximize=0";
    var windowOptions = (winOptions != null && winOptions.length > 0) ? winOptions : defWindowOptions;
    if (!winObject || winObject.closed) {
        winObject = window.open(url, "", windowOptions, bReplace);
        saveWinObject(_winName, winObject, _winObjects);
        winObject.focus();
    }
    else {
    // window has already been opened, bring it in front
        winObject.location.replace(url);
        winObject.focus();
    }
    return winObject;
}


function openAddNewRateWin(url, width, height, win)
{
    var left = (screen.width - width) / 2;
    var top = (screen.height - height) / 2;
    var cWindowOptions = "toolbar=0,location=0,directories=0," +
                         "statusbar=0,menubar=0,scrollbars=0,resizable=0," +
                         "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                         "border='none',help=0,maximize=0";
    if (isMozilla()) cWindowOptions = "toolbar=0,location=0,directories=0," +
                                      "statusbar=0,menubar=0,scrollbars=0,resizable=0,dependent=yes," +
                                      "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left + "," +
                                      "border='none'";
    return openWinReEx(url, width, height, win, cWindowOptions);
}


// -- this handler should be called from window "onload" event to close children windows when "opener" window is unloading.
function initUnloadEvent() {
    if (document.getElementsByTagName && document.all) {
        document.body.onunload = doUnloadChidren;
    } else if (window.addEventListener) {
        window.addEventListener("unload", doUnloadChidren, false);
    }
}
