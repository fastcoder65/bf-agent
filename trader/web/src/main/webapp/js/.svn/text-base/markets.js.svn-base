
var iCurrentMarketID = null;
var AUTOREFRESH = "auto";
var aHTML = new Array();

var myMarketsTreeContainer  = "myMarketsTreeContainer";
var allMarketsTreeContainer = "allMarketsTreeContainer";




// ARRAY CONTAINING THE OPENING AND CLOSING TAGS FOR EACH CONTAINER **********
var sContainerArray = new Array(2);
sContainerArray[1] = "<table width='100%' border='0' cellspacing='0' cellpadding='0' class='MainBorder'>";
sContainerArray[2] = "</table>\n";

//  ARRAY FOR THE MOVE ALL FUNCTION (FOR CUSTOMISE MENU) **********
var oAddAllArray = new Array();

//  ARRAYS CONTAINING THE EVENTS AND MARKETS INFORMATION **********
var allSkeletonArray = new Array();
var mySkeletonArray = new Array();
var myMarketsArray = new Array();
var meatArray = new Array();

//  ARRAYS CONTAINING THE PATH ITEMS **********
var allPathArray = new Array();
var myPathArray = new Array();

//  COUNTERS THAT INDICATE THE ID OF THE CURRENT SELECTED ITEM FOR EACH TYPE OF MARKET **********
var currentAllItem = 1;
var currentMyItem = 0;

var m_meatArray = "";
var m_skeletonArray = "";

var bInternalRequest = false;
var iRequestFromOutsideParentID = null;

var iCouponParentID;

var oPathArray = new Array();
var oIDArray = new Array();

var bNotMeatArray = false;

//var iCurrentBetState; // inPlay | not inPlay

var iFlushMeatCacheTimer = (iMarketRefreshTimeout != null && iMarketRefreshTimeout > 0)? 60 : 0;

// how long in seconds between flushing of the meat cache
var oTraderFlushMeatTimeout = null;
var oFlushMeatTimeout = null;

function assessDom()
{
    if (document.getElementById)
    {
        if (document.getElementById("assessDomNode"))
        {
            if (document.getElementById("assessDomNode").cloneNode(true))
            {
               top.canAccessDom = true;
            }
        }
    }
}

function traderRefreshMarkets() {
    flushMeatCache();
    var fCustomiseFlag = eval("fCustomiseFlag");
    if (!fCustomiseFlag) {
       var _currentEventId = ('sc1' == previoustab )?currentAllItem:currentMyItem;
       var sWhere = ('sc1' == previoustab )?"currentAllItem":"currentMyItem";
        if (iCurrentMarketID != null) {
            var _currentMarketId = iCurrentMarketID;
            iCurrentMarketID = null;
            //    alert("_currentEventId("+sWhere +")="+ _currentEventId + "_currentMarketId="+ _currentMarketId);
            goMarket(
                    _currentMarketId, // my EventId
                    null,
                    true, // should be true for opened windows
                    _currentEventId,
                    null,
                    AUTOREFRESH);
        }
    }
}

function setTraderFlushMeatTimeout() {
  if (iFlushMeatCacheTimer > 0)
    oTraderFlushMeatTimeout = setTimeout("traderRefreshMarkets()", iFlushMeatCacheTimer * 1000);
}

function refreshMarkets() {
  if (iFlushMeatCacheTimer > 0) {
    flushMeatCache();
    oFlushMeatTimeout = setTimeout("refreshMarkets()", iFlushMeatCacheTimer * 1000);
  }
}

function checkMeatCache(iParentID) {
    var bMarketIsInCache = false;
    for (var i = iStartupMeatCounter; i < meatArray.length; i++) {
        if (meatArray[i][PARENT_ID] == iParentID) {
            bMarketIsInCache = true;
        }
    }
    return bMarketIsInCache || bNotMeatArray;
}

function meatLoaded(strArrayName, sParentID, layerName, strMenuPathArrayName, menuPathLayer) {
    //debug();
    if (m_meatArray != "") {
        splitSkeleton('meat');
        m_meatArray = "";
        switch (layerName)
                {
            case allMarketsTreeContainer :
                if (currentAllItem == sParentID)
                {
                    writeItems(strArrayName, sParentID, layerName, strMenuPathArrayName, menuPathLayer, false);
                }
                break;

            case myMarketsTreeContainer :
                if (currentMyItem == sParentID)
                {
                 //   alert("currentMyItem="+ currentMyItem);
                    writeItems(strArrayName, sParentID, layerName, strMenuPathArrayName, menuPathLayer, false);
                }
                break;
        }
    }
    else
    {
        writeItems(strArrayName, iCouponParentID, layerName, strMenuPathArrayName, menuPathLayer, false);
        iCouponParentID = null;
    }
}


