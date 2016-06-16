package de.oglimmer.ggo.logic;

public class CombatPhase extends BasePhase {

	private int round = 0;

	public CombatPhase(Player firstActivePlayer) {
		super(firstActivePlayer);
	}

	@Override
	public boolean isHighlighted(Field field, Player forPlayer) {
		return false;
	}

	@Override
	public boolean isSelectable(Field field, Player forPlayer) {
		return false;
	}

	@Override
	public boolean isSelected(Unit unit, Player forPlayer) {
		return false;
	}

	@Override
	public boolean isSelectable(Unit unit, Player forPlayer) {
		return forPlayer == getActivePlayer() && unit.getPlayer() == forPlayer && unitOnBoard(unit);
	}

	private boolean unitOnBoard(Unit unit) {
		return unit.getPlayer().getGame().getBoard().getFields().stream().anyMatch(f -> f.getUnit() == unit);
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
