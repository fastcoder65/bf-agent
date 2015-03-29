var W3DOM   =   (document.getElementsByTagName);
var IE      =   (document.getElementsByTagName&&document.all);

var BET_ID      = 0;
var RESULT_ID   = 1;
var TYPE        = 2;
var VALUE       = 3;
var ODDS        = 4;
var AMOUNT      = 5;
var REG_DATE    = 6;
var REDO_HINT   = 7;
var DELETE_HINT = 8;

var MyBetWin = null;
var refreshOn = true;

var refreshMyBetsTimeout;

var keyEventProcessing = false

if (iMyBetsRefreshInterval > 0 ) {
 refreshMyBetsTimeout = setTimeout("RefreshMyBets()", iMyBetsRefreshInterval * 1000);
}

var tempUB = new Array();
var tempMB = new Array();

var myUB4Back = new Array();
var myUB4Lay  = new Array();

var myMB4Back = new Array();
var myMB4Lay  = new Array();

var ssPayment   = parent.ssPayment;
var ssLiability = parent.ssLiability;

var ON  = "1";
var OFF = "0";

var bBetsLoaded = false;
// key codes to prevent
var BACKSPACE = 8;

function RefreshMyBets() {
    var myBetPage = top.betBorder.mybetPage;
    if (refreshOn && myBetPage && myBetPage.currentEventId) {
        iEventId = myBetPage.currentEventId;
        getMyBetsData('eventId=' + iEventId, parent.sMatchedBets, parent.sBetView); // parent.sShowBetInfo,
    }
}

function setMyBetUpdateStatus(statusMessage) {
 var statusElement = parent.document.getElementById("myBetsUpdateStatus");
 if (statusElement) {
   statusElement.innerHTML = statusMessage;
 }
}

function getMyBetsData( sItemId, sMatchedBets, sBetView) {
  clearTimeout(refreshMyBetsTimeout);
  var target          = sContextPath + '/betex/loadBetDataAction.do?';
  var myBetPage       = top.betBorder.mybetPage;
  var matchedBetsArg     = "&mb=" + ON; //always ON, not using "sMatchedBets" when retrieving matched bets!;
  var betViewArg         = "&bv=" + sBetView; // or "avg"
  bBetsLoaded = false;
  setMyBetUpdateStatus(sMyBetUpdateStatusMessage);
  top.betBorder.mybetPage.myBetsManager.location.replace(target + sItemId + matchedBetsArg + betViewArg);
}

function setMyBetsTimeout() {
 if (iMyBetsRefreshInterval > 0) {
  refreshOn = true;
  refreshMyBetsTimeout = setTimeout("RefreshMyBets()", iMyBetsRefreshInterval * 1000);
 }
}

function clearBetUpdateTimeOut(thisCtrl, isWhole) {
if (!keyEventProcessing) {
    keyEventProcessing = true;
  var DigitPat;
  if (isWhole)
  {
   DigitPat = new RegExp("^\\d*$","g");
  }
  else {
   DigitPat = new RegExp("^\\d*[\\.]{0,1}\\d*$","g");
  }

  if (thisCtrl && thisCtrl.value) {
    if(DigitPat.test(thisCtrl.value)) {
      refreshOn = false;
      clearTimeout(refreshMyBetsTimeout);
    }
    else {
      alert(sIllegalNumber1 + ' "'+thisCtrl.value+'" '+ sIllegalNumber2 );
    }
  }
  setTimeout("keyEventProcessing = false;", 300);
 }
}

function deleteStake(iAction, iStakeId, iEventid )
{
    form = document.forms['myBetForm'];
    //   alert ('iStakeId='+iStakeId+ ', odds=' + form.odds.value + ', amount=' + form.amount.value);
    if (form) {
    form.action.value = iAction;
    form.stakeId.value = iStakeId;
    form.eventId.value = iEventid;
    form.submit();
    }
}

function redoStake(iAction, iStakeId, iEventid) {
    form = document.forms['myBetForm'];
    if (form && validateMyBetForm(form)) {
        //    alert ('iStakeId='+iStakeId+ ', odds=' + form.odds.value + ', amount=' + form.amount.value);
        form.action.value = iAction;
        form.stakeId.value = iStakeId;
        form.eventId.value = iEventid;
        form.submit();
    }
}

