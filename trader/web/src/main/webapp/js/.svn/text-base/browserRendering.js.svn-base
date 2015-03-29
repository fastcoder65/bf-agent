var bReDraw = true;

var __isDOM=document.getElementById && document.getElementsByTagName; //DOM1 browser (MSIE 5+, Netscape 6, Opera 5+)
var __isOpera = eval("window.opera") && __isDOM; //Opera 5+
var __isOpera5 = __isOpera;
var __isOpera6=__isOpera && window.print; //Opera 6+
var __isOpera7=__isOpera && document.readyState; //Opera 7+
var __isMSIE=document.all && document.all.item && !__isOpera; //Microsoft Internet Explorer 4+
var __isMSIE5=__isDOM && __isMSIE; //MSIE 5+

var __isNetscape4 = eval("document.layers"); //Netscape 4.*

var __isMozilla=__isDOM
        && (navigator.userAgent.toLowerCase().indexOf("mozilla") != -1)
        && (navigator.appName.toLowerCase().indexOf("microsoft") == -1); //navigator.appName=="Netscape" //Mozilla или Netscape 6.*

 // for backward compatibility;
var W3DOM   = __isDOM;
var IE      = __isMSIE5;
 // end: for backward compatibility;

function isMozilla()
{
    return __isMozilla;
}

function isIE()
{
    return __isMSIE5;
}

function isOpera()
{
    return __isOpera;
}


function setDivWidth(divName, newWidth) {
    if (document.getElementById(divName)) {
        document.getElementById(divName).style.width = newWidth + "px";
    }
}
function setDivHeight(divName, newHeight) {
    if (document.getElementById(divName)) {
        document.getElementById(divName).style.height = newHeight + "px";
    }
}

function setStyleHeight(id, newStyle) {
    if (document.getElementById(id)) {
        document.getElementById(id).style.height = newStyle;
    }
}

function setStyleWidth(id, newStyle) {
    if (document.getElementById(id)) {
        document.getElementById(id).style.width = newStyle;
    }
}

/*
// old "BAD" versions
function fixMarketBar() {
    if (bReDraw && (isMozilla() || isOpera())) {
        bReDraw = false;
        if (document.body) {
            if (document.body.offsetHeight && document.body.offsetHeight > 0) {
                var MarketBarHeight = document.body.offsetHeight - 4
                        - document.getElementById("imgHelp").offsetHeight;
                if (MarketBarHeight > 0) {
                    setDivHeight("arrow1", MarketBarHeight);
                    setDivHeight("arrow2", MarketBarHeight);
                }
            }
        }
        setTimeout("bReDraw = true", 250);
    }
}

function fixMarketPage() {
    if (bReDraw  && (isMozilla() || isOpera())) {
        bReDraw = false;
        if (document.body) {
            if (document.body.offsetHeight > 0) {
                var AllMarketsNewHeight = document.body.offsetHeight - 12 -
                                          document.getElementById("tabs").offsetHeight -
                                          document.getElementById("buttons_allMarkets").offsetHeight -
                                          document.getElementById("allMenuParents").offsetHeight;
                if (AllMarketsNewHeight > 0)
                    setDivHeight("allMarketsTreeContainer", AllMarketsNewHeight);
                var MyMarketsNewHeight = document.body.offsetHeight - 12 -
                                         document.getElementById("tabs").offsetHeight -
                                         document.getElementById("buttons_myMarkets").offsetHeight -
                                         document.getElementById("myMenuParents").offsetHeight;
                if (MyMarketsNewHeight > 0)
                    setDivHeight("myMarketsTreeContainer", MyMarketsNewHeight);
            }
        }
        setTimeout("bReDraw = true", 450);
    }
}

function fixCustomizeMarketPage() {
    if (bReDraw && (isMozilla() || isOpera())) {
        bReDraw = false;
        if (document.body) {
            var _offsetHeight = (isOpera())? document.body.offsetHeight : document.body.offsetHeight;
            if ( _offsetHeight > 0) {
                var newAllHeight = _offsetHeight -
                                   12 -
                                   document.getElementById("area_top").offsetHeight -
                                   document.getElementById("area_bottom").offsetHeight -
                                   document.getElementById("allMenuParents").offsetHeight;
                if (newAllHeight > 0)
                    setDivHeight("allMarketsTreeContainer", newAllHeight);
                var newMyHeight = _offsetHeight -
                                  12 -
                                  document.getElementById("area_top").offsetHeight -
                                  document.getElementById("area_bottom").offsetHeight -
                                  document.getElementById("myMenuParents").offsetHeight;
                if (newMyHeight > 0)
                    setDivHeight("myMarketsTreeContainer", newMyHeight);
               // debugPanel("scrollHeight="+ _offsetHeight +", nah="+ newAllHeight+", n.m.h" + newMyHeight);
            }
        }
        setTimeout("bReDraw = true", 450);
    }
}

function fixPopupWinInMozilla() {
    if (bReDraw && isMozilla()) {
        bReDraw = false;
        if (document.body && document.body.offsetHeight > 0) {
            document.getElementById("area_content").style.height = (document.body.offsetHeight
                    - 2 - (document.getElementById("area_top").offsetHeight)
                    - (document.getElementById("area_bottom").offsetHeight)) + "px";
            setTimeout("bReDraw = true;", 350);
        }
    }
}
*/

