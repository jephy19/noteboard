package Server;

public class Note {

	int x, y, w, h;
	String color, msg;
	int pinCount;

	public Note(int X, int Y, String c, String m, int W, int H, boolean pinned) {
		x = X;
		y = Y;
		color = c;
		msg = m;
		w = W;
		h = H;
		if (pinned) {
			pinCount = 1;
		} else {
			pinCount = 0;
		}
	}

	public boolean contains(int px, int py) { // checks if given coordinate is inside the note
		return px >= x && px < x + w && py >= y && py < y + h;
	}

	public boolean sameRect(int X, int Y, int W, int H) {
		return x == X && y == Y && w == W && h == H;
	}

	@Override
	public String toString() {
		if (pinCount >= 1) {
			return x + " " + y + " " + color + " " + msg + " " + "PINNED=true";
		} else {
			return x + " " + y + " " + color + " " + msg + " " + "PINNED=false";
		}
	}
}
