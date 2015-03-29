var DebugWin = null;

function getColumnSizes() {
    var a = [];
    if (getTableColumnSizes) {
        var columnSizes = getTableColumnSizes();
        if (columnSizes && columnSizes.length > 0) {
            for (var i = 0; i < columnSizes.length; i++) {
                var columnWidth = (columnSizes[i] == null) ? '' : 'width="' + columnSizes[i] + 'px"';
                a.push('\n<td><img src="' + sSpacer + '" height="1px" ' + columnWidth + ' ></td>');
            }
        }
        //         sHeader += '\n<td width="0px"></td>';
    }
    return a.join('');
}

/*
function getTableHeader() {
    var sHeader = '<table cellpadding="0" cellspacing="0" border="0" width="100%">';
    sHeader += '\n<tr>';
    sHeader += '\n<td class="rows-border-prof-title" valign="top" id="table_title">'; //
    sHeader += '\n<table id="tableHeader" cellpadding="0" cellspacing="0" border="0" width="100%" >';
    sHeader += '\n<tr id="title_sizer">';

    sHeader += getColumnSizes();
    sHeader += '\n</tr>';
    sHeader += '\n<tr id="titles" >';

    if (getTableCaptions)  sHeader += getTableCaptions();

    //sHeader += '\n<td width="0px"></td>';
    sHeader += '\n</tr></table></td></tr>';
    sHeader += '\n<tr><td class="rows-border-prof-body">'; //
    sHeader += '\n<div id="divScrollBody" style="width:100%;left:0;top:0;overflow:auto; overflow-y:auto;overflow-x:hidden;border-top:0;border-left:0;border-right:0;border-bottom:0;">';
    sHeader += '\n<table id="scrollBody" cellpadding="0" cellspacing="0" border="0" width="100%" >';
    sHeader += '\n<tr id="row_sizer">';

    sHeader += getColumnSizes();
    sHeader += '\n</tr>';
    return sHeader;
}

function getTableFooter() {
    return '</table></div></td></tr></table>';
}
*/

function getTableHeader() {
    var a = [];
    a.push('<table cellpadding="0" cellspacing="0" border="0" width="100%">');

    a.push('\n<tr>');
    a.push('\n<td class="rows-border-prof-title" valign="top" id="table_title">');

    a.push('\n<table id="tableHeader" cellpadding="0" cellspacing="0" border="0" width="100%" >');

    a.push('\n<tr id="titles" >');

    if (getTableCaptions)  a.push(getTableCaptions());

    a.push('\n</tr>');

    return a.join('');
}

function getTableFooter() {
    return '</table></td></tr></table>';
    // </table></div></td></tr>
}

function prepareRowData(dataRows) {
    for (var i = 0; i < dataRows.length; i++) {
        dataRows[i] = dataRows[i].split("~");
    }
}

function showContent()
{
    document.getElementById('divBorderLoading').style.display = "none";
    document.getElementById('divBorder').style.display = "block";
}

function hideContent()
{
    document.getElementById('divBorderLoading').style.display = "block";
    document.getElementById('divBorder').style.display = "none";
}

function ajustDivSize() {
    if (bReDraw && parent.IE && parent.doResize) {
        bReDraw = false;
        if (document.body) {
            if (document.body.offsetHeight > 0) {
                if (sReportName == "statement") {
                    var newHeight = document.body.offsetHeight
                            - 4
                            - document.getElementById("top-bar").offsetHeight
                            - document.getElementById("bottom-bar").offsetHeight

                            - document.getElementById("combo_panel").offsetHeight
                            - document.getElementById("statement_notes").offsetHeight;
                    if (newHeight > 0) {
                        if (document.getElementById("divBorder")) {
                            document.getElementById("divBorder").style.height = newHeight + "px";
                        }
                    }
                }
                else {
                    var newHeight = document.body.offsetHeight
                            - 4
                            - document.getElementById("top-bar").offsetHeight
                            - document.getElementById("bottom-bar").offsetHeight

                            - document.getElementById("combo_panel").offsetHeight;

                    if (newHeight > 0) {
                        if (document.getElementById("divBorder")) {
                            document.getElementById("divBorder").style.height = newHeight + "px";
                        }
                    }
                }
            }
            if (document.body.offsetWidth > 0) {
                if (document.getElementById("divBorder")) {
                    document.getElementById("divBorder").style.width = (document.body.offsetWidth - 2) + "px";
                }
            }
        }
      //  doResize(parent.window);
        setTimeout("bReDraw = true", 350);
    }
}


function displayLastRedraw() {
    if (document.getElementById("EventName")) {
        document.getElementById("EventName").innerHTML += ", last.bReDraw=" + bReDraw;
    }
}

function AdjustDivWidthMozilla() {
    if (bReDraw && isMozilla()) {
        bReDraw = false;
        if (document.body) {
            if (document.body.offsetHeight > 0) {
                if (sReportName == "statement") {
                    var newHeight = document.body.offsetHeight
                            - 4
                            - document.getElementById("top-bar").offsetHeight
                            - document.getElementById("bottom-bar").offsetHeight
                                    
                            - document.getElementById("combo_panel").offsetHeight
                            - document.getElementById("statement_notes").offsetHeight;

                    if (newHeight > 0) {
                        if (document.getElementById("divBorder")) {
                            document.getElementById("divBorder").style.height = newHeight + "px";
                        }
                    }
                }
                else {
                    var newHeight = document.body.offsetHeight
                            - 4
                            - document.getElementById("top-bar").offsetHeight
                            - document.getElementById("bottom-bar").offsetHeight
                                    
                            - document.getElementById("combo_panel").offsetHeight;

                    if (newHeight > 0) {
                        if (document.getElementById("divBorder")) {
                            document.getElementById("divBorder").style.height = newHeight + "px";
                        }
                    }
                }
            }
            if (document.body.offsetWidth > 0) {
                if (document.getElementById("divBorder")) {
                    document.getElementById("divBorder").style.width = (document.body.offsetWidth - 2) + "px";
                }
            }
        }
      //  doResize(parent.window);
        setTimeout("bReDraw = true", 350);
    }
}

function getElementCompValueOfProperty(elementId, propertyName) {
    return parseInt(document.defaultView.getComputedStyle(document.getElementById(elementId), null).getPropertyValue(propertyName), 0);
}

function AdjustDivWidthForOpera7() {
    document.getElementById("tableHeader").style.width = document.getElementById("divScrollBody").offsetWidth - 17 + "px";
}

function initSizes() {
    if (document.getElementsByTagName && document.all) {
        ajustDivSize();
        document.body.onresize = ajustDivSize;
    } else if (window.addEventListener) {
        AdjustDivWidthMozilla();
        window.addEventListener("resize", AdjustDivWidthMozilla, false);
    } else if (document.addEventListener && window.opera) {
        //   AdjustDivWidthForOpera7();
        //   document.addEventListener("resize", AdjustDivWidthForOpera7, false);
    }
}

function makeHTMLContent(dataRows) {
    prepareRowData(dataRows);
    var a = [];
    a.push(getTableHeader());
    a.push(getTableBody(dataRows));
    a.push(getTableFooter());
    //   DebugWin = MyBetHtml(sBetHistory, DebugWin);
    if (document.getElementById("divBorder")) {
        document.getElementById("divBorder").innerHTML = a.join('');
        document.close();
    }
    showContent();
    initSizes();
}