function getMenuItems(strArrayName, iParentID, layerName, strMenuPathArrayName, menuPathLayer, bAddToMenuPath, bMyBets)
{
    oAddAllArray = new Array(0);
    var bHasMarket = false;
    aHTML.length = 0;
//    debug();
    if (iParentID != 0 && layerName == myMarketsTreeContainer)
    {
        strArrayName = "allSkeletonArray";
    }

    var arrayName = eval(strArrayName);

    aHTML[aHTML.length] = sContainerArray[1];
    var i;
    for ( i = 0; i < arrayName.length; i++) {
        if (layerName == myMarketsTreeContainer) {
            if (arrayName[i][ID] == iParentID && bAddToMenuPath == "true") {
                addToMenuPath(i, arrayName, 'mySkeletonArray', strMenuPathArrayName, menuPathLayer, layerName);
            }

            if (i < mySkeletonArray.length && iParentID == 0) {
                if (mySkeletonArray[i][HAS_MARKET] == ITEM_HAS_EVENTS) {
                    for (var inew = 0; inew < meatArray.length; inew++) {
                        if (meatArray[inew][ID] == mySkeletonArray[i][ID]) {
                            aHTML[aHTML.length] = buildMarketsHTML('meatArray', inew, layerName, strMenuPathArrayName, menuPathLayer, bMyBets);
                            break;
                        }
                    }
                }
                else {
                    aHTML[aHTML.length] = buildEventsHTML(strArrayName, i, layerName, strMenuPathArrayName, menuPathLayer);
                }
            }

            if (iParentID != 0) {
                if (allSkeletonArray[i][PARENT_ID] == iParentID) {
                    aHTML[aHTML.length] = buildEventsHTML(strArrayName, i, layerName, strMenuPathArrayName, menuPathLayer);
                }
            }

            if (arrayName[i][ID] == iParentID && iParentID != 0) {
                if (arrayName[i][HAS_MARKET] == ITEM_IS_LEAF) {
                    bHasMarket = true;
                }
            }
        }
        else {
            if (arrayName[i][ID] == iParentID && bAddToMenuPath == "true") {
                addToMenuPath(i, arrayName, strArrayName, strMenuPathArrayName, menuPathLayer, layerName);
            }
            if ((arrayName[i][PARENT_ID] == iParentID)) {
                aHTML[aHTML.length] = buildEventsHTML(strArrayName, i, layerName, strMenuPathArrayName, menuPathLayer);
            }
            if (arrayName[i][ID] == iParentID && iParentID != 0) {
                if (arrayName[i][HAS_MARKET] == ITEM_IS_LEAF) {
                    bHasMarket = true;
                }
            }
        }
    }
    if (bHasMarket) {
        pathArrayName = eval(strMenuPathArrayName);
        if (pathArrayName.length >= 2) {
            iCouponParentID = pathArrayName[pathArrayName.length - 2][ID];
        }

        if (checkMeatCache(iParentID)){
            if (bNotMeatArray){
                aHTML[aHTML.length] = "\n<tr>\n<td colspan=\"3\" class=\"MenuItemLoading\">" + sNoMeat + "</td>\n</tr>\n";
            }
            else {
                for (i = iStartupMeatCounter; i < meatArray.length; i++) {
                    if (meatArray[i][PARENT_ID] == iParentID) {
                        aHTML[aHTML.length] = buildMarketsHTML('meatArray', i, layerName, strMenuPathArrayName, menuPathLayer, bMyBets);
                    }
                }
            }
        }
        else {
            aHTML[aHTML.length] = "\n<tr>\n<td colspan=\"3\" class=\"MenuItemLoading\">" + sLoadingMarketText + "</td>\n</tr>\n";
            getSpecificItemFromServer(strArrayName, iParentID, layerName, strMenuPathArrayName, menuPathLayer, bAddToMenuPath);
        }
    }
    aHTML[aHTML.length] = sContainerArray[2];
    return aHTML.join("");
}

function splitSkeleton(sWhichArray) {
    var i;
    switch (sWhichArray) {
        case "all" :
            allSkeletonArray = skeletonString.substring(0, skeletonString.length - 1).split("|");
            for ( i = 0; i < allSkeletonArray.length; i++) {
                allSkeletonArray[i] = allSkeletonArray[i].split("~");
            }
            break;

        case "my" :
            if (mySkeletonString != "")
            {
                if (mySkeletonString.substring(mySkeletonString.length - 1, mySkeletonString.length) == "|")
                {
                    mySkeletonArray = mySkeletonString.substring(0, mySkeletonString.length - 1).split("|");
                }
                else
                {
                    mySkeletonArray = mySkeletonString.split("|");
                }
                for ( i = 0; i < mySkeletonArray.length; i++)
                {
                    mySkeletonArray[i] = mySkeletonArray[i].split("~");
                }
            }
            if (myMarketsString != "")
            {
                if (myMarketsString.substring(myMarketsString.length - 1, myMarketsString.length) == "|")
                {
                    myMarketsArray = myMarketsString.substring(0, myMarketsString.length - 1).split("|");
                }
                else
                {
                    myMarketsArray = myMarketsString.split("|");
                }
                for ( i = 0; i < myMarketsArray.length; i++)
                {
                    myMarketsArray[i] = myMarketsArray[i].split("~");
                }
            }
            break;
        case "meat" :

            if (m_meatArray != "")
            {
                myMeatString += m_meatArray;
            }
            //   alert("splitSkeleton:["+myMeatString+"]");
            if (myMeatString != "")
            {
                splitMeat(myMeatString)
            }

            break;
    }
    synchroniseCache();
}

function splitMeat(aMeatString) {

  var oTempArray = new Array();

    if (aMeatString.substring(aMeatString.length - 1, aMeatString.length) == "|")
    {
        oTempArray = aMeatString.substring(0, aMeatString.length - 1).split("|");
    }
    else
    {
        oTempArray = aMeatString.split("|");
    }
    for (var i = 0; i < oTempArray.length; i++)
    {
        meatArray[i] = oTempArray[i].split("~");
    }
}


// ********** SYNCHRONISE THE MEAT **********
function synchroniseCache() {
    try {

    if (oPointerHandle)
    {
        if (!oPointerHandle.closed)
        {
            if (oPointerHandle.myMeatString)
            {
                if (oPointerHandle.myMeatString != myMeatString)
                {
                    oPointerHandle.myMeatString = myMeatString;
                    oPointerHandle.splitSkeleton("meat");
                }
            }
        }
        else
        {
            oPointerHandle = null;
        }
     }
    } catch (x) {
       alert(sOpenAgainMessage);
       parent.close();
     }
}

function flushMeatCache() {
    if (!fCustomiseFlag) {
        meatArray.length = 0;
        myMeatString = myMarketsString;
        splitSkeleton('meat');

    }
}

