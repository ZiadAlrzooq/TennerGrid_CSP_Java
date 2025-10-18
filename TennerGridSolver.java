import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * TennerGrid CSP Solver - Main class for generating and solving TennerGrid puzzles.
 * 
 * TennerGrid is a constraint satisfaction problem where:
 * - Grid contains digits 0-9
 * - Each row contains unique digits
 * - No two adjacent cells (including diagonals) have the same digit
 * - Each column must sum to a target value
 */
public class TennerGridSolver {
    private final TennerGridConfig config;
    private int[] columnTargetSums;
    
    /**
     * Creates a solver with the specified configuration.
     */
    public TennerGridSolver(TennerGridConfig config) {
        this.config = config;
    }
    
    /**
     * Creates a solver with default configuration.
     */
    public TennerGridSolver() {
        this(new TennerGridConfig());
    }
    
    /**
     * Generates all constraints for the CSP problem.
     * This includes not-equal constraints for adjacent cells and column sum constraints.
     * 
     * @param csp The constraint satisfaction problem to configure
     */
    public void generateAllConstraints(CSP csp) {
        // Generate not-equal constraints for all cells
        for (int row = 0; row < config.getHeight(); row++) {
            for (int col = 0; col < config.getWidth(); col++) {
                generateNotEqualConstraints(csp, row, col);
            }
        }
        
        // Generate initial state and get target column sums
        columnTargetSums = csp.generateInitialState();
        
        // Generate column sum constraints
        for (int col = 0; col < config.getWidth(); col++) {
            generateColumnConstraint(csp, col);
        }
    }
    
    /**
     * Generates not-equal constraints for a specific cell.
     * Ensures the cell differs from all cells in the same row and all adjacent cells.
     * 
     * @param csp The CSP instance
     * @param row The row index of the cell
     * @param col The column index of the cell
     */
    private void generateNotEqualConstraints(CSP csp, int row, int col) {
        Variable currentCell = csp.getVariable(config.getIndex(row, col));
        
        // Add constraints for all other cells in the same row
        addRowConstraints(csp, currentCell, row, col);
        
        // Add constraints for vertically adjacent cells
        addVerticalConstraints(csp, currentCell, row, col);
        
        // Add constraints for diagonally adjacent cells
        addDiagonalConstraints(csp, currentCell, row, col);
    }
    
    /**
     * Adds not-equal constraints between the current cell and all other cells in its row.
     */
    private void addRowConstraints(CSP csp, Variable currentCell, int row, int col) {
        for (int otherCol = 0; otherCol < config.getWidth(); otherCol++) {
            if (otherCol != col) {
                Variable otherCell = csp.getVariable(config.getIndex(row, otherCol));
                addNotEqualConstraint(csp, currentCell, otherCell);
            }
        }
    }
    
    /**
     * Adds not-equal constraints between the current cell and vertically adjacent cells.
     */
    private void addVerticalConstraints(CSP csp, Variable currentCell, int row, int col) {
        // Cell above
        if (row > 0) {
            Variable aboveCell = csp.getVariable(config.getIndex(row - 1, col));
            addNotEqualConstraint(csp, currentCell, aboveCell);
        }
        
        // Cell below
        if (row < config.getHeight() - 1) {
            Variable belowCell = csp.getVariable(config.getIndex(row + 1, col));
            addNotEqualConstraint(csp, currentCell, belowCell);
        }
    }
    
    /**
     * Adds not-equal constraints between the current cell and all diagonally adjacent cells.
     */
    private void addDiagonalConstraints(CSP csp, Variable currentCell, int row, int col) {
        // Top-left diagonal
        if (row > 0 && col > 0) {
            Variable topLeftCell = csp.getVariable(config.getIndex(row - 1, col - 1));
            addNotEqualConstraint(csp, currentCell, topLeftCell);
        }
        
        // Top-right diagonal
        if (row > 0 && col < config.getWidth() - 1) {
            Variable topRightCell = csp.getVariable(config.getIndex(row - 1, col + 1));
            addNotEqualConstraint(csp, currentCell, topRightCell);
        }
        
        // Bottom-left diagonal
        if (row < config.getHeight() - 1 && col > 0) {
            Variable bottomLeftCell = csp.getVariable(config.getIndex(row + 1, col - 1));
            addNotEqualConstraint(csp, currentCell, bottomLeftCell);
        }
        
        // Bottom-right diagonal
        if (row < config.getHeight() - 1 && col < config.getWidth() - 1) {
            Variable bottomRightCell = csp.getVariable(config.getIndex(row + 1, col + 1));
            addNotEqualConstraint(csp, currentCell, bottomRightCell);
        }
    }
    
