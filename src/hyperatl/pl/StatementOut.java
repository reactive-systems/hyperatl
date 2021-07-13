package hyperatl.pl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatementOut extends Statement {

	public Expression expr;
	
	public int channel;
	
	public StatementOut(Expression expr, int channel) {
		this.expr = expr;
		this.channel = channel;
	}

	@Override
	public List<ProgramState> semantics(VariableState varState) {
		
		// TODO
		ProgramState succ = new ProgramState(new StatementTerm(), varState);

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
		return expr.usedVars();
	}

	@Override
	public Set<Integer> usedChannels() {
		Set<Integer> used = new HashSet<>();
		used.add(channel);
		return used;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + channel;
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
		StatementOut other = (StatementOut) obj;
		if (channel != other.channel)
			return false;
		if (expr == null) {
			if (other.expr != null)
				return false;
		} else if (!expr.equals(other.expr))
			return false;
		return true;
	}
}
