var iCurrentEvent = null;
var iCurrentMarket = null;

// four scenarios for adding items to the custom menu:

// scenario 1:
// you add a new event
// outcome 1:
// you update the current event number to the new event and set the current market number to null

// scenario 2:
// you add a new market and it's parent event as well
// outcome 2:
// you update the current event number to the new event and set the current market number to null

// scenario 3:
// you add a new market and it's parent event is already there but not selected
// outcome 3:
// you set the current event number to null and update the current market number to the new market

// scenario 4:
// you add a new market and it's parent event is already there and is selected
// outcome 4:
// keep the current event number and set the current market number to null

function addToMyMarket(bPaintToScreen, iID, iParentID, sName, bHasMarket, iMarketType, iBetDelay, iInterfaceID, iStartTime, RegionID) {
    //   alert("addToMyMarket");
    var iNewEvent = null;
    var iNewMarket = null;
    var iCounterC;
    if (bHasMarket == 2) {
        if (checkCustomExistance(iParentID, mySkeletonArray) == null) { // check if parent event exists within custom menu
            for ( iCounterC = 0; iCounterC < allSkeletonArray.length; iCounterC++) {
                if (iParentID == allSkeletonArray[iCounterC][ID]) {
                    iNewEvent = orderMyEvents(mySkeletonArray.length, allSkeletonArray[iCounterC][ID], allSkeletonArray[iCounterC][PARENT_ID], allSkeletonArray[iCounterC][NAME], allSkeletonArray[iCounterC][HAS_MARKET]);
                }
            }
        }
        if (checkCustomExistance(iID, myMarketsArray) == null) { // check if market exists within custom menu
            for ( iCounterC = 0; iCounterC < allSkeletonArray.length; iCounterC++) {
                if (iParentID == allSkeletonArray[iCounterC][ID]) {
                    iNewMarket = orderMyMarkets(iID, iParentID, sName, bHasMarket, iMarketType, iBetDelay, iInterfaceID, iStartTime, RegionID);
                }
            }
        }
    }
    else {
        if (checkCustomExistance(iID, mySkeletonArray) == null) {
            iNewEvent = orderMyEvents(mySkeletonArray.length, iID, iParentID, sName, bHasMarket);
        }
    }

    if ((iNewEvent != null && iNewMarket == null) || (iNewEvent != null && iNewMarket != null)) { // scenarios 1 and 2
        iCurrentEvent = iNewEvent;
        iCurrentMarket = null;
    }

    if (iNewEvent == null && iNewMarket != null && checkCustomExistance(iParentID, mySkeletonArray) != iCurrentEvent) { // scenario 3
        iCurrentEvent = null;
        iCurrentMarket = iNewMarket;
    }
    if (iNewEvent == null && iNewMarket != null && checkCustomExistance(iParentID, mySkeletonArray) == iCurrentEvent) { // scenario 4
        iCurrentMarket = null;
    }
    if (bPaintToScreen) {
        buildMyCustomisedMarket();
    }
}

function checkCustomExistance(iID, oArrayName) {
    var bWhereInCustomMenu = null;
    var oArrayObject = eval(oArrayName);
    for (var iCounterA = 0; iCounterA < oArrayObject.length; iCounterA++) {
        if (iID == oArrayObject[iCounterA][ID]) {
            bWhereInCustomMenu = iCounterA;
        }
    }
    return bWhereInCustomMenu;
}

function orderMyEvents(iPosition, iID, iParentID, sName, bHasMarket) {
    var iReturnPosition = null;
    iReturnPosition = manipulateArray('insert', '', iPosition, 'mySkeletonArray', iID, iParentID, sName, bHasMarket);
    return iReturnPosition;
}

function orderMyMarkets(iID, iParentID, sName, bHasMarket, iMarketType, iBetDelay, iInterfaceID, iStartTime, RegionID) {
    var iPosition = null;
    var iReturnPosition = null;
    var bFoundSibling = false;
    for (var iCounterD = 0; iCounterD < myMarketsArray.length; iCounterD++) {
        if (iParentID == myMarketsArray[iCounterD][PARENT_ID]) {
            bFoundSibling = true;
        }
        if (iParentID != myMarketsArray[iCounterD][PARENT_ID] && bFoundSibling && iPosition == null) {
            iPosition = iCounterD;
        }
    }
    if (iPosition == null) {
        iPosition = myMarketsArray.length;
    }
    iReturnPosition = manipulateArray('insert', '', iPosition, 'myMarketsArray', iID, iParentID, sName, bHasMarket, iMarketType, iBetDelay, iInterfaceID, iStartTime, RegionID);
    return iReturnPosition;
}

