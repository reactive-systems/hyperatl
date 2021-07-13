package hyperatl.pl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatementIfN extends Statement{

	
	public Statement left;
	public Statement right;
	
	public StatementIfN(Statement left, Statement right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public List<ProgramState> semantics(VariableState varState) {

		ProgramState succ1 = new ProgramState(left, varState);
		ProgramState succ2 = new ProgramState(right, varState);

		List<ProgramState> l = new ArrayList<>(1);
		l.add(succ1);
		l.add(succ2);
		
		return l;
	}
	
	@Override
	public Player getPlayer() {
		return Player.NONDET;
	}

	@Override
	public Set<String> usedVars() {
		
		Set<String> used = left.usedVars();
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
		StatementIfN other = (StatementIfN) obj;
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
