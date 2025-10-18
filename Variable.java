/**
 * Represents a variable (cell) in the TennerGrid puzzle.
 * Each variable has a row and column position in the grid.
 */
public class Variable {
	private final int row;
	private final int col;
	private final String name;

	/**
	 * Creates a new variable at the specified position.
	 * 
	 * @param row The row index (0-based)
	 * @param col The column index (0-based)
	 */
	public Variable(int row, int col) {
		this.row = row;
		this.col = col;
		this.name = "(" + row + "," + col + ")";
	}

	/**
	 * Gets the row index of this variable.
	 * 
	 * @return The row index
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Gets the column index of this variable.
	 * 
	 * @return The column index
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Gets the name of this variable.
	 * 
	 * @return The variable name in format "(row,col)"
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
