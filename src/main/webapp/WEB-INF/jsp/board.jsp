<%@ page contentType="text/html;charset=UTF-8" language="java" session="false"%>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
	<stripes:layout-component name="head">
	</stripes:layout-component>
	<stripes:layout-component name="center">


		<div class="row">
			<div class="col-md-10 col-md-offset-2">
				<div id="messageTitle">waiting for server message ...</div>
				<div id="messageError"></div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-10 col-md-offset-2">
				<canvas id="board" width="650" height="565"></canvas>
			</div>
		</div>

		<!-- resources -->
		<img id="inf_red" src="images/inf_red.png" style="display: none" />
		<img id="inf_green" src="images/inf_green.png" style="display: none" />
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

	</stripes:layout-component>
</stripes:layout-render>