// *****************  new, "progressive" versions **********************

function fixMarketBar() {
    if (bReDraw && (isMozilla() || isOpera())) {
            if (document.body && document.body.offsetHeight && document.body.offsetHeight > 0) {
              bReDraw = false;
                var MarketBarHeight = document.body.offsetHeight - 4
                        - document.getElementById("imgHelp").offsetHeight;
                if (MarketBarHeight > 0) {
                    setDivHeight("arrow1", MarketBarHeight);
                    setDivHeight("arrow2", MarketBarHeight);
                }
              setTimeout("bReDraw = true", 50);
            }
    }
}

function fixMarketPage() {
    if (bReDraw  && (isMozilla() || isOpera())) {
            if (document.body && document.body.offsetHeight > 0) {
              bReDraw = false;
                var AllMarketsNewHeight = document.body.offsetHeight - 12 -
                                          document.getElementById("tabs").offsetHeight -
                                          document.getElementById("buttons_allMarkets").offsetHeight -
                                          document.getElementById("allMenuParents").offsetHeight;
                if (AllMarketsNewHeight > 0)
                    setDivHeight("allMarketsTreeContainer", AllMarketsNewHeight);
                var MyMarketsNewHeight = document.body.offsetHeight - 12 -
                                         document.getElementById("tabs").offsetHeight -
                                         document.getElementById("buttons_myMarkets").offsetHeight -
                                         document.getElementById("myMenuParents").offsetHeight;
                if (MyMarketsNewHeight > 0)
                    setDivHeight("myMarketsTreeContainer", MyMarketsNewHeight);
              setTimeout("bReDraw = true", 50);
            }
    }
}

function fixCustomizeMarketPage() {
    if (bReDraw && (isMozilla() || isOpera())) {
            if (document.body && document.body.offsetHeight > 0) {
                bReDraw = false;
                var newAllHeight = document.body.offsetHeight -
                                   12 -
                                   document.getElementById("area_top").offsetHeight -
                                   document.getElementById("area_bottom").offsetHeight -
                                   document.getElementById("allMenuParents").offsetHeight;
                if (newAllHeight > 0)
                    setDivHeight("allMarketsTreeContainer", newAllHeight);
                var newMyHeight = document.body.offsetHeight -
                                  12 -
                                  document.getElementById("area_top").offsetHeight -
                                  document.getElementById("area_bottom").offsetHeight -
                                  document.getElementById("myMenuParents").offsetHeight;
                if (newMyHeight > 0)
                    setDivHeight("myMarketsTreeContainer", newMyHeight);
               setTimeout("bReDraw = true", 50);
            }
    }
}

function fixPopupWinInMozilla() {
    if (false && bReDraw && isMozilla()) {//disabled
        if (document.body && document.body.offsetHeight > 0) {
            bReDraw = false;
            document.getElementById("area_content").style.height = (document.body.offsetHeight
                    - 2 - (document.getElementById("area_top").offsetHeight)
                    - (document.getElementById("area_bottom").offsetHeight)) + "px";
            setTimeout("bReDraw = true;", 50);
        }
    }
}

// -- end of new, "progressive" versions
// @todo : all procs below should be refactored as new "progressive" versions when all people will be "in idle" :)