function getUpperLevelId(levelId, _marketItems) {
    var selectedItem = null;
    for (var i = 0; i < _marketItems.length; i++)
    {
        if (_marketItems[i][0] == levelId)
        {
            var parentLevelId = _marketItems[i][1];
            selectedItem = _marketItems[i];
            break;
        }
    }
    return selectedItem;
}

function getSpecificItemFromServer(strArrayName, iParentID, layerName, strMenuPathArrayName, menuPathLayer, bAddToMenuPath) {
    var sURL = sLoadMenuNodesAction
            + "?strArrayName=" + strArrayName
            + "&marketId=" + iParentID
            + "&layerName=" + layerName
            + "&strMenuPathArrayName=" + strMenuPathArrayName
            + "&menuPathLayer=" + menuPathLayer;

    var o = fCustomiseFlag ? parent : window;

    if (layerName == myMarketsTreeContainer)
    {
        var _targetFrame = o.frames["myMarketEventContainer"];
        _targetFrame.location.replace(sURL);
    }
    else if (layerName == allMarketsTreeContainer)
    {
        var _targetFrame = o.frames["allMarketEventContainer"];
        _targetFrame.location.replace(sURL);
    }
}

// function that builds the table row code for events which is written to the screen

function buildEventsHTML(
        strArrayName,
        iIdx,
        layerName,
        strMenuPathArrayName,
        menuPathLayer) {

    arrayNewTempName = eval(strArrayName);
    if (!fCustomiseFlag) {
        return "<tr onclick=\"writeItems('"
                + strArrayName + "',"
                + arrayNewTempName[iIdx][ID] + ",'"
                + layerName + "','"
                + strMenuPathArrayName + "','" + menuPathLayer + "','true')\">\n"
                + "<td width=\"100%\" colspan=\"3\" class=\"MenuBorder\">\n"
                + "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n"
                + "<tr>\n"
                + "<td class=\"MenuEvent\" "
                + "onmouseover=\"this.className='MenuEventOver';\" "
                + "onmouseout=\"this.className='MenuEvent';\" "
                + "onMouseDown=\"this.className='MenuEventDown';\" "
                + "onMouseUp=\"this.className='MenuEvent';\" width=\"100%\">"
                + arrayNewTempName[iIdx][NAME] + "</td>\n"
                + "</tr>\n"
                + "</table>\n"
                + "</td>\n</tr>\n";
    } else {
        oAddAllArray[oAddAllArray.length] = arrayNewTempName[iIdx][ID] + ","
                + arrayNewTempName[iIdx][PARENT_ID] + ","
                + arrayNewTempName[iIdx][NAME] + ","
                + arrayNewTempName[iIdx][HAS_MARKET];

        return "<tr onclick=\"writeItems('" + strArrayName + "',"
                + arrayNewTempName[iIdx][ID] + ",'"
                + layerName + "','"
                + strMenuPathArrayName + "','"
                + menuPathLayer
                + "','true')\">\n"
                + "<td width=\"100%\" colspan=\"3\" class=\"MenuBorder\">\n"
                + "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n"
                + "<tr> "
                + "<td class=\"MenuEvent\" width=\"100%\" "
                + "onMouseOver=\"this.className='MenuEventOver';\" "
                + "onMouseOut=\"this.className='MenuEvent';\" "
                + "onMouseDown=\"this.className='MenuEventDown';\" "
                + "onMouseUp=\"this.className='MenuEvent';\" >"
                + arrayNewTempName[iIdx][NAME]
                + "</td>"
                + "</tr>\n"
                + "</table>\n"
                + "</td>"
                + "<td class=\"MenuCustomiseArrow\" "
                + "onMouseOver=\"this.className='MenuCustomiseArrowOver';\" "
                + "onMouseOut=\"this.className='MenuCustomiseArrow';\" "
                + "onClick=\"addToMyMarket('true','"
                + arrayNewTempName[iIdx][ID] + "','"
                + arrayNewTempName[iIdx][PARENT_ID] + "','"
                + arrayNewTempName[iIdx][NAME].replace("'", "\\'") + "','"
                + arrayNewTempName[iIdx][HAS_MARKET]
                + "');\" width=\"17\" valign=\"middle\" align=\"center\" nowrap>"
                + "<img src=\"" + sCustomiseArrow + "\"></td>\n</tr>\n";
    }
}


