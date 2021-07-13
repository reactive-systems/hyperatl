package hyperatl.pl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatementIf extends Statement{

	public Expression expr;
	
	public Statement left;
	public Statement right;
	
	public StatementIf(Expression expr, Statement left, Statement right) {
		this.expr = expr;
		this.left = left;
		this.right = right;
	}

	@Override
	public List<ProgramState> semantics(VariableState varState) {
		
		boolean[] e = expr.evaluateExpression(varState);
		
		assert(e.length == 1);
		
		ProgramState succ;
		
		if(e[0]) {
			succ = new ProgramState(left, varState);
		} else {
			succ = new ProgramState(right, varState);
		}
		

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
		used.addAll(left.usedVars());
		used.addAll(right.usedVars());
		
		
		return used;
	}

	@Override
	public Set<Integer> usedChannels() {
		Set<Integer> used = left.usedChannels();
		used.addAll(right.usedChannels());
		
		
		return used;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		StatementIf other = (StatementIf) obj;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
}