function getAmountElementId(Id) {
  var newAmount = "strAmount("+ Id + ")";
  var oldAmount = "amount(" + Id + ")";
if ( document.getElementById(newAmount) != null && document.getElementById(newAmount).type == "text"
	&& document.getElementById(newAmount).value != null && document.getElementById(newAmount).value.length > 0){
   return newAmount;
 } else if (document.getElementById(oldAmount) && document.getElementById(oldAmount).innerHTML) {
   return oldAmount;
 } else
    return "";
}

function getYourResultElementId(bidType, Id){
 return ( bidType == 1 )? "yourProfit(" + Id + ")" : "yourPL(" + Id + ")";
}

function getUnmatchedItemHTML(myBetItem, iEventId) {
    var Id = myBetItem[BET_ID];
    var bidType = myBetItem[TYPE];
    var cssClassName = "";
    if (bidType == 1) {
        cssClassName = "cell-back";
    }
    else if (bidType == 2) {
        cssClassName = "cell-lay";
    }
    var sCalcProfitCall = "calcProfit(\"odds(" + Id + ")\",\"\", \"" + getYourResultElementId(bidType, Id) + "\"," + bidType + ", \"plUBSwitch\" );";
    var a = [];
    a.push("\n<tr title='" + sRefId + " " + Id + "," + sAcceptedAt + " " + myBetItem[REG_DATE] + "'>");
    a.push("\n<td class='row'>");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' >");
    a.push("\n<tr >");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg + "' height='0px' width='100px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg + "' height='0px' width='75px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg + "' height='0px' width='16px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg + "' height='0px' width='45px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg + "' height='0px' width='50px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg + "' height='0px' width='32px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg + "' height='0px' width='100px'></td>");
    a.push("</tr>");
    a.push("\n<tr><td width='100px' class='" + cssClassName + "' >" + myBetItem[VALUE] + "</td>");
    a.push("\n<td class='" + cssClassName + "' ><input onKeyUp='clearBetUpdateTimeOut(this, false);" + sCalcProfitCall + "' maxlength=\"5\" type=\"text\" id=\"odds(" + Id + ")\" name=\"odds(" + Id + ")\" value='" + myBetItem[ODDS] + "' style='width:40px;text-align:right'></td>");
    a.push("\n<td class='" + cssClassName + "' style='width:16px;text-align: left;'>");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0'>");
    a.push("\n<tr><td><img src='" + parent.sImageSpinUp + "' onclick='parent.oddsUp(\"odds(" + Id + ")\", \"mybetPage\");" + sCalcProfitCall + "'></td></tr>");
    a.push("\n<tr><td><img src='" + parent.sImageSpinDown + "' onclick='parent.oddsDown(\"odds(" + Id + ")\", \"mybetPage\");" + sCalcProfitCall + "'></td></tr>");
    a.push("\n</table>");
    a.push("\n</td>");
    a.push("\n<td class='" + cssClassName + "' id=\"amount(" + Id + ")\" >" + formatCurrency("", myBetItem[AMOUNT]) + "</td>");
    a.push("\n<td class='" + cssClassName + "'><input onKeyUp='clearBetUpdateTimeOut(this, false);" + sCalcProfitCall + "' type=\"text\" maxlength=\"9\" id=\"strAmount(" + Id + ")\" name=\"strAmount(" + Id + ")\" style='width:40px;text-align:right'></td>");
    a.push("\n<td class='" + cssClassName + "' style='text-align: left;' width='32px' >");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' ><tr>");
    a.push("\n<td title='retry'>");
    a.push("\n<img src='" + sRetryImg + "' id='ri_" + Id + "' ");
    a.push("title = '" + myBetItem[REDO_HINT] + "' ");
    a.push("onClick=\"redoStake(" + iBetRedo + ", " + Id + ", " + iEventId + ")\"></td>");
    a.push("<td title='back'>");
    a.push("<img src='" + sCancelImg + "' id ='ci_" + Id + "' ");
    a.push("title = '" + myBetItem[DELETE_HINT] + "' ");
    a.push("onClick=\"deleteStake(" + iBetCancel + "," + Id + "," + iEventId + ")\">");
    a.push("</td></tr></table>");
    a.push("</td><td style='text-align:left' class='" + cssClassName + "' id='" + getYourResultElementId(bidType, Id) + "'  ></td></tr>");
    a.push("</table></td></tr>");
    return a.join('');
}