// function that builds the table row code for markets which is written to the screen
function buildMarketsHTML(
        strArrayName,
        iIdx,
        layerName,
        strMenuPathArrayName,
        menuPathLayer,
        bMyBets) {

    arrayTempName = eval(strArrayName);

    switch (layerName)
            {
        case allMarketsTreeContainer :
            sLevelID = "a";
            break;
        case myMarketsTreeContainer :
            sLevelID = "m";
            break;
    }

    if (arrayTempName[iIdx][ID] == iCurrentMarketID)
    {
        var sClassName = "MenuMarketDown";
    }
    else
    {
        var sClassName = "MenuMarket";
    }
    if (!fCustomiseFlag) {
        if (getEventStatusIcon(arrayTempName[iIdx][EVENT_STATE]) != "") {

            result = "<tr class='" + sClassName + "' "
                    + "id='" + sLevelID + arrayTempName[iIdx][ID] + "' "
                    + "name='" + sLevelID + arrayTempName[iIdx][ID] + "' "
                    + "onmouseover='canHighlight(this,\"over\");' "
                    + "onmouseout='canHighlight(this,\"out\");' "
                    + "onmousedown='canHighlight(this,\"down\");' "
                    + "onmouseup='canHighlight(this,\"up\");' "
                    + "onclick=\"bInternalRequest=true;goMarket('" + arrayTempName[iIdx][ID] + "','" // iCurrentBetState=" + arrayTempName[iIdx][EVENT_STATE] + ";
                    + ITEM_IS_EVENT + "', null, '" + arrayTempName[iIdx][PARENT_ID] + "','"
                    + arrayTempName[iIdx][NAME] + "'," + ((bMyBets)? "'MM_LHM'" : "'LHM'") + ");\">\n"
                    + "<td width='100%' class='MenuMarketBottomBorder'>" + arrayTempName[iIdx][NAME] + "</td>\n"
                    + "<td class='MenuIconSpacer' nowrap>" + getEventStatusIcon(arrayTempName[iIdx][EVENT_STATE]) + "</td>\n"
                    + "<td class=\"MenuIcon\" nowrap>" + getBetTypeIcon(arrayTempName[iIdx][MARKET_TYPE], arrayTempName[iIdx][INTERFACE_ID]) + "</td>\n"
                    + "</tr>\n";

            return result;
        }
        else {
            result = "<tr class=\"" + sClassName + "\" "
                    + "id=\"" + sLevelID + arrayTempName[iIdx][ID] + "\" "
                    + "name=\"" + sLevelID + arrayTempName[iIdx][ID] + "\" "
                    + "onmouseover=\"canHighlight(this,'over');\" "
                    + "onmouseout=\"canHighlight(this,'out');\" "
                    + "onmousedown=\"canHighlight(this,'down');\" "
                    + "onmouseup=\"canHighlight(this,'up');\" "
                    + "onclick=\"bInternalRequest=true;goMarket('" + arrayTempName[iIdx][ID] + "','" // iCurrentBetState=" + arrayTempName[iIdx][EVENT_STATE] + ";
                    + ITEM_IS_EVENT + "',null,'" + arrayTempName[iIdx][PARENT_ID] + "','"
                    + arrayTempName[iIdx][NAME] + "'," + ((bMyBets)? "'MM_LHM'" : "'LHM'") + ")\">\n"
                    + "<td width='100%' class='MenuMarketBottomBorder' >" + arrayTempName[iIdx][NAME] + "</td>\n"
                    + "<td class='MenuIconSpacer' nowrap>&nbsp;</td>\n"
                    + "<td class=\"MenuIcon\" nowrap>" + getBetTypeIcon(arrayTempName[iIdx][MARKET_TYPE], arrayTempName[iIdx][INTERFACE_ID]) + "</td>\n"
                    + "</tr>\n";
            return result;
        }
    }
    else {
        oAddAllArray[oAddAllArray.length] = arrayTempName[iIdx][ID] + ","
                + arrayTempName[iIdx][PARENT_ID] + ","
                + arrayTempName[iIdx][NAME]
                + ",2,"
                + arrayTempName[iIdx][MARKET_TYPE] + ","
                + arrayTempName[iIdx][EVENT_STATE] + ","
                + arrayTempName[iIdx][INTERFACE_ID] + ","
                + arrayTempName[iIdx][START_TIME] + ","
                + arrayTempName[iIdx][REGION_ID];

        if (getEventStatusIcon(arrayTempName[iIdx][EVENT_STATE]) != "") {

            result = "<tr class=\"" + sClassName + "\" "
                    + "id=\"" + sLevelID + arrayTempName[iIdx][ID] + "\" "
                    + "name=\"" + sLevelID + arrayTempName[iIdx][ID] + "\" "
                    + "onMouseOver=\"canHighlight(this,'over');\" "
                    + "onMouseOut=\"canHighlight(this,'out');\" "
                    + "onMouseDown=\"canHighlight(this,'down');\" "
                    + "onMouseUp=\"canHighlight(this,'up');\">\n"
                    + "<td width=\"100%\" class=\"MenuMarketBottomBorder\">" + arrayTempName[iIdx][NAME] + "</td>\n"
                    + "<td class=\"MenuIconSpacer\" nowrap>" + getEventStatusIcon(arrayTempName[iIdx][EVENT_STATE]) + "</td>"
                    + "<td class=\"MenuIcon\" nowrap>" + getBetTypeIcon(arrayTempName[iIdx][MARKET_TYPE], arrayTempName[iIdx][INTERFACE_ID]) + "</td>\n"
                    + "<td class=\"MenuCustomiseArrow\" "
                    + "onMouseOver=\"this.className='MenuCustomiseArrowOver';\" "
                    + "onMouseOut=\"this.className='MenuCustomiseArrow';\" "
                    + "onClick=\"addToMyMarket('true','"
                    + arrayTempName[iIdx][ID] + "','"
                    + arrayTempName[iIdx][PARENT_ID] + "','"
                    + arrayTempName[iIdx][NAME].replace("'", "\\'") + "','2','"
                    + arrayTempName[iIdx][MARKET_TYPE] + "','"
                    + arrayTempName[iIdx][EVENT_STATE] + "','"
                    + "0" + "','"  // arayTempName[iIdx][INTERFACE_ID]
            // @todo: should be arayTempName[iIdx][INTERFACE_ID] !!!! but don't work with
                    + arrayTempName[iIdx][START_TIME] + "','"
                    + arrayTempName[iIdx][REGION_ID] + "');\" width=\"17\" valign=\"middle\" align=\"center\">"
                    + "<img src=\"" + sCustomiseArrow + "\"></a>"
                    + "</td>\n</tr>\n";
            return result;
        }
        else {
            result = "<tr class=\"" + sClassName + "\" "
                    + "id=\"" + sLevelID + arrayTempName[iIdx][ID] + "\" "
                    + "name=\"" + sLevelID + arrayTempName[iIdx][ID] + "\" "
                    + "onMouseOver=\"canHighlight(this,'over');\" "
                    + "onMouseOut=\"canHighlight(this,'out');\" "
                    + "onMouseDown=\"canHighlight(this,'down');\" "
                    + "onMouseUp=\"canHighlight(this,'up');\">\n"
                    + "<td width=\"100%\" class=\"MenuMarketBottomBorder\" colspan=\"2\">"
                    + arrayTempName[iIdx][NAME] + "</td>\n"
                    + "<td class=\"MenuIcon\" nowrap>" + getBetTypeIcon(arrayTempName[iIdx][MARKET_TYPE], arrayTempName[iIdx][INTERFACE_ID]) + "</td>\n"
                    + "<td class=\"MenuCustomiseArrow\" "
                    + "onMouseOver=\"this.className='MenuCustomiseArrowOver';\" "
                    + "onMouseOut=\"this.className='MenuCustomiseArrow';\" "
                    + "onClick=\"addToMyMarket('true','"
                    + arrayTempName[iIdx][ID] + "','"
                    + arrayTempName[iIdx][PARENT_ID] + "','"
                    + arrayTempName[iIdx][NAME].replace("'", "\\'") + "','2','"
                    + arrayTempName[iIdx][MARKET_TYPE] + "','"
                    + arrayTempName[iIdx][EVENT_STATE] + "','"
                    + arrayTempName[iIdx][INTERFACE_ID] + "','"
                    + arrayTempName[iIdx][START_TIME] + "','"
                    + arrayTempName[iIdx][REGION_ID] + "');\" width=\"17\" valign=\"middle\" align=\"center\">"
                    + "<img src=\"" + sCustomiseArrow + "\"></a>"
                    + "</td>\n</tr>\n";
            return result;
        }
//        return "";
    }
}