function initPopup() {
    if (document.getElementsByTagName && document.all) {
    } else if (window.addEventListener) {
        fixPopupWinInMozilla();
        window.addEventListener("resize", fixPopupWinInMozilla, false);
    } else if (document.addEventListener && window.opera) {
    }
}
function fixCustomizeGamePage()
{
    if (bReDraw)
    {
        bReDraw = false;
        var maxHeight = document.body.offsetHeight - 155;
        var areaTop = document.getElementById("area_top");
        var areaNav = document.getElementById("area_nav");
        var areaValues = document.getElementById("areaValues");
        if (areaTop) {
            maxHeight -= areaTop.offsetHeight;
        }
        if (areaNav) {
            maxHeight -= areaNav.offsetHeight;
        }
        if (areaValues && maxHeight > 100) {
            areaValues.style.height = maxHeight;
            if (isMozilla()) {
                window.resizeBy(0, 1);
                window.resizeBy(0, -1);
            }
        }
        setTimeout("bReDraw = true;", 500);
    }
}
function fixCustomizeDirectoryPage()
{
    if (bReDraw)
    {
        bReDraw = false;
        var IFrameObj = document.getElementById("tab" + currentTab + "Content");
        var IFrameDoc;
        if (IFrameObj.contentDocument) {
            IFrameDoc = IFrameObj.contentDocument;
        } else if (IFrameObj.contentWindow) {
            IFrameDoc = IFrameObj.contentWindow.document;
        } else if (IFrameObj.document) {
            IFrameDoc = IFrameObj.document;
        } else {
            return;
        }
        var maxHeight = IFrameDoc.body.offsetHeight - 40;
        var areaTop = IFrameDoc.getElementById("area_top");
        var areaTitle = IFrameDoc.getElementById("area_title");
        var areaValues = IFrameDoc.getElementById("areaValues");
        var areaBottom = IFrameDoc.getElementById("area_bottom");
        var areaNav = document.getElementById("area_nav");
        if (areaTop) {
            maxHeight -= areaTop.offsetHeight
        }
        if (areaTitle) {
            maxHeight -= areaTitle.offsetHeight
        }
        if (areaBottom) {
            maxHeight -= areaBottom.offsetHeight
        }
        if (areaNav) {
            maxHeight -= 25;
        }
        if (areaValues) {
            areaValues.style.height = maxHeight;
            if (isMozilla()) {
                window.resizeBy(0, 1);
                window.resizeBy(0, -1);
            }
        }
        setTimeout("bReDraw = true;", 500);
    }
}
function fixCustomizeTreePage()
{
    if (bReDraw)
    {
        bReDraw = false;
        var maxHeight = document.body.offsetHeight - 90;
        var tree = document.getElementById("gameTree");
        var main = document.getElementById("main");
        if (tree) {
            tree.style.height = maxHeight - 20;
        }
        if (main) {
            main.style.height = maxHeight;
        }
        if (isMozilla()) {
            window.resizeBy(0, 1);
            window.resizeBy(0, -1);
        }
        setTimeout("bReDraw = true;", 1000);
    }
}
var bReDraw2 = true;
function fixCustomizeGamesPage()
{
    if (bReDraw2)
    {
        bReDraw = false;
        bReDraw2 = false;
        var maxHeight = document.body.offsetHeight - 40;
        var areaTop = document.getElementById("area_top");
        var areaTitle = document.getElementById("area_title");
        var areaValues = document.getElementById("areaValues");
        var areaNav = document.getElementById("area_nav");
        var areaTitleM = document.getElementById("area_title_Market");
        var areaValuesM = document.getElementById("areaMarketValues");
        var areaClose = document.getElementById("area_close");
        if (areaTop) {
            maxHeight -= areaTop.offsetHeight
        }
        if (areaTitle) {
            maxHeight -= areaTitle.offsetHeight
        }
        if (areaNav) {
            maxHeight -= 25;
        }
        if (areaTitleM) {
            maxHeight -= 25;
        }
        if (areaValuesM) {
            var oldHeight = areaValuesM.offsetHeight;
            if (oldHeight * 3 > maxHeight) {
                areaValuesM.style.height = maxHeight / 3;
            }
            maxHeight -= areaValuesM.offsetHeight
        }
        if (areaClose) {
            maxHeight -= 30;
            bReDraw2 = false;
        }
        if (areaValues) {
            areaValues.style.height = maxHeight;
            if (isMozilla()) {
                window.resizeBy(0, 1);
                window.resizeBy(0, -1);
            }
        }
        setTimeout("bReDraw2 = true;", 1000);
    }
}
function fixCustomizePage()
{
    if (bReDraw)
    {
        bReDraw = false;
        var maxHeight = document.body.offsetHeight - 50;
        var areaTop = document.getElementById("area_top");
        var areaTitle = document.getElementById("area_title");
        var areaValues = document.getElementById("areaValues");
        var area_Table = document.getElementById("area_Table");
        var areaNav = document.getElementById("area_bottom");
        var areaInfo = document.getElementById("area_Info");
        if (areaTop) {
            maxHeight -= areaTop.offsetHeight;
        }
        if (areaTitle) {
            maxHeight -= areaTitle.offsetHeight;
        }
        if (areaNav) {
            maxHeight -= areaNav.offsetHeight;
        }
        if (areaInfo) {
            maxHeight -= 2 * areaInfo.offsetHeight;
        }
        if (areaValues) {
            areaValues.style.height = maxHeight;
            if (isMozilla()) {
                window.resizeBy(0, 1);
                window.resizeBy(0, -1);
            }
        }
        if (area_Table) {
            area_Table.style.height = area_Table.offsetHeight - 30;
            if (isMozilla()) {
                window.resizeBy(0, 1);
                window.resizeBy(0, -1);
            }
        }
        setTimeout("bReDraw = true;", 1000);
    }
}

bClose = false;

function alertClose(error) {
    alert(error);
    if (bClose) {
        if (window.parent != null && window.parent != window) {
            window.parent.close();
        } else {
            window.close();
        }
    }
}
function setClose(status) {
    bClose = status;
}