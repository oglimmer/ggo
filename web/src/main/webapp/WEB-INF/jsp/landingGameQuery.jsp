<%@ page contentType="text/html;charset=UTF-8" language="java" session="false"%>
<%@ taglib prefix="stripes"	uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:forEach var="i" items="${actionBean.availableGames}">
	<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.JoinGameActionBean">
		<stripes:param name="gameId">${i.id}</stripes:param>
		Join ${i.id} (Started <fmt:formatDate type="BOTH" timeStyle="FULL" value="${i.createdOn }" />)
	</stripes:link>
	<br/><br/>
</c:forEach>
