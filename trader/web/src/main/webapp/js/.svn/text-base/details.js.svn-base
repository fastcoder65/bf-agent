var sRowClass = "row";
var sBackRowOddClass = "row-back-odd";
var sBackRowEvenClass = "row-back-even";

var sLayRowOddClass = "row-lay-odd";
var sLayRowEvenClass = "row-lay-even";

function closeDown() {
       window.close();
}

function ajustDivSize() {
    if (bReDraw && IE) {
        bReDraw = false;

        if (document.body) {
            if (document.body.offsetHeight > 0) {
                var newHeight = document.body.offsetHeight
                        - 2
                        - document.getElementById("top-bar").offsetHeight
                        - document.getElementById("bottom-bar").offsetHeight;

                if (newHeight > 0) {
                    if (document.getElementById("rowsContainer")) {
                        document.getElementById("rowsContainer").style.height = newHeight + "px";
                    }
                }
            }

            if (document.body.offsetWidth > 0) {
                if (document.getElementById("rowsContainer")) {
                    document.getElementById("rowsContainer").style.width = (document.body.offsetWidth - 2) + "px";
                }
            }
        }
     //   doResize(window);
        setTimeout("bReDraw = true", 350);
    }
}

function AdjustDivWidthMozilla() {
    if (bReDraw && isMozilla()) {
        bReDraw = false;
        if (document.body) {

            if (document.body.offsetHeight > 0) {
                var newHeight = document.body.offsetHeight
                        - 2
                        - document.getElementById("top-bar").offsetHeight
                        - document.getElementById("bottom-bar").offsetHeight;

                if (newHeight > 0) {
                    if (document.getElementById("rowsContainer")) {
                        document.getElementById("rowsContainer").style.height = newHeight + "px";
                    }
                }
            }
            if (document.body.offsetWidth > 0) {
                if (document.getElementById("rowsContainer")) {
                    document.getElementById("rowsContainer").style.width = (document.body.offsetWidth - 2) + "px";
                }
            }
        }
      //  doResize(window);
        setTimeout("bReDraw = true", 350);
    }
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


function prepareRowData(dataRows) {
    for (var i = 0; i < dataRows.length; i++) {
        dataRows[i] = dataRows[i].split("~");
    }
}
