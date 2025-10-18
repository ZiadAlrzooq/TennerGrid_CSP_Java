import java.util.ArrayList;
import java.util.Map;

/**
 * Constraint ensuring two variables have different values.
 * This is used to enforce that adjacent or same-row cells in the TennerGrid
 * do not have the same digit.
 */
public class NotEqualConstraint extends Constraint {
	private final Variable var1;
	private final Variable var2;

	/**
	 * Creates a not-equal constraint between two variables.
	 * 
	 * @param vars A list containing both variables (for the base class)
	 * @param var1 The first variable
	 * @param var2 The second variable
	 */
	public NotEqualConstraint(ArrayList<Variable> vars, Variable var1, Variable var2) {
		super(vars);
		this.var1 = var1;
		this.var2 = var2;
	}

	/**
	 * Checks if the constraint is satisfied.
	 * The constraint is satisfied if:
	 * - Either variable is unassigned (constraint doesn't apply yet)
	 * - Both variables are assigned to different values
	 * 
	 * @param assignment The current variable assignments
	 * @return true if the constraint is satisfied, false otherwise
	 */
	@Override
	public boolean isSatisfied(Map<Variable, Integer> assignment) {
		// If either variable is unassigned, constraint is trivially satisfied
		if (!assignment.containsKey(var1) || !assignment.containsKey(var2)) {
			return true;
		}
		
		// Both variables assigned - they must have different values
		return !assignment.get(var1).equals(assignment.get(var2));
	}
}
