<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">	 
  </stripes:layout-component>
  <stripes:layout-component name="center">
  

      <!-- Example row of columns -->
      <div class="row">
        <div class="col-md-10 col-md-offset-2">
        	Your game is ${actionBean.game.getId()}
        </div>
      </div>
      <div class="row">
        <div class="col-md-10 col-md-offset-2">
        	waiting for other player ...
        </div>
      </div>
      
      <script data-main="js/landing.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>