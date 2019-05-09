package zedly.pyro.compatibility;

public enum BaseColor {
	WHITE(0),
	ORANGE(1),
	MAGENTA(2),
	LIGHT_BLUE(3),
	YELLOW(4),
	LIME(5),
	PINK(6),
	GRAY(7),
	LIGHT_GRAY(8),
	CYAN(9),
	PURPLE(10),
	BLUE(11),
	BROWN(12),
	GREEN(13),
	RED(14),
	BLACK(15);

	private int colorIndex;

	// Constructs a new enum of given materials with the given ID
	BaseColor(int colorIndex) {
		this.colorIndex = colorIndex;
	}

	public int getIndex() {
		return colorIndex;
	}
}