function preventKeyEvent(evt) {
    var _evt = (evt) ? evt : event;
    if (_evt && _evt.keyCode != 0) {
        if (IE) {
            _evt.cancelBubble = true;
            _evt.returnValue = false;
        } else {
            alert("Mozilla:_evt.keyCode="+ _evt.keyCode) ;
            _evt.preventDefault();
            _evt.stopPropagation();
        }
    } else {
       alert ("empty evt parameter!");
    }
  return false ;
}

function addUnmatchedItems(myBetItems, iEventId) {
    var _sBetsHTML = "";
    for (var i = 0; i < myBetItems.length; i++) {
        if (myBetItems[i] != null && myBetItems[i][1]) {
            _sBetsHTML += getUnmatchedItemHTML(myBetItems[i], iEventId);
        }
    }
    return _sBetsHTML;
}

function getMatchedItemHTML(myBetItem)
{

    var Id = myBetItem[BET_ID];
    var bidType = myBetItem[TYPE];

    var cssClassName = "";

    if (bidType == 1)
    {
        cssClassName = "cell-back";
    }
    else if (bidType == 2)
    {
        cssClassName = "cell-lay";
    }

    var a = [];
    a.push("\n");

    if (myBetItem[REG_DATE] != null && myBetItem[REG_DATE] != "null")
    {
        a.push("<tr title='" + sBetRefId + " " + Id + "," + sMatchedAt  + " " + myBetItem[REG_DATE] + "'>");
    }
    else
    {
        a.push("<tr>");
    }

    a.push("\n<td class='row'>");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' height='20px'>");

    a.push("\n<tr>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='90px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='90px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='3px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='60px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='10px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='50px'></td>");
//	sMyBetItem += "\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='100px'></td>";
    a.push("\n</tr>");
    a.push("\n<tr>");
    a.push("\n<td class='" + cssClassName + "'>" + myBetItem[VALUE] + "</td>");
    a.push("\n<td class='" + cssClassName + "' id=\"odds(" + Id + ")\"   width='90px' >" + myBetItem[ODDS] + "</td>");
    a.push("\n<td class='" + cssClassName + "' width='3px'>&nbsp;</td>");
    a.push("\n<td class='" + cssClassName + " ' id=\"amount(" + Id + ")\" width='60px' >" + formatCurrency("", myBetItem[AMOUNT]) + "</td>");
    a.push("\n<td class='" + cssClassName + "' width='10px'>&nbsp;</td>");
/*
    sMyBetItem += "\n<td class='" + cssClassName + "' width='60px'></td>";
*/
    a.push("\n<td colspan='2' style='text-align:left' class='" + cssClassName + "' id='"+ getYourResultElementId(bidType, Id) +"' width='160px'>&nbsp;</td>");
    a.push("\n</tr>");
    a.push("\n</table></td></tr>");
    return a.join('');
}

function addMatchedItems(myBetItems) {
    var EMPTY = "";
    var a = [];
    var currentResult = EMPTY;
    for (var i = 0; i < myBetItems.length; i++) {
        if (myBetItems[i] != null && myBetItems[i][1]) {
          if (myBetItems[i][VALUE] != currentResult ) {
           currentResult = myBetItems[i][VALUE];
          } else {
           myBetItems[i][VALUE] = EMPTY;
         }
            a.push(getMatchedItemHTML(myBetItems[i]));
        }
    }
    return a.join('');
}

function itemsExist(myBetItems) {
    var _itemsExist = false;
    for (var i = 0; i < myBetItems.length; i++) {
        if (myBetItems[i] != null) {
            _itemsExist = true;
            break;
        }
    }
    return  _itemsExist;
}

function getBetsNotFound()
{
  return "\n <table cellpadding='0' cellspacing='0' border='0'  width='100%' >\n <tr><td class='row'><b>" + sBetsNotFound + "</b></td></tr></table>";
}

