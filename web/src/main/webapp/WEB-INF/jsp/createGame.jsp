<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">	 
	 <div class="jumbotron">
	   <div class="container">
	   	<div id="divWaitingForOtherPlayer" style="display:none">
			<h3>Your game is <span id="gameId"></span></h3>
			<p>Waiting for other player <span id="waitingProgress"></span></p>
			<p>(We have informed <span id="numberOfNotifications"></span> by email about your new game, if eventually 
			no one joins this new game, you could either add yourself to the email notification system on the portal page or
			create a play-by-email game with an opponent from the database.)
	   	</div>
	   	<div id="divChooseType">
		   	<h3>Choose a gaming type ...</h3>
		   	<p>
		   		<button id="btnByEmail" class="btn btn-primary btn-lg">Play by e-mail</button>
		   		<button id="btnRealTime" class="btn btn-primary btn-lg">Real-time vs human</button>
		   		<button id="btnAi" class="btn btn-primary btn-lg">Real-time vs A.I.</button>
		   		<button id="btnTutorial" class="btn btn-primary btn-lg">Tutorial</button>
		   	</p>
	   	</div>
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">
  
      <script data-main="js/createGame.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>