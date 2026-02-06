package client;

public class CommandValidator {

	public static String validate(String command) {
		if (command == null || command.isEmpty()) {
			return "ERROR INVALID_FORMAT empty command";
		}

		if (command.startsWith(" ") || command.endsWith(" ")) {
			return "ERROR INVALID_FORMAT leading or trailing spaces";
		}

		String[] parts = command.split(" ");

		switch (parts[0]) {
		case "POST":
			return validatePost(parts);
		case "GET":
			return parts.length == 1 ? null : "ERROR INVALID_PARAMETER_COUNT GET takes no parameters";
		case "PIN":
			return validatePin(parts);
		case "UNPIN":
			return parts.length == 2 ? null : "ERROR INVALID_PARAMETER_COUNT UNPIN <noteId>";
		default:
			return "ERROR INVALID_FORMAT unknown command";
		}
	}

	private static String validatePost(String[] parts) {
		if (parts.length < 5) {
			return "ERROR INVALID_PARAMETER_COUNT POST <x> <y> <color> <message>";
		}

		try {
			Integer.parseInt(parts[1]);
			Integer.parseInt(parts[2]);
		} catch (NumberFormatException e) {
			return "ERROR PARAMETER_OUT_OF_BOUNDS x and y must be integers";
		}

		if (!NoteColor.isValid(parts[3])) {
			return "ERROR COLOR_NOT_SUPPORTED " + parts[3] + " is not a supported color";
		}

		return null; // valid
	}

	private static String validatePin(String[] parts) {
		if (parts.length != 4) {
			return "ERROR INVALID_PARAMETER_COUNT PIN <x> <y>";
		}

		try {
			Integer.parseInt(parts[1]);
			Integer.parseInt(parts[2]);
		} catch (NumberFormatException e) {
			return "ERROR PARAMETER_OUT_OF_BOUNDS PIN parameters must be integers";
		}

		return null;
	}
}
