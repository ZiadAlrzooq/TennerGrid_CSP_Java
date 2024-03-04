import java.util.ArrayList;
import java.util.Map;

public class ColumnConstraint extends Constraint {
	int columnSum;

	public ColumnConstraint(ArrayList<Variable> vars, int columnSum) {
		super(vars);
		this.columnSum = columnSum;
	}

	@Override
	public boolean isSatisfied(Map<Variable, Integer> assignment) {
		int colSum = columnSum;
		int count = 0;
		for (Variable var : vars) {
			if (assignment.containsKey(var)) {
				colSum -= assignment.get(var);
				count++;
			}
		}
		if (count == vars.size() && colSum != 0) {
			return false;
		}
		return true;
	}

}
