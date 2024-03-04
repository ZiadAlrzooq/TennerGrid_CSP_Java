public class Variable {
	int col;
	int row;
	String name;

	public Variable(int row, int col) {
		this.row = row;
		this.col = col;
		name = "(" + row + "," + col + ")";
	}

	@Override
	public String toString() {
		return name;
	}

}
