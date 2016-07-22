<%@ page contentType="text/html;charset=UTF-8" language="java"
	session="false"%>
<%@ taglib prefix="stripes"
	uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
	<stripes:layout-component name="head">
	</stripes:layout-component>
	<stripes:layout-component name="center">
		<h3>AtmosphereResources</h3>
		<ul>
			<c:forEach var="item" items="${actionBean.atmosphereResources}">
				<li>UUID={${item.uuid}}, disconnected={${item.disconnected }}, Player={<a href="#player${item.player.id}">${item.player.id}</a>, ${item.player.side}}</li>
			</c:forEach>
		</ul>

		<c:forEach var="game" items="${actionBean.games }">

			<h2>${game.id }</h2>

			<h3>Fields</h3>
			<c:forEach var="field" items="${game.board.fields }">
				${field } <br />
			</c:forEach>

			<h3>Phase</h3>
			
			${game.currentPhase.toString(0) } <br />

			<h3>Players</h3>

			<c:forEach var="p" items="${game.players }">
				<h4><a name="player${p.id }">${p.id }/${p.side }</a></h4>
				<c:forEach var="u" items="${p.unitInHand }">
					${u } <br />
				</c:forEach>
				${p.uiStates } <br />
			</c:forEach>

		</c:forEach>


	</stripes:layout-component>
</stripes:layout-render>

