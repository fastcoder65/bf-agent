var _left;
var _left_nav;
var arr_cols;

function doCollapseLeft()
{
/*
    var __top = eval("top.window");
    _left = '0';
    _left_nav = '20';
    __top.document.getElementsByTagName("frameset")[2].cols = _left_nav + ',' + _left;
    arr_cols = __top.document.getElementsByTagName("frameset")[1].cols.split(",");
    var x = parseInt(_left) + parseInt(_left_nav);
    __top.document.getElementsByTagName("frameset")[1].cols = x + ',' + arr_cols[1] + ',' + arr_cols[2];
    __top.frames[1].document.getElementById("arrow1").style.display = "none";
    __top.frames[1].document.getElementById("arrow2").style.display = "block";
*/
    document.getElementById("marketPage").style.display = "none";
    document.getElementById("arrow1").style.display = "none";
    document.getElementById("arrow2").style.display = "block";
}

function doExpandLeft()
{
/*
    var __top = eval("top.window");
    _left = '186';
    _left_nav = '20';
    __top.document.getElementsByTagName("frameset")[2].cols = _left_nav + ',' + _left;
    arr_cols = __top.document.getElementsByTagName("frameset")[1].cols.split(",");
    var x = parseInt(_left) + parseInt(_left_nav);
    __top.document.getElementsByTagName("frameset")[1].cols = x + ',' + arr_cols[1] + ',' + arr_cols[2];
*/
    document.getElementById("marketPage").style.display = IE ? "block" : "table"; 
    document.getElementById("arrow1").style.display = "block";
    document.getElementById("arrow2").style.display = "none";
}

function doCollapseRight()
{
    var __top = eval("top.window");
    arr_cols = __top.document.getElementsByTagName("frameset")[1].cols.split(",");
    __top.document.getElementsByTagName("frameset")[1].cols = arr_cols[0] + ', 65%, 0%';
}

function doCollapseMainBar()
{
    var __top = eval("top.window");
    __top.document.getElementsByTagName("frameset")[3].rows = '0, *';
}

function doExpandMainBar(isLoggedIn) {
    var __isLoggedIn = eval("top.isLoggedIn");
    var __top = eval("top.window");
    if (__isLoggedIn) {
        __top.document.getElementsByTagName("frameset")[3].rows = '92, *';
    } else {
        if (IE && isLoggedIn)
             __top.document.getElementsByTagName("frameset")[3].rows = '92, *';
        else __top.document.getElementsByTagName("frameset")[3].rows = '22, *';
    }
}

function doExpandRight()
{
    var __top = eval("top.window");
    arr_cols = __top.document.getElementsByTagName("frameset")[1].cols.split(",");
    __top.document.getElementsByTagName("frameset")[1].cols = arr_cols[0] + ',*,33%';
}