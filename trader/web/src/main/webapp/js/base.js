var noName = "noName";
var EVENT_ID = 0;
var EVENT_MARKETID = 1;
var EVENT_MARKETNAME = 2;
var EVENT_EVENTNAME = 3;
var EVENT_NAME = 4;
var EVENT_STATUS = 5;
var EVENT_OPENDATE = 6;
var EVENT_INPLAYSTATUS = 7;
var EVENT_TIME = 8;
var currentEvents = new Array();
var lastOpenedEvents = new Array();
var inPlayEvents = new Array();
var closingSoonEvents = new Array();
var openSoonEvents = new Array();
var currentMarketId = null;
var currentEventId = null;
var iCurrentTimeZoneId = null;
var iBasePageRefreshInterval = (iBaseRefreshTimeout != null && iBaseRefreshTimeout > 0) ? 45 : 0;
var refreshBasePageTimeout = null;
var currentUserDateTime = new Date();
var restInMinutes = 1000 * 60;
var userOffsetInMillis = currentUserDateTime.getTimezoneOffset() * restInMinutes;
var currentDateTime = 0;

var ALL_EVENTS = "all_events";
var LOADING_EVENTS = "loadingEvents";

var AREA_NEWS = "area-news";

function setBasePageTimeout() {
    if (iBasePageRefreshInterval > 0)
        refreshBasePageTimeout = setTimeout("getBasePageData()", iBasePageRefreshInterval * 1000);
}

function prepareArrayData(sEventInfo) {
    var result = new Array();
    if (sEventInfo.length > 0) {
        var tempGR = sEventInfo.substring(0, sEventInfo.length - 1).split("|");
        for (var i = 0; i < tempGR.length; i++) {
            tempGR[i] = tempGR[i].split("~");
            result[result.length] = new Array(Number(tempGR[i][EVENT_ID]), tempGR[i][EVENT_MARKETID], tempGR[i][EVENT_MARKETNAME], tempGR[i][EVENT_EVENTNAME], tempGR[i][EVENT_NAME], tempGR[i][EVENT_STATUS], tempGR[i][EVENT_OPENDATE], tempGR[i][EVENT_INPLAYSTATUS], tempGR[i][EVENT_TIME]);
        }
    }
    return result;
}

function getBasePageData()
{
    hideContent();
    if (refreshBasePageTimeout != null) {
        clearTimeout(refreshBasePageTimeout);
        refreshBasePageTimeout = null;
    }
    if (top.frames['main'].frames['baseDataContainer'] && sBaseDataActionURI.length > 0 && sBaseDataActionURI.indexOf("/") == 0) {
        top.frames['main'].frames['baseDataContainer'].location.replace(sBaseDataActionURI + "?timeOffset=" + userOffsetInMillis);
    } else {
        clearTimeout(refreshBasePageTimeout);
        refreshBasePageTimeout = null;
    }
}

function gotoMarket(iMarketId, iEventId)
{
    var win = top.marketPage;
    var checkEventExists = true;
    if (win && win.goMenu && win.reloadMarkets && win.FASTLINK) {
        if (!win.goMenu(iMarketId, iEventId, checkEventExists)) {
            win.reloadMarkets(iMarketId, iEventId, win.FASTLINK);
        } else {
            if (win && win.goMarket) {
                win.goMarket(iEventId, null, true, iMarketId, null, win.FASTLINK);
            }
        }
    }
}

function reloadMarkets(targetMarketId, targetEventId) {
    var win = top.marketPage;
    var topBar = top.topBar;
    if (topBar) {
        topBar.savedMarketId = (targetMarketId != null && targetMarketId > 0) ? targetMarketId : win.currentAllItem;
        topBar.savedEventId = (targetEventId != null && targetEventId > 0) ? targetEventId : win.iCurrentMarketID;
    }
    top.presetMarketFrame();
}

function getStartDate(startDateStr, eventTime, showRest) {
    var dateDiff = Math.ceil((new Date(Number(eventTime)) - new Date(Number(currentDateTime))) / restInMinutes);
    var bCond = showRest && (dateDiff > 0);
    var sMinutesTxt;
    var lastFigure = (dateDiff >= 10) ? (dateDiff % 10) : dateDiff;
    switch (lastFigure) {case 0:case 5:case 6:case 7:case 8:case 9:sMinutesTxt = eval("sMinutesTxt3");break;case 2:case 3:case 4:if (dateDiff >= 10 && dateDiff < 15)sMinutesTxt = eval("sMinutesTxt3"); else sMinutesTxt = eval("sMinutesTxt2");break;case 1:if (dateDiff == 11)sMinutesTxt = eval("sMinutesTxt3"); else sMinutesTxt = eval("sMinutesTxt1");}
    var restTime = (bCond) ? "<b>(" + dateDiff + " " + sMinutesTxt + ")</b>" : "";
    var res = ((startDateStr != null) && (startDateStr.length > 0)) ? "&nbsp;" + startDateStr + "&nbsp;" + restTime + "&nbsp;" : "";
    return res;
}
function getEventStatusIcon(iInplayStatus)
{
    if (iInplayStatus == EVENT_STATE_INPLAY)return'&nbsp;&nbsp;<img src="' + iconInPlay + '">&nbsp;'; else if (iInplayStatus == EVENT_STATE_MAYBE_INPLAY)return'&nbsp;&nbsp;<img src="' + iconInPlayInactive + '">&nbsp;'; else return'&nbsp;&nbsp;';
}

