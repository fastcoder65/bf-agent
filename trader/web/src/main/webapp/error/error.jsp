<%
	response.setStatus(200);
%>

<html xmlns:h="http://java.sun.com/jsf/html"
        >

<head>
    <title><h:outputText value="Trader Error"/></title>
    <script type="text/javascript">
    var sOpenAgainMessage = "User session has expired or unpredictable application error has occured. Please, try open this window again. if problem remains, call support service.";

    var contextURL = '<%=request.getContextPath()%>';
    var mainURL    = contextURL + '/main.jsf';

    function checkOpenerWin(win2check, sURL) {
     var win = null;
     if (win2check)
     {
         try {
             if (win2check) {
                 win = win2check;
                 if (win) win.location.href = sURL;
             }
         } catch (x) {
          alert(sOpenAgainMessage);
         }
     }
      return win;
    }

     function toMainPage() {
       var win = null;

         if ( window.parent != null && window.parent != window && window.parent.opener != null) {
             win = checkOpenerWin(window.parent.opener, mainURL);
             window.parent.close();
             return;
         }

         if (window.opener != null) {
             win = checkOpenerWin(window.opener, mainURL);
             window.close();
             return;
         }

         if (window.parent != null && window.parent != window) {
             win = checkOpenerWin(window.parent, mainURL);
             return;
         }
         checkOpenerWin(window, mainURL);
     }
    </script>
</head>

<body onload="toMainPage();">

<h5>Application error occurs.</h5><br/>
 <form>
 		<h:commandLink  immediate="true" action="main">Return to main page</h:commandLink>
 </form>

</body>

</html>