function getUnmatchedBetsNotFound()
{
  return "\n <table cellpadding='0' cellspacing='0' border='0'  width='100%' >\n <tr><td class='row'><b>" + sUnmatchedBetsNotFound + "</b></td></tr></table>";
}

function getGroupHeader(sGroupHeaderCaption)
{
    var sHeader = "\n  <tr><td height='30px' align='left' valign='bottom'><b>" + sGroupHeaderCaption + "</b></td></tr>";
        sHeader += "\n <tr><td height='3px'></td></tr>";
    return sHeader;
}

function calcProfit(oddsElementId, amountElementId, resultElementId, bidType, plSwitchElementId) {

   document.getElementById(resultElementId).innerHTML = "";

 if (!oddsElementId )
 {
//	 alert("bad oddsElementId=" + oddsElementId );
     return;
 }

    left_s = oddsElementId.indexOf("(");
    right_s = oddsElementId.indexOf(")");

    var Id = "";

    if (left_s != -1 && right_s != -1) {
        Id = oddsElementId.substring(left_s + 1, right_s);
    }

   amountElementId = getAmountElementId(Id);

   if (!oddsElementId || !amountElementId || !resultElementId || !bidType) {
     alert(" bad one of : oddsElementId=" + oddsElementId + ", amountElementId=" + amountElementId+", resultElementId="+resultElementId+", bidType="+bidType);
     return;
   }

    if (document.getElementById(oddsElementId) && document.getElementById(oddsElementId).type=="text") {
       currentOdds = Number( stripCommas (document.getElementById(oddsElementId).value));
	}
	 else if (document.getElementById(oddsElementId))
	      {
            currentOdds =	Number(stripCommas(document.getElementById(oddsElementId).innerHTML));
	       }
		   else {
		     currentOdds = "";
           }

	if (document.getElementById(amountElementId) && document.getElementById(amountElementId).type=="text"){
       currentAmount = Number(stripCommas(document.getElementById(amountElementId).value));
	}

    else if (document.getElementById(amountElementId))
	     {
           currentAmount = Number( stripCommas(document.getElementById(amountElementId).innerHTML));
	     }
		 else {
		    currentAmount = "";
	     }

    if (currentOdds == "" || isNaN(currentOdds)) {
//		alert ("currentOdds=<"+currentOdds+">");
        return;
    }

    if (currentAmount == "" || isNaN(currentAmount)) {
//		alert ("currentAmount=<"+ currentAmount +">");
        return;
    }

    var switchStatus = getPLSwitchStatus(plSwitchElementId);

    var cProfit = internalCalcProfit (bidType, currentOdds, currentAmount, switchStatus )
	var sResult = parent.sCurrSign + formatCurrency("", cProfit);

  if (document.getElementById(resultElementId)) {
      document.getElementById(resultElementId).innerHTML = sResult;
	} else {
//      alert("resultElement with id="+resultElementId+" not found!" );
	}
}

function getPLSwitchStatus(plSwitchElementId){
     var current_plSwitch = null;
     var _switchStatus = null;

       if ((plSwitchElementId == "plUBSwitch") && document.forms.myBetForm) {
		 current_plSwitch = document.forms.myBetForm.plUBSwitch;
	   } else if ((plSwitchElementId == "plMBSwitch") && document.forms.myMBForm) {
		 current_plSwitch = document.forms.myMBForm.plMBSwitch;
	   }

       if (current_plSwitch) {
         if (current_plSwitch[ssPayment].checked) { //Payment
              _switchStatus = ssPayment;
         } else if (current_plSwitch[ssLiability].checked) { // Liability
              _switchStatus = ssLiability;
         }
	   }
  return _switchStatus;
}

function internalCalcProfit (bidType, currentOdds, currentAmount, switchStatus ) {
    var _profit = null;

    if (bidType == 1 ) {// back
        _profit = (currentOdds - 1) * currentAmount;
    } else {
        if (switchStatus == ssPayment) {
			_profit = currentOdds * currentAmount;
		} else if (switchStatus == ssLiability) {
			_profit = (currentOdds - 1) * currentAmount;
		}
    }
  return _profit;
}

