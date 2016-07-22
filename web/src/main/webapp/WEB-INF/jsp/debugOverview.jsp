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

		<h1>debug</h1>

		<c:forEach var="item" items="${actionBean.atmosphereResources}">
			${item} <br />
		</c:forEach>

		<c:forEach var="game" items="${actionBean.games }">

			<hr />

			<c:forEach var="field" items="${game.board.fields }">
				${field } <br />
			</c:forEach>

			<hr />

			<h3>Phase</h3>
			
			${game.currentPhase } <br />

			<hr />

			<c:forEach var="p" items="${game.players }">
				<hr />
				<h2>${p.id }/${p.side }</h2>
				<c:forEach var="u" items="${p.unitInHand }">
					${u } <br />
				</c:forEach>
				<hr />
				${p.uiStates } <br />
				<hr />
			</c:forEach>

		</c:forEach>


	</stripes:layout-component>
</stripes:layout-render>

