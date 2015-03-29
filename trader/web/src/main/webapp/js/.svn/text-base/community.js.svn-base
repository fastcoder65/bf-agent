
function getPrevPage() {
    if (pageNum >1) pageNum--;
    getPage(pageNum);
}

function getNextPage() {
    if (pageNum < pageCount) pageNum ++;
    getPage(pageNum);
}

function getPage(nPage) {
    if (nPage && (nPage >= 1) && (nPage<=pageCount)) pageNum = nPage;
    var searchForm = document.forms.viewMessagesList;
    searchForm.listType.value = listType;
    searchForm.pageNum.value = pageNum;
    searchForm.messageArea.value = document.forms.messageForm.messageArea.value;
    searchForm.submit();
}

function doSearch() {
    var searchForm = document.forms.viewMessagesList;
    var filter = searchForm.filterKey.value;
    searchForm.listType.value = listType;
    searchForm.pageNum.value = 1;
    searchForm.messageArea.value = document.forms.messageForm.messageArea.value;
    searchForm.submit();
}

function do_onload() {
    var isDisabledFirst = (pageNum == 1);
    var isDisabledLast  = (pageNum >= pageCount);
  //  document.getElementById("messageArea").wrap = "soft";  //"soft"|"hard"|"off"
}

function checkCharsLimit(thisCtrl) {
   var form = thisCtrl.form;
    if( getCharCount(form) > charsLimit ) {
        alert(sCharsLimitMessage);
        return false;
    }
   return true;
}

function getCharCount(thisForm) {
 var sValue = thisForm.messageArea.value;
 var vals = sValue.split('\r');
 sValue = (vals != null)?sValue.replace(/\r/gi, ""):sValue;
 return sValue.length;
}

function getChar2Send(thisForm) {
 var sValue = thisForm.messageArea.value;
 var vals = sValue.split('\r');
 sValue = (vals != null)?sValue.replace(/\r/gi, ""):sValue;
  return sValue;
}


function showCharsCount() {
 var thisForm = document.forms.messageForm;
 if (document.getElementById("charsCount")) {
    document.getElementById("charsCount").innerHTML = getCharCount(thisForm);
  }
}

function doSendMsg(form) {
    if (form.messageArea.value== null || form.messageArea.value.trim().length == 0) {
        alert(sEmptyMessage);
        return;
    }
    if( getCharCount(form) > charsLimit ) {
        alert(sCharsLimitMessage);
        return;
    }
    form.submit();
}