function myCalcProfits(myItems, resultElementId, plSwitchElementId) { // oddsElementId, amountElementId,
	var _switchStatus = getPLSwitchStatus(plSwitchElementId);

   for (var i = 0; i < myItems.length; i++) {
        if (myItems[i] != null && myItems[i][BET_ID] != null) {
            var Id      = myItems[i][BET_ID];
			var _bidType = myItems[i][TYPE];
			var _odds    = myItems[i][ODDS];
			var _amount  = myItems[i][AMOUNT];

            var cProfit = internalCalcProfit (_bidType, _odds, _amount, _switchStatus )
         	var sResult = (cProfit!=null)?(parent.sCurrSign + formatCurrency("", cProfit)):"";

            var targetResultElementId = resultElementId + "("+ Id + ")";
            if (document.getElementById(targetResultElementId)) {
		      document.getElementById(targetResultElementId).innerHTML = sResult;
			} else {
//		      alert("target ResultElement with id="+targetResultElementId+" not found!" );
			}
        }
		else {
//			alert("bad item found: myItems["+i+"][BET_ID] is null!!!");
		}
    }
}

function setUBSwitchChecked( _item) {
 if (document.forms.myBetForm) {
    document.forms.myBetForm.plUBSwitch[_item].checked = true;
    document.forms.myBetForm.plUBSwitch[1 - _item].checked = false;

	parent.UB_PlSwitchValue = _item;
	// alert("setUBSwitchChecked(" + _item + "), parent.UB_PlSwitchValue"+ _item);
 }
}

function setMBSwitchChecked( _item) {
 if (document.forms.myMBForm) {
    document.forms.myMBForm.plMBSwitch[_item].checked = true;
    document.forms.myMBForm.plMBSwitch[1 - _item].checked = false;
	parent.MB_PlSwitchValue = _item;
    // alert("setUBSwitchChecked(" + _item + "), parent.MB_PlSwitchValue=" + _item );
 }
}

function preCalcProfits()
{
  myCalcProfits(myUB4Back,  'yourProfit'); // 'odds','amount',
  myCalcProfits(myUB4Lay,  'yourPL', 'plUBSwitch'); // 'odds','amount',

  myCalcProfits(myMB4Back,  'yourProfit'); // 'odds','amount',
  myCalcProfits(myMB4Lay, 'yourPL', 'plMBSwitch'); // 'odds','amount',
}

function getUnmatchedTableHeader(bidType)
{
    var sOddsTitle = (bidType == top.main.bidTypeBACK)? parent.sBackersOdds :  parent.sLayersOdds;
    var sAmountTitle = (bidType == top.main.bidTypeBACK)? parent.sBackersAmount : parent.sLayersAmount;

	var cssClassName = "cell-title";

    var a = [];
    a.push("\n <tr><td class='row-title'>");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' height='100%'>");
    a.push("\n<tr>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='100px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='75px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='16px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='45px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='50px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='32px'></td>");
    a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='100px'></td>");
    a.push("</tr>");
    a.push("\n<tr>");
    a.push("\n<td class='" + cssClassName + "' width='100px'>" + sBetSelection + "</td>");
    a.push("\n<td class='" + cssClassName + "'>" + sOddsTitle + "</td>");
    a.push("\n<td class='" + cssClassName + "'>&nbsp;</td>");
    a.push("\n<td class='" + cssClassName + "'>" + sAmountTitle + "&nbsp;" + parent.sCurrSign +  "</td>");
    a.push("\n<td class='" + cssClassName + "'>" + sBetNewAmount + "</td>");
    a.push("\n<td class='" + cssClassName + "'>&nbsp;</td>");
    if (bidType == top.main.bidTypeBACK)
	{
        a.push("<td style='text-align:left' class='" + cssClassName + "'>" + parent.sYourProfit + "</td>");
    }
	else if (bidType == top.main.bidTypeLAY)
	{
		var sCalcLayProfitsCall = "myCalcProfits(myUB4Lay,  'yourPL', 'plUBSwitch');"; // 'odds','amount',
		var sPaymentLiabilityHeader = getPaymentLiabilityHeader("plUBSwitch", "setUBSwitchChecked", sCalcLayProfitsCall);
        a.push("<td style='text-align:left' class='" + cssClassName + "'>" + sPaymentLiabilityHeader + "</td>");
    }
    a.push("\n </tr>");
    a.push("\n </table>");
    a.push("\n </td></tr>");
    return a.join('');
}

