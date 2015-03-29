var ENTER_KEY = 13;
var COOKIE_LIVE_HOURS = 24;

function DoRnd(Np, wR) {
    if (Np == 0)return 0;
    return Math.round(Np * Math.pow(10, wR)) / Math.pow(10, wR);
}

function formatCurrency(currencySymbol, aNumber) {
    return isNaN(aNumber) ? "" : ((aNumber.toFixed) ? currencySymbol + formatCommas("" + aNumber.toFixed(2)) : formatCurrency_Old(currencySymbol, aNumber));
}

function formatNumber(aNumber, decPlaces, prec) {
    var _prec = prec ? prec : 0;
    var num = parseFloat(aNumber);
    var strNum = isNaN(num) ? "" : ((num.toFixed) ? num.toFixed(_prec) : formatWholeNumber(num, decPlaces));
    while (strNum.length <= decPlaces) {
        strNum = "0" + strNum;
    }
    return strNum;
}

function focusNext(form, elemName, evt) {
    evt = (evt) ? evt : event;
    var charCode = (evt.charCode) ? evt.charCode : ((evt.which) ? evt.which : evt.keyCode);
    if (charCode == ENTER_KEY) {
        form.elements[elemName].focus();
        return false;
    }
    return true;
}
function formatCurrency_Old(Sr, Ib) {
    var Pa = DoRnd(Ib, 2);
    FZ = (Number(Pa) >= 0) ? Sr : "-" + Sr;
    Pa = (Number(Pa) == Math.floor(Number(Pa))) ? Math.abs(Pa) + '.00' : ((Number(Pa) * 10 == Math.floor(Number(Pa) * 10)) ? Math.abs(Pa) + '0' : Math.abs(Pa));
    return FZ + formatCommas(Pa);
}
function formatWholeNumber(Ib, wR) {
    var Pa;
    if (Ib == Math.round(Ib))return Ib; else
    {
        Pa = (Number(Ib) == Math.floor(Number(Ib))) ? Math.abs(Ib) + '.00' : ((Number(Ib) * 10 == Math.floor(Number(Ib) * 10)) ? Math.abs(Ib) + '0' : Math.abs(Ib));
        return Pa;
    }
}
function processOnClickForSubmit(element, disable)
{
    if (element == null || element.disabled)
    {
        return false;
    }
    if (disable)
    {
        element.disabled = true;
    }
    return true;
}
function formatCommas(numString) {
    var re = /(-?\d+)(\d{3})/;
    while (re.test(numString)) {
        numString = numString.replace(re, "$1,$2");
    }
    return numString;
}
function stripCommas(numString) {
    var re = /,/g;
    return numString.replace(re, "");
}

function setCookie(cookieName,cookieValue) {
 var today = new Date();
 var expire = new Date();
 expire.setTime(today.getTime() + 3600000*COOKIE_LIVE_HOURS);
 document.cookie = cookieName+"="+escape(cookieValue)
                 + ";expires="+expire.toGMTString();
}

function getCookie(name) {
	var cookie = " " + document.cookie;
	var search = " " + name + "=";
	var setStr = null;
	var offset = 0;
	var end = 0;
	if (cookie.length > 0) {
		offset = cookie.indexOf(search);
		if (offset != -1) {
			offset += search.length;
			end = cookie.indexOf(";", offset)
			if (end == -1) {
				end = cookie.length;
			}
			setStr = unescape(cookie.substring(offset, end));
		}
	}
	return(setStr);
}

//fighting agains F5 bug in FF //we intercept F5 Ctrl+R Ctrl+r on this page in FF for skip FF reaction
/*
 * Intercept keys if intercepting condition is true and invokes the related submit input
 * (if one exists). This method must be invoked when a "keydown" event occurs
 */
function interceptKeyDown(e) {
 var keyCode = e.keyCode;
 if( interceptCond(keyCode, e.charCode, e.ctrlKey) ) {
  e.keyCode = 505;
  clickButton( keyCode );
  return false;
 }
}
var asciiF1 = 112;
var asciiF5 = 116;
var asciiF12 = 123;
var asciiPgUp = 33;
var asciiHome = 36;
var asciiR = 82;
var asciir = 114;
/*
 * Intercept Intercept keys if intercepting condition is true and invokes the related submit input
 * (if one exists). This method must be invoked when a "keypress" event occurs
 */
function interceptKeyPress(e) {
 if( !e ) {
  if( window.event ) e = window.event;
  else return;
 }
 //NS 4, NS 6+, Mozilla 0.9+, Opera
 if( typeof( e.which ) == 'number' ) {
  var keyCode = e.keyCode ? e.keyCode : e.which ? e.which : void 0;
  //alert("keyCode="+keyCode+" e.charCode="+e.charCode);
  if( interceptCond(keyCode, e.charCode, e.ctrlKey) ) {
     e.stopPropagation();
     e.preventDefault();
     clickButton( keyCode );
  }
 }
}
/*
 * According to the type of browser, adds a new event listener to
 * the specified object ,for the requested event, binded to the
 * specific function.
 */
function attachEventListener( obj, type, func, capture ) {
 if(window.addEventListener) { // Mozilla, Netscape, Firefox
  obj.addEventListener( type, func, capture );
 } else { // IE
   obj.attachEvent( 'on' + type, func );
 }
}
//the function clickButton( keyCode ) changes according to your specifications
function clickButton( keyCode ) { return keyCode;}
function interceptCond(keyCode, charCode, ctrlKey) { //true if F5 or Ctrl+r or Ctrl+R
  var ch = null;
  if(charCode) ch = String.fromCharCode(charCode);
  else if(charCode == undefined) ch = String.fromCharCode(keyCode); //for IE
  //alert(ch+" "+keyCode+" "+charCode+" "+ctrlKey);
  if(    (!charCode) && keyCode == asciiF5 && !ctrlKey
      || (ch == 'r' || ch == 'R') && ctrlKey ) { //charCode == asciiR || charCode == asciir
    return true;
  } else {
    return false;
  }
}

// to use 'interceptKeyDown' or 'interceptKeyPress' uncomment code listed below  
/*
if (navigator.appName == 'Netscape') {
    attachEventListener(document, "keydown", interceptKeyDown, true);
    attachEventListener(document, "keypress", interceptKeyPress, true);
}
*/
