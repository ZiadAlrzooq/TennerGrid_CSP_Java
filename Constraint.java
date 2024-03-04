
import java.util.ArrayList;
import java.util.Map;

public abstract class Constraint {
	ArrayList<Variable> vars;

	public Constraint(ArrayList<Variable> vars) {
		this.vars = vars;
	}

	public abstract boolean isSatisfied(Map<Variable, Integer> assignment);

}