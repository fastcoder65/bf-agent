
var popularMarketsWrapper   = "popularMarketsWrapper";
var popularMarketsContainer = "popularMarketsContainer";
var popularMarketsLoading   = 'popularMarketsLoading';

var TABLE_ALLTAB = "tableAllTab";
var LIST_AREA = "listArea";
var tableAllTab = TABLE_ALLTAB;
var listArea = LIST_AREA;
function setSortType(formName, _sortType) {
    var form = document.forms[formName];
    if (sortType != _sortType) {
        hideContent();
        sortType = _sortType;
        form.sortType.value = sortType;
        form.submit();
    }
}
function whereIsMoneyDataLoading(_sortType) {
    hideContent();
    var sTarget = sContextPath + '/betex/loadWhereIsMoneyData.do?sortType=' + _sortType;
    if (frames['whereIsMoneyData']) {
        frames['whereIsMoneyData'].location.href = sTarget;
    }
}

function showContent() {
    document.getElementById( popularMarketsLoading )   .style.display = "none";
    document.getElementById( popularMarketsWrapper )   .style.display = "block";
    document.getElementById( popularMarketsContainer ) .style.display = "block";
}

function hideContent() {
    document.getElementById( popularMarketsContainer ).style.display = "none";
    document.getElementById( popularMarketsWrapper )  .style.display = "none";
    document.getElementById( popularMarketsLoading )  .style.display = "block";
}

function gotoMarket(iMarketId, iEventId, bCoupon, sOrigin) {
    var win = null;
    if (opener)
        try {
            if (opener.window.top.frames['marketPage'])
                win = opener.window.top.frames['marketPage'];
        } catch(x) {
        }
    if (win)
        if (win.goMarket) {
            win.goMarket(iEventId, null, true, iMarketId, null, sOrigin);
            win.focus();
        }
        else {
            alert(sOpenAgainMessage);
            window.close();
        }
}

function ajustDivSize() {
    if (bReDraw && (isMozilla() || isOpera())) {
        bReDraw = false;
        if (document.body) {
            if (document.body.offsetHeight > 0) {
                var newHeight = document.body.offsetHeight
                        - 6
                        - document.getElementById("top-bar").offsetHeight
                        - document.getElementById("bottom-bar").offsetHeight
                        - document.getElementById("header-bar").offsetHeight;
                if (newHeight > 0) {
                    setDivHeight( popularMarketsContainer, newHeight);
                    setDivHeight( popularMarketsWrapper, newHeight);
                    setDivHeight( popularMarketsLoading, newHeight);
                }
            }
        }
        setTimeout("bReDraw = true", 250);
    }
}

function ajustStyles() {
    if (isIE()) {
        setStyleHeight(tableAllTab,                 "100%");
        setStyleHeight( popularMarketsContainer,    "100%");
        setStyleHeight( popularMarketsWrapper,      "100%");
        setStyleHeight( popularMarketsLoading,      "100%");
    }
}

function initSizes() {
    ajustStyles();
    if (window.addEventListener) {
        ajustDivSize();
        window.addEventListener("resize", ajustDivSize, false);
    }
}