function getEventsHTML(eventsArray, sEventsCaption, showRest) {
    var a = [];
    a.push("\n<table cellpadding='1' cellspacing='1' border='0' width='100%'>"
            + "<tr><td class='tops-title' height='20px'>" + sEventsCaption + "</td></tr>");
    var market_ID = noName;
    var event_ID = noName;
    for (var i = 0; i < eventsArray.length; i++) {
        if (eventsArray[i][EVENT_MARKETNAME] != market_ID) {
            market_ID = eventsArray[i][EVENT_MARKETNAME];
            a.push("\n<tr><td>"
                    + "\n<span style='font-size:14px; font-family: Tahoma; color:#DF6F6F'><b>" + eventsArray[i][EVENT_MARKETNAME] + "</b></span>"
                    + "\n</td></tr>");
        }
        var eventID = eventsArray[i][EVENT_TIME] + "," + eventsArray[i][EVENT_EVENTNAME];
        if (event_ID != eventID) {
            event_ID = eventID;

            if (eventsArray[i][EVENT_STATUS] == iEventStatusClosed) {
                a.push("<tr><td>&nbsp;" + getStartDate(eventsArray[i][EVENT_OPENDATE], eventsArray[i][EVENT_TIME], showRest));
                a.push("<b><i>" + eventsArray[i][EVENT_EVENTNAME] + "</i></b>");
                a.push("</td></tr>");
            }  else {
                a.push("<tr><td>&nbsp;" + getStartDate(eventsArray[i][EVENT_OPENDATE], eventsArray[i][EVENT_TIME], showRest));
                a.push("<a href='javascript:gotoMarket(" + eventsArray[i][EVENT_MARKETID] + "," + eventsArray[i][EVENT_ID] + ");'>");
                a.push("<b><i>" + eventsArray[i][EVENT_EVENTNAME] + "</i></b></a>");
                a.push("</td></tr>");
            }
        }
/*
        if ( eventsArray[i][EVENT_STATUS] == iEventStatusOpened &&( eventsArray[i][EVENT_INPLAYSTATUS] == EVENT_STATE_INPLAY || eventsArray[i][EVENT_INPLAYSTATUS] == EVENT_STATE_MAYBE_INPLAY )) {
              a.push("<tr><td height='10px' nowrap>&nbsp;&nbsp;" + getEventStatusIcon(eventsArray[i][EVENT_INPLAYSTATUS]) + "<a href='javascript:gotoMarket(" + eventsArray[i][EVENT_MARKETID] + "," + eventsArray[i][EVENT_ID] + ");'><u>" + eventsArray[i][EVENT_NAME] + "</u></a>" + "</td></tr>");
        }
*/

/*
        if (eventsArray[i][EVENT_STATUS] == iEventStatusClosed) {
          //  a.push("<tr><td height='10px' nowrap>&nbsp;&nbsp;" + eventsArray[i][EVENT_NAME] + "</td></tr>");
        } else if (!showRest) {
           a.push("<tr><td height='10px' nowrap>&nbsp;&nbsp;" + getEventStatusIcon(eventsArray[i][EVENT_INPLAYSTATUS]) + "<a href='javascript:gotoMarket(" + eventsArray[i][EVENT_MARKETID] + "," + eventsArray[i][EVENT_ID] + ");'><u>" + eventsArray[i][EVENT_NAME] + "</u></a>" + "</td></tr>");
        }
*/
        if (eventsArray[i][EVENT_STATUS] == iEventStatusOpened  && !showRest) {
           a.push("<tr><td height='10px' nowrap>&nbsp;&nbsp;" + getEventStatusIcon(eventsArray[i][EVENT_INPLAYSTATUS]) + "<a href='javascript:gotoMarket(" + eventsArray[i][EVENT_MARKETID] + "," + eventsArray[i][EVENT_ID] + ");'><u>" + eventsArray[i][EVENT_NAME] + "</u></a>" + "</td></tr>");
        }
    }
    a.push("<tr><td></td></tr></table>");
    return a.join('');
}

