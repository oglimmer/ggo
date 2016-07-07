<%@page import="de.oglimmer.atmospheremvc.game.Games"%>
<%@page import="de.oglimmer.atmospheremvc.com.AtmosphereResourceCache"%>
<%@page import="java.util.Collection"%>
<%@page import="de.oglimmer.ggo.logic.Unit"%>
<%@page import="de.oglimmer.ggo.logic.Player"%>
<%@page import="de.oglimmer.ggo.logic.Field"%>
<%@page import="de.oglimmer.ggo.logic.Game"%>
<h1>debug</h1>

<%

	for ( AtmosphereResourceCache.Item item : AtmosphereResourceCache.INSTANCE.getItems()) {
		out.println(item+"<br/>");
	}


	Collection<Game> games = Games.<Game>getGames().getAllGames();

	for(Game game : games) {

		out.println("<hr/>");
		
		for (Field f : game.getBoard().getFields()) {
			out.println(f + "<br/>");
		}
	
		out.println("<hr/>");
		
		for (Player p : game.getPlayers()) {
			out.println("<hr/>");
			out.println("<h2>"+p.getId()+"/" + p.getSide() + "</h2>");
			for (Unit u : p.getUnitInHand()) {
				out.println(u + "<br/>");
			}
			out.println("<hr/>");
			out.println(p.getUiStates() + "<br/>");
			out.println("<hr/>");
	
		}
	}
%>