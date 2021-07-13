package hyperatl.pl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatementWhile extends Statement{

	public Expression expr;
	public Statement body;
	
	
	public StatementWhile(Expression expr, Statement body) {
		this.expr = expr;
		this.body = body;
	}

	@Override
	public List<ProgramState> semantics(VariableState varState) {
		boolean[] e = expr.evaluateExpression(varState);
		
		assert(e.length == 1);
		
		ProgramState succ;
		
		if(e[0]) {
			succ = new ProgramState(new StatementSeq(body, this), varState);
		} else {
			succ = new ProgramState(body, varState);
			
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
		used.addAll(body.usedVars());
		return used;
	}

	@Override
	public Set<Integer> usedChannels() {
		return body.usedChannels();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
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
		StatementWhile other = (StatementWhile) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		return true;
	}
}