function canHighlight (oThisID, sBetValue ) {
    if ((iCurrentMarketID != null && oThisID.id.substring(1, oThisID.id.length) != iCurrentMarketID) || iCurrentMarketID == null || fCustomiseFlag) {
        switch (sBetValue) {
            case "over" :
                oThisID.className = "MenuMarketOver";
                break;
            case "out" :
                oThisID.className = "MenuMarket";
                break;
            case "down" :
                oThisID.className = "MenuMarketDown";
                break;
            case "up" :
                oThisID.className = "MenuMarket";
                break;
        }
    }
}

function getEventStatusIcon(iBetDelay) {
    if ( iBetDelay == EVENT_STATE_INPLAY ) return  '<img src="' + iconInPlay + '" title="'+ sInPlayEventsCaption +'">';
    else if ( iBetDelay == EVENT_STATE_MAYBE_INPLAY ) return '<img src="' + iconInPlayInactive + '" title="'+ sInPlayEventsCaption +'">';
    else return '';
}

function getBetTypeIcon(sBetType, iInterfaceID) {
    return  sBetType;
}

function goMenuRebuild(iEventID, iMarketID, bRebuild) {
    if (!bRebuild && (currentAllItem == iEventID)) {
        highlightMarket(iMarketID);
    } else {
        goMenu(iEventID, iMarketID);
    }
}

function goMenu(iEventID, iMarketID, checkEventExists) {

    var result = false;

   if (!checkEventExists) highlightMarket(iMarketID);
    if (bInternalRequest && (allPathArray.length > 0))
    {
       // alert("goMenu(bInternalRequest=true) : iEventID="+iEventID+", iMarketID="+iMarketID +", checkEventExists="+checkEventExists);
        bInternalRequest = false;
        result = true;
    }
    else
    {
        if (iRequestFromOutsideParentID && iRequestFromOutsideParentID != null && iRequestFromOutsideParentID != "")
        {
            iEventID = iRequestFromOutsideParentID;
        }

       // alert("goMenu(iRequestFromOutsideParentID=true) : iEventID="+iEventID+", iMarketID="+iMarketID +", checkEventExists="+checkEventExists);
        // look for the event in the all skeleton array
        for (var iCounterA = 0; iCounterA < allSkeletonArray.length; iCounterA++) {
            if (allSkeletonArray[iCounterA][ID] == iEventID) {

                buildPath(allSkeletonArray, iEventID);
                //if ((allPathArray.length == 0) || (allPathArray.length > 0 && allPathArray[allPathArray.length - 1][ID] != iEventID)) {
                allPathArray.length = 0;
                for (var iCounterB = oPathArray.length - 2; iCounterB >= 0; iCounterB--) {
                    addToMenuPath(oPathArray[iCounterB], allSkeletonArray, "allSkeletonArray", "allPathArray", "allMenuParents", allMarketsTreeContainer);
                }
                //}
                writeItems("allSkeletonArray", iEventID, allMarketsTreeContainer, "allPathArray", "allMenuParents", "false");
                result = true;
                break;
            }
        }
        oPathArray.length = 0;
        oIDArray.length = 0;
        bInternalRequest = false;
        iRequestFromOutsideParentID = null;
    }
  return result;
}

function buildPath(oWhichArray, iEventID) {
    var iPath = iEventID;
    oMyArrayName = eval(oWhichArray);
    do {
        for (var iCounterC = 0; iCounterC <= oMyArrayName.length; iCounterC++) {
            if (iCounterC != oMyArrayName.length) {
                if (oMyArrayName[iCounterC][ID] == iPath) {
                    oPathArray[oPathArray.length] = iCounterC;
                    oIDArray[oIDArray.length] = oMyArrayName[iCounterC][ID];
                    iPath = oMyArrayName[iCounterC][PARENT_ID];
                    iCounterC = -1;
                }
            }
            else {
                return;
            }
        }
    } while (iPath != 0);
}

function getParent(myArray, iMarket) {
    oMyArrayName = eval(myArray);
    do {
        for (var iCounterC = 0; iCounterC <= oMyArrayName.length; iCounterC++) {
            if (iCounterC != oMyArrayName.length) {
                if (oMyArrayName[iCounterC][ID] == iMarket) {
                    return oMyArrayName[iCounterC][PARENT_ID];
                }
            }
            else {
                return -1;
            }
        }
    } while (iPath != 0);
    return -1;
}

