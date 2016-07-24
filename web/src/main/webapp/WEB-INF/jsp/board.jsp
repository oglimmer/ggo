<%@ page contentType="text/html;charset=UTF-8" language="java" session="false"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
	<title>GridGameOne</title>
	<link rel="stylesheet" href="css/styles.css" />	
	<link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.min.css" />
	<link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap-theme.min.css" />

</head>
<body>

	<nav class="navbar navbar-inverse navbar-default">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">GridGameOne</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<li><a href="Help.action" target="_blank">Instructions</a></li>
			</ul>          
        </div><!--/.navbar-collapse -->
      </div>
    </nav>


	<div id="boardContainer">
		<div id="messageDiv">
			<div id="messageOpponentConnectionLost"></div>
			<div id="messageScore">Your score: 0, opponents score: 0</div>
			<div id="messageTitle">waiting for server message ...</div>
			<div id="messageInfo"></div>
			<div id="messageError"></div>
		</div>
	
		<div id="boardDiv"></div>
		
		<p>Created by <a href="http://oglimmer.de" target="_blank">oglimmer.de</a> - ${actionBean.longVersion } - <stripes:link beanclass="de.oglimmer.ggo.web.action.ImpressumActionBean">Impressum/Kontakt/Datenschutz&nbsp;</stripes:link> </p>
	</div>

	<!-- resources -->
	<img id="infantry_red" src="images/infantry_red.png" style="display: none" />
	<img id="infantry_green" src="images/infantry_green.png" style="display: none" />
	<img id="tank_green" src="images/tank_green.png" style="display: none" />
	<img id="tank_red" src="images/tank_red.png" style="display: none" />
	<img id="airborne_green" src="images/airborne_green.png" style="display: none" />
	<img id="airborne_red" src="images/airborne_red.png" style="display: none" />
	<img id="helicopter_green" src="images/helicopter_green.png" style="display: none" />
	<img id="helicopter_red" src="images/helicopter_red.png" style="display: none" />
	<img id="artillery_green" src="images/artillery_green.png" style="display: none" />
	<img id="artillery_red" src="images/artillery_red.png" style="display: none" />
	<img id="city_red" src="images/city_red.png" style="display: none" />
	<img id="city_green" src="images/city_green.png" style="display: none" />

	<script>
		var ggoPlayerId = "<%=request.getParameter("playerId")%>";
	</script>
	<script data-main="js/board.js" src="webjars/requirejs/2.2.0/require.min.js"></script>

</body>
</html>

