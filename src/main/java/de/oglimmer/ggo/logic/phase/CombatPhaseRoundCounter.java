package de.oglimmer.ggo.logic.phase;

public class CombatPhaseRoundCounter {

	private static final int MAX_ROUNDS = 3;

	private int round = 0;

	public int getCurrentRound() {
		return round + 1;
	}

	public int getMaxRounds() {
		return MAX_ROUNDS;
	}

	public void incRound() {
		round++;
	}

	public boolean lastRoundReached() {
		return round == MAX_ROUNDS;
	}

}
