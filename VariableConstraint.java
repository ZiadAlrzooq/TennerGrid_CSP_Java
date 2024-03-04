import java.util.ArrayList;
import java.util.Map;

public class VariableConstraint extends Constraint {

	public VariableConstraint(ArrayList<Variable> vars) {
		super(vars);
	}

	@Override
	public boolean isSatisfied(Map<Variable, Integer> assignment) {
		if (!assignment.containsKey(vars.get(0))) // first variable is the current grid cell that is to be compared with
													// every other cell
			return true;
		int value1 = assignment.get(vars.get(0));
		for (int i = 1; i < vars.size(); i++) {
			Variable var2 = vars.get(i);
			if (!assignment.containsKey(var2))
				continue;
			int value2 = assignment.get(var2);
			if (value1 == value2)
				return false;
		}
		return true;
	}

}