function getAllEvents() {
    var a = [];
    a.push("\n<table cellpadding='0' cellspacing='1' border='0' width='100%' height='100%'><tr>");
    var sEventsContainerBegin = "<td class='tops-border' valign='top'>"
            + "<table cellpadding='4' cellspacing='4' border='0' width='100%'><tr><td valign='top'>";
    var sNoEventsContainerBegin = "<td class='tops-border' >"
            + "<table cellpadding='4' cellspacing='4' border='0' width='100%'><tr><td align='center' valign='middle'>";
    var sEventsContainerEnd = "</td></tr></table></td>";
    var totalEventCount = 0;
    if (closingSoonEvents.length > 0 || inPlayEvents.length > 0) {
        a.push("<td class='tops-border' valign='top'>"
                + "<table cellpadding='4' cellspacing='4' border='0' width='100%'>");
        if (closingSoonEvents.length > 0) {
            a.push("<tr><td valign='top'>");
            a.push(getEventsHTML(closingSoonEvents, sClosingSoonEventsCaption, true));
            a.push("</td></tr>");
        }
        if (inPlayEvents.length > 0) {
            a.push("<tr><td valign='top'>");
            a.push(getEventsHTML(inPlayEvents, sInPlayEventsCaption, false));
            a.push("</td></tr>");
        }
        a.push("</table></td>");
        totalEventCount += closingSoonEvents.length;
        totalEventCount += inPlayEvents.length;
    }
    if (currentEvents.length > 0) {
        a.push(sEventsContainerBegin);
        a.push(getEventsHTML(currentEvents, sCurrentEventsCaption, false));
        a.push(sEventsContainerEnd);
        totalEventCount += currentEvents.length;
    }
    if (lastOpenedEvents.length > 0 || openSoonEvents.length > 0) {
        a.push("<td class='tops-border' valign='top'>"
                + "<table cellpadding='4' cellspacing='4' border='0' width='100%'>");
        if (lastOpenedEvents.length > 0) {
            a.push("<tr><td valign='top'>");
            a.push(getEventsHTML(lastOpenedEvents, sLastOpenedEventsCaption, false));
            a.push("</td></tr>");
        }
        if (openSoonEvents.length > 0) {
            a.push("<tr><td valign='top'>");
            a.push(getEventsHTML(openSoonEvents, sOpenSoonEventsCaption, false));
            a.push("</td></tr>");
        }
        a.push("</table></td>");
        totalEventCount += lastOpenedEvents.length;
        totalEventCount += openSoonEvents.length;
    }
    if (totalEventCount == 0) {
        a.push(sNoEventsContainerBegin);
        a.push(sNoActiveEventsFound);
        a.push(sEventsContainerEnd);
    }
    a.push("</tr></table>");
    return a.join('');
}

function hideContent() {
    var ELEM_ALL_EVENTS     = document.getElementById(ALL_EVENTS);
    var ELEM_LOADING_EVENTS = document.getElementById(LOADING_EVENTS);

    if (ELEM_ALL_EVENTS.style.display == "none" && ELEM_LOADING_EVENTS.style.display == "none") {
        ELEM_ALL_EVENTS.style.display = "none";
        ELEM_LOADING_EVENTS.style.display = "block";
    }
}

function swapContent() {

    var ELEM_ALL_EVENTS     = document.getElementById(ALL_EVENTS);
    var ELEM_LOADING_EVENTS = document.getElementById(LOADING_EVENTS);

    ELEM_ALL_EVENTS.style.display       = (ELEM_ALL_EVENTS.style.display     == "none"? "block": "none");
    ELEM_LOADING_EVENTS.style.display   = (ELEM_LOADING_EVENTS.style.display == "none"? "block": "none");
}

function getHiddenElement() {
    var ELEM_ALL_EVENTS     = document.getElementById(ALL_EVENTS);
    var ELEM_LOADING_EVENTS = document.getElementById(LOADING_EVENTS);
    return (ELEM_ALL_EVENTS.style.display == "none")? ELEM_ALL_EVENTS : ELEM_LOADING_EVENTS;
}

function buildEventContent() {
    var eventContentElement = getHiddenElement(); // document.getElementById("all_events");
    if (eventContentElement) {
        eventContentElement.innerHTML = getAllEvents();
    }
    swapContent();
    setTimeout("initSizes();", 50);
}

function ajustDivSize() {
    if (bReDraw && (isMozilla() || isOpera())) {
        bReDraw = false;
        if (document.body) {
            if (document.body.offsetHeight > 0) {
                var newHeight = document.body.offsetHeight
                        - ((document.getElementById(AREA_NEWS)) ? document.getElementById(AREA_NEWS).offsetHeight : 0);
                if (newHeight > 0) {
                    setDivHeight(ALL_EVENTS, newHeight);
                    setDivHeight(LOADING_EVENTS, newHeight);
                }
            }
        }
        setTimeout("bReDraw = true", 250);
    }
}

function ajustStyles() {
    if (isIE()) {
        setStyleHeight(ALL_EVENTS, "100%");
        setStyleHeight(LOADING_EVENTS, "100%");
    }
}

function initSizes() {
    ajustStyles();
    if (window.addEventListener) {
        ajustDivSize();
        window.addEventListener("resize", ajustDivSize, false);
    }
}