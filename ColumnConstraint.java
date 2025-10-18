import java.util.ArrayList;
import java.util.Map;

/**
 * Constraint ensuring that all variables in a column sum to a target value.
 * This constraint is checked only when all variables in the column are assigned.
 */
public class ColumnConstraint extends Constraint {
	private final int targetSum;

	/**
	 * Creates a column sum constraint.
	 * 
	 * @param vars      The variables in the column
	 * @param targetSum The target sum for the column
	 */
	public ColumnConstraint(ArrayList<Variable> vars, int targetSum) {
		super(vars);
		this.targetSum = targetSum;
	}

	/**
	 * Checks if the constraint is satisfied.
	 * The constraint is satisfied if:
	 * - Not all variables are assigned yet (constraint doesn't fully apply)
	 * - All variables are assigned and their sum equals the target
	 * 
	 * @param assignment The current variable assignments
	 * @return true if the constraint is satisfied, false otherwise
	 */
	@Override
	public boolean isSatisfied(Map<Variable, Integer> assignment) {
		int currentSum = 0;
		int assignedCount = 0;
		
		// Calculate sum of assigned variables in this column
		for (Variable var : vars) {
			if (assignment.containsKey(var)) {
				currentSum += assignment.get(var);
				assignedCount++;
			}
		}
		
		// If all variables are assigned, check if sum matches target
		if (assignedCount == vars.size()) {
			return currentSum == targetSum;
		}
		
		// Not all variables assigned yet - constraint is trivially satisfied
		return true;
	}
	
	/**
	 * Gets the target sum for this column.
	 * 
	 * @return The target sum
	 */
	public int getTargetSum() {
		return targetSum;
	}
}
