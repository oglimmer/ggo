<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">
	 <!-- Main jumbotron for a primary marketing message or call to action -->
	 <div class="jumbotron">
	   <div class="container">
	     <h1>Welcome to Grid Game One</h1>
	     <p>An open source, 2 player, hex-field based, <span style='font-weight:bold'>no-luck</span>, strategy, web-game.</p>
	     <iframe width="420" height="315" src="https://www.youtube.com/embed/kCPBiaSKDY4" frameborder="0" allowfullscreen></iframe>
	     <p>
	     <p>	     	
	     	<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.CreateGameActionBean">
	     		Create Game
	     	</stripes:link> <--- tutorial available!!!
	     </p>
	     <div id="availGames">querying games ...</div>
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">

	<stripes:messages/>
  
	<c:if test="${not empty actionBean.player}">
		<p>
			<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.BoardActionBean">
				<stripes:param name="playerId">${actionBean.player.id}</stripes:param>
	     			Re-join last game (${actionBean.game.id }) as player ${actionBean.player.side}
			</stripes:link>
		</p>
	</c:if>
	<%--
	<p>
		<stripes:form beanclass="de.oglimmer.ggo.web.action.LandingActionBean">
			Put your email address here to get a notification if someone hits the 'create game' button.
			<stripes:text class="form-control" name="email" style="width:250px;display:inline-block;" />
			<stripes:submit class="btn btn-primary" name="register" value="Register" />
		</stripes:form>
	</p>
  --%>
  
	<script data-main="js/landing.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>