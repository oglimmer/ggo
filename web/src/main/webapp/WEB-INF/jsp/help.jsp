<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">
	 <div class="jumbotron">
	   <div class="container">
	     <h1>Help for Grid Game One</h1>
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">

	<h1>Goal of the game</h1>
	<p>
		The player who earns more points within 5 turns (with each 3 phases) wins the game.
	</p>

	<h1>Phases</h1>
	<p>
		Each turn has 3 phases: draft, deploy and combat/move.
	</p>
	
	<h2>Draft phase</h2>
	<p>
		Each player drafts (or you could say 'buys') units. Each round a player gets 1,000 additional credits. Credits you don't spend
		this turn, will be kept for next turn. A unit is drafted by clicking on its icon at the bottom. If you want to undo the draft
		click on the unit in your hand. Drafting is done for both players in parallel. 
		The deploy phase is started by the player having more credits left, or at a tie the player with less units, or at a tie
		by random.  
	</p>
	<h2>Deploy phase</h2>
	<p>
		Each player deploys one unit at a time. After a player has deployed a unit, the other player will do so. 
		Each player must deploy all units in his hand. A unit can only be deployed on the player's side of the 
		board, unless a player deploys an Airborne unit. Airborne units can be deployed anywhere on the player's side or next to
		an existing unit which was on the board at the turn's start.
	</p>
	<h2>Combat / move phase</h2>
	<p>
		Each combat / move phase has 3 rounds. For each round a player can give each unit one command. When all units have the
		desired command press the 'done' button to see all commands issues by your and your opponent.
		To let the computer carry out the move/attacks press done again. All movements and attacks are 
		then executed in parallel. 
	</p>
	<h3>Command: Fortify</h3>
	<p>
		This is the default command. A "F" on a unit shows that the unit's command is fortify. The unit will not move, support another or 
		bombard, but get +1 (defense-)strength.
	</p>
	<h3>Command: Move / attack</h3>
	<p>
		A unit can only move to a field where no other friendly unit is located or commanded. If a unit finds an enemy unit on this
		field, both units will fight and only one unit will be left. Units will also fight if they move across each other. 
		Units will not fight if a unit moves to a field where a unit has been, but this unit moved into another field. 
		A red arrow indicates a move command. 
	</p>
	<h3>Command: Support</h3>
	<p>
		A unit can support another friendly and adjacent unit. The other unit gets +1 strength. A yellow arrow indicates a support command.
		Note: A unit getting support from another unit cannot move into a field which is not adjacent to the supporting unit.
	</p>
	<h3>Command: Bombard</h3>
	<p>
		If a unit bombards a unit, this unit will be removed. A green arrow indicates a move command.
	</p>

	<h1>Scoring</h1>
	<p>
		For each enemy city <img id="city_green" src="images/city_green.png"  /> you occupy at the of a turn, you get 25 points.
		For each kill you do while a moving attack you get 10 points. For each bombardment you get 5 points.  
	</p>

	<h1>Units</h1>
	<h2>Infantry <img src="images/infantry_green.png" /></h2>
	<p>			
		Strength: 1 (Has support ability)
	</p>
	<h2>Tank <img src="images/tank_green.png" /></h2>
	<p>	
		Strength: 2 (Has support ability)
	</p>
	<h2>Airborne forces <img src="images/airborne_green.png" /></h2>
	<p>	
		Strength: 1 (Has support ability and can be deployed into the enemy side of the board)
	</p>
	<h2>Helicopter <img src="images/helicopter_green.png" /></h2>
	<p>	
		Strength: 1 (Has support ability and can bombard fields within 1 field range)
	</p>
	<h2>Artillery <img src="images/artillery_green.png" /></h2>
	<p>	
		Strength: 0 (Doesn't have support ability, but can bombard fields within 2 fields range)
	</p>
	
  
	<script data-main="js/help.js" src="webjars/requirejs/2.2.0/require.min.js"></script>
	  
 </stripes:layout-component>
</stripes:layout-render>