function orderItems(sDirection) {
    switch (sDirection) {
        case "up" :
            if (iCurrentEvent != null && iCurrentEvent > 0) {
                manipulateArray('move', 'up', iCurrentEvent, 'mySkeletonArray');
                iCurrentEvent--;
            }
            if (iCurrentEvent == null && iCurrentMarket != null && iCurrentMarket != 0 && myMarketsArray[iCurrentMarket - 1][PARENT_ID] == myMarketsArray[iCurrentMarket][PARENT_ID]) {
                manipulateArray('move', 'up', iCurrentMarket, 'myMarketsArray');
                iCurrentMarket--;
            }
            break;
        case "down" :
            if (iCurrentEvent != null && iCurrentEvent < mySkeletonArray.length - 1) {
                manipulateArray('move', 'down', iCurrentEvent, 'mySkeletonArray');
                iCurrentEvent++;
            }
            if (iCurrentEvent == null && iCurrentMarket != null && iCurrentMarket != myMarketsArray.length - 1 && myMarketsArray[iCurrentMarket + 1][PARENT_ID] == myMarketsArray[iCurrentMarket][PARENT_ID]) {
                manipulateArray('move', 'down', iCurrentMarket, 'myMarketsArray');
                iCurrentMarket++;
            }
            break;
        case "delete" :
            if (iCurrentEvent != null) {

                if (myMarketsArray.length > 0) {
                    manipulateArray('delete', '', iCurrentMarket, 'myMarketsArray');
                }
                manipulateArray('delete', '', iCurrentEvent, 'mySkeletonArray');
                if (iCurrentEvent >= mySkeletonArray.length) {
                    iCurrentEvent--;
                }
                if (iCurrentEvent < 0) {
                    iCurrentEvent = null;
                }
            }
            if (iCurrentEvent == null && iCurrentMarket != null) {
                var iTempCurrentMarketParentID = myMarketsArray[iCurrentMarket][PARENT_ID];
                manipulateArray('delete', '', iCurrentMarket, 'myMarketsArray');
                var bChangeEvent = false;
                if (iCurrentMarket >= myMarketsArray.length) {
                    if (myMarketsArray.length == 0) {
                        bChangeEvent = true;
                    }
                    else {
                        if (myMarketsArray[iCurrentMarket - 1][PARENT_ID] == iTempCurrentMarketParentID) {
                            iCurrentMarket--;
                        }
                        else {
                            bChangeEvent = true;
                        }
                    }
                }
                else {
                    if (myMarketsArray.length == 0) {
                        bChangeEvent = true;
                    }
                    else {
                        if (myMarketsArray[iCurrentMarket][PARENT_ID] != iTempCurrentMarketParentID && myMarketsArray[iCurrentMarket - 1][PARENT_ID] == iTempCurrentMarketParentID) {
                            iCurrentMarket--;
                        }
                        else if (myMarketsArray[iCurrentMarket][PARENT_ID] != iTempCurrentMarketParentID && myMarketsArray[iCurrentMarket - 1][PARENT_ID] != iTempCurrentMarketParentID) {
                            bChangeEvent = true;
                        }
                    }
                }
                if (bChangeEvent) {
                    for (var iCounterK = 0; iCounterK < mySkeletonArray.length; iCounterK++) {
                        if (iTempCurrentMarketParentID == mySkeletonArray[iCounterK][ID]) {
                            iCurrentMarket = null;
                            iCurrentEvent = iCounterK;
                            orderItems('delete');
                        }
                    }
                }
            }
            break;
    }
    buildMyCustomisedMarket();
}


