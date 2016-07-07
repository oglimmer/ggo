<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">	 
	 <div class="jumbotron">
	   <div class="container">
	     	<h3>Enter e-mail addresses</h3>
	     	
	     	<stripes:errors />
	     	
		   	<stripes:form beanclass="de.oglimmer.ggo.web.action.CreateGameByEmailActionBean">
		   		Email player 1:<stripes:text name="email1"></stripes:text><br/>
		   		Email player 2:<stripes:text name="email2"></stripes:text><br/>
		   		<stripes:submit name="createEmail">Create game</stripes:submit>
		   	</stripes:form>
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">
  
      <script data-main="js/createGameByEmail.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>