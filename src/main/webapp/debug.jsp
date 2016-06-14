<%@page import="de.oglimmer.ggo.ui.UIBoard"%>
<%@page import="de.oglimmer.ggo.logic.Unit"%>
<%@page import="de.oglimmer.ggo.logic.Player"%>
<%@page import="de.oglimmer.ggo.logic.Field"%>
<%@page import="de.oglimmer.ggo.logic.Game"%>
<%@page import="de.oglimmer.ggo.logic.Games"%>
<h1>debug</h1>

<%
	Game game = Games.INSTANCE.getGameById(null);

	for (Field f : game.getBoard().getFields()) {
		out.println(f + "<br/>");
	}

	for (Player p : game.getPlayers()) {
		out.println("<hr/>");
		out.println("<h2>" + p.getSide() + "</h2>");
		for (Unit u : p.getUnitInHand()) {
			out.println(u + "<br/>");
		}
		out.println(p.getClientUIState() + "<br/>");
		out.println(p.getClientMessages() + "<br/>");

	}
%>