function manipulateArray(sOperationType, sOperationDirection, iPosition, oArrayName, iID, iParentID, sName, bHasMarket, iMarketType, iBetDelay, iInterfaceID, iStartTime, RegionID) {
    var iCurrentPosition = null;
    switch (sOperationDirection) {
        case "up" :
            var oArrayObject = eval(oArrayName);
            var iSwapPosition = iPosition - 1;
            break;
        case "down" :
            var oArrayObject = eval(oArrayName);
            var iSwapPosition = iPosition + 1;
            break;
    }
    switch (sOperationType) {
        case "insert" :
            var bInsertionComplete = false;
            var oArrayObject = eval(oArrayName);
            oArrayObject.length++;
            var oNewArrayObject = new Array(oArrayObject.length);
            for (var iCounterE = 0; iCounterE < oArrayObject.length; iCounterE++) {
                if (iCounterE != iPosition) {
                    if (!bInsertionComplete) {
                        oNewArrayObject[iCounterE] = new Array();
                        for (var iCounterL = 0; iCounterL < oArrayObject[0].length; iCounterL++) {
                            oNewArrayObject[iCounterE][iCounterL] = oArrayObject[iCounterE][iCounterL];
                        }
                    }
                    else {
                        oNewArrayObject[iCounterE] = new Array();
                        for (var iCounterL = 0; iCounterL < oArrayObject[0].length; iCounterL++) {
                            oNewArrayObject[iCounterE][iCounterL] = oArrayObject[iCounterE - 1][iCounterL];
                        }
                    }
                }
                else {
                    oNewArrayObject[iCounterE] = new Array();
                    for (var iCounterL = 0; iCounterL < arguments.length - 4; iCounterL++) {
                        oNewArrayObject[iCounterE][iCounterL] = arguments[iCounterL + 4];
                    }
                    bInsertionComplete = true;
                }
            }
            for (var iCounterF = 0; iCounterF < oNewArrayObject.length; iCounterF++) {
                oArrayObject[iCounterF] = oNewArrayObject[iCounterF];
            }
            iCurrentPosition = iPosition;
            break;
        case "move" :
            var oArrayObject = eval(oArrayName);
            var oTempArray = new Array();
            for (var iCounterL = 0; iCounterL < oArrayObject[0].length; iCounterL++) {
                oTempArray[iCounterL] = oArrayObject[iSwapPosition][iCounterL];
                oArrayObject[iSwapPosition][iCounterL] = oArrayObject[iPosition][iCounterL];
                oArrayObject[iPosition][iCounterL] = oTempArray[iCounterL];
            }
            break;
        case "delete" :
            var oArrayObject = eval(oArrayName);
            var oNewArrayObject = new Array();
            if (iCurrentEvent != null && iCurrentMarket == null && oArrayName == "myMarketsArray") {
                var iIdForRemoval = mySkeletonArray[iCurrentEvent][ID];
                var iArrayDimension = PARENT_ID;
            }
            else {
                var iIdForRemoval = oArrayObject[iPosition][ID];
                var iArrayDimension = ID;
            }
            for (var iCounterH = 0; iCounterH < oArrayObject.length; iCounterH++) {
                if (iIdForRemoval != oArrayObject[iCounterH][iArrayDimension]) {
                    oNewArrayObject[oNewArrayObject.length] = new Array();
                    for (var iCounterL = 0; iCounterL < oArrayObject[0].length; iCounterL++) {
                        oNewArrayObject[oNewArrayObject.length - 1][iCounterL] = oArrayObject[iCounterH][iCounterL];
                    }
                }
            }
            if (oNewArrayObject.length != 0) {
                for (var iCounterJ = 0; iCounterJ < oNewArrayObject.length; iCounterJ++) {
                    oArrayObject.length = oNewArrayObject.length;
                    oArrayObject[iCounterJ] = oNewArrayObject[iCounterJ];
                }
            }
            else {
                oArrayObject.length = 0;
            }
            break;
    }
    return iCurrentPosition;
}

function highlightItems(iWhichLevel, sWhichType)
{
    switch (sWhichType)
            {
        case "event" :
            iCurrentEvent = iWhichLevel;
            break;
        case "market" :
            iCurrentEvent = null;
            iCurrentMarket = iWhichLevel;
            break;
    }
    for (var iCounterM = 0; iCounterM < mySkeletonArray.length; iCounterM++)
    {
        if (iCounterM != iCurrentEvent)
        {
            document.getElementById("eventLevel" + iCounterM).className = "MenuEvent";
        }
        else
        {
            document.getElementById("eventLevel" + iCounterM).className = "MenuEventDown";
        }
    }

    for (var iCounterN = 0; iCounterN < myMarketsArray.length; iCounterN++)
    {
        if ((iCurrentEvent == null && iCounterN != iCurrentMarket) || (iCurrentEvent != null && myMarketsArray[iCounterN][PARENT_ID] != mySkeletonArray[iCurrentEvent][ID]))
        {
            if (document.getElementById("marketLevel" + iCounterN))
            {
                document.getElementById("marketLevel" + iCounterN).className = "MenuMarket";
            }
            if (document.getElementById("marketLevel" + iCounterN + "InPlay"))
            {
                document.getElementById("marketLevel" + iCounterN + "InPlay").className = "MenuMarket";
            }
        }
        else
        {
            if (document.getElementById("marketLevel" + iCounterN))
            {
                document.getElementById("marketLevel" + iCounterN).className = "MenuMarketDown";
            }
            if (document.getElementById("marketLevel" + iCounterN + "InPlay"))
            {
                document.getElementById("marketLevel" + iCounterN + "InPlay").className = "MenuMarketDown";
            }
        }
    }

}