function getPaymentLiabilityHeader(plSwitchName, plSwitchOnClick, sCalcLayProfitsCall)
{
	function getChecked(_plSwitchValue)
	{
		return ((_plSwitchValue == 0)?"":"checked");
	}

	var plSwitchValue = 0;
	var isUB = (plSwitchName.indexOf("UB") != -1);
	var isMB = (plSwitchName.indexOf("MB") != -1);

	if (isUB)
	{
		plSwitchValue = parent.UB_PlSwitchValue;
	}
	else if (isMB)
	{
		plSwitchValue = parent.MB_PlSwitchValue;
	}

    var a = [];
    a.push("\n <table cellpadding='0' cellspacing='0' border='0'>");
    a.push("\n <tr>");
    a.push("\n <td><input style='height:15px' type='radio' class='radio' "+getChecked(1-plSwitchValue)+" id='" + plSwitchName + "2' name='" + plSwitchName + "' value='2' onclick=\""+ plSwitchOnClick + "("+ssPayment+");" + sCalcLayProfitsCall + "\"></td>");
    a.push("\n <td><label for='" + plSwitchName + "2'><a href='#' onclick=\""+ plSwitchOnClick + "("+ssPayment+");" + sCalcLayProfitsCall + "\">" + parent.sPaymentText + "</a></label></td>");
    a.push("\n </tr>");
    a.push("\n <tr>");
    a.push("\n <td><input style='height:15px' type='radio' class='radio' "+getChecked(plSwitchValue)+" id='" + plSwitchName + "1' name='" + plSwitchName + "' value='1' onclick=\""+ plSwitchOnClick + "(" + ssLiability + ");" + sCalcLayProfitsCall + "\"></td>");
    a.push("\n <td><label for='"+ plSwitchName + "1'><a href='#' onclick=\"" + plSwitchOnClick + "(" + ssLiability + ");" + sCalcLayProfitsCall + "\">" + parent.sLiabilityText + "</a></label></td>");
    a.push("\n </tr>");
    a.push("\n </table>");
    return a.join('');
}

function getMatchedTableHeader(bidType)
{

    var sOddsTitle = (bidType == top.main.bidTypeBACK)? parent.sBackersOdds :  parent.sLayersOdds;
    var sAmountTitle = (bidType == top.main.bidTypeBACK)? parent.sBackersAmount : parent.sLayersAmount;

	var cssClassName = "cell-title";

    var a = [];
    a.push("\n <table cellpadding='0' cellspacing='0' border='0' width='100%'>");

    a.push("\n <tr><td class='row-title'>");
    a.push("\n <table cellpadding='0' cellspacing='0' border='0'>");


	a.push("\n<tr>");
	a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='90px'></td>");
	a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='90px'></td>");
	a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='3px'></td>");
	a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='60px'></td>");
	a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='10px'></td>");
	a.push("\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='50px'></td>");
//	sTableHeader += "\n<td class='" + cssClassName + "'><img src='" + sSpacerImg +"' height='0px' width='100px'></td>";

	a.push("</tr>");

    a.push("\n <tr>");
    a.push("\n <td class='" + cssClassName + "'>" + sBetSelection + "</td>");
    a.push("\n <td class='" + cssClassName + "' width='90px'>" + sOddsTitle + "</td>");
    a.push("\n <td class='" + cssClassName + "' width='3px'>&nbsp;</td>");
    a.push("\n <td class='" + cssClassName + "' width='60px'>" + sAmountTitle + "&nbsp;" + parent.sCurrSign + "</td>");
    a.push("\n <td class='" + cssClassName + "' width='10px'>&nbsp;</td>");
/*
    sTableHeader += "\n <td class='" + cssClassName + "' width='60px'></td>";
*/
    if (bidType == top.main.bidTypeBACK)
    {
        a.push("<td colspan='2' style='text-align:left' class='" + cssClassName + "' width='160px' >" + parent.sYourProfit + "</td>");
    }
    else if (bidType == top.main.bidTypeLAY)
    {
        var sCalcLayProfitsCall =  "myCalcProfits(myMB4Lay, 'yourPL', 'plMBSwitch');"; //'odds','amount',
        var sPaymentLiabilityHeader = getPaymentLiabilityHeader("plMBSwitch", "setMBSwitchChecked", sCalcLayProfitsCall);
        a.push("<td colspan='2' style='text-align:left' class='" + cssClassName + "' width='160px' >" + sPaymentLiabilityHeader + "</td>");
    }

    a.push("\n </tr></table></td></tr>");
    return a.join('');
}

