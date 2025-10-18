import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Constraint Satisfaction Problem (CSP) solver.
 * Implements multiple algorithms for solving CSP problems including:
 * - Recursive backtracking
 * - Backtracking with MRV (Minimum Remaining Values) heuristic
 * - Forward checking
 * - Forward checking with MRV
 */
public class CSP {
	private final ArrayList<Variable> variables;
	private final Map<Variable, ArrayList<Integer>> domains;
	private final Map<Variable, ArrayList<Constraint>> constraints;
	private final Map<Variable, Integer> initialAssignment;
	private int consistencyChecks = 0;

	/**
	 * Creates a new CSP with the given variables and their domains.
	 * 
	 * @param variables The list of variables in the problem
	 * @param domains   The domains (possible values) for each variable
	 */
	public CSP(ArrayList<Variable> variables, Map<Variable, ArrayList<Integer>> domains) {
		this.variables = variables;
		this.domains = domains;
		this.initialAssignment = new HashMap<>();
		this.constraints = new HashMap<>();
		
		for (Variable var : variables) {
			constraints.put(var, new ArrayList<>());
		}
	}

	/**
	 * Generates an initial state for the puzzle by randomly assigning some variables.
	 * Returns the target column sums for the puzzle.
	 * 
	 * @return Array of target sums for each column
	 */
	public int[] generateInitialState() {
		Map<Variable, Integer> assignment = new HashMap<>();
		Random rand = new Random();
		
		// Randomly assign some variables
		for (Variable var : variables) {
			if (rand.nextInt(2) == 0) {
				Collections.shuffle(domains.get(var));
				for (int value : domains.get(var)) {
					assignment.put(var, value);
					if (consistent(var, assignment)) {
						break;
					} else {
						assignment.remove(var);
					}
				}
			}
		}
		
		// Calculate target column sums
		int[] targetSums = new int[10];
		
		// Print initial state
		System.out.println("Initial state:");
		for (Variable var : variables) {
			if (assignment.containsKey(var)) {
				System.out.printf("%4d", assignment.get(var));
			} else {
				System.out.printf("%4d", 0);
			}
			if (var.getCol() == 9) {
				System.out.println();
			}
		}
		
		// Store initial assignment
		initialAssignment.putAll(assignment);
		
		// Solve to get complete grid for target sums
		assignment = recursiveBackTracking(new HashMap<>(assignment));
		if (assignment == null) {
			System.out.println("Unsolvable! Retrying...");
			return generateInitialState();
		}
		
		// Calculate column sums from complete assignment
		for (Variable var : variables) {
			if (assignment.containsKey(var)) {
				targetSums[var.getCol()] += assignment.get(var);
			}
		}
		
		// Print target sums
		for (int sum : targetSums) {
			System.out.printf("%4d", sum);
		}
		System.out.println();
		
		// Restrict domains to initial assignment values
		for (Entry<Variable, Integer> entry : initialAssignment.entrySet()) {
			domains.get(entry.getKey()).clear();
			domains.get(entry.getKey()).add(entry.getValue());
		}
		
		System.out.println();
		return targetSums;
	}

	public void addConstraint(Constraint constraint) {
		for (Variable var : constraint.vars) {
			constraints.get(var).add(constraint);
		}
	}

	public boolean consistent(Variable variable, Map<Variable, Integer> assignment) {
		consistencyChecks++;
		for (Constraint constraint : constraints.get(variable)) {
			if (!constraint.isSatisfied(assignment))
				return false;
		}
		return true;
	}

	private void removeFromDomain(Variable variable, Map<Variable, Integer> assignment) {
		for (Constraint constraint : constraints.get(variable)) {
			if (constraint instanceof NotEqualConstraint) {
				if (constraint.vars.get(0).equals(variable) || assignment.containsKey(constraint.vars.get(0))
						|| initialAssignment.containsKey(constraint.vars.get(0)))
					continue;
				else
					domains.get(constraint.vars.get(0)).remove(assignment.get(variable));
			}
		}
	}

	private void addToDomain(Variable variable, Map<Variable, Integer> assignment) {
		for (Constraint constraint : constraints.get(variable)) {
			if (constraint instanceof NotEqualConstraint) {
				if (constraint.vars.get(0).equals(variable) || assignment.containsKey(constraint.vars.get(0))
						|| domains.get(constraint.vars.get(0)).contains(assignment.get(variable))
						|| initialAssignment.containsKey(constraint.vars.get(0)))
					continue;
				domains.get(constraint.vars.get(0)).add(assignment.get(variable));
			}
		}
	}

	public HashMap<Variable, Integer> solveState(int choice) {
		HashMap<Variable, Integer> result = new HashMap<>(initialAssignment);
		switch (choice) {
		case 1:
			consistencyChecks = 0;
			result = recursiveBackTracking(new HashMap<Variable, Integer>()); // regular recursive backtracking
			System.out.println("consistencyChecks = " + consistencyChecks);
			return result;
		case 2:
			consistencyChecks = 0;
			result = backTrackingMRV(new HashMap<Variable, Integer>()); // backtracking with MRV
			System.out.println("consistencyChecks = " + consistencyChecks);
			return result;
		case 3:
			consistencyChecks = 0;
			result = forwardChecking(new HashMap<Variable, Integer>()); // forward checking
			System.out.println("consistencyChecks = " + consistencyChecks);
			return result;
		case 4:
			consistencyChecks = 0;
			result = forwardCheckingMRV(new HashMap<Variable, Integer>()); // forward checking with MRV
			System.out.println("consistencyChecks = " + consistencyChecks);
			return result;
		}
		return result;
	}

