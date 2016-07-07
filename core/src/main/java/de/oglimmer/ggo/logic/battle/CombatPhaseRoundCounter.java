package de.oglimmer.ggo.logic.battle;

import java.io.Serializable;

public class CombatPhaseRoundCounter implements Serializable {

	private static final long serialVersionUID = 1L;

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
