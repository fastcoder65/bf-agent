var ShowCellsWin = null;

function MyBetHtml(sHTML, MyBetWin) {
   var winObj;

   winObj = openWin("", 800, 600, MyBetWin);
    if (winObj)
    with (winObj.document) {
        open();
        writeln(sHTML);
        close();
    }
   return winObj;
}

function debugPanel (sDebugMessage) {
  if (document.getElementById("debugPanel")) {
    document.getElementById("debugPanel").innerHTML = sDebugMessage;
  }
}

function ShowCells(debugInfo) {
    var betDoc = top.betBorder.betPage.document;
    var width = 600;
    var height = 400;
    var _left = (screen.width - width) / 2;
    var _top = (screen.height - height) / 2;

    ShowCellsWin = openWin("", 0, 0, ShowCellsWin, "location=0,scrollbars=1,resizable=1,width=" + width + ",height=" + height + ",top=" + _top + ",left=" + _left);

    if (ShowCellsWin) {
        var doc = ShowCellsWin.document;
        if (doc != null) {
            doc.open();
            doc.writeln("<html><head></head><body>");
            doc.writeln("<p align=\"center\">** data.length=" + debugInfo.length + " **</p><br>");
            doc.writeln("<table  cellpadding='1' cellspacing='1' border='0' width='100%'>");
            for (var i = 0; i < debugInfo.length; i++) {
                doc.writeln("<tr>");
                doc.writeln("<td>" + debugInfo[i] + "</td>");
                doc.writeln("</tr>");
            }
            doc.writeln("</table>");
            doc.writeln("</body></html>");
            doc.close();
        }
    }
}


function AllMarketsHtml() {

    AllMarketsHelpWin = window.open("", "tinyWindow", 'toolbar,width=600,height=400')

    if (AllMarketsHelpWin) {
        AllMarketsHelpWin.document.open();
        AllMarketsHelpWin.document.writeln(document.getElementById(allMarketsTreeContainer).innerHTML);
        AllMarketsHelpWin.document.close();
    }
}

function MyMarketsHtml() {

    MyMarketsHelpWin = openWin("", 600, 400);
    // , "My Markets Html"

    if (MyMarketsHelpWin) {
        MyMarketsHelpWin.document.open();
        MyMarketsHelpWin.document.writeln(document.getElementById(myMarketsTreeContainer).innerHTML);
        MyMarketsHelpWin.document.close();
    }
}
/*
<!--
<table>
<tr>
 <td>
  <a href="javascript:AllMarketsHtml()">All Markets html</a>
 </td>
</tr>
</table>
-->
*/

