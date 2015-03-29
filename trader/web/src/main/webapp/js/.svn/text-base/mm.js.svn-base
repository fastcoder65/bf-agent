
var __isDOM=document.getElementById && document.getElementsByTagName; //DOM1 browser (MSIE 5+, Netscape 6, Opera 5+)

var __isMozilla=__isDOM
        && (navigator.userAgent.toLowerCase().indexOf("mozilla") != -1)
        && (navigator.appName.toLowerCase().indexOf("microsoft") == -1); //navigator.appName=="Netscape" //Mozilla или Netscape 6.*

function isMozilla()
{
    return __isMozilla;
}

function MM_findObj(n, d) { //v4.01
    var p, x;

    if (!d)
        d = document;
    if ((p = n.indexOf("?")) > 0 && parent.frames.length) {
        d = parent.frames[n.substring(p + 1)].document;
        n = n.substring(0, p);
    }

    if (!x && d.getElementById) x = d.getElementById(n);

    for (var i = 0; !x && i < d.forms.length; i++)
        x = d.forms[i][n];

    for (var i = 0; !x && d.layers && i < d.layers.length; i++)
        x = MM_findObj(n, d.layers[i].document);

    if (!x && d.getElementById)
        x = d.getElementById(n);

    return x;
}

function MM_showHideLayers() { //v6.0
    var p,v,obj,args = MM_showHideLayers.arguments;
    for (var i = 0; i < (args.length - 2); i += 3) if ((obj = MM_findObj(args[i])) != null) {
        v = args[i + 2];
        if (obj.style) {
            obj = obj.style;
            v = (v == 'show') ? 'visible' : (v == 'hide') ? 'hidden' : v;
        }
        obj.visibility = v;
    }
}

function MM_changeProp(objName, x, theProp, theValue) { //v6.0
    var obj = MM_findObj(objName);
    if (obj && (theProp.indexOf("style.") == -1 || obj.style)) {

        if (theValue == true || theValue == false)
            eval("obj." + theProp + "=" + theValue);

        else eval("obj." + theProp + "='" + theValue + "'");
    }
}

function showItem(id) {
    var cellid = "div_" + id;
    if (document.getElementById(cellid)
            && (document.getElementById(cellid).innerHTML == null
            || document.getElementById(cellid).innerHTML.length == 0 )) {
      if (window.loadContainerData ) {  
       loadContainerData(id);
      }
    }

    MM_showHideLayers(cellid, '', 'show');
    MM_changeProp(cellid, '', 'style.overflow', 'visible', 'DIV');
    if (navigator.family == 'netscape' || navigator.family == 'gecko') {
        MM_changeProp(cellid, '', 'style.height', 'auto', 'DIV')
    }
}

function hideItem(id) {
    var cellid = "div_" + id;
    MM_showHideLayers(cellid, '', 'hide');
    MM_changeProp(cellid, '', 'style.overflow', 'hidden', 'DIV');
    MM_changeProp(cellid, '', 'style.height', '1', 'DIV')
}

function switchItem(id) {
    var cellid = "div_" + id;
    var imageItem = "imgItem_" + id;
    var titleItem = 'td_' + id;

    if (document.getElementById(cellid)) {
        if (isMozilla()) {
           // display:table-row-group
            document.getElementById(cellid).style.display = "table-row-group";
        }
        
        if (document.getElementById(cellid).style.overflow == "hidden") {
           if (document.getElementById(imageItem)) {
            document.getElementById(imageItem).src = arrowImages['hidden'].src;
            document.getElementById(imageItem).alt = sHideItemTitle;
           }
            if (document.getElementById(titleItem)) {
             document.getElementById(titleItem).title = sHideItemTitle;
            }
            showItem(id);
        } else if (document.getElementById(cellid).style.overflow == "visible") {
           if (document.getElementById(imageItem)) {
            document.getElementById(imageItem).src = arrowImages['visible'].src;
            document.getElementById(imageItem).alt = sShowItemTitle;
           }
           if (document.getElementById(titleItem)) {
            document.getElementById(titleItem).title = sShowItemTitle;
           }
            hideItem(id)
        }
    }
}

