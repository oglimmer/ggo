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
	
		<stripes:link beanclass="de.oglimmer.ggo.web.action.DebugActionBean" class="btn btn-primary">
			<stripes:param name="pass">${actionBean.pass}</stripes:param>
			Refresh
		</stripes:link>
		
		<stripes:link beanclass="de.oglimmer.ggo.web.action.DebugActionBean" event="resetGame" class="btn btn-primary">
			<stripes:param name="pass">${actionBean.pass}</stripes:param>
			Reset all games
		</stripes:link>

	
		<h3>AtmosphereResources</h3>
		<ul>
			<c:forEach var="item" items="${actionBean.atmosphereResources}">
				<li>UUID={${item.uuid}}, disconnected={${item.disconnected }}, Player={<a href="#player${item.player.id}">${item.player.id}</a>, ${item.player.side}}</li>
			</c:forEach>
		</ul>

		<div class="container">
			<div class="row">
				<h3>GameNotifications</h3>
				<div class="col-sm-1">ID</div>
				<div class="col-sm-3">EMAIL</div>
				<div class="col-sm-2">CREATED_ON</div>
				<div class="col-sm-2">CONFIRMED</div>
				<div class="col-sm-4">CONFIRMED_ID</div>
			</div>
			<c:forEach var="gn" items="${actionBean.gameNotifications}">
				<div class="row">
					<div class="col-sm-1">${gn.id }</div>
					<div class="col-sm-3">${gn.email }</div>
					<div class="col-sm-2">${gn.createdOn }</div>
					<div class="col-sm-2">${gn.confirmed }</div>
					<div class="col-sm-4">${gn.confirmId }</div>
				</div>
			</c:forEach>
		</div>		

		<c:forEach var="game" items="${actionBean.games }">

			<h2>Game: ${game.id }</h2>
			<p>Created = ${game.createdOn }</p>

			<h3>Fields</h3>
			<c:forEach var="field" items="${game.board.fields }">
				${field } <br />
			</c:forEach>

			<h3>Phase</h3>
			
			<ol>
				<c:forEach var="subPhase" items="${actionBean.buildPhaseStack(game)}">
					<li>${subPhase }</li>
				</c:forEach>
			</ol>

			<h3>Players</h3>

			<c:forEach var="p" items="${game.players }">
				<h4><a name="player${p.id }">${p.id }/${p.side }</a></h4>
				<p>Last action = ${p.lastAction }</p>
				<p>Last connection = ${p.lastConnection }</p>
				<p>${actionBean.getAtmosphereResources(p) }</p>
				<c:forEach var="u" items="${p.unitInHand }">
					${u } <br />
				</c:forEach>
				${p.uiStates } <br />
			</c:forEach>

		</c:forEach>


	</stripes:layout-component>
</stripes:layout-render>