function goMarket (
        iMarketID, // my EventId
        sMarketType,
        bOutsideRequest, // should be true for opened windows
        iOutsideParentID, // my MarketId
        sMarketName,
        sOrigin) {

    if (bOutsideRequest) {
        iRequestFromOutsideParentID = iOutsideParentID;
    }

    currentMyItem = iOutsideParentID;

    if (!bOutsideRequest && iMarketID == iCurrentMarketID && iOutsideParentID == currentAllItem)
        return;

    highlightMarket(iMarketID);

    var _currentItem = (bOutsideRequest)?iRequestFromOutsideParentID:currentAllItem;

    goMenu(_currentItem, iMarketID);

    if (sOrigin != AUTOREFRESH) {
        try {
          if ( top.frames['main'].doUnloadChidren ) {
             top.frames['main'].doUnloadChidren();
          }
          top.frames['main'].getEventData(iMarketID);
        } catch (x) {
            if (sOrigin != PRESETMARKET) {
             parent.frames['mainBar'].location.replace(sContextPath + "/betex/mainBar.do?marketId=" + _currentItem + "&eventId=" + iMarketID+"&showProfitLoss="+showProfitLoss+"&showWhatIf="+showWhatIf);
             displayMarketIsLoading();
             slocation = sContextPath + "/betex/gridPage.do?marketId=" + _currentItem + "&eventId=" + iMarketID + "&showProfitLoss="+showProfitLoss+"&showWhatIf="+showWhatIf;
             setTimeout("top.frames['main'].location.replace(slocation);", 20);
            }
        }
   if (sOrigin != PRESETMARKET) {
        var betFrame  = top.frames.betBorder;
       if (betFrame) {
        betFrame.clearPreBets();
        betFrame.go_tab("sc3");
        betFrame.getEventAuxInfo();
       }
      }
    }
}

function displayMarketIsLoading() {
    var mainDoc = parent.frames['main'].document;
    var sLoadingDoc = "<html><body style='background-color:#F9F9F9'>"
    + "<table cellspacing='0' cellpadding='0' height='100%' width='100%'>"
    + "<tr><td style='font-family: Tahoma;' valign='middle' align='center'>"
    + "<font color='#999999'><h1>" + sLoadingMainText + "</h1></font>"
    + "</td></tr>"
    + "</table>"
    + "</body></html>";
    mainDoc.write(sLoadingDoc);
}

function highlightMarket(iMarketID)
{
    if (iCurrentMarketID != iMarketID)
    {
        if (iCurrentMarketID != null)
        {
            if (document.getElementById("a" + iCurrentMarketID))
            {
                document.getElementById("a" + iCurrentMarketID).className = "MenuMarket";
            }
            if (document.getElementById("m" + iCurrentMarketID))
            {
                document.getElementById("m" + iCurrentMarketID).className = "MenuMarket";
            }
        }

        if (document.getElementById("a" + iMarketID))
        {
            document.getElementById("a" + iMarketID).className = "MenuMarketDown";
        }
        if (document.getElementById("m" + iMarketID))
        {
            document.getElementById("m" + iMarketID).className = "MenuMarketDown";
        }
        iCurrentMarketID = iMarketID;
    }
}


function domWrite(
        layerName,
        sHTML,
        bForceRefresh)
{
    document.getElementById(layerName).style.display = "none";

    if (sHTML && sHTML.length > 0) {
        try {
          document.getElementById(layerName).innerHTML = sHTML;
        } finally {
          document.close();
        }

        if (((layerName == allMarketsTreeContainer || layerName == "allMenuParents"))
                || ((layerName == myMarketsTreeContainer || layerName == "myMenuParents"))
                || (layerName == myMarketsTreeContainer && fCustomiseFlag) || bForceRefresh)
        {
            document.getElementById(layerName).style.display = "block";
        }
    } else {
       try {
      document.getElementById(layerName).innerHTML = "";
    } finally {
      document.close();  
    }

      document.getElementById(layerName).style.display = "block";
    }

    if (!fCustomiseFlag && fixMarketPage )  // && layerName.indexOf("enuParents") == -1
        setTimeout("fixMarketPage();", 100);

    if (fCustomiseFlag && fixCustomizeMarketPage ) { // && layerName.indexOf("enuParents") == -1
        setTimeout("fixCustomizeMarketPage();", 100);
    }
}

function writeItems(
        strArrayName,
        parentID,
        layerName,
        strMenuPathArrayName,
        menuPathLayer,
        bAddToMenuPath) {
    switch (layerName) {
        case allMarketsTreeContainer :
            currentAllItem = parentID;
            break;
        case myMarketsTreeContainer :
            currentMyItem = parentID;
            break;
    }
    domWrite( layerName, getMenuItems(strArrayName, parentID, layerName, strMenuPathArrayName, menuPathLayer, bAddToMenuPath) );
}


function addToMenuPath(
        iIdx,
        arrayName,
        strArrayName,
        strMenuPathArrayName,
        menuPathLayer,
        layerName)
{
    menuPathArray = eval(strMenuPathArrayName);
    menuPathArray[menuPathArray.length] = new Array(arrayName[iIdx][ID], arrayName[iIdx][PARENT_ID], arrayName[iIdx][NAME], arrayName[iIdx][HAS_MARKET]);
    buildMenuPath(strArrayName, strMenuPathArrayName, menuPathLayer, layerName);
}


