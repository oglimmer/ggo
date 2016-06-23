<%@ page contentType="text/html;charset=UTF-8" language="java" session="false"%>
<%@ taglib prefix="stripes"	uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:forEach var="i" items="${actionBean.availableGames}">
	<stripes:link class="btn btn-primary btn-lg" beanclass="de.oglimmer.ggo.web.action.JoinActionBean" event="join">
		<stripes:param name="gameId">${i.id}</stripes:param>
		Join ${i.id}
	</stripes:link>
	<br/><br/>
</c:forEach>