function getMatchedTableFooter() {
    return "</table>";
}

function getCurrentEventTitle () {
  return ( top.mainBar.document.getElementById("currentEventTitle") != null) ? top.mainBar.document.getElementById("currentEventTitle").innerHTML : "N/A";
}

function makeMyBetFormContainer_start(iEventId)
{
    var a = [];
    a.push("\n");
    a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' >");
    a.push("\n<tr><td><form name='myBetForm' action='" + sMyBetFormAction + "' method='POST'>");

    a.push("\n<input type=\"hidden\" name=\"stakeId\">");
    a.push("\n<input type=\"hidden\" name=\"eventId\">");
    a.push("\n<input type=\"hidden\" name=\"action\">");
    a.push('\n<input type=\"hidden\" name=\"betMarketName\" value="' + getCurrentEventTitle () + '">');
    a.push("\n<input type=\"hidden\" name=\"" + sTokenName + "\" value=\"" + sToken + "\">");
	a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%'>");
    return a.join('');
}

function makeMyBetFormContainer_end()
{
    return "</table></td></tr></form></table>";
}

function makeMyMBFormContainer_start(iEventId)
{
  return "\n<table cellpadding='0' cellspacing='0' border='0' width='100%' ><tr><td><form name='myMBForm' action='' >";
}

function makeMyMBFormContainer_end()
{
    return "</td></tr></form></table>"; //
}

function prepareArrayData()
{
    tempUB = new Array();

 myUB4Back = new Array();
 myUB4Lay  = new Array();

 tempUB = sUB.substring(0, sUB.length - 1).split("|");

    for (var i = 0; i < tempUB.length; i++)
    {
        tempUB[i] = tempUB[i].split("~");
        if (tempUB[i][TYPE] == top.main.bidTypeBACK) {
         myUB4Back[myUB4Back.length] = new Array(Number((tempUB[i][BET_ID]==0)?i+1:tempUB[i][BET_ID]), Number(tempUB[i][RESULT_ID]), Number(tempUB[i][TYPE]), tempUB[i][VALUE], Number(tempUB[i][ODDS]), Number(tempUB[i][AMOUNT]), tempUB[i][REG_DATE], parent.hintRedoBack +"\""+ tempUB[i][VALUE]+"\"", parent.hintDeleteBack +"\"" + tempUB[i][VALUE]+"\"");
        }
         else if (tempUB[i][TYPE] == top.main.bidTypeLAY) {
         myUB4Lay[myUB4Lay.length] = new Array(Number((tempUB[i][BET_ID]==0)?i+1:tempUB[i][BET_ID]), Number(tempUB[i][RESULT_ID]), Number(tempUB[i][TYPE]), tempUB[i][VALUE], Number(tempUB[i][ODDS]), Number(tempUB[i][AMOUNT]), tempUB[i][REG_DATE], parent.hintRedoLay +"\""+ tempUB[i][VALUE]+"\"", parent.hintDeleteLay +"\""+ tempUB[i][VALUE]+"\"");
        }
    }

  if (parent.sMatchedBets == ON) {
    tempMB = new Array();

    myMB4Back = new Array();
    myMB4Lay  = new Array();

    tempMB = sMB.substring(0, sMB.length - 1).split("|");

    for (var i = 0; i < tempMB.length; i++)
    {
        tempMB[i] = tempMB[i].split("~");
        if (tempMB[i][TYPE] == top.main.bidTypeBACK) {
          myMB4Back[myMB4Back.length] = new Array(Number((tempMB[i][BET_ID]==0)?i+1:tempMB[i][BET_ID]), Number(tempMB[i][RESULT_ID]), Number(tempMB[i][TYPE]), tempMB[i][VALUE], Number(tempMB[i][ODDS]), Number(tempMB[i][AMOUNT]), tempMB[i][REG_DATE]);
        }
        else if (tempMB[i][TYPE] == top.main.bidTypeLAY) {
          myMB4Lay[myMB4Lay.length] = new Array(Number((tempMB[i][BET_ID]==0)?i+1:tempMB[i][BET_ID]), Number(tempMB[i][RESULT_ID]), Number(tempMB[i][TYPE]), tempMB[i][VALUE], Number(tempMB[i][ODDS]), Number(tempMB[i][AMOUNT]), tempMB[i][REG_DATE]);
        }
    }
  }
}

