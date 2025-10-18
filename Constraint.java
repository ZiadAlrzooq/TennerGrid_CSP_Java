
import java.util.ArrayList;
import java.util.Map;

/**
 * Abstract base class for constraints in the CSP.
 * A constraint defines a relationship that must be satisfied between variables.
 */
public abstract class Constraint {
	/** The variables involved in this constraint */
	protected final ArrayList<Variable> vars;

	/**
	 * Creates a new constraint involving the specified variables.
	 * 
	 * @param vars The variables involved in this constraint
	 */
	public Constraint(ArrayList<Variable> vars) {
		this.vars = vars;
	}

	/**
	 * Checks if this constraint is satisfied by the current assignment.
	 * 
	 * @param assignment The current variable assignments
	 * @return true if the constraint is satisfied, false otherwise
	 */
	public abstract boolean isSatisfied(Map<Variable, Integer> assignment);
	
	/**
	 * Gets the variables involved in this constraint.
	 * 
	 * @return The list of variables
	 */
	public ArrayList<Variable> getVariables() {
		return vars;
	}
}
