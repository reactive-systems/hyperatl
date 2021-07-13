package hyperatl.pl;

import java.util.List;
import java.util.Set;

public abstract class Statement {
	public enum Player {
		NONDET,
		HIGH,
		LOW
	}

	public abstract List<ProgramState> semantics(VariableState varState);
	
	public abstract Player getPlayer();

	public abstract Set<String> usedVars();
	
	public abstract Set<Integer> usedChannels();
	
	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();
	
}
