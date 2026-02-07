package client;

public enum NoteColor {
	GREEN, RED, ORANGE, PURPLE, YELLOW;

	public static boolean isValid(String value) {
		try {
			valueOf(value.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
