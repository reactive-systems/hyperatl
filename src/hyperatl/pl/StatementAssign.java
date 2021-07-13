package hyperatl.pl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatementAssign extends Statement {

	public String var;
	public Expression expr;
	
	public StatementAssign(String var, Expression expr) {
		this.var = var;
		this.expr = expr;
	}

	@Override
	public List<ProgramState> semantics(VariableState varState) {
		VariableState copyVarState = new VariableState(varState);
		
		boolean[] e = expr.evaluateExpression(copyVarState);
		copyVarState.update(var, e);
		
		ProgramState succ = new ProgramState(new StatementTerm(), copyVarState);

		List<ProgramState> l = new ArrayList<>(1);
		
		l.add(succ);
		
		return l;
	}
	
	@Override
	public Player getPlayer() {
		return Player.NONDET;
	}

	@Override
	public Set<String> usedVars() {

		Set<String> used = expr.usedVars();
		used.add(var);
		return used;
	}

	@Override
	public Set<Integer> usedChannels() {
		return new HashSet<>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		result = prime * result + ((var == null) ? 0 : var.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatementAssign other = (StatementAssign) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (var == null) {
			if (other.var != null)
				return false;
		} else if (!var.equals(other.var))
			return false;
		return true;
	}
}
