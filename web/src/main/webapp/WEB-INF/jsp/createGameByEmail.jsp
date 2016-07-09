<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">	 
	 <div class="jumbotron">
	   <div class="container">
	     	<h3>Enter e-mail addresses</h3>
	     	
	     	<stripes:errors />

			<stripes:form beanclass="de.oglimmer.ggo.web.action.CreateGameByEmailActionBean">
	            <fieldset>
	                <div class="row form-group">
	                    <div class="col-sm-12">
	                        Your email address:<stripes:text name="email1" class="form-control"></stripes:text>
	                    </div>
	                </div>
	                <div class="row form-group">
	                    <div class="col-sm-12">
	                        Opponent's email address:<stripes:text id="email2" name="email2" class="form-control"></stripes:text>
	                        <stripes:checkbox name="searchForOne" id="searchForOne" /> I don't have an opponent, use player's database
	                    </div>
	                </div>
	                <div class="row form-group">
	                    <div class="col-sm-12">
	                        <stripes:submit name="createEmail" class="btn btn-primary">Create game</stripes:submit>
	                        <stripes:link class="btn btn-primary" beanclass="de.oglimmer.ggo.web.action.LandingActionBean">Cancel</stripes:link>
	                    </div>
	                </div>
	            </fieldset>
	        </stripes:form>	     	
	     	
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">
  
      <script data-main="js/createGameByEmail.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>