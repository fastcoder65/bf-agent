function goHome() {
    // top.location.href = sMainWindowLoadAction;
    window.location.href = sMainWindowLoadAction;
}

function showErrors() {
    if (appErrors && appErrors.length > 0) {
        alert(appErrors);
    }
}

function postLoadMarketFrame() {
    if (top.frames.marketPage) {
        top.frames.marketPage.location.replace(sMarketForward);
    }
}

function framesetLoaded() {
    if (SHOW_TIMING == null || !SHOW_TIMING) return;
    top.gEndTime = new Date();
    setTimeout("displayDuration();", 500);
}

function displayDuration() {
    if (SHOW_TIMING == null || !SHOW_TIMING) return;
    var cEndTime = top.gEndTime;
    var cTimeStatistics = (cEndTime - top.gStartTime) / 1000;
    //cTimeStatistics = ( cTimeStatistics != null ) ? cTimeStatistics/1000 : 0;
    var sMessage = "Load Statistics :\n\n";

    sMessage += "start : " + top.gStartTime + "\n ";
    sMessage += "end: " + cEndTime + "\n ";
    sMessage += "duration, seconds = " + cTimeStatistics;
    alert(sMessage);
}

function presetMarketFrame() {
    var wmDoc = null;
    try {
        wmDoc = top.frames.marketPage.document;
    } catch (x) {
        wmDoc = top.frames.marketPage.document;
    }
    if (wmDoc == null) {
        return;
    }
    var sLoadingDoc = [];
    with (sLoadingDoc) {
        push("<html><body onload='top.presetMarketFrame2();' style='background-color:#F9F9F9'>");
        push("<table cellspacing='0' cellpadding='0' height='100%' width='100%'>");
        push("<tr><td id='loadingText' style='font-family: Tahoma;' valign='middle' align='center'>");
        push("</td></tr>");
        push("</table>");
        push("</body></html>");
    }
    wmDoc.write(sLoadingDoc.join(''));
    wmDoc.close();
    // need for properly work of market refresh button !
}

function presetMarketFrame2() {
    wmDoc = top.frames.marketPage.document;
    if (wmDoc != null) {
        wmDoc.getElementById("loadingText").innerHTML = "<font color='#999999'><h5>" + top.sLoadingText + "</h5></font>";
        setTimeout("top.postLoadMarketFrame()", 100);
    }
}

function blankPage() {
    return "<html><head><title>Blank page</title></head><body style='background-color:#F9F9F9'></body></html>";
}

function loadingPage() {
    return "<html><head><title>Blank page</title><META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body onload='top.presetMarketFrame2();' style='background-color:#F9F9F9'><table cellspacing='0' cellpadding='0' height='100%' width='100%'><tr><td id='loadingText' style='font-family: Tahoma;' valign='middle' align='center' ></td></tr></table></body></html>";
}