    /**
     * Helper method to add a not-equal constraint between two variables.
     */
    private void addNotEqualConstraint(CSP csp, Variable var1, Variable var2) {
        ArrayList<Variable> varPair = new ArrayList<>();
        varPair.add(var1);
        varPair.add(var2);
        csp.addConstraint(new NotEqualConstraint(varPair, var1, var2));
    }
    
    /**
     * Generates a column sum constraint for a specific column.
     * 
     * @param csp      The CSP instance
     * @param colIndex The column index
     */
    private void generateColumnConstraint(CSP csp, int colIndex) {
        ArrayList<Variable> columnVariables = new ArrayList<>();
        
        for (Variable var : csp.getVariables()) {
            if (var.getCol() == colIndex) {
                columnVariables.add(var);
            }
        }
        
        csp.addConstraint(new ColumnConstraint(columnVariables, columnTargetSums[colIndex]));
    }
    
    /**
     * Prints the solution grid in a formatted manner.
     * 
     * @param solution The solution mapping from variables to their assigned values
     */
    public void printSolution(HashMap<Variable, Integer> solution) {
        if (solution == null) {
            System.out.println("No solution found");
            return;
        }
        
        // Create 2D array for easy printing
        int[][] grid = new int[config.getHeight()][config.getWidth()];
        for (Entry<Variable, Integer> entry : solution.entrySet()) {
            Variable var = entry.getKey();
            grid[var.getRow()][var.getCol()] = entry.getValue();
        }
        
        // Print grid
        for (int row = 0; row < config.getHeight(); row++) {
            for (int col = 0; col < config.getWidth(); col++) {
                System.out.printf("%4d", grid[row][col]);
            }
            System.out.println();
        }
        
        // Print column target sums
        for (int sum : columnTargetSums) {
            System.out.printf("%4d", sum);
        }
        System.out.println();
    }
    
    /**
     * Resets the domains of all unassigned variables to their initial values.
     * 
     * @param csp The CSP instance
     */
    public void resetDomains(CSP csp) {
        ArrayList<Integer> initialDomain = new ArrayList<>();
        for (int digit = TennerGridConfig.MIN_DIGIT; digit <= TennerGridConfig.MAX_DIGIT; digit++) {
            initialDomain.add(digit);
        }
        
        for (Variable var : csp.getVariables()) {
            if (!csp.isInitiallyAssigned(var)) {
                ArrayList<Integer> domain = csp.getDomain(var);
                domain.clear();
                domain.addAll(initialDomain);
            }
        }
    }
    
    /**
     * Solves the puzzle using a specific algorithm and prints the results.
     * 
     * @param csp       The CSP instance
     * @param algorithm The solving algorithm to use
     */
    public void solveAndPrint(CSP csp, SolverAlgorithm algorithm) {
        System.out.println("=".repeat(50));
        System.out.println("Algorithm: " + algorithm.getDisplayName());
        System.out.println("=".repeat(50));
        
        long startTime = System.currentTimeMillis();
        HashMap<Variable, Integer> result = csp.solve(algorithm);
        long elapsedTime = System.currentTimeMillis() - startTime;
        
        printSolution(result);
        System.out.println();
        System.out.println("Consistency checks: " + csp.getConsistencyChecks());
        System.out.println("Time elapsed: " + elapsedTime + "ms");
        System.out.println();
    }
    
    /**
     * Main method to run the TennerGrid solver with all available algorithms.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║       TennerGrid CSP Solver                   ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();
        
        // Create configuration
        TennerGridConfig config = new TennerGridConfig();
        System.out.println("Grid Configuration:");
        System.out.println("  Width:  " + config.getWidth() + " columns");
        System.out.println("  Height: " + config.getHeight() + " rows");
        System.out.println("  Total cells: " + config.getTotalCells());
        System.out.println();
        
        // Initialize variables and domains
        ArrayList<Variable> variables = new ArrayList<>();
        for (int row = 0; row < config.getHeight(); row++) {
            for (int col = 0; col < config.getWidth(); col++) {
                variables.add(new Variable(row, col));
            }
        }
        
        HashMap<Variable, ArrayList<Integer>> domains = new HashMap<>();
        ArrayList<Integer> initialDomain = new ArrayList<>();
        for (int digit = TennerGridConfig.MIN_DIGIT; digit <= TennerGridConfig.MAX_DIGIT; digit++) {
            initialDomain.add(digit);
        }
        
        for (Variable var : variables) {
            domains.put(var, new ArrayList<>(initialDomain));
        }
        
        // Create CSP and generate constraints
        CSP csp = new CSP(variables, domains);
        TennerGridSolver solver = new TennerGridSolver(config);
        solver.generateAllConstraints(csp);
        
        // Solve using all available algorithms
        for (SolverAlgorithm algorithm : SolverAlgorithm.values()) {
            solver.resetDomains(csp);
            solver.solveAndPrint(csp, algorithm);
        }
        
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║       All algorithms completed!               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }
}
