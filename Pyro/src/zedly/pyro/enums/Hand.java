package zedly.pyro.enums;

public enum Hand {
	NONE(0), BOTH(3), LEFT(1), RIGHT(2);

	private int i;

	Hand(int i) {
		this.i = i;
	}
}
