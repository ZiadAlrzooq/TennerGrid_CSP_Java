import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class CSP {
	ArrayList<Variable> variables;
	Map<Variable, ArrayList<Integer>> domains;
	Map<Variable, ArrayList<Constraint>> constraints;
	Map<Variable, Integer> initialAssignment;
	private int consistencyChecks = 0;

	public CSP(ArrayList<Variable> variables, Map<Variable, ArrayList<Integer>> domains) {
		this.variables = variables;
		this.domains = domains;
		initialAssignment = new HashMap<>();
		constraints = new HashMap<>();
		for (Variable var : variables) {
			constraints.put(var, new ArrayList<Constraint>());
		}
	}

	public int[] initialState() {
		Map<Variable, Integer> assignment = new HashMap<>();
		Random rand = new Random();
		for (Variable var : variables) {
			if (rand.nextInt(2) == 0) {
				Collections.shuffle(domains.get(var));
				for (int value : domains.get(var)) {
					assignment.put(var, value);
					if (consistent(var, assignment)) {
						break;
					} else
						assignment.remove(var);
				}
			}
		}
		int targetSum[] = new int[10];
		// print initialState
		System.out.println("Initial state :");
		for (Variable var : variables) {
			if (assignment.containsKey(var))
				System.out.printf("%4d", assignment.get(var));
			else
				System.out.printf("%4d", 0);
			if (var.col == 9)
				System.out.println();
		}
		initialAssignment = new HashMap<>(assignment); // shallow copy of assignment to initial assignment
		assignment = recursiveBackTracking((HashMap<Variable, Integer>) assignment);
		if (assignment == null) {
			System.out.println("unsolveable! retrying...");
			return initialState();
		}
		for (Variable var : variables) {
			if (assignment.containsKey(var))
				targetSum[var.col] += assignment.get(var);
		}
		for (int value : targetSum) {
			System.out.printf("%4d", value);
		}
		System.out.println();
		// restrict domains to the values in initial assignment
		for (Entry<Variable, Integer> entry : initialAssignment.entrySet()) {
			domains.get(entry.getKey()).clear();
			domains.get(entry.getKey()).add(initialAssignment.get(entry.getKey()));
		}
		System.out.println();
		return targetSum;
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

}
