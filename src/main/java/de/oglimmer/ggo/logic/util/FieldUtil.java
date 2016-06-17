package de.oglimmer.ggo.logic.util;

import de.oglimmer.ggo.logic.Field;

public class FieldUtil {

	public static boolean adjacent(Field a, Field b) {
		int thisX = (int) a.getPos().getX();
		int thisY = (int) a.getPos().getY();
		int fx = (int) b.getPos().getX();
		int fy = (int) b.getPos().getY();

		if (minus(thisX, fx) && zero(thisY, fy) || plus(thisX, fx) && zero(thisY, fy)) {
			return true;
		}

		if (even(thisX) && even(thisY) || odd(thisX) && even(thisY)) {
			if (minus(thisX, fx) && minus(thisY, fy) || zero(thisX, fx) && minus(thisY, fy)
					|| minus(thisX, fx) && plus(thisY, fy) || zero(thisX, fx) && plus(thisY, fy)) {
				return true;
			}
		} else {
			if (zero(thisX, fx) && minus(thisY, fy) || plus(thisX, fx) && minus(thisY, fy)
					|| zero(thisX, fx) && plus(thisY, fy) || plus(thisX, fx) && plus(thisY, fy)) {
				return true;
			}
		}

		return false;
	}

	private static boolean minus(int a, int b) {
		return b - a == -1;
	}

	private static boolean zero(int a, int b) {
		return b - a == 0;
	}

	private static boolean plus(int a, int b) {
		return b - a == 1;
	}

	private static boolean odd(int i) {
		return i % 2 != 0;
	}

	private static boolean even(int i) {
		return i % 2 == 0;
	}

}
