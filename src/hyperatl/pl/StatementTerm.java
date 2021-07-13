package hyperatl.pl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatementTerm extends Statement {

	@Override
	public List<ProgramState> semantics(VariableState varState) {
		// Self-loop
		List<ProgramState> l = new ArrayList<>(1);
		l.add(new ProgramState(this, varState));
		
		return l;
	}
	
	@Override
	public Player getPlayer() {
		return Player.NONDET;
	}

	@Override
	public Set<String> usedVars() {
		return new HashSet<>();
	}

	@Override
	public Set<Integer> usedChannels() {
		return new HashSet<>();
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}
