// uses utils.js
var refreshTimeTimeout = null;
var iTimeTimeout = 30;
var LOGON_USER_FORM = "logonForm";

function getFreeAmount() {
    var result = 0.0;
    if (frames['sumManager']) {
        result = frames['sumManager'].freeAmount;
    }
    return result;
}

function updateSumData(currencySign, freeAmount, blockedAmount) {
    if (document.getElementById(idFreeAmount)) {
        try {
            document.getElementById(idFreeAmount).innerHTML = formatCurrency(currencySign, freeAmount);
        } finally {
            document.close();
        }

    }

    if (document.getElementById(idBlockedAmount)) {
        try {
            document.getElementById(idBlockedAmount).innerHTML = formatCurrency(currencySign, blockedAmount);
        } finally {
            document.close();
        }

    }
}

function updateTimeData(timeValue, timeZoneName) {
    if (document.getElementById(idClock)) {
        try {
            document.getElementById(idClock).innerHTML = timeValue + " (" + timeZoneName + ")";
        } finally {
            document.close();
        }
    }
}

function loadSumData()
{
    var sTarget = sContextPath + '/betex/loadSumDataAction.do';
    if (frames['sumManager']) {
        frames['sumManager'].location.replace(sTarget);
    }
}

function loadTimeData()
{
    var sTarget = sContextPath + '/betex/loadTimeDataAction.do';
    if (window.frames['timeManager']) {
        window.frames['timeManager'].location.replace(sTarget);
    }
}

function setTimeTimeout() {
    refreshTimeTimeout = setTimeout("loadTimeData()", iTimeTimeout * 1000);
}

function topSubmit() {
    if (a) return;
    a = true;
    document.forms.goTop.dump.value = 1;
    document.forms.goTop.submit();
}


function logoutClick() {
    clearTimeouts();
}

function clearTimeouts() {

    if (top && top.marketPage && top.marketPage.oTraderFlushMeatTimeout) {
        clearTimeout(top.marketPage.oTraderFlushMeatTimeout);
    }

    if (top && top.mainBar && top.mainBar.refreshGridTimeout) {
        clearTimeout(top.mainBar.refreshGridTimeout);
    }

    if (top && top.topBar && top.topBar.refreshTimeTimeout) {
        clearTimeout(top.topBar.refreshTimeTimeout);
    }

    if (top && top.main && top.main.refreshGridTimeout) {
        clearTimeout(top.main.refreshGridTimeout);
    }

    if (top && top.betBorder && top.betBorder.mybetPagerefreshMyBetsTimeout) {
        top.betBorder.mybetPage.refreshOn = false;
        clearTimeout(top.betBorder.mybetPage.refreshMyBetsTimeout);
    }
}

function refreshBalance() {
    loadSumData();
}

function doLogon(thisForm) {
    if (validateLogonForm(thisForm)) {
       // debug();
        getAvailableURL(sLoginAction, 1);
    }
}

function trim(s) {
    return s.replace(/^\s*/, "").replace(/\s*$/, "");
}


function inputValid(input) {
    return (input != null && input.value != null && trim(input.value).length > 0);
}

function doLogonByKey(ev) {
    var _ev = ( ev ) ? ev : (( window.event) ? event : null);
    if ((_ev != null) && ((!isIE()) || _ev.type == "keyup")) {
        if (32 == _ev.keyCode) {
            if (isMozilla()) {
                var elem = (_ev.target) ? _ev.target : ((_ev.srcElement) ? _ev.srcElement : null);
                if (elem.name == "save") {
                    elem.checked = (!elem.checked);
                }
            }
        } else if (13 == _ev.keyCode) {
            var thisForm = document.forms[LOGON_USER_FORM];
            if ((thisForm != null) && inputValid(thisForm.login) && inputValid(thisForm.password) && validateLogonUserForm(thisForm)) {
                document.getElementById("selectLanguage").focus();
                getAvailableURL(sLoginAction, 1);
            }
        }
    }

    if (isIE() || isOpera()) {
        _ev.cancelBubble = true;
        _ev.returnValue = false;
    } else if (isMozilla()) {
        _ev.preventDefault();
        _ev.stopPropagation();
    }
    return false;
}

function continueSubmitWith(_ssoURL, _actionURI, _sessionId) {
    if (_ssoURL == null || _ssoURL == "") {
        alert(sNoSSOAvailableAlert);
        return;
    }

    var _form = document.forms[LOGON_USER_FORM];
    if (!_form) _form = document.getElementById(LOGON_USER_FORM);
    if (_form) {
        processOnClickForSubmit(_form.submitButton, true);
        _form.action = _ssoURL + _actionURI;
        _form.sessionId.value = _sessionId;
        _form.submit();
    }
}

function getAvailableURL(action, sr) {
    if (window.frames['uManager']) {
        var sQuery = sAvailableURLAction + "?submitAction=" + action;
        sQuery = (sr != null && sr == 1) ? sQuery + "&sr=1":sQuery;
        window.frames['uManager'].location.replace(sQuery);
    }
}

function guestTopOnLoad() {
    if (top.appErrors != null && top.appErrors.length > 0)
        setTimeout("top.showErrors()", 200);
    if (initUnloadEvent) {
        initUnloadEvent();
    }
    if (document.forms[LOGON_USER_FORM]) {
        if (window.addEventListener) {
            document.forms[LOGON_USER_FORM].login.addEventListener("keyup", doLogonByKey, false);
            document.forms[LOGON_USER_FORM].password.addEventListener("keyup", doLogonByKey, false);
            document.forms[LOGON_USER_FORM].save.addEventListener("keyup", doLogonByKey, false);
        } else {
            document.forms[LOGON_USER_FORM].login.onkeyup = doLogonByKey;
            document.forms[LOGON_USER_FORM].password.onkeyup = doLogonByKey;
            document.forms[LOGON_USER_FORM].save.onkeyup = doLogonByKey;
        }

        if (document.forms[LOGON_USER_FORM].submitButton) {
            document.forms[LOGON_USER_FORM].submitButton.disabled = false;
        }
        if (document.forms[LOGON_USER_FORM].login) {
            document.forms[LOGON_USER_FORM].login.focus();
        }

    }
}


