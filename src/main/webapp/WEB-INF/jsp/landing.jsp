<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">
	 <!-- Main jumbotron for a primary marketing message or call to action -->
	 <div class="jumbotron">
	   <div class="container">
	     <h1>Welcome to Grid Game One</h1>
	     <p>A 2 player hex-field based no-luck turn-based game.</p>
	     <p>
	     	<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.BoardActionBean">
	     		<stripes:param name="playerId">p1</stripes:param>
	     		Create Game
	     	</stripes:link>
	     	<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.BoardActionBean">
	     		<stripes:param name="playerId">p2</stripes:param>
	     		Join Game
	     	</stripes:link>
	     	<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.LandingActionBean" event="resetGame">
	     		RESET Game
	     	</stripes:link>
	     </p>
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">
  

      <!-- Example row of columns -->
      <div class="row">
        <div class="col-md-10 col-md-offset-2">
        </div>
      </div>
      
      <script data-main="js/landing.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>