function buildMenuPath(
        strArrayName,
        strMenuPathArrayName,
        menuPathLayer,
        layerName)
{
    var a = [];
    a.push(sContainerArray[1]);
    switch (layerName)
            {
        case allMarketsTreeContainer :
            a.push("<tr onclick=\"" + strMenuPathArrayName + ".length=0;");
            a.push("domWrite('" + menuPathLayer + "','');");
            a.push("writeItems('" + strArrayName + "',1,'" + layerName + "','" + strMenuPathArrayName + "','" + menuPathLayer + "')\"\">\n");
            a.push("<td width=\"100%\" class=\"MenuBorder\">\n");
            a.push("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
            a.push("<tr>\n");
            a.push("<td class=\"MenuPath\" ");
            a.push("onmouseover=\"this.className='MenuPathOver';\" ");
            a.push("onmouseout=\"this.className='MenuPath';\" ");
            a.push("onMouseDown=\"this.className='MenuPathDown';\" ");
            a.push("onMouseUp=\"this.className='MenuPath';\" ");
            a.push("width=\"100%\">" + sAllMarkets + "</td>\n");
            a.push("</tr>\n</table>\n");
            a.push("</td>\n</tr>\n");
            break;

        case myMarketsTreeContainer :
            a.push("<tr onclick=\"" + strMenuPathArrayName + ".length=0;");
            a.push("domWrite('" + menuPathLayer + "','');");
            a.push("writeItems('" + strArrayName + "',0,'" + layerName + "','" + strMenuPathArrayName + "','" + menuPathLayer + "')\"\">\n");
            a.push("<td width=\"100%\" class=\"MenuBorder\">\n");
            a.push("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
            a.push("<tr>\n");
            a.push("<td class=\"MenuPath\" ");
            a.push("onmouseover=\"this.className='MenuPathOver';\" ");
            a.push("onmouseout=\"this.className='MenuPath';\" ");
            a.push("onMouseDown=\"this.className='MenuPathDown';\" ");
            a.push("onMouseUp=\"this.className='MenuPath';\" ");
            a.push("width=\"100%\">" + sMyMarkets + "</td>\n");
            a.push("</tr>\n</table>\n");
            a.push("</td>\n</tr>\n");
            break;
    }

    for (var i = 0; i < menuPathArray.length; i++) {
        if (i == menuPathArray.length - 1) {

            a.push("<tr onClick=\"" + strMenuPathArrayName + ".length=" + (i + 1) + ";");
            a.push("buildMenuPath('" + strArrayName + "','" + strMenuPathArrayName + "','" + menuPathLayer + "','" + layerName + "');");
            a.push("writeItems('" + strArrayName + "'," + menuPathArray[i][ID] + ",'" + layerName + "','" + strMenuPathArrayName + "','" + menuPathLayer + "','false')\">\n");
            a.push("<td width=\"100%\" class=\"MenuBorder\">\n");
            a.push("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
            a.push("<tr>\n");
            a.push("<td class=\"MenuPathSelectedItem\" width=\"100%\">" + menuPathArray[i][NAME] + "</td>\n");
            a.push("</tr>\n");
            a.push("</table>\n");
            a.push("</td>\n</tr>\n");
        }
        else {
            a.push("<tr onClick=\"" + strMenuPathArrayName + ".length=" + (i + 1) + ";");
            a.push("buildMenuPath('" + strArrayName + "','" + strMenuPathArrayName + "','" + menuPathLayer + "','" + layerName + "');");
            a.push("writeItems('" + strArrayName + "'," + menuPathArray[i][ID] + ",'" + layerName + "','" + strMenuPathArrayName + "','" + menuPathLayer + "','false')\">\n");
            a.push("<td width=\"100%\" class=\"MenuBorder\">\n");
            a.push("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
            a.push("<tr>\n");
            a.push("<td class=\"MenuPath\" ");
            a.push("onmouseover=\"this.className='MenuPathOver';\" ");
            a.push("onmouseout=\"this.className='MenuPath';\" ");
            a.push("onMouseDown=\"this.className='MenuPathDown';\" ");
            a.push("onMouseUp=\"this.className='MenuPath';\" ");
            a.push("width=\"100%\">" + menuPathArray[i][NAME]);
            a.push("</td>\n");
            a.push("</tr>\n");
            a.push("</table>\n");
            a.push("</td>\n</tr>\n");
        }
    }
    a.push(sContainerArray[2]);
    domWrite(menuPathLayer, a.join(''));
}

function createAllMarketMenu() {
    var allMarketsDiv = document.getElementById(allMarketsTreeContainer);
    splitSkeleton('all');
    var allMenu = getMenuItems("allSkeletonArray", 1, allMarketsTreeContainer, "allPathArray", "allMenuParents");
    if (allMarketsDiv) {
        allMarketsDiv.style.display = "none";
        try {
        allMarketsDiv.innerHTML = allMenu;
        } finally {
          document.close();
        }
        allMarketsDiv.style.display = "block";
    }
}

function createMyMarketMenu() {
    if (myMarketsString != "") {
        integrateMyEventsAndMarkets();
    }
    splitSkeleton('my');
    var sHtml = getMenuItems("mySkeletonArray", 0, myMarketsTreeContainer, "myPathArray", "myMenuParents", null, true);
    domWrite("myMenuParents", "", true);
    domWrite(myMarketsTreeContainer, "", true);
    domWrite(myMarketsTreeContainer, sHtml, true);
}

function getCurrentMarketAndEventName(currentMarketId, currentEventId) {
  // alert ("markets.getCurrentMarketAndEventName: currentMarketId=" + currentMarketId + ", currentEventId=" + currentEventId);
    var currentMarketName = '';
    var marketSpan = '';
    var i;
    if (allSkeletonArray.length > 0) {
        for (i = 0; i < allSkeletonArray.length; i++) {
            if (currentMarketId == allSkeletonArray[i][ID]) {

                currentMarketName = allSkeletonArray[i][NAME];
              //  alert("currentMarketName="+currentMarketName);
                break;
            }
        }
    }

    currentMarketName = (currentMarketName != null && currentMarketName.length > 0)? currentMarketName:' ';
    marketSpan = '<span id="currentMarketTitle" class="currentMarket">' + currentMarketName + '</span>';

    var currentEventName = '';
    var eventSpan = '';

    if (meatArray.length > 0) {
        for (i = 0; i < meatArray.length; i++) {
            if (currentEventId == meatArray[i][ID]) {
                currentEventName = meatArray[i][NAME];
              //  alert("currentEventName="+ currentEventName);
                break;
            }
        }
    }

    currentEventName = (currentEventName != null && currentEventName.length > 0 ) ? currentEventName :'';
    eventSpan = '<span id="currentEventTitle" class="currentEvent">' + currentEventName + '</span>';
    var a = [];
    a.push(marketSpan);
    a.push((currentEventName.length > 0) ? ' - ':'');
    a.push(eventSpan);
          // alert(retStr);
    return a.join('');
}

function getEventState(eventId){
 var _eventId = eventId ?  eventId : iCurrentMarketID;
 var _currentEventState = 0;
    if (meatArray && meatArray.length > 0) {
        for (var i = 0; i < meatArray.length; i++)
        {
            if ( _eventId == meatArray[i][ID] )
            {
                _currentEventState = meatArray[i][EVENT_STATE];
                break;
            }
        }
    }
  return _currentEventState;
}

function integrateMyEventsAndMarkets() {
    splitSkeleton('my');
    var myUberArray = new Array();
    for (var iCounterX = 0; iCounterX < mySkeletonArray.length; iCounterX++) {
        myUberArray[myUberArray.length] = mySkeletonArray[iCounterX];
        for (var iCounterW = 0; iCounterW < myMarketsArray.length; iCounterW++) {
            if (mySkeletonArray[iCounterX][ID] == myMarketsArray[iCounterW][PARENT_ID]) {
                myUberArray[myUberArray.length] = myMarketsArray[iCounterW];
                myUberArray[myUberArray.length - 1][HAS_MARKET] = "2";
            }
        }
    }
    var oTempArray = new Array(myUberArray.length);
    for (var iCounterY = 0; iCounterY < myUberArray.length; iCounterY++) {
        oTempArray[iCounterY] = myUberArray[iCounterY].join("~");
    }
    mySkeletonString = oTempArray.join("|");
}

function getMyEventsAndMarkets() {
    splitSkeleton('my');
    var result = "";
    var i;
    for ( i = 0; i < mySkeletonArray.length; i++)
    {
        if (mySkeletonArray[i][ID] != "") {
            result += mySkeletonArray[i][ID] + ",";
        }
    }

    for ( i = 0; i < myMarketsArray.length; i++)
    {
        if (myMarketsArray[i][ID] != "") {
            result += myMarketsArray[i][ID] + ",";
        }
    }

    result = ((result == null || result == "")?"-1":result.substring(0, result.length - 1));
    return result;
}


function reloadMarkets(targetMarketId, targetEventId, sOrigin) {
    var topBar = top.topBar;
    if (topBar) {
        topBar.savedMarketId = (targetMarketId !=null && targetMarketId > 0)?targetMarketId : currentAllItem;
        topBar.savedEventId = (targetEventId != null && targetEventId > 0)? targetEventId : iCurrentMarketID;
        topBar.savedOrigin = sOrigin;
    }
     top.presetMarketFrame();
}

function preSetMarket() {
    var topBar = top.topBar;
    if (topBar) {
        if (topBar.savedEventId && topBar.savedMarketId && topBar.savedOrigin) {
            goMarket(topBar.savedEventId, null, true, topBar.savedMarketId, null, topBar.savedOrigin);
            topBar.savedEventId = null;
            topBar.savedMarketId = null;
            topBar.savedOrigin = null;
        }
    }
}

function clickTab1(thisObject) {
    expandcontent('sc1', thisObject);
    //fixMarketPage();
}

function clickTab2(thisObject) {
    if (!isLoggedIn) {
        alert(sNotLoggedInAlert);
        return;
    }
    expandcontent('sc2', thisObject);
    //fixMarketPage();
}

function ajustStyles() {
 if (IE) {
  document.getElementById("allcontent").style.height = "100%";
  if (fCustomiseFlag) {
   document.getElementById("centercontent").style.height = "100%";

   document.getElementById("area_left").style.height       = "100%";
   document.getElementById("sub_area_left").style.height       = "100%";

   document.getElementById("movebuttons").style.height       = "100%";

   document.getElementById("area_right").style.height       = "100%";
   document.getElementById("sub_area_right").style.height       = "100%";
  } else {
   document.getElementById("tabcontent").style.height = "100%";

   document.getElementById("sc1").style.height       = "100%";
   document.getElementById("sc2").style.height       = "100%";
  }
     document.getElementById("td_marketContainer").style.height = "100%";

     document.getElementById("td_allMarketsTreeContainer").style.height   = "100%";
     document.getElementById("allMarketsTreeContainer").style.height      = "100%";

     document.getElementById("td_myMarketsTreeContainer").style.height    = "100%";
     document.getElementById("myMarketsTreeContainer").style.height       = "100%";
 }
}

function initialize() {
    assessDom();
    createAllMarketMenu();
    initialiseMyMarkets();
    preSetMarket();
    ajustStyles();
    if (fixMarketPage) {
      fixMarketPage();
        if( IE ) {
            document.body.onresize = fixMarketPage;
        } else if (window.addEventListener) {
            window.addEventListener("resize", fixMarketPage, false);
        }
    }
    if (initUnloadEvent) {
        initUnloadEvent();
    }

    bMenuLoaded = true;
   }


function initialiseMyMarkets()
{
    splitSkeleton('meat');
    if ((mySkeletonString && mySkeletonString.length > 0) || (myMarketsString && myMarketsString.length > 0 && iStartupMeatCounter == 0))
    {
        bCustomMarketsLoaded = true;
        splitSkeleton('my');
        iStartupMeatCounter = myMarketsArray.length;
        createMyMarketMenu();
    }
}

