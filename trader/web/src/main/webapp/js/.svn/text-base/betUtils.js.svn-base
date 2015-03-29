
var IDX_NOT_FOUND = -2;

function findOddsIdx(curOdds, sOrigin) {

    var curIndex = IDX_NOT_FOUND;

    var firstIdx = 0;
    var lastIdx = betOdds.length - 1;
    var tmpIdx = 0;

    if (curOdds < betOdds[0]) {
        curIndex = -1;
    } else if (curOdds > betOdds[lastIdx]) {
        curIndex = betOdds.length;
    } else {
        do {
            // alert("firstIdx=" + firstIdx +  " , lastIdx=" + lastIdx + " , tmpIdx=" + tmpIdx);
            flSum = firstIdx + lastIdx;
            tmpIdx = ((flSum % 2) == 0)?flSum / 2 - 1:((flSum - 1) / 2) - 1;
            if (tmpIdx == firstIdx) tmpIdx++;
            if (curOdds > betOdds[tmpIdx]) {
                firstIdx = tmpIdx;
            } else {
                lastIdx = tmpIdx;
            }
        } while (firstIdx < lastIdx - 1);

        curIndex = lastIdx;

        if (curOdds != betOdds[curIndex]) {
            if (sOrigin == "oddsDown") {
                curIndex = lastIdx;
            } else if (sOrigin == "oddsUp") {
                curIndex = firstIdx;
            }
        }
        //	 alert ("found nearest less odds: curIndex="+ curIndex+ " ,betOdds["+curIndex+"]="+ betOdds[curIndex]);
    }
    return curIndex;
}


function findOddsIndex(strOdds, sOrigin) {
    var nonDigitPat = /[^0-9\.]/g;
    var dotPat = /[\.]+(?=[^\.]*[\.]+.*)/g;
    var currentIndex = IDX_NOT_FOUND;
    var sOdds = "";
    if (strOdds != null && strOdds != "") {
        sOdds = (nonDigitPat.test(strOdds))?strOdds.replace(nonDigitPat, ""):strOdds;
    }

    if (dotPat.test(sOdds)) {
        sOdds = sOdds.replace(dotPat, "");
    }
    if (sOdds != "") {
        var currentOdds = Number(sOdds);
        if (isNaN(currentOdds)) {

        } else {
            currentIndex = findOddsIdx(currentOdds, sOrigin);
        }
    }
    return  currentIndex;
}

function oddsUp(elementId, sFrameName) {
    if (sFrameName == null) {
      //  var Document = window.frames["betPage"].document;
        var Document = window.document;
    } else {
        var Document = window.frames[sFrameName].document;
        if (sFrameName == "mybetPage") {
            frames.mybetPage.clearBetUpdateTimeOut(Document.getElementById(elementId));
        }
    }
    if (!Document) return;
    //    debug();
    var currentIndex = findOddsIndex(Document.getElementById(elementId).value, "oddsUp");

    if (currentIndex != IDX_NOT_FOUND) {
        if (currentIndex < betOdds.length - 1) {
            Document.getElementById(elementId).value = betOdds[currentIndex + 1];
        } else {
            Document.getElementById(elementId).value = betOdds[betOdds.length - 1];
        }
    } else {
        Document.getElementById(elementId).value = betOdds[0];
    }
}

function oddsDown(elementId, sFrameName) {
    if (sFrameName == null) {
      //  var Document = window.frames["betPage"].document;
          var Document = window.document;
    } else {
        var Document = window.frames[sFrameName].document;
        if (sFrameName == "mybetPage") {
            frames.mybetPage.clearBetUpdateTimeOut();
        }
    }

    if (!Document) return;
    //  debug();
    var currentIndex = findOddsIndex(Document.getElementById(elementId).value, "oddsDown");

    if (currentIndex != IDX_NOT_FOUND) {
        if (currentIndex > 0) {
            Document.getElementById(elementId).value = betOdds[currentIndex - 1];
        } else {
            Document.getElementById(elementId).value = betOdds[0];
        }
    }
    else {
        Document.getElementById(elementId).value = betOdds[0];
    }
}
