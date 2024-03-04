import java.util.ArrayList;
import java.util.Map;

public class NotEqualConstraint extends Constraint {
	private Variable var1;
	private Variable var2;

	public NotEqualConstraint(ArrayList<Variable> vars, Variable var1, Variable var2) {
		super(vars);
		this.var1 = var1;
		this.var2 = var2;
	}

	@Override
	public boolean isSatisfied(Map<Variable, Integer> assignment) {
		if (!assignment.containsKey(var1) || !assignment.containsKey(var2))
			return true;
		return assignment.get(var1) != assignment.get(var2);
	}

}
