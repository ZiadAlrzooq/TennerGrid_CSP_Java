import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class main {
	static int[] targetSum;
	static int width = 10;
	static int height = 3;

	public static void generateAllConstraints(CSP csp) {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				generateNotEqualConstraints(csp, i, j);
			}
		}
		targetSum = csp.initialState();
		for (int i = 0; i < width; i++) {
			generateColumnConstraint(csp, i);
		}
	}

	private static void generateNotEqualConstraints(CSP csp, int row, int col) {
		Variable curr = csp.variables.get(10 * row + col);
		// add row variables
		for (int i = 0; i < width; i++) {
			if (i == col)
				continue;
			ArrayList<Variable> temp = new ArrayList<>();
			temp.add(curr);
			temp.add(csp.variables.get(10 * row + i));
			csp.addConstraint(new NotEqualConstraint(temp, curr, csp.variables.get(10 * row + i)));
		}
		// add connected variables
		// add top/bottom
		int index = 0;
		if (row > 0) {
			index = (row - 1) * 10 + col;
			ArrayList<Variable> temp = new ArrayList<>();
			temp.add(curr);
			temp.add(csp.variables.get(index));
			csp.addConstraint(new NotEqualConstraint(temp, curr, csp.variables.get(index)));
		}
		if (row < height - 1) {
			index = (row + 1) * 10 + col;
			ArrayList<Variable> temp = new ArrayList<>();
			temp.add(curr);
			temp.add(csp.variables.get(index));
			csp.addConstraint(new NotEqualConstraint(temp, curr, csp.variables.get(index)));
		}
		// add top left and top right
		if (col > 0 && row > 0) {
			index = (row - 1) * 10 + col - 1;
			ArrayList<Variable> temp = new ArrayList<>();
			temp.add(curr);
			temp.add(csp.variables.get(index));
			csp.addConstraint(new NotEqualConstraint(temp, curr, csp.variables.get(index)));
		}
		if (row > 0 && col < width - 1) {
			index = (row - 1) * 10 + col + 1;
			ArrayList<Variable> temp = new ArrayList<>();
			temp.add(curr);
			temp.add(csp.variables.get(index));
			csp.addConstraint(new NotEqualConstraint(temp, curr, csp.variables.get(index)));
		}
		// add bottom left and bottom right
		if (row < height - 1 && col > 0) {
			index = (row + 1) * 10 + col - 1;
			ArrayList<Variable> temp = new ArrayList<>();
			temp.add(curr);
			temp.add(csp.variables.get(index));
			csp.addConstraint(new NotEqualConstraint(temp, curr, csp.variables.get(index)));
		}
		if (row < height - 1 && col < width - 1) {
			index = (row + 1) * 10 + col + 1;
			ArrayList<Variable> temp = new ArrayList<>();
			temp.add(curr);
			temp.add(csp.variables.get(index));
			csp.addConstraint(new NotEqualConstraint(temp, curr, csp.variables.get(index)));
		}

	}

	private static void generateColumnConstraint(CSP csp, int colIndex) {
		ArrayList<Variable> varsInCol = new ArrayList<>();
		for (Variable var : csp.variables) {
			if (var.col == colIndex)
				varsInCol.add(var);
		}
		csp.addConstraint(new ColumnConstraint(varsInCol, targetSum[colIndex]));
	}

	public static void printGrid(HashMap<Variable, Integer> result) {
		int[][] grid = new int[height][width];
		for (Entry<Variable, Integer> set : result.entrySet()) {
			grid[set.getKey().row][set.getKey().col] = set.getValue();
		}
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				System.out.printf("%4d", grid[i][j]);
			}
			System.out.println();
		}
		for (int value : targetSum) {
			System.out.printf("%4d", value);
		}
		System.out.println();
	}

	public static void resetDomain(CSP csp) {
		ArrayList<Integer> initialDomain = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			initialDomain.add(i);
		}
		for (Variable var : csp.variables) {
			if (!csp.initialAssignment.containsKey(var)) {
				csp.domains.get(var).clear();
				csp.domains.get(var).addAll((ArrayList<Integer>) initialDomain.clone());
			}
		}
	}

	public static void main(String[] args) {
		ArrayList<Variable> variables = new ArrayList<>();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Variable var = new Variable(i, j);
				variables.add(var);
			}
		}
		HashMap<Variable, ArrayList<Integer>> domains = new HashMap<>();
		ArrayList<Integer> domain = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			domain.add(i);
		}
		for (Variable var : variables) {
			domains.put(var, new ArrayList<>((ArrayList<Integer>) domain.clone()));
		}
		CSP csp = new CSP(variables, domains);
		// generate constraints
		generateAllConstraints(csp);
		long start = System.currentTimeMillis();

		HashMap<Variable, Integer> result = csp.solveState(1);
		if (result == null) {
			System.out.println("no sol");
		} else
			printGrid(result);
		System.out.println("backSearch took " + (System.currentTimeMillis() - start) + "ms");
		System.out.println();

		resetDomain(csp);
		start = System.currentTimeMillis();
		result = csp.solveState(2);
		if (result == null) {
			System.out.println("no sol");
		} else
			printGrid(result);
		System.out.println("backSearch with MRV took " + (System.currentTimeMillis() - start) + "ms");
		System.out.println();

		resetDomain(csp);
		start = System.currentTimeMillis();
		result = csp.solveState(3);
		if (result == null) {
			System.out.println("no sol");
		} else
			printGrid(result);
		System.out.println("forwardChecking took " + (System.currentTimeMillis() - start) + "ms");
		System.out.println();

		resetDomain(csp);
		start = System.currentTimeMillis();
		result = csp.solveState(4);
		if (result == null) {
			System.out.println("no sol");
		} else
			printGrid(result);
		System.out.println("forwardChecking with MRV took " + (System.currentTimeMillis() - start) + "ms");

	}
}