function clearBetItems(betItems) {
    for (var i = betItems.length; i >=0; i--) {
        if (betItems[i] != null && betItems[i].length > 0) {
           for (var j = betItems[i].length-1; j >=0 ; j--) {
             betItems[i][j] = null;
             delete betItems[i][j];
           }
            betItems[i] = null;
            delete betItems[i];
        }
    }
}

//  "main" function
function makeMyBetsHTML(iEventId)
{
    prepareArrayData();
    parent.document.getElementById("cancelAllStakes").disabled = true;
    var myBetPage = top.betBorder.mybetPage;
    if (myBetPage)
    {
        var a = [];
        a.push("\n<table cellpadding='0' cellspacing='0' border='0' width='100%' >");
        if (itemsExist(myUB4Back) || itemsExist(myUB4Lay))
        {
			parent.document.getElementById("cancelAllStakes").disabled = false;
            a.push(getGroupHeader(sUnmatchedHeaderCaption));
        }

		a.push("\n <tr><td class='grid-border'><!-- start of unmatched bets -->");

        if (itemsExist(myUB4Back) || itemsExist(myUB4Lay))
        {
            a.push(makeMyBetFormContainer_start(iEventId));

            if (itemsExist(myUB4Back))
            {
				a.push(getUnmatchedTableHeader(top.main.bidTypeBACK));
				a.push(addUnmatchedItems(myUB4Back, iEventId));
            }

            if (itemsExist(myUB4Lay))
            {
				a.push(getUnmatchedTableHeader(top.main.bidTypeLAY));
				a.push(addUnmatchedItems(myUB4Lay, iEventId));
            }
            a.push(makeMyBetFormContainer_end());
		}
		a.push("\n</td></tr><!-- end of unmatched bets -->");

		if (parent.sMatchedBets == ON && (itemsExist(myMB4Back) || itemsExist(myMB4Lay)))
		{
			a.push(getGroupHeader(sMatchedHeaderCaption));
		}

        a.push("\n<tr><td class='grid-border'><!-- start of matched bets -->");
        if (parent.sMatchedBets == ON && (itemsExist(myMB4Back) || itemsExist(myMB4Lay)))
        {
            // myMBForm
            a.push(makeMyMBFormContainer_start(iEventId));
            if (itemsExist(myMB4Back))
            {
				a.push(getMatchedTableHeader(top.main.bidTypeBACK));
				a.push(addMatchedItems(myMB4Back));
				a.push(getMatchedTableFooter());
            }
            if (itemsExist(myMB4Lay))
            {
				a.push(getMatchedTableHeader(top.main.bidTypeLAY));
				a.push(addMatchedItems(myMB4Lay));
				a.push(getMatchedTableFooter());
            }
           a.push(makeMyMBFormContainer_end());
        }

        if (!itemsExist(myUB4Back) && !itemsExist(myUB4Lay) && iMBCount == 0 && !itemsExist(myMB4Back) && !itemsExist(myMB4Lay))
        {
            a.push(getBetsNotFound());
        }

        if (!itemsExist(myUB4Back) && !itemsExist(myUB4Lay) && iMBCount > 0 && ( parent.sMatchedBets != ON))
        {
            a.push(getUnmatchedBetsNotFound());
        }

      a.push("</td></tr></table>");
      a.push("<!--end of matched bets -->");

      myBetPage.document.getElementById("myBetContainer").innerHTML = a.join('');

      preCalcProfits();
//	   MyBetWin = MyBetHtml(sBetsHTML, MyBetWin) ;
    }
}