function validate3StyleDate(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var formName = form.getAttributeNode("name");

    oDate = eval('new ' + formName.value + '_date()');

    for (x in oDate) {
        var field = form[oDate[x][0]];
        var value = field.value;
        var datePattern = oDate[x][2]("datePatternStrict");
        // try loose pattern
        if (datePattern == null)
            datePattern = oDate[x][2]("datePattern");
        if ((field.type == 'hidden' ||
             field.type == 'text' ||
             field.type == 'textarea') &&
            (value.length > 0) && (datePattern.length > 0) &&
            field.disabled == false) {
            var MONTH = "MM";
            var DAY = "dd";
            var YEAR = "yyyy";
            var orderMonth = datePattern.indexOf(MONTH);
            var orderDay = datePattern.indexOf(DAY);
            var orderYear = datePattern.indexOf(YEAR);
            /*             if ((orderDay < orderYear && orderDay > orderMonth)) {
               var iDelim1 = orderMonth + MONTH.length;
               var iDelim2 = orderDay + DAY.length;
               var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
               var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
               if (iDelim1 == orderDay && iDelim2 == orderYear) {
                  dateRegexp = new RegExp("^(\\d{2})(\\d{2})(\\d{4})$");
               } else if (iDelim1 == orderDay) {
                  dateRegexp = new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$");
               } else if (iDelim2 == orderYear) {
                  dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$");
               } else {
                  dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$");
               }
               var matched = dateRegexp.exec(value);
               if(matched != null) {
                 var iValidResult = isValidDate(matched[2], matched[1], matched[3]);
                  if (-1 != iValidResult) {
                     if (i == 0) {
                         focusField = field;
                     }
                     if (1 != iValidResult) {
                      fields[i++] = oDate[x][1];
                     }
                     bValid =  false;
                  }
               } else {
                  if (i == 0) {
                      focusField = field;
                  }
                  fields[i++] = oDate[x][1];
                  bValid =  false;
               }
           } else*/
            if ((orderMonth < orderYear && orderMonth > orderDay)) {
                var iDelim1 = orderDay + DAY.length;
                var iDelim2 = orderMonth + MONTH.length;
                var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                if (iDelim1 == orderMonth && iDelim2 == orderYear) {
                    dateRegexp = new RegExp("^(\\d{2})(\\d{2})(\\d{4})$");
                } else if (iDelim1 == orderMonth) {
                    dateRegexp = new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$");
                } else if (iDelim2 == orderYear) {
                    dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$");
                } else {
                    dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$");
                }
                var matched = dateRegexp.exec(value);
                if (matched != null) {

                    var iValidResult = isValidDate(matched[1], matched[2], matched[3]);
                    if (-1 != iValidResult) {
                        if (i == 0) {
                            focusField = field;
                        }

                        if (1 != iValidResult) {
                            fields[i++] = oDate[x][1];
                        }
                        bValid = false;
                    }
                } else {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oDate[x][1];
                    bValid = false;
                }
            }
            /* else if ((orderMonth > orderYear && orderMonth < orderDay)) {
                var iDelim1 = orderYear + YEAR.length;
                var iDelim2 = orderMonth + MONTH.length;
                var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                if (iDelim1 == orderMonth && iDelim2 == orderDay) {
                    dateRegexp = new RegExp("^(\\d{4})(\\d{2})(\\d{2})$");
                } else if (iDelim1 == orderMonth) {
                    dateRegexp = new RegExp("^(\\d{4})(\\d{2})[" + delim2 + "](\\d{2})$");
                } else if (iDelim2 == orderDay) {
                    dateRegexp = new RegExp("^(\\d{4})[" + delim1 + "](\\d{2})(\\d{2})$");
                } else {
                    dateRegexp = new RegExp("^(\\d{4})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{2})$");
                }
                var matched = dateRegexp.exec(value);
                if(matched != null) {
                  var iValidResult = isValidDate(matched[3], matched[2], matched[1]);
                    if (-1 != iValidResult ) {
                        if (i == 0) {
                            focusField = field;
                        }

                        if (1 != iValidResult ) {
                         fields[i++] = oDate[x][1];
                        }

                        bValid =  false;
                    }
                } else {
                     if (i == 0) {
                         focusField = field;
                     }
                     fields[i++] = oDate[x][1];
                     bValid =  false;
                }
            }*/ else {
                if (i == 0) {
                    focusField = field;
                }
                fields[i++] = oDate[x][1];
                bValid = false;
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}

function origin_validateDate(form) {
   var bValid = true;
   var focusField = null;
   var i = 0;
   var fields = new Array();
   var formName = form.getAttributeNode("name");

   oDate = eval('new ' + formName.value + '_date()');

   for (x in oDate) {
       var field = form[oDate[x][0]];
       var value = field.value;
       var datePattern = oDate[x][2]("datePatternStrict");
       // try loose pattern
       if (datePattern == null)
           datePattern = oDate[x][2]("datePattern");
       if ((field.type == 'hidden' ||
            field.type == 'text' ||
            field.type == 'textarea') &&
           (value.length > 0) && (datePattern.length > 0) &&
            field.disabled == false) {
             var MONTH = "MM";
             var DAY = "dd";
             var YEAR = "yyyy";
             var orderMonth = datePattern.indexOf(MONTH);
             var orderDay = datePattern.indexOf(DAY);
             var orderYear = datePattern.indexOf(YEAR);
             if ((orderDay < orderYear && orderDay > orderMonth)) {
                 var iDelim1 = orderMonth + MONTH.length;
                 var iDelim2 = orderDay + DAY.length;
                 var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                 var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                 if (iDelim1 == orderDay && iDelim2 == orderYear) {
                    dateRegexp = new RegExp("^(\\d{2})(\\d{2})(\\d{4})$");
                 } else if (iDelim1 == orderDay) {
                    dateRegexp = new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$");
                 } else if (iDelim2 == orderYear) {
                    dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$");
                 } else {
                    dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$");
                 }
                 var matched = dateRegexp.exec(value);
                 if(matched != null) {
                   var iValidResult = isValidDate(matched[2], matched[1], matched[3]);
                    if (-1 != iValidResult) {
                       if (i == 0) {
                           focusField = field;
                       }
                       if (1 != iValidResult) {
                        fields[i++] = oDate[x][1];
                       }
                       bValid =  false;
                    }
                 } else {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oDate[x][1];
                    bValid =  false;
                 }
             } else if ((orderMonth < orderYear && orderMonth > orderDay)) {
                 var iDelim1 = orderDay + DAY.length;
                 var iDelim2 = orderMonth + MONTH.length;
                 var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                 var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                 if (iDelim1 == orderMonth && iDelim2 == orderYear) {
                     dateRegexp = new RegExp("^(\\d{2})(\\d{2})(\\d{4})$");
                 } else if (iDelim1 == orderMonth) {
                     dateRegexp = new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$");
                 } else if (iDelim2 == orderYear) {
                     dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$");
                 } else {
                     dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$");
                 }
                 var matched = dateRegexp.exec(value);
                 if(matched != null) {

                   var iValidResult = isValidDate(matched[1], matched[2], matched[3]);
                     if (-1 != iValidResult) {
                         if (i == 0) {
                           focusField = field;
                         }

                         if (1 != iValidResult) {
                          fields[i++] = oDate[x][1];
                         }
                         bValid =  false;
                      }
                 } else {
                     if (i == 0) {
                         focusField = field;
                     }
                     fields[i++] = oDate[x][1];
                     bValid =  false;
                 }
             } else if ((orderMonth > orderYear && orderMonth < orderDay)) {
                 var iDelim1 = orderYear + YEAR.length;
                 var iDelim2 = orderMonth + MONTH.length;
                 var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                 var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                 if (iDelim1 == orderMonth && iDelim2 == orderDay) {
                     dateRegexp = new RegExp("^(\\d{4})(\\d{2})(\\d{2})$");
                 } else if (iDelim1 == orderMonth) {
                     dateRegexp = new RegExp("^(\\d{4})(\\d{2})[" + delim2 + "](\\d{2})$");
                 } else if (iDelim2 == orderDay) {
                     dateRegexp = new RegExp("^(\\d{4})[" + delim1 + "](\\d{2})(\\d{2})$");
                 } else {
                     dateRegexp = new RegExp("^(\\d{4})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{2})$");
                 }
                 var matched = dateRegexp.exec(value);
                 if(matched != null) {
                   var iValidResult = isValidDate(matched[3], matched[2], matched[1]);
                     if (-1 != iValidResult ) {
                         if (i == 0) {
                             focusField = field;
                         }

                         if (1 != iValidResult ) {
                          fields[i++] = oDate[x][1];
                         }

                         bValid =  false;
                     }
                 } else {
                      if (i == 0) {
                          focusField = field;
                      }
                      fields[i++] = oDate[x][1];
                      bValid =  false;
                 }
             } else {
                 if (i == 0) {
                     focusField = field;
                 }
                 fields[i++] = oDate[x][1];
                 bValid =  false;
             }
      }
   }
   if (fields.length > 0) {
      focusField.focus();
      alert(fields.join('\n'));
   }
   return bValid;
}

//@@@

function validateDate(form) {
    var bValid = true;
    var focusField = null;
    var i = 0;
    var fields = new Array();
    var formName = form.getAttributeNode("name");

    oDate = eval('new ' + formName.value + '_date()');

    for (x in oDate) {
        var field = form[oDate[x][0]];
        var value = field.value;
        var datePattern = oDate[x][2]("datePatternStrict");
        // try loose pattern
        if (datePattern == null)
            datePattern = oDate[x][2]("datePattern");
        if ((field.type == 'hidden' ||
             field.type == 'text' ||
             field.type == 'textarea') &&
            (value.length > 0) && (datePattern.length > 0) &&
            field.disabled == false) {
            var MONTH = "MM";
            var DAY = "dd";
            var YEAR = "yyyy";
            var orderMonth = datePattern.indexOf(MONTH);
            var orderDay = datePattern.indexOf(DAY);
            var orderYear = datePattern.indexOf(YEAR);
            if ((orderMonth < orderYear && orderMonth > orderDay)) {
                var iDelim1 = orderDay + DAY.length;
                var iDelim2 = orderMonth + MONTH.length;
                var delim1 = datePattern.substring(iDelim1, iDelim1 + 1);
                var delim2 = datePattern.substring(iDelim2, iDelim2 + 1);
                if (iDelim1 == orderMonth && iDelim2 == orderYear) {
                    dateRegexp = new RegExp("^(\\d{2})(\\d{2})(\\d{4})$");
                } else if (iDelim1 == orderMonth) {
                    dateRegexp = new RegExp("^(\\d{2})(\\d{2})[" + delim2 + "](\\d{4})$");
                } else if (iDelim2 == orderYear) {
                    dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})(\\d{4})$");
                } else {
                    dateRegexp = new RegExp("^(\\d{2})[" + delim1 + "](\\d{2})[" + delim2 + "](\\d{4})$");
                }
                var matched = dateRegexp.exec(value);
                if (matched != null) {
                    var iValidResult = isValidDate(matched[1], matched[2], matched[3]);
                    if (-1 != iValidResult) {
                        if (i == 0) {
                            focusField = field;
                        }
                        if (1 != iValidResult) {
                            fields[i++] = oDate[x][1];
                        }
                        bValid = false;
                    }
                } else {
                    if (i == 0) {
                        focusField = field;
                    }
                    fields[i++] = oDate[x][1];
                    bValid = false;
                }
            } else {
                if (i == 0) {
                    focusField = field;
                }
                fields[i++] = oDate[x][1];
                bValid = false;
            }
        }
    }
    if (fields.length > 0) {
        focusField.focus();
        alert(fields.join('\n'));
    }
    return bValid;
}


