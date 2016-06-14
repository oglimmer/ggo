package de.oglimmer.ggo.logic;

public class CombatPhase extends BasePhase {

	private int round = 0;

	public CombatPhase(Player firstActivePlayer) {
		super(firstActivePlayer);
	}

	@Override
	public void execCmd(Player player, String cmd, String param) {
	}

	@Override
	public void updateUI(Player player) {

	}

	@Override
	protected void nextPhase(Player firstPlayer) {
		firstPlayer.getGame().setCurrentPhase(new DeployPhase(firstPlayer));
	}

	@Override
	protected boolean hasMoreMoves(Player p) {
		return round < 5;
	}

}