function buildMyCustomisedMarket()
{
    var a = [];
    a.push(sContainerArray[1]);
    for (var iCounterB = 0; iCounterB < mySkeletonArray.length; iCounterB++)
    {
        if (iCounterB == iCurrentEvent)
        {
            var sClassName = "MenuEventDown";
        }
        else
        {
            var sClassName = "MenuEvent";
        }

        a.push("<tr>\n");
        a.push("<td width=\"100%\" colspan=\"3\" class=\"MenuBorder\">\n");
        a.push("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
        a.push("<tr>\n");
        a.push("<td class=\"" + sClassName + "\" name=\"eventLevel" + iCounterB + "\" id=\"eventLevel" + iCounterB + "\" ");
        a.push("onmouseover=\"if (canChange(" + iCounterB + ",'event')) { this.className='MenuEventOver'; }\" ");
        a.push("onmouseout=\"if (canChange(" + iCounterB + ",'event')) { this.className='MenuEvent'; }\" ");
        a.push("onmousedown=\"highlightItems(" + iCounterB + ",'event');\" width=\"100%\">" + mySkeletonArray[iCounterB][NAME] + "</td>\n");
        a.push("</tr>\n");
        a.push("</table>\n");
        a.push("</td>\n");
        a.push("</tr>\n");

        for (var iCounterG = 0; iCounterG < myMarketsArray.length; iCounterG++)
        {
            if (mySkeletonArray[iCounterB][ID] == myMarketsArray[iCounterG][PARENT_ID])
            {
                var betType = getBetTypeIcon(myMarketsArray[iCounterG][MARKET_TYPE], myMarketsArray[iCounterG][INTERFACE_ID]);
                var inPlay = getEventStatusIcon(myMarketsArray[iCounterG][EVENT_STATE]);
                if (iCounterG == iCurrentMarket || iCounterB == iCurrentEvent)
                {
                    var sClassName = "MenuMarketDown";
                }
                else
                {
                    var sClassName = "MenuMarket";
                }

                if (inPlay != "")
                {
                    a.push("<tr class=\"" + sClassName + "\" name=\"marketLevel" + iCounterG + "\" id=\"marketLevel" + iCounterG + "\" ");
                    a.push("onMouseOver=\"if (canChange(" + iCounterG + ",'market')) { this.className='MenuMarketOver'; }\" ");
                    a.push("onMouseOut=\"if (canChange(" + iCounterG + ",'market')) { this.className='MenuMarket'; }\" ");
                    a.push("onclick=\"highlightItems(" + iCounterG + ",'market');\">\n");
                    a.push("<td width=\"100%\" class='MenuMarketBottomBorder' nowrap>" + myMarketsArray[iCounterG][NAME] + "</td>\n");
                    a.push("<td class=\"MenuIconSpacer\">" + inPlay + "</td>\n");
                    a.push("<td class=\"MenuIcon\" nowrap>" + betType + "</td>\n");
                    a.push("</tr>\n");
                }
                else
                {
                    a.push("<tr class=\"" + sClassName + "\" name=\"marketLevel" + iCounterG + "\" id=\"marketLevel" + iCounterG + "\" ");
                    a.push("onMouseOver=\"if (canChange(" + iCounterG + ",'market')) { this.className='MenuMarketOver'; }\" ");
                    a.push("onMouseOut=\"if (canChange(" + iCounterG + ",'market')) { this.className='MenuMarket'; }\" ");
                    a.push("onClick=\"highlightItems(" + iCounterG + ",'market');\">\n");
                    a.push("<td width=\"100%\" class='MenuMarketBottomBorder' nowrap>" + myMarketsArray[iCounterG][NAME] + "</td>\n");
                    a.push("<td class=\"MenuIconSpacer\">&nbsp;</td>\n");
                    a.push("<td class=\"MenuIcon\">" + betType + "</td>\n");
                    a.push("</tr>\n");
                }
            }
        }
    }
    a.push(sContainerArray[2]);
    domWrite('myMarketsTreeContainer', a.join(''));
}

function canChange(iPosition, sWhichType)
{
    var bCanChange = false;
    switch (sWhichType)
            {
        case "event" :
            if (iPosition != iCurrentEvent)
            {
                bCanChange = true;
            }
            break;
        case "market" :
            if (iCurrentEvent == null && iPosition != iCurrentMarket)
            {
                bCanChange = true;
            }
            if (iCurrentEvent != null && myMarketsArray[iPosition][PARENT_ID] != mySkeletonArray[iCurrentEvent][ID])
            {
                bCanChange = true;
            }
            break;
    }
    return bCanChange;
}

function addAllToMyMarket()
{
    var bPaintIt = false;
    for (var iCounterZ = 0; iCounterZ < oAddAllArray.length; iCounterZ++)
    {
        var oTempArray = oAddAllArray[iCounterZ].split(",");
        if (iCounterZ == oAddAllArray.length - 1)
        {
            bPaintIt = true;
        }
        if (oTempArray.length == 9)
        {
            addToMyMarket(bPaintIt, oTempArray[0], oTempArray[1], oTempArray[2], oTempArray[3], oTempArray[4], oTempArray[5], oTempArray[6], oTempArray[7], oTempArray[8]);
        }
        else
        {
            addToMyMarket(bPaintIt, oTempArray[0], oTempArray[1], oTempArray[2], oTempArray[3]);
        }
    }
}

function removeAllFromMyMarket()
{
    mySkeletonArray.length = 0;
    myMarketsArray.length = 0;
    mySkeletonString = "";
    myMarketsString = "";
    myMeatString = "";
    buildMyCustomisedMarket();
}

function prepareOnSave() {
    var oTempArray = new Array(mySkeletonArray.length);
    for (var iCounterY = 0; iCounterY < mySkeletonArray.length; iCounterY++) {
        oTempArray[iCounterY] = mySkeletonArray[iCounterY].join("~");
    }
    mySkeletonString = oTempArray.join("|");
    //alert("mySkeletonString="+mySkeletonString);

    var oTempArray = new Array(myMarketsArray.length);
    for (var iCounterY = 0; iCounterY < myMarketsArray.length; iCounterY++) {
        oTempArray[iCounterY] = myMarketsArray[iCounterY].join("~");
    }
    myMarketsString = oTempArray.join("|");
    //alert("myMarketsString="+ myMarketsString);

    try {
        if (oPointerHandle) {
            oPointerHandle.mySkeletonStringTemp = mySkeletonString;
            oPointerHandle.myMarketsStringTemp = myMarketsString;
        }
    } catch (x) {}
}


function saveMyMarkets() {
    prepareOnSave();
    document.forms[0].mySkeletonString.value = getMyEventsAndMarkets();
    document.forms[0].submit();
}


function stripSkeleton()
{
    mySkeletonArray = mySkeletonString.split("|");
    for (var i = 0; i < mySkeletonArray.length; i++)
    {
        mySkeletonArray[i] = mySkeletonArray[i].split("~");
    }
    mySkeletonString = "";
    var a = [];
    var oTempArray = new Array();
    for (var iCounter = 0; iCounter < mySkeletonArray.length; iCounter++)
    {
        if (mySkeletonArray[iCounter].length == 4)
        {
            a.push(mySkeletonArray[iCounter].join("~") + "|");
        }
    }
    mySkeletonString = a.join('');
}

function onUnload() {
    try {
        if (opener && !opener.closed) {
            opener.top.frames['marketPage'].oPointerHandle = null;
        }
    } catch (x) {}
 }

    function initSizes() {
        if (document.getElementsByTagName && document.all) {
            fixCustomizeMarketPage();
            document.body.onresize = fixCustomizeMarketPage;
        } else if (window.addEventListener) {
            fixCustomizeMarketPage();
            window.addEventListener("resize", fixCustomizeMarketPage, false);
         }
    }

function initialise() {
    ajustStyles();
    if (fixCustomizeMarketPage) {
      fixCustomizeMarketPage();
        if( IE ) {
            document.body.onresize = fixCustomizeMarketPage;
        } else if (window.addEventListener) {
            window.addEventListener("resize", fixCustomizeMarketPage, false);
        }
    }

    if (myMeatString != "")
    {
        splitSkeleton("meat");
    }

    createAllMarketMenu();

    if (mySkeletonString != "")
    {
        stripSkeleton();
        splitSkeleton('my');
        buildMyCustomisedMarket();
    }
    initSizes();
}