<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">
	 <!-- Main jumbotron for a primary marketing message or call to action -->
	 <div class="jumbotron">
	   <div class="container">
	     <h1>Welcome to Grid Game One</h1>
	     <p>A 2 player hex-field based no-luck turn-based game.</p>
	     <p>
	     	<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.CreateGameActionBean">
	     		Create Game
	     	</stripes:link>
	     	<div id="availGames">querying games ...</div>
	     </p>
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">
  
	<c:if test="${not empty actionBean.player}">
		<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.BoardActionBean">
			<stripes:param name="playerId">${actionBean.player.id}</stripes:param>
	     		Re-join last game (${actionBean.game.id }) as player ${actionBean.player.side}
		</stripes:link>
	</c:if>
  
	<script data-main="js/landing.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>