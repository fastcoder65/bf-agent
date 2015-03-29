//Set tab to intially be selected when page loads:
//[which tab (1=first tab), ID of tab content to display]:
var initialtab=[1, "sc1"];

//Turn menu into single level image tabs (completely hides 2nd level)?
var turntosingle=0; //0 for no (default), 1 for yes

//Disable hyperlinks in 1st level tab images?
var disabletablinks=0; //0 for no (default), 1 for yes

////////Stop editting////////////////
var tabDesc = new Array();

tabDesc["sc1"] = new Array ("sc1", 0 );
tabDesc["sc2"] = new Array ("sc2", 1 );
tabDesc["sc3"] = new Array ("sc3", 2 );
tabDesc["sc4"] = new Array ("sc4", 3 );
tabDesc["sc5"] = new Array ("sc5", 4 );

var previoustab="";

var tabobjlinks;

if (turntosingle==1)
  document.write('<style type="text/css">\n#tabcontentcontainer{display: none;}\n</style>');



function expandcontent(cid, aobject)
{

        if (disabletablinks==1) {
          //  alert("disabletablinks");
                aobject.onclick=new Function("return false");
            }

        if (document.getElementById)
        {
         //  alert("(document.getElementById)");
                highlighttab(aobject);
                if (turntosingle==0)
                {
               //   alert("(turntosingle==0)");
                        if (previoustab!="")
                        document.getElementById(previoustab).style.display="none" ;

                        document.getElementById(cid).style.display="block";
                        previoustab=cid;
                }
        } else {
          alert("Your browser is not supported!");
        }
}

function highlighttab(aobject)
{
  // debug();
        if (typeof tabobjlinks=="undefined")
                collecttablinks();
        for (var i=0; i<tabobjlinks.length; i++)
                tabobjlinks[i].className="";
        aobject.className="current";
}


function collecttablinks()
{
        var tabobj=document.getElementById("tablist");
        tabobjlinks=tabobj.getElementsByTagName("A");
}

function do_onload()
{
        collecttablinks();
        // debug();
        expandcontent(initialtab[1], tabobjlinks[initialtab[0]-1]);
}

function go_tab(tabName)
{
        var tab = tabDesc[tabName];
        if( tab) {
         collecttablinks();
         expandcontent(tab[0], tabobjlinks[tab[1]]);
        }
}

function do_onload_one()
{
        document.getElementById(initialtab[1]).style.display="block";
}
