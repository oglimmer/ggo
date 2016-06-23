<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">	 
	 <div class="jumbotron">
	   <div class="container">
	     <h1>Your game is ${actionBean.game.getId()}</h1>
	     <p>waiting for other player <span id="waitingProgress"></span></p>
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">
  
	  <script>
		var gameId = "${actionBean.game.getId()}";
		var playerId = "${actionBean.player.getId()}";
	  </script>
      <script data-main="js/waiting.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>