	public HashMap<Variable, Integer> recursiveBackTracking(HashMap<Variable, Integer> assignment) {
		if (assignment.size() == variables.size())
			return assignment;
		Variable variable = unassignedVariable(assignment);
		for (int value : domains.get(variable)) {
			HashMap<Variable, Integer> localAssignment = new HashMap<>(assignment);
			localAssignment.put(variable, value);
			if (consistent(variable, localAssignment)) {
				HashMap<Variable, Integer> result = recursiveBackTracking(localAssignment);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	public HashMap<Variable, Integer> backTrackingMRV(HashMap<Variable, Integer> assignment) {
		if (assignment.size() == variables.size())
			return assignment;
		Variable variable = MRV(assignment);
		for (int value : domains.get(variable)) {
			HashMap<Variable, Integer> localAssignment = new HashMap<>(assignment);
			localAssignment.put(variable, value);
			if (consistent(variable, localAssignment)) {
				HashMap<Variable, Integer> result = backTrackingMRV(localAssignment);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	private HashMap<Variable, Integer> forwardChecking(HashMap<Variable, Integer> assignment) {
		if (!forwardCheck(assignment)) {
			return null;
		}
		if (assignment.size() == variables.size())
			return assignment;
		Variable variable = unassignedVariable(assignment);
		for (int value : domains.get(variable)) {
			HashMap<Variable, Integer> localAssignment = new HashMap<>(assignment);
			localAssignment.put(variable, value);
			if (consistent(variable, localAssignment)) {
				removeFromDomain(variable, localAssignment);
				HashMap<Variable, Integer> result = forwardChecking(localAssignment);
				if (result != null)
					return result;
				addToDomain(variable, localAssignment);
			}
		}
		return null;
	}

	private HashMap<Variable, Integer> forwardCheckingMRV(HashMap<Variable, Integer> assignment) {
		if (!forwardCheck(assignment)) {
			return null;
		}
		if (assignment.size() == variables.size())
			return assignment;
		Variable variable = MRV(assignment);
		for (int value : domains.get(variable)) {
			HashMap<Variable, Integer> localAssignment = new HashMap<>(assignment);
			localAssignment.put(variable, value);
			if (consistent(variable, localAssignment)) {
				removeFromDomain(variable, localAssignment);
				HashMap<Variable, Integer> result = forwardCheckingMRV(localAssignment);
				if (result != null)
					return result;
				addToDomain(variable, localAssignment);
			}
		}
		return null;
	}

	private boolean forwardCheck(HashMap<Variable, Integer> assignment) {
		for (Variable var : variables) {
			if (!assignment.containsKey(var) && domains.get(var).size() == 0) {
				return false;
			}
		}
		return true;
	}

	private Variable unassignedVariable(Map<Variable, Integer> assignment) {
		for (Variable variable : variables) {
			if (!assignment.containsKey(variable))
				return variable;
		}
		return null;
	}

	private Variable MRV(Map<Variable, Integer> assignment) {
		Variable mrv = null;
		for (Variable variable : variables) {
			if (!assignment.containsKey(variable)
					&& (mrv == null || domains.get(variable).size() < domains.get(mrv).size())) {
				mrv = variable;
			}
		}
		return mrv;
	}

	// Public accessor methods
	
	/**
	 * Gets a variable by its index in the variables list.
	 * 
	 * @param index The index of the variable
	 * @return The variable at the specified index
	 */
	public Variable getVariable(int index) {
		return variables.get(index);
	}
	
	/**
	 * Gets all variables in the CSP.
	 * 
	 * @return The list of all variables
	 */
	public ArrayList<Variable> getVariables() {
		return variables;
	}
	
	/**
	 * Checks if a variable was assigned in the initial state.
	 * 
	 * @param var The variable to check
	 * @return true if the variable was initially assigned, false otherwise
	 */
	public boolean isInitiallyAssigned(Variable var) {
		return initialAssignment.containsKey(var);
	}
	
	/**
	 * Gets the domain (possible values) for a variable.
	 * 
	 * @param var The variable
	 * @return The domain of the variable
	 */
	public ArrayList<Integer> getDomain(Variable var) {
		return domains.get(var);
	}
	
	/**
	 * Gets the number of consistency checks performed.
	 * 
	 * @return The consistency check count
	 */
	public int getConsistencyChecks() {
		return consistencyChecks;
	}
	
	/**
	 * Solves the CSP using the specified algorithm.
	 * 
	 * @param algorithm The solving algorithm to use
	 * @return The solution assignment, or null if no solution exists
	 */
	public HashMap<Variable, Integer> solve(SolverAlgorithm algorithm) {
		consistencyChecks = 0;
		HashMap<Variable, Integer> result;
		
		switch (algorithm) {
			case BACKTRACKING:
				result = recursiveBackTracking(new HashMap<>());
				break;
			case BACKTRACKING_MRV:
				result = backTrackingMRV(new HashMap<>());
				break;
			case FORWARD_CHECKING:
				result = forwardChecking(new HashMap<>());
				break;
			case FORWARD_CHECKING_MRV:
				result = forwardCheckingMRV(new HashMap<>());
				break;
			default:
				result = null;
		}
		
		return result;
	}
}
