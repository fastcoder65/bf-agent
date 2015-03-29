var oBetStakesInfoWin = null;

var RESULT_ID   = 0;
var RESULT_NAME = 1;
var RESULT_IS_HAPPEN = 2;
var RESULT_DESC = 3;

var IS_HAPPEN = 1;

function openStakesPerBetInfo( betId ) {
 if (betId)
  oBetStakesInfoWin = openTraderInfoWin( sBetStakesInfoUrl + "?betId=" + betId, oBetStakesInfoWin, "BetStakesInfo");
}

function loadContainerData ( betId ) {
 if ( betId )
    window.frames["dataFrame"].location.href = sResultsInfoUrl + "?betId="+ betId;
}

function getStakeLoginTipTableHeader() {
  var a = [];
  a.push('<table cellpadding="0" cellspacing="0" border="0">'); // removed height='100%' for CSH-27
  a.push('\n<tr>');
  a.push('\n<td class="area-border" valign="top">');
  a.push("<table cellpadding='0' cellspacing='0' border='0' width='100%'>");
  return a.join('');
}

function getStakeLoginTipTableBody ( sdDataRows ) {
    var a = [];
    var sRowClass = "row-free";
    for (var i = 0; i < sdDataRows.length; i++) {
        a.push('\n<tr id="result_'+ sdDataRows[i][RESULT_ID] + '">');
        var sResultCommentAsTitle = ((sdDataRows[i][RESULT_DESC] == null || sdDataRows[i][RESULT_DESC].length == 0)? "" : 'title="' + sdDataRows[i][RESULT_DESC] + '"');
        var sResultName = (sdDataRows[i][RESULT_IS_HAPPEN] == IS_HAPPEN) ? "<b><i>"+ sdDataRows[i][RESULT_NAME] + "</i></b>": sdDataRows[i][RESULT_NAME];
        a.push('\n<td ' + sResultCommentAsTitle + ' nowrap>' + sResultName + '</td>');
        a.push('\n</tr>');
    }
  return a.join('');
}

function getStakeLoginTipTableFooter() {
  return'</table></td></tr></table>';
}

function prepareRowData(dataRows) {
    for (var i = 0; i < dataRows.length; i++) {
        dataRows[i] = dataRows[i].split("~");
    }
}

function makeResultsContent ( dataRows, sContainerName ) {
    prepareRowData ( dataRows );
    var a = [];
    a.push(getStakeLoginTipTableHeader());
    a.push(getStakeLoginTipTableBody ( dataRows ));
    a.push(getStakeLoginTipTableFooter());
    if (document.getElementById( sContainerName )) {
        document.getElementById( sContainerName ).innerHTML = a.join('');
    }
}
